package com.rushdevo.camcamtv;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ShowFeedsActivity extends ListActivity {

	private ArrayAdapter<String> adapter;
	private JSONArray deviceArray;
	private ArrayList<String> deviceNames;
	private String selectedDeviceID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_feeds);
		new GetUserDevicesTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_show_feeds, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// feed list
	
	private void initializeList() {
		ListView listView = getListView();
		listView.setClickable(true);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		  @Override
		  public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			 Log.d("ShowFeedsActivity", "in onItemClick");
			 JSONObject device = (JSONObject) deviceArray.get(position);
			 selectedDeviceID = String.valueOf(device.get("id"));
		     new RequestFeedTask().execute();
		  }
		});
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceNames);
        setListAdapter(adapter);
	}
	
	// async tasks 
	
	public final class GetUserDevicesTask extends AsyncTask<String, Boolean, String> {

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			try {
			    HttpClient httpClient = new DefaultHttpClient();
			    String url = "http://"+CamCamTVActivity.DOMAIN+"/users/"+CamCamTVActivity.USER_ID+"/devices.json";
			    HttpResponse response = httpClient.execute(new HttpGet(url));
			    HttpEntity entity = response.getEntity();
			    result = EntityUtils.toString(entity);
			} catch (Exception e) {
			    Log.d("ShowFeedsActivity", "Network exception "+e);
			}
			return result;
        }		
		
		@Override
        protected void onPostExecute(String result) {
			  Object obj=JSONValue.parse(result);
			  deviceArray = (JSONArray)obj;
			  deviceNames = new ArrayList<String>();
			  for (int i = 0; i < deviceArray.size(); ++i) {
				    JSONObject device = (JSONObject) deviceArray.get(i);
				    deviceNames.add((String) device.get("name"));
			  }
			  initializeList();
		}	
	}
	
	public final class RequestFeedTask extends AsyncTask<String, Boolean, String> {

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			HttpClient httpclient = new DefaultHttpClient();
			String url = "http://"+CamCamTVActivity.DOMAIN+"/devices/"+selectedDeviceID+"/request_feed.json";
		    HttpPost httppost = new HttpPost(url);

		    try {
		        HttpResponse response = httpclient.execute(httppost);
		        result = response.getStatusLine().toString();
		        Log.d("RequestFeedTask", "GCM Response: "+response.getEntity());
		    } catch (ClientProtocolException e) {
		    	Log.d("RequestFeedTask", "ClientProtocolException: "+e);
		    } catch (IOException e) {
		    	Log.d("RequestFeedTask", "IOException: "+e);
		    }
			return result;

		}
		
		@Override
		protected void onPostExecute(String result) {
			// start polling for feed 
			new PollForFeedTask().execute();
		}
		
	}
	
	public final class PollForFeedTask extends AsyncTask<String, Boolean, String> {
		
		@Override
		protected String doInBackground(String... params) {
			String result = "";
			try {
			    HttpClient httpClient = new DefaultHttpClient();
			    String url = "http://"+CamCamTVActivity.DOMAIN+"/devices/"+selectedDeviceID+"/feeds.json";
			    HttpResponse response = httpClient.execute(new HttpGet(url));
			    HttpEntity entity = response.getEntity();
			    result = EntityUtils.toString(entity);
			} catch (Exception e) {
			    Log.d("ShowFeedsActivity", "Network exception "+e);
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Object obj=JSONValue.parse(result);
			JSONObject json = (JSONObject) obj;
			String status = (String)json.get("status");
			Log.d("PollForFeedTask", "Poll result status: "+status);
			if (status.equals("unavailable")) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				new PollForFeedTask().execute();
			} else if (status.equals("available")) {
				Log.d("PollForFeedTask", "MP4 url: "+(String)json.get("video_url"));
				Intent intent = new Intent(getApplicationContext(), DisplayFeedActivity.class);
	        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	intent.putExtra("videoURL", (String)json.get("video_url"));
	            startActivity(intent);
			}
		}
	}
}