package com.group1.englishchichewatranslator;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class Delete extends Activity {
	ArrayList<String> codes = new ArrayList<String>();
	ListView histList;
	CharSequence [] items;
	boolean[] itemsChecked;
	Cursor c;
	
	DBHelper myDbHelper;
	SQLiteDatabase db;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		myDbHelper = new DBHelper(getApplicationContext());
		//myhelper = new DBHelper(getApplicationContext(), "translateDB", null, 1);
		
		db =myDbHelper.getReadableDatabase();
		c = db.rawQuery("SELECT * FROM history ORDER BY id DESC;",null);
		c.moveToFirst();
		if(!c.moveToFirst()){
			Toast.makeText(getApplicationContext(), "No records are found to delete", Toast.LENGTH_LONG).show();
			Delete.this.finish();
		}
		setContentView(R.layout.delete);
		
		histList = (ListView) findViewById(R.id.dilaoglist);
		items = new CharSequence[c.getCount()];
		itemsChecked = new boolean[items.length];
		int it =0;
		if(c.moveToFirst()){
			do {
				codes.add(c.getString(c.getColumnIndex("eng")).replaceAll("MNZYSKYYY", "'"));
				items[it] = c.getString(c.getColumnIndex("eng")).replaceAll("MNZYSKYYY", "'");
				
				
			} while (c.moveToNext());
			
		}
		else{
			Toast.makeText(getApplicationContext(), "No History Found", Toast.LENGTH_LONG).show();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(Delete.this,android.R.layout.simple_list_item_multiple_choice, codes);
		histList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		histList.setAdapter(adapter);
		
		Button delete = (Button) findViewById(R.id.procDelete);
		delete.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				
				int itemcount = histList.getCount();
				
				SparseBooleanArray sparseBooleanArray =histList.getCheckedItemPositions();
				
				for (int i = 0; i < itemcount; i++) {
					if(sparseBooleanArray.get(i)){
						String item = histList.getItemAtPosition(i).toString();
						String sql = "DELETE FROM history WHERE eng = '" + item.replaceAll("'", "MNZYSKYYY") +"';";
						db.execSQL(sql);
						Toast.makeText(getApplicationContext(), "Sucessfully Deleted", Toast.LENGTH_LONG).show();
						Delete.this.finish();
					}
				}
			}
		});
		
		Button cancel = (Button) findViewById(R.id.cancelDel);
		cancel.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View arg0) {
				Delete.this.finish();
				
			}
		});
		
		Button deleteAll = (Button) findViewById(R.id.deleteAll);
		deleteAll.setOnClickListener(new OnClickListener() {
			
	
			public void onClick(View arg0) {
				myAlertDialog("Do you want to delete");
			}
		});
	}
	private void myAlertDialog(String mesage) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Delete.this);
		alertDialogBuilder.setMessage(mesage)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				
				
				public void onClick(DialogInterface arg0, int arg1) {
					if(!c.moveToFirst()){
						Toast.makeText(getApplicationContext(), "No records are found to delete", Toast.LENGTH_LONG).show();
						Delete.this.finish();
					}
					db.execSQL("DROP TABLE IF EXISTS history;");
					
					db.execSQL("CREATE TABLE IF NOT EXISTS history(id INTEGER PRIMARY KEY AUTOINCREMENT,eng text NOT NULL,chich TEXT NOT NULL);");
					
					Toast.makeText(getApplicationContext(), "Cleared All history", Toast.LENGTH_LONG).show();
					Delete.this.finish();
					
				}
			}).setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					
				}
			});
		
		AlertDialog alertDialog =alertDialogBuilder.create();
		alertDialog.show();
	}

}
