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
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.officemover.model.OfficeLayout;
import com.firebase.officemover.model.OfficeThing;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @author Jenny Tong (mimming)
 *
 * This is the main Activity for Office Mover. It manages the Firebase client and all of the
 * listeners.
 */
public class OfficeMoverActivity extends Activity {
    private static final String TAG = OfficeMoverActivity.class.getSimpleName();

    //TODO: Update to your Firebase
    public static final String FIREBASE = "https://<your-firebase>.firebaseio.com/";

    // How often (in ms) we push write updates to Firebase
    private static final int UPDATE_THROTTLE_DELAY = 40;

    // The Firebase client
    private Firebase mFirebaseRef;

    // The office layout
    private OfficeLayout mOfficeLayout;

    // The currently selected thing in the office
    private OfficeThing mSelectedThing;

    // A list of elements to be written to Firebase on the next push
    private HashMap<String, OfficeThing> mStuffToUpdate = new HashMap<String, OfficeThing>();

    // View stuff
    private OfficeCanvasView mOfficeCanvasView;
    private FrameLayout mOfficeFloorView;
    private Menu mActionMenu;

    public abstract class ThingChangeListener {
        public abstract void thingChanged(String key, OfficeThing officeThing);
    }

    public abstract class SelectedThingChangeListener {
        public abstract void thingChanged(OfficeThing officeThing);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_office_mover);

        // Initialize Firebase
        mFirebaseRef = new Firebase(FIREBASE);

        // Process authentication
        Bundle extras = getIntent().getExtras();
        String authToken;
        if (extras != null) {
            authToken = extras.getString(LoginActivity.AUTH_TOKEN_EXTRA);
        } else {
            Log.w(TAG, "Users must be authenticated to do this activity. Redirecting to login activity.");
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
            return;
        }

        mFirebaseRef.authWithOAuthToken("google", authToken, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Log.v(TAG, "Authentication worked");
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Log.e(TAG, "Authentication failed: " + firebaseError.getMessage());
                Toast.makeText(getApplicationContext(), "Authentication failed. Please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize the view stuff
        mOfficeLayout = new OfficeLayout();
        mOfficeCanvasView = (OfficeCanvasView) findViewById(R.id.office_canvas);
        mOfficeCanvasView.setOfficeLayout(mOfficeLayout);
        mOfficeFloorView = (FrameLayout) findViewById(R.id.office_floor);

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
                Log.v(TAG, "Floor update canceled: " + firebaseError.getMessage());

            }
        });

        // Listen for furniture changes
        mFirebaseRef.child("furniture").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String key = dataSnapshot.getKey();
                OfficeThing existingThing = dataSnapshot.getValue(OfficeThing.class);

                Log.v(TAG, "New thing added " + existingThing);

                addUpdateThingToLocalModel(key, existingThing);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                OfficeThing existingThing = dataSnapshot.getValue(OfficeThing.class);

                Log.v(TAG, "Thing changed " + existingThing);

                addUpdateThingToLocalModel(key, existingThing);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();

                Log.v(TAG, "Thing removed " + key);

                removeThingFromLocalModel(key);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                OfficeThing existingThing = dataSnapshot.getValue(OfficeThing.class);

                Log.v(TAG, "Thing moved " + existingThing);

                addUpdateThingToLocalModel(key, existingThing);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.w(TAG, "Furniture move was canceled: " + firebaseError.getMessage());
            }
        });

        // Handles menu changes that happen when an office thing is selected or de-selected
        mOfficeCanvasView.setThingFocusChangeListener(new SelectedThingChangeListener() {
            @Override
            public void thingChanged(OfficeThing officeThing) {
                mSelectedThing = officeThing;

                if (mActionMenu != null) {
                    // Clean things up, if they're there
                    mActionMenu.removeItem(R.id.action_delete);
                    mActionMenu.removeItem(R.id.action_edit);
                    mActionMenu.removeItem(R.id.action_rotate);

                    // If I have a new thing, add menu items back to it
                    if (officeThing != null) {
                        mActionMenu.add(Menu.NONE, R.id.action_delete, Menu.NONE,
                                getString(R.string.action_delete));

                        // Only desks can be edited
                        if (officeThing.getType().equals("desk")) {
                            mActionMenu.add(Menu.NONE, R.id.action_edit, Menu.NONE,
                                    getString(R.string.action_edit));
                        }

                        mActionMenu.add(Menu.NONE, R.id.action_rotate, Menu.NONE,
                                getString(R.string.action_rotate));
                    }
                }
            }
        });

        // Triggers whenever an office thing changes on the screen. This binds the
        // user interface to the scheduler that throttles updates to Firebase
        mOfficeCanvasView.setThingChangedListener(new ThingChangeListener() {
            @Override
            public void thingChanged(String key, OfficeThing officeThing) {
                mStuffToUpdate.put(key, officeThing);
                mOfficeCanvasView.invalidate();
            }
        });

        // A scheduled executor that throttles updates to Firebase to about 40ms each.
        // This prevents the high frequency change events from swamping Firebase.
        ScheduledExecutorService firebaseUpdateScheduler = Executors.newScheduledThreadPool(1);
        firebaseUpdateScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (mStuffToUpdate != null && mStuffToUpdate.size() > 0) {
                    for (OfficeThing officeThing : mStuffToUpdate.values()) {
                        updateOfficeThing(officeThing.getKey(), officeThing);
                        mStuffToUpdate.remove(officeThing.getKey());
                    }
                }
            }
        }, UPDATE_THROTTLE_DELAY, UPDATE_THROTTLE_DELAY, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_new_thing:
                renderNewThingPopup();
                break;
            case R.id.change_floor:
                renderChangeCarpetPopup();
                break;
            case R.id.action_rotate:
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
            case R.id.action_delete:
                deleteOfficeThing(mSelectedThing.getKey(), mSelectedThing);
                break;
            case R.id.action_edit:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText entry = new EditText(this);

                builder.setMessage(getString(R.string.edit_desk_name_description))
                        .setTitle(getString(R.string.edit_desk_name_title)).setView(entry);

                builder.setPositiveButton(getString(R.string.edit_desk_name_save),
                        new DialogInterface.OnClickListener() {
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

    /**
     * The add item popup menu
     */
    private void renderNewThingPopup() {
        View menuItemView = findViewById(R.id.action_new_thing);
        PopupMenu popup = new PopupMenu(this, menuItemView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.add_office_thing, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String menuName = getResources().getResourceName(item.getItemId());
                if (menuName.contains("action_add_")) {
                    String newThingName = menuName.split("action_add_")[1];
                    addOfficeThing(newThingName);
                } else {
                    Log.e(TAG, "Attempted to add unknown thing " + menuName);
                }
                return true;
            }

            /**
             * Saves a new thing to Firebase, which is then picked up and displayed by
             * the view
             *
             * @param thingType The type of furniture to add to Firebase
             */
            private void addOfficeThing(String thingType) {
                if (null == thingType) {
                    throw new IllegalArgumentException("Typeless office things are not allowed");
                }

                OfficeThing newThing = new OfficeThing();
                newThing.setType(thingType);
                newThing.setzIndex(mOfficeLayout.getHighestzIndex() + 1);
                newThing.setRotation(0);
                newThing.setName("");
                newThing.setLeft(OfficeCanvasView.LOGICAL_WIDTH / 2);
                newThing.setTop(OfficeCanvasView.LOGICAL_HEIGHT / 2);

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
        });
        popup.show();
    }

    /**
     * The change floor pattern popup menu
     */
    private void renderChangeCarpetPopup() {
        View menuItemView = findViewById(R.id.change_floor);
        PopupMenu popup = new PopupMenu(this, menuItemView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.change_floor, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String menuName = getResources().getResourceName(item.getItemId());
                if(menuName.contains("action_floor_")) {
                    String newFloor = menuName.split("action_floor_")[1];
                    if(newFloor.equals("none")) {
                        mFirebaseRef.child("background").removeValue();
                    } else {
                        mFirebaseRef.child("background").setValue(newFloor);
                    }
                } else {
                    Log.e(TAG, "Attempted change carpet to unknown value " + menuName);
                }
                return true;
            }
        });
        popup.show();
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