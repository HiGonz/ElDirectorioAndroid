package directorio.actividades;

import android.content.Intent;
import android.graphics.Typeface;
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
import directorio.services.dao.FavoritosDAO;

/**
 * Clase que muestra la lista de Favoritos
 * 
 * @author Carlos Tirado
 * @author Juan Carlos Hinojo
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */
public class Favoritos extends SherlockActivity implements
		ISideNavigationCallback {

	private SideNavigationView sideNavigationFavoritos;

	private ArrayList<Advertiser> adds = null;
	private ListView Lista;
	private static final String TAG = "Favoritos";
	private FavoritosDAO fd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_destacados);
		setTitle("Favoritos");
		Log.d(TAG, "Actividad " + TAG);

		// clase de acceso a la base de datos.
		fd = new FavoritosDAO(this);
		sideNavigationFavoritos = (SideNavigationView) findViewById(R.id.show_destacados_sidenavigationview);
		sideNavigationFavoritos.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationFavoritos.setMenuClickCallback(this);

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
		customView.setText("Favoritos");
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_USE_LOGO);
		getSupportActionBar().setCustomView(customView, params);

		ProgressBar pb = (ProgressBar) findViewById(R.id.progresBar);
		pb.setVisibility(View.INVISIBLE);

		// se cargan los favoritos
		loadFavs();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Método que carga los favoritos de la base de datos.
	 */
	private void loadFavs() {
		TodoManagerApplication todoManagerApp = (TodoManagerApplication) getApplication();
		
		// se obtiene el estado de la red.
		final boolean networkStatus = todoManagerApp.isNetworkAvailable();
		
		
		if (networkStatus != false) {
			// si hay red, se descargan los favoritos
			adds = fd.returnFavoritos(todoManagerApp.getCountry());
		} else {
			// si no hay red, se muestran solo los datos que hay en la base de datos
			adds = fd.getFavoritosOffline();
		}
		
		Lista = (ListView) findViewById(R.id.list);
		Lista.setAdapter(new MyCustomAdapter(this, adds));
		Lista.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (networkStatus != false) {
					final String nombrenegocio = adds.get(arg2).getNombre();
					Thread threadNegocio = new Thread() {
						public void run() {

							try {
								sleep(100);
								Intent correo = new Intent(Favoritos.this,
										ShowAdvertiser.class);
								correo.putExtra("advertiser", nombrenegocio);
								startActivity(correo);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					};
					threadNegocio.start();
				} else {
					Intent intent = new Intent(Favoritos.this,
							NoNetworkActivity.class);
					Favoritos.this.startActivity(intent);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		loadFavs();
		com.facebook.Settings.publishInstallAsync(getApplicationContext(),
				getString(R.string.facebook_app_id));
		super.onResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			sideNavigationFavoritos.toggleMenu();
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
		if (sideNavigationFavoritos.isShown()) {
			sideNavigationFavoritos.hideMenu();
		} else {
			Intent intent = new Intent(Favoritos.this, Search.class);
			startActivity(intent);
		}
	}

}
