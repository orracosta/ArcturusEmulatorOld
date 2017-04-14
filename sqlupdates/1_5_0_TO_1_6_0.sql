#DATABASE UPDATE: 1.5.0 -> 1.6.0

ALTER TABLE  `users` ADD  `machine_id` VARCHAR( 64 ) NOT NULL DEFAULT  '' AFTER  `ip_current`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('generic.user.not_found', '%user% not found.');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.name', 'Habbo Hotel'), ('hotel.player.name', 'Habbo');
UPDATE  `emulator_texts` SET  `key` =  'commands.succes.cmd_ban.ban_issued' WHERE  `emulator_texts`.`key` =  'commands.succes.cmd_about.ban_issued';
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('catalog.page.vipgifts', '0');

CREATE TABLE `users_achievements_queue` (
    `user_id` INT NOT NULL ,
    `achievement_id` INT NOT NULL ,
    `amount` INT NOT NULL
) ENGINE = MYISAM ;
ALTER TABLE users_achievements_queue ADD UNIQUE `unique_index` (`user_id`, `achievement_id`);

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.rollers.speed.maximum', '100');

ALTER TABLE  `vouchers` ADD  `catalog_item_id` INT NOT NULL DEFAULT  '0';

ALTER TABLE  `permissions` ADD  `cmd_reload_room` ENUM(  '0',  '1',  '2' ) NOT NULL DEFAULT  '2' AFTER  `cmd_redeem`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_reload_room', 'reload_room;reload;reloadroom'), ('commands.description.cmd_reload_room', ':reload_room');
ALTER TABLE  `bots` ADD  `effect` INT( 3 ) NOT NULL DEFAULT  '0';

UPDATE  `emulator_texts` SET  `key` =  'commands.error.cmd_ban.forgot_user' WHERE  `emulator_texts`.`key` =  'commands.error.cmd_ban.forgot_message';

#END DATABASE UPDATE: 1.5.0 -> 1.6.0