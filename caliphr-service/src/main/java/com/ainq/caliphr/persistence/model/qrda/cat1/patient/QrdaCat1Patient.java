package com.ainq.caliphr.persistence.model.qrda.cat1.patient;

import lombok.Data;

/**
 * Created by mmelusky on 10/26/2015.
 */
@Data
public class QrdaCat1Patient {

    private String medicalRecordNumber;
    private String sourceOid;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String firstName;
    private String lastName;
    private String gender;
    private String birthTime;
    private String race;
    private String ethnicity;
    private String language;

}
