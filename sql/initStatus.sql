create table status (id integer not null auto_increment, description varchar(255) not null, primary key (id));

insert into status values
                       (1, 'No input'),
                       (2, 'Group title input'),
                       (3, 'User invite input'),
                       (4, 'Group message input'),
                       (5, 'Schedule file input'),
                       (6, 'All message input'),
                       (7, 'Lesson title input'),
                       (8, 'Teacher name input'),
                       (9, 'Auditorium title input'),
                       (10, 'Lesson time input');