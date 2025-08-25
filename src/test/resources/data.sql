INSERT INTO users (email, login, name, birthday)
VALUES ('user1@mail.com', 'user1', 'User One', '1990-01-01'),
       ('user2@mail.com', 'user2', 'User Two', '1992-02-02');

INSERT INTO mpa_ratings (id, name) VALUES
                                       (1, 'G'),
                                       (2, 'PG'),
                                       (3, 'PG-13'),
                                       (4, 'R'),
                                       (5, 'NC-17');


INSERT INTO genres (id, name) VALUES
                                  (1, 'Комедия'),
                                  (2, 'Драма'),
                                  (3, 'Мультфильм'),
                                  (4, 'Триллер'),
                                  (5, 'Документальный'),
                                  (6, 'Боевик');

INSERT INTO directors (id, name) VALUES (1, 'Director 1');