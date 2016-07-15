ALTER TABLE  `navigator_publiccats` ADD  `image` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `name`;

ALTER IGNORE TABLE pet_breeds
ADD UNIQUE INDEX idx_name (race, color_one, color_two, has_color_one, has_color_two);