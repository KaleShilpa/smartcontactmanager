package com.javaexpress.smt.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.javaexpress.smt.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
	@Query("select c from Contact c where c.user.id = :userId")
	public Page<Contact> getContactsByUser(@Param("userId") int userId, Pageable pageable);
	

}
