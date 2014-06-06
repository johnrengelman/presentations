## Building Grails Projects

----
## Quick Start

```
buildscript {
  repositories { jcenter() }
  dependencies { classpath 'org.grails:grails-gradle-plugin:2.0.1' }
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
| grails-run-app | $ grails run-app |
| grails-test-app | $ grails test-app  |
| grails-war | $ grails war <outputFile> |
| grails-package-plugin | $ grails package-plugin |

----
## Build Pipeline Participation

| Task | Dependent Tasks |
| ---- | ---------------:|
| test | grails-test-app |
| clean | grails-clean |
| check | no default |
| build | assemble, check |
| assemble | grails-war, grails-package-plugin |

Note: No 'classes' by default

----
## Run Any Grails Script

`$ gradle grails-<script-name>`

----
## Configure Grails tasks

```
'grails-run-app' {
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

1. Create GrailsLaunchContext
2. Serialize to file
3. Fork GrailsLauncher.Main in new JVM

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
  + Issues with generating plugin.xml, pom.xml, and grails-package-plugin UP-TO-DATE

----
## Demo

Note:
Git grails0 -> basic config, then run gradle init
Git grails1 -> gradle grails-run-app
Git grails2 ->
  gradle grails-create-controller -PgrailsArgs='TodoController',
  show reloading (render "Hello")
  gradle assemble
