# Пояснение схемы базы данных

[Ссылка на исходный материал](https://dbdiagram.io/d/644260896b31947051f90e2b)



![db_map](src/main/resources/db_map.png)


Эта база данных содержит информацию о фильмах, жанрах, пользователях, дружбе и лайках. 
Схема состоит из шести таблиц: `film`, `genre_list`, `film_genre`, `service_user`, `friendship` и `like`.

Таблица `film` содержит информацию о фильмах, включая их `id`, `name`, `description`, `releaseDate`, `duration` и рейтинг `mpa`.

Таблица `genre_list` содержит список жанров с их `id` и `name`.

Таблица `film_genre` является связующей таблицей, которая соединяет фильмы с их жанрами. Она содержит две колонки: `film_id` и `genre_id`.

Таблица `service_user` содержит информацию о пользователях сервиса, включая их `id`, `email`, `login`, `name` и `birthday`.

Таблица `friendship` представляет дружбу между пользователями. Она содержит три колонки: `user1_id`, `user2_id` и статус дружбы.

Таблица `like` представляет лайки фильмов пользователями. Она содержит две колонки: `user_id` и `film_id`.

## Примеры запросов

-- Добавить новый фильм
```sql
INSERT INTO film (id, name, description, releaseDate, duration, mpa)
VALUES (1, 'The Shawshank Redemption', 'Two imprisoned men bond over a number of years...', 
        '1994-09-22', 142, 'R');
```

-- Добавить новый жанр
```sql
INSERT INTO genre_list (name)
VALUES ('Drama');
```

-- Связать фильм с жанром
```sql
INSERT INTO film_genre (film_id, genre_id)
VALUES (1, 1);
```

-- Добавить нового пользователя
```sql
INSERT INTO service_user (id, email, login, name, birthday)
VALUES (1, 'andy@example.com', 'andy', 'Andy Dufresne', '1959-06-06');
```

-- Добавить запрос на дружбу от одного пользователя к другому
```sql
INSERT INTO friendship (user1_id, user2_id, status)
VALUES (1, 2, 'unconfirmed');
```

-- Подтвердить запрос на дружбу
```sql
UPDATE friendship
SET status = 'confirmed'
WHERE user1_id = 2 AND user2_id = 1;
```

-- Поставить лайк фильму
```sql
INSERT INTO like (user_id, film_id)
VALUES (1, 1);
```

-- Найти все фильмы с лайками от пользователя
```sql
SELECT f.*
FROM film f
JOIN like l ON f.id = l.film_id
WHERE l.user_id = 1;
```

-- Найти все жанры связанные с фильмом
```sql
SELECT g.*
FROM genre_list g
JOIN film_genre fg ON g.id = fg.genre_id
WHERE fg.film_id = 1;
```
