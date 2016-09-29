package com.ainq.caliphr.hqmf.service.transformation;

import java.util.Date;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.hqmf.model.DataCriteria;
import com.ainq.caliphr.hqmf.model.HQMFDocument;
import com.ainq.caliphr.hqmf.model.PopulationCriteria;
import com.ainq.caliphr.hqmf.model.type.HQMFAttribute;
import com.ainq.caliphr.hqmf.model.type.HQMF_ED;
import com.ainq.caliphr.hqmf.util.MeasureMetadataUtil;
import com.ainq.caliphr.persistence.dao.MeasureDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.ProviderRepository;
import com.google.gson.Gson;

@Component
public class HQMFTransformer {

    @Autowired
    private MeasureMetadataUtil measureMetadataUtil;

    @Autowired
    private MeasureDao measureDao;

    @Autowired
    ProviderRepository providerRepository;
    
    @PersistenceContext 
	private EntityManager entityManager;

    @Transactional
    public HqmfDocument transformAndSave(HQMFDocument srcDoc, Integer providerId, Integer userId, Integer bundleId) {
        HqmfDocument destDoc = new HqmfDocument();
        destDoc.setBundle(measureDao.getBundleById(bundleId));

        Gson gson = new Gson();
        Date now = new Date();

        if (srcDoc.getId() != null) {
            destDoc.setId(Long.parseLong(srcDoc.getId()));
        }
        if (providerId != null) {
            destDoc.setProvider(providerRepository.getOne(providerId));
        }
        destDoc.setUserId(userId);
        destDoc.setTitle(srcDoc.getTitle());
        destDoc.setDescription(srcDoc.getDescription());
        destDoc.setCmsId(srcDoc.getCmsId());
        destDoc.setHqmfId(srcDoc.getHqmfId());
        destDoc.setHqmfSetId(srcDoc.getHqmfSetId());
        destDoc.setHqmfVersionNumber(Integer.parseInt(srcDoc.getHqmfVersionNumber()));

        HqmfMeasurePeriod hqmfMeasurePeriod = new HqmfMeasurePeriod();
        hqmfMeasurePeriod.setHigh(srcDoc.getMeasurePeriod().getHigh().getValue());
        hqmfMeasurePeriod.setLow(srcDoc.getMeasurePeriod().getLow().getValue());
        hqmfMeasurePeriod.setWidth(srcDoc.getMeasurePeriod().getWidth().getValue());
        hqmfMeasurePeriod.setDateCreated(now);
        hqmfMeasurePeriod.setDateUpdated(now);
        hqmfMeasurePeriod.setUserCreated(userId);
        hqmfMeasurePeriod.setUserUpdated(userId);
        destDoc.setMeasurePeriod(hqmfMeasurePeriod);

        for (HQMFAttribute srcHQMFAttribute : srcDoc.getAttributes()) {
            HqmfAttribute hqmfAttr = new HqmfAttribute();
            if (srcHQMFAttribute.getId() != null) {
                hqmfAttr.setId(Long.parseLong(srcHQMFAttribute.getId()));
            }
            hqmfAttr.setCode(srcHQMFAttribute.getCode());
            hqmfAttr.setAttributeName(srcHQMFAttribute.getName());
            hqmfAttr.setCodeObjJson(gson.toJson(srcHQMFAttribute.getCodeObj()));
            
            // necessary hard-coded manipulation for CMS68v4: replace a succession of 4 carriage returns with just 2, 
            // as the guidance text specified in the HQMF file results in too much space between paragraphs
            if ("CMS68v4".equals(srcDoc.getCmsId()) && "Guidance".equals(srcHQMFAttribute.getName())) {
            	HQMF_ED valueObj =  (HQMF_ED)srcHQMFAttribute.getValueObj();
            	valueObj.setValue(valueObj.getValue().replace("\\n\\n\\n\\n", "\\n\\n"));
            }
            
            hqmfAttr.setValueObjJson(gson.toJson(srcHQMFAttribute.getValueObj()));
            hqmfAttr.setDateCreated(now);
            hqmfAttr.setDateUpdated(now);
            hqmfAttr.setUserCreated(userId);
            hqmfAttr.setUserUpdated(userId);
            destDoc.addHqmfAttribute(hqmfAttr);
        }

        for (DataCriteria srcDataCriteria : srcDoc.getDataCriteria().values()) {
            HqmfDataCriteria hqmfDC = new HqmfDataCriteria();

            hqmfDC.setHqmfId(srcDataCriteria.getId());
            hqmfDC.setTitle(srcDataCriteria.getTitle());
            hqmfDC.setDescription(srcDataCriteria.getDescription());
            hqmfDC.setCodeListId(srcDataCriteria.getCodeListId());
            hqmfDC.setDataCriteriaJson(gson.toJson(srcDataCriteria));

            hqmfDC.setDateCreated(now);
            hqmfDC.setDateUpdated(now);
            hqmfDC.setUserCreated(userId);
            hqmfDC.setUserUpdated(userId);
            destDoc.addHqmfDataCriteria(hqmfDC);
        }

        for (DataCriteria srcDataCriteria : srcDoc.getSourceDataCriteria().values()) {
        	HqmfDataCriteria hqmfDC = new HqmfDataCriteria();

        	hqmfDC.setHqmfId(srcDataCriteria.getId());
            hqmfDC.setTitle(srcDataCriteria.getTitle());
            hqmfDC.setDescription(srcDataCriteria.getDescription());
            hqmfDC.setCodeListId(srcDataCriteria.getCodeListId());
            hqmfDC.setDataCriteriaJson(gson.toJson(srcDataCriteria));
            hqmfDC.setDateCreated(now);
            hqmfDC.setDateUpdated(now);
            hqmfDC.setUserCreated(userId);
            hqmfDC.setUserUpdated(userId);
            destDoc.addHqmfDataCriteria(hqmfDC);
        }

        for (PopulationCriteria srcPopulationCriteria : srcDoc.getPopulationCriteria()) {

            HqmfPopulation hqmfPop = new HqmfPopulation();
            hqmfPop.setHqmfId(srcPopulationCriteria.getHqmfId());
            hqmfPop.setTitle(srcPopulationCriteria.getTitle());
            hqmfPop.setPopulationType(srcPopulationCriteria.getType());
            hqmfPop.setHqmfPopulationId(srcPopulationCriteria.getId());
            hqmfPop.setPopulationJson(gson.toJson(srcPopulationCriteria));
            
            hqmfPop.setDateCreated(now);
            hqmfPop.setDateUpdated(now);
            hqmfPop.setUserCreated(userId);
            hqmfPop.setUserUpdated(userId);
            destDoc.addHqmfPopulation(hqmfPop);
        }

        for (int i = 0; i < srcDoc.getPopulations().size(); i++) {
            Map<String, String> srcPopulationsData = srcDoc.getPopulations().get(i);
            final int index = i;
            srcPopulationsData.forEach((k, v) -> {
                HqmfPopulationSet hqmfPopSet = new HqmfPopulationSet();
                hqmfPopSet.setIndex(index);
                hqmfPopSet.setKey(k);
                hqmfPopSet.setValue(v);
                hqmfPopSet.setDateCreated(now);
                hqmfPopSet.setDateUpdated(now);
                hqmfPopSet.setUserCreated(userId);
                hqmfPopSet.setUserUpdated(userId);
                destDoc.addHqmfPopulationSet(hqmfPopSet);
            });
        }

        Domain domain = measureMetadataUtil.getDomainByCmsId(srcDoc.getCmsId());
        if (domain != null) {
        	
        	// merge the domain entity into the current transaction context
        	domain = entityManager.merge(domain);
        	
            destDoc.setDomain(domain);
        }

        destDoc.setDateCreated(now);
        destDoc.setDateUpdated(now);
        destDoc.setUserCreated(userId);
        destDoc.setUserUpdated(userId);

        measureDao.saveHqmfDocument(destDoc);

        return destDoc;

    }

}
