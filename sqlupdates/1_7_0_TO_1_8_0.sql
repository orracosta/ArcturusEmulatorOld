INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_pet_info.pet_not_found', '"Please provide a petname!"');
ALTER TABLE  `bans` ADD  `cfh_topic` INT( 4 ) NOT NULL DEFAULT  '-1';
ALTER TABLE  `rooms` CHANGE  `paper_floor`  `paper_floor` VARCHAR( 5 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '0.0';
ALTER TABLE  `rooms` CHANGE  `paper_wall`  `paper_wall` VARCHAR( 5 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '0.0';
ALTER TABLE  `rooms` CHANGE  `paper_landscape`  `paper_landscape` VARCHAR( 5 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '0.0';

UPDATE rooms SET paper_floor = '0.0' WHERE paper_floor = '0';
UPDATE rooms SET paper_wall = '0.0' WHERE paper_wall = '0';
UPDATE rooms SET paper_landscape = '0.0' WHERE paper_landscape = '0';

UPDATE items_base SET interaction_type = item_name WHERE item_name LIKE 'wf_xtra_%';
UPDATE items_base SET interaction_type = item_name WHERE item_name LIKE 'wf_act_move_furni_to';