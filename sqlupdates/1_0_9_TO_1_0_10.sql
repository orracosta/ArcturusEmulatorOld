#DATABASE UPDATE: 1.0.9 -> 1.0.10

ALTER TABLE  `polls_questions` ADD  `parent_id` INT( 11 ) NOT NULL DEFAULT  '0' AFTER  `id`;
ALTER TABLE  `polls_questions` CHANGE  `question_number`  `order` INT( 11 ) NOT NULL;
ALTER TABLE  `permissions` ADD  `cmd_word_quiz` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_userinfo`;

INSERT INTO  `emulator_texts` (`key`, `value`) VALUES
('commands.keys.cmd_word_quiz',  'wordquiz;quiz'),
('commands.description.cmd_word_quiz',  ':wordquiz <question>');

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('scripter.warning.marketplace.forbidden', '%username% tried to sell an %itemname% for %credits% which is not allowed to be sold on the marketplace!');

ALTER TABLE `permissions`
ADD `acc_helper_use_guide_tool` ENUM('0','1') NOT NULL DEFAULT '0',
ADD `acc_helper_give_guide_tours` ENUM('0','1') NOT NULL DEFAULT '0',
ADD `acc_helper_judge_chat_reviews` ENUM('0','1') NOT NULL DEFAULT '0';

ALTER TABLE  `achievements_talents` ADD  `reward_badges` VARCHAR( 100 ) NOT NULL DEFAULT  '';

ALTER TABLE  `pet_actions` ADD  `pet_name` VARCHAR( 32 ) NOT NULL AFTER  `pet_type`;

CREATE TABLE  `pet_breeding` (
`pet_id` INT( 2 ) NOT NULL ,
`offspring_id` INT( 2 ) NOT NULL
) ENGINE = MYISAM ;

CREATE TABLE  `pet_breeding_races` (
`pet_type` INT( 2 ) NOT NULL ,
`rarity_level` INT( 2 ) NOT NULL ,
`breed` INT( 2 ) NOT NULL
) ENGINE = MYISAM ;

ALTER TABLE  `users_settings` ADD  `talent_track_citizenship_level` INT( 2 ) NOT NULL DEFAULT  '0',
ADD  `talent_track_helpers_level` INT( 2 ) NOT NULL DEFAULT  '0';

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.navigator.popular.amount', '35');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.navigator.popular.listtype', '1');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('inventory.max.items', '7500');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('inventory.full', 'You''ve reached the inventory limit. Move furniture out of your inventory before buying more!');

ALTER TABLE  `navigator_flatcats` ADD  `list_type` INT NOT NULL DEFAULT  '0' COMMENT  'Display mode in the navigator. 0 for list, 1 for thumbnails.';

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_setmax.invalid_number', 'Invalid number. Specify a number between 0 and 9999'), ('commands.error.cmd_setmax.forgot_number', 'No number specified. Dork.');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_setmax', 'setmax;set_max'), ('commands.description.cmd_setmax', ':setmax <amount>'), ('commands.description.cmd_staffalert', ':sA <message>');
ALTER TABLE  `permissions` ADD  `cmd_setmax` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_say_all`;

ALTER TABLE  `catalog_pages` ADD  `room_id` INT( 11 ) NOT NULL DEFAULT  '0';

ALTER TABLE  `permissions` ADD  `cmd_take_badge` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_superpull`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES
    ('commands.description.cmd_take_badge', ':takebadge <username> <badge>'),
    ('commands.keys.cmd_take_badge', 'takebadge;take_badge;remove_badge;removebadge'),
    ('commands.error.cmd_take_badge.forgot_badge', 'No badge specified!'),
    ('commands.error.cmd_take_badge.forgot_username', 'No username specified!'),
    ('commands.error.cmd_take_badge.no_badge', '%username% does not have %badge%!'),
    ('commands.succes.cmd_take_badge', 'Badge has been taken!');

ALTER TABLE  `permissions`
    ADD  `acc_floorplan_editor` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0',
    ADD  `acc_camera` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0';

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('camera.permission', 'You don''t have permission to use the camera!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('floorplan.permission', 'You don''t have permission to use the floorplan editor!');

ALTER TABLE  `room_wordfilter` CHANGE  `word`  `word` VARCHAR( 25 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL;

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
    ('commands.keys.cmd_update_polls', 'update_polls;reload_polls'),
    ('commands.description.cmd_update_polls', ':update_polls'),
    ('commands.keys.cmd_set_poll', 'setpoll;set_poll'),
    ('commands.description.cmd_set_poll', ':setpoll <id>'),
    ('commands.succes.cmd_set_poll', 'Room poll has been updated!'),
    ('commands.error.cmd_set_poll.invalid_number', 'Please specify a valid number. Use 0 to remove the poll.'),
    ('commands.error.cmd_set_poll.missing_arg', 'Missing poll id. Use 0 to remove the poll from this room.'),
    ('commands.keys.cmd_roomcredits', 'roomcredits;room_credits;roomcoins;room_coins'),
    ('commands.description.cmd_roomcredits', ':roomcredits <amount>'),
    ('commands.keys.cmd_roompixels', 'roompixels;room_pixels;roomduckets;room_duckets'),
    ('commands.description.cmd_roompixels', ':roompixels <amount>'),
    ('commands.keys.cmd_roompoints', 'roompoints;room_points'),
    ('commands.description.cmd_roompoints', ':roompoints <amount>'),
    ('commands.keys.cmd_roomgift', 'roomgift;room_gift'),
    ('commands.description.cmd_roomgift', ':roomgift <item_id> [message]'),
    ('commands.succes.cmd_update_polls', 'Room polls have been reloaded!'),
    ('commands.error.cmd_set_poll.not_found', 'Poll %id% not found!');

ALTER TABLE  `permissions` ADD  `cmd_set_poll` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_setmax`;
ALTER TABLE  `permissions` ADD  `cmd_update_polls` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_update_plugins`;
ALTER TABLE  `permissions` ADD  `cmd_roomcredits` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_redeem`;
ALTER TABLE  `permissions` ADD  `cmd_roompixels` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_roomitem` , ADD  `cmd_roompoints` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_roompixels`;
ALTER TABLE  `permissions` ADD  `cmd_roomgift` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_roomeffect`;

#END DATABASE UPDATE: 1.0.9 -> 1.0.10