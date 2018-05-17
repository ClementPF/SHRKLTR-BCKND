To create DB

install mysql

- Create sharkulator DB

1. mysql.server start
2. create database sharkulator;

- Then to import tables from file
mysql -u root -p sharkulator < /Users/clementperez/Code/BackEnd/sharkulator/target/classes/sharkulator_schema_2.sql
