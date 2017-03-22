# KaosKit
The KaosKit is a means of testing how well a software project runs under less that idea conditions. It provides a
means to force race conditions, thread interrupts and random exceptions. Introducing this to systems can help flush out
any number of potential bugs in single JVM instances or distributed systems.

## Installation

At present the project only available as source via GitHub. The project consists of a parent pom and a number
of sub modules.
The main module is the kaos-core which provides the common classes and interfaces used to provide the
implementations of the various sub projects, such as logging framework specific appenders.


## Usage

### Configuration
The KaosKit is configured by supplying a named list of 'Strategy' instanes. A Strategy is a configured set of
"before" behaviours which occour before an action, "after" behaviours which occour after an action, and
modifiers, which can change the output of a method. Depending on the usage being made, some things may or may
not be usable. For example, when using the KaosAppender implementations, there would be no modifiers used.

By default, configuration is set using a number of system properties to define the list of before and after
behaviours (see the test classes for the log appenders for examples). The configuration class can be overridden
by setting the value of the "com.polydeucesys.kaos.conf.class" property to an appropriate implementation of
the `com.polydeucesys.kaos.core.Configuration` interface.

### Log4J 1.2.x, Logback, Log4j 2.x
An implementation for `com.polydeucesys.kaos.KaosAppender` is provided for each of the three major logging
frameworks. In each case, the appender will run both its' before and after behaviours on a call to append.
The appender will not call itself recursively. If it is configured to have an ExceptionThrower behaviour, then
this must throw a RuntimeException (as the append method does not declare checked exceptions). RuntimeExceptions
will be rethrown by the appender or appropriate error handling class depending on log framework.

### Aspect Oriented
In order to make use of facilities like Modifier instances and have more fine grained control over exception
throwing Aspect Oritented tools or similar bytecode level integrations can be used.