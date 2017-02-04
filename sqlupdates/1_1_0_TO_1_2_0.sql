#DATABASE UPDATE: 1.1.0 -> 1.2.0

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
    ('commands.keys.cmd_roommute', 'roommute;room_mute'),
    ('commands.description.cmd_roommute', ':roommute'),
    ('commands.succes.cmd_roommute.unmuted', 'The room has been unmuted!'),
    ('commands.succes.cmd_roommute.muted', 'The room has been muted!'),
    ('commands.keys.cmd_lay', 'lay'),
    ('commands.description.cmd_lay', ':lay');

#This is your backup table. Just incase you mess things up.
CREATE TABLE catalog_pages_1_1_0 SELECT * FROM catalog_pages;

ALTER TABLE `catalog_pages` CHANGE `page_layout` `page_layout` ENUM('default_3x3','club_buy','club_gift','frontpage','spaces','recycler','recycler_info','recycler_prizes','trophies','plasto','marketplace','marketplace_own_items','pets','spaces_new','soundmachine','guilds','guild_furni','info_duckets','info_rentables','info_pets','roomads','single_bundle','sold_ltd_items','badge_display','bots','pets2','pets3','productpage1','room_bundle','recent_purchases','default_3x3_color_grouping','guild_forum','vip_buy','info_loyalty','loyalty_vip_buy','collectibles','frontpage_featured') CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT 'default_3x3';

INSERT INTO `catalog_pages` (`id`, `parent_id`, `caption_save`, `caption`, `icon_color`, `icon_image`, `visible`, `enabled`, `min_rank`, `club_only`, `order_num`, `page_layout`, `page_headline`, `page_teaser`, `page_special`, `page_text1`, `page_text2`, `page_text_details`, `page_text_teaser`, `vip_only`, `includes`, `room_id`) VALUES (NULL, '-1', 'furni', 'Furni', '1', '1', '1', '0', '1', '0', '1', 'default_3x3', '', '', '', '', '', '', '', '0', '', '0');
UPDATE catalog_pages, (SELECT id FROM catalog_pages ORDER BY id DESC LIMIT 1) new_id SET parent_id = new_id.id WHERE parent_id = -1;
UPDATE catalog_pages SET parent_id = -1, page_layout = 'frontpage_featured' WHERE caption LIKE 'frontpage';
UPDATE catalog_pages SET parent_id = -1 ORDER BY id DESC LIMIT 1;

#END DATABASE UPDATE: 1.1.0 -> 1.2.0