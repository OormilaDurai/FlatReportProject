package qrtestapp.testapp.com.flatreportproject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RoomNoAdapter extends CursorAdapter {
    public RoomNoAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_room_no_adapter, parent, false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
       // Log.d("entering", "bindView: ");
                // Find fields to populate in inflated template
                TextView roomNo = (TextView) view.findViewById(R.id.roomNumber);
                // Extract properties from cursor
                String roomnoval = cursor.getString(cursor.getColumnIndex("ROOMNO"));
                // Populate fields with extracted properties
                roomNo.setText("Room No:" + roomnoval);
    }
}