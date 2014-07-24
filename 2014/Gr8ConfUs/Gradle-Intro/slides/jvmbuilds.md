## Basics of a JVM Project Build

![Gradle JVM Build](images/gradle-build.jpg)

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

```
+ <projectDir>/
|
+-- src/
    |
    +-- main/
    |   |
    |   +-- java/
    |   |
    |   +-- groovy/
    |   |
    |   +-- resources/
    |
    +-- test/
        |
        +-- java/
        |
        +-- groovy/
        |
        +-- resources/
```

----

* Project structure is tied to `SourceSets`
* JVM projects have 2: main & test
  * SourceSets has at minimum the java & resources dirs.
  * Plugins can extends the SourceSet
    * Gradle plugin adds `<sourceSet>/groovy`
    * Scala adds `<sourceSet>/scala`
* Can declare addition `SourceSets` (i.e "intTest")

```groovy
sourceSets {
  intTest
}
```

* Gradle create compile/runtime configurations for source sets
  * `<sourceSet>Compile` & `<sourceSet>Runtime`
  * intTestCompile & intTestRuntime
