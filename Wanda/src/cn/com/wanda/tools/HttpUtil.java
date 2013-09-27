
package cn.com.wanda.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;


public class HttpUtil {

	private static Header[] headers = new BasicHeader[1];

	private static String TAG = "HTTPUTIL";

	private static int TIMEOUT = 5 * 1000;

	private static final String BOUNDARY = "---------------------------7db1c523809b2";
	/**
	 * Your header of http op
	 * 
	 * @return
	 */
	static {

		headers[0] = new BasicHeader("User-Agent", "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
				+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");

	}

	public static boolean delete(String murl) throws Exception {

		URL url = new URL(murl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("DELETE");
		conn.setConnectTimeout(5000);
		if (conn.getResponseCode() == 204) {

			MLog.e(conn.toString());
			return true;
		}
		MLog.e(conn.getRequestMethod());
		MLog.e(conn.getResponseCode() + "");
		return false;
	}

	static public String get(String url, HashMap<String, String> map, int id) {

		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT);
		HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT);
		ConnManagerParams.setTimeout(client.getParams(), TIMEOUT);

		String result = "ERROR";
		if (null != map) {
			int i = 0;
			for (Map.Entry<String, String> entry : map.entrySet()) {

				Log.i(TAG, entry.getKey() + "=>" + entry.getValue());
				if (i == 0) {
					url = url + "?" + entry.getKey() + "=" + entry.getValue();
				}
				else {
					url = url + "&" + entry.getKey() + "=" + entry.getValue();
				}

				i++;

			}
		}

		Log.i(TAG, url);
		try {
			HttpGet get = new HttpGet(url);
			get.setHeader("Accept", "application/json");
			HttpResponse response = client.execute(get);
//			if (id == 0) {
//				Header[] headers = response.getHeaders("set-cookie");
//				// 保存服务器返回的session
//				for (int i = 0; i < headers.length; i++) {
//					// Log.e("sessionid", headers<i>.getValue());
//					String value = headers[i].getValue();
//					SinInfo.SESSIONID = value.substring(0, value.indexOf(";"));
//				}
//			}

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// setCookie(response);

				result = EntityUtils.toString(response.getEntity(), "UTF-8");

			}
			else {
				result = EntityUtils.toString(response.getEntity(), "UTF-8") + response.getStatusLine().getStatusCode()
						+ "ERROR";
			}

		}
		catch (ConnectTimeoutException e) {
			result = "TIMEOUTERROR";
		}

		catch (Exception e) {
			result = e.toString()+"";
			e.printStackTrace();

		}
		Log.i(TAG, "result =>" + result);

		return result;
	}

	private static byte[] httpConnect(HttpRequest request, Context con) throws Exception {

		// Log.e("请求", CommonDefines.SESSIONID);
		HttpResponse response = null;
//		SaveUserMessage su = new SaveUserMessage(con);
//		if (SinInfo.SESSIONID != null || SinInfo.SESSIONID != "") {
//			// 设置sessionid，把第一次请求的id放在之后要请求的request报文头里
//			request.setHeader("Cookie", SinInfo.SESSIONID);
//
//		}

		if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity resEntity = response.getEntity();
			Header[] headers = response.getHeaders("set-cookie");
			// 保存服务器返回的session
			for (int i = 0; i < headers.length; i++) {
				// Log.e("sessionid", headers<i>.getValue());
//				String value = headers[i].getValue();
//				SinInfo.SESSIONID = value.substring(0, value.indexOf(";"));
			}

			return (resEntity == null) ? null : EntityUtils.toByteArray(resEntity);
		}
		return null;
	}

	/**
	 * Op Http post request , "404error" response if failed
	 * 
	 * @param url
	 * @param map
	 *            Values to request
	 * @return
	 * @throws IOException
	 */

	static public String post(String url, HashMap<String, String> map) throws IOException {

		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT);
		HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT);
		ConnManagerParams.setTimeout(client.getParams(), TIMEOUT);
		HttpPost post = new HttpPost(url);
		post.setHeader("Accept", "application/json");
		MLog.i(TAG, url);
		String result = "ERROR";
		ArrayList<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
		if (map != null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				Log.i(TAG, entry.getKey() + "=>" + entry.getValue());
				BasicNameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
				pairList.add(pair);
			}

		}
		try {
			HttpEntity entity = new UrlEncodedFormEntity(pairList, "UTF-8");
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				result = EntityUtils.toString(response.getEntity(), "UTF-8");

			}
			else {
				result = EntityUtils.toString(response.getEntity(), "UTF-8") + response.getStatusLine().getStatusCode()
						+ "ERROR";
			}

		}
		catch (ConnectTimeoutException e) {
			result = "TIMEOUTERROR";
		}

		catch (Exception e) {
			result = "OTHERERROR";
			e.printStackTrace();

		}
		Log.i(TAG, "result =>" + result);
		return result;
	}

	/**
	 * 自定义的http请求可以设置为DELETE PUT等而不是GET
	 * 
	 * @param url
	 * @param params
	 * @param method
	 * @throws IOException
	 */

	public static String customrequest(String url, HashMap<String, String> params, String method) {

		try {

			URL postUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) postUrl.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(10 * 1000);

			conn.setRequestMethod(method);
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows XP; DigExt)");

			conn.connect();
			OutputStream out = conn.getOutputStream();
			StringBuilder sb = new StringBuilder();
			if (null != params) {
				int i = params.size();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					if (i == 1) {
						sb.append(entry.getKey() + "=" + entry.getValue());
					}
					else {
						sb.append(entry.getKey() + "=" + entry.getValue() + "&");
					}

					i--;
				}
			}
			String content = sb.toString();
			out.write(content.getBytes("UTF-8"));
			out.flush();
			out.close();
			InputStream inStream = conn.getInputStream();
			String result = inputStream2String(inStream);
			Log.i(TAG, "result>" + result);
			conn.disconnect();
			return result;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * 必须严格限制get请求所以增加这个方法 这个方法也可以自定义请求
	 * 
	 * @param url
	 * @param method
	 * @throws Exception
	 */

	public static String customrequestget(String url, HashMap<String, String> map, String method) {

		if (null != map) {
			int i = 0;
			for (Map.Entry<String, String> entry : map.entrySet()) {

				if (i == 0) {
					url = url + "?" + entry.getKey() + "=" + entry.getValue();
				}
				else {
					url = url + "&" + entry.getKey() + "=" + entry.getValue();
				}

				i++;
			}
		}
		try {

			URL murl = new URL(url);
			System.out.print(url);
			HttpURLConnection conn = (HttpURLConnection) murl.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod(method);

			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows XP; DigExt)");

			InputStream inStream = conn.getInputStream();
			String result = inputStream2String(inStream);
			Log.i(TAG, "result>" + result);
			conn.disconnect();
			return result;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * 上传多张图片
	 */
	public static void post(String actionUrl, Map<String, String> params, Map<String, File> files) throws IOException {

		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(5 * 1000); // 缓存的最长时间
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

		// 首先组拼文本类型的参数
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}

		DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
		outStream.write(sb.toString().getBytes());
		InputStream in = null;
		// 发送文件数据
		if (files != null) {
			for (Map.Entry<String, File> file : files.entrySet()) {

				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1.append("Content-Disposition: form-data; name=\"source\"; filename=\"" + file.getValue().getName()
						+ "\"" + LINEND);
				sb1.append("Content-Type: image/pjpeg; " + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());

				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}

				is.close();
				outStream.write(LINEND.getBytes());
			}

			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();
			// 得到响应码
			int res = conn.getResponseCode();
			// if (res == 200) {
			in = conn.getInputStream();
			int ch;
			StringBuilder sb2 = new StringBuilder();
			while ((ch = in.read()) != -1) {
				sb2.append((char) ch);
			}

			// }
			outStream.close();
			conn.disconnect();
		}
		// return in.toString();

	}

	/**
	 * is转String
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String inputStream2String(InputStream in) throws IOException {

		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	/**
	 * check net work
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasNetwork(Context context) {

		ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo workinfo = con.getActiveNetworkInfo();
		if (workinfo == null || !workinfo.isAvailable()) {
			Toast.makeText(context, "当前无网络连接,请稍后重试", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/***
	 * @category check if the string is null
	 * @return true if is null
	 * */
	public static boolean isNull(String string) {

		boolean t1 = "".equals(string);
		boolean t2 = string == null;
		boolean t3 = string.equals("null");
		if (t1 || t2 || t3) {
			return true;
		}
		else {
			return false;
		}
	}

	static public byte[] getBytes(File file) throws IOException {

		InputStream ios = null;
		ByteArrayOutputStream ous = null;
		try {
			byte[] buffer = new byte[4096];
			ous = new ByteArrayOutputStream();
			ios = new FileInputStream(file);
			int read = 0;
			while ((read = ios.read(buffer)) != -1) {
				ous.write(buffer, 0, read);
			}
		}
		finally {
			try {
				if (ous != null)
					ous.close();
			}
			catch (IOException e) {
			}

			try {
				if (ios != null)
					ios.close();
			}
			catch (IOException e) {
			}
		}

		return ous.toByteArray();
	}

	public static class MLog {

		static public void e(String msg) {

			android.util.Log.e("=======ERROR======", msg);
		}

		static public void e(String tag, String msg) {

			android.util.Log.e(tag, msg);
		}

		static public void i(String msg) {

			android.util.Log.i("=======INFO======", msg);
		}

		static public void i(String tag, String msg) {

			android.util.Log.i(tag, msg);
		}

	}
}
