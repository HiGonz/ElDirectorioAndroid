package directorio.others;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DownloadAndReadImage {

	// String que tiene el Json con el string de la imagen
	JSONObject JSONData;

	// número de imagen que se descargo
	int identificador;

	// bitmap de la imagen que se va a regresar
	Bitmap bitmap = null;

	/**
	 * Constructor
	 * 
	 * @param JSONData
	 *            El string con los datos de la imagen
	 * @param position
	 *            Un identificador para la imagen
	 */
	public DownloadAndReadImage(JSONObject JSONData, int position) {
		this.JSONData = JSONData;
		this.identificador = position;
	}

	/**
	 * Método que descarga la imagen y la lee de memoria
	 * 
	 * @return El bitmap de la imagen que se guardá en memoria
	 */
	public Bitmap getBitmapImage() {
		// se descarga la imagen
		downloadBitmapImage();
		// se lee de memoria
		return readBitmapImage(identificador);
	}

	/**
	 * Método que descarga la imagen y la guarda en memoria
	 */
	@SuppressLint("SdCardPath")
	private void downloadBitmapImage() {
		InputStream input;
		try {

			Gson gson = new Gson();

			// se convierte a byte array
			input = new ByteArrayInputStream(gson.fromJson(
					JSONData.getString("PhotoBytes"), byte[].class));

			byte[] buffer = new byte[1500];
			// output stream para convertir la imagen a png y guardarlo en la
			// dirección especificada
			
			OutputStream output = new FileOutputStream("/sdcard/identificador"
					+ identificador + ".png");
			// se escribe en memoria
			try {
				int bytesRead = 0;
				while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
					output.write(buffer, 0, bytesRead);
				}
			} finally {
				output.close();
				buffer = null;
			}
		} catch (Exception e) {
			// Toast.makeText(getApplicationContext(), e.toString(),
			// Toast.LENGTH_LONG).show();
			Log.e("DownloadImages", e.toString());
		}
	}
	/**
	 * Método que lee de memoria las imágenes
	 * @param position la posición de la imagen a leer
	 * @return el Bitmap de la imagen
	 */
	@SuppressLint("SdCardPath")
	private Bitmap readBitmapImage(int position) {
		//string con el path hacia la imagen
		String imageInSD = "/sdcard/identificador" + position + ".png";
		
		//opciones para la codificacion de la imagen
		BitmapFactory.Options bOptions = new BitmapFactory.Options();
		bOptions.inTempStorage = new byte[16 * 1024];
		
		//se codifica el bitmap con las opciones
		bitmap = BitmapFactory.decodeFile(imageInSD, bOptions);

		return bitmap;
	}

}
