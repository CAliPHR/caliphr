package com.ainq.caliphr.persistence.transformation.cda.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.model.ccda.CodeMapType;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;
import com.ainq.caliphr.persistence.service.ValueSetService;
import com.ainq.caliphr.persistence.transformation.cda.PracticeCodeMapper;

import java.util.List;

/**
 * Created by mmelusky on 2/15/2016.
 */
@Component
public class PracticeCodeMapperImpl implements PracticeCodeMapper {

    @Autowired
    private ValueSetService valueSetService;

    @SuppressWarnings("incomplete-switch")
	@Override
    public void mapPatientProblem(List<CodeMapping> codeMappings, PatientProblem patientProblem) {
    	for (CodeMapping codeMapping : codeMappings) {
            CodeMapType codeMapType = extractType(codeMapping);
            if (codeMapType != null) {
                switch (codeMapType) {
                    case PATIENT_PROBLEM_MAPPING:

                        if (
                            //
                            //  Map code ID to code ID
                                (patientProblem.getProblemCode() != null
                                        && patientProblem.getProblemCode().getId() != null
                                        && codeMapping.getFromCode() != null
                                        && codeMapping.getFromCode().getId() != null
                                        && patientProblem.getProblemCode().getId().equals(codeMapping.getFromCode().getId()))

                                        ||

                                        //
                                        //  Map code description to code ID
                                        (patientProblem.getProblemCodeDescription() != null
                                                && codeMapping.getFromDisplayName() != null
                                                && patientProblem.getProblemCodeDescription().trim().equalsIgnoreCase(codeMapping.getFromDisplayName().trim()))

                                ) {
                        	
                        	patientProblem.setCodeMapping(codeMapping);
                        	
                        } else if (

                            //
                            // No from code in the database for the mapping
                                codeMapping.getFromCode() == null
                                        && codeMapping.getFromCodeName() != null
                                        && codeMapping.getFromCodeSystem() != null
                                ) {
                            String hl7Oid = valueSetService.findHl7OidForCodeSystemByName(codeMapping.getFromCodeSystem());
                            if (hl7Oid != null
                            		&& patientProblem.getProblemCode() != null
                                    && patientProblem.getProblemCode().getCodeSystem() != null
                                    && patientProblem.getProblemCode().getCodeName() != null
                                    && patientProblem.getProblemCode().getCodeName().equalsIgnoreCase(codeMapping.getFromCodeName())
                                    && hl7Oid.equalsIgnoreCase(patientProblem.getProblemCode().getCodeSystem().getHl7Oid())) {
                            	
                            	patientProblem.setCodeMapping(codeMapping);
                            	
                            }
                        }

                        break;
                }
            }
        }
    }

    @SuppressWarnings("incomplete-switch")
	@Override
    public void mapPatientProcedure(List<CodeMapping> codeMappings, PatientProcedure patientProcedure) {
        for (CodeMapping codeMapping : codeMappings) {
            CodeMapType codeMapType = extractType(codeMapping);
            if (codeMapType != null) {
                switch (codeMapType) {
                    case PATIENT_PROCEDURE_MAPPING:
                    case PATIENT_PROCEDURE_MAPPING_UNKNOWN_VALUE:

                        if (
                            //
                            //  Map code ID to code ID
                                (patientProcedure.getCode() != null
                                        && patientProcedure.getCode().getId() != null
                                        && codeMapping.getFromCode() != null
                                        && codeMapping.getFromCode().getId() != null
                                        && patientProcedure.getCode().getId().equals(codeMapping.getFromCode().getId()))

                                        ||

                                        //
                                        //  Map code description to code ID
                                        (patientProcedure.getCodeDescription() != null
                                                && codeMapping.getFromDisplayName() != null
                                                && patientProcedure.getCodeDescription().trim().equalsIgnoreCase(codeMapping.getFromDisplayName().trim()))

                                ) {
                        	
                        	patientProcedure.setCodeMapping(codeMapping);
                        	
                            if (codeMapType.getTypeId() == CodeMapType.PATIENT_PROCEDURE_MAPPING_UNKNOWN_VALUE.getTypeId()
                                    && (patientProcedure.getValueString() == null || patientProcedure.getValueString().isEmpty())) {
                                patientProcedure.setValueString(CodeMapType.UNKNOWN_VALUE);
                            }
                        } else if (

                            //
                            // No from code in the database for the mapping
                                codeMapping.getFromCode() == null
                                        && codeMapping.getFromCodeName() != null
                                        && codeMapping.getFromCodeSystem() != null
                                ) {
                            String hl7Oid = valueSetService.findHl7OidForCodeSystemByName(codeMapping.getFromCodeSystem());
                            if (hl7Oid != null
                            		&& patientProcedure.getCode() != null
                            		&& patientProcedure.getCode().getCodeSystem() != null
                                    && patientProcedure.getCode().getCodeName() != null
                                    && patientProcedure.getCode().getCodeName().equalsIgnoreCase(codeMapping.getFromCodeName())
                                    && hl7Oid.equalsIgnoreCase(patientProcedure.getCode().getCodeSystem().getHl7Oid())) {
                            	
                            	patientProcedure.setCodeMapping(codeMapping);
                            	
                            }
                        }

                        break;
                }
            }
        }
    }

    @SuppressWarnings("incomplete-switch")
	@Override
    public void mapPatientMedication(List<CodeMapping> codeMappings, PatientMedication patientMedication) {
        for (CodeMapping codeMapping : codeMappings) {
            CodeMapType codeMapType = extractType(codeMapping);
            if (codeMapType != null) {
                switch (codeMapType) {
                    case PATIENT_MEDICATION_MAPPING:
                        if (
                            //
                            //  Map code ID to code ID
                                (patientMedication.getProductCode() != null
                                        && patientMedication.getProductCode().getId() != null
                                        && codeMapping.getFromCode() != null
                                        && codeMapping.getFromCode().getId() != null
                                        && patientMedication.getProductCode().getId().equals(codeMapping.getFromCode().getId()))

                                        ||

                                        //
                                        //  Map code description to code ID
                                        (patientMedication.getProductCodeDescription() != null
                                                && codeMapping.getFromDisplayName() != null
                                                && patientMedication.getProductCodeDescription().trim().equalsIgnoreCase(codeMapping.getFromDisplayName().trim()))

                                ) {

                    		patientMedication.setCodeMapping(codeMapping);
                    		
                        } else if (

                            //
                            // No from code in the database for the mapping
                                codeMapping.getFromCode() == null
                                        && codeMapping.getFromCodeName() != null
                                        && codeMapping.getFromCodeSystem() != null
                                ) {
                            String hl7Oid = valueSetService.findHl7OidForCodeSystemByName(codeMapping.getFromCodeSystem());
                            if (hl7Oid != null
                            		&& patientMedication.getProductCode() != null
                                    && patientMedication.getProductCode().getCodeSystem() != null
                                    && patientMedication.getProductCode().getCodeName() != null
                                    && patientMedication.getProductCode().getCodeName().equalsIgnoreCase(codeMapping.getFromCodeName())
                                    && hl7Oid.equalsIgnoreCase(patientMedication.getProductCode().getCodeSystem().getHl7Oid())) {

                        		patientMedication.setCodeMapping(codeMapping);
                            }
                        }

                        break;
                }
            }
        }
    }

    @SuppressWarnings("incomplete-switch")
	@Override
    public void mapPatientResult(List<CodeMapping> codeMappings, PatientResult patientResult) {
        for (CodeMapping codeMapping : codeMappings) {
            CodeMapType codeMapType = extractType(codeMapping);
            if (codeMapType != null) {
                switch (codeMapType) {
                    case PATIENT_RESULT_VALUE_MAPPING:

                    	if (
                                (codeMapping.getFromCode() != null
                                        && codeMapping.getFromDisplayName() != null
                                        && patientResult.getCode() == null
                                        && patientResult.getResultValue() != null
                                        && codeMapping.getFromDisplayName().equalsIgnoreCase(patientResult.getResultValue().trim()))
                                || 
                                (

                                //
                                //  Map code description to code ID
                                (patientResult.getResultValue() != null
                                        && codeMapping.getFromDisplayName() != null
                                        && patientResult.getResultValue().trim().equalsIgnoreCase(codeMapping.getFromDisplayName().trim())))
                                ) {

                        	patientResult.setCodeMapping(codeMapping);
                        	
                        } else if (

                                //
                                // No from code in the database for the mapping
                                    codeMapping.getFromCode() == null
                                            && codeMapping.getFromCodeName() != null
                                            && codeMapping.getFromCodeSystem() != null
                                    ) {
                                String hl7Oid = valueSetService.findHl7OidForCodeSystemByName(codeMapping.getFromCodeSystem());
                                if (hl7Oid != null
                                		&& patientResult.getReasonCode() != null
                                        && patientResult.getReasonCode().getCodeSystem() != null
                                        && patientResult.getReasonCode().getCodeName() != null
                                        && patientResult.getReasonCode().getCodeName().equalsIgnoreCase(codeMapping.getFromCodeName())
                                        && hl7Oid.equalsIgnoreCase(patientResult.getReasonCode().getCodeSystem().getHl7Oid())) {

                                	patientResult.setCodeMapping(codeMapping);

                                }
                            } 


                        break;
                }
            }
        }
    }

    //
    //  Utility Method
    //
    private CodeMapType extractType(CodeMapping practiceCodeMapping) {
        if (practiceCodeMapping.getType() != null && practiceCodeMapping.getType().getId() > 0) {
            try {
                return CodeMapType.fromType(practiceCodeMapping.getType().getId());
            } catch (Exception ex) {
                return null;
            }
        } else {
            return null;
        }
    }

}
