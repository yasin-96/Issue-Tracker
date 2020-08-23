CREATE TABLE IF NOT EXISTS  users (
   id UUID NOT NULL DEFAULT random_uuid (),
   username VARCHAR(255) NOT NULL,
   password VARCHAR(255) NOT NULL,
   role VARCHAR(255) NOT NULL,

   PRIMARY KEY (id),
   UNIQUE(username)
);