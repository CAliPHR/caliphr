package com.ainq.caliphr.persistence.model.obj.caliphrDb.repository;

import  org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.ApplicationUserSecurity;

/** 
 * Spring Data Repository for table: application_user_security.
 * @author autogenerated/custom
 */ 
public interface ApplicationUserSecurityRepository extends JpaRepository<ApplicationUserSecurity, Integer>, QueryDslPredicateExecutor<ApplicationUserSecurity> {

	// Add any extra methods here. This file will not get overwritten unlike any other generated file
}
