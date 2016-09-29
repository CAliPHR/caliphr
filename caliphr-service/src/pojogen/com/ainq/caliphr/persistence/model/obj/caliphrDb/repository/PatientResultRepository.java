package com.ainq.caliphr.persistence.model.obj.caliphrDb.repository;

import  org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientResult;

/** 
 * Spring Data Repository for table: patient_result.
 * @author autogenerated/custom
 */ 
public interface PatientResultRepository extends JpaRepository<PatientResult, Integer>, QueryDslPredicateExecutor<PatientResult> {

	// Add any extra methods here. This file will not get overwritten unlike any other generated file
}