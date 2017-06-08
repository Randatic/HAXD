package com.rdb.haxd.Model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.backendless.messaging.PublishOptions;
import com.backendless.push.BackendlessPushService;
import com.rdb.haxd.Presenter.AcceptChatActivity;

/**
 * Created by Randy Bruner on 6/7/2017.
 */

public class PushService extends BackendlessPushService {

    private final String TAG = "PushService";

    @Override
    public void onRegistered(Context context, String registrationId )
    {
        Toast.makeText( context, "device registered" + registrationId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnregistered( Context context, Boolean unregistered )
    {
        Toast.makeText( context, "device unregistered", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMessage( Context context, Intent intent )
    {
        CharSequence tickerText = intent.getStringExtra( PublishOptions.ANDROID_TICKER_TEXT_TAG );
        CharSequence contentTitle = intent.getStringExtra( PublishOptions.ANDROID_CONTENT_TITLE_TAG );
        CharSequence contentText = intent.getStringExtra( PublishOptions.ANDROID_CONTENT_TEXT_TAG );
        String subtopic = intent.getStringExtra( "message" );

        if( tickerText != null && tickerText.length() > 0 )
        {
            int appIcon = context.getApplicationInfo().icon;
            if( appIcon == 0 )
                appIcon = android.R.drawable.sym_def_app_icon;

            Intent notificationIntent = new Intent( context, AcceptChatActivity.class );
            notificationIntent.putExtra( "subtopic", subtopic );
            PendingIntent contentIntent = PendingIntent.getActivity( context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT );

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder( context );
            notificationBuilder.setSmallIcon( appIcon );
            notificationBuilder.setTicker( tickerText );
            notificationBuilder.setWhen( System.currentTimeMillis() );
            notificationBuilder.setContentTitle( contentTitle );
            notificationBuilder.setContentText( contentText );
            notificationBuilder.setAutoCancel( true );
            notificationBuilder.setContentIntent( contentIntent );

            Notification notification = notificationBuilder.build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE );
            notificationManager.notify( 0, notification );
        }

        return false;

        //String message = intent.getStringExtra( "message" );
        //Toast.makeText( context, "Push message received. Message: " + message, Toast.LENGTH_LONG ).show();
        // When returning 'true', default Backendless onMessage implementation will be executed.
        // The default implementation displays the notification in the Android Notification Center.
        // Returning false, cancels the execution of the default implementation.
        //return true;
    }

    @Override
    public void onError( Context context, String message )
    {
        Toast.makeText( context, message, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "handleFault: "+message);
    }

}
