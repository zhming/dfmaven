// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ConfigFile.java

package com.gihow.dfc;

import com.documentum.fc.internal.xml.IXMLParserFactory;
import com.documentum.fc.internal.xml.XMLUtilsFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import java.io.InputStream;

// Referenced classes of package com.documentum.web.formext.config:
//			ConfigElement

public class ConfigFile extends ConfigElement
{
	private static class ErrorListener
		implements ErrorHandler
	{

		private StringBuffer m_strWarningErrorMessages;
		private int m_nNoOfErrors;
		private int m_nNoOfWarnings;

		public void error(SAXParseException e)
		{
			m_strWarningErrorMessages.append("XML parse error occurred at line ");
			m_strWarningErrorMessages.append(e.getLineNumber());
			m_strWarningErrorMessages.append(" column ");
			m_strWarningErrorMessages.append(e.getColumnNumber());
			m_strWarningErrorMessages.append(" - ");
			m_strWarningErrorMessages.append(e.toString());
			m_strWarningErrorMessages.append("\n");
			m_nNoOfErrors++;
		}

		public void fatalError(SAXParseException e)
		{
			m_strWarningErrorMessages.append("XML parse fatal error occurred at line ");
			m_strWarningErrorMessages.append(e.getLineNumber());
			m_strWarningErrorMessages.append(" column ");
			m_strWarningErrorMessages.append(e.getColumnNumber());
			m_strWarningErrorMessages.append(" - ");
			m_strWarningErrorMessages.append(e.toString());
			m_strWarningErrorMessages.append("\n");
			m_nNoOfErrors++;
		}

		public void warning(SAXParseException e)
		{
			m_strWarningErrorMessages.append("XML parse warning occurred at line ");
			m_strWarningErrorMessages.append(e.getLineNumber());
			m_strWarningErrorMessages.append(" column ");
			m_strWarningErrorMessages.append(e.getColumnNumber());
			m_strWarningErrorMessages.append(" - ");
			m_strWarningErrorMessages.append(e.toString());
			m_strWarningErrorMessages.append("\n");
			m_nNoOfWarnings++;
		}

		public int getNumberOfErrors()
		{
			return m_nNoOfErrors;
		}

		public int getNumberOfWarnings()
		{
			return m_nNoOfWarnings;
		}

		public String getWarningErrorMessages()
		{
			return m_strWarningErrorMessages.toString();
		}

		private ErrorListener()
		{
			m_strWarningErrorMessages = new StringBuffer(256);
			m_nNoOfErrors = 0;
			m_nNoOfWarnings = 0;
		}

	}


	private String m_strPathName;

	public ConfigFile(String strPathName, String strAppName)
	{
		super(loadXML(strPathName), null);
		m_strPathName = strPathName;
	}

	public String getPathName()
	{
		return m_strPathName;
	}

	public String toString()
	{
		StringBuffer strBuff = new StringBuffer(128);
		if (m_strPathName != null)
		{
			strBuff.append('(');
			strBuff.append(m_strPathName);
			strBuff.append(')');
		}
		strBuff.append(super.toString());
		return strBuff.toString();
	}

	private static Element loadXML(String strPathName)
	{
		Element domElement;
		InputStream xmlFileStream;
		Exception exception;
		domElement = null;
		xmlFileStream = ResourceFileUtil.getResourceAsStream(strPathName, null);
		if (xmlFileStream == null)
			throw new WrapperRuntimeException((new StringBuilder()).append("Can not find the config file: ").append(strPathName).toString());
		try
		{
			IXMLParserFactory utils = (new XMLUtilsFactory()).getParserFactory();
			DocumentBuilder xmlParser = utils.getDOMParser();
			ErrorListener errorListener = new ErrorListener();
			xmlParser.setErrorHandler(errorListener);
			InputSource xmlSource = new InputSource(xmlFileStream);
			Document document = xmlParser.parse(xmlSource);
			if (errorListener.getNumberOfErrors() > 0 || errorListener.getNumberOfWarnings() > 0)
				throw new IllegalStateException(errorListener.getWarningErrorMessages());
			domElement = document.getDocumentElement();
		}
		catch (SAXException se)
		{
			throw new WrapperRuntimeException((new StringBuilder()).append("SAXException parsing: ").append(strPathName).toString(), se);
		}
		catch (IOException ioe)
		{
			throw new WrapperRuntimeException((new StringBuilder()).append("IOException reading: ").append(strPathName).toString(), ioe);
		}
		finally { }
		try
		{
			xmlFileStream.close();
		}
		catch (IOException e) { }
		return domElement;
	}
}
