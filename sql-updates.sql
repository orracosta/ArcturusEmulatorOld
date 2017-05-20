ALTER TABLE `achievements`
CHANGE COLUMN `pixels` `reward_amount`  int(11) NOT NULL DEFAULT 100 AFTER `level`,
ADD COLUMN `reward_currency`  int(11) NOT NULL DEFAULT 0 AFTER `level`;

