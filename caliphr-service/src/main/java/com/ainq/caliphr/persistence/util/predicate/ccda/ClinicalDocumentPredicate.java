package com.ainq.caliphr.persistence.util.predicate.ccda;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;

/**
 * Created by mmelusky on 5/21/2015.
 */
public class ClinicalDocumentPredicate {

    public static Predicate findAllPatientMedications(PatientMedication patientMedication) {
        QPatientMedication qPatientMedication = QPatientMedication.patientMedication;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qPatientMedication.patient.id.eq(patientMedication.getPatient().getId()));
        booleanBuilder.and(qPatientMedication.dateDisabled.isNull());
        return booleanBuilder;
    }

    public static Predicate findAllPatientProblems(PatientProblem patientProblem) {
        QPatientProblem qPatientProblem = QPatientProblem.patientProblem;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qPatientProblem.patient.id.eq(patientProblem.getPatient().getId()));
        booleanBuilder.and(qPatientProblem.dateDisabled.isNull());
        return booleanBuilder;
    }

    public static Predicate findAllPatientImmunizations(PatientImmunization patientImmunization) {
        QPatientImmunization qPatientImmunization = QPatientImmunization.patientImmunization;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qPatientImmunization.patient.id.eq(patientImmunization.getPatient().getId()));
        booleanBuilder.and(qPatientImmunization.dateDisabled.isNull());
        return booleanBuilder;
    }

    public static Predicate findAllPatientResults(PatientResult patientResult) {
        QPatientResult qPatientResult = QPatientResult.patientResult;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qPatientResult.patient.id.eq(patientResult.getPatient().getId()));
        booleanBuilder.and(qPatientResult.dateDisabled.isNull());
        return booleanBuilder;
    }

    public static Predicate findAllPatientEncounters(PatientEncounter patientEncounter) {
        QPatientEncounter qPatientEncounter = QPatientEncounter.patientEncounter;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qPatientEncounter.patient.id.eq(patientEncounter.getPatient().getId()));
        booleanBuilder.and(qPatientEncounter.dateDisabled.isNull());
        return booleanBuilder;
    }

    public static Predicate findAllPatientInstructions(PatientInstruction patientInstruction) {
        QPatientInstruction qPatientInstruction = QPatientInstruction.patientInstruction;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qPatientInstruction.patient.id.eq(patientInstruction.getPatient().getId()));
        booleanBuilder.and(qPatientInstruction.dateDisabled.isNull());
        return booleanBuilder;
    }

    public static Predicate findAllPatientVitalSigns(PatientVitalSign patientVitalSign) {
        QPatientVitalSign qPatientVitalSign = QPatientVitalSign.patientVitalSign;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qPatientVitalSign.patient.id.eq(patientVitalSign.getPatient().getId()));
        booleanBuilder.and(qPatientVitalSign.dateDisabled.isNull());
        return booleanBuilder;
    }

    public static Predicate findAllPatientProcedures(PatientProcedure patientProcedure) {
        QPatientProcedure qPatientProcedure = QPatientProcedure.patientProcedure;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qPatientProcedure.patient.id.eq(patientProcedure.getPatient().getId()));
        booleanBuilder.and(qPatientProcedure.dateDisabled.isNull());
        return booleanBuilder;
    }

    public static Predicate findAllPatientReasonForVisits(PatientReasonForVisit patientReasonForVisit) {
        QPatientReasonForVisit qPatientReasonForVisit = QPatientReasonForVisit.patientReasonForVisit;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qPatientReasonForVisit.patient.id.eq(patientReasonForVisit.getPatient().getId()));
        booleanBuilder.and(qPatientReasonForVisit.dateDisabled.isNull());
        return booleanBuilder;
    }

    public static Predicate findAllPatientReasonsForReferral(PatientReasonForReferral patientReasonForReferral) {
        QPatientReasonForReferral qPatientReasonForReferral = QPatientReasonForReferral.patientReasonForReferral;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qPatientReasonForReferral.patient.id.eq(patientReasonForReferral.getPatient().getId()));
        booleanBuilder.and(qPatientReasonForReferral.dateDisabled.isNull());
        return booleanBuilder;
    }

    public static Predicate findAllPatientAdvancedDirectives(PatientAdvancedDirective patientAdvancedDirective) {
        QPatientAdvancedDirective qPatientAdvancedDirective = QPatientAdvancedDirective.patientAdvancedDirective;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qPatientAdvancedDirective.patient.id.eq(patientAdvancedDirective.getPatient().getId()));
        booleanBuilder.and(qPatientAdvancedDirective.dateDisabled.isNull());
        return booleanBuilder;
    }

    public static Predicate findAllPatientMedicalEquipment(PatientMedicalEquipment patientMedicalEquipment) {
        QPatientMedicalEquipment qPatientMedicalEquipment = QPatientMedicalEquipment.patientMedicalEquipment;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qPatientMedicalEquipment.patient.id.eq(patientMedicalEquipment.getPatient().getId()));
        booleanBuilder.and(qPatientMedicalEquipment.dateDisabled.isNull());
        return booleanBuilder;
    }

    public static Predicate findAllPatientAllergies(PatientAllergy patientAllergy) {
        QPatientAllergy qPatientAllergy = QPatientAllergy.patientAllergy;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qPatientAllergy.patient.id.eq(patientAllergy.getPatient().getId()));
        booleanBuilder.and(qPatientAllergy.dateDisabled.isNull());
        return booleanBuilder;
    }

    public static Predicate findAllPatientPlansOfCare(PatientPlanOfCare patientPlanOfCare) {
        QPatientPlanOfCare qPatientPlanOfCare = QPatientPlanOfCare.patientPlanOfCare;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qPatientPlanOfCare.patient.id.eq(patientPlanOfCare.getPatient().getId()));
        booleanBuilder.and(qPatientPlanOfCare.dateDisabled.isNull());
        return booleanBuilder;
    }

	public static Predicate findAllPatientSocialHistory(PatientSocialHistory patientSocialHistory) {
		QPatientSocialHistory qPatientSocialHistory = QPatientSocialHistory.patientSocialHistory;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qPatientSocialHistory.patient.id.eq(patientSocialHistory.getPatient().getId()));
        booleanBuilder.and(qPatientSocialHistory.dateDisabled.isNull());
        return booleanBuilder;
	}
}
