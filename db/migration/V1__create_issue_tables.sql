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

CREATE TABLE IF NOT EXISTS comments (
    id UUID NOT NULL DEFAULT random_uuid (),
    content VARCHAR(255) NOT NULL,
    user_id UUID NOT NULL,
    issue_id UUID NOT NULL,
    creation VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (issue_id) REFERENCES issues(id)
      ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS  users (
    id UUID NOT NULL DEFAULT random_uuid (),
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE(username)
);