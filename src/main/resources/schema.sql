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
	"ownerId" UUID not null,
	deadline varchar(255) not null,

    primary key (id),
    FOREIGN Key ("ownerId") REFERENCES users(id)
);


CREATE TABLE comments (
   id UUID NOT NULL DEFAULT random_uuid (),
   content VARCHAR(255) NOT NULL,
   "userId" UUID NOT NULL,
   "issueId" UUID NOT NULL,
   creation VARCHAR(255) NOT NULL,

   PRIMARY KEY (id),
   FOREIGN KEY ("userId") REFERENCES users(id),
   FOREIGN KEY ("issueId") REFERENCES issues(id)
);

