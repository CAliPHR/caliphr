package com.ainq.caliphr.persistence.model.ccda;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientInfo;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientPhoneNumber;

import lombok.Data;

public @Data class PatientInfoHolder {
	
	private final PatientInfo patientInfo;
	private String address;
	private String address2;
	private Date birthTime;
	private String city;
	private String country;
	private Date deathDate;
	private String firstName;
	private String lastName;
	private String ssn;
	private Integer stateId;
	private String stateValue;
	private String zipcode;
	private String medicalRecordNumber;
	private List<PatientPhoneNumberHolder> phoneNumberHolders = new ArrayList<PatientPhoneNumberHolder>();

	public static @Data class PatientPhoneNumberHolder {
		private final PatientPhoneNumber patientPhoneNumber;
		private String phoneNumber;
	}
}
