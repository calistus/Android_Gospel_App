package DataBaseHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by CrowdStar on 8/25/2014.
 */
public class DatabaseSQLiteHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "Bible";

    public DatabaseSQLiteHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Mat (Chapter INTEGER,VERSE INTEGER, Scripture TEXT,Heading VARCHAR);");
        db.execSQL("CREATE TABLE Mrk (Chapter INTEGER,VERSE INTEGER, Scripture TEXT,Heading VARCHAR);");
        db.execSQL("CREATE TABLE Luk (Chapter INTEGER,VERSE INTEGER, Scripture TEXT,Heading VARCHAR);");
        db.execSQL("CREATE TABLE Jhn (Chapter INTEGER,VERSE INTEGER, Scripture TEXT,Heading VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public static ArrayList<String> heading;

    public static ArrayList<String> ScriptureStringArrayList;
    public static ArrayList<String> verseStringArrayList;
    public static ArrayList<String> chapterStringArrayList;


    public void queryDB_ByHeading(String TABLE_NAME, String heading) {
        init();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String column[] = {"chapter","verse","Scripture"};
        String args[] = {heading};
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, column, "Heading=?", args, null, null, null,null);
        while (cursor.moveToNext()) {
                chapterStringArrayList.add(cursor.getString(0));
                verseStringArrayList.add(cursor.getString(1));
                ScriptureStringArrayList.add(cursor.getString(2));
            }

    }

    public void queryChapter(String TABLE_NAME, int chapter) {
        init();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String column[] = {"Scripture", "Chapter", "Verse"};
        String args[] = {"" + chapter};
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, column, "Chapter=?", args, null, null, null, null);
        while (cursor.moveToNext()) {
            ScriptureStringArrayList.add(cursor.getString(0));
            chapterStringArrayList.add(cursor.getString(1));
            verseStringArrayList.add(cursor.getString(2));
        }

    }

    public void queryDBHeading(String TABLE_NAME) {
        init();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String column[] = {"heading"};
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, column, null, null, null, null, null);
        while (cursor.moveToNext()) {
            if (!heading.contains(cursor.getString(0))) {
                heading.add(cursor.getString(0));
            }

        }
        Collections.sort(heading);

    }

    public void init() {
        heading = new ArrayList<String>();
        ScriptureStringArrayList = new ArrayList<String>();
        chapterStringArrayList = new ArrayList<String>();
        verseStringArrayList = new ArrayList<String>();
    }

    public void populateDB(int chapter, int verse, String scripture, String heading, String TABLE_NAME) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Chapter", chapter);
        contentValues.put("Verse", verse);
        contentValues.put("Scripture", scripture);
        contentValues.put("heading", heading);
        this.getWritableDatabase().insert(TABLE_NAME, "Security", contentValues);
    }


}
