package com.ainq.caliphr.persistence.transformation.cda;

import org.hl7.v3.*;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;

/**
 * Created by mmelusky on 5/17/2015.
 */
public interface ProviderImporter {

    Provider findProviderByGroupAndNPI(Object performer, PracticeGroup practiceGroup);

    Provider loadProviderInfo(Object performer, PracticeGroup practiceGroup);

    Provider createOrUpdateProviderFromAssignedEntity(POCDMT000040AssignedEntity assignedEntity, PracticeGroup practiceGroup, String npi);
}
