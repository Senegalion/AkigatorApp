CREATE TABLE users
(
    user_id     SERIAL       NOT NULL,
    name        VARCHAR(64)  NOT NULL,
    surname     VARCHAR(64)  NOT NULL,
    username    VARCHAR(64)  NOT NULL,
    email       VARCHAR(64)  NOT NULL,
    password    VARCHAR(255) NOT NULL,
    reset_token VARCHAR(255),
    UNIQUE (username, email),
    PRIMARY KEY (user_id)
);

CREATE TABLE categories
(
    category_id SERIAL      NOT NULL,
    name        VARCHAR(64) NOT NULL,
    description VARCHAR(64) NOT NULL,
    UNIQUE (name),
    PRIMARY KEY (category_id)
);

CREATE TABLE questions
(
    question_id SERIAL NOT NULL,
    content     TEXT   NOT NULL,
    category_id INT    NOT NULL,
    PRIMARY KEY (question_id),
    CONSTRAINT fk_question_category
        FOREIGN KEY (category_id)
            REFERENCES categories (category_id)
);

CREATE TABLE entities
(
    entity_id   SERIAL      NOT NULL,
    name        VARCHAR(64) NOT NULL,
    category_id INT         NOT NULL,
    UNIQUE (name),
    PRIMARY KEY (entity_id),
    CONSTRAINT fk_entity_category
        FOREIGN KEY (category_id)
            REFERENCES categories (category_id)
);

CREATE TABLE game_sessions
(
    game_session_id SERIAL      NOT NULL,
    user_id         INT         NOT NULL,
    category_id     INT         NOT NULL,
    start_time      TIMESTAMP   NOT NULL,
    end_time        TIMESTAMP   NOT NULL,
    completed       BOOLEAN     NOT NULL DEFAULT FALSE,
    result          VARCHAR(32) NOT NULL,
    PRIMARY KEY (game_session_id),
    CONSTRAINT fk_game_session_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id),
    CONSTRAINT fk_game_session_category
        FOREIGN KEY (category_id)
            REFERENCES categories (category_id)
);

CREATE TABLE answers
(
    answer_id   SERIAL      NOT NULL,
    question_id INT         NOT NULL,
    entity_id   INT         NOT NULL,
    response    VARCHAR(32) NOT NULL,
    PRIMARY KEY (answer_id),
    CONSTRAINT fk_answer_question
        FOREIGN KEY (question_id)
            REFERENCES questions (question_id),
    CONSTRAINT fk_answer_entity
        FOREIGN KEY (entity_id)
            REFERENCES entities (entity_id)
);

CREATE TABLE game_questions
(
    game_question_id SERIAL      NOT NULL,
    game_session_id  INT         NOT NULL,
    question_id      INT         NOT NULL,
    user_response    VARCHAR(32) NOT NULL,
    PRIMARY KEY (game_question_id),
    CONSTRAINT fk_game_questions_game_session
        FOREIGN KEY (game_session_id)
            REFERENCES game_sessions (game_session_id),
    CONSTRAINT fk_game_questions_question
        FOREIGN KEY (question_id)
            REFERENCES questions (question_id)
);

CREATE TABLE roles
(
    role_id SERIAL              NOT NULL,
    name    VARCHAR(255) UNIQUE NOT NULL CHECK (name IN ('ROLE_USER', 'ROLE_ADMIN')),
    PRIMARY KEY (role_id)
);

INSERT INTO roles (name)
SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT * FROM roles WHERE roles.name='ROLE_ADMIN');
INSERT INTO roles (name)
SELECT 'ROLE_USER' WHERE NOT EXISTS (SELECT * FROM roles WHERE roles.name='ROLE_USER');
