package com.ainq.caliphr.persistence.transformation.cda;

/**
 * Created by mmelusky on 5/27/2015.
 */
public enum ClinicalDocumentType {

    C_CDA("2.16.840.1.113883.10.20.22.1.2"),
    CAT_I("2.16.840.1.113883.10.20.24.1.2");

    private final String root;

    ClinicalDocumentType(final String root) {
        this.root = root;
    }

    public String getRoot() {
        return root;
    }

    public static ClinicalDocumentType parseRoot(String root) {
        if (root != null) {
            for (ClinicalDocumentType templateIdRoot : ClinicalDocumentType.values()) {
                if (root.equalsIgnoreCase(templateIdRoot.root)) {
                    return templateIdRoot;
                }
            }
        }
        throw new IllegalArgumentException("Unsupported clinical document with value " + root + " found");
    }
}
