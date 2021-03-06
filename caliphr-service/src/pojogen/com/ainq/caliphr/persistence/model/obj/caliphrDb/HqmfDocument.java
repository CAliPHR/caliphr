package com.ainq.caliphr.persistence.model.obj.caliphrDb;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.HqmfAttribute;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.HqmfDataCriteria;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.HqmfPopulation;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.HqmfPopulationSet;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.iface.IHqmfDocument;
import com.ainq.caliphr.persistence.util.IPojoGenEntity;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Parameter;
import org.hibernate.proxy.HibernateProxy;

/**
 * Object mapping for hibernate-handled table: hqmf_document.
 * 
 *
 * @author autogenerated
 */
		

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;


import javax.persistence.*;

@Entity
@Table(name = "hqmf_document", catalog = "caliphr_db", schema = "caliphr")
@Cache(region = "com.ainq.caliphr.persistence.model.obj.caliphrDb.HqmfDocument", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class HqmfDocument implements Cloneable, Serializable, IPojoGenEntity, IHqmfDocument {

	/** Serial Version UID. */
	private static final long serialVersionUID = 3008832099000793108L;

	/** Use a WeakHashMap so entries will be garbage collected once all entities 
		referring to a saved hash are garbage collected themselves. */
	private static final Map<Serializable, Long> SAVED_HASHES =
		Collections.synchronizedMap(new WeakHashMap<>());
	
	/** hashCode temporary storage. */
	private volatile Long hashCode;
	

	/** Field mapping. */
	private Bundle bundle;
	/** Field mapping. */
	private String cmsId;
	/** Field mapping. */
	private Date dateCreated;
	/** Field mapping. */
	private Date dateDisabled;
	/** Field mapping. */
	private Date dateUpdated;
	/** Field mapping. */
	private String description;
	/** Field mapping. */
	private Domain domain;
	/** Field mapping. */
	private Set<HqmfAttribute> hqmfAttributes = new HashSet<>();

	/** Field mapping. */
	private Set<HqmfDataCriteria> hqmfDataCriterias = new HashSet<>();

	/** Field mapping. */
	private Set<HqmfPopulation> hqmfPopulations = new HashSet<>();

	/** Field mapping. */
	private Set<HqmfPopulationSet> hqmfPopulationSets = new HashSet<>();

	/** Field mapping. */
	private String hqmfId;
	/** Field mapping. */
	private String hqmfSetId;
	/** Field mapping. */
	private Integer hqmfVersionNumber;
	/** Field mapping. */
	private Long id;
	/** Field mapping. */
	private HqmfMeasurePeriod measurePeriod;
	/** Field mapping. */
	private Provider provider;
	/** Field mapping. */
	private String title;
	/** Field mapping. */
	private Integer userCreated;
	/** Field mapping. */
	private Integer userId;
	/** Field mapping. */
	private Integer userUpdated;
	/**
	 * Default constructor, mainly for hibernate use.
	 */
	public HqmfDocument() {
		// Default constructor
	} 

	/** Constructor taking a given ID.
	 * @param id to set
	 */
	public HqmfDocument(Long id) {
		this.id = id;
	}
	
 


 
	/** Return the type of this class. Useful for when dealing with proxies.
	* @return Defining class.
	*/
	@Transient
	public Class<?> getClassType() {
		return HqmfDocument.class;
	}


	 /**
	 * Return the value associated with the column: bundle.
	 * @return A Bundle object (this.bundle)
	 */

	@JsonBackReference @ManyToOne( cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY )
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@Basic( optional = true )
	@JoinColumn(name = "bundle_id", nullable = true )
	public Bundle getBundle() {
		return this.bundle;
		
	}
	

  
	 /**  
	 * Set the value related to the column: bundle.
	 * @param bundle the bundle value you wish to set
	 */
	public void setBundle(final Bundle bundle) {
		this.bundle = bundle;
	}

	 /**
	 * Return the value associated with the column: cmsId.
	 * @return A String object (this.cmsId)
	 */
	@Basic( optional = true )
	@Column( name = "cms_id", length = 2147483647  )
	public String getCmsId() {
		return this.cmsId;
		
	}
	

  
	 /**  
	 * Set the value related to the column: cmsId.
	 * @param cmsId the cmsId value you wish to set
	 */
	public void setCmsId(final String cmsId) {
		this.cmsId = cmsId;
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
	 * Return the value associated with the column: description.
	 * @return A String object (this.description)
	 */
	@Basic( optional = true )
	@Column( length = 2147483647  )
	public String getDescription() {
		return this.description;
		
	}
	

  
	 /**  
	 * Set the value related to the column: description.
	 * @param description the description value you wish to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	 /**
	 * Return the value associated with the column: domain.
	 * @return A Domain object (this.domain)
	 */

	@JsonBackReference @ManyToOne( cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY )
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@Basic( optional = true )
	@JoinColumn(name = "domain_id", nullable = true )
	public Domain getDomain() {
		return this.domain;
		
	}
	

  
	 /**  
	 * Set the value related to the column: domain.
	 * @param domain the domain value you wish to set
	 */
	public void setDomain(final Domain domain) {
		this.domain = domain;
	}

	 /**
	 * Return the value associated with the column: hqmfAttribute.
	 * @return A Set&lt;HqmfAttribute&gt; object (this.hqmfAttribute)
	 */
	@JsonManagedReference @OneToMany( fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "hqmfDoc"  )
 	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@Basic( optional = false )
	@Column( name = "hqmf_doc_id", nullable = false  )
	public Set<HqmfAttribute> getHqmfAttributes() {
		return this.hqmfAttributes;
		
	}
	
	/**
	 * Adds a bi-directional link of type HqmfAttribute to the hqmfAttributes set.
	 * @param hqmfAttribute item to add
	 */
	public void addHqmfAttribute(HqmfAttribute hqmfAttribute) {
		hqmfAttribute.setHqmfDoc(this);
		this.hqmfAttributes.add(hqmfAttribute);
	}

  
	 /**  
	 * Set the value related to the column: hqmfAttribute.
	 * @param hqmfAttribute the hqmfAttribute value you wish to set
	 */
	public void setHqmfAttributes(final Set<HqmfAttribute> hqmfAttribute) {
		this.hqmfAttributes = hqmfAttribute;
	}

	 /**
	 * Return the value associated with the column: hqmfDataCriteria.
	 * @return A Set&lt;HqmfDataCriteria&gt; object (this.hqmfDataCriteria)
	 */
	@JsonManagedReference @OneToMany( fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "hqmfDoc"  )
 	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@Basic( optional = false )
	@Column( name = "hqmf_doc_id", nullable = false  )
	public Set<HqmfDataCriteria> getHqmfDataCriterias() {
		return this.hqmfDataCriterias;
		
	}
	
	/**
	 * Adds a bi-directional link of type HqmfDataCriteria to the hqmfDataCriterias set.
	 * @param hqmfDataCriteria item to add
	 */
	public void addHqmfDataCriteria(HqmfDataCriteria hqmfDataCriteria) {
		hqmfDataCriteria.setHqmfDoc(this);
		this.hqmfDataCriterias.add(hqmfDataCriteria);
	}

  
	 /**  
	 * Set the value related to the column: hqmfDataCriteria.
	 * @param hqmfDataCriteria the hqmfDataCriteria value you wish to set
	 */
	public void setHqmfDataCriterias(final Set<HqmfDataCriteria> hqmfDataCriteria) {
		this.hqmfDataCriterias = hqmfDataCriteria;
	}

	 /**
	 * Return the value associated with the column: hqmfPopulation.
	 * @return A Set&lt;HqmfPopulation&gt; object (this.hqmfPopulation)
	 */
	@JsonManagedReference @OneToMany( fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "hqmfDoc"  )
 	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@Basic( optional = false )
	@Column( name = "hqmf_doc_id", nullable = false  )
	public Set<HqmfPopulation> getHqmfPopulations() {
		return this.hqmfPopulations;
		
	}
	
	/**
	 * Adds a bi-directional link of type HqmfPopulation to the hqmfPopulations set.
	 * @param hqmfPopulation item to add
	 */
	public void addHqmfPopulation(HqmfPopulation hqmfPopulation) {
		hqmfPopulation.setHqmfDoc(this);
		this.hqmfPopulations.add(hqmfPopulation);
	}

  
	 /**  
	 * Set the value related to the column: hqmfPopulation.
	 * @param hqmfPopulation the hqmfPopulation value you wish to set
	 */
	public void setHqmfPopulations(final Set<HqmfPopulation> hqmfPopulation) {
		this.hqmfPopulations = hqmfPopulation;
	}

	 /**
	 * Return the value associated with the column: hqmfPopulationSet.
	 * @return A Set&lt;HqmfPopulationSet&gt; object (this.hqmfPopulationSet)
	 */
	@JsonManagedReference @OneToMany( fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "hqmfDocument"  )
 	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@Basic( optional = false )
	@Column( name = "hqmf_doc_id", nullable = false  )
	public Set<HqmfPopulationSet> getHqmfPopulationSets() {
		return this.hqmfPopulationSets;
		
	}
	
	/**
	 * Adds a bi-directional link of type HqmfPopulationSet to the hqmfPopulationSets set.
	 * @param hqmfPopulationSet item to add
	 */
	public void addHqmfPopulationSet(HqmfPopulationSet hqmfPopulationSet) {
		hqmfPopulationSet.setHqmfDocument(this);
		this.hqmfPopulationSets.add(hqmfPopulationSet);
	}

  
	 /**  
	 * Set the value related to the column: hqmfPopulationSet.
	 * @param hqmfPopulationSet the hqmfPopulationSet value you wish to set
	 */
	public void setHqmfPopulationSets(final Set<HqmfPopulationSet> hqmfPopulationSet) {
		this.hqmfPopulationSets = hqmfPopulationSet;
	}

	 /**
	 * Return the value associated with the column: hqmfId.
	 * @return A String object (this.hqmfId)
	 */
	@Basic( optional = true )
	@Column( name = "hqmf_id", length = 2147483647  )
	public String getHqmfId() {
		return this.hqmfId;
		
	}
	

  
	 /**  
	 * Set the value related to the column: hqmfId.
	 * @param hqmfId the hqmfId value you wish to set
	 */
	public void setHqmfId(final String hqmfId) {
		this.hqmfId = hqmfId;
	}

	 /**
	 * Return the value associated with the column: hqmfSetId.
	 * @return A String object (this.hqmfSetId)
	 */
	@Basic( optional = true )
	@Column( name = "hqmf_set_id", length = 2147483647  )
	public String getHqmfSetId() {
		return this.hqmfSetId;
		
	}
	

  
	 /**  
	 * Set the value related to the column: hqmfSetId.
	 * @param hqmfSetId the hqmfSetId value you wish to set
	 */
	public void setHqmfSetId(final String hqmfSetId) {
		this.hqmfSetId = hqmfSetId;
	}

	 /**
	 * Return the value associated with the column: hqmfVersionNumber.
	 * @return A Integer object (this.hqmfVersionNumber)
	 */
	@Basic( optional = true )
	@Column( name = "hqmf_version_number"  )
	public Integer getHqmfVersionNumber() {
		return this.hqmfVersionNumber;
		
	}
	

  
	 /**  
	 * Set the value related to the column: hqmfVersionNumber.
	 * @param hqmfVersionNumber the hqmfVersionNumber value you wish to set
	 */
	public void setHqmfVersionNumber(final Integer hqmfVersionNumber) {
		this.hqmfVersionNumber = hqmfVersionNumber;
	}

	 /**
	 * Return the value associated with the column: id.
	 * @return A Long object (this.id)
	 */
    @Id 
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hqmfDocumentHqmf_doc_idGenerator")
	@Basic( optional = false )
	@Column( name = "hqmf_doc_id", nullable = false  )
	@SequenceGenerator(allocationSize = 50, name = "hqmfDocumentHqmf_doc_idGenerator", sequenceName = "caliphr_db.caliphr.hqmf_document_id_seq", schema = "caliphr", catalog = "caliphr_db")
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
	 * Return the value associated with the column: measurePeriod.
	 * @return A HqmfMeasurePeriod object (this.measurePeriod)
	 */

	@JsonBackReference @ManyToOne( cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY )
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@Basic( optional = true )
	@JoinColumn(name = "measure_period_id", nullable = true )
	public HqmfMeasurePeriod getMeasurePeriod() {
		return this.measurePeriod;
		
	}
	

  
	 /**  
	 * Set the value related to the column: measurePeriod.
	 * @param measurePeriod the measurePeriod value you wish to set
	 */
	public void setMeasurePeriod(final HqmfMeasurePeriod measurePeriod) {
		this.measurePeriod = measurePeriod;
	}

	 /**
	 * Return the value associated with the column: provider.
	 * @return A Provider object (this.provider)
	 */

	@JsonBackReference @ManyToOne( cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY )
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@Basic( optional = true )
	@JoinColumn(name = "provider_id", nullable = true )
	public Provider getProvider() {
		return this.provider;
		
	}
	

  
	 /**  
	 * Set the value related to the column: provider.
	 * @param provider the provider value you wish to set
	 */
	public void setProvider(final Provider provider) {
		this.provider = provider;
	}

	 /**
	 * Return the value associated with the column: title.
	 * @return A String object (this.title)
	 */
	@Basic( optional = true )
	@Column( length = 2147483647  )
	public String getTitle() {
		return this.title;
		
	}
	

  
	 /**  
	 * Set the value related to the column: title.
	 * @param title the title value you wish to set
	 */
	public void setTitle(final String title) {
		this.title = title;
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
	 * Return the value associated with the column: userId.
	 * @return A Integer object (this.userId)
	 */
	@Basic( optional = true )
	@Column( name = "user_id"  )
	public Integer getUserId() {
		return this.userId;
		
	}
	

  
	 /**  
	 * Set the value related to the column: userId.
	 * @param userId the userId value you wish to set
	 */
	public void setUserId(final Integer userId) {
		this.userId = userId;
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
    * Deep copy.
	* @return cloned object
	* @throws CloneNotSupportedException on error
    */
    @Override
    public HqmfDocument clone() throws CloneNotSupportedException {
		
        final HqmfDocument copy = (HqmfDocument)super.clone();

 		copy.setBundle(this.getBundle());
 		copy.setCmsId(this.getCmsId());
 		copy.setDateCreated(this.getDateCreated());
 		copy.setDateDisabled(this.getDateDisabled());
 		copy.setDateUpdated(this.getDateUpdated());
 		copy.setDescription(this.getDescription());
 		copy.setDomain(this.getDomain());
		if (this.getHqmfAttributes() != null) {
			copy.getHqmfAttributes().addAll(this.getHqmfAttributes());
		}
		if (this.getHqmfDataCriterias() != null) {
			copy.getHqmfDataCriterias().addAll(this.getHqmfDataCriterias());
		}
		if (this.getHqmfPopulations() != null) {
			copy.getHqmfPopulations().addAll(this.getHqmfPopulations());
		}
		if (this.getHqmfPopulationSets() != null) {
			copy.getHqmfPopulationSets().addAll(this.getHqmfPopulationSets());
		}
 		copy.setHqmfId(this.getHqmfId());
 		copy.setHqmfSetId(this.getHqmfSetId());
 		copy.setHqmfVersionNumber(this.getHqmfVersionNumber());
 		copy.setId(this.getId());
 		copy.setMeasurePeriod(this.getMeasurePeriod());
 		copy.setProvider(this.getProvider());
 		copy.setTitle(this.getTitle());
 		copy.setUserCreated(this.getUserCreated());
 		copy.setUserId(this.getUserId());
 		copy.setUserUpdated(this.getUserUpdated());
		return copy;
	}
	


	/** Provides toString implementation.
	 * @see java.lang.Object#toString()
	 * @return String representation of this class.
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("cmsId: " + this.getCmsId() + ", ");
		sb.append("dateCreated: " + this.getDateCreated() + ", ");
		sb.append("dateDisabled: " + this.getDateDisabled() + ", ");
		sb.append("dateUpdated: " + this.getDateUpdated() + ", ");
		sb.append("description: " + this.getDescription() + ", ");
		sb.append("hqmfId: " + this.getHqmfId() + ", ");
		sb.append("hqmfSetId: " + this.getHqmfSetId() + ", ");
		sb.append("hqmfVersionNumber: " + this.getHqmfVersionNumber() + ", ");
		sb.append("id: " + this.getId() + ", ");
		sb.append("title: " + this.getTitle() + ", ");
		sb.append("userCreated: " + this.getUserCreated() + ", ");
		sb.append("userId: " + this.getUserId() + ", ");
		sb.append("userUpdated: " + this.getUserUpdated());
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
		
		final HqmfDocument that; 
		try {
			that = (HqmfDocument) proxyThat;
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
		result = result && (((getBundle() == null) && (that.getBundle() == null)) || (getBundle() != null && getBundle().getId().equals(that.getBundle().getId())));	
		result = result && (((getCmsId() == null) && (that.getCmsId() == null)) || (getCmsId() != null && getCmsId().equals(that.getCmsId())));
		result = result && (((getDateCreated() == null) && (that.getDateCreated() == null)) || (getDateCreated() != null && getDateCreated().equals(that.getDateCreated())));
		result = result && (((getDateDisabled() == null) && (that.getDateDisabled() == null)) || (getDateDisabled() != null && getDateDisabled().equals(that.getDateDisabled())));
		result = result && (((getDateUpdated() == null) && (that.getDateUpdated() == null)) || (getDateUpdated() != null && getDateUpdated().equals(that.getDateUpdated())));
		result = result && (((getDescription() == null) && (that.getDescription() == null)) || (getDescription() != null && getDescription().equals(that.getDescription())));
		result = result && (((getDomain() == null) && (that.getDomain() == null)) || (getDomain() != null && getDomain().getId().equals(that.getDomain().getId())));	
		result = result && (((getHqmfId() == null) && (that.getHqmfId() == null)) || (getHqmfId() != null && getHqmfId().equals(that.getHqmfId())));
		result = result && (((getHqmfSetId() == null) && (that.getHqmfSetId() == null)) || (getHqmfSetId() != null && getHqmfSetId().equals(that.getHqmfSetId())));
		result = result && (((getHqmfVersionNumber() == null) && (that.getHqmfVersionNumber() == null)) || (getHqmfVersionNumber() != null && getHqmfVersionNumber().equals(that.getHqmfVersionNumber())));
		result = result && (((getMeasurePeriod() == null) && (that.getMeasurePeriod() == null)) || (getMeasurePeriod() != null && getMeasurePeriod().getId().equals(that.getMeasurePeriod().getId())));	
		result = result && (((getProvider() == null) && (that.getProvider() == null)) || (getProvider() != null && getProvider().getId().equals(that.getProvider().getId())));	
		result = result && (((getTitle() == null) && (that.getTitle() == null)) || (getTitle() != null && getTitle().equals(that.getTitle())));
		result = result && (((getUserCreated() == null) && (that.getUserCreated() == null)) || (getUserCreated() != null && getUserCreated().equals(that.getUserCreated())));
		result = result && (((getUserId() == null) && (that.getUserId() == null)) || (getUserId() != null && getUserId().equals(that.getUserId())));
		result = result && (((getUserUpdated() == null) && (that.getUserUpdated() == null)) || (getUserUpdated() != null && getUserUpdated().equals(that.getUserUpdated())));
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
