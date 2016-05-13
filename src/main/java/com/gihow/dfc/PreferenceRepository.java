// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   PreferenceRepository.java

package com.gihow.dfc;

import com.documentum.fc.client.*;
import com.documentum.fc.common.*;
import com.gihow.util.PropertyLooker;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

// Referenced classes of package com.documentum.web.env:
//			IApplicationListener

public final class PreferenceRepository
{
	private static class DisabledException extends Exception
	{

		public DisabledException(String message, Exception e)
		{
			super(message, e);
		}
	}

	public static class ApplicationListener
		implements IApplicationListener
	{

		public void notifyApplicationStart(ServletContext context)
		{
			PreferenceRepository.s_preferenceRepository = new PreferenceRepository();
		}

		public void notifyApplicationFinish(ServletContext servletcontext)
		{
		}

		public ApplicationListener()
		{
		}
	}


	private static PreferenceRepository s_preferenceRepository = null;
	private static final String CONFIG_ROOT = "application.preferencesrepository";
	private static final String REPOSITORY_PATH = "repository_path";
	private static final String REPOSITORY = "repository";
	private static final String ABORT_ON_REPOSITORY_UNREACHABLE = "abort_on_repository_unreachable";
	private static final String CONFIG_PWD = "password";
	private static final String NON_REPOSITORY_PREFERENCES = "non_repository_preferences";
	private static final String DEFAULT_PREFERENCES_USER = "dmc_wdk_preferences_owner";
	private static final String DEFAULT_PREFERENCES_ACL_NAME = "dmc_wdk_preferences_acl";
	private static final String DEFAULT_PREFERENCES_ACL_DOMAIN = "dm_dbo";
	private static final String DOCBASE_PATH_SEPARATOR = "/";
	private Boolean m_enabled;
	private String m_repositoryPath;
	private String m_preferenceRepository;
	private Boolean m_abortOnRepositoryUnreachable;
	private List m_nonRepositoryPreferences;
	private IDfSessionManager m_sessionManager;

	public static PreferenceRepository getInstance()
	{
		return s_preferenceRepository;
	}

	public Boolean getEnabled()
	{
		return m_enabled;
	}

	public String getPreferenceRepositoryPath()
	{
		return m_repositoryPath;
	}

	public String getPreferenceRepository()
	{
		return m_preferenceRepository;
	}

	public List getNonRepositoryPreferences()
	{
		return m_nonRepositoryPreferences;
	}

	public IDfSession getRepositorySession()
		throws DfServiceException
	{
		return m_sessionManager.getSession(m_preferenceRepository);
	}

	public void releaseSession(IDfSession session)
	{
		if (session != null)
			m_sessionManager.release(session);
	}

	public IDfDocument getPreferenceDocument(IDfSession iDfSession, String prefDocName, IDfId prefDocId, boolean create)
		throws DfException
	{
		IDfDocument preferenceDoc = null;
		if (prefDocId != null && prefDocId.isObjectId())
		{
			preferenceDoc = (IDfDocument)iDfSession.getObject(prefDocId);
		} else
		{
			String resolvedPath = getPreferenceRepositoryPath();
			int pathLength = resolvedPath.length() + 64;
			StringBuffer qualification = new StringBuffer(pathLength);
			qualification.append("dm_sysobject where folder ('").append(DfUtil.escapeQuotedString(resolvedPath)).append("') and object_name = '").append(DfUtil.escapeQuotedString(prefDocName)).append('\'');
			preferenceDoc = (IDfDocument)iDfSession.getObjectByQualification(qualification.toString());
			if (preferenceDoc == null && create)
			{
				preferenceDoc = (IDfDocument)iDfSession.newObject("dm_document");
				preferenceDoc.setObjectName(prefDocName);
				preferenceDoc.link(resolvedPath);
				preferenceDoc.setACLName("dmc_wdk_preferences_acl");
				preferenceDoc.setACLDomain("dm_dbo");
			}
		}
		return preferenceDoc;
	}

	private PreferenceRepository()
	{
		m_enabled = Boolean.TRUE;
		m_repositoryPath = null;
		m_preferenceRepository = null;
		m_abortOnRepositoryUnreachable = Boolean.FALSE;
		m_nonRepositoryPreferences = Collections.emptyList();
		m_sessionManager = null;
		try
		{
			initialize();
			m_enabled = Boolean.TRUE;
		}
		catch (DisabledException e)
		{
			Trace.error((new StringBuilder()).append("Preference repository is not available, reason: ").append(e.getMessage()).toString(), e);
			m_enabled = Boolean.FALSE;
		}
	}

	private void initialize()
		throws DisabledException
	{
		if (Trace.PREFERENCES)
			Trace.println("Creating PreferencesRepository Instance...");
		Context context = Context.getApplicationContext();
		IConfigLookup configLookup = ConfigService.getConfigLookup();
	//	IConfigElement preferencesRepositoryElement = configLookup.lookupElement("application.preferencesrepository", context);
	//	if (preferencesRepositoryElement == null)
	//		throw new DisabledException("application.preferencesrepository element does not exist in app.xml.", null);
//		m_repositoryPath = configLookup.lookupString("application.preferencesrepository.repository_path", context);
        m_repositoryPath =  PropertyLooker.get("application.preferencesrepository.repository_path");
        if (m_repositoryPath == null || m_repositoryPath.length() == 0)
			throw new DisabledException("application.preferencesrepository.repository_path element does not exist in app.xml", null);
		m_preferenceRepository = PropertyLooker.get("application.preferencesrepository.repository");
		if (m_preferenceRepository == null || m_preferenceRepository.length() == 0)
		{
			DfPreferences dfPreferences = DfPreferences.getInstance();
			m_preferenceRepository = dfPreferences.getGlobalRegistryRepository();
		}
//		if (m_preferenceRepository == null)
//			throw new DisabledException("preference repository has not been configured in app.xml and the DFC global registry docbase has not been set.", null);
		IDfLoginInfo preferenceRepositoryCredentials = new DfLoginInfo();
		//preferenceRepositoryCredentials.setUser("dmc_wdk_preferences_owner");
        preferenceRepositoryCredentials.setUser(PropertyLooker.get("dfc.globalregistry.username"));
		//String password =  configLookup.lookupString("application.preferencesrepository.password", context);
        //从conf.properties中获取超级用户密码
        String password = PropertyLooker.get("dfc.globalregistry.password");
        if (password != null && password.length() > 0)
		{
			try
			{
				//password = TrustedAuthenticatorUtils.decryptByDES(password);
                password = com.gihow.util.Base64.decode(password);
			}
			catch (Exception e)
			{
				throw new DisabledException("repository credentials are not configured", e);
			}
		} else
		{
			Trace.error("preference repository credential is not configured in app.xml", null);
			throw new DisabledException("preference repository credential is not configured in app.xml", null);
		}
		preferenceRepositoryCredentials.setPassword(password);
		try
		{
			m_sessionManager = createDfSessionManager(preferenceRepositoryCredentials, m_preferenceRepository);
			verifyRepositoryPath();
		}
		catch (DfException e)
		{
			m_abortOnRepositoryUnreachable = configLookup.lookupBoolean("application.preferencesrepository.abort_on_repository_unreachable", context);
			if (m_abortOnRepositoryUnreachable != null && m_abortOnRepositoryUnreachable.equals(Boolean.TRUE))
				throw new WrapperRuntimeException("Application failed to start as the Preference Repository is not reachable ");
			else
				throw new DisabledException("Preference Repository is not reachable. ", e);
		}
		m_nonRepositoryPreferences = getPreferencesList(configLookup.lookupElement("application.preferencesrepository.non_repository_preferences", context));
		if (Trace.PREFERENCES)
			Trace.println((new StringBuilder()).append("Created PreferenceRepository(application.preferencesrepository.repository_path = ").append(m_repositoryPath).append(", ").append("repository").append(" = ").append(m_preferenceRepository).toString());
	}

	private void verifyRepositoryPath()
		throws DfException
	{
		IDfSession idfSession = null;
		idfSession = getRepositorySession();
		if (idfSession.getObjectByPath(m_repositoryPath) == null)
		{
			String strParentPath = "";
			StringBuffer strCurrentNamespace = new StringBuffer(strParentPath);
			int i = 0;
			for (StringTokenizer tokens = new StringTokenizer(m_repositoryPath, "/"); tokens.hasMoreTokens();)
			{
				String strToken = tokens.nextToken();
				strCurrentNamespace.append("/").append(strToken);
				IDfFolder idfFolder = (IDfFolder)idfSession.getObjectByPath(strCurrentNamespace.toString());
				if (idfFolder == null)
				{
					if (i == 0)
						idfFolder = (IDfFolder)idfSession.newObject("dm_cabinet");
					else
						idfFolder = (IDfFolder)idfSession.newObject("dm_folder");
					idfFolder.setObjectName(strToken);
					idfFolder.setACLDomain("dm_dbo");
					idfFolder.setACLName("dmc_wdk_preferences_acl");
					if (strParentPath.length() > 0)
						idfFolder.link(strParentPath);
					idfFolder.save();
				}
				strParentPath = strCurrentNamespace.toString();
				i++;
			}

		}
		releaseSession(idfSession);

	}

	private IDfSessionManager createDfSessionManager(IDfLoginInfo repositoryCredentials, String preferenceRepository)
		throws DfException
	{
		IDfSessionManager sessionManager = DfcUtils.getClientX().getLocalClient().newSessionManager();
		sessionManager.setIdentity(preferenceRepository, repositoryCredentials);
		return sessionManager;
	}

	private List getPreferencesList(IConfigElement peferencesElement)
	{
		List tmpList = new ArrayList();
		if (peferencesElement != null)
		{
			IConfigElement iConfigElement;
			for (Iterator prefIterator = peferencesElement.getChildElements("preference"); prefIterator.hasNext(); tmpList.add(iConfigElement.getValue()))
				iConfigElement = (IConfigElement)prefIterator.next();

		}
		return Collections.unmodifiableList(tmpList);
	}



}
