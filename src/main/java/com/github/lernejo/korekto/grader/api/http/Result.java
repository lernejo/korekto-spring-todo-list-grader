package com.github.lernejo.korekto.grader.api.http;

public sealed interface Result<VALUE, ERR> {
    static <VALUE, ERR> Result<VALUE, ERR> ok(VALUE value) {
        return new Ok<>(value);
    }

    static <VALUE, ERR> Result<VALUE, ERR> err(ERR err) {
        return new Err<>(err);
    }

    boolean isErr();

    VALUE ok();

    ERR err();

    record Ok<VALUE, ERR>(VALUE ok) implements Result<VALUE, ERR> {
        @Override
        public boolean isErr() {
            return false;
        }

        @Override
        public ERR err() {
            throw new IllegalStateException("No err value on Ok");
        }
    }

    record Err<VALUE, ERR>(ERR err) implements Result<VALUE, ERR> {
        @Override
        public boolean isErr() {
            return true;
        }

        @Override
        public VALUE ok() {
            throw new IllegalStateException("No value on Err");
        }
    }
}
