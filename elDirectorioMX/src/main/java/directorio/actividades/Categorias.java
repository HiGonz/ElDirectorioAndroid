package directorio.actividades;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.Spinner;
import android.widget.TextView;

//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.app.SherlockActivity;
//import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import directorio.others.IndexableListView;
import directorio.services.dao.AdvertiserDAO;
import directorio.services.dao.CategoriaDAO;
import directorio.services.dao.CiudadDAO;
import directorio.tools.StringMatcher;

/**
 * Clase que se encarga deostrar todas las categorías, y filtrarlas por ciudad.
 * 
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */

public class Categorias extends ActionBarActivity implements
		ISideNavigationCallback {

	// Partes de la interfaz
	private SideNavigationView sideNavigationCategorias;
	private IndexableListView mListView;
	private ProgressBar progreso;

	/** Usado para el retraer las ciudades del WebService **/
	private CiudadDAO cityDao;

	/** Spinner **/
	private Spinner spCiudades;

	private static final String TAG = "Categorias";

	/** El país seleccionado **/
	private String selectedCountry;

	private ArrayList<String> datos;
	private ArrayAdapter<CharSequence> adapter1;
	private CategoriaDAO categoriaDao;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categorias);
		setTitle("Categorias");
		Log.d(TAG, "Actividad " + TAG);

		cityDao = new CiudadDAO();

		sideNavigationCategorias = (SideNavigationView) findViewById(R.id.categorias_sidenavigationview);
		sideNavigationCategorias.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationCategorias.setMenuClickCallback(this);

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
		customView.setText("Categorías");
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_USE_LOGO);
		getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(Categorias.this);
		selectedCountry = sp.getString("countrySelected", "México");
        selectedCountry = selectedCountry.replace(" ","%20");
		// se preparan las vistas
		setupViews();

		// se hace la descarga asíncrona
		new DownloadCitiesAsync(this).execute();

		// se cargan las categorías
		cargarTodasCats();
	}

	/**
	 * Método que hace la carga de los elementos de la interfaz antes de que se
	 * vayan a llenar.
	 */
	private void setupViews() {
		/*
		 * Spinner Ciudades
		 */
		spCiudades = (Spinner) findViewById(R.id.spCiudadesCats);
		adapter1 = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	/**
	 * Método que hace la descarga de las ciudades que van a llenar los
	 * elementos de la interfaz.
	 */
	private void backgroundCities() {
		// Se obtiene la lista de ciudades del web service.
		try {
			datos = cityDao.getCiudadesConCategorias(selectedCountry);
			adapter1.add("Todas las ciudades");
			for (int i = 0; i < datos.size(); i++) {
				adapter1.add(datos.get(i));
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	/**
	 * Método que agarra la información descargada y la usa para llenar los
	 * elementos de la interfaz
	 */
	private void loadViews() {
		spCiudades.setAdapter(adapter1);
		// Cuando seleccionen un elemento de la lista:
		spCiudades.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				String city = spCiudades.getSelectedItem().toString();

				if (!city.equals("Todas las ciudades")) {
					cargarCiudadCats(city);
				} else {
					cargarTodasCats();
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Do nothing...

			}

		});

	}

	/**
	 * Método que pone todas las categorías en la lista de pantalla.
	 */
	public void cargarTodasCats() {
		categoriaDao = new CategoriaDAO();
		// se descargan categorías del web service
		final ArrayList<String> categorias = new ArrayList<String>();
		ArrayList<String> downloadedCategories;
		try {
			downloadedCategories = new DownloadCategoriesAsync(this).execute()
					.get();
			for (int i = 0; i < downloadedCategories.size(); i++) {
				categorias.add(downloadedCategories.get(i));
			}
		} catch (InterruptedException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		} catch (ExecutionException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}

		progreso = (ProgressBar) findViewById(R.id.progreso);
		boolean estadoBarra = progreso.isShown();

		final AdvertiserDAO advertiserDao = new AdvertiserDAO();

		if (!estadoBarra) {
			progreso.setVisibility(ProgressBar.INVISIBLE);
			progreso.setIndeterminate(false);
		}
		Collections.sort(categorias);

		ContentAdapter adapter = new ContentAdapter(this,
				android.R.layout.simple_list_item_1, categorias);

		mListView = (IndexableListView) findViewById(R.id.listview);
		mListView.setAdapter(adapter);
		mListView.setFastScrollEnabled(true);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final String categoria = categorias.get(arg2);

				Thread thread = new Thread() {
					public void run() {
						advertiserDao.getByCategory(categoria, selectedCountry);
						Intent intent = new Intent(Categorias.this,
								ShowCategorias.class);
						intent.putExtra("estado", 2);
						intent.putExtra("categoria", categoria);
						intent.putExtra("city", "Todas las ciudades");
						startActivity(intent);
					}
				};
				thread.start();
				progreso.setVisibility(ProgressBar.VISIBLE);
				progreso.setIndeterminate(true);

			}
		});
	}

	/**
	 * Método que carga las categorías dependiendo de la ciudad seleccionada.
	 * 
	 * @param cityName
	 *            La ciudad que se usará para cargar las categorías.
	 */
	public void cargarCiudadCats(final String cityName) {

		final ArrayList<String> categorias = categoriaDao
				.getCategoriasCiudad(cityName);
		progreso = (ProgressBar) findViewById(R.id.progreso);
		boolean estadoBarra = progreso.isShown();
		final AdvertiserDAO advDao = new AdvertiserDAO();

		if (!estadoBarra) {
			progreso.setVisibility(ProgressBar.INVISIBLE);
			progreso.setIndeterminate(false);
		}
		Collections.sort(categorias);

		ContentAdapter adapter = new ContentAdapter(this,
				android.R.layout.simple_list_item_1, categorias);

		mListView = (IndexableListView) findViewById(R.id.listview);
		mListView.setAdapter(adapter);
		mListView.setFastScrollEnabled(true);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final String categoria = categorias.get(arg2);

				Thread showCategoryThread = new Thread() {
					public void run() {
						advDao.getByCategory(categoria, selectedCountry);
						Intent correo = new Intent(Categorias.this,
								ShowCategorias.class);
						correo.putExtra("estado", 2);
						correo.putExtra("categoria", categoria);
						correo.putExtra("city", cityName);
						startActivity(correo);
					}
				};
				showCategoryThread.start();
				progreso.setVisibility(ProgressBar.VISIBLE);
				progreso.setIndeterminate(true);

			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			sideNavigationCategorias.toggleMenu();
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
		if (sideNavigationCategorias.isShown()) {
			sideNavigationCategorias.hideMenu();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		progreso.setVisibility(ProgressBar.INVISIBLE);
		progreso.setIndeterminate(false);
	//	com.facebook.Settings.publishInstallAsync(getApplicationContext(),
	//			getString(R.string.facebook_app_id));
	}

	/**
	 * Sub-Clase que se encarga de mostrar del lado derecho de la pantalla un
	 * abecedario.
	 * 
	 * @author Juan Carlos Hinojo
	 * 
	 */
	private class ContentAdapter extends ArrayAdapter<String> implements
			SectionIndexer {

		private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		public ContentAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
		}

		public int getPositionForSection(int section) {
			for (int i = section; i >= 0; i--) {
				for (int j = 0; j < getCount(); j++) {
					if (i == 0) {
						for (int k = 0; k <= 9; k++) {
							if (StringMatcher.match(
									String.valueOf(getItem(j).charAt(0)),
									String.valueOf(k)))
								return j;
						}
					} else {
						if (StringMatcher.match(
								String.valueOf(getItem(j).charAt(0)),
								String.valueOf(mSections.charAt(i))))
							return j;
					}
				}
			}
			return 0;
		}

		public int getSectionForPosition(int position) {
			return 0;
		}

		public Object[] getSections() {
			String[] sections = new String[mSections.length()];
			for (int i = 0; i < mSections.length(); i++)
				sections[i] = String.valueOf(mSections.charAt(i));
			return sections;
		}

	}

	/**
	 * Async Task que se encarga de descargar las ciudades en el background.
	 * 
	 * @author Publysorpresas
	 * 
	 */
	public class DownloadCitiesAsync extends AsyncTask<Void, Integer, Void> {

		private Context ctx;
		private ProgressDialog pd = null;

		public DownloadCitiesAsync(Context c) {
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
			// se prepara el progress dialog
			pd = new ProgressDialog(ctx);
			pd.setTitle("Obteniendo Ciudades Disponibles");
			pd.setMessage("Descargando Ciudades");
			pd.setIndeterminate(false);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			backgroundCities();
			return null;
		}

	}

	private class DownloadCategoriesAsync extends
			AsyncTask<Void, Integer, ArrayList<String>> {

		private ProgressDialog pd;
		private Context ctx;

		public DownloadCategoriesAsync(Context c) {
			ctx = c;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			pd.cancel();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(ctx);
			pd.setTitle("Descargando Categorías");
			pd.setMessage("Descargando categorías disponibles");
			pd.setIndeterminate(false);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {
			return categoriaDao.getCategorias(selectedCountry, false);
		}

	}

}