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
import directorio.services.dao.AdvertiserDAO;

/**
 * Método que muestra los negocios que se encuentran en las categorías.
 * 
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */
public class ShowCategorias extends SherlockActivity implements
		ISideNavigationCallback {

	private SideNavigationView sideNavigationShowCategorias;

	private ArrayList<Advertiser> adds = null;
	private ListView Lista;
	private final static String TAG = "ShowCategorias";
	private ProgressBar progreso;

	private TodoManagerApplication tma;

	private AdvertiserDAO advDAO;

	private String cat;

	private String city;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_destacados);
		Log.d(TAG, "Actividad " + TAG);

		sideNavigationShowCategorias = (SideNavigationView) findViewById(R.id.show_destacados_sidenavigationview);
		sideNavigationShowCategorias.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationShowCategorias.setMenuClickCallback(this);

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

		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_USE_LOGO);
		getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		progreso = (ProgressBar) findViewById(R.id.progresBar);
		boolean estadoBarra = progreso.isShown();
		if (!estadoBarra) {
			progreso.setVisibility(ProgressBar.INVISIBLE);
			progreso.setIndeterminate(false);
		}

		advDAO = new AdvertiserDAO();

		// se obtiene el nombre de la categoría a mostrar
		cat = getIntent().getExtras().getString("categoria");

		// se obtiene la ciudad de la cual se mostraran las categorías
		city = getIntent().getExtras().getString("city");

		setTitle(cat);// Nombre de la Categoria como Titulo
		customView.setText(cat);

		tma = (TodoManagerApplication) getApplication();

		// se hace la descarga de la información a mostrar
		ShowAsync doStuff = new ShowAsync(this);
		doStuff.execute();

		// se cargan los elementos de la interfaz
		prepareStuff();

	}

	/**
	 * Se preparan las cosas de la interfaz gráfica
	 */
	private void prepareStuff() {
		Lista = (ListView) findViewById(R.id.list);
	}

	/**
	 * Se llenan los elementos de la interfaz gráfica con la información
	 * descargada.
	 */
	private void loadStuff() {
		Lista.setAdapter(new MyCustomAdapter(this, adds));
		Lista.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final String nombrenegocio = adds.get(arg2).getNombre();
				Thread holo = new Thread() {
					public void run() {
						try {
							sleep(100);
							Intent correo = new Intent(ShowCategorias.this,
									ShowAdvertiser.class);
							correo.putExtra("advertiser", nombrenegocio);
							startActivity(correo);
						} catch (InterruptedException e) {
							Log.e(TAG, e.toString());
							e.printStackTrace();
						}
					}
				};
				holo.start();
				progreso.setVisibility(ProgressBar.VISIBLE);
				progreso.setIndeterminate(true);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			sideNavigationShowCategorias.toggleMenu();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	protected void onResume() {
		progreso.setVisibility(ProgressBar.INVISIBLE);
		progreso.setIndeterminate(false);
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
		if (sideNavigationShowCategorias.isShown()) {
			sideNavigationShowCategorias.hideMenu();
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * Async Task que hace la descarga de información.
	 * 
	 * @author Publysorpresas
	 * 
	 */
	public class ShowAsync extends AsyncTask<Void, Integer, Void> {

		private ProgressDialog pd;
		private Context ctx;

		public ShowAsync(Context c) {
			ctx = c;
		}

		@Override
		protected void onPostExecute(Void result) {
			loadStuff();
			pd.cancel();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(ctx);
			pd.setTitle("Cargando");
			pd.setMessage("Descargando información");
			pd.setIndeterminate(false);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			String country = tma.getCountry();
            String parsed = country.replace(" ","%20");

			if (city != null && !city.equals("Todas las ciudades")
					&& !city.equals("")) {
				adds = advDAO.getByCategoryCity(cat, city);
			} else {
				adds = advDAO.getByCategory(cat, parsed);
			}
			return null;
		}

	}

}
