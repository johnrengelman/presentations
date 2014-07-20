## Building Grails Projects

----
## Quick Start

```
buildscript {
  repositories { jcenter() }
  dependencies { classpath 'org.grails:grails-gradle-plugin:2.1.0' }
}

apply plugin: 'grails'

grails {
  grailsVersion '2.3.8'
  groovyVersion '2.1.9'
}

repositories { grails.central() }

dependencies {
  bootstrap 'org.grails.plugins:tomcat:7.0.50.1'
}
```

Note:
Stop-gap until Grails 3
Grails currently uses Gant

----
## Configuration Options

```
grails {
  grailsVersion = '2.3.8'
  groovyVersion = '2.1.9'
  springLoadedVersion = '1.1.3'
}
```

----
## Resolving Grails Plugins

```
repositories {
  grails.central() // Add the Grails Central Maven repo
}

dependencies {
  bootstrap 'org.grails.plugins:tomcat:7.0.50.1'
  // bootstrap, compile, runtime, test, provided
  // MUST add 'org.grails.plugins'
}
```

----
## Useful Tasks

| Task | Action |
| ---- | ---------------:|
| init | creates a new Grails application project |
| init-plugin | creates a new Grails Plugin project |
| run | $ grails run-app |
| test | $ grails test-app  |
| war | $ grails war <outputFile> |
| packagePlugin | $ grails package-plugin |

----
## Build Pipeline Participation

| Task | Dependent Tasks |
| ---- | ---------------:|
| clean | delete buildPlugins/ dir |
| assemble | war, packagePlugin |
| check | no default |
| build | assemble, check |

Note: No 'classes' by default

----
## Run Any Grails Script

`$ gradle grails-<script-name>`

Note:
Need to run a Grails task before getting plugin scripts

----
## Configure Grails tasks

```
run {
  env = 'prod'
  args += '--stacktrace'
}
```

----
## Question: How Does it work?

<br><br>

Answer:

[Grails Launcher](https://github.com/grails/grails-launcher)

----
Gradle is *__NOT__* building Grails project

----
What Gradle Does:

1. Resolve dependencies
1. Create GrailsLaunchContext
1. Serialize to file
1. Fork GrailsLauncher.Main in new JVM

----
What GrailsLauncher Does:

1. Deserialize GrailsLaunchContext from file
2. Create ClassLoader with bootstrap classpath from context
3. Load GrailsScriptRunner class
4. Call GrailsScriptRunner.executeCommand(scriptName, [args], env)

----
## Plugin Gotchyas

----
+ Do **NOT** apply Java/Groovy plugin
+ Do **NOT** apply plugins that apply the Java/Groovy plugin
+ Support for plugin publishing lacks
  + Grails Release Plugin generates POM from BuildConfig
  + Issues with generating plugin.xml, pom.xml, and packagePlugin UP-TO-DATE

----
## Demo

Note:
Git grails0 -> basic config, then run gradle init
Git grails1 -> gradle run
Git grails2 ->
  gradle grails-create-controller -PgrailsArgs='TodoController',
  show reloading (render "Hello")
  gradle assemble
