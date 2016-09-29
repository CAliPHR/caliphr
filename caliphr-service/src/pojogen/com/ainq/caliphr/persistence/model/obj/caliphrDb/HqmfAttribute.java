package com.ainq.caliphr.persistence.model.obj.caliphrDb;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.iface.IHqmfAttribute;
import com.ainq.caliphr.persistence.util.IPojoGenEntity;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Parameter;
import org.hibernate.proxy.HibernateProxy;

/**
 * Object mapping for hibernate-handled table: hqmf_attribute.
 * 
 *
 * @author autogenerated
 */
		

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;


import javax.persistence.*;

@Entity
@Table(name = "hqmf_attribute", catalog = "caliphr_db", schema = "caliphr")
@Cache(region = "com.ainq.caliphr.persistence.model.obj.caliphrDb.HqmfAttribute", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class HqmfAttribute implements Cloneable, Serializable, IPojoGenEntity, IHqmfAttribute {

	/** Serial Version UID. */
	private static final long serialVersionUID = -6408039003008450604L;

	/** Use a WeakHashMap so entries will be garbage collected once all entities 
		referring to a saved hash are garbage collected themselves. */
	private static final Map<Serializable, Long> SAVED_HASHES =
		Collections.synchronizedMap(new WeakHashMap<>());
	
	/** hashCode temporary storage. */
	private volatile Long hashCode;
	

	/** Field mapping. */
	private String attributeName;
	/** Field mapping. */
	private String code;
	/** Field mapping. */
	private String codeObjJson;
	/** Field mapping. */
	private Date dateCreated;
	/** Field mapping. */
	private Date dateDisabled;
	/** Field mapping. */
	private Date dateUpdated;
	/** Field mapping. */
	private HqmfDocument hqmfDoc;
	/** Field mapping. */
	private Long id;
	/** Field mapping. */
	private Integer userCreated;
	/** Field mapping. */
	private Integer userUpdated;
	/** Field mapping. */
	private String valueObjJson;
	/**
	 * Default constructor, mainly for hibernate use.
	 */
	public HqmfAttribute() {
		// Default constructor
	} 

	/** Constructor taking a given ID.
	 * @param id to set
	 */
	public HqmfAttribute(Long id) {
		this.id = id;
	}
	
 


 
	/** Return the type of this class. Useful for when dealing with proxies.
	* @return Defining class.
	*/
	@Transient
	public Class<?> getClassType() {
		return HqmfAttribute.class;
	}


	 /**
	 * Return the value associated with the column: attributeName.
	 * @return A String object (this.attributeName)
	 */
	@Basic( optional = true )
	@Column( name = "attribute_name", length = 250  )
	public String getAttributeName() {
		return this.attributeName;
		
	}
	

  
	 /**  
	 * Set the value related to the column: attributeName.
	 * @param attributeName the attributeName value you wish to set
	 */
	public void setAttributeName(final String attributeName) {
		this.attributeName = attributeName;
	}

	 /**
	 * Return the value associated with the column: code.
	 * @return A String object (this.code)
	 */
	@Basic( optional = true )
	@Column( length = 250  )
	public String getCode() {
		return this.code;
		
	}
	

  
	 /**  
	 * Set the value related to the column: code.
	 * @param code the code value you wish to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	 /**
	 * Return the value associated with the column: codeObjJson.
	 * @return A String object (this.codeObjJson)
	 */
	@Basic( optional = true )
	@Column( name = "code_obj_json", length = 2147483647  )
	public String getCodeObjJson() {
		return this.codeObjJson;
		
	}
	

  
	 /**  
	 * Set the value related to the column: codeObjJson.
	 * @param codeObjJson the codeObjJson value you wish to set
	 */
	public void setCodeObjJson(final String codeObjJson) {
		this.codeObjJson = codeObjJson;
	}

	 /**
	 * Return the value associated with the column: dateCreated.
	 * @return A Date object (this.dateCreated)
	 */
	@Basic( optional = true )
	@Column( name = "date_created"  )
	public Date getDateCreated() {
		return this.dateCreated;
		
	}
	

  
	 /**  
	 * Set the value related to the column: dateCreated.
	 * @param dateCreated the dateCreated value you wish to set
	 */
	public void setDateCreated(final Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	 /**
	 * Return the value associated with the column: dateDisabled.
	 * @return A Date object (this.dateDisabled)
	 */
	@Basic( optional = true )
	@Column( name = "date_disabled"  )
	public Date getDateDisabled() {
		return this.dateDisabled;
		
	}
	

  
	 /**  
	 * Set the value related to the column: dateDisabled.
	 * @param dateDisabled the dateDisabled value you wish to set
	 */
	public void setDateDisabled(final Date dateDisabled) {
		this.dateDisabled = dateDisabled;
	}

	 /**
	 * Return the value associated with the column: dateUpdated.
	 * @return A Date object (this.dateUpdated)
	 */
	@Basic( optional = true )
	@Column( name = "date_updated"  )
	public Date getDateUpdated() {
		return this.dateUpdated;
		
	}
	

  
	 /**  
	 * Set the value related to the column: dateUpdated.
	 * @param dateUpdated the dateUpdated value you wish to set
	 */
	public void setDateUpdated(final Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	 /**
	 * Return the value associated with the column: hqmfDoc.
	 * @return A HqmfDocument object (this.hqmfDoc)
	 */

	@JsonBackReference @ManyToOne( cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY )
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@Basic( optional = true )
	@JoinColumn(name = "hqmf_doc_id", nullable = true )
	public HqmfDocument getHqmfDoc() {
		return this.hqmfDoc;
		
	}
	

  
	 /**  
	 * Set the value related to the column: hqmfDoc.
	 * @param hqmfDoc the hqmfDoc value you wish to set
	 */
	public void setHqmfDoc(final HqmfDocument hqmfDoc) {
		this.hqmfDoc = hqmfDoc;
	}

	 /**
	 * Return the value associated with the column: id.
	 * @return A Long object (this.id)
	 */
    @Id 
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hqmfAttributeAttribute_idGenerator")
	@Basic( optional = false )
	@Column( name = "attribute_id", nullable = false  )
	@SequenceGenerator(allocationSize = 50, name = "hqmfAttributeAttribute_idGenerator", sequenceName = "caliphr_db.caliphr.hqmf_attribute_id_seq", schema = "caliphr", catalog = "caliphr_db")
	public Long getId() {
		return this.id;
		
	}
	

  
	 /**  
	 * Set the value related to the column: id.
	 * @param id the id value you wish to set
	 */
	public void setId(final Long id) {
		// If we've just been persisted and hashCode has been
		// returned then make sure other entities with this
		// ID return the already returned hash code
		if ( (this.id == null || this.id == 0L) &&
				(id != null) &&
				(this.hashCode != null) ) {
		SAVED_HASHES.put( id, this.hashCode );
		}
		this.id = id;
	}

	 /**
	 * Return the value associated with the column: userCreated.
	 * @return A Integer object (this.userCreated)
	 */
	@Basic( optional = true )
	@Column( name = "user_created"  )
	public Integer getUserCreated() {
		return this.userCreated;
		
	}
	

  
	 /**  
	 * Set the value related to the column: userCreated.
	 * @param userCreated the userCreated value you wish to set
	 */
	public void setUserCreated(final Integer userCreated) {
		this.userCreated = userCreated;
	}

	 /**
	 * Return the value associated with the column: userUpdated.
	 * @return A Integer object (this.userUpdated)
	 */
	@Basic( optional = true )
	@Column( name = "user_updated"  )
	public Integer getUserUpdated() {
		return this.userUpdated;
		
	}
	

  
	 /**  
	 * Set the value related to the column: userUpdated.
	 * @param userUpdated the userUpdated value you wish to set
	 */
	public void setUserUpdated(final Integer userUpdated) {
		this.userUpdated = userUpdated;
	}

	 /**
	 * Return the value associated with the column: valueObjJson.
	 * @return A String object (this.valueObjJson)
	 */
	@Basic( optional = true )
	@Column( name = "value_obj_json", length = 2147483647  )
	public String getValueObjJson() {
		return this.valueObjJson;
		
	}
	

  
	 /**  
	 * Set the value related to the column: valueObjJson.
	 * @param valueObjJson the valueObjJson value you wish to set
	 */
	public void setValueObjJson(final String valueObjJson) {
		this.valueObjJson = valueObjJson;
	}


   /**
    * Deep copy.
	* @return cloned object
	* @throws CloneNotSupportedException on error
    */
    @Override
    public HqmfAttribute clone() throws CloneNotSupportedException {
		
        final HqmfAttribute copy = (HqmfAttribute)super.clone();

 		copy.setAttributeName(this.getAttributeName());
 		copy.setCode(this.getCode());
 		copy.setCodeObjJson(this.getCodeObjJson());
 		copy.setDateCreated(this.getDateCreated());
 		copy.setDateDisabled(this.getDateDisabled());
 		copy.setDateUpdated(this.getDateUpdated());
 		copy.setHqmfDoc(this.getHqmfDoc());
 		copy.setId(this.getId());
 		copy.setUserCreated(this.getUserCreated());
 		copy.setUserUpdated(this.getUserUpdated());
 		copy.setValueObjJson(this.getValueObjJson());
		return copy;
	}
	


	/** Provides toString implementation.
	 * @see java.lang.Object#toString()
	 * @return String representation of this class.
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("attributeName: " + this.getAttributeName() + ", ");
		sb.append("code: " + this.getCode() + ", ");
		sb.append("codeObjJson: " + this.getCodeObjJson() + ", ");
		sb.append("dateCreated: " + this.getDateCreated() + ", ");
		sb.append("dateDisabled: " + this.getDateDisabled() + ", ");
		sb.append("dateUpdated: " + this.getDateUpdated() + ", ");
		sb.append("id: " + this.getId() + ", ");
		sb.append("userCreated: " + this.getUserCreated() + ", ");
		sb.append("userUpdated: " + this.getUserUpdated() + ", ");
		sb.append("valueObjJson: " + this.getValueObjJson());
		return sb.toString();		
	}


	/** Equals implementation. 
	 * @param aThat Object to compare with
	 * @return true/false
	 */
	@Override
	public boolean equals(final Object aThat) {
		Object proxyThat = aThat;
		
		if ( this == aThat ) {
			 return true;
		}

		
		if (aThat instanceof HibernateProxy) {
 			// narrow down the proxy to the class we are dealing with.
 			try {
				proxyThat = ((HibernateProxy) aThat).getHibernateLazyInitializer().getImplementation(); 
			} catch (org.hibernate.ObjectNotFoundException e) {
				return false;
		   	}
		}
		if (aThat == null)  {
			 return false;
		}
		
		final HqmfAttribute that; 
		try {
			that = (HqmfAttribute) proxyThat;
			if ( !(that.getClassType().equals(this.getClassType()))){
				return false;
			}
		} catch (org.hibernate.ObjectNotFoundException e) {
				return false;
		} catch (ClassCastException e) {
				return false;
		}
		
		
		boolean result = true;
		result = result && (((this.getId() == null) && ( that.getId() == null)) || (this.getId() != null  && this.getId().equals(that.getId())));
		result = result && (((getAttributeName() == null) && (that.getAttributeName() == null)) || (getAttributeName() != null && getAttributeName().equals(that.getAttributeName())));
		result = result && (((getCode() == null) && (that.getCode() == null)) || (getCode() != null && getCode().equals(that.getCode())));
		result = result && (((getCodeObjJson() == null) && (that.getCodeObjJson() == null)) || (getCodeObjJson() != null && getCodeObjJson().equals(that.getCodeObjJson())));
		result = result && (((getDateCreated() == null) && (that.getDateCreated() == null)) || (getDateCreated() != null && getDateCreated().equals(that.getDateCreated())));
		result = result && (((getDateDisabled() == null) && (that.getDateDisabled() == null)) || (getDateDisabled() != null && getDateDisabled().equals(that.getDateDisabled())));
		result = result && (((getDateUpdated() == null) && (that.getDateUpdated() == null)) || (getDateUpdated() != null && getDateUpdated().equals(that.getDateUpdated())));
		result = result && (((getHqmfDoc() == null) && (that.getHqmfDoc() == null)) || (getHqmfDoc() != null && getHqmfDoc().getId().equals(that.getHqmfDoc().getId())));	
		result = result && (((getUserCreated() == null) && (that.getUserCreated() == null)) || (getUserCreated() != null && getUserCreated().equals(that.getUserCreated())));
		result = result && (((getUserUpdated() == null) && (that.getUserUpdated() == null)) || (getUserUpdated() != null && getUserUpdated().equals(that.getUserUpdated())));
		result = result && (((getValueObjJson() == null) && (that.getValueObjJson() == null)) || (getValueObjJson() != null && getValueObjJson().equals(that.getValueObjJson())));
		return result;
	}
	
	/** Calculate the hashcode.
	 * @see java.lang.Object#hashCode()
	 * @return a calculated number
	 */
	@Override
	public int hashCode() {
		if ( this.hashCode == null ) {
			synchronized ( this ) {
				if ( this.hashCode == null ) {
					Long newHashCode = null;

					if ( getId() != null ) {
					newHashCode = SAVED_HASHES.get( getId() );
					}
					
					if ( newHashCode == null ) {
						if ( getId() != null && getId() != 0L) {
							newHashCode = getId();
						} else {
							newHashCode = (long) super.hashCode();

						}
					}
					
					this.hashCode = newHashCode;
				}
			}
		}
		return (int) (this.hashCode & 0xffffff);
	}
	

	
	@PreUpdate
	public void preUpdate() {
		this.dateUpdated = new Date();
	}
}