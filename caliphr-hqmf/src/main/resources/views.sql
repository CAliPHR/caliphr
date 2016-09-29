CREATE VIEW caliphr.allergies AS
	SELECT patient_id, effective_time_start, effective_time_end, 
		code_id, status_code_name, record_id as id, null as negation_code_id
	FROM caliphr.patient_allergy;

/* allMedications => medications, immunizations */
CREATE VIEW caliphr.allMedications AS
	SELECT patient_id, effective_time_start, effective_time_end, 
		status_code_name, record_id as id, product_code_id , null as negation_code_id, reason_code_id
	FROM caliphr.patient_medication
UNION
	SELECT patient_id, effective_time_start, effective_time_end, 
		status_code_name, record_id as id, product_code_id, null as negation_code_id, null as reason_code_id
	FROM caliphr.patient_immunization;

/* allProblems => conditions, socialHistories, procedures */
CREATE VIEW caliphr.allProblems AS
	SELECT patient_id, effective_time_start, effective_time_end,
		problem_code_id as code_id, status_code_name, record_id as id, null as negation_code_id, 
		ordinality_code_id, severity_code_id
	FROM caliphr.patient_problem p
UNION
	SELECT patient_id, effective_time_start, effective_time_end, 
		value_code_id, status_code_name, record_id as id, null as negation_code_id, 
		null as ordinality_code_id, null as severity_code_id
	FROM caliphr.patient_social_history
UNION
	SELECT patient_id, effective_time_start, effective_time_end, 
		code_id, status_code_name, record_id as id, negation_code_id, 
		null as ordinality_code_id, null as severity_code_id
	FROM caliphr.patient_procedure;

/* allProcedures => procedures, immunizations, medications */
CREATE VIEW caliphr.allProcedures AS
	SELECT patient_id, effective_time_start, effective_time_end, 
		code_id, status_code_name, record_id as id, negation_code_id, reason_code_id
	FROM caliphr.patient_procedure
UNION
	SELECT patient_id, effective_time_start, effective_time_end, 
		product_code_id, status_code_name, record_id as id, null as negation_code_id, null as reason_code_id
	FROM caliphr.patient_immunization
UNION
	SELECT patient_id, effective_time_start, effective_time_end, 
		product_code_id, status_code_name, record_id as id, null as negation_code_id, reason_code_id
	FROM caliphr.patient_medication;

CREATE VIEW caliphr.encounters AS
	SELECT patient_id, effective_time_start, effective_time_end, 
		code_id, record_id as id, null as negation_code_id, facility_location_code_id
	FROM caliphr.patient_encounter;

/* laboratoryTests => results, vitalSigns */
CREATE VIEW caliphr.laboratoryTests AS
	SELECT patient_id, effective_time_start, effective_time_end, 
		code_id, status_code_name, record_id as id, result_value as record_value, result_value_unit as record_value_unit, value_code_id, null as negation_code_id
	FROM caliphr.patient_result
UNION
	SELECT patient_id, effective_time_start, effective_time_end, 
		code_id, status_code_name, record_id as id,	record_value, record_value_unit, value_code_id, null as negation_code_id
	FROM caliphr.patient_vital_sign;

CREATE VIEW caliphr.procedures AS
	SELECT patient_id, effective_time_start, effective_time_end, 
		code_id, status_code_name, record_id as id, value_string, 
		COALESCE(value_code_id, result_value_code_id) as value_code_id, negation_code_id
	FROM caliphr.patient_procedure;

/* procedureResults => results, vitalSigns, procedures */
CREATE VIEW caliphr.procedureResults AS
	SELECT patient_id, effective_time_start, effective_time_end, 
		code_id, status_code_name, record_id as id, result_value, result_value_unit, 
		value_code_id, null as negation_code_id
	FROM caliphr.patient_result
UNION
	SELECT patient_id, effective_time_start, effective_time_end, 
		code_id, status_code_name, record_id as id, record_value as result_value, record_value_unit as result_value_unit, 
		value_code_id, null as negation_code_id
	FROM caliphr.patient_vital_sign
UNION
	SELECT patient_id, effective_time_start, effective_time_end, 
		code_id, status_code_name, record_id as id, value_string, null, 
		COALESCE(value_code_id, result_value_code_id) as value_code_id, negation_code_id
	FROM caliphr.patient_procedure;

/* allDevices => conditions, procedures, careGoals, medicalEquipment */
CREATE VIEW caliphr.allDevices AS
	SELECT patient_id, effective_time_start, effective_time_end, 
		problem_code_id as code_id, status_code_name, record_id as id, null as negation_code_id
	FROM caliphr.patient_problem
UNION
	SELECT patient_id, effective_time_start, effective_time_end, 
		code_id, status_code_name, record_id as id, negation_code_id
	FROM caliphr.patient_procedure
UNION
	SELECT patient_id, effective_time_start, effective_time_end, 
		code_id, status_code_name, record_id as id, null as negation_code_id
	FROM caliphr.patient_plan_of_care
UNION
	SELECT patient_id, effective_time_start, effective_time_end, 
		code_id, status_code_name, record_id as id, null as negation_code_id
	FROM caliphr.patient_medical_equipment;

