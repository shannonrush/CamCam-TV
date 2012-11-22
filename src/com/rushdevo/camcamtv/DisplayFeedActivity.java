package com.rushdevo.camcamtv;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;
import android.support.v4.app.NavUtils;

public class DisplayFeedActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_feed);
		Intent intent = getIntent();
		String videoURL = intent.getStringExtra("videoURL");
		displayFeed(videoURL);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_display_feed, menu);
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
	
	private void displayFeed(String videoURL) {
		VideoView videoView = (VideoView) findViewById(R.id.videoView);
		Uri uri = Uri.parse("http://"+CamCamTVActivity.DOMAIN+videoURL);
	    videoView.setVideoURI(uri);
	    videoView.setMediaController(new MediaController(this));
	    videoView.requestFocus();
	}

}
