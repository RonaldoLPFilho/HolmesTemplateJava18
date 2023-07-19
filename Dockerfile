# [Dockerfile] - https://docs.docker.com/engine/reference/builder/
# [best-practices] - https://docs.docker.com/develop/develop-images/dockerfile_best-practices/#use-multi-stage-builds
# [multi-stage-build] - https://docs.docker.com/develop/develop-images/multistage-build/
# Esta imagen es descartable. Los objetivos son:
# 1. Descargar dependencias
# 2. Compilar
# 3. Buildear el jar
FROM hub.nebula/maven:jdk17_0.0.5 AS build

# Copiamos el pom para exclusivamente descargar las dependencias y que pueda guardase este layer en la cache. [cache-layers]: https://docs.docker.com/develop/develop-images/dockerfile_best-practices/#leverage-build-cache
# HELP: COPY <archivo_de_nuestro_directorio> <destino_dentro_de_la_imagen_*build*>
COPY pom.xml /app/pom.xml

# Con el comando WORKDIR cambiamos de directorio
# HELP: WORKDIR <nuevo_directorio_de_trabajo>
WORKDIR /app/

# Descargamos dependencias exclusivamente para que pueda guardarse en la cache si no hubo cambios en el pom
# HELP: RUN <comandos a ejecutar>
RUN mvn dependency:go-offline

# Copio exclusivamente el directorio para compilarlo
# HELP: COPY <archivo_de_nuestro_directorio> <destino_dentro_de_la_imagen_*build*>
COPY src /app/src

# Buildeamos el jar SIN ejecutar los tests
# HELP: RUN <comandos a ejecutar>
RUN mvn package -Dmaven.test.skip=true

# Descargamos la herramienta para actualizar tz data, y el archivo con los últimos cambios.
RUN curl "https://cdn.azul.com/tools/ziupdater1.1.1.1-jse8+7-any_jvm.tar.gz" --output ziupdater.tar.gz \
    && tar -xzf ziupdater.tar.gz \
    && curl "https://data.iana.org/time-zones/tzdata-latest.tar.gz" --output tzdata-latest.tar.gz

#Esta imagen es la que se va a deployar en nebula. Los objetivos son:
# 1. Imagen de base liviana.
# 2. Instalar herramientas para troubleshooting.
# 3. Copiar de la imagen de *build* los archivos necesarios para ejecutar correctamente la aplicacion
FROM hub.nebula/java-17-corretto:0.0.6 AS final

WORKDIR /tmp
# Traemos los archivos descargados en la imagen anterior.
COPY --from=build /app/ziupdater-1.1.1.1.jar ziupdater-1.1.1.1.jar
COPY --from=build /app/tzdata-latest.tar.gz tzdata-latest.tar.gz

# Verificamos la versión anterior, actualizamos con lo último, y mostramos la versión actualizada.
RUN java -jar ziupdater-1.1.1.1.jar -V \
    && java -jar ziupdater-1.1.1.1.jar -l file:tzdata-latest.tar.gz \
    && java -jar ziupdater-1.1.1.1.jar -V

# Eliminamos los archivos descargados.
RUN rm tzdata-latest.tar.gz && rm ziupdater-1.1.1.1.jar

# Exponer el puerto 9290 donde nebula enviara el trafico a la aplicacion
# Exponer el puerto 9800 para que los componentes de nebula puedan acceder a las metricas de la JVM
EXPOSE 9290 9800

WORKDIR /app

# Descargo las dependencias de la aplicación (si aplica)
# RUN apt update -y \
#    && apt install curl -y

# Habilito Jemalloc para mejorar el manejo de memoria nativa
RUN apt update -y && apt install -y dpkg-dev
RUN apt update -y && apt install -y libjemalloc2 libjemalloc-dev

# Directorio para guardar logs del GC
RUN mkdir -p /app/logs && chown $NBL_USER_ID /app/logs

# Luego de realizar todas las acciones requeridas con el usuario root cambiamos el usuario a uno NO Root para que la aplicación se ejecute con este usuario
# La imagen base hub.nebula/java-XX-corretto:X.X.X ya vienen con un usuario creado listo para usar, sólo es necesario ejecutar la siguiente instrucción
USER $NBL_USER_ID

# En este caso, copiamos de la imagen descartable el jar generado y el jar de newrelic a usar como agent que necesitamos en la imagen
# HELP: COPY <archivo_de_directorio_de_build> <destino_dentro_de_la_imagen_*final*>
COPY --from=build /app/target/java-template.jar java-template.jar

# El script de startup se usa para ejecutar la aplicación parametrizada por el archivo jvm-args que es inyectado por nebula según el ambiente
COPY support/nebula/startup .

# Esta linea es la que ejecuta nuestra app y se recomienda ejecutar con CMD para propagar SIGTERM a la aplicacion y
# comenzar el proceso de graceful shutdown.
CMD ["/bin/bash", "-c", "./startup"]
