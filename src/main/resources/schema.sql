create table issues
(
	id UUID not null default random_uuid(),
	title varchar(255) not null,
	owner varchar(255) not null,

    primary key (id)
);
