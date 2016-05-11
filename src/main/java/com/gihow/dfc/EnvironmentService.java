package com.gihow.dfc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Referenced classes of package com.documentum.web.env:
//            AbstractEnvironment, Environment, ILifecycle, IRequestListener,
//            ISessionListener, Trace

public class EnvironmentService
        implements IRequestListener, ISessionListener
{

    public EnvironmentService()
    {
    }

    public static final EnvironmentService getEnvironmentService()
    {
        return s_envService;
    }

    public static final void bindHttpRequest(HttpServletRequest request)
    {
        String requestScopedEnvironmentClass = request != null ? (String)request.getAttribute("requestScopedEnvironmentClass") : null;
        AbstractEnvironment env = null;
        if(requestScopedEnvironmentClass != null)
        {
            env = createEnvironment(requestScopedEnvironmentClass);
        } else
        {
            TransientObjectWrapper object = (TransientObjectWrapper) SessionState.getAttribute(STR_ENVIRONMENT);
            if(object != null)
                env = (AbstractEnvironment)object.get();
            if(env == null)
            {
                IConfigLookup lookup = ConfigService.getConfigLookup();
                String strServerEnvClass = lookup.lookupString("application.environment.serverenv.class", new Context());
                if(strServerEnvClass == null || strServerEnvClass.length() == 0)
                    throw new WrapperRuntimeException("Unable to bind environment to the session.  Check your app.xml settings.");
                env = createEnvironment(strServerEnvClass);
                if(env != null)
                    SessionState.setAttribute(STR_ENVIRONMENT, new TransientObjectWrapper(env));
            }
        }
        s_currentEnvironment.set(env);
    }

    public static final void releaseHttpRequest(HttpServletRequest request)
    {
        s_currentEnvironment.set(null);
    }

    public static final AbstractEnvironment getEnvironment()
    {
        AbstractEnvironment env = (AbstractEnvironment)s_currentEnvironment.get();
        if(env == null)
        {
            TransientObjectWrapper object = (TransientObjectWrapper) SessionState.getAttribute(STR_ENVIRONMENT);
            if(object != null)
                env = (AbstractEnvironment)object.get();
            if(env == null)
            {
                synchronized(EnvironmentService.class)
                {
                    if(s_defEnvironment == null)
                    {
                        String strDefaultServerEnvClass = com.documentum.web.env.app.AppServerEnvironment.class.getName();
                        s_defEnvironment = createEnvironment(strDefaultServerEnvClass);
                    }
                }
                env = s_defEnvironment;
            }
            s_currentEnvironment.set(env);
        }
        return env;
    }

    private static AbstractEnvironment createEnvironment(String strEnvironmentClass)
    {
        AbstractEnvironment env;
        Exception exception;
        if(Trace.ENVIRONMENT)
            Trace.println((new StringBuilder()).append("Environment Service: Environment Class = ").append(strEnvironmentClass).toString());
        env = null;
        if(s_inCreateEnvironmentCall.get() != null)
           // break MISSING_BLOCK_LABEL_265;
        s_inCreateEnvironmentCall.set(Boolean.TRUE);
        try
        {
            Class classEnvironment = Class.forName(strEnvironmentClass);
            Environment registeredEnv = (Environment)classEnvironment.newInstance();
            env = registeredEnv;
        }
        catch(ClassNotFoundException err)
        {
            throw new WrapperRuntimeException((new StringBuilder()).append("Could not find environment impl class ").append(strEnvironmentClass).toString(), err);
        }
        catch(InstantiationException err)
        {
            throw new WrapperRuntimeException((new StringBuilder()).append("Could not create environment ").append(strEnvironmentClass).toString(), err);
        }
        catch(IllegalAccessException err)
        {
            throw new WrapperRuntimeException((new StringBuilder()).append("Could not create environment ").append(strEnvironmentClass).append(" due to insuficient privileges").toString(), err);
        }
        catch(ClassCastException err)
        {
            throw new WrapperRuntimeException((new StringBuilder()).append("Environment ").append(strEnvironmentClass).append(" is not a type of ").append(com.documentum.web.env.Environment.class.getName()).toString(), err);
        }
        finally
        {
            s_inCreateEnvironmentCall.set(null);
        }
        s_inCreateEnvironmentCall.set(null);
        //break MISSING_BLOCK_LABEL_220;
       // throw exception;
        if(env == null)
            throw new WrapperRuntimeException("Failed to create environment impl");
        if(Trace.ENVIRONMENT)
            Trace.println((new StringBuilder()).append("Environment Service: Environment Created... ").append(env.toString()).toString());
        return env;
    }

    public final String getVersion()
    {
        return "6.0";
    }

    public final void notifyRequestStart(HttpServletRequest request, HttpServletResponse response)
    {
        if(Trace.ENVIRONMENT)
        {
            Trace.println("Environment Service: Notify request start...");
            Trace.println((new StringBuilder()).append("Environment Service: ...session = ").append(request.getSession().getId()).toString());
            Trace.println((new StringBuilder()).append("Environment Service: ...URI = ").append(request.getRequestURI()).toString());
        }
        AbstractEnvironment env = getEnvironment();
        env.notifyRequestStart(request, response);
        if(env instanceof ILifecycle)
            ((ILifecycle)env).onRequestStart(request);
        String tzOff = request.getParameter("__dmfTzoff");
        if(tzOff != null && tzOff.length() > 0)
            LocaleService.setTimeZone(Integer.valueOf(tzOff, 10).intValue());
    }

    public final void notifyRequestFinish(HttpServletRequest request, HttpServletResponse response)
    {
        AbstractEnvironment env = getEnvironment();
        env.notifyRequestFinish(request, response);
        if(env instanceof ILifecycle)
            ((ILifecycle)env).onRequestFinish(request);
        request.removeAttribute(Thread.currentThread().getName());
    }

    public final void notifySessionStart(HttpSession session)
    {
        if(Trace.ENVIRONMENT)
        {
            Trace.println("Environment Service: Notify session start...");
            Trace.println((new StringBuilder()).append("Environment Service: ...session = ").append(session.getId()).toString());
        }
        AbstractEnvironment env = getEnvironment();
        if(env instanceof ILifecycle)
            ((ILifecycle)env).onSessionStart(session);
    }

    public final void notifySessionFinish(HttpSession session)
    {
        if(Trace.ENVIRONMENT)
        {
            Trace.println("Environment Service: Notify session finish...");
            Trace.println((new StringBuilder()).append("Environment Service: ...session = ").append(session.getId()).toString());
        }
        AbstractEnvironment env = getEnvironment();
        if(env instanceof ILifecycle)
            ((ILifecycle)env).onSessionFinish(session);
    }

    private static EnvironmentService s_envService = new EnvironmentService();
    private static final String STR_ENVIRONMENT = com.documentum.web.env.AbstractEnvironment.class.getName();
    private static final String ENVIRONMENT_SERVICE_VERSION = "6.0";
    private static AbstractEnvironment s_defEnvironment = null;
    private static final ThreadLocal s_inCreateEnvironmentCall = new ThreadLocal();
    private static final ThreadLocal s_currentEnvironment = new ThreadLocal();
    public static final String REQUEST_SCOPED_ENV_CLASS = "requestScopedEnvironmentClass";

}