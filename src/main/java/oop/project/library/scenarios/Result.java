package oop.project.library.scenarios;

public sealed interface Result<T> {

    record Success<T>(T value) implements Result<T> {}
    record Failure<T>(String error) implements Result<T> {}

}
