package ru.stroy1click.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class Stroy1ClickUserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
