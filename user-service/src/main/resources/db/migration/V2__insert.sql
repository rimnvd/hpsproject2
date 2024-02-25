INSERT INTO roles(role)
VALUES('STANDARD_USER'),
      ('ADMIN'),
      ('PREMIUM_USER'),
      ('BLOCKED_USER');

INSERT INTO users(email, password, username, description, balance)
VALUES ('nick@mail.ru', '$2a$10$qwvfpjtnY/ck6GmB5Um/oeCUAKXZRFg1wClPJ2NvIxlHnYQP.0O4C', 'nick', 'My description', 0);

INSERT INTO users_roles
VALUES (1, 1),
       (2, 1);
