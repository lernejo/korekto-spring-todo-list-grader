package com.github.lernejo.korekto.grader.api;

import com.github.lernejo.korekto.grader.api.http.scenario.ToFormatInput;
import com.github.lernejo.korekto.toolkit.misc.RandomSupplier;

import java.util.Map;

public class TestData {

    public final UserDef user1;
    public final UserDef user2;
    public final UserDef user3;
    public final UserDef user4;

    public TestData(RandomSupplier randomSource) {
        user1 = UserDef.build(randomSource);
        user2 = UserDef.build(randomSource);
        user3 = UserDef.build(randomSource);
        user4 = UserDef.build(randomSource);
    }

    public record UserDef(String email, String password) implements ToFormatInput {
        static UserDef build(RandomSupplier randomSource) {
            return new UserDef(randomSource.nextUuid() + "@korekto.io", randomSource.nextUuid().toString());
        }

        @Override
        public Map<String, String> toFormatInput() {
            return Map.of("email", email, "password", password);
        }
    }
}
