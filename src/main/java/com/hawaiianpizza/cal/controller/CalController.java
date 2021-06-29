package com.hawaiianpizza.cal.controller;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hawaiianpizza.cal.model.Career;
import com.hawaiianpizza.cal.model.Salary;
import com.hawaiianpizza.cal.model.SalaryResponse;
import com.hawaiianpizza.cal.model.Standard;
import com.hawaiianpizza.cal.service.CalService;

import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/cal")
public class CalController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CalService calService;

	@PostMapping(value = "/standard")
	public ResponseEntity<HashMap<String,Object>> standard(@RequestBody Standard standard) {
		logger.info("standard Controller");
		HashMap<String,Object> ret = new HashMap<>();
		try {
			double result = calService.standard(standard.getFirNum(), standard.getSecNum(), standard.getOperator());
			ret.put("result",result);
			return new ResponseEntity<>(ret, HttpStatus.OK);
		} catch (Exception e) {
			ret.put("error",e);
			return new ResponseEntity<>(ret, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	@ApiOperation("start 입력 형식 : yyyy-MM-dd , type에는 군복무 일수를 입력.")
	@PostMapping(value = "/campaign")
	public ResponseEntity<HashMap<String,Object>> campaign(@RequestParam String start, @RequestParam int type) {
		logger.info("campaign Controller");
		HashMap<String, Object> ret = new HashMap<>();
		try {

			start+=" 00:00:00";
			Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(start);
			long time = date.getTime()+((long)type*24*60*60*1000);
			Date next = new Date(time);
			

			long dayCnt = calService.campaignDay(date, type);
			double perCnt = (double)dayCnt/type;

			dayCnt = type-dayCnt;
			if(dayCnt<0)
				dayCnt=0;
			if(perCnt>=1)
				perCnt = 1;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			ret.put("day", dayCnt);
			ret.put("percent", perCnt*100);
			ret.put("campaign", format.format(next));
			return new ResponseEntity<>(ret, HttpStatus.OK);
		} catch (Exception e) {
			ret.put("error",e);
			return new ResponseEntity<>(ret, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@PostMapping(value = "/career")
	public ResponseEntity<HashMap<String, Object>> career(@RequestBody Career[] career) {
		logger.info("career Controller");
		HashMap<String, Object> ret = new HashMap<>();
		try {
			// 유효성검사
			if(calService.validity(career)) {
				logger.error("유효성 검사 오류");
				ret.put("유효성 검사 오류","");
				return new ResponseEntity<>(ret,HttpStatus.METHOD_NOT_ALLOWED);
			}
			
			int m = calService.career(career);
			ret.put("year", m/12);
			ret.put("month", m%12);
			return new ResponseEntity<>(ret, HttpStatus.OK);
		} catch (Exception e) {
			ret.put("error",e);
			return new ResponseEntity<>(ret, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/salary")
	public ResponseEntity<HashMap<String, Object>> salary(@RequestBody Salary salary) {
		logger.info("salary Controller");
		HashMap<String, Object> ret = new HashMap<>();
		try {

			SalaryResponse m = calService.salary(salary);
			ret.put("salary",m);
			String response = String.valueOf(m);
			logger.info(response);
			return new ResponseEntity<>(ret, HttpStatus.OK);
		} catch (Exception e) {
			ret.put("error",e);
			return new ResponseEntity<>(ret, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}


}
