-- noinspection SqlWithoutWhereForFile

-- Delete all data from all tables
DELETE FROM refresh_tokens;

-- Reset all sequences
ALTER SEQUENCE refresh_tokens_id_seq RESTART WITH 1;
