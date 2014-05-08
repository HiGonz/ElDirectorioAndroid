package directorio.actividades;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import directorio.adapters.CustomCategories;
import directorio.applications.TodoManagerApplication;
import directorio.services.dao.CategoriaDAO;
import directorio.servicios.RegisterDevice;

/**
 * Clase que se encarga de mostrar las categorías principales.
 * 
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */

@SuppressLint("ParserError")
public class MainCategories extends SherlockActivity implements  ISideNavigationCallback {
    //Variables de registro para el GCM/a
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    GoogleCloudMessaging gcm;
    String regid;
    String SENDER_ID = "732420872532";

    private SideNavigationView sideNavigationCategorias;

	private ProgressBar progreso;

	private ArrayList<String> categorias = null;
	private ListView Lista;
	private static final String TAG = "MainCategories";

	private CategoriaDAO catDAO;

	private TodoManagerApplication tma;

    private Context context;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitiy_main_categories);
		setTitle("Categorias");
		Log.d(TAG, "Actividad " + TAG);

     context = getApplicationContext();

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
			getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.header));
			getSupportActionBar().setIcon(R.drawable.menu);
			getSupportActionBar().setDisplayOptions(0,	ActionBar.DISPLAY_HOME_AS_UP);
			TextView customView = new TextView(this);
			customView.setTextColor(getResources().getColor(android.R.color.white));
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

            if (checkPlayServices()) {
                gcm = GoogleCloudMessaging.getInstance(this);
                regid = getRegistrationId(context);
                if (regid.length() == 0) {
                    Log.d("GCM","No hubo registro, comenzando a registrar...");
                    registerInBackground();
                }else{
                    Log.d("GCM",regid);
                    Intent registrationService = new Intent(MainCategories.this, RegisterDevice.class);
                    registrationService.putExtra("uuid",regid);
                    startService(registrationService);
                }
            } else {
                Log.i(TAG, "No valid Google Play Services APK found.");
            }
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
								Intent in = new Intent(MainCategories.this, Categorias.class);
								startActivity(in);
							} else {
								sleep(100);
								Intent showCategoriesIntent = new Intent(MainCategories.this,ShowCategorias.class);
								showCategoriesIntent.putExtra("estado", 2);
								showCategoriesIntent.putExtra("categoria", categoria);
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
     * Metodo que verifica la existencia de Google Play Services en el dispositivo
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /* Se obtiene el UUID persistente en de los preferences */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (registrationId.length() == 0) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /*
    * Metodo que nos regresa las preferencias.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(MainCategories.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    /*
    * Metodo que nos regresa la versión de la aplicación (No Utilizar esta forma)
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager() .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /*
    * Metodo que se encarga de registrar el celular con el GCM y mandar el Registro al Backend
     */
    private void registerInBackground() {
        new AsyncTask(){
            @Override
            protected String doInBackground(Object[] objects) {
                String msg = "";
                Log.d("GCM","Comenzando registro");
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    Log.d("GCM","Comenzando registro ;D");
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    Log.d("GCM",msg);
                    storeRegistrationId(context, regid);

                    Log.d("Mandando",regid);
                    Intent registrationService = new Intent(MainCategories.this, RegisterDevice.class);
                    registrationService.putExtra("uuid",regid);
                    startService(registrationService);
                    //Aqui mandamos el id atraves de un service
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                    ex.printStackTrace();
                    registerInBackground();
                }
                return msg;
            }
        }.execute(null,null,null);
    }

    /*
    * Metodo que manda el ID de registro del GCM en las preferencias para hacerla persistente.
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

	/*
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
        String parsed = tma.getCountry();
        parsed = parsed.replace(" ","%20");
		categorias = catDAO.getFeatured(parsed);
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