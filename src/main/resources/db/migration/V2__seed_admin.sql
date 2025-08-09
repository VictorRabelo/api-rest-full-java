INSERT INTO users (id, uuid, name, email, password, created_at, updated_at)
VALUES (1, '11111111-1111-1111-1111-111111111111', 'Admin', 'admin@local', '$2a$10$7eqJtq98hPqEX7fNZaFWoOsiZkfa9S9Zpoh0eX4jG/BCpsuUW7KLa', NOW(), NOW());
INSERT INTO user_roles (user_id, role) VALUES (1, 'ADMIN');
