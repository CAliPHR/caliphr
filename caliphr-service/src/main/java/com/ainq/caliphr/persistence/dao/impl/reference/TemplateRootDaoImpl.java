package com.ainq.caliphr.persistence.dao.impl.reference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ainq.caliphr.persistence.dao.reference.TemplateRootDao;
import com.ainq.caliphr.persistence.dao.reference.TemplateRootNewXactionDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.TemplateRootRepository;
import com.ainq.caliphr.persistence.util.predicate.ccda.TemplateRootPredicate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

/**
 * Created by mmelusky on 11/10/2015.
 */
@Repository
public class TemplateRootDaoImpl implements TemplateRootDao {

	@PersistenceContext
	private EntityManager entityManager;

    @Autowired
    private TemplateRootRepository templateRootRepository;

	@Autowired
	private TemplateRootNewXactionDao templateRootNewXactionDao;
	
	private final static StampedLock lock = new StampedLock();
    
    private static final Map<String, Integer> templateCache = Collections.synchronizedMap(new HashMap<>());

    @Transactional
    @Override
    public TemplateRoot findOrCreateTemplate(String root) {

		Integer templateRootId = templateCache.get(root);
		if (templateRootId != null) {
			return templateRootRepository.getOne(templateRootId);
		}

		// first query using the current transaction.  If no record is found, call a method to create one outside a transaction
		// and merge it back into the current persistence context
		TemplateRoot foundTemplateRoot = templateRootRepository.findOne(TemplateRootPredicate.findTemplateByRoot(root));
		if (foundTemplateRoot == null) {
			Long stamp = lock.writeLock();
			try {
				foundTemplateRoot = templateRootNewXactionDao.findOrCreateTemplateNewTransaction(root);
				foundTemplateRoot = entityManager.merge(foundTemplateRoot);
			} finally {
	            lock.unlock(stamp);
	        }
		}
		templateCache.put(root, foundTemplateRoot.getId());
		return foundTemplateRoot;
    }
}
