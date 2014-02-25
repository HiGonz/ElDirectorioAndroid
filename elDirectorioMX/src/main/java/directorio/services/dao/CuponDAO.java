package directorio.services.dao;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import directorio.objetos.Cupon;
import directorio.tools.JSONParser;

/**
 * Clase que se encarga de descargar información de los cupones del web service.
 *
 * @author NinjaDevelop
 * @author Carlos Tirado
 * @author Publysorpresas
 * 
 */
public class CuponDAO {

	/** URL del web service **/
	private static final String URL = "http://173.193.3.218/couponservice.svc/";

	/** Tags para decifrar el JSON **/
	private static final String HOW_TO_TAG = "HowToCash";
	private static final String NEGOCIO_TAG = "Negocio";
	private static final String ADV_ID_TAG = "advertiserId";
	private static final String CONDITIONS = "conditions";
	private static final String ID_TAG = "couponId";
	private static final String DESCRIPCION = "descripcion";
	private static final String END_TAG = "end";
	private static final String NAME_TAG = "name";
	private static final String PIC_URL_TAG = "picUrl";
	private static final String START_TAG = "start";

	private static final String TAG = "CuponDAO";

	private JSONParser jp;

	/**
	 * Constructor, inicializa la clase JSONParser para su uso a través de la
	 * actividad.
	 */
	public CuponDAO() {
		jp = new JSONParser();
	}

	/**
	 * Método que prepara el string para ser mandado al web service
	 * 
	 * @param input
	 *            EL string a preparar.
	 * @return El string preparado.
	 */
	private String prepareString(String input) {
		String result = "";
		String temporal = "";

		/*
		 * Se reemplaza el símbolo '/' con '()' y el web service se encarga de
		 * hacer la conversión a la inversa.
		 */
		for (int i = 0; i < input.length(); i++) {
			char temp = input.charAt(i);
			if (temp == '/') {
				temporal += "()";
			} else {
				temporal += temp;
			}
		}

		// se reemplazan los espacios por %20
		result = temporal;
		result = result.replace(" ", "%20");

		return result;
	}

	/**
	 * Método que retrae los cupones de cierto advertiser.
	 * 
	 * @param advId
	 *            El id del Advertiser.
	 * @return El array list de cupones del advertiser.
	 */
	public ArrayList<Cupon> cuponesPorAdvertiser(String advId) {

		ArrayList<Cupon> cupones = new ArrayList<Cupon>();

		JSONArray ja = jp.getJSONFromUrl(URL + "cuponesPorAdvertiser/" + advId);
		try {
			for (int i = 0; i < ja.length(); i++) {

				JSONObject jo = ja.getJSONObject(i);
				Cupon c = new Cupon();
				c.setHowToCash(jo.getString(HOW_TO_TAG));
				c.setNegocio(jo.getString(NEGOCIO_TAG));
				c.setAdvertiserId(jo.getString(ADV_ID_TAG));
				c.setConditions(jo.getString(CONDITIONS));
				c.setCuponId(jo.getInt(ID_TAG));
				c.setDescripcion(jo.getString(DESCRIPCION));
				c.setEnd(jo.getString(END_TAG));
				c.setName(jo.getString(NAME_TAG));
				c.setPicUrl(jo.getString(PIC_URL_TAG));
				c.setStart(jo.getString(START_TAG));
				cupones.add(c);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
		return cupones;
	}

	/**
	 * Método que descarga los cupones del club del advertiser
	 * 
	 * @param advId
	 *            EL id del advertiser
	 * @return Una lista de cupones del advertiser.
	 */
	public ArrayList<Cupon> cuponesPorAdvertiserClub(String advId) {
		ArrayList<Cupon> cupones = new ArrayList<Cupon>();

		JSONArray ja = jp.getJSONFromUrl(URL + "cuponesPorAdvertiserClub/"
				+ advId);
		try {
			for (int i = 0; i < ja.length(); i++) {

				JSONObject jo = ja.getJSONObject(i);
				Cupon c = new Cupon();
				c.setHowToCash(jo.getString(HOW_TO_TAG));
				c.setNegocio(jo.getString(NEGOCIO_TAG));
				c.setAdvertiserId(jo.getString(ADV_ID_TAG));
				c.setConditions(jo.getString(CONDITIONS));
				c.setCuponId(jo.getInt(ID_TAG));
				c.setDescripcion(jo.getString(DESCRIPCION));
				c.setEnd(jo.getString(END_TAG));
				c.setName(jo.getString(NAME_TAG));
				c.setPicUrl(jo.getString(PIC_URL_TAG));
				c.setStart(jo.getString(START_TAG));
				cupones.add(c);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
		return cupones;
	}

	/**
	 * Método que regresa los cupones de cada categoría.
	 * 
	 * @param categoryName
	 *            El nombre de la categoría.
	 * @return Un Array List con los cupones.
	 */
	public ArrayList<Cupon> cuponesPorCategorias(String categoryName) {

		ArrayList<Cupon> cupones = new ArrayList<Cupon>();

		// se prepara el string
		String name = prepareString(categoryName);

		JSONArray ja = jp.getJSONFromUrl(URL + "cuponesPorCategorias/" + name);
		try {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				Cupon c = new Cupon();
				c.setHowToCash(jo.getString(HOW_TO_TAG));
				c.setNegocio(jo.getString(NEGOCIO_TAG));
				c.setAdvertiserId(jo.getString(ADV_ID_TAG));
				c.setConditions(jo.getString(CONDITIONS));
				c.setCuponId(jo.getInt(ID_TAG));
				c.setDescripcion(jo.getString(DESCRIPCION));
				c.setEnd(jo.getString(END_TAG));
				c.setName(jo.getString(NAME_TAG));
				c.setPicUrl(jo.getString(PIC_URL_TAG));
				c.setStart(jo.getString(START_TAG));
				cupones.add(c);

			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		return cupones;
	}

	/**
	 * Método que regresa los cupones del club de cada categoria.
	 * 
	 * @param categoryName
	 *            El nombre de la categoría.
	 * @return Un array list con los cupones.
	 */
	public ArrayList<Cupon> cuponesClubPorCategorias(String categoryName) {

		ArrayList<Cupon> cupones = new ArrayList<Cupon>();

		// se prepara el string
		String name = prepareString(categoryName);

		String urlToSend = URL + "cuponesClubPorCategorias/" + name;
		JSONArray ja = jp.getJSONFromUrl(urlToSend);
		try {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				Cupon c = new Cupon();
				c.setHowToCash(jo.getString(HOW_TO_TAG));
				c.setNegocio(jo.getString(NEGOCIO_TAG));
				c.setAdvertiserId(jo.getString(ADV_ID_TAG));
				c.setConditions(jo.getString(CONDITIONS));
				c.setCuponId(jo.getInt(ID_TAG));
				c.setDescripcion(jo.getString(DESCRIPCION));
				c.setEnd(jo.getString(END_TAG));
				c.setName(jo.getString(NAME_TAG));
				c.setPicUrl(jo.getString(PIC_URL_TAG));
				c.setStart(jo.getString(START_TAG));
				cupones.add(c);

			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		return cupones;
	}

	/**
	 * Regresa los cupones de cierta categoría en cierta ciudad.
	 * 
	 * @param categoryName
	 *            El nombre de la categoría a buscar.
	 * @param cityName
	 *            El nombre de la ciudad.
	 * @return Array list con los cupones de cierta categoría en cierta ciudad.
	 */
	public ArrayList<Cupon> cuponesPorCategoriasCity(String categoryName,
			String cityName) {

		ArrayList<Cupon> cupones = new ArrayList<Cupon>();
		String name = prepareString(categoryName);
		cityName = prepareString(cityName);

		JSONArray ja = jp.getJSONFromUrl(URL + "cuponesPorCategoriasCity/"
				+ name + "/" + cityName);

		try {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				Cupon c = new Cupon();
				c.setHowToCash(jo.getString(HOW_TO_TAG));
				c.setNegocio(jo.getString(NEGOCIO_TAG));
				c.setAdvertiserId(jo.getString(ADV_ID_TAG));
				c.setConditions(jo.getString(CONDITIONS));
				c.setCuponId(jo.getInt(ID_TAG));
				c.setDescripcion(jo.getString(DESCRIPCION));
				c.setEnd(jo.getString(END_TAG));
				c.setName(jo.getString(NAME_TAG));
				c.setPicUrl(jo.getString(PIC_URL_TAG));
				c.setStart(jo.getString(START_TAG));
				cupones.add(c);

			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return cupones;

	}

	/**
	 * Método que descarga los cupones del club de cierta categoría en cierta
	 * ciudad.
	 * 
	 * @param categoryName
	 *            El nombre de la categoría.
	 * @param cityName
	 *            El nombre de la ciudad.
	 * @return El array list de los cupones.
	 */
	public ArrayList<Cupon> cuponesClubPorCategoriasCity(String categoryName,
			String cityName) {
		ArrayList<Cupon> cupones = new ArrayList<Cupon>();
		String name = prepareString(categoryName);
		cityName = prepareString(cityName);

		JSONArray ja = jp.getJSONFromUrl(URL + "cuponesClubPorCategoriasCity/"
				+ name + "/" + cityName);
		try {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				Cupon c = new Cupon();
				c.setHowToCash(jo.getString(HOW_TO_TAG));
				c.setNegocio(jo.getString(NEGOCIO_TAG));
				c.setAdvertiserId(jo.getString(ADV_ID_TAG));
				c.setConditions(jo.getString(CONDITIONS));
				c.setCuponId(jo.getInt(ID_TAG));
				c.setDescripcion(jo.getString(DESCRIPCION));
				c.setEnd(jo.getString(END_TAG));
				c.setName(jo.getString(NAME_TAG));
				c.setPicUrl(jo.getString(PIC_URL_TAG));
				c.setStart(jo.getString(START_TAG));
				cupones.add(c);

			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		return cupones;

	}

}
