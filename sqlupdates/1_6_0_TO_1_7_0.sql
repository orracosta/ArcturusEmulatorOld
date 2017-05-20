#DATABASE UPDATE: 1.6.0 -> 1.7.0

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('scripter.warning.packet.closedice', '%username% tried to change furniture state on non dice using the close dice packet on item %id% %itemname%');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_update_guildparts', 'Succes! Guild badgeparts and guild badge imager has been reloaded!');

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('ban.info.user_id', 'User ID: '), ('ban.info.type', 'Ban Type: '), ('ban.info.reason', 'Reason: '), ('ban.info.staff_id', 'Staff ID: '), ('ban.info.date_issued', 'Date: '), ('ban.info.date_expire', 'Expire Date: '), ('ban.info.ip', 'IP: '), ('ban.info.machine', 'Machine: ');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('command.cmd_userinfo.user_id', 'ID'), ('command.cmd_userinfo.user_name', 'Username'), ('command.cmd_userinfo.motto', 'Motto'), ('command.cmd_userinfo.rank', 'Rank'), ('command.cmd_userinfo.online', 'Online'), ('command.cmd_userinfo.email', 'Email'), ('command.cmd_userinfo.ip_register', 'Register IP'), ('command.cmd_userinfo.ip_current', 'Current IP'), ('command.cmd_userinfo.banned', 'Banned'), ('command.cmd_userinfo.currencies', 'Currencies'), ('command.cmd_userinfo.achievement_score', 'Score'), ('command.cmd_userinfo.credits', 'Credits'), ('command.cmd_userinfo.current_activity', 'Current Activity'), ('command.cmd_userinfo.room', 'Room'), ('command.cmd_userinfo.respect_left', 'Respect Left'), ('command.cmd_userinfo.pet_respect_left', 'Pet Respect Left'), ('command.cmd_userinfo.allow_trade', 'Allow Trade'), ('command.cmd_userinfo.allow_follow', 'Allow Follow'), ('command.cmd_userinfo.allow_friend_request', 'Allow Friend Request'), ('command.cmd_userinfo.total_bans', 'Total bans'), ('command.cmd_userinfo.ban_info', 'Recent Ban Info'), ('command.cmd_userinfo.userinfo', 'Userinfo');

ALTER TABLE  `users_wardrobe` CHANGE  `look`  `look` VARCHAR( 256 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL;
UPDATE `items_base` SET `interaction_type` = 'football_goal_blue', stack_height = '0.01' WHERE item_name LIKE 'fball_goal_b';
UPDATE `items_base` SET `interaction_type` = 'football_goal_green', stack_height = '0.01' WHERE item_name LIKE 'fball_goal_g';
UPDATE `items_base` SET `interaction_type` = 'football_goal_red', stack_height = '0.01' WHERE item_name LIKE 'fball_goal_r';
UPDATE `items_base` SET `interaction_type` = 'football_goal_yellow', stack_height = '0.01' WHERE item_name LIKE 'fball_goal_y';
UPDATE `items_base` SET `interaction_type` = 'football_counter_blue' WHERE item_name LIKE 'fball_score_b';
UPDATE `items_base` SET `interaction_type` = 'football_counter_green' WHERE item_name LIKE 'fball_score_g';
UPDATE `items_base` SET `interaction_type` = 'football_counter_red' WHERE item_name LIKE 'fball_score_r';
UPDATE `items_base` SET `interaction_type` = 'football_counter_yellow' WHERE item_name LIKE 'fball_score_y';
UPDATE `items_base` SET `interaction_type` = 'football_gate' WHERE item_name LIKE 'fball_gate';

ALTER TABLE  `achievements` CHANGE  `pixels`  `reward_amount` INT( 11 ) NOT NULL DEFAULT  '100';
ALTER TABLE  `achievements` ADD  `reward_type` INT( 2 ) NOT NULL DEFAULT  '0' AFTER  `reward_amount`;
#END DATABASE UPDATE: 1.6.0 -> 1.7.0