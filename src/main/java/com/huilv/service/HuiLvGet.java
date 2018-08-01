package com.huilv.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class HuiLvGet {

	private static String key = "";
	private static String[] currencyNames = { "美元", "英国", "日元", "欧元" };
	private static List<String> currencyCodeList = new ArrayList<String>();

	private static BigDecimal getHuiLv(String fromMoney, String toMoney) {

		return BigDecimal.ZERO;
	}

	private static String getCurrencyCode(String name) {

		return null;
	}

	private static void initCurrencyCode() {
		for (String name : currencyNames) {
			String currencyCode = getCurrencyCode(name);
			if (currencyCode == null || currencyCode.isEmpty()) {
				System.out.println("無法獲取" + name + "的貨幣code");
			} else {
				currencyCodeList.add(name);
			}
		}
	}

	private static BigDecimal getMostLowRate() {

		return BigDecimal.ZERO;
	}

	public static void main(String[] args) {
		initCurrencyCode();

	}

}
