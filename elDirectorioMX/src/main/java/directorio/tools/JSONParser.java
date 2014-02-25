package directorio.tools;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

/**
 * Clase que se encarga de decifrar los JSON que se descargan.
 * 
 * @author Publysorpresas
 * 
 */
public class JSONParser {

	/**
	 * Variable que se encarga de guardar el JSONArray que se va a regresar.
	 */
	static JSONArray jArr = null;

	/** Variable que se encarga de guardar el resultado del Web Service **/
	static String json = "";

	/** Tag para los debugs **/
	private static String TAG = "JSONParser";

	/** Input stream que se obtiene del web service. **/
	private static InputStream is;

	/**
	 * Constructor vacío
	 */
	public JSONParser() {
	}

	/**
	 * Método que se encarga de hacer el acceso al web service.
	 * 
	 * @param url
	 *            EL url del web service al que se planea accesar.
	 */
	private void retrieveData(String url) {
		// Se crea una async task que haga todo el manejo de datos en un thread
		// aparte.
		DownloadClass dc = new DownloadClass();
		try {
			// se guardan los resultados en el thread
			json = dc.execute(url).get();
		} catch (InterruptedException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		} catch (ExecutionException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Si el resultado del service es un integer o un booleano, se llama este
	 * método
	 * 
	 * @param url
	 *            EL url del service
	 * @return Valor integer ya sea 0 y 1 para true o false, o cualquier otro
	 *         valor integer para valores numéricos.
	 */
	public int getIntegerBoolean(String url) {
		int valor = 0;

		// se descargan los datos del web service
		retrieveData(url);

		// se quitan los saltos de linea para poder hacer un parse a integer
		json = json.replace("\n", "");

		// se hace el parse
		valor = Integer.parseInt(json);
		return valor;
	}

	/**
	 * Método que descarga un valor doble del web service.
	 * 
	 * @paam url
	 *            Url del web service.
	 * @return EL valor descargado.
	 */
	public double getValorDouble(String url) {
		double result = 0.0;

		// se llama el web service
		retrieveData(url);

		// se quitan los saltos de linea para parsear
		json = json.replace("\n", "");

		// parse
		result = Double.parseDouble(json);
		return result;
	}

	/**
	 * Método que descarga la información del web service como un string
	 * 
	 * @param url
	 *            Url del web service
	 * @return EL objeto interno json con el valor guardado.
	 */
	public String getString(String url) {
		retrieveData(url);
		return json.toString();
	}

	/**
	 * M�todo que hace la descarga de datos a trav�s de un Async Task.
	 * 
	 * @param url
	 *            El url de donde se descargará la información.
	 * @return La informaci�n en formato JSON.
	 */
	public JSONArray getJSONFromUrlAsync(String url) {
		// se descargan los datos.
		retrieveData(url);

		// Parse to JSOn object
		try {
			jArr = new JSONArray(json);
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jArr;
	}

	/**
	 * Método que hace la descarga de datos en el thread principal.
	 * 
	 * @param url
	 *            EL url de del web service.
	 * @return EL resultado.
	 */
	public JSONArray getJSONFromUrl(String url) {

		String result = "";
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		json = result;
		json = json.replace("\n", "");

		try {
			jArr = new JSONArray(json);
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		return jArr;
	}

	/**
	 * Async TAsk que se encarga de descargar la información del web service en
	 * un thread aparte.
	 * 
	 * @author Publysorpresas
	 * 
	 */
	private class DownloadClass extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			String url = "";
			String result = "";

			// se guarda el parámetro de la async task
			for (String s : params) {
				url = s;
			}

			// Hacer el http request
			try {
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);

				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				Log.e(TAG, e.toString());

			} catch (ClientProtocolException e) {
				e.printStackTrace();
				Log.e(TAG, e.toString());
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, e.toString());
			}
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;

				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
			} catch (Exception e) {
				Log.e(TAG, e.toString());
				Log.e("Buffer Error", "Error converting result " + e.toString());
			}
			return result;
		}

	}

}
