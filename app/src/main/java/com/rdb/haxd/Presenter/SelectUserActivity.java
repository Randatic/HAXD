package com.rdb.haxd.Presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.PublishOptions;
import com.backendless.messaging.PushPolicyEnum;
import com.backendless.services.messaging.MessageStatus;
import com.backendless.services.messaging.PublishStatusEnum;
import com.rdb.haxd.Model.DefaultCallback;
import com.rdb.haxd.Model.DefaultMessages;
import com.rdb.haxd.Model.Defaults;
import com.rdb.haxd.Model.Hacker;
import com.rdb.haxd.R;

import java.util.ArrayList;
import java.util.List;

import static com.rdb.haxd.Model.Hacker.currentUser;

public class SelectUserActivity extends Activity
{
    private final String TAG = "SelectUserActivity";
  private Button previousPageButton, nextPageButton;
  private ListView usersList;

  private HackerAdapter adapter;

  private List<Hacker> users;
  private Hacker companion;
  private int totalPages;
  private int currentPageNumber;

  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.select_user );

    currentPageNumber = 1;

    //initUI();
      usersList = (ListView) findViewById( R.id.usersList );
      //previousPageButton = (Button) findViewById( R.id.previousButton );
      //nextPageButton = (Button) findViewById( R.id.nextButton );

      usersList.setOnItemClickListener( new AdapterView.OnItemClickListener()
      {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id )
          {
              onListItemClick( position );
          }
      } );
    initList();
  }

  private void initUI()
  {


    /*previousPageButton.setOnClickListener( new View.OnClickListener()
    {
      @Override
      public void onClick( View v )
      {
        users.previousPage(new DefaultCallback<BackendlessCollection<Hacker>>(SelectUserActivity.this) {
          @Override
          public void handleResponse(BackendlessCollection<Hacker> response) {
            super.handleResponse(response);
            initList(response);
          }
        });
      }
    } );

    nextPageButton.setOnClickListener( new View.OnClickListener()
    {
        @Override
        public void onClick( View v )
        {
          users.nextPage( new DefaultCallback<BackendlessCollection<Hacker>>( SelectUserActivity.this )
          {
            @Override
            public void handleResponse( BackendlessCollection<Hacker> response )
            {
              super.handleResponse( response );
              initList( response );
              currentPageNumber++;
              initButtons();
            }
          } );
        }
    } );*/
  }

  private void onListItemClick( int position )
  {
      Hacker c = (Hacker) usersList.getItemAtPosition(position);
      Log.d(TAG, "onListItemClick: "+c.info());
    final String companionNickname = c.getUsername();
    String whereClause = "username = '".concat( companionNickname ).concat( "'" );

    Backendless.Persistence.of(Hacker.class).findById(c.getObjectId(), new AsyncCallback<Hacker>() {
        @Override
        public void handleResponse(Hacker response) {
            companion = new Hacker();
            companion.set(response);
            if(!companion.getObjectId().equals(currentUser().getObjectId())){
                onCompanionFound();
            }

        }

        @Override
        public void handleFault(BackendlessFault fault) {

        }
    });
  }

  private void onCompanionFound()
  {

    PublishOptions publishOptions = new PublishOptions();
    publishOptions.putHeader( PublishOptions.ANDROID_TICKER_TEXT_TAG, String.format( DefaultMessages.CONNECT_DEMAND, currentUser().getUsername() ) );
    publishOptions.putHeader( PublishOptions.ANDROID_CONTENT_TITLE_TAG, getResources().getString( R.string.app_name ) );
    publishOptions.putHeader( PublishOptions.ANDROID_CONTENT_TEXT_TAG, String.format( DefaultMessages.CONNECT_DEMAND, currentUser().getUsername() ) );
    DeliveryOptions deliveryOptions = new DeliveryOptions();
    deliveryOptions.setPushPolicy( PushPolicyEnum.ONLY );
    deliveryOptions.addPushSinglecast( companion.getDeviceId() );

    final String message_subtopic = Hacker.currentUser().getUsername().concat( "_with_" ).concat( companion.getUsername() );

    Backendless.Messaging.publish( Defaults.DEFAULT_CHANNEL, message_subtopic, publishOptions, deliveryOptions, new DefaultCallback<MessageStatus>( this, "Sending push message" )
    {
      @Override
      public void handleResponse( MessageStatus response )
      {
        super.handleResponse( response );

        PublishStatusEnum messageStatus = response.getStatus();

        if( messageStatus == PublishStatusEnum.SCHEDULED )
        {
          Intent chatIntent = new Intent( SelectUserActivity.this, ChatActivity.class );
          chatIntent.putExtra( "owner", true );
          chatIntent.putExtra( "subtopic", message_subtopic );
          chatIntent.putExtra( "companionNickname", companion.getUsername() );
          startActivity( chatIntent );
          finish();
        }
        else
        {
          Toast.makeText( SelectUserActivity.this, "Message status: " + messageStatus.toString(), Toast.LENGTH_SHORT ).show();
        }
      }
    } );
  }

  private void initList()
  {
      Backendless.Persistence.of(Hacker.class).find(new AsyncCallback<BackendlessCollection<Hacker>>() {
          @Override
          public void handleResponse(BackendlessCollection<Hacker> response) {
              users = response.getData();
              //String[] usersArray = removeNulls( response );
              adapter = new HackerAdapter(SelectUserActivity.this, users);
              usersList.setAdapter( adapter );
          }

          @Override
          public void handleFault(BackendlessFault fault) {

          }
      });



  }

  private void initButtons()
  {
    //previousPageButton.setEnabled( currentPageNumber != 1 );
    //nextPageButton.setEnabled( currentPageNumber != totalPages );
  }

  private String[] removeNulls(BackendlessCollection<Hacker> users )
  {
    List<String> result = new ArrayList<String>();
    for( int i = 0; i < users.getCurrentPage().size(); i++ )
    {
      Hacker hacker = users.getCurrentPage().get( i );
      if( hacker.getUsername() != null && hacker.getDeviceId() != null && !hacker.getDeviceId().isEmpty() && !hacker.getUsername().isEmpty() )
      {
        result.add( hacker.getUsername() );
      }
    }

    return result.toArray( new String[ result.size() ] );
  }
}

                                            