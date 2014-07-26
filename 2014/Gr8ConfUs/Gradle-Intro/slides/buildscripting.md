## Scripting Builds


* Extend builds through plugins
* Plugins come in 3 flavors
  * Core Plugins: shipped w/ Gradle
  * Script Plugins
  * 3rd Party Plugins: resolved from outside sources

----

### Creating Script Plugins

* Configure project, create tasks and set up dependencies in a filed
* Apply the file to your project

```groovy
//docs.gradle
task javadocJar(type: Jar, dependsOn: javadoc) {
    classifier = 'javadoc'
    from 'build/docs/javadoc'
}

task sourcesJar(type: Jar) {
    classifier = 'sources'
    from sourceSets.main.allSource
}

build.dependsOn javadocJar, sourcesJar

//build.gradle
apply plugin: 'groovy'
apply from: file('docs.gradle')
```

----

### Adding 3rd party plugins

* Plugins must be available to Gradle itself
  * They are not project dependencies, need something else.
  * Configure Gradle's executiong using `buildscript {}`
  * Similar to configure a normal project
* Find plugins at Gradle Plugin Portal - http://plugins.gradle.org

```groovy
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath 'org.github.jengelman.gradle.plugins:shadow:1.0.2'
  }
}

apply plugin: 'java'
apply plugin: 'com.github.johnrengelman.shadow'
```

Note: classpath is only configuration available in buildscript!

----

### Gradle 2.1 Plugins DSL

* New plugin resolution via Gradle Plugin Portal & Bintray

```groovy
plugins {
  id 'com.github.johnrengelman.shadow' version '1.0.2'
}
```

----

### Classpath Isolation

* All plugins are evaluated & executed with an isolated classloader
* Each classloader inherits from its parent classloader
  * Apply a plugin in build.gradle and all subsequent script plugins can use classes from it
  * Apply a plugin in a script plugin, then those classes are isolated to that script

----

```groovy
//shadow.gradle
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath 'com.github.jengelman.gradle.plugins:shadow:1.0.2'
  }
}
import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
apply plugin: ShadowPlugin

//build.gradle
apply plugin: 'groovy'
apply from: file('shadow.gradle')

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
task customShadow(type: ShadowJar) //NoClassDefFoundException or MissingPropertyException
```
