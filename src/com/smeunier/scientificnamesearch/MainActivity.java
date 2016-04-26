package com.smeunier.scientificnamesearch;

import android.os.Bundle;
import android.os.AsyncTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ArrayAdapter;  
import java.util.ArrayList;  
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

public class MainActivity extends Activity {
	private String searchUrl = "http://www.itis.gov/ITISWebService/jsonservice/searchByCommonName?srchKey=";
	private String searchType = "common";
	
	private int orientation;
	private StartAppAd startAppAd = new StartAppAd(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.orientation = getScreenOrientation();
        StartAppSDK.init(this, "106210200", "206826615");
        UpdateViews();

		
	}
	 
	@Override
	public void onBackPressed() {
		startAppAd.onBackPressed();
	    super.onBackPressed();
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    startAppAd.onResume();
	}
	
    AdapterView.OnItemSelectedListener searchTypelistener = new AdapterView.OnItemSelectedListener () {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        	final EditText searchText = (EditText) findViewById(R.id.searchText);
        	
        	if (parent.getSelectedItem().toString().equals(getString(R.string.common_name_contains))){
        		searchText.setHint(getString(R.string.common_name_contains));
        		searchType = "common";
        		searchUrl = "http://www.itis.gov/ITISWebService/jsonservice/searchByCommonName?srchKey=";
        	} else if (parent.getSelectedItem().toString().equals(getString(R.string.common_name_starts_with))){
        		searchText.setHint(getString(R.string.common_name_starts_with));
        		searchType = "common";
        		searchUrl = "http://www.itis.gov/ITISWebService/jsonservice/searchByCommonNameBeginsWith?srchKey=";
        	} else if (parent.getSelectedItem().toString().equals(getString(R.string.common_name_ends_with))){
        		searchText.setHint(getString(R.string.common_name_ends_with));
        		searchType = "common";
        		searchUrl = "http://www.itis.gov/ITISWebService/jsonservice/searchByCommonNameEndsWith?srchKey=";
        	} else if (parent.getSelectedItem().toString().equals(getString(R.string.scientific_name_contains))){
        		searchText.setHint(getString(R.string.scientific_name_contains));
        		searchType = "scientific";
        		searchUrl = "http://www.itis.gov/ITISWebService/jsonservice/searchByScientificName?srchKey?srchKey=";
        	} else {
        		searchText.setHint(getString(R.string.common_name_contains));
        		searchType = "common";
        		searchUrl = "http://www.itis.gov/ITISWebService/jsonservice/searchByCommonName?srchKey=";
        	}
        }

		public void onNothingSelected(AdapterView<?> parent) {
			
		}
    };
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {    
    	switch (item.getItemId()) {
		    case R.id.menu_about:
		    	//About
	        	AboutDialog about = new AboutDialog(this);
	        	about.setTitle("About this app");
	        	about.show();
		        return true;
	    }
	    return false;
    }
    
    private class AsyncSearch extends AsyncTask<String, Void, String> {

    	private final ListView resultView;
    	private ProgressDialog progressDialog;
    	private final String searchType;
    	
		public AsyncSearch(ListView resultView, String searchType)
		{
			this.resultView = resultView;
			this.searchType = searchType;
		}
		
        @Override
        protected String doInBackground(String... uri) {
            return WebAccess.queryRESTurl(uri[0]);
            
        }      

        @Override
        protected void onPostExecute(String result) { 
        	if (result.startsWith("ERROR-")){
        		progressDialog.dismiss();
        		Toast.makeText(MainActivity.this, result.substring(6), Toast.LENGTH_LONG).show();
        		return;
        	}
        	final ArrayList<SearchResult> searchResultList = new ArrayList<SearchResult>(); 
        	
        	try {
				JSONObject jObject = new JSONObject(result);
				String arrayName = "commonNames";
				String itemName = "commonName";
				if (this.searchType == "scientific") {
					arrayName = "scientificNames";
					itemName = "combinedName";
				}
				JSONArray jArray = jObject.getJSONArray(arrayName);
				Log.i("a", String.valueOf(jArray.length()));
				for (int i=0; i < jArray.length(); i++)
				{
				    JSONObject oneObject = jArray.getJSONObject(i);
				    // Pulling items from the array
					Log.i("a", oneObject.getString(itemName));
				 //   if (oneObject.getString("language") == "English")
				    	searchResultList.add( new SearchResult(oneObject.getString("tsn"), oneObject.getString(itemName)));
				}
			} catch (JSONException e) {
				// If we get here no results returned or else invalid json. either way just don't show anything
				Log.println(Log.ERROR, "a", e.getMessage());
				progressDialog.dismiss();
			}
        	
            if (searchResultList.isEmpty()) {
            	searchResultList.add( new SearchResult("noclick", getString(R.string.no_results_found)));
            }
            
            Collections.sort(searchResultList, new Comparator<SearchResult>(){
            	public int compare(SearchResult s1, SearchResult s2){
            		return s1.getName().compareToIgnoreCase(s2.getName());
            	}
            });

            final ArrayAdapter<SearchResult> listAdapter = new ArrayAdapter<SearchResult>(MainActivity.this, R.layout.simplerow, searchResultList){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = convertView;
                    if (v == null) {
                        LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        v = vi.inflate(R.layout.simplerow, null);
                    }
                    TextView textView = (TextView)v.findViewById(R.id.rowTextView);
                    textView.setText(searchResultList.get(position).getName());
                    v.setTag(searchResultList.get(position).getId());
                    
                    return v;
                }
            };  
              
            progressDialog.dismiss();
            // Set the ArrayAdapter as the ListView's adapter.  
            resultView.setAdapter( listAdapter );        
             
        }

        @Override
        protected void onPreExecute() {
        	 progressDialog = ProgressDialog.show(MainActivity.this, "", "Searching...");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
    
	public void UpdateViews() {
		if (this.orientation == Configuration.ORIENTATION_PORTRAIT)
			setContentView(R.layout.activity_main_portrait);
		else
			setContentView(R.layout.activity_main_landscape);

		final EditText searchText = (EditText) findViewById(R.id.searchText);
        final ListView resultView = (ListView) findViewById(R.id.resultView);
		final Button searchButton = (Button) findViewById(R.id.searchButton);
        
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            	new AsyncSearch(resultView, searchType).execute(searchUrl + searchText.getText().toString());
            }

        });
        
        ArrayList<String> searchTypeList = new ArrayList<String>();
        searchTypeList.add(getString(R.string.common_name_contains));
        searchTypeList.add(getString(R.string.common_name_starts_with));
        searchTypeList.add(getString(R.string.common_name_ends_with));
        searchTypeList.add(getString(R.string.scientific_name_contains));
        Spinner searchTypeSpinner = (Spinner) findViewById(R.id.searchTypeSpinner);
        
        ArrayAdapter<String> searchTypeAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.simplespinnerrow, searchTypeList);
        
        searchTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchTypeSpinner.setAdapter(searchTypeAdapter);

        searchTypeSpinner.setOnItemSelectedListener(searchTypelistener);
        
        resultView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	String tsn = view.getTag().toString();
            	if (!tsn.equals("noclick")){
					Intent intentDetails = new Intent(MainActivity.this, DetailsActivity.class);
					intentDetails.putExtra("TSN", tsn);
					MainActivity.this.startActivity(intentDetails);           
            	}
            
            }
        });

	}
	
	public void UpdateDisplay(){
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    
	    this.orientation = newConfig.orientation;
	    UpdateViews();
	}
	
	public int getScreenOrientation()
    {
        Display getOrient = getWindowManager().getDefaultDisplay();

        int orientation = getOrient.getOrientation();

        // Sometimes you may get undefined orientation Value is 0
        // simple logic solves the problem compare the screen
        // X,Y Co-ordinates and determine the Orientation in such cases
        if(orientation==Configuration.ORIENTATION_UNDEFINED){

            Configuration config = getResources().getConfiguration();
            orientation = config.orientation;

            if(orientation==Configuration.ORIENTATION_UNDEFINED){
                //if height and widht of screen are equal then
                // it is square orientation
                if(getOrient.getWidth()==getOrient.getHeight()){
                    orientation = Configuration.ORIENTATION_SQUARE;
                }else{ //if widht is less than height than it is portrait
                    if(getOrient.getWidth() < getOrient.getHeight()){
                        orientation = Configuration.ORIENTATION_PORTRAIT;
                    }else{ // if it is not any of the above it will defineitly be landscape
                        orientation = Configuration.ORIENTATION_LANDSCAPE;
                    }
                }
            }
        }
        return orientation; // return value 1 is portrait and 2 is Landscape Mode
    }

}
