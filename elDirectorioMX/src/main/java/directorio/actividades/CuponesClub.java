package directorio.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
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
 * Clase que se encarga de mostrar los cupones que son del club.
 * 
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */
public class CuponesClub extends SherlockActivity implements
		ISideNavigationCallback {

	private SideNavigationView cuponesSideNavView;

	/** Variable que representa si se pudo o no iniciar sesión **/
	private String logStatus = null;

	// TAG de la actividad
	private static final String TAG = "CuponesClub";

	/** Variable que representa el estado de la red **/
	private boolean networkStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Actividad " + TAG);

		// Codigo guardar
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(CuponesClub.this);

		// se verifica el estado de la red
		TodoManagerApplication tma = (TodoManagerApplication) getApplication();
		networkStatus = tma.isNetworkAvailable();

		// En caso de que no hay internet, el estado se vuelve not loggedIn
		String estado = "";
		if (networkStatus != false) {
			estado = settings.getString("estado", "Not LoggedIn");
		} else {
			estado = "Not LoggedIn";
		}

		if (estado.equals("LoggedClub")) {
			ChangeLayout();
		} else if (estado.equals("Logged")) {
			ChangeLayoutNormal();
		} else {

			setContentView(R.layout.activity_cupones);
			setTitle("Club El Directorio");

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
			customView.setText("Cupones El Directorio");
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
						Intent intent = new Intent(CuponesClub.this,
								NoNetworkActivity.class);
						CuponesClub.this.startActivity(intent);
					}
				}
			});

			btnLogin.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (networkStatus != false) {
						new LoginAsync().execute(username.getText().toString(),
								toMd5(password.getText().toString()));
					} else {
						Intent intent = new Intent(CuponesClub.this,
								NoNetworkActivity.class);
						CuponesClub.this.startActivity(intent);
					}
				}
			});
            
		}

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
			cuponesSideNavView.toggleMenu();
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
		if (cuponesSideNavView.isShown()) {
			cuponesSideNavView.hideMenu();
		} else {
			Intent intent = new Intent(CuponesClub.this, Search.class);
			startActivity(intent);
		}
	}

	/**
	 * M�todo que cifra un string a un hash de MD5.
	 * 
	 * @param pass
	 *            El password a cifrar.
	 * @return El password ya cifrado.
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
	 * M�todo que hace el log in via web services
	 * 
	 * @param usuario
	 *            Usuario
	 * @param contramd5
	 *            Contraseña
	 * @return Un string con un mensaje dictando si ya se inició sesión o no.
	 */
	private String loguear(final String usuario, final String contramd5) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://m.eldirectorio.mx/loginHandler.axd");
		logStatus = "Not LoggedIn";
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("username", usuario));
			nameValuePairs.add(new BasicNameValuePair("md5", contramd5));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			int comprobacion = response.getStatusLine().getStatusCode();

			if (comprobacion == 200) {
				logStatus = "Logged";

				// entity response
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);

			} else {
				logStatus = "Not LoggedIn";

			}
		} catch (ClientProtocolException e) {

		} catch (IOException e) {

		}

		return logStatus;

	}

	/**
	 * Método que inicia la actividad de login de cupones del club.
	 */
	public void ChangeLayout() {
		Intent i = new Intent(CuponesClub.this, CuponClubLogin.class);
		CuponesClub.this.startActivity(i);
		finish();
	}

	/**
	 * Método que inicia la actividad que se encarga de mostrar la información
	 * de el club.
	 */
	public void ChangeLayoutNormal() {
		Intent i = new Intent(CuponesClub.this, InfoClub.class);
		CuponesClub.this.startActivity(i);
		finish();
	}

	/**
	 * Clase AsyncTask que se encarga de hacer el log in.
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
			Toast.makeText(CuponesClub.this, "Resultado=" + result,
					Toast.LENGTH_LONG).show();
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(CuponesClub.this);
			Editor session = settings.edit();

			if (result.equals("0")) {
				session.putString("estado", "Logged");
				session.commit();

				ChangeLayoutNormal();

			} else if (result.equals("1")) {
				session.putString("estado", "LoggedClub");
				session.commit();

				// Change Layout
				ChangeLayout();

				Toast.makeText(CuponesClub.this, "Loggeado Correctamente!",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(CuponesClub.this, "Datos Incorrectos",
						Toast.LENGTH_LONG).show();
			}
		}
	}

}
