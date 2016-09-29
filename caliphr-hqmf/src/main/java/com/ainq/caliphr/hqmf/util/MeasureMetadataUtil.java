package com.ainq.caliphr.hqmf.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import com.ainq.caliphr.hqmf.Constants;
import com.ainq.caliphr.persistence.dao.ValueSetDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Domain;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.QDomain;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.DomainRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

@Component
public class MeasureMetadataUtil {
	
	@Autowired
	private DomainRepository domainRepository;
	
	private String ACTIVE_BUNDLE_ROOT;
	private String EP_PATH;
	
	@Autowired
    private Environment environment;
	
    @Autowired
    private ValueSetDao valueSetDao;
	
	private static Map<String, JsonObject> measureMetatdataMap = new HashMap<>();
	
	// temporarily hard code what domain measures belong to
	// TODO: perhaps move to property file
	private static Map<String, String> domainProps = new HashMap<>();
	static {
		domainProps.put("CMS117v3", "Community, Population and Public Health");
		domainProps.put("CMS122v3", "Effective Clinical Care");
		domainProps.put("CMS123v3", "Effective Clinical Care");
		domainProps.put("CMS124v3", "Effective Clinical Care");
		domainProps.put("CMS125v3", "Effective Clinical Care");
		domainProps.put("CMS126v3", "Effective Clinical Care");
		domainProps.put("CMS127v3", "Effective Clinical Care");
		domainProps.put("CMS128v3", "Effective Clinical Care");
		domainProps.put("CMS129v4", "Efficiency and Cost Reduction Use of Healthcare Resources");
		domainProps.put("CMS130v3", "Effective Clinical Care");
		domainProps.put("CMS131v3", "Effective Clinical Care");
		domainProps.put("CMS132v3", "Patient Safety");
		domainProps.put("CMS133v3", "Effective Clinical Care");
		domainProps.put("CMS134v3", "Effective Clinical Care");
		domainProps.put("CMS135v3", "Effective Clinical Care");
		domainProps.put("CMS136v4", "Effective Clinical Care");
		domainProps.put("CMS137v3", "Effective Clinical Care");
		domainProps.put("CMS138v3", "Community, Population and Public Health");
		domainProps.put("CMS139v3", "Patient Safety");
		domainProps.put("CMS140v3", "Effective Clinical Care");
		domainProps.put("CMS141v4", "Effective Clinical Care");
		domainProps.put("CMS142v3", "Effective Clinical Care");
		domainProps.put("CMS143v3", "Effective Clinical Care");
		domainProps.put("CMS144v3", "Effective Clinical Care");
		domainProps.put("CMS145v3", "Effective Clinical Care");
		domainProps.put("CMS146v3", "Efficiency and Cost Reduction Use of Healthcare Resources");
		domainProps.put("CMS147v4", "Community, Population and Public Health");
		domainProps.put("CMS148v3", "Effective Clinical Care");
		domainProps.put("CMS149v3", "Effective Clinical Care");
		domainProps.put("CMS153v3", "Community, Population and Public Health");
		domainProps.put("CMS154v3", "Efficiency and Cost Reduction Use of Healthcare Resources");
		domainProps.put("CMS155v3", "Community, Population and Public Health");
		domainProps.put("CMS156v3", "Patient Safety");
		domainProps.put("CMS157v3", "Person and Caregiver-Centered Experience Outcomes");
		domainProps.put("CMS158v3", "Effective Clinical Care");
		domainProps.put("CMS159v3", "Effective Clinical Care");
		domainProps.put("CMS160v3", "Effective Clinical Care");
		domainProps.put("CMS161v3", "Effective Clinical Care");
		domainProps.put("CMS163v3", "Effective Clinical Care");
		domainProps.put("CMS164v3", "Effective Clinical Care");
		domainProps.put("CMS165v3", "Effective Clinical Care");
		domainProps.put("CMS166v4", "Efficiency and Cost Reduction Use of Healthcare Resources");
		domainProps.put("CMS167v3", "Effective Clinical Care");
		domainProps.put("CMS169v3", "Effective Clinical Care");
		domainProps.put("CMS177v3", "Patient Safety");
		domainProps.put("CMS179v3", "Patient Safety");
		domainProps.put("CMS182v4", "Effective Clinical Care");
		domainProps.put("CMS22v3", "Community, Population and Public Health");
		domainProps.put("CMS2v4", "Community, Population and Public Health");
		domainProps.put("CMS50v3", "Communication and Care Coordination");
		domainProps.put("CMS52v3", "Effective Clinical Care");
		domainProps.put("CMS56v3", "Person and Caregiver-Centered Experience Outcomes");
		domainProps.put("CMS61v4", "Effective Clinical Care");
		domainProps.put("CMS62v3", "Effective Clinical Care");
		domainProps.put("CMS64v4", "Effective Clinical Care");
		domainProps.put("CMS65v4", "Effective Clinical Care");
		domainProps.put("CMS66v3", "Person and Caregiver-Centered Experience Outcomes");
		domainProps.put("CMS68v4", "Patient Safety");
		domainProps.put("CMS69v3", "Community, Population and Public Health");
		domainProps.put("CMS74v4", "Effective Clinical Care");
		domainProps.put("CMS75v3", "Effective Clinical Care");
		domainProps.put("CMS77v3", "Effective Clinical Care");
		domainProps.put("CMS82v2", "Community, Population and Public Health");
		domainProps.put("CMS90v4", "Person and Caregiver-Centered Experience Outcomes");
	}
	
	@PostConstruct
	public void init() {
		ACTIVE_BUNDLE_ROOT = String.format("%s/%s"
                , environment.getProperty(Constants.PropertyKey.BUNDLE_FILESYSTEM_ROOT)
                , valueSetDao.getActiveBundle().getBundleVersion());
		EP_PATH = ACTIVE_BUNDLE_ROOT + "/sources/ep/";
	}
	
	public String getActiveBundleRoot() {
		return ACTIVE_BUNDLE_ROOT;
	}
	
	public String getActiveBundleEpPath() {
		return EP_PATH;
	}
	
	public Domain getDomainByCmsId(String cmsId) {
		String domainName = domainProps.get(cmsId);
		return (domainName != null ? getDomainByName(domainName) : null);
	}

	private Domain getDomainByName(String domainName) {
		return domainRepository.findOne(QDomain.domain.name.eq(domainName));
	}
	
	public boolean isEpisodeOfCareMeasure(String cmsId)  {
		return getMeasureMetadataJson(cmsId).get("episode_of_care").getAsBoolean();
	}
	
	public JsonArray getEpisodeIds(String cmsId) {
		return getMeasureMetadataJson(cmsId).get("episode_ids").getAsJsonArray();
	}
	
	public JsonObject getMeasureMetadataJson(String cmsId)  {
		JsonObject measureMetadataObj = measureMetatdataMap.get(cmsId);
		if (measureMetadataObj == null) {
			synchronized (MeasureMetadataUtil.class) {
				measureMetadataObj = measureMetatdataMap.get(cmsId);
				if (measureMetadataObj == null) {
					JsonParser jsonParser = new JsonParser();
					JsonReader jsonReader;
					try {
						jsonReader = new JsonReader(new InputStreamReader(
							new	FileSystemResource(getActiveBundleEpPath() + cmsId + "/measure.metadata").getInputStream()
						));
						measureMetadataObj = jsonParser.parse(jsonReader).getAsJsonObject();
					} catch (IOException e) {
						throw new IllegalStateException(e);
					}
					
					measureMetatdataMap.put(cmsId, measureMetadataObj);
				}
			}
		}
		return measureMetadataObj;
	}
	
}
