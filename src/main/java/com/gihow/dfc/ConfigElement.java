// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ConfigElement.java

package com.gihow.dfc;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

// Referenced classes of package com.documentum.web.formext.config:
//			IConfigElement, ConfigService

public class ConfigElement
	implements IConfigElement
{
	private static class IndistinguishableModificationTargetException extends Exception
	{

		private IndistinguishableModificationTargetException(String message)
		{
			super(message);
		}

	}

	private static class AppLevelElement
	{

		private IConfigElement m_element;
		private int m_appLevel;

		IConfigElement getElement()
		{
			return m_element;
		}

		int getAppLevel()
		{
			return m_appLevel;
		}

		AppLevelElement(IConfigElement element, int appLevel)
		{
			m_element = element;
			m_appLevel = appLevel;
		}
	}

	private class Pattern
	{

		private String m_strName;
		private String m_strAttributeName;
		private String m_strAttributeValue;
		final ConfigElement this$0;

		public boolean matches(IConfigElement element)
		{
			if (!element.getName().equals(m_strName))
				return false;
			if (m_strAttributeName != null)
			{
				String strAttributeValue = element.getAttributeValue(m_strAttributeName);
				if (strAttributeValue == null || !strAttributeValue.equals(m_strAttributeValue))
					return false;
			}
			return true;
		}

		public String getName()
		{
			return m_strName;
		}

		public Pattern(String strPattern)
		{
            super();
			this$0 = ConfigElement.this;
			int iSpace = strPattern.indexOf(' ');
			if (iSpace != -1)
				throw new IllegalArgumentException("Invalid Argument 'pattern': 'element-name[attribute-name=attribute-value]'");
			int iOpenBracket = strPattern.indexOf('[');
			if (iOpenBracket != -1)
			{
				int iEquals = strPattern.indexOf('=', iOpenBracket);
				if (iEquals == -1)
					throw new IllegalArgumentException("Invalid Argument 'pattern': Missing '=' in 'element-name[attribute-name=attribute-value]'");
				int iCloseBracket = strPattern.indexOf(']', iEquals);
				if (iCloseBracket == -1)
					throw new IllegalArgumentException("Invalid Argument 'pattern': Missing ']' in 'element-name[attribute-name=attribute-value]'");
				m_strAttributeName = strPattern.substring(iOpenBracket + 1, iEquals);
				if (m_strAttributeName == null || m_strAttributeName.length() == 0)
					throw new IllegalArgumentException("Invalid Argument 'pattern': Missing attribute-name in 'element-name[attribute-name=attribute-value]'");
				m_strAttributeValue = strPattern.substring(iEquals + 1, iCloseBracket);
				if (m_strAttributeValue == null || m_strAttributeValue.length() == 0)
					throw new IllegalArgumentException("Invalid Argument 'pattern': Missing attribute-value in 'element-name[attribute-name=attribute-value]'");
				m_strName = strPattern.substring(0, iOpenBracket);
			} else
			{
				m_strName = strPattern;
			}
			if (m_strName == null || m_strName.length() == 0)
				throw new IllegalArgumentException("Invalid Argument 'pattern': Missing element-name in 'element-name[attribute-name=attribute-value]'");
			else
				return;
		}
	}


	private static final String NLSID = "nlsid";
	private static final String NLSBUNDLE = "nlsbundle";
	private static final String NLSCLASS = "nlsclass";
	private static final String FILTER = "filter";
	private static final String INSERT = "insert";
	private static final String INSERTAFTER = "insertafter";
	private static final String INSERTBEFORE = "insertbefore";
	private static final String REMOVE = "remove";
	private static final String REPLACE = "replace";
	private static final String PATH = "path";
	private static ConfigService.LookupFilter SOLE_SCOPELESS_ELEMENT_FILTER = null;
	private static ConfigService.LookupFilter MULTIPLE_SCOPELESS_ELEMENT_FILTER = null;
	private static final ConfigElement SOLE_ELEMENT_MARKER = new ConfigElement("SoleElementMarker", ((String) (null)));
	private static final ConfigElement MULTIPLE_ELEMENT_MARKER = new ConfigElement("MultipleElementMarker", ((String) (null)));
	private String m_strName;
	private String m_strValue;
	private String m_strNLSid;
	private IConfigElement m_parent;
	private AppLevelElement m_originalParent;
	private ArrayList m_children;
	private HashMap m_uniqueChildren;
	private ArrayList m_filters;

	public String getName()
	{
		return m_strName;
	}

	public IConfigElement getParent()
	{
		return m_parent;
	}

	public String getValue()
	{
		String strValue = null;
		if (m_strNLSid != null)
		{
			strValue = m_strValue;
		}
		if (strValue == null)
			strValue = "";
		return strValue;
	}

	public Boolean getValueAsBoolean()
	{
		if (m_strValue != null)
		{
			if (m_strValue.equalsIgnoreCase("true"))
				return Boolean.TRUE;
			if (m_strValue.equalsIgnoreCase("false"))
				return Boolean.FALSE;
		}
		return null;
	}

	public Integer getValueAsInteger()
	{
		if (m_strValue != null)
			return new Integer(m_strValue);
		else
			return null;
	}

	public String getAttributeValue(String strAttributeName)
	{
//		if (m_attributes != null)
//			return m_attributes.get(strAttributeName);
//		else
//			return null;
        return null;
	}

	public Boolean getAttributeValueAsBoolean(String strAttributeName)
	{
//		if (m_attributes != null)
//		{
//			String strValue = m_attributes.get(strAttributeName);
//			if (strValue != null)
//			{
//				if (strValue.equalsIgnoreCase("true"))
//					return Boolean.TRUE;
//				if (strValue.equalsIgnoreCase("false"))
//					return Boolean.FALSE;
//			}
//		}
//		return null;
        return null;
	}

	public Integer getAttributeValueAsInteger(String strAttributeName)
	{
//		if (m_attributes != null)
//		{
//			String strValue = m_attributes.get(strAttributeName);
//			if (strValue != null)
//				return new Integer(strValue);
//		}
//		return null;
        return null;
	}

	public Iterator getAttributeNames()
	{
//		if (m_attributes != null)
//			return m_attributes.nameIterator();
//		else
//			return (new ArrayList()).iterator();
        return (new ArrayList()).iterator();
	}

	public void setAttributeValue(String strAttributeName, String strValue)
	{
//		if (m_attributes == null)
//			m_attributes = new ArgumentList();
//		m_attributes.replace(strAttributeName, strValue);
	}

	public void setAttributeValue(String strAttributeName, boolean bValue)
	{
//		if (m_attributes == null)
//			m_attributes = new ArgumentList();
//		m_attributes.replace(strAttributeName, bValue ? "true" : "false");
	}

	public void setAttributeValue(String strAttributeName, int iValue)
	{
//		if (m_attributes == null)
//			m_attributes = new ArgumentList();
//		m_attributes.replace(strAttributeName, Integer.toString(iValue));
	}

	public Iterator getChildElements()
	{
		return getChildElements(false);
	}

	Iterator getChildElements(boolean unfiltered)
	{
		if (m_children != null)
		{
			ArrayList childElements = new ArrayList(m_children.size());
			for (int iChild = 0; iChild < m_children.size(); iChild++)
			{
				ConfigElement childElement = (ConfigElement)m_children.get(iChild);
				if (unfiltered || isVisible(childElement))
					childElements.add(childElement);
			}

			return childElements.iterator();
		} else
		{
			return Collections.emptyList().iterator();
		}
	}

	public Iterator getChildElements(String strPattern)
	{
		ArrayList childElements = new ArrayList(1);
		Pattern pattern = new Pattern(strPattern);
		if (m_uniqueChildren != null)
		{
			IConfigElement childElement = (IConfigElement)m_uniqueChildren.get(pattern.getName());
			if (childElement != null && pattern.matches(childElement) && isVisible(childElement))
				childElements.add(childElement);
		}
		if (m_children != null && childElements.size() == 0)
		{
			for (int iChildElement = 0; iChildElement < m_children.size(); iChildElement++)
			{
				IConfigElement childElement = (IConfigElement)m_children.get(iChildElement);
				if (pattern.matches(childElement) && isVisible(childElement))
					childElements.add(childElement);
			}

		}
		return childElements.iterator();
	}

	public IConfigElement getChildElement(String strPattern)
	{
		IConfigElement uniqueChildElement = null;
		Pattern pattern = new Pattern(strPattern);
		if (m_uniqueChildren != null)
		{
			IConfigElement childElement = (IConfigElement)m_uniqueChildren.get(pattern.getName());
			if (childElement != null && pattern.matches(childElement) && isVisible(childElement))
				uniqueChildElement = childElement;
		}
		if (uniqueChildElement == null && m_children != null)
		{
			IConfigElement firstVisible = null;
			List visibleChildElements = null;
			for (int iChildElement = 0; iChildElement < m_children.size(); iChildElement++)
			{
				IConfigElement childElement = (IConfigElement)m_children.get(iChildElement);
				if (!pattern.matches(childElement) || !isVisible(childElement))
					continue;
				if (firstVisible == null)
				{
					firstVisible = childElement;
					continue;
				}
				if (visibleChildElements == null)
				{
					visibleChildElements = new ArrayList();
					visibleChildElements.add(firstVisible);
				}
				visibleChildElements.add(childElement);
			}

			if (visibleChildElements != null)
				uniqueChildElement = getMostRelevantVisible(visibleChildElements);
			else
				uniqueChildElement = firstVisible;
		}
		return uniqueChildElement;
	}

	public String getChildValue(String strPattern)
	{
		IConfigElement element = getChildElement(strPattern);
		if (element != null)
			return element.getValue();
		else
			return null;
	}

	public Boolean getChildValueAsBoolean(String strPattern)
	{
		IConfigElement element = getChildElement(strPattern);
		if (element != null)
			return element.getValueAsBoolean();
		else
			return null;
	}

	public Integer getChildValueAsInteger(String strPattern)
	{
		IConfigElement element = getChildElement(strPattern);
		if (element != null)
			return element.getValueAsInteger();
		else
			return null;
	}

	public IConfigElement getDescendantElement(String strPatternPath)
	{
		IConfigElement configElement = this;
		for (StringTokenizer elementTokenizer = new StringTokenizer(strPatternPath, "."); elementTokenizer.hasMoreElements() && configElement != null; configElement = configElement.getChildElement(elementTokenizer.nextToken()));
		return configElement;
	}

	public String getDescendantValue(String strPatternPath)
	{
		IConfigElement element = getDescendantElement(strPatternPath);
		if (element != null)
			return element.getValue();
		else
			return null;
	}

	public Boolean getDescendantValueAsBoolean(String strPatternPath)
	{
		IConfigElement element = getDescendantElement(strPatternPath);
		if (element != null)
			return element.getValueAsBoolean();
		else
			return null;
	}

	public Integer getDescendantValueAsInteger(String strPatternPath)
	{
		IConfigElement element = getDescendantElement(strPatternPath);
		if (element != null)
			return element.getValueAsInteger();
		else
			return null;
	}

	public void setParent(IConfigElement parent)
	{
		if (m_parent != parent)
		{
			m_parent = parent;
			if (m_parent != null)
				m_parent.addChildElement(this);
		}
	}

	public void addChildElement(IConfigElement childElement)
	{
		if (childElement.getParent() != this)
		{
			if (m_children == null)
				m_children = new ArrayList(5);
			m_children.add(childElement);
			updateUniqueChildren();
			childElement.setParent(this);
		}
	}

	public String toString()
	{
		StringBuffer strBuff = new StringBuffer(128);
		if (m_originalParent != null && (m_originalParent.getElement() instanceof ConfigElement))
		{
			ConfigElement configElement = (ConfigElement)m_originalParent.getElement();
			strBuff.append(configElement.toString());
			strBuff.append('.');
		} else
		if (m_parent != null && (m_parent instanceof ConfigElement))
		{
			ConfigElement configElement = (ConfigElement)m_parent;
			strBuff.append(configElement.toString());
			strBuff.append('.');
		}
		strBuff.append(m_strName);
		boolean bHasArguments = false;
		String strAttributeValue;
		for (Iterator strAttributeNames = getAttributeNames(); strAttributeNames.hasNext(); strBuff.append(strAttributeValue))
		{
			if (!bHasArguments)
			{
				strBuff.append('[');
				bHasArguments = true;
			} else
			{
				strBuff.append(',');
			}
			String strAttributeName = (String)strAttributeNames.next();
			strAttributeValue = getAttributeValue(strAttributeName);
			strBuff.append(strAttributeName);
			strBuff.append('=');
		}

		if (bHasArguments)
			strBuff.append(']');
		return strBuff.toString();
	}

	public ConfigElement(String strName, String strValue)
	{
		m_filters = null;
		m_strName = strName;
		m_strValue = strValue;
		if (m_strValue != null && m_strValue.length() == 0)
			m_strValue = null;
	}

	public ConfigElement(Element domElement, ConfigElement parent)
	{
		m_filters = null;
		m_parent = parent;
		m_strName = domElement.getTagName();
		Node node = domElement.getFirstChild();
		if (node != null && node.getNodeType() == 3)
		{
			m_strValue = node.getNodeValue();
			if (m_strValue != null)
			{
				m_strValue = m_strValue.trim();
				if (m_strValue.length() == 0)
					m_strValue = null;
			}
		}
		if (m_strName.equals("nlsid"))
		{
			if (parent == null)
				throw new IllegalStateException("Invalid placement of nlsclass tag");
			parent.m_strNLSid = m_strValue;
		} else
		if (m_strName.equals("nlsbundle"))
		{
			if (parent == null)
				throw new IllegalStateException("Invalid placement of nlsbundle tag");
//			parent.m_nlsClass = new NlsResourceBundle(m_strValue);
		} else
		if (m_strName.equals("nlsclass"))
		{
			if (parent == null)
				throw new IllegalStateException("Invalid placement of nlsbundle tag");
			try
			{
				Class aClass = Class.forName(m_strValue);
//				parent.m_nlsClass = (NlsResourceClass)aClass.newInstance();
			}
			catch (ClassNotFoundException e)
			{
				throw new WrapperRuntimeException((new StringBuilder()).append("Nls class not found: ").append(m_strValue).toString(), e);
			}
		}
		NamedNodeMap domAttributes = domElement.getAttributes();
		for (int iAttribute = 0; iAttribute < domAttributes.getLength(); iAttribute++)
		{
//			if (m_attributes == null)
//				m_attributes = new ArgumentList();
			Node attributeNode = domAttributes.item(iAttribute);
			String strAttributeName = attributeNode.getNodeName();
			String strAttributeValue = attributeNode.getNodeValue();
//			m_attributes.add(strAttributeName, strAttributeValue);
		}

		ConfigService.LookupFilter filter = null;
		if (m_strName.equals("filter") && (parent instanceof ConfigElement))
			filter = ConfigService.getLookupFilter(this, false);
		ConfigElement parentElement = this;
		if (filter != null)
			parentElement = parent;
		NodeList domChildren = domElement.getChildNodes();
		if (domChildren != null)
		{
			int numChildNodes = domChildren.getLength();
			for (int iNode = 0; iNode < numChildNodes; iNode++)
			{
				Node domChildNode = domChildren.item(iNode);
				if (domChildNode.getNodeType() != 1)
					continue;
				Element domChildElement = (Element)domChildNode;
				ConfigElement childElement = new ConfigElement(domChildElement, parentElement);
				if (!"filter".equals(domChildElement.getTagName()))
				{
					if (parentElement.m_children == null)
						parentElement.m_children = new ArrayList(5);
					parentElement.m_children.add(childElement);
				}
				if (filter == null)
					continue;
				if (childElement.m_filters == null)
					childElement.m_filters = new ArrayList(1);
				childElement.m_filters.add(filter);
			}

			parentElement.updateUniqueChildren();
		}
	}

	public boolean hasFilters()
	{
		return (m_filters == null || m_filters.size() == 0 ? Boolean.FALSE : Boolean.TRUE).booleanValue();
	}

	Set applyModification(ConfigElement primaryModification, int appLevel)
	{
		Set setTarget = new HashSet();
		if (Trace.CONFIGMODIFICATION)
		{
			Trace.println((new StringBuilder()).append("apply modification of app level ").append(appLevel).append(": ").append(primaryModification).toString());
			Trace.println((new StringBuilder()).append("to primary element: ").append(this).toString());
		}
		if (primaryModification.m_children == null || primaryModification.m_children.size() == 0)
			return setTarget;
		ConfigElement modificationScope = (ConfigElement)primaryModification.m_parent;
		Iterator i$ = primaryModification.m_children.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			Object element = i$.next();
			ConfigElement modificationAction = (ConfigElement)element;
			String action = modificationAction.getName();
			String path = modificationAction.getAttributeValue("path");
			if ((path == null || path.length() == 0) && !action.equals("insert"))
				throw new IllegalStateException((new StringBuilder()).append("path is required for modification action: ").append(modificationAction).toString());
			ConfigElement targetElement;
			if (path == null || path.length() == 0)
				targetElement = this;
			else
				try
				{
					targetElement = getModificationTarget(path, appLevel);
					if (targetElement == null)
						throw new IllegalStateException((new StringBuilder()).append("Can't locate a unique child element with path: ").append(path).append(" in: ").append(this).append(".  \nThe path is specified by: ").append(modificationAction).toString());
				}
				catch (IndistinguishableModificationTargetException ex)
				{
					throw new IllegalStateException((new StringBuilder()).append("There are more than one child elements with path: ").append(path).append(".  ").append(ex.getMessage()).append(".  \nThe path is specified by: ").append(modificationAction).toString());
				}
			setTarget.add(targetElement);
			ConfigElement traceElement = null;
			if (Trace.CONFIGMODIFICATION)
			{
				Trace.println((new StringBuilder()).append("modification action: ").append(toShortString(modificationAction)).toString());
				Trace.println(">>> original target element: ");
				if (action.equals("insert"))
					traceElement = targetElement;
				else
					traceElement = (ConfigElement)targetElement.m_parent;
				traceModificationTarget(traceElement);
			}
			if (action.equals("insert"))
				targetElement.addModificationAsChild(modificationAction, modificationScope, -1, appLevel);
			else
			if (action.equals("insertafter"))
				targetElement.addModificationAsSibling(modificationAction, modificationScope, true, appLevel);
			else
			if (action.equals("insertbefore"))
				targetElement.addModificationAsSibling(modificationAction, modificationScope, false, appLevel);
			else
			if (action.equals("remove"))
				targetElement.applyRemoveModification(modificationAction, modificationScope, appLevel);
			else
			if (action.equals("replace"))
			{
				targetElement.applyRemoveModification(modificationAction, modificationScope, appLevel);
				targetElement.addModificationAsSibling(modificationAction, modificationScope, true, appLevel);
			} else
			{
				throw new IllegalStateException((new StringBuilder()).append("Unsupported modification action: ").append(modificationAction).toString());
			}
			if (Trace.CONFIGMODIFICATION)
			{
				Trace.println("<<< modified target element: ");
				traceModificationTarget(traceElement);
			}
		} while (true);
		updateUniqueChildren();
		if (modificationScope.m_children != null)
		{
			modificationScope.m_children.remove(primaryModification);
			modificationScope.updateUniqueChildren();
		}
		return setTarget;
	}

	IConfigElement getOriginalParent()
	{
		IConfigElement originalParent = null;
		if (m_originalParent != null)
			originalParent = m_originalParent.getElement();
		return originalParent;
	}

	private void applyRemoveModification(ConfigElement modificationAction, ConfigElement modificationScope, int appLevel)
	{
		if (modificationAction.getName().equals("remove") && modificationAction.m_children != null && modificationAction.m_children.size() > 0)
			throw new IllegalStateException((new StringBuilder()).append("remove modification is not allowed to have sub elements: ").append(modificationAction).toString());
		ConfigElement filteredElement = new ConfigElement("filter", ((String) (null)));
		filteredElement.m_parent = m_parent;
		filteredElement.m_originalParent = new AppLevelElement(modificationAction, appLevel);
		filteredElement.m_children = null;
//		filteredElement.m_attributes = new ArgumentList(modificationScope.m_attributes);
//		filteredElement.m_attributes.remove("application");
//		String strVersion = filteredElement.m_attributes.get("version");
//		if ("latest".equals(strVersion))
//			filteredElement.m_attributes.remove("version");
		ConfigService.LookupFilter filter = ConfigService.getLookupFilter(filteredElement, true);
		if (m_filters == null)
			m_filters = new ArrayList(1);
		m_filters.add(filter);
	}

	private void addModificationAsSibling(ConfigElement modificationAction, ConfigElement modificationScope, boolean afterMe, int appLevel)
	{
		if (m_parent == null)
			throw new IllegalStateException((new StringBuilder()).append("Cannot locate parent of modification target: ").append(this).toString());
		ConfigElement ourParent = (ConfigElement)m_parent;
		int index = ourParent.m_children.indexOf(this);
		if (afterMe)
			index++;
		ourParent.addModificationAsChild(modificationAction, modificationScope, index, appLevel);
	}

	private void addModificationAsChild(ConfigElement modificationAction, ConfigElement modificationScope, int index, int appLevel)
	{
		if (modificationAction.m_children == null || modificationAction.m_children.size() == 0)
			return;
		int numChildren = m_children == null ? 0 : m_children.size();
		if (index != -1 && index > numChildren)
			throw new IllegalStateException((new StringBuilder()).append("illegal index ").append(index).append(" is specified for adding modification: ").append(modificationAction).toString());
		ConfigElement filteredElement = new ConfigElement("filter", ((String) (null)));
		filteredElement.m_parent = this;
		filteredElement.m_originalParent = new AppLevelElement(modificationAction, appLevel);
		filteredElement.m_children = null;
//		filteredElement.m_attributes = new ArgumentList(modificationScope.m_attributes);
//		filteredElement.m_attributes.remove("application");
//		String strVersion = filteredElement.m_attributes.get("version");
//		if ("latest".equals(strVersion))
//			filteredElement.m_attributes.remove("version");
		ConfigService.LookupFilter filter = ConfigService.getLookupFilter(filteredElement, false);
		ConfigElement addItem;
		for (Iterator i$ = modificationAction.m_children.iterator(); i$.hasNext(); addItem.m_filters.add(filter))
		{
			Object element = i$.next();
			addItem = (ConfigElement)element;
			String elemName = addItem.getName();
//			if (elemName.equals("nlsbundle") || elemName.equals("nlsclass"))
//				m_nlsClass = addItem.getNlsClass();
			addItem.m_originalParent = new AppLevelElement(addItem.m_parent, appLevel);
			addItem.m_parent = this;
			if (addItem.m_filters == null)
				addItem.m_filters = new ArrayList(1);
		}

		if (m_children == null)
			m_children = new ArrayList(5);
		if (index == -1)
			m_children.addAll(modificationAction.m_children);
		else
			m_children.addAll(index, modificationAction.m_children);
		updateUniqueChildren();
	}

	private ConfigElement getModificationTarget(String path, int appLevel)
		throws IndistinguishableModificationTargetException
	{
		ConfigElement target = this;
		for (StringTokenizer elementTokenizer = new StringTokenizer(path, "."); elementTokenizer.hasMoreElements() && target != null; target = target.getModificationTargetChildElement(elementTokenizer.nextToken(), appLevel));
		return target;
	}

	private ConfigElement getModificationTargetChildElement(String strPattern, int appLevel)
		throws IndistinguishableModificationTargetException
	{
		Pattern pattern = new Pattern(strPattern);
		if (m_uniqueChildren != null)
		{
			ConfigElement childElement = (ConfigElement)m_uniqueChildren.get(pattern.getName());
			if (childElement != null && (childElement.m_originalParent == null || childElement.m_originalParent.getAppLevel() < appLevel) && pattern.matches(childElement))
				return childElement;
		}
		ConfigElement uniqueChildElement = null;
		if (m_children != null)
		{
			ConfigElement nonModificationChild = null;
			ConfigElement modificationChild1 = null;
			ConfigElement modificationChild2 = null;
			for (int iChildElement = 0; iChildElement < m_children.size(); iChildElement++)
			{
				ConfigElement childElement = (ConfigElement)m_children.get(iChildElement);
				if (childElement.m_originalParent != null && childElement.m_originalParent.getAppLevel() >= appLevel || !pattern.matches(childElement))
					continue;
				if (childElement.m_originalParent == null)
				{
					if (nonModificationChild == null)
					{
						nonModificationChild = childElement;
					} else
					{
						String msg = (new StringBuilder()).append("Indistinguishable modification targets - \nelement 1: ").append(nonModificationChild).append("; \nelement 2: ").append(childElement).toString();
						throw new IndistinguishableModificationTargetException(msg);
					}
					continue;
				}
				if (modificationChild1 == null)
				{
					modificationChild1 = childElement;
					continue;
				}
				if (modificationChild2 == null)
					modificationChild2 = childElement;
			}

			if (nonModificationChild != null)
			{
				uniqueChildElement = nonModificationChild;
			} else
			{
				if (modificationChild1 != null && modificationChild2 != null)
				{
					String msg = (new StringBuilder()).append("Indistinguishable modification targets - \nelement 1: ").append(modificationChild1).append("; \nelement 2: ").append(modificationChild2).toString();
					throw new IndistinguishableModificationTargetException(msg);
				}
				uniqueChildElement = modificationChild1;
			}
		}
		return uniqueChildElement;
	}

	private static String toShortString(ConfigElement element)
	{
		String strConfigElement = element.toString();
		int beginInx = strConfigElement.indexOf(".scope");
		if (beginInx != -1)
		{
			strConfigElement = strConfigElement.substring(beginInx + 1);
			beginInx = strConfigElement.indexOf("].");
			if (beginInx != -1)
				strConfigElement = strConfigElement.substring(beginInx + 2);
		}
		return strConfigElement;
	}

	private static void traceModificationTarget(ConfigElement target)
	{
		Trace.println(toShortString(target));
		if (target.m_children == null || target.m_children.size() == 0)
		{
			Trace.println("no children");
		} else
		{
			StringBuffer strBuff = new StringBuffer(128);
			String filterBeginStr = ".filter";
			String negateStr = "negate-";
			Iterator i$ = target.m_children.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				Object element = i$.next();
				ConfigElement childElement = (ConfigElement)element;
				strBuff.append("\n   child: ");
				if (childElement.m_filters != null && childElement.m_filters.size() > 0)
				{
					boolean appendComma = false;
					for (Iterator a$ = childElement.m_filters.iterator(); a$.hasNext();)
					{
						Object obj = a$.next();
						if (appendComma)
							strBuff.append(", ");
						ConfigService.LookupFilter filter = (ConfigService.LookupFilter)obj;
						String filterToString = filter.toString();
						int startInx = filterToString.indexOf(filterBeginStr);
						if (startInx != -1)
						{
							boolean isNegate = filterToString.indexOf(negateStr) != -1;
							filterToString = filterToString.substring(startInx + 1);
							if (isNegate)
								filterToString = (new StringBuilder()).append(negateStr).append(filterToString).toString();
						}
						strBuff.append(filterToString);
						appendComma = true;
					}

					strBuff.append(" ");
				}
				strBuff.append(childElement.m_strName);
				boolean bHasArguments = false;
				String strAttributeValue;
				for (Iterator strAttributeNames = childElement.getAttributeNames(); strAttributeNames.hasNext(); strBuff.append(strAttributeValue))
				{
					if (!bHasArguments)
					{
						strBuff.append('[');
						bHasArguments = true;
					} else
					{
						strBuff.append(',');
					}
					String strAttributeName = (String)strAttributeNames.next();
					strAttributeValue = childElement.getAttributeValue(strAttributeName);
					strBuff.append(strAttributeName);
					strBuff.append('=');
				}

				if (bHasArguments)
					strBuff.append(']');
			} while (true);
			Trace.println(strBuff.toString());
		}
	}

//	private NlsResourceClass getNlsClass()
//	{
//		if (m_nlsClass == null)
//		{
//			NlsResourceClass nlsClass = null;
//			for (ConfigElement element = this; element != null; element = (ConfigElement)element.m_parent)
//			{
//				if (element.m_nlsClass != null)
//				{
//					nlsClass = element.m_nlsClass;
//					break;
//				}
//				if (element.m_parent == null || !element.m_parent.getName().equals("scope"))
//					continue;
//				IConfigElement extendsElement = element.getExtendedElement();
//				if (extendsElement == null || !(extendsElement instanceof ConfigElement))
//					continue;
//				nlsClass = ((ConfigElement)extendsElement).getNlsClass();
//				if (nlsClass != null)
//					break;
//			}
//
//			m_nlsClass = nlsClass;
//		}
//		return m_nlsClass;
//	}

	private IConfigElement getExtendedElement()
	{
		return ConfigService.getExtendedElement(this);
	}

	private boolean isVisible(IConfigElement element)
	{
		if (element instanceof ConfigElement)
		{
			ConfigElement configElement = (ConfigElement)element;
			if (configElement.m_filters != null)
			{
				for (int iFilter = 0; iFilter < configElement.m_filters.size(); iFilter++)
				{
					ConfigService.LookupFilter filter = (ConfigService.LookupFilter)configElement.m_filters.get(iFilter);
					if (!filter.showContents())
						return false;
				}

			}
		}
		return true;
	}

	private IConfigElement getMostRelevantVisible(List visibleElements)
	{
		Map filterToElementMap = new HashMap();
		boolean hasValidFilter = false;
		boolean foundSoleFilterlessElement = false;
		Iterator i$ = visibleElements.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			IConfigElement element = (IConfigElement)i$.next();
			if (!(element instanceof ConfigElement))
				return null;
			boolean filterAdded = false;
			ConfigElement configElement = (ConfigElement)element;
			if (configElement.m_filters != null)
			{
				for (int iFilter = 0; iFilter < configElement.m_filters.size(); iFilter++)
				{
					ConfigService.LookupFilter filter = (ConfigService.LookupFilter)configElement.m_filters.get(iFilter);
					if (!filter.isNegated())
					{
						filterAdded = true;
						filterToElementMap.put(filter, element);
					}
				}

			}
			if (filterAdded)
				hasValidFilter = true;
			else
			if (!foundSoleFilterlessElement)
			{
				foundSoleFilterlessElement = true;
				ConfigService.LookupFilter soleScopelessFilter = getScopelessFilter(true);
				filterToElementMap.put(soleScopelessFilter, configElement);
			} else
			{
				ConfigService.LookupFilter multipleScopelessFilter = getScopelessFilter(false);
				if (!filterToElementMap.containsKey(multipleScopelessFilter))
				{
					ConfigService.LookupFilter soleScopelessFilter = getScopelessFilter(true);
					filterToElementMap.remove(soleScopelessFilter);
					filterToElementMap.put(multipleScopelessFilter, MULTIPLE_ELEMENT_MARKER);
				}
			}
		} while (true);
		IConfigElement moreRelevant;
		if (!hasValidFilter)
		{
			moreRelevant = null;
		} else
		{
			IConfigElement configElement = ConfigService.getMostRelevantFilterElement(filterToElementMap);
			if (configElement == MULTIPLE_ELEMENT_MARKER)
				moreRelevant = null;
			else
				moreRelevant = configElement;
		}
		return moreRelevant;
	}

	private void updateUniqueChildren()
	{
		HashMap nonUniqueChildren = new HashMap(17);
		if (m_uniqueChildren != null)
			m_uniqueChildren = null;
		if (m_children != null)
		{
			for (int iChild = 0; iChild < m_children.size(); iChild++)
			{
				IConfigElement childElement = (IConfigElement)m_children.get(iChild);
				String strChildElementName = childElement.getName();
				if (m_uniqueChildren == null)
					m_uniqueChildren = new HashMap(5);
				if (nonUniqueChildren.get(strChildElementName) != null)
					continue;
				if (m_uniqueChildren.get(strChildElementName) == null)
				{
					m_uniqueChildren.put(strChildElementName, childElement);
				} else
				{
					m_uniqueChildren.remove(strChildElementName);
					nonUniqueChildren.put(strChildElementName, "");
				}
			}

		}
	}

	private static synchronized ConfigService.LookupFilter getScopelessFilter(boolean isSoleElement)
	{
		if (SOLE_SCOPELESS_ELEMENT_FILTER == null)
		{
			ConfigElement filteredElement = SOLE_ELEMENT_MARKER;
			filteredElement.m_children = null;
//			filteredElement.m_attributes = new ArgumentList();
			SOLE_SCOPELESS_ELEMENT_FILTER = ConfigService.getLookupFilter(filteredElement, false);
			filteredElement = MULTIPLE_ELEMENT_MARKER;
			filteredElement.m_children = null;
//			filteredElement.m_attributes = new ArgumentList();
			MULTIPLE_SCOPELESS_ELEMENT_FILTER = ConfigService.getLookupFilter(filteredElement, false);
		}
		if (isSoleElement)
			return SOLE_SCOPELESS_ELEMENT_FILTER;
		else
			return MULTIPLE_SCOPELESS_ELEMENT_FILTER;
	}

}
