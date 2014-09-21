package com.group1.englishchichewatranslator;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.group1.englishchichewatranslator.SuggestTranslation.Suggest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Suggest2 extends Activity {
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
    EditText engInput;
    EditText chichInput;
    EditText commentInput;
 
    //connect to the host
    private static String url_suggest = "http://10.0.2.2:80/trans_proj/add_suggestion.php";
    
    private static final String TAG_SUCCESS = "success";
 
    //Network status
    Boolean isInternetPresent = false;
    InternetConnDetector internetConn;
    
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.suggest);
		
		internetConn = new InternetConnDetector(getApplicationContext());
		
		 engInput = (EditText) findViewById(R.id.english_suggest);
		 chichInput = (EditText) findViewById(R.id.chichewa_suggest);
		 commentInput = (EditText) findViewById(R.id.comment_suggest);
		 if(getIntent()!=null){
			 engInput.setText(getIntent().getExtras().getString("english"));
			 chichInput.setText(getIntent().getExtras().getString("chichewa"));
		 }
		 Button suggest = (Button) findViewById(R.id.submit);
		 
		 suggest.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View arg0) {
				
				isInternetPresent = internetConn.isConnectingToInternet();
				if (isInternetPresent) {
					new Suggest().execute();
				}
				else {
					Toast.makeText(getApplicationContext(), "No Internet is Present\nPalibe intaneti", Toast.LENGTH_LONG).show();
				}
			}
		});
		 
		//clear edit views
			ImageView clear = (ImageView) findViewById(R.id.clearenglish);
			clear.setOnClickListener(new OnClickListener() {
				
				
				public void onClick(View arg0) {
				
					clearEditText(engInput, "Lembani apa\t\t write here");
					clearEditText(chichInput,"Lembani apa\t\t write here");
					clearEditText(commentInput, "Lembani apa\t\t write here");
				}
			});
		
			ImageView clear2 = (ImageView) findViewById(R.id.clearchichewa);
			clear2.setOnClickListener(new OnClickListener() {
				
				
				public void onClick(View arg0) {
				
					clearEditText(chichInput, "Lembani apa\t\t write here");
				}
			});
			ImageView clear3 = (ImageView) findViewById(R.id.clearcomment);
			clear3.setOnClickListener(new OnClickListener() {
				
				
				public void onClick(View arg0) {
				
					clearEditText(commentInput, "Lembani apa");
				}
			});
		//connect to local host
		
		
	}
	
	public void clearEditText(EditText ed,String hint){
		ed.setText("");
		ed.setHint(hint);
	}
	
	class Suggest extends AsyncTask<String, String, String> {
		 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Suggest2.this);
            pDialog.setMessage("Adding Suggestion..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
        	String eng = engInput.getText().toString().replaceAll("'", "''");
            String chich = chichInput.getText().toString().replaceAll("'", "''");
            String comment = commentInput.getText().toString().replaceAll("'", "''");
 
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("eng", eng));
            params.add(new BasicNameValuePair("chich", chich));
            params.add(new BasicNameValuePair("comment", comment));
            
            Log.d("FINISH", "Adding Params");
 
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_suggest,
                    "POST", params);
 
            // check log cat fro response
            Log.d("Create Response", json.toString());
 
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // successfully created product
                   
                	startActivity(new Intent(getApplicationContext(), LaunchTranslator.class));
 
                    // closing this screen
                    finish();
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
 
    }
}
