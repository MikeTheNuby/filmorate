MERGE INTO MPA
    KEY (MPA_ID)
    VALUES (1, 'G', 'The film has no age restrictions'),
    (2, 'PG', 'Children are encouraged to watch the film with their parents'),
    (3, 'PG-13', 'Children under 13 are not recommended to view'),
    (4, 'R', 'Persons under 17 years of age can watch the film only in the presence of an adult'),
    (5, 'NC-17', 'Persons under 18 years of age are not allowed to view');

MERGE INTO GENRE
    KEY (GENRE_ID)
    VALUES (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');