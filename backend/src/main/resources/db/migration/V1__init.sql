-- Esquema inicial del sistema de biblioteca

CREATE TABLE autores (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    nacionalidad VARCHAR(60),
    biografia TEXT
);

CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

CREATE TABLE libros (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    autor_id BIGINT NOT NULL REFERENCES autores(id),
    categoria_id BIGINT NOT NULL REFERENCES categorias(id),
    stock INTEGER NOT NULL DEFAULT 0,
    anio_publicacion INTEGER,
    editorial VARCHAR(120),
    portada_url VARCHAR(500)
);

CREATE INDEX idx_libros_titulo ON libros (titulo);

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMIN', 'BIBLIOTECARIO', 'ESTUDIANTE')),
    codigo_estudiante VARCHAR(20)
);

CREATE TABLE prestamos (
    id BIGSERIAL PRIMARY KEY,
    libro_id BIGINT NOT NULL REFERENCES libros(id),
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion_esperada DATE NOT NULL,
    fecha_devolucion_real DATE,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('ACTIVO', 'DEVUELTO', 'ATRASADO'))
);

CREATE INDEX idx_prestamos_usuario ON prestamos (usuario_id);

CREATE TABLE reservas (
    id BIGSERIAL PRIMARY KEY,
    libro_id BIGINT NOT NULL REFERENCES libros(id),
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    fecha_reserva DATE NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('PENDIENTE', 'ATENDIDA', 'CANCELADA'))
);

CREATE INDEX idx_reservas_usuario ON reservas (usuario_id);

CREATE TABLE multas (
    id BIGSERIAL PRIMARY KEY,
    prestamo_id BIGINT NOT NULL UNIQUE REFERENCES prestamos(id),
    monto NUMERIC(8, 2) NOT NULL,
    pagada BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_generacion DATE NOT NULL
);
