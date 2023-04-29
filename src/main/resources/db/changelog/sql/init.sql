--liquibase formatted sql

--changeset vx007(pasha):1
create table if not exists pets
(
    id        bigint primary key,
    name      varchar not null,
    age       int check ( age > 0 ),
    which_pet varchar not null,
    breed     varchar
);
create table if not exists photo_report
(
    id         bigint primary key,
    file_path  varchar not null,
    file_size  bigint  not null,
    media_type varchar not null,
    pet_id     bigint references pets (id)
);
create table if not exists shelter_cat_user
(
    id                 bigint primary key,
    chat_id            bigint  not null,
    name               varchar not null,
    mail               varchar not null,
    phone              varchar not null,
    start_trial_period date,
    end_trial_period   date,
    trial_period       varchar,
    pet_id             bigint references pets (id)
);
create table if not exists shelter_dog_user
(
    id                 bigint primary key,
    chat_id            bigint  not null,
    name               varchar not null,
    mail               varchar not null,
    phone              varchar not null,
    start_trial_period date,
    end_trial_period   date,
    trial_period       varchar,
    pet_id             bigint references pets (id)
);
create table if not exists report_pet
(
    id                  bigint primary key,
    chat_id             bigint    not null,
    info_pet            varchar   not null,
    date_time           timestamp not null,
    quality             boolean,
    photo_report_id     bigint references photo_report (id),
    shelter_dog_user_id bigint references shelter_dog_user (id),
    shelter_cat_user_id bigint references shelter_cat_user (id)
);
create table if not exists users
(
    chat_id       bigint primary key,
    shelter       varchar,
    name          varchar,
    phone         varchar,
    mail          varchar,
    registered_at timestamp not null
);
create table if not exists volunteers
(
    id      bigint primary key,
    chat_id bigint  not null,
    name    varchar not null
);

create table if not exists info
(
    name    varchar,
    details varchar
);