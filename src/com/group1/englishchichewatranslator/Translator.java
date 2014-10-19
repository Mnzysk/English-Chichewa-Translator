package com.group1.englishchichewatranslator;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Translator extends Activity {
	String input="";
	String outputString="";
	String translation ="";
	SQLiteDatabase db ;
	Cursor solutions;
	ArrayList<Sentence> sentences;
	
	SQLiteDatabase d;
	DBHelper myhelper ;
	
	int fails =0;
	SourceSentRecombine phrase = new SourceSentRecombine();
	Solution solution = new Solution();
	Cursor 	tables;
	
	
	private ProgressDialog pDialog;
	//Varibles needed to sync data
	JSONParser jsonParser = new JSONParser();
	JSONParser jsParser = new JSONParser();
	
	private static String url_sync_lanmodel= "http://10.0.2.2:80/trans_proj/sync_lan_model.php";
	private static String url_sync_transnmodel="http://10.0.2.2:80/trans_proj/sync_trans_model.php";
    private static final String TAG_SUCCESS = "success";
    
    JSONArray dataToSync = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_translator);
		
		//myhelper = new DBHelper(getApplicationContext(), "translateDB", null, 1);
		myhelper = new DBHelper(getApplicationContext());
		
		
		d= myhelper.getReadableDatabase();
		/*Log.d("Out", "Out");
		if(!myhelper.getReadableDatabase().rawQuery("SELECT * FROM transModel;", null).moveToFirst()){
			pDialog = ProgressDialog.show(Translator.this, "", "Loading Database... Please wait It may take few minutes\nDikilani kwa mphindi zingapo");
			
			new Thread(){
				
				public void run() {
					
					myhelper.populateDB(getAssets(),myhelper);
					pDialog.dismiss();
					
				}
			}.start();
			
			
		}*/
		try {
			myhelper.open();
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			 throw ex;
		}
		
		db= myhelper.getReadableDatabase();
		
		//db = myhelper.getReadableDatabase();
		
		
		
		//Input Area
		final EditText inputText = (EditText) findViewById(R.id.inputText);
		
		//Translate Button
		Button translate = (Button) findViewById(R.id.translate);
		
		//Out put view 
		final TextView output = (TextView) findViewById(R.id.outputText);
		translate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				input= inputText.getText().toString().toLowerCase().replace("'", "\'").trim();
				//String querry = "SELECT * FROM Codes WHERE name = '"+ input+ "';";
				
				//solutions = db.rawQuery("SELECT * FROM translation;",null);
				if(input.isEmpty()){
					Toast.makeText(getApplicationContext(),"You did not input anything \nSimunalembe malembo", Toast.LENGTH_LONG).show();
					return;
				}
				//String solution = getTranslation(input).replaceAll("[.][a-z]", ".").replaceAll("\\sa\\s", " ").replaceAll("\\san\\s", " ").replaceAll("\\sthe\\s", " ");
				sentences = (new Passage(input+".")).getSentences();
				translation="";
				/*solution.reset();
				getTranslation(input);
				outputString =solution.toString();
				output.setText(outputString);*/
				
				for(int i =0 ;i<sentences.size();i++){
					
					solution.reset();
					getTranslation(sentences.get(i).returnText().replace(".", "").replaceAll("'", "MNZYSKYYY").replaceAll("\"", "\"\"").trim());
					outputString =solution.toString()+sentences.get(i).returnPunctuation();
					
					translation = translation +" "+outputString;
					 
				}
				
				translation = translation.replaceAll("MNZYSKYYY", "'");
				output.setText(translation);
				Log.d("translated After Setting text view",translation);
				
				if (!input.isEmpty() && !outputString.isEmpty()) {
					//Handling History
					db.execSQL("CREATE TABLE IF NOT EXISTS history(id INTEGER PRIMARY KEY AUTOINCREMENT,eng text NOT NULL,chich TEXT NOT NULL);");
					String insertHistory = "INSERT INTO history(eng,chich) VALUES('"
							+ input.replaceAll("'","MNZYSKYYY") + "','" + translation.replaceAll("'", "MNZYSKYYY") + "');";
					db.execSQL(insertHistory);
					Log.d("History", "Insert");
				}
				if(fails>1){
					//Toast.makeText(getApplicationContext(),"No Available Translation\nPalibe tanthauzo lake", Toast.LENGTH_LONG).show();
				}
				
			}
		});
		output.setOnLongClickListener(new OnLongClickListener() {
			
			public boolean onLongClick(View arg0) {
				
				String mesage = "Suggest Translation";
				myAlertDialog(mesage);
				
				return false;
			}

			
		});
		
		ImageView clear = (ImageView) findViewById(R.id.cleartext);
		clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				inputText.setText("");
				inputText.setHint("Lembani apa");
				output.setText("");
				output.setHint("Thanthauzo la Chichewa");
			}
		});
		
	ImageView edit = (ImageView) findViewById(R.id.edittra);
		edit.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				String mesage = "Suggest Translation";
				myAlertDialog(mesage);	
				
			}
		});
		
	}
	
	private void myAlertDialog(String mesage) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Translator.this);
		alertDialogBuilder.setMessage(mesage)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				
				
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent = new Intent(Translator.this, Suggest2.class);
					intent.putExtra("english", input);
					intent.putExtra("chichewa", outputString);
					startActivity(intent);
					
				}
			}).setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					
				}
			});
		
		AlertDialog alertDialog =alertDialogBuilder.create();
		alertDialog.show();
	}
	
	
	/*
	 * This section consist all the methods needed to translate the text*/
	public void getTranslation(String text){
		if(!text.isEmpty()){
			Log.d("Valid", "Vallid");
			
			
			translate(text);
		}
		
		//return text;
	}
	public boolean validLength(String text){
		if(text.split(" ").length <=5 && !text.isEmpty()){
			return true;
		}
		else{
			return false;
		}
	}
	public void translate(String text){
		
		Log.d("Source", text);
		if(searchForTranslation(text)!= null){
			solution.addResult(searchForTranslation(text));
		}
		else if(text.split(" ").length > 1){
			Log.d("In side", "Splitted");
			String [] split = text.split("\\s");
			
			String part1="";
			String part2="";
			for(int i =0 ; i<Math.ceil((double)split.length)-1; i++){
				part1=part1+" "+split[i];
			}
			//Log.d("TAG", ""+part1);
			/*for(int i = (int) Math.ceil((double)split.length/2) ; i<split.length; i++){
				part2=part2+" "+split[i];
			}*/
			part2= split[split.length-1];
			Log.d("TAG", ""+part2);
			Log.d("TAG", ""+part1);
			
			phrase.addtext(part2);
			Log.d("Splitted",phrase.toString());
			translate(part1.trim()); 
			String remphrase = phrase.toString();
			phrase.reset();
			translate(remphrase);
			
		}
		else {
			fails++;
			solution.addResult(text);
		}
	}
	public String searchForTranslation(String text){
		
		
		
		
		try {
			String query = "SELECT * FROM transModel WHERE eng ='"+text+"' AND prob1 > 0.8;";
			
			Log.d("SQL", query);
			solutions = db.rawQuery(query, null);
			Log.d("SQL", "Executed");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		ArrayList< Hyponthesis> posTrans = new ArrayList<Hyponthesis>();
		solutions.moveToFirst();
		int control=0;
		if(solutions.moveToFirst()){
			Log.d("Done", "Query execute");
			do {
				Hyponthesis object = new Hyponthesis(solutions.getString(solutions.getColumnIndex("eng")),
						solutions.getString(solutions.getColumnIndex("chich")),
						Float.parseFloat(solutions.getString(solutions.getColumnIndex("prob1"))));
						Cursor lanModel = db.rawQuery("SELECT prob FROM langModel WHERE mau ='"+solutions.getString(solutions.getColumnIndex("chich"))+"';",null);
						lanModel.moveToFirst();
						
						
						if(lanModel.moveToFirst()){
							
							object.setlanModel(Float.parseFloat(lanModel.getString(lanModel.getColumnIndex("prob"))));
							
						}
						control++;
					Log.d("Possibles",object.getTarget()+control);
				posTrans.add(object);
			} while (solutions.moveToNext());
			
			Hyponthesis max = new Hyponthesis(null,null,0);
			for(int i =0;i<posTrans.size();i++){
				max = posTrans.get(i);
				if(max.getprbText() < posTrans.get(i).getprbText()){
					max = posTrans.get(i);
				}
			}
			Log.d("Done", max.getTarget().trim());
			return max.getTarget().trim().replaceAll("MNZYSKYYY", "'");
		}
		Log.d("TAG", "Nothing Returned");
		
		return null;
		
	}
	
	//Sync class

	
	
}
