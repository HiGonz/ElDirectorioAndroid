package directorio.actividades;

import android.annotation.SuppressLint;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

import java.util.ArrayList;
import java.util.Collections;

import directorio.adapters.CustomCategories;
import directorio.applications.TodoManagerApplication;
import directorio.services.dao.CategoriaDAO;

/**
 * Clase que se encarga de mostrar las categorías principales.
 * 
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */
@SuppressLint("ParserError")
public class MainCategories extends SherlockActivity implements
		ISideNavigationCallback {

	private SideNavigationView sideNavigationCategorias;

	private ProgressBar progreso;

	private ArrayList<String> categorias = null;
	private ListView Lista;
	private static final String TAG = "MainCategories";

	private CategoriaDAO catDAO;

	private TodoManagerApplication tma;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitiy_main_categories);
		setTitle("Categorias");
		Log.d(TAG, "Actividad " + TAG);

		tma = (TodoManagerApplication) getApplication();
		boolean network = tma.isNetworkAvailable();
		if (network == false) {
			Intent intent = new Intent(MainCategories.this,
					NoNetworkActivity.class);
			startActivity(intent);
			finish();
		} else {

			sideNavigationCategorias = (SideNavigationView) findViewById(R.id.categorias_sidenavigationview);
			sideNavigationCategorias.setMenuItems(R.menu.side_navigation_menu);
			sideNavigationCategorias.setMenuClickCallback(this);

			// Cambio Diseño ActionBar
			getSupportActionBar().setBackgroundDrawable(
					this.getResources().getDrawable(R.drawable.header));
			getSupportActionBar().setIcon(R.drawable.menu);
			getSupportActionBar().setDisplayOptions(0,
					ActionBar.DISPLAY_HOME_AS_UP);
			TextView customView = new TextView(this);
			customView.setTextColor(getResources().getColor(
					android.R.color.white));
			customView.setTextSize(20f);
			customView.setTypeface(null, Typeface.BOLD);
			ActionBar.LayoutParams params = new ActionBar.LayoutParams(
					ActionBar.LayoutParams.WRAP_CONTENT,
					ActionBar.LayoutParams.WRAP_CONTENT,
					Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
			customView.setText("Categorías");
			getSupportActionBar().setDisplayOptions(
					ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
							| ActionBar.DISPLAY_USE_LOGO);
			getSupportActionBar().setCustomView(customView, params);

			catDAO = new CategoriaDAO();

			categorias = new ArrayList<String>();

			// se descargan las categorías disponibles en el país seleccionado
			// por el usuario
			new LoadCategoriasAsync(this).execute();

			// carga los elementos de la interfaz
			setupViews();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	/**
	 * Método que carga las vistas con los elementos descargados.F
	 */
	private void loadViews() {
		categorias.add("Todas las categorias");
		Collections.sort(categorias);
		Lista.setAdapter(new CustomCategories(MainCategories.this, categorias));
		Lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				final String categoria = categorias.get(arg2);

				Thread thread = new Thread() {
					public void run() {
						try {

							if (categoria == "Todas las categorias") {
								Intent in = new Intent(MainCategories.this,
										Categorias.class);
								startActivity(in);
							} else {

								sleep(100);

								Intent showCategoriesIntent = new Intent(
										MainCategories.this,
										ShowCategorias.class);
								showCategoriesIntent.putExtra("estado", 2);
								showCategoriesIntent.putExtra("categoria",
										categoria);
								startActivity(showCategoriesIntent);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
							Log.e(TAG, e.toString());
						}
					}
				};
				thread.start();
				progreso.setVisibility(ProgressBar.VISIBLE);
				progreso.setIndeterminate(true);

			}
		});

	}

	/**
	 * Método que prepara los elementos de la interfaz para mostrar al usuario.
	 */
	private void setupViews() {
		try {
			progreso = (ProgressBar) findViewById(R.id.progreso);
			boolean estadoBarra = progreso.isShown();
			if (!estadoBarra) {
				progreso.setVisibility(ProgressBar.INVISIBLE);
				progreso.setIndeterminate(false);
			}

		} catch (Exception ex) {
			Log.e(TAG, ex.toString());
		}

		Lista = (ListView) findViewById(R.id.list);
	}

	/**
	 * Método que hace la descarga de información
	 */
	private void downloadCategories() {
		categorias = catDAO.getFeatured(tma.getCountry());
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
			Intent intent = new Intent(MainCategories.this, Search.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		progreso.setVisibility(ProgressBar.INVISIBLE);
		progreso.setIndeterminate(false);
		com.facebook.Settings.publishInstallAsync(getApplicationContext(),
				getString(R.string.facebook_app_id));
	}

	/**
	 * Async Task que se encarga de hacer la descaga de la información en un
	 * thread aparte.
	 * 
	 * @author Publysorpresas.
	 * 
	 */
	public class LoadCategoriasAsync extends AsyncTask<Void, Integer, Void> {
		private Context ctx;
		private ProgressDialog pd;

		public LoadCategoriasAsync(Context c) {
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
			pd.setTitle("Obteniendo categorías");
			pd.setMessage("Descargando categorías disponibles");
			pd.setIndeterminate(false);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			downloadCategories();
			return null;
		}

	}
}