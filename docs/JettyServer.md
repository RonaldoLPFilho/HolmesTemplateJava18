# Jetty Server
## Configuración
Podemos configurar Jetty Server a través de properties de Spring.

### Configuraciones soportadas

| Property                                                                  | Tipo de dato       | Descripción | Default |
|---------------------------------------------------------------------------| --------------     | ----------- | ----------- |
| `server.port`                                                             | Integer (opcional) | Puerto http  | `9290` |
| `server.jetty.context-path`                                               | String (opcional)  | Context path | `/` |
| `server.jetty.thread-pool.[executor type]`                                | Property           | ThreadPool de Jetty <br> Keys posibles: <br> - `executor` instancia `org.eclipse.jetty.util.thread.ExecutorThreadPool` con `java.util.concurrent.ThreadPoolExecutor`, requiere la config `server.jetty.thread-pool.executor.*` <br> - `queued` instancia `org.eclipse.jetty.util.thread.QueuedThreadPool`, requiere la config `server.jetty.thread-pool.queued.*` | - |
| `server.jetty.thread-pool.executor.name`                                  | String             | Nombre del thread principal de jetty | - |
| `server.jetty.thread-pool.executor.core-pool-size`                        | Integer            | Cantidad de threads que se mantendrán en el pool, incluso estando inactivos | - |
| `server.jetty.thread-pool.executor.maximum-pool-size`                     | Integer            | Cantidad maxima de threads disponibles para el pool | - |
| `server.jetty.thread-pool.executor.keep-alive-time`                       | Integer            | Cuando `maximum-pool-size` es mayor que `core-pool-size`, es el tiempo máximo en ms que los threads inactivos esperan nuevas tareas antes de terminar | - |
| `server.jetty.thread-pool.queued.name`                                    | String             | Nombre del thread principal de jetty | - |
| `server.jetty.thread-pool.queued.min-threads`                             | Integer            | Cantidad minima de threads del pool | - |
| `server.jetty.thread-pool.queued.max-threads`                             | Integer            | Cantidad maxima de threads (capacidad) del pool | - |
| `server.jetty.thread-pool.queued.idle-timeout`                            | Integer            | Tiempo máximo de inactividad del thread en ms | - |
| `server.jetty.thread-pool.[executor type].queue.[queue type]`             | Property           | Implementación de BlockingQueue usada en el thread-pool <br> Keys posibles: <br> - `linked` instancia `java.util.concurrent.LinkedBlockingQueue`, requiere la config `server.jetty.thread-pool.[server.jetty.thread-pool.type].queue.linked.*` <br> - `array` instancia `java.util.concurrent.ArrayBlockingQueue`, requiere la config `server.jetty.thread-pool.[server.jetty.thread-pool.type].queue.array.*` <br> - `jetty-array` instancia `org.eclipse.jetty.util.BlockingArrayQueue`, requiere la config `server.jetty.thread-pool.[server.jetty.thread-pool.type].queue.jetty-array.*` <br> - `synchronous` instancia `java.util.concurrent.SynchronousQueue`, requiere la config `server.jetty.thread-pool.[server.jetty.thread-pool.type].queue.synchronous.*` | - |
| `server.jetty.thread-pool.[executor type].queue.linked.capacity`          | Integer (opcional) | Capacidad de la cola | `Integer.MAX_VALUE` |
| `server.jetty.thread-pool.[executor type].queue.array.capacity`           | Integer            | Capacidad de la cola | - |
| `server.jetty.thread-pool.[executor type].queue.array.fair`               | Boolean (opcional) | Política de acceso <br> `fair=true`: los accesos a la cola para subprocesos bloqueados en la inserción o eliminación se procesan en orden FIFO <br> `fair=false`: la orden de acceso sin especificar | `false` |
| `server.jetty.thread-pool.[executor type].queue.jetty-array.capacity`     | Integer (opcional) | Capacidad de la cola. Si no se define `max-capacity` y `grow-by`, se usa también como `max-capacity` | `128` |
| `server.jetty.thread-pool.[executor type].queue.jetty-array.grow-by`      | Integer (opcional) | Factor de crecimiento de la cola. Obligatorio si se define `capacity` y `max-capacity` | `64` |
| `server.jetty.thread-pool.[executor type].queue.jetty-array.max-capacity` | Integer (opcional) | Capacidad maxima de la cola | `Integer.MAX_VAlUE` |
| `server.jetty.thread-pool.[executor type].queue.synchronous.fair`         | Boolean (opcional) | Política de acceso <br> `fair=true`: los accesos a la cola para subprocesos bloqueados en la inserción o eliminación se procesan en orden FIFO <br> `fair=false`: la orden de acceso sin especificar | `false` |

### Ejemplos
`ExecutorThreadPool` con `LinkedBlockingQueued`
```properties
server.port=9290
server.jetty.context-path=/java-template
server.jetty.thread-pool.executor.maximum-pool-size=240
server.jetty.thread-pool.executor.core-pool-size=120
server.jetty.thread-pool.executor.keep-alive-time=60000
server.jetty.thread-pool.executor.thread-name-prefix=jetty-thread-pool
server.jetty.thread-pool.executor.queue.linked.capacity=10
```

`QueuedThreadPool` con `BlockingArrayQueue`
```properties
server.port=9290
server.jetty.context-path=/java-template
server.jetty.thread-pool.queued.max-threads=240
server.jetty.thread-pool.queued.min-threads=120
server.jetty.thread-pool.queued.idle-timeout=60000
server.jetty.thread-pool.queued.thread-name-prefix=jetty-thread-pool
server.jetty.thread-pool.queued.queue.jetty-array.capacity=120
server.jetty.thread-pool.queued.queue.jetty-array.grow-by=64
server.jetty.thread-pool.queued.queue.jetty-array.max-capacity=240
```

`ExecutorThreadPool` con `ArrayBlockingQueue`
```properties
server.port=9290
server.jetty.context-path=/java-template
server.jetty.thread-pool.executor.maximum-pool-size=240
server.jetty.thread-pool.executor.core-pool-size=120
server.jetty.thread-pool.executor.keep-alive-time=60000
server.jetty.thread-pool.executor.thread-name-prefix=jetty-thread-pool
server.jetty.thread-pool.executor.queue.array.capacity=10
server.jetty.thread-pool.executor.queue.array.fair=true
```
### Implementación
Revisar [com.despegar.javatemplate.config.server.JettyServerFactoryCustomizer](../src/main/java/com/despegar/javatemplate/config/server/JettyServerFactoryCustomizer.java)
