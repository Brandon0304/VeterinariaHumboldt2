--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

-- Started on 2025-11-30 18:30:38

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2 (class 3079 OID 34878)
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- TOC entry 5583 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- TOC entry 344 (class 1255 OID 43496)
-- Name: actualizar_estado_factura_after_pago(); Type: FUNCTION; Schema: public; Owner: vet_admin
--

CREATE FUNCTION public.actualizar_estado_factura_after_pago() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NEW.factura_id IS NOT NULL AND NEW.tipo = 'INGRESO' AND NEW.estado = 'COMPLETADA' THEN
        UPDATE facturas
        SET 
            monto_pagado = monto_pagado + NEW.monto,
            saldo_pendiente = total - (monto_pagado + NEW.monto),
            estado = CASE
                WHEN (monto_pagado + NEW.monto) >= total THEN 'PAGADA'
                WHEN (monto_pagado + NEW.monto) > 0 THEN 'PARCIAL'
                ELSE estado
            END,
            updated_at = CURRENT_TIMESTAMP
        WHERE id_factura = NEW.factura_id;
    END IF;
    
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.actualizar_estado_factura_after_pago() OWNER TO vet_admin;

--
-- TOC entry 342 (class 1255 OID 43492)
-- Name: update_facturas_timestamp(); Type: FUNCTION; Schema: public; Owner: vet_admin
--

CREATE FUNCTION public.update_facturas_timestamp() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.update_facturas_timestamp() OWNER TO vet_admin;

--
-- TOC entry 343 (class 1255 OID 43494)
-- Name: update_transacciones_timestamp(); Type: FUNCTION; Schema: public; Owner: vet_admin
--

CREATE FUNCTION public.update_transacciones_timestamp() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.update_transacciones_timestamp() OWNER TO vet_admin;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 251 (class 1259 OID 34613)
-- Name: alertas_inventario; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.alertas_inventario (
    id_alerta bigint NOT NULL,
    producto_id bigint NOT NULL,
    nivel_stock integer NOT NULL,
    fecha_generada timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    mensaje text,
    created_by character varying(100),
    created_at timestamp without time zone,
    updated_by character varying(100),
    updated_at timestamp without time zone
);


ALTER TABLE public.alertas_inventario OWNER TO vet_admin;

--
-- TOC entry 250 (class 1259 OID 34612)
-- Name: alertas_inventario_id_alerta_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.alertas_inventario_id_alerta_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.alertas_inventario_id_alerta_seq OWNER TO vet_admin;

--
-- TOC entry 5584 (class 0 OID 0)
-- Dependencies: 250
-- Name: alertas_inventario_id_alerta_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.alertas_inventario_id_alerta_seq OWNED BY public.alertas_inventario.id_alerta;


--
-- TOC entry 297 (class 1259 OID 43324)
-- Name: auditoria_detallada; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.auditoria_detallada (
    id bigint NOT NULL,
    historial_accion_id bigint,
    usuario_id bigint NOT NULL,
    rol_nombre character varying(50) NOT NULL,
    modulo character varying(100) NOT NULL,
    entidad character varying(100),
    entidad_id bigint,
    datos_anteriores jsonb,
    datos_nuevos jsonb,
    relevancia character varying(20) DEFAULT 'NORMAL'::character varying,
    requiere_revision boolean DEFAULT false,
    ip_address character varying(45),
    user_agent text,
    fecha_accion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_relevancia CHECK (((relevancia)::text = ANY ((ARRAY['ALTA'::character varying, 'NORMAL'::character varying, 'BAJA'::character varying])::text[])))
);


ALTER TABLE public.auditoria_detallada OWNER TO vet_admin;

--
-- TOC entry 296 (class 1259 OID 43323)
-- Name: auditoria_detallada_id_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.auditoria_detallada_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.auditoria_detallada_id_seq OWNER TO vet_admin;

--
-- TOC entry 5585 (class 0 OID 0)
-- Dependencies: 296
-- Name: auditoria_detallada_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.auditoria_detallada_id_seq OWNED BY public.auditoria_detallada.id;


--
-- TOC entry 275 (class 1259 OID 34804)
-- Name: backups_sistema; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.backups_sistema (
    id_backup bigint NOT NULL,
    fecha_creacion timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ruta_archivo character varying(500) NOT NULL,
    metadata jsonb
);


ALTER TABLE public.backups_sistema OWNER TO vet_admin;

--
-- TOC entry 274 (class 1259 OID 34803)
-- Name: backups_sistema_id_backup_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.backups_sistema_id_backup_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.backups_sistema_id_backup_seq OWNER TO vet_admin;

--
-- TOC entry 5586 (class 0 OID 0)
-- Dependencies: 274
-- Name: backups_sistema_id_backup_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.backups_sistema_id_backup_seq OWNED BY public.backups_sistema.id_backup;


--
-- TOC entry 271 (class 1259 OID 34782)
-- Name: canales_app; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.canales_app (
    id_canal bigint NOT NULL,
    app_topic character varying(150)
);


ALTER TABLE public.canales_app OWNER TO vet_admin;

--
-- TOC entry 269 (class 1259 OID 34762)
-- Name: canales_email; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.canales_email (
    id_canal bigint NOT NULL,
    smtp_server character varying(150),
    from_address character varying(150)
);


ALTER TABLE public.canales_email OWNER TO vet_admin;

--
-- TOC entry 261 (class 1259 OID 34703)
-- Name: canales_envio; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.canales_envio (
    id_canal bigint NOT NULL,
    nombre character varying(50) NOT NULL,
    configuracion jsonb
);


ALTER TABLE public.canales_envio OWNER TO vet_admin;

--
-- TOC entry 260 (class 1259 OID 34702)
-- Name: canales_envio_id_canal_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.canales_envio_id_canal_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.canales_envio_id_canal_seq OWNER TO vet_admin;

--
-- TOC entry 5587 (class 0 OID 0)
-- Dependencies: 260
-- Name: canales_envio_id_canal_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.canales_envio_id_canal_seq OWNED BY public.canales_envio.id_canal;


--
-- TOC entry 270 (class 1259 OID 34772)
-- Name: canales_sms; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.canales_sms (
    id_canal bigint NOT NULL,
    proveedor_api character varying(150)
);


ALTER TABLE public.canales_sms OWNER TO vet_admin;

--
-- TOC entry 255 (class 1259 OID 34640)
-- Name: citas; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.citas (
    id_cita bigint NOT NULL,
    paciente_id bigint NOT NULL,
    veterinario_id bigint NOT NULL,
    fecha_hora timestamp with time zone NOT NULL,
    tipo_servicio character varying(50),
    estado character varying(30) DEFAULT 'PROGRAMADA'::character varying NOT NULL,
    motivo text,
    triage_nivel character varying(30),
    created_by character varying(100),
    created_at timestamp without time zone,
    updated_by character varying(100),
    updated_at timestamp without time zone,
    CONSTRAINT citas_estado_check CHECK (((estado)::text = ANY ((ARRAY['PROGRAMADA'::character varying, 'REALIZADA'::character varying, 'CANCELADA'::character varying])::text[])))
);


ALTER TABLE public.citas OWNER TO vet_admin;

--
-- TOC entry 254 (class 1259 OID 34639)
-- Name: citas_id_cita_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.citas_id_cita_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.citas_id_cita_seq OWNER TO vet_admin;

--
-- TOC entry 5588 (class 0 OID 0)
-- Dependencies: 254
-- Name: citas_id_cita_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.citas_id_cita_seq OWNED BY public.citas.id_cita;


--
-- TOC entry 227 (class 1259 OID 34419)
-- Name: clientes; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.clientes (
    id_usuario bigint NOT NULL,
    fecha_registro timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    documento_identidad character varying(50)
);


ALTER TABLE public.clientes OWNER TO vet_admin;

--
-- TOC entry 301 (class 1259 OID 43371)
-- Name: configuracion_avanzada; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.configuracion_avanzada (
    id bigint NOT NULL,
    clave character varying(100) NOT NULL,
    valor text NOT NULL,
    categoria character varying(50) NOT NULL,
    tipo_dato character varying(20) NOT NULL,
    descripcion text,
    valor_por_defecto text,
    requerido boolean DEFAULT false,
    editable boolean DEFAULT true,
    creado_por character varying(255),
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    modificado_por character varying(255),
    fecha_modificacion timestamp without time zone,
    CONSTRAINT chk_tipo_dato CHECK (((tipo_dato)::text = ANY ((ARRAY['STRING'::character varying, 'INTEGER'::character varying, 'BOOLEAN'::character varying, 'JSON'::character varying, 'DECIMAL'::character varying])::text[])))
);


ALTER TABLE public.configuracion_avanzada OWNER TO vet_admin;

--
-- TOC entry 300 (class 1259 OID 43370)
-- Name: configuracion_avanzada_id_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.configuracion_avanzada_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.configuracion_avanzada_id_seq OWNER TO vet_admin;

--
-- TOC entry 5589 (class 0 OID 0)
-- Dependencies: 300
-- Name: configuracion_avanzada_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.configuracion_avanzada_id_seq OWNED BY public.configuracion_avanzada.id;


--
-- TOC entry 241 (class 1259 OID 34534)
-- Name: desparasitaciones; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.desparasitaciones (
    id_desparasitacion bigint NOT NULL,
    paciente_id bigint NOT NULL,
    producto_usado character varying(150) NOT NULL,
    fecha_aplicacion date NOT NULL,
    proxima_aplicacion date,
    created_by character varying(100),
    created_at timestamp without time zone,
    updated_by character varying(100),
    updated_at timestamp without time zone
);


ALTER TABLE public.desparasitaciones OWNER TO vet_admin;

--
-- TOC entry 240 (class 1259 OID 34533)
-- Name: desparasitaciones_id_desparasitacion_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.desparasitaciones_id_desparasitacion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.desparasitaciones_id_desparasitacion_seq OWNER TO vet_admin;

--
-- TOC entry 5590 (class 0 OID 0)
-- Dependencies: 240
-- Name: desparasitaciones_id_desparasitacion_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.desparasitaciones_id_desparasitacion_seq OWNED BY public.desparasitaciones.id_desparasitacion;


--
-- TOC entry 267 (class 1259 OID 34741)
-- Name: destinatarios; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.destinatarios (
    id_destinatario bigint NOT NULL,
    tipo_destinatario character varying(30) NOT NULL,
    referencia_id bigint NOT NULL
);


ALTER TABLE public.destinatarios OWNER TO vet_admin;

--
-- TOC entry 266 (class 1259 OID 34740)
-- Name: destinatarios_id_destinatario_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.destinatarios_id_destinatario_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.destinatarios_id_destinatario_seq OWNER TO vet_admin;

--
-- TOC entry 5591 (class 0 OID 0)
-- Dependencies: 266
-- Name: destinatarios_id_destinatario_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.destinatarios_id_destinatario_seq OWNED BY public.destinatarios.id_destinatario;


--
-- TOC entry 303 (class 1259 OID 43432)
-- Name: detalle_factura; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.detalle_factura (
    id_detalle bigint NOT NULL,
    factura_id bigint NOT NULL,
    concepto character varying(200) NOT NULL,
    descripcion text,
    cantidad integer DEFAULT 1 NOT NULL,
    precio_unitario numeric(12,2) NOT NULL,
    subtotal numeric(12,2) NOT NULL,
    producto_id bigint,
    metadatos jsonb,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT detalle_factura_cantidad_check CHECK ((cantidad > 0)),
    CONSTRAINT detalle_factura_precio_unitario_check CHECK ((precio_unitario >= (0)::numeric)),
    CONSTRAINT detalle_factura_subtotal_check CHECK ((subtotal >= (0)::numeric))
);


ALTER TABLE public.detalle_factura OWNER TO vet_admin;

--
-- TOC entry 5592 (class 0 OID 0)
-- Dependencies: 303
-- Name: TABLE detalle_factura; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON TABLE public.detalle_factura IS 'Desglose detallado de conceptos facturados';


--
-- TOC entry 5593 (class 0 OID 0)
-- Dependencies: 303
-- Name: COLUMN detalle_factura.concepto; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.detalle_factura.concepto IS 'Nombre del servicio o producto facturado';


--
-- TOC entry 302 (class 1259 OID 43431)
-- Name: detalle_factura_id_detalle_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.detalle_factura_id_detalle_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.detalle_factura_id_detalle_seq OWNER TO vet_admin;

--
-- TOC entry 5594 (class 0 OID 0)
-- Dependencies: 302
-- Name: detalle_factura_id_detalle_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.detalle_factura_id_detalle_seq OWNED BY public.detalle_factura.id_detalle;


--
-- TOC entry 281 (class 1259 OID 34844)
-- Name: estadisticas; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.estadisticas (
    id_estadistica bigint NOT NULL,
    nombre character varying(120) NOT NULL,
    valor numeric(18,4),
    periodo_inicio date,
    periodo_fin date
);


ALTER TABLE public.estadisticas OWNER TO vet_admin;

--
-- TOC entry 280 (class 1259 OID 34843)
-- Name: estadisticas_id_estadistica_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.estadisticas_id_estadistica_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.estadisticas_id_estadistica_seq OWNER TO vet_admin;

--
-- TOC entry 5595 (class 0 OID 0)
-- Dependencies: 280
-- Name: estadisticas_id_estadistica_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.estadisticas_id_estadistica_seq OWNED BY public.estadisticas.id_estadistica;


--
-- TOC entry 259 (class 1259 OID 34683)
-- Name: facturas; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.facturas (
    id_factura bigint NOT NULL,
    numero_factura character varying(50) NOT NULL,
    fecha_emision timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    total numeric(14,2) NOT NULL,
    estado character varying(20) DEFAULT 'PENDIENTE'::character varying NOT NULL,
    cliente_id bigint NOT NULL,
    contenido jsonb,
    created_by character varying(100),
    created_at timestamp without time zone,
    updated_by character varying(100),
    updated_at timestamp without time zone,
    fecha_pago timestamp without time zone,
    fecha_vencimiento timestamp without time zone,
    servicio_prestado_id bigint,
    paciente_id bigint,
    subtotal numeric(12,2) DEFAULT 0,
    descuento numeric(12,2) DEFAULT 0,
    iva_porcentaje numeric(5,2) DEFAULT 19,
    iva_monto numeric(12,2) DEFAULT 0,
    monto_pagado numeric(12,2) DEFAULT 0,
    saldo_pendiente numeric(12,2) DEFAULT 0,
    notas text,
    metadatos jsonb,
    CONSTRAINT facturas_descuento_check CHECK ((descuento >= (0)::numeric)),
    CONSTRAINT facturas_estado_check CHECK (((estado)::text = ANY ((ARRAY['PENDIENTE'::character varying, 'PAGADA'::character varying, 'VENCIDA'::character varying, 'CANCELADA'::character varying, 'PARCIAL'::character varying, 'ANULADA'::character varying])::text[]))),
    CONSTRAINT facturas_iva_monto_check CHECK ((iva_monto >= (0)::numeric)),
    CONSTRAINT facturas_iva_porcentaje_check CHECK ((iva_porcentaje >= (0)::numeric)),
    CONSTRAINT facturas_monto_pagado_check CHECK ((monto_pagado >= (0)::numeric)),
    CONSTRAINT facturas_saldo_pendiente_check CHECK ((saldo_pendiente >= (0)::numeric)),
    CONSTRAINT facturas_subtotal_check CHECK ((subtotal >= (0)::numeric)),
    CONSTRAINT facturas_total_check CHECK ((total >= (0)::numeric))
);


ALTER TABLE public.facturas OWNER TO vet_admin;

--
-- TOC entry 5596 (class 0 OID 0)
-- Dependencies: 259
-- Name: TABLE facturas; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON TABLE public.facturas IS 'Facturas emitidas por servicios veterinarios';


--
-- TOC entry 5597 (class 0 OID 0)
-- Dependencies: 259
-- Name: COLUMN facturas.numero_factura; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.facturas.numero_factura IS 'Número único de factura con formato FV-YYYY-NNNN';


--
-- TOC entry 5598 (class 0 OID 0)
-- Dependencies: 259
-- Name: COLUMN facturas.estado; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.facturas.estado IS 'Estado del pago: PENDIENTE, PAGADA, VENCIDA, CANCELADA, PARCIAL, ANULADA';


--
-- TOC entry 5599 (class 0 OID 0)
-- Dependencies: 259
-- Name: COLUMN facturas.fecha_pago; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.facturas.fecha_pago IS 'Fecha y hora en que se registró el pago de la factura';


--
-- TOC entry 5600 (class 0 OID 0)
-- Dependencies: 259
-- Name: COLUMN facturas.metadatos; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.facturas.metadatos IS 'Información adicional en formato JSON';


--
-- TOC entry 258 (class 1259 OID 34682)
-- Name: facturas_id_factura_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.facturas_id_factura_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.facturas_id_factura_seq OWNER TO vet_admin;

--
-- TOC entry 5601 (class 0 OID 0)
-- Dependencies: 258
-- Name: facturas_id_factura_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.facturas_id_factura_seq OWNED BY public.facturas.id_factura;


--
-- TOC entry 218 (class 1259 OID 34348)
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO vet_admin;

--
-- TOC entry 285 (class 1259 OID 34864)
-- Name: fuentes_datos; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.fuentes_datos (
    id_fuente bigint NOT NULL,
    nombre character varying(120) NOT NULL,
    tipo character varying(50),
    configuracion jsonb
);


ALTER TABLE public.fuentes_datos OWNER TO vet_admin;

--
-- TOC entry 284 (class 1259 OID 34863)
-- Name: fuentes_datos_id_fuente_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.fuentes_datos_id_fuente_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.fuentes_datos_id_fuente_seq OWNER TO vet_admin;

--
-- TOC entry 5602 (class 0 OID 0)
-- Dependencies: 284
-- Name: fuentes_datos_id_fuente_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.fuentes_datos_id_fuente_seq OWNED BY public.fuentes_datos.id_fuente;


--
-- TOC entry 231 (class 1259 OID 34453)
-- Name: historial_acciones; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.historial_acciones (
    id_accion bigint NOT NULL,
    usuario_id bigint NOT NULL,
    fecha_hora timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    descripcion text NOT NULL,
    metadata jsonb,
    tipo_accion character varying(100) NOT NULL,
    ip_address character varying(45)
);


ALTER TABLE public.historial_acciones OWNER TO vet_admin;

--
-- TOC entry 230 (class 1259 OID 34452)
-- Name: historial_acciones_id_accion_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.historial_acciones_id_accion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.historial_acciones_id_accion_seq OWNER TO vet_admin;

--
-- TOC entry 5603 (class 0 OID 0)
-- Dependencies: 230
-- Name: historial_acciones_id_accion_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.historial_acciones_id_accion_seq OWNED BY public.historial_acciones.id_accion;


--
-- TOC entry 235 (class 1259 OID 34482)
-- Name: historias_clinicas; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.historias_clinicas (
    id_historia bigint NOT NULL,
    paciente_id bigint NOT NULL,
    fecha_apertura timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    resumen text,
    metadatos jsonb,
    created_by character varying(100),
    created_at timestamp without time zone,
    updated_by character varying(100),
    updated_at timestamp without time zone
);


ALTER TABLE public.historias_clinicas OWNER TO vet_admin;

--
-- TOC entry 234 (class 1259 OID 34481)
-- Name: historias_clinicas_id_historia_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.historias_clinicas_id_historia_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.historias_clinicas_id_historia_seq OWNER TO vet_admin;

--
-- TOC entry 5604 (class 0 OID 0)
-- Dependencies: 234
-- Name: historias_clinicas_id_historia_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.historias_clinicas_id_historia_seq OWNED BY public.historias_clinicas.id_historia;


--
-- TOC entry 295 (class 1259 OID 43306)
-- Name: horarios_atencion; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.horarios_atencion (
    id bigint NOT NULL,
    dia_semana integer NOT NULL,
    hora_apertura time without time zone NOT NULL,
    hora_cierre time without time zone NOT NULL,
    abierto boolean DEFAULT true,
    descripcion character varying(255),
    creado_por character varying(255),
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    modificado_por character varying(255),
    fecha_modificacion timestamp without time zone,
    activo boolean DEFAULT true,
    CONSTRAINT chk_horarios_validos CHECK ((hora_cierre > hora_apertura)),
    CONSTRAINT horarios_atencion_dia_semana_check CHECK (((dia_semana >= 1) AND (dia_semana <= 7)))
);


ALTER TABLE public.horarios_atencion OWNER TO vet_admin;

--
-- TOC entry 294 (class 1259 OID 43305)
-- Name: horarios_atencion_id_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.horarios_atencion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.horarios_atencion_id_seq OWNER TO vet_admin;

--
-- TOC entry 5605 (class 0 OID 0)
-- Dependencies: 294
-- Name: horarios_atencion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.horarios_atencion_id_seq OWNED BY public.horarios_atencion.id;


--
-- TOC entry 283 (class 1259 OID 34853)
-- Name: indicadores; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.indicadores (
    id_indicador bigint NOT NULL,
    nombre character varying(120) NOT NULL,
    descripcion text,
    valor_actual numeric(18,4)
);


ALTER TABLE public.indicadores OWNER TO vet_admin;

--
-- TOC entry 282 (class 1259 OID 34852)
-- Name: indicadores_id_indicador_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.indicadores_id_indicador_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.indicadores_id_indicador_seq OWNER TO vet_admin;

--
-- TOC entry 5606 (class 0 OID 0)
-- Dependencies: 282
-- Name: indicadores_id_indicador_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.indicadores_id_indicador_seq OWNED BY public.indicadores.id_indicador;


--
-- TOC entry 289 (class 1259 OID 43243)
-- Name: informacion_clinica; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.informacion_clinica (
    id bigint NOT NULL,
    nombre_clinica character varying(255) NOT NULL,
    nit character varying(50) NOT NULL,
    telefono character varying(20),
    email character varying(255),
    direccion character varying(500),
    idioma character varying(10) DEFAULT 'es'::character varying,
    moneda character varying(10) DEFAULT 'COP'::character varying,
    zona_horaria character varying(50) DEFAULT 'America/Bogota'::character varying,
    formato_fecha character varying(20) DEFAULT 'DD/MM/YYYY'::character varying,
    logo_url character varying(500),
    creado_por character varying(255),
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    modificado_por character varying(255),
    fecha_modificacion timestamp without time zone,
    activo boolean DEFAULT true
);


ALTER TABLE public.informacion_clinica OWNER TO vet_admin;

--
-- TOC entry 288 (class 1259 OID 43242)
-- Name: informacion_clinica_id_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.informacion_clinica_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.informacion_clinica_id_seq OWNER TO vet_admin;

--
-- TOC entry 5607 (class 0 OID 0)
-- Dependencies: 288
-- Name: informacion_clinica_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.informacion_clinica_id_seq OWNED BY public.informacion_clinica.id;


--
-- TOC entry 277 (class 1259 OID 34814)
-- Name: logs_sistema; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.logs_sistema (
    id_log bigint NOT NULL,
    fecha_hora timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    nivel character varying(20) NOT NULL,
    componente character varying(100),
    mensaje text NOT NULL,
    metadata jsonb
);


ALTER TABLE public.logs_sistema OWNER TO vet_admin;

--
-- TOC entry 276 (class 1259 OID 34813)
-- Name: logs_sistema_id_log_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.logs_sistema_id_log_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.logs_sistema_id_log_seq OWNER TO vet_admin;

--
-- TOC entry 5608 (class 0 OID 0)
-- Dependencies: 276
-- Name: logs_sistema_id_log_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.logs_sistema_id_log_seq OWNED BY public.logs_sistema.id_log;


--
-- TOC entry 247 (class 1259 OID 34573)
-- Name: lotes; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.lotes (
    id_lote bigint NOT NULL,
    producto_id bigint NOT NULL,
    fecha_vencimiento date NOT NULL,
    cantidad integer NOT NULL,
    numero_lote character varying(100),
    CONSTRAINT lotes_cantidad_check CHECK ((cantidad > 0)),
    CONSTRAINT lotes_fecha_vencimiento_check CHECK ((fecha_vencimiento >= CURRENT_DATE))
);


ALTER TABLE public.lotes OWNER TO vet_admin;

--
-- TOC entry 246 (class 1259 OID 34572)
-- Name: lotes_id_lote_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.lotes_id_lote_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.lotes_id_lote_seq OWNER TO vet_admin;

--
-- TOC entry 5609 (class 0 OID 0)
-- Dependencies: 246
-- Name: lotes_id_lote_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.lotes_id_lote_seq OWNED BY public.lotes.id_lote;


--
-- TOC entry 249 (class 1259 OID 34589)
-- Name: movimientos_inventario; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.movimientos_inventario (
    id_movimiento bigint NOT NULL,
    producto_id bigint NOT NULL,
    tipo_movimiento character varying(20) NOT NULL,
    cantidad integer NOT NULL,
    fecha timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    proveedor_id bigint,
    referencia character varying(100),
    usuario_id bigint,
    created_by character varying(100),
    created_at timestamp without time zone,
    updated_by character varying(100),
    updated_at timestamp without time zone,
    CONSTRAINT movimientos_inventario_tipo_movimiento_check CHECK (((tipo_movimiento)::text = ANY ((ARRAY['IN'::character varying, 'OUT'::character varying, 'AJUSTE'::character varying])::text[])))
);


ALTER TABLE public.movimientos_inventario OWNER TO vet_admin;

--
-- TOC entry 248 (class 1259 OID 34588)
-- Name: movimientos_inventario_id_movimiento_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.movimientos_inventario_id_movimiento_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.movimientos_inventario_id_movimiento_seq OWNER TO vet_admin;

--
-- TOC entry 5610 (class 0 OID 0)
-- Dependencies: 248
-- Name: movimientos_inventario_id_movimiento_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.movimientos_inventario_id_movimiento_seq OWNED BY public.movimientos_inventario.id_movimiento;


--
-- TOC entry 268 (class 1259 OID 34747)
-- Name: notificacion_destinatarios; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.notificacion_destinatarios (
    notificacion_id bigint NOT NULL,
    destinatario_id bigint NOT NULL
);


ALTER TABLE public.notificacion_destinatarios OWNER TO vet_admin;

--
-- TOC entry 265 (class 1259 OID 34725)
-- Name: notificaciones; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.notificaciones (
    id_notificacion bigint NOT NULL,
    tipo character varying(50) NOT NULL,
    mensaje text NOT NULL,
    fecha_envio_programada timestamp with time zone,
    fecha_envio_real timestamp with time zone,
    estado character varying(30) DEFAULT 'PENDIENTE'::character varying NOT NULL,
    plantilla_id bigint,
    datos jsonb,
    created_by character varying(100),
    created_at timestamp without time zone,
    updated_by character varying(100),
    updated_at timestamp without time zone,
    CONSTRAINT notificaciones_estado_check CHECK (((estado)::text = ANY ((ARRAY['PENDIENTE'::character varying, 'ENVIADA'::character varying, 'FALLIDA'::character varying])::text[])))
);


ALTER TABLE public.notificaciones OWNER TO vet_admin;

--
-- TOC entry 264 (class 1259 OID 34724)
-- Name: notificaciones_id_notificacion_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.notificaciones_id_notificacion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.notificaciones_id_notificacion_seq OWNER TO vet_admin;

--
-- TOC entry 5611 (class 0 OID 0)
-- Dependencies: 264
-- Name: notificaciones_id_notificacion_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.notificaciones_id_notificacion_seq OWNED BY public.notificaciones.id_notificacion;


--
-- TOC entry 233 (class 1259 OID 34468)
-- Name: pacientes; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.pacientes (
    id_paciente bigint NOT NULL,
    nombre character varying(100) NOT NULL,
    especie character varying(30) NOT NULL,
    raza character varying(80),
    fecha_nacimiento date,
    sexo character varying(10),
    peso_kg numeric(5,2),
    estado_salud character varying(100),
    cliente_id bigint NOT NULL,
    identificador_externo uuid NOT NULL,
    created_by character varying(100),
    created_at timestamp without time zone,
    updated_by character varying(100),
    updated_at timestamp without time zone,
    CONSTRAINT pacientes_especie_check CHECK (((especie)::text = ANY ((ARRAY['perro'::character varying, 'gato'::character varying])::text[]))),
    CONSTRAINT pacientes_peso_kg_check CHECK ((peso_kg > (0)::numeric))
);


ALTER TABLE public.pacientes OWNER TO vet_admin;

--
-- TOC entry 5612 (class 0 OID 0)
-- Dependencies: 233
-- Name: COLUMN pacientes.identificador_externo; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.pacientes.identificador_externo IS 'Identificador único UUID del paciente. Utilizado para evitar duplicidad y facilitar trazabilidad.';


--
-- TOC entry 232 (class 1259 OID 34467)
-- Name: pacientes_id_paciente_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.pacientes_id_paciente_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pacientes_id_paciente_seq OWNER TO vet_admin;

--
-- TOC entry 5613 (class 0 OID 0)
-- Dependencies: 232
-- Name: pacientes_id_paciente_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.pacientes_id_paciente_seq OWNED BY public.pacientes.id_paciente;


--
-- TOC entry 273 (class 1259 OID 34793)
-- Name: parametros_sistema; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.parametros_sistema (
    id_parametro bigint NOT NULL,
    clave character varying(150) NOT NULL,
    valor character varying(500) NOT NULL,
    descripcion text,
    aplicacion character varying(50)
);


ALTER TABLE public.parametros_sistema OWNER TO vet_admin;

--
-- TOC entry 272 (class 1259 OID 34792)
-- Name: parametros_sistema_id_parametro_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.parametros_sistema_id_parametro_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.parametros_sistema_id_parametro_seq OWNER TO vet_admin;

--
-- TOC entry 5614 (class 0 OID 0)
-- Dependencies: 272
-- Name: parametros_sistema_id_parametro_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.parametros_sistema_id_parametro_seq OWNED BY public.parametros_sistema.id_parametro;


--
-- TOC entry 224 (class 1259 OID 34378)
-- Name: permisos; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.permisos (
    id_permiso bigint NOT NULL,
    nombre character varying(100) NOT NULL,
    descripcion character varying(255)
);


ALTER TABLE public.permisos OWNER TO vet_admin;

--
-- TOC entry 223 (class 1259 OID 34377)
-- Name: permisos_id_permiso_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.permisos_id_permiso_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.permisos_id_permiso_seq OWNER TO vet_admin;

--
-- TOC entry 5615 (class 0 OID 0)
-- Dependencies: 223
-- Name: permisos_id_permiso_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.permisos_id_permiso_seq OWNED BY public.permisos.id_permiso;


--
-- TOC entry 291 (class 1259 OID 43261)
-- Name: permisos_rol; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.permisos_rol (
    id bigint NOT NULL,
    rol_id bigint NOT NULL,
    modulo character varying(100) NOT NULL,
    accion character varying(100) NOT NULL,
    ruta character varying(255),
    descripcion text,
    permitido boolean DEFAULT true,
    creado_por character varying(255),
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    modificado_por character varying(255),
    fecha_modificacion timestamp without time zone,
    activo boolean DEFAULT true
);


ALTER TABLE public.permisos_rol OWNER TO vet_admin;

--
-- TOC entry 290 (class 1259 OID 43260)
-- Name: permisos_rol_id_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.permisos_rol_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.permisos_rol_id_seq OWNER TO vet_admin;

--
-- TOC entry 5616 (class 0 OID 0)
-- Dependencies: 290
-- Name: permisos_rol_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.permisos_rol_id_seq OWNED BY public.permisos_rol.id;


--
-- TOC entry 220 (class 1259 OID 34358)
-- Name: personas; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.personas (
    id_persona bigint NOT NULL,
    nombre character varying(100) NOT NULL,
    apellido character varying(100) NOT NULL,
    correo character varying(150) NOT NULL,
    telefono character varying(30),
    direccion character varying(255),
    created_by character varying(100),
    created_at timestamp without time zone,
    updated_by character varying(100),
    updated_at timestamp without time zone
);


ALTER TABLE public.personas OWNER TO vet_admin;

--
-- TOC entry 5617 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN personas.created_by; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.personas.created_by IS 'Usuario que creó el registro';


--
-- TOC entry 5618 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN personas.created_at; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.personas.created_at IS 'Fecha y hora de creación del registro';


--
-- TOC entry 5619 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN personas.updated_by; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.personas.updated_by IS 'Usuario que modificó el registro por última vez';


--
-- TOC entry 5620 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN personas.updated_at; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.personas.updated_at IS 'Fecha y hora de la última modificación';


--
-- TOC entry 219 (class 1259 OID 34357)
-- Name: personas_id_persona_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.personas_id_persona_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.personas_id_persona_seq OWNER TO vet_admin;

--
-- TOC entry 5621 (class 0 OID 0)
-- Dependencies: 219
-- Name: personas_id_persona_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.personas_id_persona_seq OWNED BY public.personas.id_persona;


--
-- TOC entry 263 (class 1259 OID 34714)
-- Name: plantillas_mensajes; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.plantillas_mensajes (
    id_plantilla bigint NOT NULL,
    nombre character varying(100) NOT NULL,
    asunto character varying(150),
    cuerpo text NOT NULL,
    variables jsonb
);


ALTER TABLE public.plantillas_mensajes OWNER TO vet_admin;

--
-- TOC entry 262 (class 1259 OID 34713)
-- Name: plantillas_mensajes_id_plantilla_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.plantillas_mensajes_id_plantilla_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.plantillas_mensajes_id_plantilla_seq OWNER TO vet_admin;

--
-- TOC entry 5622 (class 0 OID 0)
-- Dependencies: 262
-- Name: plantillas_mensajes_id_plantilla_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.plantillas_mensajes_id_plantilla_seq OWNED BY public.plantillas_mensajes.id_plantilla;


--
-- TOC entry 245 (class 1259 OID 34557)
-- Name: productos; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.productos (
    id_producto bigint NOT NULL,
    sku character varying(60) NOT NULL,
    nombre character varying(150) NOT NULL,
    descripcion text,
    tipo character varying(50),
    stock integer DEFAULT 0 NOT NULL,
    precio_unitario numeric(12,2) NOT NULL,
    um character varying(20),
    metadatos jsonb,
    created_by character varying(100),
    created_at timestamp without time zone,
    updated_by character varying(100),
    updated_at timestamp without time zone,
    CONSTRAINT productos_precio_unitario_check CHECK ((precio_unitario >= (0)::numeric)),
    CONSTRAINT productos_stock_check CHECK ((stock >= 0))
);


ALTER TABLE public.productos OWNER TO vet_admin;

--
-- TOC entry 244 (class 1259 OID 34556)
-- Name: productos_id_producto_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.productos_id_producto_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.productos_id_producto_seq OWNER TO vet_admin;

--
-- TOC entry 5623 (class 0 OID 0)
-- Dependencies: 244
-- Name: productos_id_producto_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.productos_id_producto_seq OWNED BY public.productos.id_producto;


--
-- TOC entry 243 (class 1259 OID 34546)
-- Name: proveedores; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.proveedores (
    id_proveedor bigint NOT NULL,
    nombre character varying(150) NOT NULL,
    contacto character varying(100),
    telefono character varying(30),
    direccion character varying(255),
    correo character varying(150),
    created_by character varying(100),
    created_at timestamp without time zone,
    updated_by character varying(100),
    updated_at timestamp without time zone
);


ALTER TABLE public.proveedores OWNER TO vet_admin;

--
-- TOC entry 242 (class 1259 OID 34545)
-- Name: proveedores_id_proveedor_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.proveedores_id_proveedor_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.proveedores_id_proveedor_seq OWNER TO vet_admin;

--
-- TOC entry 5624 (class 0 OID 0)
-- Dependencies: 242
-- Name: proveedores_id_proveedor_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.proveedores_id_proveedor_seq OWNED BY public.proveedores.id_proveedor;


--
-- TOC entry 237 (class 1259 OID 34497)
-- Name: registros_medicos; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.registros_medicos (
    id_registro bigint NOT NULL,
    historia_id bigint NOT NULL,
    fecha timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    motivo text,
    diagnostico text,
    signos_vitales jsonb,
    tratamiento text,
    veterinario_id bigint,
    insumos_usados jsonb,
    archivos jsonb
);


ALTER TABLE public.registros_medicos OWNER TO vet_admin;

--
-- TOC entry 236 (class 1259 OID 34496)
-- Name: registros_medicos_id_registro_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.registros_medicos_id_registro_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.registros_medicos_id_registro_seq OWNER TO vet_admin;

--
-- TOC entry 5625 (class 0 OID 0)
-- Dependencies: 236
-- Name: registros_medicos_id_registro_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.registros_medicos_id_registro_seq OWNED BY public.registros_medicos.id_registro;


--
-- TOC entry 279 (class 1259 OID 34827)
-- Name: reportes; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.reportes (
    id_reporte bigint NOT NULL,
    nombre character varying(120) NOT NULL,
    tipo character varying(50),
    fecha_generacion timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    generado_por bigint,
    parametros jsonb
);


ALTER TABLE public.reportes OWNER TO vet_admin;

--
-- TOC entry 278 (class 1259 OID 34826)
-- Name: reportes_id_reporte_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.reportes_id_reporte_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.reportes_id_reporte_seq OWNER TO vet_admin;

--
-- TOC entry 5626 (class 0 OID 0)
-- Dependencies: 278
-- Name: reportes_id_reporte_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.reportes_id_reporte_seq OWNED BY public.reportes.id_reporte;


--
-- TOC entry 299 (class 1259 OID 43353)
-- Name: respaldos_sistema; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.respaldos_sistema (
    id bigint NOT NULL,
    nombre character varying(255) NOT NULL,
    descripcion text,
    tipo character varying(20) NOT NULL,
    ruta_archivo character varying(500) NOT NULL,
    tamano_bytes bigint,
    hash_verificacion character varying(64),
    fecha_respaldo timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion timestamp without time zone,
    estado character varying(20) DEFAULT 'EXITOSO'::character varying,
    puede_restaurar boolean DEFAULT true,
    creado_por character varying(255),
    restaurado_por character varying(255),
    fecha_restauracion timestamp without time zone,
    CONSTRAINT chk_estado_respaldo CHECK (((estado)::text = ANY ((ARRAY['EXITOSO'::character varying, 'FALLIDO'::character varying, 'EN_PROGRESO'::character varying])::text[]))),
    CONSTRAINT chk_tipo_respaldo CHECK (((tipo)::text = ANY ((ARRAY['AUTOMATICO'::character varying, 'MANUAL'::character varying])::text[])))
);


ALTER TABLE public.respaldos_sistema OWNER TO vet_admin;

--
-- TOC entry 298 (class 1259 OID 43352)
-- Name: respaldos_sistema_id_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.respaldos_sistema_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.respaldos_sistema_id_seq OWNER TO vet_admin;

--
-- TOC entry 5627 (class 0 OID 0)
-- Dependencies: 298
-- Name: respaldos_sistema_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.respaldos_sistema_id_seq OWNED BY public.respaldos_sistema.id;


--
-- TOC entry 225 (class 1259 OID 34386)
-- Name: rol_permisos; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.rol_permisos (
    rol_id bigint NOT NULL,
    permiso_id bigint NOT NULL
);


ALTER TABLE public.rol_permisos OWNER TO vet_admin;

--
-- TOC entry 222 (class 1259 OID 34369)
-- Name: roles; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.roles (
    id_rol bigint NOT NULL,
    nombre_rol character varying(50) NOT NULL,
    descripcion character varying(255)
);


ALTER TABLE public.roles OWNER TO vet_admin;

--
-- TOC entry 221 (class 1259 OID 34368)
-- Name: roles_id_rol_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.roles_id_rol_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.roles_id_rol_seq OWNER TO vet_admin;

--
-- TOC entry 5628 (class 0 OID 0)
-- Dependencies: 221
-- Name: roles_id_rol_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.roles_id_rol_seq OWNED BY public.roles.id_rol;


--
-- TOC entry 229 (class 1259 OID 34442)
-- Name: secretarios; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.secretarios (
    id_usuario bigint NOT NULL,
    extension character varying(20)
);


ALTER TABLE public.secretarios OWNER TO vet_admin;

--
-- TOC entry 253 (class 1259 OID 34628)
-- Name: servicios; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.servicios (
    id_servicio bigint NOT NULL,
    nombre character varying(120) NOT NULL,
    descripcion text,
    tipo character varying(50),
    precio_base numeric(12,2) NOT NULL,
    duracion_min integer,
    created_by character varying(100),
    created_at timestamp without time zone,
    updated_by character varying(100),
    updated_at timestamp without time zone,
    CONSTRAINT servicios_precio_base_check CHECK ((precio_base >= (0)::numeric))
);


ALTER TABLE public.servicios OWNER TO vet_admin;

--
-- TOC entry 293 (class 1259 OID 43283)
-- Name: servicios_configuracion; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.servicios_configuracion (
    id bigint NOT NULL,
    servicio_id bigint,
    nombre character varying(255) NOT NULL,
    descripcion text,
    precio_base numeric(10,2) NOT NULL,
    duracion_estimada_minutos integer DEFAULT 30,
    disponible boolean DEFAULT true,
    requiere_cita boolean DEFAULT true,
    color_hex character varying(7) DEFAULT '#3B82F6'::character varying,
    icono character varying(50),
    creado_por character varying(255),
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    modificado_por character varying(255),
    fecha_modificacion timestamp without time zone,
    activo boolean DEFAULT true
);


ALTER TABLE public.servicios_configuracion OWNER TO vet_admin;

--
-- TOC entry 292 (class 1259 OID 43282)
-- Name: servicios_configuracion_id_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.servicios_configuracion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.servicios_configuracion_id_seq OWNER TO vet_admin;

--
-- TOC entry 5629 (class 0 OID 0)
-- Dependencies: 292
-- Name: servicios_configuracion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.servicios_configuracion_id_seq OWNED BY public.servicios_configuracion.id;


--
-- TOC entry 252 (class 1259 OID 34627)
-- Name: servicios_id_servicio_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.servicios_id_servicio_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.servicios_id_servicio_seq OWNER TO vet_admin;

--
-- TOC entry 5630 (class 0 OID 0)
-- Dependencies: 252
-- Name: servicios_id_servicio_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.servicios_id_servicio_seq OWNED BY public.servicios.id_servicio;


--
-- TOC entry 257 (class 1259 OID 34662)
-- Name: servicios_prestados; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.servicios_prestados (
    id_prestado bigint NOT NULL,
    cita_id bigint NOT NULL,
    servicio_id bigint NOT NULL,
    fecha_ejecucion timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    observaciones text,
    costo_total numeric(12,2) NOT NULL,
    insumos_consumidos jsonb,
    created_by character varying(100),
    created_at timestamp without time zone,
    updated_by character varying(100),
    updated_at timestamp without time zone,
    CONSTRAINT servicios_prestados_costo_total_check CHECK ((costo_total >= (0)::numeric))
);


ALTER TABLE public.servicios_prestados OWNER TO vet_admin;

--
-- TOC entry 256 (class 1259 OID 34661)
-- Name: servicios_prestados_id_prestado_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.servicios_prestados_id_prestado_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.servicios_prestados_id_prestado_seq OWNER TO vet_admin;

--
-- TOC entry 5631 (class 0 OID 0)
-- Dependencies: 256
-- Name: servicios_prestados_id_prestado_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.servicios_prestados_id_prestado_seq OWNED BY public.servicios_prestados.id_prestado;


--
-- TOC entry 287 (class 1259 OID 43120)
-- Name: solicitudes_citas; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.solicitudes_citas (
    id_solicitud bigint NOT NULL,
    cliente_id bigint NOT NULL,
    paciente_id bigint NOT NULL,
    fecha_solicitada date NOT NULL,
    hora_solicitada time without time zone NOT NULL,
    tipo_servicio character varying(100),
    motivo text,
    estado character varying(20) NOT NULL,
    motivo_rechazo text,
    cita_id bigint,
    observaciones text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_by character varying(100),
    updated_by character varying(100),
    aprobado_por bigint,
    aprobado_en timestamp without time zone,
    rechazado_por bigint,
    rechazado_en timestamp without time zone,
    cancelado_por bigint,
    cancelado_en timestamp without time zone,
    CONSTRAINT solicitudes_citas_estado_check CHECK (((estado)::text = ANY ((ARRAY['PENDIENTE'::character varying, 'APROBADA'::character varying, 'RECHAZADA'::character varying, 'CANCELADA'::character varying])::text[])))
);


ALTER TABLE public.solicitudes_citas OWNER TO vet_admin;

--
-- TOC entry 5632 (class 0 OID 0)
-- Dependencies: 287
-- Name: TABLE solicitudes_citas; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON TABLE public.solicitudes_citas IS 'Tabla para almacenar solicitudes de cita del portal del cliente que requieren aprobación del secretario (columnas de auditoría alineadas con Auditable)';


--
-- TOC entry 5633 (class 0 OID 0)
-- Dependencies: 287
-- Name: COLUMN solicitudes_citas.estado; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.solicitudes_citas.estado IS 'Estado de la solicitud: PENDIENTE (en espera de aprobación), APROBADA (convertida en cita), RECHAZADA (rechazada por el secretario), CANCELADA (cancelada por el cliente)';


--
-- TOC entry 5634 (class 0 OID 0)
-- Dependencies: 287
-- Name: COLUMN solicitudes_citas.aprobado_por; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.solicitudes_citas.aprobado_por IS 'ID del usuario (secretario) que aprobó la solicitud';


--
-- TOC entry 5635 (class 0 OID 0)
-- Dependencies: 287
-- Name: COLUMN solicitudes_citas.aprobado_en; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.solicitudes_citas.aprobado_en IS 'Fecha y hora cuando fue aprobada la solicitud';


--
-- TOC entry 5636 (class 0 OID 0)
-- Dependencies: 287
-- Name: COLUMN solicitudes_citas.rechazado_por; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.solicitudes_citas.rechazado_por IS 'ID del usuario (secretario) que rechazó la solicitud';


--
-- TOC entry 5637 (class 0 OID 0)
-- Dependencies: 287
-- Name: COLUMN solicitudes_citas.rechazado_en; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.solicitudes_citas.rechazado_en IS 'Fecha y hora cuando fue rechazada la solicitud';


--
-- TOC entry 5638 (class 0 OID 0)
-- Dependencies: 287
-- Name: COLUMN solicitudes_citas.cancelado_por; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.solicitudes_citas.cancelado_por IS 'ID del usuario que canceló la solicitud';


--
-- TOC entry 5639 (class 0 OID 0)
-- Dependencies: 287
-- Name: COLUMN solicitudes_citas.cancelado_en; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.solicitudes_citas.cancelado_en IS 'Fecha y hora cuando fue cancelada la solicitud';


--
-- TOC entry 286 (class 1259 OID 43119)
-- Name: solicitudes_citas_id_solicitud_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.solicitudes_citas_id_solicitud_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.solicitudes_citas_id_solicitud_seq OWNER TO vet_admin;

--
-- TOC entry 5640 (class 0 OID 0)
-- Dependencies: 286
-- Name: solicitudes_citas_id_solicitud_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.solicitudes_citas_id_solicitud_seq OWNED BY public.solicitudes_citas.id_solicitud;


--
-- TOC entry 305 (class 1259 OID 43458)
-- Name: transacciones; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.transacciones (
    id_transaccion bigint NOT NULL,
    numero_transaccion character varying(50) NOT NULL,
    fecha_transaccion timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    tipo character varying(20) NOT NULL,
    categoria character varying(50),
    factura_id bigint,
    monto numeric(12,2) NOT NULL,
    metodo_pago character varying(30) NOT NULL,
    referencia_pago character varying(100),
    descripcion text,
    concepto character varying(200) NOT NULL,
    estado character varying(20) DEFAULT 'COMPLETADA'::character varying,
    usuario_id bigint,
    metadatos jsonb,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT transacciones_estado_check CHECK (((estado)::text = ANY ((ARRAY['COMPLETADA'::character varying, 'PENDIENTE'::character varying, 'FALLIDA'::character varying, 'REVERSADA'::character varying])::text[]))),
    CONSTRAINT transacciones_metodo_pago_check CHECK (((metodo_pago)::text = ANY ((ARRAY['EFECTIVO'::character varying, 'TARJETA'::character varying, 'TRANSFERENCIA'::character varying, 'PSE'::character varying, 'NEQUI'::character varying, 'DAVIPLATA'::character varying, 'OTRO'::character varying])::text[]))),
    CONSTRAINT transacciones_monto_check CHECK ((monto >= (0)::numeric)),
    CONSTRAINT transacciones_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['INGRESO'::character varying, 'EGRESO'::character varying, 'REEMBOLSO'::character varying, 'AJUSTE'::character varying])::text[])))
);


ALTER TABLE public.transacciones OWNER TO vet_admin;

--
-- TOC entry 5641 (class 0 OID 0)
-- Dependencies: 305
-- Name: TABLE transacciones; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON TABLE public.transacciones IS 'Registro de todos los movimientos financieros de la clínica';


--
-- TOC entry 5642 (class 0 OID 0)
-- Dependencies: 305
-- Name: COLUMN transacciones.numero_transaccion; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.transacciones.numero_transaccion IS 'Código único de transacción con formato TRX-YYYY-NNNN';


--
-- TOC entry 5643 (class 0 OID 0)
-- Dependencies: 305
-- Name: COLUMN transacciones.tipo; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.transacciones.tipo IS 'INGRESO: entrada de dinero, EGRESO: salida de dinero';


--
-- TOC entry 5644 (class 0 OID 0)
-- Dependencies: 305
-- Name: COLUMN transacciones.metodo_pago; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON COLUMN public.transacciones.metodo_pago IS 'Método utilizado para realizar el pago';


--
-- TOC entry 304 (class 1259 OID 43457)
-- Name: transacciones_id_transaccion_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.transacciones_id_transaccion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.transacciones_id_transaccion_seq OWNER TO vet_admin;

--
-- TOC entry 5645 (class 0 OID 0)
-- Dependencies: 304
-- Name: transacciones_id_transaccion_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.transacciones_id_transaccion_seq OWNED BY public.transacciones.id_transaccion;


--
-- TOC entry 226 (class 1259 OID 34401)
-- Name: usuarios; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.usuarios (
    id_usuario bigint NOT NULL,
    username character varying(60) NOT NULL,
    password_hash character varying(255) NOT NULL,
    activo boolean DEFAULT true NOT NULL,
    ultimo_acceso timestamp with time zone,
    rol_id bigint NOT NULL,
    password_reset_token character varying(255),
    password_reset_token_expiry timestamp with time zone
);


ALTER TABLE public.usuarios OWNER TO vet_admin;

--
-- TOC entry 228 (class 1259 OID 34430)
-- Name: usuarios_veterinarios; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.usuarios_veterinarios (
    id_usuario bigint NOT NULL,
    licencia_profesional character varying(100),
    especialidad character varying(100),
    disponibilidad jsonb
);


ALTER TABLE public.usuarios_veterinarios OWNER TO vet_admin;

--
-- TOC entry 239 (class 1259 OID 34517)
-- Name: vacunaciones; Type: TABLE; Schema: public; Owner: vet_admin
--

CREATE TABLE public.vacunaciones (
    id_vacunacion bigint NOT NULL,
    paciente_id bigint NOT NULL,
    tipo_vacuna character varying(100) NOT NULL,
    fecha_aplicacion date NOT NULL,
    proxima_dosis date,
    veterinario_id bigint,
    created_by character varying(100),
    created_at timestamp without time zone,
    updated_by character varying(100),
    updated_at timestamp without time zone
);


ALTER TABLE public.vacunaciones OWNER TO vet_admin;

--
-- TOC entry 238 (class 1259 OID 34516)
-- Name: vacunaciones_id_vacunacion_seq; Type: SEQUENCE; Schema: public; Owner: vet_admin
--

CREATE SEQUENCE public.vacunaciones_id_vacunacion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.vacunaciones_id_vacunacion_seq OWNER TO vet_admin;

--
-- TOC entry 5646 (class 0 OID 0)
-- Dependencies: 238
-- Name: vacunaciones_id_vacunacion_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vet_admin
--

ALTER SEQUENCE public.vacunaciones_id_vacunacion_seq OWNED BY public.vacunaciones.id_vacunacion;


--
-- TOC entry 4988 (class 2604 OID 34616)
-- Name: alertas_inventario id_alerta; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.alertas_inventario ALTER COLUMN id_alerta SET DEFAULT nextval('public.alertas_inventario_id_alerta_seq'::regclass);


--
-- TOC entry 5044 (class 2604 OID 43327)
-- Name: auditoria_detallada id; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.auditoria_detallada ALTER COLUMN id SET DEFAULT nextval('public.auditoria_detallada_id_seq'::regclass);


--
-- TOC entry 5010 (class 2604 OID 34807)
-- Name: backups_sistema id_backup; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.backups_sistema ALTER COLUMN id_backup SET DEFAULT nextval('public.backups_sistema_id_backup_seq'::regclass);


--
-- TOC entry 5004 (class 2604 OID 34706)
-- Name: canales_envio id_canal; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.canales_envio ALTER COLUMN id_canal SET DEFAULT nextval('public.canales_envio_id_canal_seq'::regclass);


--
-- TOC entry 4991 (class 2604 OID 34643)
-- Name: citas id_cita; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.citas ALTER COLUMN id_cita SET DEFAULT nextval('public.citas_id_cita_seq'::regclass);


--
-- TOC entry 5052 (class 2604 OID 43374)
-- Name: configuracion_avanzada id; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.configuracion_avanzada ALTER COLUMN id SET DEFAULT nextval('public.configuracion_avanzada_id_seq'::regclass);


--
-- TOC entry 4981 (class 2604 OID 34537)
-- Name: desparasitaciones id_desparasitacion; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.desparasitaciones ALTER COLUMN id_desparasitacion SET DEFAULT nextval('public.desparasitaciones_id_desparasitacion_seq'::regclass);


--
-- TOC entry 5008 (class 2604 OID 34744)
-- Name: destinatarios id_destinatario; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.destinatarios ALTER COLUMN id_destinatario SET DEFAULT nextval('public.destinatarios_id_destinatario_seq'::regclass);


--
-- TOC entry 5056 (class 2604 OID 43435)
-- Name: detalle_factura id_detalle; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.detalle_factura ALTER COLUMN id_detalle SET DEFAULT nextval('public.detalle_factura_id_detalle_seq'::regclass);


--
-- TOC entry 5016 (class 2604 OID 34847)
-- Name: estadisticas id_estadistica; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.estadisticas ALTER COLUMN id_estadistica SET DEFAULT nextval('public.estadisticas_id_estadistica_seq'::regclass);


--
-- TOC entry 4995 (class 2604 OID 34686)
-- Name: facturas id_factura; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.facturas ALTER COLUMN id_factura SET DEFAULT nextval('public.facturas_id_factura_seq'::regclass);


--
-- TOC entry 5018 (class 2604 OID 34867)
-- Name: fuentes_datos id_fuente; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.fuentes_datos ALTER COLUMN id_fuente SET DEFAULT nextval('public.fuentes_datos_id_fuente_seq'::regclass);


--
-- TOC entry 4973 (class 2604 OID 34456)
-- Name: historial_acciones id_accion; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.historial_acciones ALTER COLUMN id_accion SET DEFAULT nextval('public.historial_acciones_id_accion_seq'::regclass);


--
-- TOC entry 4976 (class 2604 OID 34485)
-- Name: historias_clinicas id_historia; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.historias_clinicas ALTER COLUMN id_historia SET DEFAULT nextval('public.historias_clinicas_id_historia_seq'::regclass);


--
-- TOC entry 5040 (class 2604 OID 43309)
-- Name: horarios_atencion id; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.horarios_atencion ALTER COLUMN id SET DEFAULT nextval('public.horarios_atencion_id_seq'::regclass);


--
-- TOC entry 5017 (class 2604 OID 34856)
-- Name: indicadores id_indicador; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.indicadores ALTER COLUMN id_indicador SET DEFAULT nextval('public.indicadores_id_indicador_seq'::regclass);


--
-- TOC entry 5022 (class 2604 OID 43246)
-- Name: informacion_clinica id; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.informacion_clinica ALTER COLUMN id SET DEFAULT nextval('public.informacion_clinica_id_seq'::regclass);


--
-- TOC entry 5012 (class 2604 OID 34817)
-- Name: logs_sistema id_log; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.logs_sistema ALTER COLUMN id_log SET DEFAULT nextval('public.logs_sistema_id_log_seq'::regclass);


--
-- TOC entry 4985 (class 2604 OID 34576)
-- Name: lotes id_lote; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.lotes ALTER COLUMN id_lote SET DEFAULT nextval('public.lotes_id_lote_seq'::regclass);


--
-- TOC entry 4986 (class 2604 OID 34592)
-- Name: movimientos_inventario id_movimiento; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.movimientos_inventario ALTER COLUMN id_movimiento SET DEFAULT nextval('public.movimientos_inventario_id_movimiento_seq'::regclass);


--
-- TOC entry 5006 (class 2604 OID 34728)
-- Name: notificaciones id_notificacion; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.notificaciones ALTER COLUMN id_notificacion SET DEFAULT nextval('public.notificaciones_id_notificacion_seq'::regclass);


--
-- TOC entry 4975 (class 2604 OID 34471)
-- Name: pacientes id_paciente; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.pacientes ALTER COLUMN id_paciente SET DEFAULT nextval('public.pacientes_id_paciente_seq'::regclass);


--
-- TOC entry 5009 (class 2604 OID 34796)
-- Name: parametros_sistema id_parametro; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.parametros_sistema ALTER COLUMN id_parametro SET DEFAULT nextval('public.parametros_sistema_id_parametro_seq'::regclass);


--
-- TOC entry 4970 (class 2604 OID 34381)
-- Name: permisos id_permiso; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.permisos ALTER COLUMN id_permiso SET DEFAULT nextval('public.permisos_id_permiso_seq'::regclass);


--
-- TOC entry 5029 (class 2604 OID 43264)
-- Name: permisos_rol id; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.permisos_rol ALTER COLUMN id SET DEFAULT nextval('public.permisos_rol_id_seq'::regclass);


--
-- TOC entry 4968 (class 2604 OID 34361)
-- Name: personas id_persona; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.personas ALTER COLUMN id_persona SET DEFAULT nextval('public.personas_id_persona_seq'::regclass);


--
-- TOC entry 5005 (class 2604 OID 34717)
-- Name: plantillas_mensajes id_plantilla; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.plantillas_mensajes ALTER COLUMN id_plantilla SET DEFAULT nextval('public.plantillas_mensajes_id_plantilla_seq'::regclass);


--
-- TOC entry 4983 (class 2604 OID 34560)
-- Name: productos id_producto; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.productos ALTER COLUMN id_producto SET DEFAULT nextval('public.productos_id_producto_seq'::regclass);


--
-- TOC entry 4982 (class 2604 OID 34549)
-- Name: proveedores id_proveedor; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.proveedores ALTER COLUMN id_proveedor SET DEFAULT nextval('public.proveedores_id_proveedor_seq'::regclass);


--
-- TOC entry 4978 (class 2604 OID 34500)
-- Name: registros_medicos id_registro; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.registros_medicos ALTER COLUMN id_registro SET DEFAULT nextval('public.registros_medicos_id_registro_seq'::regclass);


--
-- TOC entry 5014 (class 2604 OID 34830)
-- Name: reportes id_reporte; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.reportes ALTER COLUMN id_reporte SET DEFAULT nextval('public.reportes_id_reporte_seq'::regclass);


--
-- TOC entry 5048 (class 2604 OID 43356)
-- Name: respaldos_sistema id; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.respaldos_sistema ALTER COLUMN id SET DEFAULT nextval('public.respaldos_sistema_id_seq'::regclass);


--
-- TOC entry 4969 (class 2604 OID 34372)
-- Name: roles id_rol; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.roles ALTER COLUMN id_rol SET DEFAULT nextval('public.roles_id_rol_seq'::regclass);


--
-- TOC entry 4990 (class 2604 OID 34631)
-- Name: servicios id_servicio; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.servicios ALTER COLUMN id_servicio SET DEFAULT nextval('public.servicios_id_servicio_seq'::regclass);


--
-- TOC entry 5033 (class 2604 OID 43286)
-- Name: servicios_configuracion id; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.servicios_configuracion ALTER COLUMN id SET DEFAULT nextval('public.servicios_configuracion_id_seq'::regclass);


--
-- TOC entry 4993 (class 2604 OID 34665)
-- Name: servicios_prestados id_prestado; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.servicios_prestados ALTER COLUMN id_prestado SET DEFAULT nextval('public.servicios_prestados_id_prestado_seq'::regclass);


--
-- TOC entry 5019 (class 2604 OID 43123)
-- Name: solicitudes_citas id_solicitud; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.solicitudes_citas ALTER COLUMN id_solicitud SET DEFAULT nextval('public.solicitudes_citas_id_solicitud_seq'::regclass);


--
-- TOC entry 5059 (class 2604 OID 43461)
-- Name: transacciones id_transaccion; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.transacciones ALTER COLUMN id_transaccion SET DEFAULT nextval('public.transacciones_id_transaccion_seq'::regclass);


--
-- TOC entry 4980 (class 2604 OID 34520)
-- Name: vacunaciones id_vacunacion; Type: DEFAULT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.vacunaciones ALTER COLUMN id_vacunacion SET DEFAULT nextval('public.vacunaciones_id_vacunacion_seq'::regclass);


--
-- TOC entry 5522 (class 0 OID 34613)
-- Dependencies: 251
-- Data for Name: alertas_inventario; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.alertas_inventario (id_alerta, producto_id, nivel_stock, fecha_generada, mensaje, created_by, created_at, updated_by, updated_at) FROM stdin;
1	3	15	2025-11-26 17:32:26.833755-05	Stock bajo para vacunas antirrábicas	\N	\N	\N	\N
\.


--
-- TOC entry 5568 (class 0 OID 43324)
-- Dependencies: 297
-- Data for Name: auditoria_detallada; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.auditoria_detallada (id, historial_accion_id, usuario_id, rol_nombre, modulo, entidad, entidad_id, datos_anteriores, datos_nuevos, relevancia, requiere_revision, ip_address, user_agent, fecha_accion) FROM stdin;
\.


--
-- TOC entry 5546 (class 0 OID 34804)
-- Dependencies: 275
-- Data for Name: backups_sistema; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.backups_sistema (id_backup, fecha_creacion, ruta_archivo, metadata) FROM stdin;
1	2025-11-26 17:32:26.833755-05	/backups/backup_inicial.sql	{"creadoPor": "script"}
\.


--
-- TOC entry 5542 (class 0 OID 34782)
-- Dependencies: 271
-- Data for Name: canales_app; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.canales_app (id_canal, app_topic) FROM stdin;
3	veterinaria-recordatorios
\.


--
-- TOC entry 5540 (class 0 OID 34762)
-- Dependencies: 269
-- Data for Name: canales_email; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.canales_email (id_canal, smtp_server, from_address) FROM stdin;
1	smtp.gmail.com	notificaciones@veterinaria.com
\.


--
-- TOC entry 5532 (class 0 OID 34703)
-- Dependencies: 261
-- Data for Name: canales_envio; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.canales_envio (id_canal, nombre, configuracion) FROM stdin;
1	EMAIL	{"host": "smtp.gmail.com", "puerto": 587}
2	SMS	{"proveedor": "twilio"}
3	APP	{"topic": "veterinaria"}
\.


--
-- TOC entry 5541 (class 0 OID 34772)
-- Dependencies: 270
-- Data for Name: canales_sms; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.canales_sms (id_canal, proveedor_api) FROM stdin;
2	twilio
\.


--
-- TOC entry 5526 (class 0 OID 34640)
-- Dependencies: 255
-- Data for Name: citas; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.citas (id_cita, paciente_id, veterinario_id, fecha_hora, tipo_servicio, estado, motivo, triage_nivel, created_by, created_at, updated_by, updated_at) FROM stdin;
1	1	2	2025-11-08 10:00:00-05	CONSULTA	PROGRAMADA	Control anual	BAJO	system	2025-11-08 10:00:00	\N	\N
2	1	2	2025-11-27 18:29:00-05	Consulta	REALIZADA	Equis	MEDIA	system	2025-11-27 18:29:00	\N	\N
\.


--
-- TOC entry 5498 (class 0 OID 34419)
-- Dependencies: 227
-- Data for Name: clientes; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.clientes (id_usuario, fecha_registro, documento_identidad) FROM stdin;
4	2025-11-26 17:32:26.833755-05	CC-100200300
9	2025-11-26 18:26:28.462618-05	\N
\.


--
-- TOC entry 5572 (class 0 OID 43371)
-- Dependencies: 301
-- Data for Name: configuracion_avanzada; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.configuracion_avanzada (id, clave, valor, categoria, tipo_dato, descripcion, valor_por_defecto, requerido, editable, creado_por, fecha_creacion, modificado_por, fecha_modificacion) FROM stdin;
1	respaldos.automaticos.habilitado	true	RESPALDOS	BOOLEAN	Habilitar respaldos automáticos diarios	true	t	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N
2	respaldos.automaticos.hora	02:00	RESPALDOS	STRING	Hora de ejecución de respaldos automáticos (HH:MM)	02:00	t	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N
3	respaldos.retencion.dias	30	RESPALDOS	INTEGER	Días de retención de respaldos antiguos	30	t	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N
4	respaldos.ubicacion	/var/backups/veterinaria	RESPALDOS	STRING	Ubicación de almacenamiento de respaldos	/var/backups/veterinaria	t	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N
5	auditoria.retener.dias	90	AUDITORIA	INTEGER	Días de retención del historial de auditoría	90	t	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N
6	auditoria.nivel.detalle	NORMAL	AUDITORIA	STRING	Nivel de detalle de auditoría (MINIMO, NORMAL, COMPLETO)	NORMAL	t	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N
7	citas.duracion.estandar	30	CITAS	INTEGER	Duración estándar de citas en minutos	30	t	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N
8	citas.maximo.dia	12	CITAS	INTEGER	Máximo de citas por día	12	t	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N
9	citas.anticipacion.horas	24	CITAS	INTEGER	Horas de anticipación para agendar citas	24	t	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N
10	citas.cancelacion.horas	12	CITAS	INTEGER	Horas máximas para cancelar citas	12	t	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N
\.


--
-- TOC entry 5512 (class 0 OID 34534)
-- Dependencies: 241
-- Data for Name: desparasitaciones; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.desparasitaciones (id_desparasitacion, paciente_id, producto_usado, fecha_aplicacion, proxima_aplicacion, created_by, created_at, updated_by, updated_at) FROM stdin;
1	1	Ivermectina	2025-02-05	2025-08-05	\N	\N	\N	\N
\.


--
-- TOC entry 5538 (class 0 OID 34741)
-- Dependencies: 267
-- Data for Name: destinatarios; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.destinatarios (id_destinatario, tipo_destinatario, referencia_id) FROM stdin;
1	CLIENTE	4
\.


--
-- TOC entry 5574 (class 0 OID 43432)
-- Dependencies: 303
-- Data for Name: detalle_factura; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.detalle_factura (id_detalle, factura_id, concepto, descripcion, cantidad, precio_unitario, subtotal, producto_id, metadatos, created_at) FROM stdin;
\.


--
-- TOC entry 5552 (class 0 OID 34844)
-- Dependencies: 281
-- Data for Name: estadisticas; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.estadisticas (id_estadistica, nombre, valor, periodo_inicio, periodo_fin) FROM stdin;
1	Ingresos Mensuales	0.0000	2025-11-01	2025-11-30
\.


--
-- TOC entry 5530 (class 0 OID 34683)
-- Dependencies: 259
-- Data for Name: facturas; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.facturas (id_factura, numero_factura, fecha_emision, total, estado, cliente_id, contenido, created_by, created_at, updated_by, updated_at, fecha_pago, fecha_vencimiento, servicio_prestado_id, paciente_id, subtotal, descuento, iva_porcentaje, iva_monto, monto_pagado, saldo_pendiente, notas, metadatos) FROM stdin;
1	FAC-2025-0001	2025-11-26 17:32:26.833755-05	45000.00	PAGADA	4	{"detalle": [{"valor": 45000, "servicio": "Consulta general"}]}	system	2025-11-26 17:32:26.833755	\N	\N	\N	\N	\N	\N	0.00	0.00	19.00	0.00	0.00	0.00	\N	\N
\.


--
-- TOC entry 5489 (class 0 OID 34348)
-- Dependencies: 218
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	1	init schema	SQL	V1__init_schema.sql	1662312837	vet_admin	2025-11-26 17:32:26.743898	721	t
2	2	add password reset fields	SQL	V2__add_password_reset_fields.sql	-1923717659	vet_admin	2025-11-26 17:32:27.575796	45	t
3	3	add audit columns	SQL	V3__add_audit_columns.sql	191555518	vet_admin	2025-11-30 13:33:39.941829	167	t
4	4	create solicitudes citas table	SQL	V4__create_solicitudes_citas_table.sql	119094535	vet_admin	2025-11-30 13:57:06.919216	93	t
5	5	improve cita concurrency	SQL	V5__improve_cita_concurrency.sql	1633222720	vet_admin	2025-11-30 13:57:07.090571	30	t
6	6	add audit trail solicitudes	SQL	V6__add_audit_trail_solicitudes.sql	-1948092781	vet_admin	2025-11-30 13:57:07.136866	8	t
7	8	alter historial acciones	SQL	V8__alter_historial_acciones.sql	304374157	vet_admin	2025-11-30 13:57:07.156822	15	t
8	9	add fecha pago to facturas	SQL	V9__add_fecha_pago_to_facturas.sql	-1833487162	vet_admin	2025-11-30 13:57:07.183501	9	t
9	10	create configuracion module tables	SQL	V10__create_configuracion_module_tables.sql	1312677195	vet_admin	2025-11-30 14:02:10.782842	266	t
10	11	fix solicitudes citas audit columns	SQL	V11__fix_solicitudes_citas_audit_columns.sql	911492933	vet_admin	2025-11-30 14:08:06.123186	22	t
11	12	add unique identifier pacientes	SQL	V12__add_unique_identifier_pacientes.sql	-623294966	vet_admin	2025-11-30 18:07:45.911155	86	t
12	13	create finanzas facturacion	SQL	V13__create_finanzas_facturacion.sql	-499688925	vet_admin	2025-11-30 18:15:07.120098	306	t
\.


--
-- TOC entry 5556 (class 0 OID 34864)
-- Dependencies: 285
-- Data for Name: fuentes_datos; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.fuentes_datos (id_fuente, nombre, tipo, configuracion) FROM stdin;
1	PostgreSQL Interno	DATABASE	{"descripcion": "Fuente principal del sistema"}
\.


--
-- TOC entry 5502 (class 0 OID 34453)
-- Dependencies: 231
-- Data for Name: historial_acciones; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.historial_acciones (id_accion, usuario_id, fecha_hora, descripcion, metadata, tipo_accion, ip_address) FROM stdin;
1	1	2025-11-26 17:32:26.833755-05	Usuario inicial creado	{"ip": "127.0.0.1"}	ACCION_GENERAL	\N
2	2	2025-11-26 17:32:26.833755-05	Usuario inicial creado	{"ip": "127.0.0.1"}	ACCION_GENERAL	\N
3	3	2025-11-26 17:32:26.833755-05	Usuario inicial creado	{"ip": "127.0.0.1"}	ACCION_GENERAL	\N
4	4	2025-11-26 17:32:26.833755-05	Usuario inicial creado	{"ip": "127.0.0.1"}	ACCION_GENERAL	\N
\.


--
-- TOC entry 5506 (class 0 OID 34482)
-- Dependencies: 235
-- Data for Name: historias_clinicas; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.historias_clinicas (id_historia, paciente_id, fecha_apertura, resumen, metadatos, created_by, created_at, updated_by, updated_at) FROM stdin;
1	1	2025-11-26 17:32:26.833755-05	Historia clínica creada automáticamente para Firulais	{"origen": "registro inicial"}	system	2025-11-26 17:32:26.833755	\N	\N
\.


--
-- TOC entry 5566 (class 0 OID 43306)
-- Dependencies: 295
-- Data for Name: horarios_atencion; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.horarios_atencion (id, dia_semana, hora_apertura, hora_cierre, abierto, descripcion, creado_por, fecha_creacion, modificado_por, fecha_modificacion, activo) FROM stdin;
1	1	08:00:00	18:00:00	t	Lunes a Viernes - Horario de atención regular	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
2	2	08:00:00	18:00:00	t	Lunes a Viernes - Horario de atención regular	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
3	3	08:00:00	18:00:00	t	Lunes a Viernes - Horario de atención regular	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
4	4	08:00:00	18:00:00	t	Lunes a Viernes - Horario de atención regular	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
5	5	08:00:00	18:00:00	t	Lunes a Viernes - Horario de atención regular	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
6	6	09:00:00	18:00:00	t	Sábados - Horario de atención fin de semana	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
7	7	08:00:00	12:00:00	f	Domingos y Festivos - Cerrado	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
\.


--
-- TOC entry 5554 (class 0 OID 34853)
-- Dependencies: 283
-- Data for Name: indicadores; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.indicadores (id_indicador, nombre, descripcion, valor_actual) FROM stdin;
1	CitasPendientes	Cantidad de citas pendientes de atención	0.0000
\.


--
-- TOC entry 5560 (class 0 OID 43243)
-- Dependencies: 289
-- Data for Name: informacion_clinica; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.informacion_clinica (id, nombre_clinica, nit, telefono, email, direccion, idioma, moneda, zona_horaria, formato_fecha, logo_url, creado_por, fecha_creacion, modificado_por, fecha_modificacion, activo) FROM stdin;
1	Clínica Veterinaria Universitaria Humboldt	900.123.456-7	+57 312 456 7890	contacto@vetclinic.com	Calle 123 #45-67, Armenia, Quindío	es	COP	America/Bogota	DD/MM/YYYY	\N	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
\.


--
-- TOC entry 5548 (class 0 OID 34814)
-- Dependencies: 277
-- Data for Name: logs_sistema; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.logs_sistema (id_log, fecha_hora, nivel, componente, mensaje, metadata) FROM stdin;
1	2025-11-26 17:32:26.833755-05	INFO	bootstrap	Inicialización de datos de ejemplo completada	{"script": "V1__init_schema.sql"}
\.


--
-- TOC entry 5518 (class 0 OID 34573)
-- Dependencies: 247
-- Data for Name: lotes; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.lotes (id_lote, producto_id, fecha_vencimiento, cantidad, numero_lote) FROM stdin;
1	1	2026-12-31	20	L-ANTIP-2025
\.


--
-- TOC entry 5520 (class 0 OID 34589)
-- Dependencies: 249
-- Data for Name: movimientos_inventario; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.movimientos_inventario (id_movimiento, producto_id, tipo_movimiento, cantidad, fecha, proveedor_id, referencia, usuario_id, created_by, created_at, updated_by, updated_at) FROM stdin;
1	1	IN	50	2025-11-26 17:32:26.833755-05	1	OC-2025-001	3	system	2025-11-26 17:32:26.833755	\N	\N
\.


--
-- TOC entry 5539 (class 0 OID 34747)
-- Dependencies: 268
-- Data for Name: notificacion_destinatarios; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.notificacion_destinatarios (notificacion_id, destinatario_id) FROM stdin;
1	1
\.


--
-- TOC entry 5536 (class 0 OID 34725)
-- Dependencies: 265
-- Data for Name: notificaciones; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.notificaciones (id_notificacion, tipo, mensaje, fecha_envio_programada, fecha_envio_real, estado, plantilla_id, datos, created_by, created_at, updated_by, updated_at) FROM stdin;
1	RECORDATORIO_CITA	Recordatorio automático de cita	2025-11-27 17:32:26.833755-05	\N	PENDIENTE	1	{"hora": "10:00", "fecha": "2025-11-08", "nombreCliente": "Diego", "nombreMascota": "Firulais"}	system	2025-11-27 17:32:26.833755	\N	\N
2	CITA_PROGRAMADA	🐾 *Confirmación de Cita - Clínica Veterinaria Humboldt*\n\nEstimado/a Diego López,\n\nSu cita ha sido programada exitosamente:\n\n📅 *Fecha y Hora:* 27/11/2025 18:29\n🐕 *Paciente:* Firulais (perro)\n👨‍⚕️ *Veterinario:* Dr. Carlos Méndez\n🏥 *Tipo de Servicio:* Consulta\n*Motivo:* Equis\n\nPor favor, llegue 10 minutos antes de su cita.\n\nSi necesita cancelar o reprogramar, contáctenos con al menos 24 horas de anticipación.\n\nSaludos cordiales,\nClínica Veterinaria Humboldt	\N	2025-11-26 18:30:19.729108-05	FALLIDA	\N	{"fechaCita": "27/11/2025 18:29", "destinatario": "diego.cliente@email.com", "nombreCliente": "Diego López", "nombrePaciente": "Firulais"}	system	\N	\N	\N
\.


--
-- TOC entry 5504 (class 0 OID 34468)
-- Dependencies: 233
-- Data for Name: pacientes; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.pacientes (id_paciente, nombre, especie, raza, fecha_nacimiento, sexo, peso_kg, estado_salud, cliente_id, identificador_externo, created_by, created_at, updated_by, updated_at) FROM stdin;
1	Firulais	perro	Labrador	2020-05-15	Macho	28.50	Estable	4	04e0ed97-75c9-47d0-8baa-b3d6e4c0da6c	system	2025-11-30 13:33:40.016764	\N	\N
\.


--
-- TOC entry 5544 (class 0 OID 34793)
-- Dependencies: 273
-- Data for Name: parametros_sistema; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.parametros_sistema (id_parametro, clave, valor, descripcion, aplicacion) FROM stdin;
1	notificaciones.email.enabled	true	Habilita el envío de notificaciones por email	notificaciones
2	notificaciones.sms.enabled	false	Habilita el envío de notificaciones por SMS	notificaciones
3	inventario.stock.minimo	10	Stock mínimo para generar alertas	inventario
4	sistema.mantenimiento	false	Indica si el sistema está en mantenimiento	global
\.


--
-- TOC entry 5495 (class 0 OID 34378)
-- Dependencies: 224
-- Data for Name: permisos; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.permisos (id_permiso, nombre, descripcion) FROM stdin;
1	CREAR_USUARIO	Permite crear nuevos usuarios
2	EDITAR_USUARIO	Permite editar usuarios existentes
3	ELIMINAR_USUARIO	Permite eliminar usuarios
4	VER_USUARIOS	Permite ver lista de usuarios
5	CREAR_PACIENTE	Permite crear nuevos pacientes
6	EDITAR_PACIENTE	Permite editar pacientes existentes
7	VER_PACIENTES	Permite ver lista de pacientes
8	CREAR_CITA	Permite crear nuevas citas
9	EDITAR_CITA	Permite editar citas existentes
10	CANCELAR_CITA	Permite cancelar citas
11	GESTIONAR_INVENTARIO	Permite gestionar inventario
12	VER_REPORTES	Permite ver reportes y estadísticas
\.


--
-- TOC entry 5562 (class 0 OID 43261)
-- Dependencies: 291
-- Data for Name: permisos_rol; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.permisos_rol (id, rol_id, modulo, accion, ruta, descripcion, permitido, creado_por, fecha_creacion, modificado_por, fecha_modificacion, activo) FROM stdin;
1	1	usuarios	ver	/usuarios	Ver lista de usuarios	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
2	1	usuarios	crear	/usuarios	Crear nuevos usuarios	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
3	1	usuarios	editar	/usuarios	Editar usuarios existentes	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
4	1	usuarios	eliminar	/usuarios	Eliminar usuarios	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
5	1	inventario	ver	/admin/inventario	Ver inventario	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
6	1	inventario	gestionar	/admin/inventario	Gestionar inventario completo	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
7	1	finanzas	ver	/admin/finanzas	Ver finanzas	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
8	1	reportes	generar	/reportes	Generar reportes	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
9	1	configuracion	gestionar	/configuracion	Acceso total a configuración	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
10	2	pacientes	ver	/veterinario/pacientes	Ver pacientes	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
11	2	pacientes	gestionar	/veterinario/pacientes	Gestionar pacientes	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
12	2	historias	ver	/veterinario/historias	Ver historias clínicas	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
13	2	historias	editar	/veterinario/historias	Editar historias clínicas	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
14	2	consultas	realizar	/veterinario/consultas	Realizar consultas	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
15	2	citas	gestionar	/veterinario/agenda	Gestionar agenda de citas	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
16	2	inventario	ver	/veterinario/inventario	Ver inventario disponible	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
17	3	citas	ver	/secretario/citas	Ver citas	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
18	3	citas	crear	/secretario/citas	Crear citas	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
19	3	citas	editar	/secretario/citas	Editar citas	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
20	3	clientes	ver	/clientes	Ver clientes	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
21	3	clientes	gestionar	/clientes	Gestionar clientes	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
22	3	facturas	ver	/secretario/facturas	Ver facturas	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
23	3	facturas	crear	/secretario/facturas	Crear facturas	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
24	3	inventario	ver	/secretario/inventario	Ver inventario	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
25	3	notificaciones	enviar	/notificaciones	Enviar notificaciones	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
26	4	mascotas	ver	/cliente/mascotas	Ver mis mascotas	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
27	4	citas	ver	/cliente/citas	Ver mis citas	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
28	4	citas	solicitar	/cliente/citas	Solicitar citas	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
29	4	historial	ver	/cliente/historial	Ver historial médico	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
30	4	facturas	ver	/cliente/facturas	Ver mis facturas	t	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
\.


--
-- TOC entry 5491 (class 0 OID 34358)
-- Dependencies: 220
-- Data for Name: personas; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.personas (id_persona, nombre, apellido, correo, telefono, direccion, created_by, created_at, updated_by, updated_at) FROM stdin;
1	Ana	Ramírez	admin@veterinaria.com	3100000001	Calle 1 #2-3	system	2025-11-30 13:33:40.016764	\N	\N
2	Carlos	Méndez	carlos.vet@veterinaria.com	3100000002	Carrera 5 #10-20	system	2025-11-30 13:33:40.016764	\N	\N
3	Laura	Gómez	laura.sec@veterinaria.com	3100000003	Avenida 15 #8-40	system	2025-11-30 13:33:40.016764	\N	\N
4	Diego	López	diego.cliente@email.com	3100000004	Transversal 9 #6-15	system	2025-11-30 13:33:40.016764	\N	\N
5	Maria	Gonzalez	maria.secretaria@veterinaria.com	3100000006	Calle 25 #10-20	system	2025-11-30 13:33:40.016764	\N	\N
6	Carlos	Rodriguez	admin2@veterinaria.com	3001112222	Oficina Central	system	2025-11-30 13:33:40.016764	\N	\N
7	Maria	Lopez	maria.vet@veterinaria.com	3002223333	Calle 15 #20-30	system	2025-11-30 13:33:40.016764	\N	\N
8	Pedro	Martinez	pedro.sec@veterinaria.com	3003334444	Calle 30 #15-25	system	2025-11-30 13:33:40.016764	\N	\N
9	Ana	Gomez	ana.cliente@gmail.com	3004445555	Carrera 5 #10-15	system	2025-11-30 13:33:40.016764	\N	\N
10	Laura	Vélez	lgvelez_159@cue.edu.co	3225987813	sta rita mz 11 casa 13	system	2025-11-30 13:33:40.016764	\N	\N
11	Martin	Martinez	mmnez@gmail.com	1216514653135	cualquiera	system	2025-11-30 13:33:40.016764	\N	\N
12	Juan	Mendez	jmendez@gmail.com	616516151	equis	system	2025-11-30 13:33:40.016764	\N	\N
13	Luisa	Gonzalez	LuGonza@gmail.com	68546485	cualquiera	system	2025-11-30 13:33:40.016764	\N	\N
\.


--
-- TOC entry 5534 (class 0 OID 34714)
-- Dependencies: 263
-- Data for Name: plantillas_mensajes; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.plantillas_mensajes (id_plantilla, nombre, asunto, cuerpo, variables) FROM stdin;
1	Recordatorio Cita	Recordatorio de cita veterinaria	Hola {{nombreCliente}}, recuerda tu cita para {{nombreMascota}} el {{fecha}} a las {{hora}}.	["nombreCliente", "nombreMascota", "fecha", "hora"]
2	Vacunación Pendiente	Próxima dosis de vacuna	Estimado {{nombreCliente}}, la próxima dosis de {{tipoVacuna}} para {{nombreMascota}} está programada el {{fecha}}.	["nombreCliente", "nombreMascota", "tipoVacuna", "fecha"]
\.


--
-- TOC entry 5516 (class 0 OID 34557)
-- Dependencies: 245
-- Data for Name: productos; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.productos (id_producto, sku, nombre, descripcion, tipo, stock, precio_unitario, um, metadatos, created_by, created_at, updated_by, updated_at) FROM stdin;
1	SKU-ANTIP	Antiparasitario interno	Tabletas antiparasitarias para perros medianos	medicamento	35	22000.00	tableta	{"dosis": "1 tableta por cada 10kg"}	system	2025-11-30 13:33:40.016764	\N	\N
2	SKU-GASA	Gasas estériles 10x10	Paquete de gasas para curaciones	insumo	120	5000.00	paquete	\N	system	2025-11-30 13:33:40.016764	\N	\N
3	SKU-VACRAB	Vacuna antirrábica	Vacuna para rabia canina/felina	biologico	15	58000.00	frasco	{"fabricante": "BioVet"}	system	2025-11-30 13:33:40.016764	\N	\N
\.


--
-- TOC entry 5514 (class 0 OID 34546)
-- Dependencies: 243
-- Data for Name: proveedores; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.proveedores (id_proveedor, nombre, contacto, telefono, direccion, correo, created_by, created_at, updated_by, updated_at) FROM stdin;
1	Distribuidora VetMed	Paula Herrera	3001234567	Zona Industrial 45	ventas@vetmed.com	\N	\N	\N	\N
2	FarmAnimal SAS	Ricardo Díaz	3007654321	Av. Central 100-20	contacto@farmanimal.com	\N	\N	\N	\N
\.


--
-- TOC entry 5508 (class 0 OID 34497)
-- Dependencies: 237
-- Data for Name: registros_medicos; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.registros_medicos (id_registro, historia_id, fecha, motivo, diagnostico, signos_vitales, tratamiento, veterinario_id, insumos_usados, archivos) FROM stdin;
1	1	2025-11-26 17:32:26.833755-05	Chequeo general de ingreso	Sin hallazgos relevantes	{"fc": 95, "fr": 22, "temperatura": 38.2}	Continuar alimentación balanceada	2	{"vitaminas": 1}	\N
\.


--
-- TOC entry 5550 (class 0 OID 34827)
-- Dependencies: 279
-- Data for Name: reportes; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.reportes (id_reporte, nombre, tipo, fecha_generacion, generado_por, parametros) FROM stdin;
1	Reporte Inicial de Citas	OPERATIVO	2025-11-26 17:32:26.833755-05	1	{"descripcion": "Reporte vacío generado en la migración"}
\.


--
-- TOC entry 5570 (class 0 OID 43353)
-- Dependencies: 299
-- Data for Name: respaldos_sistema; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.respaldos_sistema (id, nombre, descripcion, tipo, ruta_archivo, tamano_bytes, hash_verificacion, fecha_respaldo, fecha_expiracion, estado, puede_restaurar, creado_por, restaurado_por, fecha_restauracion) FROM stdin;
\.


--
-- TOC entry 5496 (class 0 OID 34386)
-- Dependencies: 225
-- Data for Name: rol_permisos; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.rol_permisos (rol_id, permiso_id) FROM stdin;
1	1
1	2
1	3
1	4
1	5
1	6
1	7
1	8
1	9
1	10
1	11
1	12
2	5
2	6
2	7
2	8
2	9
2	10
2	12
3	7
3	8
3	9
3	10
3	11
4	7
4	8
4	10
\.


--
-- TOC entry 5493 (class 0 OID 34369)
-- Dependencies: 222
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.roles (id_rol, nombre_rol, descripcion) FROM stdin;
1	ADMIN	Administrador del sistema con acceso completo
2	VETERINARIO	Veterinario que atiende pacientes
3	SECRETARIO	Secretario que gestiona citas e inventario
4	CLIENTE	Cliente que posee mascotas
\.


--
-- TOC entry 5500 (class 0 OID 34442)
-- Dependencies: 229
-- Data for Name: secretarios; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.secretarios (id_usuario, extension) FROM stdin;
3	101
5	101
8	102
\.


--
-- TOC entry 5524 (class 0 OID 34628)
-- Dependencies: 253
-- Data for Name: servicios; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.servicios (id_servicio, nombre, descripcion, tipo, precio_base, duracion_min, created_by, created_at, updated_by, updated_at) FROM stdin;
1	Consulta general	Evaluación clínica de pacientes	CONSULTA	45000.00	30	\N	\N	\N	\N
2	Vacunación	Aplicación de vacunas caninas/felinas	VACUNACIÓN	60000.00	20	\N	\N	\N	\N
3	Cirugía menor	Procedimientos quirúrgicos ambulatorios	CIRUGÍA	180000.00	90	\N	\N	\N	\N
\.


--
-- TOC entry 5564 (class 0 OID 43283)
-- Dependencies: 293
-- Data for Name: servicios_configuracion; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.servicios_configuracion (id, servicio_id, nombre, descripcion, precio_base, duracion_estimada_minutos, disponible, requiere_cita, color_hex, icono, creado_por, fecha_creacion, modificado_por, fecha_modificacion, activo) FROM stdin;
1	\N	Consulta General	Examen médico completo y diagnóstico	250000.00	30	t	t	#3B82F6	stethoscope	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
2	\N	Vacunación	Aplicación de vacunas y esquema de inmunización	180000.00	20	t	t	#10B981	syringe	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
3	\N	Cirugía	Procedimientos quirúrgicos y atención especializada	450000.00	120	t	t	#EF4444	scissors	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
4	\N	Control	Seguimiento y control post-tratamiento	150000.00	20	t	t	#8B5CF6	clipboard	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
5	\N	Desparasitación	Tratamiento antiparasitario interno y externo	80000.00	15	t	t	#F59E0B	bug	SYSTEM	2025-11-30 14:02:10.86105	\N	\N	t
\.


--
-- TOC entry 5528 (class 0 OID 34662)
-- Dependencies: 257
-- Data for Name: servicios_prestados; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.servicios_prestados (id_prestado, cita_id, servicio_id, fecha_ejecucion, observaciones, costo_total, insumos_consumidos, created_by, created_at, updated_by, updated_at) FROM stdin;
1	1	1	2025-11-08 10:00:00-05	Consulta de control realizada sin complicaciones	45000.00	{"SKU-GASA": 1}	\N	\N	\N	\N
\.


--
-- TOC entry 5558 (class 0 OID 43120)
-- Dependencies: 287
-- Data for Name: solicitudes_citas; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.solicitudes_citas (id_solicitud, cliente_id, paciente_id, fecha_solicitada, hora_solicitada, tipo_servicio, motivo, estado, motivo_rechazo, cita_id, observaciones, created_at, updated_at, created_by, updated_by, aprobado_por, aprobado_en, rechazado_por, rechazado_en, cancelado_por, cancelado_en) FROM stdin;
\.


--
-- TOC entry 5576 (class 0 OID 43458)
-- Dependencies: 305
-- Data for Name: transacciones; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.transacciones (id_transaccion, numero_transaccion, fecha_transaccion, tipo, categoria, factura_id, monto, metodo_pago, referencia_pago, descripcion, concepto, estado, usuario_id, metadatos, created_at, updated_at) FROM stdin;
\.


--
-- TOC entry 5497 (class 0 OID 34401)
-- Dependencies: 226
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.usuarios (id_usuario, username, password_hash, activo, ultimo_acceso, rol_id, password_reset_token, password_reset_token_expiry) FROM stdin;
2	vet_carlos	$2a$10$2b2GZHTCqkfNNPJBWlAbGuY/jGj33jjXNMTvEihQ/HVBuUaz2Ys3S	t	2025-11-26 17:32:26.833755-05	2	\N	\N
3	sec_laura	$2a$10$wN5e7/geXjr6JvLCYOtJk.nK0Os/.xLtEPPYU.GOCa/Br0gS2wPFe	t	2025-11-26 17:32:26.833755-05	3	\N	\N
5	secretario_demo	$2a$10$8z.Ib0VLmQ5Y3zNKJ.fS5ukF8w1q7vH9yYPqJ4gLKjH8pGzPnQgYq	t	\N	3	\N	\N
6	admin2	admin123	t	\N	1	\N	\N
7	vet_maria	veterinario123	t	\N	2	\N	\N
8	sec_pedro	secretario123	t	\N	3	\N	\N
9	cliente_ana	cliente123	t	\N	4	\N	\N
11	martinnez	$2a$10$bY/NVD6eEx6qshlu64vtzOzAUV8ZfHQbohhKiQVg6hd6mln1.QIR.	t	\N	2	\N	\N
13	luisagonza2	$2a$10$MY.mv8tkgZhAR1v1zKJ78uKXjJrUMVcTNbqfL79yKMA1gAYF.zBHS	t	2025-11-30 11:32:38.366672-05	4	\N	\N
4	cliente_diego	$2a$10$A8EjqCOUMIqFZ3VAb9Y/jOb0IhiHBpjz2uVH54camzatoNgtrENcO	t	2025-11-26 17:32:26.833755-05	4	\N	\N
12	jMendez34	$2a$10$bYOzZEUcofXuRZtBPKXd6uAHmIUbE48Ulla8/Amb79cIQi8.f.KNu	t	2025-11-30 15:47:23.503128-05	2	\N	\N
10	lauvel	$2a$10$bDYVHMrQsALlQvEe9mlxVOVapw0S1m18PxVJPKWyq8LnWFhdmq.Sy	t	2025-11-30 15:48:10.743609-05	3	\N	\N
1	admin	$2a$10$FbWIi1RD.XJO0Wgth56fL.wwlPLQmpBhvFRzBkkmwomAyD0pzOZOC	t	2025-11-30 15:55:03.190563-05	1	\N	\N
\.


--
-- TOC entry 5499 (class 0 OID 34430)
-- Dependencies: 228
-- Data for Name: usuarios_veterinarios; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.usuarios_veterinarios (id_usuario, licencia_profesional, especialidad, disponibilidad) FROM stdin;
2	MVZ-12345	Consulta General	{"lunes": [{"fin": "13:00", "inicio": "09:00"}], "miércoles": [{"fin": "18:00", "inicio": "14:00"}]}
7	MVZ-12345	Cirugía Veterinaria	{"lunes": [{"fin": "17:00", "inicio": "09:00"}], "martes": [{"fin": "17:00", "inicio": "09:00"}]}
\.


--
-- TOC entry 5510 (class 0 OID 34517)
-- Dependencies: 239
-- Data for Name: vacunaciones; Type: TABLE DATA; Schema: public; Owner: vet_admin
--

COPY public.vacunaciones (id_vacunacion, paciente_id, tipo_vacuna, fecha_aplicacion, proxima_dosis, veterinario_id, created_by, created_at, updated_by, updated_at) FROM stdin;
1	1	Rabia	2025-01-10	2026-01-10	2	\N	\N	\N	\N
\.


--
-- TOC entry 5647 (class 0 OID 0)
-- Dependencies: 250
-- Name: alertas_inventario_id_alerta_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.alertas_inventario_id_alerta_seq', 1, true);


--
-- TOC entry 5648 (class 0 OID 0)
-- Dependencies: 296
-- Name: auditoria_detallada_id_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.auditoria_detallada_id_seq', 1, false);


--
-- TOC entry 5649 (class 0 OID 0)
-- Dependencies: 274
-- Name: backups_sistema_id_backup_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.backups_sistema_id_backup_seq', 1, true);


--
-- TOC entry 5650 (class 0 OID 0)
-- Dependencies: 260
-- Name: canales_envio_id_canal_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.canales_envio_id_canal_seq', 3, true);


--
-- TOC entry 5651 (class 0 OID 0)
-- Dependencies: 254
-- Name: citas_id_cita_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.citas_id_cita_seq', 2, true);


--
-- TOC entry 5652 (class 0 OID 0)
-- Dependencies: 300
-- Name: configuracion_avanzada_id_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.configuracion_avanzada_id_seq', 10, true);


--
-- TOC entry 5653 (class 0 OID 0)
-- Dependencies: 240
-- Name: desparasitaciones_id_desparasitacion_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.desparasitaciones_id_desparasitacion_seq', 1, true);


--
-- TOC entry 5654 (class 0 OID 0)
-- Dependencies: 266
-- Name: destinatarios_id_destinatario_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.destinatarios_id_destinatario_seq', 1, true);


--
-- TOC entry 5655 (class 0 OID 0)
-- Dependencies: 302
-- Name: detalle_factura_id_detalle_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.detalle_factura_id_detalle_seq', 1, false);


--
-- TOC entry 5656 (class 0 OID 0)
-- Dependencies: 280
-- Name: estadisticas_id_estadistica_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.estadisticas_id_estadistica_seq', 1, true);


--
-- TOC entry 5657 (class 0 OID 0)
-- Dependencies: 258
-- Name: facturas_id_factura_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.facturas_id_factura_seq', 1, true);


--
-- TOC entry 5658 (class 0 OID 0)
-- Dependencies: 284
-- Name: fuentes_datos_id_fuente_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.fuentes_datos_id_fuente_seq', 1, true);


--
-- TOC entry 5659 (class 0 OID 0)
-- Dependencies: 230
-- Name: historial_acciones_id_accion_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.historial_acciones_id_accion_seq', 4, true);


--
-- TOC entry 5660 (class 0 OID 0)
-- Dependencies: 234
-- Name: historias_clinicas_id_historia_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.historias_clinicas_id_historia_seq', 1, true);


--
-- TOC entry 5661 (class 0 OID 0)
-- Dependencies: 294
-- Name: horarios_atencion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.horarios_atencion_id_seq', 7, true);


--
-- TOC entry 5662 (class 0 OID 0)
-- Dependencies: 282
-- Name: indicadores_id_indicador_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.indicadores_id_indicador_seq', 1, true);


--
-- TOC entry 5663 (class 0 OID 0)
-- Dependencies: 288
-- Name: informacion_clinica_id_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.informacion_clinica_id_seq', 1, true);


--
-- TOC entry 5664 (class 0 OID 0)
-- Dependencies: 276
-- Name: logs_sistema_id_log_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.logs_sistema_id_log_seq', 1, true);


--
-- TOC entry 5665 (class 0 OID 0)
-- Dependencies: 246
-- Name: lotes_id_lote_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.lotes_id_lote_seq', 1, true);


--
-- TOC entry 5666 (class 0 OID 0)
-- Dependencies: 248
-- Name: movimientos_inventario_id_movimiento_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.movimientos_inventario_id_movimiento_seq', 1, true);


--
-- TOC entry 5667 (class 0 OID 0)
-- Dependencies: 264
-- Name: notificaciones_id_notificacion_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.notificaciones_id_notificacion_seq', 2, true);


--
-- TOC entry 5668 (class 0 OID 0)
-- Dependencies: 232
-- Name: pacientes_id_paciente_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.pacientes_id_paciente_seq', 1, true);


--
-- TOC entry 5669 (class 0 OID 0)
-- Dependencies: 272
-- Name: parametros_sistema_id_parametro_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.parametros_sistema_id_parametro_seq', 4, true);


--
-- TOC entry 5670 (class 0 OID 0)
-- Dependencies: 223
-- Name: permisos_id_permiso_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.permisos_id_permiso_seq', 12, true);


--
-- TOC entry 5671 (class 0 OID 0)
-- Dependencies: 290
-- Name: permisos_rol_id_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.permisos_rol_id_seq', 30, true);


--
-- TOC entry 5672 (class 0 OID 0)
-- Dependencies: 219
-- Name: personas_id_persona_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.personas_id_persona_seq', 28, true);


--
-- TOC entry 5673 (class 0 OID 0)
-- Dependencies: 262
-- Name: plantillas_mensajes_id_plantilla_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.plantillas_mensajes_id_plantilla_seq', 2, true);


--
-- TOC entry 5674 (class 0 OID 0)
-- Dependencies: 244
-- Name: productos_id_producto_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.productos_id_producto_seq', 3, true);


--
-- TOC entry 5675 (class 0 OID 0)
-- Dependencies: 242
-- Name: proveedores_id_proveedor_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.proveedores_id_proveedor_seq', 2, true);


--
-- TOC entry 5676 (class 0 OID 0)
-- Dependencies: 236
-- Name: registros_medicos_id_registro_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.registros_medicos_id_registro_seq', 1, true);


--
-- TOC entry 5677 (class 0 OID 0)
-- Dependencies: 278
-- Name: reportes_id_reporte_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.reportes_id_reporte_seq', 1, true);


--
-- TOC entry 5678 (class 0 OID 0)
-- Dependencies: 298
-- Name: respaldos_sistema_id_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.respaldos_sistema_id_seq', 1, false);


--
-- TOC entry 5679 (class 0 OID 0)
-- Dependencies: 221
-- Name: roles_id_rol_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.roles_id_rol_seq', 4, true);


--
-- TOC entry 5680 (class 0 OID 0)
-- Dependencies: 292
-- Name: servicios_configuracion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.servicios_configuracion_id_seq', 5, true);


--
-- TOC entry 5681 (class 0 OID 0)
-- Dependencies: 252
-- Name: servicios_id_servicio_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.servicios_id_servicio_seq', 3, true);


--
-- TOC entry 5682 (class 0 OID 0)
-- Dependencies: 256
-- Name: servicios_prestados_id_prestado_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.servicios_prestados_id_prestado_seq', 1, true);


--
-- TOC entry 5683 (class 0 OID 0)
-- Dependencies: 286
-- Name: solicitudes_citas_id_solicitud_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.solicitudes_citas_id_solicitud_seq', 1, false);


--
-- TOC entry 5684 (class 0 OID 0)
-- Dependencies: 304
-- Name: transacciones_id_transaccion_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.transacciones_id_transaccion_seq', 1, false);


--
-- TOC entry 5685 (class 0 OID 0)
-- Dependencies: 238
-- Name: vacunaciones_id_vacunacion_seq; Type: SEQUENCE SET; Schema: public; Owner: vet_admin
--

SELECT pg_catalog.setval('public.vacunaciones_id_vacunacion_seq', 1, true);


--
-- TOC entry 5160 (class 2606 OID 34621)
-- Name: alertas_inventario alertas_inventario_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.alertas_inventario
    ADD CONSTRAINT alertas_inventario_pkey PRIMARY KEY (id_alerta);


--
-- TOC entry 5263 (class 2606 OID 43335)
-- Name: auditoria_detallada auditoria_detallada_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.auditoria_detallada
    ADD CONSTRAINT auditoria_detallada_pkey PRIMARY KEY (id);


--
-- TOC entry 5211 (class 2606 OID 34812)
-- Name: backups_sistema backups_sistema_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.backups_sistema
    ADD CONSTRAINT backups_sistema_pkey PRIMARY KEY (id_backup);


--
-- TOC entry 5205 (class 2606 OID 34786)
-- Name: canales_app canales_app_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.canales_app
    ADD CONSTRAINT canales_app_pkey PRIMARY KEY (id_canal);


--
-- TOC entry 5201 (class 2606 OID 34766)
-- Name: canales_email canales_email_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.canales_email
    ADD CONSTRAINT canales_email_pkey PRIMARY KEY (id_canal);


--
-- TOC entry 5187 (class 2606 OID 34712)
-- Name: canales_envio canales_envio_nombre_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.canales_envio
    ADD CONSTRAINT canales_envio_nombre_key UNIQUE (nombre);


--
-- TOC entry 5189 (class 2606 OID 34710)
-- Name: canales_envio canales_envio_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.canales_envio
    ADD CONSTRAINT canales_envio_pkey PRIMARY KEY (id_canal);


--
-- TOC entry 5203 (class 2606 OID 34776)
-- Name: canales_sms canales_sms_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.canales_sms
    ADD CONSTRAINT canales_sms_pkey PRIMARY KEY (id_canal);


--
-- TOC entry 5166 (class 2606 OID 34649)
-- Name: citas citas_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.citas
    ADD CONSTRAINT citas_pkey PRIMARY KEY (id_cita);


--
-- TOC entry 5121 (class 2606 OID 34424)
-- Name: clientes clientes_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.clientes
    ADD CONSTRAINT clientes_pkey PRIMARY KEY (id_usuario);


--
-- TOC entry 5277 (class 2606 OID 43384)
-- Name: configuracion_avanzada configuracion_avanzada_clave_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.configuracion_avanzada
    ADD CONSTRAINT configuracion_avanzada_clave_key UNIQUE (clave);


--
-- TOC entry 5279 (class 2606 OID 43382)
-- Name: configuracion_avanzada configuracion_avanzada_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.configuracion_avanzada
    ADD CONSTRAINT configuracion_avanzada_pkey PRIMARY KEY (id);


--
-- TOC entry 5141 (class 2606 OID 34539)
-- Name: desparasitaciones desparasitaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.desparasitaciones
    ADD CONSTRAINT desparasitaciones_pkey PRIMARY KEY (id_desparasitacion);


--
-- TOC entry 5197 (class 2606 OID 34746)
-- Name: destinatarios destinatarios_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.destinatarios
    ADD CONSTRAINT destinatarios_pkey PRIMARY KEY (id_destinatario);


--
-- TOC entry 5283 (class 2606 OID 43444)
-- Name: detalle_factura detalle_factura_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.detalle_factura
    ADD CONSTRAINT detalle_factura_pkey PRIMARY KEY (id_detalle);


--
-- TOC entry 5222 (class 2606 OID 34851)
-- Name: estadisticas estadisticas_nombre_periodo_inicio_periodo_fin_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.estadisticas
    ADD CONSTRAINT estadisticas_nombre_periodo_inicio_periodo_fin_key UNIQUE (nombre, periodo_inicio, periodo_fin);


--
-- TOC entry 5224 (class 2606 OID 34849)
-- Name: estadisticas estadisticas_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.estadisticas
    ADD CONSTRAINT estadisticas_pkey PRIMARY KEY (id_estadistica);


--
-- TOC entry 5177 (class 2606 OID 34696)
-- Name: facturas facturas_numero_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.facturas
    ADD CONSTRAINT facturas_numero_key UNIQUE (numero_factura);


--
-- TOC entry 5179 (class 2606 OID 34694)
-- Name: facturas facturas_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.facturas
    ADD CONSTRAINT facturas_pkey PRIMARY KEY (id_factura);


--
-- TOC entry 5098 (class 2606 OID 34355)
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- TOC entry 5230 (class 2606 OID 34873)
-- Name: fuentes_datos fuentes_datos_nombre_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.fuentes_datos
    ADD CONSTRAINT fuentes_datos_nombre_key UNIQUE (nombre);


--
-- TOC entry 5232 (class 2606 OID 34871)
-- Name: fuentes_datos fuentes_datos_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.fuentes_datos
    ADD CONSTRAINT fuentes_datos_pkey PRIMARY KEY (id_fuente);


--
-- TOC entry 5127 (class 2606 OID 34461)
-- Name: historial_acciones historial_acciones_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.historial_acciones
    ADD CONSTRAINT historial_acciones_pkey PRIMARY KEY (id_accion);


--
-- TOC entry 5135 (class 2606 OID 34490)
-- Name: historias_clinicas historias_clinicas_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.historias_clinicas
    ADD CONSTRAINT historias_clinicas_pkey PRIMARY KEY (id_historia);


--
-- TOC entry 5257 (class 2606 OID 43318)
-- Name: horarios_atencion horarios_atencion_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.horarios_atencion
    ADD CONSTRAINT horarios_atencion_pkey PRIMARY KEY (id);


--
-- TOC entry 5226 (class 2606 OID 34862)
-- Name: indicadores indicadores_nombre_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.indicadores
    ADD CONSTRAINT indicadores_nombre_key UNIQUE (nombre);


--
-- TOC entry 5228 (class 2606 OID 34860)
-- Name: indicadores indicadores_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.indicadores
    ADD CONSTRAINT indicadores_pkey PRIMARY KEY (id_indicador);


--
-- TOC entry 5241 (class 2606 OID 43256)
-- Name: informacion_clinica informacion_clinica_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.informacion_clinica
    ADD CONSTRAINT informacion_clinica_pkey PRIMARY KEY (id);


--
-- TOC entry 5216 (class 2606 OID 34822)
-- Name: logs_sistema logs_sistema_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.logs_sistema
    ADD CONSTRAINT logs_sistema_pkey PRIMARY KEY (id_log);


--
-- TOC entry 5154 (class 2606 OID 34582)
-- Name: lotes lotes_numero_lote_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.lotes
    ADD CONSTRAINT lotes_numero_lote_key UNIQUE (numero_lote);


--
-- TOC entry 5156 (class 2606 OID 34580)
-- Name: lotes lotes_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.lotes
    ADD CONSTRAINT lotes_pkey PRIMARY KEY (id_lote);


--
-- TOC entry 5158 (class 2606 OID 34596)
-- Name: movimientos_inventario movimientos_inventario_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.movimientos_inventario
    ADD CONSTRAINT movimientos_inventario_pkey PRIMARY KEY (id_movimiento);


--
-- TOC entry 5199 (class 2606 OID 34751)
-- Name: notificacion_destinatarios notificacion_destinatarios_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.notificacion_destinatarios
    ADD CONSTRAINT notificacion_destinatarios_pkey PRIMARY KEY (notificacion_id, destinatario_id);


--
-- TOC entry 5195 (class 2606 OID 34734)
-- Name: notificaciones notificaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.notificaciones
    ADD CONSTRAINT notificaciones_pkey PRIMARY KEY (id_notificacion);


--
-- TOC entry 5131 (class 2606 OID 34475)
-- Name: pacientes pacientes_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.pacientes
    ADD CONSTRAINT pacientes_pkey PRIMARY KEY (id_paciente);


--
-- TOC entry 5207 (class 2606 OID 34802)
-- Name: parametros_sistema parametros_sistema_clave_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.parametros_sistema
    ADD CONSTRAINT parametros_sistema_clave_key UNIQUE (clave);


--
-- TOC entry 5209 (class 2606 OID 34800)
-- Name: parametros_sistema parametros_sistema_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.parametros_sistema
    ADD CONSTRAINT parametros_sistema_pkey PRIMARY KEY (id_parametro);


--
-- TOC entry 5110 (class 2606 OID 34385)
-- Name: permisos permisos_nombre_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.permisos
    ADD CONSTRAINT permisos_nombre_key UNIQUE (nombre);


--
-- TOC entry 5112 (class 2606 OID 34383)
-- Name: permisos permisos_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.permisos
    ADD CONSTRAINT permisos_pkey PRIMARY KEY (id_permiso);


--
-- TOC entry 5248 (class 2606 OID 43271)
-- Name: permisos_rol permisos_rol_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.permisos_rol
    ADD CONSTRAINT permisos_rol_pkey PRIMARY KEY (id);


--
-- TOC entry 5102 (class 2606 OID 34367)
-- Name: personas personas_correo_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.personas
    ADD CONSTRAINT personas_correo_key UNIQUE (correo);


--
-- TOC entry 5104 (class 2606 OID 34365)
-- Name: personas personas_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.personas
    ADD CONSTRAINT personas_pkey PRIMARY KEY (id_persona);


--
-- TOC entry 5191 (class 2606 OID 34723)
-- Name: plantillas_mensajes plantillas_mensajes_nombre_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.plantillas_mensajes
    ADD CONSTRAINT plantillas_mensajes_nombre_key UNIQUE (nombre);


--
-- TOC entry 5193 (class 2606 OID 34721)
-- Name: plantillas_mensajes plantillas_mensajes_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.plantillas_mensajes
    ADD CONSTRAINT plantillas_mensajes_pkey PRIMARY KEY (id_plantilla);


--
-- TOC entry 5150 (class 2606 OID 34567)
-- Name: productos productos_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.productos
    ADD CONSTRAINT productos_pkey PRIMARY KEY (id_producto);


--
-- TOC entry 5152 (class 2606 OID 34569)
-- Name: productos productos_sku_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.productos
    ADD CONSTRAINT productos_sku_key UNIQUE (sku);


--
-- TOC entry 5143 (class 2606 OID 34555)
-- Name: proveedores proveedores_correo_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.proveedores
    ADD CONSTRAINT proveedores_correo_key UNIQUE (correo);


--
-- TOC entry 5145 (class 2606 OID 34553)
-- Name: proveedores proveedores_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.proveedores
    ADD CONSTRAINT proveedores_pkey PRIMARY KEY (id_proveedor);


--
-- TOC entry 5137 (class 2606 OID 34505)
-- Name: registros_medicos registros_medicos_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.registros_medicos
    ADD CONSTRAINT registros_medicos_pkey PRIMARY KEY (id_registro);


--
-- TOC entry 5218 (class 2606 OID 34837)
-- Name: reportes reportes_nombre_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.reportes
    ADD CONSTRAINT reportes_nombre_key UNIQUE (nombre);


--
-- TOC entry 5220 (class 2606 OID 34835)
-- Name: reportes reportes_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.reportes
    ADD CONSTRAINT reportes_pkey PRIMARY KEY (id_reporte);


--
-- TOC entry 5275 (class 2606 OID 43365)
-- Name: respaldos_sistema respaldos_sistema_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.respaldos_sistema
    ADD CONSTRAINT respaldos_sistema_pkey PRIMARY KEY (id);


--
-- TOC entry 5114 (class 2606 OID 34390)
-- Name: rol_permisos rol_permisos_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.rol_permisos
    ADD CONSTRAINT rol_permisos_pkey PRIMARY KEY (rol_id, permiso_id);


--
-- TOC entry 5106 (class 2606 OID 34376)
-- Name: roles roles_nombre_rol_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_nombre_rol_key UNIQUE (nombre_rol);


--
-- TOC entry 5108 (class 2606 OID 34374)
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id_rol);


--
-- TOC entry 5125 (class 2606 OID 34446)
-- Name: secretarios secretarios_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.secretarios
    ADD CONSTRAINT secretarios_pkey PRIMARY KEY (id_usuario);


--
-- TOC entry 5255 (class 2606 OID 43296)
-- Name: servicios_configuracion servicios_configuracion_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.servicios_configuracion
    ADD CONSTRAINT servicios_configuracion_pkey PRIMARY KEY (id);


--
-- TOC entry 5162 (class 2606 OID 34638)
-- Name: servicios servicios_nombre_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.servicios
    ADD CONSTRAINT servicios_nombre_key UNIQUE (nombre);


--
-- TOC entry 5164 (class 2606 OID 34636)
-- Name: servicios servicios_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.servicios
    ADD CONSTRAINT servicios_pkey PRIMARY KEY (id_servicio);


--
-- TOC entry 5175 (class 2606 OID 34671)
-- Name: servicios_prestados servicios_prestados_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.servicios_prestados
    ADD CONSTRAINT servicios_prestados_pkey PRIMARY KEY (id_prestado);


--
-- TOC entry 5238 (class 2606 OID 43130)
-- Name: solicitudes_citas solicitudes_citas_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.solicitudes_citas
    ADD CONSTRAINT solicitudes_citas_pkey PRIMARY KEY (id_solicitud);


--
-- TOC entry 5293 (class 2606 OID 43475)
-- Name: transacciones transacciones_numero_transaccion_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.transacciones
    ADD CONSTRAINT transacciones_numero_transaccion_key UNIQUE (numero_transaccion);


--
-- TOC entry 5295 (class 2606 OID 43473)
-- Name: transacciones transacciones_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.transacciones
    ADD CONSTRAINT transacciones_pkey PRIMARY KEY (id_transaccion);


--
-- TOC entry 5133 (class 2606 OID 43388)
-- Name: pacientes uk_pacientes_identificador_externo; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.pacientes
    ADD CONSTRAINT uk_pacientes_identificador_externo UNIQUE (identificador_externo);


--
-- TOC entry 5261 (class 2606 OID 43320)
-- Name: horarios_atencion uq_horarios_atencion_dia; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.horarios_atencion
    ADD CONSTRAINT uq_horarios_atencion_dia UNIQUE (dia_semana, activo);


--
-- TOC entry 5243 (class 2606 OID 43258)
-- Name: informacion_clinica uq_informacion_clinica_activo; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.informacion_clinica
    ADD CONSTRAINT uq_informacion_clinica_activo UNIQUE (activo);


--
-- TOC entry 5250 (class 2606 OID 43273)
-- Name: permisos_rol uq_permisos_rol_modulo_accion; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.permisos_rol
    ADD CONSTRAINT uq_permisos_rol_modulo_accion UNIQUE (rol_id, modulo, accion);


--
-- TOC entry 5117 (class 2606 OID 34406)
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id_usuario);


--
-- TOC entry 5119 (class 2606 OID 34408)
-- Name: usuarios usuarios_username_key; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_username_key UNIQUE (username);


--
-- TOC entry 5123 (class 2606 OID 34436)
-- Name: usuarios_veterinarios usuarios_veterinarios_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.usuarios_veterinarios
    ADD CONSTRAINT usuarios_veterinarios_pkey PRIMARY KEY (id_usuario);


--
-- TOC entry 5139 (class 2606 OID 34522)
-- Name: vacunaciones vacunaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.vacunaciones
    ADD CONSTRAINT vacunaciones_pkey PRIMARY KEY (id_vacunacion);


--
-- TOC entry 5099 (class 1259 OID 34356)
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- TOC entry 5264 (class 1259 OID 43349)
-- Name: idx_auditoria_entidad; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_auditoria_entidad ON public.auditoria_detallada USING btree (entidad, entidad_id);


--
-- TOC entry 5265 (class 1259 OID 43350)
-- Name: idx_auditoria_fecha; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_auditoria_fecha ON public.auditoria_detallada USING btree (fecha_accion DESC);


--
-- TOC entry 5266 (class 1259 OID 43348)
-- Name: idx_auditoria_modulo; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_auditoria_modulo ON public.auditoria_detallada USING btree (modulo);


--
-- TOC entry 5267 (class 1259 OID 43351)
-- Name: idx_auditoria_relevancia; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_auditoria_relevancia ON public.auditoria_detallada USING btree (relevancia);


--
-- TOC entry 5268 (class 1259 OID 43347)
-- Name: idx_auditoria_rol; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_auditoria_rol ON public.auditoria_detallada USING btree (rol_nombre);


--
-- TOC entry 5269 (class 1259 OID 43346)
-- Name: idx_auditoria_usuario; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_auditoria_usuario ON public.auditoria_detallada USING btree (usuario_id);


--
-- TOC entry 5167 (class 1259 OID 34919)
-- Name: idx_citas_created_at; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_citas_created_at ON public.citas USING btree (created_at);


--
-- TOC entry 5168 (class 1259 OID 43151)
-- Name: idx_citas_estado; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_citas_estado ON public.citas USING btree (estado);


--
-- TOC entry 5169 (class 1259 OID 43154)
-- Name: idx_citas_fecha_hora; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_citas_fecha_hora ON public.citas USING btree (fecha_hora DESC);


--
-- TOC entry 5170 (class 1259 OID 43152)
-- Name: idx_citas_paciente_estado; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_citas_paciente_estado ON public.citas USING btree (paciente_id, estado);


--
-- TOC entry 5171 (class 1259 OID 43153)
-- Name: idx_citas_vet_estado_fecha; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_citas_vet_estado_fecha ON public.citas USING btree (veterinario_id, estado, fecha_hora DESC);


--
-- TOC entry 5172 (class 1259 OID 34660)
-- Name: idx_citas_veterinario_fecha; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE UNIQUE INDEX idx_citas_veterinario_fecha ON public.citas USING btree (veterinario_id, fecha_hora) WHERE ((estado)::text = 'PROGRAMADA'::text);


--
-- TOC entry 5173 (class 1259 OID 43150)
-- Name: idx_citas_veterinario_fecha_unique; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE UNIQUE INDEX idx_citas_veterinario_fecha_unique ON public.citas USING btree (veterinario_id, fecha_hora) WHERE ((estado)::text = 'PROGRAMADA'::text);


--
-- TOC entry 5686 (class 0 OID 0)
-- Dependencies: 5173
-- Name: INDEX idx_citas_veterinario_fecha_unique; Type: COMMENT; Schema: public; Owner: vet_admin
--

COMMENT ON INDEX public.idx_citas_veterinario_fecha_unique IS 'Previene race conditions al agendar citas. Garantiza que un veterinario no tenga dos citas programadas en la misma fecha/hora.';


--
-- TOC entry 5280 (class 1259 OID 43385)
-- Name: idx_config_avanzada_categoria; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_config_avanzada_categoria ON public.configuracion_avanzada USING btree (categoria);


--
-- TOC entry 5281 (class 1259 OID 43386)
-- Name: idx_config_avanzada_editable; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_config_avanzada_editable ON public.configuracion_avanzada USING btree (editable);


--
-- TOC entry 5284 (class 1259 OID 43455)
-- Name: idx_detalle_factura_factura; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_detalle_factura_factura ON public.detalle_factura USING btree (factura_id);


--
-- TOC entry 5285 (class 1259 OID 43456)
-- Name: idx_detalle_factura_producto; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_detalle_factura_producto ON public.detalle_factura USING btree (producto_id);


--
-- TOC entry 5180 (class 1259 OID 43427)
-- Name: idx_facturas_cliente; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_facturas_cliente ON public.facturas USING btree (cliente_id);


--
-- TOC entry 5181 (class 1259 OID 34920)
-- Name: idx_facturas_created_at; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_facturas_created_at ON public.facturas USING btree (created_at);


--
-- TOC entry 5182 (class 1259 OID 43428)
-- Name: idx_facturas_estado; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_facturas_estado ON public.facturas USING btree (estado);


--
-- TOC entry 5183 (class 1259 OID 43429)
-- Name: idx_facturas_fecha_emision; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_facturas_fecha_emision ON public.facturas USING btree (fecha_emision DESC);


--
-- TOC entry 5184 (class 1259 OID 43426)
-- Name: idx_facturas_numero; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_facturas_numero ON public.facturas USING btree (numero_factura);


--
-- TOC entry 5185 (class 1259 OID 43430)
-- Name: idx_facturas_servicio; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_facturas_servicio ON public.facturas USING btree (servicio_prestado_id);


--
-- TOC entry 5258 (class 1259 OID 43322)
-- Name: idx_horarios_atencion_activo; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_horarios_atencion_activo ON public.horarios_atencion USING btree (activo);


--
-- TOC entry 5259 (class 1259 OID 43321)
-- Name: idx_horarios_atencion_dia; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_horarios_atencion_dia ON public.horarios_atencion USING btree (dia_semana);


--
-- TOC entry 5239 (class 1259 OID 43259)
-- Name: idx_informacion_clinica_activo; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_informacion_clinica_activo ON public.informacion_clinica USING btree (activo);


--
-- TOC entry 5212 (class 1259 OID 34824)
-- Name: idx_logs_componente; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_logs_componente ON public.logs_sistema USING btree (componente);


--
-- TOC entry 5213 (class 1259 OID 34825)
-- Name: idx_logs_fecha; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_logs_fecha ON public.logs_sistema USING btree (fecha_hora);


--
-- TOC entry 5214 (class 1259 OID 34823)
-- Name: idx_logs_nivel; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_logs_nivel ON public.logs_sistema USING btree (nivel);


--
-- TOC entry 5128 (class 1259 OID 34918)
-- Name: idx_pacientes_created_at; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_pacientes_created_at ON public.pacientes USING btree (created_at);


--
-- TOC entry 5129 (class 1259 OID 43389)
-- Name: idx_pacientes_identificador_externo; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_pacientes_identificador_externo ON public.pacientes USING btree (identificador_externo);


--
-- TOC entry 5244 (class 1259 OID 43281)
-- Name: idx_permisos_rol_activo; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_permisos_rol_activo ON public.permisos_rol USING btree (activo);


--
-- TOC entry 5245 (class 1259 OID 43280)
-- Name: idx_permisos_rol_modulo; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_permisos_rol_modulo ON public.permisos_rol USING btree (modulo);


--
-- TOC entry 5246 (class 1259 OID 43279)
-- Name: idx_permisos_rol_rol_id; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_permisos_rol_rol_id ON public.permisos_rol USING btree (rol_id);


--
-- TOC entry 5100 (class 1259 OID 34917)
-- Name: idx_personas_created_at; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_personas_created_at ON public.personas USING btree (created_at);


--
-- TOC entry 5146 (class 1259 OID 34921)
-- Name: idx_productos_created_at; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_productos_created_at ON public.productos USING btree (created_at);


--
-- TOC entry 5147 (class 1259 OID 34570)
-- Name: idx_productos_nombre; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_productos_nombre ON public.productos USING btree (nombre);


--
-- TOC entry 5148 (class 1259 OID 34571)
-- Name: idx_productos_tipo; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_productos_tipo ON public.productos USING btree (tipo);


--
-- TOC entry 5270 (class 1259 OID 43368)
-- Name: idx_respaldos_estado; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_respaldos_estado ON public.respaldos_sistema USING btree (estado);


--
-- TOC entry 5271 (class 1259 OID 43367)
-- Name: idx_respaldos_fecha; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_respaldos_fecha ON public.respaldos_sistema USING btree (fecha_respaldo DESC);


--
-- TOC entry 5272 (class 1259 OID 43369)
-- Name: idx_respaldos_puede_restaurar; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_respaldos_puede_restaurar ON public.respaldos_sistema USING btree (puede_restaurar);


--
-- TOC entry 5273 (class 1259 OID 43366)
-- Name: idx_respaldos_tipo; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_respaldos_tipo ON public.respaldos_sistema USING btree (tipo);


--
-- TOC entry 5251 (class 1259 OID 43303)
-- Name: idx_servicios_config_activo; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_servicios_config_activo ON public.servicios_configuracion USING btree (activo);


--
-- TOC entry 5252 (class 1259 OID 43302)
-- Name: idx_servicios_config_disponible; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_servicios_config_disponible ON public.servicios_configuracion USING btree (disponible);


--
-- TOC entry 5253 (class 1259 OID 43304)
-- Name: idx_servicios_config_servicio_id; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_servicios_config_servicio_id ON public.servicios_configuracion USING btree (servicio_id);


--
-- TOC entry 5233 (class 1259 OID 43146)
-- Name: idx_solicitudes_cliente; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_solicitudes_cliente ON public.solicitudes_citas USING btree (cliente_id);


--
-- TOC entry 5234 (class 1259 OID 43148)
-- Name: idx_solicitudes_estado; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_solicitudes_estado ON public.solicitudes_citas USING btree (estado);


--
-- TOC entry 5235 (class 1259 OID 43149)
-- Name: idx_solicitudes_fecha; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_solicitudes_fecha ON public.solicitudes_citas USING btree (fecha_solicitada);


--
-- TOC entry 5236 (class 1259 OID 43147)
-- Name: idx_solicitudes_paciente; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_solicitudes_paciente ON public.solicitudes_citas USING btree (paciente_id);


--
-- TOC entry 5286 (class 1259 OID 43490)
-- Name: idx_transacciones_estado; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_transacciones_estado ON public.transacciones USING btree (estado);


--
-- TOC entry 5287 (class 1259 OID 43489)
-- Name: idx_transacciones_factura; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_transacciones_factura ON public.transacciones USING btree (factura_id);


--
-- TOC entry 5288 (class 1259 OID 43488)
-- Name: idx_transacciones_fecha; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_transacciones_fecha ON public.transacciones USING btree (fecha_transaccion DESC);


--
-- TOC entry 5289 (class 1259 OID 43491)
-- Name: idx_transacciones_metodo_pago; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_transacciones_metodo_pago ON public.transacciones USING btree (metodo_pago);


--
-- TOC entry 5290 (class 1259 OID 43486)
-- Name: idx_transacciones_numero; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_transacciones_numero ON public.transacciones USING btree (numero_transaccion);


--
-- TOC entry 5291 (class 1259 OID 43487)
-- Name: idx_transacciones_tipo; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_transacciones_tipo ON public.transacciones USING btree (tipo);


--
-- TOC entry 5115 (class 1259 OID 34876)
-- Name: idx_usuarios_password_reset_token; Type: INDEX; Schema: public; Owner: vet_admin
--

CREATE INDEX idx_usuarios_password_reset_token ON public.usuarios USING btree (password_reset_token) WHERE (password_reset_token IS NOT NULL);


--
-- TOC entry 5342 (class 2620 OID 43497)
-- Name: transacciones trigger_actualizar_factura_after_pago; Type: TRIGGER; Schema: public; Owner: vet_admin
--

CREATE TRIGGER trigger_actualizar_factura_after_pago AFTER INSERT ON public.transacciones FOR EACH ROW EXECUTE FUNCTION public.actualizar_estado_factura_after_pago();


--
-- TOC entry 5341 (class 2620 OID 43493)
-- Name: facturas trigger_update_facturas_timestamp; Type: TRIGGER; Schema: public; Owner: vet_admin
--

CREATE TRIGGER trigger_update_facturas_timestamp BEFORE UPDATE ON public.facturas FOR EACH ROW EXECUTE FUNCTION public.update_facturas_timestamp();


--
-- TOC entry 5343 (class 2620 OID 43495)
-- Name: transacciones trigger_update_transacciones_timestamp; Type: TRIGGER; Schema: public; Owner: vet_admin
--

CREATE TRIGGER trigger_update_transacciones_timestamp BEFORE UPDATE ON public.transacciones FOR EACH ROW EXECUTE FUNCTION public.update_transacciones_timestamp();


--
-- TOC entry 5315 (class 2606 OID 34622)
-- Name: alertas_inventario alertas_inventario_producto_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.alertas_inventario
    ADD CONSTRAINT alertas_inventario_producto_id_fkey FOREIGN KEY (producto_id) REFERENCES public.productos(id_producto) ON DELETE CASCADE;


--
-- TOC entry 5335 (class 2606 OID 43336)
-- Name: auditoria_detallada auditoria_detallada_historial_accion_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.auditoria_detallada
    ADD CONSTRAINT auditoria_detallada_historial_accion_id_fkey FOREIGN KEY (historial_accion_id) REFERENCES public.historial_acciones(id_accion) ON DELETE CASCADE;


--
-- TOC entry 5336 (class 2606 OID 43341)
-- Name: auditoria_detallada auditoria_detallada_usuario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.auditoria_detallada
    ADD CONSTRAINT auditoria_detallada_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id_usuario) ON DELETE CASCADE;


--
-- TOC entry 5328 (class 2606 OID 34787)
-- Name: canales_app canales_app_id_canal_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.canales_app
    ADD CONSTRAINT canales_app_id_canal_fkey FOREIGN KEY (id_canal) REFERENCES public.canales_envio(id_canal) ON DELETE CASCADE;


--
-- TOC entry 5326 (class 2606 OID 34767)
-- Name: canales_email canales_email_id_canal_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.canales_email
    ADD CONSTRAINT canales_email_id_canal_fkey FOREIGN KEY (id_canal) REFERENCES public.canales_envio(id_canal) ON DELETE CASCADE;


--
-- TOC entry 5327 (class 2606 OID 34777)
-- Name: canales_sms canales_sms_id_canal_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.canales_sms
    ADD CONSTRAINT canales_sms_id_canal_fkey FOREIGN KEY (id_canal) REFERENCES public.canales_envio(id_canal) ON DELETE CASCADE;


--
-- TOC entry 5316 (class 2606 OID 34650)
-- Name: citas citas_paciente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.citas
    ADD CONSTRAINT citas_paciente_id_fkey FOREIGN KEY (paciente_id) REFERENCES public.pacientes(id_paciente) ON DELETE CASCADE;


--
-- TOC entry 5317 (class 2606 OID 34655)
-- Name: citas citas_veterinario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.citas
    ADD CONSTRAINT citas_veterinario_id_fkey FOREIGN KEY (veterinario_id) REFERENCES public.usuarios_veterinarios(id_usuario);


--
-- TOC entry 5300 (class 2606 OID 34425)
-- Name: clientes clientes_id_usuario_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.clientes
    ADD CONSTRAINT clientes_id_usuario_fkey FOREIGN KEY (id_usuario) REFERENCES public.usuarios(id_usuario) ON DELETE CASCADE;


--
-- TOC entry 5310 (class 2606 OID 34540)
-- Name: desparasitaciones desparasitaciones_paciente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.desparasitaciones
    ADD CONSTRAINT desparasitaciones_paciente_id_fkey FOREIGN KEY (paciente_id) REFERENCES public.pacientes(id_paciente) ON DELETE CASCADE;


--
-- TOC entry 5320 (class 2606 OID 34697)
-- Name: facturas facturas_cliente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.facturas
    ADD CONSTRAINT facturas_cliente_id_fkey FOREIGN KEY (cliente_id) REFERENCES public.clientes(id_usuario);


--
-- TOC entry 5337 (class 2606 OID 43445)
-- Name: detalle_factura fk_detalle_factura; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.detalle_factura
    ADD CONSTRAINT fk_detalle_factura FOREIGN KEY (factura_id) REFERENCES public.facturas(id_factura) ON DELETE CASCADE;


--
-- TOC entry 5338 (class 2606 OID 43450)
-- Name: detalle_factura fk_detalle_producto; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.detalle_factura
    ADD CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES public.productos(id_producto) ON DELETE SET NULL;


--
-- TOC entry 5321 (class 2606 OID 43421)
-- Name: facturas fk_factura_paciente; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.facturas
    ADD CONSTRAINT fk_factura_paciente FOREIGN KEY (paciente_id) REFERENCES public.pacientes(id_paciente) ON DELETE SET NULL;


--
-- TOC entry 5322 (class 2606 OID 43416)
-- Name: facturas fk_factura_servicio; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.facturas
    ADD CONSTRAINT fk_factura_servicio FOREIGN KEY (servicio_prestado_id) REFERENCES public.servicios_prestados(id_prestado) ON DELETE SET NULL;


--
-- TOC entry 5330 (class 2606 OID 43141)
-- Name: solicitudes_citas fk_solicitud_cita; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.solicitudes_citas
    ADD CONSTRAINT fk_solicitud_cita FOREIGN KEY (cita_id) REFERENCES public.citas(id_cita) ON DELETE SET NULL;


--
-- TOC entry 5331 (class 2606 OID 43131)
-- Name: solicitudes_citas fk_solicitud_cliente; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.solicitudes_citas
    ADD CONSTRAINT fk_solicitud_cliente FOREIGN KEY (cliente_id) REFERENCES public.usuarios(id_usuario) ON DELETE CASCADE;


--
-- TOC entry 5332 (class 2606 OID 43136)
-- Name: solicitudes_citas fk_solicitud_paciente; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.solicitudes_citas
    ADD CONSTRAINT fk_solicitud_paciente FOREIGN KEY (paciente_id) REFERENCES public.pacientes(id_paciente) ON DELETE CASCADE;


--
-- TOC entry 5339 (class 2606 OID 43476)
-- Name: transacciones fk_transaccion_factura; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.transacciones
    ADD CONSTRAINT fk_transaccion_factura FOREIGN KEY (factura_id) REFERENCES public.facturas(id_factura) ON DELETE SET NULL;


--
-- TOC entry 5340 (class 2606 OID 43481)
-- Name: transacciones fk_transaccion_usuario; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.transacciones
    ADD CONSTRAINT fk_transaccion_usuario FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id_usuario) ON DELETE SET NULL;


--
-- TOC entry 5303 (class 2606 OID 34462)
-- Name: historial_acciones historial_acciones_usuario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.historial_acciones
    ADD CONSTRAINT historial_acciones_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id_usuario) ON DELETE CASCADE;


--
-- TOC entry 5305 (class 2606 OID 34491)
-- Name: historias_clinicas historias_clinicas_paciente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.historias_clinicas
    ADD CONSTRAINT historias_clinicas_paciente_id_fkey FOREIGN KEY (paciente_id) REFERENCES public.pacientes(id_paciente) ON DELETE CASCADE;


--
-- TOC entry 5311 (class 2606 OID 34583)
-- Name: lotes lotes_producto_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.lotes
    ADD CONSTRAINT lotes_producto_id_fkey FOREIGN KEY (producto_id) REFERENCES public.productos(id_producto) ON DELETE CASCADE;


--
-- TOC entry 5312 (class 2606 OID 34597)
-- Name: movimientos_inventario movimientos_inventario_producto_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.movimientos_inventario
    ADD CONSTRAINT movimientos_inventario_producto_id_fkey FOREIGN KEY (producto_id) REFERENCES public.productos(id_producto) ON DELETE CASCADE;


--
-- TOC entry 5313 (class 2606 OID 34602)
-- Name: movimientos_inventario movimientos_inventario_proveedor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.movimientos_inventario
    ADD CONSTRAINT movimientos_inventario_proveedor_id_fkey FOREIGN KEY (proveedor_id) REFERENCES public.proveedores(id_proveedor);


--
-- TOC entry 5314 (class 2606 OID 34607)
-- Name: movimientos_inventario movimientos_inventario_usuario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.movimientos_inventario
    ADD CONSTRAINT movimientos_inventario_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id_usuario);


--
-- TOC entry 5324 (class 2606 OID 34757)
-- Name: notificacion_destinatarios notificacion_destinatarios_destinatario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.notificacion_destinatarios
    ADD CONSTRAINT notificacion_destinatarios_destinatario_id_fkey FOREIGN KEY (destinatario_id) REFERENCES public.destinatarios(id_destinatario) ON DELETE CASCADE;


--
-- TOC entry 5325 (class 2606 OID 34752)
-- Name: notificacion_destinatarios notificacion_destinatarios_notificacion_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.notificacion_destinatarios
    ADD CONSTRAINT notificacion_destinatarios_notificacion_id_fkey FOREIGN KEY (notificacion_id) REFERENCES public.notificaciones(id_notificacion) ON DELETE CASCADE;


--
-- TOC entry 5323 (class 2606 OID 34735)
-- Name: notificaciones notificaciones_plantilla_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.notificaciones
    ADD CONSTRAINT notificaciones_plantilla_id_fkey FOREIGN KEY (plantilla_id) REFERENCES public.plantillas_mensajes(id_plantilla);


--
-- TOC entry 5304 (class 2606 OID 34476)
-- Name: pacientes pacientes_cliente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.pacientes
    ADD CONSTRAINT pacientes_cliente_id_fkey FOREIGN KEY (cliente_id) REFERENCES public.clientes(id_usuario) ON DELETE CASCADE;


--
-- TOC entry 5333 (class 2606 OID 43274)
-- Name: permisos_rol permisos_rol_rol_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.permisos_rol
    ADD CONSTRAINT permisos_rol_rol_id_fkey FOREIGN KEY (rol_id) REFERENCES public.roles(id_rol) ON DELETE CASCADE;


--
-- TOC entry 5306 (class 2606 OID 34506)
-- Name: registros_medicos registros_medicos_historia_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.registros_medicos
    ADD CONSTRAINT registros_medicos_historia_id_fkey FOREIGN KEY (historia_id) REFERENCES public.historias_clinicas(id_historia) ON DELETE CASCADE;


--
-- TOC entry 5307 (class 2606 OID 34511)
-- Name: registros_medicos registros_medicos_veterinario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.registros_medicos
    ADD CONSTRAINT registros_medicos_veterinario_id_fkey FOREIGN KEY (veterinario_id) REFERENCES public.usuarios_veterinarios(id_usuario);


--
-- TOC entry 5329 (class 2606 OID 34838)
-- Name: reportes reportes_generado_por_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.reportes
    ADD CONSTRAINT reportes_generado_por_fkey FOREIGN KEY (generado_por) REFERENCES public.usuarios(id_usuario);


--
-- TOC entry 5296 (class 2606 OID 34396)
-- Name: rol_permisos rol_permisos_permiso_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.rol_permisos
    ADD CONSTRAINT rol_permisos_permiso_id_fkey FOREIGN KEY (permiso_id) REFERENCES public.permisos(id_permiso) ON DELETE CASCADE;


--
-- TOC entry 5297 (class 2606 OID 34391)
-- Name: rol_permisos rol_permisos_rol_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.rol_permisos
    ADD CONSTRAINT rol_permisos_rol_id_fkey FOREIGN KEY (rol_id) REFERENCES public.roles(id_rol) ON DELETE CASCADE;


--
-- TOC entry 5302 (class 2606 OID 34447)
-- Name: secretarios secretarios_id_usuario_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.secretarios
    ADD CONSTRAINT secretarios_id_usuario_fkey FOREIGN KEY (id_usuario) REFERENCES public.usuarios(id_usuario) ON DELETE CASCADE;


--
-- TOC entry 5334 (class 2606 OID 43297)
-- Name: servicios_configuracion servicios_configuracion_servicio_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.servicios_configuracion
    ADD CONSTRAINT servicios_configuracion_servicio_id_fkey FOREIGN KEY (servicio_id) REFERENCES public.servicios(id_servicio) ON DELETE SET NULL;


--
-- TOC entry 5318 (class 2606 OID 34672)
-- Name: servicios_prestados servicios_prestados_cita_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.servicios_prestados
    ADD CONSTRAINT servicios_prestados_cita_id_fkey FOREIGN KEY (cita_id) REFERENCES public.citas(id_cita) ON DELETE CASCADE;


--
-- TOC entry 5319 (class 2606 OID 34677)
-- Name: servicios_prestados servicios_prestados_servicio_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.servicios_prestados
    ADD CONSTRAINT servicios_prestados_servicio_id_fkey FOREIGN KEY (servicio_id) REFERENCES public.servicios(id_servicio);


--
-- TOC entry 5298 (class 2606 OID 34409)
-- Name: usuarios usuarios_id_usuario_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_id_usuario_fkey FOREIGN KEY (id_usuario) REFERENCES public.personas(id_persona) ON DELETE CASCADE;


--
-- TOC entry 5299 (class 2606 OID 34414)
-- Name: usuarios usuarios_rol_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_rol_id_fkey FOREIGN KEY (rol_id) REFERENCES public.roles(id_rol);


--
-- TOC entry 5301 (class 2606 OID 34437)
-- Name: usuarios_veterinarios usuarios_veterinarios_id_usuario_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.usuarios_veterinarios
    ADD CONSTRAINT usuarios_veterinarios_id_usuario_fkey FOREIGN KEY (id_usuario) REFERENCES public.usuarios(id_usuario) ON DELETE CASCADE;


--
-- TOC entry 5308 (class 2606 OID 34523)
-- Name: vacunaciones vacunaciones_paciente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.vacunaciones
    ADD CONSTRAINT vacunaciones_paciente_id_fkey FOREIGN KEY (paciente_id) REFERENCES public.pacientes(id_paciente) ON DELETE CASCADE;


--
-- TOC entry 5309 (class 2606 OID 34528)
-- Name: vacunaciones vacunaciones_veterinario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: vet_admin
--

ALTER TABLE ONLY public.vacunaciones
    ADD CONSTRAINT vacunaciones_veterinario_id_fkey FOREIGN KEY (veterinario_id) REFERENCES public.usuarios_veterinarios(id_usuario);


--
-- TOC entry 5582 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: pg_database_owner
--

GRANT ALL ON SCHEMA public TO vet_admin;


--
-- TOC entry 2315 (class 826 OID 34347)
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON SEQUENCES TO vet_admin;


--
-- TOC entry 2314 (class 826 OID 34346)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON TABLES TO vet_admin;


-- Completed on 2025-11-30 18:30:38

--
-- PostgreSQL database dump complete
--

