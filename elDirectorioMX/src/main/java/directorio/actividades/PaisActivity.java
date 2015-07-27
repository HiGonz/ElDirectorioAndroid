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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import directorio.adapters.CustomCategories;
import directorio.services.dao.PaisDAO;

//import directorio.others.ConnectionChangeReceiver;
//import android.content.BroadcastReceiver;
//import android.content.IntentFilter;

/**
 * Actividad que se le muestra al usuario al principio de la aplicación, solo si
 * no ha seleccionado un país.
 * 
 * @author Publysorpresas
 * 
 */
public class PaisActivity extends ActionBarActivity {

	private ArrayList<String> paisesNames;
	private ListView Lista;
	private static final String TAG = "PaisActivity";
	private PaisDAO pDao;
	//private final BroadcastReceiver bcr = new ConnectionChangeReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pais);
		setTitle("Selecciona tu País");
		Log.d(TAG, "Actividad " + TAG);

		getSupportActionBar().setBackgroundDrawable(
				this.getResources().getDrawable(R.drawable.header));
		getSupportActionBar().setIcon(R.drawable.menu);
		getSupportActionBar()
				.setDisplayOptions(0, ActionBar.DISPLAY_HOME_AS_UP);
		TextView customView = new TextView(this);
		customView.setTextColor(getResources().getColor(android.R.color.white));
		customView.setTextSize(20f);
		customView.setTypeface(null, Typeface.BOLD);
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,	ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL	| Gravity.CENTER_VERTICAL);

		customView.setText("Selecciona tu País");
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO);
		getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

		doPrefs();

	}

	@Override
	protected void onResume() {
	//	com.facebook.Settings.publishInstallAsync(getApplicationContext(),getString(R.string.facebook_app_id));
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(CONNECTIVITY_SERVICE);
//		registerReceiver(bcr, intentFilter);
		super.onResume();
	}

	/**
	 * Método que se encarga de hacer lo de las preferencias.
	 */
	private void doPrefs() {
		// se revisa si se ha seleccionado algún país
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(PaisActivity.this);
		boolean selectionDone = sp.getBoolean("hasSelected", false);

		if (selectionDone == true) {
			// en caso de que si se seleccione un país, empezar la aplicación
			Intent intent = new Intent(PaisActivity.this, Search.class);
			startActivity(intent);
		} else {
			// en caso de que no, mostrar la lista de paises para seleccionar
			pDao = new PaisDAO();

			paisesNames = pDao.getPaises();

			Lista = (ListView) findViewById(R.id.listPaises);
			Lista.setAdapter(new CustomCategories(PaisActivity.this,paisesNames));

			Lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					String pais = paisesNames.get(arg2);
					SharedPreferences paisPreference = PreferenceManager
							.getDefaultSharedPreferences(PaisActivity.this);
					SharedPreferences.Editor editor = paisPreference.edit();
					editor.putString("countrySelected", pais);
					editor.putBoolean("hasSelected", true);
					editor.commit();
					Log.d(TAG, paisPreference.getString("countrySelected",
							"no jala"));
					finish();
					Intent intent = new Intent(PaisActivity.this,
							ScreenSlidePagerActivity.class);
					startActivity(intent);
				}

			});

		}

	}

}
