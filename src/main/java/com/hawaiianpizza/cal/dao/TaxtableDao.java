package com.hawaiianpizza.cal.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hawaiianpizza.cal.model.TaxTable;
@Repository
public interface TaxtableDao extends JpaRepository<TaxTable,Object>{
	public TaxTable findByNum(int num);
}
