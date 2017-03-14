package com.cyberlink.cosmetic.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

public class ResourceBundleController {

	private static final String DEFAULT_LOCALE = "en_US";
	private static final Map<String, ResourceBundle> resbundleMap = new HashMap<String, ResourceBundle>();

	private ResourceBundleController() {

	}

	private static List<String> getSupportedLang() {
		List<String> list = new ArrayList<String>();
		list.add("de");
		list.add("en");
		list.add("es");
		list.add("fr");
		list.add("id");
		list.add("it");
		list.add("ja");
		list.add("ko");
		list.add("ms");
		list.add("nl");
		list.add("pt");
		list.add("ru");
		list.add("th");
		list.add("tr");
		list.add("zh");
		return list;
	}

	public static ResourceBundle getResourceBundle(String locale) {
		if (locale == null || locale.isEmpty() || locale.length() < 5
				|| !getSupportedLang().contains(locale.substring(0, 2)))
			locale = DEFAULT_LOCALE;

		try {
			String key;
			if (locale.equalsIgnoreCase("zh_TW"))
				key = locale;
			else
				key = locale.substring(0, 2);

			if (!resbundleMap.containsKey(key)) {
				resbundleMap.put(key, ResourceBundle.getBundle(
						"/lang/language", new Locale(locale.substring(0, 2),
								locale.substring(3, 5)), new UTF8Control()));
			}
			return resbundleMap.get(key);
		} catch (Exception e) {
			return null;
		}
	}

	private static class UTF8Control extends Control {
		public ResourceBundle newBundle(String baseName, Locale locale,
				String format, ClassLoader loader, boolean reload)
				throws IllegalAccessException, InstantiationException,
				IOException {
			// The below is a copy of the default implementation.
			String bundleName = toBundleName(baseName, locale);
			String resourceName = toResourceName(bundleName, "properties");
			ResourceBundle bundle = null;
			InputStream stream = null;
			if (reload) {
				URL url = loader.getResource(resourceName);
				if (url != null) {
					URLConnection connection = url.openConnection();
					if (connection != null) {
						connection.setUseCaches(false);
						stream = connection.getInputStream();
					}
				}
			} else {
				stream = loader.getResourceAsStream(resourceName);
			}
			if (stream != null) {
				try {
					// Only this line is changed to make it to read properties
					// files as UTF-8.
					bundle = new PropertyResourceBundle(new InputStreamReader(
							stream, "UTF-8"));
				} finally {
					stream.close();
				}
			}
			return bundle;
		}
	}
}