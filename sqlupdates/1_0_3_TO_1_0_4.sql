#DATABASE UPDATE: 1.0.3 -> 1.0.4

ALTER TABLE  `navigator_publiccats` ADD  `image` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `name`;

ALTER IGNORE TABLE pet_breeds
ADD UNIQUE INDEX idx_name (race, color_one, color_two, has_color_one, has_color_two);

#Removing favourite guilds for deleted guilds:
UPDATE users_settings SET guild_id = 0 WHERE guild_id NOT IN (SELECT id FROM guilds) and guild_id > 0;
UPDATE achievements SET category = 'explore' WHERE name LIKE 'CameraPhotoCount';

#END DATABASE UPDATE: 1.0.3 -> 1.0.4