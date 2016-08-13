#DATABASE UPDATE: 1.0.5 -> 1.0.6

INSERT INTO `emulator_settings` (`key`, `value`) VALUES
        ('catalog.guild.price', '10');

ALTER TABLE  `permissions` ADD  `cmd_update_navigator` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_update_items`;

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
        ('commands.keys.cmd_update_navigator', 'update_navigator;update_nav'),
        ('commands.succes.cmd_update_navigator', 'Navigator succesfully reloaded!');

#END DATABASE UPDATE: 1.0.5 -> 1.0.6