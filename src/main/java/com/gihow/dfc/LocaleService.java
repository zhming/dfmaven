// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   LocaleService.java

package com.gihow.dfc;


import java.util.*;

// Referenced classes of package com.documentum.web.common:
//			ILocaleHook, WrapperRuntimeException, SessionState, BrandingService, 
//			IThemeResolver, Trace

public class LocaleService
{

	private static Boolean s_bclientTimeZoneEnabled = null;
	public static final String LOCALE_CONFIG_PATH = "application.language.default_locale";
	private static final String FALLBACK_TO_ENGLISH_CONFIG_PATH = "application.language.fallback_to_english_locale";
	private static final String CONFIG_SUPPORTED_LOCALES = "application.language.supported_locales";
	private static final String CONFIG_SUPPORTED_LOCALE = "locale";
	private static final String ATTRIBUTE_RIGHT_TO_LEFT = "righttoleft";
	private static final String RB_LOCALE_HOOK = "com.documentum.web.common.LocaleHook";
	private static final String RB_LOCALE_HOOK_CLASS = "localeHook";
	private static final String LOCALE_SESSION_VAR = "__dmfLocale";
	private static final String IS_RTL_LOCALE = "isRTLLocale";
	private static final String TIMEZONE_SESSION_VAR = "__dmfTimeZone";
	private static final String TIMEZONE_PREFERENCE = "clientTimeZone";
	private static final String APP_DIR_SESSION_VAR = "__dmfAppDirection";
	private static ILocaleHook s_localeHook;
	private static Boolean s_bFallbackToEnglishLocale = null;
	private static Locale s_supportedLocales[] = null;
	private static Context s_context = null;
	private static final String CLIENT_TIMEZONE_CONFIG_ELEMENT = "application.client_timezone_awareness.enabled";

	public LocaleService()
	{
	}

	public static Locale getLocale()
	{
		Locale locale = null;
		if (s_localeHook != null)
			locale = s_localeHook.getLocale();
		if (locale == null)
		{
			locale = (Locale)SessionState.getAttribute("__dmfLocale");
			if (locale == null)
				locale = Locale.getDefault();
		}
		return locale;
	}

	public static void setLocale(Locale locale)
	{
		if (locale == null)
			throw new IllegalArgumentException("Locale is a mandatory parameter");
		IConfigLookup lookup = ConfigService.getConfigLookup();
		IConfigElement configLocales = lookup.lookupElement("application.language.supported_locales", getContext());
		Iterator iterLocales = configLocales.getChildElements("locale");
		SessionState.removeAttribute("isRTLLocale");
		do
		{
			if (!iterLocales.hasNext())
				break;
			IConfigElement configLocale = (IConfigElement)iterLocales.next();
			Locale cfgLocaleObj = createLocale(configLocale.getValue());
			Boolean isRTLLocale = configLocale.getAttributeValueAsBoolean("righttoleft");
			if (!cfgLocaleObj.equals(locale) || isRTLLocale == null || !isRTLLocale.booleanValue())
				continue;
			SessionState.setAttribute("isRTLLocale", Boolean.TRUE);
			break;
		} while (true);
		SessionState.setAttribute("__dmfLocale", locale);
//		BrandingService.getThemeResolver().refresh();
		if (Trace.LOCALESERVICE)
			Trace.println((new StringBuilder()).append("LocaleService: Locale set to ").append(locale.getDisplayName()).append(" (").append(locale.toString()).append(")").toString());
	}

	public static Locale getDefaultLocale()
	{
		Locale defaultLocale = null;
		IConfigLookup lookup = ConfigService.getConfigLookup();
		String strDefaultLocale = lookup.lookupString("application.language.default_locale", getContext());
		if (strDefaultLocale != null && strDefaultLocale.length() > 0)
			defaultLocale = createLocale(strDefaultLocale);
		if (defaultLocale == null)
			defaultLocale = Locale.getDefault();
		return defaultLocale;
	}

	public static synchronized boolean fallbackToEnglishLocale()
	{
		if (s_bFallbackToEnglishLocale == null)
		{
			IConfigLookup lookup = ConfigService.getConfigLookup();
			String strValue = lookup.lookupString("application.language.fallback_to_english_locale", getContext());
			s_bFallbackToEnglishLocale = Boolean.valueOf(strValue == null || !strValue.equalsIgnoreCase("false"));
		}
		return s_bFallbackToEnglishLocale.booleanValue();
	}

	public static synchronized Locale[] getSupportedLocales()
	{
		if (s_supportedLocales == null)
		{
			ArrayList arrLocales = new ArrayList(10);
			IConfigLookup lookup = ConfigService.getConfigLookup();
			IConfigElement configLocales = lookup.lookupElement("application.language.supported_locales", getContext());
			Locale locale;
			for (Iterator iterLocales = configLocales.getChildElements("locale"); iterLocales.hasNext(); arrLocales.add(locale))
			{
				IConfigElement configLocale = (IConfigElement)iterLocales.next();
				String strLocale = configLocale.getValue();
				locale = createLocale(strLocale);
			}

			s_supportedLocales = new Locale[arrLocales.size()];
			arrLocales.toArray(s_supportedLocales);
		}
		Locale locales[] = new Locale[s_supportedLocales.length];
		System.arraycopy(s_supportedLocales, 0, locales, 0, s_supportedLocales.length);
		return locales;
	}

	public static Locale createLocale(String strLocale)
	{
		if (strLocale == null || strLocale.length() == 0)
			throw new IllegalArgumentException("strLocale is a mandatory parameter");
		Locale locale = null;
		StringTokenizer tokens = new StringTokenizer(strLocale, "_");
		String strLanguage = tokens.nextToken();
		if (tokens.hasMoreTokens())
		{
			String strCountry = tokens.nextToken();
			if (tokens.hasMoreTokens())
			{
				String strVarient = tokens.nextToken();
				locale = new Locale(strLanguage, strCountry, strVarient);
			} else
			{
				locale = new Locale(strLanguage, strCountry);
			}
		} else
		{
			throw new IllegalArgumentException((new StringBuilder()).append("Malformed Locale ").append(strLocale).toString());
		}
		return locale;
	}

	public static TimeZone getTimeZone()
	{
		if (!hasClientTimeZoneEnabled())
			return TimeZone.getDefault();
		TimeZone tz = (TimeZone)SessionState.getAttribute("__dmfTimeZone");
		if (tz == null)
		{
//			IPreferenceStore prefs = PreferenceService.getPreferenceStore();
//			if (prefs.readString("clientTimeZone") != null)
//				tz = TimeZone.getTimeZone(prefs.readString("clientTimeZone"));
//			else
//				tz = TimeZone.getDefault();
//			SessionState.setAttribute("__dmfTimeZone", tz);
//			if (Trace.LOCALESERVICE)
//				Trace.println((new StringBuilder()).append("LocaleService: Using default time zone: ").append(tz.getDisplayName()).toString());
		}
		return tz;
	}

	public static void setTimeZone(int clientTzOffset)
	{
		if (!hasClientTimeZoneEnabled())
			return;
		if (SessionState.getAttribute("__dmfTimeZone") != null)
			return;
		clientTzOffset = -clientTzOffset * 60 * 1000;
		long now = System.currentTimeMillis();
		TimeZone clientTz = TimeZone.getDefault();
		if (clientTz.getOffset(now) != clientTzOffset)
		{
			String arr$[] = TimeZone.getAvailableIDs();
			int len$ = arr$.length;
			int i$ = 0;
			do
			{
				if (i$ >= len$)
					break;
				String tzid = arr$[i$];
				TimeZone tz = TimeZone.getTimeZone(tzid);
				if (tz.getOffset(now) == clientTzOffset)
				{
					clientTz = tz;
					break;
				}
				i$++;
			} while (true);
		}
		SessionState.setAttribute("__dmfTimeZone", clientTz);
//		IPreferenceStore prefs = PreferenceService.getPreferenceStore();
//		prefs.writeString("clientTimeZone", clientTz.getID());
		if (Trace.LOCALESERVICE)
			Trace.println((new StringBuilder()).append("LocaleService: Time zone set to ").append(clientTz.getDisplayName()).toString());
	}

	public static boolean isRightToLeftLocale()
	{
		boolean retVal = false;
		Boolean bIsRTLLocale = (Boolean)SessionState.getAttribute("isRTLLocale");
		if (bIsRTLLocale != null && bIsRTLLocale.booleanValue())
			retVal = true;
		return retVal;
	}

	private static void initialiseLocaleHook()
	{
		ResourceBundle localeProps = null;
		String strLocaleHookClass = null;
		try
		{
			localeProps = ResourceBundle.getBundle("com.documentum.web.common.LocaleHook");
			strLocaleHookClass = localeProps.getString("localeHook");
		}
		catch (MissingResourceException e)
		{
			Trace.println("LocaleService: com.documentum.web.common.LocaleHook file not found");
		}
		if (strLocaleHookClass != null && strLocaleHookClass.length() > 0)
		{
			try
			{
				s_localeHook = (ILocaleHook)Class.forName(strLocaleHookClass).newInstance();
			}
			catch (ClassNotFoundException e)
			{
				throw new WrapperRuntimeException((new StringBuilder()).append("LocaleHook class not found: ").append(strLocaleHookClass).toString(), e);
			}
			catch (IllegalAccessException e)
			{
				throw new WrapperRuntimeException((new StringBuilder()).append("Unable to access LocaleHook class: ").append(strLocaleHookClass).toString(), e);
			}
			catch (InstantiationException e)
			{
				throw new WrapperRuntimeException((new StringBuilder()).append("Unable to instantiate LocaleHook class: ").append(strLocaleHookClass).toString(), e);
			}
			if (Trace.LOCALESERVICE)
				Trace.println((new StringBuilder()).append("LocaleService: Created Locale Hook ").append(strLocaleHookClass).toString());
		}
	}

	private static synchronized Context getContext()
	{
		if (s_context == null)
			s_context = new Context();
		return s_context;
	}

	private static boolean hasClientTimeZoneEnabled()
	{
		if (s_bclientTimeZoneEnabled != null)
			return s_bclientTimeZoneEnabled.booleanValue();
		IConfigLookup lookup = ConfigService.getConfigLookup();
		s_bclientTimeZoneEnabled = lookup.lookupBoolean("application.client_timezone_awareness.enabled", Context.getApplicationContext());
		if (s_bclientTimeZoneEnabled == null)
			s_bclientTimeZoneEnabled = Boolean.TRUE;
		return s_bclientTimeZoneEnabled.booleanValue();
	}

	static 
	{
		initialiseLocaleHook();
	}
}
