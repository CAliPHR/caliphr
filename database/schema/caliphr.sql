-- Database generated with pgModeler (PostgreSQL Database Modeler).
-- pgModeler  version: 0.8.2
-- PostgreSQL version: 9.4
-- Project Site: pgmodeler.com.br
-- Model Author: ---


-- Database creation must be done outside an multicommand file.
-- These commands were put in this file only for convenience.
-- -- object: caliphr_db | type: DATABASE --
-- -- DROP DATABASE IF EXISTS caliphr_db;
-- CREATE DATABASE caliphr_db
-- ;
-- -- ddl-end --
-- 

-- object: caliphr | type: SCHEMA --
-- DROP SCHEMA IF EXISTS caliphr CASCADE;
CREATE SCHEMA caliphr;
-- ddl-end --

SET search_path TO pg_catalog,public,caliphr;
-- ddl-end --

-- object: caliphr.hqmf_document_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.hqmf_document_id_seq CASCADE;
CREATE SEQUENCE caliphr.hqmf_document_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.hqmf_measure_period_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.hqmf_measure_period_id_seq CASCADE;
CREATE SEQUENCE caliphr.hqmf_measure_period_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.hqmf_population_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.hqmf_population_id_seq CASCADE;
CREATE SEQUENCE caliphr.hqmf_population_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.hqmf_measure_period | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.hqmf_measure_period CASCADE;
CREATE TABLE caliphr.hqmf_measure_period(
	measure_period_id bigint NOT NULL DEFAULT nextval('caliphr.hqmf_measure_period_id_seq'::regclass),
	low varchar,
	high varchar,
	width varchar,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_hqmf_measure_period PRIMARY KEY (measure_period_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.hqmf_data_criteria_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.hqmf_data_criteria_id_seq CASCADE;
CREATE SEQUENCE caliphr.hqmf_data_criteria_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.hqmf_document | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.hqmf_document CASCADE;
CREATE TABLE caliphr.hqmf_document(
	hqmf_doc_id bigint NOT NULL DEFAULT nextval('caliphr.hqmf_document_id_seq'::regclass),
	measure_period_id bigint,
	domain_id integer,
	provider_id integer,
	bundle_id integer,
	hqmf_id varchar,
	hqmf_set_id varchar,
	hqmf_version_number integer,
	title varchar,
	description varchar,
	cms_id varchar,
	user_id integer,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_hqmf_document PRIMARY KEY (hqmf_doc_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.provider_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.provider_id_seq CASCADE;
CREATE SEQUENCE caliphr.provider_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.clinical_document_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.clinical_document_id_seq CASCADE;
CREATE SEQUENCE caliphr.clinical_document_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.provider | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.provider CASCADE;
CREATE TABLE caliphr.provider(
	provider_id integer NOT NULL DEFAULT nextval('caliphr.provider_id_seq'::regclass),
	state_id integer,
	type_id integer,
	group_id integer,
	npi character varying,
	org_name character varying(250),
	full_name character varying,
	first_name character varying(250),
	middle_name character varying(250),
	last_name character varying(250),
	address character varying,
	address2 character varying,
	city character varying,
	state_value character varying,
	zipcode character varying,
	country character varying,
	tax_id character varying(250),
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_provider PRIMARY KEY (provider_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_advanced_directive_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_advanced_directive_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_advanced_directive_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_allergy_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_allergy_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_allergy_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_medication_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_medication_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_medication_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_plan_of_care_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_plan_of_care_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_plan_of_care_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_problem_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_problem_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_problem_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_reason_for_visit_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_reason_for_visit_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_reason_for_visit_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_vital_sign_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_vital_sign_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_vital_sign_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_family_history_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_family_history_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_family_history_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.parse_status_type_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.parse_status_type_id_seq CASCADE;
CREATE SEQUENCE caliphr.parse_status_type_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_negation_detail_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_negation_detail_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_negation_detail_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_info_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_info_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_info_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_func_cog_status_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_func_cog_status_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_func_cog_status_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_social_history_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_social_history_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_social_history_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_result_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_result_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_result_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.clinical_document | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.clinical_document CASCADE;
CREATE TABLE caliphr.clinical_document(
	document_id bigint NOT NULL DEFAULT nextval('caliphr.clinical_document_id_seq'::regclass),
	patient_id integer,
	type_id integer,
	parse_status_type_id integer,
	care_setting character varying(250),
	title character varying,
	file_name character varying,
	file_size_kb numeric,
	parse_time numeric,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_clinical_document PRIMARY KEY (document_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_vital_sign | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_vital_sign CASCADE;
CREATE TABLE caliphr.patient_vital_sign(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_vital_sign_id_seq'::regclass),
	code_mapping_id integer,
	patient_id integer,
	code_id integer,
	value_code_id integer,
	status_code_id integer,
	template_id integer,
	negation_detail_id integer,
	code_description text,
	value_code_description text,
	status_code_name character varying,
	effective_time_start timestamp,
	effective_time_end timestamp,
	record_value character varying,
	record_value_unit character varying,
	external_id character varying,
	date_created timestamp DEFAULT NOW(),
	user_created integer,
	date_updated timestamp DEFAULT NOW(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_vital_sign PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_instruction_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_instruction_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_instruction_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_advanced_directive | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_advanced_directive CASCADE;
CREATE TABLE caliphr.patient_advanced_directive(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_advanced_directive_id_seq'::regclass),
	code_mapping_id integer,
	patient_id integer,
	code_id integer,
	template_id integer,
	negation_detail_id integer,
	code_description text,
	effective_time_start timestamp,
	effective_time_end timestamp,
	external_id character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_advanced_directive PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_allergy | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_allergy CASCADE;
CREATE TABLE caliphr.patient_allergy(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_allergy_id_seq'::regclass),
	code_mapping_id integer,
	patient_id integer,
	code_id integer,
	reaction_code_id integer,
	severity_code_id integer,
	template_id integer,
	negation_detail_id integer,
	substance_code_id integer,
	status_code_id integer,
	code_description text,
	reaction_code_description text,
	severity_code_description text,
	substance_code_description text,
	status_code_name character varying,
	effective_time_start timestamp,
	effective_time_end timestamp,
	external_id character varying,
	date_created timestamp DEFAULT NOW(),
	user_created integer,
	date_updated timestamp DEFAULT NOW(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_allergy PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_reason_for_visit | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_reason_for_visit CASCADE;
CREATE TABLE caliphr.patient_reason_for_visit(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_reason_for_visit_id_seq'::regclass),
	code_mapping_id integer,
	patient_id integer,
	code_id integer,
	template_id integer,
	negation_detail_id integer,
	code_description text,
	effective_time_start timestamp,
	effective_time_end timestamp,
	external_id character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_reason_for_visit PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_family_history | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_family_history CASCADE;
CREATE TABLE caliphr.patient_family_history(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_family_history_id_seq'::regclass),
	code_mapping_id integer,
	patient_id integer,
	code_id integer,
	template_id integer,
	negation_detail_id integer,
	diagnosis_code_id integer,
	code_description text,
	diagnosis_code_description text,
	age_at_onset integer,
	effective_time_start timestamp,
	effective_time_end timestamp,
	external_id character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_family_history PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_func_cog_status | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_func_cog_status CASCADE;
CREATE TABLE caliphr.patient_func_cog_status(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_func_cog_status_id_seq'::regclass),
	code_mapping_id integer,
	patient_id integer,
	code_id integer,
	value_code_id integer,
	status_code_id integer,
	template_id integer,
	negation_detail_id integer,
	code_description text,
	status_code_name character varying,
	value_code_description text,
	effective_time_start timestamp,
	effective_time_end timestamp,
	external_id character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_func_cog_status PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_encounter_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_encounter_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_encounter_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_encounter | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_encounter CASCADE;
CREATE TABLE caliphr.patient_encounter(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_encounter_id_seq'::regclass),
	code_mapping_id integer,
	patient_id integer,
	provider_id integer,
	code_id integer,
	facility_location_code_id integer,
	template_id integer,
	negation_detail_id integer,
	code_description text,
	effective_time_start timestamp,
	effective_time_end timestamp,
	performer character varying,
	encounter_location character varying,
	facility_location_code_description text,
	external_id character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	status_code_id integer,
	CONSTRAINT pk_patient_encounter PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_instruction | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_instruction CASCADE;
CREATE TABLE caliphr.patient_instruction(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_instruction_id_seq'::regclass),
	code_mapping_id integer,
	patient_id integer,
	code_id integer,
	status_code_id integer,
	template_id integer,
	negation_detail_id integer,
	code_description text,
	status_code character varying,
	effective_time_start timestamp,
	effective_time_end timestamp,
	external_id character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_instruction PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_procedure_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_procedure_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_procedure_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_immunization_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_immunization_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_immunization_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_result | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_result CASCADE;
CREATE TABLE caliphr.patient_result(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_result_id_seq'::regclass),
	code_mapping_id integer,
	connected_record_id bigint,
	patient_id integer,
	code_id integer,
	value_code_id integer,
	reason_code_id integer,
	status_code_id integer,
	template_id integer,
	negation_detail_id integer,
	code_description text,
	value_code_description text,
	status_code_name character varying,
	effective_time_start timestamp,
	effective_time_end timestamp,
	result_value character varying,
	result_value_unit character varying,
	reference_range_low_value character varying,
	reference_range_low_value_unit character varying,
	reference_range_high_value character varying,
	reference_range_high_value_unit character varying,
	reason_code_description text,
	reason_effective_time timestamp,
	external_id character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_result PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_reason_for_referral_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_reason_for_referral_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_reason_for_referral_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_immunization | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_immunization CASCADE;
CREATE TABLE caliphr.patient_immunization(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_immunization_id_seq'::regclass),
	code_mapping_id integer,
	patient_id integer,
	product_code_id integer,
	status_code_id integer,
	template_id integer,
	negation_detail_id integer,
	effective_time_start timestamp,
	effective_time_end timestamp,
	status_code_name character varying,
	product_code_description text,
	dose_quantity_value character varying,
	dose_quantity_unit character varying,
	external_id character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_immunization PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_procedure | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_procedure CASCADE;
CREATE TABLE caliphr.patient_procedure(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_procedure_id_seq'::regclass),
	code_mapping_id integer,
	connected_record_id integer,
	patient_id integer,
	code_id integer,
	value_code_id integer,
	ordinality_code_id integer,
	reason_code_id integer,
	result_value_code_id integer,
	status_code_id integer,
	template_id integer,
	negation_detail_id integer,
	code_description text,
	value_code_description text,
	value_string character varying,
	value_unit character varying,
	status_code_name character varying,
	effective_time_start timestamp,
	effective_time_end timestamp,
	ordinality_code_name text,
	ordinality_code_system character varying,
	ordinality_code_description smallint,
	reason_code_name text,
	reason_code_system character varying,
	reason_code_description text,
	reason_effective_time timestamp,
	result_value_code_name text,
	result_value_code_system character varying,
	result_value_code_description text,
	external_id character varying,
	date_created timestamp DEFAULT NOW(),
	user_created integer,
	date_updated timestamp DEFAULT NOW(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_procedure PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_reason_for_referral | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_reason_for_referral CASCADE;
CREATE TABLE caliphr.patient_reason_for_referral(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_reason_for_referral_id_seq'::regclass),
	code_mapping_id integer,
	patient_id integer,
	code_id integer,
	template_id integer,
	negation_detail_id integer,
	code_description text,
	effective_time_start timestamp,
	effective_time_end timestamp,
	external_id character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_reason_for_referral PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_info | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_info CASCADE;
CREATE TABLE caliphr.patient_info(
	patient_id integer NOT NULL DEFAULT nextval('caliphr.patient_info_id_seq'::regclass),
	group_id integer,
	gender_code_id integer,
	race_code_id integer,
	ethnicity_code_id integer,
	marital_status character varying,
	religious_affiliations character varying,
	guardian character varying,
	language_communication character varying,
	gender_code_description text,
	race_code_description text,
	ethnicity_code_description text,
	expired bool,
	date_created timestamp DEFAULT NOW(),
	user_created integer,
	date_updated timestamp DEFAULT NOW(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_info PRIMARY KEY (patient_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.code_system_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.code_system_id_seq CASCADE;
CREATE SEQUENCE caliphr.code_system_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.code_system | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.code_system CASCADE;
CREATE TABLE caliphr.code_system(
	record_id integer NOT NULL DEFAULT nextval('caliphr.code_system_id_seq'::regclass),
	hl7_oid character varying(250),
	code_system_name text,
	date_created timestamp DEFAULT NOW(),
	user_created integer,
	date_updated timestamp DEFAULT NOW(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_code_system PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.value_set_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.value_set_id_seq CASCADE;
CREATE SEQUENCE caliphr.value_set_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.value_set | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.value_set CASCADE;
CREATE TABLE caliphr.value_set(
	value_set_id integer NOT NULL DEFAULT nextval('caliphr.value_set_id_seq'::regclass),
	hl7_oid character varying(250),
	value_set_name text,
	date_created timestamp DEFAULT NOW(),
	user_created integer,
	date_updated timestamp DEFAULT NOW(),
	user_updated integer,
	date_disabled timestamp,
	bundle_id integer,
	CONSTRAINT pk_value_set PRIMARY KEY (value_set_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.code_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.code_id_seq CASCADE;
CREATE SEQUENCE caliphr.code_id_seq
	INCREMENT BY 10
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.code | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.code CASCADE;
CREATE TABLE caliphr.code(
	code_id integer NOT NULL DEFAULT nextval('caliphr.code_id_seq'::regclass),
	code_system_id integer,
	code_name text,
	description text,
	date_created timestamp DEFAULT NOW(),
	user_created integer,
	date_updated timestamp DEFAULT NOW(),
	user_updated integer,
	date_disabled timestamp,
	latest_cda_description text,
	CONSTRAINT pk_code PRIMARY KEY (code_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.value_set_code_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.value_set_code_id_seq CASCADE;
CREATE SEQUENCE caliphr.value_set_code_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.value_set_code | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.value_set_code CASCADE;
CREATE TABLE caliphr.value_set_code(
	record_id integer NOT NULL DEFAULT nextval('caliphr.value_set_code_id_seq'::regclass),
	value_set_id integer,
	code_id integer,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_value_set_code PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_medical_equipment_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_medical_equipment_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_medical_equipment_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_medical_equipment | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_medical_equipment CASCADE;
CREATE TABLE caliphr.patient_medical_equipment(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_medical_equipment_id_seq'::regclass),
	code_mapping_id integer,
	patient_id integer,
	code_id integer,
	reason_code_id integer,
	status_code_id integer,
	template_id integer,
	negation_detail_id integer,
	code_description text,
	effective_time_start timestamp,
	effective_time_end timestamp,
	status_code_name character varying,
	reason_code_description text,
	reason_effective_time timestamp,
	external_id character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_medical_equipment PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.hqmf_attribute_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.hqmf_attribute_id_seq CASCADE;
CREATE SEQUENCE caliphr.hqmf_attribute_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.hqmf_attribute | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.hqmf_attribute CASCADE;
CREATE TABLE caliphr.hqmf_attribute(
	attribute_id bigint NOT NULL DEFAULT nextval('caliphr.hqmf_attribute_id_seq'::regclass),
	hqmf_doc_id bigint,
	code character varying(250),
	attribute_name character varying(250),
	code_obj_json text,
	value_obj_json text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_hqmf_attribute PRIMARY KEY (attribute_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.state_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.state_id_seq CASCADE;
CREATE SEQUENCE caliphr.state_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_phone_number_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_phone_number_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_phone_number_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.state | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.state CASCADE;
CREATE TABLE caliphr.state(
	state_id integer NOT NULL DEFAULT nextval('caliphr.state_id_seq'::regclass),
	name character varying,
	abbreviation char(2),
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_state PRIMARY KEY (state_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_phone_number | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_phone_number CASCADE;
CREATE TABLE caliphr.patient_phone_number(
	record_id integer NOT NULL DEFAULT nextval('caliphr.patient_phone_number_id_seq'::regclass),
	patient_id integer,
	phone_number_type character varying,
	date_created timestamp,
	user_created integer,
	date_updated timestamp,
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_phone_number PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.status_code_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.status_code_id_seq CASCADE;
CREATE SEQUENCE caliphr.status_code_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.status_code | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.status_code CASCADE;
CREATE TABLE caliphr.status_code(
	status_code_id integer NOT NULL DEFAULT nextval('caliphr.status_code_id_seq'::regclass),
	status_code_name character varying,
	hl7_oid character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_status_code PRIMARY KEY (status_code_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.document_type_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.document_type_id_seq CASCADE;
CREATE SEQUENCE caliphr.document_type_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.document_type | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.document_type CASCADE;
CREATE TABLE caliphr.document_type(
	type_id integer NOT NULL DEFAULT nextval('caliphr.document_type_id_seq'::regclass),
	type_name character varying(250),
	hl7_oid character varying(250),
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_document_type PRIMARY KEY (type_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.template_root_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.template_root_id_seq CASCADE;
CREATE SEQUENCE caliphr.template_root_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.template_root | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.template_root CASCADE;
CREATE TABLE caliphr.template_root(
	record_id integer NOT NULL DEFAULT nextval('caliphr.template_root_id_seq'::regclass),
	hl7_oid character varying(250),
	template_name character varying(250),
	template_description text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_template_root PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_payer_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_payer_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_payer_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_payer | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_payer CASCADE;
CREATE TABLE caliphr.patient_payer(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_payer_id_seq'::regclass),
	code_mapping_id integer,
	patient_id integer,
	status_code_id integer,
	payer_code_id integer,
	template_id integer,
	negation_detail_id integer,
	payer_code_description text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_insurance_company PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_medication | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_medication CASCADE;
CREATE TABLE caliphr.patient_medication(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_medication_id_seq'::regclass),
	code_mapping_id integer,
	patient_id integer,
	product_code_id integer,
	reason_code_id integer,
	administration_code_id integer,
	status_code_id integer,
	template_id integer,
	negation_detail_id integer,
	effective_time_start timestamp,
	effective_time_end timestamp,
	status_code_name character varying,
	product_code_description text,
	administration_code_description text,
	dose_quantity_value character varying,
	dose_quantity_unit character varying,
	rate_quantity_value character varying,
	rate_quantity_unit character varying,
	reason_code_description text,
	reason_effective_time timestamp,
	external_id character varying,
	date_created timestamp DEFAULT NOW(),
	user_created integer,
	date_updated timestamp DEFAULT NOW(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_medication PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.domain_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.domain_id_seq CASCADE;
CREATE SEQUENCE caliphr.domain_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.domain | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.domain CASCADE;
CREATE TABLE caliphr.domain(
	domain_id integer NOT NULL DEFAULT nextval('caliphr.domain_id_seq'::regclass),
	name character varying(250),
	description text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_domain PRIMARY KEY (domain_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.practice_group_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.practice_group_id_seq CASCADE;
CREATE SEQUENCE caliphr.practice_group_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.hqmf_population_set_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.hqmf_population_set_id_seq CASCADE;
CREATE SEQUENCE caliphr.hqmf_population_set_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.hqmf_population_set | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.hqmf_population_set CASCADE;
CREATE TABLE caliphr.hqmf_population_set(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.hqmf_population_set_id_seq'::regclass),
	hqmf_document_id bigint,
	key character varying,
	value character varying,
	index integer,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_hqmf_population_set PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.result_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.result_id_seq CASCADE;
CREATE SEQUENCE caliphr.result_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.result | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.result CASCADE;
CREATE TABLE caliphr.result(
	result_id bigint NOT NULL DEFAULT nextval('caliphr.result_id_seq'::regclass),
	hqmf_population_set_id bigint,
	result_value integer,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_result PRIMARY KEY (result_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.hqmf_data_criteria | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.hqmf_data_criteria CASCADE;
CREATE TABLE caliphr.hqmf_data_criteria(
	data_criteria_id bigint NOT NULL DEFAULT nextval('caliphr.hqmf_data_criteria_id_seq'::regclass),
	hqmf_doc_id bigint,
	title varchar,
	hqmf_id character varying NOT NULL,
	description varchar,
	code_list_id varchar,
	data_criteria_json text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_hqmf_data_criteria PRIMARY KEY (data_criteria_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.result_patient_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.result_patient_id_seq CASCADE;
CREATE SEQUENCE caliphr.result_patient_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.result_patient | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.result_patient CASCADE;
CREATE TABLE caliphr.result_patient(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.result_patient_id_seq'::regclass),
	result_id bigint,
	patient_id integer,
	date_created timestamp,
	user_created integer,
	date_updated timestamp,
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_result_patient PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_plan_of_care | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_plan_of_care CASCADE;
CREATE TABLE caliphr.patient_plan_of_care(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_plan_of_care_id_seq'::regclass),
	code_mapping_id integer,
	patient_id integer,
	code_id integer,
	status_code_id integer,
	template_id integer,
	negation_detail_id integer,
	code_description text,
	status_code_name character varying,
	effective_time_start timestamp,
	effective_time_end timestamp,
	external_id character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_plan_of_care PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_negation_detail | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_negation_detail CASCADE;
CREATE TABLE caliphr.patient_negation_detail(
	record_id integer NOT NULL DEFAULT nextval('caliphr.patient_negation_detail_id_seq'::regclass),
	code_id integer,
	code_description text,
	value character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_negation_detail PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.practice_group | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.practice_group CASCADE;
CREATE TABLE caliphr.practice_group(
	group_id integer NOT NULL DEFAULT nextval('caliphr.practice_group_id_seq'::regclass),
	organization_id integer,
	vendor_id integer,
	type_id integer,
	group_name character varying,
	sender_oid character varying,
	address_1 character varying,
	address2 character varying,
	city character varying,
	state_name character varying,
	zipcode character varying,
	practice_npi character varying,
	active_problems_only_flag boolean,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_practice_group PRIMARY KEY (group_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.application_user_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.application_user_id_seq CASCADE;
CREATE SEQUENCE caliphr.application_user_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.organization_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.organization_id_seq CASCADE;
CREATE SEQUENCE caliphr.organization_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.ehr_vendor_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.ehr_vendor_id_seq CASCADE;
CREATE SEQUENCE caliphr.ehr_vendor_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.organization | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.organization CASCADE;
CREATE TABLE caliphr.organization(
	organization_id integer NOT NULL DEFAULT nextval('caliphr.organization_id_seq'::regclass),
	type_id integer,
	organization_name character varying,
	address_1 character varying,
	address2 character varying,
	city character varying,
	state_name character varying,
	zipcode character varying,
	organization_number character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_organization PRIMARY KEY (organization_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.application_user | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.application_user CASCADE;
CREATE TABLE caliphr.application_user(
	user_id integer NOT NULL DEFAULT nextval('caliphr.application_user_id_seq'::regclass),
	first_name character varying,
	last_name character varying,
	email_address character varying,
	password_hash character varying,
	date_last_login timestamp,
	last_login_ip_address character varying,
	date_password_changed timestamp,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_application_user PRIMARY KEY (user_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.provider_phone_number_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.provider_phone_number_id_seq CASCADE;
CREATE SEQUENCE caliphr.provider_phone_number_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.provider_phone_number | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.provider_phone_number CASCADE;
CREATE TABLE caliphr.provider_phone_number(
	record_id integer NOT NULL DEFAULT nextval('caliphr.provider_phone_number_id_seq'::regclass),
	provider_id integer,
	phone_number character varying,
	phone_number_type character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_provider_phone_number PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.application_user_security_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.application_user_security_id_seq CASCADE;
CREATE SEQUENCE caliphr.application_user_security_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.security_role_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.security_role_id_seq CASCADE;
CREATE SEQUENCE caliphr.security_role_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.application_user_security | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.application_user_security CASCADE;
CREATE TABLE caliphr.application_user_security(
	record_id integer NOT NULL DEFAULT nextval('caliphr.application_user_security_id_seq'::regclass),
	role_id integer,
	user_id integer,
	provider_id integer,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_application_user_security PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.security_role | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.security_role CASCADE;
CREATE TABLE caliphr.security_role(
	role_id integer NOT NULL DEFAULT nextval('caliphr.security_role_id_seq'::regclass),
	role_name character varying,
	role_description text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_security_role PRIMARY KEY (role_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.result_supplemental_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.result_supplemental_id_seq CASCADE;
CREATE SEQUENCE caliphr.result_supplemental_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.result_supplemental | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.result_supplemental CASCADE;
CREATE TABLE caliphr.result_supplemental(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.result_supplemental_id_seq'::regclass),
	result_id bigint,
	code_id integer,
	result_value integer,
	code_name character varying,
	code_system text,
	code_description character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_result_supplemental PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_info_secure | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_info_secure CASCADE;
CREATE TABLE caliphr.patient_info_secure(
	patient_id integer NOT NULL,
	ssn bytea,
	mrn_hash bytea,
	medical_record_number bytea,
	first_name bytea,
	last_name bytea,
	address bytea,
	address2 bytea,
	city bytea,
	state_id bytea,
	state_value bytea,
	zipcode bytea,
	country bytea,
	birth_time bytea,
	death_date bytea,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_info_secure PRIMARY KEY (patient_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_problem | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_problem CASCADE;
CREATE TABLE caliphr.patient_problem(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_problem_id_seq'::regclass),
	code_mapping_id integer,
	connected_record_id bigint,
	patient_id integer,
	code_id integer,
	problem_code_id integer,
	ordinality_code_id integer,
	severity_code_id integer,
	status_code_id integer,
	template_id integer,
	negation_detail_id integer,
	code_description text,
	effective_time_start timestamp,
	effective_time_end timestamp,
	status_code_name character varying,
	problem_code_description text,
	ordinality_code_description text,
	severity_code_description text,
	external_id character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	laterality_code_id integer,
	laterality_code_description text,
	CONSTRAINT pk_patient_problem PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_phone_number_secure | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_phone_number_secure CASCADE;
CREATE TABLE caliphr.patient_phone_number_secure(
	record_id integer NOT NULL,
	phone_number bytea,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_phone_number_secure PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.clinical_document_parse_error_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.clinical_document_parse_error_id_seq CASCADE;
CREATE SEQUENCE caliphr.clinical_document_parse_error_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.clinical_document_parse_error | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.clinical_document_parse_error CASCADE;
CREATE TABLE caliphr.clinical_document_parse_error(
	record_id integer NOT NULL DEFAULT nextval('caliphr.clinical_document_parse_error_id_seq'::regclass),
	document_id bigint,
	stack_trace text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_clinical_document_parse_error PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.parse_status_type | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.parse_status_type CASCADE;
CREATE TABLE caliphr.parse_status_type(
	type_id integer NOT NULL DEFAULT nextval('caliphr.parse_status_type_id_seq'::regclass),
	status_name character varying,
	status_description text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_parse_status_type PRIMARY KEY (type_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.audit_type_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.audit_type_id_seq CASCADE;
CREATE SEQUENCE caliphr.audit_type_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.audit_type | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.audit_type CASCADE;
CREATE TABLE caliphr.audit_type(
	type_id integer NOT NULL DEFAULT nextval('caliphr.audit_type_id_seq'::regclass),
	audit_type_name character varying,
	audit_type_description text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_audit_type PRIMARY KEY (type_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.application_user_audit_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.application_user_audit_id_seq CASCADE;
CREATE SEQUENCE caliphr.application_user_audit_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.application_user_audit | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.application_user_audit CASCADE;
CREATE TABLE caliphr.application_user_audit(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.application_user_audit_id_seq'::regclass),
	user_id integer,
	audit_type_id integer,
	class_name character varying,
	method_name character varying,
	json_request text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_application_user_audit PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: pgcrypto | type: EXTENSION --
-- DROP EXTENSION IF EXISTS pgcrypto CASCADE;
CREATE EXTENSION pgcrypto
      WITH SCHEMA caliphr;
-- ddl-end --

-- object: caliphr.organization_type_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.organization_type_id_seq CASCADE;
CREATE SEQUENCE caliphr.organization_type_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.organization_type | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.organization_type CASCADE;
CREATE TABLE caliphr.organization_type(
	type_id integer NOT NULL DEFAULT nextval('caliphr.organization_type_id_seq'::regclass),
	name character varying,
	description text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_type_id PRIMARY KEY (type_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.practice_group_type_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.practice_group_type_id_seq CASCADE;
CREATE SEQUENCE caliphr.practice_group_type_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.practice_group_type | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.practice_group_type CASCADE;
CREATE TABLE caliphr.practice_group_type(
	type_id integer NOT NULL DEFAULT nextval('caliphr.practice_group_type_id_seq'::regclass),
	name character varying,
	description text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_practice_group_type PRIMARY KEY (type_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.provider_type_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.provider_type_id_seq CASCADE;
CREATE SEQUENCE caliphr.provider_type_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.provider_type | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.provider_type CASCADE;
CREATE TABLE caliphr.provider_type(
	type_id integer NOT NULL DEFAULT nextval('caliphr.provider_type_id_seq'::regclass),
	name character varying,
	description text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	mental_health_indicator bool,
	CONSTRAINT pk_provider_type PRIMARY KEY (type_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.application_user_password_request_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.application_user_password_request_id_seq CASCADE;
CREATE SEQUENCE caliphr.application_user_password_request_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.application_user_password_request | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.application_user_password_request CASCADE;
CREATE TABLE caliphr.application_user_password_request(
	record_id integer NOT NULL DEFAULT nextval('caliphr.application_user_password_request_id_seq'::regclass),
	user_id integer,
	uuid character varying,
	date_uuid_expiration timestamp,
	request_ip_address character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_application_user_password_request PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.patient_social_history | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_social_history CASCADE;
CREATE TABLE caliphr.patient_social_history(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_social_history_id_seq'::regclass),
	code_mapping_id integer,
	patient_id integer,
	code_id integer,
	value_code_id integer,
	status_code_id integer,
	template_id integer,
	negation_detail_id integer,
	code_description text,
	status_code_name character varying,
	effective_time_start timestamp,
	effective_time_end timestamp,
	value_code_description text,
	external_id character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_social_history PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.application_user_password_history_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.application_user_password_history_id_seq CASCADE;
CREATE SEQUENCE caliphr.application_user_password_history_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.application_user_password_history | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.application_user_password_history CASCADE;
CREATE TABLE caliphr.application_user_password_history(
	record_id integer NOT NULL DEFAULT nextval('caliphr.application_user_password_history_id_seq'::regclass),
	user_id integer,
	password_hash character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_application_user_password_history PRIMARY KEY (record_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.bundle_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.bundle_id_seq CASCADE;
CREATE SEQUENCE caliphr.bundle_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.bundle | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.bundle CASCADE;
CREATE TABLE caliphr.bundle(
	bundle_id integer NOT NULL DEFAULT nextval('caliphr.bundle_id_seq'::regclass),
	bundle_version character varying,
	description text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_bundle PRIMARY KEY (bundle_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.appoinment_type_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.appoinment_type_id_seq CASCADE;
CREATE SEQUENCE caliphr.appoinment_type_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_hl7_appointment_type_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_hl7_appointment_type_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_hl7_appointment_type_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: "clinical_document_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."clinical_document_1_IDX" CASCADE;
CREATE INDEX "clinical_document_1_IDX" ON caliphr.clinical_document
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_vital_sign_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_vital_sign_1_IDX" CASCADE;
CREATE INDEX "patient_vital_sign_1_IDX" ON caliphr.patient_vital_sign
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_advanced_directive_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_advanced_directive_1_IDX" CASCADE;
CREATE INDEX "patient_advanced_directive_1_IDX" ON caliphr.patient_advanced_directive
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_allergy_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_allergy_1_IDX" CASCADE;
CREATE INDEX "patient_allergy_1_IDX" ON caliphr.patient_allergy
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_reason_for_visit_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_reason_for_visit_1_IDX" CASCADE;
CREATE INDEX "patient_reason_for_visit_1_IDX" ON caliphr.patient_reason_for_visit
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_family_history_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_family_history_1_IDX" CASCADE;
CREATE INDEX "patient_family_history_1_IDX" ON caliphr.patient_family_history
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_func_cog_status_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_func_cog_status_1_IDX" CASCADE;
CREATE INDEX "patient_func_cog_status_1_IDX" ON caliphr.patient_func_cog_status
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_encounter_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_encounter_1_IDX" CASCADE;
CREATE INDEX "patient_encounter_1_IDX" ON caliphr.patient_encounter
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_instruction_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_instruction_1_IDX" CASCADE;
CREATE INDEX "patient_instruction_1_IDX" ON caliphr.patient_instruction
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_result_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_result_1_IDX" CASCADE;
CREATE INDEX "patient_result_1_IDX" ON caliphr.patient_result
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_immunization_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_immunization_1_IDX" CASCADE;
CREATE INDEX "patient_immunization_1_IDX" ON caliphr.patient_immunization
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_procedure_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_procedure_1_IDX" CASCADE;
CREATE INDEX "patient_procedure_1_IDX" ON caliphr.patient_procedure
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_reason_for_referral_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_reason_for_referral_1_IDX" CASCADE;
CREATE INDEX "patient_reason_for_referral_1_IDX" ON caliphr.patient_reason_for_referral
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "code_system_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."code_system_1_IDX" CASCADE;
CREATE INDEX "code_system_1_IDX" ON caliphr.code_system
	USING btree
	(
	  hl7_oid ASC NULLS LAST
	);
-- ddl-end --

-- object: "value_set_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."value_set_1_IDX" CASCADE;
CREATE INDEX "value_set_1_IDX" ON caliphr.value_set
	USING btree
	(
	  hl7_oid ASC NULLS LAST
	);
-- ddl-end --

-- object: "code_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."code_1_IDX" CASCADE;
CREATE INDEX "code_1_IDX" ON caliphr.code
	USING btree
	(
	  code_name ASC NULLS LAST,
	  code_system_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "value_set_code_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."value_set_code_1_IDX" CASCADE;
CREATE INDEX "value_set_code_1_IDX" ON caliphr.value_set_code
	USING btree
	(
	  value_set_id ASC NULLS LAST,
	  code_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "value_set_code_2_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."value_set_code_2_IDX" CASCADE;
CREATE INDEX "value_set_code_2_IDX" ON caliphr.value_set_code
	USING btree
	(
	  code_id ASC NULLS LAST,
	  value_set_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_medical_equipment_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_medical_equipment_1_IDX" CASCADE;
CREATE INDEX "patient_medical_equipment_1_IDX" ON caliphr.patient_medical_equipment
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "hqmf_attribute_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."hqmf_attribute_1_IDX" CASCADE;
CREATE INDEX "hqmf_attribute_1_IDX" ON caliphr.hqmf_attribute
	USING btree
	(
	  hqmf_doc_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_payer_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_payer_1_IDX" CASCADE;
CREATE INDEX "patient_payer_1_IDX" ON caliphr.patient_payer
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_medication_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_medication_1_IDX" CASCADE;
CREATE INDEX "patient_medication_1_IDX" ON caliphr.patient_medication
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "hqmf_population_set_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."hqmf_population_set_1_IDX" CASCADE;
CREATE INDEX "hqmf_population_set_1_IDX" ON caliphr.hqmf_population_set
	USING btree
	(
	  hqmf_document_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "result_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."result_1_IDX" CASCADE;
CREATE INDEX "result_1_IDX" ON caliphr.result
	USING btree
	(
	  hqmf_population_set_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "hqmf_data_criteria_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."hqmf_data_criteria_1_IDX" CASCADE;
CREATE INDEX "hqmf_data_criteria_1_IDX" ON caliphr.hqmf_data_criteria
	USING btree
	(
	  hqmf_doc_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "result_patient_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."result_patient_1_IDX" CASCADE;
CREATE INDEX "result_patient_1_IDX" ON caliphr.result_patient
	USING btree
	(
	  result_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_plan_of_care_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_plan_of_care_1_IDX" CASCADE;
CREATE INDEX "patient_plan_of_care_1_IDX" ON caliphr.patient_plan_of_care
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "result_supplemental_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."result_supplemental_1_IDX" CASCADE;
CREATE INDEX "result_supplemental_1_IDX" ON caliphr.result_supplemental
	USING btree
	(
	  result_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_problem_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_problem_1_IDX" CASCADE;
CREATE INDEX "patient_problem_1_IDX" ON caliphr.patient_problem
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: "patient_social_history_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."patient_social_history_1_IDX" CASCADE;
CREATE INDEX "patient_social_history_1_IDX" ON caliphr.patient_social_history
	USING btree
	(
	  patient_id ASC NULLS LAST
	);
-- ddl-end --

-- object: caliphr.hqmf_population | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.hqmf_population CASCADE;
CREATE TABLE caliphr.hqmf_population(
	population_id bigint NOT NULL DEFAULT nextval('caliphr.hqmf_population_id_seq'::regclass),
	hqmf_doc_id bigint,
	hqmf_id character varying NOT NULL,
	hqmf_population_id character varying,
	population_type varchar,
	title varchar,
	population_json text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_hqmf_population PRIMARY KEY (population_id)
	 WITH (FILLFACTOR = 100)

);
-- ddl-end --

-- object: caliphr.practice_available_measure_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.practice_available_measure_id_seq CASCADE;
CREATE SEQUENCE caliphr.practice_available_measure_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.practice_available_measure | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.practice_available_measure CASCADE;
CREATE TABLE caliphr.practice_available_measure(
	record_id integer NOT NULL DEFAULT nextval('caliphr.practice_available_measure_id_seq'::regclass),
	group_id integer,
	bundle_id integer,
	cms_id character varying,
	effective_date_start timestamp,
	effective_date_end timestamp,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_practice_available_measure PRIMARY KEY (record_id)

);
-- ddl-end --

-- object: caliphr.ehr_vendor | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.ehr_vendor CASCADE;
CREATE TABLE caliphr.ehr_vendor(
	vendor_id integer NOT NULL DEFAULT nextval('caliphr.ehr_vendor_id_seq'::regclass),
	vendor_name character varying,
	vendor_description text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_ehr_vendor PRIMARY KEY (vendor_id)

);
-- ddl-end --

-- object: caliphr.code_mapping_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.code_mapping_id_seq CASCADE;
CREATE SEQUENCE caliphr.code_mapping_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.code_mapping_type_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.code_mapping_type_id_seq CASCADE;
CREATE SEQUENCE caliphr.code_mapping_type_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.code_mapping_type | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.code_mapping_type CASCADE;
CREATE TABLE caliphr.code_mapping_type(
	type_id integer NOT NULL DEFAULT nextval('caliphr.code_mapping_type_id_seq'::regclass),
	type_name character varying,
	type_description text,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_code_mapping_type PRIMARY KEY (type_id)

);
-- ddl-end --

-- object: caliphr.code_mapping | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.code_mapping CASCADE;
CREATE TABLE caliphr.code_mapping(
	record_id integer NOT NULL DEFAULT nextval('caliphr.code_mapping_id_seq'::regclass),
	group_id integer,
	vendor_id integer,
	type_id integer,
	from_code_id integer,
	from_code_name character varying,
	from_code_system character varying,
	from_display_name character varying,
	is_globally_mapped bool,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_practice_code_mapping PRIMARY KEY (record_id)

);
-- ddl-end --

-- object: "hqmf_document_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."hqmf_document_1_IDX" CASCADE;
CREATE INDEX "hqmf_document_1_IDX" ON caliphr.hqmf_document
	USING btree
	(
	  user_id ASC NULLS LAST,
	  provider_id ASC NULLS LAST,
	  date_disabled ASC NULLS FIRST
	);
-- ddl-end --

-- object: "hqmf_population_1_IDX" | type: INDEX --
-- DROP INDEX IF EXISTS caliphr."hqmf_population_1_IDX" CASCADE;
CREATE INDEX "hqmf_population_1_IDX" ON caliphr.hqmf_population
	USING btree
	(
	  hqmf_doc_id ASC NULLS LAST
	);
-- ddl-end --

-- object: caliphr.patient_encounter_diagnosis_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.patient_encounter_diagnosis_id_seq CASCADE;
CREATE SEQUENCE caliphr.patient_encounter_diagnosis_id_seq
	INCREMENT BY 50
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.patient_encounter_diagnosis | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.patient_encounter_diagnosis CASCADE;
CREATE TABLE caliphr.patient_encounter_diagnosis(
	record_id bigint NOT NULL DEFAULT nextval('caliphr.patient_encounter_diagnosis_id_seq'::regclass),
	encounter_id bigint,
	problem_code_id integer,
	template_id integer,
	status_code_id integer,
	problem_code_description text,
	effective_time_start timestamp,
	effective_time_end timestamp,
	external_id character varying,
	status_code_name character varying,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_patient_encounter_diagnosis PRIMARY KEY (record_id)

);
-- ddl-end --

-- object: patient_info_secure_idx | type: INDEX --
-- DROP INDEX IF EXISTS caliphr.patient_info_secure_idx CASCADE;
CREATE INDEX patient_info_secure_idx ON caliphr.patient_info_secure
	USING btree
	(
	  mrn_hash ASC NULLS LAST
	);
-- ddl-end --

-- object: caliphr.code_mapping_to_code_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS caliphr.code_mapping_to_code_id_seq CASCADE;
CREATE SEQUENCE caliphr.code_mapping_to_code_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --

-- object: caliphr.code_mapping_to_code | type: TABLE --
-- DROP TABLE IF EXISTS caliphr.code_mapping_to_code CASCADE;
CREATE TABLE caliphr.code_mapping_to_code(
	record_id integer NOT NULL DEFAULT nextval('caliphr.code_mapping_to_code_id_seq'::regclass),
	code_mapping_id integer,
	to_code_id integer,
	date_created timestamp DEFAULT now(),
	user_created integer,
	date_updated timestamp DEFAULT now(),
	user_updated integer,
	date_disabled timestamp,
	CONSTRAINT pk_code_mapping_to_code PRIMARY KEY (record_id)

);
-- ddl-end --

-- object: code_description_idx | type: INDEX --
-- DROP INDEX IF EXISTS caliphr.code_description_idx CASCADE;
CREATE INDEX code_description_idx ON caliphr.code
	USING btree
	(
	  description ASC NULLS LAST
	);
-- ddl-end --

-- object: fk_measure_period_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.hqmf_document DROP CONSTRAINT IF EXISTS fk_measure_period_id CASCADE;
ALTER TABLE caliphr.hqmf_document ADD CONSTRAINT fk_measure_period_id FOREIGN KEY (measure_period_id)
REFERENCES caliphr.hqmf_measure_period (measure_period_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_domain_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.hqmf_document DROP CONSTRAINT IF EXISTS fk_domain_id CASCADE;
ALTER TABLE caliphr.hqmf_document ADD CONSTRAINT fk_domain_id FOREIGN KEY (domain_id)
REFERENCES caliphr.domain (domain_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_provider_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.hqmf_document DROP CONSTRAINT IF EXISTS fk_provider_id CASCADE;
ALTER TABLE caliphr.hqmf_document ADD CONSTRAINT fk_provider_id FOREIGN KEY (provider_id)
REFERENCES caliphr.provider (provider_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_bundle_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.hqmf_document DROP CONSTRAINT IF EXISTS fk_bundle_id CASCADE;
ALTER TABLE caliphr.hqmf_document ADD CONSTRAINT fk_bundle_id FOREIGN KEY (bundle_id)
REFERENCES caliphr.bundle (bundle_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_state_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.provider DROP CONSTRAINT IF EXISTS fk_state_id CASCADE;
ALTER TABLE caliphr.provider ADD CONSTRAINT fk_state_id FOREIGN KEY (state_id)
REFERENCES caliphr.state (state_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_type_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.provider DROP CONSTRAINT IF EXISTS fk_type_id CASCADE;
ALTER TABLE caliphr.provider ADD CONSTRAINT fk_type_id FOREIGN KEY (type_id)
REFERENCES caliphr.provider_type (type_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_group_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.provider DROP CONSTRAINT IF EXISTS fk_group_id CASCADE;
ALTER TABLE caliphr.provider ADD CONSTRAINT fk_group_id FOREIGN KEY (group_id)
REFERENCES caliphr.practice_group (group_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.clinical_document DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.clinical_document ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_type_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.clinical_document DROP CONSTRAINT IF EXISTS fk_type_id CASCADE;
ALTER TABLE caliphr.clinical_document ADD CONSTRAINT fk_type_id FOREIGN KEY (type_id)
REFERENCES caliphr.document_type (type_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_parse_status_type_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.clinical_document DROP CONSTRAINT IF EXISTS fk_parse_status_type_id CASCADE;
ALTER TABLE caliphr.clinical_document ADD CONSTRAINT fk_parse_status_type_id FOREIGN KEY (parse_status_type_id)
REFERENCES caliphr.parse_status_type (type_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_vital_sign DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_vital_sign ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_vital_sign DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_vital_sign ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_vital_sign DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.patient_vital_sign ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_status_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_vital_sign DROP CONSTRAINT IF EXISTS fk_status_code_id CASCADE;
ALTER TABLE caliphr.patient_vital_sign ADD CONSTRAINT fk_status_code_id FOREIGN KEY (status_code_id)
REFERENCES caliphr.status_code (status_code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_vital_sign DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_vital_sign ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_value_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_vital_sign DROP CONSTRAINT IF EXISTS fk_value_code_id CASCADE;
ALTER TABLE caliphr.patient_vital_sign ADD CONSTRAINT fk_value_code_id FOREIGN KEY (value_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_vital_sign DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_vital_sign ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_advanced_directive DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_advanced_directive ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_advanced_directive DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_advanced_directive ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_advanced_directive DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.patient_advanced_directive ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_advanced_directive DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_advanced_directive ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_advanced_directive DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_advanced_directive ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_allergy DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_allergy ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_allergy DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_allergy ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_allergy DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.patient_allergy ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_reaction_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_allergy DROP CONSTRAINT IF EXISTS fk_reaction_code_id CASCADE;
ALTER TABLE caliphr.patient_allergy ADD CONSTRAINT fk_reaction_code_id FOREIGN KEY (reaction_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_severity_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_allergy DROP CONSTRAINT IF EXISTS fk_severity_code_id CASCADE;
ALTER TABLE caliphr.patient_allergy ADD CONSTRAINT fk_severity_code_id FOREIGN KEY (severity_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_substance_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_allergy DROP CONSTRAINT IF EXISTS fk_substance_code_id CASCADE;
ALTER TABLE caliphr.patient_allergy ADD CONSTRAINT fk_substance_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_status_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_allergy DROP CONSTRAINT IF EXISTS fk_status_code_id CASCADE;
ALTER TABLE caliphr.patient_allergy ADD CONSTRAINT fk_status_code_id FOREIGN KEY (status_code_id)
REFERENCES caliphr.status_code (status_code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_allergy DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_allergy ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_allergy DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_allergy ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_reason_for_visit DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_reason_for_visit ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_reason_for_visit DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_reason_for_visit ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_reason_for_visit DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.patient_reason_for_visit ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_reason_for_visit DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_reason_for_visit ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_reason_for_visit DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_reason_for_visit ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_family_history DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_family_history ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_family_history DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_family_history ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_family_history DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.patient_family_history ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_diagnosis_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_family_history DROP CONSTRAINT IF EXISTS fk_diagnosis_code_id CASCADE;
ALTER TABLE caliphr.patient_family_history ADD CONSTRAINT fk_diagnosis_code_id FOREIGN KEY (diagnosis_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_family_history DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_family_history ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_family_history DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_family_history ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_func_cog_status DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_func_cog_status ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_func_cog_status DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_func_cog_status ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_func_cog_status DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.patient_func_cog_status ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_value_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_func_cog_status DROP CONSTRAINT IF EXISTS fk_value_code_id CASCADE;
ALTER TABLE caliphr.patient_func_cog_status ADD CONSTRAINT fk_value_code_id FOREIGN KEY (value_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_status_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_func_cog_status DROP CONSTRAINT IF EXISTS fk_status_code_id CASCADE;
ALTER TABLE caliphr.patient_func_cog_status ADD CONSTRAINT fk_status_code_id FOREIGN KEY (status_code_id)
REFERENCES caliphr.status_code (status_code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_func_cog_status DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_func_cog_status ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_func_cog_status DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_func_cog_status ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_encounter DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_encounter ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_encounter DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_encounter ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_encounter DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.patient_encounter ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_encounter DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_encounter ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_encounter DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_encounter ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_facility_location_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_encounter DROP CONSTRAINT IF EXISTS fk_facility_location_code_id CASCADE;
ALTER TABLE caliphr.patient_encounter ADD CONSTRAINT fk_facility_location_code_id FOREIGN KEY (facility_location_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_provider_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_encounter DROP CONSTRAINT IF EXISTS fk_provider_id CASCADE;
ALTER TABLE caliphr.patient_encounter ADD CONSTRAINT fk_provider_id FOREIGN KEY (provider_id)
REFERENCES caliphr.provider (provider_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_status_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_encounter DROP CONSTRAINT IF EXISTS fk_status_code_id CASCADE;
ALTER TABLE caliphr.patient_encounter ADD CONSTRAINT fk_status_code_id FOREIGN KEY (status_code_id)
REFERENCES caliphr.status_code (status_code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_instruction DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_instruction ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_instruction DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_instruction ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_instruction DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.patient_instruction ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_status_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_instruction DROP CONSTRAINT IF EXISTS fk_status_code_id CASCADE;
ALTER TABLE caliphr.patient_instruction ADD CONSTRAINT fk_status_code_id FOREIGN KEY (status_code_id)
REFERENCES caliphr.patient_instruction (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_instruction DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_instruction ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_instruction DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_instruction ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_result DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_result ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_result DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_result ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_result DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.patient_result ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_status_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_result DROP CONSTRAINT IF EXISTS fk_status_code_id CASCADE;
ALTER TABLE caliphr.patient_result ADD CONSTRAINT fk_status_code_id FOREIGN KEY (status_code_id)
REFERENCES caliphr.status_code (status_code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_result DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_result ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_value_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_result DROP CONSTRAINT IF EXISTS fk_value_code_id CASCADE;
ALTER TABLE caliphr.patient_result ADD CONSTRAINT fk_value_code_id FOREIGN KEY (value_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_result DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_result ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_reason_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_result DROP CONSTRAINT IF EXISTS fk_reason_code_id CASCADE;
ALTER TABLE caliphr.patient_result ADD CONSTRAINT fk_reason_code_id FOREIGN KEY (reason_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_connected_record_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_result DROP CONSTRAINT IF EXISTS fk_connected_record_id CASCADE;
ALTER TABLE caliphr.patient_result ADD CONSTRAINT fk_connected_record_id FOREIGN KEY (connected_record_id)
REFERENCES caliphr.patient_result (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_immunization DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_immunization ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_immunization DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_immunization ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_product_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_immunization DROP CONSTRAINT IF EXISTS fk_product_code_id CASCADE;
ALTER TABLE caliphr.patient_immunization ADD CONSTRAINT fk_product_code_id FOREIGN KEY (product_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_status_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_immunization DROP CONSTRAINT IF EXISTS fk_status_code_id CASCADE;
ALTER TABLE caliphr.patient_immunization ADD CONSTRAINT fk_status_code_id FOREIGN KEY (status_code_id)
REFERENCES caliphr.status_code (status_code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_immunization DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_immunization ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_immunization DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_immunization ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_procedure DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_procedure ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_procedure DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_procedure ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_procedure DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.patient_procedure ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_status_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_procedure DROP CONSTRAINT IF EXISTS fk_status_code_id CASCADE;
ALTER TABLE caliphr.patient_procedure ADD CONSTRAINT fk_status_code_id FOREIGN KEY (status_code_id)
REFERENCES caliphr.status_code (status_code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_procedure DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_procedure ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_value_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_procedure DROP CONSTRAINT IF EXISTS fk_value_code_id CASCADE;
ALTER TABLE caliphr.patient_procedure ADD CONSTRAINT fk_value_code_id FOREIGN KEY (value_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_procedure DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_procedure ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_ordinality_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_procedure DROP CONSTRAINT IF EXISTS fk_ordinality_code_id CASCADE;
ALTER TABLE caliphr.patient_procedure ADD CONSTRAINT fk_ordinality_code_id FOREIGN KEY (ordinality_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_reason_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_procedure DROP CONSTRAINT IF EXISTS fk_reason_code_id CASCADE;
ALTER TABLE caliphr.patient_procedure ADD CONSTRAINT fk_reason_code_id FOREIGN KEY (reason_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_result_value_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_procedure DROP CONSTRAINT IF EXISTS fk_result_value_code_id CASCADE;
ALTER TABLE caliphr.patient_procedure ADD CONSTRAINT fk_result_value_code_id FOREIGN KEY (result_value_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_connected_record_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_procedure DROP CONSTRAINT IF EXISTS fk_connected_record_id CASCADE;
ALTER TABLE caliphr.patient_procedure ADD CONSTRAINT fk_connected_record_id FOREIGN KEY (connected_record_id)
REFERENCES caliphr.patient_procedure (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_reason_for_referral DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_reason_for_referral ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_reason_for_referral DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.patient_reason_for_referral ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_reason_for_referral DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_reason_for_referral ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_reason_for_referral DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_reason_for_referral ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_reason_for_referral DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_reason_for_referral ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_group_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_info DROP CONSTRAINT IF EXISTS fk_group_id CASCADE;
ALTER TABLE caliphr.patient_info ADD CONSTRAINT fk_group_id FOREIGN KEY (group_id)
REFERENCES caliphr.practice_group (group_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_gender_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_info DROP CONSTRAINT IF EXISTS fk_gender_code_id CASCADE;
ALTER TABLE caliphr.patient_info ADD CONSTRAINT fk_gender_code_id FOREIGN KEY (gender_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_race_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_info DROP CONSTRAINT IF EXISTS fk_race_code_id CASCADE;
ALTER TABLE caliphr.patient_info ADD CONSTRAINT fk_race_code_id FOREIGN KEY (race_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_ethnicity_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_info DROP CONSTRAINT IF EXISTS fk_ethnicity_code_id CASCADE;
ALTER TABLE caliphr.patient_info ADD CONSTRAINT fk_ethnicity_code_id FOREIGN KEY (ethnicity_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_bundle_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.value_set DROP CONSTRAINT IF EXISTS fk_bundle_id CASCADE;
ALTER TABLE caliphr.value_set ADD CONSTRAINT fk_bundle_id FOREIGN KEY (bundle_id)
REFERENCES caliphr.bundle (bundle_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_system_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.code DROP CONSTRAINT IF EXISTS fk_code_system_id CASCADE;
ALTER TABLE caliphr.code ADD CONSTRAINT fk_code_system_id FOREIGN KEY (code_system_id)
REFERENCES caliphr.code_system (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.value_set_code DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.value_set_code ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_value_set_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.value_set_code DROP CONSTRAINT IF EXISTS fk_value_set_id CASCADE;
ALTER TABLE caliphr.value_set_code ADD CONSTRAINT fk_value_set_id FOREIGN KEY (value_set_id)
REFERENCES caliphr.value_set (value_set_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_medical_equipment DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_medical_equipment ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_medical_equipment DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_medical_equipment ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_medical_equipment DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.patient_medical_equipment ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_status_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_medical_equipment DROP CONSTRAINT IF EXISTS fk_status_code_id CASCADE;
ALTER TABLE caliphr.patient_medical_equipment ADD CONSTRAINT fk_status_code_id FOREIGN KEY (status_code_id)
REFERENCES caliphr.status_code (status_code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_medical_equipment DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_medical_equipment ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_medical_equipment DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_medical_equipment ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_reason_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_medical_equipment DROP CONSTRAINT IF EXISTS fk_reason_code_id CASCADE;
ALTER TABLE caliphr.patient_medical_equipment ADD CONSTRAINT fk_reason_code_id FOREIGN KEY (reason_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_hqmf_doc_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.hqmf_attribute DROP CONSTRAINT IF EXISTS fk_hqmf_doc_id CASCADE;
ALTER TABLE caliphr.hqmf_attribute ADD CONSTRAINT fk_hqmf_doc_id FOREIGN KEY (hqmf_doc_id)
REFERENCES caliphr.hqmf_document (hqmf_doc_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_phone_number DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_phone_number ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_payer DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_payer ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_payer DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_payer ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_payer DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_payer ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_status_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_payer DROP CONSTRAINT IF EXISTS fk_status_code_id CASCADE;
ALTER TABLE caliphr.patient_payer ADD CONSTRAINT fk_status_code_id FOREIGN KEY (status_code_id)
REFERENCES caliphr.status_code (status_code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_payer DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_payer ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_payer_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_payer DROP CONSTRAINT IF EXISTS fk_payer_code_id CASCADE;
ALTER TABLE caliphr.patient_payer ADD CONSTRAINT fk_payer_code_id FOREIGN KEY (payer_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_medication DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_medication ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_medication DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_medication ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_product_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_medication DROP CONSTRAINT IF EXISTS fk_product_code_id CASCADE;
ALTER TABLE caliphr.patient_medication ADD CONSTRAINT fk_product_code_id FOREIGN KEY (product_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_status_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_medication DROP CONSTRAINT IF EXISTS fk_status_code_id CASCADE;
ALTER TABLE caliphr.patient_medication ADD CONSTRAINT fk_status_code_id FOREIGN KEY (status_code_id)
REFERENCES caliphr.status_code (status_code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_administration_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_medication DROP CONSTRAINT IF EXISTS fk_administration_code_id CASCADE;
ALTER TABLE caliphr.patient_medication ADD CONSTRAINT fk_administration_code_id FOREIGN KEY (administration_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_medication DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_medication ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_medication DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_medication ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_reason_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_medication DROP CONSTRAINT IF EXISTS fk_reason_code_id CASCADE;
ALTER TABLE caliphr.patient_medication ADD CONSTRAINT fk_reason_code_id FOREIGN KEY (reason_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_hqmf_document_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.hqmf_population_set DROP CONSTRAINT IF EXISTS fk_hqmf_document_id CASCADE;
ALTER TABLE caliphr.hqmf_population_set ADD CONSTRAINT fk_hqmf_document_id FOREIGN KEY (hqmf_document_id)
REFERENCES caliphr.hqmf_document (hqmf_doc_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_hqmf_population_set_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.result DROP CONSTRAINT IF EXISTS fk_hqmf_population_set_id CASCADE;
ALTER TABLE caliphr.result ADD CONSTRAINT fk_hqmf_population_set_id FOREIGN KEY (hqmf_population_set_id)
REFERENCES caliphr.hqmf_population_set (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_hqmf_doc_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.hqmf_data_criteria DROP CONSTRAINT IF EXISTS fk_hqmf_doc_id CASCADE;
ALTER TABLE caliphr.hqmf_data_criteria ADD CONSTRAINT fk_hqmf_doc_id FOREIGN KEY (hqmf_doc_id)
REFERENCES caliphr.hqmf_document (hqmf_doc_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_result_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.result_patient DROP CONSTRAINT IF EXISTS fk_result_id CASCADE;
ALTER TABLE caliphr.result_patient ADD CONSTRAINT fk_result_id FOREIGN KEY (result_id)
REFERENCES caliphr.result (result_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.result_patient DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.result_patient ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_plan_of_care DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_plan_of_care ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_plan_of_care DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_plan_of_care ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_plan_of_care DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.patient_plan_of_care ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_status_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_plan_of_care DROP CONSTRAINT IF EXISTS fk_status_code_id CASCADE;
ALTER TABLE caliphr.patient_plan_of_care ADD CONSTRAINT fk_status_code_id FOREIGN KEY (status_code_id)
REFERENCES caliphr.status_code (status_code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_plan_of_care DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_plan_of_care ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_plan_of_care DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_plan_of_care ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_negation_detail DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.patient_negation_detail ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_organization_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.practice_group DROP CONSTRAINT IF EXISTS fk_organization_id CASCADE;
ALTER TABLE caliphr.practice_group ADD CONSTRAINT fk_organization_id FOREIGN KEY (organization_id)
REFERENCES caliphr.organization (organization_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_type_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.practice_group DROP CONSTRAINT IF EXISTS fk_type_id CASCADE;
ALTER TABLE caliphr.practice_group ADD CONSTRAINT fk_type_id FOREIGN KEY (type_id)
REFERENCES caliphr.practice_group_type (type_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_vendor_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.practice_group DROP CONSTRAINT IF EXISTS fk_vendor_id CASCADE;
ALTER TABLE caliphr.practice_group ADD CONSTRAINT fk_vendor_id FOREIGN KEY (vendor_id)
REFERENCES caliphr.ehr_vendor (vendor_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_type_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.organization DROP CONSTRAINT IF EXISTS fk_type_id CASCADE;
ALTER TABLE caliphr.organization ADD CONSTRAINT fk_type_id FOREIGN KEY (type_id)
REFERENCES caliphr.organization_type (type_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_provider_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.provider_phone_number DROP CONSTRAINT IF EXISTS fk_provider_id CASCADE;
ALTER TABLE caliphr.provider_phone_number ADD CONSTRAINT fk_provider_id FOREIGN KEY (provider_id)
REFERENCES caliphr.provider (provider_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_user_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.application_user_security DROP CONSTRAINT IF EXISTS fk_user_id CASCADE;
ALTER TABLE caliphr.application_user_security ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id)
REFERENCES caliphr.application_user (user_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_provider_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.application_user_security DROP CONSTRAINT IF EXISTS fk_provider_id CASCADE;
ALTER TABLE caliphr.application_user_security ADD CONSTRAINT fk_provider_id FOREIGN KEY (provider_id)
REFERENCES caliphr.provider (provider_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_role_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.application_user_security DROP CONSTRAINT IF EXISTS fk_role_id CASCADE;
ALTER TABLE caliphr.application_user_security ADD CONSTRAINT fk_role_id FOREIGN KEY (role_id)
REFERENCES caliphr.security_role (role_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_result_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.result_supplemental DROP CONSTRAINT IF EXISTS fk_result_id CASCADE;
ALTER TABLE caliphr.result_supplemental ADD CONSTRAINT fk_result_id FOREIGN KEY (result_id)
REFERENCES caliphr.result (result_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.result_supplemental DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.result_supplemental ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_info_secure DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_info_secure ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_problem DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_problem ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_problem DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_problem ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_problem DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.patient_problem ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_status_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_problem DROP CONSTRAINT IF EXISTS fk_status_code_id CASCADE;
ALTER TABLE caliphr.patient_problem ADD CONSTRAINT fk_status_code_id FOREIGN KEY (status_code_id)
REFERENCES caliphr.status_code (status_code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_problem_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_problem DROP CONSTRAINT IF EXISTS fk_problem_code_id CASCADE;
ALTER TABLE caliphr.patient_problem ADD CONSTRAINT fk_problem_code_id FOREIGN KEY (problem_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_problem DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_problem ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_problem DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_problem ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_ordinality_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_problem DROP CONSTRAINT IF EXISTS fk_ordinality_code_id CASCADE;
ALTER TABLE caliphr.patient_problem ADD CONSTRAINT fk_ordinality_code_id FOREIGN KEY (ordinality_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_severity_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_problem DROP CONSTRAINT IF EXISTS fk_severity_code_id CASCADE;
ALTER TABLE caliphr.patient_problem ADD CONSTRAINT fk_severity_code_id FOREIGN KEY (severity_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_connected_record_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_problem DROP CONSTRAINT IF EXISTS fk_connected_record_id CASCADE;
ALTER TABLE caliphr.patient_problem ADD CONSTRAINT fk_connected_record_id FOREIGN KEY (connected_record_id)
REFERENCES caliphr.patient_problem (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_laterality_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_problem DROP CONSTRAINT IF EXISTS fk_laterality_code_id CASCADE;
ALTER TABLE caliphr.patient_problem ADD CONSTRAINT fk_laterality_code_id FOREIGN KEY (laterality_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_record_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_phone_number_secure DROP CONSTRAINT IF EXISTS fk_record_id CASCADE;
ALTER TABLE caliphr.patient_phone_number_secure ADD CONSTRAINT fk_record_id FOREIGN KEY (record_id)
REFERENCES caliphr.patient_phone_number (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_document_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.clinical_document_parse_error DROP CONSTRAINT IF EXISTS fk_document_id CASCADE;
ALTER TABLE caliphr.clinical_document_parse_error ADD CONSTRAINT fk_document_id FOREIGN KEY (document_id)
REFERENCES caliphr.clinical_document (document_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_user_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.application_user_audit DROP CONSTRAINT IF EXISTS fk_user_id CASCADE;
ALTER TABLE caliphr.application_user_audit ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id)
REFERENCES caliphr.application_user (user_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_audit_type_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.application_user_audit DROP CONSTRAINT IF EXISTS fk_audit_type_id CASCADE;
ALTER TABLE caliphr.application_user_audit ADD CONSTRAINT fk_audit_type_id FOREIGN KEY (audit_type_id)
REFERENCES caliphr.audit_type (type_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_user_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.application_user_password_request DROP CONSTRAINT IF EXISTS fk_user_id CASCADE;
ALTER TABLE caliphr.application_user_password_request ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id)
REFERENCES caliphr.application_user (user_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_social_history DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.patient_social_history ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_patient_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_social_history DROP CONSTRAINT IF EXISTS fk_patient_id CASCADE;
ALTER TABLE caliphr.patient_social_history ADD CONSTRAINT fk_patient_id FOREIGN KEY (patient_id)
REFERENCES caliphr.patient_info (patient_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_social_history DROP CONSTRAINT IF EXISTS fk_code_id CASCADE;
ALTER TABLE caliphr.patient_social_history ADD CONSTRAINT fk_code_id FOREIGN KEY (code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_status_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_social_history DROP CONSTRAINT IF EXISTS fk_status_code_id CASCADE;
ALTER TABLE caliphr.patient_social_history ADD CONSTRAINT fk_status_code_id FOREIGN KEY (status_code_id)
REFERENCES caliphr.status_code (status_code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_value_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_social_history DROP CONSTRAINT IF EXISTS fk_value_code_id CASCADE;
ALTER TABLE caliphr.patient_social_history ADD CONSTRAINT fk_value_code_id FOREIGN KEY (value_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_social_history DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_social_history ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_negation_detail_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_social_history DROP CONSTRAINT IF EXISTS fk_negation_detail_id CASCADE;
ALTER TABLE caliphr.patient_social_history ADD CONSTRAINT fk_negation_detail_id FOREIGN KEY (negation_detail_id)
REFERENCES caliphr.patient_negation_detail (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_user_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.application_user_password_history DROP CONSTRAINT IF EXISTS fk_user_id CASCADE;
ALTER TABLE caliphr.application_user_password_history ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id)
REFERENCES caliphr.application_user (user_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_hqmf_doc_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.hqmf_population DROP CONSTRAINT IF EXISTS fk_hqmf_doc_id CASCADE;
ALTER TABLE caliphr.hqmf_population ADD CONSTRAINT fk_hqmf_doc_id FOREIGN KEY (hqmf_doc_id)
REFERENCES caliphr.hqmf_document (hqmf_doc_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_bundle_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.practice_available_measure DROP CONSTRAINT IF EXISTS fk_bundle_id CASCADE;
ALTER TABLE caliphr.practice_available_measure ADD CONSTRAINT fk_bundle_id FOREIGN KEY (bundle_id)
REFERENCES caliphr.bundle (bundle_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_group_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.practice_available_measure DROP CONSTRAINT IF EXISTS fk_group_id CASCADE;
ALTER TABLE caliphr.practice_available_measure ADD CONSTRAINT fk_group_id FOREIGN KEY (group_id)
REFERENCES caliphr.practice_group (group_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_type_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.code_mapping DROP CONSTRAINT IF EXISTS fk_type_id CASCADE;
ALTER TABLE caliphr.code_mapping ADD CONSTRAINT fk_type_id FOREIGN KEY (type_id)
REFERENCES caliphr.code_mapping_type (type_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_from_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.code_mapping DROP CONSTRAINT IF EXISTS fk_from_code_id CASCADE;
ALTER TABLE caliphr.code_mapping ADD CONSTRAINT fk_from_code_id FOREIGN KEY (from_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_vendor_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.code_mapping DROP CONSTRAINT IF EXISTS fk_vendor_id CASCADE;
ALTER TABLE caliphr.code_mapping ADD CONSTRAINT fk_vendor_id FOREIGN KEY (vendor_id)
REFERENCES caliphr.ehr_vendor (vendor_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_group_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.code_mapping DROP CONSTRAINT IF EXISTS fk_group_id CASCADE;
ALTER TABLE caliphr.code_mapping ADD CONSTRAINT fk_group_id FOREIGN KEY (group_id)
REFERENCES caliphr.practice_group (group_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_encounter_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_encounter_diagnosis DROP CONSTRAINT IF EXISTS fk_encounter_id CASCADE;
ALTER TABLE caliphr.patient_encounter_diagnosis ADD CONSTRAINT fk_encounter_id FOREIGN KEY (encounter_id)
REFERENCES caliphr.patient_encounter (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_problem_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_encounter_diagnosis DROP CONSTRAINT IF EXISTS fk_problem_code_id CASCADE;
ALTER TABLE caliphr.patient_encounter_diagnosis ADD CONSTRAINT fk_problem_code_id FOREIGN KEY (problem_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_status_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_encounter_diagnosis DROP CONSTRAINT IF EXISTS fk_status_code_id CASCADE;
ALTER TABLE caliphr.patient_encounter_diagnosis ADD CONSTRAINT fk_status_code_id FOREIGN KEY (status_code_id)
REFERENCES caliphr.status_code (status_code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_template_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.patient_encounter_diagnosis DROP CONSTRAINT IF EXISTS fk_template_id CASCADE;
ALTER TABLE caliphr.patient_encounter_diagnosis ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id)
REFERENCES caliphr.template_root (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_code_mapping_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.code_mapping_to_code DROP CONSTRAINT IF EXISTS fk_code_mapping_id CASCADE;
ALTER TABLE caliphr.code_mapping_to_code ADD CONSTRAINT fk_code_mapping_id FOREIGN KEY (code_mapping_id)
REFERENCES caliphr.code_mapping (record_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fk_to_code_id | type: CONSTRAINT --
-- ALTER TABLE caliphr.code_mapping_to_code DROP CONSTRAINT IF EXISTS fk_to_code_id CASCADE;
ALTER TABLE caliphr.code_mapping_to_code ADD CONSTRAINT fk_to_code_id FOREIGN KEY (to_code_id)
REFERENCES caliphr.code (code_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --


