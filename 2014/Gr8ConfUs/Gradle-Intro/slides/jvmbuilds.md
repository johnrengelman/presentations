## Basics of a JVM Project Build

![Gradle JVM Build](images/gradle-build.jpg)

----

* Build a Java/Groovy project

```bash
$ gradle build
```

```bash
:compileJava UP-TO-DATE
:compileGroovy
:processResources UP-TO-DATE
:classes
:jar
:assemble
:compileTestJava UP-TO-DATE
:compileTestGroovy
:processTestResources UP-TO-DATE
:testClasses
:test
:check
:build

BUILD SUCCESSFUL

Total time: 6.213 secs
```

----

### Adding Dependencies

* Dependencies are added to a `Configuration`
* JVM projects have
  * compile, runtime, testCompile, testRuntime
* Configurations can extend other repositories
  * compile extends runtime

```groovy
dependencies {
  compile 'org.codehaus.groovy:groovy-all:2.3.3'
  runtime 'mysql:mysql-connector-java:5.1.31'

  testCompile 'org.spockframework:spock-core:0.7-groovy-2.0'
  testRuntime 'com.h2database:h2:1.4.180'
}
```

----

### Locating Dependencies

* Gradle supplies some default repositories to search:
  * jcenter, mavenCentral, mavenLocal
* Can also look at custom maven, ivy, and file system paths

```groovy
repositories {
  mavenLocal()
  jcenter()
  mavenCentral()
  maven {
    url 'http://maven.myhost.com/'
    credentials {
      username 'username'
      password 'password'
    }
  }
  ivy {
    url 'http://ivy.myhost.com'
  }
  flatDir {
    dir file('repo')
  }
}
```

----

### Project structure

* Follows the Maven convention

```bash
+ <projectDir>/
+-- src/
    +-- main/
    |   +-- java/
    |   +-- groovy/
    |   +-- resources/
    +-- test/
        +-- java/
        +-- groovy/
        +-- resources/
```

* But can be configured

```groovy
sourceSets.main.java.srcDirs = ['src']
sourceSets.test.java.srcDirs = ['test']
```

----

* Project structure is tied to `SourceSets`
* JVM projects have 2 - `main` & `test`
  * Each `SourceSet` starts w/
    * `java`
    * `resources`

```groovy
apply plugin: 'java'

sourceSets {
  main {
    java { ... }
    resources { ... }
  }
  test {
    java { ... }
    resources { ... }
  }
}
```

----

* Plugins can extends the SourceSet
  * Gradle plugin adds `<sourceSet>/groovy`
  * Scala adds `<sourceSet>/scala`

```groovy
apply plugin: 'groovy'
apply plugin: 'scala'

sourceSets {
  main {
    groovy { ... }
    scala { ... }
  }
  test {
    groovy { ... }
    scala { ... }
  }
}
```

----

* Can declare additional `SourceSets` (i.e "intTest")

```groovy
sourceSets {
  intTest
}
```

* Gradle automatically creates compile/runtime configurations for source sets

```groovy
// No need to declare this
configurations {
  intTestCompile
  intTestRuntime
}
```

Note: runtime always extends compile
