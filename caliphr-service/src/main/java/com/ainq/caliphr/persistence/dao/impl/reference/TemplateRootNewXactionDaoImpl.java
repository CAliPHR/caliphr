package com.ainq.caliphr.persistence.dao.impl.reference;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.dao.reference.TemplateRootNewXactionDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.TemplateRootRepository;
import com.ainq.caliphr.persistence.util.predicate.ccda.TemplateRootPredicate;

/**
 * Created by mmelusky on 5/10/2016.
 */
@Repository
public class TemplateRootNewXactionDaoImpl implements TemplateRootNewXactionDao {

    @Autowired
    private TemplateRootRepository templateRootRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TemplateRoot findOrCreateTemplateNewTransaction(String root) {

    	// This method assumes the caller has already locked to prevent other threads from creating duplicate rows
    	
        // query again, in case another thread created a record while waiting for the lock
        TemplateRoot templateRoot = templateRootRepository.findOne(TemplateRootPredicate.findTemplateByRoot(root));
        if (templateRoot == null) {
            templateRoot = new TemplateRoot();
            templateRoot.setHl7Oid(root);
            templateRoot.setDateCreated(new Date());
            templateRoot.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            templateRoot.setDateUpdated(new Date());
            templateRoot.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            templateRootRepository.saveAndFlush(templateRoot);
        }
        

        return templateRoot;
    }

}
