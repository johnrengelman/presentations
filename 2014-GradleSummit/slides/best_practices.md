## Gradle Multi-Project Best Practices

----
## Project Layout

<br><br>
Flat vs Nested

Note: step0

----
## Flat Layout

```
myproject/
|
+-- foo-lib/
|
+-- bar-service/
|
+-- baz-service/
```

----
## Nested Layout

```
myproject/
|
+-- libs/
|   |
|   +-- foo-lib/
|
+-- services/
   |
   +-- bar-service/
   |
   +-- baz-service/
```

Note: Git: step1 -> gradle projects, no projects
using flat layout

----
## Auto-Detecting Sub-Projects

* Don't need to explicitly include each project
* Automatically find subprojects

```
//settings.gradle
def skippedDirs = ['buildSrc', 'gradle']
def path = [] as LinkedList
rootDir.traverse(
  type: FileType.FILES,
  nameFilter: ~/.*\.gradle/, //find .gradle files
  maxDepth: 3, //limit search depth
  preDir: {
      path << it.name // build up the directory structure
      if (skippedDirs.contains(it.name)) { // ignore skipped directories
          return FileVisitResult.SKIP_SUBTREE
      }
  },
  postDir: { path.removeLast() } //drop the .gradle file off the path
) {
    if (path) {
        include path.join(':') //register the project path using : notation
    }
}
```

Note: Do not include buildSrc as a sub-project. Will cause problems.
Git: step2 -> gradle projects, 3 projects

----
## Name Gradle files after Project names

* Quicker access to project's build file

```
//settings.gradle
rootProject.children.each { project ->
    setSubprojectBuildFile(project)
}

void setSubprojectBuildFile(def project) {
    String fileBaseName = project.name.replaceAll("\\p{Upper}") { "-${it.toLowerCase()}"}
    project.buildFileName = "${fileBaseName}.gradle"
    assert project.buildFile.isFile()
    project.children.each { subproject ->
        setSubprojectBuildFile(subproject)
    }
}
```

Note: Git: step3 -> gradle projects, 3 projects

----
```
myproject/
|
+-- fooLib/
|   |
|   +-- foo-lib.gradle
|
+-- barService/
|   |
|   +-- bar-service.gradle
|
+-- build.gradle
|
+-- settings.gradle
```

----
## Demo

Note:
Git bp0 -> empty project
Git bp1 -> flat layout, no projects
Git bp2 -> auto-detect projects, 3
git bp3 -> use project names for files 
