package org.webank.dependents.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class LicenseUtl {

	private static final String MAVEN_CENTOR = "https://mvnrepository.com/artifact/";

	private static final Integer CONNECT_TIMEOUT = 100 * 1000;

	private static final Integer READ_TIMEOUT = 100 * 1000;

	private static final Integer RESPONSE_CODE_SUCCESS = 200;

	public static final String DEFAULT_LICENSE = "NOT FOUND";

	public static Map<String, String> cacheLicenseMap = new ConcurrentHashMap<String, String>();

	private static final String LOCATE_CACHE_FILE = "./cache/cacheLicense";

	static {
		initLocateLicense();
	}

	/**
	 * artifact: org.springframework/spring-test/5.1.1.RELEASE
	 * 
	 */
	private static String getHTMLFromMaven(String artifact, Proxy proxy) {
		InputStream inputStream = null;
		try {
			URL url = new URL(MAVEN_CENTOR + artifact);

			HttpURLConnection connection = null;
			if (proxy != null) {
				connection = (HttpURLConnection) url.openConnection(proxy);
			} else {
				connection = (HttpURLConnection) url.openConnection();
			}

			// 如果是https
			if (connection instanceof HttpsURLConnection) {
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
				((HttpsURLConnection) connection).setSSLSocketFactory(sc.getSocketFactory());
				((HttpsURLConnection) connection).setHostnameVerifier(new TrustAnyHostnameVerifier());
			}

			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.connect();

			int code = connection.getResponseCode();
			if (code == RESPONSE_CODE_SUCCESS.intValue()) {
				inputStream = connection.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
				String line = "";
				StringBuffer sb = new StringBuffer();
				while ((line = br.readLine()) != null) {
					sb.append(line).append(NodeUtl.NEW_LINE);
				}
				return sb.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return NodeUtl.EMPTY;
	}

	public static Proxy buildProxy(String host, int port) {
		return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
	}

	public static String getHTMLFromMaven(String artifact, String host, int port) {
		return getHTMLFromMaven(artifact, buildProxy(host, port));
	}

	public static String getHTMLFromMaven(String artifact) {
		return getHTMLFromMaven(artifact, null);
	}

	private static Document parseDocument(String htmlBody) {
		return Jsoup.parse(htmlBody);
	}

	private static String parseLicense(Document document) {
		Elements elements = document.select(".grid").select("tr");
		if (elements.size() > 0) {
			return elements.get(0).select("td span").text();
		}
		return DEFAULT_LICENSE;
	}

	private static String getLicense_(String htmlBody) {

		if (htmlBody == null) {
			return DEFAULT_LICENSE;
		}

		Document document = parseDocument(htmlBody);
		String license = parseLicense(document);

		if (license == null || license.trim().equals("")) {
			return DEFAULT_LICENSE;
		}
		return license;
	}

	public static String getLicense(String artifact) {
		return getLicense_(getHTMLFromMaven(artifact));
	}

	public static String getLicense(String artifact, String host, int port) {
		return getLicense_(getHTMLFromMaven(artifact, buildProxy(host, port)));
	}

	private static class TrustAnyTrustManager implements X509TrustManager {

		@SuppressWarnings("unused")
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws java.security.cert.CertificateException {
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws java.security.cert.CertificateException {
		}

		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	public static void initLocateLicense() {
		BufferedReader bis = null;
		try {
			File file = new File(LOCATE_CACHE_FILE);
			if (!file.exists()) {
				return;
			}

			bis = new BufferedReader(
					new InputStreamReader(new FileInputStream(LOCATE_CACHE_FILE), StandardCharsets.UTF_8));
			String line = null;

			while ((line = bis.readLine()) != null) {
				String[] values = line.split(NodeUtl.CONNECTOR_LICENSE);
				cacheLicenseMap.put(values[0], values[1]);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void updateLocateCache() {
		Set<Entry<String, String>> set = cacheLicenseMap.entrySet();
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : set) {
			sb.append(entry.getKey()).append(NodeUtl.CONNECTOR_LICENSE).append(entry.getValue())
					.append(NodeUtl.NEW_LINE);
		}
		FileUtil.saveData(sb.toString(), LOCATE_CACHE_FILE);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getLicense("org.xmlunit/xmlunit-core/2.6.2"));
	}
}
