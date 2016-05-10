/* Deletes acc_kickwars column from the permissions table. */
drop procedure if exists drop_acc_kickwars;
delimiter ';;'
create procedure drop_acc_kickwars() begin

 /* delete columns if they exist */
 if exists (select * from information_schema.columns where table_name = 'permissions' and column_name = 'acc_kickwars') then
  alter table permissions drop column `acc_kickwars`;
 end if;

end;;

delimiter ';'
call schema_change();

drop procedure if exists drop_acc_kickwars;