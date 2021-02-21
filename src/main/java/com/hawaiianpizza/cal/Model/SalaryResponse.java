package com.hawaiianpizza.cal.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalaryResponse {
	int gukmin;
	int gungang;
	int janggi;
	int employ;
	int incomeTax;
	int loaclIncomeTax;
	int total;
	int income;
}
