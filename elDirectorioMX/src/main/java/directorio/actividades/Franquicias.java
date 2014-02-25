package directorio.actividades;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

import directorio.services.dao.PaisDAO;

/**
 * Clase que se encarga de mostrar la sección de franquicias.
 * 
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 * 
 */
public class Franquicias extends SherlockActivity implements
		ISideNavigationCallback {

	private SideNavigationView sideNavigationView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_franquicias);
		setTitle("Franquicias");
		Log.d("Franquicias", "Actividad " + "Franquicias");

		final WebView webviewFranquicias = (WebView) findViewById(R.id.franquicias_webview);

		// se obtienen el país seleccionado de las preferencias
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(Franquicias.this);
		String pais = sp.getString("countrySelected", "M�xico");

		// se obtiene el código del país seleccionado
		PaisDAO pd = new PaisDAO();
		String codigo = pd.getCountryCode(pais);

		// se carga la página adecuada para el país
		webviewFranquicias
				.loadUrl("http://m.eldirectorio.mx/FranchisesForm.aspx?pa=1&pais="
						+ codigo);
		webviewFranquicias.getSettings().setJavaScriptEnabled(true);
		webviewFranquicias.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				webviewFranquicias
						.loadUrl("file:///android_asset/errorPage.html");
			}
		});

		sideNavigationView = (SideNavigationView) findViewById(R.id.franquicias_sidenavigationview);
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
		customView.setText("Franquicias");
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_USE_LOGO);
		getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
	protected void onResume() {
		com.facebook.Settings.publishInstallAsync(getApplicationContext(),
				getString(R.string.facebook_app_id));
		super.onResume();
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
			Intent intent = new Intent(Franquicias.this, Search.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_franquicias, menu);
		return true;
	}

}
