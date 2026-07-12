package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmorateApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    void postUser_whenCorrectData_addUser() {
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
        assertNotNull(createdUser.getUserId()); // Проверяем, что ID сгенерировался
        assertEquals("valid_login", createdUser.getLogin());
    }

    @Test
    void postUser_whenInvalidData_Error() {
        User user = User.builder()
                .email("test")
                .login("valid_login")
                .name("kosticin")
                .birthday(LocalDate.of(1984, 6, 6))
                .build();
        ResponseEntity<User> postResponse = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(400, postResponse.getStatusCode().value());

    }

    @Test
    void getUsers() {
        User user = User.builder()
                .email("test@yandex.ru")
                .login("valid_login")
                .name("kosticin")
                .birthday(LocalDate.of(1984, 6, 6))
                .build();
        ResponseEntity<User> postResponse = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponse.getStatusCode().value());
        ResponseEntity<User[]> getResponse = restTemplate.getForEntity("/users", User[].class);
        assertEquals(200, getResponse.getStatusCode().value());
        User[] getUsers = getResponse.getBody();
        assertNotNull(getUsers);
        assertTrue(getUsers.length > 0, "Список не должен быть пустым");
    }

    @Test
    void putUser_whenCorrectData_updateUser() {
        User initialUser = User.builder()
                .email("old_email@yandex.ru")
                .login("old_login")
                .name("Старое Имя")
                .birthday(LocalDate.of(1990, 5, 5))
                .build();
        ResponseEntity<User> postResponse = restTemplate.postForEntity("/users", initialUser, User.class);
        assertEquals(200, postResponse.getStatusCode().value());
        User savedUser = postResponse.getBody();
        assertNotNull(savedUser);
        Long userId = savedUser.getUserId();
        User updatedData = User.builder()
                .userId(userId)
                .email("new_email@yandex.ru") // Меняем почту
                .login("new_login")           // Меняем логин
                .name("Новое Имя")            // Меняем имя
                .birthday(LocalDate.of(1995, 10, 10))
                .build();
        ResponseEntity<User> putResponse = restTemplate.exchange(
                "/users",
                HttpMethod.PUT,
                new HttpEntity<>(updatedData),
                User.class
        );
        assertEquals(200, putResponse.getStatusCode().value());
        User updatedUser = putResponse.getBody();
        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getUserId(), "ID пользователя не должен измениться");
        assertEquals("new_login", updatedUser.getLogin());
        assertEquals("new_email@yandex.ru", updatedUser.getEmail());
        assertEquals("Новое Имя", updatedUser.getName());
    }

    @Test
    void putUser_whenIncorrectID_getError() {
        User updatedData = User.builder()
                .userId(-999L)
                .email("new_email@yandex.ru") // Меняем почту
                .login("new_login")           // Меняем логин
                .name("Новое Имя")            // Меняем имя
                .birthday(LocalDate.of(1995, 10, 10))
                .build();
        ResponseEntity<User> putResponse = restTemplate.exchange(
                "/users",
                HttpMethod.PUT,
                new HttpEntity<>(updatedData),
                User.class
        );
        assertEquals(404, putResponse.getStatusCode().value());
    }

    @Test
    void postFilm_whenCorrectData_addFilm() {
        Film film = Film.builder()
                .name("Inception")
                .description("A thief who steals corporate secrets...")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .build();
        ResponseEntity<Film> postResponse = restTemplate.postForEntity("/films", film, Film.class);
        assertEquals(200, postResponse.getStatusCode().value());
        Film createdFilm = postResponse.getBody();
        assertNotNull(createdFilm);
        assertNotNull(createdFilm.getFilmId()); // Проверяем, что ID сгенерировался
        assertEquals("Inception", createdFilm.getName());

    }

    @Test
    void postFilm_whenInvalidData_Error() {
        Film film = Film.builder()
                .name("      ")
                .description("A thief who steals corporate secrets...")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .build();
        ResponseEntity<Film> postResponse = restTemplate.postForEntity("/films", film, Film.class);
        assertEquals(400, postResponse.getStatusCode().value());
    }

    @Test
    void getFilms() {
        Film film = Film.builder()
                .name("Inception")
                .description("A thief who steals corporate secrets...")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .build();
        ResponseEntity<Film> postResponse = restTemplate.postForEntity("/films", film, Film.class);
        assertEquals(200, postResponse.getStatusCode().value());
        ResponseEntity<Film[]> getResponse = restTemplate.getForEntity("/films", Film[].class);
        assertEquals(200, getResponse.getStatusCode().value());
        Film[] getFilms = getResponse.getBody();
        assertNotNull(getFilms);
        assertTrue(getFilms.length > 0, "Список фильмов не должен быть пустым");
    }

    @Test
    void putFilm_whenCorrectData_updateFilm() {
        Film initialFilm = Film.builder()
                .name("Old Film")
                .description("Old Film")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(111)
                .build();
        ResponseEntity<Film> postResponse = restTemplate.postForEntity("/films", initialFilm, Film.class);
        assertEquals(200, postResponse.getStatusCode().value());
        Film savedFilm = postResponse.getBody();
        assertNotNull(savedFilm);
        Long filmId = savedFilm.getFilmId();
        Film updatedData = Film.builder()
                .filmId(filmId)
                .name("New Film name")
                .description("New Film description")
                .releaseDate(LocalDate.of(2025, 7, 16))
                .duration(222)
                .build();
        ResponseEntity<Film> putResponse = restTemplate.exchange(
                "/films",
                HttpMethod.PUT,
                new HttpEntity<>(updatedData),
                Film.class
        );
        assertEquals(200, putResponse.getStatusCode().value());
        Film updatedFilm = putResponse.getBody();
        assertNotNull(updatedFilm);
        assertEquals(filmId, updatedFilm.getFilmId(), "ID фильма не должен измениться");
        assertEquals("New Film name", updatedFilm.getName());
        assertEquals("New Film description", updatedFilm.getDescription());
        assertEquals(222, updatedFilm.getDuration());
        assertEquals(LocalDate.of(2025, 7, 16), updatedFilm.getReleaseDate());
    }

    @Test
    void putFilm_whenIncorrectID_getError() {
        Film updatedData = Film.builder()
                .filmId(-999L)
                .name("New Film")
                .description("New Film")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .build();
        ResponseEntity<User> putResponse = restTemplate.exchange(
                "/films",
                HttpMethod.PUT,
                new HttpEntity<>(updatedData),
                User.class
        );
        assertEquals(404, putResponse.getStatusCode().value());
    }

}
