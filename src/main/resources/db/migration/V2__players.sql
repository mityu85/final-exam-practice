create table players(
    id bigint not null auto_increment,
    name varchar(40),
    date_of_birth date,
    position varchar(20),
    team_id bigint,
    primary key (id)
);