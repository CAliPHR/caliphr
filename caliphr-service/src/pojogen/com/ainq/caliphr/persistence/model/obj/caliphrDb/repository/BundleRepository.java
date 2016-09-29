package com.ainq.caliphr.persistence.model.obj.caliphrDb.repository;

import  org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.Bundle;

/** 
 * Spring Data Repository for table: bundle.
 * @author autogenerated/custom
 */ 
public interface BundleRepository extends JpaRepository<Bundle, Integer>, QueryDslPredicateExecutor<Bundle> {

	// Add any extra methods here. This file will not get overwritten unlike any other generated file
}