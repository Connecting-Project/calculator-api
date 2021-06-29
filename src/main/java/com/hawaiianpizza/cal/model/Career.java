package com.hawaiianpizza.cal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Career {
	int id;
	int startYear;
	int startMonth;
	int endYear;
	int endMonth;
	int now;
}
