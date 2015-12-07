package com.github.johnrengelman.jdk8.functional;

public interface Baz {

    String getName();

    default Bar bar() {
        return new Bar(this);
    }
}
