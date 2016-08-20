#DATABASE UPDATE: 1.0.4 -> 1.0.5

INSERT INTO emulator_settings (`key`, `value`) VALUES
        ('camera.enabled', '1'),
        ('camera.price.credits', '100'),
        ('camera.price.points', '100'),
        ('camera.price.points.publish', '1000'),
        ('camera.item_id', '23425'),
        ('camera.extradata', '{"t":%timestamp%, "u":"%id%", "s":%room_id%, "w":"%url%"}'),
        ('hotel.navigator.search.maxresults', '35'),
        ('hotel.rooms.max.favorite', '30');

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
        ('commands.error.cmd_kick.unkickable', '%username% is unkickable!'),
        ('camera.disabled', 'Sorry! Camera is disabled :(');

DROP TABLE navigator_filter;
CREATE TABLE IF NOT EXISTS `navigator_filter` (
  `key` varchar(11) NOT NULL,
  `field` varchar(32) NOT NULL,
  `compare` enum('equals','equals_ignore_case','contains') NOT NULL,
  `database_query` varchar(256) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO `navigator_filter` (`key`, `field`, `compare`, `database_query`) VALUES
    ('owner', 'getOwnerName', 'equals_ignore_case', 'SELECT * FROM rooms WHERE owner_name LIKE ?'),
    ('anything', 'filterAnything', 'contains', 'SELECT rooms.* FROM rooms INNER JOIN guilds ON rooms.guild_id = guilds.id = WHERE CONCAT(rooms.owner_name, rooms.name, rooms.description, rooms.tags, guilds.name, guilds.description) LIKE ?'),
    ('roomname', 'getName', 'equals_ignore_case', 'SELECT * FROM rooms WHERE name LIKE ?'),
    ('tag', 'getTags', 'equals', 'SELECT * FROM rooms WHERE tags LIKE ?'),
    ('group', 'getGuildName', 'contains', 'SELECT rooms.* FROM guilds INNER JOIN rooms ON guilds.room_id = rooms.id WHERE CONCAT(guilds.name, guilds.description) LIKE ?'),
    ('desc', 'getDescription', 'contains', 'SELECT * FROM rooms WHERE description LIKE ?'),
    ('promo', 'getPromotionDesc', 'contains', 'SELECT rooms.* FROM rooms INNER JOIN room_promotions ON rooms.id = room_promotions.id WHERE room_promotions.end_timestamp >= UNIX_TIMESTAMP() AND CONCAT (room_promotions.title, room_promotions.description) LIKE ?');


ALTER TABLE  `rooms` ADD  `users` INT NOT NULL DEFAULT  '0' AFTER  `state`;

CREATE TABLE  `users_favorite_rooms` (
    `user_id` INT NOT NULL ,
    `room_id` INT NOT NULL ,
    UNIQUE (
        `user_id` ,
        `room_id`
    )
) ENGINE = MYISAM ;

ALTER TABLE `catalog_pages` CHANGE `page_headline` `page_headline` TEXT CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '';
ALTER TABLE `catalog_pages` CHANGE `page_teaser` `page_teaser` TEXT CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '';
ALTER TABLE `catalog_pages` CHANGE `page_special` `page_special` TEXT CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '';
ALTER TABLE `catalog_pages` CHANGE `page_text1` `page_text1` TEXT CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '';
ALTER TABLE `catalog_pages` CHANGE `page_text2` `page_text2` TEXT CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '';
ALTER TABLE `catalog_pages` CHANGE `page_text_details` `page_text_details` TEXT CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '';
ALTER TABLE `catalog_pages` CHANGE `page_text_teaser` `page_text_teaser` TEXT CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '';

#END DATABASE UPDATE: 1.0.4 -> 1.0.5