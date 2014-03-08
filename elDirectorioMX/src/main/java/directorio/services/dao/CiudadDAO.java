package directorio.services.dao;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import directorio.tools.JSONParser;

/**
 * Clase que se encarga de retraer la información de las ciudades del Web
 * Service.
 * 
 * @author Publysorpresas
 * 
 */
public class CiudadDAO {

	/** URL del web service **/
	private static String URL = "http://173.193.3.218/EntidadService.svc/";

	// tag para el log cat
	private static final String TAG = "CiudadDAO";

	/**
	 * Constructor vac�o.
	 */
	public CiudadDAO() {
	}

	/**
	 * Método que descarga las ciudades del web service, todo lo hace en el main
	 * thread.
	 * 
	 * @param pais
	 *            El pa�s de donde se bajan las ciudades.
	 * @return Una lista con los nombres de las ciudades.
	 */
	public ArrayList<String> getCiudades(String pais) {

		ArrayList<String> ciudades = new ArrayList<String>();
		JSONParser jp = new JSONParser();
		String ad = URL + "getEntidades/"+ pais;
		JSONArray jArray = jp.getJSONFromUrl(ad);
		for (int i = 0; i < jArray.length(); i++) {
			try {
				
				ciudades.add(jArray.getString(i));
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
		}
		return ciudades;
	}

	/**
	 * Método que regresa las ciudades con cupones de un país
	 * 
	 * @param pais
	 *            El país seleccionado
	 * @return La lista con los nombres de las ciudades
	 */
	public ArrayList<String> getCiudadesConCupones(String pais) {

		ArrayList<String> ciudades = new ArrayList<String>();
		JSONParser jp = new JSONParser();
		JSONArray jArray = jp.getJSONFromUrl(URL + "getEntidadesConCupones/"
				+ pais);
		for (int i = 0; i < jArray.length(); i++) {
			try {
				ciudades.add(jArray.getString(i));
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
		}
		return ciudades;
	}

	/**
	 * Método que regresa los nombres de las ciudades de cierto país, que tienen
	 * cupones del club.
	 * 
	 * @param pais
	 *            El país seleccionado
	 * @return La lista con los nombres de las ciudades
	 */
	public ArrayList<String> getCiudadesConCuponesClub(String pais) {
		ArrayList<String> ciudades = new ArrayList<String>();
		JSONParser jp = new JSONParser();
		JSONArray jArray = jp.getJSONFromUrl(URL
				+ "getEntidadesConCuponesClub/" + pais);
		for (int i = 0; i < jArray.length(); i++) {
			try {
				ciudades.add(jArray.getString(i));
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
		}
		return ciudades;
	}

	/**
	 * Método que descarga los nombres de las ciudades que se encuentran en la
	 * zona especificada.
	 * 
	 * @param zona
	 *            La zona de la cual se quieren descargar las imágenes.
	 * @return Una lista de nombres de las ciudades que están dentro de esa
	 *         zona.
	 */
//	public ArrayList<String> getCiudadesZona(String zona) {
//		zona = zona.replace(" ", "%20");
//		ArrayList<String> ciudades = new ArrayList<String>();
//		JSONParser jp = new JSONParser();
//		JSONArray jArray = jp.getJSONFromUrl(URL + "getCiudadesZona/" + zona);
//		for (int i = 0; i < jArray.length(); i++) {
//			try {
//				ciudades.add(jArray.getString(i));
//			} catch (JSONException e) {
//				Log.e(TAG, e.toString());
//				e.printStackTrace();
//			}
//		}
//		return ciudades;
//	}

	/**
	 * Método que descarga en el main thread las ciudades que tienen categorías
	 * disponibles.
	 * 
	 * @param pais
	 *            El país de donde son las ciudades.
	 * @return Una lista con los nombres de las Ciudades.
	 */
	public ArrayList<String> getCiudadesConCategorias(String pais) {
		ArrayList<String> ciudades = new ArrayList<String>();
		JSONParser jp = new JSONParser();
		JSONArray jArray = jp.getJSONFromUrl(URL + "getEntidadesConCategorias/"
				+ pais);
		for (int i = 0; i < jArray.length(); i++) {
			try {
				ciudades.add(jArray.getString(i));
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
		}
		return ciudades;
	}

}