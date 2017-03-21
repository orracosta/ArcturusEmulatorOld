#DATABASE UPDATE: 1.4.0 -> 1.5.0

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.bot.butler.servedistance', '5');
UPDATE items_base SET interaction_type = 'fx_box' WHERE item_name LIKE 'fxbox_fx%';

ALTER TABLE `rooms` CHANGE `paper_floor` `paper_floor` VARCHAR(5) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE `rooms` CHANGE `paper_wall` `paper_wall` VARCHAR(5) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE `rooms` CHANGE `paper_landscape` `paper_landscape` VARCHAR(5) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';

#END DATABASE UPDATE: 1.4.0 -> 1.5.0