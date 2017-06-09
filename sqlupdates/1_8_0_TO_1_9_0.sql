ALTER TABLE  `permissions` ADD  `cmd_empty_bots` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '1' AFTER  `cmd_empty`;
ALTER TABLE  `permissions` ADD  `cmd_empty_pets` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '1' AFTER  `cmd_empty_bots`;

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
    ('commands.succes.cmd_empty_bots.cleared', 'Bots inventory cleared!'),
    ('commands.succes.cmd_empty_bots.verify', 'This will remove all bots from your inventory. Type :emptybots %generic.yes% to continue!'),
    ('commands.keys.cmd_empty_bots', 'emptybots;empty_bots;deletebots'),
    ('commands.description.cmd_empty_bots', ':emptybots'),
    ('commands.succes.cmd_empty_pets.cleared', 'Pets inventory cleared!'),
    ('commands.succes.cmd_empty_pets.verify', 'This will remove all pets from your inventory. Type :emptypets %generic.yes% to continue!'),
    ('commands.keys.cmd_empty_pets', 'emptypets;empty_pets;deletepets'),
    ('commands.description.cmd_empty_pets', ':emptypets');