package com.nooverlap314.uncertaintycalculator;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class InformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        ActionBar ab = getSupportActionBar();

        ab.setDisplayHomeAsUpEnabled(true);

        WebView webView = (WebView) findViewById(R.id.information_textbox);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setBackgroundColor(0x00FFFFFF);

        String path="file:///android_asset/JQMath/";
        String js = "<html><head><meta charset=\"utf-8\">" +
                "<link rel=\"stylesheet\" href=\"jqmath-0.4.3.css\">" +
                "<script src=\"jquery-1.4.3.min.js\"></script>" +
                "<script src=\"jqmath-etc-0.4.5.min.js\" charset=\"utf-8\"></script>" +
                "</head><body>" +
                getString(R.string.info_message_main) +
                "</body></html>";

        webView.loadDataWithBaseURL(path, js, "text/html",  "UTF-8", null);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }
}
