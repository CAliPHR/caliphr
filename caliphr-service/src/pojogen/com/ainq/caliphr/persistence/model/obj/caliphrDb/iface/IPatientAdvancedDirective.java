package com.ainq.caliphr.persistence.model.obj.caliphrDb.iface;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Code;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.CodeMapping;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientInfo;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientNegationDetail;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.util.IPojoGenEntity;
import java.util.Date;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;


/** 
 * Object interface mapping for hibernate-handled table: patient_advanced_directive.
 * @author autogenerated
 */

public interface IPatientAdvancedDirective {



    /**
     * Return the value associated with the column: code.
	 * @return A Code object (this.code)
	 */
	Code getCode();
	

  
    /**  
     * Set the value related to the column: code.
	 * @param code the code value you wish to set
	 */
	void setCode(final Code code);

    /**
     * Return the value associated with the column: codeDescription.
	 * @return A String object (this.codeDescription)
	 */
	String getCodeDescription();
	

  
    /**  
     * Set the value related to the column: codeDescription.
	 * @param codeDescription the codeDescription value you wish to set
	 */
	void setCodeDescription(final String codeDescription);

    /**
     * Return the value associated with the column: codeMapping.
	 * @return A CodeMapping object (this.codeMapping)
	 */
	CodeMapping getCodeMapping();
	

  
    /**  
     * Set the value related to the column: codeMapping.
	 * @param codeMapping the codeMapping value you wish to set
	 */
	void setCodeMapping(final CodeMapping codeMapping);

    /**
     * Return the value associated with the column: dateCreated.
	 * @return A Date object (this.dateCreated)
	 */
	Date getDateCreated();
	

  
    /**  
     * Set the value related to the column: dateCreated.
	 * @param dateCreated the dateCreated value you wish to set
	 */
	void setDateCreated(final Date dateCreated);

    /**
     * Return the value associated with the column: dateDisabled.
	 * @return A Date object (this.dateDisabled)
	 */
	Date getDateDisabled();
	

  
    /**  
     * Set the value related to the column: dateDisabled.
	 * @param dateDisabled the dateDisabled value you wish to set
	 */
	void setDateDisabled(final Date dateDisabled);

    /**
     * Return the value associated with the column: dateUpdated.
	 * @return A Date object (this.dateUpdated)
	 */
	Date getDateUpdated();
	

  
    /**  
     * Set the value related to the column: dateUpdated.
	 * @param dateUpdated the dateUpdated value you wish to set
	 */
	void setDateUpdated(final Date dateUpdated);

    /**
     * Return the value associated with the column: effectiveTimeEnd.
	 * @return A Date object (this.effectiveTimeEnd)
	 */
	Date getEffectiveTimeEnd();
	

  
    /**  
     * Set the value related to the column: effectiveTimeEnd.
	 * @param effectiveTimeEnd the effectiveTimeEnd value you wish to set
	 */
	void setEffectiveTimeEnd(final Date effectiveTimeEnd);

    /**
     * Return the value associated with the column: effectiveTimeStart.
	 * @return A Date object (this.effectiveTimeStart)
	 */
	Date getEffectiveTimeStart();
	

  
    /**  
     * Set the value related to the column: effectiveTimeStart.
	 * @param effectiveTimeStart the effectiveTimeStart value you wish to set
	 */
	void setEffectiveTimeStart(final Date effectiveTimeStart);

    /**
     * Return the value associated with the column: externalId.
	 * @return A String object (this.externalId)
	 */
	String getExternalId();
	

  
    /**  
     * Set the value related to the column: externalId.
	 * @param externalId the externalId value you wish to set
	 */
	void setExternalId(final String externalId);

    /**
     * Return the value associated with the column: id.
	 * @return A Long object (this.id)
	 */
	Long getId();
	

  
    /**  
     * Set the value related to the column: id.
	 * @param id the id value you wish to set
	 */
	void setId(final Long id);

    /**
     * Return the value associated with the column: negationDetail.
	 * @return A PatientNegationDetail object (this.negationDetail)
	 */
	PatientNegationDetail getNegationDetail();
	

  
    /**  
     * Set the value related to the column: negationDetail.
	 * @param negationDetail the negationDetail value you wish to set
	 */
	void setNegationDetail(final PatientNegationDetail negationDetail);

    /**
     * Return the value associated with the column: patient.
	 * @return A PatientInfo object (this.patient)
	 */
	PatientInfo getPatient();
	

  
    /**  
     * Set the value related to the column: patient.
	 * @param patient the patient value you wish to set
	 */
	void setPatient(final PatientInfo patient);

    /**
     * Return the value associated with the column: template.
	 * @return A TemplateRoot object (this.template)
	 */
	TemplateRoot getTemplate();
	

  
    /**  
     * Set the value related to the column: template.
	 * @param template the template value you wish to set
	 */
	void setTemplate(final TemplateRoot template);

    /**
     * Return the value associated with the column: userCreated.
	 * @return A Integer object (this.userCreated)
	 */
	Integer getUserCreated();
	

  
    /**  
     * Set the value related to the column: userCreated.
	 * @param userCreated the userCreated value you wish to set
	 */
	void setUserCreated(final Integer userCreated);

    /**
     * Return the value associated with the column: userUpdated.
	 * @return A Integer object (this.userUpdated)
	 */
	Integer getUserUpdated();
	

  
    /**  
     * Set the value related to the column: userUpdated.
	 * @param userUpdated the userUpdated value you wish to set
	 */
	void setUserUpdated(final Integer userUpdated);

	// end of interface
}