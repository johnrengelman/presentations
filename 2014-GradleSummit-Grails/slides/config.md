## Advanced Options/Configurations

----
## Configuring JVM options

```
run {
  jvmOptions {
    minHeapSize = '512m'
    jvmArgs '-XX:MaxPermSize=512m'
  }
}
```

----
## Handling Project versions (2.0.x)

```
task setVersion(type: GrailsTask) {
  inputs.property('version', version)
  outputs.file("application.properties")
  command 'set-version'
  args version
}

war.dependsOn setVersion
```

Note:
for plugins convert project.name to camel case and outputs in ProjectNameGrailsPlugin.groovy
dependsOn for package-plugin in plugin projects

----
## Handling Project version (2.1.0)

```
//Nothing to do
```
