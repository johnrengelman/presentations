## Getting Started w/ Gradle

----

### Installing Gradle

1. Install GVM <!-- .element: class="fragment" data-fragment-index="1" --> - http://gvmtool.net/
1. <!-- .element: class="fragment" data-fragment-index="2" --> `gvm install gradle 2.0`

----

### Starting a project

1. <!-- .element: class="fragment" data-fragment-index="1" -->`mkdir todo && cd todo`
1. Initialize project <!-- .element: class="fragment" data-fragment-index="2" -->
   * Create & edit <!-- .element: class="fragment" data-fragment-index="3" -->`build.gradle`
   * <!-- .element: class="fragment" data-fragment-index="4" -->`gradle init --type groovy-library`
   * Convert existing Maven pom.xml: <!-- .element: class="fragment" data-fragment-index="5" -->`gradle init`

Note: --type pom inferred if pom.xml exists

----

### Command Line Gradle

----

* List Available tasks

```bash
$ gradle tasks
```

```bash
:tasks

------------------------------------------------------------
All tasks runnable from root project
------------------------------------------------------------

Build tasks
-----------
assemble - Assembles the outputs of this project.
build - Assembles and tests this project.
buildDependents - Assembles and tests this project and all projects that depend on it.
buildNeeded - Assembles and tests this project and all projects it depends on.
classes - Assembles classes 'main'.
clean - Deletes the build directory.
jar - Assembles a jar archive containing the main classes.
testClasses - Assembles classes 'test'.

Build Setup tasks
-----------------
init - Initializes a new Gradle build. [incubating]
wrapper - Generates Gradle wrapper files. [incubating]

Documentation tasks
-------------------
groovydoc - Generates Groovydoc API documentation for the main source code.
javadoc - Generates Javadoc API documentation for the main source code.

Help tasks
----------
dependencies - Displays all dependencies declared in root project 'todo'.
dependencyInsight - Displays the insight into a specific dependency in root project 'todo'.
help - Displays a help message
projects - Displays the sub-projects of root project 'todo'.
properties - Displays the properties of root project 'todo'.
tasks - Displays the tasks runnable from root project 'todo'.

Verification tasks
------------------
check - Runs all checks.
test - Runs the unit tests.

Rules
-----
Pattern: clean<TaskName>: Cleans the output files of a task.
Pattern: build<ConfigurationName>: Assembles the artifacts of a configuration.
Pattern: upload<ConfigurationName>: Assembles and uploads the artifacts belonging to a configuration.

To see all tasks and more detail, run with --all.

BUILD SUCCESSFUL

Total time: 1.951 secs
```

Note: use --all to show dependencies

----

* Using the Gradle wrapper

```bash
$ ./gradlew tasks
```

Note: create with gradle wrapper, use to sync gradle versions to team & CI

----

### Gradle Tasks

----

* Single, atomic piece of work
* Consists of a list of "actions"
  * Each "action" is a `org.gradle.api.Action`
  * `Closure` is coerced into `Action`

```groovy
task helloWorld { //defines a new task with name 'helloWorld'
  doLast { //add action to the end of the action list
    println 'Hello World!'
  }
}
```

----

* Build script is backed by a Gradle `Project` instance
  * `task` is method from Gradle DSL (`org.gradle.api.Project.task(String name, Closure configure)`)
* `<< {..}` is shorthand for `doLast {..}`

```groovy
task helloWorld << {
  println 'Hello World!'
}
```

----

### Task Dependencies & Ordering

* `dependsOn` creates an execution dependency
* All execution dependencies of a task must also be executed and completed before the task

```groovy
task a << { println 'a' }
task b(dependsOn: a) << { println 'b' }
```

```bash
$ gradle b
:a
a
:b
b
```

----

* `finalizedBy` creates a finalization dependency
* The finalizer task is added if the finalized task is present in the task graph
  * finalizer will execute after the finalized even when finalized fails
  * finalizer will not execute if finalized did no work or was UP-TO-DATE

```groovy
task cleanup << { println 'cleanup' }
task run << println 'run'

run.finalizedBy cleanup // 'run' is the "finalized" task, 'cleanup' is the "finalizer"
```

```bash
$ gradle run
:run
run
:cleanup
cleanup
```

----

* Task ordering allows you to specify ordering w/ creating an execution dependency
* `mustRunAfter`
  * Task A runs after Task B only if both are in the task graph
  * Always respected
* `shouldRunAfter`
  * Same as `mustRunAfter` but less strict
  * Ignored if
    * Creates an ordering cycle
    * When executing in parallel and all other dependencies are completed except the `shouldRunAfter`

----

```groovy
task first << { println 'first' }
task second << { println 'second' }
second.mustRunAfter first
```

```bash
$ gradle second
:second
second

$ gradle first
:first
first

$ gradle second first
:first
first
:second
second
```

----

* Gradle is Groovy!

```groovy
task ready() << {
  println 'Ready'
}

3.times { num ->
  task "count${num+1}" << {
    println num+1
  }
}

task go() << {
  println 'Go!'
}

count3.dependsOn ready
count2.dependsOn count3
count1.dependsOn count2
go.dependsOn count1

task countdown(dependsOn: go)
```

----

```bash
$ gradle countdown -q
Ready
3
2
1
Go!
```

----

* Task name shortening

```bash
$ gradle hW
:helloWorld
Hellow World!

BUILD SUCCESSFUL

Total time: 0.673 secs
```

----

* Excluding Tasks

```bash
$ gradle countdown -q -x ready
3
2
1
Go!
```

----

* Task Rules
  * Dynamically creates tasks based on the requested task name

```groovy
tasks.addRule('Pattern: countdown<From>') { String taskName ->
  if (taskName.startsWith('countdown')) {
    task(taskName) << {
      ((taskName - 'countdown').toInteger()..0).each {
        println it
      }
    }
  }
}
```

```bash
$ gradle countdown5
:countdown5
5
4
3
2
1
0

$ gradle countdown2
:countdown2
2
1
0
```

----

* Passing Properties
  * `-Dmyprop=myvalue`: JVM System Properties for Gradle JVM
  * `-Pprojectprop=projectval`: Gradle Project properties
* Specify Build File (default: `build.gradle`)
  * `-b <path to build file>`
* Logging
  * `-i, --info`: Log more Gradle information
  * `-d, --debug`: Log more information than Info
  * `-s, --stacktrace`: Log stacktrace on error
  * `-q, --quiet`: Log errors only (or printlns)

----

### Gradle Daemon

* Reduce startup hit by keeping a live JVM
* Expires after 3 hours
* `gradle --daemon`
* `echo "org.gradle.daemon=true" >> ~/.gradle/gradle.properties`

----

### Debugging Gradle builds

```bash
$ gradle build -Dorg.gradle.debug=true
```

* Attach debugger to port 5005.
