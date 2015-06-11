package directorio.actividades;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
//
//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.app.SherlockActivity;
//import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

import java.util.ArrayList;

import directorio.adapters.GalleryImageAdapter;
import directorio.objetos.Cupon;
import directorio.services.dao.CuponDAO;

/**
 * Método que muestra una lista de cupones.
 * 
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */
@SuppressWarnings({ "deprecation" })
public class CuponesLista extends ActionBarActivity implements
		ISideNavigationCallback {

	private SideNavigationView sideNavigationCuponLista;

	private ArrayList<Cupon> cups = null;

	private String categoria;
	private String ciudad;

	// /Imgs
	private ImageView selectedImageView;

	private ImageView leftArrowImageView;

	private ImageView rightArrowImageView;

	private Gallery gallery;

	private int selectedImagePosition = 0;

	private GalleryImageAdapter galImageAdapter;

	private ArrayList<Bitmap> images;

	private TextView cuponName;
	private static final String TAG = "CuponesLista";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cupones_images);
		Log.d(TAG, "Actividad " + TAG);

		sideNavigationCuponLista = (SideNavigationView) findViewById(R.id.cupones_lista_sidenavigationview);
		sideNavigationCuponLista.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationCuponLista.setMenuClickCallback(this);

		// se obtiene la cateogría que se va a desplegar del intent
		categoria = getIntent().getExtras().getString("categoria");

		setTitle(categoria);

		// se obtiene la ciudad seleccionada por el usuario
		ciudad = getIntent().getExtras().getString("ciudad");

		// Cambio Diseño ActionBar
		getSupportActionBar().setBackgroundDrawable(
				this.getResources().getDrawable(R.drawable.header));
		getSupportActionBar().setIcon(R.drawable.menu);
		getSupportActionBar()
				.setDisplayOptions(0, ActionBar.DISPLAY_HOME_AS_UP);
		TextView customView = new TextView(this);
		customView.setTextColor(getResources().getColor(android.R.color.white));
		customView.setTextSize(20f);
		customView.setTypeface(null, Typeface.BOLD);
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.WRAP_CONTENT,
				ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL
						| Gravity.CENTER_VERTICAL);
		customView.setText(categoria);
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_USE_LOGO);
		getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// Se declara el async task que descargará las imágenes
		LoadImages li = new LoadImages(this);

		// Ejecutar async task
		li.execute();

		// Preparar los contenidos de la interfaz mientras se descargan las
		// imágenes
		setupUI();

	}

	@Override
	protected void onResume() {
	//	com.facebook.Settings.publishInstallAsync(getApplicationContext(),	getString(R.string.facebook_app_id));
	//	super.onResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			sideNavigationCuponLista.toggleMenu();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onSideNavigationItemClick(int itemId) {
		ChangeLayout.layoutChange(itemId, this);
		finish();
	}

	@Override
	public void onBackPressed() {
		if (sideNavigationCuponLista.isShown()) {
			sideNavigationCuponLista.hideMenu();
		} else {
			super.onBackPressed();
		}
	}

	/**
	 *  Método que prepara los elementso para ser mostrados en la pantalla.
	 */
	private void setupUI() {
		selectedImageView = (ImageView) findViewById(R.id.selected_imageview);
		leftArrowImageView = (ImageView) findViewById(R.id.left_arrow_imageview);
		rightArrowImageView = (ImageView) findViewById(R.id.right_arrow_imageview);
		gallery = (Gallery) findViewById(R.id.gallery);

		leftArrowImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (selectedImagePosition > 0) {
					--selectedImagePosition;
				}

				gallery.setSelection(selectedImagePosition, false);
			}
		});

		rightArrowImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (selectedImagePosition < images.size() - 1) {
					++selectedImagePosition;

				}

				gallery.setSelection(selectedImagePosition, false);

			}
		});

		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {

				selectedImagePosition = pos;

				if (selectedImagePosition > 0
						&& selectedImagePosition < images.size() - 1) {

					leftArrowImageView.setImageDrawable(getResources()
							.getDrawable(R.drawable.arrow_left_enabled));
					rightArrowImageView.setImageDrawable(getResources()
							.getDrawable(R.drawable.arrow_right_enabled));

				} else if (selectedImagePosition == 0) {

					leftArrowImageView.setImageDrawable(getResources()
							.getDrawable(R.drawable.arrow_left_disabled));

				} else if (selectedImagePosition == images.size() - 1) {

					rightArrowImageView.setImageDrawable(getResources()
							.getDrawable(R.drawable.arrow_right_disabled));
				}

				changeBorderForSelectedImage(selectedImagePosition);
				setSelectedImage(selectedImagePosition);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});

	}

	/**
	 * Método que se encarga de cargar las imágenes descargadas en el view.
	 */
	private void loadAdapter() {
		cuponName = (TextView) findViewById(R.id.cuponName);
		if (cups.size() <= 0)
			cuponName.setVisibility(View.VISIBLE);
		else
			cuponName.setVisibility(View.INVISIBLE);

		galImageAdapter = new GalleryImageAdapter(this, images);

		gallery.setAdapter(galImageAdapter);

		if (images.size() > 0) {

			gallery.setSelection(selectedImagePosition, false);

		}

		if (images.size() == 1) {

			rightArrowImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.arrow_right_disabled));
		}

	}

	/**
	 *  Método que se encarga de modificar la imagen.
	 * 
	 * @param selectedItemPos
	 *            La posición de la imagen en el arreglo.
	 */
	private void changeBorderForSelectedImage(int selectedItemPos) {

		int count = gallery.getChildCount();

		for (int i = 0; i < count; i++) {

			ImageView imageView = (ImageView) gallery.getChildAt(i);
			imageView.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.image_border));

			imageView.setPadding(5, 5, 5, 5);

		}

		ImageView imageView = (ImageView) gallery.getSelectedView();
		imageView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.selected_image_border));

		imageView.setPadding(5, 5, 5, 5);
	}

	/**
	 * Método que mueve el cursor a la imagen seleccionada.
	 * 
	 * @param selectedImagePosition
	 *            La posición de la imagen seleccionada.
	 */
	private void setSelectedImage(int selectedImagePosition) {
		Bitmap b = images.get(selectedImagePosition);
		selectedImageView.setImageBitmap(b);
		selectedImageView.setScaleType(ScaleType.FIT_XY);

	}

	/**
	 * Async Task que se encarga de descargar las imágenes en un thread aparte.
	 * Una vez que se termina de descargar las im�genes, se cargan a la
	 * pantalla.
	 * 
	 * @author Publysorpresas
	 * 
	 */
	public class LoadImages extends AsyncTask<Void, Integer, Void> {

		private Context ctx;
		private ProgressDialog pd = null;

		/**
		 * Constructor que requiere el contexto para poder mostrar el progress
		 * dialog.
		 * 
		 * @param c
		 *            EL contexto de la aplicación.
		 */
		public LoadImages(Context c) {
			ctx = c;
		}

		/**
		 * Método que carga los cupones sorteados por ciudad
		 */
		public void cargarViewCuponesCiudad() {
			CuponDAO cuponDao = new CuponDAO();

			cups = cuponDao.cuponesPorCategoriasCity(categoria, ciudad);
			images = new ArrayList<Bitmap>();
			for (int i = 0; i < cups.size(); i++) {

				if (cups.get(i).getPicUrl() != null) {

					Bitmap bmp = cups.get(i).setImgSrc(cups.get(i).getPicUrl());
					images.add(bmp);

				}
				publishProgress(i * 50 / cups.size());
			}

		}

		/**
		 * Método que carga los cupones.
		 */
		public void cargarViewCupones() {
			CuponDAO cuponDao = new CuponDAO();

			cups = cuponDao.cuponesPorCategorias(categoria);
			images = new ArrayList<Bitmap>();
			for (int i = 0; i < cups.size(); i++) {

				if (cups.get(i).getPicUrl() != null) {
					Bitmap bmp = cups.get(i).setImgSrc(cups.get(i).getPicUrl());
					images.add(bmp);

				}
				publishProgress(i * 50 / cups.size());

			}

		}

		/**
		 * Método que se corre después de que acaba el async task, este método
		 * se corre en el main thread.
		 */
		@Override
		protected void onPostExecute(Void result) {
			// Se destruye el progress dialog.
			pd.cancel();

			// Se cargan los elementos en pantalla.
			loadAdapter();
			super.onPostExecute(result);
		}

		/**
		 * Elemento que carga el progreso de la descarga de imágenes en
		 * pantalla.
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			Integer i = values[0];
			pd.setProgress(pd.getProgress() + i);
			super.onProgressUpdate(values);
		}

		/**
		 * Método que se carga antes de que empiece la async task, en este caso
		 * se muestra el progress dialog.
		 */
		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(ctx);
			pd.setTitle("Descargando Cupones");
			pd.setIndeterminate(false);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.show();
			super.onPreExecute();
		}

		/**
		 * Lo que se ejecuta en el background.
		 */
		@Override
		protected Void doInBackground(Void... params) {

			// en caso de que se seleccioná una ciudad en específico.
			if (ciudad != null) {
				cargarViewCuponesCiudad();

			} else {
				cargarViewCupones();
			}

			return null;
		}

	}

}
