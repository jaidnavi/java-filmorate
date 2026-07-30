--очищение данных
DELETE FROM genre_ref;
DELETE FROM user_friends;
DELETE FROM film_likes;
DELETE FROM users;
DELETE FROM films;
DELETE FROM mpa;
DELETE FROM genre;
DELETE FROM reviews;
DELETE FROM review_likes;



-- сброс сиквенсов
ALTER TABLE genre_ref ALTER COLUMN genre_ref_id RESTART WITH 1;
ALTER TABLE user_friends ALTER COLUMN user_friend_id RESTART WITH 1;
ALTER TABLE film_likes ALTER COLUMN film_like_id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;
ALTER TABLE mpa ALTER COLUMN mpa_id RESTART WITH 1;
ALTER TABLE genre ALTER COLUMN genre_id RESTART WITH 1;
ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1;
ALTER TABLE review_likes ALTER COLUMN review_like_id RESTART WITH 1;

-- Заполнение или обновление рейтингов MPA

MERGE INTO mpa (mpa_id, name) KEY(mpa_id) VALUES (1, 'G');
MERGE INTO mpa (mpa_id, name) KEY(mpa_id) VALUES (2, 'PG');
MERGE INTO mpa (mpa_id, name) KEY(mpa_id) VALUES (3, 'PG-13');
MERGE INTO mpa (mpa_id, name) KEY(mpa_id) VALUES (4, 'R');
MERGE INTO mpa (mpa_id, name) KEY(mpa_id) VALUES (5, 'NC-17');

-- Заполнение или обновление жанров
MERGE INTO genre (genre_id, genre) KEY(genre_id) VALUES (1, 'Комедия');
MERGE INTO genre (genre_id, genre) KEY(genre_id) VALUES (2, 'Драма');
MERGE INTO genre (genre_id, genre) KEY(genre_id) VALUES (3, 'Мультфильм');
MERGE INTO genre (genre_id, genre) KEY(genre_id) VALUES (4, 'Триллер');
MERGE INTO genre (genre_id, genre) KEY(genre_id) VALUES (5, 'Документальный');
MERGE INTO genre (genre_id, genre) KEY(genre_id) VALUES (6, 'Боевик');