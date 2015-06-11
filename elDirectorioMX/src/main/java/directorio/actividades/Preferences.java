package directorio.actividades;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

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
import directorio.services.dao.PaisDAO;

/**
 * Clase que se encarga de guardar las preferencias del usuario
 * 
 * @author Publysorpresas
 * 
 */
public class Preferences extends PreferenceActivity {

	private static final String TAG = "PreferenceActivity";

	@SuppressLint("CommitPrefEdits")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// se carga el xml que tiene las preferencias
		// hay métodos depreciados para que la aplicación sea compatible con
		// versiones antiguas de android.
		addPreferencesFromResource(R.layout.preferences);

		//PaisDAO pDao = new PaisDAO();

		// se accesa a la preferencia que guarda el país
		/*ListPreference lp = (ListPreference) findPreference("countrySelected");
		TodoManagerApplication tma = (TodoManagerApplication) getApplication();
*/
		// se revisa si hay conexión a internet para descargar la lista de
		// paises
		/*boolean network = tma.isNetworkAvailable();
		ArrayList<String> paises = new ArrayList<String>();
		if (network != false) {
			// en caso de que si hay internet, se descargan los paises
			paises = pDao.getPaises();
			int nPaises = paises.size();
			CharSequence[] entries = new CharSequence[nPaises];
			for (int i = 0; i < nPaises; i++) {
				entries[i] = paises.get(i);
			}

			lp.setEntries(entries);
			lp.setEntryValues(entries);
		} else {
			// en caso de que no hay internet, se muestra que no hay internet
			paises.add("No hay conexión");
			int nPaises = paises.size();
			CharSequence[] entries = new CharSequence[nPaises];
			for (int i = 0; i < nPaises; i++) {
				entries[i] = paises.get(i);
			}

			lp.setEntries(entries);
			lp.setEntryValues(entries);

			// también se desactiva la opción, para que no pueda seleccionar
			// valores
			lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

				@Override
				public boolean onPreferenceChange(Preference preference,
						Object newValue) {
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(Preferences.this);
					SharedPreferences.Editor editor = sp.edit();
					editor.putBoolean("hasSelected", false);
					editor.commit();
					Log.d("Preferences", "valor");
					return false;
				}
			});
		}*/

		// se cargan los elementos de la interfaz
		setOnViews();

	}

	/**
	 * Método que prepara los elementos de la interfaz.
	 */
	@SuppressWarnings("deprecation")
	private void setOnViews() {

		// Elemento acerca de
		Preference p = findPreference("acercade");
		p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(Preferences.this, AcercaDe.class);
				startActivity(i);
				return false;
			}
		});

		// elemento del email
		Preference preferenceemail1 = findPreference("email1");
		preferenceemail1
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						Intent i = new Intent(Intent.ACTION_SEND);
						i.setType("message/rfc822");
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "info@eldirectorio.mx" });
						try {
							startActivity(Intent.createChooser(i,
									"Enviar correo desde..."));
						} catch (android.content.ActivityNotFoundException ex) {
							Toast.makeText(
									Preferences.this,
									"No tienes instaladas aplicaciones para enviar emails!",
									Toast.LENGTH_SHORT).show();
						}
						return false;
					}
				});

		// elemento del segundo email
		Preference preferenceemail2 = findPreference("email2");
		preferenceemail2
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						Intent i = new Intent(Intent.ACTION_SEND);
						i.setType("message/rfc822");
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "ventas@eldirectorio.mx" });
						try {
							startActivity(Intent.createChooser(i,
									"Enviar correo desde..."));
						} catch (android.content.ActivityNotFoundException ex) {
							Toast.makeText(
									Preferences.this,
									"No tienes instaladas aplicaciones para enviar emails!",
									Toast.LENGTH_SHORT).show();
						}
						return false;
					}
				});

		// elemento que te lleva a la página de registo
	/*	Preference register = findPreference("registerHere");
		register.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Preferences.this,
						CuponesRegistro.class);
				startActivity(intent);
				return false;
			}
		});*/

		// elemento para iniciar sesión
		/*Preference logUser = findPreference("iniciarCerrarSesion");
		logUser.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(Preferences.this);
				String estado = sp.getString("estado", "Not LoggedIn");

				if (estado.equals("Logged") || estado.equals("LoggedClub")) {
					Editor editor = sp.edit();
					editor.putString("estado", "NotLoggedIn");
					editor.commit();
					Toast.makeText(Preferences.this, "Se ha cerrado sesi�n",
							Toast.LENGTH_LONG).show();
				} else {
					String username = sp.getString("username", null);
					String password = sp.getString("password", null);

					new LoginAsync().execute(username, toMd5(password));
				}

				return false;
			}
		});*/

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Preferences.this, Search.class);
		this.startActivity(intent);

	}

	/**
	 * Método que encripta el password
	 * 
	// * @param pass
	 *            El password a encriptar
	// * @return EL password encriptado.
	 */
	/*private String toMd5(String pass) {
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
	}*/

	@Override
	protected void onResume() {
	//	com.facebook.Settings.publishInstallAsync(getApplicationContext(),
	//			getString(R.string.facebook_app_id));
		super.onResume();
	}

	/**
	 * Async Task que se encarga de hacer el log in.
	 * 
	 * @author NinjaDevelop
	 * 
	 */
	/*private class LoginAsync extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String username = params[0];
			String pass = params[1];
			return loguear(username, pass);

		}

		private String loguear(final String usuario, final String contramd5) {
			String log;
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://m.eldirectorio.mx/loginHandler.axd");
			log = "Not LoggedIn";
			Log.d(TAG, "Not LoggedIn");
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("username", usuario));
				nameValuePairs.add(new BasicNameValuePair("md5", contramd5));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpclient.execute(httppost);
				int comprobacion = response.getStatusLine().getStatusCode();

				if (comprobacion == 200) {
					log = "Logged";
					Log.d(TAG, "Logged");
					// entity response
					HttpEntity entity = response.getEntity();
					log = EntityUtils.toString(entity);

				} else {
					Log.d(TAG, "Not LoggedIn");

				}
			} catch (ClientProtocolException e) {
				Log.e(TAG, e.toString());

			} catch (IOException e) {
				Log.e(TAG, e.toString());

			}

			return log;

		}

		protected void onPostExecute(String result) {
			Toast.makeText(Preferences.this, "Resultado=" + result,
					Toast.LENGTH_LONG).show();
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(Preferences.this);
			Editor session = settings.edit();

			if (result.equals("0")) {
				session.putString("estado", "Logged");
				session.commit();

			} else if (result.equals("1")) {
				session.putString("estado", "LoggedClub");
				session.commit();
				Toast.makeText(Preferences.this, "Loggeado Correctamente!",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(Preferences.this, "Datos Incorrectos",
						Toast.LENGTH_LONG).show();
			}
		}
	}*/

}
