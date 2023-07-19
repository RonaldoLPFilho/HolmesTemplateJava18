# Primeros pasos y ejemplos

En la carpeta **src/examples** se encuetran algunos ejemplos de uso de algunas de las features del template. En el caso de usar este proyecto como template es necesario **eliminar la carpeta *examples* completa**.

A continuación se describen los pasos que se deben seguir para personalizar el template para la creación de una nueva aplicación.

1. Crear nuevo repositorio usando el botón "Use this template".

   <img width="917" alt="Screen Shot 2022-01-25 at 16 55 52" src="https://user-images.githubusercontent.com/1668933/151247586-cad2ab90-ba76-4d30-94b6-3e8bb3fb172a.png">
2. Eliminar la carpeta **examples** que está dentro de **src**.
3. Eliminar el archivo CODEOWNERS.
4. Quitar del pom.xml el build-helper-maven-plugin que se encarga de agregar las carpetas de examples como sources a buildear por maven.
5. Modificar el nombre de la aplicación en:
    - artifactId dentro del pom.xml
    - application.clientId en el archivo src/main/resources/despegar.properties
    - APP_NAME dentro de la clase ServiceName
6. Configurar Server (para mas información ver la [documentación](JettyServer.md))
    - Context path y puerto.
    - Tipo de executor y tamaño del pool.
    - Tipo de cola y tamaño.
7. Configurar log (para mas información ver la [documentación](Logging.md))
    - Crear la carpeta en nuestro server de log donde se va a escribir el log.
    - Iniciar proceso para que el server reciba nuestro log en un puerto determinado (definir algún puerto que sepamos que no esta en uso).
    - Luego ir al logback.xml perteneciente al ambiente que estamos configurando y reemplazar los valores {host} y {port} por los que correspondan.
    - Repetir este procedimiento por cada uno de los ambientes que queremos configurar.
8. Configurar New Relic en los distintos ambientes (para mas información ver la [documentación](NewRelic.md))
    - Nombre de aplicación (distinto por ambiente)
    - License key
    - Configuración general: Que excepciones reportar? que http status reportar? log level del agente, etc.
9. Configurar métricas que exponemos para Prometheus (para mas información ver la [documentación](Prometheus.md))
10. (Plus si queremos usar Nebula) Modificar el nombre del jar que vamos a generar en el script support/nebula/startup y el deployment-template. (para mas información ver la [documentación](Nebula.md))
