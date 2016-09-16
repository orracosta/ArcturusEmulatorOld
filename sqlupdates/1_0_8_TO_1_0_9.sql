#DATABASE UPDATE: 1.0.8 -> 1.0.9

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.description.cmd_plugins', ':plugins');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.description.cmd_massbadge', ':massbadge <badge>');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.description.cmd_update_bots', ':update_bots');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.description.cmd_update_navigator', ':update_navigator');

ALTER TABLE  `bans` ADD  `type` ENUM(  'account',  'ip',  'machine' ) NOT NULL DEFAULT  'account';
ALTER TABLE  `bans` ADD  `ip` VARCHAR( 50 ) NOT NULL DEFAULT  '' AFTER  `user_id`;

ALTER TABLE  `permissions` ADD  `cmd_ip_ban` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_ha`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_ip_ban', 'ipban;banip;ip_ban;ban_ip'), ('commands.description.cmd_ip_ban', ':ipban <username> [reason]');

ALTER TABLE `items` CHANGE `extra_data` `extra_data` VARCHAR(1024) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '';
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('runtime.threads', '8');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('io.bossgroup.threads', '1');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('io.workergroup.threads', '5');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.teleport.locked.allowed', '1');
#END DATABASE UPDATE: 1.0.8 -> 1.0.9