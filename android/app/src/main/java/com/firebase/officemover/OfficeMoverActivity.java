package com.firebase.officemover;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.officemover.model.OfficeLayout;
import com.firebase.officemover.model.OfficeThing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OfficeMoverActivity extends Activity {
    private final static String TAG = OfficeMoverActivity.class.getSimpleName();
    public static final String FIREBASE = "https://office-mover-demo.firebaseio.com";

    // TODO: make these not random numbers
    public static final int ACTION_ROTATE_ID = 42;
    public static final int ACTION_DELETE_ID = 43;
    public static final int ACTION_EDIT_ID = 44;

    private OfficeLayout mOfficeLayout;
    private OfficeCanvasView mOfficeCanvasView;
    private FrameLayout mOfficeFloorView;
    private Firebase mFirebaseRef;
    private Menu mActionMenu;
    private OfficeThing mSelectedThing;
    private String authToken;

    private ScheduledExecutorService mFirebaseUpdateScheduler;
    private HashMap<String, OfficeThing> mStuffToUpdate = new HashMap<String, OfficeThing>();

    public abstract class ThingChangeListener {
        public abstract void thingChanged(String key, OfficeThing officeThing);
    }

    public abstract class ThingFocusChangeListener {
        public abstract void thingChanged(OfficeThing officeThing);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_office_mover);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            authToken = extras.getString("authToken");
        } else {
            throw new RuntimeException("not auth'd");
        }

        mOfficeLayout = new OfficeLayout();
        mOfficeCanvasView = (OfficeCanvasView) findViewById(R.id.office_canvas);
        mOfficeCanvasView.setOfficeLayout(mOfficeLayout);
        mOfficeFloorView = (FrameLayout) findViewById(R.id.office_floor);

        Firebase.setAndroidContext(this);

        // add an on child added listener for the table
        mFirebaseRef = new Firebase(FIREBASE);
        mFirebaseRef.authWithOAuthToken("google", authToken, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Log.v(TAG, "Authentication worked");
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                throw new RuntimeException("Auth failed :(" + firebaseError.getMessage());
            }
        });

        // Listen for floor changes
        mFirebaseRef.child("background").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String floor = dataSnapshot.getValue(String.class);
                if (floor == null || floor.equals("none")) {
                    mOfficeFloorView.setBackground(null);
                } else if (floor.equals("carpet")) {
                    mOfficeFloorView.setBackground(getResources().getDrawable(R.drawable.floor_carpet));
                } else if (floor.equals("grid")) {
                    mOfficeFloorView.setBackground(getResources().getDrawable(R.drawable.floor_grid));
                } else if (floor.equals("tile")) {
                    mOfficeFloorView.setBackground(getResources().getDrawable(R.drawable.floor_tile));
                } else if (floor.equals("wood")) {
                    mOfficeFloorView.setBackground(getResources().getDrawable(R.drawable.floor_wood));
                }
                mOfficeFloorView.invalidate();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v(TAG, "Canceled!" + firebaseError);

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
                mStuffToUpdate.put(key, officeThing);
                mOfficeCanvasView.invalidate();
            }
        });


        mOfficeCanvasView.setThingFocusChangeListener(new ThingFocusChangeListener() {
            @Override
            public void thingChanged(OfficeThing officeThing) {
                mSelectedThing = officeThing;

                if (mActionMenu != null) {
                    mActionMenu.removeItem(ACTION_ROTATE_ID);
                    mActionMenu.removeItem(ACTION_DELETE_ID);
                    mActionMenu.removeItem(ACTION_EDIT_ID);

                    if (officeThing != null && officeThing.getType().equals("desk")) {
                        // show desk menu
                        mActionMenu.add(Menu.NONE, ACTION_ROTATE_ID, Menu.NONE, "Rotate");
                        mActionMenu.add(Menu.NONE, ACTION_DELETE_ID, Menu.NONE, "Delete");
                        mActionMenu.add(Menu.NONE, ACTION_EDIT_ID, Menu.NONE, "Edit");
                    } else if (officeThing != null) {
                        // show everything else menu
                        mActionMenu.add(Menu.NONE, ACTION_ROTATE_ID, Menu.NONE, "Rotate");
                        mActionMenu.add(Menu.NONE, ACTION_DELETE_ID, Menu.NONE, "Delete");
                    }
                }
            }
        });

        mFirebaseUpdateScheduler = Executors.newScheduledThreadPool(1);
        mFirebaseUpdateScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (mStuffToUpdate != null && mStuffToUpdate.size() > 0) {
                    for (OfficeThing officeThing : mStuffToUpdate.values()) {
                        updateOfficeThing(officeThing.getKey(), officeThing);
                        mStuffToUpdate.remove(officeThing.getKey());
                    }
                }
            }
        }, 40, 40, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            case ACTION_ROTATE_ID:
                if (mSelectedThing != null) {
                    int rotation = mSelectedThing.getRotation();

                    if (rotation >= 270) {
                        mSelectedThing.setRotation(0);
                    } else {
                        mSelectedThing.setRotation(rotation + 90);
                    }
                    updateOfficeThing(mSelectedThing.getKey(), mSelectedThing);
                }
                break;
            case ACTION_DELETE_ID:
                deleteOfficeThing(mSelectedThing.getKey(), mSelectedThing);
                break;
            case ACTION_EDIT_ID:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText entry = new EditText(this);

                builder.setMessage("Edit the name for this desk")
                        .setTitle("Edit name").setView(entry);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String text = entry.getText().toString();
                        mSelectedThing.setName(text);
                        updateOfficeThing(mSelectedThing.getKey(), mSelectedThing);
                    }
                });
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.office_mover, menu);
        mActionMenu = menu;
        return true;
    }

    private void renderNewThingPopup() {
        View menuItemView = findViewById(R.id.action_new_thing);
        PopupMenu popup = new PopupMenu(this, menuItemView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.add_office_thing, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String menuName = getResources().getResourceName(item.getItemId());
                if(menuName.contains("action_add_")) {
                    String newThingName = menuName.split("action_add_")[1];
                    addOfficeThing(newThingName);
                } else {
                    //TODO: better exception
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
                        mFirebaseRef.child("background").removeValue();
                }
                return true;
            }
        });
        popup.show();
    }

    /**
     * Saves a new thing to Firebase, which is then picked up and displayed by
     * the view
     *
     * @param thingType
     */
    private void addOfficeThing(String thingType) {
        if (null == thingType) throw new IllegalArgumentException();

        OfficeThing newThing = new OfficeThing();
        newThing.setType(thingType);
        newThing.setzIndex(mOfficeCanvasView.getOfficeLayout().getHighestzIndex() + 1);
        newThing.setRotation(0);
        newThing.setName("");
        newThing.setLeft(mOfficeCanvasView.screenToModel(mOfficeCanvasView.getWidth()) / 2);
        newThing.setTop(mOfficeCanvasView.screenToModel(mOfficeCanvasView.getHeight()) / 2);

        Log.w(TAG, "Added thing to firebase " + newThing);

        Firebase newThingFirebaseRef = mFirebaseRef.child("furniture").push();
        newThingFirebaseRef.setValue(newThing, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.w(TAG, "Add failed! " + firebaseError.getMessage());
                }
            }
        });
    }

    public void updateOfficeThing(String key, OfficeThing officeThing) {
        if (null == key || null == officeThing) throw new IllegalArgumentException();

        // re-apply the cached key, just in case
        officeThing.setKey(key);

        mFirebaseRef.child("furniture").child(key).setValue(officeThing, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.w(TAG, "Update failed! " + firebaseError.getMessage());
                }
            }
        });
    }

    public void deleteOfficeThing(String key, OfficeThing officeThing) {
        if (null == key || null == officeThing) throw new IllegalArgumentException();

        mFirebaseRef.child("furniture").child(key).removeValue();
    }

    /**
     * Adds a thing to the local model used in rendering
     *
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
     *
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
