package com.rushdevo.camcamtv;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.MediaController;
import android.widget.VideoView;

public class CamCamTVActivity extends Activity {
	
	//public static String DOMAIN = "www.epiccamcam.com";
	public static String DOMAIN = "10.0.1.28:3000"; // development
	public static String USER_ID = "1"; // temporarily hard coded
	public static String GCM_PROJECT_ID = "945612395303";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cam_cam_tv);
		showFeeds();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void showFeeds() {
    	Intent intent = new Intent(this, ShowFeedsActivity.class);
        startActivity(intent);      
        finish();
    }

}
