# --- !Ups

CREATE TABLE category (
    id serial primary key,
    name character varying(200) NOT NULL
    );

CREATE TABLE subcategory(
    id serial primary key,
    category_id integer references category(id) on delete cascade,
    name character varying(200) NOT NULL
    );

insert into category (name) values ('Fruits');
insert into category (name) values ('Vegetables');
insert into category (name) values ('Hamburgers');

insert into subcategory (name, category_id) values ('Pear', 1);
insert into subcategory (name, category_id) values ('Cherry',1);
insert into subcategory (name, category_id) values ('Watermelon',1);
insert into subcategory (name, category_id) values ('Kangkong',2);
insert into subcategory (name, category_id) values ('Natto',2);
insert into subcategory (name, category_id) values ('Kumara',2);
insert into subcategory (name, category_id) values ('Bacon Cheese',3);
insert into subcategory (name, category_id) values ('Teriyaki',3);

# --- !Downs

DROP TABLE category;
DROP TABLE subcategory cascade;
