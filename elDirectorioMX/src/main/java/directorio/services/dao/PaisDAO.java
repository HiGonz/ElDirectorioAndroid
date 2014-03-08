package directorio.services.dao;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import directorio.tools.JSONParser;

/**
 * Clase que se encarga de descargar la información de los paises del web
 * service.
 * 
 * @author Publysorpresas
 * 
 */
public class PaisDAO {
	/** url del serb service **/
	private static String URL = "http://173.193.3.218/PaisService.svc/";

	// tag para el log cat
	private static final String TAG = "PaisDAO";

	/**
	 * Constructor vac�o
	 */
	public PaisDAO() {

	}

	/**
	 * Método que descarga toda la lista de países.
	 * 
	 * @return Una lista con los nombres de los países disponibles.
	 */
	public ArrayList<String> getPaises() {
		ArrayList<String> paises = new ArrayList<String>();
		JSONParser jp = new JSONParser();
		JSONArray jArray = jp.getJSONFromUrlAsync(URL + "getPaises");
		try {
			for (int i = 0; i < jArray.length(); i++) {
				paises.add(jArray.getString(i));
			}

		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
		return paises;
	}

	/**
	 * Método que descarga el código del país que se da como parámetro.
	 * 
	 * @param pais
	 *            El país del que se busca su código.
	 * @return El código del país.
	 */
	public String getCountryCode(String pais) {
		String result = "";
		JSONParser jp = new JSONParser();
		result = jp.getString(URL + "/getCountryCode/" + pais);

		// se prepara el string
		result = result.replace("\n", "");
		result = result.replace("\"", "");

		return result;
	}
}
