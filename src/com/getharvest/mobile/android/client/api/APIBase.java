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
package com.getharvest.mobile.android.client.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import biz.source_code.base64Coder.Base64Coder;

import com.getharvest.mobile.android.client.LoginActivity;

import android.os.Handler;
import android.util.Log;


public abstract class APIBase implements Runnable {
	
	private HttpURLConnection http;
	private Handler handler;
	private APIListener listener;
	
	protected static enum Method { GET, POST };
	
	protected APIBase(APIListener l) {
		handler = new Handler();
		listener = l;
	}
	
	protected void setup() {
		try {
			URL server = new URL("http://" + LoginActivity.url + ".harvestapp.com" + getUrl());
			Log.d(getClass().getCanonicalName(), "URL=" + server.toString());
			http = (HttpURLConnection)server.openConnection();
			if (method() == Method.GET)
				http.setRequestMethod("GET");
			if (method() == Method.POST)
				http.setRequestMethod("POST");
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setReadTimeout(4000);
			http.setRequestProperty("Accept", "application/xml");
			http.setRequestProperty("Content-Type", "application/xml");
			http.setRequestProperty("Authorization", "Basic " + Base64Coder.encodeString(
					LoginActivity.email + ":" + LoginActivity.password));
			http.setRequestProperty("User-Agent", "");
			http.getOutputStream().write(sendBytes());
		} catch (MalformedURLException e) {
			Log.e(getClass().getCanonicalName(), e.getMessage());
			throw new RuntimeException(e);
		} catch (IOException e) {
			Log.e(getClass().getCanonicalName(), e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	public void run() {
		try {
			http.connect();
			http.getOutputStream().close();
			int code = http.getResponseCode();
			if (code >= 200 && code < 300) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				boolean test = false;
				if (test) {
					Log.e("Mark","printing input stream");
					BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
					String line = null;
					while((line = in.readLine()) != null) {
						System.err.println(line);
					}
					Log.e("Mark","done printing input stream");
				}
				Document dom = builder.parse(http.getInputStream());
				parseDocument(dom);
				final APIBase base = this;
				handler.post(new Runnable() {
					@Override public void run() {
						listener.connectSuccess(base);
					}
				});
				return;
			} else
				Log.e(getClass().getCanonicalName(), "Could not connect to server");
		} catch (IOException e) {
			Log.e(getClass().getCanonicalName(), e.getMessage());
		} catch (ParserConfigurationException e) {
			Log.e(getClass().getCanonicalName(), e.getMessage());
		} catch (SAXException e) {
			Log.e(getClass().getCanonicalName(), e.getMessage());
		}
		handler.post(new Runnable() {
			@Override public void run() {
				listener.connectFailure();
			}
		});
	}
	
	abstract protected Method method();
	abstract protected String getUrl();
	abstract protected byte[] sendBytes();
	abstract protected void parseDocument(Document dom);
	
	protected String getNodeText(Node n) {
		NodeList nl = n.getChildNodes();
		String ret = new String();
		for (int i=0; i<nl.getLength(); i++) {
			Node tn = nl.item(i);
			if (tn.getNodeType() == Node.TEXT_NODE) {
				ret += tn.getNodeValue();
			}
		}
		return ret;
	}
	
}

