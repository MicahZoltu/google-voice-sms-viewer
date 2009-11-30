package com.zoltu.GoogleVoiceSMSViewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Inbox extends Activity
{
	@Override public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Intent lIntent = new Intent(this, NewMessageChecker.class);
		this.startService(lIntent);
	}
}
