package directorio.others;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

import directorio.objetos.Advertiser;
import directorio.tools.JSONParser;

/**
 * Clase que se encarga de manejar los algoritmos de búsqueda de negocios.
 * 
 * @author NinjaDevelop
 * @author Juan Carlos Hinojo
 * @author Carlos Tirado
 * @author Publysorpresas
 * 
 */
@SuppressLint({ "ParserError", "ParserError" })
public class SearchManager {

	/** URL del web service **/
	private static String URL = "http://173.193.3.218/AdvertiserService.svc/";

	// tags del json
	private static final String TAG_ADDRESS = "Address";
	private static final String TAG_ADVERTISER_ID = "AdvertiserId";
	private static final String TAG_CITY = "City";
	private static final String TAG_CATS = "Categorias";
	private static final String TAG_CONTACT = "Contact";
	private static final String TAG_DESC = "Description";
	private static final String TAG_EMAIL = "Email";
	private static final String TAG_FACEBOOK = "Facebook";
	private static final String TAG_FEAT = "Featured";
	private static final String TAG_IMG_URL = "url";
	private static final String TAG_NAME = "Name";
	private static final String TAG_POSX = "MapReferenceX";
	private static final String TAG_POSY = "MapReferenceY";
	private static final String TAG_WEB = "WebPage";
	private static final String TAG_TAGS = "Tags";
	private static final String TAG_PHONE = "Phones";
	private static final String TAG_TWITTER = "Twitter";

	private static final String TAG = "SearchManager";

	/**
	 * Constructor vacío
	 */
	public SearchManager() {
	}

	/**
	 * Algoritmo que calcula la distancia entre dos objetos.
	 * 
	 * @param lat1
	 *            latitud de objeto 1
	 * @param lon1
	 *            longitud de objeto 1
	 * @param lat2
	 *            latitud de objeto 2
	 * @param lon2
	 *            longitud de objeto 2
	 * @return la distancia entre los dos objetos.
	 */
	public static Double calculateDistance(Double lat1, Double lon1,
			Double lat2, Double lon2) {

		double nRadius = 6371; // Earth's radius in Kilometers
		// Get the difference between our two points
		// then convert the difference into radians

		double nDLat = Math.toRadians(lat2 - lat1);
		double nDLon = Math.toRadians(lon2 - lon1);

		// Here is the new line
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		double nA = Math.pow(Math.sin(nDLat / 2), 2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.pow(Math.sin(nDLon / 2), 2);

		double nC = 2 * Math.atan2(Math.sqrt(nA), Math.sqrt(1 - nA));
		double nD = nRadius * nC;

		return nD; // Return our calculated distance
	}

	/**
	 * Método que regresa negocios dentro del rango a buscar.
	 * 
	 * @param latitude
	 *            Latitud del usuario
	 * @param longitude
	 *            Longitud del usuario
	 * @param rangoBuscar
	 *            El rango a buscar de negocios
	 * @param ciudad
	 *            La ciudad donde te encuentras
	 * @param filtroString
	 *            El parámetro de búsqueda de negocios
	 * @return Los negocios dentro de ese rango.
	 */
	public static ArrayList<Advertiser> negociosenRango(double latitude,
			double longitude, double rangoBuscar, String ciudad,
			String filtroString, String pais) {

		ArrayList<Advertiser> negociosPorNombre = new ArrayList<Advertiser>();

		negociosPorNombre = returnAll(filtroString, pais);

		// Si el rango es 0, traer TODOS los negocios en la ciudad especificada.
		if (rangoBuscar == 0) {
			ArrayList<Advertiser> negociosEnRango = new ArrayList<Advertiser>();

			for (int i = 0; i < negociosPorNombre.size(); i++) {
				String ciudadcomparar = negociosPorNombre.get(i).getCiudad();

				if (ciudad.equals(ciudadcomparar)) {
					negociosEnRango.add(negociosPorNombre.get(i));
				}

			}
			return negociosEnRango;
			// En caso de que no
		} else {
			ArrayList<Advertiser> negociosEnRango = new ArrayList<Advertiser>();
			for (int i = 0; i < negociosPorNombre.size(); i++) {
				// Se calcula si el negocio esta en el rango deseado
				double kilometrosDistancia = calculateDistance(latitude,
						longitude, negociosPorNombre.get(i).getPosx(),
						negociosPorNombre.get(i).getPosy());
				// Si si esta en el rango, agregar a lista
				if (kilometrosDistancia < rangoBuscar) {
					negociosEnRango.add(negociosPorNombre.get(i));
				}
			}
			return negociosEnRango;
		}
	}

	public static ArrayList<Advertiser> negociosCiudad(String ciudad){
		ArrayList<Advertiser> result = new ArrayList<Advertiser>();
		JSONParser jp = new JSONParser();
		JSONArray jArray = jp.getJSONFromUrl(URL + "searchByCity/" + ciudad);
		try {
			for (int i = 0; i < jArray.length(); i++) {
				Advertiser a = new Advertiser();
				JSONObject o = jArray.getJSONObject(i);
				a.setDireccion(o.getString(TAG_ADDRESS));
				a.setId(o.getString(TAG_ADVERTISER_ID));
				a.setCiudad(o.getString(TAG_CITY));
				String[] cats = new String[o.getJSONArray(TAG_CATS).length()];
				for (int j = 0; j < cats.length; j++) {
					cats[j] = o.getJSONArray(TAG_CATS).get(j).toString();
				}
				a.setCategorias(cats);
				a.setContacto(o.getString(TAG_CONTACT));
				a.setDescripcion(TAG_DESC);

				JSONArray jat = o.getJSONArray(TAG_EMAIL);
				for (int j = 0; j < jat.length(); j++) {
					a.setEmail2(jat.getString(j));
				}

				a.setFacebook(o.getString(TAG_FACEBOOK));
				a.setFeatured(o.getBoolean(TAG_FEAT));
				a.setImgSrc(o.getString(TAG_IMG_URL));
				a.setNombre(o.getString(TAG_NAME));
				a.setPosx(o.getDouble(TAG_POSX));
				a.setPosy(o.getDouble(TAG_POSY));
				a.setSitioWeb(o.getString(TAG_WEB));

				String tags = o.getString(TAG_TAGS);
				StringTokenizer st = new StringTokenizer(tags, ",");
				String[] tagsArray = new String[st.countTokens()];
				for (int j = 0; j < tagsArray.length; j++) {
					tagsArray[j] = st.nextToken();
				}
				a.setTags(tagsArray);

				JSONArray arrayPhones = o.getJSONArray(TAG_PHONE);
				for (int j = 0; j < arrayPhones.length(); j++) {
					a.setTelefono2(arrayPhones.getString(j));
				}

				a.setTwitter(o.getString(TAG_TWITTER));
				result.add(a);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Método que descarga los advertisers que concuerden con el filtro.
	 * 
	 * @param filtro
	 *            El filtro de búsqueda, que viene siendo el nombre.
	 * @param pais
	 *            El país de donde se realiza la búsqueda
	 * @return La lista de Advertisers resultantes
	 */
	public static ArrayList<Advertiser> returnAll(String filtro, String pais) {
		// Se quitan los espacios
		String spaceString = " ";
		char spaceCharacter = spaceString.charAt(0);
		int last = filtro.length() - 1;
		if (last == -1) {
			filtro = "";
		} else if (filtro.charAt(last) == spaceCharacter) {
			filtro = filtro.substring(0, filtro.length() - 1);
		}

		filtro = filtro.replace(" ", "%20");

		ArrayList<Advertiser> resultados = new ArrayList<Advertiser>();
		JSONParser jp = new JSONParser();
		// trae el JSON
		JSONArray jArray = jp.getJSONFromUrl(URL + "searchAll/" + filtro + "/"
				+ pais);
		// Se guardan los valores
		try {
			for (int i = 0; i < jArray.length(); i++) {
				Advertiser a = new Advertiser();
				JSONObject o = jArray.getJSONObject(i);
				a.setDireccion(o.getString(TAG_ADDRESS));
				a.setId(o.getString(TAG_ADVERTISER_ID));
				a.setCiudad(o.getString(TAG_CITY));
				String[] cats = new String[o.getJSONArray(TAG_CATS).length()];
				for (int j = 0; j < cats.length; j++) {
					cats[j] = o.getJSONArray(TAG_CATS).get(j).toString();
				}
				a.setCategorias(cats);
				a.setContacto(o.getString(TAG_CONTACT));
				a.setDescripcion(TAG_DESC);

				JSONArray jat = o.getJSONArray(TAG_EMAIL);
				for (int j = 0; j < jat.length(); j++) {
					a.setEmail2(jat.getString(j));
				}

				a.setFacebook(o.getString(TAG_FACEBOOK));
				a.setFeatured(o.getBoolean(TAG_FEAT));
				a.setImgSrc(o.getString(TAG_IMG_URL));
				a.setNombre(o.getString(TAG_NAME));
				a.setPosx(o.getDouble(TAG_POSX));
				a.setPosy(o.getDouble(TAG_POSY));
				a.setSitioWeb(o.getString(TAG_WEB));

				String tags = o.getString(TAG_TAGS);
				StringTokenizer st = new StringTokenizer(tags, ",");
				String[] tagsArray = new String[st.countTokens()];
				for (int j = 0; j < tagsArray.length; j++) {
					tagsArray[j] = st.nextToken();
				}
				a.setTags(tagsArray);

				JSONArray arrayPhones = o.getJSONArray(TAG_PHONE);
				for (int j = 0; j < arrayPhones.length(); j++) {
					a.setTelefono2(arrayPhones.getString(j));
				}

				a.setTwitter(o.getString(TAG_TWITTER));
				resultados.add(a);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
		return resultados;
	}
}
