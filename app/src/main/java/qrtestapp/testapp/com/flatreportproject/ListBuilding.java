package qrtestapp.testapp.com.flatreportproject;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ListBuilding extends AppCompatActivity {

    BuildingReportDbHelper brdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        final String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        final String EXTRA_MESSAGE = "com.example.flatreportproject.MESSAGE";

        getSupportActionBar().hide();

        setContentView(R.layout.activity_list_building);

        //displaying building list
        brdb = new BuildingReportDbHelper(this);
        Cursor values = brdb.getAllBuildingData();


        if ( values != null && values.moveToFirst()){
            do{
                String name = values.getString(values.getColumnIndex("NAME"));
                //String address = values.getString(values.getColumnIndex("ADDRESS"));
                String nofloors = values.getString(values.getColumnIndex("NOFLOORS"));
                final String id = values.getString(values.getColumnIndex("ID"));

                TableLayout root = (TableLayout) findViewById(R.id.buildingList);
                TableRow row = new TableRow(this);
                RelativeLayout relativeLayout = new RelativeLayout(this);
                TextView tvname = new TextView(this);
                TextView tvnofloor = new TextView(this);
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.ic_action_name);

                tvname.setText(name);
                tvnofloor.setText("No of floor: "+ nofloors);
                tvname.setTextColor(Color.parseColor("#FFFFFF"));
                tvnofloor.setTextColor(Color.parseColor("#FFFFFF"));
                tvname.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
                tvnofloor.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);

                relativeLayout.addView(tvname);
                relativeLayout.addView(tvnofloor);
                relativeLayout.addView(imageView);

                Resources r = this.getResources();

                int pximgwidth = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,120,r.getDisplayMetrics());
                int pximgheight = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,120,r.getDisplayMetrics());
                RelativeLayout.LayoutParams lpimg = new RelativeLayout.LayoutParams(
                        pximgwidth,
                        pximgheight);

                RelativeLayout.LayoutParams lpname = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams lpnof = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                int pximgstart = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,10,r.getDisplayMetrics());
                int pximgtop = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,15,r.getDisplayMetrics());
                int pximgright = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,20,r.getDisplayMetrics());
                int pximgbottom = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,10,r.getDisplayMetrics());
                lpimg.setMargins(pximgstart,pximgtop,pximgright,pximgbottom);
                imageView.setLayoutParams(lpimg);

                int pxnamestart = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,140,r.getDisplayMetrics());
                int pxnametop = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,30,r.getDisplayMetrics());
                int pxnameright = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,20,r.getDisplayMetrics());
                int pxnamebottom = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,20,r.getDisplayMetrics());

                lpname.setMargins(pxnamestart,pxnametop,pxnameright,pxnamebottom);
                tvname.setLayoutParams(lpname);

                int pxnofloorstart = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,140,r.getDisplayMetrics());
                int pxflooretop = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,70,r.getDisplayMetrics());
                int pxfloorright = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,20,r.getDisplayMetrics());
                int pxfloorbottom = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,50,r.getDisplayMetrics());
                lpnof.setMargins(pxnofloorstart,pxflooretop,pxfloorright,pxfloorbottom);
                tvnofloor.setLayoutParams(lpnof);

                int pxbtnwidth = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,130,r.getDisplayMetrics());
                int pxbtnheight = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,40,r.getDisplayMetrics());
                RelativeLayout.LayoutParams lpbtn = new RelativeLayout.LayoutParams(
                        pxbtnwidth,
                        pxbtnheight);

                Button btn = new Button(this);
                btn.setText("Create Report");
                //create report
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.d("insName",message);
                        Intent intent1 = new Intent(getApplicationContext(),CreateReport.class);
                        intent1.putExtra(EXTRA_MESSAGE, id);
                        startActivity(intent1);

                    }
                });
                int pxbtnstart = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,140,r.getDisplayMetrics());
                int pxbtntop = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,100,r.getDisplayMetrics());
                int pxbtnright = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,20,r.getDisplayMetrics());
                int pxbtnbottom = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,50,r.getDisplayMetrics());
                lpbtn.setMargins(pxbtnstart,pxbtntop,pxbtnright,pxbtnbottom);
                btn.setLayoutParams(lpbtn);
                btn.setBackgroundResource(R.drawable.button_rounded_corners_white);
                relativeLayout.addView(btn);

                GradientDrawable border = new GradientDrawable();
                border.setColor(Color.parseColor("#2962A9"));
                border.setCornerRadius(50);

                border.setStroke(40, Color.parseColor("#0C3F7E"));
                row.setBackground(border);
                row.addView(relativeLayout);
                root.addView(row);

            }while(values.moveToNext());
        }
        values.close();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        //edit page
        ImageView editBuildng = (ImageView)findViewById(R.id.addBuilding);
        //edit button activity
        editBuildng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(),AddBuilding.class);
                startActivity(intent1);

            }
        });

    }
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
