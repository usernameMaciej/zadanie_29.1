INSERT INTO users(first_name, last_name, email, password)
VALUES
    -- admin@o2.pl / admin
    ('jan', 'kowalski', 'admin@o2.pl', '{bcrypt}$2a$10$NBBLA77KLW4jc5ApiOzm1eBMYXNOoR4kkxoVRApuhaFd6v1romxp.'),
    -- maciek@o2.pl / maciek
    ('maciej', 'polonski', 'maciek@o2.pl', '{bcrypt}$2a$10$0Lsh.Kj2LTLKmeTs0vcg5OgimAyBXqxouA6vdmoYe/GSi/2436QFG'),
    -- orbit@o2.pl / orbit
    ('orbit', 'orbitowski', 'orbit@o2.pl', '{bcrypt}$2a$10$IvHVYG6SfW2Z7kg3nI4Oo.N8hbXNPTgX7WUGttAsLSotffxDHopDK');

INSERT INTO user_role(name)
VALUES ('ADMIN'),
       ('USER');

INSERT INTO user_roles(user_id, role_id)
VALUES (1, 1),
       (2, 2),
       (3, 2);