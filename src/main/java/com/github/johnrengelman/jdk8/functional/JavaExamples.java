package com.github.johnrengelman.jdk8.functional;

import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

public class JavaExamples {

    Supplier<Foo> supply() {
        return (() -> {
            return new Foo();
        });
    }

    Supplier<Foo> supplySimple() {
        return (() -> new Foo());
    }

    Supplier<Foo> supplyMethod() {
        return Foo::new;
    }


    Consumer<Foo> consume() {
        return (val -> System.out.println(val));
    }

    Consumer<Foo> consumeMethod() {
        return System.out::println;
    }

    Function<Foo, Bar> convert() {
        return (foo -> {
            return new Bar(foo.name);
        });
    }

    Function<Foo, Bar> convertMethod() {
        return Bar::new;
    }

    BiConsumer<Foo, Bar> consumeTwo() {
        return ((foo, bar) ->
                System.out.println(
                "Foo is: " + foo.name + "; Bar is: " + bar.name));
    }

    BiFunction<Foo, Bar, FooBar> convertMulti() {
        return ((foo, bar) -> new FooBar(foo, bar));
    }

    BiFunction<Foo, Bar, FooBar> convertMultiMethod() {
        return FooBar::new;
    }

    List<Foo> filter(List<Foo> foos) {
        return foos.stream().filter(foo -> foo.name.equals("bar")).collect(Collectors.toList());
    }

    List<Bar> map(List<Foo> foos) {
        return foos.stream().map(Bar::new).collect(Collectors.toList());
    }

    List<Foo> distinct(List<Foo> foos) {
        return foos.stream().distinct().collect(Collectors.toList());
    }

    Bar interfaceMethod(Baz baz) {
        return baz.bar();
    }

    Bar useInterface() {
        return interfaceMethod(() -> "John");
    }

}
