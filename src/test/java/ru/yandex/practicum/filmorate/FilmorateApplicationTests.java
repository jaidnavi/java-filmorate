package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmorateApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void postUser_whenCorrectData_addUser() {
        // Создаем валидного пользователя через Builder
        User user = User.builder()
                .email("test@yandex.ru")
                .login("valid_login")
                .name("kosticin")
                .birthday(LocalDate.of(1984, 6, 6))
                .build();

        ResponseEntity<User> postResponse = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(200, postResponse.getStatusCode().value());

        User createdUser = postResponse.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals("valid_login", createdUser.getLogin());

    }

}
