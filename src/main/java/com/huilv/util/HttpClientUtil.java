package com.huilv.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;


public class HttpClientUtil {
	public static CloseableHttpClient httpClient = null;
	public static HttpClientContext context = null;
	public static CookieStore cookieStore = null;
	public static RequestConfig requestConfig = null;
	public static String regEx = "(?=<meta).*?(?<=charset=[\\'|\\\"]?)([[a-z]|[A-Z]|[0-9]|-]*)";

	static {
		initHttp();
	}

	static void initHttp() {
		context = HttpClientContext.create();
		// ���ó�ʱʱ�䣨���ӷ���˳�ʱ1�룬�������ݷ��س�ʱ2�룩
		requestConfig = RequestConfig.custom().setConnectTimeout(120000).setSocketTimeout(60000)
				.setConnectionRequestTimeout(60000).build();
		// ����Ĭ����ת�Լ��洢cookie
		httpClient = HttpClientBuilder.create().setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
				.setRedirectStrategy(new DefaultRedirectStrategy()).setDefaultRequestConfig(requestConfig).build();
	}

	public static String jsonPost(String url, Map<String, String> params) {
		System.out.println(url);

		List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
		String respContent = null;
		Iterator it = null;
		if (params != null) {
			it = params.entrySet().iterator();

			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				pairList.add(new BasicNameValuePair(key, value));
			}
		}
		HttpPost post = new HttpPost(url);
		try {
			post.setEntity(new UrlEncodedFormEntity(pairList, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// CloseableHttpClient client = HttpClients.createDefault();
		// post.setEntity(new UrlEncodedFormEntity(pairList,"utf-8"));

		HttpResponse resp;
		try {
			resp = httpClient.execute(post);
			if (resp.getStatusLine().getStatusCode() == 200) {
				HttpEntity he = resp.getEntity();
				respContent = EntityUtils.toString(he, "UTF-8");
			} else {
				// System.out.println("״̬��Ϊ:" + resp.getStatusLine().getStatusCode());
				// System.out.println("�e�`����:" + resp.getStatusLine().getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return respContent;
	}

	@SuppressWarnings("deprecation")
	public static String jsonGet(String url, Map<String, String> params) {
		// System.out.println(url);

		String charSet = "utf-8";
		List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
		String respContent = null;
		Iterator it = null;
		if (params != null) {
			it = params.entrySet().iterator();

			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				pairList.add(new BasicNameValuePair(key, value));
			}
		}
		HttpGet get = new HttpGet(url);
		HttpResponse resp;
		try {
			resp = httpClient.execute(get);
			if (resp.getStatusLine().getStatusCode() == 200) {
				HttpEntity he = resp.getEntity();
				String contentCharSet = EntityUtils.getContentCharSet(he);
				respContent = EntityUtils.toString(he, charSet);

			} else {
				System.out.println("״̬��Ϊ:" + resp.getStatusLine().getStatusCode());
				System.out.println("�e�`����:" + resp.getStatusLine().getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return respContent;

	}

	public static String proxyHttpCilent(String host, String port) {

		SocketAddress addr = new InetSocketAddress(host, Integer.parseInt(port));
		Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
		try {
			URL url = new URL("http://www.sina.com.cn/");
			URLConnection conn = url.openConnection(proxy);
			conn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 7.0; NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)");
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";
			while ((line = in.readLine()) != null) {
				result += line;
			}

			return new String(result.getBytes(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String postJson(String urlPath, Map<String, String> params) {
		try {

			String jsonString = JSONObject.toJSONString(params);
			
			System.out.println(jsonString);
			// ����url��Դ
			URL url = new URL(urlPath);
			// ����http����
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// �����������
			conn.setDoOutput(true);

			conn.setDoInput(true);

			// ���ò��û���
			conn.setUseCaches(false);
			// ���ô��ݷ�ʽ
			conn.setRequestMethod("POST");
			// ����ά�ֳ�����
			conn.setRequestProperty("Connection", "Keep-Alive");
			// �����ļ��ַ���:
			conn.setRequestProperty("Charset", "UTF-8");
			// ת��Ϊ�ֽ�����
			byte[] data = (jsonString).getBytes();
			// �����ļ�����
			conn.setRequestProperty("Content-Length", String.valueOf(data.length));

			// �����ļ�����:
			conn.setRequestProperty("contentType", "application/json");

			// ��ʼ��������
			conn.connect();
			OutputStream out = conn.getOutputStream();
			// д��������ַ���
			out.write((jsonString).getBytes());
			out.flush();
			out.close();

			System.out.println(conn.getResponseCode());

			// ���󷵻ص�״̬
			if (conn.getResponseCode() == 200) {
				System.out.println("���ӳɹ�");
				// ���󷵻ص�����
				InputStream in = conn.getInputStream();
				String a = null;
				try {
					byte[] data1 = new byte[in.available()];
					in.read(data1);
					// ת���ַ���
					a = new String(data1);
					return a;
				} catch (Exception e1) {
					e1.printStackTrace();
					return "";
				}
			} else {
				return "";
			}

		} catch (Exception e) {
			return "";
		}
	}

	public static void main(String[] args) {
		String proxyHttpCilent = proxyHttpCilent("180.168.179.193", "8080");
	}

}
