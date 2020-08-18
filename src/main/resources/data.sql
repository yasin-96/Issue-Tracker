INSERT INTO users (id, username, password, role) VALUES ('11111111-1111-1111-1111-111111111111', 'admin', '$2a$10$9X.BJxMFiHvYXksZvX3TheqzO4SPuFpG/LIYc.nkdx2eS.UhGfgvy', 'ADMIN');

INSERT INTO users VALUES ('101', 'username1', 'password1', 'admin');
INSERT INTO users VALUES ('102', 'username2', 'password1', 'reporter');
INSERT INTO users VALUES ('103', 'username3', 'password1', 'reporter');
INSERT INTO users VALUES ('104', 'username4', 'password1', 'maintainer');
INSERT INTO users VALUES ('105', 'username5', 'password1', 'maintainer');
INSERT INTO users VALUES ('444', 'alex', '$2a$10$9JMXNUUZlSpJo76B1luxeOgK8huQ.0.i6Y2oxNaR0MjSJ66cD8SjW', 'maintainer');

INSERT INTO issues VALUES ('001', '1title', '00000000-0000-0000-0000-000000000102', '2020.12.01');
INSERT INTO issues VALUES ('002', '2title', '00000000-0000-0000-0000-000000000101', '2020.12.01');
INSERT INTO issues VALUES ('003', '3title', '00000000-0000-0000-0000-000000000101', '2020.12.01');
INSERT INTO issues VALUES ('004', '4title', '00000000-0000-0000-0000-000000000102', '2020.12.01');
INSERT INTO issues VALUES ('005', '5title', '00000000-0000-0000-0000-000000000102', '2020.12.01');

INSERT INTO comments VALUES ('201', '1. comment', '11111111-1111-1111-1111-111111111111', '001', '2020.12.01' );
INSERT INTO comments VALUES ('202', '2. comment', '11111111-1111-1111-1111-111111111111', '002', '2020.12.01' );
INSERT INTO comments VALUES ('203', '3. comment', '102', '002', '2020.12.01' );
INSERT INTO comments VALUES ('204', '4. comment', '102', '001', '2020.12.01' );
INSERT INTO comments VALUES ('205', '5. comment', '102', '003', '2020.12.01' );


