-- Insertion des refresh tokens
-- valid refresh token
INSERT INTO refresh_tokens (token, user_email, expiry_date, created_date)
VALUES ('refresh-token-1', 'test-user@example.com', '2100-12-31 23:59:59', '2021-01-01 00:00:00');
-- expired refresh token
INSERT INTO refresh_tokens (token, user_email, expiry_date, created_date)
VALUES ('refresh-token-2', 'test-expired-user@example.com', '2021-12-31 23:59:59', '2021-01-01 00:00:00');