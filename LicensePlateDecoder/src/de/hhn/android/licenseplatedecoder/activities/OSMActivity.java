package de.hhn.android.licenseplatedecoder.activities;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import de.hhn.android.licenseplatedecoder.R;
import de.hhn.android.licenseplatedecoder.decoder.RegionInformation;

/*
 ** CLASS NAME: 
 **	  OSMActivity
 **
 ** DESCRIPTION:
 **   This class represents the Open Street Maps Activity Viewer
 **
 **
 ** DEVELOPED BY:
 **   Mario Orozco Borrego (MOB)
 **
 ** SUPERVISED BY:
 **	  Prof. Dr.-Ing. Permantier, Gerald
 **
 **		
 ** NOTES:
 **   It's dependent of osmdroid-android-3.0.10.jar
 **
 */

public class OSMActivity extends Activity{

	
	private MapView mapView;
	private MapController mapController;
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.osmview);
        
        mapView = (MapView) findViewById(R.id.osmView);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        
        mapController = mapView.getController();
        mapController.setZoom(2);
        
        RegionInformation regInfo = getIntent().getExtras().getParcelable("region-info");
        
        String buildQuery = regInfo.getRegionName() + ", " + regInfo.getCountryName();
        Toast.makeText(this, 
                "Query: " + buildQuery, 
                Toast.LENGTH_SHORT).show();
        new ProcessQuery().execute(buildQuery);   
    }
    
    public JSONArray searchLocation(String query) {
        JSONArray results = new JSONArray();

        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return results;
        }
        String url = "http://nominatim.openstreetmap.org/search?";
        url += "q=" + query;
        url += "&format=json";

        HttpGet httpGet = new HttpGet(url);
        DefaultHttpClient httpClient = new DefaultHttpClient();

        try {
            HttpResponse response = httpClient.execute(httpGet);
            String content = EntityUtils.toString(response.getEntity(), "utf-8");
            results = new JSONArray(content);

        } catch (Exception e) {
            Log.e("searchLocation", 
                  "Error executing url: " + url + "; " + e.getMessage());
        }

        return results;
    }
    

    private class ProcessQuery extends AsyncTask<String, Void, JSONArray> {
       @Override
       protected JSONArray doInBackground(String... query) {
           return searchLocation(query[0]);
       }
       // onPostExecute displays the results of the AsyncTask.
       @Override
       protected void onPostExecute(JSONArray result) {
    	   if (result.length() > 0) {
               try {
                   JSONObject firstResult = (JSONObject)result.get(0);
                   Double lat = firstResult.getDouble("lat");
                   Double lon = firstResult.getDouble("lon");

                   GeoPoint point = new GeoPoint((int) (lat * 1E6),
                                                 (int) (lon * 1E6));
                   mapController.setZoom(15);
                   //mapController.setCenter(point);
                   mapController.animateTo(point);

                   mapView.invalidate();

               } catch (JSONException e) {
                   Log.e("OnClickListener", e.getMessage());
               }
           } else {
               Toast.makeText(OSMActivity.this, 
                              "No results found", 
                              Toast.LENGTH_SHORT).show();
           }
       }
   
    }
    
    
    
 
    
}
