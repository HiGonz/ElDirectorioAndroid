package directorio.services.dao;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import directorio.objetos.Sucursal;
import directorio.tools.JSONParser;

/**
 * Clase que maneja el acceso a web services de las sucursales.
 * 
 * @author NinjaDevelop
 * @author Carlos Tirado
 * @author Publysorpresas
 * 
 */
public class SucursalDAO {
	/** El url del web service **/
	private static final String URL = "http://173.193.3.218/SucursalService.svc/";

	/** Los tags que se usar�n para decifrar el objeto JSON **/
	private static final String ADDRESS_TAG = "Address";
	private static final String ID_TAG = "AdvertiserID";
	private static final String CITY_TAG = "cityName";
	private static final String NAME_TAG = "name";
	private static final String OFFICE_ID_TAG = "officeId";
	private static final String POINT_X_TAG = "pointX";
	private static final String POINT_Y_TAG = "pointY";
	private static final String ZIP_TAG = "zip";
	private static final String NUMBER_TAG = "number";
	private static final String NEIGHBOR_TAG = "neighbor";

	public static final String TAG = "SucursalDAO";

	/**
	 * Constructor vacío.
	 */
	public SucursalDAO() {

	}

	/**
	 * Regresa las sucursales usando el id del negocio.
	 * 
	 * @param id
	 *            Id del negocio.
	 * @return Un array list de los nombres de las sucursales.
	 */
	public ArrayList<String> getStringSucursales(String id) {
		ArrayList<String> resultado = new ArrayList<String>();

		// se descargan las sucursales del web service
		ArrayList<Sucursal> sucursales = getSucursales(id);

		// se preparan los strings que se van a mostrar en la interfaz
		for (int i = 0; i < sucursales.size(); i++) {
			Sucursal s = sucursales.get(i);
			String temporal = s.getName() + ", " + s.getAddress() + ", ";
			int number = s.getNumber();
			if (number > 0) {
				temporal += "#" + number + ", ";
			}

			String colonia = s.getNeighborhood();
			if (colonia.equals("noHay")) {
				// do nothing
			} else {
				temporal += "Colonia " + colonia + ", ";
			}

			int cp = s.getZipCode();
			if (cp > 0) {
				temporal += "C.P. " + cp + ", ";
			}

			temporal += s.getCityName();
			resultado.add(temporal);
		}
		return resultado;

	}

	/**
	 * Método que descarga las sucursales usando el id del negocio.
	 * 
	 * @param id
	 *            EL id del negocio
	 * @return Las sucursales del negocio.
	 */
	public ArrayList<Sucursal> getSucursales(String id) {
		ArrayList<Sucursal> resultado = new ArrayList<Sucursal>();
		JSONParser jp = new JSONParser();
		JSONArray jArray = jp.getJSONFromUrlAsync(URL + "getSucs/" + id);
		try {
			for (int i = 0; i < jArray.length(); i++) {
				Sucursal s = new Sucursal();
				JSONObject o = jArray.getJSONObject(i);
				s.setAddress(o.getString(ADDRESS_TAG));
				s.setAdvertiserID(o.getInt(ID_TAG));
				s.setCityName(o.getString(CITY_TAG));
				s.setId(o.getInt(OFFICE_ID_TAG));
				s.setName(o.getString(NAME_TAG));
				s.setPointX(o.getDouble(POINT_X_TAG));
				s.setPointY(o.getDouble(POINT_Y_TAG));
				s.setNeighborhood(o.getString(NEIGHBOR_TAG));
				s.setNumber(o.getInt(NUMBER_TAG));
				s.setZipCode(o.getInt(ZIP_TAG));
				resultado.add(s);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}

		return resultado;
	}

	/**
	 * Ver si un negocio tiene sucursales.
	 * 
	 * @param id
	 *            El id del negocio.
	 * @return Valor booleano indicando si tiene o no sucursales.
	 */
	public boolean tieneSucursales(String id) {
		boolean resultado = false;
		JSONParser jp = new JSONParser();
		int temp = jp.getIntegerBoolean(URL + "hasSucursales/" + id);
		if (temp == 1) {
			resultado = true;
		}
		return resultado;
	}

}