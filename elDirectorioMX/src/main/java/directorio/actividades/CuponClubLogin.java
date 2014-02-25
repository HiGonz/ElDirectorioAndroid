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
 * Actividad que se encarga de mostrar las categorías que tienen cupones del
 * club una vez que iniciaste sesión con el club, de otra manera, muestra una
 * imagen que te invita a registrarte.
 * 
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */
public class CuponClubLogin extends SherlockActivity implements
		ISideNavigationCallback {

	private SideNavigationView sideNavigationCupon;

	private ArrayList<String> categorias;
	private IndexableListView mListView;
	private CiudadDAO cityDao;

	/** Spinner **/
	private Spinner spCiudades;

	private ProgressBar loadingCats;
	private static final String TAG = "CuponClubLogin";
	private String selectedCountry;

	private ArrayList<String> datos;

	private CategoriaDAO categoriaDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cupon_login);
		setTitle("Club El Directorio");
		Log.d(TAG, "Actividad " + TAG);

		cityDao = new CiudadDAO();
		categoriaDao = new CategoriaDAO();

		TodoManagerApplication tma = (TodoManagerApplication) getApplication();
		selectedCountry = tma.getCountry();

		sideNavigationCupon = (SideNavigationView) findViewById(R.id.cupon_login_sidenavigationview);
		sideNavigationCupon.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationCupon.setMenuClickCallback(this);

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
		customView.setText("Club El Directorio");
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_USE_LOGO);
		getSupportActionBar().setCustomView(customView, params);

		loadingCats = (ProgressBar) findViewById(R.id.loadingCats);
		loadingCats.setVisibility(View.INVISIBLE);

		cargarViewCategorias();

		spCiudades = (Spinner) findViewById(R.id.spCiudades);

		ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item);

		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		try {
			datos = new DownloadCiudadesAsync(this).execute().get();
		} catch (InterruptedException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		} catch (ExecutionException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}

		adapter1.add("Todas las ciudades");

		for (int i = 0; i < datos.size(); i++) {
			adapter1.add(datos.get(i));
		}

		spCiudades.setAdapter(adapter1);

		spCiudades.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String city = spCiudades.getSelectedItem().toString();
				if (!city.equals("Todas las ciudades")) {
					city = city.replace(" ", "%20");
					cargarViewCategoriasCity(city);
				} else {
					cargarViewCategorias();
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});

	}

	/**
	 * Método que se encarga de cargar las categorías y mostrarlas.
	 */
	private void cargarViewCategorias() {
		try {
			categorias = new DownloadCategoriesAsync(this).execute().get();
		} catch (InterruptedException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		} catch (ExecutionException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
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

				final String categoria = categorias.get(arg2);

				Thread thread = new Thread() {
					public void run() {

						Intent correo = new Intent(CuponClubLogin.this,
								CuponesClubLista.class);

						correo.putExtra("categoria", categoria);

						startActivity(correo);
					}
				};
				thread.start();
			}
		});

	}

	/**
	 * Método que ajusta la vista conforme a la ciudad seleccionada.
	 * 
	 * @param cityName
	 *            La ciudad seleccionada.
	 */
	private void cargarViewCategoriasCity(String cityName) {
		final String cityN = cityName;

		// se descargan las cateogrías de esa ciudad
		categorias = categoriaDao.getCategoriasConCuponesClubCiudad(cityName);

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

				final CuponDAO cupdao = new CuponDAO();
				final String categoria = categorias.get(arg2);
				Thread thread = new Thread() {
					public void run() {
						try {
							sleep(100);
							cupdao.cuponesPorCategorias(categoria);

							Intent correo = new Intent(CuponClubLogin.this,
									CuponesClubLista.class);
							correo.putExtra("categoria", categoria);
							correo.putExtra("ciudad", cityN);
							startActivity(correo);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
					}
				};
				thread.start();

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
			Intent intent = new Intent(CuponClubLogin.this, Search.class);
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
	 * SubClase que se encarga de mostrar el abecedario lateral de navegación.
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
	 * Async Task que se encarga de descargar las ciudades con cupones.
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
			return cityDao.getCiudadesConCuponesClub(selectedCountry);

		}
	}

	/**
	 * AsyncTask que se encarga de descargar las Categorias disponibles.
	 * 
	 * @author Publysorpresas
	 * 
	 */
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
			pd.setTitle("Descargando Informaci�n");
			pd.setMessage("Descargando Informaci�n");
			pd.setIndeterminate(false);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			return categoriaDao.getCategoriasConCuponesClub(selectedCountry);
		}

	}

}
