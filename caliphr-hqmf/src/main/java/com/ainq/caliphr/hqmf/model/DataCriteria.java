package com.ainq.caliphr.hqmf.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ainq.caliphr.hqmf.model.type.HQMFAnyValue;
import com.ainq.caliphr.hqmf.model.type.HQMFCoded;
import com.ainq.caliphr.hqmf.model.type.HQMFEffectiveTime;
import com.ainq.caliphr.hqmf.model.type.HQMFRange;
import com.ainq.caliphr.hqmf.model.type.HQMFSubsetOperator;
import com.ainq.caliphr.hqmf.model.type.HQMFTemporalReference;
import com.ainq.caliphr.hqmf.model.type.HQMFValue;
import com.ainq.caliphr.hqmf.util.CodeSystemHelper;
import com.ainq.caliphr.hqmf.util.HQMFJsonUtil;
import com.ainq.caliphr.hqmf.util.HQMFReferenceUtil;
import com.ainq.caliphr.hqmf.util.XPathUtil;
import com.google.gson.JsonObject;

public class DataCriteria {
	
	private String id;
	private String title;
	private String description;
	private String codeListId;
	private String property;
	private String type;
	private String patientApiFunction;
	private String definition;
	private String status;
	private boolean negation;
	private String specificOccurrence;
	private String specificOccurrenceConst;
	private String sourceDataCriteria;
	private String negationCodeListId;
	private HQMFEffectiveTime effectiveTime;
	private List<String> childrenCriteria;
	private String derivationOperator;
	private boolean variable;
	private boolean isSourceDataDriteria;
	private String codeListXpath;
	private Object value;
	private Map<String, List<String>> inlineCodeList;
	private Map<String, Object> fieldValues; 
	private List<HQMFSubsetOperator> subsetOperators;
	private List<HQMFTemporalReference> temporalReferences;
	
	@SuppressWarnings("serial")
	private static final Map<String, String> CONJUNCTION_CODE_TO_DERIVATION_OP = new HashMap<String, String>() {{
		put("OR" , "UNION");
		put("AND", "XPRODUCT");
	}};

	private static final String CRITERIA_GLOB = "*[substring(name(),string-length(name())-7) = \'Criteria\']";
	
	@SuppressWarnings("serial")
	private static final Map<String, String> VALUE_FIELDS = new HashMap<String, String>() {{
 		put("SEV", "SEVERITY");
        put("117363000", "ORDINAL");
        put("410666004", "REASON");
        put("260753009", "SOURCE");
        put("363819003", "CUMULATIVE_MEDICATION_DURATION");
        put("SDLOC", "FACILITY_LOCATION");
        put("442864001", "DISCHARGE_DATETIME");
        put("309039003", "DISCHARGE_STATUS");
        put("399423000", "ADMISSION_DATETIME");
        put("183797002", "LENGTH_OF_STAY");
        put("398232005", "DOSE");
        put("263513008", "ROUTE");
        put("398201009", "START_DATETIME");
        put("260864003", "FREQUENCY");
        put("91723000", "NATOMICAL_STRUCTURE");
        put("397898000", "STOP_DATETIME");
        put("34896006", "NCISION_DATETIME");
        put("118292001", "REMOVAL_DATETIME");
        put("SDLOC_ARRIVAL", "FACILITY_LOCATION_ARRIVAL_DATETIME");
        put("SDLOC_DEPARTURE", "FACILITY_LOCATION_DEPARTURE_DATETIME");
	}};
	
	public DataCriteria() {
		
	}
	
	public DataCriteria(Node node) {
		id = XPathUtil.evalOrNull("./*/cda:id/@extension", node);
		status = XPathUtil.evalOrNull("./*/cda:statusCode/@code", node);
		description = XPathUtil.evalOrNull("./" + CRITERIA_GLOB + "/cda:text/@value", node);
		extractNegation(node);
		extractSpecificOrSource(node);
		extractEffectiveTime(node);
		extractTemportalReferences(node);
		extractDerivationOperator(node);
		extractFieldValues(node);
		extractSubsetOperators(node);
		extractChildCriteria(node);
		// @id_xpath = './*/cda:id/@extension'
		setCodeListXpath("./*/cda:code");
		// @value_xpath = './*/cda:value'
		// @comments = @entry.xpath("./#{CRITERIA_GLOB}/cda:text/cda:xml/cda:qdmUserComments/cda:item/text()", HQMF2::Document::NAMESPACES).map{ |v| v.content }
		variable = false;
		
		// # Try to determine what kind of data criteria we are dealing with
	    // # First we look for a template id and if we find one just use the definition
	    // # status and negation associated with that
		if (extractTypeFromTemplateId(node) == false) {
			// # If no template id or not one we recognize then try to determine type from
	        // # the definition element
	        extractTypeFromDefinition(node);
		}
		
		patchXpathsForCriteriaType(node);
		
		title = determineTitle(node);
		codeListId = XPathUtil.evalOrNull(codeListXpath + "/@valueSet", node);
		inlineCodeList = determineInlineCodeList(node);
		
		determineSomeFieldsFromSettings();
	}
	
	private void determineSomeFieldsFromSettings() {
		String normalizedStatus = normalizeStatus();
		JsonObject settings = HQMFJsonUtil.getSettingsForDefinition(definition, normalizedStatus);
		if (settings == null) {
			throw new IllegalStateException("settings not found for " + definition + "_" + normalizedStatus);
		}
		type = settings.get("category").getAsString();
		setPatientApiFunction(settings.get("patient_api_function").getAsString());
		status = normalizedStatus;
		if (settings.has("property")) {
			property = settings.get("property").getAsString();
		}
	}

	private String normalizeStatus() {
		if (status == null) {
			return null;
		}
		switch (status.toLowerCase()) {
			case "completed":
			case "complete":
				return "diagnosis".equals(definition)? "active" : "performed";
			case "order":
				return "ordered";
		}
		return status.toLowerCase();
	}

	private void extractChildCriteria(Node node) {
		NodeList childNodes = XPathUtil.evalToNodeList("./*/cda:outboundRelationship[@typeCode='COMP']/cda:criteriaReference/cda:id", node);
		if (childNodes.getLength() == 0) {
			return;
		}
		childrenCriteria = new ArrayList<String>();
		for (int i = 0; i < childNodes.getLength(); i++) {	
			childrenCriteria.add(HQMFReferenceUtil.getReferenceId(childNodes.item(i)));
		}
		
	}

	private void extractFieldValues(Node node) {
		Map<String, Object> fields = new HashMap<String, Object>();
		NodeList fieldNodes = XPathUtil.evalToNodeList("./*/cda:outboundRelationship[*/cda:code]", node);
		for (int i = 0; i < fieldNodes.getLength(); i++) {	
			Node fieldNode = fieldNodes.item(i);
			String code = XPathUtil.evalOrNull("./*/cda:code/@code", fieldNode);
			String codeId = VALUE_FIELDS.get(code);
			Object val = parseValue(fieldNode, "./*/cda:value");
			if (val != null && codeId != null) {
				fields.put(codeId, val);
			}
		}
		// # special case for facility location which uses a very different structure
		fieldNodes = XPathUtil.evalToNodeList("./*/cda:outboundRelationship[*/cda:participation]", node);
		for (int i = 0; i < fieldNodes.getLength(); i++) {	
			Node fieldNode = fieldNodes.item(i);
			String code = XPathUtil.evalOrNull("./*/cda:participation/cda:role/@classCode", fieldNode);
			String codeId = VALUE_FIELDS.get(code);
			Object val = new HQMFCoded(XPathUtil.evalToNode("./*/cda:participation/cda:role/cda:code", fieldNode));
			if (val != null && codeId != null) {
				fields.put(codeId, val);
			}
		}
		if (!fields.isEmpty() ) {
			setFieldValues(fields);
		}
	}
	
	private void extractSubsetOperators(Node node) {
		NodeList excerptNodes = XPathUtil.evalToNodeList("./*/cda:excerpt", node);
		if (excerptNodes.getLength() == 0) {
			return;
		}
		List<HQMFSubsetOperator> result = new ArrayList<HQMFSubsetOperator>();
		for (int i = 0; i < excerptNodes.getLength(); i++) {	
			result.add(new HQMFSubsetOperator(excerptNodes.item(i)));
		}
		for (Iterator<HQMFSubsetOperator> it = result.iterator(); it.hasNext();) {
			HQMFSubsetOperator operator = it.next();
			if ("UNION".equals(operator.getType()) || "XPRODUCT".equals(operator.getType())) {
				it.remove();
			}
		}
		if (!result.isEmpty()) {
			subsetOperators = result;
		}
	}

	private void extractTemportalReferences(Node node) {
		NodeList temporalRefNodes = XPathUtil.evalToNodeList("./*/cda:temporallyRelatedInformation", node);
		
		if (temporalRefNodes.getLength() > 0) {
			temporalReferences = new ArrayList<HQMFTemporalReference>();
		}
		for (int i = 0; i < temporalRefNodes.getLength(); i++) {
			temporalReferences.add(new HQMFTemporalReference(temporalRefNodes.item(i)));
		}
	}

	private Map<String, List<String>> determineInlineCodeList(Node node) {
		String codeSystem = XPathUtil.evalOrNull(codeListXpath + "/@codeSystem", node);
		String codeSystemName = null;
		if (codeSystem != null) {
			codeSystemName = CodeSystemHelper.codeSystemFor(codeSystem);
		} else {
			codeSystemName = XPathUtil.evalOrNull(codeListXpath + "/@codeSystemName", node);
		}
		String codeValue = XPathUtil.evalOrNull(codeListXpath + "/@code", node);
		
		Map<String, List<String>> result = null;
		if (codeSystemName != null && codeValue != null) {
			result = new HashMap<String, List<String>>();
			ArrayList<String> codeList = new ArrayList<String>();
			codeList.add(codeValue);
			result.put(codeSystemName, codeList);
		}
		return result;
	}

	private static final Pattern TITLE_REGEX = Pattern.compile(".*:\\s+(.+)");
	
	private String determineTitle(Node node) {
		String dispValue = XPathUtil.evalOrNull(codeListXpath + "/cda:displayName/@value", node);
		if (dispValue != null) {
			return dispValue;
		}
		if (StringUtils.contains(description, ":")) {
			// match(/.*:\s+(.+)/)[1]
			Matcher matcher = TITLE_REGEX.matcher(description);
			matcher.find();
			return matcher.group(1);
		}
		return id;
	}

	private void patchXpathsForCriteriaType(Node node) {
		//System.out.println("d="+definition);
		switch (definition) {
			case "transfer_to":
			case "transfer_from":
				codeListXpath = "./cda:observationCriteria/cda:value";
				break;
			case "diagnosis":
			case "diagnosis_family_history":
				codeListXpath = "./cda:observationCriteria/cda:value";
				break;
			case "physical_exam":
			case "risk_category_assessment":
			case "procedure_result": 
			case "laboratory_test": 
			case "diagnostic_study_result": 
			case "functional_status_result": 
			case "intervention_result":
				value = extractValue(node);
				break;
			case "medication":
	        	switch (status) {
			        case "dispensed": 
			        case "ordered":
			          codeListXpath = "./cda:supplyCriteria/cda:participation/cda:role/cda:code";
			          break;
			        default: //# active or administered
			          codeListXpath = "./cda:substanceAdministrationCriteria/cda:participation/cda:role/cda:code";
	        	}
	        	break;
			case "patient_characteristic": 
			case "patient_characteristic_birthdate": 
			case "patient_characteristic_clinical_trial_participant": 
			case "patient_characteristic_expired": 
			case "patient_characteristic_gender": 
			case "patient_characteristic_age": 
			case "patient_characteristic_languages": 
			case "patient_characteristic_marital_status": 
			case "patient_characteristic_race":
				value = extractValue(node);
				break;
			case "variable":
	      		value = extractValue(node);
				break;
		}
		
	}

	private Object extractValue(Node node) {
		return parseValue(node, "./*/cda:value");
	}
	
	private Object parseValue(Node node, String xpathExpr) {
		//System.out.println("in parseValue, node="+node);
		Node valueDefNode = XPathUtil.evalToNode(xpathExpr, node);
		//System.out.println("valueDefNode="+valueDefNode);
		if (valueDefNode != null) {
			String valueType = XPathUtil.eval("@xsi:type", valueDefNode);
			if (valueType != null) {
				//System.out.println("valueType="+valueType);
				switch (valueType) {
					case "PQ":
						return new HQMFValue(valueDefNode, "PQ", true);
					case "TS":
						return new HQMFValue(valueDefNode);
					case "IVL_PQ":
					case "IVL_INT":
						return new HQMFRange(valueDefNode);
					case "CD":
						return new HQMFCoded(valueDefNode);
					case "ANY":
						return new HQMFAnyValue(valueDefNode);
					default:
						throw new IllegalArgumentException("Unknown value type " + valueType);
				}
			}
		}
		return null;
	}

	private void extractTypeFromDefinition(Node node) {
		if (XPathUtil.evalToNode("./cda:grouperCriteria", node) != null) {
			String localVariable = XPathUtil.evalOrNull("./cda:localVariableName/@value", node);
			if (localVariable != null && localVariable.contains("qdm_")) {
				variable = true;
			}
			definition = "derived";
			return;
		}
		// # See if we can find a match for the entry definition value and status.
		String entryType = XPathUtil.evalOrNull("./*/cda:definition/*/cda:id/@extension", node);
		JsonObject settingsForDef = HQMFJsonUtil.getSettingsForDefinition(entryType, status);
		if (settingsForDef != null) {
			definition = entryType;
		} else {
			// # if no exact match then try a string match just using entry definition value
			if (entryType == null) {
				definition = "variable";
			} else {
				switch (entryType) {
					case "Problem":
					case "Problems":
						definition = "diagnosis";
						break;
					case "Encounter":
					case "Encounters":
						definition = "encounter";
						break;
					case "LabResults":
					case "Results":
						definition = "laboratory_test";
						break;
					case "Procedure":
					case "Procedures":
						definition = "procedure";
						break;
					case "Medication":
					case "Medications":
						definition = "medication";
						if (status == null) {
							status = "active";
						}
						break;
					case "RX":
						definition = "medication";
						if (status == null) {
							status = "dispensed";
						}
						break;
					case "Demographics":
						definition = definitionForDemographic(node);
						break;
					case "Derived":
						definition = "derived";
						break;
					default:
						throw new IllegalArgumentException("Unknown data criteria template identifier " + entryType);
				}
			}
		}	
	}

	private String definitionForDemographic(Node node) {
		String demographicType = XPathUtil.evalOrNull("./cda:observationCriteria/cda:code/@code", node);
		switch (demographicType) {
			case "21112-8":
				return "patient_characteristic_birthdate";
			case "424144002":
				return "patient_characteristic_age";
			case "263495000":
				return "patient_characteristic_gender";
			case "102902016":
				return "patient_characteristic_languages";
			case "125680007":
				return "patient_characteristic_marital_status";
			case "103579009":
				return "patient_characteristic_race";
			default:
				throw new IllegalArgumentException("Unknown demographic identifier " + demographicType);
		}
	}

	private void extractDerivationOperator(Node node) {
		NodeList codeNodes = XPathUtil.evalToNodeList("./*/cda:outboundRelationship[@typeCode='COMP']/cda:conjunctionCode/@code", node);
		
		String prevCode = null;
		for (int i = 0; i < codeNodes.getLength(); i++) {
			String code = CONJUNCTION_CODE_TO_DERIVATION_OP.get(codeNodes.item(i).getNodeValue());
			if (prevCode != null && !code.equals(prevCode)) {
				throw new RuntimeException("More than one derivation operator in data criteria");
			}
			prevCode = code;
		}
		derivationOperator = prevCode;
		
	}

	private boolean extractTypeFromTemplateId(Node node) {
		List<String> templateIds = new ArrayList<String>();
		NodeList templateIdNodes = XPathUtil.evalToNodeList("./*/cda:templateId/cda:item", node);
		if (templateIdNodes != null) {
			for (int i = 0; i < templateIdNodes.getLength(); i++) {
				templateIds.add(XPathUtil.eval("@root", templateIdNodes.item(i)));
			}
		}
		if (templateIds.contains("2.16.840.1.113883.3.100.1.1")) { // SOURCE_DATA_CRITERIA_TEMPLATE_ID
			isSourceDataDriteria = true;
		}
		boolean found = false;
		for (String templateId : templateIds) {
			JsonObject defs = HQMFJsonUtil.getDefinitionForTemplate(templateId);
			if (defs != null) {
				definition = defs.get("definition").getAsString();
				status = defs.get("status").getAsString().length() > 0 ? defs.get("status").getAsString() : null;
				negation = defs.get("negation").getAsBoolean();
				found = true;
			} else if ("0.1.2.3.4.5.6.7.8.9.1".equals(templateId)) { // VARIABLE_TEMPLATE
				if ("XPRODUCT".equals(derivationOperator)) {
					derivationOperator = "INTERSECT";
				}
				if (definition == null) {
					definition = "derived";
				}
				negation = false;
				variable = true;
				found = true;
			} else if ("0.1.2.3.4.5.6.7.8.9.2".equals(templateId)) { // SATISFIES_ANY_TEMPLATE
				definition = "satisfies_any";
				negation = false;
				return true;
			} else if ("0.1.2.3.4.5.6.7.8.9.3".equals(templateId)) { // SATISFIES_ALL_TEMPLATE
				definition = "satisfies_all";
				derivationOperator = "INTERSECT";
				negation = false;
				found = true;
			}
		}
		return found;
	}

	private void extractEffectiveTime(Node node) {
		Node effectiveTimeNode = XPathUtil.evalToNode("./*/cda:effectiveTime", node);
		if (effectiveTimeNode != null) {
			effectiveTime = new HQMFEffectiveTime(effectiveTimeNode);
		}
		
	}

	private void extractNegation(Node node) {
		negation = XPathUtil.evalToBoolean("./*/@actionNegationInd", node);
		if (negation) {
			negationCodeListId = XPathUtil.evalOrNull("./*/cda:reasonCode/cda:item/@valueSet", node);
		}
	}

	private void extractSpecificOrSource(Node node) {
		Node specificDef = XPathUtil.evalToNode("./*/cda:outboundRelationship[@typeCode=\"OCCR\"]", node);
		Node sourceDef = XPathUtil.evalToNode("./*/cda:outboundRelationship[cda:subsetCode/@code=\"SOURCE\"]", node);
		if (specificDef != null) {
			sourceDataCriteria = XPathUtil.evalOrNull("./cda:criteriaReference/cda:id/@extension", specificDef);
			specificOccurrenceConst = XPathUtil.evalOrNull("./cda:localVariableName/@controlInformationRoot", specificDef);
			specificOccurrence = XPathUtil.evalOrNull("./cda:localVariableName/@controlInformationExtension", specificDef);
			if (specificOccurrence == null) {
				specificOccurrence = "A";
				specificOccurrenceConst = StringUtils.upperCase(specificOccurrenceConst);
			}
		} else if (sourceDef != null) {
			sourceDataCriteria = XPathUtil.evalOrNull("./cda:criteriaReference/cda:id/@extension", sourceDef);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSourceDataCriteria() {
		return sourceDataCriteria;
	}

	public void setSourceDataCriteria(String sourceDataCriteria) {
		this.sourceDataCriteria = sourceDataCriteria;
	}

	public String getSpecificOccurrenceConst() {
		return specificOccurrenceConst;
	}

	public void setSpecificOccurrenceConst(String specificOccurrenceConst) {
		this.specificOccurrenceConst = specificOccurrenceConst;
	}

	public String getSpecificOccurrence() {
		return specificOccurrence;
	}

	public void setSpecificOccurrence(String specificOccurrence) {
		this.specificOccurrence = specificOccurrence;
	}

	public boolean isNegation() {
		return negation;
	}

	public void setNegation(boolean negation) {
		this.negation = negation;
	}

	public String getNegationCodeListId() {
		return negationCodeListId;
	}

	public void setNegationCodeListId(String negationCodeListId) {
		this.negationCodeListId = negationCodeListId;
	}

	public HQMFEffectiveTime getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(HQMFEffectiveTime effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public List<HQMFTemporalReference> getTemporalReferences() {
		return temporalReferences;
	}

	public void setTemporalReferences(List<HQMFTemporalReference> temporalReferences) {
		this.temporalReferences = temporalReferences;
	}

	public String getDerivationOperator() {
		return derivationOperator;
	}

	public void setDerivationOperator(String derivationOperator) {
		this.derivationOperator = derivationOperator;
	}

	public boolean isVariable() {
		return variable;
	}

	public void setVariable(boolean variable) {
		this.variable = variable;
	}

	public boolean isSourceDataDriteria() {
		return isSourceDataDriteria;
	}

	public void setSourceDataDriteria(boolean isSourceDataDriteria) {
		this.isSourceDataDriteria = isSourceDataDriteria;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getCodeListXpath() {
		return codeListXpath;
	}

	public void setCodeListXpath(String codeListXpath) {
		this.codeListXpath = codeListXpath;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getCodeListId() {
		return codeListId;
	}

	public void setCodeListId(String codeListId) {
		this.codeListId = codeListId;
	}

	public String getType() {
		return type;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPatientApiFunction() {
		return patientApiFunction;
	}

	public void setPatientApiFunction(String patientApiFunction) {
		this.patientApiFunction = patientApiFunction;
	}

	public Map<String, List<String>> getInlineCodeList() {
		return inlineCodeList;
	}

	public void setInlineCodeList(Map<String, List<String>> inlineCodeList) {
		this.inlineCodeList = inlineCodeList;
	}

	public Map<String, Object> getFieldValues() {
		return fieldValues;
	}

	public void setFieldValues(Map<String, Object> fieldValues) {
		this.fieldValues = fieldValues;
	}

	public List<HQMFSubsetOperator> getSubsetOperators() {
		return subsetOperators;
	}

	public void setSubsetOperators(List<HQMFSubsetOperator> subsetOperators) {
		this.subsetOperators = subsetOperators;
	}
	
	public boolean hasSubsetOperators() {
		return this.subsetOperators != null && this.subsetOperators.size() > 0;
	}

	public List<String> getChildrenCriteria() {
		return childrenCriteria;
	}
	
	public void setChildrenCriteria(List<String> childrenCriteria) {
		this.childrenCriteria = childrenCriteria;
	}

}
