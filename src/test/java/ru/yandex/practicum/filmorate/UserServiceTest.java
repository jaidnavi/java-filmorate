package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;


    private User addFriend_whenCorrectData_addFriend() {

        String uniqueLogin = "login_" + System.currentTimeMillis();
        User user = User.builder()
                .email("test@yandex.ru")
                .login(uniqueLogin)
                .name("kosticin")
                .birthday(LocalDate.of(1984, 6, 6))
                .build();

        // добавляем пользователя 1
        ResponseEntity<User> postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponseUser.getStatusCode().value());
        User createdUser = postResponseUser.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals(uniqueLogin, createdUser.getLogin());
        Long userId1 = createdUser.getId();

        // добавляем пользователя 2
        String uniqueLogin2 = "login_" + System.currentTimeMillis();
        user.setLogin(uniqueLogin2);
        postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponseUser.getStatusCode().value());
        createdUser = postResponseUser.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals(uniqueLogin2, createdUser.getLogin());
        Long userId2 = createdUser.getId();

        // добавляем друга
        ResponseEntity<Void> putFriendResponse = restTemplate.exchange(
                "/users/" + userId1 + "/friends/" + userId2,
                HttpMethod.PUT,
                null,
                Void.class
        );

        assertEquals(200, putFriendResponse.getStatusCode().value());

        // друг появился в списке друзей
        ResponseEntity<User> getFilmResponse = restTemplate.getForEntity("/users/" + userId1, User.class);
        assertEquals(200, getFilmResponse.getStatusCode().value());
        User updatedUser = getFilmResponse.getBody();
        assertNotNull(updatedUser);
        assertNotNull(updatedUser.getFriends());
        assertTrue(updatedUser.getFriends().contains(userId2));

        return updatedUser;
    }

    @Test
    void addFriend_whenCorrectData_addFriendTest() {

        String uniqueLogin = "login_" + System.currentTimeMillis();
        User user = User.builder()
                .email("test@yandex.ru")
                .login(uniqueLogin)
                .name("kosticin")
                .birthday(LocalDate.of(1984, 6, 6))
                .build();

        // добавляем пользователя 1
        ResponseEntity<User> postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponseUser.getStatusCode().value());
        User createdUser = postResponseUser.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals(uniqueLogin, createdUser.getLogin());
        Long userId1 = createdUser.getId();

        // добавляем пользователя 2
        String uniqueLogin2 = "login_" + System.currentTimeMillis();
        user.setLogin(uniqueLogin2);

        postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponseUser.getStatusCode().value());
        createdUser = postResponseUser.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals(uniqueLogin2, createdUser.getLogin());
        Long userId2 = createdUser.getId();

        // добавляем друга
        ResponseEntity<Void> putFriendResponse = restTemplate.exchange(
                "/users/" + userId1 + "/friends/" + userId2,
                HttpMethod.PUT,
                null,
                Void.class
        );

        assertEquals(200, putFriendResponse.getStatusCode().value());

        // друг появился в списке друзей
        ResponseEntity<User> getFilmResponse = restTemplate.getForEntity("/users/" + userId1, User.class);
        assertEquals(200, getFilmResponse.getStatusCode().value());
        User updatedUser = getFilmResponse.getBody();
        assertNotNull(updatedUser);
        assertNotNull(updatedUser.getFriends());
        assertTrue(updatedUser.getFriends().contains(userId2));

    }

    @Test
    void addFriend_whenUnknownFriend_getError() {

        String uniqueLogin = "login_" + System.currentTimeMillis();
        User user = User.builder()
                .email("test@yandex.ru")
                .login(uniqueLogin)
                .name("kosticin")
                .birthday(LocalDate.of(1984, 6, 6))
                .build();

        // добавляем пользователя 1
        ResponseEntity<User> postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponseUser.getStatusCode().value());
        User createdUser = postResponseUser.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals(uniqueLogin, createdUser.getLogin());
        Long userId1 = createdUser.getId();
        // добавляем несуществующего друга
        ResponseEntity<Void> putFriendResponse = restTemplate.exchange(
                "/users/" + userId1 + "/friends/" + 99999,
                HttpMethod.PUT,
                null,
                Void.class
        );

        assertEquals(404, putFriendResponse.getStatusCode().value());
    }

    @Test
    void addFriend_whenUnknownUser_getError() {

        String uniqueLogin = "login_" + System.currentTimeMillis();
        User user = User.builder()
                .email("test@yandex.ru")
                .login(uniqueLogin)
                .name("kosticin")
                .birthday(LocalDate.of(1984, 6, 6))
                .build();

        // добавляем пользователя 1
        ResponseEntity<User> postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponseUser.getStatusCode().value());
        User createdUser = postResponseUser.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals(uniqueLogin, createdUser.getLogin());
        Long userId1 = createdUser.getId();
        // добавляем несуществующего друга
        ResponseEntity<Void> putFriendResponse = restTemplate.exchange(
                "/users/" + 9999 + "/friends/" + userId1,
                HttpMethod.PUT,
                null,
                Void.class
        );

        assertEquals(404, putFriendResponse.getStatusCode().value());
    }


    @Test
    void deleteFriend_whenCorrectData_deleteFriend() {
        User user = addFriend_whenCorrectData_addFriend();
        Long userId = user.getId();
        Long friendId = user.getFriends().stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Список друзей пуст, тест не может быть продолжен"));
        // удаляем друга
        ResponseEntity<Void> deleteFriendResponse = restTemplate.exchange(
                "/users/" + userId + "/friends/" + friendId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(204, deleteFriendResponse.getStatusCode().value());

        // друг исчез из списка друзей
        ResponseEntity<User> getFilmResponse = restTemplate.getForEntity("/users/" + userId, User.class);
        assertEquals(200, getFilmResponse.getStatusCode().value());
        User updatedUser = getFilmResponse.getBody();
        assertNotNull(updatedUser);
        assertFalse(updatedUser.getFriends().contains(friendId));

        // пользователь изчез из списка друзей друга
        getFilmResponse = restTemplate.getForEntity("/users/" + friendId, User.class);
        assertEquals(200, getFilmResponse.getStatusCode().value());
        updatedUser = getFilmResponse.getBody();
        assertNotNull(updatedUser);
        assertFalse(updatedUser.getFriends().contains(userId));

    }

    @Test
    void deleteFriend_whenUnknownUsers_getError() {
        User user = addFriend_whenCorrectData_addFriend();
        Long userId = user.getId();
        Long friendId = user.getFriends().stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Список друзей пуст, тест не может быть продолжен"));
        // удаляем друга
        ResponseEntity<Void> deleteFriendResponse = restTemplate.exchange(
                "/users/" + (userId + 9999) + "/friends/" + friendId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(404, deleteFriendResponse.getStatusCode().value());
    }

    @Test
    void deleteFriend_whenUnknownFriends_getError() {
        User user = addFriend_whenCorrectData_addFriend();
        Long userId = user.getId();
        Long friendId = user.getFriends().stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Список друзей пуст, тест не может быть продолжен"));
        // удаляем друга
        ResponseEntity<Void> deleteFriendResponse = restTemplate.exchange(
                "/users/" + userId + "/friends/" + (friendId + 999),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(404, deleteFriendResponse.getStatusCode().value());
    }

    @Test
    void deleteFriend_noFriendRemove_noDeleteFriend() {
        String uniqueLogin = "login_" + System.currentTimeMillis();
        User user = User.builder()
                .email("test@yandex.ru")
                .login(uniqueLogin)
                .name("kosticin")
                .birthday(LocalDate.of(1984, 6, 6))
                .build();

        // добавляем пользователя 1
        ResponseEntity<User> postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponseUser.getStatusCode().value());
        User createdUser = postResponseUser.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals(uniqueLogin, createdUser.getLogin());
        Long userId1 = createdUser.getId();

        // добавляем пользователя 2
        String uniqueLogin2 = "login_" + System.currentTimeMillis();
        user.setLogin(uniqueLogin2);
        postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponseUser.getStatusCode().value());
        createdUser = postResponseUser.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals(uniqueLogin2, createdUser.getLogin());
        Long userId2 = createdUser.getId();

        // удаляем друга, которого не добавляли
        ResponseEntity<Void> deleteFriendResponse = restTemplate.exchange(
                "/users/" + userId1 + "/friends/" + userId2,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(204, deleteFriendResponse.getStatusCode().value());
    }

    @Test
    void findFriends() {
        String uniqueLogin = "login_" + System.currentTimeMillis();
        User user = User.builder()
                .email("test@yandex.ru")
                .login(uniqueLogin)
                .name("kosticin")
                .birthday(LocalDate.of(1984, 6, 6))
                .build();

        // добавляем пользователя 1
        ResponseEntity<User> postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponseUser.getStatusCode().value());
        User createdUser = postResponseUser.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals(uniqueLogin, createdUser.getLogin());
        Long userId1 = createdUser.getId();

        // добавляем 10 пользователей и всех добавляем в друзья пользователю 1
        for (int i = 0; i < 10; i++) {
            User.builder()
                    .email("test@yandex.ru")
                    .login("valid_login")
                    .name("kosticin")
                    .birthday(LocalDate.of(1984, 6, 6))
                    .build();

            // добавляем пользователя 2 (10 раз)
            String uniqueLogin2 = "login_" + System.currentTimeMillis();
            user.setLogin(uniqueLogin2);
            ResponseEntity<User> postResponseUser2 = restTemplate.postForEntity("/users", user, User.class);
            assertEquals(200, postResponseUser2.getStatusCode().value());
            User createdUser2 = postResponseUser2.getBody();
            assertNotNull(createdUser2);
            assertNotNull(createdUser2.getId()); // Проверяем, что ID сгенерировался
            assertEquals(uniqueLogin2, createdUser2.getLogin());
            Long userId2 = createdUser2.getId();

            // каждого добавляем к пользователю 1 в друзья
            ResponseEntity<Void> putFriendResponse = restTemplate.exchange(
                    "/users/" + userId1 + "/friends/" + userId2,
                    HttpMethod.PUT,
                    null,
                    Void.class
            );
            assertEquals(200, putFriendResponse.getStatusCode().value());
        }

        // получаем список друзей
        ResponseEntity<User[]> getResponse = restTemplate.getForEntity("/users/" + userId1 + "/friends", User[].class);
        assertEquals(200, getResponse.getStatusCode().value());
        User[] getUsers = getResponse.getBody();
        assertNotNull(getUsers);
        assertEquals(10, getUsers.length, "Список пользователей должен содержать 10 друзей");

    }

    @Test
    void findCommonFriends() {
        String uniqueLogin = "login_" + System.currentTimeMillis();
        User user = User.builder()
                .email("test@yandex.ru")
                .login(uniqueLogin)
                .name("kosticin")
                .birthday(LocalDate.of(1984, 6, 6))
                .build();

        // добавляем пользователя 1, 2, 3
        ResponseEntity<User> postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(200, postResponseUser.getStatusCode().value());
        User createdUser = postResponseUser.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId()); // Проверяем, что ID сгенерировался
        assertEquals(uniqueLogin, createdUser.getLogin());
        Long userId1 = createdUser.getId();
        assertEquals(200, postResponseUser.getStatusCode().value());

        String uniqueLogin2 = "login_" + System.currentTimeMillis();
        user.setLogin(uniqueLogin2);
        postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        User createdUser2 = postResponseUser.getBody();
        Long userId2 = Objects.requireNonNull(createdUser2).getId();

        String uniqueLogin3 = "login_" + System.currentTimeMillis();
        user.setLogin(uniqueLogin3);
        postResponseUser = restTemplate.postForEntity("/users", user, User.class);
        User createdUser3 = postResponseUser.getBody();
        Long userId3 = Objects.requireNonNull(createdUser3).getId();

        // добавляем пользователю 2 в друзья пользователя 1
        ResponseEntity<Void> putFriendResponse = restTemplate.exchange(
                "/users/" + userId1 + "/friends/" + userId2,
                HttpMethod.PUT,
                null,
                Void.class
        );
        assertEquals(200, putFriendResponse.getStatusCode().value());


        putFriendResponse = restTemplate.exchange(
                "/users/" + userId2 + "/friends/" + userId1,
                HttpMethod.PUT,
                null,
                Void.class
        );
        assertEquals(200, putFriendResponse.getStatusCode().value());

        // добавляем пользователю 2 в друзья пользователя 3
        putFriendResponse = restTemplate.exchange(
                "/users/" + userId2 + "/friends/" + userId3,
                HttpMethod.PUT,
                null,
                Void.class
        );
        assertEquals(200, putFriendResponse.getStatusCode().value());


        putFriendResponse = restTemplate.exchange(
                "/users/" + userId3 + "/friends/" + userId2,
                HttpMethod.PUT,
                null,
                Void.class
        );
        assertEquals(200, putFriendResponse.getStatusCode().value());

        // смотрим общих друзей пользователя 1 и 3
        ResponseEntity<User[]> getResponse = restTemplate.getForEntity("/users/" + userId1 + "/friends/common/" + userId3, User[].class);
        assertEquals(200, getResponse.getStatusCode().value());
        User[] commonFriends = getResponse.getBody();
        assertNotNull(commonFriends);
        assertEquals(1, commonFriends.length, "Список пользователей должен содержать 1 друга - пользователя 2");
        assertEquals(userId2, commonFriends[0].getId(), "Общим другом должен быть Пользователь 2");
    }
}