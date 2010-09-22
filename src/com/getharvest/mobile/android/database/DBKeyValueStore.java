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
package com.getharvest.mobile.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DBKeyValueStore {
	
	private static final String DB_NAME = "Keystore";
	private static final String DB_TABLE = "data";
	private static final int DB_VERSION = 1;
	
	private DBHelper helper;
	
	public DBKeyValueStore(Context context) {
		helper = new DBHelper(context, DB_NAME, null, DB_VERSION);
	}
	
	public void put(String key, String value) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("name", key);
		cv.put("value", value);
		if (db.update(DB_TABLE, cv, "name=?", new String[]{key}) == 0) {
			db.insert(DB_TABLE, null, cv);
		}
		db.close();
	}
	
	public String get(String key) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor curs = db.query(DB_TABLE, new String[]{"value"}, "name=?", new String[]{key}, null, null, null);
		if (curs.getCount() == 0) {
			curs.close();
			db.close();
			return null;
		} else if (curs.getCount() > 1) {
			Log.e(getClass().getCanonicalName(), "multiple entries for key " + key);
			curs.close();
			db.close();
			return null;
		} else {
			curs.moveToPosition(0);
			String value = curs.getString(0);
			curs.close();
			db.close();
			return value;
		}
	}
	
	public class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("create table " + DB_TABLE + " (id integer primary key autoincrement, name text unique not null, value text);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(getClass().getCanonicalName(),
					"Cannot upgrade database from " + oldVersion + " to " + newVersion);
		}

	}
	
}
