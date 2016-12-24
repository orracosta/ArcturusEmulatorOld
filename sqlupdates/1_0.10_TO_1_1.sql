#DATABASE UPDATE: 1.0.10 -> 1.1.0

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
    ('commands.error.cmd_unmute.not_specified', 'No user specified to unmute!'),
    ('commands.error.cmd_unmute.not_found', '%user% is not online!'),
    ('commands.succes.cmd_unmute', '%user% has been unmuted!'),
    ('commands.keys.cmd_unmute', 'unmute'),
    ('commands.description.cmd_unmute', ':unmute <username>');

ALTER TABLE  `permissions` ADD  `cmd_unmute` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_unload`;

ALTER TABLE  `bans` ADD  `timestamp` INT NOT NULL AFTER  `user_staff_id`;

ALTER TABLE  `permissions` ADD  `cmd_give_rank` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_gift`;

INSERT INTO`emulator_texts` (`key`, `value`) VALUES
    ('commands.keys.cmd_give_rank', 'giverank;setrank;give_rank;set_rank'),
    ('commands.description.cmd_give_rank', ':giverank <username> <rank>'),
    ('commands.error.cmd_give_rank.not_found', 'Rank %id% was not found!'),
    ('commands.succes.cmd_give_rank.updated', '%username% has been given the rank %id%'),
    ('commands.error.cmd_give_rank.higher', 'You cannot rank %username% to a higher rank than you are!'),
    ('commands.error.cmd_give_rank.user_offline', '%username% is not online!'),
    ('commands.error.cmd_give_rank.missing_rank', 'Missing rank. Usage: '),
    ('commands.error.cmd_give_rank.missing_username', 'Missing username. Usage: '),
    ('commands.generic.cmd_give_rank.new_rank', 'Your rank has been updated. Your rank is now %id%');

#END DATABASE UPDATE: 1.0.10 -> 1.1.0