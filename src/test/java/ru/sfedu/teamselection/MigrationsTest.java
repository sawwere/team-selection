package ru.sfedu.teamselection;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MigrationsTest extends BasicTestContainerTest{
    @Test
    void migrate() {

    }
}
