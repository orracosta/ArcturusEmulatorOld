#DATABASE UPDATE: 1.0.1 -> 1.0.2

CREATE TABLE  `users_clothing` (
`user_id` INT NOT NULL ,
`clothing_id` INT NOT NULL ,
UNIQUE (
`user_id` ,
`clothing_id`
)
) ENGINE = MYISAM ;

#FILLING PERMISSIONS TABLE TILL RANK 7.

INSERT INTO permissions (id, rank_name) VALUES (1, 'rank_1');
INSERT INTO permissions (id, rank_name) VALUES (2, 'rank_2');
INSERT INTO permissions (id, rank_name) VALUES (3, 'rank_3');
INSERT INTO permissions (id, rank_name) VALUES (4, 'rank_4');
INSERT INTO permissions (id, rank_name) VALUES (5, 'rank_5');
INSERT INTO permissions (id, rank_name) VALUES (6, 'rank_6');
INSERT INTO permissions (id, rank_name) VALUES (7, 'rank_7');

#END DATABASE UPDATE: 1.0.1 -> 1.0.2