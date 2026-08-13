# java-filmorate
Template repository for Filmorate project.
<br>
Добавлена  ER-диаграмма. <br>
USERS - таблица пользователей. <br>
USER_FRIENDS - таблица друзей.<br>
Добавлен синтетический ключ.
FILMS - таблицы фильмов. <br>
FILM_LIKES - таблица лайков к фильмам. <br>
Добавлен синтетический ключ. <br>
GENRE - таблица жанров.<br>
GENRE_REF - таблица для определение связи многие-ко-многим. <br>
Добавлен синтетический ключ. <br>
MPA - таблица  рейтингов Ассоциации кинокомпаний (Motion Picture Association, сокращённо МРА). <br>
DIRECTORS - таблица режисеров. <br>
FILM_DIRECTORS -  таблица для определение связи многие-ко-многим. <br>
Добавлен синтетический ключ. <br>
REVIEWS - таблица отзывов. <br>
REVIEW_LIKES - таблица лайков и дизлайков в отзывам. <br>
Добавлен синтетический ключ. <br>
EVENTS - таблица событий. <br>
<img src="/src/main/resources/ER.png" alt="MDN">

примеры запросов SELECT: <br>
```sql
-- пользователь по id
select 
  name,
  login,
  email,
  birthday
from users
where user_id = ?;

-- фильм по id
select 
  f.name,
  f.description,
  f.release_date,
  f.duration,
  mpa.name mpa_name
from films f
inner join mpa on mpa.mpa_id = f.mpa_id
where f.film_id = ?;

-- список всех рейтингов
select 
  mpa.name
from mpa;

-- список всех жанров
select 
  g.genre
from genre g;

-- список всех фильмов с жанром Боевик
select
  f.*
from films f
inner join genre_ref r on r.film_id = f.film_id
inner join genre g on g.genre_id = r.genre_id
where g.genre = 'Боевик';

-- все пары друзей
select
  u.name,
  u2.name friend_name
from users u
inner join user_friends f on f.user_id = u.user_id
inner join users u2 on u2.user_id = f.friend_id and confirm = TRUE;

-- общеие друзья двух пользователей
select 
  u.*
from users u
where exists (select 1
              from user_friends f
              where f.user_id = :id1
                and f.friend_id = u.user_id
                and f.confirm = TRUE)
  and exists (select 1
              from user_friends f
              where f.user_id = :id2
                and f.friend_id = u.user_id
                and f.confirm = TRUE);
```
примеры запросов DELETE: <br>
```sql
-- удаление пользователя
delete from users u
where u.user_id = ?;

-- удаление фильма 
delete from films f
where f.film_id = ?


-- удаление лайка
delete from film_likes l 
where l.film_id = ?
```
примеры запросов UPDATE:
```sql
-- обновление логина пользователя
update users u
set u.login = 'kosticin'
where u.user_id = ?;

-- обновление описание пользователя
update films f 
set f.description = 'новое описание'
where f.film_id = ?;
```
примеры запросов INSERT:
```sql
--добавление нового пользователя (user_id генерируется автоматически)
insert into users (
  email
, login
, name
, birthday
) values (
  'roman.kosticin@mail.ru'
, 'kosticin'
, 'Костицын'
, to_date('06.06.1984','dd.mm.yyyy')
);
-- добавление нового жанра
insert into genre (genre)
values ('Боевик');

-- добавление нового фильма
insert into films(
  name       
, description
, release_date
, duration
, mpa_id
)
values (
  'Брат 2'
, 'Данила едет в США'
, to_date('06.06.2016','dd.mm.yyyy')
, 120
, 1
);

-- добавление заявки в друзья
insert into user_friends(
  user_id
, friend_id
, confirm    
) values (
  1
, 2
, FALSE);

```