CREATE TABLE users (
   id UUID NOT NULL DEFAULT random_uuid (),
   username VARCHAR(255) NOT NULL,
   password VARCHAR(255) NOT NULL,
   role VARCHAR(255) NOT NULL,

   PRIMARY KEY (id),
   UNIQUE(username)
);


create table issues
(
	id UUID not null default random_uuid(),
	title varchar(255) not null,
	owner_id UUID not null,
	deadline varchar(255) not null,

    primary key (id),
    FOREIGN Key (owner_id) REFERENCES users(id)
);


CREATE TABLE comments (
   id UUID NOT NULL DEFAULT random_uuid (),
   content VARCHAR(255) NOT NULL,
   user_id UUID NOT NULL,
   issue_id UUID NOT NULL,
   creation VARCHAR(255) NOT NULL,

   PRIMARY KEY (id),
   FOREIGN KEY (user_id) REFERENCES users(id),
   FOREIGN KEY (issue_id) REFERENCES issues(id)
);

