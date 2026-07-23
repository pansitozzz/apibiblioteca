-- Datos de ejemplo. Todos los usuarios son ficticios (no corresponden a
-- personas reales de UNTELS). La contrasena en texto plano de los 3 usuarios
-- de ejemplo es "Password123" (hash BCrypt generado en build time).

INSERT INTO usuarios (nombre, apellido, email, password, rol, codigo_estudiante) VALUES
('Ana', 'Torres', 'admin@biblioteca.demo', '$2a$10$rl1dni3MMjbeOnDjAxh84u1vpBx6f6RXvkyNeA6oSCe793qui0Ll6', 'ADMIN', NULL),
('Luis', 'Ramirez', 'bibliotecario@biblioteca.demo', '$2a$10$rl1dni3MMjbeOnDjAxh84u1vpBx6f6RXvkyNeA6oSCe793qui0Ll6', 'BIBLIOTECARIO', NULL),
('Maria', 'Quispe', 'estudiante@biblioteca.demo', '$2a$10$rl1dni3MMjbeOnDjAxh84u1vpBx6f6RXvkyNeA6oSCe793qui0Ll6', 'ESTUDIANTE', '2021100123');

INSERT INTO autores (nombre, apellido, nacionalidad, biografia) VALUES
('Gabriel', 'Garcia Marquez', 'Colombiana', 'Escritor y periodista, premio Nobel de Literatura en 1982.'),
('Isabel', 'Allende', 'Chilena', 'Novelista reconocida por sus obras dentro del realismo magico.'),
('George', 'Orwell', 'Britanica', 'Autor y periodista conocido por sus criticas al totalitarismo.'),
('Mario', 'Vargas Llosa', 'Peruana', 'Escritor peruano, premio Nobel de Literatura en 2010.'),
('Robert', 'Martin', 'Estadounidense', 'Ingeniero de software conocido por sus obras sobre buenas practicas.');

INSERT INTO categorias (nombre, descripcion) VALUES
('Novela', 'Obras de ficcion narrativa extensa'),
('Ciencia Ficcion', 'Narrativa especulativa basada en ciencia y tecnologia'),
('Tecnologia', 'Libros tecnicos de ingenieria y computacion'),
('Historia', 'Obras sobre hechos y procesos historicos'),
('Poesia', 'Obras liricas y poemarios');

INSERT INTO libros (titulo, isbn, autor_id, categoria_id, stock, anio_publicacion, editorial, portada_url) VALUES
('Cien anios de soledad', '9780307474728', 1, 1, 3, 1967, 'Sudamericana', NULL),
('La casa de los espiritus', '9788401341836', 2, 1, 2, 1982, 'Plaza & Janes', NULL),
('1984', '9780451524935', 3, 2, 0, 1949, 'Secker & Warburg', NULL),
('La ciudad y los perros', '9788420471839', 4, 1, 4, 1963, 'Seix Barral', NULL),
('Clean Code', '9780132350884', 5, 3, 5, 2008, 'Prentice Hall', NULL);
