## Building Application Distributions

----
Many options

1. Zip/Jar tasks
1. War task
1. Applications plugin
1. Deploy with Gradle
1. Fat (Uber) Jars

----
Zip/Jar tasks

+ Pros
    + Compiles project classes/resources into a single file
    + Part of normal build cycle
    + Built in to Gradle
+ Cons
    + No dependency mgmt after build

Note: Works great for Java apps with no deps. Who has those?

----
War task

+ Pros
    + Bundles application code with dependencies into a single file
    + Easy to deploy to container
+ Cons
    + Only works for things that understand WAR (Tomcat)

----
Application Plugin

+ Pros
    + Creates single Zip file for application
    + Bundles all dependencies
    + Creates shell scripts for executing program
+ Cons
    + Zip is just a package for the app. Requires unzipping for deploy

----
Deploying with Gradle

+ Pros
    + Relies on Jar files and POMs
    + Benefit from Gradle's dependency resolution
    + Gradle dependency caching
    + Gradle Wrapper (or provisioning Gradle instance)
    + Light-weight artifact (just need a build.gradle file)
+ Cons
    + Complex to initially setup
    + Resolution time
    + ...

----
Fat (Uber) Jars

+ Pros
    + Single jar file with all dependencies
    + Launch as "java -jar file.jar"
    + Runs anywhere a JRE is available (of the right version)
+ Cons
    + Longer build time
    + Bigger artifacts (= longer downloads)
    + Still need to manager JAVA_OPTS externally

----
## Ways to FatJar

----
Like Node integration, lots of ways

+ [Gradle FatJar Plugin](https://github.com/musketyr/gradle-fatjar-plugin)
+ [Gradle OneJar Plugin](https://github.com/rholder/gradle-one-jar)
+ [Gradle Shadow Plugin](https://github.com/johnrengelman/shadow)
+ Use Gradle's zipTree and Copy

Note: Ratpack uses zipTree & Copy

----
## Using zipTree & Copy

+ I/O expensive.
+ Has to write out all the jars to disk before creating a new jar
+ But, has all the goodies that Gradle provides for copying (filtering, mapping, etc.)

```
task fatJat(type: Jar) {
  from sourceSets.main.output
  from {
    mainSourceSet.runtimeClasspath.collect {
      it (it.name.endsWith('.zip') || it.name.endsWith('.jar')) {
        project.zipTree(it)
      } else {
        project.files(it)
      }
    }
  }
}
```

Note: project.zipTree expands the archive to a temporary directory on disk

----
## Gradle Shadow Plugin

----
+ Based on Maven Shade
+ Uber-jarring
+ Resource transformation
+ File filtering
+ Class relocating
+ Based on Gradle's Jar task (and all it's inherent abilities)
+ Uses JarInputStream & JarOutputStream to write file (fast!)

Note: Publishing, task inputs/outputs, task dependency / no unzip/zip IO overhead

----
Quick Start

```large-code
buildscript {
  respositories { jcenter() }
  dependencies {
    classpath 'com.github.jengelman.gradle.plugins:shadow:1.0.2'
  }
}

apply plugin: 'com.github.johnrengelman.shadow'

jar {
  manifest {
    attributes 'Main-Class': 'myapp.Main'
  }
}

shadowJar {
  mergeServiceFiles()
}
```

```
$ gradle shadowJar
```


Note:
mergeServiceFiles adds the ServiceFileTransformer
Shade's Transformer interface is *mostly* supported

----
Plugin Defaults

+ Includes all dependencies in 'runtime'
+ Excludes any 'META-INF/INDEX.LIST', 'META-INF/\*.DSA', and 'META-INF/\*.RSA' files
+ Uses same 'MANIFEST.MF' as 'jar' task
+ Classifier is 'all'
+ Creates 'shadow' configuration & component and assigns output as an artifact

----
## Speed Comparison

[Example Ratpack Gradle App](https://github.com/ratpack/example-ratpack-gradle-java-app)

Total files in resulting Jar: ~4074

| Plugin | Time |
|--------|--------|
| zipTree (RatPack plugin) | 1167 ms |
| oneJar | 452 ms |
| fatJar | 2325 ms |
| shadow | 62.25 ms |

----

![Performance Chart](slides/chart.png)

Note:
Single run with each tool. Timing from first to last action of the uberjar task
does not include times for dependent tasks

----

Integration with Application plugin

```
apply plugin: 'application'
apply plugin: 'com.github.johnrengelman.shadow'

mainClassName = 'myapp.Main'
```

```
$ gradle installShadow
$ gradle runShadow
$ gradle distShadowZip
$ gradle distShadowTar
```

----
## Demo

Note:
git app0 -> Create api project with TO, wire to Grails
git app1 -> Create service project, wire API (use Strings in @Produces, groovy/reflection, cp load)
git app2 -> Change grails service to retrieves todo's from DW and pass to angular
