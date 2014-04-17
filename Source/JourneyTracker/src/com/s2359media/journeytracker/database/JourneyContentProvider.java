package com.s2359media.journeytracker.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class JourneyContentProvider extends ContentProvider {

	// fields for my content provider
	static final String PROVIDER_NAME = "com.s2359media.journeytracker.provider";
	public static final String URL = "content://" + PROVIDER_NAME + "/journey";
	public static final String URL_GETITEM = "content://" + PROVIDER_NAME + "/item/";
	public static final Uri CONTENT_URI = Uri.parse(URL);

	// fields for the database
	public static final String ID = "_id";
	public static final String LAT = "lat";
	public static final String LNG = "lng";
	public static final String NAME = "name";
	public static final String DATE = "date";
	public static final String TIME = "times";
	// integer values used in content URI
	static final int GET_DATE = 1;
	static final int JOURNEY = 2;
	static final int GET_ITEM = 3;

	DBHelper dbHelper;

	// maps content URI "patterns" to the integer values that were set above
	static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "journey", GET_DATE);
		uriMatcher.addURI(PROVIDER_NAME, "journey/#", JOURNEY);
		uriMatcher.addURI(PROVIDER_NAME, "item/#", GET_ITEM);
	}

	// database declarations
	private SQLiteDatabase database;
	static final String DATABASE_NAME = "Journey";
	static final String TABLE_NAME = "journey";
	static final int DATABASE_VERSION = 1;
	static final String CREATE_TABLE = " CREATE TABLE " + TABLE_NAME + " ("
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LAT
			+ " DOUBLE NOT NULL, " + LNG + " DOUBLE NOT NULL, " + NAME
			+ " TEXT, " + DATE + " LONG NOT NULL, " + TIME + " Long NOT NULL "
			+ ");";

	// class that creates and manages the provider's database
	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			Log.d("createdb", CREATE_TABLE);
			db.execSQL(CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w(DBHelper.class.getName(), "Upgrading database from version "
					+ oldVersion + " to " + newVersion
					+ ". Old data will be destroyed");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}

	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		Context context = getContext();
		dbHelper = new DBHelper(context);
		// permissions to be writable
		database = dbHelper.getWritableDatabase();

		if (database == null)
			return false;
		else
			return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		// the TABLE_NAME to query on
		queryBuilder.setTables(TABLE_NAME);

		switch (uriMatcher.match(uri)) {
		// maps all database column names
		case GET_DATE:
			return database.rawQuery("SELECT " + DATE + " FROM " + TABLE_NAME
					+ " GROUP BY " + DATE, null);

		case JOURNEY:
			queryBuilder.appendWhere(DATE + "=" + uri.getLastPathSegment());
			break;
		case GET_ITEM:
			queryBuilder.appendWhere(ID + "=" + uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		if (sortOrder == null || sortOrder == "") {
			// No sorting-> sort on names by default
			sortOrder = TIME;
		}
		Cursor cursor = queryBuilder.query(database, projection, selection,
				selectionArgs, null, null, sortOrder);
		/**
		 * register to watch a content URI for changes
		 */
		// cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		long row = database.insert(TABLE_NAME, "", values);

		// If record is added successfully
		if (row > 0) {
			Uri newUri = ContentUris.withAppendedId(CONTENT_URI, row);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		}
		throw new SQLException("Fail to add a new record into " + uri);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		int count = 0;

		count = database.update(TABLE_NAME, values,
				ID + " = " + uri.getLastPathSegment(), null);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		int count = 0;

		String id = uri.getLastPathSegment(); // gets the id
		count = database.delete(TABLE_NAME, ID + " = " + id, null);

		getContext().getContentResolver().notifyChange(uri, null);
		return count;

	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

}
