package com.ainq.caliphr.persistence.model.obj.caliphrDb.repository;

import  org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroupType;

/** 
 * Spring Data Repository for table: practice_group_type.
 * @author autogenerated/custom
 */ 
public interface PracticeGroupTypeRepository extends JpaRepository<PracticeGroupType, Integer>, QueryDslPredicateExecutor<PracticeGroupType> {

	// Add any extra methods here. This file will not get overwritten unlike any other generated file
}
