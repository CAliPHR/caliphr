package com.ainq.caliphr.persistence.transformation.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.JAXBElement;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.hl7.v3.II;
import org.hl7.v3.POCDMT000040Act;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Encounter;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040Observation;
import org.hl7.v3.POCDMT000040Organizer;
import org.hl7.v3.POCDMT000040Performer1;
import org.hl7.v3.POCDMT000040Procedure;
import org.hl7.v3.POCDMT000040RecordTarget;
import org.hl7.v3.POCDMT000040Section;
import org.hl7.v3.POCDMT000040SubstanceAdministration;
import org.hl7.v3.POCDMT000040Supply;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.dao.ClinicalDocumentDao;
import com.ainq.caliphr.persistence.dao.reference.TemplateRootDao;
import com.ainq.caliphr.persistence.model.ccda.PatientInfoHolder;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.CodeMapping;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.DocumentType;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroup;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Provider;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.DocumentTypeRepository;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.ParseStatusTypeRepository;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.PracticeGroupRepository;
import com.ainq.caliphr.persistence.transformation.ClinicalDocumentParser;
import com.ainq.caliphr.persistence.transformation.cda.AdvancedDirectiveImporter;
import com.ainq.caliphr.persistence.transformation.cda.AllergyImporter;
import com.ainq.caliphr.persistence.transformation.cda.ClinicalDocumentType;
import com.ainq.caliphr.persistence.transformation.cda.EncounterImporter;
import com.ainq.caliphr.persistence.transformation.cda.FamilyHistoryImporter;
import com.ainq.caliphr.persistence.transformation.cda.FuncCogStatusImporter;
import com.ainq.caliphr.persistence.transformation.cda.ImmunizationImporter;
import com.ainq.caliphr.persistence.transformation.cda.InstructionImporter;
import com.ainq.caliphr.persistence.transformation.cda.MedicalEquipmentImporter;
import com.ainq.caliphr.persistence.transformation.cda.MedicationImporter;
import com.ainq.caliphr.persistence.transformation.cda.ParseStatus;
import com.ainq.caliphr.persistence.transformation.cda.PatientInfoImporter;
import com.ainq.caliphr.persistence.transformation.cda.PatientPayerImporter;
import com.ainq.caliphr.persistence.transformation.cda.PlanOfCareImporter;
import com.ainq.caliphr.persistence.transformation.cda.ProblemImporter;
import com.ainq.caliphr.persistence.transformation.cda.ProcedureImporter;
import com.ainq.caliphr.persistence.transformation.cda.ProviderImporter;
import com.ainq.caliphr.persistence.transformation.cda.ReasonForReferralImporter;
import com.ainq.caliphr.persistence.transformation.cda.ReasonForVisitImporter;
import com.ainq.caliphr.persistence.transformation.cda.ResultImporter;
import com.ainq.caliphr.persistence.transformation.cda.SocialHistoryImporter;
import com.ainq.caliphr.persistence.transformation.cda.TemplateIdRoot;
import com.ainq.caliphr.persistence.transformation.cda.VitalSignImporter;
import com.ainq.caliphr.persistence.transformation.cda.impl.ProblemImporterImpl;
import com.ainq.caliphr.persistence.transformation.cda.impl.ProcedureImporterImpl;
import com.ainq.caliphr.persistence.transformation.cda.impl.ResultImporterImpl;
import com.ainq.caliphr.persistence.transformation.util.JaxbUtility;
import com.ainq.caliphr.persistence.util.DatabaseEncyptionUtil;
import com.ainq.caliphr.persistence.util.predicate.ccda.DocumentTypePredicate;
import com.ainq.caliphr.persistence.util.predicate.patient.PracticeGroupPredicate;

import ch.qos.logback.classic.Logger;

@Repository
public class ClinicalDocumentParserImpl implements ClinicalDocumentParser {

    //
    // Instance/Class Data
    static Logger logger = (Logger) LoggerFactory.getLogger(ClinicalDocumentParserImpl.class);
    
    //
    // Spring-injected properties

    @Autowired
    private ClinicalDocumentDao clinicalDocumentDao;

    @Autowired
    private Environment environment;

    @Autowired
    private AdvancedDirectiveImporter advancedDirectiveImporter;

    @Autowired
    private AllergyImporter allergyImporter;

    @Autowired
    private DatabaseEncyptionUtil databaseEncyptionUtil;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private FamilyHistoryImporter familyHistoryImporter;

    @Autowired
    private EncounterImporter encounterImporter;

    @Autowired
    private FuncCogStatusImporter funcCogStatusImporter;

    @Autowired
    private ImmunizationImporter immunizationImporter;

    @Autowired
    private InstructionImporter instructionImporter;

    @Autowired
    private MedicationImporter medicationImporter;

    @Autowired
    private MedicalEquipmentImporter medicalEquipmentImporter;

    @Autowired
    private PatientPayerImporter patientPayerImporter;

    @Autowired
    private PatientInfoImporter patientInfoImporter;

    @Autowired
    private ProviderImporter providerImporter;

    @Autowired
    private PlanOfCareImporter planOfCareImporter;

    @Autowired
    private ProblemImporter problemImporter;

    @Autowired
    private ProcedureImporter procedureImporter;

    @Autowired
    private ReasonForReferralImporter reasonForReferralImporter;

    @Autowired
    private ReasonForVisitImporter reasonForVisitImporter;

    @Autowired
    private ResultImporter resultImporter;

    @Autowired
    private SocialHistoryImporter socialHistoryImporter;

    @Autowired
    private VitalSignImporter vitalSignImporter;

    @Autowired
    private ParseStatusTypeRepository parseStatusTypeRepository;

    @Autowired
    private PracticeGroupRepository practiceGroupRepository;

    @Autowired
    private TemplateRootDao templateRootDao;

    // counter for unique archive filenames due to multi-threading
    private static final AtomicInteger archiveFileCounter = new AtomicInteger();

    @Override
    public DocumentType validateClinicalDocument(JAXBElement<?> document) {
        POCDMT000040ClinicalDocument documentRoot = (POCDMT000040ClinicalDocument) document.getValue();
        DocumentType documentType = null;
        if (documentRoot.getTemplateId() != null) {
            for (II templateId : documentRoot.getTemplateId()) {
                if (templateId != null && templateId.getRoot() != null) {

                    //
                    //  Set the enum value for the document.  Find the appropriate database entity
                    documentType = documentTypeRepository.findOne(DocumentTypePredicate.searchByHl7Oid(templateId.getRoot()));
                    if (documentType != null) {
                        break;
                    }
                }
            }
        } else {
            logger.error("Document found with missing document root template!", document);
            throw new IllegalStateException("Document found with missing document root templates.");
        }

        // Throw an exception for unsupported document types
        if (documentType == null) {
            logger.error("Unsupported clinical document found", document);
            throw new IllegalStateException("Illegal state argument encountered for document with template");
        }

        return documentType;
    }

    @Override
    public com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroup findSendingGroup(JAXBElement<?> document) {
        POCDMT000040ClinicalDocument documentRoot = (POCDMT000040ClinicalDocument) document.getValue();
        com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroup practiceGroup = null;

        // Find the source from the patient using the appropriate repository
        if (documentRoot.getRecordTarget() != null) {
            for (POCDMT000040RecordTarget recordTarget : documentRoot.getRecordTarget()) {
                if (recordTarget.getPatientRole() != null && recordTarget.getPatientRole().getId() != null) {
                    for (II id : recordTarget.getPatientRole().getId()) {
                        if (id.getRoot() != null) {
                            practiceGroup = practiceGroupRepository.findOne(PracticeGroupPredicate.findGroupBySendingOid(id.getRoot().trim()));
                            if (practiceGroup != null) {
                                return practiceGroup;
                            }
                        }
                    }
                }
            }
        }

        if (practiceGroup == null) {
            //
            // Either throw an exception or find a "NOTFOUND" source depending on the region
            if (Boolean.parseBoolean(environment.getProperty(Constants.PropertyKey.ALLOW_PATIENT_WITH_UNKNOWN_SOURCE))) {
                practiceGroup = practiceGroupRepository.findOne(Constants.PracticeGroup.UNKNOWN_SENDING_GROUP_ID);
            } else {
                throw new IllegalStateException("Patient with unknown source find in clinical document file!");
            }
        }

        return practiceGroup;
    }

    @Override
    public PatientInfoHolder findPatientBySourceAndMRN(JAXBElement<?> document, PracticeGroup practiceGroup) {
        if (!(practiceGroup == null || practiceGroup.getSenderOid() == null
                || practiceGroup.getId().equals(Constants.PracticeGroup.UNKNOWN_SENDING_GROUP_ID))) {

            //
            //  Find the medical record number given the source
            POCDMT000040ClinicalDocument documentRoot = (POCDMT000040ClinicalDocument) document.getValue();

            // Find the source from the patient using the appropriate repository
            if (documentRoot.getRecordTarget() != null) {
                for (POCDMT000040RecordTarget recordTarget : documentRoot.getRecordTarget()) {
                    if (recordTarget.getPatientRole() != null && recordTarget.getPatientRole().getId() != null) {
                        for (II id : recordTarget.getPatientRole().getId()) {
                            if (id.getRoot() != null && id.getExtension() != null && id.getRoot().equals(practiceGroup.getSenderOid())) {
                                return patientInfoImporter.findPatientBySourceAndMRN(practiceGroup, id.getExtension());
                            }
                        }
                    }
                }
            }
        }

        // Skip (an exception thrown above ensure this will never be hit in higher regions of the application)
        return null;
    }

    @Override
    public ClinicalDocument archiveClinicalDocument(JAXBElement<?> document, ClinicalDocument clinicalDocument, PracticeGroup practiceGroup, Boolean archiveToFileSystem) {

        if (archiveToFileSystem == Boolean.TRUE) {

            //
            //  Format the practice name neatly
            String practiceDirectory = "NOTFOUND";
            if (practiceGroup != null && practiceGroup.getId() > 0 && practiceGroup.getGroupName() != null) {
                String practiceName = practiceGroup.getGroupName().toLowerCase().replaceAll("\\W+", "");
                practiceDirectory = String.format("%s_%s", practiceGroup.getId(), practiceName);
            }

            //  Write C-CDA files to an encrypted zip file on the filesystem instead of the database.
            //
            String currentDirectory = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String fileName = new SimpleDateFormat("yyyyMMdd-HHmmss-").format(new Date()) + (archiveFileCounter.incrementAndGet() % 100);
            String directoryRoot = String.format("%s/%s/%s/", environment.getProperty(Constants.PropertyKey.CLINICAL_DOCUMENT_BACKUP_ROOT)
                    , practiceDirectory, currentDirectory);
            String zipPath = String.format("%s/%s.xml.zip", directoryRoot, fileName);

            // Create the directory structure if not present
            File directoryInstance = new File(directoryRoot);
            if (!directoryInstance.exists()) {
                directoryInstance.mkdirs();
            }

            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File(zipPath)))) {

                ZipParameters zipParams = new ZipParameters();
                zipParams.setFileNameInZip(fileName + ".xml");
                zipParams.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
                zipParams.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
                zipParams.setEncryptFiles(true);
                zipParams.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
                zipParams.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
                zipParams.setPassword(databaseEncyptionUtil.getKey());
                zipParams.setSourceExternalStream(true);

                zos.putNextEntry(null, zipParams);
                JaxbUtility.jaxbToOutputStream(document, POCDMT000040ClinicalDocument.class, zos);
                zos.closeEntry();
                zos.finish();

                
            } catch (IOException | ZipException e) {
                logger.warn("error while parsing CCDA " + clinicalDocument.getFileName(), e);
                clinicalDocumentDao.saveClinicalDocumentParseError(clinicalDocument, e);
            }
            
            clinicalDocument.setFileName(fileName);
        }
        

        return clinicalDocument;
    }

    @Override
    public PatientInfoHolder findPatientBySSN(JAXBElement<?> document) {
        POCDMT000040ClinicalDocument documentRoot = (POCDMT000040ClinicalDocument) document.getValue();
        if (documentRoot.getRecordTarget() != null) {
            for (POCDMT000040RecordTarget recordTarget : documentRoot.getRecordTarget()) {
                if (recordTarget.getPatientRole() != null) {
                    return patientInfoImporter.findPatientBySSN(recordTarget.getPatientRole());
                }
            }
        }

        return null;
    }

    @Override
    public PatientInfoHolder createPatientInfo(JAXBElement<?> document, PracticeGroup practiceGroup) {
        PatientInfoHolder holder = null;
        POCDMT000040ClinicalDocument documentRoot = (POCDMT000040ClinicalDocument) document.getValue();
        if (documentRoot.getRecordTarget() != null) {
            for (POCDMT000040RecordTarget recordTarget : documentRoot.getRecordTarget()) {
                if (recordTarget.getPatientRole() != null) {
                    holder = patientInfoImporter.loadPatientInfo(recordTarget.getPatientRole(), practiceGroup);
                }
            }
        }
        if (holder != null) {
            holder.getPatientInfo().setDateCreated(new Date());
            holder.getPatientInfo().setDateUpdated(new Date());
            holder.getPatientInfo().setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            holder.getPatientInfo().setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        }
        return holder;
    }

    @Override
    public void loadPatientSections(JAXBElement<?> document, ClinicalDocument clinicalDocument, PracticeGroup practiceGroup, List<CodeMapping> codeMappings) {

    	ExistingItemContext existingItemCxt = new ExistingItemContext(clinicalDocument);
    	
    	// see the top of the class files below for a description of the temporary kludge
    	((ProblemImporterImpl)problemImporter).resetThreadLocal();
    	((ProcedureImporterImpl)procedureImporter).resetThreadLocal();
    	((ResultImporterImpl)resultImporter).resetThreadLocal();
    	
    	ClinicalDocumentType clinicalDocumentType = ClinicalDocumentType.parseRoot(clinicalDocument.getType().getHl7Oid());
    	
            	
        POCDMT000040ClinicalDocument documentRoot = (POCDMT000040ClinicalDocument) document.getValue();
        if (documentRoot.getComponent() != null && documentRoot.getComponent().getStructuredBody() != null && documentRoot.getComponent().getStructuredBody().getComponent() != null) {
            /*
                Iterate over each section in the document
             */
            documentRoot.getComponent().getStructuredBody().getComponent().stream().filter(component -> component.getSection() != null).forEach(component -> {

                POCDMT000040Section section = component.getSection();
                if (clinicalDocumentType != null) {
                    switch (clinicalDocumentType) {
                        case C_CDA:
                            parseCcdaSection(section, clinicalDocument, practiceGroup, codeMappings, existingItemCxt);
                            break;
                        case CAT_I:
                            parseCatISection(section, clinicalDocument, practiceGroup, codeMappings, existingItemCxt);
                            break;
                    }
                }
                
            });
        }
        
        // clean up after.  see the top of the class files below for a description of the temporary kludge
    	((ProblemImporterImpl)problemImporter).resetThreadLocal();
    	((ProcedureImporterImpl)procedureImporter).resetThreadLocal();
    	((ResultImporterImpl)resultImporter).resetThreadLocal();

    }

    @Override
    public Provider createProviderDetails(POCDMT000040Performer1 performer, PracticeGroup practiceGroup) {
        Provider provider = this.providerImporter.findProviderByGroupAndNPI(performer, practiceGroup);
        if (provider == null) {
            provider = this.providerImporter.loadProviderInfo(performer, practiceGroup);
        }
        return provider;
    }

    /*
        Parse C-CDA section
     */
    @SuppressWarnings("incomplete-switch")
	private void parseCcdaSection(POCDMT000040Section section, ClinicalDocument clinicalDocument, PracticeGroup practiceGroup, List<CodeMapping> codeMappings, ExistingItemContext existingItemCxt) {

        //
        // Load C-CDA by parsing entries
        if (section.getEntry() != null) {
            TemplateIdRoot foundRoot = null;
            for (POCDMT000040Entry entry : section.getEntry()) {

                if (section.getTemplateId() != null) {
                    for (II templateId : section.getTemplateId()) {
                        if (templateId != null && templateId.getRoot() != null) {
                            try {
                                foundRoot = TemplateIdRoot.fromRoot(templateId.getRoot());
                            } catch (IllegalArgumentException e) {
                            }
                        }
                    }
                }

                if (foundRoot != null) {

                    //
                    //  Find the matching template
                    TemplateRoot templateRoot = templateRootDao.findOrCreateTemplate(foundRoot.getRoot());

                    try {
                        switch (foundRoot) {
                            case ADVANCED_DIRECTIVE__ENTRIES_OPTIONAL:
                            case ADVANCED_DIRECTIVE__ENTRIES_REQUIRED:
                                advancedDirectiveImporter.loadAdvancedDirectiveEntry(entry, clinicalDocument, templateRoot);
                                break;
                            case ALLERGY__ENTRIES_OPTIONAL:
                            case ALLERGY__ENTRIES_REQUIRED:
                                allergyImporter.loadPatientAllergyEntry(entry, clinicalDocument, templateRoot);
                                break;
                            case ENCOUNTER__ENTRIES_OPTIONAL:
                            case ENCOUNTER__ENTRIES_REQUIRED:
                                encounterImporter.loadPatientEncounterEntry(entry, clinicalDocument, templateRoot, practiceGroup);
                                break;
                            case FUNC_COG_STATUS:
                                funcCogStatusImporter.loadPatientFuncCogStatusEntry(entry, clinicalDocument, templateRoot);
                                break;
                            case FAMILY_HISTORY:
                                familyHistoryImporter.loadFamilyHistoryEntry(entry, clinicalDocument, templateRoot);
                                break;
                            case IMMUNIZATION__ENTRIES_OPTIONAL:
                            case IMMUNIZATION__ENTRIES_REQUIRED:
                                immunizationImporter.loadPatientImmunizationEntry(entry, clinicalDocument, templateRoot, existingItemCxt.getExistingImmunizations());
                                break;
                            case INSTRUCTIONS__GENERAL:
                            case INSTRUCTIONS__HOSPITAL_DISCHARGE:
                                instructionImporter.loadPatientInstructions(section, clinicalDocument, templateRoot);
                                break;
                            case MEDICAL_EQUIPMENT:
                                medicalEquipmentImporter.loadMedicalEquipmentEntry(entry, clinicalDocument, templateRoot);
                                break;
                            case MEDICATIONS__GENERAL_ENTRIES_OPTIONAL:
                            case MEDICATIONS__GENERAL_ENTRIES_REQUIRED:
                                medicationImporter.loadPatientMedicationEntry(entry, clinicalDocument, templateRoot, codeMappings, existingItemCxt.getExistingMedications());
                                break;
                            case PLAN_OF_CARE:
                                planOfCareImporter.loadPatientPlanOfCareEntry(entry, clinicalDocument, templateRoot);
                                break;
                            case PROBLEM__ENTRIES_OPTIONAL:
                            case PROBLEM__ENTRIES_REQUIRED:
                                problemImporter.loadPatientProblemEntry(entry, clinicalDocument, templateRoot, codeMappings, existingItemCxt.getExistingProblems());
                                break;
                            case PROCEDURE__ENTRIES_OPTIONAL:
                            case PROCEDURE__ENTRIES_REQUIRED:
                                procedureImporter.loadPatientProcedureEntry(entry, clinicalDocument, templateRoot, codeMappings, existingItemCxt.getExistingProcedures());
                                break;
                            case REASON_FOR_REFERRAL:
                                reasonForReferralImporter.loadPatientReasonForReferrals(section, clinicalDocument, templateRoot);
                                break;
                            case REASON_FOR_VISIT__GENERAL:
                                reasonForVisitImporter.loadPatientReasonForVisits(section, clinicalDocument, templateRoot);
                                break;
                            case RESULT__ENTRIES_OPTIONAL:
                            case RESULT__ENTRIES_REQUIRED:
                                resultImporter.loadPatientResultEntry(entry, clinicalDocument, templateRoot, codeMappings, existingItemCxt.getExistingResults());
                                break;
                            case SOCIAL_HISTORY:
                                socialHistoryImporter.loadPatientSocialHistoryEntry(entry, clinicalDocument, templateRoot);
                                break;
                            case VITAL_SIGN__ENTRIES_OPTIONAL:
                            case VITAL_SIGN__ENTRIES_REQURIED:
                                vitalSignImporter.loadPatientVitalSignEntry(entry, clinicalDocument, templateRoot, existingItemCxt.getExistingVitalSigns());
                                break;
                        }
                    } catch (Exception ex) {
                        logger.error("C-CDA template processing error " + clinicalDocument.getFileName(), ex);
                        clinicalDocument.setParseStatusType(parseStatusTypeRepository.findOne(ParseStatus.PARSING_ERRORS.getTypeId()));
                        clinicalDocumentDao.saveClinicalDocumentParseError(clinicalDocument, ex);
                    }
                }
            }
        }
    }
    /*
        Parse QRDA CAT_I section
     */
    @SuppressWarnings("incomplete-switch")
	private void parseCatISection(POCDMT000040Section section, ClinicalDocument clinicalDocument, PracticeGroup practiceGroup, List<CodeMapping> codeMappings, ExistingItemContext existingItemCxt) {

        //
        // Load CAT_I an entry per entry basis
        if (section.getEntry() != null) {
            for (POCDMT000040Entry entry : section.getEntry()) {
                TemplateIdRoot foundRoot = null;

                //
                //  Find the correct template
                if (entry.getObservation() != null) {
                    POCDMT000040Observation observation = entry.getObservation();
                    if (observation.getTemplateId() != null) {
                        for (II id : observation.getTemplateId()) {
                            if (id.getRoot() != null) {
                                try {
                                    foundRoot = TemplateIdRoot.fromRoot(id.getRoot());
                                } catch (IllegalArgumentException e) {
                                }
                            }
                        }
                    }
                } else if (entry.getAct() != null) {
                    POCDMT000040Act act = entry.getAct();
                    if (act.getTemplateId() != null) {
                        for (II id : act.getTemplateId()) {
                            if (id.getRoot() != null) {
                                try {
                                    foundRoot = TemplateIdRoot.fromRoot(id.getRoot());
                                } catch (IllegalArgumentException e) {
                                }
                            }
                        }
                    }
                } else if (entry.getSubstanceAdministration() != null) {
                    POCDMT000040SubstanceAdministration substanceAdministration = entry.getSubstanceAdministration();
                    if (substanceAdministration.getTemplateId() != null) {
                        for (II id : substanceAdministration.getTemplateId()) {
                            if (id.getRoot() != null) {
                                try {
                                    foundRoot = TemplateIdRoot.fromRoot(id.getRoot());
                                } catch (IllegalArgumentException e) {
                                }
                            }
                        }
                    }
                } else if (entry.getProcedure() != null) {
                    POCDMT000040Procedure procedure = entry.getProcedure();
                    if (procedure.getTemplateId() != null) {
                        for (II id : procedure.getTemplateId()) {
                            if (id.getRoot() != null) {
                                try {
                                    foundRoot = TemplateIdRoot.fromRoot(id.getRoot());
                                } catch (IllegalArgumentException e) {
                                }
                            }
                        }
                    }
                } else if (entry.getOrganizer() != null) {
                    POCDMT000040Organizer organizer = entry.getOrganizer();
                    if (organizer.getTemplateId() != null) {
                        for (II id : organizer.getTemplateId()) {
                            if (id.getRoot() != null) {
                                try {
                                    foundRoot = TemplateIdRoot.fromRoot(id.getRoot());
                                } catch (IllegalArgumentException e) {
                                }
                            }
                        }
                    }
                } else if (entry.getEncounter() != null) {
                    POCDMT000040Encounter encounter = entry.getEncounter();
                    if (encounter.getTemplateId() != null) {
                        for (II id : encounter.getTemplateId()) {
                            if (id.getRoot() != null) {
                                try {
                                    foundRoot = TemplateIdRoot.fromRoot(id.getRoot());
                                } catch (IllegalArgumentException e) {
                                }
                            }
                        }
                    }
                } else if (entry.getSupply() != null) {
                    POCDMT000040Supply supply = entry.getSupply();
                    if (supply.getTemplateId() != null) {
                        for (II id : supply.getTemplateId()) {
                            if (id.getRoot() != null) {
                                try {
                                    foundRoot = TemplateIdRoot.fromRoot(id.getRoot());
                                } catch (IllegalArgumentException e) {
                                }
                            }
                        }
                    }
                }

                //
                //  Attempt to load the section using the appropiate section
                if (foundRoot != null) {

                    //
                    //  Find the matching template
                    TemplateRoot templateRoot = templateRootDao.findOrCreateTemplate(foundRoot.getRoot());

                    try {
                        switch (foundRoot) {
                            case ALLERGY__QRDA_MEDICATION_ADVERSE_EFFECT:
                            case ALLERGY__QRDA_MEDICATION_INTOLERANCE:
                            case ALLERGY__QRDA_MEDICATION_ALLERGY:
                            case ALLERGY__QRDA_INTOLERANCE:
                                allergyImporter.loadPatientAllergyEntry(entry, clinicalDocument, templateRoot);
                                break;
                            case ENCOUNTER__QRDA_PERFORMED:
                            case ENCOUNTER__QRDA_ORDER:
                                encounterImporter.loadPatientEncounterEntry(entry, clinicalDocument, templateRoot, practiceGroup);
                                break;
                            case MEDICAL_EQUIPMENT__QRDA:
                                medicalEquipmentImporter.loadMedicalEquipmentEntry(entry, clinicalDocument, templateRoot);
                                break;
                            case MEDICATIONS__QRDA_DISCHARGE_MEDICATION_ACTIIVE:
                            case MEDICATIONS__QRDA_MEDICATIONS_ADMINISTERED:
                            case MEDICATIONS__QRDA_MEDICATIONS_ADMINISTERED2:
                            case MEDICATIONS__QRDA_ORDERED:
                            case MEDICATIONS__QRDA_DISPENSED:
                                medicationImporter.loadPatientMedicationEntry(entry, clinicalDocument, templateRoot, codeMappings, existingItemCxt.getExistingMedications());
                                break;
                            case PLAN_OF_CARE__QRDA:
                                planOfCareImporter.loadPatientPlanOfCareEntry(entry, clinicalDocument, templateRoot);
                                break;
                            case CONDITION__QRDA_CHARACTERISTIC_AGE:
                            case CONDITION__QRDA_DIAGNOSIS_RESOLVED:
                            case CONDITION__QRDA_DIAGNOSIS_ACTIVE:
                            case CONDITION__QRDA_DIAGNOSIS_INACTIVE:
                            case CONDITION__QRDA_ECOG_STATUS:
                            case CONDITION__QRDA_GESTATIONAL_AGE:
                            case CONDITION__QRDA_SYMPTOM_ACTIVE:
                                problemImporter.loadPatientProblemEntry(entry, clinicalDocument, templateRoot, codeMappings, existingItemCxt.getExistingProblems());
                                break;
                            case PATIENT_CHARACTERISTIC_PAYER:
                                patientPayerImporter.loadPatientPayerEntry(entry, clinicalDocument, templateRoot);
                                break;
                            case PROCEDURE__QRDA_EXAM_PERFORMED:
                            case PROCEDURE__QRDA_PROVIDER_PATIENT_COMM:
                            case PROCEDURE__QRDA_INTERVENTION_ORDERED:
                            case PROCEDURE__QRDA_INTERVENTION_PERFORMED:
                            case PROCEDURE__QRDA_PROVIDER_PROVIDER_COMM:
                            case PROCEDURE__QRDA_PATIENT_PROVIDER_COMM:
                            case PROCEDURE__QRDA_PROCEDURE_PERFORMED:
                            case PROCEDURE__QRDA_UNLABELED_2:
                            case PROCEDURE__QRDA_RISK_CATEGORY_ASSESSMENT:
                            case PROCEDURE__QRDA_DIAGNOSIS_STUDY_PERFORMED:
                            case PROCEDURE__QRDA_DIAGNOSIS_STUDY_RESULT:
                            case PROCEDURE__QRDA_DIAGNOSTIC_STUDY_ORDER:
                            case PROCEDURE__QRDA_ORDER:
                                procedureImporter.loadPatientProcedureEntry(entry, clinicalDocument, templateRoot, codeMappings, existingItemCxt.getExistingProcedures());
                                break;
                            case RESULT__QRDA_FUNCTIONAL_FINDING:
                            case RESULT__QRDA_INTERVENTION:
                            case RESULT__QRDA_LAB_FINDING:
                            case RESULT__QRDA_LAB_PERFORMED:
                            case RESULT__QRDA_LAB_ORDER:
                            case RESULT__QRDA_LAB_RESULT:
                                resultImporter.loadPatientResultEntry(entry, clinicalDocument, templateRoot, codeMappings, existingItemCxt.getExistingResults());
                                break;
                            case SOCIAL_HISTORY__QRDA_TOBACCO_USE:
                                socialHistoryImporter.loadPatientSocialHistoryEntry(entry, clinicalDocument, templateRoot);
                                break;
                        }
                    } catch (Exception ex) {
                        logger.error("QRDA CAT_I Template processing error " + clinicalDocument.getFileName(), ex);
                        clinicalDocument.setParseStatusType(parseStatusTypeRepository.findOne(ParseStatus.PARSING_ERRORS.getTypeId()));
                        clinicalDocumentDao.saveClinicalDocumentParseError(clinicalDocument, ex);
                    }
                }
            }
        }
    }
    

}
