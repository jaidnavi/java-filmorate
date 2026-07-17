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