-- noinspection SqlWithoutWhereForFile

-- Delete all data from all tables
DELETE FROM refresh_tokens;
DELETE FROM users;

-- Reset all sequences
ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE refresh_tokens_id_seq RESTART WITH 1;


