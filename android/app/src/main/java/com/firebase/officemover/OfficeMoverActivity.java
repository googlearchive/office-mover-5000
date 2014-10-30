package com.firebase.officemover;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.officemover.model.OfficeLayout;
import com.firebase.officemover.model.OfficeThing;

public class OfficeMoverActivity extends Activity {

    private OfficeCanvasView mOffice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_office_mover);

        mOffice = (OfficeCanvasView)findViewById(R.id.office_canvas);

        Firebase.setAndroidContext(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // add an on child added listener for the table
        Firebase myFirebaseRef = new Firebase("https://office-mover2.firebaseio.com/");
        myFirebaseRef.child("furniture").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                OfficeThing existingThing = dataSnapshot.getValue(OfficeThing.class);

                mOffice.addExistingThing(existingThing);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.office_mover, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            //TODO: do this with less copy and paste
            case R.id.action_add_android:
                mOffice.addNewThing("android");
                break;
            case R.id.action_add_ballpit:
                mOffice.addNewThing("ballpit");
                break;
            case R.id.action_add_desk:
                mOffice.addNewThing("desk");
                break;
            case R.id.action_add_dog_corgi:
                mOffice.addNewThing("dog_corgi");
                break;
            case R.id.action_add_dog_retriever:
                mOffice.addNewThing("dog_retriever");
                break;
            case R.id.action_add_laptop:
                mOffice.addNewThing("laptop");
                break;
            case R.id.action_add_nerfgun:
                mOffice.addNewThing("nerfgun");
                break;
            case R.id.action_add_pacman:
                mOffice.addNewThing("pacman");
                break;
            case R.id.action_add_pingpong:
                mOffice.addNewThing("pingpong");
                break;
            case R.id.action_add_plant:
                mOffice.addNewThing("plant");
                break;
            case R.id.action_add_plant2:
                mOffice.addNewThing("plant2");
                break;
            case R.id.action_add_redstapler:
                mOffice.addNewThing("redstapler");
                break;
            default:
                throw new RuntimeException();
        }
        return super.onOptionsItemSelected(item);
    }
}
