# KaosKit
The KaosKit is a destructive testing framework. It is intended to provide a simple means of forcing systems to run
under difficult conditions, to highlight issues such as race conditions or improper exception handling which
may occour only rarely under normal testing and UAT conditions, but which can cause serious issues when running
in production.

Kaoskit is intended to be non-invasive, and to be usable without changing the code of the target application,
in order to allow its' usage with existing codebases. Although it can be used simply by adding log appenders
and setting some system properties for configuration, it is a framework which allows the user to set up a
tailored set of condtions to inflict on a system, and is especially powerful when combined with Aspect Oriented
Programming libraries.

## Installation
At present the project only available as source via GitHub. The project consists of a parent project and a number
of sub modules as follows.

+ kaos-core : Provides the code definitions and implementations of KaosKit. The `Behaviour`, `Modifier` and
`Strategy` interfaces and implementations are found here.
+ kaos-appender-log4j : Provides a `log4j 1.x` compatible log appender which can be configured to run a `Behaviour`
only `Strategy`
+ kaos-appender-log4j2 : Provides a `log4j 2.x` compatible log appender which can be configured to run a `Behaviour`
only `Strategy`
+ kaos-appender-logback : Provides a `logback` compatible log appender which can be configured to run a `Behaviour`
only `Strategy`
+ kaos-appender-example : Provides a simple example application for demonstrating KaosKit configuration and beaviour.
It provides a badly written Scala application which can be run with KaosKit using `logback` based appenders or using
AOP. It demonstrates the use of custom configuration classes to provide appender or pointcut specific strategies.

## Building
The projects are built using Gradle. As `kaos-core` is the basis of the other projects, it is important to build
this first, and very likely necessary to pubish this to your local Maven repository in order to build the other
projects. In addition, the unit tests for the other projects may reference the test classes of `kaos-core`
(the project is configured to publish both the jar and test jar). Similarly, the `kaos-appender-example` will
require `kaos-appender-logback` in order to build.

The projects are configured to use the Gradle `maven-publish` plugin, so building should simply be a case of running:

    gradle -p kaos-core publishToMavenLocal
    gradle -p kaos-appender-log4j publishToMavenLocal
    gradle -p kaos-appender-log4j2 publishToMavenLocal
    gradle -p kaos-appender-logback publishToMavenLocal

in order to make use of the KaosKit in a project or to run the examples.

## Usage
### Behaviours, Modifiers and Strategies
The key concepts of KaosKit are defined by the `Behaviour`, `Modifier` and `Strategy` interfaces. They are
used to define what kind of actions will be taken at runtime.

#### Strategy
A `Strategy` defines collections of "before" `Behaviour` instances, "after" `Behaviour` instances and `Modifier`
instances. In terms of the log appender implementations, "before" behaviours are executed before the log line
is written, "after" behaviours are executed after, and modifiers have no particular meaning. In AOP of course,
it will depend on how a given aspect is defined, but clearly the model would be to use "before" behaviours
with `@Before` pointcuts, "after" with `@After` pointcuts modifiers (and indeed optionally before and after
behaviours) with `@Around` pointcuts. For example:

    @Pointcut( value = "execution (* processMessage(String))" +
                        "&& args(msg)", argNames = "msg")
    public void readLinePointcut(String msg){}

    @Around("readLinePointcut(msg)")
    public Object modifyMessage( ProceedingJoinPoint pjp, String msg ) throws Throwable{
        Strategy<String> strategy = ConfigurationFactory.getInstance().getConfiguration().strategyForName("msg-aspect");
        if(!isReadStarted){
            strategy.start();
            isReadStarted = true;
        }
        for(Behaviour before : strategy.beforeBehaviours()){
            before.execute();
        }
        String work = msg;
        Object[] args = pjp.getArgs();
        for(Modifier<String> m : strategy.modifiers()){
            work = m.modify(work);
        }
        args[0] = work;
        Object res = pjp.proceed(args);
        for(Behaviour after : strategy.afterBehaviours()){
            after.execute();
        }
        return res;
    }

#### Behaviour
A `Behaviour` simply defines an action that can be taken, such as calling `sleep` on the current thread,
interrupting other waiting threads, or throwing an exception. `Behaviour` defines both an `void execute()` and
`<T> void execute(T arg)`. The latter being used for exaple to create conditional execution of behaviours
(via the `IfBehaviour` wrapper class).

#### Modifier
A `Modifier` defines and action which can take an input of some type `T` and return an output of the same type.
As an example use case, this could be used to simulate a change to the format of data received from a 3rd party,
for example an addition of a field to Json data returned by a REST service call.

#### Conditionals, If and Sometimes
Although not in the core interfaces, it is worth being aware of the flow control implementation available
as well. Both `Behaviour` and `Modifier` instances can be wrapped in flow control constructs. The `Sometimes`
constructs provide a wrapper which will choose to execute the wrapped interface randomly based on a probability
set in the constructor. The `IfBehaviour` and `IfModifier` take a condition, a true and a false. If the condition
evaluates to true, the wrapped 'true' behaviour/modifier is called, otherwis the 'false' is called.

The condition is a `Behaviour` which is called with the :
        @Override
        public boolean execute( T returnValue ) throws Exception

method. The condition will return true if the `returnValue` is accepted by the condition. See the various
implementations in the `com.polydeucesys.kaos.core.behaviours.conditions` package for examples.

#### Component Lifecycle
The supplied `Behaviour` and `Modifier` implementations operate independantly of any outside input or
influence. However, in order to support instances which may use resources, all components implements the
`Lifecycle` interface, which defines a start` and `stop` method. These methods are called in the log
appender implementations at the corresponding points in the appender lifecycle. When using AOP
or other implementations it is possible/advisable to call these lifecycle mathods as appropriate.

### Configuration
Configuration of KaosKit is done by either supplying a concrete implementation of the `Configuration` interface,
and setting the  `com.polydeucesys.kaos.conf.class` to this class name property, or by setting a number of
system properties for the default implementation of `Configuration`.

#### Configuring With Properties
The default implementation of `Configuration` can be configured with the following properties:
+ `com.polydeucesys.kaos.conf.default.before` - A comma separated list of one or more of `sleep`,`throw`, or `interrupt`
+ `com.polydeucesys.kaos.conf.default.after` - A comma separated list of one or more of `sleep`,`throw`, or `interrupt`
+ `com.polydeucesys.kaos.conf.default.sleep.params` - A `;` separated list of parameters (see below).
+ `com.polydeucesys.kaos.conf.default.throw.params` - A `;` separated list of parameters (see below).
+ `com.polydeucesys.kaos.conf.default.interrupt.params` - A `;` separated list of parameters (see below).
+ `com.polydeucesys.kaos.conf.default.monitor.class` - The default `Monitor` implementation to be used. Monitors
are used to provide feedback on the behaviour of KaosKit. See the javadoc of the `Monitor` interface.

Parameters are set in a `;` separated list of `key=value` pairs. For the `sleep`, `interrupt` and `throw`
behaviours, the following *common* parameters can be set:
+ `odds` - a float giving the % chance of the behaviour being executed on any given call.
+ `matches` - a string representing a regex which will be compared against the input in the after behaviour.

Note that the `matches` setting in particular is intended log appender usage, as it requires that the
behaviour receives an `executeAfter` call with a `String` argument.

For each behaviour, the specific parameters are as follows:
+ `sleep` - Takes `max` an integer argument specifying the max time in millis that the behaviour will `sleep`
for on any given call. See the `RandomSleeper` behaviour.
+ `throws` - A comma separated list of the full class names of the exceptions which may be thrown on any call.
See the `ExceptionThrower` behaviour.
+ `interrupt` - Takes `states`, a comma separated list of one or more of `Thread.State` names. Threads in a
matching state will be sent an interrupt.  Additionally takes `first`, a boolean indicating of only the
first matching thread should be interrupted or if al matching threads should be interrupted.

#### Configuring with Configuration classes
Provided an instance of the class specified in the `com.polydeucesys.kaos.conf.class` can be instantiated, this
class will be used to provide any requested `Strategy`. See for example the implemenation used for the log
appender unit tests:

    public class UnitTestConfigurationImpl implements Configuration {

        private final Map<String, Strategy> strategies = new HashMap<String, Strategy>();

        public UnitTestConfigurationImpl(){
            Monitor m = new StringListMonitor();
            StrategyBuilder b = new KaosStrategy.KaosStrategyBuilder();
            b.setName("test1");
            b.setMonitor(m);
            RandomSleeper s1 = new RandomSleeper(50L);
            s1.setMonitor(m);
            List<Exception> le1 = new LinkedList<>();
            le1.add(new WebServiceException());
            le1.add(new FileSystemNotFoundException());
            ExceptionThrower e1 = new ExceptionThrower(le1);
            e1.setMonitor(m);
            SometimesBehaviour se1 = new SometimesBehaviour(1.0f, e1);
            se1.setMonitor(m);
            b.addBeforeBehaviour(s1);
            b.addAfterBehaviour(se1);
            strategies.put("test1", b.build());
            StrategyBuilder b2 = new KaosStrategy.KaosStrategyBuilder();
            b2.setName("test2");
            b2.setMonitor(m);
            RegexBehaviour re = new RegexBehaviour("^DoThrow");
            IfBehaviour<String> ifs = new IfBehaviour<>(re, e1, new DoNothing.Behaviour<>());
            ifs.setMonitor(m);
            b2.addAfterBehaviour(ifs);
            strategies.put("test2", b2.build());
        }

        @Override
        public Map<String, Strategy> strategiesByName() {
            return strategies;
        }

        @Override
        public Strategy strategyForName(String s) {
            return strategies.get(s);
        }
    }

### Log4J 1.2.x, Log4j 2.x, Logback
An implementation for `com.polydeucesys.kaos.KaosAppender` is provided for each of the three major logging
frameworks. In each case, the appender will run both its' before and after behaviours on a call to append.
The appender will not call itself recursively. If it is configured to have an ExceptionThrower behaviour, then
this must throw a RuntimeException (as the append method does not declare checked exceptions). RuntimeExceptions
will be rethrown by the appender or appropriate error handling class depending on log framework.

### Aspect Oriented
In order to make use of facilities like Modifier instances and have more fine grained control over exception
throwing Aspect Oritented tools or similar bytecode level integrations can be used. For pre-existing installations
run time weaving can be used to enable the KaosKit strategies in various pointcuts.

See the `kaos-appender-example` for examples of usage.


### Example Project
The (slightly misnamed) `kaos-appender-example` project provides a set of examples of how to configure and use
the KaosKit. The example itself is a simple Scala application which forms a cluster based on a Well Known
Address List of members, and a client appliction which communicates with the cluster, load balancing its'
messages by sending them to random cluster members. When a cluster member receives a client message it
propagates it to the other cluster members. In addition he cluster members track each others 'aliveness'
by using heartbeat messages.

*The code is intentionally not that well written*

The idea being that we can demonstrate problems which pass unit testing and can run happily for some extended
time, but which could eventually cause serious issues. There are several Gradle tasks which can be used to run
the example servers and client in different modes.

    runStringServerOne - Start the first server without the KaosKit enabled
    runStringServerTwo - Start the second server without the KaosKit enabled
    runStringServerThree - Start the third server without te KaosKit enabled

    runStringServerOneKaos - Start the first server with KaosKit enabled as log appenders
    runStringServerOneKaosDebug - KaosKit enabled as log appenders and remote debug options

    runStringServerOneAspectKaos - Start the first server with KaosKit enabled via AOP
    runStringServerOneAspectKaosDebug - KaosKit via AOP and remote debug (suspend=y) enabled

With KaosKit enabled, the servers very quickly run in to trouble. By changing the behaviours and modifiers in
the `com.polydeucesys.kaos.example.ExampleConfigurationImpl` you can experiment with how different problems
with the server can impact the cluster.


#### Some notes on AOP
Having not previously used AspectJ much meant there were some tiny things to be aware of in order to get
it working. So as a note to save some searching on the internet:

1. Note how the aspectj weaving jar is referenced in the `build.gradle`:
    dependencies{
        ...
        aspectj group: 'org.aspectj', name: 'aspectjweaver', version: '1.8.10'
        compile configurations.aspectj.dependencies
        ...
    }
By being is' own configuration, it can later be referenced in the task to run the server with AOP as follows:
    jvmArgs=['-javaagent:' + file(configurations.aspectj.asPath)

2. Note in the aop.xml file:
    <aspectj>
        <aspects>
            <aspect name="com.polydeucesys.kaos.example.aspect.StringSharerAspects"></aspect>
        </aspects>
        <!-- You can include "-debug"  on the weaver options to see what classes get weaved and what do not -->
        <weaver options="-XnoInline">
            <!-- Weaver also needs the aspect class itself! -->
            <include within="com.polydeucesys.kaos.example.aspect.StringSharerAspects"/>
            <include within="com.polydeucesys.kaos.example.*"/>
            <include within="com.polydeucesys.kaos.example.StringSharer$StringServerListenThread"/>
        </weaver>
    </aspectj>
You must include your aspect class itself under the weaver. Setting `-debug` in the weaver will show which
classes get woven and which do not. This can be critical when figuring out why aspects do not seem to work.

3. Only the classes in the aop.xml will be affected. So you can put pointcuts on things like `println` or `read`
methods on streams and not have them blow up everywhere, but in calls directly in your code.

4. Not you can pointcut a `call` to something you not specified in the `weaver` section of `aop.xml`, as the
call can occour in a woven class, but not on an `execution`, as the execution is in the target.