/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.entando.apsadmin.system.services.shortcut;


import org.entando.entando.ent.exception.EntException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.List;

/**
 * @author E.Santoboni
 */
public class UserShortcutConfigDOM {

	private static final Logger _logger = LoggerFactory.getLogger(UserShortcutConfigDOM.class);
	
	protected static String createUserConfigXml(String[] config) throws EntException {
		XMLOutputter out = new XMLOutputter();
		Document document = new Document();
		try {
			Element rootElement = new Element(ROOT_ELEMENT_NAME);
			document.setRootElement(rootElement);
			for (int i = 0; i < config.length; i++) {
				String shortcut = config[i];
				if (null != shortcut) {
					Element element = new Element(BOX_ELEMENT_NAME);
					element.setAttribute(POS_ATTRIBUTE_NAME, String.valueOf(i));
					element.setText(shortcut);
					rootElement.addContent(element);
				}
			}
		} catch (Throwable t) {
			_logger.error("Error parsing user config", t);
			//ApsSystemUtils.logThrowable(t, UserShortcutConfigDOM.class, "extractUserShortcutConfig");
			throw new EntException("Error parsing user config", t);
		}
		Format format = Format.getPrettyFormat();
		format.setIndent("\t");
		out.setFormat(format);
		return out.outputString(document);
	}
	
	protected static String[] extractUserConfig(String xml, Integer definiteSize) throws EntException {
		String[] config = new String[definiteSize];
		try {
			if (null == xml || xml.trim().length() == 0) return config;
			Document doc = decodeDOM(xml);
			List<Element> elements = doc.getRootElement().getChildren(BOX_ELEMENT_NAME);
			for (int i=0; i<elements.size(); i++) {
				if (i > definiteSize) break;
				Element element = elements.get(i);
				Integer pos = null;
				try {
					pos = Integer.parseInt(element.getAttributeValue(POS_ATTRIBUTE_NAME));
				} catch (Exception e) {
					//nothing to catch
				}
				String shortcutCode = element.getText();
				if (null != pos) {
					config[pos] = shortcutCode;
				}
			}
		} catch (Throwable t) {
			_logger.error("Error parsing user config", t);
			//ApsSystemUtils.logThrowable(t, UserShortcutConfigDOM.class, "extractUserShortcutConfig");
			throw new EntException("Error parsing user config", t);
		}
		return config;
	}
	
	private static Document decodeDOM(String xmlText) throws EntException {
		Document doc = null;
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		StringReader reader = new StringReader(xmlText);
		try {
			doc = builder.build(reader);
		} catch (Throwable t) {
			_logger.error("Error while parsing xml: {}", xmlText, t);
			throw new EntException("Error detected while parsing the XML", t);
		}
		return doc;
	}
	
	public static final String ROOT_ELEMENT_NAME = "shortcuts";
	public static final String BOX_ELEMENT_NAME = "box";
	public static final String POS_ATTRIBUTE_NAME = "pos";
	
}
