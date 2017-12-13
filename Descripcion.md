# Descripción Modelo Interfaz-Enrutador (Implementación Individual)

## Por: Paola Ortega Saborío

La implementación de este proyecto fue hecha en Java 9, utilizando el IDE IntelliJIdea 2017.2.5. Sin embargo, este programa se puede correr en cualquier máquina que contenga un JVM. Dado a que existieron conflictos de grupo, el programa fue diseñado lo más modular posible de tal forma que no tuviera que modificarse el código puro para ser utilizado en otros ámbitos, como una posible réplica de interfaces bajo el mismo código. 

1. *Inicialización de interfaz:* El actor principal del sistema es la clase InternalNetwork.InternalNetwork. En su constructor, se settean las variables necesarias para el funcionamiento del sistema y se inicializa un hilo encargado de levantar un servidor encargado de recibir mensajes dentro de la red local y almacenarlos en la cola. Un Usuario instancia directamente una clase interfaz, pasándole por parámetro:
 * Dirección IP virtual de la interfaz
 * Dirección MAC de la interfaz
 * Dirección IP real y puerto del nodo que contiene al shared memory
 * Puerto local donde se recibirán mensajes de otros nodos locales a la red.

  

2. *Trigger wakeUp:* El método wakeUp sirve principalmente para recorrer las dos funciones principales del nodo interfaz: checkMessageQueue() y checkMemory(). La primera se encarga de revisar la cola de mensajes que llegan de la red local, y la segunda se encarga de consultar el socket del shared memory para recibir mensajes que fueron enrutados hacia este nodo interfaz. Si los métodos no tienen mensajes aún, no hacen nada.


3. *Codificación y decodificación de tramas:* Una vez que se recibe un mensaje en cualquiera de los dos tipos de entradas, se materializa el String a un objeto tipo Mensaje. Este contiene el IP del emisor, IP del receptor, número de acción, IP de la acción (en casos especiales) y el cuerpo del mensaje. El método disassembleStringMessage se encarga de recibir dicho String y materializarlo para que sea más fácil de utilizar en el programa. El método de assembleStringMessage hace lo inverso para poder sacar un mensaje del sistema y mandarlo a través de la red. Es importante mencionar que el formato del mensaje se envía tal como se indica en el enunciado para mantener la consistencia y posible flexibilidad con otros grupos.


4. *Procesamiento de mensajes:* En el método de processMessage se toma un mensaje como parámetro y se analiza para saber el objetivo del mensaje. Primero, se compara el IPReceiver del mensaje y se compara con el IP virtual de la interfaz. Si dichos números coinciden, entonces el mensaje es para uno y simplemente se imprime en pantalla. En caso de que no, hay dos opciones: es de alguien de la red local de uno, o no. Para ambos casos, se redirige a un mismo método para mandar mensajes, con la diferencia de que se especifica si el mensaje se va a mandar localmente o no (esto para evitar la repetición de código).


5. *Envío de mensaje:* Primero, se consulta en la tabla IP (archivo de texto) a quién le debo pasar el mensaje. Para esto, la tabla contiene tuplas del IP local a buscar, a cuántos nodos de distancia están, y a quién tendría que pasarle el mensaje para llegar en esa distancia. Por lo tanto, después de consultar la tabla se me devolvería una dirección local, sea de la red local de la interfaz o de la interfaz enrutadora. Luego, se necesita el IP de verdad para realmente enviar el mensaje. Primero se revisa en el cache (Map<String,String>) local dentro de la clase. Si no se encuentra en el cache, entonces se consulta al broadcaster (archivo de texto), que para efectos de esta entrega ya tenía escritas todas las direcciones y puertos en tiempo de compilación, pero está en proceso de ser un nodo por aparte y utilizar broadcasts como su forma de comunicación. Luego, considerando el caso de si es o no local, se construye la trama en una sola hilera. Si es local, entonces se envía el mensaje dentro de el contenido de un sobre, el cual parte principalmente del formato macSendermacReceivermessage. En caso de que sea para el enrutador, se utiliza el protocolo de share;macSender;macReceiver;message. Se abre una nueva conexión con sockets y se envía el mensaje. 


6. *Servidor memoria compartida:* por aparte, está un nodo servidor que se encarga de organizar, recibir y mandar mensajes de dos nodos interfaces, cumpliendo así su rol de enrutador. Este tiene un puerto y un Map<String,ArrayDeque\<String\>\>. Se utiliza la dirección mac de los nodos como llave, y en valor se almacena una cola de mensajes que se van poniendo conforme los dos clientes llegan y los depositan. Este utiliza un protocolo cordial que se rige por dos acciones: depósito de mensaje o extracción de mensaje. Para poder depositar un mensaje, se necesita delimitar dentro de la hilera la palabra "share;" adjunto del macAddress de la persona a la que le queremos depositar el mensaje en la cola. En caso de que sea una extracción, se pasa sockets la dirección MAC de uno, y el servidor responde extrayendo y devolviendo la primera hilera de la cola, o bien "" en caso de que no hay mensajes en la cola del servidor.


