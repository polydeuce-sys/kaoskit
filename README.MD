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
`<T> void execute(T arg)`. The latter being used to create conditional execution of behaviours
(via the `IfBehaviour` wrapper class).

#### Modifier
A `Modifier` defines and action which can take an input of some type `T` and return an output of the same type.
As an example use case, this could be used to simulate a change to the format of data received from a 3rd party,
for example an addition of a field to Json data returned by a REST service call.

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

### Log4J 1.2.x, Logback, Log4j 2.x
An implementation for `com.polydeucesys.kaos.KaosAppender` is provided for each of the three major logging
frameworks. In each case, the appender will run both its' before and after behaviours on a call to append.
The appender will not call itself recursively. If it is configured to have an ExceptionThrower behaviour, then
this must throw a RuntimeException (as the append method does not declare checked exceptions). RuntimeExceptions
will be rethrown by the appender or appropriate error handling class depending on log framework.

### Aspect Oriented
In order to make use of facilities like Modifier instances and have more fine grained control over exception
throwing Aspect Oritented tools or similar bytecode level integrations can be used.