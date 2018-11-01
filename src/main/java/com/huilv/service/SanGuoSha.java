package com.huilv.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.huilv.util.HttpClientUtil;

public class SanGuoSha {

	public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException {
		while (true) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", "1268");
			params.put("order_no", "my_heart_willgoon");
			String jsonGet = HttpClientUtil.jsonGet("http://www.sgsquhao.com/sure.asp", params);
			System.out.println(jsonGet);
			Thread.sleep(10000);
		}
	}

}
