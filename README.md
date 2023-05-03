# Пояснение схемы базы данных

[Ссылка на исходный материал](https://dbdiagram.io/d/644260896b31947051f90e2b)

![db_map](src/main/resources/db_map.png)

Эта база данных содержит информацию о фильмах, жанрах, рейтингах MPA, пользователях, дружбе
между ними и лайках, которые они ставят фильмам.

Схема состоит из семи таблиц: `film`, `rating`, `genre`, `film_genre`, `users`, `friendship`
и `film_like`.

Таблица `film` содержит информацию о фильмах, включая
их `film_id`, `name`, `description`, `release_date`, `duration` и рейтинг `rating_id`.

Таблица `rating` содержит список возможных рейтингов с их `rating_id` и названием.

Таблица `genre` содержит список жанров с их `genre_id` и названием.

Таблица `film_genre` является связующей таблицей, которая соединяет фильмы с их жанрами. Она
содержит две колонки: `film_id` и `genre_id`.

Таблица `users` содержит информацию о пользователях сервиса, включая
их `user_id`, `email`, `login`, `name` и дату рождения.

Таблица `friendship` представляет дружбу между пользователями. Она содержит две колонки: `user_id` и
friend_id.

Таблица `film_like` представляет лайки фильмов пользователями. Она содержит две колонки:  `film_id`
и  `user_id`.

## Примеры запросов

Добавить новый фильм:

```sql
INSERT INTO film (film_id, name, description, release_date, duration, rating_id)
VALUES (1, 'The Shawshank Redemption',
        'Два заключенных завязывают дружбу на протяжении многих лет...',
        '1994-09-22', 142, 1);
```

Добавить новый рейтинг:

```sql
INSERT INTO rating (name)
VALUES ('R');
```

Добавить новый жанр:

```sql
INSERT INTO genre (name)
VALUES ('Драма');
```

Связать фильм с жанром:

```sql
INSERT INTO film_genre (film_id, genre_id)
VALUES (1, 1);
```

Добавить нового пользователя:

```sql
INSERT INTO users (user_id, email, login, name, birthday)
VALUES (1, 'andy@example.com', 'andy', 'Энди Дюфрейн', '1959-06-06');
```

Добавить запрос на дружбу от одного пользователя к другому:

```sql
INSERT INTO friendship (user_id, friend_id)
VALUES (1, 2);
```

Поставить лайк фильму:

```sql
INSERT INTO film_like (user_id, film_id)
VALUES (1, 1);
```

Найти все фильмы с лайками от пользователя:

```sql
SELECT f.*
FROM film f
         JOIN film_like l ON f.film_id = l.film_id
WHERE l.user_id = 1;
```

Найти все жанры связанные с фильмом:

```sql
SELECT g.*
FROM genre g
         JOIN film_genre fg ON g.genre_id = fg.genre_id
WHERE fg.film_id = 1;
```
