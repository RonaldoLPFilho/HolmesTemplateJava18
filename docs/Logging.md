# Logging

Se utiliza Logback en conjunto con la libreria de Despegar [logging-appenders](https://github.com/despegar/logging-appenders),
de la cual utilizamos los siguientes features:
- UDP Appender: para poder loguear a través de UDP a un server remoto.
- One Line Exception: Formateo de excepciones en una sola línea.

Para comenzar a loguear de forma remota en los distintos ambientes, se debe modificar los valores **{host}** y **{port}** por los que correspondan 
en los distintos archivos de configuración (configuration/cloudia/{env}/logback.xml)