#DATABASE UPDATE: 1.0.10 -> 1.1.0

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
    ('commands.error.cmd_unmute.not_specified', 'No user specified to unmute!'),
    ('commands.error.cmd_unmute.not_found', '%user% is not online!'),
    ('commands.succes.cmd_unmute', '%user% has been unmuted!'),
    ('commands.keys.cmd_unmute', 'unmute'),
    ('commands.description.cmd_unmute', ':unmute <username>');

ALTER TABLE  `permissions` ADD  `cmd_unmute` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_unload`;

ALTER TABLE  `bans` ADD  `timestamp` INT NOT NULL AFTER  `user_staff_id`;

#END DATABASE UPDATE: 1.0.10 -> 1.1.0