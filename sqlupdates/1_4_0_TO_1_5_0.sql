#DATABASE UPDATE: 1.4.0 -> 1.5.0

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.bot.butler.servedistance', '5');
UPDATE items_base SET interaction_type = 'fx_box' WHERE item_name LIKE 'fxbox_fx%';
#END DATABASE UPDATE: 1.4.0 -> 1.5.0