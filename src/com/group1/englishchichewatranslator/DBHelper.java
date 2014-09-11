package com.group1.englishchichewatranslator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	final static int DATABASE_VERSION = 1;		// Database Version			    
    final static String DATABASE_NAME = "translateDB";
    final String LOG = "DatabaseHelper";
    
  //Contacts table name 
	final String TRANSMODEL = "transModel";
	final String LANGMODEL = "langModel";
	
	SQLiteDatabase db;

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
		
	}

	
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TRANSMODEL_TABLE = "CREATE TABLE IF NOT EXISTS "
				  + TRANSMODEL + "(id INTEGER PRIMARY KEY AUTOINCREMENT,eng TEXT, chich TEXT, prob1 REAL, prob2 REAL);";
		String CREATE_LAN_MODEL = "CREATE TABLE IF NOT EXISTS "+LANGMODEL+"(id INTEGER PRIMARY KEY AUTOINCREMENT,mau TEXT,prob REAL, type VARCHAR(10));" ;
		
		//	Comment out b4 shipping
		//db.execSQL("DROP TABLE IF EXISTS transModel;");
		//db.execSQL("DROP TABLE IF EXISTS langModel;");
		
		//creating tables in the database
		db.execSQL(CREATE_TRANSMODEL_TABLE);
		db.execSQL(CREATE_LAN_MODEL);
		
		db.execSQL("CREATE TABLE IF NOT EXISTS tableRows(tableName text NOT NULL,number REAL);");
		
		
	}
	
	public void populateDB(AssetManager files,DBHelper myDBHelper){
		
		//Populating the translation model please uncomment the statement below
		populateTrModel(files, myDBHelper);
		polateTraModel(files,myDBHelper);
		
		
		//populating the unigram Language Model please uncomment the statements below
		//populateLanModel(files,"test1.txt","Unigram",myDBHelper);
		//populateLanModel(files,"test2.txt","Bigram",myDBHelper);
		//populateLanModel(files,"test3.txt","Trigram",myDBHelper);
		
	}
	

	private void populateLanModel(AssetManager files,String filename,String type,DBHelper myDBHelper) {
		try {
			 BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    files.open(filename)));
			 
			 String line;
	            // NOW READING THEM LINE BY LINE UPTO THE END OF FILE
			 	int lows=0;
	            while ((line = reader.readLine()) != null) {
	            	//Log.d("How", line);
	            	
	            	String [] splitted = line.replaceAll("'", "''").replaceAll("\"", "\"\"").split("(\t)");
					String query = "INSERT INTO "+LANGMODEL+"(mau,prob,type) VALUES('"+splitted[1].replaceAll("[.,*#;]", "").trim()
									+"',"+Math.pow(10, Double.parseDouble( splitted[0]))+",'"+type+"');";
					
					insert(query, myDBHelper);
					
					Log.d(LOG, query);
					
					lows++;
	            }
	            
	            String insrtLnRows ="INSERT INTO tableRows VALUES('lanModel',"+lows+");";
	            if (lows != 0) {
					insert(insrtLnRows, myDBHelper);
				}
				Log.d("Done", "Polulating");
	 
	            //CLOSE THE FILE AFTER WE HAVE FINISHED READING
	            reader.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//Toast.makeText(getApplicationContext(),
                //  "Error Opening the File !!!", Toast.LENGTH_LONG).show();
		}
	}

    public void insert(String query,DBHelper myDBHelper){
    	SQLiteDatabase db = myDBHelper.getReadableDatabase();
    	db.execSQL(query);
    }
    private void populateTrModel(AssetManager files,DBHelper myDBHelper) {
		try {
			 BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    files.open("englishCoparaU.txt")));
			 BufferedReader reader2 = new BufferedReader(new InputStreamReader(
	                    files.open("chichewaCoparaU.txt")));
			 String line1;
			 String line;
			 int lows=0;
	            // NOW READING THEM LINE BY LINE UPTO THE END OF FILE
	            while ((line = reader.readLine()) != null&& (line1 = reader2.readLine()) !=null ) {
	            	
	            	//Log.d("How", line);
	            	String eng = line.replaceAll("'", "''").replaceAll("\"", "\"\"").trim().toLowerCase();
					String chich = line1.replaceAll("'", "''").replaceAll("\"", "\"\"").trim().toLowerCase();
					if(eng.isEmpty()|| chich.isEmpty()) continue;
					String query = "INSERT INTO transModel(eng, chich, prob1, prob2) VALUES('"+eng+"','"+chich+"',1,1);";
	            	
					insert(query, myDBHelper);
					
					//break;
					lows++;
					Log.d(LOG, lows+"\t"+query);
	            }
	            Log.d(LOG, ""+lows);
	            String insrtLnRows ="INSERT INTO tableRows VALUES('tranModel',"+lows+");";
	            if (lows != 0) {
					insert(insrtLnRows, myDBHelper);
				}
	            // CLOSE THE FILE AFTER WE HAVE FINISHED READING
	            reader.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//Toast.makeText(getApplicationContext(),
                //  "Error Opening the File !!!", Toast.LENGTH_LONG).show();
		}
	}
	private void polateTraModel(AssetManager files,DBHelper myDBHelper) {
		try {
			 BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    files.open("eng_chich.txt")));
			 String line;
			 int lows=0;
	            // NOW READING THEM LINE BY LINE UPTO THE END OF FILE
	            while ((line = reader.readLine()) != null) {
	            	
	            	//Log.d("How", line);
	            	String[] splitted = line.replaceAll("'", "''").replaceAll("\"", "\"\"").split("\\t-\\t");
					String[] words = splitted[0].replaceAll("[;*,.-]","").split("\\t");
					String[] numbers = splitted[1].split("\\s");
					String query = "INSERT INTO transModel(eng, chich, prob1, prob2) VALUES('"+words[0].trim()+"','"+words[1].trim()+"',"
									+Float.parseFloat(numbers[0].trim())+","+Float.parseFloat(numbers[1].trim())+");";
					if(Float.parseFloat(numbers[0].trim())> 0.7){
						insert(query, myDBHelper);
						lows++;
					}
					
					Log.d(LOG, query);
					//break;
	            }
	            Log.d(LOG, ""+lows);
	            String insrtLnRows ="INSERT INTO tableRows VALUES('tranModel',"+lows+");";
	            if (lows != 0) {
					insert(insrtLnRows, myDBHelper);
				}
	            // CLOSE THE FILE AFTER WE HAVE FINISHED READING
	            reader.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//Toast.makeText(getApplicationContext(),
                 //  "Error Opening the File !!!", Toast.LENGTH_LONG).show();
		}
	}


		    
    // on upgrade drop older tables			 
	public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion >= newVersion) return;
			db.execSQL("DROP DATABASE IF EXISTS " + DATABASE_NAME +";");		
			this.onCreate(db);
	}

}
