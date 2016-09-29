package com.ainq.caliphr.persistence.model.obj.caliphrDb.iface;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.HqmfDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeAvailableMeasure;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ValueSet;
import com.ainq.caliphr.persistence.util.IPojoGenEntity;
import java.util.Date;
import java.util.Set;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;


/** 
 * Object interface mapping for hibernate-handled table: bundle.
 * @author autogenerated
 */

public interface IBundle {



    /**
     * Return the value associated with the column: bundleVersion.
	 * @return A String object (this.bundleVersion)
	 */
	String getBundleVersion();
	

  
    /**  
     * Set the value related to the column: bundleVersion.
	 * @param bundleVersion the bundleVersion value you wish to set
	 */
	void setBundleVersion(final String bundleVersion);

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
     * Return the value associated with the column: description.
	 * @return A String object (this.description)
	 */
	String getDescription();
	

  
    /**  
     * Set the value related to the column: description.
	 * @param description the description value you wish to set
	 */
	void setDescription(final String description);

    /**
     * Return the value associated with the column: hqmfDocument.
	 * @return A Set&lt;HqmfDocument&gt; object (this.hqmfDocument)
	 */
	Set<HqmfDocument> getHqmfDocuments();
	
	/**
	 * Adds a bi-directional link of type HqmfDocument to the hqmfDocuments set.
	 * @param hqmfDocument item to add
	 */
	void addHqmfDocument(HqmfDocument hqmfDocument);

  
    /**  
     * Set the value related to the column: hqmfDocument.
	 * @param hqmfDocument the hqmfDocument value you wish to set
	 */
	void setHqmfDocuments(final Set<HqmfDocument> hqmfDocument);

    /**
     * Return the value associated with the column: id.
	 * @return A Integer object (this.id)
	 */
	Integer getId();
	

  
    /**  
     * Set the value related to the column: id.
	 * @param id the id value you wish to set
	 */
	void setId(final Integer id);

    /**
     * Return the value associated with the column: practiceAvailableMeasure.
	 * @return A Set&lt;PracticeAvailableMeasure&gt; object (this.practiceAvailableMeasure)
	 */
	Set<PracticeAvailableMeasure> getPracticeAvailableMeasures();
	
	/**
	 * Adds a bi-directional link of type PracticeAvailableMeasure to the practiceAvailableMeasures set.
	 * @param practiceAvailableMeasure item to add
	 */
	void addPracticeAvailableMeasure(PracticeAvailableMeasure practiceAvailableMeasure);

  
    /**  
     * Set the value related to the column: practiceAvailableMeasure.
	 * @param practiceAvailableMeasure the practiceAvailableMeasure value you wish to set
	 */
	void setPracticeAvailableMeasures(final Set<PracticeAvailableMeasure> practiceAvailableMeasure);

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

    /**
     * Return the value associated with the column: valueSet.
	 * @return A Set&lt;ValueSet&gt; object (this.valueSet)
	 */
	Set<ValueSet> getValueSets();
	
	/**
	 * Adds a bi-directional link of type ValueSet to the valueSets set.
	 * @param valueSet item to add
	 */
	void addValueSet(ValueSet valueSet);

  
    /**  
     * Set the value related to the column: valueSet.
	 * @param valueSet the valueSet value you wish to set
	 */
	void setValueSets(final Set<ValueSet> valueSet);

	// end of interface
}