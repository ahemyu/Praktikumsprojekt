drop table if exists gruppe, ausgabe;
create table "gruppe" (id INT AUTO_INCREMENT PRIMARY KEY,
                     name VARCHAR(300),
                     personen VARCHAR(40)ARRAY,
                     ist_geschlossen BOOLEAN);

create table "ausgabe" (id INT AUTO_INCREMENT PRIMARY KEY,
                      zahlende_person VARCHAR(40), schuldner VARCHAR(40)ARRAY,
                      betrag DOUBLE PRECISION, beschreibung VARCHAR(500),
                      gruppe_dto INT REFERENCES "gruppe"(id));

