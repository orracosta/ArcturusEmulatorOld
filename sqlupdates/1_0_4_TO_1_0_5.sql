INSERT INTO emulator_settings (`key`, `value`) VALUES
        ('camera.enabled', '1'),
        ('camera.price.credits', '100'),
        ('camera.price.points', '100'),
        ('camera.price.points.publish', '1000'),
        ('camera.item_id', '23425'),
        ('camera.extradata', '{"t":%timestamp%, "u":"%id%", "s":%room_id%, "w":"%url%"}');

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
        ('commands.error.cmd_kick.unkickable', '%username% is unkickable!');


DROP TABLE navigator_filter;
CREATE TABLE IF NOT EXISTS `navigator_filter` (
  `key` varchar(11) NOT NULL,
  `field` varchar(32) NOT NULL,
  `compare` enum('equals','equals_ignore_case','contains') NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO `navigator_filter` (`key`, `field`, `compare`) VALUES
        ('owner', 'getOwnerName', 'equals_ignore_case'),
        ('anything', 'filterAnything', 'contains'),
        ('roomname', 'getName', 'equals_ignore_case'),
        ('tag', 'getTags', 'equals'),
        ('group', 'getGuildName', 'contains'),
        ('desc', 'getDescription', 'contains'),
        ('promo', 'getPromotionDesc', 'contains');