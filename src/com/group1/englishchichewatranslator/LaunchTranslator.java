package com.group1.englishchichewatranslator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LaunchTranslator extends TabActivity{
	DBHelper myhelper ;
	SQLiteDatabase db;
	ProgressDialog pDialog;
	
	Cursor 	tables;
	
	//sync variable
	JSONParser jsonParser = new JSONParser();
	JSONParser jsParser = new JSONParser();
	
	//private static String url_sync_lanmodel= "http://10.0.2.2:80/trans_proj/get_new_translations.php";
	private static String url_sync_lanmodel= "http://192.168.177.1:80/trans_proj/get_new_translations.php";
	//private static String url_sync_transnmodel="http://10.0.2.2:80/trans_proj/get_new_translations.php";
	private static String url_sync_transnmodel= "http://192.168.177.1:80/trans_proj/get_new_translations.php";
    private static final String TAG_SUCCESS = "success";
    
    JSONArray dataToSync = null;
    
    //Variables for network connecting
    Boolean isInternetPresent = false;
    InternetConnDetector internetConn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		setContentView(R.layout.activity_launch_translator);
		setupTabHost();
		myhelper = new DBHelper(getApplicationContext());
		//myhelper = new DBHelper(getApplicationContext(), "translateDB", null, 1);
		
		
		
		pDialog = ProgressDialog.show(LaunchTranslator.this, "", "Setting up the application...\nMay take several minutes resume to other Aplications \nZithenga thawi pang'ono mutha kumapanga zina");
			
			new Thread(){
				
				public void run() {
					
					try {
						myhelper.createDB();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pDialog.dismiss();
					
				}
			}.start();
			
			try {
				myhelper.open();
			} catch (SQLException ex) {
				// TODO Auto-generated catch block
				 throw ex;
			}
			
			db= myhelper.getReadableDatabase();
			
			Log.d("IN", "Out");
		
		internetConn = new InternetConnDetector(getApplicationContext());
		displaynofication();
		
		
	}

	private void setupTabHost() {
		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		
		TabSpec tab1 = tabHost.newTabSpec("First");
		TabSpec tab2 = tabHost.newTabSpec("Second");
		TabSpec tab3 = tabHost.newTabSpec("Third");
		
		Resources rs = getResources();
		
		//tab1.setIndicator("",rs.getDrawable(R.drawable.ic_launcher));
		tab1.setIndicator("Translate");
		tab1.setContent(new Intent(this, Translator.class));
		
		tab2.setIndicator("History");
		tab2.setContent(new Intent(this, History.class));
		
		tab3.setIndicator("Suggest");
		tab3.setContent(new Intent(this, SuggestTranslation.class));
		
		tabHost.addTab(tab1);
		tabHost.addTab(tab2);
		tabHost.addTab(tab3);
		
		
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		exitDialog("Do you Want to exit");
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.translator, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
		case R.id.action_help:
			help();
			break;
		
		case R.id.action_exit:
			exitDialog("Do you Want to Exit?") ;
			break;
			
		case R.id.action_sync:
			isInternetPresent = internetConn.isConnectingToInternet();
			if(isInternetPresent){
				new InitSyncDB().execute();
			}
			else{
				Toast.makeText(getApplicationContext(), "No Internet is Present\nPalibe intaneti", Toast.LENGTH_LONG).show();
			}
			break;
		
		/*case R.id.action_suggest:
			suggest();
			break;*/
		default:
			break;
		}
		
		return true;
	}
	public void help(){
		Intent intent = new Intent(LaunchTranslator.this, Help.class);
		startActivity(intent);
	}
	public void settings(){
		Intent intent = new Intent(LaunchTranslator.this, Settings.class);
		startActivity(intent);
	}
	public void history(){
		Intent intent = new Intent(LaunchTranslator.this, History.class);
		startActivity(intent);
	}
	public void suggest(){
		Intent intent = new Intent(LaunchTranslator.this, SuggestTranslation.class);
		startActivity(intent);
	}
	
	public void displaynofication(){
		
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification noti = new Notification(R.drawable.ic_launcher, "Launch English-Chichewa Translator", System.currentTimeMillis());
		
		Context context = LaunchTranslator.this;
		
		CharSequence title = "English-Chichewa Translator";
		
		CharSequence detail = "Click the Message to Launch the application";
		
		Intent intent = new Intent(	context	,LaunchTranslator.class);
		
		PendingIntent pending = PendingIntent.getActivity(context, 0, intent, Notification.FLAG_ONGOING_EVENT);
		noti.setLatestEventInfo(context, title, detail, pending);
		nm.notify(0, noti);
	}
	
	private void exitDialog(String mesage) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LaunchTranslator.this);
		alertDialogBuilder.setTitle(mesage)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				
				
				public void onClick(DialogInterface arg0, int arg1) {
					finish();
					System.exit(0);
				}
			}).setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					
				}
			});
		
		AlertDialog alertDialog =alertDialogBuilder.create();
		alertDialog.show();
	}
	public class InitSyncDB extends AsyncTask<String, String, String>{

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LaunchTranslator.this);
            pDialog.setMessage("Sync Database. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
		
		protected String doInBackground(String... arg0) {
			
			new SyncTr().execute();
			//new SyncLn().execute();
			return null;
		}
		
		protected void onPostExecute(String result) {
			pDialog.dismiss();
		}
		
	}
	public class SyncTr extends AsyncTask<String, String, String>{

        protected void onPreExecute() {
            super.onPreExecute();
        }
		
		protected String doInBackground(String... arg0) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JSONObject json = jsonParser.makeHttpRequest(url_sync_transnmodel, "GET", params);
			 Log.d("Translation  model: ", json.toString());
			 
			 
			/* tables = db.rawQuery("SELECT number FROM tableRows WHERE tableName ='tranModel';", null);
			 	if(tables.moveToFirst()){
			 		Log.d("In Tran Sync", "Am in"+ tables.getString(tables.getColumnIndex("number")));
			 		tables.moveToFirst();
			 		int lows = Integer.parseInt(tables.getString(tables.getColumnIndex("number")));
			 		
			 		Log.d("Lows", ""+lows);*/
			 		 try {
			 			 
						int success = json.getInt("success");
						Log.d("In Tran Sync", "Get Success");
						if (success == 1) {
							Log.d("In Tran Sync", "Succeed");
		                    // Data found
		                    // Getting Array of Database content
		                    dataToSync = json.getJSONArray("translation");
		 
		                    // looping through All Products
		                    //if(lows != dataToSync.length()){
		                    
		                    	for (int i = 0; i < dataToSync.length(); i++) {
		                    		JSONObject c = dataToSync.getJSONObject(i);
		 
		                        // Storing each json item in variable
		                    		String eng = c.getString("eng").toLowerCase().replaceAll("'", "MNZYSKYYY").trim();
		                    		String chich = c.getString("chich").toLowerCase().replaceAll("'", "MNZYSKYYY").trim();
		                    		String prob1 = c.getString("prob1").toLowerCase().trim();
		                    		String prob2 = c.getString("prob2").toLowerCase().trim();
		                    		Log.d("Sync", eng +"\t"+ chich);
		                        
		                    		String query = "INSERT INTO transModel(eng,chich,prob1,prob2) VALUES('"+eng+"','"+chich+"',"+prob1+","+prob2+");";
		                    		Log.d("Query", query);
		                    		db.execSQL(query);
		                    		Log.d("SQL", "Query executed");
		                    		myhelper = new DBHelper(getApplicationContext());
		                    		db= myhelper.getReadableDatabase();
		                       
		                    	}
		                    
		                    	//db.execSQL("DELETE FROM tableRows WHERE tableName ='tranModel';");
		                    	//db.execSQL("INSERT INTO tableRows VALUES('tranModel',"+lows+");");
		                    //}
		                } else {
		                    // no Data found
		                    // Launch Translator Activity
		                    Intent i = new Intent(getApplicationContext(),
		                            LaunchTranslator.class);
		                    // Closing all previous activities
		                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		                    startActivity(i);
		                }
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
			 		
			return null;
		}
		
		protected void onPostExecute(String result) {
			
		}
		
	}
	public class SyncLn extends AsyncTask<String, String, String>{

        protected void onPreExecute() {
            super.onPreExecute();
        }
		
		protected String doInBackground(String... arg0) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JSONObject json = jsParser.makeHttpRequest(url_sync_lanmodel, "GET", params);
			 Log.d("Language model: ", json.toString());
			 
			 tables = db.rawQuery("SELECT number FROM tableRows WHERE tableName ='lanModel';", null);
			 	if(tables.moveToFirst()){
			 		Log.d("In Lan Sync", "Am in");
			 		tables.moveToFirst();
			 		int lows = Integer.parseInt(tables.getString(tables.getColumnIndex("number")));
			 		
			 		Log.d("Lows", ""+lows);
			 try {
	 			 
					int success = json.getInt("success");
					Log.d("In Tran Sync", "Get Success");
					if (success == 1) {
						Log.d("In Tran Sync", "Succeed");
	                    // Data found
	                    // Getting Array of Database content
	                    dataToSync = json.getJSONArray("lanmodel");
	                    int i = lows;
	                    // looping through All Products
	                    for (; i < dataToSync.length(); i++) {
	                        JSONObject c = dataToSync.getJSONObject(i);
	 
	                        // Storing each json item in variable
	                        String words = c.getString("words");
	                        String prob = c.getString("prob");
	                        String type = c.getString("type");
	                        String query = "INSERT INTO lanModel(mau,prob,type) VALUES('"+words+"',"+prob+",'"+type+"');";
	                        db.execSQL(query);
	                       
	                        Log.d("Sync ", words);
	 
	                       
	                    }
	                    //db.execSQL("DELETE FROM tableRows WHERE tableName ='lanModel';");
	                    //db.execSQL("INSERT INTO tableRows VALUES('lanModel',"+lows+");");
	                } else {
	                    // no Data found
	                    // Launch Translator Activity
	                    Intent i = new Intent(getApplicationContext(),
	                            Translator.class);
	                    // Closing all previous activities
	                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                    startActivity(i);
	                }
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			 }
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			
		}
		
	}

}
