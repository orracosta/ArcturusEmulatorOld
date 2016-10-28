#DATABASE UPDATE: 1.0.8 -> 1.0.9

ALTER TABLE  `polls_questions` ADD  `parent_id` INT( 11 ) NOT NULL DEFAULT  '0' AFTER  `id`;
ALTER TABLE  `polls_questions` CHANGE  `question_number`  `order` INT( 11 ) NOT NULL;

#END DATABASE UPDATE: 1.0.8 -> 1.0.9