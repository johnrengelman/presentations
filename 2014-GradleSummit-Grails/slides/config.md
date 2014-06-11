## Configuring JVM optoins

```
'grails-run-app' {
  jvmOptions {
    minHeapSize = '512m'
    jvmArgs '-XX:MaxPermSize=512m'
  }
}
```
