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

import com.getharvest.mobile.android.client.messages.EditEntryData;

public class APIFactory {

	public static APIDaily createAPIDaily(APIListener l) {
		APIDaily api = new APIDaily(l);
		api.setup();
		new Thread(api).start();
		return api;
	}
	
	public static APIDaily createAPIDaily(APIListener l, int doy, int y) {
		APIDaily api = new APIDaily(l);
		api.dayOfYear = doy;
		api.year = y;
		api.setup();
		new Thread(api).start();
		return api;
	}
	
	public static APILogin createAPILogin(APIListener l) {
		APILogin api = new APILogin(l);
		api.setup();
		new Thread(api).start();
		return api;
	}
	
	public static APIUpdate createAPIUpdate(APIListener l, EditEntryData eed) {
		APIUpdate api = new APIUpdate(l, eed.create);
		api.entryData = eed;
		api.setup();
		new Thread(api).start();
		return api;
	}
	
	public static APIToggleTimer createAPIToggleTimer(APIListener l, int id) {
		APIToggleTimer api = new APIToggleTimer(l);
		api.dailyId = id;
		api.setup();
		new Thread(api).start();
		return api;
	}
	
	public static APIDelete createAPIDelete(APIListener l, int id) {
		APIDelete api = new APIDelete(l);
		api.dailyId = id;
		api.setup();
		new Thread(api).start();
		return api;
	}
	
}
