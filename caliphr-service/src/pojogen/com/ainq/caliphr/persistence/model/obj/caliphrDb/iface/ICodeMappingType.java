package com.ainq.caliphr.persistence.model.obj.caliphrDb.iface;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.CodeMapping;
import com.ainq.caliphr.persistence.util.IPojoGenEntity;
import java.util.Date;
import java.util.Set;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;


/** 
 * Object interface mapping for hibernate-handled table: code_mapping_type.
 * @author autogenerated
 */

public interface ICodeMappingType {



    /**
     * Return the value associated with the column: codeMapping.
	 * @return A Set&lt;CodeMapping&gt; object (this.codeMapping)
	 */
	Set<CodeMapping> getCodeMappings();
	
	/**
	 * Adds a bi-directional link of type CodeMapping to the codeMappings set.
	 * @param codeMapping item to add
	 */
	void addCodeMapping(CodeMapping codeMapping);

  
    /**  
     * Set the value related to the column: codeMapping.
	 * @param codeMapping the codeMapping value you wish to set
	 */
	void setCodeMappings(final Set<CodeMapping> codeMapping);

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
     * Return the value associated with the column: typeDescription.
	 * @return A String object (this.typeDescription)
	 */
	String getTypeDescription();
	

  
    /**  
     * Set the value related to the column: typeDescription.
	 * @param typeDescription the typeDescription value you wish to set
	 */
	void setTypeDescription(final String typeDescription);

    /**
     * Return the value associated with the column: typeName.
	 * @return A String object (this.typeName)
	 */
	String getTypeName();
	

  
    /**  
     * Set the value related to the column: typeName.
	 * @param typeName the typeName value you wish to set
	 */
	void setTypeName(final String typeName);

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