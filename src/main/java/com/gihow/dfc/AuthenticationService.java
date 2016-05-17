// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AuthenticationService.java

package com.gihow.dfc;

import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.license.ILicenseManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

// Referenced classes of package com.documentum.web.formext.session:
//			IAuthenticationServiceEx2, PasswordExpiredException, IAuthenticationScheme, IAuthenticationServiceEx3, 
//			SessionManagerHttpBinding

public class AuthenticationService
	implements IAuthenticationServiceEx3
{

	private IAuthenticationScheme m_schemes[];
	private static IAuthenticationServiceEx2 m_service = null;
	private static String m_productName = null;
	private static String m_productVersion = null;
	private static ILicenseManager m_licensemgr = null;
	private static final String IGNORE_LICENSE = "RM_WDK";
	private static final String AUTHENTICATION_SERVICE = "application.authentication";
	private static final String FALLBACK_IDENTITY_ENABLED = "application.fallback_identity.enabled";
	private static final String AUTHENTICATION_SCHEMES_PROP = "com.documentum.web.formext.session.AuthenticationSchemes";
	private static final String SCHEME_KEY = "scheme_class.";
	private static final String PRODUCT_NAME = "application.authentication.license.product_name";
	private static final String PRODUCT_BUILD_PROPERTIES = "application.authentication.license.product_version_properties";
	public static final String DCTM_TICKET_PREFIX = "DM_TICKET=";
	public static final String LOGININFO_USERARG1 = "DM_USERARG1";
	private HashMap attrsMap;
//	private com.documentum.web.form.FormProcessor.HistoryByClientIdMap historyByClientId;
	public static final String HTTP_SESSION_ATTRIBUTES = "application.session_config.change_sessionid_on_authentication.session_attributes";

	public static synchronized IAuthenticationServiceEx2 getService()
	{
		IAuthenticationServiceEx2 service = null;
		if (m_service != null)
		{
			service = m_service;
		} else
		{
			IConfigElement icfgElement = ConfigService.getConfigLookup().lookupElement("application.authentication", Context.getApplicationContext());
			String strClassName = null;
			if (icfgElement != null)
			{
				strClassName = icfgElement.getChildValue("service_class");
				if (strClassName == null || strClassName.length() == 0)
					throw new WrapperRuntimeException("application.authentication.class not specified in app.xml");
				try
				{
					Class cls = Class.forName(strClassName);
					service = m_service = (IAuthenticationServiceEx2)cls.newInstance();
				}
				catch (ClassNotFoundException e)
				{
					throw new WrapperRuntimeException(e);
				}
				catch (IllegalAccessException e)
				{
					throw new WrapperRuntimeException(e);
				}
				catch (InstantiationException e)
				{
					throw new WrapperRuntimeException(e);
				}
			} else
			{
				service = new AuthenticationService();
			}
			initLicenseManager();
		}
		return service;
	}

	private static void initLicenseManager()
	{
		m_productName = ConfigService.getConfigLookup().lookupString("application.authentication.license.product_name", Context.getApplicationContext());
		String productProperies = ConfigService.getConfigLookup().lookupString("application.authentication.license.product_version_properties", Context.getApplicationContext());
		m_productVersion = null;
		try
		{
			ResourceBundle bundle = ResourceBundle.getBundle(productProperies);
			if (bundle != null)
			{
				String strBuild = bundle.getString("Build");
				if (strBuild != null && strBuild.length() != 0)
				{
//					Version version = new Version(strBuild);
//					m_productVersion = (new StringBuilder()).append(String.valueOf(version.getMajorVersion())).append(".").append(String.valueOf(version.getMinorVersion())).toString();
				}
			}
		}
		catch (MissingResourceException e1)
		{
			if (Trace.SESSION)
				Trace.println("AuthenticationService: Properties file application.authentication.license.product_version_properties not found");
		}
		try
		{
			IDfClient client = DfcUtils.getClientX().getLocalClient();
			m_licensemgr = client.getLicenseManager();
		}
		catch (DfException dfe)
		{
			if (Trace.SESSION)
			{
				Trace.println("AuthenticationService: Exception while getting an instance of LicenseManager");
			}
		}
	}

	public AuthenticationService()
	{
		m_schemes = null;
		attrsMap = new HashMap();
	}

	public String authenticate(HttpServletRequest request, HttpServletResponse response, String docbase)
		throws DfException
	{
		String authenticatedDocbase = null;
		IAuthenticationScheme theSchemes[] = getSchemes();
		for (int ii = 0; ii < theSchemes.length; ii++)
		{
			authenticatedDocbase = theSchemes[ii].authenticate(request, response, docbase);
			if (authenticatedDocbase != null)
				return authenticatedDocbase;
		}

		return null;
	}

	private void recordLicense(IDfSession session)
	{
		if (m_productName == null || m_productName.length() == 0)
		{
			if (Trace.SESSION)
				Trace.println((new StringBuilder()).append("AuthenticationService: Unable to log a license as product name is: ").append(m_productName).toString());
			return;
		}
		try
		{
			IDfLoginInfo loginInfo = session.getLoginInfo();
			//String userName = UserCacheUtil.getCurrentUserObject(session).getUserLoginName();
            String userName = "";
			if (m_licensemgr != null && !m_productName.equalsIgnoreCase("RM_WDK"))
				m_licensemgr.getLicense(m_productName, m_productVersion, userName, loginInfo.getDomain());
		}
		catch (DfException dfe)
		{
			if (Trace.SESSION)
			{
				Trace.println("AuthenticationService: Exception on call to DFC LicenseManager");
			}
		}
	}

	public String getLoginComponent(HttpServletRequest request, HttpServletResponse response, String docbase, ArgumentList outArgs)
	{
		String loginComponent = null;
		IAuthenticationScheme theSchemes[] = getSchemes();
		int ii = 0;
		do
		{
			if (ii >= theSchemes.length)
				break;
			loginComponent = theSchemes[ii].getLoginComponent(request, response, docbase, outArgs);
			if (loginComponent != null)
				break;
			ii++;
		} while (true);
		return loginComponent;
	}

	private void setCurrentDocBase(String docbase)
	{
//		SessionManagerHttpBinding.addConnectedDocbase(docbase);
//		VisibleRepositoryService visibleRepositoryService = VisibleRepositoryService.getInstance();
//		String orgCurrentDocbase = SessionManagerHttpBinding.getCurrentDocbase();
//		SessionManagerHttpBinding.setCurrentDocbase(docbase);
//		visibleRepositoryService.addVisibleRepository(docbase);
//		visibleRepositoryService.updateDocbrokerClient(docbase);
//		SessionManagerHttpBinding.setCurrentDocbase(orgCurrentDocbase);
	}

	private void copySessionParams(HttpSession httpSession)
	{
//		com.documentum.web.form.FormProcessor.HistoryByClientIdMap historyByClientId = (com.documentum.web.form.FormProcessor.HistoryByClientIdMap)httpSession.getAttribute("__dmfFormHistoryByClientId");
//		if (historyByClientId != null)
//			historyByClientId.getHistoryByClientIdMap().put("removeSnapshot", Boolean.FALSE);
//		String strUserArg1 = (String)httpSession.getAttribute("DM_USERARG1");
//		attrsMap.put("__dmfFormHistoryByClientId", historyByClientId);
//		attrsMap.put("DM_USERARG1", strUserArg1);
//		attrsMap.put("ISVALIDAPPINTGSESSION", SessionState.getAttribute("ISVALIDAPPINTGSESSION"));
//		attrsMap.put("randomid_for_session", SessionState.getAttribute("randomid_for_session"));
//		String attributeName = null;
//		Enumeration attrNamesEnumerator = httpSession.getAttributeNames();
//		do
//		{
//			if (!attrNamesEnumerator.hasMoreElements())
//				break;
//			attributeName = attrNamesEnumerator.nextElement().toString();
//			if (!attributeName.startsWith("__appIntgDispatchArgs"))
//				continue;
//			attrsMap.put(attributeName, httpSession.getAttribute(attributeName));
//			break;
//		} while (true);
//		IConfigLookup config = ConfigService.getConfigLookup();
//		Context context = Context.getApplicationContext();
//		IConfigElement sessAttrs = config.lookupElement("application.session_config.change_sessionid_on_authentication.session_attributes", context);
//		if (sessAttrs != null)
//		{
//			Iterator iter = sessAttrs.getChildElements("session_attribute");
//			if (iter != null)
//				do
//				{
//					if (!iter.hasNext())
//						break;
//					IConfigElement elemFormat = (IConfigElement)iter.next();
//					String attrName = elemFormat.getValue();
//					if (attrName != null)
//						attrsMap.put(attrName, httpSession.getAttribute(attrName));
//				} while (true);
//		}
	}

	private void restoreSessionParams(HttpSession httpSession)
	{
		Object name;
		for (Iterator i$ = attrsMap.keySet().iterator(); i$.hasNext(); httpSession.setAttribute(name.toString(), attrsMap.get(name)))
			name = i$.next();

//		com.documentum.web.form.FormProcessor.HistoryByClientIdMap historyByClientId = (com.documentum.web.form.FormProcessor.HistoryByClientIdMap)httpSession.getAttribute("__dmfFormHistoryByClientId");
//		if (historyByClientId != null)
//			historyByClientId.getHistoryByClientIdMap().remove("removeSnapshot");
		attrsMap.clear();
	}

	public HttpSession changeSessionIdOnAuthentication(HttpSession httpSession, HttpServletRequest request)
	{
		Locale userLocale = LocaleService.getLocale();
		if (request.getSession() != null)
		{
			SessionSync.unlock(request.getSession());
			copySessionParams(httpSession);
			request.getSession(false).invalidate();
			httpSession = request.getSession(true);
			restoreSessionParams(httpSession);
			HttpSessionState.bindHttpSession(httpSession);
			ClientSessionState.bindHttpRequest(request);
			EnvironmentService.bindHttpRequest(request);
			SessionSync.lock(request.getSession());
		}
		LocaleService.setLocale(userLocale);
		return httpSession;
	}

	public void authDfcSessionWU(String userName, String docbase, boolean recordLicense)
		throws DfException
	{
		IDfSessionManager sessionManager;
		IDfSession dfSession;
		Exception exception;
		sessionManager = SessionManagerHttpBinding.getSessionManager();
		dfSession = null;
		try
		{
			sessionManager.setPrincipalName(userName);
			dfSession = sessionManager.getSession(docbase);
			sessionManager.setLocale(LocaleService.getLocale().toString());
			setCurrentDocBase(docbase);
			if (recordLicense)
				recordLicense(dfSession);
		}
		catch (DfException dfe)
		{
			sessionManager.setPrincipalName(null);
			throw dfe;
		}
		finally
		{
            if (dfSession != null)
                sessionManager.release(dfSession);
		}

	}

	public void login(HttpSession httpSession, String userName, String docbase, HttpServletRequest request)
		throws DfException
	{
		authDfcSessionWU(userName, docbase, true);
		if (SessionManagerHttpBinding.getConnectedDocbases().size() == 1)
		{
			HttpSession newHttpSession = changeSessionIdOnAuthentication(httpSession, request);
			authDfcSessionWU(userName, docbase, false);
		}
	}

	public void login(HttpSession httpSession, String userName, String docbase)
		throws DfException
	{
		IDfSessionManager sessionManager;
		IDfSession dfSession;
		Exception exception;
		sessionManager = SessionManagerHttpBinding.getSessionManager();
		dfSession = null;
		try
		{
			sessionManager.setPrincipalName(userName);
			dfSession = sessionManager.getSession(docbase);
			sessionManager.setLocale(LocaleService.getLocale().toString());
			setCurrentDocBase(docbase);
			recordLicense(dfSession);
		}
		catch (DfException dfe)
		{
			sessionManager.setPrincipalName(null);
			throw dfe;
		}
		finally
		{
            if (dfSession != null)
                sessionManager.release(dfSession);
		}

	}

	public void login(HttpSession httpSession, String strDocbase, String strUsername, String strPassword, String strDomain)
		throws  DfException
	{
		IDfLoginInfo loginInfo = new DfLoginInfo();
		loginInfo.setUser(strUsername);
		loginInfo.setPassword(strPassword);
		loginInfo.setDomain(strDomain);
		String strUserArg1 = (String)httpSession.getAttribute("DM_USERARG1");
		if (strUserArg1 != null && strUserArg1.length() > 0)
			loginInfo.setUserArg1(strUserArg1);
		login(httpSession, strDocbase, loginInfo);
	}

	public void login(HttpSession httpSession, String strDocbase, String strDomain, Object binaryCredential)
		throws DfException
	{
		IDfLoginInfo loginInfo = new DfLoginInfo();
		loginInfo.setDomain(strDomain);
		loginInfo.setBinaryCredential(binaryCredential);
		login(httpSession, strDocbase, loginInfo);
	}

	public void login(HttpSession httpSession, String strDocbase, String strDomain, Object binaryCredential, HttpServletRequest request)
		throws DfException
	{
		IDfLoginInfo loginInfo = new DfLoginInfo();
		loginInfo.setDomain(strDomain);
		loginInfo.setBinaryCredential(binaryCredential);
		login(httpSession, strDocbase, loginInfo);
		if (SessionManagerHttpBinding.getConnectedDocbases().size() == 1)
		{
			HttpSession newHttpSession = changeSessionIdOnAuthentication(httpSession, request);
			login(newHttpSession, strDocbase, loginInfo);
		}
	}

	public void authDfcSession(HttpSession httpSession, String strDocbase, String strUsername, String strPassword, String strDomain, boolean recordLicense)
		throws DfException
	{
		IDfSessionManager sessionManager;
		IDfSession session;
		Exception exception;
		IDfLoginInfo loginInfo = new DfLoginInfo();
		loginInfo.setUser(strUsername);
		loginInfo.setPassword(strPassword);
		loginInfo.setDomain(strDomain);
		String strUserArg1 = (String)httpSession.getAttribute("DM_USERARG1");
		if (strUserArg1 != null && strUserArg1.length() > 0)
			loginInfo.setUserArg1(strUserArg1);
		sessionManager = SessionManagerHttpBinding.getSessionManager();
		session = null;
		boolean bfallbackIdentityEnabled = false;
		try
		{
			SessionManagerHttpBinding.removeConnectedDocbase(strDocbase);
			if (sessionManager.hasIdentity(strDocbase))
				sessionManager.clearIdentity(strDocbase);
			sessionManager.setIdentity(strDocbase, loginInfo);
			Boolean fallbackIdentityEnabled = ConfigService.getConfigLookup().lookupBoolean("application.fallback_identity.enabled", Context.getApplicationContext());
			if (fallbackIdentityEnabled != null && fallbackIdentityEnabled.booleanValue())
			{
				if (sessionManager.hasIdentity("*"))
					sessionManager.clearIdentity("*");
				sessionManager.setIdentity("*", loginInfo);
				bfallbackIdentityEnabled = true;
			}
			session = sessionManager.getSession(strDocbase);
			sessionManager.setLocale(LocaleService.getLocale().toString());
			if (Trace.SESSION)
			{
				String strUser = loginInfo.getUser();
				if (strUser != null && strUser.length() > 0)
					Trace.println((new StringBuilder()).append("AuthenticationService: Successfully log user '").append(loginInfo.getUser()).append("' into docbase '").append(strDocbase).append("'").toString());
				else
					Trace.println((new StringBuilder()).append("AuthenticationService: Successfully logged into docbase '").append(strDocbase).append("'").toString());
			}
			Cache.invalidate(httpSession);
			int dot = strDocbase.indexOf('.');
			if (dot != -1)
				strDocbase = strDocbase.substring(0, dot);
			setCurrentDocBase(strDocbase);
			if (recordLicense)
				recordLicense(session);
		}
		catch (DfException dfe)
		{
			if (Trace.SESSION)
				Trace.println((new StringBuilder()).append("AuthenticationService: Failed to log user '").append(loginInfo.getUser()).append("' into docbase '").append(strDocbase).append("', error=").append(dfe).toString());
			sessionManager.clearIdentity(strDocbase);
			if (bfallbackIdentityEnabled)
				sessionManager.clearIdentity("*");
			throw dfe;
		}
		finally
		{
            if (session != null)
                sessionManager.release(session);
		}

	}

	public void login(HttpSession httpSession, String strDocbase, String strUsername, String strPassword, String strDomain, HttpServletRequest request)
		throws DfException
	{
		authDfcSession(httpSession, strDocbase, strUsername, strPassword, strDomain, true);
		if (SessionManagerHttpBinding.getConnectedDocbases().size() == 1)
		{
			HttpSession newHttpSession = changeSessionIdOnAuthentication(httpSession, request);
			authDfcSession(newHttpSession, strDocbase, strUsername, strPassword, strDomain, false);
		}
	}

	private void login(HttpSession httpSession, String strDocbase, IDfLoginInfo loginInfo)
		throws DfException
	{
		IDfSessionManager sessionManager;
		IDfSession session;
		Exception exception;
		sessionManager = SessionManagerHttpBinding.getSessionManager();
		boolean bfallbackIdentityEnabled = false;
		session = null;
		try
		{
			SessionManagerHttpBinding.removeConnectedDocbase(strDocbase);
			if (sessionManager.hasIdentity(strDocbase))
				sessionManager.clearIdentity(strDocbase);
			sessionManager.setIdentity(strDocbase, loginInfo);
			Boolean fallbackIdentityEnabled = ConfigService.getConfigLookup().lookupBoolean("application.fallback_identity.enabled", Context.getApplicationContext());
			if (fallbackIdentityEnabled != null && fallbackIdentityEnabled.booleanValue())
			{
				if (sessionManager.hasIdentity("*"))
					sessionManager.clearIdentity("*");
				sessionManager.setIdentity("*", loginInfo);
				bfallbackIdentityEnabled = true;
			}
			session = sessionManager.getSession(strDocbase);
			String strMessage = session.getMessage(3);
			session.getDocbaseConfig();
			sessionManager.setLocale(LocaleService.getLocale().toString());
			if (Trace.SESSION)
			{
				String strUser = loginInfo.getUser();
				if (strUser != null && strUser.length() > 0)
					Trace.println((new StringBuilder()).append("AuthenticationService: Successfully log user '").append(loginInfo.getUser()).append("' into docbase '").append(strDocbase).append("'").toString());
				else
					Trace.println((new StringBuilder()).append("AuthenticationService: Successfully logged into docbase '").append(strDocbase).append("'").toString());
			}
			Cache.invalidate(httpSession);
			int dot = strDocbase.indexOf('.');
			if (dot != -1)
				strDocbase = strDocbase.substring(0, dot);
			setCurrentDocBase(strDocbase);
			recordLicense(session);
		}
		catch (DfException dfe)
		{
			if (Trace.SESSION)
				Trace.println((new StringBuilder()).append("AuthenticationService: Failed to log user '").append(loginInfo.getUser()).append("' into docbase '").append(strDocbase).append("', error=").append(dfe).toString());
			sessionManager.clearIdentity(strDocbase);
			if (bfallbackIdentityEnabled)
				sessionManager.clearIdentity("*");
			throw dfe;
		}
		finally
		{
            if (session != null)
                sessionManager.release(session);
		}

	}

	public void changePassword(HttpSession httpSession, String strUsername, String strDocbase, String strDomain, String strOldPassword, String strNewPassword)
		throws DfException
	{
		IDfSessionManager sessionManager;
		IDfSession dfSession;
		Exception exception;
		IDfLoginInfo oldLoginInfo = null;
		IDfLoginInfo loginInfo = new DfLoginInfo();
		loginInfo.setUser(strUsername);
		loginInfo.setPassword(strOldPassword);
		loginInfo.setDomain(strDomain);
		sessionManager = SessionManagerHttpBinding.getSessionManager();
		if (sessionManager.hasIdentity(strDocbase))
		{
			oldLoginInfo = sessionManager.getIdentity(strDocbase);
			sessionManager.clearIdentity(strDocbase);
		}
		sessionManager.setIdentity(strDocbase, loginInfo);
		dfSession = null;
		try
		{
			dfSession = sessionManager.getSession(strDocbase);
			dfSession.changePassword(strOldPassword, strNewPassword);
			if (Trace.SESSION)
				Trace.println((new StringBuilder()).append("AuthenticationService: Successfully change password for user '").append(strUsername).append("' in docbase '").append(strDocbase).append("'").toString());
		}
		catch (DfException dfe)
		{
			if (Trace.SESSION)
				Trace.println((new StringBuilder()).append("AuthenticationService: Failed to change password for user '").append(strUsername).append("' in docbase '").append(strDocbase).append("', error=").append(dfe).toString());
			if (oldLoginInfo != null)
				sessionManager.setIdentity(strDocbase, oldLoginInfo);
			else
				sessionManager.clearIdentity(strDocbase);
			throw dfe;
		}
		finally
		{
            if (dfSession != null)
                sessionManager.release(dfSession);
            SessionManagerHttpBinding.releaseSessionManager();
		}

	}

	protected synchronized IAuthenticationScheme[] getSchemes()
	{
		if (m_schemes != null)
			return m_schemes;
		ResourceBundle bundle = ResourceBundle.getBundle("com.documentum.web.formext.session.AuthenticationSchemes", LocaleService.getLocale());
		ArrayList theList = new ArrayList();
		int count = 1;
		String strClassName = null;
		try
		{
			while ((strClassName = bundle.getString((new StringBuilder()).append("scheme_class.").append(String.valueOf(count)).toString())) != null) 
			{
				try
				{
					Class cls = Class.forName(strClassName);
					IAuthenticationScheme aScheme = (IAuthenticationScheme)cls.newInstance();
					theList.add(aScheme);
				}
				catch (ClassNotFoundException e)
				{
					throw new WrapperRuntimeException(e);
				}
				catch (IllegalAccessException e)
				{
					throw new WrapperRuntimeException(e);
				}
				catch (InstantiationException e)
				{
					throw new WrapperRuntimeException(e);
				}
				count++;
			}
		}
		catch (MissingResourceException ignore)
		{
			if (theList.size() == 0)
				throw new WrapperRuntimeException("Failed to load authentication schemes", ignore);
		}
		m_schemes = (IAuthenticationScheme[])(IAuthenticationScheme[])theList.toArray(new IAuthenticationScheme[theList.size()]);
		return m_schemes;
	}

}
