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

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.getharvest.mobile.android.client.data.DailyEntry;
import com.getharvest.mobile.android.client.data.ProjectEntry;
import com.getharvest.mobile.android.client.data.TaskEntry;
import com.getharvest.mobile.android.client.util.XMLSerializer;

import android.util.Log;


public class APIDaily extends APIBase {
	
	protected int dayOfYear;
	protected int year;

	public String Date;
	public ArrayList<DailyEntry> entries = new ArrayList<DailyEntry>();
	public ArrayList<ProjectEntry> projects = new ArrayList<ProjectEntry>();
	
	protected APIDaily(APIListener l) {
		super(l);
	}
	
	public int getProjectIndex(String client, String name) {
		int index = 0;
		Iterator<ProjectEntry> iter = projects.iterator();
		while (iter.hasNext()) {
			if (iter.next().matches(client, name))
				return index;
			index++;
		}
		return -1;
	}
	
	public int getTaskIndex(int projectIndex, String task) {
		int index = 0;
		Iterator<TaskEntry> iter = projects.get(projectIndex).tasks.iterator();
		while (iter.hasNext()) {
			if (iter.next().matches(task))
				return index;
			index++;
		}
		return -1;
	}

	@Override
	public String getUrl() {
		if (year == 0)
			return "/daily";
		else
			return "/daily/" + dayOfYear + "/" + year;
	}

	@Override
	protected void parseDocument(Document dom) {
		Log.d(getClass().getCanonicalName(), XMLSerializer.getStringFromNode(dom));
		Element root = dom.getDocumentElement();
		Node n = root.getElementsByTagName("for_day").item(0);
		Date = getNodeText(n);
		Element dayEntries = (Element) root.getElementsByTagName("day_entries").item(0);
		NodeList nl = dayEntries.getElementsByTagName("day_entry");
		for (int i=0; i<nl.getLength(); i++) {
			n = nl.item(i);
			entries.add(processDayEntry((Element)n));
		}
		Element pe = (Element) root.getElementsByTagName("projects").item(0);
		nl = pe.getElementsByTagName("project");
		for (int i=0; i<nl.getLength(); i++) {
			n = nl.item(i);
			projects.add(processProjectEntry((Element)n));
		}
	}
	
	private DailyEntry processDayEntry(Element e) {
		DailyEntry de = new DailyEntry();
		de.id = Integer.parseInt(getNodeText(e.getElementsByTagName("id").item(0)));
		de.client = getNodeText(e.getElementsByTagName("client").item(0));
		de.project = getNodeText(e.getElementsByTagName("project").item(0));
		de.task = getNodeText(e.getElementsByTagName("task").item(0));
		de.comment = getNodeText(e.getElementsByTagName("notes").item(0));
		de.hours = Float.parseFloat(getNodeText(e.getElementsByTagName("hours").item(0)));
		NodeList nl = e.getElementsByTagName("timer_started_at");
		if (nl.getLength() > 0) 
			de.running = true;
		else
			de.running = false;
		return de;
	}
	
	private ProjectEntry processProjectEntry(Element e) {
		ProjectEntry pe = new ProjectEntry();
		pe.id = Integer.parseInt(getNodeText(e.getElementsByTagName("id").item(0)));
		pe.client = getNodeText(e.getElementsByTagName("client").item(0));
		pe.name = getNodeText(e.getElementsByTagName("name").item(0));
		Element taskEntries = (Element) e.getElementsByTagName("tasks").item(0);
		NodeList nl = taskEntries.getElementsByTagName("task");
		for (int i=0; i<nl.getLength(); i++) {
			Node n = nl.item(i);
			pe.tasks.add(processTaskEntry((Element)n));
		}
		return pe;
	}
	
	private TaskEntry processTaskEntry(Element e) {
		TaskEntry te = new TaskEntry();
		te.id = Integer.parseInt(getNodeText(e.getElementsByTagName("id").item(0)));
		te.name = getNodeText(e.getElementsByTagName("name").item(0));
		return te;
	}

	@Override
	protected Method method() {
		return Method.GET;
	}

	@Override
	protected byte[] sendBytes() {
		return new byte[0];
	}
	
}
