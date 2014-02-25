package directorio.actividades;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import directorio.services.dao.AdvertiserDAO;

/**
 * Clase que se encarga de mostrar las imágenes de las galerías de los
 * advertisers.
 * 
 * @author Carlos Tirado
 * @author Publysorpresas
 * 
 */
@SuppressWarnings("deprecation")
public class GalleryView extends Activity {

	// elementos de la interfaz
	private ImageView imageView;
	private Button back;
	private Gallery ga;

	private String GalleryId;
	private ArrayList<Bitmap> images = new ArrayList<Bitmap>();
	private ArrayList<Bitmap> thumbs = new ArrayList<Bitmap>();
	private ArrayList<String> urls;
	private AdvertiserDAO aDao = new AdvertiserDAO();

	// tag de la actividad
	private String TAG = "GalleryView";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_galery_view);
		Log.d(TAG, "Actividad " + TAG);

		// se cargan los elementos de la interfaz
		setupViews();

		// se descargan y muestran las imágenes
		new ImageWork(this).execute();
	}

	/**
	 * Método que prepara los elementos de la interfaz.
	 */
	private void setupViews() {
		back = (Button) findViewById(R.id.Back);
		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		ga = (Gallery) findViewById(R.id.Gallery01);

		imageView = (ImageView) findViewById(R.id.ImageView01);

	}

	/**
	 * Método que muestra los elementos descargados en la interfaz gráfica.
	 */
	private void loadViews() {
		ga.setAdapter(new ImageAdapter(getApplicationContext()));

		ga.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				imageView.setImageBitmap(images.get(arg2));
			}
		});
	}

	@Override
	protected void onResume() {
		com.facebook.Settings.publishInstallAsync(getApplicationContext(),
				getString(R.string.facebook_app_id));
		super.onResume();
	}

	/**
	 * Método que se encarga de descargar las imágenes a utilizar del url que se
	 * tiene como parámetro.
	 * 
	 * @param url
	 *            EL url de donde se descargará la imagen.
	 * @return Objeto Bitmap para poder mostrarse.
	 */
	private Bitmap ImageOperations(String url) {
		// Input stream donde se pondra el resultado del metodo fetch
		Object o = fetch(url);
		InputStream is = (InputStream) o;
		
		Bitmap d = null;

		// se descargan las imágenes con el método
		d = executeDownload(is);

		return d;
	}

	/**
	 * Método que realiza la descarga de la imagen.
	 * 
	 * @param is
	 *            El imput stream desde donde se codificará y realizará la
	 *            descarga.
	 * @return Un objeto tipo Bitmap que se descarga.
	 */
	private Bitmap executeDownload(InputStream is) {
		// en caso de necesitar cambiar el tamaño de la imagen para poder
		// desplegarlas, la variable decode se hace true.
		boolean decode = false;

		// Se obtiene el puntero al display
		Display display = getWindowManager().getDefaultDisplay();

		// variables que representar�n el tama�o del display
		int width = 0;
		int height = 0;

		// Si la versión del celular es mayor a 13
		if (android.os.Build.VERSION.SDK_INT >= 13) {
			// crear un tipo point, que guarde las medidas y guardarlas en
			// las variables
			// este método de obtención de tamaño no funciona en versiones
			// viejas de android
			Point size = new Point();
			display.getSize(size);
			width = size.x;
			height = size.y;
		} else {
			// en caso de que la versión de android sea "vieja", usar los
			// métodos despeciados.
			width = display.getWidth();
			height = display.getHeight();
		}

		// en caso de que la pantalla sea "pequeña", cambiar el tamaño de la
		// imagen para que no gaste tanta memoria.
		if (width < 320 && height < 480) {
			decode = true;
		}

		return decodeFile(is, decode);
	}

	/**
	 * Método que realiza la descarga de datos de la url.
	 * 
	 * @param address
	 *            la dirección de donde se hará la descarga
	 * @return Un objeto con los datos a ser convertidos a tipo Drawable o tipo
	 *         Bitmap.
	 */
	public Object fetch(String address) {
		Object content = null;
		// Se usa un async task para retraer esos datos.
		content = UrlOperations(address);
		return content;
	}

	/**
	 * Método que convierte el archivo en tipo Bitmap
	 * 
	 * @param f
	 *            el inputstream que se convertirá.
	 * @param resize
	 *            En caso de true, se cambiará el tamaño de la imagen para que
	 *            no se acabe la memoria del celualr.
	 * @return El Bitmap de la imagen lista para ser mostrada.
	 */
	private Bitmap decodeFile(InputStream f, boolean resize) {
		Bitmap result = null;

		if (resize == true) {

			// si requiere cambiar tamaño, se cambia
			BitmapFactory.Options options = new BitmapFactory.Options();

			// valor obtenido por trial and error xD
			options.inSampleSize = 6;

			// se crea un bitmap, y el tamaño será tamañoFinal =
			// tamañoInicial/6
			result = BitmapFactory.decodeStream(f, null, options);
		} else {

			// en caso de que no necesite cambiar tamaño, simplemente
			// decodificar a bitmap.
			result = BitmapFactory.decodeStream(f);
		}
		return result;
	}

	/**
	 * Método que se encarga de hacer la descarga desde el URL.
	 * 
	 * @param address
	 *            La dirección desde donde se va a descargar la imagen.
	 * @return Un objeto descargado desde la URL.
	 */
	private Object UrlOperations(String address) {
		URL url;
		Object content = null;
		try {
			// se crea un objeto url
			url = new URL(address);
			// se obtiene el contenido que hay en el url
			content = url.getContent();
		} catch (MalformedURLException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * Async Task que se encarga de hacer todo lo referente a descarga y
	 * modificación de las imágenes.
	 * 
	 * @author Publysorpresas
	 * 
	 */
	public class ImageWork extends AsyncTask<Void, Integer, Void> {

		private Context ctx;
		private ProgressDialog pd = null;

		public ImageWork(Context c) {
			ctx = c;
		}

		/**
		 * Método que se encarga de obtener las imágenes de la galería y
		 * prepararlas.
		 */
		private void setGallery() {
			// Se obtiene el gallery id a usar.
			SharedPreferences sp = getSharedPreferences("galeriaId", 0);
			GalleryId = sp.getString("galeriaId", null);
			
			// Se obtienen los urls de las impagenes de las galerias.
			urls = aDao.getUrls(GalleryId);
			for (int i = 0; i < urls.size(); i++) {
				// Se crea el objeto bitmap usando el método ImageOperations.
				Bitmap d = ImageOperations(urls.get(i));
				images.add(d);
				// se publica el resultado
				publishProgress(i * 50 / urls.size());
			}

		}

		/**
		 * Método que se encarga de preparar los Thumbs que se van a desplegar
		 * en el Gallery View.
		 */
		private void setThumbs() {
			// Se trae el id de la galería.
			SharedPreferences sp = getSharedPreferences("galeriaId", 0);
			GalleryId = sp.getString("galeriaId", null);
			
			// Se obtienen los urls de los thumbs
			urls = aDao.getThumbs(GalleryId);
			for (int i = 0; i < urls.size(); i++) {
				// Se comvierne a bitmap usando el método imageOperations
				Bitmap d = ImageOperations(urls.get(i));
				thumbs.add(d);
				// se publica el resultado
				publishProgress(i * 50 / urls.size());
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Integer i = values[0];
			pd.setProgress(pd.getProgress() + i);
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {
			loadViews();
			pd.cancel();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(ctx);
			pd.setTitle("Descargando Im�genes");
			pd.setIndeterminate(false);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			setGallery();
			setThumbs();
			return null;
		}

	}

	/**
	 * Image Adapter para mostrar las imagenes
	 * 
	 * @author NinjaDevelop
	 * 
	 */
	public class ImageAdapter extends BaseAdapter {

		private Context ctx;
		int imageBackground;

		public ImageAdapter(Context c) {
			ctx = c;
			TypedArray ta = obtainStyledAttributes(R.styleable.Gallery1);
			imageBackground = ta.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 1);
			ta.recycle();
		}

		public int getCount() {

			return images.size();
		}

		public Object getItem(int arg0) {

			return arg0;
		}

		public long getItemId(int arg0) {

			return arg0;
		}

		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ImageView iv = new ImageView(ctx);
			iv.setImageBitmap(thumbs.get(arg0));

			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			iv.setLayoutParams(new Gallery.LayoutParams(150, 120));
			iv.setBackgroundResource(imageBackground);
			return iv;
		}

	}
}