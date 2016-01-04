provider "aws" {
  region = "us-west-2"
}

variable "name" {
  default = {
    vpc = "demo"
  }
}

resource "aws_vpc" "demo" {
  cidr_block = "10.0.0.0/16"
  tags {
    Name = "${var.name.vpc}"
  }
}
