package com.hxq.daka;

import java.util.Calendar;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "com.hxq.daka.db";
	public static final int DB_VERSION = 1;
	
	public static final String TABLE_NAME = "records";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_YEAR = "year";
	public static final String COLUMN_MONTH = "month";
	public static final String COLUMN_DAY_OF_MONTH = "day_of_month";
	public static final String COLUMN_MISSION_ID = "mission_id";
	public static final String COLUMN_MISSION_INFO = "mission_info";
	
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
				COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				COLUMN_YEAR + " INTEGER, " + 
				COLUMN_MONTH + " INTEGER, " + 
				COLUMN_DAY_OF_MONTH + " INTEGER, " + 
			 	COLUMN_MISSION_ID + " INTEGER, " + 
				COLUMN_MISSION_INFO + " TEXT" +
				");"
				);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	public long addMission(MissionManager.Mission mission) {
		if(mission == null) 
			return -1l;
		ContentValues values = new ContentValues();
		values.put(COLUMN_YEAR, mission.getYear());
		values.put(COLUMN_MONTH, mission.getMonth());
		values.put(COLUMN_DAY_OF_MONTH, mission.getDayOfMonth());
		values.put(COLUMN_MISSION_ID, mission.getId());
		values.put(COLUMN_MISSION_INFO, mission.getInfo());
		SQLiteDatabase db = getWritableDatabase();
		return db.insert(TABLE_NAME, null, values);
	}
	
	public int removeMission() {
		Calendar c = Calendar.getInstance(Locale.getDefault());
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		return removeMission(year, month, day);
	}
	
	public int removeMission(MissionManager.Mission mission) {
		int year = mission.getYear();
		int month = mission.getMonth();
		int day = mission.getDayOfMonth();
		return removeMission(year, month, day);
	}
	
	public int removeMission(int year, int month, int day) {
		String where = COLUMN_YEAR + "=? and " + COLUMN_MONTH + "=? and " + COLUMN_DAY_OF_MONTH + "=?";
		String[] whereArgs = new String[] { String.valueOf(year), String.valueOf(month), 
				String.valueOf(day) };
		SQLiteDatabase db = getWritableDatabase();
		return db.delete(TABLE_NAME, where, whereArgs);
	}
	
	public MissionManager.Mission queryMissionByDate(MissionManager manager, int year, int month, int day_of_month) {
		String selection = COLUMN_YEAR + "=? and " + COLUMN_MONTH + "=? and " + COLUMN_DAY_OF_MONTH + "=?";
		String[] selectionArgs = new String[] { String.valueOf(year), String.valueOf(month), 
				String.valueOf(day_of_month) };
		String[] columns = { COLUMN_MISSION_ID };
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
		if(c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex(COLUMN_MISSION_ID));
			return manager.getMission(id, year, month, day_of_month);
		}
		else 
			return null;
	}
	
	public SparseArray<MissionManager.Mission> queryMissionInSingleMonth(MissionManager manager, int year, int month) {
		String selection = COLUMN_YEAR + "=? and " + COLUMN_MONTH + "=?";
		String[] selectionArgs = new String[] { String.valueOf(year), String.valueOf(month) };
		String[] columns = { COLUMN_MISSION_ID, COLUMN_DAY_OF_MONTH };
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
		SparseArray<MissionManager.Mission> missionArray = new SparseArray<MissionManager.Mission>();
		while(c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex(COLUMN_MISSION_ID));
			int day = c.getInt(c.getColumnIndex(COLUMN_DAY_OF_MONTH));
			missionArray.put(day, manager.getMission(id, year, month, day));
		}
		return missionArray;
	}

}
