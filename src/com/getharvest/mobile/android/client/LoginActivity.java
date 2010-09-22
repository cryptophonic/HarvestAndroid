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

import com.getharvest.mobile.android.client.api.APIBase;
import com.getharvest.mobile.android.client.api.APIFactory;
import com.getharvest.mobile.android.client.api.APIListener;
import com.getharvest.mobile.android.database.DBKeyValueStore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends Activity implements OnClickListener, APIListener {
	
	private Button loginButton;
	private TextView urlView;
	private TextView emailView;
	private TextView passwordView;
	
	public static String url;
	public static String email;
	public static String password;
	
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		urlView = (TextView) findViewById(R.id.URL);
		emailView = (TextView) findViewById(R.id.Email);
		passwordView = (TextView) findViewById(R.id.Password);
		
		DBKeyValueStore db = new DBKeyValueStore(getApplicationContext());
		url = db.get("loginurl");
		email = db.get("loginemail");
		password = db.get("loginpassword");
		
		if (url != null) urlView.setText(url);
		if (email != null) emailView.setText(email);
		if (password != null) passwordView.setText(password);
		
		loginButton = (Button) findViewById(R.id.LoginButton);
		loginButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		url = urlView.getText().toString();
		email = emailView.getText().toString();
		password = passwordView.getText().toString();
		
		urlView.setEnabled(false);
		emailView.setEnabled(false);
		passwordView.setEnabled(false);
		loginButton.setEnabled(false);
		
		pd = ProgressDialog.show(this, "", "Authenticating. Please wait ... ", 
				true);
		
		APIFactory.createAPILogin(this);
	}

	@Override
	public void connectFailure() {
		pd.dismiss();
		
		Builder alert = new AlertDialog.Builder(this);
		alert.setMessage(getString(R.string.login_failed));
		alert.setNeutralButton("Ok", null);
		alert.show();
		
		urlView.setEnabled(true);
		emailView.setEnabled(true);
		passwordView.setEnabled(true);
		loginButton.setEnabled(true);
	}

	@Override
	public void connectSuccess(APIBase base) {
		pd.dismiss();
		
		DBKeyValueStore db = new DBKeyValueStore(getApplicationContext());
		db.put("loginurl", url);
		db.put("loginemail", email);
		db.put("loginpassword", password);
		
		finish();
		startActivity(new Intent(this, DayActivity.class));
	}

}
