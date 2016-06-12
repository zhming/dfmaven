package com.ecm.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class PropertyLooker {
	private static Properties properties = new Properties();
	static {
		try {
			properties.load(getResourceAsStream("conf.properties"));
		} catch (IOException e){
			e.printStackTrace();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}
	}
	public static String get(String propertyName){
		return properties.getProperty(propertyName);
	}
	public static URL getResource(String resourceName) {
		URL url = null;
		url = Thread.currentThread().getContextClassLoader().getResource(
				resourceName);
		if (url == null) {
			url = PropertyLooker.class.getClassLoader().getResource(resourceName);
		}
		return url;
	}
	public static InputStream getResourceAsStream(String resourceName) {
		URL url = getResource(resourceName);
		try {
			return (url != null) ? url.openStream() : null;
		} catch (IOException e) {
			return null;
		}
	}
}
