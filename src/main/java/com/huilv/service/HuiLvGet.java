package com.huilv.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huilv.util.HttpClientUtil;

public class HuiLvGet {

	private static String from = "CNY";
	private static String to = "USD";
	private static String[] currencyNames = { "英镑", "日元", "港币" };
	private static List<String> currencyCodeList = new ArrayList<String>();
	private static Map<String, String> codeMap = new HashMap<String, String>();
	static List<List<String>> lle = new ArrayList<List<String>>();

	public static void main(String[] args) {
		initCurrencyCode();
		// 获取全排列下的货币组合
		arrange(currencyCodeList, 0, currencyCodeList.size());
		// 计算所有组合得到的汇率结果
		List<Map<String, Object>> calculateTotalRate = calculateTotalRate();
		Double lowestHuiLv = getLowestHuiLv(calculateTotalRate);
		Double hignest = gethighestHuiLv(calculateTotalRate);
		System.out.println(calculateTotalRate);

	}

	private static Double gethighestHuiLv(List<Map<String, Object>> calculateTotalRate) {
		List<Double> huiLvList = new ArrayList<Double>();
		for (Map<String, Object> map : calculateTotalRate) {
			BigDecimal huiLv = (BigDecimal) map.get("huiLv");
			huiLvList.add(huiLv.doubleValue());
		}
		Collections.sort(huiLvList);
		Collections.reverse(huiLvList);
		return huiLvList.get(0);
	}

	private static Double getLowestHuiLv(List<Map<String, Object>> calculateTotalRate) {
		List<Double> huiLvList = new ArrayList<Double>();
		for (Map<String, Object> map : calculateTotalRate) {
			BigDecimal huiLv = (BigDecimal) map.get("huiLv");
			huiLvList.add(huiLv.doubleValue());
		}
		Collections.sort(huiLvList);
		return huiLvList.get(0);
	}

	static {
		// 初始化货币列表
		Map<String, String> params = new HashMap<String, String>();
		params.put("key", "8bb4117eaecf2fa0226c55ba1e264637");
		String jsonGet = HttpClientUtil.jsonGet("http://op.juhe.cn/onebox/exchange/list", params);
		JSONObject parseObject = JSON.parseObject(jsonGet);
		JSONObject jsonObject = parseObject.getJSONObject("result");
		JSONArray jsonArray = jsonObject.getJSONArray("list");
		for (Object object : jsonArray) {
			Map<String, String> map = (Map<String, String>) object;
			codeMap.put(map.get("name"), map.get("code"));
		}
	}

	public static List<Map<String, Object>> calculateTotalRate() {
		List<Map<String, Object>> routeHuiLv = new ArrayList<Map<String, Object>>();
		for (List<String> list : lle) {
			BigDecimal totalHuilv = new BigDecimal(1);
			for (int i = 0; i < list.size(); i++) {
				BigDecimal huiLv;
				if (i == 0) {
					huiLv = getHuiLv(from, list.get(i));
				} else if (i == list.size() - 1) {
					huiLv = getHuiLv(list.get(i - 1), list.get(i));
					huiLv = huiLv.multiply(getHuiLv(list.get(i), to));
				} else {
					huiLv = getHuiLv(list.get(i - 1), list.get(i));
				}
				totalHuilv = totalHuilv.multiply(huiLv);
			}
			Map<String, Object> huiLvForSpecificCurrency = new HashMap<String, Object>();
			huiLvForSpecificCurrency.put("huiLv", totalHuilv);
			huiLvForSpecificCurrency.put("route", list.toString());
			routeHuiLv.add(huiLvForSpecificCurrency);
		}
		return routeHuiLv;
	}

	public static void swap(List<String> listM, int i, int j) {
		String temp = "";
		temp = listM.get(i);
		listM.set(i, listM.get(j));
		listM.set(j, temp);
	}

	public static void arrange(List<String> listM, int st, int len) {

		List<String> le = new ArrayList<String>();
		if (st == len - 1) {
			for (int i = 0; i < len; i++) {
				le.add(listM.get(i));
			}
			lle.add(le);
		} else {
			for (int i = st; i < len; i++) {
				swap(listM, st, i);
				arrange(listM, st + 1, len);
				swap(listM, st, i);
			}
		}
	}

	/**
	 * 
	 * 查询货币的汇率
	 * 
	 * @param fromMoney
	 * @param toMoney
	 * @return
	 */
	private static BigDecimal getHuiLv(String fromMoney, String toMoney) {
		System.out.println("汇率查询：" + "from:" + fromMoney + "。to:" + toMoney);
		Map<String, String> params = new HashMap<String, String>();
		params.put("key", "8bb4117eaecf2fa0226c55ba1e264637");
		params.put("from", fromMoney);
		params.put("to", toMoney);
		String jsonGet = HttpClientUtil.jsonGet("http://op.juhe.cn/onebox/exchange/currency", params);
		JSONObject parseObject = JSON.parseObject(jsonGet);
		if (!"0".equals(parseObject.getString("error_code"))) {
			System.out.println("无法查询到汇率信息：" + "from:" + fromMoney + "。to:" + toMoney);
			return BigDecimal.ZERO;
		}

		JSONArray jSONArray = parseObject.getJSONArray(("result"));
		for (Object object : jSONArray) {
			Map<String, String> map = (Map<String, String>) object;
			if (fromMoney.equals(map.get("currencyF"))) {
				return new BigDecimal(map.get("result"));
			}
		}
		return BigDecimal.ZERO;
	}

	private static String getCurrencyCode(String name) {
		return codeMap.get(name);
	}

	private static void initCurrencyCode() {
		for (String name : currencyNames) {
			String currencyCode = getCurrencyCode(name);
			if (currencyCode == null || currencyCode.isEmpty()) {
				System.out.println("無法獲取" + name + "的貨幣code");
			} else {
				currencyCodeList.add(currencyCode);
			}
		}
	}

}
