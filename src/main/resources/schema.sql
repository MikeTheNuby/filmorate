CREATE TABLE IF NOT EXISTS films (
    film_id INTEGER PRIMARY KEY,
    name VARCHAR,
    description VARCHAR(200),
    releaseDate DATE,
    duration INTEGER,
    rating_id INTEGER
);

CREATE TABLE IF NOT EXISTS rating_list (
    rating_id INTEGER PRIMARY KEY,
    rating_title VARCHAR
);

CREATE TABLE IF NOT EXISTS genre_list (
    genre_id INTEGER PRIMARY KEY,
    genre_title VARCHAR
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id INTEGER,
    genre_id INTEGER,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS service_user (
    user_id INTEGER PRIMARY KEY,
    email VARCHAR,
    login VARCHAR,
    name VARCHAR,
    birthday DATE,
    status VARCHAR
);

CREATE TABLE IF NOT EXISTS friendship (
    user1_id INTEGER,
    user2_id INTEGER,
    PRIMARY KEY (user1_id, user2_id)
);

CREATE TABLE IF NOT EXISTS like (
    user_id INTEGER,
    film_id INTEGER,
    PRIMARY KEY (user_id, film_id)
);

ALTER TABLE film_genre ADD FOREIGN KEY (film_id) REFERENCES films(film_id);
ALTER TABLE film_genre ADD FOREIGN KEY (genre_id) REFERENCES genre_list(genre_id);
ALTER TABLE friendship ADD FOREIGN KEY (user1_id) REFERENCES service_user(user_id);
ALTER TABLE friendship ADD FOREIGN KEY (user2_id) REFERENCES service_user(user_id);
ALTER TABLE like ADD FOREIGN KEY (user_id) REFERENCES service_user(user_id);
ALTER TABLE like ADD FOREIGN KEY (film_id) REFERENCES films(film_id);
ALTER TABLE films ADD FOREIGN KEY (rating_id) REFERENCES rating_list(rating_id);
