package com.arriwe.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.arriwe.wayndr.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sancsvision.arriwe.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Admin on 23-02-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
	
	private static final String TAG = "MyFirebaseMsgService";
	Bitmap bitmap;
	
	/**
	 * Called when message is received.
	 *
	 * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
	 */
	// [START receive_message]
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		// [START_EXCLUDE]
		// There are two types of messages data messages and notification messages. Data messages are handled
		// here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
		// traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
		// is in the foreground. When the app is in the background an automatically generated notification is displayed.
		// When the user taps on the notification they are returned to the app. Messages containing both notification
		// and data payloads are treated as notification messages. The Firebase console always sends notification
		// messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
		// [END_EXCLUDE]
		
		// TODO(developer): Handle FCM messages here.
		// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
		Log.d(TAG, "From: " + remoteMessage.getFrom());
		
		// Check if message contains a data payload.
		if (remoteMessage.getData().size() > 0) {
			Log.d(TAG, "Message data payload: " + remoteMessage.getData());
		}
		
		// Check if message contains a notification payload.
		if (remoteMessage.getNotification() != null) {
			Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
		}
		
		// Also if you intend on generating your own notifications as a result of a received FCM
		// message, here is where that should be initiated. See sendNotification method below.
		
		String message = remoteMessage.getData().get("message");
		String imageUri = remoteMessage.getData().get("image");
		String TrueOrFlase = remoteMessage.getData().get("AnotherActivity");
		bitmap = getBitmapfromUrl(imageUri);
		
	//	sendNotification(message, bitmap, TrueOrFlase);
	}
	// [END receive_message]
	
	/**
	 * Create and show a simple notification containing the received FCM message.
	 *
	 * @param messageBody FCM message body received.
	 */
	private void sendNotification(String messageBody, Bitmap image, String TrueOrFalse) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("AnotherActivity", TrueOrFalse);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
		    PendingIntent.FLAG_ONE_SHOT);
		
		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
		    .setLargeIcon(image)
		    .setSmallIcon(R.mipmap.launcher)
		    .setContentTitle("ARRIWE")
		    .setContentText(messageBody)
		    .setStyle(new NotificationCompat.BigPictureStyle()
			.bigPicture(image))
		    .setAutoCancel(true)
		    .setSound(defaultSoundUri)
		    .setContentIntent(pendingIntent);
		
		NotificationManager notificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
		
		//showprogress();
		
		
	}
	
	private void showprogress() {
		final NotificationManager mNotifyManager;
		final NotificationCompat.Builder mBuilder;
		final int id = 1;
		mNotifyManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContentTitle("Picture Download")
		    .setContentText("Download in progress")
		    .setSmallIcon(R.mipmap.launcher);
// Start a lengthy operation in a background thread
		new Thread(
		    new Runnable() {
			    @Override
			    public void run() {
				    int incr;
				    // Do the "lengthy" operation 20 times
				    for (incr = 0; incr <= 100; incr += 5) {
					    // Sets the progress indicator to a max value, the
					    // current completion percentage, and "determinate"
					    // state
					    mBuilder.setProgress(100, incr, false);
					    // Displays the progress bar for the first time.
					    mNotifyManager.notify(id, mBuilder.build());
					    // Sleeps the thread, simulating an operation
					    // that takes time
					    try {
						    // Sleep for 5 seconds
						    Thread.sleep(5 * 1000);
					    } catch (InterruptedException e) {
						    Log.d(TAG, "sleep failure");
					    }
				    }
				    // When the loop is finished, updates the notification
				    mBuilder.setContentText("Download complete")
					// Removes the progress bar
					.setProgress(0, 0, false);
				    mNotifyManager.notify(id, mBuilder.build());
			    }
		    }
// Starts the thread by calling the run() method in its Runnable
		).start();
	}
	
	public Bitmap getBitmapfromUrl(String imageUrl) {
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(input);
			return bitmap;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
			
		}
	}
}