package com.ainq.caliphr.persistence.model.ccda;

/**
 * 
 * This class was not created as a proper enum so it can be extended
 * 
 * Created by mmelusky on 2/15/2016.
 */
public class EhrVendorType {

    public static final EhrVendorType UNKNOWN_VENDOR = new EhrVendorType(0);

    private Integer vendorId;

    // Constructor
    public EhrVendorType(Integer vendorId) {
        this.vendorId = vendorId;
    }

    // Getter
    public Integer getVendorId() {
        return vendorId;
    }
}
