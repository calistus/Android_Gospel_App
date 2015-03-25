package com.example.Gospel;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class QueryActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private WebView displayView;
    private TextView bookTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_activity);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33b5e5")));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        displayView = (WebView) findViewById(R.id.displayView);
        bookTitle = (TextView) findViewById(R.id.bookTitle);
        bookTitle.setText(getIntent().getStringExtra("title"));
        setText(getIntent().getStringExtra("Scripture"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void setText(String text) {
        WebSettings set = displayView.getSettings();
        set.setDefaultTextEncodingName("UTF-8");
        displayView.loadDataWithBaseURL(null,
                text, "text/html", "uft-8",
                null);
    }


}
