INSERT INTO todo(id, title, completed, ordering, url) VALUES (1, 'Introduction to Quarkus', true, 0, null);
INSERT INTO todo(id, title, completed, ordering, url) VALUES (2, 'The Reactive Powers of Quarkus', false, 1, null);
INSERT INTO todo(id, title, completed, ordering, url) VALUES (3, 'Hibernate Reactive with Panache', false, 1, null);
INSERT INTO todo(id, title, completed, ordering, url) VALUES (4, 'RESTEasy Reactive', false, 2, 'https://quarkus.io');
ALTER SEQUENCE todo_seq RESTART WITH 5;