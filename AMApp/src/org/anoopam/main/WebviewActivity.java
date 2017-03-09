package org.anoopam.main;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


import org.anoopam.main.home.HomeListActivity;

/**
 * Created by ADMIN on 3/9/2017.
 */

public class WebviewActivity extends AppCompatActivity {
    private String postUrl = "http://www.anoopam.org/amapp/misc/splash-redirect.php";
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setTitle("Anoopam Mission");

        initWebView();

        renderPost();
        webView.loadUrl(postUrl);
    }

    private void initWebView() {
        webView.setWebChromeClient(new MyWebChromeClient(this));
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(view.getVisibility());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /**
                 * Check for the url, if the url is from same domain
                 * open the url in the same activity as new intent
                 * else pass the url to browser activity
                 * */
//
//                if (Webview_utils.isSameDomain(postUrl, url)) {
//                    Intent intent = new Intent(WebviewActivity.this, WebviewActivity.class);
//                    intent.putExtra("postUrl", url);
//                    startActivity(intent);
//                } else {
//                    // launch in-app browser i.e BrowserActivity
//                  //  openInAppBrowser(url);
//                }
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);

    }

    private void renderPost() {
        webView.loadUrl(postUrl);


    }
    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        public MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }

        public void onBackPressed (){

            if (webView.isFocused() && webView.canGoBack()) {
                webView.goBack();
            }
            else {
                openMyDialog(null);

            }
        }
        public void openMyDialog(View view) {
            showDialog(10);
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WebviewActivity.this,HomeListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(WebviewActivity.this,HomeListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
