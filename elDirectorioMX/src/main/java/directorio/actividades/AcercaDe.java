package directorio.actividades;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import android.support.v7.appcompat.BuildConfig;

//import com.AppCompat.app.ActionBar;
//import com.actionbarsherlock.app.SherlockActivity;
//import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

/**
 * Clase que muestra el Acerca De de la aplicación.
 * 
 * @author NinjaDevelop
 * 
 */
public class AcercaDe extends ActionBarActivity implements
		ISideNavigationCallback {

	private SideNavigationView sideNavigationAcercaDe;
	private static final String TAG = "AcercaDe";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_acerca_de);
		setTitle("Acerca De");
		Log.d(TAG, "Actividad " + TAG);

		// Menu Lateral
		sideNavigationAcercaDe = (SideNavigationView) findViewById(R.id.acerca_de_sidenavigationview);
		sideNavigationAcercaDe.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationAcercaDe.setMenuClickCallback(this);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
		customView.setText("Acerca De");
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_USE_LOGO);
		getSupportActionBar().setCustomView(customView, params);

	}

	@Override
	protected void onResume() {
		// código que se debe agregar para hacer la comunicación con facebook
		// para los adds
		//com.facebook.Settings.publishInstallAsync(getApplicationContext(),
		//		getString(R.string.facebook_app_id));
		super.onResume();
	}

	/**
	 * Métodos para el menú lateral.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			sideNavigationAcercaDe.toggleMenu();
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
		if (sideNavigationAcercaDe.isShown()) {
			sideNavigationAcercaDe.hideMenu();
		} else {
			super.onBackPressed();
		}
	}

}
