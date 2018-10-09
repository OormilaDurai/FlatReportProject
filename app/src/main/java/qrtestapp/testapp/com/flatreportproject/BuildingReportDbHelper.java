package qrtestapp.testapp.com.flatreportproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class BuildingReportDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "building_report.db";

    public BuildingReportDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public BuildingReportDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table building_table (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT," +
                "NOFLOORS INTEGER,ADDRESS VARCHAR,TYPE VARCHAR)");
        db.execSQL("create table image_table (IMAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT,PATH VARCHAR)");
        db.execSQL("create table report_table (REPORT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "FLOOR INTEGER,ROOMNO INTEGER,ISSUE VARCHAR,COMMENTS VARCHAR," +
                "WORKID VARCHAR,BUILDING_ID INTEGER,IMAGE_ID INTEGER," +
                " FOREIGN KEY(BUILDING_ID) REFERENCES building_table(ID),FOREIGN KEY(IMAGE_ID)" +
                " REFERENCES image_table(ID))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS building_table");
        onCreate(db);

    }

    public long insertBuildingData(String name,int nofloors,String address,String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME",name);
        contentValues.put("NOFLOORS",nofloors);
        contentValues.put("ADDRESS",address);
        contentValues.put("TYPE",type);
        String TABLE_NAME = "building_table";
        db.execSQL("create table if not exists building_table (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT," +
                "NOFLOORS INTEGER,ADDRESS VARCHAR,TYPE VARCHAR)");
        long id = db.insert(TABLE_NAME,null ,contentValues);
        if(id == -1)
            return 0;
        else
            return id;
    }

    public long insertReport(String buildingId,String floor,int roomno,String issue,String comments,
                             String workid,String filepath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("BUILDING_ID",buildingId);
        contentValues.put("FLOOR",floor);
        contentValues.put("ROOMNO",roomno);
        contentValues.put("ISSUE",issue);
        contentValues.put("COMMENTS",comments);
        contentValues.put("WORKID",workid);
        ContentValues contentValuesimg = new ContentValues();
        contentValuesimg.put("PATH",filepath);

        String TABLE_NAME = "image_table";
        db.execSQL("create table if not exists image_table (IMAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT,PATH VARCHAR)");
        long id = db.insert(TABLE_NAME,null ,contentValuesimg);
        if(id == -1)
            return 0;
        else
            contentValues.put("IMAGE_ID",id);
            db.execSQL("create table if not exists report_table (REPORT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "FLOOR INTEGER,ROOMNO INTEGER,ISSUE VARCHAR,COMMENTS VARCHAR," +
                    "WORKID VARCHAR,BUILDING_ID INTEGER,IMAGE_ID INTEGER," +
                    " FOREIGN KEY(BUILDING_ID) REFERENCES building_table(ID)," +
                    "FOREIGN KEY(IMAGE_ID) REFERENCES image_table(ID))");
            long idr = db.insert("report_table",null ,contentValues);

            return idr;
    }

    public void updateReport(String buildingId,String floor,int roomno,String issue,String comments,
                             String workid,String filepath,String reportId,String imageId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("BUILDING_ID",buildingId);
        contentValues.put("FLOOR",floor);
        contentValues.put("ROOMNO",roomno);
        contentValues.put("ISSUE",issue);
        contentValues.put("COMMENTS",comments);
        contentValues.put("WORKID",workid);
        ContentValues contentValuesimg = new ContentValues();
        contentValuesimg.put("PATH",filepath);

        String TABLE_NAME = "image_table";
        db.execSQL("create table if not exists image_table (IMAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT,PATH VARCHAR)");
        db.update("image_table",
                contentValuesimg,
                "image_table.image_id = ?",
                new String[]{String.valueOf(imageId)});

        db.update("report_table",
                contentValues,
                "report_table.report_id = ?",
                new String[]{String.valueOf(reportId)});
    }

    public Cursor getAllBuildingData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from building_table",null);
        return res;
    }

    public Cursor getBuildingData(String building_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from building_table where id ='"+building_id+"'",null);
        return res;
    }

    public Cursor getFloorValue(String building_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select NOFLOORS from building_table " +
                     "where ID='"+building_id+"'",null);

        return res;
    }

    public Cursor getRoomNoValue(String building_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select distinct r.ROOMNO as ROOMNO,b.ID as _id from building_table as b " +
                "join report_table as r on r.building_id = b.ID " +
                "where b.ID='"+building_id+"'",null);

        Log.d("rocount",Integer.toString(res.getCount()));
        return res;
    }

    public Cursor getReportData(String buildingId) {
       Log.d("entering",buildingId);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select r.REPORT_ID as REPORT_ID," +
                "r.IMAGE_ID as IMAGE_ID,r.FLOOR as FLOOR,r.ROOMNO as ROOMNO," +
                "r.ISSUE as ISSUE,r.COMMENTS as COMMENTS,r.WORKID as WORKID,i.PATH as PATH" +
                " from report_table as r" +
                " join image_table as i on r.image_id = i.image_id " +
                "where building_id='"+buildingId+"'",null);
        return res;
    }
    public Cursor getReportDataI(String buildingId) {
        Log.d("entering",buildingId);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select distinct r.REPORT_ID as REPORT_ID," +
                "r.IMAGE_ID as IMAGE_ID,r.FLOOR as FLOOR,r.ROOMNO as ROOMNO," +
                "r.ISSUE as ISSUE,r.COMMENTS as COMMENTS,r.WORKID as WORKID,i.PATH as PATH" +
                " from report_table as r" +
                " join image_table as i on r.image_id = i.image_id " +
                "where r.building_id='"+buildingId+"' and r.image_id = i.image_id limit 1",null);
        return res;
    }

    public Cursor getReportDataRoomNo(String roomno,String building_id) {
        Log.d("getReportDataRoomNo",roomno);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select distinct r.REPORT_ID as REPORT_ID" +
                " from report_table as r" +
                " join image_table as i on r.image_id = i.image_id " +
                "where r.ROOMNO='"+roomno+"' and r.building_id = '"
                + building_id + "' limit 1",null);
        return res;
    }

    public Cursor getReportDataRI(String reportId) {
        Log.d("enterinrig",reportId);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select distinct r.REPORT_ID as REPORT_ID," +
                "r.IMAGE_ID as IMAGE_ID,r.FLOOR as FLOOR,r.ROOMNO as ROOMNO," +
                "r.ISSUE as ISSUE,r.COMMENTS as COMMENTS,r.WORKID as WORKID,i.PATH as PATH" +
                " from report_table as r" +
                " join image_table as i on r.image_id = i.image_id " +
                "where r.REPORT_ID='"+reportId+"' limit 1",null);
        return res;
    }

    public void deleteReport(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("report_table", "1", null);
        db.delete("image_table", "1", null);
    }

    public String getReportPicturePath(String imageId) {
        SQLiteDatabase db = getReadableDatabase();
        String picturePath="";

        Cursor imgCursor = db.rawQuery("select distinct * from image_table where image_id = '" + imageId+"'",null);
        Log.d("query", "select * from image_table where image_id = '" + imageId+"'");
        Log.d("count", Integer.toString(imgCursor.getCount()));
        if ( imgCursor != null && imgCursor.moveToFirst()) {
            do {
                picturePath = imgCursor.getString(imgCursor.
                        getColumnIndex("PATH"));
                Log.d("Pic Path", picturePath);

                }while(imgCursor.moveToNext());
        }
        imgCursor.close();
        return (picturePath);
    }

    public ArrayList<String> getIssueCounts(String building_id){
        ArrayList<String> xDATA = new ArrayList<String>();

        SQLiteDatabase db = getReadableDatabase();

        String[] issues = new String[]{"Maintainance","Cleaning","Wear and Tear","Other"};
        for(int i=0;i<=3;i++){
            Cursor imgCursor = db.rawQuery("select distinct * from report_table where " +
                    "issue = '"+issues[i]+"' and building_id = '" + building_id+"'",null);

            xDATA.add(Integer.toString(imgCursor.getCount()));
        }

        return xDATA;
    }
}
