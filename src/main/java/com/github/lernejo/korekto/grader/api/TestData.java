package com.github.lernejo.korekto.grader.api;

import com.github.lernejo.korekto.grader.api.http.scenario.ToFormatInput;
import com.github.lernejo.korekto.toolkit.misc.RandomSupplier;

import java.util.Map;

public class TestData {

    public final UserDef user1;
    public final UserDef user2;
    public final UserDef user3;
    public final UserDef user4;

    public final TodoList todoList1;
    public final TodoList todoList2;
    public final TodoList todoList3;

    public TestData(RandomSupplier randomSource) {
        user1 = UserDef.build("user1", randomSource);
        user2 = UserDef.build("user2", randomSource);
        user3 = UserDef.build("user3", randomSource);
        user4 = UserDef.build("user4", randomSource);

        todoList1 = TodoList.build("todolist1", randomSource);
        todoList2 = TodoList.build("todolist2", randomSource);
        todoList3 = TodoList.build("todolist3", randomSource);
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

    public record TodoList(String name, String title, String description) implements ToFormatInput {

        public static TodoList build(String name, RandomSupplier randomSource) {
            return new TodoList(name, randomSource.nextUuid().toString(), randomSource.nextUuid().toString());
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Map<String, String> toFormatInput() {
            return Map.of("title", title, "description", description);
        }
    }
}
