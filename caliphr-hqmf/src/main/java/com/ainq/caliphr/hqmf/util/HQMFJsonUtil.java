package com.ainq.caliphr.hqmf.util;

import java.io.InputStreamReader;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class HQMFJsonUtil {
	
	private static JsonObject hqmfTemplateJsonObj;
	private static JsonObject hqmfSettingsJsonObj;
	
	public static JsonObject getDefinitionForTemplate(String templateId) {
		return getHqmfTemplateJsonObj().getAsJsonObject(templateId);
	}
	
	public static JsonObject getHqmfTemplateJsonObj() {
		if (hqmfTemplateJsonObj == null) {
			synchronized (HQMFJsonUtil.class) {
				if (hqmfTemplateJsonObj == null) {
					JsonParser jsonParser = new JsonParser();
					JsonReader jsonReader = new JsonReader(new InputStreamReader(
							HQMFJsonUtil.class.getResourceAsStream(
									"/hqmf_template_oid_map.json")));
					hqmfTemplateJsonObj = jsonParser.parse(jsonReader)
														.getAsJsonObject();
				}
			}
		}
		return hqmfTemplateJsonObj;
	}
	
	public static JsonObject getSettingsForDefinition(String definition, String status) {
		String key = definition + (StringUtils.isEmpty(status) ? "" : "_" + status);
		JsonObject settings = getHqmfSettingsJsonObj().getAsJsonObject(key);
		if (settings == null || settings.get("not_supported").getAsBoolean() == true) {
			return null;
		}
		return settings;
	}
	
	public static JsonObject getHqmfSettingsJsonObj() {
		if (hqmfSettingsJsonObj == null) {
			synchronized (HQMFJsonUtil.class) {
				if (hqmfSettingsJsonObj == null) {
					JsonParser jsonParser = new JsonParser();
					JsonReader jsonReader = new JsonReader(new InputStreamReader(
							HQMFJsonUtil.class.getResourceAsStream(
									"/data_criteria.json")));
					hqmfSettingsJsonObj = jsonParser.parse(jsonReader)
														.getAsJsonObject();
				}
			}
		}
		return hqmfSettingsJsonObj;
	}
	
}
