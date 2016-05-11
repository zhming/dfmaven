// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SessionManagerHttpBinding.java

package com.gihow.dfc;

import com.documentum.com.DfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.client.impl.session.ISession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.Serializable;
import java.security.Principal;
import java.util.*;

// Referenced classes of package com.documentum.web.formext.session:
//			UserPrincipalAuthenticationInformationService, KerberosSSOAuthenticationScheme, IHttpSessionManagerUnboundListener, ConnectionBrokerClientWrapper

public class SessionManagerHttpBinding
	implements IRequestListener
{
	private static final class DocbrokerClientBinding
		implements HttpSessionBindingListener, Serializable
	{

		private transient IDfDocbrokerClient m_docbrokerClient;
		private List m_defaultDocbrokerList;

		private synchronized List getDefaultDocbrokerList()
		{
			return m_defaultDocbrokerList;
		}

		private synchronized IDfDocbrokerClient getDocbrokerClient()
		{
			try
			{
				if (m_docbrokerClient == null)
				{
					IDfDocbrokerClient docbrokerClient = (new DfClientX()).getDocbrokerClient();
					m_docbrokerClient = ConnectionBrokerClientWrapper.getInstance(docbrokerClient);
				}
			}
			catch (DfException exp)
			{
				//throw new WrapperRuntimeException(exp);
			}
			return m_docbrokerClient;
		}

		public void valueBound(HttpSessionBindingEvent httpsessionbindingevent)
		{
		}

		public void valueUnbound(HttpSessionBindingEvent httpsessionbindingevent)
		{
		}



		DocbrokerClientBinding()
		{
			m_docbrokerClient = null;
			m_defaultDocbrokerList = null;
			m_defaultDocbrokerList = Collections.EMPTY_LIST;
			try
			{
				IDfTypedObject docbrokerList = getDocbrokerClient().getDocbrokerMap();
				if (docbrokerList != null)
				{
					int count = docbrokerList.getValueCount("host_name");
					m_defaultDocbrokerList = new ArrayList(count);
					for (int index = 0; index < count; index++)
					{
						String docbrokerHostName = docbrokerList.getRepeatingString("host_name", index);
						int portNumber = docbrokerList.getRepeatingInt("port_number", index);
						String strPortNumber = Integer.toString(portNumber);
						ConnectionBroker docbroker = new ConnectionBroker();
						docbroker.setDocbrokerName(docbrokerHostName);
						docbroker.setDocbrokerPort(strPortNumber);
						m_defaultDocbrokerList.add(docbroker);
					}

					m_defaultDocbrokerList = Collections.unmodifiableList(m_defaultDocbrokerList);
				}
			}
			catch (DfException exp)
			{
				Trace.println("Failed to retrieve Docbase list.");
			}
		}
	}

	private static final class SessionBinding
		implements HttpSessionBindingListener, Serializable
	{

		private IDfSessionManager m_sessionManager;
		private ArrayList m_unboundListener;
		private HashSet m_connectedDocbases;
		private HashMap m_connectedRepositories;

		public IDfSessionManager getSessionManager()
		{
			return m_sessionManager;
		}

		public void valueBound(HttpSessionBindingEvent event)
		{
			if (Trace.SESSION)
				Trace.println((new StringBuilder()).append("Session: HTTP Session attach session manager ").append(m_sessionManager).toString());
		}

		public void valueUnbound(HttpSessionBindingEvent event)
		{
			for (int idxListener = 0; idxListener < m_unboundListener.size(); idxListener++)
			{
				IHttpSessionManagerUnboundListener listener = (IHttpSessionManagerUnboundListener)m_unboundListener.get(idxListener);
				listener.unbound(m_sessionManager);
			}

			if (SessionManagerHttpBinding.inRequest())
				SessionManagerHttpBinding.setForCleanup(this);
			else
				cleanup();

		}

		private void cleanup()
		{
			if (m_sessionManager != null)
			{
				if (Trace.SESSION)
					Trace.println((new StringBuilder()).append("Session: HTTP Session closing session manager ").append(m_sessionManager).toString());
				m_sessionManager.clearIdentities();
			}
		}

		private void addUnboundListener(IHttpSessionManagerUnboundListener listener)
		{
			if (!m_unboundListener.contains(listener))
				m_unboundListener.add(listener);
		}

		private void removeUnboundListener(IHttpSessionManagerUnboundListener listener)
		{
			m_unboundListener.remove(listener);
		}

		private synchronized boolean isConnectedToDocbase(String docbaseName)
		{
			return m_connectedDocbases.contains(getCanonicalDocbase(docbaseName));
		}

		private synchronized void addConnectedDocbase(String docbaseName)
		{
			docbaseName = getCanonicalDocbase(docbaseName);
			if (!m_connectedDocbases.contains(docbaseName))
				m_connectedDocbases.add(docbaseName);
			Repository repo = new Repository();
			repo.setRepositoryName(docbaseName);
			m_connectedRepositories.put(docbaseName, repo);
		}

		private synchronized void removeConnectedDocbase(String docbaseName)
		{
			docbaseName = getCanonicalDocbase(docbaseName);
			if (m_connectedDocbases.contains(docbaseName))
			{
				m_connectedDocbases.remove(docbaseName);
				m_connectedRepositories.remove(docbaseName);
			}
		}

		private synchronized List getConnectedDocbases()
		{
			ArrayList docbaseNameList = new ArrayList();
			Iterator iterator = m_connectedDocbases.iterator();
			if (iterator != null)
			{
				String docbaseName;
				for (; iterator.hasNext(); docbaseNameList.add(docbaseName))
					docbaseName = (String)iterator.next();

			}
			return docbaseNameList;
		}

		private synchronized List getConnectedRepositories()
		{
			ArrayList repositoryList = new ArrayList();
			Iterator iterator = m_connectedRepositories.values().iterator();
			if (iterator != null)
				for (; iterator.hasNext(); repositoryList.add(iterator.next()));
			return repositoryList;
		}

		private String getCanonicalDocbase(String docbase)
		{
			int dot = -1;
			if (docbase != null)
				dot = docbase.indexOf('.');
			if (dot != -1)
				docbase = docbase.substring(0, dot);
			return docbase;
		}









		public SessionBinding(IDfSessionManager sessionManager)
		{
			m_sessionManager = null;
			m_unboundListener = new ArrayList(10);
			m_connectedDocbases = new HashSet();
			m_connectedRepositories = new HashMap();
			m_sessionManager = sessionManager;
		}
	}


	private static final ThreadLocal s_strCurrentDocbase = new ThreadLocal();
	private static final ThreadLocal s_forCleanup = new ThreadLocal();
	private static String s_eventListener = null;
	private static boolean s_isInitializing = false;
	private static final String EVENT_LISTENER = "application.dfsessionmanagereventlistener";
	private static final String DFSESSIONMANAGER_MODE = "application.dfsessionmanager_mode";
	private static final String SESSION_BINDING = "__dmfSessionBinding";
	private static final String DOCBROKER_CLIENT = "__dmfDocbrokerClient";
	private static final String CLIENT_DOCBASE = "__clientDocbase";
	private static final String SESSION_USER_NAME = ".dctm.roleService.username";
	public static final String DOMAIN_CONFIG_PATH = "application.authentication.domain";
	public static final String DOCBASE_CONFIG_PATH = "application.authentication.docbase";
	public static final String DOCBROKER_CONFIG_PATH = "application.authentication.docbroker";
	/**
	 * @deprecated Field DOCBROKER_CONFIG_LIST is deprecated
	 */
	public static final String DOCBROKER_CONFIG_LIST = "application.authentication.docbroker.list";
	/**
	 * @deprecated Field FAVORITES_CONFIG_LIST is deprecated
	 */
	public static final String FAVORITES_CONFIG_LIST = "application.authentication.favorites.list";
	/**
	 * @deprecated Field DEFAULT_DOCBROKER_PORT is deprecated
	 */
	public static final String DEFAULT_DOCBROKER_PORT = "1489";
	/**
	 * @deprecated Field DOCBROKER_NAME_AND_PORT_TABLE is deprecated
	 */
	public static final String DOCBROKER_NAME_AND_PORT_TABLE = "docbrokernameandporttable";
	public static final String REQUEST_ARG_CURRENT_DOCBASE = "currentDocbase";

	/**
	 * @deprecated Method SessionManagerHttpBinding is deprecated
	 */

	public SessionManagerHttpBinding()
	{
	}

	public static IDfSessionManager getSessionManager()
	{
		IDfSessionManager sessionManager;
		sessionManager = null;
		SessionBinding binding = (SessionBinding) SessionState.getAttribute("__dmfSessionBinding");
		if (binding != null)
			sessionManager = binding.getSessionManager();

		if (sessionManager != null){
		    return sessionManager;
        }else{
            sessionManager = newDfSessionManager(null);
            initDfSessionManager(sessionManager);

        }
        SessionState.setAttribute("__dmfSessionBinding", new SessionBinding(sessionManager));
		return sessionManager;
	}

	public static IDfDocbrokerClient getDocbrokerClient()
	{
		IDfDocbrokerClient docbrokerClient = null;
		DocbrokerClientBinding docbroker = (DocbrokerClientBinding) SessionState.getAttribute("__dmfDocbrokerClient");
		if (docbroker == null)
		{
			docbroker = new DocbrokerClientBinding();
			SessionState.setAttribute("__dmfDocbrokerClient", docbroker);
		}
		docbrokerClient = docbroker.getDocbrokerClient();
		return docbrokerClient;
	}

	public static List getDefaultDocbrokerList()
	{
		List docbrokerList = null;
		DocbrokerClientBinding docbroker = (DocbrokerClientBinding) SessionState.getAttribute("__dmfDocbrokerClient");
		if (docbroker == null)
		{
			docbroker = new DocbrokerClientBinding();
			SessionState.setAttribute("__dmfDocbrokerClient", docbroker);
		}
		docbrokerList = docbroker.getDefaultDocbrokerList();
		return docbrokerList;
	}

	public static void releaseSessionManager()
	{
		IDfSessionManager sessionManager = null;
		SessionBinding binding = (SessionBinding) SessionState.getAttribute("__dmfSessionBinding");
		if (binding != null)
			sessionManager = binding.getSessionManager();
		if (sessionManager != null)
		{
			sessionManager.clearIdentities();
			SessionState.removeAttribute("__dmfSessionBinding");
		}
	}

	public static boolean isConnectedToDocbase(String docbaseName)
	{
		boolean isConnected = false;
		SessionBinding binding = (SessionBinding) SessionState.getAttribute("__dmfSessionBinding");
		if (binding != null)
			isConnected = binding.isConnectedToDocbase(docbaseName);
		return isConnected;
	}

	static void addConnectedDocbase(String docbaseName)
	{
		SessionBinding binding = (SessionBinding) SessionState.getAttribute("__dmfSessionBinding");
		if (binding != null)
			binding.addConnectedDocbase(docbaseName);
		else
			throw new WrapperRuntimeException((new StringBuilder()).append("Failed to add ").append(docbaseName).append(" to the connected docbase list").toString());
	}

	static void removeConnectedDocbase(String docbaseName)
	{
		SessionBinding binding = (SessionBinding) SessionState.getAttribute("__dmfSessionBinding");
		if (binding != null)
			binding.removeConnectedDocbase(docbaseName);
		else
			throw new WrapperRuntimeException((new StringBuilder()).append("Failed to remove ").append(docbaseName).append(" from the connected docbase list").toString());
	}

	public static List getConnectedDocbases()
	{
		List docbaseNameList = Collections.EMPTY_LIST;
		SessionBinding binding = (SessionBinding) SessionState.getAttribute("__dmfSessionBinding");
		if (binding != null)
			docbaseNameList = binding.getConnectedDocbases();
		else
			throw new WrapperRuntimeException("Failed to get list of connected docbases");
		return docbaseNameList;
	}

	public static List getConnectedRepositories()
	{
		SessionBinding binding = (SessionBinding) SessionState.getAttribute("__dmfSessionBinding");
		List lstResult = Collections.EMPTY_LIST;
		if (binding != null)
			lstResult = binding.getConnectedRepositories();
		else
			throw new WrapperRuntimeException("Failed to get list of connected repositories");
		return lstResult;
	}

	public static String getCurrentDocbase()
	{
		return (String)s_strCurrentDocbase.get();
	}

	public static void setCurrentDocbase(String strDocbase)
	{
		s_strCurrentDocbase.set(strDocbase);
	}

	public static String getClientDocbase(String aFriend)
	{
		if (aFriend == null)
			throw new IllegalArgumentException("SessionManagerHttpBinding.getClientDocbase(...) - friend argument is required");
		else
			return getClientDocbase();
	}

	private static String getClientDocbase()
	{
		String strDocbase = null;
		strDocbase = (String) ClientSessionState.getAttribute("__clientDocbase");
		if (strDocbase == null)
			strDocbase = (String) SessionState.getAttribute("__clientDocbase");
		return strDocbase;
	}

	public static void setClientDocbase(String strDocbase)
	{
		ClientSessionState.setAttribute("__clientDocbase", strDocbase);
		SessionState.setAttribute("__clientDocbase", strDocbase);
		setCurrentDocbase(strDocbase);
		if (Trace.CLIENTDOCBASE)
			Trace.println((new StringBuilder()).append("SessionManagerHttpBinding.setClientDocbase ").append(strDocbase).toString());
	}

	public static void syncClientDocbase(String sourceClientId)
	{
		String strDocbase = (String) ClientSessionState.getAttribute(sourceClientId, "__clientDocbase");
		if (strDocbase != null && strDocbase.length() > 0)
		{
			setClientDocbase(strDocbase);
			setCurrentDocbase(strDocbase);
			if (Trace.CLIENTDOCBASE)
				Trace.println((new StringBuilder()).append("SessionManagerHttpBinding.syncClientDocbase: sync docbase ").append(strDocbase).append(" for client ").append(ClientSessionState.getClientId()).append(" with the setting of client ").append(sourceClientId).toString());
		}
	}

	public static String getUsername()
	{
		String strUsername;
		IDfSessionManager sessionManager;
		IDfSession dfSession;
		Exception exception;
		String stateVarKey = (new StringBuilder()).append(getCurrentDocbase()).append(".dctm.roleService.username").toString();
		strUsername = (String) SessionState.getAttribute(stateVarKey);
		if (isConnected() && strUsername == null)
		{
			sessionManager = getSessionManager();
			if (sessionManager == null)
				return null;
			dfSession = null;
			try
			{
				dfSession = sessionManager.getSession(getCurrentDocbase());
				strUsername = dfSession.getLoginUserName();
				SessionState.setAttribute(stateVarKey, strUsername);
			}
			catch (DfException dfe)
			{
				throw new WrapperRuntimeException("Failed to get user name", dfe);
			}
			finally
			{
                if (dfSession != null)
                    sessionManager.release(dfSession);
			}

		}

		return strUsername;
	}

	public static boolean isConnected()
	{
		return isConnectedToDocbase(getCurrentDocbase());
	}

	public static IDfSessionManager getNewDfSessionManager()
	{
		IDfSessionManager oldSessionManager;
		IDfSessionManager newSessionManager;
		String targetDocbaseName;
		Locale targetLocale;
		IDfSession dfSession = null;
		Exception exception;
		oldSessionManager = getSessionManager();
		newSessionManager = newDfSessionManager(oldSessionManager);
		targetDocbaseName = getCurrentDocbase();
		targetLocale = LocaleService.getLocale();
		if (UserPrincipalAuthenticationInformationService.getService().isApplied())
		{
//			AbstractEnvironment env = EnvironmentService.getEnvironment();
//			ISession envSession = env.getSessionContract();
//			Principal principalUser = null;
//			if (envSession != null)
//				principalUser = envSession.getUserPrincipal();
//			if (principalUser != null)
//			{
//				newSessionManager.setPrincipalName(principalUser.getName());
//			} else
//			{
//				String msg = "Failed to create session manager; the original user principal is not available.";
//				throw new WrapperRuntimeException(msg);
//			}
		} else
		if (newSessionManager.getIdentity(targetDocbaseName) == null)
		{
			String strPwd = null;
			IDfLoginInfo oldLoginInfo = null;
			oldLoginInfo = oldSessionManager.getIdentity(targetDocbaseName);
			if (oldLoginInfo == null)
			{
				String msg = "Failed to create session manager; the original session manager lost the user identity.";
				throw new WrapperRuntimeException(msg);
			}

			try
			{
				DfLoginInfo loginInfo = new DfLoginInfo();
				loginInfo.setUser(oldLoginInfo.getUser());
				Object binaryCredential = oldLoginInfo.getBinaryCredential();
				if (binaryCredential == null)
				{
					strPwd = oldLoginInfo.getPassword();
					if (strPwd == null)
						strPwd = "";
					else
					if (strPwd.indexOf("DM_TICKET=") >= 0)
					{
						dfSession = oldSessionManager.getSession(targetDocbaseName);
						strPwd = dfSession.getLoginTicket();
					}
					loginInfo.setPassword(strPwd);
				} else
				{
					loginInfo.setBinaryCredential(oldLoginInfo.getBinaryCredential());
				}
				loginInfo.setDomain(oldLoginInfo.getDomain());
				loginInfo.setSecurityMode(oldLoginInfo.getSecurityMode());
				loginInfo.setUserArg1(oldLoginInfo.getUserArg1());
				newSessionManager.setIdentity(targetDocbaseName, loginInfo);
			}
			catch (DfException e)
			{
				String msg = "Failed to create session manager.";
				throw new WrapperRuntimeException(msg, e);
			}
			finally
			{
                if (dfSession != null)
                    oldSessionManager.release(dfSession);
			}

		}

		try
		{
			newSessionManager.setLocale(targetLocale.toString());
			initDfSessionManager(newSessionManager);
			dfSession = newSessionManager.getSession(targetDocbaseName);
			if (dfSession == null || !dfSession.isConnected())
			{
				String msg = (new StringBuilder()).append("Failed to create a session for repository '").append(targetDocbaseName).append("'.").toString();
				throw new WrapperRuntimeException(msg);
			}
		}
		catch (DfException e)
		{
			String msg = "Failed to create session manager.";
			throw new WrapperRuntimeException(msg, e);
		}
		finally
		{
            if (newSessionManager != null && dfSession != null)
                newSessionManager.release(dfSession);
		}

		return newSessionManager;
	}

	private static void initDfSessionManager(IDfSessionManager sessionManager)
	{
		try
		{
			String strEventListener = getEventListener();
			if (strEventListener != null && strEventListener.length() > 0)
			{
				Class cls = Class.forName(strEventListener);
				IDfSessionManagerEventListener listener = (IDfSessionManagerEventListener)cls.newInstance();
				sessionManager.setListener(listener);
			}
		}
		catch (ClassNotFoundException e)
		{
			throw new WrapperRuntimeException("Failed to instantiate instance of IDfSessionManagerEventListener", e);
		}
		catch (IllegalAccessException e)
		{
			throw new WrapperRuntimeException("Failed to instantiate instance of IDfSessionManagerEventListener", e);
		}
		catch (InstantiationException e)
		{
			throw new WrapperRuntimeException("Failed to instantiate instance of IDfSessionManagerEventListener", e);
		}
	}

	private static String getEventListenerClassPath()
	{
		IConfigElement icfgElement = ConfigService.getConfigLookup().lookupElement("application.dfsessionmanagereventlistener", Context.getApplicationContext());
		String strClassName = null;
		if (icfgElement != null)
			strClassName = icfgElement.getChildValue("class");
		return strClassName != null ? strClassName : "";
	}

	private static synchronized String getEventListener()
	{
		return s_eventListener;
	}

	private static IDfSessionManager newDfSessionManager(IDfSessionManager oldManager)
	{
		String mode = ConfigService.getConfigLookup().lookupString("application.dfsessionmanager_mode", Context.getApplicationContext());
		IDfSessionManager sessionManager = null;
		try
		{
			if (mode != null && mode.equals("admin"))
			{
				if (canSupportKerberos())
				{
					IDfClient client = DfcUtils.getClientX().getLocalClient();
					sessionManager = client.newSessionManager();
				} else
				if (oldManager != null && (oldManager instanceof DfAdminSessionManager))
					sessionManager = new DfAdminSessionManager((DfAdminSessionManager)oldManager);
				else
					sessionManager = new DfAdminSessionManager();
			} else
			{
				IDfClient client = DfcUtils.getClientX().getLocalClient();
				sessionManager = client.newSessionManager();
			}
		}
		catch (DfException dfe)
		{
			throw new WrapperRuntimeException("Failed to create session manager", dfe);
		}
		return sessionManager;
	}

	private static boolean canSupportKerberos()
	{
		boolean retValue = false;
//		IConfigElement icfgElement = ConfigService.getConfigLookup().lookupElement("application.authentication.kerberos_sso", Context.getApplicationContext());
//		if (icfgElement != null && KerberosSSOAuthenticationScheme.getKerberosEnabledConfig(icfgElement) && KerberosSSOAuthenticationScheme.checkBrowserSupport(icfgElement).booleanValue() && KerberosSSOAuthenticationScheme.getKerberosDomainConfig(icfgElement) != null)
//			retValue = true;
		return retValue;
	}

	public static void addHttpSessionUnboundListener(IHttpSessionManagerUnboundListener listener)
	{
		SessionBinding binding = (SessionBinding) SessionState.getAttribute("__dmfSessionBinding");
		if (binding == null)
		{
			throw new IllegalStateException("unbound event listener can only be added binding");
		} else
		{
			binding.addUnboundListener(listener);
			return;
		}
	}

	public static void removeHttpSessionUnboundListener(IHttpSessionManagerUnboundListener listener)
	{
		SessionBinding binding = (SessionBinding) SessionState.getAttribute("__dmfSessionBinding");
		if (binding == null)
		{
			throw new IllegalStateException("unbound event listener can only be added binding");
		} else
		{
			binding.removeUnboundListener(listener);
			return;
		}
	}

	public final void notifyRequestStart(HttpServletRequest request, HttpServletResponse response)
	{
		String strDocbaseSource = null;
		String strDocbase = SECURITY.validator().getValidParameterValue("currentDocbase", request.getParameter("currentDocbase"), "XSS");
		strDocbase = SECURITY.validator().getValidHeader("SessionManagerHttpBinding", strDocbase, "Header Manipulation");
		strDocbase = SECURITY.validator().getValidatedPath("DocbaseName", strDocbase, 1, null);
		if (strDocbase != null)
		{
			strDocbaseSource = "currentDocbase Reqeust parameter";
		} else
		{
			strDocbase = getClientDocbase();
			if (strDocbase != null)
				strDocbaseSource = "ClientSessionState";
		}
		if (strDocbase != null)
		{
			if (Trace.CLIENTDOCBASE)
				Trace.println((new StringBuilder()).append("SessionManagerHttpBinding.notifyRequestStart: Setting CurrentDocbase from ").append(strDocbaseSource).append(" DOCBASE: ").append(strDocbase).append("    request:").append(request.getRequestURL()).toString());
			setCurrentDocbase(strDocbase);
		}
	}

	public final void notifyRequestFinish(HttpServletRequest request, HttpServletResponse response)
	{
		s_strCurrentDocbase.set(null);
	}

	public static void bindHttpRequest(HttpServletRequest req, HttpServletResponse res)
	{
		s_forCleanup.set(Boolean.TRUE);
	}

	public static void releaseHttpRequest(HttpServletRequest req, HttpServletResponse res)
	{
		Object o = s_forCleanup.get();
		s_forCleanup.set(null);
		if (o != null && (o instanceof SessionBinding))
			((SessionBinding)o).cleanup();
	}

	private static boolean inRequest()
	{
		return s_forCleanup.get() != null;
	}

	private static void setForCleanup(SessionBinding b)
	{
		s_forCleanup.set(b);
	}



}
