create table gruppe (id serial primary key,
name varchar(300),
personen varchar(40)[],
ist_geschlossen boolean);

create table ausgabe (id serial primary key,
zahlende_person varchar(40), schuldner varchar(40)[],
betrag double precision, beschreibung varchar(500),
gruppe_dto int references gruppe(id));


