INSERT INTO emulator_settings (`key`, `value`) VALUES
        ('camera.enabled', '1'),
        ('camera.price.credits', '100'),
        ('camera.price.points', '100'),
        ('camera.price.points.publish', '1000'),
        ('camera.item_id', '23425'),
        ('camera.extradata', '{"t":%timestamp%, "u":"%id%", "s":%room_id%, "w":"%url%"}');

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
        ('commands.error.cmd_kick.unkickable', '%username% is unkickable!');