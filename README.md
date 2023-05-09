# Пояснение схемы базы данных

[Ссылка на исходный материал](https://dbdiagram.io/d/644260896b31947051f90e2b)

![db_map](src/main/resources/db_map.png)

База данных содержит информацию о фильмах, жанрах, рейтингах MPAA, пользователях, дружбе между ними
и лайках, которые они ставят фильмам.

Схема состоит из семи таблиц: `films`, `mpa`, `genre`, `film_genre`, `users`, `friendship`
и `likes`.

Таблица `films` содержит информацию о фильмах, включая
их `film_id`, `name`, `description`, `release_date`, `duration` и рейтинг `mpa_id`.

Таблица `mpa` содержит список возможных рейтингов MPA с их `mpa_id` и `name`.

Таблица `genre` содержит список жанров с их `genre_id` и `name`.

Таблица `film_genre` является связующей таблицей, которая соединяет фильмы с их жанрами. Она
содержит три колонки: `film_genre_id`, `film_id` и `genre_id`.

Таблица `users` содержит информацию о пользователях сервиса, включая
их `user_id`, `email`, `login`, `name`, `birthday`.

Таблица `friendship` представляет дружбу между пользователями. Она содержит четыре
колонки: `friendship_id`, `user_id`, `friend_id` и `status`.

Таблица `likes` представляет лайки фильмов пользователями. Она содержит три
колонки: `like_id`, `user_id` и `film_id`.

## Примеры запросов

Добавить новый фильм

```sql
INSERT INTO films (film_id, name, description, release_date, duration, mpa_id)
VALUES (1, 'The Shawshank Redemption', 'Two imprisoned men bond over a number of years...',
        '1994-09-22', 142, 1);
```

Добавить новый рейтинг MPA

```sql
INSERT INTO mpa (mpa_id, name)
VALUES (1, 'R');
```

Добавить новый жанр

```sql
INSERT INTO genre (genre_id, name)
VALUES (1, 'Drama');
```

Связать фильм с жанром

```sql
INSERT INTO film_genre (film_genre_id, film_id, genre_id)
VALUES (1, 1, 1);
```

Добавить нового пользователя

```sql
INSERT INTO users (user_id, email, login, name, birthday)
VALUES (1, 'andy@example.com', 'andy', 'Andy Dufresne', '1959-06-06');
```

Добавить запрос на дружбу от одного пользователя к другому

```sql
INSERT INTO friendship (friendship_id, user_id, friend_id)
VALUES (1, 1, 2);
```

Поставить лайк фильму

```sql
INSERT INTO likes (like_id, user_id, film_id)
VALUES (1, 1, 1);
```

Найти все фильмы с лайками от пользователя

```sql
SELECT f.*
FROM films f
         JOIN likes l ON f.film_id = l.film_id
WHERE l.user_id = 1;
```

Найти все жанры связанные с фильмом

```sql
SELECT g.*
FROM genre g
         JOIN film_genre fg ON g.genre_id = fg.genre_id
WHERE fg.film_id = 1;
```
