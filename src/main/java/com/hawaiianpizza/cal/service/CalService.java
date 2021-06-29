package com.hawaiianpizza.cal.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hawaiianpizza.cal.model.Career;
import com.hawaiianpizza.cal.model.Salary;
import com.hawaiianpizza.cal.model.SalaryResponse;
import com.hawaiianpizza.cal.model.TaxTable;
import com.hawaiianpizza.cal.dao.taxtableDao;

@Service
public class CalService {

	@Autowired
	taxtableDao taxtabledao;

	public double standard(double fir, double sec, String oper) {
		switch (oper) {
		case "+":
			return fir + sec;
		case "-":
			return fir - sec;
		case "/":
			return fir / sec;
		case "*":
			return fir * sec;
		case "^2":
			return fir * fir;
		case "//":
			return Math.sqrt(fir);
		case "1/x":
			return 1 / fir;
		default:
			return 0.0;
		}
	};
	//

	public long campaign_day(Date start, int type) {
		Date now = new Date();
		long diffInMillies = Math.abs(now.getTime() - start.getTime());
		long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

		return diff;
	};

	public int career(Career[] career) {
		int ret = 0;
		for (int i = 0; i < career.length; i++) {
			ret += (career[i].getEndYear() - career[i].getStartYear()) * 12 + career[i].getEndMonth()
					- career[i].getStartMonth();
		}
		return ret;
	};

	public SalaryResponse salary(Salary salary) {
		SalaryResponse sal = new SalaryResponse();
		int money = salary.getSalary();
		if (salary.isYear()) {
			money /= 12;
		}
		money -= salary.getNonTaxableAmount();
		// 국민연금
		if (money >= 5030000) {
			sal.setGukmin((int) (503000 * 0.045));
		} else if (money <= 320000) {
			sal.setGukmin(0);
		} else
			sal.setGukmin((int) (money * 0.045));
		// 건강보험
		sal.setGungang((int) (money * 0.0306));
		if (sal.getGungang() > 3523950) {
			sal.setGungang(3523950);
		}
		if (sal.getGungang() < 14380) {
			sal.setGungang(14380);
		}
		sal.setJanggi((int) (sal.getGungang() * 0.0655));

		sal.setEmploy((int) (money * 0.0065));
		// 소득세
		int incomeTax = 0;
		if (money >= 1000000 && money < 10000000) {
			TaxTable t = new TaxTable();
			if (money <= 1500000) {
				t = taxtabledao.findByNum(money / 1000 - (money / 1000 % 5));
			} else if (money <= 3000000) {

				t = taxtabledao.findByNum(money / 1000 - (money / 1000 % 10));
			} else
				t = taxtabledao.findByNum(money / 1000 - (money / 1000 % 20));

			// 부양가족수
			switch (salary.getDependentNum()) {
			case 1:
				incomeTax = t.getOne();
				break;
			case 2:
				incomeTax = t.getTwo();
				break;
			case 3:
				incomeTax = t.getThree();
				break;
			case 4:
				incomeTax = t.getFour();
				break;
			case 5:
				incomeTax = t.getFive();
				break;
			case 6:
				incomeTax = t.getSix();
				break;
			case 7:
				incomeTax = t.getSeven();
				break;
			case 8:
				incomeTax = t.getEight();
				break;
			case 9:
				incomeTax = t.getNine();
				break;
			case 10:
				incomeTax = t.getTen();
				break;

			default:
				incomeTax = t.getOver();
				break;
			}
		}
		int thousand[] = { 1552400, 1476570, 1245840, 1215840, 1185840, 1155840, 1125840, 1095840, 1065840, 1035840,
				1005840 };

		if (money == 10000000) {
			incomeTax = thousand[salary.getDependentNum() - 1];
		}
		if (money > 10000000 && money <= 14000000) {
			incomeTax = thousand[salary.getDependentNum() - 1] + (int) ((money - 10000000) * 0.98 * 0.35);
		}
		if (money > 14000000 && money <= 28000000) {
			incomeTax = thousand[salary.getDependentNum() - 1] + 137200 + (int) ((money - 14000000) * 0.98 * 0.38);

		}
		if (money > 28000000 && money <= 30000000) {
			incomeTax = thousand[salary.getDependentNum() - 1] + 6585600 + (int) ((money - 28000000) * 0.98 * 0.40);
		}
		if (money > 30000000 && money <= 45000000) {
			incomeTax = thousand[salary.getDependentNum() - 1] + 7385600 + (int) ((money - 30000000) * 0.40);

		}
		if (money > 45000000) {
			incomeTax = thousand[salary.getDependentNum() - 1] + 13385600 + (int) ((money - 45000000) * 0.42);

		}
		sal.setIncomeTax(incomeTax);
		sal.setLoaclIncomeTax(incomeTax / 10);

		// 10원단위 절삭
		sal.setEmploy(sal.getEmploy() - sal.getEmploy() % 10);
		sal.setGukmin(sal.getGukmin() - sal.getGukmin() % 10);
		sal.setGungang(sal.getGungang() - sal.getGungang() % 10);
		sal.setIncomeTax(sal.getIncomeTax() - sal.getIncomeTax() % 10);
		sal.setJanggi(sal.getJanggi() - sal.getJanggi() % 10);
		sal.setLoaclIncomeTax(sal.getLoaclIncomeTax() - sal.getLoaclIncomeTax() % 10);

		sal.setTotal(sal.getEmploy() + sal.getGukmin() + sal.getGungang() + sal.getIncomeTax() + sal.getJanggi()
				+ sal.getLoaclIncomeTax());
		sal.setIncome(money + salary.getNonTaxableAmount() - sal.getTotal());
		sal.setIncome(sal.getIncome() - sal.getIncome() % 10);
		return sal;
	}

	public boolean validity(Career[] career) { // 유효성 검사
		// 경력이 겹쳤을때
		for (int i = 0; i < career.length; i++) {
			int si = career[i].getStartMonth()+career[i].getStartYear()*12;
			int ei = career[i].getEndMonth()+career[i].getEndYear()*12;
			if(si>=ei) // 시작일이 퇴사일보다 뒤에있을때
				return false;
			for (int j = 0; j < career.length; j++) {
				if (i == j)
					continue;
				int sj = career[j].getStartMonth()+career[j].getStartYear()*12;
				int ej = career[j].getEndMonth()+career[j].getEndYear()*12;
				if(sj>si && sj<ei) {
					return false;
				}
				if(ej>si && ej<ei) {
					return false;
				}
			}
		}
		// 퇴사일이 현재날짜보다 뒤에 있을때
		for (int i = 0; i < career.length; i++) {
			int si = career[i].getStartMonth()+career[i].getStartYear()*12;
			int ei = career[i].getEndMonth()+career[i].getEndYear()*12;
			Date date = new Date();
			if(ei > date.getMonth()+date.getYear())
				return false;
		}
		
		return true;
	}
}
