#DATABASE UPDATE: 1.0.7 -> 1.0.8

DROP TABLE `crafting_recipes`;

CREATE TABLE IF NOT EXISTS `crafting_altars_recipes` (
  `altar_id` int(11) NOT NULL,
  `recipe_id` int(11) NOT NULL,
  UNIQUE KEY `altar_id` (`altar_id`,`recipe_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO `crafting_altars_recipes` (`altar_id`, `recipe_id`) VALUES
(22319, 1),
(22319, 2),
(22319, 3),
(22319, 4),
(22319, 5),
(22319, 6),
(22319, 7),
(22319, 8),
(22319, 9),
(22319, 10),
(22319, 11),
(22319, 12),
(22319, 13),
(22319, 14),
(22319, 15),
(22319, 16),
(22319, 17),
(22319, 18),
(22319, 19),
(22319, 20),
(22319, 21),
(22319, 22),
(22319, 23),
(22319, 24);

CREATE TABLE IF NOT EXISTS `crafting_recipes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_name` varchar(32) NOT NULL COMMENT 'WARNING! This field must match a entry in your productdata or crafting WILL NOT WORK!',
  `reward` int(11) NOT NULL,
  `enabled` enum('0','1') NOT NULL DEFAULT '1',
  `achievement` varchar(255) NOT NULL DEFAULT '',
  `secret` enum('0','1') NOT NULL DEFAULT '1',
  `limited` enum('0','1') NOT NULL DEFAULT '0',
  `remaining` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `name` (`product_name`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=25 ;

INSERT INTO `crafting_recipes` (`id`, `product_name`, `reward`, `enabled`, `achievement`, `secret`, `limited`, `remaining`) VALUES
(1, 'clothing_firehelm', 22273, '1', '', '0', '0', 0),
(2, 'clothing_airhelm', 22271, '1', '', '0', '0', 0),
(3, 'clothing_waterhelm', 22267, '1', '', '0', '0', 0),
(4, 'clothing_earthhelm', 22277, '1', '', '0', '0', 0),
(5, 'hween_c15_purecrystal2', 22294, '1', '', '1', '0', 0),
(6, 'hween_c15_purecrystal3', 22304, '1', '', '1', '0', 0),
(7, 'hween_c15_evilcrystal2', 22293, '1', '', '1', '0', 0),
(8, 'hween_c15_evilcrystal3', 22296, '1', '', '1', '0', 0),
(9, 'gothic_c15_chandelier', 22161, '1', '', '1', '0', 0),
(10, 'hween12_guillotine', 18669, '1', '', '1', '0', 0),
(11, 'hween14_mariachi', 20110, '1', '', '1', '0', 0),
(12, 'hween14_doll3', 20134, '1', '', '1', '0', 0),
(13, 'hween14_doll4', 20135, '1', '', '1', '0', 0),
(14, 'clothing_wavy2', 20217, '1', '', '1', '0', 0),
(15, 'guitar_skull', 22953, '1', '', '1', '0', 0),
(16, 'fxbox_fx152', 20253, '1', '', '1', '0', 0),
(17, 'LT_skull', 17136, '1', '', '1', '0', 0),
(18, 'hween14_skelepieces', 20109, '1', '', '1', '0', 0),
(19, 'hween13_bldtrail', 19231, '1', '', '1', '0', 0),
(20, 'penguin_glow', 16940, '1', '', '1', '0', 0),
(21, 'skullcandle', 15722, '1', '', '1', '0', 0),
(22, 'fxbox_fx125', 20255, '1', '', '1', '0', 0),
(23, 'deadduck', 15723, '1', '', '1', '0', 0),
(24, 'qt_xm10_iceduck', 17670, '1', '', '1', '0', 0);

CREATE TABLE IF NOT EXISTS `crafting_recipes_ingredients` (
  `recipe_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `amount` int(11) NOT NULL DEFAULT '1'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO `crafting_recipes_ingredients` (`recipe_id`, `item_id`, `amount`) VALUES
(6, 22294, 3),
(5, 22335, 3),
(7, 22301, 3),
(8, 22293, 3),
(2, 22294, 4),
(2, 22304, 4),
(4, 22335, 4),
(4, 22294, 4),
(1, 22304, 4),
(3, 22294, 8),
(9, 17136, 6),
(9, 22293, 1),
(9, 17237, 1),
(9, 17177, 2),
(10, 18248, 2),
(10, 19231, 1),
(10, 18231, 4),
(10, 22296, 1),
(11, 20109, 3),
(11, 22294, 1),
(11, 18022, 1),
(11, 17136, 1),
(12, 20109, 3),
(12, 22294, 1),
(12, 18933, 1),
(13, 20109, 3),
(13, 22294, 1),
(13, 15696, 1),
(14, 22294, 1),
(14, 19800, 1),
(14, 20084, 2),
(15, 18933, 1),
(15, 22335, 1),
(15, 17191, 1),
(16, 18669, 1),
(16, 22296, 1),
(16, 20122, 1),
(17, 15724, 2),
(17, 22335, 1),
(18, 22335, 1),
(18, 15723, 3),
(19, 15723, 2),
(19, 22335, 1),
(20, 22293, 1),
(20, 16893, 1),
(20, 16924, 1),
(21, 17136, 1),
(21, 17181, 1),
(22, 18207, 1),
(22, 22293, 1),
(23, 15696, 1),
(24, 15696, 1),
(23, 22301, 1),
(24, 22335, 1);

UPDATE achievements SET category = 'explore' WHERE name LIKE 'CameraPhotoCount';

CREATE TABLE  `room_mutes` (
    `room_id` INT NOT NULL ,
    `user_id` INT NOT NULL ,
    `ends` INT NOT NULL
) ENGINE = MYISAM ;

CREATE TABLE  `catalog_items_limited` (
    `catalog_item_id` INT NOT NULL ,
    `number` INT NOT NULL ,
    `user_id` INT NOT NULL DEFAULT  '0',
    `timestamp` INT NOT NULL DEFAULT  '0',
    `item_id` INT NOT NULL DEFAULT  '0',
UNIQUE (
    `catalog_item_id` ,
    `number`
    )
) ENGINE = MYISAM ;

ALTER TABLE  `items_crackable` CHANGE  `prizes`  `prizes` VARCHAR( 255 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT  'Used in the format of item_id:chance;item_id_2:chance. item_id must be id in the items_base table. Default value for chance is 100.';

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.trading.enabled', '1');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.catalog.recycler.enabled', '1');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('debug.show.errors', '1');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.wired.furni.selection.count', '5');

UPDATE `emulator_settings` SET  `key` =  'hotel.catalog.discounts.amount' WHERE  `emulator_settings`.`key` =  'hotel.catalogue.discounts.amount';

DELETE FROM emulator_settings WHERE `key` LIKE 'emulator.log%';

CREATE TABLE IF NOT EXISTS `users_recipes` (
  `user_id` int(11) NOT NULL,
  `recipe` int(11) NOT NULL,
  UNIQUE KEY `user_id` (`user_id`,`recipe`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

#END DATABASE UPDATE: 1.0.7 -> 1.0.8