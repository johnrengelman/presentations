package com.github.johnrengelman.jdk8.functional

import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier


class GroovyExamples {

    Supplier<Foo> supply() {
        return {
            new Foo()
        }
    }

    Supplier<Foo> supplyMethod() {
        //http://mrhaki.blogspot.com/2015/03/groovy-goodness-use-constructor-as.html
        Foo.metaClass.&invokeConstructor
    }


    Consumer<Foo> consume() {
        return { val ->
            println val.name
        }
    }

    Consumer<Foo> consumeMethod() {
        this.&println
    }

    Function<Foo, Bar> convert() {
        return { foo ->
            new Bar(foo.name)
        }
    }

    Function<Foo, Bar> convertMethod() {
        Bar.metaClass.&invokeConstructor
    }

    BiConsumer<Foo, Bar> consumeTwo() {
        return { foo, bar ->
            "Foo is: ${foo.name}; Bar is: ${bar.name}"
        }
    }

    List<Foo> filter(List<Foo> foos) {
        foos.findAll { foo -> foo.name == 'bar' }
    }

    List<Bar> map(List<Foo> foos) {
        foos.collect { new Bar(foo) }
    }

    List<Foo> distinct(List<Foo> foos) {
        foos.unique()
    }
}
