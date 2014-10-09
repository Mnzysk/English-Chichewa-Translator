package com.group1.englishchichewatranslator;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class Help extends Activity {
	WebView webview;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		setWebView();
		String help = "file:///android_asset/about/about.htm";
		webview.loadUrl(help);
	}
	private void setWebView() {
		webview = (WebView) findViewById(R.id.transHelp);
		webview.setInitialScale(1);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setUseWideViewPort(true);
	}
}
