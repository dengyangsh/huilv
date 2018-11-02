package com.huilv.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.huilv.util.HttpClientUtil;

public class SanGuoSha {
	//
	private final static String one = "1268";
	private final static String two = "1444";

	private final static String taobaoUrl = "http://item.taobao.com/item.htm?id=580783815036";

	public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException {
		// String id = getId();
		// apply(id);
		apply(two);
	}

	/**
	 * 获取申请ID
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public static String getId() throws InterruptedException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("tb_tc", "1");
		params.put("addr", "my_heart_willgoon");
		while (true) {
			Document parse;
			try {
				String jsonPost = HttpClientUtil.jsonPost("http://www.sgsquhao.com/main.asp", params);
				parse = Jsoup.parse(jsonPost);
				Elements tables = parse.select("table");
				Element table = tables.get(1);
				// 查到匹配的字符,获取到唯一的淘宝路径节点
				Elements taobaoLink = table.select("a[href=" + taobaoUrl + "]");
				if (taobaoLink.isEmpty()) {
					Thread.sleep(5000);
					continue;
				}

				// 获取到可以提取数据所在table

				Element taobaoParent = taobaoLink.get(0).parent();
				// 获取到请求路径
				Element nextElementSibling = taobaoParent.nextElementSibling();
				Element child = nextElementSibling.child(0);
				String attr = child.attr("href");
				// 解析获取ID值
				String[] split = attr.split("\\?");
				for (String string : split) {
					String[] split2 = string.split("&");
					for (String string2 : split2) {
						if (string2.contains("id")) {
							return string2.split("=")[1];
						}
					}
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	/**
	 * 进行账号申请
	 * 
	 * @param id
	 * @throws InterruptedException
	 */
	public static void apply(String id) throws InterruptedException {
		while (true) {
			try {
				Map<String, String> params = new HashMap<String, String>();
				params.put("id", id);
				params.put("order_no", "my_heart_willgoon");
				String jsonGet = HttpClientUtil.jsonGet("http://www.sgsquhao.com/sure.asp", params);
				System.out.println(jsonGet);
				Thread.sleep(10000);
			} catch (Exception e) {
				continue;
			}
		}
	}
}
