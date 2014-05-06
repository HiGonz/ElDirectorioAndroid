package directorio.applications;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import directorio.services.dao.LogDAO;

/**
 * Custom Aplication creada para guardar y facilitar el acceso a ciertos
 * m�todos. 
 * 
 * NOTA: Esta clase antes era muy usada para todo lo referente a
 * favoritos, pero con las modificaciones respecto al país, esta clase ya no es
 * tan utilizada (lo de los logs podrían hacerse directamente en el LogDao y lo
 * de los países directamente obtenerlos de las preferencias) sin embargo, se
 * decidió dejar este "custom adapter" por si se llega a requerir
 * posteriormente.
 * Attentamente: Carlos Tirado
 * 
 * @author Carlos Tirado
 * @author NinjaDevelopment
 * @author Publysorpresas
 * 
 */
public class TodoManagerApplication extends Application {

	private String selectedCountry;

	/*
	 * El tag está comentado por si se llega a usar posteriormente
	 */
	// private static final String TAG = "ManagerApplication";

	private LogDAO logDao;

	@Override
	public void onCreate() {
		super.onCreate();
		// se obtiene el país seleccionado
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(TodoManagerApplication.this);
		boolean countrySelected = sp.getBoolean("hasSelected", false);
		logDao = new LogDAO(getApplicationContext());
		if (countrySelected == true) {
			selectedCountry = sp.getString("countrySelected", "M�xico");
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	/**
	 * M�todo que obtiene el pa�s que el usuario seleccion�.
	 * 
	 * @return EL país que el usuario seleccioné.
	 */
	public String getCountry() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(TodoManagerApplication.this);
		selectedCountry = sp.getString("countrySelected", "México");
		return selectedCountry;
	}

	/**
	 * Método que nos dice si hay conexión a internet disponible.
	 * 
	 * @return True si hay internet, false si no lo hay.
	 */
	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	/**
	 * Regresa los usuarios loggeados.
	 * 
	 * @return Un ArrayLIst con los usuarios Loggeados.
	 */
	public ArrayList<String> dameLogeado() {

		return logDao.dameLogeado();
	}

	/**
	 * Método que cierra sesión.
	 */
	public void desloguear() {
		logDao.desloguear();
	}

}