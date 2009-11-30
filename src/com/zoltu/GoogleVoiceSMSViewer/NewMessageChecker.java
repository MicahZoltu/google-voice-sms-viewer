package com.zoltu.GoogleVoiceSMSViewer;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class NewMessageChecker extends Service implements AccountManagerCallback<Bundle>
{
	public static final int NOTIFICATION_ID_AUTHENTICATION = 1;
	
	@Override public void onCreate()
	{
		super.onCreate();
		
		AccountManager lAccountManager = AccountManager.get(this);
		
		Log.d(getClass().getName(), "Authenticator Descriptions:");
		AuthenticatorDescription[] lAuthenticatorDescriptions = lAccountManager.getAuthenticatorTypes();
		for (AuthenticatorDescription lAuthenticatorDescription : lAuthenticatorDescriptions)
		{
			Log.d(getClass().getName(), lAuthenticatorDescription.type);
		}
		
		Log.d(getClass().getName(), "Accounts:");
		Account[] lAccounts = lAccountManager.getAccounts();
		for (Account lAccount : lAccounts)
		{
			Log.d(getClass().getName(), lAccount.name);
		}
		
		lAccountManager.addAccount("com.google", "GOOGLE", null, null, null, this, null);
	}
	
	@Override public IBinder onBind(Intent pIntent)
	{
		return null;
	}
	
	/**  **/
	@Override public void run(AccountManagerFuture<Bundle> pFuture)
	{
		Bundle lBundle;
		try
		{
			lBundle = pFuture.getResult();
		}
		catch (Exception lException)
		{
			lException.printStackTrace();
			return;
		}
		
		if (lBundle.containsKey(AccountManager.KEY_INTENT))
		{
			Intent lIntent = (Intent)lBundle.getParcelable(AccountManager.KEY_INTENT);
			NotificationManager lNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			
			int lIcon = R.drawable.icon;
			String lShortMessage = getResources().getString(R.string.authentication_intent_notification_message_short);
			String lLongMessage = getResources().getString(R.string.authentication_intent_notification_message_long);
			long lCurrentTime = System.currentTimeMillis();
			PendingIntent lPendingIntent = PendingIntent.getActivity(this, 0, lIntent, PendingIntent.FLAG_ONE_SHOT);
			
			Notification lNotification = new Notification(lIcon, lShortMessage, lCurrentTime);
			lNotification.setLatestEventInfo(this, lShortMessage, lLongMessage, lPendingIntent);
			
			lNotificationManager.notify(NOTIFICATION_ID_AUTHENTICATION, lNotification);
			
			return;
		}
		
		if (lBundle.containsKey(AccountManager.KEY_ACCOUNT_NAME) && lBundle.containsKey(AccountManager.KEY_ACCOUNT_TYPE) && lBundle.containsKey(AccountManager.KEY_AUTHTOKEN))
		{
			String lAccountName = lBundle.getString(AccountManager.KEY_ACCOUNT_NAME);
			String lAccountType = lBundle.getString(AccountManager.KEY_ACCOUNT_TYPE);
			String lAuthToken = lBundle.getString(AccountManager.KEY_AUTHTOKEN);
			return;
		}
		
		if (lBundle.containsKey(AccountManager.KEY_ERROR_CODE))
		{
			switch (lBundle.getInt(AccountManager.KEY_ERROR_CODE))
			{
				case AccountManager.ERROR_CODE_CANCELED:
				{
					return;
				}
				default:
				{
					int lErrorCode = lBundle.getInt(AccountManager.KEY_ERROR_CODE);
					String lErrorMessage = lBundle.getString(AccountManager.KEY_ERROR_MESSAGE);
					return;
				}
			}
		}
	}
}
