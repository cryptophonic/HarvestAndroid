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

import com.getharvest.mobile.android.client.messages.EditEntryData;

import android.util.Log;


public class APIUpdate extends APIBase {
	
	protected EditEntryData entryData;
	protected boolean create;

	protected APIUpdate(APIListener l, boolean c) {
		super(l);
		create = c;
	}

	@Override
	public String getUrl() {
		if (create)
			return "/daily/add";
		return "/daily/update/" + entryData.dailyId;
	}

	@Override
	protected void parseDocument(Document dom) {
	}

	@Override
	protected Method method() {
		return Method.POST;
	}

	@Override
	protected byte[] sendBytes() {
		//	<request>
		//	  <notes>New notes</notes>
		//	  <hours>1.07</hours>
		//	  <spent_at type="date">Tue, 17 Oct 2006</spent_at>
		//	  <project_id>52234</project_id>
		//	  <task_id>67567</task_id>
		//	</request>
		String update = "<request>";
		update += "<notes>" + entryData.comment + "</notes>";
		update += "<hours>" + entryData.duration + "</hours>";
		update += "<spent_at type=\"date\">" + entryData.date + "</spent_at>";
		update += "<project_id>" + entryData.getSelectedProject().id + "</project_id>";
		update += "<task_id>" + entryData.getSelectedTask().id + "</task_id>";
		update += "</request>";
		Log.d(getClass().getCanonicalName(), update);
		return update.getBytes();
	}

}
