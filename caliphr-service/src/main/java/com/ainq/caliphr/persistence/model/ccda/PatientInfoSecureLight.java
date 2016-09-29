package com.ainq.caliphr.persistence.model.ccda;

import java.util.Date;

import lombok.Data;

public @Data class PatientInfoSecureLight {
	
	Integer id;
    Date birthTime;
    Date deathDate;

}
