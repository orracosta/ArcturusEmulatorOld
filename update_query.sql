ALTER TABLE `server_configuration`
ADD COLUMN `default_event_points_column` varchar(255) NOT NULL DEFAULT 'event_pts' AFTER `storage_player_queue_enabled`,
ADD COLUMN `default_event_points_quantity`  int NOT NULL DEFAULT 10 AFTER `default_event_points_column`,
ADD COLUMN `default_promo_points_column`  varchar(255) NOT NULL DEFAULT 'promo_pts' AFTER `default_event_points_quantity`,
ADD COLUMN `default_promo_points_quantity`  int NOT NULL DEFAULT 10 AFTER `default_promo_points_column`,
ADD COLUMN `enable_event_winner_reward`  enum('0','1') NOT NULL DEFAULT '0' AFTER `default_promo_points_quantity`,
ADD COLUMN `event_winner_reward`  enum('credits','duckets','diamonds') NOT NULL DEFAULT 'diamonds' AFTER `enable_event_winner_reward`,
ADD COLUMN `event_winner_reward_quantity`  int NOT NULL DEFAULT 10 AFTER `event_winner_reward`,
ADD COLUMN `enable_promo_winner_reward`  enum('0','1') NOT NULL DEFAULT '0' AFTER `event_winner_reward_quantity`,
ADD COLUMN `promo_winner_reward`  enum('credits','duckets','diamonds') NOT NULL DEFAULT 'diamonds' AFTER `enable_promo_winner_reward`,
ADD COLUMN `promo_winner_reward_quantity`  int NOT NULL DEFAULT 0 AFTER `promo_winner_reward`,
ADD COLUMN `enable_achievement_progress_reward`  enum('0','1') NOT NULL DEFAULT '1' AFTER `promo_winner_reward_quantity`,
ADD COLUMN `camera_photo_furniture_id`  int NOT NULL DEFAULT 0 AFTER `enable_achievement_progress_reward`,
ADD COLUMN `camera_photo_url`  varchar(255) NOT NULL DEFAULT 'localhost' AFTER `camera_photo_furniture_id`,
ADD COLUMN `enable_event_winner_notification`  enum('0','1') NOT NULL DEFAULT '1' AFTER `camera_photo_url`,
ADD COLUMN `default_marketplace_coin`  enum('credits','duckets','diamonds') NOT NULL DEFAULT 'credits' AFTER `enable_event_winner_notification`,
ADD COLUMN `default_marketplace_type`  enum('ALL','LTD') NOT NULL DEFAULT 'ALL' AFTER `default_marketplace_coin`,
ADD COLUMN `online_reward_diamonds_enabled`  enum('0','1') NOT NULL DEFAULT '0' AFTER `default_marketplace_type`,
ADD COLUMN `online_reward_diamonds_interval`  int NOT NULL DEFAULT 0 AFTER `online_reward_diamonds_enabled`,
ADD COLUMN `online_reward_diamonds_quantity`  int NOT NULL DEFAULT 0 AFTER `online_reward_diamonds_interval`;

CREATE TABLE `player_effects` (
`id`  int NOT NULL AUTO_INCREMENT ,
`effect_id`  int NOT NULL ,
`effect_duration`  int NOT NULL ,
`is_totem`  enum('0','1') NOT NULL DEFAULT '0' ,
`is_activated`  enum('0','1') NOT NULL DEFAULT '0' ,
`activated_stamp`  int NOT NULL ,
`quantity`  int NOT NULL ,
PRIMARY KEY (`id`)
)
;
INSERT INTO server_locale VALUES ('command.unloadroom.description', 'Unload do quarto');
INSERT INTO server_locale VALUES ('command.unloadroom.name', 'unload');
INSERT INTO server_locale VALUES ('command.reloadroom.description', 'Reload no quarto');
INSERT INTO server_locale VALUES ('command.reloadroom.name', 'reload_room');
INSERT INTO permission_commands VALUES ('reloadroom_command', 1, 0);
INSERT INTO permission_commands VALUES ('unloadroom_command', 1, 0);

ALTER TABLE `players` ADD COLUMN `fastfood_token` varchar(255) NULL AFTER `forum_posts`;

ALTER TABLE `group_forum_messages`
ADD COLUMN `deleter_id`  int NULL AFTER `pinned`,
ADD COLUMN `deleter_time`  int NULL AFTER `deleter_id`;