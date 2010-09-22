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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
	
	private static int splashDelay = 5000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		final SplashActivity splashActivity = this;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
				startActivity(new Intent(splashActivity, LoginActivity.class));
			}
		}, splashDelay);
	}

}
