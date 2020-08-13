create table issues
(
	id UUID not null default random_uuid(),
	title varchar(255) not null,
	owner varchar(255) not null,

    primary key (id)
);


CREATE TABLE users (
   id UUID NOT NULL DEFAULT random_uuid (),
   username VARCHAR(255) NOT NULL,
   password VARCHAR(255) NOT NULL,
   role VARCHAR(255) NOT NULL,

   PRIMARY KEY (id),
   UNIQUE(username)
);
