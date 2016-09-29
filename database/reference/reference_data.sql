--Bundle
insert into caliphr.bundle (bundle_version) values ('2.6.0');

-- Audit Type
insert into caliphr.audit_type (audit_type_name) values ('Login Request');
insert into caliphr.audit_type (audit_type_name) values ('Query Request');
insert into caliphr.audit_type (audit_type_name) values ('Edit Request');

-- Document Type
insert into caliphr.document_type (type_name, hl7_oid) values ('C-CDA', '2.16.840.1.113883.10.20.22.1.2');
insert into caliphr.document_type (type_name, hl7_oid) values ('QRDA (CAT I)', '2.16.840.1.113883.10.20.24.1.2');

-- Domains
insert into caliphr.domain (name) values ('Effective Clinical Care');
insert into caliphr.domain (name) values ('Efficiency and Cost Reduction Use of Healthcare Resources');
insert into caliphr.domain (name) values ('Patient Safety');
insert into caliphr.domain (name) values ('Community, Population and Public Health');
insert into caliphr.domain (name) values ('Person and Caregiver-Centered Experience Outcomes');
insert into caliphr.domain (name) values ('Communication and Care Coordination');

-- Parse Status Type
insert into caliphr.parse_status_type (status_name) values ('Success');
insert into caliphr.parse_status_type (status_name) values ('Failed Validation');
insert into caliphr.parse_status_type (status_name) values ('Parsing Errors');

-- States
insert into caliphr.state (name, abbreviation) values ('Alabama','AL');
insert into caliphr.state (name, abbreviation) values ('Alaska','AK');
insert into caliphr.state (name, abbreviation) values ('Arizona','AZ');
insert into caliphr.state (name, abbreviation) values ('Arkansas','AR');
insert into caliphr.state (name, abbreviation) values ('California','CA');
insert into caliphr.state (name, abbreviation) values ('Colorado','CO');
insert into caliphr.state (name, abbreviation) values ('Connecticut','CT');
insert into caliphr.state (name, abbreviation) values ('Delaware','DE');
insert into caliphr.state (name, abbreviation) values ('Florida','FL');
insert into caliphr.state (name, abbreviation) values ('Georgia','GA');
insert into caliphr.state (name, abbreviation) values ('Hawaii','HI');
insert into caliphr.state (name, abbreviation) values ('Idaho','ID');
insert into caliphr.state (name, abbreviation) values ('Illinois','IL');
insert into caliphr.state (name, abbreviation) values ('Indiana','IN');
insert into caliphr.state (name, abbreviation) values ('Iowa','IA');
insert into caliphr.state (name, abbreviation) values ('Kansas','KS');
insert into caliphr.state (name, abbreviation) values ('Kentucky','KY');
insert into caliphr.state (name, abbreviation) values ('Louisiana','LA');
insert into caliphr.state (name, abbreviation) values ('Maine','ME');
insert into caliphr.state (name, abbreviation) values ('Maryland','MD');
insert into caliphr.state (name, abbreviation) values ('Massachusetts','MA');
insert into caliphr.state (name, abbreviation) values ('Michigan','MI');
insert into caliphr.state (name, abbreviation) values ('Minnesota','MN');
insert into caliphr.state (name, abbreviation) values ('Mississippi','MS');
insert into caliphr.state (name, abbreviation) values ('Missouri','MO');
insert into caliphr.state (name, abbreviation) values ('Montana','MT');
insert into caliphr.state (name, abbreviation) values ('Nebraska','NE');
insert into caliphr.state (name, abbreviation) values ('Nevada','NV');
insert into caliphr.state (name, abbreviation) values ('New Hampshire','NH');
insert into caliphr.state (name, abbreviation) values ('New Jersey','NJ');
insert into caliphr.state (name, abbreviation) values ('New Mexico','NM');
insert into caliphr.state (name, abbreviation) values ('New York','NY');
insert into caliphr.state (name, abbreviation) values ('North Carolina','NC');
insert into caliphr.state (name, abbreviation) values ('North Dakota','ND');
insert into caliphr.state (name, abbreviation) values ('Ohio','OH');
insert into caliphr.state (name, abbreviation) values ('Oklahoma','OK');
insert into caliphr.state (name, abbreviation) values ('Oregon','OR');
insert into caliphr.state (name, abbreviation) values ('Pennsylvania','PA');
insert into caliphr.state (name, abbreviation) values ('Rhode Island','RI');
insert into caliphr.state (name, abbreviation) values ('South Carolina','SC');
insert into caliphr.state (name, abbreviation) values ('South Dakota','SD');
insert into caliphr.state (name, abbreviation) values ('Tennessee','TN');
insert into caliphr.state (name, abbreviation) values ('Texas','TX');
insert into caliphr.state (name, abbreviation) values ('Utah','UT');
insert into caliphr.state (name, abbreviation) values ('Vermont','VT');
insert into caliphr.state (name, abbreviation) values ('Virginia','VA');
insert into caliphr.state (name, abbreviation) values ('Washington','WA');
insert into caliphr.state (name, abbreviation) values ('West Virginia','WV');
insert into caliphr.state (name, abbreviation) values ('Wisconsin','WI');
insert into caliphr.state (name, abbreviation) values ('Wyoming','WY');
insert into caliphr.state (name, abbreviation) values ('American Samoa','AS');
insert into caliphr.state (name, abbreviation) values ('District of Columbia','DC');
insert into caliphr.state (name, abbreviation) values ('Federated States of Micronesia','FM');
insert into caliphr.state (name, abbreviation) values ('Guam','GU');
insert into caliphr.state (name, abbreviation) values ('Marshall Islands','MH');
insert into caliphr.state (name, abbreviation) values ('Northern Mariana Islands','MP');
insert into caliphr.state (name, abbreviation) values ('Palau','PW');
insert into caliphr.state (name, abbreviation) values ('Puerto Rico','PR');
insert into caliphr.state (name, abbreviation) values ('Virgin Islands','VI');

-- Status Code
insert into caliphr.status_code(status_code_name) values ('active');
insert into caliphr.status_code(status_code_name) values ('completed');
insert into caliphr.status_code(status_code_name) values ('new');
insert into caliphr.status_code(status_code_name) values ('suspended');

-- Security Role
insert into caliphr.security_role (role_name) values ('System Administrator');
insert into caliphr.security_role (role_name) values ('Application User');
insert into caliphr.security_role (role_name) values ('Application Developer');
insert into caliphr.security_role (role_name) values ('Application Tester');

-- Code Mapping Type
insert into caliphr.code_mapping_type (type_name, type_description) values ('Patient Problem Mapping', 'Translation which maps code values in the patient_problem table.');
insert into caliphr.code_mapping_type (type_name, type_description) values ('Patient Procedure Mapping', 'Translation which maps code values in the patient_procedure table.');
insert into caliphr.code_mapping_type (type_name, type_description) values ('Patient Medication Mapping', 'Translation which maps code values in the patient_medication table.');
insert into caliphr.code_mapping_type (type_name, type_description) values ('Patient Procedure Mapping Unknown Value', 'Translation which maps code values in the patient_procedure table and sets value_string to "unknown".');
insert into caliphr.code_mapping_type (type_name, type_description) values ('Patient Result Value Mapping', 'Translation which maps code values in the patient_result table using the result_value field.');

-- Organization
insert into caliphr.organization (organization_name) values ('Empty Source Organization');
insert into caliphr.organization (organization_name) values ('QRDA Testing Organization');

-- Practice Group
insert into caliphr.practice_group (organization_id, group_name, sender_oid, vendor_id, active_problems_only_flag) values (1, 'Unknown Practice', 'NOTFOUND', NULL, TRUE);
insert into caliphr.practice_group (organization_id, group_name, sender_oid, vendor_id, active_problems_only_flag) values (2, 'QRDA Practice I', '2.16.840.1.113883.3.1257', NULL, TRUE);

-- Provider
insert into caliphr.provider (group_id, npi, full_name, first_name, last_name) values (2, '111111111', 'Test QRDA Provider', 'Test', 'QRDA');

-- User (initial Password '123456')
insert into caliphr.application_user (first_name, last_name, email_address, password_hash) values ('George','Washington','test@test.com','$2a$10$BHG59UT6p7bgT6U2fQ/9wOyTIdejh4Rk1vWilvl4b6ysNPdhnViUS');

-- Application User Security
insert into caliphr.application_user_security (role_id, user_id, provider_id) values (2, 1, 1);