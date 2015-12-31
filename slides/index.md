
# Managing Infrastructure As Code with Terraform

---

# Why Infrastructure As Code?

---

# Basic Training

---

## Resource Graphs

* Directed A-cyclic Graph
  * NO loops
  * Each node points to it's dependencies

![Node graph](resources/dag.svg)

(Source: Wikimedia Commons)

---

* Benefits
  * Build nodes in order they are needed
  * Allow downstream use of information from upstream nodes

---

## Execution Flow

* Pieces of the execution
  * Model - logical resources you've defined in Terraform
  * Current State - the state of physical resources from the last Terraform run
  * Existing State - the state of physical resources as they exist in the remote provider now

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

## The Warriors Three

![The Warriors Three](resources/warriors.jpg)

(Source: ComicVine)

---

//TODO fix this slide

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

//TODO provisioner examples
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

## Terraform files

---

All `.tf`/`.tf.json` files in working directory are loaded and __appended__

(in alphabetical order)


```
dir/
  - aws.tf
  - instance.tf
```

Note: duplicate resources will cause errors

---

## Override Files

---

Loaded __AFTER__ normal files.

Are __MERGED__

* `override.tf`
* `override.tf.json`
* `temp_override.tf`

Useful for machine or temporary overrides

Note: name must be `override` or end in `_override`

---

## Referencing Resource Attributes

* Resource attributes are referenced using a standard form:
  * `<type>.<name>.<attribute>`

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

![Heredoc, Heredoc, Heredoc](resources/alf.jpg)

---

* Create multiline strings
* Interpolate values using normal style
* Literal `$` by doubling up - `$$`

---

```
user_data = <<EOF
#cloud-config
write_files:
 - path: /opt/userify/creds.py
   content: |
     # creds.py custom integration
     api_id = "${var.userifyId}"
     api_key = "${var.userifyKey}"
 - path: /etc/default/docker
   content: |
     DOCKER_PORT="2375"
     DOCKER_OPTS="-H tcp://0.0.0.0:$${DOCKER_PORT}"
EOF
```

Note: `"` doesn't have to be escaped

---

## Repeat Resources with Count

* Creates multiple physical instances of a logical definition
* Each physical instance has a unique `count.index` value
* Can vary the definition using `count.index` as key

---

```
variable cidrs {
  default = "10.0.0.0/16,10.1.0.0/16,10.2.0.0/16"
}

variable azs {
  default = "us-east-1a,us-east-1b,us-east-1c"
}

resource "aws_subnet" "public" {
  count = 3
  cidr_block = "${element(split(",", var.cidrs), count.index)}"
  availability_zone = "${element(split(",", var.azs), count.index)}"
  map_public_ip_on_launch = true
  vpc_id = "${aws_vpc.vpc.id}"
}
```

//TODO mention the cidrsubnet functions here

---

* Use a modified form for accessing attributes of `count` resources
  * `<type>.<name>.<index>.<attribute>`
* Can "collect" a list of attributes values using `*`
  * `<type>.<name>.*.<attribute>`

---

```
resource "aws_network_acl" "open" {
  vpc_id = "${aws_vpc.vpc.id}"
  subnet_ids = [
    "${aws_subnet.public.*.id}"
  ]
  ingress {
    rule_no = "100"
    protocol = "-1"
    action = "allow"
    from_port = 0
    to_port = 0
    cidr_block = "0.0.0.0/0"
  }
}
```

---

Caveat: When using __list__/__array__ type accessors

* The _interpolation_ results in an array
* __HOWEVER__, you still __must__ declare the value as an array using `[]`

---

This:

```
subnets_ids = ["${aws_subnet.public.*.id}"]
```

---

__NOT__ this:

```
subnet_ids = "${aws_subnet.public.*.id}"
```

Note: When the HCL is parsed, this appears to be a String, not an array

---

# Collaborating with Terraform

---

3 useful ways of being a "team"

---

1. Reusable modules
1. Shared state
1. Hosted execution w/ Atlas

---

## DRY off with Modules

![Wet dogs wrapped in towels](resources/dogs.jpg)

---

What's a Module?

---

1. Child tree of resources
1. Reusable
1. Encapsulates complexity w/ defined interface
1. Shareable

---

## Module Inputs

* Module inputs are simply `variables`
* In fact, we've been using a module this entire time
  * Implicit `root` module

---

## Module Outputs

* Define outputs using `output` keyword
* Since there is always the `root` module
  * Any project can have outputs
  * Good way to provide some information to users
    * DNS/IPs
    * Resource IDs
    * Access later using `terraform output`

---

```
output "loadbalancer_dns_name" {
  value = "${aws_elb.loadbalancer.dns_name}"
}
```

---

## Where do modules come from?

---

![Stork delivering baby](http://www.clipartbest.com/cliparts/9Tp/oMG/9TpoMGpbc.png)<!-- .element: class="stork" -->

(Source: ClipArtBest)

---

* File System
* Version control
  * Git/Mercurial
  * GitHub
  * BitBucket
* HTTP(S)

---

```
module "vpc" {
  source = "../tf-modules/vpc/main"
  name = "load"
  cidr_block = "10.1.0.0/16"
  availability_zones = "us-east-1b,us-east-1c,us-east-1d,us-east-1e"
}
```

---

```
module "rancher" {
  source = "github.com/objectpartners/tf-modules//rancher/server-standalone-elb-db"
  server_ami = "ami-49cedf28"
  server_key_name = "test-default"
  database_host = "${aws_db_instance.devops_rds.address}"
  database_schema = "rancher"
  database_username = "foo"
  database_password = "${var.db_password}"
  vpc_id = "${var.vpc_id}"
  server_subnet_id = "${var.subnet_id}"
  loadbalancer_subnet_ids = "${var.subnet_id}"
}
```

---

```
source = "github.com/objectpartners/tf-modules//rancher/server-standalone-elb-db"
                      [1]                    [2]          [3]
```

* [1] - The GitHub user/repo
* [2] - delineates repo from subdirectory
* [3] - Path within repository

---

* Can lock to branch/tags/commit as well

```
source = "github.com/objectpartners/tf-modules//drone/drone-db?ref=9b2e590"
```

---

## Using modules

---

* `terraform get`
  * Resolves/retrieves modules from source
* `terraform get -u`
  * Re-resolves module sources
  * Needed if using version control
  * Not needed for file system sources

---

## Module Isolation

* Module sources are not shared
  * Each module uses an isolated path on disk
  * Different modules can use different version of same source
  * Nested modules can use different versions

---

## Module Best Practices

---

Keep It Simple...Silly

* HCL doesn't have conditional flow (`if`/`else`)
* Rely on sources to modify behavior

---

Join multiple resources' attributes into single `output`

```
output subnet_ids {
  value = "${join(",", aws_subnet.subnet.*.id)}"
}
```

---

Use prefixes on outputs

```
output sg_web {
  value = "${aws_security_group.web.id}"
}
```

---

Lock version when using 3rd party modules

* https://github.com/terraform-community-modules
* https://github.com/objectpartners/tf-modules

---

## Team Collaboration

---

The _key_ to collaborating is _knowing_ the _current_ state.

Note: Remember that Terraform is a logical abstraction of physical resources

---

Terraform can sync state to remote storage

* S3
* Consul
* Etcd
* HTTP
* Atlas

---

```bash
$ terraform \
  remote config \
  -backend=S3 \
  -backend-config="bucket=my-company-tf-states" \
  -backend-config="key=demo" \
  -backend-config="region=us-east-1"
```

---

## Flow

sync local state w/ remote ->

terraform apply ->

push updated state to remote

Note: `terraform remote config` will implicitly pull

---

```bash
$ terraform remote pull
$ terraform apply
//will implicitly perform a push
```

---

## Disclaimer

<br/>
Currently,

remote config is stored in the

__state file__


---

* Remote state cannot be defined as part of the Terraform files
  * CLI only
  * __Best Practice:__ add an `init` script to repo 
* When using remote state
  * `./.terraform/terraform.tfstate`
* When __not__ using remote state
  * `./terraform.tfstate`

---

![Remote State Sharing](resources/remote_state.jpg)

---

## Another Disclaimer

<br/>
__NO__ execution locks

---

* Multiple users can apply changes at the same time
* Resulting states will clash
  * State tracks a `serial` incrementor
  * Helps, but doesn't solve

---

## Solutions

---

##  Use CI

* Pros
  * Runs on every commit
  * Runs in hosted environment
* Cons
  * Planning/Approving
  * Error handling

---

## HashiCorp Atlas

* Pros:
  * HashiCorp backing
  * Vertical integration
    * Vagrant, Packer, Consul, Terraform
* Cons
  * Cost

---

//TODO more Atlas stuff...setup test project

---

## What about custom behavior?

---

## Terraform Plugins

---

Plugins are multi-process RPC.

Each `provider`/`provisioner` is a plugin.

Note: each plugin is a separately compiled binary

---

This includes the bundled ones!!

(`aws`, `google`, `etc`)

---

Install plugin binaries to:

`~/.terrformrc` (\*nix)

`%APPDATA%/terraform.rc` (Windows)

---

Plugins follow a naming pattern:

`terraform-<type>-<name>`

* `terraform-provider-aws`
* `terraform-provider-google`
* `terraform-provisioner-local-exec`

---

## Plugin Quick Hints

---

Start here:

https://terraform.io/docs/plugins/index.html

---

Use the provided Go library

`github.com/hashicorp/terraform/plugin`

---

## Example Stackdriver Provider

---

```
provider "stackdriver" {
  api_key = "MyStackDriverAPIKey"
}

resource "stackdriver_group" "foo" {
  name = "foo"
  //...
}
```

---

`main.go`

```go
package main

import (
	"github.com/hashicorp/terraform/plugin"
	"github.com/peoplenet/terraform-provider-stackdriver/stackdriver"
)

func main() {
	plugin.Serve(&plugin.ServeOpts{
		ProviderFunc: stackdriver.Provider,
	})
}
```

---

`stackdriver/provider.go`

```go
func Provider() terraform.ResourceProvider {
	return &schema.Provider{
		ResourcesMap: map[string]*schema.Resource{
			"stackdriver_group":          resourceGroup(),
			"stackdriver_endpoint_check": resourceEndpointCheck(),
		},
		Schema: map[string]*schema.Schema{
			"api_key": &schema.Schema{
				Type:        schema.TypeString,
				Required:    true,
				DefaultFunc: schema.EnvDefaultFunc("STACKDRIVER_API_KEY", nil),
				Description: descriptions["api_key"],
			},//....
		},
		ConfigureFunc: configureAPIConnection,
	}
}
```

---

`stackdriver/resource_group.go`

```go
func resourceGroup() *schema.Resource {
	return &schema.Resource{
		Create: resourceGroupCreate,
		Read:   resourceGroupRead,
		Update: resourceGroupUpdate,
		Delete: resourceGroupDelete,

		Schema: map[string]*schema.Schema{
			"name": &schema.Schema{
				Type:     schema.TypeString,
				Required: true,
			},//....
		},
	}
}
```

---

## Plugin Testing

---

Plugin library contains test constructs

```go

var testAccProviders map[string]terraform.ResourceProvider
var testAccProvider *schema.Provider

func init() {
	testAccProvider = Provider().(*schema.Provider)
	testAccProviders = map[string]terraform.ResourceProvider{
		"stackdriver": testAccProvider,
	}
}
```

---

Ensure your Schemas are valid!

```go
func TestProvider(t *testing.T) {
	if err := Provider().(*schema.Provider).InternalValidate(); err != nil {
		t.Fatalf("err: %s", err)
	}
}
```

---

Refer to Terraform source for more examples!

https://github.com/hashicorp/terraform/tree/master/builtin/providers

---

## What about the same provider with different configs?

(e.g. create AWS resources in multiple regions?)

---


Provider Aliases

---

```
provider "aws" {
  region = "us-east-1"
}

provider "aws" {
  alias = "west"
  region = "us-west2"
}
```

---

```
resource "aws_instance" "foo-east" {
  //provider not specified, so use default 'aws'
}

resource "aws_instance" "foo-west" {
  provider = "aws.west"
  //...
}
```

---

## How do I get started with my existing infrastructure?

---

Unfortunately, currently, there is __not__ a built-in "import" feature

---

Can still be done!!

* https://github.com/dtan4/terraforming
* Manually modified the state file
  * It's JUST JSON!
  * For __most__ cases you just need the ID
  * Then run `terraform refresh`
  * And fill in the blanks!

---

Example tfstate

```json-small
{
    "version": 1,
    "serial": 3,
    "modules": [
        {
            "path": [
                "root"
            ],
            "outputs": {},
            "resources": {
              "aws_security_group.public": {
                "type": "aws_security_group",
                "depends_on": [
                    "terraform_remote_state.network"
                ],
                "primary": {
                    "id": "sg-3e35365a",
                    "attributes": {
                    }
                }
              }
            }
        }
    ]
}
```

---

Let's zoom in!

---

```json
"aws_security_group.public": { [1]
  "type": "aws_security_group", [2]
  "depends_on": [
      "terraform_remote_state.network"
  ],
  "primary": { [3]
      "id": "sg-3e35365a", [4]
      "attributes": {
      }
  }
}
```

1. the full resource name
1. the resource type
1. current state
1. the unique identifier
