## What is Gradle?

----

1. Expressive, declarative, & maintainable build language
1. Dependency Resolver & Manager
1. Build Task Scheduler & Executor

----

### What Gradle is NOT!

----

It is **NOT** Groovy Ant!

(That tool exists -> [GANT](http://gant.codehaus.org/))

---

## Core Gradle Features

----

1. Build-By-Convention w/ Flexibility
1. Project & Build Groovy DSL
1. Support for Ivy & Maven Dependencies
1. Multi-Project Builds
1. Easy to add custom logic
1. 1st class integration w/ Ant builds

---

## The Quick & Dirty

----

### A Typical Maven Build

* 11.28s (`mvn package`)
* 2.061s (`rm -r target && mvn package`)

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>de.uulm.vs</groupId>
  <artifactId>netty-example</artifactId>
  <packaging>jar</packaging>
  <version>1.0-SNAPSHOT</version>
  <name>netty-example</name>
  <url>http://maven.apache.org</url>
  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>3.8.1</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.jboss.netty</groupId>
      <artifactId>netty</artifactId>
      <version>3.2.2.Final</version>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>2.0.2</version>
        <configuration>
          <source>1.6</source>
          <target>1.6</target>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

---

### The Same Build w/ Gradle

* 11.07s (`gradle build`)
* 2.161s (`rm -r build/ && gradle build`)

```
apply plugin: 'java'
apply plugin: 'maven'

group = 'de.uulm.vs'
version = '1.0-SNAPSHOT'

repositories {
  jcenter()
}

dependencies {
  compile 'org.jboss.netty:netty:3.2.2.Final'
  testCompile 'junit:junit:3.8.1'
}

targetCompatibility = '1.6'
sourceCompatibility = '1.6'
```
