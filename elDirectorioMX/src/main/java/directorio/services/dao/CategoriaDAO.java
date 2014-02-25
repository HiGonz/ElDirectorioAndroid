package directorio.services.dao;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import directorio.objetos.Categoria;
import directorio.tools.JSONParser;

/**
 * Clase que hace el acceso a los servicios web referentes de las categorías.
 * 
 * @author Carlos Tirado
 * @author Juan Carlos Hinojo
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */
public class CategoriaDAO {

	// ArrayList donde se guardarón los resultados
	private ArrayList<String> categorias;
	private ArrayList<Categoria> cats;

	/** URL del servicio **/
	private static final String URL = "http://173.193.3.218/CategoryService.svc";

	/** TAGS que se usan para decifrar los elementos JSON **/
	private static final String ID = "CategoryId";
	private static final String NAME = "Name";
	private static final String LETRA = "Letter";
	private static final String TAG = "CategoriaDAO";

	/**
	 * Constructor vacío
	 */
	public CategoriaDAO() {
	}

	/**
	 * Método que prepara el string para ser mandado a los web services.
	 * 
	 * @param input
	 *            El string a ser preparado
	 * @return El string preparado
	 */
	public String prepareString(String input) {
		// se eliminan espacios
		String result = input.replace(" ", "%20");
		return result;
	}

	/**
	 * Método que carga las categorías y las guarda en la variable cats.
	 */
	private void cargaCategoriasAsync(String pais) {
		// Se vuelven a vaciar los arraList en caso de que ya tuviera valores
		cats = new ArrayList<Categoria>();

		// Se hacen la descargas
		JSONParser jp = new JSONParser();
		JSONArray ja = jp.getJSONFromUrlAsync(URL + "/getCategorias/" + pais);
		try {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				int id = jo.getInt(ID);
				String nombre = jo.getString(NAME);
				char letra = jo.get(LETRA).toString().charAt(0);
				Categoria c = new Categoria(id, nombre, letra);
				cats.add(c);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
	}

	private void cargaCategorias(String pais) {
		// Se vuelven a vaciar los arraList en caso de que ya tuviera valores
		cats = new ArrayList<Categoria>();

		// Se hacen la descargas
		JSONParser jp = new JSONParser();
		JSONArray ja = jp.getJSONFromUrl(URL + "/getCategorias/" + pais);
		try {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				int id = jo.getInt(ID);
				String nombre = jo.getString(NAME);
				char letra = jo.get(LETRA).toString().charAt(0);
				Categoria c = new Categoria(id, nombre, letra);
				cats.add(c);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
	}

	/**
	 * Método que descarga las categorías principales en el Main Thread.
	 * 
	 * @param pais
	 *            El país que seleccioné el usuario.
	 * @return Una lista con los nombres de las categorías principales
	 */
	public ArrayList<String> getFeatured(String pais) {
		ArrayList<String> resultado = new ArrayList<String>();
		JSONParser jp = new JSONParser();
		JSONArray jArray = jp.getJSONFromUrl(URL + "/getCategoriasFeatured/"
				+ pais);
		for (int i = 0; i < jArray.length(); i++) {
			try {
				resultado.add(jArray.getString(i));
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
		}
		return resultado;
	}

	/**
	 * Método que regresa el ArrayList con los nombres de las categorías de un
	 * país.
	 * 
	 * @param pais
	 *            El país seleccionado
	 * @return La lista con los nombres de las categorías.
	 */
	public ArrayList<String> getCategorias(String pais, boolean async) {
		// se vacía el array list de strings.
		categorias = new ArrayList<String>();
		// Se cargan las categorías.
		if (async == true) {
			cargaCategoriasAsync(pais);
		} else {
			cargaCategorias(pais);
		}
		// Se guardan solamente los nombres en el arraylist
		for (int i = 0; i < cats.size(); i++) {
			categorias.add(cats.get(i).getNombre());
		}
		return categorias;
	}

	/**
	 * Método que descarga del web service las categorías usando como parámetro
	 * la ciudad.
	 * 
	 * @param cityName
	 *            La ciudad de la cual se quieren las categorías.
	 */
	private void cargaCategoriasCiudad(String cityName) {
		// Los arraylist se vacían
		cats = new ArrayList<Categoria>();

		JSONParser jp = new JSONParser();
		// Se reemplazan los espacios
		cityName = prepareString(cityName);
		String url = URL + "/getCategoriasCiudad/" + cityName;
		JSONArray ja = jp.getJSONFromUrlAsync(url);
		try {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				int id = jo.getInt(ID);
				String nombre = jo.getString(NAME);
				char letra = jo.get(LETRA).toString().charAt(0);
				Categoria c = new Categoria(id, nombre, letra);
				cats.add(c);
			}

		} catch (JSONException je) {
			Log.e(TAG, je.toString());
		}

	}

	/**
	 * Método que regresa los nombres de las categorías por ciudad.
	 * 
	 * @param cityName
	 *            La ciudad que se busca.
	 * @return Regresa un ArrayList de las Categorias de cada ciudad.
	 */
	public ArrayList<String> getCategoriasCiudad(String cityName) {
		cityName = prepareString(cityName);
		try {
			cargaCategoriasCiudad(cityName);
			categorias = new ArrayList<String>();
			for (int i = 0; i < cats.size(); i++) {
				categorias.add(cats.get(i).getNombre());
			}
			return categorias;
		} catch (Exception ex) {
			Log.e(TAG, ex.toString());
			return null;
		}
	}

	/**
	 * Método que regresa las categorías de un país que tienen cupones.
	 * 
	 * @param pais
	 *            El pa�s seleccionado
	 * @return La lista con los nombres de las categorías.
	 */
	public ArrayList<String> getCategoriasConCupones(String pais) {
		ArrayList<String> resultado = new ArrayList<String>();
		JSONParser jp = new JSONParser();
		JSONArray jArray = jp.getJSONFromUrl(URL + "/getCategoriasConCupones/"
				+ pais);
		for (int i = 0; i < jArray.length(); i++) {
			try {
				resultado.add(jArray.getString(i));
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
		}
		return resultado;
	}

	/**
	 * Regresa las categorías que tienen cupones, dependiendo de la ciudad.
	 * 
	 * @param cityName
	 *            La ciudad que se está buscando.
	 * @return Regresa un ArrayList con las categorias que tiene cupones.
	 */
	public ArrayList<String> getCategoriasConCuponesCiudad(String cityName) {
		cityName = prepareString(cityName);
		ArrayList<String> resultado = new ArrayList<String>();
		JSONParser jp = new JSONParser();
		JSONArray jArray = jp.getJSONFromUrlAsync(URL
				+ "/getCategoriasConCuponesCiudad/" + cityName);
		for (int i = 0; i < jArray.length(); i++) {
			try {
				resultado.add(jArray.getString(i));
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
		}
		return resultado;
	}

	/**
	 * Método que descarga las categorías de un país que tienen cupones del
	 * club.
	 * 
	 * @param pais
	 *            El país seleccionado.
	 * @return Una lista con los nombres de las categorías.
	 */
	public ArrayList<String> getCategoriasConCuponesClub(String pais) {
		ArrayList<String> resultado = new ArrayList<String>();
		JSONParser jp = new JSONParser();
		JSONArray jArray = jp.getJSONFromUrl(URL
				+ "/getCategoriasConCuponesClub/" + pais);
		for (int i = 0; i < jArray.length(); i++) {
			try {
				resultado.add(jArray.getString(i));
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
		}
		return resultado;
	}

	/**
	 * Método que regresa las categorias con cupones que son parte del club,
	 * sorteados por ciudad.
	 * 
	 * @param cityName
	 *            La ciudad que se buscará.
	 * @return Regresa el ArrayLIst de resultado.
	 */
	public ArrayList<String> getCategoriasConCuponesClubCiudad(String cityName) {
		cityName = prepareString(cityName);
		ArrayList<String> resultado = new ArrayList<String>();
		JSONParser jp = new JSONParser();
		JSONArray jArray = jp.getJSONFromUrlAsync(URL
				+ "/getCategoriasConCuponesClubCiudad/" + cityName);
		for (int i = 0; i < jArray.length(); i++) {
			try {
				resultado.add(jArray.getString(i));
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
		}
		return resultado;
	}

}
