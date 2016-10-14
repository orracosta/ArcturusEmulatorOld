#DATABASE UPDATE: 1.0.8 -> 1.0.9

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.description.cmd_plugins', ':plugins');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.description.cmd_massbadge', ':massbadge <badge>');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.description.cmd_update_bots', ':update_bots');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.description.cmd_update_navigator', ':update_navigator');

ALTER TABLE  `bans` ADD  `type` ENUM(  'account',  'ip',  'machine', 'super' ) NOT NULL DEFAULT  'account';
ALTER TABLE  `bans` ADD  `ip` VARCHAR( 50 ) NOT NULL DEFAULT  '' AFTER  `user_id`;
ALTER TABLE  `bans` ADD  `machine_id` VARCHAR( 255 ) NOT NULL AFTER  `ip`;
ALTER TABLE  `bans` CHANGE  `type`  `type` ENUM(  'account',  'ip',  'machine',  'super' ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT  'account' COMMENT  'Account is the entry in the users table banned.
IP is any client that connects with that IP.
Machine is the computer that logged in.
Super is all of the above.';

ALTER TABLE  `permissions` ADD  `cmd_ip_ban` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_ha`;
ALTER TABLE  `permissions` ADD  `cmd_machine_ban` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_ip_ban`;
ALTER TABLE  `permissions` ADD  `cmd_super_ban` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_summonrank`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_ip_ban', 'ipban;banip;ip_ban;ban_ip'), ('commands.description.cmd_ip_ban', ':ipban <username> [reason]');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_machine_ban', 'machineban;banmachine;banmac;macban'), ('commands.description.cmd_machine_ban', ':machineban <username> [reason]');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_super_ban', 'superban;megaban'), ('commands.description.cmd_super_ban', ':superban <username> [reason]');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_machine_ban', '%count% accounts have been MAC banned!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_ip_ban', '%count% accounts have been IP banned!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_super_ban', '%count% accounts have been Super banned!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_ip_ban.ban_self', 'You cannot IP Ban yourself!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_machine_ban.ban_self', 'You cannot issue yourself a MAC Ban!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_super_ban.ban_self', 'You cannot superban yourself!');

ALTER TABLE `items` CHANGE `extra_data` `extra_data` VARCHAR(1024) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '';
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('runtime.threads', '8');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('io.bossgroup.threads', '1');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('io.workergroup.threads', '5');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.teleport.locked.allowed', '1');

ALTER TABLE  `users` CHANGE  `ip_register`  `ip_register` VARCHAR( 45 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
                     CHANGE  `ip_current`   `ip_current`  VARCHAR( 45 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL;

ALTER TABLE  `items` CHANGE  `z`  `z` DOUBLE( 10, 5 ) NOT NULL DEFAULT  '0.0';

ALTER TABLE  `catalog_pages` ADD  `includes` VARCHAR( 32 ) NOT NULL DEFAULT  '' COMMENT  'Example usage: 1;2;3
 This will include page 1, 2 and 3 in the current page.
 Note that permissions are only used for the current entry.';

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('pathfinder.step.maximum.height', '1.1');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('pathfinder.step.allow.falling', '1');

#END DATABASE UPDATE: 1.0.8 -> 1.0.9