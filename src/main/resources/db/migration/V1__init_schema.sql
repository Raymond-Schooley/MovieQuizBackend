CREATE TABLE movie (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    release_year    INT          NOT NULL,
    runtime_minutes INT,
    average_rating  DECIMAL(3, 1),
    num_votes       INT          NOT NULL DEFAULT 0
) ENGINE = InnoDB;

CREATE TABLE actor (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    birth_year INT,
    death_year INT
) ENGINE = InnoDB;

CREATE TABLE movie_actor (
    movie_id       BIGINT       NOT NULL,
    actor_id       BIGINT       NOT NULL,
    importance     INT          NOT NULL,
    character_name VARCHAR(255),
    PRIMARY KEY (movie_id, actor_id),
    CONSTRAINT fk_movie_actor_movie FOREIGN KEY (movie_id) REFERENCES movie (id) ON DELETE CASCADE,
    CONSTRAINT fk_movie_actor_actor FOREIGN KEY (actor_id) REFERENCES actor (id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE INDEX idx_movie_actor_actor ON movie_actor (actor_id);
CREATE INDEX idx_movie_year_votes ON movie (release_year, num_votes);
CREATE INDEX idx_movie_title ON movie (title);
CREATE INDEX idx_actor_name ON actor (name);
