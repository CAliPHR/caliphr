package com.ainq.caliphr.persistence.model.qrda.cat1.provider;

import lombok.Data;

import java.util.List;

/**
 * Created by mmelusky on 10/27/2015.
 */
@Data
public class QrdaCat1Provider {

    private List<QrdaCat1Id> id;
    private String providerName;
    private String firstName;
    private String lastName;
    private QrdaCat1Address address;
    private List<QrdaCat1Telecom> phoneNumbers;

}
