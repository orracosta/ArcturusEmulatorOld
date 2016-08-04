#DATABASE UPDATE: 1.0.0 -> 1.0.1

#IF YOU GET ERRORS ON THIS PROCEDURE, JUST DROP THE acc_kickwars COLUMN FROM THE permissions TABLE!!

drop procedure if exists drop_acc_kickwars;
delimiter ';;'
create procedure drop_acc_kickwars() begin

 if exists (select * from information_schema.columns where table_name = 'permissions' and column_name = 'acc_kickwars') then
  alter table permissions drop column `acc_kickwars`;
 end if;

end;;

delimiter ';'
call drop_acc_kickwars();

drop procedure if exists drop_acc_kickwars;

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.navigator.camera', '1');
ALTER TABLE `navigator_flatcats` ADD  `max_user_count` INT( 11 ) NOT NULL DEFAULT  '100';
ALTER TABLE `navigator_flatcats` ADD  `public` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0';
DELETE FROM `navigator_flatcats`;
ALTER TABLE `navigator_flatcats` AUTO_INCREMENT = 1;
INSERT INTO `navigator_flatcats` (`id`, `min_rank`, `caption`, `can_trade`, `max_user_count`, `public`) VALUES
                                 (NULL, '1', '${navigator.flatcategory.global.BC}', '0', '100', '0'),
                                 (NULL, '1', '${navigator.flatcategory.global.BUILDING}', '0', '100', '0'),
                                 (NULL, '1', '${navigator.flatcategory.global.CHAT}', '0', '100', '0'),
                                 (NULL, '1', '${navigator.flatcategory.global.FANSITE}', '0', '100', '0'),
                                 (NULL, '1', '${navigator.flatcategory.global.GAMES}', '0', '100', '0'),
                                 (NULL, '1', '${navigator.flatcategory.global.HELP}', '0', '100', '0'),
                                 (NULL, '1', '${navigator.flatcategory.global.LIFE}', '0', '100', '0'),
                                 (NULL, '7', '${navigator.flatcategory.global.OFFICIAL}', '0', '100', '1'),
                                 (NULL, '1', '${navigator.flatcategory.global.PARTY}', '0', '100', '0'),
                                 (NULL, '1', '${navigator.flatcategory.global.PERSONAL}', '0', '100', '0'),
                                 (NULL, '1', '${navigator.flatcategory.global.REVIEWS}', '0', '100', '0'),
                                 (NULL, '1', '${navigator.flatcategory.global.TRADING}', '1', '100', '0');

CREATE TABLE  `navigator_publiccats` (
`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
`name` VARCHAR( 32 ) NOT NULL DEFAULT  'Staff Picks',
`visible` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '1'
) ENGINE = MYISAM ;

INSERT INTO `navigator_publiccats` (`id`, `name`) VALUES
                                   ('1', 'Staff Picks'),
                                   ('2', 'Official Games'),
                                   ('3', 'Official Fansites'),
                                   ('4', 'BAW: Builders at Work'),
                                   ('5', 'Room Bundles'),
                                   ('6', 'Safety');

DROP TABLE `navigator_publics`;

CREATE TABLE  `navigator_publics` (
`public_cat_id` INT NOT NULL ,
`room_id` INT NOT NULL,
`visible` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '1'
) ENGINE = MYISAM ;

CREATE TABLE  `navigator_filter` (
`field` VARCHAR( 32 ) NOT NULL ,
`compare` ENUM(  'equals',  'equals_ignore_case',  'contains' ) NOT NULL
) ENGINE = MYISAM ;

INSERT INTO `navigator_filter` (`field`, `compare`) VALUES
                               ('ownerName', 'equals'),
                               ('description', 'contains'),
                               ('name', 'contains'),
                               ('tags', 'equals');

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
                             ('commands.keys.cmd_update_hotel_view', 'update_view;update_hotel_view;update_hotelview'),
                             ('commands.description.cmd_update_hotel_view', ':update_hotel_view'),
                             ('commands.succes.cmd_update_hotel_view', 'Hotelview reloaded!');

ALTER TABLE  `permissions` ADD  `cmd_update_hotel_view` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_update_guildparts`;

#END DATABASE UPDATE 1.0.0 -> 1.0.1