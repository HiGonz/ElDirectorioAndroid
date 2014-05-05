package directorio.actividades;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

import java.util.ArrayList;

import directorio.applications.TodoManagerApplication;
import directorio.others.SearchManager;
import directorio.services.dao.CiudadDAO;

//import directorio.others.ConnectionChangeReceiver;
//import android.content.BroadcastReceiver;
//import android.content.IntentFilter;

/**
 * Actividad que hace la búsqueda de negocios.
 * 
 * @author NinjaDevelop
 * @author Carlos Tirado
 * @author Juan Carlos Hinojo
 * @author Publysorpresas
 * 
 */
public class Search extends SherlockActivity implements ISideNavigationCallback {

	private SideNavigationView sideNavigationSearch;

	// elementos de la interfaz
    private Context ctx;
	private TextView textoBarra;
	private Spinner spinner;
	private SeekBar barra;
	private Button info;
	private EditText Busqueda;
	private Button busqueda;
	private ProgressBar cargando;
	private TextView buscando;

	private LocationManager lm;
	private double longitude;
	private double latitude;

	private double kilometrosRedonda;

	private Intent intent;

	private LocationManager manager;
	private LocationListener locationListener;

	private static final String TAG = "Search";
	private String selectedCountry;

	private CiudadDAO cityDao;

	private boolean networkStatus;

	private ArrayAdapter<CharSequence> adapter;

	// private final BroadcastReceiver bcr = new ConnectionChangeReceiver();
    //there we go

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_3);
		setTitle("Buscar");
		Log.d(TAG, "Actividad " + TAG);
        ctx = this;
		// se obtiene el estado de la red inalámbrica
		TodoManagerApplication tma = (TodoManagerApplication) getApplication();
		networkStatus = tma.isNetworkAvailable();

		cityDao = new CiudadDAO();
		selectedCountry = tma.getCountry();

		sideNavigationSearch = (SideNavigationView) findViewById(R.id.search_sidenavigationview);
		sideNavigationSearch.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationSearch.setMenuClickCallback(this);

		// Cambio Diseño ActionBar
		getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.header));
		getSupportActionBar().setIcon(R.drawable.menu);

		// getSupportActionBar().set(R.drawable.logo_eldirectorio_2);
		getSupportActionBar()
				.setDisplayOptions(0, ActionBar.DISPLAY_HOME_AS_UP);
		TextView customView = new TextView(this);
		customView.setTextColor(getResources().getColor(android.R.color.white));
		customView.setTextSize(20f);
		customView.setTypeface(null, Typeface.BOLD);
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,	ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		customView.setText("Buscar");
		getSupportActionBar().setDisplayOptions(	ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME| ActionBar.DISPLAY_USE_LOGO);
		getSupportActionBar().setCustomView(customView, params);

		// se cargan los elementos de la interfaz
		setupViews();
		GetCitiesAsync gla = new GetCitiesAsync(Search.this);
		gla.execute();

		// mientras tanto se obtiene la localización
		getLocation();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			sideNavigationSearch.toggleMenu();
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
		if (sideNavigationSearch.isShown()) {
			sideNavigationSearch.hideMenu();
		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

	/**
	 * Cuando se cargue el on resume, se vuelve a buscar la localización del
	 * dispositivo.F
	 */
	@Override
	protected void onResume() {
		super.onResume();
		buscando.setVisibility(TextView.INVISIBLE);
		cargando.setVisibility(ProgressBar.INVISIBLE);
		cargando.setIndeterminate(false);
		manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

		com.facebook.Settings.publishInstallAsync(getApplicationContext(),	getString(R.string.facebook_app_id));
		//
		// IntentFilter filter = new IntentFilter();
		// filter.addAction(CONNECTIVITY_SERVICE);
		// registerReceiver(bcr, filter);
	}

	@Override
    protected void onPause() {
		super.onPause();
		Log.d(TAG, "on pause");
        if (locationListener != null && lm != null){
            lm.removeUpdates(locationListener);
            lm = null;
        }
		// unregisterReceiver(bcr);
    }

	/**
	 * Método que retrae la localización del dispositivo
	 */
	private void getLocation() {
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// La ultima localización conocida del dispositivo, en este caso se
		// trata de obtener por medio de los datos del proovedor de datos, en
		// caso de que no se conozca, se regresa NULL.
		Location loc1 = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		if (loc1 != null) {
			Log.d(TAG, "Lo obtuve por WIFI");
			latitude = loc1.getLatitude();
			longitude = loc1.getLongitude();

		} else {
			Log.d(TAG, "Lo obtuve por GPS");
			Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
			intent.putExtra("enabled", true);
			sendBroadcast(intent);
			lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Location loc = lm
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			try {
				latitude = loc.getLatitude();
				longitude = loc.getLongitude();
			} catch (NullPointerException npe) {
				Log.e(TAG, "No pude obtener localizacion");
				latitude = 0.0;
				longitude = 0.0;
			}
			Intent intento = new Intent("android.location.GPS_ENABLED_CHANGE");
			intento.putExtra("enabled", false);
			sendBroadcast(intento);
		}

//		manager = (LocationManager) this	.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {
			public void onLocationChanged(Location arg0) {
				latitude = arg0.getLatitude();
				longitude = arg0.getLongitude();
				Log.d(TAG, "Localizacion: " + latitude + "," + longitude);
			}
			public void onProviderDisabled(String arg0) {
			}
			public void onProviderEnabled(String arg0) {
			}
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}
		};
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,locationListener);
	}

	/**
	 * Método que carga las partes de la interfaz con los datos descargados
	 */
	private void loadViews() {
		spinner.setAdapter(adapter);
	}

	/**
	 * Método que prepara los elementos de la interfaz gráfica
	 */
	private void setupViews() {

		buscando = (TextView) findViewById(R.id.textoBusqueda);
		buscando.setVisibility(TextView.INVISIBLE);
		cargando = (ProgressBar) findViewById(R.id.progresoBusqueda);
		cargando.setVisibility(ProgressBar.INVISIBLE);

		boolean haycarga = cargando.isShown();

		if (!haycarga) {
			cargando.setVisibility(ProgressBar.INVISIBLE);
			cargando.setIndeterminate(false);
		}

		spinner = (Spinner) findViewById(R.id.spinner_localidades);

		adapter = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		textoBarra = (TextView) findViewById(R.id.mostrar_metros);
		Busqueda = (EditText) findViewById(R.id.busqueda);

		Busqueda.setCompoundDrawablesWithIntrinsicBounds(null, null,
				getResources().getDrawable(R.drawable.search2), null);

		// EditText Search
		Busqueda.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					android.view.KeyEvent event) {
				if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
					if (networkStatus != false) {
						search();
					} else {
						Intent intent = new Intent(Search.this,NoNetworkActivity.class);
						Search.this.startActivity(intent);
					}
					return true;
				}
				return false;
			}
		});

		// Button Search
		busqueda = (Button) findViewById(R.id.search);
		busqueda.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (networkStatus != false) {
					search();
				} else {
					Intent intent = new Intent(Search.this,
							NoNetworkActivity.class);
					Search.this.startActivity(intent);
				}

			}
		});

		barra = (SeekBar) findViewById(R.id.radioALaRedonda);
		barra.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (progress == 0) {
					kilometrosRedonda = 0.0;
					textoBarra.setText("--");
				} else {
					kilometrosRedonda = (double) progress;
					textoBarra.setText(progress + "km");
				}

			}
		});

		// Botón de información, abre una nueva actividad con la información de
		// la aplicación
		info = (Button) findViewById(R.id.info_button);
		info.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(Search.this, AcercaDe.class);
				Search.this.startActivity(myIntent);
			}
		});
	}
	/**
	 * Método que hace la búsqueda usando las entradas del usuario.
	 */
	public void search() {
		// SegundoAlgoritmo de Búsqueda
		if (Busqueda.getText().toString().trim().equals("")) {
			// Mostrar todos los advertiser de la ciudad seleccionada
			String ciudadSeleccionada = spinner.getSelectedItem().toString();
			if (ciudadSeleccionada.equals("Todas las ciudades")) {
				Toast.makeText(Search.this,"¡Debes de insertar un valor de búsqueda!",	Toast.LENGTH_SHORT).show();
			} else {
				intent = new Intent(Search.this, ShowSearch.class);
				intent.putExtra("ciudad", ciudadSeleccionada);
				intent.putExtra("porCiudad", true);
				Search.this.startActivity(intent);
			}
			// Thread timer = new Thread() {
			// public void run() {
			//
			// intent = new Intent(Search.this, ShowSearch.class);
			// intent.putExtra("estado", 1);
			// intent.putExtra("latitude", latitude);
			// intent.putExtra("longitude", longitude);
			// intent.putExtra("kil", kilometrosRedonda);
			// intent.putExtra("ciudad", spinner.getSelectedItem()
			// .toString());
			// intent.putExtra("busqueda", Busqueda.getText().toString());
			// Search.this.startActivity(intent);
			// }
			// };
			// buscando.setVisibility(TextView.VISIBLE);
			// cargando.setVisibility(ProgressBar.VISIBLE);
			// cargando.setIndeterminate(true);
			// timer.start();
			// No se realizará la búsqueda, ya que consume muchos datos.

		} else if (longitude == 0.0 && latitude == 0.0 && kilometrosRedonda != 0.0) {
			// en caso de que no se tenga la ubicación exacta.
			Log.d(TAG, "kil es: " + kilometrosRedonda);
			Toast.makeText(
					Search.this,
					"Espera un momento mas en lo que obtenemos tu ubicación exacta....",
					Toast.LENGTH_SHORT).show();

			lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Location loc1 = lm
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (loc1 != null) {
				Log.d(TAG, "Lo obtuve por WIFI");
				latitude = loc1.getLatitude();
				longitude = loc1.getLongitude();
			} else {
				Log.d(TAG, "Lo obtuve por GPS");
				Intent intent = new Intent(
						"android.location.GPS_ENABLED_CHANGE");
				intent.putExtra("enabled", true);
				sendBroadcast(intent);
				lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				Location loc = lm
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				try {
					latitude = loc.getLatitude();
					longitude = loc.getLongitude();
				} catch (NullPointerException npe) {
					Log.e(TAG, "No pude obtener localización");
					Log.e(TAG, npe.toString());
					latitude = 0.0;
					longitude = 0.0;
				}
				Intent intento = new Intent("android.location.GPS_ENABLED_CHANGE");
				intento.putExtra("enabled", false);
				sendBroadcast(intento);
			}
		} else {
			Thread timer = new Thread() {
				public void run() {
					if (spinner.getSelectedItem().toString()
							.equals("Todas las ciudades")
							&& kilometrosRedonda == 0.0) {
						SearchManager.returnAll(Busqueda.getText().toString(),
								selectedCountry);
					} else {
						SearchManager.negociosenRango(latitude, longitude,
								kilometrosRedonda, spinner.getSelectedItem()
										.toString(), Busqueda.getText()
										.toString(), selectedCountry);
					}

					intent = new Intent(Search.this, ShowSearch.class);
					intent.putExtra("estado", 1);
					intent.putExtra("latitude", latitude);
					intent.putExtra("longitude", longitude);
					intent.putExtra("kil", kilometrosRedonda);
					intent.putExtra("ciudad", spinner.getSelectedItem()
							.toString());
					intent.putExtra("busqueda", Busqueda.getText().toString());
					Search.this.startActivity(intent);
				}
			};
			buscando.setVisibility(TextView.VISIBLE);
			cargando.setVisibility(ProgressBar.VISIBLE);
			cargando.setIndeterminate(true);
			timer.start();
		}
	}
	/**
	 * Async Task que se encarga de la descarga de la lista de las ciudades.
	 * 
	 * @author Publysorpresas
	 * 
	 */
	public class GetCitiesAsync extends AsyncTask<Void, Integer, Void> {

		private Context ctx;
		private ProgressDialog pd = null;

		public GetCitiesAsync(Context c) {
			ctx = c;
		}

		@Override
		protected void onPostExecute(Void result) {
			loadViews();
			pd.cancel();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(ctx);
			pd.setTitle("Obteniendo ciudades");
			pd.setMessage("Obteniendo ciudades disponibles.");
			pd.setIndeterminate(false);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (networkStatus != false) {
				// Se cargan los datos de la ciudades desde la base de datos
				try {
					ArrayList<String> datos = cityDao
							.getCiudades(selectedCountry);
					adapter.add("Todas las ciudades");
					for (int i = 0; i < datos.size(); i++) {
						adapter.add(datos.get(i));
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			} else {
				adapter.add("No hay conexión, no hay datos.");
			}
			return null;
		}
	}

}
