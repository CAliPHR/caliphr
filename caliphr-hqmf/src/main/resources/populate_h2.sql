CREATE SCHEMA IF NOT EXISTS caliphr;

CREATE TABLE caliphr.patient_allergy
(
  record_id bigint NOT NULL primary key,
  patient_id integer,
  code_id integer,
  effective_time_start long,
  effective_time_end long,
  status_code_name character varying,
);

CREATE INDEX caliphr.patient_allergy_1_IDX ON caliphr.patient_allergy(patient_id);
CREATE INDEX caliphr.patient_allergy_2_IDX ON caliphr.patient_allergy(code_id);
-- CREATE INDEX caliphr.patient_allergy_2_IDX ON caliphr.patient_allergy(effective_time_start);
-- CREATE INDEX caliphr.patient_allergy_3_IDX ON caliphr.patient_allergy(effective_time_end);

CREATE TABLE caliphr.patient_immunization
(
  record_id bigint NOT NULL primary key,
  patient_id integer,
  effective_time_start long,
  effective_time_end long,
  status_code_name character varying,
  product_code_id integer
);

CREATE INDEX caliphr.patient_immunization_1_IDX ON caliphr.patient_immunization(patient_id);
-- CREATE INDEX caliphr.patient_immunization_3_IDX ON caliphr.patient_immunization(effective_time_start);
-- CREATE INDEX caliphr.patient_immunization_4_IDX ON caliphr.patient_immunization(effective_time_end);
CREATE INDEX caliphr.patient_immunization_5_IDX ON caliphr.patient_immunization(product_code_id);

CREATE TABLE caliphr.patient_medication
(
  record_id bigint NOT NULL primary key,
  patient_id integer,
  effective_time_start long,
  effective_time_end long,
  status_code_name character varying,
  product_code_id integer,
  reason_code_id integer
);

CREATE INDEX caliphr.patient_medication_1_IDX ON caliphr.patient_medication(patient_id);
-- CREATE INDEX caliphr.patient_medication_3_IDX ON caliphr.patient_medication(effective_time_start);
-- CREATE INDEX caliphr.patient_medication_4_IDX ON caliphr.patient_medication(effective_time_end);
CREATE INDEX caliphr.patient_medication_5_IDX ON caliphr.patient_medication(product_code_id);

-- there is no primary key for patient_problem, as connected records are inserted using the same record_id
CREATE TABLE caliphr.patient_problem
(
  record_id bigint NOT NULL,
  patient_id integer,
  code_id integer,
  effective_time_start long,
  effective_time_end long,
  status_code_name character varying,
  problem_code_id integer,
  ordinality_code_id integer,
  severity_code_id integer
);

CREATE INDEX caliphr.patient_problem_0_IDX ON caliphr.patient_problem(record_id);
CREATE INDEX caliphr.patient_problem_1_IDX ON caliphr.patient_problem(patient_id);
CREATE INDEX caliphr.patient_problem_2_IDX ON caliphr.patient_problem(code_id);
-- CREATE INDEX caliphr.patient_problem_3_IDX ON caliphr.patient_problem(effective_time_start);
-- CREATE INDEX caliphr.patient_problem_4_IDX ON caliphr.patient_problem(effective_time_end);
CREATE INDEX caliphr.patient_problem_5_IDX ON caliphr.patient_problem(problem_code_id);

-- there is no primary key for patient_procedure, as connected records are inserted using the same record_id
CREATE TABLE caliphr.patient_procedure
(
  record_id bigint NOT NULL,
  patient_id integer,
  code_id integer,
  effective_time_start long,
  effective_time_end long,
  status_code_name character varying,
  value_string character varying,
  value_code_id integer,
  result_value_code_id integer,
  negation_code_id integer,
  reason_code_id integer
);

CREATE INDEX caliphr.patient_procedure_0_IDX ON caliphr.patient_procedure(record_id);
CREATE INDEX caliphr.patient_procedure_1_IDX ON caliphr.patient_procedure(patient_id);
CREATE INDEX caliphr.patient_procedure_2_IDX ON caliphr.patient_procedure(code_id);
-- CREATE INDEX caliphr.patient_procedure_3_IDX ON caliphr.patient_procedure(effective_time_start);
-- CREATE INDEX caliphr.patient_procedure_4_IDX ON caliphr.patient_procedure(effective_time_end);
CREATE INDEX caliphr.patient_procedure_5_IDX ON caliphr.patient_procedure(value_string);
CREATE INDEX caliphr.patient_procedure_6_IDX ON caliphr.patient_procedure(value_code_id);

CREATE TABLE caliphr.patient_plan_of_care
(
  record_id bigint NOT NULL primary key,
  patient_id integer,
  code_id integer,
  effective_time_start long,
  effective_time_end long,
  status_code_name character varying
);

CREATE INDEX caliphr.patient_plan_of_care_1_IDX ON caliphr.patient_plan_of_care(patient_id);
CREATE INDEX caliphr.patient_plan_of_care_2_IDX ON caliphr.patient_plan_of_care(code_id);
-- CREATE INDEX caliphr.patient_plan_of_care_3_IDX ON caliphr.patient_plan_of_care(effective_time_start);
-- CREATE INDEX caliphr.patient_plan_of_care_4_IDX ON caliphr.patient_plan_of_care(effective_time_end);

CREATE TABLE caliphr.patient_social_history
(
  record_id bigint NOT NULL primary key,
  patient_id integer,
  code_id integer,
  value_code_id integer,
  effective_time_start long,
  effective_time_end long,
  status_code_name character varying
);

CREATE INDEX caliphr.patient_social_history_1_IDX ON caliphr.patient_social_history(patient_id);
CREATE INDEX caliphr.patient_social_history_2_IDX ON caliphr.patient_social_history(code_id);
-- CREATE INDEX caliphr.patient_social_history_3_IDX ON caliphr.patient_social_history(effective_time_start);
-- CREATE INDEX caliphr.patient_social_history_4_IDX ON caliphr.patient_social_history(effective_time_end);

CREATE TABLE caliphr.patient_encounter
(
  record_id bigint NOT NULL primary key,
  patient_id integer,
  code_id integer,
  effective_time_start long,
  effective_time_end long,
  facility_location_code_id integer
);

CREATE INDEX caliphr.patient_encounter_1_IDX ON caliphr.patient_encounter(patient_id);
CREATE INDEX caliphr.patient_encounter_2_IDX ON caliphr.patient_encounter(code_id);
-- CREATE INDEX caliphr.patient_encounter_3_IDX ON caliphr.patient_encounter(effective_time_start);
-- CREATE INDEX caliphr.patient_encounter_4_IDX ON caliphr.patient_encounter(effective_time_end);

CREATE TABLE caliphr.patient_medical_equipment
(
  record_id bigint primary key,
  patient_id integer,
  code_id integer,
  effective_time_start long,
  effective_time_end long,
  status_code_name character varying
);

CREATE INDEX caliphr.patient_medical_equipment_1_IDX ON caliphr.patient_medical_equipment(patient_id);
CREATE INDEX caliphr.patient_medical_equipment_2_IDX ON caliphr.patient_medical_equipment(code_id);
-- CREATE INDEX caliphr.patient_medical_equipment_3_IDX ON caliphr.patient_medical_equipment(effective_time_start);
-- CREATE INDEX caliphr.patient_medical_equipment_4_IDX ON caliphr.patient_medical_equipment(effective_time_end);

CREATE TABLE caliphr.patient_vital_sign
(
  record_id bigint NOT NULL primary key,
  patient_id integer,
  code_id integer,
  effective_time_start long,
  effective_time_end long,
  status_code_name character varying,
  record_value character varying,
  record_value_unit character varying,
  value_code_id integer
);

CREATE INDEX caliphr.patient_vital_sign_1_IDX ON caliphr.patient_vital_sign(patient_id);
CREATE INDEX caliphr.patient_vital_sign_2_IDX ON caliphr.patient_vital_sign(code_id);
-- CREATE INDEX caliphr.patient_vital_sign_3_IDX ON caliphr.patient_vital_sign(effective_time_start);
-- CREATE INDEX caliphr.patient_vital_sign_4_IDX ON caliphr.patient_vital_sign(effective_time_end);
CREATE INDEX caliphr.patient_vital_sign_5_IDX ON caliphr.patient_vital_sign(record_value);
CREATE INDEX caliphr.patient_vital_sign_6_IDX ON caliphr.patient_vital_sign(value_code_id);

-- there is no primary key for patient_result, as connected records are inserted using the same record_id
CREATE TABLE caliphr.patient_result
(
  record_id bigint NOT NULL,
  patient_id integer,
  code_id integer,
  effective_time_start long,
  effective_time_end long,
  status_code_name character varying,
  result_value character varying,
  result_value_unit character varying,
  value_code_id integer
);

CREATE INDEX caliphr.patient_result_0_IDX ON caliphr.patient_result(record_id);
CREATE INDEX caliphr.patient_result_1_IDX ON caliphr.patient_result(patient_id);
CREATE INDEX caliphr.patient_result_2_IDX ON caliphr.patient_result(code_id);
-- CREATE INDEX caliphr.patient_result_2_IDX ON caliphr.patient_result(effective_time_start);
-- CREATE INDEX caliphr.patient_result_3_IDX ON caliphr.patient_result(effective_time_end);
CREATE INDEX caliphr.patient_result_3_IDX ON caliphr.patient_result(result_value);
CREATE INDEX caliphr.patient_result_4_IDX ON caliphr.patient_result(value_code_id);

CREATE TABLE caliphr.patient_info
(
  patient_id integer NOT NULL primary key,
  gender character varying,
  birth_time long,
  death_date long,
);
CREATE INDEX caliphr.patient_info_1_IDX ON caliphr.patient_info(birth_time);
CREATE INDEX caliphr.patient_info_2_IDX ON caliphr.patient_info(gender);
