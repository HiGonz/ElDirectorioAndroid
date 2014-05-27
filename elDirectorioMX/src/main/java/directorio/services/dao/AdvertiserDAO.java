package directorio.services.dao;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

import directorio.objetos.Advertiser;
import directorio.tools.JSONParser;

/**
 * Clase que intenta accesar a los Advertisers que va a mostrar.
 * 
 * @author Carlos Tirado
 * @author Juan Carlos Hinojo
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */
public class AdvertiserDAO {

	/** URL del web service de advertisers **/
	private static final String URL = "http://173.193.3.218/AdvertiserService.svc/";

	/** URL del web service de las galerías **/
	private static final String URL2 = "http://173.193.3.218/GalleryService.svc/";

	// TAGS para decifrar JSON
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
	private static final String TAG_YOUTUBE = "Youtube";
	private static final String TAG_PROMOCION = "promocion";

	private static final String TAG = "AdvertiserDAO";

	/**
	 * Constructor vacío
	 */
	public AdvertiserDAO() {

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
		// Se reemplaza el símbolo '/' por un () y el web service lo
		// identificaría como un /, se decidi usar este método y no otra manera
		// porque el web service marcaba error.
		String temporal = "";
		for (int i = 0; i < input.length(); i++) {
			char temp = input.charAt(i);
			if (temp == '/') {
				temporal += "()";
			} else {
				temporal += temp;
			}
		}
		result = temporal;

		// se quitan espacios
		result = result.replace(" ", "%20");

		return result;
	}

	/**
	 * Método que regresa una lista de Advertisers que sean parte una categoría,
	 * y que sean del país dado.
	 * 
	 * @param category
	 *            La categoría de la que se pretende regresar la lista.
	 * @param pais
	 *            El país en el que el usuario esté.
	 * @return La lista de Advertisers.
	 */
	public ArrayList<Advertiser> getByCategory(String category, String pais) {
		// se prepara el string para mandarlo al web service
		category = prepareString(category);

		ArrayList<Advertiser> resultados = new ArrayList<Advertiser>();
		JSONParser jp = new JSONParser();

		// se manda llamar al método del web service
		JSONArray jArray = jp.getJSONFromUrl(URL + "searchByCategory/"
				+ category + "/" + pais);
		int jl = jArray.length();

		// se guardan los valores en la lista
		try {
			for (int i = 0; i < jl; i++) {
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
				a.setDescripcion(o.getString(TAG_DESC));

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
				a.setYoutubVideo(o.getString(TAG_YOUTUBE));
				a.setPromocion(o.getString(TAG_PROMOCION));

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

	/**
	 * Métdo que regresa un ArrayList de Advertisers, usando como parámetros de
	 * búsqueda la categoría y la ciudad.
	 * 
	 * @param category
	 *            La categoría.
	 * @param cityName
	 *            La ciudad donde se está buscando.
	 * @return ArrayList de los Negocios.
	 */
	public ArrayList<Advertiser> getByCategoryCity(String category,
			String cityName) {
		// se preparan los parámetros a mandar al web service
		category = prepareString(category);
		cityName = prepareString(cityName);

		ArrayList<Advertiser> resultados = new ArrayList<Advertiser>();
		JSONParser jp = new JSONParser();

		// se guarda en un JSON Array el resultado de la búsqueda
		JSONArray jArray = jp.getJSONFromUrl(URL + "getByCategoryCity/"
				+ category + "/" + cityName);
		// se guardan los resultados
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
				a.setDescripcion(o.getString(TAG_DESC));

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
				a.setYoutubVideo(o.getString(TAG_YOUTUBE));
				a.setPromocion(o.getString(TAG_PROMOCION));

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

	/**
	 * Método que busca un Advertiser en el web service. Este método usa el
	 * thread principal para hacer la búsqueda.
	 * 
	 * @param nombre
	 *            El nombre del advertiser a buscar.
	 * @param pais
	 *            El pa�s donde se está buscando.
	 * @return El resultado de la búsqueda.
	 */
	public Advertiser find(String nombre, String pais) {

		Advertiser resultado = new Advertiser();
		JSONParser jp = new JSONParser();

		// se prepara el string a mandar
		String temporal = prepareString(nombre);

		// se guarda el resultado en un JSONArray
		JSONArray jArray = jp.getJSONFromUrl(URL + "find/" + temporal + "/"
				+ pais);

		try {
			Advertiser a = new Advertiser();
			JSONObject o = jArray.getJSONObject(0);
			a.setDireccion(o.getString(TAG_ADDRESS));
			a.setId(o.getString(TAG_ADVERTISER_ID));
			a.setCiudad(o.getString(TAG_CITY));
			String[] cats = new String[o.getJSONArray(TAG_CATS).length()];
			for (int j = 0; j < cats.length; j++) {
				cats[j] = o.getJSONArray(TAG_CATS).get(j).toString();
			}
			a.setCategorias(cats);
			a.setContacto(o.getString(TAG_CONTACT));
			a.setDescripcion(o.getString(TAG_DESC));

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
			a.setYoutubVideo(o.getString(TAG_YOUTUBE));
			a.setPromocion(o.getString(TAG_PROMOCION));

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
			resultado = a;

		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}

		return resultado;
	}

	/**
	 * Segundo método para buscar en el web service un advertiser, este método
	 * usa un AsyncTask para realizar las operaciones de búsqueda.
	 * 
	 * @param nombre
	 *            El nombre del advertiser a buscar.
	 * @param pais
	 *            El país que el usuaario seleccioné.
	 * @return El resultado de la búsqueda.
	 */
	public Advertiser findAsync(String nombre, String pais) {

		Advertiser resultado = new Advertiser();
		JSONParser jp = new JSONParser();

		// se preparael string a mandar por we service
		String temporal = prepareString(nombre);

		// se guardan los resultado en un JSON array.
		JSONArray jArray = jp.getJSONFromUrlAsync(URL + "find/" + temporal
				+ "/" + pais);

		// se guarda el advertiser en un objeto
		try {
			Advertiser a = new Advertiser();
			JSONObject o = jArray.getJSONObject(0);
			a.setDireccion(o.getString(TAG_ADDRESS));
			a.setId(o.getString(TAG_ADVERTISER_ID));
			a.setCiudad(o.getString(TAG_CITY));
			String[] cats = new String[o.getJSONArray(TAG_CATS).length()];
			for (int j = 0; j < cats.length; j++) {
				cats[j] = o.getJSONArray(TAG_CATS).get(j).toString();
			}
			a.setCategorias(cats);
			a.setContacto(o.getString(TAG_CONTACT));
			a.setDescripcion(o.getString(TAG_DESC));

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
			a.setYoutubVideo(o.getString(TAG_YOUTUBE));
			a.setPromocion(o.getString(TAG_PROMOCION));

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
			resultado = a;

		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		} catch (NullPointerException npe) {
			Log.e(TAG, npe.toString());
		}

		return resultado;
	}

	/**
	 * Método que busca si un Advertiser tiene galería.
	 * 
	 * @param id
	 *            El id del advertiser.
	 * @return EL valor booleano de si se tiene galería o no.
	 */
	public boolean hasGallery(String id) {
		boolean resultado = false;
		JSONParser jp = new JSONParser();
		int valor = jp.getIntegerBoolean(URL2 + "hasGallery/" + id);
		if (valor == 1) {
			resultado = true;
		}
		return resultado;
	}

	/**
	 * Método que regresa el nombre de la galería del Advertiser.
	 * 
	 * @param id
	 *            EL Id del advertiser que se va a usar.
	 * @return El nombre de la galería.
	 */
	public String getGalleryName(String id) {
		String resultado = "";
		JSONParser jp = new JSONParser();
		resultado = jp.getString(URL2 + "getGalleryName/" + id);
		return resultado;
	}

	/**
	 * Método que regresa el GaleryId del advertiser.
	 * 
	 * @param id
	 *            El Id del Advertiser.
	 * @return El Id de la galería.
	 */
	public String getGalleryId(String id) {
		String resultado = "";
		JSONParser jp = new JSONParser();
		int integerId = jp.getIntegerBoolean(URL2 + "getGalleryId/" + id);
		resultado = integerId + "";
		return resultado;
	}

	/**
	 * Método que se encarga de descargar los urls de las imágenes en el main
	 * thread.
	 * 
	 * @param galleryId
	 *            EL id de la galería.
	 * @return Una lista con los URL's de las imágenes de la galería.
	 */
	public ArrayList<String> getUrls(String galleryId) {

		ArrayList<String> resultados = new ArrayList<String>();
		JSONParser jp = new JSONParser();
		JSONArray jArray = jp.getJSONFromUrl(URL2 + "getUrls/" + galleryId);
		try {
			for (int i = 0; i < jArray.length(); i++) {
				resultados.add(jArray.getString(i));
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return resultados;

	}

	/**
	 * Método que regresa los iconos de las imágenes.
	 * 
	 * @param galleryId
	 *            El id de la galería a usar.
	 * @return EL string con el valor de los iconos.
	 */
	public ArrayList<String> getThumbs(String galleryId) {

		ArrayList<String> resultados = new ArrayList<String>();
		JSONParser jp = new JSONParser();
		JSONArray jArray = jp.getJSONFromUrl(URL2 + "getThumbs/" + galleryId);
		try {
			for (int i = 0; i < jArray.length(); i++) {
				resultados.add(jArray.getString(i));
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return resultados;

	}

	/**
	 * Regresa el número de cupones que tiene el Advertiser.
	 * 
	 * @param advId
	 *            El id del advertiser a usar.
	 * @return El número de cupones que tiene dicho advertiser.
	 */
	public int advertiserCoupons(String advId) {
		int resultado = 0;
		JSONParser jp = new JSONParser();
		resultado = jp.getIntegerBoolean(URL + "advertiserCoupons/" + advId);
		return resultado;
	}

	/**
	 * Regresa el número de cupones que tiene el Advertiser.
	 * 
	 * @param advId
	 *            El id del advertiser a usar.
	 * @return El número de cupones que tiene dicho advertiser.
	 */
	public int advertiserCouponsClub(String advId) {
		int resultado = 0;
		JSONParser jp = new JSONParser();
		resultado = jp
				.getIntegerBoolean(URL + "advertiserCouponsClub/" + advId);
		return resultado;
	}

}