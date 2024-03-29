CREATE TABLE if not exists items
(
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name    VARCHAR(255)                            NOT NULL,
    rarity  VARCHAR(255)                            NOT NULL,
    user_id BIGINT                                  NOT NULL,
    CONSTRAINT pk_items PRIMARY KEY (id)
);

ALTER TABLE items
    ADD CONSTRAINT FK_ITEMS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);