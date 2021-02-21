package com.hawaiianpizza.cal.Model;

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
