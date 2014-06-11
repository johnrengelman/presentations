## Running tests

```
$ gradle test
```

----
## Configuring a test phase task rule

```
project.tasks.addRule('Pattern: test<Phase>') { String taskName ->
  if(taskName.startsWith('test') && taskName != 'test') {
    task(taskName, type: GrailsTestType) {
      String testPhase = (taskName - 'test').toLowerCase() + ':'
      phases = [testPhase]
    }
  }
}
```

```
$ gradle testUnit
```

----
## Run a specific test

```
$ gradle test -Dtest.single="FooControllerSpec"
```

Note:
test.single passes to Grails and results in all the Grails matching rules for tests

----
## Configuring multiple test tasks

```
task testUnit(type: GrailsTestTask) {
  phases = ['unit:']
  testResultsDir = new File(buildDir, 'reports/test-unit')
}

task testInt(type: GrailsTestTask) {
  phases = ['integration:']
  testResultsDir = new File(buildDir, 'reports/test-int')
}

test.dependsOn [testUnit, testInt]
```

Note:
Disable 'grails-test-app' default task
