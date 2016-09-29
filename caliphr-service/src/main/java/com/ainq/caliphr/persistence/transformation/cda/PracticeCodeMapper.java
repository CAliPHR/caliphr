package com.ainq.caliphr.persistence.transformation.cda;

import java.util.List;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;

/**
 * Created by mmelusky on 2/15/2016.
 */
public interface PracticeCodeMapper {
    void mapPatientProblem(List<CodeMapping> practiceCodeMappings, PatientProblem patientProblem);

    void mapPatientProcedure(List<CodeMapping> practiceCodeMappings, PatientProcedure patientProcedure);

    void mapPatientMedication(List<CodeMapping> practiceCodeMappings, PatientMedication patientMedication);

    void mapPatientResult(List<CodeMapping> practiceCodeMappings, PatientResult patientResult);
}
