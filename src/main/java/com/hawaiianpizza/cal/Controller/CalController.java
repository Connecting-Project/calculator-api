package com.hawaiianpizza.cal.Controller;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hawaiianpizza.cal.Model.Career;
import com.hawaiianpizza.cal.Model.Salary;
import com.hawaiianpizza.cal.Model.SalaryResponse;
import com.hawaiianpizza.cal.Model.Standard;
import com.hawaiianpizza.cal.Service.CalService;

import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/cal")
public class CalController {

	@Autowired
	private CalService calService;
	@PostMapping(value = "/standard")
	public ResponseEntity<?> standard(@RequestBody Standard standard) {
		System.out.println("standard Controller");
		try {
			double ret = calService.standard(standard.getFir_num(), standard.getSec_num(), standard.getOperator());
			
			return new ResponseEntity<>(ret, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	@ApiOperation("start 입력 형식 : yyyy-MM-dd , type에는 군복무 일수를 입력.")
	@PostMapping(value = "/campaign")
	public ResponseEntity<?> campaign(@RequestParam String start, @RequestParam int type) {
		System.out.println("campaign Controller");
		try {
			HashMap<String, Object> ret = new HashMap<String, Object>();
			start+=" 00:00:00";
			Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(start);
			long time = date.getTime()+((long)type*24*60*60*1000);
			Date next = new Date(time);
			

			long day_cnt = calService.campaign_day(date, type);
			double per_cnt = (double)day_cnt/type;
			
			
			day_cnt = type-day_cnt;
			if(day_cnt<0)
				day_cnt=0;
			if(per_cnt>=1)
				per_cnt = 1;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			ret.put("day", day_cnt);
			ret.put("percent", per_cnt*100);
			ret.put("campaign", format.format(next));
			System.out.println(date);
			System.out.println(next);
			System.out.println(ret);
			return new ResponseEntity<>(ret, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@PostMapping(value = "/career")
	public ResponseEntity<?> career(@RequestBody Career[] career) {
		System.out.println("career Controller");
		try {
			HashMap<String, Object> ret = new HashMap<String, Object>();
			
			// 유효성검사
			if(calService.validity(career)) {
				return new ResponseEntity<>("유효성 검사 오류",HttpStatus.METHOD_NOT_ALLOWED);
			}
			
			int m = calService.career(career);
			ret.put("year", m/12);
			ret.put("month", m%12);
			return new ResponseEntity<>(ret, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/salary")
	public ResponseEntity<?> salary(@RequestBody Salary salary) {
		System.out.println("salary Controller");
		try {
			HashMap<String, Object> ret = new HashMap<String, Object>();
			SalaryResponse m = calService.salary(salary);
			System.out.println(m);
			return new ResponseEntity<>(m, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}


}
