INSERT INTO users (id, username, password, role) VALUES ('11111111-1111-1111-1111-111111111111', 'admin', '$2a$10$9X.BJxMFiHvYXksZvX3TheqzO4SPuFpG/LIYc.nkdx2eS.UhGfgvy', 'ADMIN');

INSERT INTO issues VALUES ('001', '1title', '1owner', '2020.12.01');
INSERT INTO issues VALUES ('002', '2title', '1owner', '2020.12.01');
INSERT INTO issues VALUES ('003', '3title', '2owner', '2020.12.01');
INSERT INTO issues VALUES ('004', '4title', '3owner', '2020.12.01');
INSERT INTO issues VALUES ('005', '5title', '1owner', '2020.12.01');

INSERT INTO users VALUES ('101', 'username1', 'password1', 'admin');
INSERT INTO users VALUES ('102', 'username2', 'password1', 'reporter');
INSERT INTO users VALUES ('103', 'username3', 'password1', 'reporter');
INSERT INTO users VALUES ('104', 'username4', 'password1', 'maintainer');
INSERT INTO users VALUES ('105', 'username5', 'password1', 'maintainer');