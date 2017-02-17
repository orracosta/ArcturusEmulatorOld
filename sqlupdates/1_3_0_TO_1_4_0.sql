
ALTER TABLE  `users_pets` ADD  `mp_nose_color` TINYINT( 2 ) NOT NULL DEFAULT  '0' AFTER  `mp_nose`;
ALTER TABLE  `users_pets` ADD  `mp_eyes_color` TINYINT( 2 ) NOT NULL DEFAULT  '0' AFTER  `mp_eyes`;
ALTER TABLE  `users_pets` ADD  `mp_mouth_color` TINYINT( 2 ) NOT NULL DEFAULT  '0' AFTER  `mp_mouth`;
ALTER TABLE  `users_pets` ADD  `mp_death_timestamp` INT NOT NULL DEFAULT  '0' AFTER  `mp_mouth_color`;
ALTER TABLE  `users_pets` ADD  `mp_breedable` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0';
ALTER TABLE  `users_pets` ADD  `mp_allow_breed` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0';