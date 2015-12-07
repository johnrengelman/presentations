package com.github.johnrengelman.jdk8.functional;

public class Bar {

    public Bar() {}
    public Bar(String name) {
        this.name = name;
    }
    public Bar(Foo foo) {
        this.name = foo.name;
    }
    public Bar(Baz baz) {
        this.name = baz.getName();
    }

    public String name;
}
