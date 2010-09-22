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
package com.getharvest.mobile.android.client;

import java.util.Iterator;

import com.getharvest.mobile.android.client.api.APIBase;
import com.getharvest.mobile.android.client.api.APIFactory;
import com.getharvest.mobile.android.client.api.APIListener;
import com.getharvest.mobile.android.client.data.ProjectEntry;
import com.getharvest.mobile.android.client.data.TaskEntry;
import com.getharvest.mobile.android.client.messages.EditEntryData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class EditActivity extends Activity implements OnClickListener, APIListener {
	
	private Button projectButton;
	private Button taskButton;
	private EditText comment;
	private EditText duration;
	
	private EditEntryData eed;
	
	private ProjectEntry selectedProject;
	private TaskEntry selectedTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editactivity);
		
		Intent in = getIntent();
		
		TextView title = (TextView) findViewById(R.id.EditActivityTitle);
		title.setText(in.getStringExtra("title"));
		
		eed = (EditEntryData) in.getParcelableExtra(EditEntryData.class.getCanonicalName());
		TextView date = (TextView) findViewById(R.id.EditDateHeader);
		date.setText(eed.date);
		
		final Context context = this;
		
		projectButton = (Button) findViewById(R.id.EditActivityProject);
		projectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(context);
				dialog.setTitle(getString(R.string.project_title));
				dialog.addContentView(populateProjects(new OnClickListener() {
					@Override
					public void onClick(View v) {
						selectedProject = (ProjectEntry) v.getTag();
						projectButton.setText(selectedProject.client + " : " + selectedProject.name);
						dialog.cancel();
						taskButton.setText(getString(R.string.task_title));
						taskButton.setEnabled(true);
					}
				}), new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				
				LayoutParams params = dialog.getWindow().getAttributes(); 
                params.width = LayoutParams.FILL_PARENT;
                params.height = LayoutParams.FILL_PARENT;
                dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
				
				dialog.show();
			}
		});
		selectedProject = eed.getSelectedProject();
		if (selectedProject != null) {
			projectButton.setText(selectedProject.client + " : " + selectedProject.name);
		} else {
			projectButton.setText(R.string.project_title);
		}
		
		taskButton = (Button) findViewById(R.id.EditActivityTask);
		taskButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectedProject == null) return;
				final Dialog dialog = new Dialog(context);
				dialog.setTitle(getString(R.string.task_title));
				dialog.addContentView(populateTasks(new OnClickListener() {
					@Override
					public void onClick(View v) {
						selectedTask = (TaskEntry) v.getTag();
						taskButton.setText(selectedTask.name);
						dialog.cancel();
					}
				}), new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				
				LayoutParams params = dialog.getWindow().getAttributes(); 
                params.width = LayoutParams.FILL_PARENT;
                params.height = LayoutParams.FILL_PARENT;
                dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
				
				dialog.show();
			}
		});
		selectedTask = eed.getSelectedTask();
		if (selectedTask != null) {
			taskButton.setText(selectedTask.name);
			taskButton.setEnabled(true);
		} else {
			taskButton.setText(getString(R.string.task_title));
			taskButton.setEnabled(false);
		}
		
		duration = (EditText) findViewById(R.id.EditActivityDuration);
		duration.setText(Float.toString(eed.duration));
		
		comment = (EditText) findViewById(R.id.EditActivityComment);
		comment.setText(eed.comment);
		
		Button doneButton = (Button) findViewById(R.id.EditActivityDoneButton);
		doneButton.setOnClickListener(this);
	}
	
	private View populateProjects(OnClickListener listener) {
		ScrollView scrollView = new ScrollView(this);
		scrollView.setHorizontalScrollBarEnabled(false);
		scrollView.setVerticalScrollBarEnabled(false);
		
		LinearLayout layoutView = new LinearLayout(this);
		layoutView.setOrientation(LinearLayout.VERTICAL);
		scrollView.addView(layoutView);
		
		String currentClient = null;
		Iterator<ProjectEntry> iter = eed.projects.iterator();
		while (iter.hasNext()) {
			ProjectEntry pe = iter.next();
			if (currentClient == null || !currentClient.equals(pe.client)) {
				TextView text = new TextView(this);
				text.setText(pe.client);
				layoutView.addView(text);
				currentClient = pe.client;
			}
			Button button = new Button(this);
			button.setText(pe.name);
			button.setTag(pe);
			button.setOnClickListener(listener);
			layoutView.addView(button);
		}
		
		return scrollView;
	}
	
	private View populateTasks(OnClickListener listener) {
		ScrollView scrollView = new ScrollView(this);
		scrollView.setHorizontalScrollBarEnabled(false);
		scrollView.setVerticalScrollBarEnabled(false);
		
		LinearLayout layoutView = new LinearLayout(this);
		layoutView.setOrientation(LinearLayout.VERTICAL);
		scrollView.addView(layoutView);
		
		Iterator<TaskEntry> iter = selectedProject.tasks.iterator();
		while (iter.hasNext()) {
			TaskEntry te = iter.next();
			Button button = new Button(this);
			button.setText(te.name);
			button.setTag(te);
			button.setOnClickListener(listener);
			layoutView.addView(button);
		}
		
		return scrollView;
	}

	@Override
	public void onClick(View button) {
		if (selectedProject != null && selectedTask != null) {
			eed.setSelectedProject(selectedProject);
			eed.setSelectedTask(selectedTask);
			eed.comment = comment.getText().toString();
			eed.duration = Float.parseFloat(duration.getText().toString());
			APIFactory.createAPIUpdate(this, eed);
		} else {
			Builder alert = new AlertDialog.Builder(this);
			alert.setMessage(getString(R.string.need_project_task));
			alert.setNeutralButton("Ok", null);
			alert.show();
		}
	}

	@Override
	public void connectFailure() {
		Builder alert = new AlertDialog.Builder(this);
		alert.setMessage(getString(R.string.update_failed));
		alert.setNeutralButton("Ok", null);
		alert.show();
	}

	@Override
	public void connectSuccess(APIBase base) {
		finish();
	}

}
