#DATABASE UPDATE: 1.5.0 -> 1.6.0

ALTER TABLE  `users` ADD  `machine_id` VARCHAR( 64 ) NOT NULL DEFAULT  '' AFTER  `ip_current`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('generic.user.not_found', '%user% not found.');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.name', 'Habbo Hotel'), ('hotel.player.name', 'Habbo');
UPDATE  `emulator_texts` SET  `key` =  'commands.succes.cmd_ban.ban_issued' WHERE  `emulator_texts`.`key` =  'commands.succes.cmd_about.ban_issued';
#END DATABASE UPDATE: 1.5.0 -> 1.6.0