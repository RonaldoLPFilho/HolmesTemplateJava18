# Scripts para cloudIA

## Resumen deploy
1) Crear tag en github.
2) Empacar con nombre del tag para dejarlo en nexus productivo.
   1) En este paso va a utilizar el script "build" para generar el .tar y le va a pasar como parametro la version del tag
3) Desplegar en cloudIA
   1) Va a utilizar el app/shutdown (si es que existe)
   2) Va a utilizar el app/install
   3) Va a utilizar el app/startup

## Script "build"
Principal funcion: crear un .tar para que cloudia lo pueda utilizar en el deploy.

Funcionamiento interno:
* Actualizar a java 17.
* Ejecutar mvn package para crear el .jar de la app.
* Crear carpeta "/app" que contenga el .jar generado y scripts de install/startup/shutdown para cloudIA (archivos de ambiente pendientes).
* Generar el .tar de la carpeta "/app" generada.

## Script "install"
Principal funcion: dejar la instancia configurada para poder levantar la app

Funcionamiento interno:
* Actualizar a java 17.
* Establecer configuracion de contexto como principal de la app
  * El nombre de contexto a utilizar es obtenido del cluster.info
  * (Pendiente) Se obtiene siempre de "___________" carpeta el contexto
* Crear carpeta para loguear en /home/despegar/shares/logs
* Crear carpeta para memory dumps en /home/despegar/shares/dumps

## Script "startup"
Principal funcion: Levantar la app, es el script que va a utilizar siempre cloudIA en los restarts por ejemplo

Funcionamiento interno:
* Validar si ya se esta ejecutando la app
* Ejecutar java ____.jar
  * Va a pasarle por parametros lo que se encuentre en el archivo jvm-args

## Script "snapshot" (opcional)
Principal función: Buildear el tar para Cloudia sin necesidad de crear un tag en Github y subirlo como SNAPSHOT al Nexus de dependencias. Ese snapshot podrá ser deployado en clusters de CloudIA que estén taggeados como TESTING.

Funcionamiento interno:
* Ejecuta el script de build tal cual lo haría Empacar
* Utiliza el plugin deploy-file de Maven para subir el tar generado en el paso anterior al Nexus de dependencias como Snapshot