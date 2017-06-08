package com.rdb.haxd.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.PublishOptions;
import com.backendless.services.messaging.MessageStatus;
import com.backendless.services.messaging.PublishStatusEnum;
import com.rdb.haxd.Model.DefaultCallback;
import com.rdb.haxd.Model.DefaultMessages;
import com.rdb.haxd.Model.Defaults;
import com.rdb.haxd.Model.Hacker;
import com.rdb.haxd.R;

public class AcceptChatActivity extends Activity
{
  private Button acceptButton, declineButton;
  private TextView chatInvitationTextView;

  private PublishOptions publishOptions;
  private String subtopic;
  private String companionNickname;

  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.accept_chat );

    Intent currentIntent = getIntent();
    subtopic = currentIntent.getStringExtra( "subtopic" );
    companionNickname = subtopic.split( "_" )[ 0 ];

    initUI();

    Backendless.setUrl( Defaults.SERVER_URL );
    Backendless.initApp( this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION );

    publishOptions = new PublishOptions();
    publishOptions.setSubtopic( subtopic );
  }

  private void initUI()
  {
    acceptButton = (Button) findViewById( R.id.acceptButton );
    declineButton = (Button) findViewById( R.id.declineButton );
    chatInvitationTextView = (TextView) findViewById( R.id.chatInvitationText );

    chatInvitationTextView.setText( String.format( getResources().getString( R.string.chat_invitation_message ), companionNickname ) );

    acceptButton.setOnClickListener( new View.OnClickListener()
    {
      @Override
      public void onClick( View v )
      {
        onAccept();
      }
    } );

    declineButton.setOnClickListener( new View.OnClickListener()
    {
      @Override
      public void onClick( View v )
      {
        onDecline();
      }
    } );
  }

  private void onAccept()
  {
    Backendless.Messaging.registerDevice( Defaults.GOOGLE_PROJECT_ID, Defaults.DEFAULT_CHANNEL, new AsyncCallback<Void>()
    {
      @Override
      public void handleResponse( Void response )
      {
        Toast.makeText( AcceptChatActivity.this, "Registered", Toast.LENGTH_SHORT ).show();
      }

      @Override
      public void handleFault( BackendlessFault fault )
      {
        Toast.makeText( AcceptChatActivity.this, fault.getMessage(), Toast.LENGTH_SHORT ).show();
      }
    } );
      SharedPreferences settings = getSharedPreferences( "com.backendless.settings", Context.MODE_PRIVATE );
      String id = settings.getString( "id", "" );
      Backendless.Persistence.of(Hacker.class).findById(id, new AsyncCallback<Hacker>() {
          @Override
          public void handleResponse(Hacker response) {
              Hacker.currentUser().set(response);
              final String subtopic = getIntent().getStringExtra( "subtopic" );
              PublishOptions publishOptions = new PublishOptions();
              publishOptions.setSubtopic( subtopic );
              Backendless.Messaging.publish( Defaults.DEFAULT_CHANNEL, DefaultMessages.ACCEPT_CHAT_MESSAGE, publishOptions, new DefaultCallback<MessageStatus>( AcceptChatActivity.this )
              {
                  @Override
                  public void handleResponse( MessageStatus response )
                  {
                      super.handleResponse( response );
                      if( response.getStatus() == PublishStatusEnum.SCHEDULED )
                      {
                          Intent chatIntent = new Intent( AcceptChatActivity.this, ChatActivity.class );
                          chatIntent.putExtra( "owner", false );
                          chatIntent.putExtra( "subtopic", subtopic );
                          startActivity( chatIntent );
                          finish();
                      }
                      else
                      {
                          Toast.makeText( AcceptChatActivity.this, response.getStatus().toString(), Toast.LENGTH_SHORT ).show();
                      }
                  }
              } );
          }

          @Override
          public void handleFault(BackendlessFault fault) {
              onDecline();
              finish();
          }
      });
    /*Intent nextIntent = new Intent( this, ChooseNicknameActivity.class );
    nextIntent.putExtra( "owner", false );
    nextIntent.putExtra( "subtopic", subtopic );
    nextIntent.putExtra( "companionNickname", companionNickname );
    startActivity( nextIntent );
    finish();*/
  }

  private void onDecline()
  {
    Backendless.Messaging.publish( (Object) DefaultMessages.DECLINE_CHAT_MESSAGE, publishOptions, new DefaultCallback<MessageStatus>( this )
    {
      @Override
      public void handleResponse( MessageStatus response )
      {
        super.handleResponse( response );
        if( response.getStatus() == PublishStatusEnum.SCHEDULED )
        {
          finish();
        }
        else
        {
          Toast.makeText( AcceptChatActivity.this, response.getStatus().toString(), Toast.LENGTH_SHORT ).show();
        }
      }
    } );
  }
}
                                            