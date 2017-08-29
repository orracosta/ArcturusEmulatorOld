ALTER TABLE  `support_tickets` ADD  `category` INT( 3 ) NOT NULL DEFAULT  '0' AFTER  `issue`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('supporttools.not_ticket_owner', 'You are not the moderator currently handling the ticket.');
ALTER TABLE  `support_cfh_topics` ADD  `default_sanction` INT( 3 ) NOT NULL DEFAULT  '0' AFTER  `auto_reply`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.success.cmd_setmax', 'Success! Maximum users in this room changed to %value%.');