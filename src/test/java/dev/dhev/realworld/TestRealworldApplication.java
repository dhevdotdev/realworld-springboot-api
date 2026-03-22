package dev.dhev.realworld;

import org.springframework.boot.SpringApplication;

public class TestRealworldApplication {

    public static void main(String[] args) {
        SpringApplication.from(RealworldApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
