--liquibase formatted sql

--changeset ShelterBotContributors:1
create table if not exists pets
(
    id            bigint primary key,
    type          varchar not null,
    name          varchar not null,
    age           int not null,
    breed         varchar not null
);
create table if not exists users
(
    chat_id       bigint primary key,
    name          varchar,
    phone         varchar,
    last_cmd      varchar,
    registered_at timestamp not null,
    pet_id        bigint references pets (id)
);
create table if not exists reports
(
    id            bigint primary key,
    chat_id       bigint not null references users (chat_id),
    photo_id      varchar,
    text          varchar,
    date_time     timestamp not null,
    is_approved   boolean
);
