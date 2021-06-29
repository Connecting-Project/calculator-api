package com.hawaiianpizza.cal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Salary {
	boolean year;
	int nonTaxableAmount;
	int dependentNum;
	int childNum;
	int grossSalary;
}
