## Multi-Project Builds

----

Define sub-projects in `settings.gradle`

```bash
+<projectDir>
+-- api/
|   +-- build.gradle
+-- client/
|   +-- build.gradle
+-- server/
    +-- build.gradle
```

```groovy
// settings.gradle
include "api", "client", "server"
```

```bash
$ gradle projects
------------------------------------------------------------
Root project
------------------------------------------------------------

Root project 'todo'
+--- Project ':api'
+--- Project ':client'
\--- Project ':server'
```

----

### Declaring dependencies on projects

```groovy
// server/build.gradle
dependencies {
  compile project(":api") //depends on artifacts of the 'default' configuration
}
```

Note: project paths start with : then name

----

### Enabling Parallel Builds

```bash
$ gradle build --parallel
```

OR

```bash
// gradle.properties or ~/.gradle/gradle.properties
org.gradle.parallel=true
```
