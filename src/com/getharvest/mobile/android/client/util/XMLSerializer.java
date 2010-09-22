/*
    This file is part of Harvest Android Client.

    Harvest Android Client is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Harvest Android Client is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Harvest Android Client.  If not, see <http://www.gnu.org/licenses/>.
    
    Copyright (c) 2010 Mark Jackson <mdj at educomgov.org>
*/
package com.getharvest.mobile.android.client.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLSerializer {

	public static String getStringFromNode(Node root) {
		StringBuilder result = new StringBuilder();

		if (root.getNodeType() == Node.TEXT_NODE)
			result.append(root.getNodeValue());
		else {
			if (root.getNodeType() != Node.DOCUMENT_NODE) {
				StringBuffer attrs = new StringBuffer();
				for (int k = 0; k < root.getAttributes().getLength(); ++k) {
					attrs.append(" ").append(
							root.getAttributes().item(k).getNodeName()).append(
							"=\"").append(
							root.getAttributes().item(k).getNodeValue())
							.append("\" ");
				}
				result.append("<").append(root.getNodeName()).append(" ")
						.append(attrs).append(">");
			} else {
				result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			}

			NodeList nodes = root.getChildNodes();
			for (int i = 0, j = nodes.getLength(); i < j; i++) {
				Node node = nodes.item(i);
				result.append(getStringFromNode(node));
			}

			if (root.getNodeType() != Node.DOCUMENT_NODE) {
				result.append("</").append(root.getNodeName()).append(">");
			}
		}
		return result.toString();
	}
	
}
