package com.github.johnrengelman.jdk8.functional;

public class Foo {

    public String name;

    Bar bar() {
        return new Bar(this);
    }

    public String toString() {
        return name;
    }
}
