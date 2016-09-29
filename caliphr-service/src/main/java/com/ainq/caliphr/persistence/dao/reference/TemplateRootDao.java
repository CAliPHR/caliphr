package com.ainq.caliphr.persistence.dao.reference;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;

/**
 * Created by mmelusky on 11/10/2015.
 */
public interface TemplateRootDao {
    TemplateRoot findOrCreateTemplate(String root);
}
