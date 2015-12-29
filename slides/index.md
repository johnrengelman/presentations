
# Managing Infrastruce As Code with Terraform

---

# Why Infrastructure As Code?

---

# Basic Training

---

## Resource Graphs

* Directed A-cyclic Graph
  * NO loops
  * Each node points to it's dependencies

  //TODO Picture of a DAG

* Benefits
  * Build nodes in order they are needed
  * Allow downstream use of information from upstream nodes

---

## Execution Flow

* Pieces of the execution
  * Model - logical resources you've defined in Terraform
  * Current State - the state of physical resources from the last Terraform run
  * Existing State - the state of physical resources as the exist in the remote provider now

---

1. Generate model from logical definition (desired state)
1. Load current state model (preliminary source state)
1. Refresh current state model by querying remote provider (final source state)
1. Calculate different from source state to target state (plan)
1. Apply plan

//TODO Why state on disk if just refreshing on run?

---

## `terraform plan`

* What is planning?
  * Generates a set of actions to modify state (current to desired)
  * Actions are: Create, Update, Delete
* Occurs immediately before applying
  * Can use to see "what will happen"
  * Can generate a plan for application later
    * Pipeline/Promote infrastructure changes

---

# The Warriors Three

![The Warriors Three](http://static.comicvine.com/uploads/scale_super/5/58606/2158499-127314-170141-warriors-three.jpg)

---

| Provider                                |
|:----------------------------------------|
| API abstraction (AWS, GCE, Vsphere)     |
| Account details (username, access keys) |

| Resource                                      |
|:----------------------------------------------|
| Logical representation of "physical" resource |
| Defines the desired state of a resource       |

| Provisioner                                        |
|:---------------------------------------------------|
| Post-creation "initialization" of resource         |
| Has access to the current properties of a resource |

NOTE: "physical" in the sense that its a provider specific item (i.e. elastic cache cluster)

---

## HashiCorp Configuration Language (HCL)

* Structured configuration language
* Fully JSON compliant
* Human and machine friendly

---

### Why HCL?

* Less verbose than JSON
* More concise than YAML
* Restricts to subset of features for configuration
  * Doesn't allow unneeded, complex behavior
* Any tool expecting HCL, can accept JSON
* Allows comments

//TODO HCL Basic Syntax
//TODO file load order (IMPORTANT that files are **appended**)

---

## HCL in Terraform

```
provider "aws" {
  access_key = "mypersonalaccesskey"
  secret_key = "mysupersecretkey"
  region = "us-east-1"
}
```

---

## Referencing Resource Attributes

* Resource attributes are referenced in 1 of 2 forms:
  * `<resource_type>.<resource_name>.<resource_attribute>`
  * `<resource_type>.<resource_name>.<index>.<resource_attribute>`
* Can reference a "collection" for values using the form (when using `count`):
  * `<resource_type>.<resource_name>.*.<resource_attribute>`

---

## Examples

//TODO example of interpolating resource attributes

---

## All About the Vars

* Can define _static_ variables (i.e. defined once)
  * Single value variables are defined at execution time for the user (more on this in a minute)
  * Can have a __default__ value
* Reference using the form: `var.<variable_name>`

//TODO examples of using variables

---

## Defining a variable

```
variable "first_name" {
  description = "The persons's first name" //optional
  default = "Bob" //optional
}
```

---

## Providing variables at runtime

* All variables **must** be defined for Terraform at runtime
  * CLI arguments
  * Environment Variables
  * Properties file

---

### CLI Arguments

```
$ terraform apply -var '<name>=<value>'
```

---

```
resource "template_file" "hello" {
  template = "Hello, ${name}"
  vars {
    name = "${var.first_name} ${var.last_name}"
  }
}

$ terraform apply -var 'first_name=John' -var 'last_name=Engelman'
```

---

### Environment Variables

```
$ TF_VAR_<name> terraform apply
```

---

```
resource "template_file" "hello" {
  template = "Hello, ${name}"
  vars {
    name = "${var.first_name} ${var.last_name}"
  }
}

$ TF_VAR_first_name=John TF_VAR_last_name=Engelman terraform apply
```

---

## Properties files

* You can provide properties files using the `-var-file` option on the CLI
* The `terraform.tfvars` files is implicitly included

```
first_name="John"
last_name="Engelman"
```

---

## Map Variables

* You can also define a lookup table of variables.
* Reference using the form: `var.<map_name>.<key>`
* Cannot be defined at runtime

---

```
variable "amis" {
  default = {
    us-east-1 = "ami-12345"
    us-west-1 = "ami-23456"
    us-west-2 = "ami-34567"
  }
}
```

---

```
resource "template_file" "ami" {
  template = "AMI: ${ami}"
  vars {
    ami = "${var.amis.us-east-1}"
  }
}
```

---

## HCL Functions

* Basic math is supported
  * add, subtract, multiple, divide, modulo
  * Make sure to use _spaces_ with operators
* Built in functions
  * element, lookup, format, length, join, split, etc.
  * https://terraform.io/docs/configuration/interpolation.html

Note: because hyphen can be used in name

---

```
variable "boy_names" {
  default = "John,Bill,Bob"
}

variable "girl_names" {
  default = "Katie,Jenn,Sara"
}

resource "template_file" "names" {
  template = "Names:\n${names}"
  vars {
    names = "${join("\n", concat(split(",", var.girl_names), split(",", var.boy_names)))}"
  }
}
```

---

```
Names:
Katie
Jenn
Sara
John
Bill
Bob
```

---

## Variables in functions

* Previous Example
* Note that `var.girl_names` is __not__ wrapped with `${}`
  * `${}` sets off the interpolation

```
resource "template_file" "names" {
  template = "Names:\n${names}"
  vars {
    names = "${join("\n", concat(split(",", var.girl_names), split(",", var.boy_names)))}"
  }
}
```

---

## Multiple Functions

* Again back to previous example
* Note that function outputs can be used as inputs to other functions

```
resource "template_file" "names" {
  template = "Names:\n${names}"
  vars {
    names = "${join("\n", concat(split(",", var.girl_names), split(",", var.boy_names)))}"
  }
}
```

---

## HEREDOC, HEREDOC, HEREDOC

![Heredoc, Heredoc, Heredoc](http://cdn.meme.am/instances2/500x/3583470.jpg)
