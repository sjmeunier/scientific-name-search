package com.smeunier.scientificnamesearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		String tsn = "";
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    tsn = extras.getString("TSN");
		}
		Log.i("a", tsn);
		TableLayout detailsTable = (TableLayout) findViewById(R.id.detailsTable);
		detailsTable.removeAllViews();
		
		new AsyncNameInfo(detailsTable).execute("http://www.itis.gov/ITISWebService/jsonservice/ITISService/getCommonNamesFromTSN?tsn=" + tsn);
		new AsyncHierarchyInfo(detailsTable).execute("http://www.itis.gov/ITISWebService/jsonservice/getFullHierarchyFromTSN?tsn=" + tsn);


    }
    
    private class AsyncNameInfo extends AsyncTask<String, Void, String> {

    	private final TableLayout detailsTable;
    	private ProgressDialog progressDialog;
    	
		public AsyncNameInfo(TableLayout detailsTable)
		{
			this.detailsTable = detailsTable;
		}
		
        @Override
        protected String doInBackground(String... uri) {
            return WebAccess.queryRESTurl(uri[0]);
            
        }      

        @Override
        protected void onPostExecute(String result) { 
        	if (result.startsWith("ERROR-")){
        		progressDialog.dismiss();
        		Toast.makeText(DetailsActivity.this, result.substring(6), Toast.LENGTH_LONG).show();
        		return;
        	}        	
        	try {
				JSONObject jObject = new JSONObject(result);
				
				JSONArray jArray = jObject.getJSONArray("commonNames");
				Log.i("a", String.valueOf(jArray.length()));
				for (int i=0; i < jArray.length(); i++)
				{
				    JSONObject oneObject = jArray.getJSONObject(i);
				    // Pulling items from the array
					Log.i("a", oneObject.getString("commonName"));
				    	
			        TableRow tableRow = new TableRow(getApplicationContext());
			        
			        final TextView rankNameView = new TextView(getApplicationContext());
			        if (i == 0)
			        	rankNameView.setText("Common Names");  
			        else
			        	rankNameView.setText(""); 
			        rankNameView.setWidth(200);
			        rankNameView.setTextColor(Color.BLACK);
			        tableRow.addView(rankNameView);
			        
			        final TextView taxonNameView = new TextView(getApplicationContext());
			        taxonNameView.setText(oneObject.getString("commonName"));   
			        taxonNameView.setTextColor(Color.BLACK);
			        tableRow.addView(taxonNameView);     
			        
			        detailsTable.addView(tableRow);
			        progressDialog.dismiss();
				}
			} catch (JSONException e) {
				// If we get here no results returned or else invalid json. either way just don't show anything
				Log.println(Log.ERROR, "a", e.getMessage());
				progressDialog.dismiss();
			}
        }
        
        @Override
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(DetailsActivity.this, "", "Searching...");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
    private class AsyncHierarchyInfo extends AsyncTask<String, Void, String> {

    	private final TableLayout detailsTable;
    	private ProgressDialog progressDialog;
    	
		public AsyncHierarchyInfo(TableLayout detailsTable)
		{
			this.detailsTable = detailsTable;
		}
		
        @Override
        protected String doInBackground(String... uri) {
            return WebAccess.queryRESTurl(uri[0]);
            
        }      

        @Override
        protected void onPostExecute(String result) { 
        	if (result.startsWith("ERROR-")){
        		progressDialog.dismiss();
        		Toast.makeText(DetailsActivity.this, result.substring(6), Toast.LENGTH_LONG).show();
        		return;
        	}  
        	try {
				JSONObject jObject = new JSONObject(result);
				
				JSONArray jArray = jObject.getJSONArray("hierarchyList");
				Log.i("a", String.valueOf(jArray.length()));
				for (int i=0; i < jArray.length(); i++)
				{
				    JSONObject oneObject = jArray.getJSONObject(i);
				    // Pulling items from the array
					Log.i("a", oneObject.getString("taxonName"));
				    	
			        TableRow tableRow = new TableRow(getApplicationContext());
			        
			        final TextView rankNameView = new TextView(getApplicationContext());
			        rankNameView.setText(oneObject.getString("rankName"));  
			        rankNameView.setWidth(200);
			        rankNameView.setTextColor(Color.BLACK);
			        tableRow.addView(rankNameView);
			        
			        final TextView taxonNameView = new TextView(getApplicationContext());
			        taxonNameView.setText(oneObject.getString("taxonName"));  
			        taxonNameView.setTextColor(Color.BLACK);
			        tableRow.addView(taxonNameView);     
			        
			        detailsTable.addView(tableRow);
			        progressDialog.dismiss();
				}
			} catch (JSONException e) {
				// If we get here no results returned or else invalid json. either way just don't show anything
				Log.println(Log.ERROR, "a", e.getMessage());
				progressDialog.dismiss();
			}
        }

        @Override
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(DetailsActivity.this, "", "Searching...");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }    
    
}