package com.firebase.officemover;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class OfficeMoverActivity extends Activity {

    private OfficeCanvasView mOffice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_office_mover);

        mOffice = (OfficeCanvasView)findViewById(R.id.office_canvas);
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
        if (id == R.id.action_new_thing) {
            mOffice.addNewThing();
            return true;
        } else if(id == R.id.action_clear) {
            mOffice.clearLayout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
