drop database if exists railway;

create database railway;

use railway;

create table customer (
    name varchar(32) not null,
    address varchar(128) not null,
    email varchar(128) not null,

    primary key(name)
);

create table orders (
	order_id char(8) not null,
    delivery_id varchar(128) not null,
    name varchar(32) not null,
    address varchar(128) not null,
    email varchar(128) not null,
	status enum('pending', 'dispatched') default 'pending',


    primary key(order_id),
    constraint fk_name
        foreign key(name) references customer(name)
);

create table order_status (
    order_id char(8) not null,
    delivery_id varchar(128),
    status enum('pending', 'dispatched') default 'pending',
    status_update date not null,

    primary key(delivery_id),
    constraint fk_order_id
        foreign key(order_id) references orders(order_id)

);