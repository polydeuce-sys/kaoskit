### String Sharer
The StringSharer is a simple distributed applicaiton where the server can propagate messages they receive from
a client application to other server in the cluster. The client application will randomly pic as server to message.
The server receiving the message will propagate it to the oter servers in the cluster. The cluster is defined
as a Well Know Address list passed to the cluster members at startup. Liveness in the cluster is tracked
by a heartbeat betweeen members.

#### Logger Example
By configuring Kaos appenders, we can make use of Behaviours like thread interruption, random sleeps,
and exception throwing. By setting specific loggers for specific classes (or packages), we can target different
behaviours to different parts of an application. In this example we use a custom Configuration for our Kaos
Strategies. This would normally be packaged in a separate jar, so that in the test instance, we would run with
the KaosKit and this Configuration implementation on the classpath, with a specific logger config, whereas in
Production, these jars would not be deployed, and a normal logging configuration would be used.

#### Aspect Oriented Example
The Logger approach can cover a number of scenarios, and even trigger behaviours based on the specific log
message seen, however it does not allow the use of Modifier instances, or allow targeting of specific methods.
By using Aspect Oriented Programming, we can use specific targeting of methods and even run Modifier instances
to change the values returned from methods. This could be used to change the JSON returned by a 3rd party
web service for example, to simulate some version change or similar or alter a message type on a JMS.

The AOP example includes a simple modifier, which adds a field to the JSON format of messages, and an exception
thrower which throws an IOException on attempts to write messages to other servers.

The Modifier could be used to simulate a change to a 3rd party service used by an application, where there could
potentially be changes to the data supplied by that service. In our case, it highlights the fragility of using
a regex to parse Json. Using a json library which would ignore unrecognized properties (logging as warnings)
would be useful here.

The ExceptionThrower highlights the lack of proper exception checking in our code. Any exception thrown within
out heartbeat thread for example results in other servers regarding our node as dead. Since the exception could
actually be cause by a remote server, it would indicate a bad node could bring down other nodes accross the
cluster. Better exception handling is clearly needed.

Again, like the logging example, the AOP example is ultimately non-invasice and does not require changes to our
application in order to run, but rather some runtime configuration, which can simply not be present in production.


