package com.ainq.caliphr.hqmf.util;

import java.util.HashMap;
import java.util.Map;

public class CodeSystemHelper {
	
	@SuppressWarnings("serial")
	private static Map<String, String> CODE_SYSTEMS = new HashMap<String, String>() {{
        put("2.16.840.1.113883.6.1", "LOINC");
        put("2.16.840.1.113883.6.96", "SNOMED-CT");
        put("2.16.840.1.113883.6.12", "CPT");
        // #'2.16.840.1.113883.3.88.12.80.32", "CPT"; # Encounter Type from C32, a subset of CPT
        put("2.16.840.1.113883.6.88", "RxNorm");
        put("2.16.840.1.113883.6.103", "ICD-9-CM");
        put("2.16.840.1.113883.6.104", "ICD-9-PCS");
        put("2.16.840.1.113883.6.4", "ICD-10-PCS");
        put("2.16.840.1.113883.6.90", "ICD-10-CM");
        put("2.16.840.1.113883.6.14", "HCP");
        put("2.16.840.1.113883.6.285", "HCPCS");
        put("2.16.840.1.113883.5.2", "HL7 Marital Status");
        put("2.16.840.1.113883.12.292", "CVX");
        put("2.16.840.1.113883.5.83", "HITSP C80 Observation Status");
        put("2.16.840.1.113883.3.26.1.1", "NCI Thesaurus");
        put("2.16.840.1.113883.3.88.12.80.20", "FDA");
        put("2.16.840.1.113883.4.9", "UNII");
        put("2.16.840.1.113883.6.69", "NDC");
        put("2.16.840.1.113883.5.14", "HL7 ActStatus");
        put("2.16.840.1.113883.6.259", "HL7 Healthcare Service Location");
        put("2.16.840.1.113883.12.112", "DischargeDisposition");
        put("2.16.840.1.113883.5.4", "HL7 Act Code");
        put("2.16.840.1.113883.1.11.18877", "HL7 Relationship Code");
        put("2.16.840.1.113883.6.238", "CDC Race");
        put("2.16.840.1.113883.6.177", "NLM MeSH");
        put("2.16.840.1.113883.5.1076", "Religious Affiliation");
        put("2.16.840.1.113883.1.11.19717", "HL7 ActNoImmunicationReason");
        put("2.16.840.1.113883.3.88.12.80.33", "NUBC");
        put("2.16.840.1.113883.1.11.78", "HL7 Observation Interpretation");
        put("2.16.840.1.113883.3.221.5", "Source of Payment Typology");
        put("2.16.840.1.113883.6.13", "CDT");
        put("2.16.840.1.113883.18.2", "AdministrativeSex");
	}};
	
	@SuppressWarnings("serial")
	private static Map<String, String> OID_ALIASES = new HashMap<String, String>() {{
		put("2.16.840.1.113883.6.59", "2.16.840.1.113883.12.292"); //   # CVX")
	}};
	
	public static String codeSystemFor(String oid) {
		if (OID_ALIASES.containsKey(oid)) {
			oid = OID_ALIASES.get(oid);
		}
		return CODE_SYSTEMS.containsKey(oid) ? CODE_SYSTEMS.get(oid) : "Unknown";
	}

}
