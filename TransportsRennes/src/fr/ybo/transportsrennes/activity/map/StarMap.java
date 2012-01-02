package fr.ybo.transportsrennes.activity.map;

import android.os.Bundle;
import android.webkit.WebView;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.commun.BaseActivity.BaseSimpleActivity;

public class StarMap extends BaseSimpleActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.starmap);
		getActivityHelper().setupActionBar(R.menu.default_menu_items);
		WebView webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.loadUrl("http://support-twitter.herokuapp.com/public/images/Rennes_urb_complet.png");
	}

}
