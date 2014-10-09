package com.group1.englishchichewatranslator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLDataException;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	
	private Context appcontext;
	private String DB_PATH= "/data/data/com.group1.englishchichewatranslator/databases/";
	private static String DB_NAME="translateDB.db";
	final String LOG = "DatabaseHelper";
	SQLiteDatabase translateDB;
	
	public DBHelper(Context context) {
		
		super(context,DB_NAME,null,1);
		this.appcontext = context;
		boolean dbexist = checkdatabase();
		if (dbexist) {
			
		}
		else{
			Log.d(LOG,"Database Doesn't Exist");
			try {
				createDB();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void createDB() throws IOException {
		// TODO Auto-generated method stub
		boolean dbexist = checkdatabase();
		if (dbexist) {
			
		}
		else{
			this.getReadableDatabase();
			//the method copys the db file from the assets folder
			try {
				copydatabase();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void copydatabase() throws IOException {
		// TODO Auto-generated method stub
		InputStream input = appcontext.getAssets().open("translateDB.db");
		String outputFile = DB_PATH+DB_NAME;
		OutputStream  output = new FileOutputStream(outputFile);
		
		byte[] buffer = new byte[1024];
		
		int length;
		
		while ((length = input.read(buffer)) > 0) {
			output.write(buffer,0,length);
			
		}
		
		// Close the streams        
				output.flush();
				output.close();
				input.close();
		
	}

	private boolean checkdatabase() {
		boolean exists = false;
		
		try {
			String myDBPath = DB_PATH+DB_NAME;
			File dbFile = new File(myDBPath);
			exists = dbFile.exists();
		} catch (SQLiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return exists;
	}
	public void open() {
		String path = DB_PATH+DB_NAME;
		translateDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
		
	}
	public synchronized void closed(){
		translateDB.close();
		super.close();
	}
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion >= newVersion) return;
			db.execSQL("DROP DATABASE IF EXISTS " + DB_NAME +";");		
			this.onCreate(db);
	}

}
