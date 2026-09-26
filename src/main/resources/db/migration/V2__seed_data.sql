-- Sample dataset for local development and demos: a small, hand-picked set
-- of well-known movies and their top-billed cast. Titles, years and cast
-- lists are plain facts (comparable to what a catalog like IMDb publishes),
-- not the movies' creative content. This is nowhere near a real production
-- dataset - see Old/MovieConnect/setupDatabase for the original project's
-- approach to loading and trimming the full IMDB non-commercial datasets if
-- you want to grow this into something bigger.

INSERT INTO actor (id, name) VALUES
    (1, 'Keanu Reeves'),
    (2, 'Laurence Fishburne'),
    (3, 'Carrie-Anne Moss'),
    (4, 'Leonardo DiCaprio'),
    (5, 'Kate Winslet'),
    (6, 'Billy Zane'),
    (7, 'Joseph Gordon-Levitt'),
    (8, 'Tom Hardy'),
    (9, 'Christian Bale'),
    (10, 'Heath Ledger'),
    (11, 'Aaron Eckhart'),
    (12, 'Tom Hanks'),
    (13, 'Robin Wright'),
    (14, 'Gary Sinise'),
    (15, 'John Travolta'),
    (16, 'Samuel L. Jackson'),
    (17, 'Uma Thurman'),
    (18, 'Sam Neill'),
    (19, 'Laura Dern'),
    (20, 'Jeff Goldblum'),
    (21, 'Tim Robbins'),
    (22, 'Morgan Freeman'),
    (23, 'Russell Crowe'),
    (24, 'Joaquin Phoenix'),
    (25, 'Connie Nielsen'),
    (26, 'Marlon Brando'),
    (27, 'Al Pacino'),
    (28, 'James Caan'),
    (29, 'Michael J. Fox'),
    (30, 'Christopher Lloyd'),
    (31, 'Tim Allen'),
    (32, 'Robert Downey Jr.'),
    (33, 'Chris Evans'),
    (34, 'Scarlett Johansson'),
    (35, 'Brad Pitt'),
    (36, 'Edward Norton'),
    (37, 'Helena Bonham Carter');

INSERT INTO movie (id, title, release_year, runtime_minutes, average_rating, num_votes) VALUES
    (1, 'The Matrix', 1999, 136, 8.7, 2000000),
    (2, 'Titanic', 1997, 195, 7.9, 1300000),
    (3, 'Inception', 2010, 148, 8.8, 2400000),
    (4, 'The Dark Knight', 2008, 152, 9.0, 2700000),
    (5, 'Forrest Gump', 1994, 142, 8.8, 2100000),
    (6, 'Pulp Fiction', 1994, 154, 8.9, 2100000),
    (7, 'Jurassic Park', 1993, 127, 8.2, 1000000),
    (8, 'The Shawshank Redemption', 1994, 142, 9.3, 2800000),
    (9, 'Gladiator', 2000, 155, 8.5, 1500000),
    (10, 'The Godfather', 1972, 175, 9.2, 2000000),
    (11, 'Back to the Future', 1985, 116, 8.5, 1200000),
    (12, 'Toy Story', 1995, 81, 8.3, 1050000),
    (13, 'The Avengers', 2012, 143, 8.0, 1400000),
    (14, 'Fight Club', 1999, 139, 8.8, 2200000);

INSERT INTO movie_actor (movie_id, actor_id, importance, character_name) VALUES
    -- The Matrix
    (1, 1, 1, 'Neo'),
    (1, 2, 2, 'Morpheus'),
    (1, 3, 3, 'Trinity'),
    -- Titanic
    (2, 4, 1, 'Jack Dawson'),
    (2, 5, 2, 'Rose DeWitt Bukater'),
    (2, 6, 3, 'Cal Hockley'),
    -- Inception
    (3, 4, 1, 'Cobb'),
    (3, 7, 2, 'Arthur'),
    (3, 8, 3, 'Eames'),
    -- The Dark Knight
    (4, 9, 1, 'Bruce Wayne'),
    (4, 10, 2, 'The Joker'),
    (4, 11, 3, 'Harvey Dent'),
    -- Forrest Gump
    (5, 12, 1, 'Forrest Gump'),
    (5, 13, 2, 'Jenny Curran'),
    (5, 14, 3, 'Lieutenant Dan'),
    -- Pulp Fiction
    (6, 15, 1, 'Vincent Vega'),
    (6, 16, 2, 'Jules Winnfield'),
    (6, 17, 3, 'Mia Wallace'),
    -- Jurassic Park
    (7, 18, 1, 'Dr. Alan Grant'),
    (7, 19, 2, 'Dr. Ellie Sattler'),
    (7, 20, 3, 'Dr. Ian Malcolm'),
    -- The Shawshank Redemption
    (8, 21, 1, 'Andy Dufresne'),
    (8, 22, 2, 'Ellis "Red" Redding'),
    -- Gladiator
    (9, 23, 1, 'Maximus'),
    (9, 24, 2, 'Commodus'),
    (9, 25, 3, 'Lucilla'),
    -- The Godfather
    (10, 26, 1, 'Don Vito Corleone'),
    (10, 27, 2, 'Michael Corleone'),
    (10, 28, 3, 'Sonny Corleone'),
    -- Back to the Future
    (11, 29, 1, 'Marty McFly'),
    (11, 30, 2, 'Dr. Emmett Brown'),
    -- Toy Story
    (12, 12, 1, 'Woody (voice)'),
    (12, 31, 2, 'Buzz Lightyear (voice)'),
    -- The Avengers
    (13, 32, 1, 'Tony Stark'),
    (13, 33, 2, 'Steve Rogers'),
    (13, 34, 3, 'Natasha Romanoff'),
    -- Fight Club
    (14, 35, 1, 'Tyler Durden'),
    (14, 36, 2, 'The Narrator'),
    (14, 37, 3, 'Marla Singer');
