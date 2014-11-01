package com.firebase.officemover;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.officemover.model.OfficeLayout;
import com.firebase.officemover.model.OfficeThing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

public class OfficeMoverActivity extends Activity {
    private final static String TAG = OfficeMoverActivity.class.getSimpleName();
    public static final String FIREBASE = "https://mover-app-5000-demo.firebaseio.com/";

    private OfficeLayout mOfficeLayout;
    private OfficeCanvasView mOfficeCanvasView;
    private FrameLayout mOfficeFloorView;
    private Firebase mFirebaseRef;

    // For sign out
    private GoogleApiClient mGoogleApiClient;


    public abstract class ThingChangeListener {
        public abstract void thingChanged(String key, OfficeThing officeThing);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_office_mover);

        mOfficeLayout = new OfficeLayout();
        mOfficeCanvasView = (OfficeCanvasView) findViewById(R.id.office_canvas);
        mOfficeCanvasView.setOfficeLayout(mOfficeLayout);
        mOfficeFloorView = (FrameLayout)findViewById(R.id.office_floor);

        Firebase.setAndroidContext(this);

        // add an on child added listener for the table
        mFirebaseRef = new Firebase(FIREBASE);

        // Listen for floor changes
        mFirebaseRef.child("background").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String floor = dataSnapshot.getValue(String.class);
                if(floor.equals("carpet")) {
                    mOfficeFloorView.setBackground(getResources().getDrawable(R.drawable.floor_carpet));
                } else if(floor.equals("grid")) {
                    mOfficeFloorView.setBackground(getResources().getDrawable(R.drawable.floor_grid));
                } else if(floor.equals("tile")) {
                    mOfficeFloorView.setBackground(getResources().getDrawable(R.drawable.floor_tile));
                } else if(floor.equals("wood")) {
                    mOfficeFloorView.setBackground(getResources().getDrawable(R.drawable.floor_wood));
                }
                mOfficeFloorView.invalidate();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // Listen for furniture changes
        mFirebaseRef.child("furniture").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String key = dataSnapshot.getName();
                OfficeThing existingThing = dataSnapshot.getValue(OfficeThing.class);

                Log.v(TAG, "New thing " + existingThing);

                addUpdateThingToLocalModel(key, existingThing);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getName();
                OfficeThing existingThing = dataSnapshot.getValue(OfficeThing.class);

                Log.v(TAG, "Thing changed " + existingThing);

                addUpdateThingToLocalModel(key, existingThing);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getName();

                Log.v(TAG, "Thing removed " + key);

                removeThingFromLocalModel(key);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getName();
                OfficeThing existingThing = dataSnapshot.getValue(OfficeThing.class);

                Log.v(TAG, "Thing moved " + existingThing);

                addUpdateThingToLocalModel(key, existingThing);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v(TAG, "Something canceled");
                //TODO: handle this
                throw new RuntimeException();
            }
        });

        mOfficeCanvasView.setThingChangedListener(new ThingChangeListener() {
            @Override
            public void thingChanged(String key, OfficeThing officeThing) {
                updateOfficeThing(key, officeThing);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

    }

    @Override
    protected void onResume() {
        super.onResume();
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
            case R.id.action_new_thing:
                renderNewThingPopup();
                break;
            case R.id.change_floor:
                renderChangeCarpetPopup();
                break;
            case R.id.action_sign_out:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void renderNewThingPopup() {
        View menuItemView = findViewById(R.id.action_new_thing);
        PopupMenu popup = new PopupMenu(this, menuItemView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.add_office_thing, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    //TODO: do this with less copy and paste
                    case R.id.action_add_android:
                        addOfficeThing("android");
                        break;
                    case R.id.action_add_ballpit:
                        addOfficeThing("ballpit");
                        break;
                    case R.id.action_add_desk:
                        addOfficeThing("desk");
                        break;
                    case R.id.action_add_dog_corgi:
                        addOfficeThing("dog_corgi");
                        break;
                    case R.id.action_add_dog_retriever:
                        addOfficeThing("dog_retriever");
                        break;
                    case R.id.action_add_laptop:
                        addOfficeThing("laptop");
                        break;
                    case R.id.action_add_nerfgun:
                        addOfficeThing("nerfgun");
                        break;
                    case R.id.action_add_pacman:
                        addOfficeThing("pacman");
                        break;
                    case R.id.action_add_pingpong:
                        addOfficeThing("pingpong");
                        break;
                    case R.id.action_add_plant1:
                        addOfficeThing("plant1");
                        break;
                    case R.id.action_add_plant2:
                        addOfficeThing("plant2");
                        break;
                    case R.id.action_add_redstapler:
                        addOfficeThing("redstapler");
                        break;
                    default:
                        throw new RuntimeException();
                }
                return true;
            }
        });
        popup.show();
    }
    private void renderChangeCarpetPopup() {

        View menuItemView = findViewById(R.id.change_floor);
        PopupMenu popup = new PopupMenu(this, menuItemView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.change_floor, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    //TODO: do this with less copy and paste
                    //TODO: make this real time
                    case R.id.action_floor_carpet:
                        mFirebaseRef.child("background").setValue("carpet");
                        break;
                    case R.id.action_floor_grid:
                        mFirebaseRef.child("background").setValue("grid");
                        break;
                    case R.id.action_floor_tile:
                        mFirebaseRef.child("background").setValue("tile");
                        break;
                    case R.id.action_floor_wood:
                        mFirebaseRef.child("background").setValue("wood");
                        break;
                    default:
                        throw new RuntimeException();
                }
                return true;
            }
        });
        popup.show();
    }

    /**
     * Saves a new thing to Firebase, which is then picked up and displayed by
     * the view
     * @param thingType
     */
    private void addOfficeThing(String thingType) {
        if (null == thingType) throw new IllegalArgumentException();

        OfficeThing newThing = new OfficeThing();
        newThing.setType(thingType);
        newThing.setzIndex(mOfficeCanvasView.getOfficeLayout().getHighestzIndex() + 1);
        newThing.setRotation(0);
        newThing.setLeft(mOfficeCanvasView.screenToModel(mOfficeCanvasView.getWidth()) / 2);
        newThing.setTop(mOfficeCanvasView.screenToModel(mOfficeCanvasView.getHeight()) / 2);

        Log.w(TAG, "Added thing to firebase " + newThing);

        Firebase newThingFirebaseRef = mFirebaseRef.child("furniture").push();
        newThingFirebaseRef.setValue(newThing);
    }

    public void updateOfficeThing(String key, OfficeThing officeThing) {
        if (null == key || null == officeThing) throw new IllegalArgumentException();

        // re-apply the cached key, just in case
        officeThing.setKey(key);

        mFirebaseRef.child("furniture").child(key).setValue(officeThing);
    }

    /**
     * Adds a thing to the local model used in rendering
     * @param key
     * @param officeThing
     */
    public void addUpdateThingToLocalModel(String key, OfficeThing officeThing) {
        officeThing.setKey(key);
        mOfficeLayout.put(key, officeThing);
        mOfficeCanvasView.invalidate();
    }

    /**
     * Removes a thing from the local model used in rendering
     * @param key
     */
    public void removeThingFromLocalModel(String key) {
        mOfficeLayout.remove(key);
        mOfficeCanvasView.invalidate();
    }

    public boolean signOut(MenuItem item) {
        Intent signOutIntent = new Intent(this, LoginActivity.class);
        signOutIntent.putExtra("SIGNOUT", true);
        startActivity(signOutIntent);
        return true;
    }
}
