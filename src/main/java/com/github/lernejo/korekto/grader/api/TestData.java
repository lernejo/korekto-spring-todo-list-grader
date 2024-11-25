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
        user1 = UserDef.build("user1", randomSource);
        user2 = UserDef.build("user2", randomSource);
        user3 = UserDef.build("user3", randomSource);
        user4 = UserDef.build("user4", randomSource);
    }

    public record UserDef(String name, String email, String password) implements ToFormatInput {
        static UserDef build(String name, RandomSupplier randomSource) {
            return new UserDef(name, randomSource.nextUuid() + "@korekto.io", randomSource.nextUuid().toString());
        }

        @Override
        public Map<String, String> toFormatInput() {
            return Map.of("email", email, "password", password);
        }
    }
}
