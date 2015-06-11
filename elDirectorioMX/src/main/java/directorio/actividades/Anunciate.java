package directorio.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.app.SherlockActivity;
//import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

import directorio.services.dao.PaisDAO;

/**
 * Clase que muestra la sección de Anúnciate.
 * 
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */
public class    Anunciate extends ActionBarActivity implements
		ISideNavigationCallback {

	private SideNavigationView sideNavigationView;
	private static final String TAG = "Anunciate";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anunciate);
		setTitle("An�nciate");
		Log.d(TAG, "actividad: " + TAG);
		// web view
		final WebView webviewAnunciate = (WebView) findViewById(R.id.webview);

		// se usan las preferencias para ver el país que se ha seleccionado
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(Anunciate.this);
		// se guarda el país
		String pais = sp.getString("countrySelected", "M�xico");

		PaisDAO pd = new PaisDAO();
		// se descarga el código referente al país seleccionado
		String codigo = pd.getCountryCode(pais);
		String URL = "http://m.eldirectorio.mx/ContactForm.aspx?pa=1&pais=";
		// el url es el url y se pasa el código de país para saber que página
		// descargar
		String finalUrl = URL + codigo;
		
		webviewAnunciate.loadUrl(finalUrl);
		//en caso de que no haya internet, cargar la página que dice que no hay internet
		webviewAnunciate.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				webviewAnunciate
						.loadUrl("file:///android_asset/errorPage.html");
			}
		});

		sideNavigationView = (SideNavigationView) findViewById(R.id.anunciate2_sidenavigationview);
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationView.setMenuClickCallback(this);

		// Cambio Diseño ActionBar
		getSupportActionBar().setBackgroundDrawable(
				this.getResources().getDrawable(R.drawable.header));
		getSupportActionBar().setIcon(R.drawable.menu);
		getSupportActionBar()
				.setDisplayOptions(0, ActionBar.DISPLAY_HOME_AS_UP);
		TextView customView = new TextView(this);
		customView.setTextColor(getResources().getColor(android.R.color.white));
		customView.setTextSize(20f);
		customView.setTypeface(null, Typeface.BOLD);
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.WRAP_CONTENT,
				ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL
						| Gravity.CENTER_VERTICAL);
		customView.setText("Anúnciate");
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_USE_LOGO);
		getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume() {
	//	com.facebook.Settings.publishInstallAsync(getApplicationContext(),
	//			getString(R.string.facebook_app_id));
		super.onResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			sideNavigationView.toggleMenu();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onSideNavigationItemClick(int itemId) {
		ChangeLayout.layoutChange(itemId, this);
		finish();
	}

	@Override
	public void onBackPressed() {
		if (sideNavigationView.isShown()) {
			sideNavigationView.hideMenu();
		} else {
			Intent intent = new Intent(Anunciate.this, Search.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_change_view, menu);
		return true;
	}

}
