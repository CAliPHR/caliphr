package com.ainq.caliphr.persistence.model.obj.caliphrDb.repository;

import  org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientAdvancedDirective;

/** 
 * Spring Data Repository for table: patient_advanced_directive.
 * @author autogenerated/custom
 */ 
public interface PatientAdvancedDirectiveRepository extends JpaRepository<PatientAdvancedDirective, Integer>, QueryDslPredicateExecutor<PatientAdvancedDirective> {

	// Add any extra methods here. This file will not get overwritten unlike any other generated file
}