package org.schabi.terminightor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by the-scrabi on 27.08.15.
 *
 * Copyright (C) Christian Schabesberger 2015 <chris.schabesberger@mailbox.org>
 * AlarmDBOpenHelper.java is part of Terminightor.
 *
 * Terminightor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Terminightor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

public class AlarmDBOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = AlarmDBOpenHelper.class.toString();

    public static class Index {
        public int ciId;
        public int ciName;
        public int ciAlarmEnabled;
        public int ciAlarmTime;
        public int ciEnabledDays;
        public int ciAlarmTone;
        public int ciVibrate;
        public int ciAlarmNfcId;

    }

    private static AlarmDBOpenHelper alarmDBOpenHelper = null;

    // db
    public static final String DB_NAME = "alarm.db";
    public static final int DB_VERSION = 1;

    // keys
    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String ALARM_ENABLED = "alarm_enabled";
    public static final String ALARM_TIME = "alarm_time";
    public static final String ALARM_DAYS = "alarm_days";
    public static final String ALARM_TONE = "alarm_tone";
    public static final String VIBRATE = "vibrate";
    public static final String NFC_TAG_ID = "nfc_tag_id";

    // table
    public static final String ALARM_TABLE = "alarm";
    public static final String ALARM_TABLE_CREATE = "CREATE TABLE " + ALARM_TABLE
            + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " TEXT,"
            + ALARM_ENABLED + " INTEGER,"
            + ALARM_TIME + " INTEGER,"
            + ALARM_DAYS + " INTEGER,"
            + ALARM_TONE + " TEXT,"
            + VIBRATE + " INTEGER,"
            + NFC_TAG_ID + " BLOB);";
    public static final String ALARM_TABLE_DROP = "DROP TABLE IF EXISTS " + ALARM_TABLE + ";";

    // day flags
    public static int ENABLE_REPEAT = (1<<7);
    public static int MONDAY = 1;
    public static int THUESDAY = (1<<1);
    public static int WEDNESDAY = (1<<2);
    public static int THURSDAY = (1<<3);
    public static int FRIDAY = (1<<4);
    public static int SATURDAY = (1<<5);
    public static int SUNDAY = (1<<6);

    public AlarmDBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static AlarmDBOpenHelper getAlarmDBOpenHelper(Context context) {
        if(alarmDBOpenHelper == null) {
            alarmDBOpenHelper = new AlarmDBOpenHelper(context);
        }
        return alarmDBOpenHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ALARM_TABLE_CREATE);
        Log.v(TAG, ALARM_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ALARM_TABLE_DROP);
        onCreate(db);
    }

    public long insert(String name, boolean alarmEnabled, int alarmTime, int alarmDays,
                       String alarmTone, boolean vibrate, byte[] nfcTag) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(NAME, name);
            values.put(ALARM_ENABLED, alarmEnabled ? 1 : 0);
            values.put(ALARM_TIME, alarmTime);
            values.put(ALARM_DAYS, alarmDays);
            values.put(ALARM_TONE, alarmTone);
            values.put(VIBRATE, vibrate ? 1 : 0);
            values.put(NFC_TAG_ID, nfcTag);
            return db.insert(ALARM_TABLE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void update(long id, String name, boolean alarmEnabled, int alarmTime, int alarmDays,
                       String alarmTone, boolean vibrate, byte[] nfcTagId) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(NAME, name);
            values.put(ALARM_ENABLED, alarmEnabled ? 1 : 0);
            values.put(ALARM_TIME, alarmTime);
            values.put(ALARM_DAYS, alarmDays);
            values.put(ALARM_TONE, alarmTone);
            values.put(VIBRATE, vibrate ? 1 : 0);
            values.put(NFC_TAG_ID, nfcTagId);
            db.update(ALARM_TABLE, values, "_id = ?", new String[]{Long.toString(id)});
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setAlarmEnabled(long id, boolean enabled) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(ALARM_ENABLED, enabled ? 1 : 0);
            db.update(ALARM_TABLE, values, "_id = ?", new String[]{Long.toString(id)});
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cursor query() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(ALARM_TABLE,
                null, null, null,
                null, null, null,
                null);
    }

    public Cursor getValueOf(long id) {
        Cursor cursor = getReadableDatabase()
                .query(ALARM_TABLE, null, "_id = ?", new String[]{Long.toString(id)},
                        null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getValueOf(long id, String column) {
        Cursor cursor = getReadableDatabase()
                .query(ALARM_TABLE, new String[]{column}, "_id = ?", new String[]{Long.toString(id)},
                        null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    public String getName(long id) {
        return getValueOf(id, NAME).getString(0);
    }

    public boolean isAlarmEnabled(long id) {
        return getValueOf(id, ALARM_ENABLED).getInt(0) == 1;
    }

    public int getAlarmTime(long id) {
        return getValueOf(id, ALARM_TIME).getInt(0);
    }

    public int getAlarmDays(long id) {
        return getValueOf(id, ALARM_DAYS).getInt(0);
    }

    public String getAlarmTone(long id) {
        return getValueOf(id, ALARM_TONE).getString(0);
    }

    public boolean isVibrateEnabled(long id) {
        return getValueOf(id, VIBRATE).getInt(0) == 1;
    }

    public byte[] getNfcTagId(long id) {
        return getValueOf(id, NFC_TAG_ID).getBlob(0);
    }

    public void delete(long id) {
        getWritableDatabase()
                .delete(ALARM_TABLE, "_id = ?", new String[]{Long.toString(id)});
    }

    public static Index getIndex(Cursor cursor) {
        Index index = new Index();
        index.ciId = cursor.getColumnIndex(AlarmDBOpenHelper._ID);
        index.ciName = cursor.getColumnIndex(AlarmDBOpenHelper.NAME);
        index.ciAlarmEnabled = cursor.getColumnIndex(AlarmDBOpenHelper.ALARM_ENABLED);
        index.ciAlarmTime = cursor.getColumnIndex(AlarmDBOpenHelper.ALARM_TIME);
        index.ciEnabledDays = cursor.getColumnIndex(AlarmDBOpenHelper.ALARM_DAYS);
        index.ciAlarmTone = cursor.getColumnIndex(AlarmDBOpenHelper.ALARM_TONE);
        index.ciVibrate = cursor.getColumnIndex(AlarmDBOpenHelper.VIBRATE);
        index.ciAlarmNfcId = cursor.getColumnIndex(AlarmDBOpenHelper.NFC_TAG_ID);
        return index;
    }
}