package common.xiao.util;


import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;

public class HttpClientUtil {

	public static void main(String[] args) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("PARAM",
				"{\"lon\":\"113.10906\",\"lat\":\"22.60421\",\"dateTime\":\"2017-02-05 08:00:00\",\"zoom\":\"8\",\"code\":\"440703001\"}");
		// HttpClientUtil.doGet(
		// "http://10.148.16.52:7012/server/airq/queryAirqDataByClick?PARAM={\"lon\":\"113.10906\",\"lat\":\"22.60421\",\"dateTime\":\"2017-02-05
		// 08:00:00\",\"zoom\":\"8\",\"code\":\"440703001\"}");
		// String doGet =
		// HttpClientUtil.doGet("http://10.148.16.52:7012/server/airq/queryAirqDataByClick",
		// param);
		// System.out.println(doGet);

		/*
		 * Map<String, Object> param = new HashMap<String, Object>(); param.put("param",
		 * "{\"name\":\"南石头\",\"params\":[\"\"]}"); param.put("Authorization",
		 * "92uIr/qijCEBX/4ZuDQaFY5OXgBrQwTPbtVaaRNbfPLFrSQPDVLzXXZKHFJeTHLSeHn+6HU4HOiSK3cbbnQwAGw9Y8HCbJlceOWVkJvIv7nPsNI2r02SSubdakRlVhVvSa4vAhVao6dVmhxMRM8boQ=="
		 * ); String doPostJson = HttpClientUtil.doPost(
		 * "http://enav.ngscs.org/services/hydroMeteo/v1/getHydroMeteoByStation",
		 * param);
		 */

	
		String names="坭洲头 、舢舨洲 、南石头 、东江囗 、仙屋角 、担杆岛 、内伶仃 、四尺岩 、大九洲 、高栏港、台山、阳江 、水东 、麻斜岛 、徐闻 、放鸡岛 、东海岛 、湾仔、肇庆 、山狗吼";
		String[] split = names.split("、");
		for (String name : split) {
			Map<String, Object> param2 = new HashMap<String, Object>();
			param2.put("param", "{\"name\":\""+name+"\",\"params\":[\"ALL\"]}");
			String doPostJson = HttpClientUtil.doPost("http://enav.ngscs.org/services/hydroMeteo/v1/getHydroMeteoByStation",
					param2,
					"92uIr/qijCEBX/4ZuDQaFY5OXgBrQwTPbtVaaRNbfPLFrSQPDVLzXXZKHFJeTHLSeHn+6HU4HOiSK3cbbnQwAGw9Y8HCbJlceOWVkJvIv7nPsNI2r02SSubdakRlVhVvSa4vAhVao6dVmhxMRM8boQ==");
			Map parseObject = JSON.parseObject(doPostJson, Map.class);
			try {
				Map parseObject2 = JSON.parseObject(((List) (parseObject.get("data"))).get(0).toString(), Map.class);
				System.out.println(parseObject2);
			} catch (Exception e) {
				// TODO: handle exception
			}

			// String update="update hydrology_station_info set lat='"++"',lon='"+"' where
			// station_name='"+name+"';";
		}
		
	}

	private HttpClientUtil() {
	}

	public static String doGet(String url, Map<String, String> param) {

		// 创建Httpclient对象
		CloseableHttpClient httpclient = HttpClients.createDefault();

		String resultString = "";
		CloseableHttpResponse response = null;
		try {
			// 创建uri
			URIBuilder builder = new URIBuilder(url);
			if (param != null) {
				for (String key : param.keySet()) {
					builder.addParameter(key, param.get(key));
				}
			}
			URI uri = builder.build();

			// 创建http GET请求
			HttpGet httpGet = new HttpGet(uri);

			// 执行请求
			response = httpclient.execute(httpGet);
			// 判断返回状态是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}

	public static String doGet(String url) {
		return doGet(url, null);
	}

	public static String doPost(String url, Map<String, Object> param, String token) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Authorization", token); // 认证token
			// 创建参数列表
			if (param != null) {
				List<NameValuePair> paramList = new ArrayList<>();
				for (String key : param.keySet()) {
					paramList.add(new BasicNameValuePair(key, (String) param.get(key)));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, Consts.UTF_8);
				httpPost.setEntity(entity);
				String string = EntityUtils.toString(entity, "gbk");
				System.out.println(string);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return resultString;
	}

	public static String doPost(String url) {
		return doPost(url, null, null);
	}

	public static String doPostJson(String url, String json, String token_header) throws Exception {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建请求内容
			httpPost.setHeader("HTTP Method", "POST");
			httpPost.setHeader("Connection", "Keep-Alive");
			httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
			httpPost.setHeader("x-authentication-token", token_header);
			httpPost.setHeader("Authorization", token_header); // 认证token
			StringEntity entity = new StringEntity(json);
			entity.setContentType("application/json;charset=utf-8");
			httpPost.setEntity(entity);

			// 执行http请求
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return resultString;
	}

}