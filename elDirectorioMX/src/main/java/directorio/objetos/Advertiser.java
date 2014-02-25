package directorio.objetos;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.RejectedExecutionException;

/**
 * Clase que guarda las caracter�sticas del Advertiser.
 * 
 * @author Carlos Tirado
 * @author Juan Carlos Hinojo
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */

public class Advertiser {

	/**
	 * El id del advertiser
	 */
	private String id;
	/**
	 * Nombre del Advertiser
	 */
	private String nombre;
	/**
	 * Descripción del advertiser
	 */
	private String descripcion;
	/**
	 * COntacto con el advertiser
	 */
	private String contacto;
	/**
	 * Dirección del advertiser
	 */
	private String Direccion;
	/**
	 * Dirección web del advertiser
	 */
	private String sitioWeb;
	/**
	 * Posiciones X y Y en el mapa del advertiser
	 */
	private double posx;
	private double posy;
	/**
	 * Ciudad del advertiser.
	 */
	private String ciudad;
	/**
	 * Redes sociales de los advertisers
	 */
	private String facebook;
	private String twitter;
	/**
	 * Contacto de los advertisers
	 */
	private ArrayList<String> telefono;
	private ArrayList<String> email;
	/**
	 * Categorías de los advertisers
	 */
	private String[] categorias;
	/**
	 * Tags de los advertisers
	 */
	private String[] tags;
	/**
	 * DOnde se guardarón las imágenes.
	 */
	private byte[] imgSrc;
	/**
	 * Dice si es un advertiser featured o no.
	 */
	private boolean featured;

	private String _url;
	private File _archivo;

	private String youtubVideo;
	private String promocion;

	/**
	 * Getter de Featured, regresa true si el advertiser es un "featured".
	 * 
	 * @return
	 */
	public boolean getFeatured() {
		return featured;
	}

	/**
	 * Setter de featured, le asigna un valor booleano de featured.
	 * 
	 * @param featured
	 *            True si es featured, false en caso contrario.
	 */
	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	/**
	 * Getter de la imagen, regresa el archivo de la imagen.
	 * 
	 * @return El arreglo de imágenes.
	 */
	public byte[] getImgSrc() {

		return imgSrc;
	}

	/**
	 * Setter de las imágenes del advertiser.
	 * 
	 * @param url
	 *            El url con la localización de la imagen
	 */
	public void setImgSrc(String url) {

		File RutaImagenes = new File(
				"data/data/directorio.actividades/cache/imgs");
		RutaImagenes.mkdirs();
		File archivo = new File(RutaImagenes, this.getId() + ".png");

		if (archivo.exists()) {
			try {
				InputStream is = new FileInputStream(archivo);
				this.imgSrc = IOUtils.toByteArray(is);
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		} else {

			_url = url;
			_archivo = archivo;

			try {
				new AsyncImgDownload().execute();
			} catch (RejectedExecutionException ex) {
				Log.d("REJECTED EXECUTION - ASYNC TASK",
						nombre
								+ " - There was an error about the queu for running too many AsyncTasks");
			}

		}

	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Id del advertiser
	 * @param nombre
	 *            Nombre del advertiser
	 * @param descripcion
	 *            Descripción del advertiser
	 * @param contacto
	 *            Contacto del advertiser
	 * @param direccion
	 *            Dirección del advertiser
	 * @param sitioWeb
	 *            Sitio web del advertiser
	 * @param posx
	 *            Posición X en el mapa del advertiser
	 * @param posy
	 *            Posición Y en el mapa del advertiser
	 * @param ciudad
	 *            Ciudad del Advertiser
	 * @param facebook
	 *            Facebook del advertiser
	 * @param twitter
	 *            Twitter del advertiser
	 * @param telefono
	 *            Teléfono del advertiser
	 * @param email
	 *            Email del advertiser
	 * @param categorias
	 *            Categorías del Advertiser
	 * @param tags
	 *            Tags del advertiser
	 * @param favorito
	 *            Valor booleano representando si el advertiser es favorito o
	 *            no.
	 */
	public Advertiser(String id, String nombre, String descripcion,
			String contacto, String direccion, String sitioWeb, double posx,
			double posy, String ciudad, String facebook, String twitter,
			ArrayList<String> telefono, ArrayList<String> email,
			String[] categorias, String[] tags, boolean favorito) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.contacto = contacto;
		Direccion = direccion;
		this.sitioWeb = sitioWeb;
		this.posx = posx;
		this.posy = posy;
		this.ciudad = ciudad;
		this.facebook = facebook;
		this.twitter = twitter;
		this.telefono = telefono;
		this.email = email;
		this.categorias = categorias;
		this.tags = tags;
		this.favorito = favorito;
	}

	/**
	 * Constructor vacío
	 */
	public Advertiser() {

	}

	/**
	 * Getter de la descripción
	 * 
	 * @return Un string con la descripción del advertiser.
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Setter de la descripci�n
	 * 
	 * @param descripcion
	 *            El valor string que será la descripción.
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getYoutubVideo() {
		return youtubVideo;
	}

	public void setYoutubVideo(String youtubVideo) {
		this.youtubVideo = youtubVideo;
	}

	/**
	 * Getter del contacto
	 * 
	 * @return Un valor string con el contacto
	 */
	public String getContacto() {
		return contacto;
	}

	public String getPromocion() {
		return promocion;
	}

	public void setPromocion(String promocion) {
		this.promocion = promocion;
	}

	/**
	 * Setter de contacto
	 * 
	 * @param contacto
	 *            El contacto
	 */
	public void setContacto(String contacto) {
		this.contacto = contacto;
	}

	/**
	 * Getter de la posición X del advertiser.
	 * 
	 * @return Un valor doble con la posición X del advertiser.
	 */
	public double getPosx() {
		return posx;
	}

	/**
	 * Setter de la posición X.
	 * 
	 * @param posx
	 *            EL setter de la posición X del advertiser.
	 */
	public void setPosx(double posx) {
		this.posx = posx;
	}

	/**
	 * Getter de la posic�n Y
	 * 
	 * @return Un valor double con la posición Y del advertiser.
	 */
	public double getPosy() {
		return posy;
	}

	/**
	 * Setter de la posición Y
	 * 
	 * @param posy
	 *            El valor con la posición Y del advertiser.
	 */
	public void setPosy(double posy) {
		this.posy = posy;
	}

	/**
	 * Getter de la ciudad
	 * 
	 * @return Regresa un string con la ciudad donde se encuentra el advertiser.
	 */
	public String getCiudad() {
		return ciudad;
	}

	/**
	 * Setter de la ciudad
	 * 
	 * @param ciudad
	 *            La ciudad donde se encuentra el advertiser.
	 */
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	/**
	 * Getter de los tel�fonos del advertiser
	 * 
	 * @return Una lista con los teléfonos del advertiser
	 */
	public ArrayList<String> getTelefono() {
		return telefono;
	}

	/**
	 * Setter de los teléfonos, este le quita los separadores de la base de
	 * datos.
	 * 
	 * @param telefonos
	 *            La lista de los teléfonos
	 */
	public void setTelefono(String telefonos) {
		telefono = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(telefonos);
		while (st.hasMoreElements()) {
			String temp = st.nextToken("*|@") + ": " + st.nextToken("*|@");
			telefono.add(temp);
		}

	}

	/**
	 * Setter número 2 del teléfono, este los agrega tal cual.
	 * 
	 * @param numeroTelefono
	 */
	public void setTelefono2(String numeroTelefono) {
		if (telefono == null) {
			telefono = new ArrayList<String>();
		}

		telefono.add(numeroTelefono);
	}

	/**
	 * Getter del array list de email.
	 * 
	 * @return Regresa un arraylist con los teléfonos del advertiser.
	 */
	public ArrayList<String> getEmail() {
		return email;
	}

	/**
	 * Setter que agrega un email desde la base de datos, quitando los
	 * separadores.
	 * 
	 * @param emails
	 *            El email de la base de datos.
	 */
	public void setEmail(String emails) {
		email = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(emails);
		while (st.hasMoreElements()) {
			String temp = st.nextToken("*|");
			String resultado = "";
			for (int j = 1; j < temp.length(); j++) {
				resultado += temp.charAt(j);
			}
			email.add(resultado);
		}
	}

	/**
	 * Setter que le agrega el email a la lista de emails
	 * 
	 * @param mail
	 *            El email a agregar.
	 */
	public void setEmail2(String mail) {
		if (email == null) {
			email = new ArrayList<String>();
		}

		email.add(mail);
	}

	/**
	 * Regresa un array de strings con las categorías.
	 * 
	 * @return EL array de strings con las cateogrías.
	 */
	public String[] getCategorias() {
		return categorias;
	}

	/**
	 * Setter que agrega las categorías.
	 * 
	 * @param categorias
	 *            las categorías del advertiser.
	 */
	public void setCategorias(String[] categorias) {
		this.categorias = categorias;
	}

	/**
	 * Regresa los tags del advertiser.
	 * 
	 * @return Los tags del advertiser.
	 */
	public String[] getTags() {
		return tags;
	}

	/**
	 * Setter de los tags
	 * 
	 * @param tags
	 *            Los tags del advertiser.
	 */
	public void setTags(String[] tags) {
		this.tags = tags;
	}

	/**
	 * Valor booleano que guarda si el advertiser es favorito o no.
	 */
	private boolean favorito; // en caso de que sea favorito, guardarlo en la
								// clase

	/**
	 * EL getter del id
	 * 
	 * @return EL id del advertiser.
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Getter del nombre
	 * 
	 * @return el nombre del advertiser
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Setter del nombre
	 * 
	 * @param nombre
	 *            nombre del advertiser.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Getter de la dirección
	 * 
	 * @return La dirección del advertiser.
	 */
	public String getDireccion() {
		return Direccion;
	}

	/**
	 * Setter de la dirección
	 * 
	 * @param direccion
	 *            La dirección del advertiser.
	 */
	public void setDireccion(String direccion) {
		Direccion = direccion;
	}

	/**
	 * Getter de la dirección web
	 * 
	 * @return EL sitio web
	 */
	public String getSitioWeb() {
		return sitioWeb;
	}

	/**
	 * Setter del sitio web
	 * 
	 * @param sitioWeb
	 *            El sitio web del advertiser.
	 */
	public void setSitioWeb(String sitioWeb) {
		this.sitioWeb = sitioWeb;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public boolean isFavorito() {
		return favorito;
	}

	public void setFavorito(boolean favorito) {
		this.favorito = favorito;
	}

	@Override
	public String toString() {

		return "" + nombre + descripcion + ciudad + facebook;
	}

	/**
	 * AsyncTask que se encarga de descargar las imágenes en otro thread.
	 * 
	 * @author NinjaDevelop
	 * 
	 */
	public class AsyncImgDownload extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			URL uri;
			try {

				if (_url == null) {
					Advertiser.this.imgSrc = null;
				} else {
					int totalSize = 0;
					InputStream inputStream = null;
					FileOutputStream fileOutput = null;
					try {
						uri = new URL(_url);
						HttpURLConnection urlConnection = (HttpURLConnection) uri
								.openConnection();

						urlConnection.setRequestMethod("GET");
						urlConnection.setDoOutput(true);
						urlConnection.connect();

						inputStream = urlConnection.getInputStream();
						fileOutput = new FileOutputStream(_archivo);

						totalSize = urlConnection.getContentLength();
					} catch (Exception ex) {
					}
					if (totalSize == 0) {
						Advertiser.this.imgSrc = null;
					} else {
						int downloadedSize = 0;

						byte[] buffer = new byte[1024];
						int bufferLength = 0;
						while ((bufferLength = inputStream.read(buffer)) > 0) {

							downloadedSize += bufferLength;

							publishProgress((int) (downloadedSize * 100 / totalSize));

							fileOutput.write(buffer, 0, bufferLength);
						}
						fileOutput.close();
						InputStream is = new FileInputStream(_archivo);
						imgSrc = IOUtils.toByteArray(is);
						Advertiser.this.imgSrc = imgSrc;
					}
				}

			} catch (MalformedURLException e) {

				e.printStackTrace();
			} catch (ProtocolException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

			return true;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {

			super.onProgressUpdate(progress);

		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {

			}
		}

		@Override
		protected void onCancelled() {

		}

	}

}