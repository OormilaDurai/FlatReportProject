package qrtestapp.testapp.com.flatreportproject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
//import android.media.Image;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfWriter;




import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.system.Os.chmod;


public class FinishReport extends AppCompatActivity {

    private String TAG = "FinishReport";
    BuildingReportDbHelper brdb;

    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        final String building_id = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Log.d(TAG, "onCreate: buildingId" + building_id);

        getSupportActionBar().setTitle("Report Details"); // hide the title bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_finish_report);

        brdb = new BuildingReportDbHelper(getApplicationContext());
        Cursor buildingData = brdb.getBuildingData(building_id);
        if ( buildingData != null && buildingData.moveToFirst()) {
            do {
                String buildingName = buildingData.getString(buildingData.getColumnIndex("NAME"));
                TextView buildName = (TextView) findViewById(R.id.buildingName);
                buildName.setText(buildingName);
            }
            while (buildingData.moveToNext()) ;
        }
        buildingData.close();
            //}

        pieChart = (PieChart) findViewById(R.id.reportPieChart);

        // pieChart.setDescription(Description );
        pieChart.setRotationEnabled(true);
        pieChart.setHoleColor(Color.parseColor("#0C3F7E"));
        pieChart.setHoleRadius(40);
        pieChart.setTransparentCircleAlpha(25);
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelTextSize(20);
        pieChart.setUsePercentValues(false);
        addDataSet(building_id);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: value selected");
                Log.d(TAG, e.toString());
                Log.d(TAG, h.toString());
            }

            @Override
            public void onNothingSelected() {

            }
        });
        final ImageView createpdf = (ImageView) findViewById(R.id.pdfImg);
        createpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createandDisplayPdf("Urmila",building_id);
            }
        });

        Button sendpdf = (Button) findViewById(R.id.sendPdf);
        sendpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "sendpdf");
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"urmiladurai@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Building Report");
                emailIntent.setData(Uri.parse("mailto:"));

                File root = Environment.getExternalStorageDirectory();
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pdf/"+building_id+"pdf.pdf";

                File file = new File(path);
                if (!file.exists() || !file.canRead()) {
                    return;
                }
                Uri uri = Uri.fromFile(file);
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
            }
        });
    }

    private PieChart addDataSet(String building_id) {
        Log.d(TAG, "addDataSet: Started");
        ArrayList<PieEntry> yEntries = new ArrayList<>();
        ArrayList<String> xEntries = new ArrayList<>();
        brdb = new BuildingReportDbHelper(this);

        ArrayList<String> xDATA = new ArrayList<String>();
        xDATA.add("Maintainance");
        xDATA.add("Cleaning");
        xDATA.add("Wear and Tear");
        xDATA.add("Other");

        ArrayList<String> yDATA = brdb.getIssueCounts(building_id);

        for (int i = 0; i < yDATA.size(); i++) {
            yEntries.add(new PieEntry(Float.valueOf(yDATA.get(i))));
        }
        for (int i = 0; i < xDATA.size(); i++) {
            xEntries.add(xDATA.get(i));
        }
        addListView(yDATA, xEntries);


        //creating dataset
        PieDataSet pieDataSet = new PieDataSet(yEntries, "Issue");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setValueTextColor(Color.parseColor("#FFFFFF"));

        //add colours to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);
        colors.add(Color.GREEN);

        pieDataSet.setColors(colors);

        //adding legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setEnabled(true);


        legend.setDrawInside(true);

        //create piedata object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();

        return pieChart;
    }

    private void addListView(ArrayList<String> yEntries, ArrayList<String> xEntries) {
        ListView reportListView = (ListView) findViewById(R.id.reportListView);

        final ArrayList<String> list1 = new ArrayList<String>();
        int sum=0;
        for (int i = 0; i < yEntries.size(); i++) {

            Log.d(TAG, "addListView: "+yEntries.get(i));
            sum+=Integer.parseInt(yEntries.get(i));

        }
        for (int i = 0; i < yEntries.size(); i++) {
            Log.d(TAG, "addListView: "+yEntries.get(i));
            Log.d(Float.toString((Float.parseFloat(yEntries.get(i))/sum)*100), "addListView: ");
            list1.add((((Float.parseFloat(yEntries.get(i)))/sum)*100) +"% " + xEntries.get(i));
        }
        final StableArrayAdapter adapter1 = new StableArrayAdapter(this,
                R.layout.list_item_white, list1);
        reportListView.setAdapter(adapter1);
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
    public void createandDisplayPdf(String text,String building_id) {
        Document document = new Document();
        try {
            Log.d("entering", "createandDisplayPdf: ");
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pdf";
                try {
                    File dir = new File(path);
                    if (!dir.exists())
                        dir.mkdirs();

                    File file = new File(dir, building_id+"pdf.pdf");

                    FileOutputStream fOut = new FileOutputStream(file);
                    PdfWriter.getInstance(document, fOut);

                    //open the document
                    document.open();

                    Paragraph p1 = new Paragraph(text);
                    Font paraFont = new Font(Font.FontFamily.COURIER);
                    p1.setAlignment(Paragraph.ALIGN_CENTER);
                    p1.setFont(paraFont);


                    //add paragraph to document
                    document.add(p1);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bitmap = pieChart.getChartBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    Image myImg = Image.getInstance(stream.toByteArray());
                    myImg.setAlignment(Image.BOTTOM);
                    document.add(myImg);


                    ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                    Bitmap bm = getWholeListViewItemsToBitmap(building_id);
                    Log.d(TAG, bm.toString());
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, stream2);
                    Image myImg2 = Image.getInstance(stream2.toByteArray());
                    myImg2.setAlignment(Image.MIDDLE);
                    document.add(myImg2);

                }catch (IOException e){
                    e.printStackTrace();
                } catch (BadElementException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

            }
            finally
            {
                document.close();
            }
        viewPdf(building_id+"pdf.pdf", "Pdf");
    }

    // Method for opening a pdf file
    private void viewPdf(String file, String directory) {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        Log.d(TAG, "viewPdf: pdfFile"+pdfFile);
        Uri path = Uri.fromFile(pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(FinishReport.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }


    public Bitmap getWholeListViewItemsToBitmap(String building_id) {

        Log.d(TAG, "addDataSet: Started");
        brdb = new BuildingReportDbHelper(this);
        ArrayList<String> xDATA = new ArrayList<String>();
        xDATA.add("Maintainance");
        xDATA.add("Cleaning");
        xDATA.add("Wear and Tear");
        xDATA.add("Other");

        ArrayList<String> yDATA = brdb.getIssueCounts(building_id);

        ListView reportListView1 = (ListView) findViewById(R.id.reportListView);

        final ArrayList<String> list = new ArrayList<String>();

        int sum=0;
        Log.d(TAG, "xDATA.size(): "+xDATA.size());

        for (int i = 0; i < xDATA.size(); i++) {

            Log.d(TAG, "addListViewsum: "+yDATA.get(i));
            sum+=Integer.parseInt(yDATA.get(i));

        }
        for (int i = 0; i < xDATA.size(); i++) {
            Log.d(TAG, "addListViewval: "+yDATA.get(i));
            Log.d(Float.toString((Float.parseFloat(yDATA.get(i))/sum)*100), "perval: ");
            list.add((((Float.parseFloat(yDATA.get(i)))/sum)*100) +"% " + xDATA.get(i));
        }
        final StableArrayAdapter adapter2 = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        reportListView1.setAdapter(adapter2);
        int itemscount       = adapter2.getCount();
        int allitemsheight   = 0;

        List<Bitmap> bmps    = new ArrayList<Bitmap>();

        for (int i = 0; i < itemscount; i++) {

            View childView      = adapter2.getView(i, null, reportListView1);
            childView.measure(View.MeasureSpec.makeMeasureSpec(reportListView1.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();
            Log.d(TAG, "entering: childview");

            bmps.add(childView.getDrawingCache());
            allitemsheight+=childView.getMeasuredHeight();
        }
        Bitmap bigbitmap    = Bitmap.createBitmap(reportListView1.getMeasuredWidth(), allitemsheight, Bitmap.Config.ARGB_8888);
        Canvas bigcanvas    = new Canvas(bigbitmap);
        bigcanvas.drawColor(-1);

        Paint paint = new Paint();
        int iHeight = 0;
        for (int i = 0; i < bmps.size(); i++) {
            Bitmap bmp = bmps.get(i);
            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight+=bmp.getHeight();
            bmp.recycle();
            bmp=null;
        }
        return bigbitmap;
    }
}



