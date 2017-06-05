#DATABASE UPDATE: 1.7.0 -> 1.8.0

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_pet_info.pet_not_found', '"Please provide a petname!"');
ALTER TABLE  `bans` ADD  `cfh_topic` INT( 4 ) NOT NULL DEFAULT  '-1';
ALTER TABLE  `rooms` CHANGE  `paper_floor`  `paper_floor` VARCHAR( 5 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '0.0';
ALTER TABLE  `rooms` CHANGE  `paper_wall`  `paper_wall` VARCHAR( 5 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '0.0';
ALTER TABLE  `rooms` CHANGE  `paper_landscape`  `paper_landscape` VARCHAR( 5 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '0.0';

UPDATE rooms SET paper_floor = '0.0' WHERE paper_floor = '0';
UPDATE rooms SET paper_wall = '0.0' WHERE paper_wall = '0';
UPDATE rooms SET paper_landscape = '0.0' WHERE paper_landscape = '0';

UPDATE items_base SET interaction_type = item_name WHERE item_name LIKE 'wf_xtra_%';
UPDATE items_base SET interaction_type = item_name WHERE item_name LIKE 'wf_act_move_furni_to';
UPDATE items_base SET allow_walk = '0' WHERE interaction_type LIKE 'gate';


#Thanks Beny
DROP TABLE IF EXISTS `support_cfh_categories`;
CREATE TABLE `support_cfh_categories` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name_internal` varchar(255) DEFAULT NULL,
  `name_external` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

INSERT INTO `support_cfh_categories` VALUES ('1', 'cyber', 'Cyber sex');
INSERT INTO `support_cfh_categories` VALUES ('2', 'scamming', 'Scamming');
INSERT INTO `support_cfh_categories` VALUES ('3', 'badwords', 'Inappropriate words');
INSERT INTO `support_cfh_categories` VALUES ('4', 'badbehavior', 'Bad behavior');
INSERT INTO `support_cfh_categories` VALUES ('5', 'account', 'Account Issues');
INSERT INTO `support_cfh_categories` VALUES ('6', 'hacking', 'Hacking');

DROP TABLE IF EXISTS `support_cfh_topics`;
CREATE TABLE `support_cfh_topics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) DEFAULT NULL,
  `name_internal` varchar(255) DEFAULT NULL,
  `name_external` varchar(255) DEFAULT NULL,
  `action` enum('mods','auto_ignore','auto_reply') DEFAULT 'mods',
  `auto_reply` text DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=latin1;

INSERT INTO `support_cfh_topics` VALUES ('1', '1', 'cyber.sextalk', 'Sexual talk', 'auto_ignore', 'Thank you for reporting someone for sexual talk. We have put this user on ignore for you. This means that you can no longer see what they are saying. To turn ignore off for this person you need to click on them and press \'Listen\'. We will take a look at this.');
INSERT INTO `support_cfh_topics` VALUES ('2', '1', 'cyber.asking', 'Asking for cyber sex', 'auto_ignore', 'Thank you for reporting someone for sexual talk. We have put this user on ignore for you. This means that you can no longer see what they are saying. To turn ignore off for this person you need to click on them and press \'Listen\'. We will take a look at this.');
INSERT INTO `support_cfh_topics` VALUES ('3', '1', 'cyber.offering', 'Offering cyber sex', 'auto_ignore', 'Thank you for reporting someone for sexual talk. We have put this user on ignore for you. This means that you can no longer see what they are saying. To turn ignore off for this person you need to click on them and press \'Listen\'. We will take a look at this.');
INSERT INTO `support_cfh_topics` VALUES ('4', '1', 'cyber.porn', 'Sending porn', 'mods', null);
INSERT INTO `support_cfh_topics` VALUES ('5', '2', 'scamming.scamsite', 'Promoting scam sites', 'mods', null);
INSERT INTO `support_cfh_topics` VALUES ('6', '2', 'scamming.sellingirl', 'Selling virtual items for real money', 'mods', null);
INSERT INTO `support_cfh_topics` VALUES ('7', '2', 'scamming.stealingfurni', 'Stealing furni or coins', 'mods', null);
INSERT INTO `support_cfh_topics` VALUES ('8', '2', 'scamming.account', 'Stealing account information', 'mods', null);
INSERT INTO `support_cfh_topics` VALUES ('9', '2', 'scamming.casino', 'Casino scamming', 'auto_reply', 'Habbo does not get involved with the casino community due to cases being complex and hard to track. Players gamble at their own risk. When the fun stops, stop.');
INSERT INTO `support_cfh_topics` VALUES ('10', '3', 'badwords.roomname', 'Room name', 'mods', null);
INSERT INTO `support_cfh_topics` VALUES ('11', '3', 'badwords.roomdesc', 'Room description', 'mods', null);
INSERT INTO `support_cfh_topics` VALUES ('12', '3', 'badwords.username', 'Username', 'mods', null);
INSERT INTO `support_cfh_topics` VALUES ('13', '3', 'badwords.motto', 'Moto', 'mods', null);
INSERT INTO `support_cfh_topics` VALUES ('14', '3', 'badwords.grouporevent', 'Group or event name', 'mods', null);
INSERT INTO `support_cfh_topics` VALUES ('15', '4', 'badbehavior.trolling', 'Trolling', 'auto_reply', 'Thanks for your report. Please call a moderator to the room by following these steps.\r\n1. Under the Help window click on \'Get immediate help\'.\r\n2. Then click on \'Chat to a Moderator\'.\r\n3. Let them know that somebody is trolling in the room.\r\n4. A moderator will open a chat session with you to resolve the problem.');
INSERT INTO `support_cfh_topics` VALUES ('16', '4', 'badbehavior.blocking', 'Blocking', 'auto_reply', 'Thanks for your report. Please call a moderator to the room by following these steps.\r\n1. Under the Help window click on \'Get immediate help\'.\r\n2. Then click on \'Chat to a Moderator\'.\r\n3. Let them know that somebody is blocking in the room.\r\n4. A moderator will open a chat session with you to resolve the problem.');
INSERT INTO `support_cfh_topics` VALUES ('17', '4', 'badbehavior.flooding', 'Flooding', 'auto_reply', 'Thanks for your report. Please call a moderator to the room by following these steps.\r\n1. Under the Help window click on \'Get immediate help\'.\r\n2. Then click on \'Chat to a Moderator\'.\r\n3. Let them know that somebody is flooding the room.\r\n4. A moderator will open a chat session with you to resolve the problem.');
INSERT INTO `support_cfh_topics` VALUES ('18', '4', 'badbehavior.young', 'Too young for Habbo', 'mods', null);
INSERT INTO `support_cfh_topics` VALUES ('19', '4', 'badbehavior.staffimpersonation', 'Staff impersonation', 'mods', null);
INSERT INTO `support_cfh_topics` VALUES ('20', '4', 'badbehavior.offensive', 'Offensive language', 'auto_ignore', 'We have put this user on ignore for you. This means that you can no longer see what they are saying. To turn ignore off for this person you need to click on them and press \'Listen\'');
INSERT INTO `support_cfh_topics` VALUES ('21', '4', 'badbehavior.hate', 'Hate speech', 'auto_ignore', 'We have put this user on ignore for you. This means that you can no longer see what they are saying. To turn ignore off for this person you need to click on them and press \'Listen\'');
INSERT INTO `support_cfh_topics` VALUES ('22', '4', 'badbehavior.violence', 'Violence', 'auto_ignore', 'We have put this user on ignore for you. This means that you can no longer see what they are saying. To turn ignore off for this person you need to click on them and press \'Listen\'');
INSERT INTO `support_cfh_topics` VALUES ('23', '5', 'account.username', 'Change username', 'auto_reply', 'It is currently not possible to change your username in Habbo. When that feature becomes available you\'ll be sure to know! :)');
INSERT INTO `support_cfh_topics` VALUES ('24', '5', 'accunt.payment', 'Payment issues', 'auto_reply', 'Thanks for your report. Unfortunately Game Moderators cannot help with payment issues. Please report your payment issue to us at the help desk on the website where our team of specialists will get back to you.');
INSERT INTO `support_cfh_topics` VALUES ('25', '5', 'account.earn', 'Earn gems', 'auto_reply', 'Thanks for your report. Unfortunately Game Moderators cannot help with payment issues. Please report your payment issue to us at the help desk on the website where our team of specialists will get back to you.');
INSERT INTO `support_cfh_topics` VALUES ('26', '5', 'account.other', 'Something else', 'auto_reply', 'Please use the helpdesk on the website for help with this matter.');
INSERT INTO `support_cfh_topics` VALUES ('27', '6', 'hacking.game', 'Threat to hack Habbo', 'auto_reply', 'We work very hard to ensure that Habbo is safe for all that play. This involves using only the best security technology. We would like to thank you for reporting this to us, but we don\'t think this person is capable of hacking Habbo :)');
INSERT INTO `support_cfh_topics` VALUES ('28', '6', 'hacking.player', 'Threat to hack a player', 'auto_reply', 'There is no way that another Habbo can hack you without knowing your Habbo password or Habbo email address. Please make sure that you are using a secure password which is not easy to remember. We recommend passwords which include letters and numbers such as fl0w3rs. If you wanted to be even more secure you could include a special letter, such as fl0w3r$.\r\n\r\nTo change your Habbo password go to your profile on the website.');
INSERT INTO `support_cfh_topics` VALUES ('29', '6', 'hacking.furni', 'Scripted furniture', 'mods', null);
INSERT INTO `support_cfh_topics` VALUES ('30', '6', 'hacking.room', 'Scripted room', 'mods', null);
INSERT INTO `support_cfh_topics` VALUES ('31', '6', 'hacking.character', 'Scripted character', 'mods', null);
INSERT INTO `support_cfh_topics` VALUES ('32', '6', 'hacking.other', 'Other hacking', 'mods', null);

ALTER TABLE  `camera_web` ADD  `room_id` INT( 11 ) NOT NULL DEFAULT  '0' AFTER  `user_id`;

ALTER TABLE  `commandlogs` CHANGE  `command`  `command` VARCHAR( 256 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT  '';
UPDATE emulator_settings SET `value` = '0' WHERE `key` LIKE  'debug.show.%';
DELETE FROM emulator_errors;
DELETE FROM gift_wrappers;
INSERT INTO gift_wrappers (sprite_id, item_id) SELECT sprite_id, id FROM items_base WHERE item_name LIKE 'present_gen%';
UPDATE gift_wrappers SET type = 'gift';
INSERT INTO gift_wrappers (sprite_id, item_id) SELECT sprite_id, id FROM items_base WHERE item_name LIKE 'present_wrap%';
INSERT INTO `emulator_settings` (`key`, `value`) VALUES
('seasonal.currency.ducket', '0'),
('seasonal.currency.pixel', '0'),
('seasonal.currency.diamond', '5'),
('seasonal.currency.shell', '4'),
('seasonal.currency.names', 'ducket;pixel;shell;diamond');

ALTER TABLE  `permissions` ADD  `cmd_hal` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_ha`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.description.cmd_hal', ':hal <url> <message>'), ('commands.keys.cmd_hal', 'hal;halink');
ALTER TABLE  `permissions` ADD  `acc_enable_others` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `acc_empty_others`;
ALTER TABLE  `rooms` CHANGE  `chat_mode`  `chat_mode` INT( 11 ) NOT NULL DEFAULT  '0';
UPDATE  `emulator_settings` SET  `value` =  '0' WHERE  `emulator_settings`.`key` =  'debug.show.packets';
UPDATE  `emulator_settings` SET  `value` =  '0' WHERE  `emulator_settings`.`key` =  'debug.show.packets.undefined';
#END DATABASE UPDATE: 1.7.0 -> 1.8.0