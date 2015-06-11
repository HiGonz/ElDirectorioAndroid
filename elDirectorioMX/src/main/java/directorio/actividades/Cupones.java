package directorio.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.app.SherlockActivity;
//import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import directorio.applications.TodoManagerApplication;

//Guardar
//Async

/**
 * Clase que muestra los cupones
 * 
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */
public class Cupones extends ActionBarActivity implements
		ISideNavigationCallback {

	private SideNavigationView cuponesSideNavView;

	// Guardar
	private static final String TAG = "Cupones";

	/**
	 * Guarda el estado del log del usuario (si está loggeado, si no lo está, o
	 * si esta loggeado como club)
	 **/
	private String estadoLogUsuario = null;

	/**
	 * Guarda el estado de la red del celular (si hay red disponible, o si no la
	 * hay)
	 **/
	private boolean networkStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Actividad " + TAG);

		// Codigo guardar
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(Cupones.this);
		TodoManagerApplication tma = (TodoManagerApplication) getApplication();

		// se consigue el estado de la red
		networkStatus = tma.isNetworkAvailable();

		// variable temporal para guaradar temporalmente el estado del usuario
		// el estado se cambia a not logged in si no hay conexión a internet
		String estado = "";
		if (networkStatus != false) {
			estado = settings.getString("estado", "Not LoggedIn");
		} else {
			estado = "Not LoggedIn";
		}


        ChangeLayout();
		// EN caso de que si hay internet, y se esta logueado, se cambia la
		// interfaz
		if (estado.equals("Logged") || estado.equals("LoggedClub")) {
			ChangeLayout();
		} else {
			// END
			setContentView(R.layout.activity_cupones);
			setTitle("Cupones");

			cuponesSideNavView = (SideNavigationView) findViewById(R.id.cupones_sidenavigationview);
			cuponesSideNavView.setMenuItems(R.menu.side_navigation_menu);
			cuponesSideNavView.setMenuClickCallback(this);

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
			customView.setText("Cupones");
			getSupportActionBar().setDisplayOptions(
					ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
							| ActionBar.DISPLAY_USE_LOGO);
			getSupportActionBar().setCustomView(customView, params);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

			Button btnRegistro = (Button) findViewById(R.id.BtnRegistro);
			Button btnLogin = (Button) findViewById(R.id.BtnIniciar);

			final EditText username = (EditText) findViewById(R.id.usuarioCupones);
			final EditText password = (EditText) findViewById(R.id.contraCupones);

			btnRegistro.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (networkStatus != false) {
						Intent intentRegistro = new Intent(v.getContext(),
								CuponesRegistro.class);
						v.getContext().startActivity(intentRegistro);
					} else {
						Intent intent = new Intent(Cupones.this,
								NoNetworkActivity.class);
						Cupones.this.startActivity(intent);
					}
				}
			});

			btnLogin.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (networkStatus != false) {
						new LoginAsync().execute(username.getText().toString(),
								toMd5(password.getText().toString()));
					} else {
						Intent intent = new Intent(Cupones.this,
								NoNetworkActivity.class);
						Cupones.this.startActivity(intent);
					}

				}
			});

		} // End if guardar

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			cuponesSideNavView.toggleMenu();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	protected void onResume() {
	//	com.facebook.Settings.publishInstallAsync(getApplicationContext(),
	//			getString(R.string.facebook_app_id));
		super.onResume();
	}

	@Override
	public void onSideNavigationItemClick(int itemId) {
		ChangeLayout.layoutChange(itemId, this);
		finish();
	}

	@Override
	public void onBackPressed() {
		if (cuponesSideNavView.isShown()) {
			cuponesSideNavView.hideMenu();
		} else {
			Intent intent = new Intent(Cupones.this, Search.class);
			startActivity(intent);
		}
	}

	/**
	 * M'etodo que encripta la contraseña para mandarla al servidor.
	 * 
	 * @param pass
	 *            La contraseña del usuario.
	 * @return Un String con la contraseña ya encriptada.
	 */
	private String toMd5(String pass) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			md.update(pass.getBytes("UTF-8"));
			byte[] digest = md.digest();
			BigInteger bigInt = new BigInteger(1, digest);
			String hashtext = bigInt.toString(16);
			// Zero pad it to complete the 32 chars
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;

		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Método que se encarga de hacer el log hacia el servidor, comprobando que
	 * las credenciales del usuario sean las correctas.
	 * 
	 * @param usuario
	 *            El username del usuario.
	 * @param contramd5
	 *            La contraseña ya encriptada del usuario
	 * @return El estado del log del usuario
	 */
	private String loguear(final String usuario, final String contramd5) {

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://m.eldirectorio.mx/loginHandler.axd");
		estadoLogUsuario = "Not LoggedIn";
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("username", usuario));
			nameValuePairs.add(new BasicNameValuePair("md5", contramd5));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			int comprobacion = response.getStatusLine().getStatusCode();

			if (comprobacion == 200) {
				estadoLogUsuario = "Logged";
				// entity response
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);

			} else {
				estadoLogUsuario = "Not LoggedIn";

			}
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.toString());

		} catch (IOException e) {
			Log.e(TAG, e.toString());

		}

		return estadoLogUsuario;

	}

	/**
	 * Cambiar el Layout
	 */
	public void ChangeLayout() {
		Intent i = new Intent(Cupones.this, CuponLogin.class);
		Cupones.this.startActivity(i);
		finish();
	}

	/**
	 * Async Task que se encarga de hacer la comunicación necesaria para el Log
	 * In.
	 * 
	 * @author NinjaDevelop
	 * 
	 */
	private class LoginAsync extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String username = params[0];
			String pass = params[1];
			String result = loguear(username, pass);
			return result;

		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(Cupones.this, "Resultado=" + result,	Toast.LENGTH_LONG).show();
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(Cupones.this);
			Editor session = settings.edit();

			if (result.equals("0")) {
				session.putString("estado", "Logged");
				session.commit();
				ChangeLayout();
			} else if (result.equals("1")) {
				session.putString("estado", "LoggedClub");
				session.commit();
				// Change Layout
				ChangeLayout();
				Toast.makeText(Cupones.this, "Loggeado Correctamente!",Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(Cupones.this, "Datos Incorrectos",Toast.LENGTH_LONG).show();
			}
		}
	}

}
