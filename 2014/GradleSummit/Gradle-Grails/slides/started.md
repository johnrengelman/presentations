## Getting Started

----
## Applying the plugin

```
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath 'org.grails:grails-gradle-plugin:2.1.0'
  }
}

apply plugin: 'grails'

version = '0.0.1'

grails {
  grailsVersion = '2.4.0'
}
```

Note:
Always specify Version in a grails project.

----
## Creating a project

```
$ gradle init
```

```
$ gradle init-plugin
```

----
## Configuring dependencies

```
repositories {
  grails.central()
}

dependencies {
  bootstrap 'org.grails.plugins:tomcat:7.0.52.1'
  compile 'org.grails.plugins:asset-pipeline:1.8.7'
  runtime 'com.h2database:h2:1.4.178'
}
```

Note:
grails.central adds URL to Grails Central Maven Repo
configurations: bootstrap, provided, compile, runtime, test
MUST use fully maven coordinates for deps

----
## Built-In Tasks

<br>

| Gradle Task | Grails Task |
|-------------|-------------|
| run | run-app |
| test | test-app (all phases) |
| war | war (defaults to dev env) |
| packagePlugin | package-plugin (defaults to source) |

----
## Plugin Configurations

```
grails {
  grailsVersion = '2.4.0'
  groovyVersion = '2.3.1' //creates ResolutionStrategy for groovy-all
  springLoadedVersion = '1.1.3' //Which version to include
}
```

----
## Executing Grails Scripts

```
$ gradle grails-<script-name>
```

```
$ gradle grails-refresh-dependencies
```

Note:
To run plugin scripts, some task must execute that causes Grails to install the plugin first

----
## Passing arguments to Grails commands

```
$ gradle -PgrailsArgs="--non-interactive" grails-run-app
```

```
$ gradle -PgrailsEnv=prod grails-run-app
```

\* Has some bugs currently

Note:
only works for dynamic tasks right now, will be fixed in 2.0.2 (in some form)
