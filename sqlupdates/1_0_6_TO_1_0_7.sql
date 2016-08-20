#DATABASE UPDATE: 1.0.6 -> 1.0.7

ALTER TABLE  `permissions` ADD  `acc_update_notifications` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0';
DELETE FROM emulator_settings WHERE `key` LIKE 'emulator.version';

#END DATABASE UPDATE: 1.0.5 -> 1.0.6