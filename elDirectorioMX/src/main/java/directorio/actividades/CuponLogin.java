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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import directorio.applications.TodoManagerApplication;
import directorio.others.IndexableListView;
import directorio.services.dao.CategoriaDAO;
import directorio.services.dao.CiudadDAO;
import directorio.services.dao.CuponDAO;
import directorio.tools.StringMatcher;

/**
 * Actividad que muestra la lista de categorías con cupones normales después de
 * haber hecho el log in.
 * 
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */
public class CuponLogin extends SherlockActivity implements
		ISideNavigationCallback {

	private SideNavigationView sideNavigationCupon;

	private ArrayList<String> categorias;
	private IndexableListView mListView;
	private CiudadDAO cityDao;

	/** Spinner **/
	private Spinner spCiudades;

	private ProgressBar loadingCats;
	private static final String TAG = "CuponLogin";
	private String country;

	private ArrayList<String> ciudadesDisponibles;

	private CategoriaDAO categoriaDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cupon_login);
		setTitle("Cupones");
		Log.d(TAG, "Actividad " + TAG);

		cityDao = new CiudadDAO();
		TodoManagerApplication tma = (TodoManagerApplication) getApplication();
		country = tma.getCountry();

		sideNavigationCupon = (SideNavigationView) findViewById(R.id.cupon_login_sidenavigationview);
		sideNavigationCupon.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationCupon.setMenuClickCallback(this);

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
		customView.setText("Cupones");
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_USE_LOGO);
		getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// se obtiene el progress bar y se pone invisible
		loadingCats = (ProgressBar) findViewById(R.id.loadingCats);
		loadingCats.setVisibility(View.INVISIBLE);

		// se prepara la lista de categorías
		cargarViewCategorias();

		// Spinner ciudades
		spCiudades = (Spinner) findViewById(R.id.spCiudades);

		// se pone el tipo de adapter que va a tener el spinner
		ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item);

		// y el tipo de dropdown que va a tener
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		try {
			// se usa un async task para descargar las ciudades, y para que se
			// pueda mostrar en pantalla un progress dialog
			ciudadesDisponibles = new DownloadCiudadesAsync(this).execute()
					.get();
		} catch (InterruptedException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		} catch (ExecutionException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}

		// se agrega la primera opción a la lista
		adapter1.add("Todas las ciudades");
		for (int i = 0; i < ciudadesDisponibles.size(); i++) {
			// se agregan las ciudades a la lista
			String ciudad = ciudadesDisponibles.get(i);
			boolean exists = false;
			for (int j = 0; j < adapter1.getCount(); j++) {
				if (adapter1.getItem(j).equals(ciudad)) {
					exists = true;
				}
			}
			if (exists != true) {
				adapter1.add(ciudad);
			}
		}

		// se agrega al spinner de las ciudades el adapter con toda la
		// información
		spCiudades.setAdapter(adapter1);

		// lo que pasa cuando se selecciona un item de la lista
		spCiudades.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				// se obtiene la ciudad seleccionada
				String city = spCiudades.getSelectedItem().toString();
				// se cargan todas las categor�as
				if (!city.equals("Todas las ciudades")) {
					city = city.replace(" ", "%20");
					cargarViewCategoriasCity(city);
				} else {
					// se cargan nada mas las que están seleccionadas
					cargarViewCategorias();
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});

	}

	/**
	 * Método que carga las categorías con cupones.
	 */
	public void cargarViewCategorias() {
		categoriaDao = new CategoriaDAO();

		// se hace la descarga de la información
		try {
			categorias = new DownloadCategoriasAsync(this).execute().get();
		} catch (InterruptedException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		} catch (ExecutionException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}

		// se ordena la información descargada
		Collections.sort(categorias);

		// se agrega el content adapter
		ContentAdapter adapter = new ContentAdapter(this,
				android.R.layout.simple_list_item_1, categorias);

		// se declara el custom list
		mListView = (IndexableListView) findViewById(R.id.android_listview);

		// se le a�ade el adapter con la ifnromación
		mListView.setAdapter(adapter);

		// se le permite scrollear de manera rápida
		mListView.setFastScrollEnabled(true);

		// click listener
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// se pone visible el spinner
				loadingCats.setVisibility(View.VISIBLE);

				// la categoría seleccionada
				final String categoria = categorias.get(arg2);

				// un thread para empezar la siguiente actividad
				Thread thread = new Thread() {
					public void run() {
						// los intent para la nueva actividad
						Intent intent = new Intent(CuponLogin.this,
								CuponesLista.class);

						// se pasa el nombre de la categoría
						intent.putExtra("categoria", categoria);

						// se empieza el intent
						startActivity(intent);
					}
				};
				thread.start();
			}
		});
	}

	/**
	 * Muestra las categorías sorteadas por ciudad.
	 * 
	 * @param cityName
	 *            La ciudad por lo que se va a sortear.
	 */
	public void cargarViewCategoriasCity(String cityName) {

		final String cityN = cityName;
		CategoriaDAO categoriaDao = new CategoriaDAO();
		categorias = categoriaDao.getCategoriasConCuponesCiudad(cityName);
		Collections.sort(categorias);
		ContentAdapter adapter = new ContentAdapter(this,
				android.R.layout.simple_list_item_1, categorias);
		mListView = (IndexableListView) findViewById(R.id.android_listview);
		mListView.setAdapter(adapter);
		mListView.setFastScrollEnabled(true);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				loadingCats.setVisibility(View.VISIBLE);

				final CuponDAO cuponDao = new CuponDAO();
				final String categoria = categorias.get(arg2);
				Thread holo = new Thread() {
					public void run() {
						try {
							sleep(100);
							cuponDao.cuponesPorCategorias(categoria);

							Intent intent = new Intent(CuponLogin.this,
									CuponesLista.class);
							intent.putExtra("categoria", categoria);
							intent.putExtra("ciudad", cityN);
							startActivity(intent);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
					}
				};
				holo.start();

			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			sideNavigationCupon.toggleMenu();
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
		if (sideNavigationCupon.isShown()) {
			sideNavigationCupon.hideMenu();
		} else {
			Intent intent = new Intent(CuponLogin.this, Search.class);
			startActivity(intent);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		loadingCats.setVisibility(View.INVISIBLE);
		com.facebook.Settings.publishInstallAsync(getApplicationContext(),
				getString(R.string.facebook_app_id));
	}

	/**
	 * Clase que se encarga de mostrar el abecedario en la parte lateral de la
	 * interfaz.
	 * 
	 * @author NinjaDevelop
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
	 * AsyncTask que se encarga de descargar las ciudades que están disponibles.
	 * 
	 * @author Publysorpresas
	 * 
	 */
	private class DownloadCiudadesAsync extends
			AsyncTask<Void, Integer, ArrayList<String>> {

		private ProgressDialog pd;
		private Context ctx;

		public DownloadCiudadesAsync(Context c) {
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
			pd.setTitle("Descargando Informaci�n");
			pd.setMessage("Descargando Informaci�n");
			pd.setIndeterminate(false);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			return cityDao.getCiudadesConCupones(country);
		}

	}

	/**
	 * AsyncTask que se encarga de descargar las categorías disponibles.
	 * 
	 * @author Publysorpresas
	 * 
	 */
	private class DownloadCategoriasAsync extends
			AsyncTask<Void, Integer, ArrayList<String>> {

		private ProgressDialog pd;
		private Context ctx;

		public DownloadCategoriasAsync(Context c) {
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
			pd.setTitle("Descargando Informaci�n");
			pd.setMessage("Descargando Informaci�n");
			pd.setIndeterminate(false);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			return categoriaDao.getCategoriasConCupones(country);
		}

	}

}
