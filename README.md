# Пояснение схемы базы данных

[Ссылка на исходный материал](https://dbdiagram.io/d/644260896b31947051f90e2b)

![db_map](src/main/resources/db_map.png)

Эта база данных содержит информацию о фильмах, жанрах, рейтингах MPAA, пользователях, дружбе
между ними и лайках, которые они ставят фильмам.

Схема состоит из семи таблиц: `films`, `ratings_list`, `genre_list`, `genres`, `users`,
`friends` и `likes`.

Таблица `films` содержит информацию о фильмах, включая
их `film_id`, `name`, `description`, `releaseDate`, `duration` и рейтинг `rating_id`.

Таблица `ratings_list` содержит список возможных рейтингов с их `rating_id` и `rating_title`.

Таблица `genre_list` содержит список жанров с их `genre_id` и `genre_title`.

Таблица `genres` является связующей таблицей, которая соединяет фильмы с их жанрами. Она
содержит две колонки: `film_id` и `genre_id`.

Таблица `users` содержит информацию о пользователях сервиса, включая
их `user_id`, `email`, `login`, `name`, `birthday` и статус.

Таблица `friends` представляет дружбу между пользователями. Она содержит две колонки: `user1_id`
и `user2_id`.

Таблица `likes` представляет лайки фильмов пользователями. Она содержит две колонки:
`film_id` и  `user_id`.

## Примеры запросов

Добавить новый фильм
```sql
INSERT INTO films (film_id, name, description, releaseDate, duration, rating_id)
VALUES (1, 'The Shawshank Redemption', 'Two imprisoned men bond over a number of years...',
        '1994-09-22', 142, 1);
```
Добавить новый рейтинг
```sql
INSERT INTO ratings_list (rating_title)
VALUES ('R');
```
Добавить новый жанр
```sql
INSERT INTO genre_list (genre_title)
VALUES ('Drama');
```
Связать фильм с жанром
```sql
INSERT INTO genres (film_id, genre_id)
VALUES (1, 1);
```
Добавить нового пользователя
```sql
INSERT INTO users (user_id, email, login, name, birthday)
VALUES (1, 'andy@example.com', 'andy', 'Andy Dufresne', '1959-06-06');
```
Добавить запрос на дружбу от одного пользователя к другому
```sql
INSERT INTO friends (user1_id, user2_id)
VALUES (1, 2);
```
Поставить лайк фильму
```sql
INSERT INTO likes (user_id, film_id)
VALUES (1, 1);
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
FROM genre_list g
JOIN genres fg ON g.genre_id = fg.genre_id
WHERE fg.film_id = 1;
```
