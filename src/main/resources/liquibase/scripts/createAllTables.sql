-- liquebase formatted sql
-- changeset dianakyz

create table pets(
                     id      BIGSERIAL primary key,
                     name    varchar not null,
                     age     integer check (age > 0),
                     whichPet    varchar not null,
                     breed   varchar
);
create table photo_report(
                             id           BIGSERIAL primary key,
                             file_path    varchar not null,
                             file_size    BIGINT not null,
                             media_type   varchar not null,
                             pet_id       bigint REFERENCES pets (id)
);
create table shelter_cat_user(
                                 id                  BIGSERIAL primary key,
                                 chat_id             bigint not null,
                                 name                varchar not null,
                                 mail                varchar not null,
                                 phone               varchar not null,
                                 start_trial_period  date,
                                 end_trial_period    date,
                                 trial_period        varchar,
                                 pet_id              bigint REFERENCES pets (id)
);
create table shelter_dog_user(
                                 id                  BIGSERIAL primary key,
                                 chat_id             bigint not null,
                                 name                varchar not null,
                                 mail                varchar not null,
                                 phone               varchar not null,
                                 start_trial_period  date,
                                 end_trial_period    date,
                                 trial_period        varchar,
                                 pet_id              bigint REFERENCES pets (id)
);
create table text_report(
                            id                     BIGSERIAL primary key,
                            chat_id                bigint    not null,
                            info_pet               text      not null,
                            date_time              Timestamp not null,
                            quality                Boolean,
                            photo_pet_id           bigint REFERENCES photo_report (id),
                            shelter_dog_user_id    bigint REFERENCES shelter_dog_user (id),
                            shelter_cat_user_id    bigint REFERENCES shelter_cat_user (id)
);
Create TABLE users (
                       chat_id     BIGSERIAL primary key,
                       shelter     varchar,
                       name        varchar,
                       phone       varchar,
                       mail        varchar
);
create table volunteers(
                           id      BIGSERIAL primary key,
                           chat_id bigint    not null,
                           name    varchar   not null
);

create table info(
                     name              varchar,
                     details bigint    varchar
);