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
package com.getharvest.mobile.android.client.messages;

import java.util.ArrayList;
import java.util.Iterator;

import com.getharvest.mobile.android.client.api.APIDaily;
import com.getharvest.mobile.android.client.data.DailyEntry;
import com.getharvest.mobile.android.client.data.ProjectEntry;
import com.getharvest.mobile.android.client.data.TaskEntry;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class EditEntryData implements Parcelable {
	
	public int dailyId;
	public String date;
	public float duration;
	public String comment;
	public ArrayList<ProjectEntry> projects = new ArrayList<ProjectEntry>();
	
	private int projectIndex;
	private int taskIndex;
	
	public boolean create;
	
	public EditEntryData(Parcel in) {
		readFromParcel(in);
	}
	
	public EditEntryData(DailyEntry de, APIDaily apiDaily, boolean c) {
		dailyId = de.id;
		date = apiDaily.Date;
		duration = de.hours;
		comment = de.comment;
		projectIndex = apiDaily.getProjectIndex(de.client, de.project);
		taskIndex = apiDaily.getTaskIndex(projectIndex, de.task);
		projects = apiDaily.projects;
		create = c;
	}
	
	public EditEntryData(APIDaily apiDaily, boolean c) {
		dailyId = -1;
		date = apiDaily.Date;
		duration = 0;
		comment = "";
		projectIndex = -1;
		taskIndex = -1;
		projects = apiDaily.projects;
		create = c;
	}
	
	public ProjectEntry getSelectedProject() {
		if (projectIndex >= 0)
			return projects.get(projectIndex);
		return null;
	}
	
	public TaskEntry getSelectedTask() {
		if (taskIndex >= 0) 
			return getSelectedProject().tasks.get(taskIndex);
		return null;
	}
	
	public void setSelectedProject(ProjectEntry project) {
		Iterator<ProjectEntry> iter = projects.iterator();
		int index = 0;
		while (iter.hasNext()) {
			ProjectEntry pe = iter.next();
			if (pe.id == project.id) {
				projectIndex = index;
				return;
			}
			index++;
		}
	}
	
	public void setSelectedTask(TaskEntry task) {
		Iterator<TaskEntry> iter = getSelectedProject().tasks.iterator();
		int index = 0;
		while (iter.hasNext()) {
			TaskEntry te = iter.next();
			if (te.id == task.id) {
				taskIndex = index;
				return;
			}
			index++;
		}
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Log.d(getClass().getCanonicalName(), "writing to parcel");
		dest.writeInt(dailyId);
		dest.writeString(date);
		dest.writeFloat(duration);
		dest.writeString(comment);
		dest.writeInt(projectIndex);
		dest.writeInt(taskIndex);
		dest.writeString(Boolean.toString(create));
		// Project / Tasks
		dest.writeInt(projects.size());
		for (int i=0; i<projects.size(); i++) {
			ProjectEntry pe = projects.get(i);
			dest.writeInt(pe.id);
			dest.writeString(pe.client);
			dest.writeString(pe.name);
			dest.writeInt(pe.tasks.size());
			for (int j=0; j<pe.tasks.size(); j++) {
				TaskEntry te = pe.tasks.get(j);
				dest.writeInt(te.id);
				dest.writeString(te.name);
			}
		}
	}
	
	private void readFromParcel(Parcel in) {
		dailyId = in.readInt();
		date = in.readString();
		duration = in.readFloat();
		comment = in.readString();
		projectIndex = in.readInt();
		taskIndex = in.readInt();
		create = Boolean.parseBoolean(in.readString());
		// Project / Tasks
		int n = in.readInt();
		for (int i=0; i<n; i++) {
			ProjectEntry pe = new ProjectEntry();
			pe.id = in.readInt();
			pe.client = in.readString();
			pe.name = in.readString();
			int m = in.readInt();
			for (int j=0; j<m; j++) {
				TaskEntry te = new TaskEntry();
				te.id = in.readInt();
				te.name = in.readString();
				pe.tasks.add(te);
			}
			projects.add(pe);
		}
	}
	
	public static final Parcelable.Creator<EditEntryData> CREATOR = 
		new Parcelable.Creator<EditEntryData>() {
		@Override
		public EditEntryData createFromParcel(Parcel in) {
			return new EditEntryData(in);
		}
		@Override
		public EditEntryData[] newArray(int size) {
			return new EditEntryData[size];
		}
	};
	
}
