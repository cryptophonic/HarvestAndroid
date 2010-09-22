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

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Iterator;

import com.getharvest.mobile.android.client.api.APIBase;
import com.getharvest.mobile.android.client.api.APIDaily;
import com.getharvest.mobile.android.client.api.APIFactory;
import com.getharvest.mobile.android.client.api.APIListener;
import com.getharvest.mobile.android.client.api.APIToggleTimer;
import com.getharvest.mobile.android.client.data.DailyEntry;
import com.getharvest.mobile.android.client.messages.EditEntryData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DayActivity extends Activity implements OnClickListener, OnLongClickListener,
	OnDateSetListener, APIListener {
	
	private APIDaily daily;
	private ProgressDialog pd;
	private DailyEntry focus;
	
	private static final int DATE_DIALOG_ID=1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dailyactivity);
		ImageButton dateButton = (ImageButton) findViewById(R.id.DayViewCalendarButton);
		dateButton.setOnClickListener(this);
		ImageButton addButton = (ImageButton) findViewById(R.id.DayViewAddButton);
		addButton.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LinearLayout dailyTable = (LinearLayout) findViewById(R.id.DayViewTableLayout);
		dailyTable.removeAllViews();
		pd = ProgressDialog.show(this, "", getString(R.string.loading_daily_data), true);
		daily = APIFactory.createAPIDaily(this);
	}
	
	public void queryDailyData(int doy, int year) {
		daily = APIFactory.createAPIDaily(this, doy, year);
	}

	@Override
	public void onClick(View button) {
		if (button.getId() == R.id.DayViewCalendarButton) {
			showDialog(DATE_DIALOG_ID);
		}
		if (button.getId() == R.id.DayViewAddButton) {
			Intent intent = new Intent(this, EditActivity.class);
			intent.putExtra("title", "New Activity");
			intent.putExtra(EditEntryData.class.getCanonicalName(), 
					new EditEntryData(daily, true));
			startActivity(intent);
		}
		final DailyEntry de = (DailyEntry) button.getTag();
		if (de != null) {
			final Context context = this;
			APIFactory.createAPIToggleTimer(new APIListener() {
				@Override
				public void connectFailure() {
					Builder alert = new AlertDialog.Builder(context);
					alert.setMessage(getString(R.string.connect_failed));
					alert.setNeutralButton("Ok", null);
					alert.show();
				}
				@Override
				public void connectSuccess(APIBase base) {
					APIToggleTimer toggleTimer = (APIToggleTimer) base;
					View view = findViewById(de.id);
					if (toggleTimer.running) {
						Iterator<DailyEntry> iter = daily.entries.iterator();
						while (iter.hasNext()) {
							DailyEntry tde = iter.next();
							if (tde.running && tde.id != de.id && toggleTimer.previousTimer > 0.0) {
								tde.hours = toggleTimer.previousTimer;
								View tview = findViewById(tde.id);
								tview.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
							}
							tde.running = false;
						}
						de.running = true;
						view.setBackgroundDrawable(getResources().getDrawable(R.drawable.selected));
					} else {
						de.running = false;
						view.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
					}
				}
			}, de.id);
		}
	}
	
	@Override
	public boolean onLongClick(View button) {
		DailyEntry de = (DailyEntry) button.getTag();
		if (de != null) {
			focus = de;
		}
		return false;
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, 100, 0, "Edit");
		menu.add(0, 200, 0, "Delete");
	}

	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 100:
			// Edit entry
			Intent intent = new Intent(this, EditActivity.class);
			intent.putExtra("title", "Edit Activity");
			intent.putExtra(EditEntryData.class.getCanonicalName(), 
					new EditEntryData(focus, daily, false));
			startActivity(intent);
			break;
		case 200:
			// Delete entry
			final Context context = this;
			APIFactory.createAPIDelete(new APIListener() {
				@Override
				public void connectFailure() {
					Builder alert = new AlertDialog.Builder(context);
					alert.setMessage(getString(R.string.delete_failed));
					alert.setNeutralButton("Ok", null);
					alert.show();
				}
				@Override
				public void connectSuccess(APIBase base) {
					startActivity(new Intent(context, DayActivity.class));
				}
			}, focus.id);
			break;
		default:
		}
		return true;
	}

	@Override
	public void connectFailure() {
		pd.dismiss();
		
		Builder alert = new AlertDialog.Builder(this);
		alert.setMessage(getString(R.string.connect_failed));
		alert.setNeutralButton("Ok", null);
		alert.show();
	}

	@Override
	public void connectSuccess(APIBase base) {
		pd.dismiss();
		TextView title = (TextView) findViewById(R.id.DayViewTitle);
		title.setText(daily.Date);
		LinearLayout dailyTable = (LinearLayout) findViewById(R.id.DayViewTableLayout);
		float total = 0.0f;
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		for (int i=0; i<daily.entries.size(); i++) {
			DailyEntry de = daily.entries.get(i);
			View rootView = getLayoutInflater().inflate(R.layout.activityrow, null);
			LinearLayout rowLayout = (LinearLayout) rootView.findViewById(R.id.RowLayout);
			rowLayout.setId(de.id);
			rowLayout.setTag(de);
			rowLayout.setOnClickListener(this);
			rowLayout.setOnLongClickListener(this);
			rowLayout.setOnCreateContextMenuListener(this);
			TextView task = (TextView) rootView.findViewById(R.id.RowTaskDescription);
			task.setText(de.task);
			TextView project = (TextView) rootView.findViewById(R.id.RowProjectDescription);
			project.setText(de.project);
			TextView comment = (TextView) rootView.findViewById(R.id.RowComment);
			comment.setText(de.comment);
			TextView hours = (TextView) rootView.findViewById(R.id.RowTime);
			hours.setText(df.format(de.hours));
			total += de.hours;
			if (de.running)
				rowLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.selected));
			dailyTable.addView(rootView);
		}
		TextView totalView = (TextView) findViewById(R.id.DayViewTotal);
		totalView.setText(df.format(total));
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Calendar c = Calendar.getInstance();
		int cyear = c.get(Calendar.YEAR);
		int cmonth = c.get(Calendar.MONTH);
		int cday = c.get(Calendar.DAY_OF_MONTH);
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, this, cyear, cmonth, cday);
		}
		return null;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, monthOfYear, dayOfMonth);
		Calendar today = Calendar.getInstance();
		if (cal.compareTo(today) < 0) {
			int doy = cal.get(Calendar.DAY_OF_YEAR);
			LinearLayout dailyTable = (LinearLayout) findViewById(R.id.DayViewTableLayout);
			dailyTable.removeAllViews();
			pd = ProgressDialog.show(this, "", getString(R.string.loading_daily_data), true);
			queryDailyData(doy, year);
		} else {
			Builder alert = new AlertDialog.Builder(this);
			alert.setMessage(getString(R.string.error_future_date));
			alert.setNeutralButton("Ok", null);
			alert.show();
		}
	}

}
