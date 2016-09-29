package com.ainq.caliphr.common.model.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

/**
 * Created by mmelusky on 8/21/2015.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient {
    Integer id;
    String medicalRecordNumber;
    String firstName;
    String lastName;
    String gender;
    String address;
    String address2;
    String city;
    String stateId;
    String stateValue;
    String zipcode;
    String country;
    String language;
    String ethnicity;
    String race;
    Date birthTime;
    Date deathDate;
}
