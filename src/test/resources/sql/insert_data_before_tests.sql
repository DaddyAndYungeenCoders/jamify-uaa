-- Insertion des users
INSERT INTO users (name, email, role, country, provider, provider_id, img_url, created_date)
VALUES ('Test User', 'test-user@example.com', 'ROLE_USER', 'FR', 'spotify', '123456', 'https://example.com/test-user.jpg', '2021-01-01 00:00:00');

-- Insertion des refresh tokens
INSERT INTO refresh_tokens (token, user_id, expiry_date, created_date)
VALUES ('refresh-token-1', 1, '2100-12-31 23:59:59', '2021-01-01 00:00:00');