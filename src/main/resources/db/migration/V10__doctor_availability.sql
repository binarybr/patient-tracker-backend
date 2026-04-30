create table doctor_availability
(
    id          bigint auto_increment primary key,
    doctor_id   bigint not null,
    day_of_week varchar(10),
    start_time  time,
    end_time    time,
    active      boolean,
    foreign key (doctor_id) references doctors (id)
);