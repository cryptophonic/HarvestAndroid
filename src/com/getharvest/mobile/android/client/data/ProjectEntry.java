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
package com.getharvest.mobile.android.client.data;

import java.util.ArrayList;

public class ProjectEntry {
	public int id;
	public String client;
	public String name;
	public ArrayList<TaskEntry> tasks = new ArrayList<TaskEntry>();
	
	public boolean matches(String c, String n) {
		if (client.equals(c) && name.equals(n)) {
			return true;
		}
		return false;
	}
}
