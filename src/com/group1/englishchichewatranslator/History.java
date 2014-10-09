package com.group1.englishchichewatranslator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.view.View;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;

public class History extends Activity {
	DBHelper myHelper;
	SQLiteDatabase db;
	Cursor history;
	
	ArrayList< Bilingual> hisArray;
	
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		
		setupHisView();
		
		ImageView refreshHis = (ImageView) findViewById(R.id.refreshHis);
		refreshHis.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setupHisView();
				
			}
		});
		
		ImageView delete = (ImageView) findViewById(R.id.delete);
		delete.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				delete();
				
			}

			private void delete() {
				startActivity(new Intent(History.this, Delete.class));
				
			}
		});
	}

	private void setupHisView() {
		myHelper = new DBHelper(getApplicationContext());
		
		db = myHelper.getReadableDatabase();
		hisArray = new ArrayList<Bilingual>();
		

		
		
		
		expListView = (ExpandableListView) findViewById(R.id.lvExp);
		
		
		
		prepareListData();
		
		Log.d("History", "Done Adding His Objects");
		
		listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
		
		expListView.setAdapter(listAdapter);
		
		// Listview Group click listener
		expListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// Toast.makeText(getApplicationContext(),
				// "Group Clicked " + listDataHeader.get(groupPosition),
				// Toast.LENGTH_SHORT).show();
				return false;
			}
		});

		// Listview Group expanded listener
		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				/*Toast.makeText(getApplicationContext(),
						listDataHeader.get(groupPosition) + " Expanded",
						Toast.LENGTH_SHORT).show();*/
			}
		});

		// Listview Group collasped listener
		expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				/*Toast.makeText(getApplicationContext(),
						listDataHeader.get(groupPosition) + " Collapsed",
						Toast.LENGTH_SHORT).show();*/

			}
		});

		// Listview on child click listener
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(
						getApplicationContext(),
						listDataHeader.get(groupPosition)
								+ " : "
								+ listDataChild.get(
										listDataHeader.get(groupPosition)).get(
										childPosition), Toast.LENGTH_SHORT)
						.show();
				return false;
			}
		});
	}

	private void prepareListData() {
		
		Log.d("Prepare", "In side");
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String,List<String>>();
		
		db.execSQL("CREATE TABLE IF NOT EXISTS history(id INTEGER PRIMARY KEY AUTOINCREMENT,eng text NOT NULL,chich TEXT NOT NULL);");
		
		
		String getHistory = "SELECT * FROM history ORDER BY id DESC;";
		history = db.rawQuery(getHistory, null);
		if(!history.moveToFirst()){
			Toast.makeText(getApplicationContext(), "No History Found", Toast.LENGTH_LONG).show();
			return;
		}
		
		history.moveToFirst();
		do{
			String eng= history.getString(history.getColumnIndex("eng")).replaceAll("MNZYSKYYY", "'");
			Log.d("History", eng);
			String chich = history.getString(history.getColumnIndex("chich")).replaceAll("MNZYSKYYY", "'");
			Log.d("History", chich);
			listDataHeader.add(eng);
			List<String> child = new ArrayList<String>();
			child.add(chich);		
			listDataChild.put(eng,child );
			
			Log.d("Done", "Adding data");
		}while(history.moveToNext());
	}
	
	
}
