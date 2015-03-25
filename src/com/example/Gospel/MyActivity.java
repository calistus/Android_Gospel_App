package com.example.Gospel;

import DataBaseHandler.DatabaseSQLiteHelper;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private WebView displayView;
    private TextView bookTitle;
    private static int chapter = 1;
    private static int verse = 1;
    private int maximumChapter = 28;
    private static boolean isNavigationSelected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        displayView = (WebView) findViewById(R.id.displayView);
        bookTitle = (TextView) findViewById(R.id.bookTitle);
        Button prevButton = (Button) findViewById(R.id.btnPrev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lastOpened = getSharedPreferences("com.bible.preference", Context.MODE_PRIVATE).getString("lastOpened", "mat");
                if (chapter == 1) {
                    chapter = maximumChapter;
                } else {
                    chapter -= 1;
                }
                populateTextViewWithScripture(lastOpened);
                setBookTitle(lastOpened);
            }
        });
        Button nextButton = (Button) findViewById(R.id.btnNext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lastOpened = getSharedPreferences("com.bible.preference", Context.MODE_PRIVATE).getString("lastOpened", "mat");
                if (chapter == maximumChapter) {
                    chapter = 1;
                } else {
                    chapter += 1;
                }
                populateTextViewWithScripture(lastOpened);
                setBookTitle(lastOpened);
            }
        });
        if (getSharedPreferences("com.bible.preference", Context.MODE_PRIVATE).getBoolean("firstRun", true)) {
            populateDB();
            getSharedPreferences("com.bible.preference", Context.MODE_PRIVATE).edit().putBoolean("firstRun", false).commit();
        } else {
            String lastOpened = getSharedPreferences("com.bible.preference", Context.MODE_PRIVATE).getString("lastOpened", "mat");
            populateActionBarNavigation(lastOpened);
            populateTextViewWithScripture(lastOpened);
            decideMaximum(lastOpened);
            setBookTitle(lastOpened);
        }
    }

    public void setBookTitle(String bookTag) {
        if (bookTag.trim().equalsIgnoreCase("mat")) {
            bookTitle.setText("MATTHEW " + chapter);
        } else if (bookTag.trim().equalsIgnoreCase("mrk")) {
            bookTitle.setText("MARK " + chapter);
        } else if (bookTag.trim().equalsIgnoreCase("luk")) {
            bookTitle.setText("LUKE " + chapter);
        } else {
            bookTitle.setText("JOHN " + chapter);
        }
    }

    public void decideMaximum(String book) {
        if (book.equalsIgnoreCase("mat")) {
            maximumChapter = 28;
        } else if (book.equalsIgnoreCase("mrk")) {
            maximumChapter = 16;
        } else if (book.equalsIgnoreCase("luk")) {
            maximumChapter = 24;
        } else {
            maximumChapter = 21;
        }
    }

    public void populateTextViewWithScripture(String TableName) {
        new DatabaseSQLiteHelper(MyActivity.this).queryChapter(TableName, chapter);
        StringBuilder chapterContent = new StringBuilder();
        for (int i = 0; i < DatabaseSQLiteHelper.chapterStringArrayList.size(); i++) {
            chapterContent.append(DatabaseSQLiteHelper.chapterStringArrayList.get(i));
            chapterContent.append(":" + DatabaseSQLiteHelper.verseStringArrayList.get(i));
            chapterContent.append("&nbsp;&nbsp;&nbsp;&nbsp;" + DatabaseSQLiteHelper.ScriptureStringArrayList.get(i));
            chapterContent.append("<br><br>");
        }
        setText(chapterContent.toString());

    }

    public void populateActionBarNavigation(String book) {
        new DatabaseSQLiteHelper(MyActivity.this).queryDBHeading(book);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33b5e5")));
        final String header[] = DatabaseSQLiteHelper.heading.toArray(new String[DatabaseSQLiteHelper.heading.size()]);
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, header);
        SpinnerAdapter spinnerAdapter = stringArrayAdapter;
        ActionBar.OnNavigationListener listener = new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                String lastOpened = getSharedPreferences("com.bible.preference", Context.MODE_PRIVATE).getString("lastOpened", "mat");
                if (isNavigationSelected) {
                    new DatabaseSQLiteHelper(MyActivity.this).queryDB_ByHeading(lastOpened, header[itemPosition]);
                    arrangeQuery_ByHeadingText(header[itemPosition]);
                }
                isNavigationSelected = true;
                return false;
            }


        };
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setListNavigationCallbacks(spinnerAdapter, listener);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);


    }

    public void arrangeQuery_ByHeadingText(String heading) {
        String Scripture = "";
        for (int index = 0; index < DatabaseSQLiteHelper.chapterStringArrayList.size(); index++) {
            Scripture += DatabaseSQLiteHelper.chapterStringArrayList.get(index) + " : " +
                    DatabaseSQLiteHelper.verseStringArrayList.get(index) +
                    "&nbsp;&nbsp;&nbsp;&nbsp;" + DatabaseSQLiteHelper.ScriptureStringArrayList.get(index) + "<br><br>";
        }
        startActivity(new Intent(MyActivity.this, QueryActivity.class).putExtra("Scripture", Scripture).putExtra("title", heading));
    }

    public void showToast(String message) {
        Toast.makeText(MyActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1: {
                progressDialog = new ProgressDialog(MyActivity.this);
                progressDialog.setMessage("Deploying data files");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                return progressDialog;
            }
            case 2: {
                progressDialog.dismiss();

            }

        }
        return super.onCreateDialog(id);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void setText(String text) {
        WebSettings set = displayView.getSettings();
        set.setDefaultTextEncodingName("UTF-8");
        displayView.loadDataWithBaseURL(null,
                text, "text/html", "uft-8",
                null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        isNavigationSelected = false;
        chapter = 1;
        switch (item.getItemId()) {
            case R.id.mat:
                populateActionBarNavigation("mat");
                getSharedPreferences("com.bible.preference", Context.MODE_PRIVATE).edit().putString("lastOpened", "mat").commit();
                populateTextViewWithScripture("mat");
                decideMaximum("mat");
                setBookTitle("mat");
                break;
            case R.id.mrk:
                populateActionBarNavigation("mrk");
                getSharedPreferences("com.bible.preference", Context.MODE_PRIVATE).edit().putString("lastOpened", "mrk").commit();
                populateTextViewWithScripture("mrk");
                decideMaximum("mrk");
                setBookTitle("mrk");
                break;
            case R.id.luk:
                populateActionBarNavigation("luk");
                getSharedPreferences("com.bible.preference", Context.MODE_PRIVATE).edit().putString("lastOpened", "luk").commit();
                populateTextViewWithScripture("luk");
                decideMaximum("luk");
                setBookTitle("luk");
                break;
            default:
                populateActionBarNavigation("jhn");
                getSharedPreferences("com.bible.preference", Context.MODE_PRIVATE).edit().putString("lastOpened", "jhn").commit();
                populateTextViewWithScripture("jhn");
                decideMaximum("jhn");
                setBookTitle("jhn");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    ProgressDialog progressDialog;

    public void populateDB() {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                showDialog(1);
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                String localstorage[] = {"mat", "mrk", "luk", "jhn"};
                for (String txt : localstorage) {
                    try {
                        InputStream inputStream = getAssets().open(txt + ".txt");
                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                        int chapter = 0;
                        int verse = 0;
                        String heading = "";
                        try {
                            String line = br.readLine();

                            while (line != null) {
                                if (line.trim().equalsIgnoreCase("<" + txt + ">")) {
                                    chapter += 1;
                                    verse = 0;
                                } else if (line.trim().startsWith("HEADING")) {
                                    heading = " " + line.substring(8, line.length());
                                } else {
                                    verse++;
                                    DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(MyActivity.this);
                                    databaseSQLiteHelper.populateDB(chapter, verse, line, heading, txt);
                                }
                                line = br.readLine();
                            }
                        } finally {
                            br.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                showDialog(2);
                populateActionBarNavigation("mat");
                populateTextViewWithScripture("mat");
                decideMaximum("mat");
                setBookTitle("mat");
                super.onPostExecute(s);
            }


        }.execute(null, null, null);
    }
}
