DROP TABLE IF EXISTS issues;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS users;

DROP EXTENSION IF EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
   id UUID NOT NULL DEFAULT uuid_generate_v4(),
   username VARCHAR(255) NOT NULL,
   password VARCHAR(255) NOT NULL,
   role VARCHAR(255) NOT NULL,

   PRIMARY KEY (id),
   UNIQUE(username)
);


CREATE TABLE issues
(
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    title varchar(255) NOT NULL,
    owner_id UUID NOT NULL,
    deadline varchar(255) NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (owner_id) REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE comments (
  id UUID NOT NULL DEFAULT uuid_generate_v4(),
  content VARCHAR(255) NOT NULL,
  user_id UUID NOT NULL,
  issue_id UUID NOT NULL,
  creation VARCHAR(255) NOT NULL,

  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (issue_id) REFERENCES issues(id)
      ON DELETE CASCADE
);

