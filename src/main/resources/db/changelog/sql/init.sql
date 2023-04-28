--liquibase formatted sql

--changeset vx007(pasha):1
create table if not exists chats
(
    id bigint primary key,
    status text,
    command text
);
