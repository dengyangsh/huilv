package com.huilv.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huilv.util.HttpClientUtil;

public class HuiLvGet {

	private static String from = "CNY";
	private static String to = "USD";
	// , "加拿大元", "欧元"
	private static String[] currencyNames = { "英镑", "日元", "港币", "加拿大元", "欧元" };
	private static List<String> currencyCodeList = new ArrayList<String>();
	private static Map<String, String> codeMap = new HashMap<String, String>();
	static List<List<String>> lle = new ArrayList<List<String>>();
	private static List<List<String>> str_List = new ArrayList<List<String>>();

	public static void main(String[] args) {
		initCurrencyCode();
		// 获取全排列下的货币组合
		List<List<String>> arrange = getArrange(currencyCodeList);
		lle.addAll(arrange);
		// 计算所有组合得到的汇率结果
		List<Map<String, Object>> calculateTotalRate = calculateTotalRate();
		Map<String, Object> lowestHuiLv = getLowestHuiLv(calculateTotalRate);
		Map<String, Object> highestHuiLv = gethighestHuiLv(calculateTotalRate);
		// System.out.println(calculateTotalRate);
		System.out.println(lowestHuiLv.get("huiLv") + "," + lowestHuiLv.get("route"));
		System.out.println(highestHuiLv.get("huiLv") + "," + highestHuiLv.get("route"));

	}

	private static Map<String, Object> gethighestHuiLv(List<Map<String, Object>> calculateTotalRate) {
		List<Map<String, Object>> tempMap = new ArrayList<Map<String, Object>>(calculateTotalRate);
		Collections.sort(tempMap, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> map1, Map<String, Object> map2) {
				BigDecimal huiLv1 = (BigDecimal) map1.get("huiLv");
				BigDecimal huiLv2 = (BigDecimal) map2.get("huiLv");
				if (huiLv1.compareTo(huiLv2) > 0) {
					return 1;
				} else if (huiLv1.compareTo(huiLv2) == 0) {
					return 0;
				} else {
					return -1;
				}
			}

		});
		Collections.reverse(tempMap);
		return tempMap.get(0);
	}

	private static Map<String, Object> getLowestHuiLv(List<Map<String, Object>> calculateTotalRate) {
		List<Map<String, Object>> tempMap = new ArrayList<Map<String, Object>>(calculateTotalRate);
		Collections.sort(tempMap, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> map1, Map<String, Object> map2) {
				BigDecimal huiLv1 = (BigDecimal) map1.get("huiLv");
				BigDecimal huiLv2 = (BigDecimal) map2.get("huiLv");
				if (huiLv1.compareTo(huiLv2) > 0) {
					return 1;
				} else if (huiLv1.compareTo(huiLv2) == 0) {
					return 0;
				} else {
					return -1;
				}

			}

		});
		return tempMap.get(0);
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

				if (list.size() == 1) {
					huiLv = getHuiLv(from, list.get(i));
					totalHuilv = huiLv.multiply(getHuiLv(list.get(i), to));
					continue;
				}

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
			// 避免查询不到汇率的情况下。结项整理结果
			if (totalHuilv.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
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
		// System.out.println("汇率查询：" + "from:" + fromMoney + "。to:" + toMoney);
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

	public static List<List<String>> getArrange(List<String> lists) {
		str_List.clear();
		listAll(lists, new ArrayList<String>());
		str_List.remove(0);
		return str_List;
	}

	public static void listAll(List<String> candidate, List<String> prefix) {
		str_List.add(prefix);
		for (int i = 0; i < candidate.size(); i++) {
			List<String> tmp = new LinkedList<String>(candidate);
			List<String> prefixList = new LinkedList<String>();
			prefixList.addAll(prefix);
			prefixList.add(tmp.remove(i));
			listAll(tmp, prefixList);
		}
	}

}
