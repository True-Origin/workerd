package one.trueorigin.workerd.mysql;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class MysqlDataTest {

    @JsonSerialize
    private String test;

    public MysqlDataTest() {
    }

    public MysqlDataTest(String test) {
        this.test = test;
    }

    public String getTest() {
        return test;
    }
}
