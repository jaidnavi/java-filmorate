package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void addLike_whenCorrectData_addLike() {
        // добавляем пользователя
        User user = User.builder().email("test@yandex.ru").login("valid_login").name("kosticin").birthday(LocalDate.of(1984, 6, 6)).build();

        ResponseEntity<User> postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponseUser.getStatusCode().value());
        User createdUser = postResponseUser.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals("valid_login", createdUser.getLogin());

        // добавляем фильм

        Film film = Film.builder().name("Inception").description("A thief who steals corporate secrets...").releaseDate(LocalDate.of(2010, 7, 16)).duration(148).build();
        ResponseEntity<Film> postResponseFilm = restTemplate.postForEntity("/films", film, Film.class);
        assertEquals(200, postResponseFilm.getStatusCode().value());
        Film createdFilm = postResponseFilm.getBody();
        assertNotNull(createdFilm);
        assertNotNull(createdFilm.getId());
        assertEquals("Inception", createdFilm.getName());

        Long filmId = createdFilm.getId();
        Long userId = createdUser.getId();

        // добавляем лайк
        ResponseEntity<Void> putLikeResponse = restTemplate.exchange("/films/" + filmId + "/like/" + userId, HttpMethod.PUT, null, Void.class);

        assertEquals(200, putLikeResponse.getStatusCode().value());
        ResponseEntity<Film> getFilmResponse = restTemplate.getForEntity("/films/" + filmId, Film.class);
        assertEquals(200, getFilmResponse.getStatusCode().value());
        Film updatedFilm = getFilmResponse.getBody();
        assertNotNull(updatedFilm);
        assertNotNull(updatedFilm.getLikeUsers());
        assertTrue(updatedFilm.getLikeUsers().contains(userId));
    }

    @Test
    void addLike_whenUnknownFilm_getError() {
        // добавляем пользователя
        User user = User.builder().email("test@yandex.ru").login("valid_login").name("kosticin").birthday(LocalDate.of(1984, 6, 6)).build();

        ResponseEntity<User> postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponseUser.getStatusCode().value());
        User createdUser = postResponseUser.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals("valid_login", createdUser.getLogin());

        // добавляем фильм

        Film film = Film.builder().name("Inception").description("A thief who steals corporate secrets...").releaseDate(LocalDate.of(2010, 7, 16)).duration(148).build();
        ResponseEntity<Film> postResponseFilm = restTemplate.postForEntity("/films", film, Film.class);
        assertEquals(200, postResponseFilm.getStatusCode().value());
        Film createdFilm = postResponseFilm.getBody();
        assertNotNull(createdFilm);
        assertNotNull(createdFilm.getId());
        assertEquals("Inception", createdFilm.getName());

        Long filmId = createdFilm.getId();
        Long userId = createdUser.getId();

        // добавляем лайк
        ResponseEntity<Void> putLikeResponse = restTemplate.exchange("/films/" + (filmId + 999) + "/like/" + userId, HttpMethod.PUT, null, Void.class);

        assertEquals(404, putLikeResponse.getStatusCode().value());

    }

    @Test
    void addLike_whenUnknownUser_getError() {
        // добавляем пользователя
        User user = User.builder().email("test@yandex.ru").login("valid_login").name("kosticin").birthday(LocalDate.of(1984, 6, 6)).build();

        ResponseEntity<User> postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponseUser.getStatusCode().value());
        User createdUser = postResponseUser.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals("valid_login", createdUser.getLogin());

        // добавляем фильм

        Film film = Film.builder().name("Inception").description("A thief who steals corporate secrets...").releaseDate(LocalDate.of(2010, 7, 16)).duration(148).build();
        ResponseEntity<Film> postResponseFilm = restTemplate.postForEntity("/films", film, Film.class);
        assertEquals(200, postResponseFilm.getStatusCode().value());
        Film createdFilm = postResponseFilm.getBody();
        assertNotNull(createdFilm);
        assertNotNull(createdFilm.getId());
        assertEquals("Inception", createdFilm.getName());

        Long filmId = createdFilm.getId();
        Long userId = createdUser.getId();

        // добавляем лайк
        ResponseEntity<Void> putLikeResponse = restTemplate.exchange("/films/" + filmId + "/like/" + (userId + 999), HttpMethod.PUT, null, Void.class);

        assertEquals(404, putLikeResponse.getStatusCode().value());

    }


    @Test
    void deleteLike_whenCorrectData_deleteLike() {
        // добавляем лайк
        // добавляем пользователя
        User user = User.builder().email("test@yandex.ru").login("valid_login").name("kosticin").birthday(LocalDate.of(1984, 6, 6)).build();

        ResponseEntity<User> postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponseUser.getStatusCode().value());
        User createdUser = postResponseUser.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals("valid_login", createdUser.getLogin());

        // добавляем фильм

        Film film = Film.builder().name("Inception").description("A thief who steals corporate secrets...").releaseDate(LocalDate.of(2010, 7, 16)).duration(148).build();
        ResponseEntity<Film> postResponseFilm = restTemplate.postForEntity("/films", film, Film.class);
        assertEquals(200, postResponseFilm.getStatusCode().value());
        Film createdFilm = postResponseFilm.getBody();
        assertNotNull(createdFilm);
        assertNotNull(createdFilm.getId());
        assertEquals("Inception", createdFilm.getName());

        Long filmId = createdFilm.getId();
        Long userId = createdUser.getId();

        // добавляем лайк
        ResponseEntity<Void> putLikeResponse = restTemplate.exchange("/films/" + filmId + "/like/" + userId, HttpMethod.PUT, null, Void.class);

        assertEquals(200, putLikeResponse.getStatusCode().value());
        ResponseEntity<Film> getFilmResponse = restTemplate.getForEntity("/films/" + filmId, Film.class);
        assertEquals(200, getFilmResponse.getStatusCode().value());
        Film updatedFilm = getFilmResponse.getBody();
        assertNotNull(updatedFilm);
        assertNotNull(updatedFilm.getLikeUsers());
        assertTrue(updatedFilm.getLikeUsers().contains(userId));

        // удаляем лайк
        ResponseEntity<Void> deleteLikeResponse = restTemplate.exchange("/films/" + filmId + "/like/" + userId, HttpMethod.DELETE, null, Void.class);

        assertEquals(200, deleteLikeResponse.getStatusCode().value());
        // проверяем, что лайк удален
        getFilmResponse = restTemplate.getForEntity("/films/" + filmId, Film.class);
        assertEquals(200, getFilmResponse.getStatusCode().value());
        updatedFilm = getFilmResponse.getBody();
        assertNotNull(updatedFilm);
        assertNotNull(updatedFilm.getLikeUsers());
        assertFalse(updatedFilm.getLikeUsers().contains(userId));
    }

    @Test
    void deleteLike_whenUnknownFilm_getError() {
        // добавляем лайк
        // добавляем пользователя
        User user = User.builder().email("test@yandex.ru").login("valid_login").name("kosticin").birthday(LocalDate.of(1984, 6, 6)).build();

        ResponseEntity<User> postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponseUser.getStatusCode().value());
        User createdUser = postResponseUser.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals("valid_login", createdUser.getLogin());

        // добавляем фильм

        Film film = Film.builder().name("Inception").description("A thief who steals corporate secrets...").releaseDate(LocalDate.of(2010, 7, 16)).duration(148).build();
        ResponseEntity<Film> postResponseFilm = restTemplate.postForEntity("/films", film, Film.class);
        assertEquals(200, postResponseFilm.getStatusCode().value());
        Film createdFilm = postResponseFilm.getBody();
        assertNotNull(createdFilm);
        assertNotNull(createdFilm.getId());
        assertEquals("Inception", createdFilm.getName());

        Long filmId = createdFilm.getId();
        Long userId = createdUser.getId();

        // добавляем лайк
        ResponseEntity<Void> putLikeResponse = restTemplate.exchange("/films/" + filmId + "/like/" + userId, HttpMethod.PUT, null, Void.class);

        assertEquals(200, putLikeResponse.getStatusCode().value());
        ResponseEntity<Film> getFilmResponse = restTemplate.getForEntity("/films/" + filmId, Film.class);
        assertEquals(200, getFilmResponse.getStatusCode().value());
        Film updatedFilm = getFilmResponse.getBody();
        assertNotNull(updatedFilm);
        assertNotNull(updatedFilm.getLikeUsers());
        assertTrue(updatedFilm.getLikeUsers().contains(userId));

        // удаляем лайк
        ResponseEntity<Void> deleteLikeResponse = restTemplate.exchange("/films/" + (filmId + 999) + "/like/" + userId, HttpMethod.DELETE, null, Void.class);

        assertEquals(404, deleteLikeResponse.getStatusCode().value());

    }

    @Test
    void deleteLike_whenUnknownUser_getError() {
        // добавляем лайк
        // добавляем пользователя
        User user = User.builder().email("test@yandex.ru").login("valid_login").name("kosticin").birthday(LocalDate.of(1984, 6, 6)).build();

        ResponseEntity<User> postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponseUser.getStatusCode().value());
        User createdUser = postResponseUser.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals("valid_login", createdUser.getLogin());

        // добавляем фильм

        Film film = Film.builder().name("Inception").description("A thief who steals corporate secrets...").releaseDate(LocalDate.of(2010, 7, 16)).duration(148).build();
        ResponseEntity<Film> postResponseFilm = restTemplate.postForEntity("/films", film, Film.class);
        assertEquals(200, postResponseFilm.getStatusCode().value());
        Film createdFilm = postResponseFilm.getBody();
        assertNotNull(createdFilm);
        assertNotNull(createdFilm.getId());
        assertEquals("Inception", createdFilm.getName());

        Long filmId = createdFilm.getId();
        Long userId = createdUser.getId();

        // добавляем лайк
        ResponseEntity<Void> putLikeResponse = restTemplate.exchange("/films/" + filmId + "/like/" + userId, HttpMethod.PUT, null, Void.class);

        assertEquals(200, putLikeResponse.getStatusCode().value());
        ResponseEntity<Film> getFilmResponse = restTemplate.getForEntity("/films/" + filmId, Film.class);
        assertEquals(200, getFilmResponse.getStatusCode().value());
        Film updatedFilm = getFilmResponse.getBody();
        assertNotNull(updatedFilm);
        assertNotNull(updatedFilm.getLikeUsers());
        assertTrue(updatedFilm.getLikeUsers().contains(userId));

        // удаляем лайк
        ResponseEntity<Void> deleteLikeResponse = restTemplate.exchange("/films/" + filmId + "/like/" + (userId + 999), HttpMethod.DELETE, null, Void.class);

        assertEquals(404, deleteLikeResponse.getStatusCode().value());

    }

    @Test
    void findPopular() {
        // добавляем 11 фильмов
        for (int i = 0; i < 11; i++) {
            Film film = Film.builder().name("Inception").description("A thief who steals corporate secrets...").releaseDate(LocalDate.of(2010, 7, 16)).duration(148).build();
            ResponseEntity<Film> postResponse = restTemplate.postForEntity("/films", film, Film.class);
            assertEquals(200, postResponse.getStatusCode().value());
        }
        // добавляем 10 пользователей
        for (int i = 0; i < 10; i++) {
            User user = User.builder().email("test@yandex.ru").login("valid_login").name("kosticin").birthday(LocalDate.of(1984, 6, 6)).build();

            ResponseEntity<User> postResponse = restTemplate.postForEntity("/users", user, User.class);
            assertEquals(200, postResponse.getStatusCode().value());
        }
        // добавляем лайки на 11 фильмов от 0 до 10 от разных пользователей
        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= i; j++) {
                ResponseEntity<Void> putLikeResponse = restTemplate.exchange("/films/" + i + "/like/" + j, HttpMethod.PUT, null, Void.class);
                assertEquals(200, putLikeResponse.getStatusCode().value());
            }
        }


        // получаем топ 10 самых популярных (по умолчанию)
        ResponseEntity<Film[]> getResponse = restTemplate.getForEntity("/films/popular", Film[].class);
        assertEquals(200, getResponse.getStatusCode().value());
        Film[] getFilms = getResponse.getBody();
        assertNotNull(getFilms);
        assertEquals(10, getFilms.length, "Список фильмов должен содержать 10 фильмов");


        // получаем топ 5 самых популярных (по умолчанию)
        ResponseEntity<Film[]> getResponse2 = restTemplate.getForEntity("/films/popular?count=5", Film[].class);
        assertEquals(200, getResponse2.getStatusCode().value());
        Film[] getFilms2 = getResponse2.getBody();
        assertNotNull(getFilms2);
        assertEquals(5, getFilms2.length, "Список фильмов должен содержать 5 фильмов");


        // получаем топ 1000 самых популярных (по умолчанию)
        ResponseEntity<Film[]> getResponse3 = restTemplate.getForEntity("/films/popular?count=100000", Film[].class);
        assertEquals(200, getResponse3.getStatusCode().value());
        Film[] getFilms3 = getResponse3.getBody();
        assertNotNull(getFilms3);
        assertTrue(getFilms3.length > 10, "Список фильмов должен содержать более 10 фильмов");


    }
}