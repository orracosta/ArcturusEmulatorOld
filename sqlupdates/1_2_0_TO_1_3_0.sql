#DATABASE UPDATE: 1.2.0 -> 1.3.0

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('camera.wait', 'Please wait %seconds% seconds before making another picture.');

ALTER TABLE  `rooms` CHANGE  `name`  `name` VARCHAR( 50 ) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT  '';
ALTER TABLE  `rooms` CHANGE  `description`  `description` VARCHAR( 512 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '';
ALTER TABLE  `rooms` CHANGE  `password`  `password` VARCHAR( 20 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '';

#END DATABASE UPDATE: 1.2.0 -> 1.3.0