CREATE TABLE IF NOT EXISTS  issues
(
    id UUID NOT NULL DEFAULT random_uuid(),
    title varchar(255) NOT NULL,
    owner_id UUID NOT NULL,
    deadline varchar(255) NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (owner_id) REFERENCES users(id)
        ON DELETE CASCADE
);