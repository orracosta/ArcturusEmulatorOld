#DATABASE UPDATE: 1.0.8 -> 1.0.9

ALTER TABLE  `polls_questions` ADD  `parent_id` INT( 11 ) NOT NULL DEFAULT  '0' AFTER  `id`;
ALTER TABLE  `polls_questions` CHANGE  `question_number`  `order` INT( 11 ) NOT NULL;
ALTER TABLE  `permissions` ADD  `cmd_word_quiz` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_userinfo`;

INSERT INTO  `emulator_texts` (`key`, `value`) VALUES
('commands.keys.cmd_word_quiz',  'wordquiz;quiz'),
('commands.description.cmd_word_quiz',  ':wordquiz <question>');


#END DATABASE UPDATE: 1.0.8 -> 1.0.9