# Building Java Script Projects

----
## Lots of Tools Available

+ Grunt
+ Gulp
+ Bower
+ Tomorrow's next cool thing

----
## Integrating with Bower & Grunt

----
### Step 1 - Get Node

```
buildscript {
  repositories { jcenter() }
  dependencies { classpath 'com.moowork.gradle:gradle-node-plugin:0.5' }
}

apply plugin: 'node'
```

----
### Step 2 - Configure Node

```
allprojects {
  plugins.withType(NodePlugin) {
    node {
      version = '0.10.26'
      download = true
      workDir = rootProject.file("${rootProject.buildDir}/nodejs")
    }
  }
}

project.afterEvaluate {
  nodeSetup {
    inputs.property 'version', node.version
    outputs variant.nodeDir
    onlyIf { !(it.variant.nodeDir).exists() }
  }
}
```

Note:
Do this in RootProject!
All projects use 1 installation of Node (if you want)
v0.5 has issues with multi-projects installing in same directory by default

----
### Step 3 - Configure Project Node tasks

```
task npmClean(type: Delete) {
  delete 'node_modules'
}

project.afterEvaluate {
  //use the 1 Node install from root
  nodeSetup.enabled = false
  npmInstall.dependsOn rootProject.tasks.nodeSetup
}
```
Note: Do this in project that uses Node

----
### Step 4 - Get Grunt

```
buildscript {
  repositories { jcenter() }
  dependencies { classpath 'com.moowork.gradle:gradle-grunt-plugin:0.5' }
}

apply plugin: 'grunt'


//Make sure NPM is installed before running Grunt
project.afterEvaluate {
    project.tasks.withType(GruntTask) { task ->
        task.dependsOn npmInstall
    }
}
```

----
### Step 5 - Configure Grunt Tasks

```
task compileJs(type: GruntTask) {
  args = ['compile']
}

assemble.dependsOn compileJs
```

----
### Step 6 - Get Bower

```
class BowerTask extends NodeTask {
  private String bowerScriptPath

  BowerTask() {
    group = 'Bower'
  }

  String getBowerScript() {
    bowerScriptPath ?: 'node_modules/bower/bin/bower'
  }

  @Override
  void exec() {
    def localBower = project.file(bowerScript)
    if (!localBower.file) {
      throw new GradleException('Bower not installed')
    }
    setScript(localBower)
    super.exec()
  }
}

project.afterEvaluate {
  project.tasks.withType(BowerTask) { task ->
    task.dependsOn npmInstall
  }
}
```

----
### Step 7 - Configure Bower Tasks

```
task bowerInstall(type: BowerTask) {
  args = ['install']
  inputs.file 'bower.json'
  inputs.file '.bowerrc'
}

assemble.dependsOn bowerInstall
```

Note: Should also configure bower output directory

----
### Step 8 - Task Ordering

Example: Grails WAR contains Grunt/Bower output

```
'grails-war'.mustRunAfter compileJs, bowerInstall
```

Note: Do not forget!
