package directorio.actividades;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

import directorio.services.dao.PaisDAO;

/**
 * Actividad que permite el registro de usuarios al servidor.
 * 
 * @author NinjaDevelop
 * 
 */
public class CuponesRegistro extends SherlockActivity implements
		ISideNavigationCallback {

	private SideNavigationView sideNavigationView;
	private static final String TAG="CuponesRegistro";

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CuponesRegistro.this);
		final Editor session = settings.edit();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cupones_registro);
		setTitle("Registro");
		
		Log.d(TAG, "Actividad " + TAG);

		WebView webviewRegistro = (WebView) findViewById(R.id.cupones_registro_webview);
		
		// se obtiene el país que seleccioné el usuario
		String pais = settings.getString("countrySelected", "M�xico");
		PaisDAO pd = new PaisDAO();
		// se obtiene el código del país seleccionado
		String codigo = pd.getCountryCode(pais);
		// se carga la página de registro del país seleccionado
		webviewRegistro.loadUrl("http://m.eldirectorio.mx/Register.aspx?pa=1&pais=" + codigo);
		webviewRegistro.getSettings().setJavaScriptEnabled(true);
		webviewRegistro.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				finish();
				Intent intent = new Intent(CuponesRegistro.this,
						CuponLogin.class);
				CuponesRegistro.this.startActivity(intent);
				session.putString("estado", "Logged");
				session.commit();
				return false;
			}
		});

		sideNavigationView = (SideNavigationView) findViewById(R.id.cupones_registro_sidenavigationview);
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
		customView.setText("Registro");
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
	public void onSideNavigationItemClick(int itemId) {
		ChangeLayout.layoutChange(itemId, this);
	}

	@Override
	public void onBackPressed() {
		if (sideNavigationView.isShown()) {
			sideNavigationView.hideMenu();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onResume() {
		com.facebook.Settings.publishInstallAsync(getApplicationContext(),
				getString(R.string.facebook_app_id));
		super.onResume();
	}
	
	

}
