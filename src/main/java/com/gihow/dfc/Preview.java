// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Preview.java

package com.gihow.dfc;

import java.util.*;

// Referenced classes of package com.documentum.web.formext.config:
//			IConfigElement, ConfigService, ConfigElement

public class Preview
{
	static class Result
	{

		private IConfigElement m_configElement;
		private IConfigElement m_primaryElement;
		private List m_primaryElementTree;

		IConfigElement getConfigElement()
		{
			return m_configElement;
		}

		IConfigElement getPrimaryElement()
		{
			return m_primaryElement;
		}

		List getPrimaryElementTree()
		{
			if (m_primaryElementTree == null)
				return Collections.emptyList();
			else
				return m_primaryElementTree;
		}

		Result(IConfigElement configElement, IConfigElement primaryElement, List tree)
		{
			m_configElement = null;
			m_primaryElement = null;
			m_primaryElementTree = null;
			m_configElement = configElement;
			m_primaryElement = primaryElement;
			m_primaryElementTree = tree;
		}
	}

	public class Element
	{

		private IConfigElement m_configElement;
		final Preview this$0;

		public IConfigElement asConfigElement()
		{
			return m_configElement;
		}

		public String getName()
		{
			return m_configElement.getName();
		}

		public Element getParent()
		{
			return makeElement(m_configElement.getParent());
		}

		public String getValue()
		{
			return m_configElement.getValue();
		}

		public Boolean getValueAsBoolean()
		{
			return m_configElement.getValueAsBoolean();
		}

		public Integer getValueAsInteger()
		{
			return m_configElement.getValueAsInteger();
		}

		public String getAttributeValue(String strAttributeName)
		{
			return m_configElement.getAttributeValue(strAttributeName);
		}

		public Boolean getAttributeValueAsBoolean(String strAttributeName)
		{
			return m_configElement.getAttributeValueAsBoolean(strAttributeName);
		}

		public Integer getAttributeValueAsInteger(String strAttributeName)
		{
			return m_configElement.getAttributeValueAsInteger(strAttributeName);
		}

		public List getAttributeNames()
		{
			List list = new ArrayList();
			for (Iterator iter = m_configElement.getAttributeNames(); iter.hasNext(); list.add((String)iter.next()));
			return list;
		}

		public List getChildElements()
		{
			List list = new ArrayList();
			final Iterator data[] = {
				null
			};
			ConfigService.contextualProcess(new ConfigService.ContextualProcessor() {

                final Element this$1;
                {
                    this$1 = Element.this;
                }
                public void run() {
                    data[0] = m_configElement.getChildElements();
                }



            }, m_virtualContext.asMap());
			Element element;
			for (; data[0].hasNext(); list.add(element))
				element = makeElement((IConfigElement)data[0].next());

			return list;
		}

		public List getChildElements(final String strPattern)
		{
			List list = new ArrayList();
			final Iterator data[] = {
				null
			};
			ConfigService.contextualProcess(new ConfigService.ContextualProcessor() {

                final Element this$1;

                public void run() {
                    data[0] = m_configElement.getChildElements(strPattern);
                }


                {
                    this$1 = Element.this;
                }
            }, m_virtualContext.asMap());
			Element element;
			for (; data[0].hasNext(); list.add(element))
				element = makeElement((IConfigElement)data[0].next());

			return list;
		}

		public Element getChildElement(final String strPattern)
		{
			final IConfigElement data[] = {
				null
			};
			ConfigService.contextualProcess(new ConfigService.ContextualProcessor() {

                final Element this$1;

                public void run() {
                    data[0] = m_configElement.getChildElement(strPattern);
                }


                {
                    this$1 = Element.this;
                }
            }, m_virtualContext.asMap());
			return makeElement(data[0]);
		}

		public String getChildValue(final String strPattern)
		{
			final String data[] = {
				null
			};
			ConfigService.contextualProcess(new ConfigService.ContextualProcessor() {

                final Element this$1;

                public void run() {
                    data[0] = m_configElement.getChildValue(strPattern);
                }


                {
                    this$1 = Element.this;
                }
            }, m_virtualContext.asMap());
			return data[0];
		}

		public Boolean getChildValueAsBoolean(final String strPattern)
		{
			final Boolean data[] = {
				null
			};
			ConfigService.contextualProcess(new ConfigService.ContextualProcessor() {

                final Element this$1;

                public void run() {
                    data[0] = m_configElement.getChildValueAsBoolean(strPattern);
                }


                {
                    this$1 = Element.this;
                }
            }, m_virtualContext.asMap());
			return data[0];
		}

		public Integer getChildValueAsInteger(final String strPattern)
		{
			final Integer data[] = {
				null
			};
			ConfigService.contextualProcess(new ConfigService.ContextualProcessor() {

                final Element this$1;

                public void run() {
                    data[0] = m_configElement.getChildValueAsInteger(strPattern);
                }


                {
                    this$1 = Element.this;
                }
            }, m_virtualContext.asMap());
			return data[0];
		}

		public Element getDescendantElement(final String strPatternPath)
		{
			final IConfigElement data[] = {
				null
			};
			ConfigService.contextualProcess(new ConfigService.ContextualProcessor() {

                final Element this$1;

                public void run() {
                    data[0] = m_configElement.getDescendantElement(strPatternPath);
                }


                {
                    this$1 = Element.this;
                }
            }, m_virtualContext.asMap());
			return makeElement(data[0]);
		}

		public String getDescendantValue(final String strPatternPath)
		{
			final String data[] = {
				null
			};
			ConfigService.contextualProcess(new ConfigService.ContextualProcessor() {

                final Element this$1;

                public void run() {
                    data[0] = m_configElement.getDescendantValue(strPatternPath);
                }


                {
                    this$1 = Element.this;
                }
            }, m_virtualContext.asMap());
			return data[0];
		}

		public Boolean getDescendantValueAsBoolean(final String strPatternPath)
		{
			final Boolean data[] = {
				null
			};
			ConfigService.contextualProcess(new ConfigService.ContextualProcessor() {

                final Element this$1;

                public void run() {
                    data[0] = m_configElement.getDescendantValueAsBoolean(strPatternPath);
                }


                {
                    this$1 = Element.this;
                }
            }, m_virtualContext.asMap());
			return data[0];
		}

		public Integer getDescendantValueAsInteger(final String strPatternPath)
		{
			final Integer data[] = {
				null
			};
			ConfigService.contextualProcess(new ConfigService.ContextualProcessor() {

                final Element this$1;

                public void run() {
                    data[0] = m_configElement.getDescendantValueAsInteger(strPatternPath);
                }


                {
                    this$1 = Element.this;
                }
            }, m_virtualContext.asMap());
			return data[0];
		}

		public Element getModificationDirective()
		{
			ConfigElement element = (ConfigElement)m_configElement;
			IConfigElement originalParent = element.getOriginalParent();
			if (originalParent == null)
				return null;
			else
				return makeElement(originalParent);
		}

		public boolean equals(Object obj)
		{
			if (obj != null && (obj instanceof Element))
			{
				Element element = (Element)obj;
				return m_configElement == element.m_configElement;
			} else
			{
				return false;
			}
		}

		public int hashCode()
		{
			return m_configElement.hashCode();
		}

		public String toString()
		{
			return m_configElement.toString();
		}


		private Element(IConfigElement element)
		{
            super();
			this$0 = Preview.this;

			m_configElement = null;
			m_configElement = element;
		}

	}

	public static final class VirtualContext
	{

		private Map m_scopeValues;
		private String m_scopeNames[];

		public String[] getScopeNames()
		{
			if (m_scopeNames == null)
				m_scopeNames = ConfigService.getScopeNames();
			return m_scopeNames;
		}

		public String get(String scopeName)
		{
			return (String)m_scopeValues.get(scopeName);
		}

		public void set(String scopeName, String scopeValue)
		{
			m_scopeValues.put(scopeName, scopeValue);
		}

		public String getDefault(String scopeName)
		{
			return ConfigService.getDefaultScopeValue(scopeName);
		}

		public Map asMap()
		{
			Map previewContext = new HashMap();
			String arr$[] = getScopeNames();
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				String scope = arr$[i$];
				String value = get(scope);
				if (value != null)
					previewContext.put(scope, value);
			}

			return previewContext;
		}

		public VirtualContext()
		{
			m_scopeValues = new HashMap();
			m_scopeNames = null;
		}

		private VirtualContext(VirtualContext oldContext)
		{
			m_scopeValues = new HashMap();
			m_scopeNames = null;
			m_scopeValues.putAll(oldContext.m_scopeValues);
		}

	}


	private String m_elementPath;
	private VirtualContext m_virtualContext;
	private Result m_result;

	public Preview(String strElementPath, VirtualContext context)
	{
		m_result = null;
		m_elementPath = strElementPath;
		m_virtualContext = new VirtualContext(context);
	}

	public void go()
	{
		m_result = ConfigService.previewElement(m_elementPath, m_virtualContext.asMap());
	}

	public String getElementPath()
	{
		return m_elementPath;
	}

	public Element getElement()
	{
		if (m_result == null)
			return null;
		else
			return makeElement(m_result.getConfigElement());
	}

	public Element getPrimaryElement()
	{
		if (m_result == null)
			return null;
		else
			return makeElement(m_result.getPrimaryElement());
	}

	public List getPrimaryElementTree()
	{
		if (m_result == null)
			return Collections.emptyList();
		List tree = new ArrayList();
		IConfigElement element;
		for (Iterator i$ = m_result.getPrimaryElementTree().iterator(); i$.hasNext(); tree.add(makeElement(element)))
			element = (IConfigElement)i$.next();

		return tree;
	}

	private Element makeElement(IConfigElement configElement)
	{
		Element element = null;
		if (configElement != null)
			element = new Element(configElement);
		return element;
	}


}
