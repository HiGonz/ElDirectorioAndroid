package directorio.objetos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * Clase que representa el cupón.
 * 
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */
public class Cupon {

	// Variables que forman parte del cupón.
	private int cuponId;
	private String advertiserId;
	private String name;
	private String descripcion;
	private String conditions;
	private String HowToCash;
	private String start;
	private String end;
	private String picUrl;
	private String Negocio;
	private byte[] imgSrc;

	private final String TAG = "Cupon";

	/**
	 * Constructor vacío.
	 */
	public Cupon() {
	}

	// Getters y setters

	public String getNegocio() {
		return Negocio;
	}

	public void setNegocio(String negocio) {
		Negocio = negocio;
	}

	public int getCuponId() {
		return cuponId;
	}

	public void setCuponId(int cuponId) {
		this.cuponId = cuponId;
	}

	public byte[] getImgSrc() {

		return imgSrc;
	}

	/**
	 * M�todo que hace la descarga del cup�n.
	 * 
	 * @param url
	 *            El url donde se encuentra la imagen del cupón.
	 * @return Un objeto bitmap con el cupón.
	 */
	public Bitmap setImgSrc(String url) {
		Bitmap result = downloadCupon(url);
		return result;
	}

	public String getAdvertiserId() {
		return advertiserId;
	}

	public void setAdvertiserId(String advertiserId) {
		this.advertiserId = advertiserId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public String getHowToCash() {
		return HowToCash;
	}

	public void setHowToCash(String howToCash) {
		HowToCash = howToCash;
	}

	public String getStart() {
		try {
			StringTokenizer st = new StringTokenizer(start);
			String result = st.nextToken();
			return result;
		} catch (NullPointerException npe) {
			Log.e(TAG, npe.toString());
			return "";
		}
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	/**
	 * Método que hace la descarga del cupón en el thread principal.
	 * 
	 * @param url
	 *            EL url donde se encuentra la imágen.
	 * @return Un objeto Bitmap que representa la imagen.
	 */
	private Bitmap downloadCupon(String url) {
		// Se crea un objeto url
		URL src = null;
		try {
			src = new URL(url);
		} catch (MalformedURLException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
		// Se abre la conexi�n con el servidor
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) src.openConnection();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
		connection.setDoInput(true);
		try {
			connection.connect();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
		// Se obtiene el input stream
		InputStream input = null;
		try {
			input = connection.getInputStream();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
		// Se codifica hacia un bitmap
		Bitmap myBitmap = BitmapFactory.decodeStream(input);
		// Se regresa el objeto bitmap
		return myBitmap;

	}

}
