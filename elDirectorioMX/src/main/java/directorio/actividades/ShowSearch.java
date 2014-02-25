package directorio.actividades;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

import java.util.ArrayList;

import directorio.adapters.MyCustomAdapter;
import directorio.applications.TodoManagerApplication;
import directorio.objetos.Advertiser;
import directorio.others.SearchManager;

/**
 * Actividad que muestra los resultados de una búsqueda
 * 
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */
public class ShowSearch extends SherlockActivity implements
		ISideNavigationCallback {

	private SideNavigationView sideNavigationShowSearch;

	private ArrayList<Advertiser> adds = null;
	private ListView Lista;
	private static final String TAG = "ShowSearch";
	private String country;

	private double latitude;

	private double longitude;

	private double kil;

	private String ciudad;

	private String filtro;

	private boolean citySearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_destacados);
		setTitle("Buscar");
		Log.d(TAG, "Actividad " + TAG);

		sideNavigationShowSearch = (SideNavigationView) findViewById(R.id.show_destacados_sidenavigationview);// show_search_sidenavigationview);
		sideNavigationShowSearch.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationShowSearch.setMenuClickCallback(this);

		TodoManagerApplication tma = (TodoManagerApplication) getApplication();
		country = tma.getCountry();

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
		customView.setText("Buscar");
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_USE_LOGO);
		getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		ProgressBar pb = (ProgressBar) findViewById(R.id.progresBar);
		pb.setVisibility(View.INVISIBLE);

		// se obtienen los datos necesarios para trabajar
		citySearch = getIntent().getBooleanExtra("porCiudad", false);
		latitude = getIntent().getExtras().getDouble("latitude");
		longitude = getIntent().getExtras().getDouble("longitude");
		kil = getIntent().getExtras().getDouble("kil");
		ciudad = getIntent().getExtras().getString("ciudad");
		filtro = getIntent().getExtras().getString("busqueda");

		// se crea un async task que haga la búsqueda y descarga en el
		// background.
		AsyncSearch as = new AsyncSearch(this);
		as.execute();

		// en el foreground se preparan los elementos de la interfaz.
		prepareViews();

	}

	@Override
	protected void onResume() {
		com.facebook.Settings.publishInstallAsync(getApplicationContext(),
				getString(R.string.facebook_app_id));
		super.onResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			sideNavigationShowSearch.toggleMenu();
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
		if (sideNavigationShowSearch.isShown()) {
			sideNavigationShowSearch.hideMenu();
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * Se preparan los elementos de la interfaz para ser llenados.
	 */
	private void prepareViews() {
		Lista = (ListView) findViewById(R.id.list);
	}

	/**
	 * Método que llena la lista una vez que se descargó la información.
	 */
	private void fillViews() {
		Lista.setAdapter(new MyCustomAdapter(this, adds));
		Lista.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				final String nombrenegocio = adds.get(arg2).getNombre();
				Thread thread = new Thread() {
					public void run() {

						try {

							sleep(100);
							Intent correo = new Intent(ShowSearch.this,
									ShowAdvertiser.class);
							correo.putExtra("advertiser", nombrenegocio);
							startActivity(correo);

						} catch (InterruptedException e) {
							Log.e(TAG, e.toString());
							e.printStackTrace();
						}
					}
				};
				thread.start();

			}
		});

	}

	/**
	 * Async Task que se encarga de hacer la descarga de los resultados de la
	 * búsqueda.
	 * 
	 * @author Publysorpresas
	 * 
	 */
	public class AsyncSearch extends AsyncTask<Void, Integer, Void> {

		private ProgressDialog pd;
		private Context ctx;

		public AsyncSearch(Context c) {
			ctx = c;
		}

		@Override
		protected void onPostExecute(Void result) {
			fillViews();
			pd.cancel();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(ctx);
			pd.setTitle("Cargando");
			pd.setMessage("Descargando informaci�n");
			pd.setIndeterminate(false);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			if (citySearch == true) {
				adds = SearchManager.negociosCiudad(ciudad);
			} else {
				if (ciudad.equals("Todas las ciudades") && kil == 0.0) {
					adds = SearchManager.returnAll(filtro, country);
				} else {
					adds = SearchManager.negociosenRango(latitude, longitude,
							kil, ciudad, filtro, country);
				}
			}
			return null;
		}

	}

}
