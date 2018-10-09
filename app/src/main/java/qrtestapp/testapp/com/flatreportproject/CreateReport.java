package qrtestapp.testapp.com.flatreportproject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.pdf.parser.Line;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CreateReport extends AppCompatActivity {

    BuildingReportDbHelper brdb;

    Spinner floor;
    EditText comments;
    EditText workid;
    Spinner issue;
    EditText roomno;
    EditText buildingId;
    LinearLayout lvItems;
    public static final String EXTRA_MESSAGE = "com.example.flatreportproject.MESSAGE";
    public static final String EXTRA_MESSAGE1 = "com.example.flatreportproject.MESSAGE1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Intent intent = getIntent();
        final String buildingid = intent.getStringExtra(EXTRA_MESSAGE);
        final String isNewR = intent.getStringExtra(EXTRA_MESSAGE1);

        Log.d("building id",buildingid);

/*        brdb = new BuildingReportDbHelper(this);
        brdb.deleteReport();*/
        if(isNewR!=null){
        Log.d("isNewR", isNewR);
        }
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Create Report"); // hide the title bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_create_report);

        brdb = new BuildingReportDbHelper(getApplicationContext());
        Cursor buildingData = brdb.getBuildingData(buildingid);
        if ( buildingData != null && buildingData.moveToFirst()) {
            do {
                String buildingName = buildingData.getString(buildingData.getColumnIndex("NAME"));
                TextView buildName = (TextView) findViewById(R.id.editBuildingName);
                buildName.setText(buildingName);
            }
            while (buildingData.moveToNext()) ;
        }
        buildingData.close();

        lvItems = (LinearLayout) findViewById(R.id.addRepRoomNo);
        brdb = new BuildingReportDbHelper(this);
        Cursor valuesroomno = brdb.getRoomNoValue(buildingid);
        // Setup cursor adapter using cursor from last step
        if ( valuesroomno != null && valuesroomno.moveToFirst()) {
            do {
                Log.d("entering", "onCreate: ");
                final String roomnoo = valuesroomno.getString(valuesroomno.getColumnIndex("ROOMNO"));
                TextView view= new TextView(this);
                view.setText("Room No:"+roomnoo);
                view.setBackgroundColor(Color.parseColor("#2962A9"));

                Resources r = this.getResources();

                LinearLayout.LayoutParams lpview = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                int pximgstart = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,10,r.getDisplayMetrics());
                int pximgtop = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,0,r.getDisplayMetrics());
                int pximgright = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,0,r.getDisplayMetrics());
                int pximgbottom = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,0,r.getDisplayMetrics());
                lpview.setMargins(pximgstart,pximgtop,pximgright,pximgbottom);
                view.setLayoutParams(lpview);

                int paddingsize = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,5,r.getDisplayMetrics());
                view.setPadding(paddingsize,paddingsize,paddingsize,paddingsize);
                view.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        brdb = new BuildingReportDbHelper(getApplicationContext());
                        Cursor valuesrn = brdb.getReportDataRoomNo(roomnoo,buildingid);
                        Log.d(roomnoo, "onClick: roomno print");

                        EditText reportId = (EditText) findViewById(R.id.reportId);
                        EditText imageId = (EditText) findViewById(R.id.imageId);

                        if ( valuesrn != null && valuesrn.moveToFirst()) {
                            do {
                                String rnvall = valuesrn.getString(valuesrn.getColumnIndex("REPORT_ID"));
                                reportId.setText(rnvall);
                                imageId.setText("");
                                Log.d(rnvall, "onClick: reportId print");
                                View parrentView = findViewById( R.id.addRepRoomNo );
                                parrentView.performClick();

                            }while(valuesrn.moveToNext());
                        }
                        valuesrn.close();
                    }
                });
                view.setClickable(true);
                lvItems.addView(view);


            }while(valuesroomno.moveToNext());
        }
        valuesroomno.close();

        //get the spinner for floor from the xml.
        Spinner dropdownfloor = findViewById(R.id.selectFloor);
        ArrayList<String> floorItems = new ArrayList<String>();
        floorItems.add("Select Floor");
        brdb = new BuildingReportDbHelper(this);
        Cursor values = brdb.getFloorValue(buildingid);
        if ( values != null && values.moveToFirst()) {
            do {
                int floors = values.getInt(values.getColumnIndex("NOFLOORS"));
                for (int i = 0; i < floors ; i++) {
                    floorItems.add(Integer.toString(i + 1));
                }
            }while(values.moveToNext());
        }
        values.close();
        floor = (Spinner)findViewById(R.id.selectFloor);
        issue = (Spinner)findViewById(R.id.selectIssue);
        roomno = (EditText)findViewById(R.id.editRoomNo);
        comments = (EditText)findViewById(R.id.editComments);
        workid = (EditText)findViewById(R.id.editWorkId);

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, floorItems);
        //set the spinners adapter to the previously created one.
        dropdownfloor.setAdapter(adapter);
        //set focus to first field
        floor.setFocusable(true);
        floor.setFocusableInTouchMode(true);
        floor.requestFocus();

        //get the spinner for floor from the xml.
        Spinner dropdownIssue = findViewById(R.id.selectIssue);
        String[] issueItems = new String[]{"Maintainance","Cleaning","Wear and Tear","Other"};


        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        final ArrayAdapter<String> adapterIssue = new ArrayAdapter<>(this, R.layout.spinner_item, issueItems);
        //set the spinners adapter to the previously created one.
        dropdownIssue.setAdapter(adapterIssue);

        View parrentView = findViewById( R.id.addRepRoomNo );

        parrentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText rnv = (EditText)findViewById(R.id.reportId) ;
                String rnval = rnv.getText().toString();
                Log.d(rnval, "onClick: rval print");
                Cursor reportData = brdb.getReportDataRI(rnval);

                if (reportData != null && reportData.moveToFirst()) {
                    do {
                        String report_id = reportData.getString(reportData.getColumnIndex("REPORT_ID"));
                        String image_id = reportData.getString(reportData.getColumnIndex("IMAGE_ID"));
                        String floorrep = reportData.getString(reportData.getColumnIndex("FLOOR"));
                        String roomnorep = reportData.getString(reportData.getColumnIndex("ROOMNO"));
                        String issuerep = reportData.getString(reportData.getColumnIndex("ISSUE"));
                        String commentsrep = reportData.getString(reportData.getColumnIndex("COMMENTS"));
                        String workidrep = reportData.getString(reportData.getColumnIndex("WORKID"));
                        String filepathrep = reportData.getString(reportData.getColumnIndex("PATH"));
                        Log.d("values", report_id + image_id + "h" + floorrep + "h" + roomnorep + issuerep + commentsrep + workidrep + filepathrep);

                        if (floorrep != null) {
                            int floorspinnerPosition = adapter.getPosition(floorrep);
                            floor.setSelection(floorspinnerPosition);
                        }
                        if (issuerep != null) {
                            int issuespinnerPosition = adapterIssue.getPosition(issuerep);
                            issue.setSelection(issuespinnerPosition);
                        }

                        comments.setText(commentsrep);
                        workid.setText(workidrep);
                        roomno.setText(roomnorep);
                        getReportPicture(image_id);
                        EditText reportId = (EditText) findViewById(R.id.reportId);
                        EditText imageId = (EditText) findViewById(R.id.imageId);
                        reportId.setText(report_id);
                        imageId.setText(image_id);


                    } while (reportData.moveToNext());
                }
                reportData.close();

            }
        });
        brdb = new BuildingReportDbHelper(this);
        if((isNewR!=null)&&isNewR.equals("newR")) {
            Log.d(buildingid, "onCreate: "+buildingid);
        }
        else {
            Cursor reportData = brdb.getReportDataI(buildingid);

            if (reportData != null && reportData.moveToFirst()) {
                do {
                    String report_id = reportData.getString(reportData.getColumnIndex("REPORT_ID"));
                    String image_id = reportData.getString(reportData.getColumnIndex("IMAGE_ID"));
                    String floorrep = reportData.getString(reportData.getColumnIndex("FLOOR"));
                    String roomnorep = reportData.getString(reportData.getColumnIndex("ROOMNO"));
                    String issuerep = reportData.getString(reportData.getColumnIndex("ISSUE"));
                    String commentsrep = reportData.getString(reportData.getColumnIndex("COMMENTS"));
                    String workidrep = reportData.getString(reportData.getColumnIndex("WORKID"));
                    String filepathrep = reportData.getString(reportData.getColumnIndex("PATH"));
                    //Log.d("values", report_id + image_id + "h" + floorrep + "h" + roomnorep + issuerep + commentsrep + workidrep + filepathrep);

                    if (floorrep != null) {
                        int floorspinnerPosition = adapter.getPosition(floorrep);
                        floor.setSelection(floorspinnerPosition);
                    }
                    if (issuerep != null) {
                        int issuespinnerPosition = adapterIssue.getPosition(issuerep);
                        issue.setSelection(issuespinnerPosition);
                    }

                    comments.setText(commentsrep);
                    workid.setText(workidrep);
                    roomno.setText(roomnorep);
                    getReportPicture(image_id);
                    EditText reportId = (EditText) findViewById(R.id.reportId);
                    EditText imageId = (EditText) findViewById(R.id.imageId);
                    reportId.setText(report_id);
                    imageId.setText(image_id);


                } while (reportData.moveToNext());
            }
            reportData.close();
        }



        final Button addRep = (Button) findViewById(R.id.addReport);

        addRep.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String imgPathName = "";
                String floorval = floor.getSelectedItem().toString();
                String issueval = issue.getSelectedItem().toString();

                String roomnoval = roomno.getText().toString();
                String commentsval = comments.getText().toString();
                String workidval = workid.getText().toString();

                EditText reportId = (EditText) findViewById(R.id.reportId);
                EditText imageId = (EditText) findViewById(R.id.imageId);
                String report_Id = reportId.getText().toString();
                String image_Id = imageId.getText().toString();

                LinearLayout viewImgLayout = (LinearLayout) findViewById(R.id.viewImg);
                int rowCount = viewImgLayout.getChildCount();

                Log.d("rowc",Integer.toString(rowCount));
                for (int j = 0; j < rowCount; j++) {
                    Log.d("rowc",Integer.toString(j));

                    ImageView getImg = (ImageView) viewImgLayout.getChildAt(j);
                        Bitmap bm = ((BitmapDrawable) getImg.getDrawable()).getBitmap();
                        String imgName = buildingid + roomnoval + j;

                        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
                        File file = wrapper.getDir("Images", MODE_PRIVATE);
                        file = new File(file, imgName + ".jpg");
                        try {
                            OutputStream stream = null;
                            stream = new FileOutputStream(file);
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            stream.flush();
                            stream.close();
                        } catch (IOException e) // Catch the exception
                        {
                            e.printStackTrace();
                        }

                        imgPathName += "," + file.getAbsolutePath();
                        Log.d("path", imgPathName);

                }
                if(report_Id.equals("")) {
                    if(roomnoval.equals("")){
                        Toast.makeText(getApplicationContext(), "Error adding report please give the Room No", Toast.LENGTH_SHORT).show();
                        roomno.requestFocus();
                    }
                    else if(issueval.equals("")){
                        Toast.makeText(getApplicationContext(), "Error adding report please select the Issue", Toast.LENGTH_SHORT).show();
                        issue.requestFocus();
                    }else {
                        brdb.insertReport(buildingid, floorval, Integer.parseInt(roomnoval), issueval, commentsval, workidval, imgPathName);
                        Intent intent1 = new Intent(getApplicationContext(),ListBuilding.class);
                        startActivity(intent1);
                    }
                }else{
                    Log.d("image_Id",image_Id);
                    Log.d("report_Id",report_Id);
                    if(roomnoval.equals("")){
                        Toast.makeText(getApplicationContext(), "Error updating report please give the Room No", Toast.LENGTH_SHORT).show();
                        roomno.requestFocus();
                    }
                    else if(issueval.equals("")){
                        Toast.makeText(getApplicationContext(), "Error updating report please select the Issue", Toast.LENGTH_SHORT).show();
                        issue.requestFocus();
                    }else {
                        brdb.updateReport(buildingid, floorval, Integer.parseInt(roomnoval), issueval, commentsval,
                                workidval, imgPathName, report_Id, image_Id);
                        Intent intent1 = new Intent(getApplicationContext(),ListBuilding.class);
                        startActivity(intent1);
                    }
                }
            }
        });

        final Button finishRep = (Button) findViewById(R.id.finishReport);
        finishRep.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText report_id = (EditText) findViewById(R.id.reportId);
                addRep.performClick();
                Intent intent1 = new Intent(getApplicationContext(),FinishReport.class);
                intent1.putExtra(EXTRA_MESSAGE, buildingid);
                startActivity(intent1);
            }
        });



        //for image next report
        ImageView nextRep=(ImageView)findViewById(R.id.nextReport);

        nextRep.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText report_id = (EditText) findViewById(R.id.reportId);
                addRep.performClick();
                EditText reportId = (EditText) findViewById(R.id.reportId);
                EditText imageId = (EditText) findViewById(R.id.imageId);

                reportId.setText("");
                imageId.setText("");
                floor.setSelected(false);
                roomno.setText("");
                issue.setSelected(false);
                comments.setText("");
                workid.setText("");
                Intent intent = new Intent(getApplicationContext(),CreateReport.class);
                intent.putExtra(EXTRA_MESSAGE,buildingid);
                intent.putExtra(EXTRA_MESSAGE1, "newR");
                startActivityForResult(intent, 0);
            }
        });

        //for image uploading
        ImageView b=(ImageView)findViewById(R.id.selectImg);

        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 0);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        LinearLayout viewImgLayout = (LinearLayout) findViewById(R.id.viewImg);

        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            Resources r = this.getResources();
            ImageView viewImg = new ImageView(this);

            int viewImgwidth = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,100,r.getDisplayMetrics());
            int viewImgheight = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,100,r.getDisplayMetrics());
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, viewImgwidth, viewImgheight, true);
            viewImg.setImageBitmap(scaledBitmap);
            viewImg.setPadding(0,0,10,0);
            viewImgLayout.addView(viewImg);

        }
    }


    public void getReportPicture(String imageId) {
        String picturePath = brdb.getReportPicturePath(imageId);
        if (picturePath != null || picturePath.length() != 0) {
            String[] pathsep = picturePath.split(",");
            int pathseplen = pathsep.length;
            LinearLayout viewImgLayout = (LinearLayout) findViewById(R.id.viewImg);
            viewImgLayout.removeAllViews();
            for (int i = 1; i < pathseplen; i++) {
                Log.d("i", pathsep[i].replace(",", "")+"ghg");

                Log.d("i", Integer.toString(i));
                    Log.d("i", pathsep[i].replace(",", ""));

                    ImageView viewImg = new ImageView(this);
                    Resources r = this.getResources();

                    int viewImgwidth = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,100,r.getDisplayMetrics());
                    int viewImgheight = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,100,r.getDisplayMetrics());

                    Bitmap reportPicture = BitmapFactory.decodeFile(pathsep[i].replace(",", ""));
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(reportPicture, viewImgwidth, viewImgheight, true);

                    viewImg.setImageBitmap(scaledBitmap);
                    viewImg.setPadding(0,0,10,0);
                    viewImgLayout.addView(viewImg);
            }
        }
    }
}



