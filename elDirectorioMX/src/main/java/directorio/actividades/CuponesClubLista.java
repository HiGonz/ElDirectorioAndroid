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
 * Muestra cupones del club de las categorías.
 * 
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */
@SuppressWarnings({ "deprecation" })
public class CuponesClubLista extends ActionBarActivity implements
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

	private static final String TAG = "CuponesClubLista";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cupones_images);
		Log.d(TAG, "Actividad " + TAG);

		sideNavigationCuponLista = (SideNavigationView) findViewById(R.id.cupones_lista_sidenavigationview);
		sideNavigationCuponLista.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationCuponLista.setMenuClickCallback(this);

		// se obtiene la cateogría del intent
		categoria = getIntent().getExtras().getString("categoria");

		setTitle(categoria);

		// se obtiene la ciudad del intent
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
		// se crea el async task que se ejecutará para la descarga de las
		// imágenes
		DownloadImages di = new DownloadImages(this);

		// se ejecuta el async task
		di.execute();

		// mientras se descargan las imágenes, se prepara la interfaz.
		setupUI();
	}

	@Override
	protected void onResume() {
	//	com.facebook.Settings.publishInstallAsync(getApplicationContext(),
	//			getString(R.string.facebook_app_id));
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
	 * Método que prepara la interfaz gráfica.
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
	 * Método que carga las imágenes en pantalla una vez que se descargaron
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
	 * Método que le cambia el borde al elemento seleccionado.
	 * 
	 * @param selectedItemPos
	 *            La posición del elemento seleccionado.
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
	 * Método que convierte a tipo bitmap una imagen.
	 * 
	 * @param selectedImagePosition
	 *            LA posición del elemento a convertir.
	 */
	private void setSelectedImage(int selectedImagePosition) {
		Bitmap b = images.get(selectedImagePosition);
		selectedImageView.setImageBitmap(b);
		selectedImageView.setScaleType(ScaleType.FIT_XY);

	}

	/**
	 * Async Task que se encarga de la descarga de las imágenes en otro thread,
	 * mientras que en el principal muetra una barra con el progreso de la
	 * descarga.
	 * 
	 * @author Publysorpresas
	 * 
	 */
	public class DownloadImages extends AsyncTask<Void, Integer, Void> {

		private Context ctx;
		private ProgressDialog pd;

		public DownloadImages(Context c) {
			ctx = c;
		}

		/**
		 * Método que carga los cupones en la interfaz gráfica.
		 */
		private void cargarViewCupones() {
			CuponDAO cuponDao = new CuponDAO();

			cups = cuponDao.cuponesClubPorCategorias(categoria);
			images = new ArrayList<Bitmap>();
			for (int i = 0; i < cups.size(); i++) {
				if (cups.get(i).getPicUrl() != null) {
					Bitmap bmp = cups.get(i).setImgSrc(cups.get(i).getPicUrl());
					images.add(bmp);
					publishProgress(i * 50 / cups.size());
				}
			}
		}

		/**
		 * Método que carga los cupones dependiendo de la ciudad seleccionada.
		 */
		private void cargarViewCuponesCiudad() {
			CuponDAO cuponDao = new CuponDAO();
			cups = cuponDao.cuponesClubPorCategoriasCity(categoria, ciudad);
			images = new ArrayList<Bitmap>();
			for (int i = 0; i < cups.size(); i++) {
				if (cups.get(i).getPicUrl() != null) {
					Bitmap bmp = cups.get(i).setImgSrc(cups.get(i).getPicUrl());
					images.add(bmp);
					publishProgress(i * 50 / cups.size());
				}
			}

		}

		@Override
		protected void onPostExecute(Void result) {
			// Se destruye el progress dialog.
			pd.cancel();

			// Se cargan los elementos en pantalla.
			loadAdapter();
			super.onPostExecute(result);
		}

		/**
		 * Método que prepara el progress dialog que mostrará el proceso de
		 * carga de los cupones.
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

		@Override
		protected void onProgressUpdate(Integer... values) {
			Integer i = values[0];
			pd.setProgress(pd.getProgress() + i);
			super.onProgressUpdate(values);
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (ciudad != null) {
				cargarViewCuponesCiudad();

			} else {
				cargarViewCupones();

			}
			return null;
		}

	}

}
