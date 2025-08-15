package ru.stroy1click.userservice;

import org.springframework.boot.SpringApplication;

public class TestStroy1ClickUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(Stroy1ClickUserServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
