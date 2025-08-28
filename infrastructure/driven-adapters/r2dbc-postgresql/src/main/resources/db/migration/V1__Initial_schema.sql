-- ===========================================
-- MIGRACIÓN INICIAL PARA MICROSERVICIO SOLICITUDES
-- Versión: V1 - Solo estados requeridos para HU2
-- ===========================================

-- Crear extensiones necesarias (si no existen)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ===========================================
-- 1. TABLA DE ESTADOS (Solo 4 estados según EstadoSolicitud.java)
-- ===========================================
CREATE TABLE estados (
                         id_estado VARCHAR(50) PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL,
                         descripcion TEXT
);

-- Insertar solo los 4 estados del dominio
INSERT INTO estados (id_estado, nombre, descripcion) VALUES
                                                         ('PENDIENTE_REVISION', 'Pendiente de Revisión', 'Solicitud pendiente de revisión manual'),
                                                         ('EN_REVISION', 'En Revisión', 'Solicitud siendo revisada por un analista'),
                                                         ('APROBADA', 'Aprobada', 'Solicitud aprobada para desembolso'),
                                                         ('RECHAZADA', 'Rechazada', 'Solicitud rechazada');

-- ===========================================
-- 2. TABLA DE TIPOS DE PRÉSTAMO
-- ===========================================
CREATE TABLE tipo_prestamo (
                               id_tipo_prestamo VARCHAR(50) PRIMARY KEY,
                               nombre VARCHAR(100) NOT NULL,
                               monto_minimo DECIMAL(15,2) NOT NULL,
                               monto_maximo DECIMAL(15,2) NOT NULL,
                               tasa_interes DECIMAL(5,4) NOT NULL,
                               validacion_automatica BOOLEAN DEFAULT false
);

-- Insertar tipos de préstamo según criterios HU2
INSERT INTO tipo_prestamo (id_tipo_prestamo, nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica) VALUES
                                                                                                                          ('PERSONAL', 'Préstamo Personal', 500000.00, 50000000.00, 0.0180, true),
                                                                                                                          ('VEHICULO', 'Préstamo Vehículo', 10000000.00, 200000000.00, 0.0120, false),
                                                                                                                          ('HIPOTECARIO', 'Préstamo Hipotecario', 50000000.00, 1000000000.00, 0.0090, false),
                                                                                                                          ('LIBRE_INVERSION', 'Libre Inversión', 1000000.00, 30000000.00, 0.0220, true);

-- ===========================================
-- 3. TABLA PRINCIPAL DE SOLICITUDES
-- ===========================================
CREATE TABLE solicitudes (
                             id_solicitud VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::text,
                             numero_documento VARCHAR(20) NOT NULL,
                             nombres VARCHAR(100) NOT NULL,
                             apellidos VARCHAR(100) NOT NULL,
                             email VARCHAR(255) NOT NULL,
                             telefono VARCHAR(20),
                             monto_solicitado DECIMAL(15,2) NOT NULL,
                             plazo_meses INTEGER NOT NULL,
                             tipo_credito VARCHAR(50) NOT NULL,
                             ingresos_mensuales DECIMAL(15,2) NOT NULL,
                             gastos_mensuales DECIMAL(15,2),
                             estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE_REVISION',
                             observaciones TEXT,
                             fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                             fecha_actualizacion TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
                             CONSTRAINT fk_solicitud_estado FOREIGN KEY (estado) REFERENCES estados(id_estado),
                             CONSTRAINT fk_solicitud_tipo FOREIGN KEY (tipo_credito) REFERENCES tipo_prestamo(id_tipo_prestamo),
                             CONSTRAINT chk_monto_positivo CHECK (monto_solicitado > 0),
                             CONSTRAINT chk_ingresos_positivos CHECK (ingresos_mensuales > 0),
                             CONSTRAINT chk_plazo_valido CHECK (plazo_meses > 0 AND plazo_meses <= 360)
);

-- ===========================================
-- 4. ÍNDICES PARA OPTIMIZACIÓN
-- ===========================================

-- Índice por documento (consultas frecuentes)
CREATE INDEX idx_solicitudes_documento ON solicitudes(numero_documento);

-- Índice por estado (consultas frecuentes)
CREATE INDEX idx_solicitudes_estado ON solicitudes(estado);

-- Índice compuesto para verificación de solicitudes activas
CREATE INDEX idx_solicitudes_documento_estado ON solicitudes(numero_documento, estado);

-- Índice por fecha de creación
CREATE INDEX idx_solicitudes_fecha_creacion ON solicitudes(fecha_creacion DESC);

-- Índice por email (para consultas de cliente)
CREATE INDEX idx_solicitudes_email ON solicitudes(email);

-- ===========================================
-- 5. FUNCIÓN PARA ACTUALIZAR FECHA DE MODIFICACIÓN
-- ===========================================
CREATE OR REPLACE FUNCTION update_fecha_actualizacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger para actualizar automáticamente fecha_actualizacion
CREATE TRIGGER trigger_update_fecha_actualizacion
    BEFORE UPDATE ON solicitudes
    FOR EACH ROW
    EXECUTE FUNCTION update_fecha_actualizacion();