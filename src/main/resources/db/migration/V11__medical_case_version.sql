create table medical_case_version
(
    id              bigint auto_increment primary key,
    medical_case_id bigint not null,
    version         int,
    diagnosis       longtext,
    symptoms        longtext,
    medicines       longtext,
    created_at      datetime,
    foreign key (medical_case_id) references medical_cases (id)
);