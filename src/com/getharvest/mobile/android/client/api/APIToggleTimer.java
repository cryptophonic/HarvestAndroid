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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class APIToggleTimer extends APIBase {
	
	public int dailyId;
	public boolean running;
	public float previousTimer;

	protected APIToggleTimer(APIListener l) {
		super(l);
	}

	@Override
	protected String getUrl() {
		return "/daily/timer/" + dailyId;
	}

	@Override
	protected Method method() {
		return Method.GET;
	}

	@Override
	protected void parseDocument(Document dom) {
		//Log.d(getClass().getCanonicalName(), XMLSerializer.getStringFromNode(dom));
		Element root = dom.getDocumentElement();
		Element day_entry = (Element) root.getElementsByTagName("day_entry").item(0);
		Node t = day_entry.getElementsByTagName("timer_started_at").item(0);
		if (t == null)
			running = false;
		else 
			running = true;
		t = root.getElementsByTagName("hours_for_previously_running_timer").item(0);
		if (t != null) {
			previousTimer = Float.parseFloat(getNodeText(t));
		}
	}

	@Override
	protected byte[] sendBytes() {
		return new byte[0];
	}

}
