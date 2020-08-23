CREATE TABLE comments (
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