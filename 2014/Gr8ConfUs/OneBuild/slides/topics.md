## What is a full application stack?

<br>

> For the purposes of this talk, a "full application stack" is a
> multi-project build that consists of multiple technologies,
> languages, frameworks, and/or external integrations that share
> components and participate in a single build to produce a set
> of linked output artifacts.

----
## Origin

<br>

Single Repository Multi-Project Build

~20 Developers

<br>

77 Projects (lots of small libs)

4 Grails Applications w/ AngularJS

Grunt/Gulp & Bower to build JS code

9 Dropwizard Applications

2 Gradle Plugins

<br>

Avg. Build Time: ~ 40 mins

Note:
From clean checkout, Codenarc, Findbugs, Shadow, publishing to Maven

---
## Topics
<br><br>

1. Multiproject Best Practices
1. Building Grails Applications & Plugins
1. Building Javascript projects
1. Building Application Distributions
