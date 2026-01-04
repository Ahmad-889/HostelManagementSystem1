package com.example.hostelmanagementsystem.ui;

import com.example.hostelmanagementsystem.R;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PortfolioActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private static final String PORTFOLIO_URL = "https://ahmadev.site/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        // Initialize views
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        // Set up toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Portfolio");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Configure WebView settings
        configureWebView();

        // Load the portfolio URL
        loadPortfolio();
    }

    private void configureWebView() {
        WebSettings webSettings = webView.getSettings();

        // Enable JavaScript
        webSettings.setJavaScriptEnabled(true);

        // Set user agent for better website compatibility
        webSettings.setUserAgentString(webSettings.getUserAgentString());

        // Enable DOM storage
        webSettings.setDomStorageEnabled(true);

        // Enable database
        webSettings.setDatabaseEnabled(true);

        // Set default text encoding
        webSettings.setDefaultTextEncodingName("utf-8");

        // Enable mixed content (http + https)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // Set zoom support
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // Set cache mode
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // Set custom WebViewClient
        webView.setWebViewClient(new CustomWebViewClient());
    }

    private void loadPortfolio() {
        webView.loadUrl(PORTFOLIO_URL);
    }

    // Custom WebViewClient to handle page loading states
    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(android.view.View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(android.view.View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            progressBar.setVisibility(android.view.View.GONE);
            Toast.makeText(PortfolioActivity.this,
                    "Error: " + description,
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Allow all URLs to load within the WebView
            view.loadUrl(url);
            return true;
        }
    }

    // Handle back button to go back in WebView history
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // Handle toolbar back button
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
