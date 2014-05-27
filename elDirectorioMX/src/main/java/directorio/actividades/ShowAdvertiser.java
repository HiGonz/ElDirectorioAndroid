package directorio.actividades;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

import java.util.ArrayList;

import directorio.applications.TodoManagerApplication;
import directorio.objetos.Advertiser;
import directorio.objetos.Sucursal;
import directorio.services.dao.AdvertiserDAO;
import directorio.services.dao.FavoritosDAO;
import directorio.services.dao.SucursalDAO;

/**
 * Actividad que se encarga de mostrar la información especfica de un negocio.
 * 
 * @author NinjaDevelop
 * @author Carlos Tirado
 * @author Juan Carlos Hinojo
 * @author Publysorpresas
 * 
 */
public class ShowAdvertiser extends SherlockActivity implements ISideNavigationCallback {

	private SideNavigationView sideNavigationShowAdv;

	/** El advertiser que se va a mostrar **/
	private Advertiser advertiserToShow;

	// daos
	private AdvertiserDAO advertiserDao;
	private FavoritosDAO fd;

	private Intent intent;
	private String advertiser;
	private ImageView iv;
	private ProgressBar loadCoupons;
	private static final String TAG = "ShowAdvertiser";

	private TextView nombreEmpresa;

	private TextView descripcionEmpresa;

	private TextView direccionEmpresa;

	private LinearLayout rl;

	private TextView suc;

	private SucursalDAO sDAO;

	private LinearLayout ll;

	private TextView gal;

	private TextView galName;

	private TextView coupon;

	private TextView couponLink;

	private TextView couponClub;

	private TextView couponLinkClub;

	private Button favs;

	private Button addContacts;

	private TextView youtubeBlue;

	private TextView youtube;

	private ImageView destacado;

	private TextView promocionBlue;
	private TextView promocion;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_advertiser);
		Log.d(TAG, "Actividad " + TAG);

		fd = new FavoritosDAO(this);

		sideNavigationShowAdv = (SideNavigationView) findViewById(R.id.show_advertiser_sidenavigationview);
		sideNavigationShowAdv.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationShowAdv.setMenuClickCallback(this);

		// el nombre del advertiser a buscar
		advertiser = getIntent().getExtras().getString("advertiser");

		setTitle(advertiser);

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
		customView.setText(advertiser);
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_USE_LOGO);
		getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Change Font
		Typeface tf = Typeface.createFromAsset(getAssets(),
				"RobotoCondensed-Regular.ttf");
		TextView nombreEmpresa = (TextView) findViewById(R.id.nombre_empresa);
		nombreEmpresa.setTypeface(tf);

		advertiserDao = new AdvertiserDAO();

		loadCoupons = (ProgressBar) findViewById(R.id.loadingAdvCoupons);
		loadCoupons.setVisibility(View.INVISIBLE);

		sDAO = new SucursalDAO();

		// métodos para cargar los elementos de la interfaz
		searchAdvertiser();
		setupViews();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			sideNavigationShowAdv.toggleMenu();
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
		if (sideNavigationShowAdv.isShown()) {
			sideNavigationShowAdv.hideMenu();
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * Método que descarga la información del advertiser que se va a mostrar
	 */
	private void searchAdvertiser() {
		// Se usa el manager application para poder ver el pa�s que seleccioné
		// el usuario
		TodoManagerApplication tma = (TodoManagerApplication) getApplication();

        String  country = getIntent().getStringExtra("pais");
        if (country == null){
            System.out.println("entre aqui");
            country = tma.getCountry();
        }
        System.out.println(country);

        try{
            country = country.replace(" ","%20");
        }catch (Exception e){
            e.printStackTrace();
        }


		// se usa un async task para descargar la información
		PrepareStuff ps = new PrepareStuff(this);
		ps.execute(country);
	}

	/**
	 * Método que manda la información para que se abra en el mapa de google.
	 * 
	 * @param posx
	 *            Posición X del negocio.
	 * @param posy
	 *            Posición Y del negocio.
	 * @param nombre
	 *            Nombre del negocio.
	 * @param Direccion
	 *            Dirección del negocio.
	 */
	public void sendInfo(double posx, double posy, String nombre,
			String Direccion) {
		try {

			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse("geo:0,0?q=" + posx + ", " + posy + " (" + nombre
							+ ")"));
			startActivity(intent);

		} catch (ActivityNotFoundException e) {
			(Toast.makeText(this,
					"No se ha encontrado la aplicación de Google Maps",
					Toast.LENGTH_LONG)).show();
		} catch (Exception ex) {
			ex.printStackTrace();
			(Toast.makeText(this,
					"No se ha encontrado la aplicaci�n de Google Maps",
					Toast.LENGTH_LONG)).show();
		}
	}

	/**
	 * Método que empieza una llamada al número.
	 * 
	 * @param nameNumber
	 *            Número al que se desea llamar.
	 */
	private final void makeCall(String nameNumber) {
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + nameNumber));
		startActivity(callIntent);
	}

	/**
	 * Método que abre el sitio del url.
	 * 
	 * @param url
	 *            Url que al que se desea ingresar.
	 */
	private final void openSite(String url) {
		Uri uri;
		try {
			uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		} catch (Exception e1) {
			Log.e(TAG, e1.toString());
			e1.printStackTrace();
		}
	}

	/**
	 * Método que agrega este advertiser a los contactos del dispositivo.
	 */
	@SuppressWarnings("deprecation")
	private void addToContacts() {
		Advertiser a = advertiserToShow;
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		int rawContactInsertIndex = ops.size();

		ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_TYPE, null)
				.withValue(RawContacts.ACCOUNT_NAME, null).build());

		// ------------------------Names ---------------------------------
		ops.add(ContentProviderOperation
				.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID,
						rawContactInsertIndex)
				.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
				.withValue(StructuredName.DISPLAY_NAME, a.getNombre()).build());

		// ---------------------------- Number ----------------------------
		ops.add(ContentProviderOperation
				.newInsert(Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
						rawContactInsertIndex)
				.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
				.withValue(Phone.NUMBER, a.getTelefono().get(0))
				.withValue(Phone.TYPE, Phone.TYPE_COMPANY_MAIN).build());

		try {

			getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			AlertDialog alertDialog = new AlertDialog.Builder(
					ShowAdvertiser.this).create();
			alertDialog.setTitle("Agregado a Contactos");
			alertDialog.setMessage("Se agreg� " + advertiserToShow.getNombre()
					+ " a contactos");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					ShowAdvertiser.this.finish();
				}
			});
			alertDialog.show();
		} catch (RemoteException e) {
			Log.e(TAG, "Error RemoteException");
			Log.e(TAG, e.toString());
		} catch (OperationApplicationException e) {
			Log.e(TAG, "Error! OperationApplicationException");
			Log.e(TAG, e.toString());
		}

	}

	/**
	 * Método que prepara los elementos de la interfaz
	 */
	private void setupViews() {

		loadCoupons = (ProgressBar) findViewById(R.id.loadingAdvCoupons);
		loadCoupons.setVisibility(View.INVISIBLE);

		// Setup Business Image
		iv = (ImageView) findViewById(R.id.negocio_logo);

		// Image View si es destacado
		destacado = (ImageView) findViewById(R.id.logo_destacados);

		// Setup Business Name
		nombreEmpresa = (TextView) findViewById(R.id.nombre_empresa);

		// Setup Business Description
		descripcionEmpresa = (TextView) findViewById(R.id.descripcion_empresa);

		// Setup Main Business Address
		direccionEmpresa = (TextView) findViewById(R.id.direccion_empresa);

		// SETUP Business Branches (if it has)
		rl = (LinearLayout) findViewById(R.id.linear_layout_si);
		suc = (TextView) findViewById(R.id.sucursales_empresa);

		// SETUP BUSINESS CONTACT INFO
		ll = (LinearLayout) findViewById(R.id.linear_layout_contacto);

		// YoutubeVideo
		youtubeBlue = (TextView) findViewById(R.id.youtube);
		youtube = (TextView) findViewById(R.id.ver_video);

		// Promociones del club
		promocionBlue = (TextView) findViewById(R.id.promocion_club);
		promocion = (TextView) findViewById(R.id.promocion);

		// Setup Business Galleries
		gal = (TextView) findViewById(R.id.galerias);
		galName = (TextView) findViewById(R.id.nombre_galeria);

		// Setup Coupons
		coupon = (TextView) findViewById(R.id.adv_coupons);
		couponLink = (TextView) findViewById(R.id.link_adv_coupons);

		// Setup Coupons
		couponClub = (TextView) findViewById(R.id.adv_coupons_club);
		couponLinkClub = (TextView) findViewById(R.id.link_adv_coupons_club);

		// boton de favoritos
		favs = (Button) findViewById(R.id.agregar_favs);

		// boton de agregar contactos
		addContacts = (Button) findViewById(R.id.agregar_contectos);
		addContacts.setBackgroundResource(R.drawable.contacto);

		addContacts.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addToContacts();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		loadCoupons.setVisibility(View.INVISIBLE);
		com.facebook.Settings.publishInstallAsync(getApplicationContext(),
				getString(R.string.facebook_app_id));
	}

	/**
	 * Método que usa la información descargada para llenar los elementos de la
	 * interfaz de la base de datos.
	 */
	private void fill() {
		// Preferencias para obtener el país y el estado de si iniciá o no
		// sesión.
		sp = PreferenceManager.getDefaultSharedPreferences(ShowAdvertiser.this);

		// se guarda el estado de la sesión
		String log = sp.getString("estado", "NotLoggedIn");

		// se descarga el icono del negocio
		int position = 0;
		try {
			iv.setImageBitmap(BitmapFactory.decodeByteArray(
					advertiserToShow.getImgSrc(), 0,
					advertiserToShow.getImgSrc().length));
		} catch (NullPointerException npe) {
			Log.e(TAG, npe.toString());
			iv.setBackgroundColor(Color.WHITE);
		}

		// se revisa si el advertiser es parte del club
		boolean esDestacado = advertiserToShow.getFeatured();
		if (esDestacado != true) {
			destacado.setVisibility(View.INVISIBLE);
		}
		// en caso de que no sea parte del club, o de que no se haya iniciado la
		// sesión, no se muestra en caso de que la inicion no se haya iniciado,
		// o en caso de que no se sea parte del club.


			promocion.setText(advertiserToShow.getPromocion());

		// se muestra el nombre de la empresa
		String name = advertiserToShow.getNombre();
		if (name.equals("") || name.equals(null)) {
			nombreEmpresa.setVisibility(View.INVISIBLE);
		} else {
			nombreEmpresa.setText(name);
		}

		// se muestra la descripción de la empresa
		String descripcion = advertiserToShow.getDescripcion();
		if (descripcion.equals(null) || descripcion.equals("")) {
			descripcionEmpresa.setVisibility(View.INVISIBLE);
		} else {
			descripcionEmpresa.setText(descripcion);
		}

		// Se muestra la dirección de la empresa
		String direccion = advertiserToShow.getDireccion();
		if (direccion.equals(null) || direccion.equals("")) {
			direccionEmpresa.setVisibility(View.INVISIBLE);
		} else {
			direccionEmpresa.setText(direccion);
			final double x = advertiserToShow.getPosx();
			final double y = advertiserToShow.getPosy();

			if (x != 0.0 && y != 0.0) {
				direccionEmpresa.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						sendInfo(x, y, advertiserToShow.getNombre(),
								advertiserToShow.getDireccion());
					}
				});
			}
		}

		// se obtienen las sucursales del negocio
		String sucursalId = advertiserToShow.getId();
		boolean tieneSucursales = sDAO.tieneSucursales(sucursalId);
		if (tieneSucursales == false) {
			suc.setVisibility(View.GONE);

		} else {
			suc.setVisibility(View.VISIBLE);

			final ArrayList<String> sucursales = sDAO
					.getStringSucursales(sucursalId);

			final ArrayList<Sucursal> arraySucursales = sDAO
					.getSucursales(sucursalId);
			for (int i = 0; i < sucursales.size(); i++) {
				final Sucursal s = arraySucursales.get(i);
				// Image View Maps
				ImageView iv1 = new ImageView(this);
				iv1.setId(3);
				iv1.setImageResource(R.drawable.local_icon);

				// Text View Directions
				TextView tv = new TextView(this);
				tv.setId(2);
				tv.setTextSize(18);
				tv.setText(sucursales.get(i));
				tv.setTextColor(Color.BLACK);

				tv.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						sendInfo(s.getPointX(), s.getPointY(),
								advertiserToShow.getNombre(), s.getAddress());

					}
				});

				// RelativeLayout
				RelativeLayout rlEj = new RelativeLayout(this);

				RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				p.addRule(RelativeLayout.ALIGN_BOTTOM, suc.getId());

				// Params ImageView Next
				RelativeLayout.LayoutParams lpEj = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpEj.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lpEj.addRule(RelativeLayout.CENTER_VERTICAL);
				lpEj.setMargins(5, 5, 5, 5);

				// Image View Next
				ImageView iv2 = new ImageView(this);
				iv2.setId(1);
				iv2.setImageResource(R.drawable.next_icon);

				// Params TextView
				RelativeLayout.LayoutParams lptv = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lptv.addRule(RelativeLayout.LEFT_OF, iv2.getId());
				lptv.addRule(RelativeLayout.RIGHT_OF, iv1.getId());
				lptv.setMargins(5, 10, 5, 10);

				// Params ImageView Maps
				RelativeLayout.LayoutParams lpiv1 = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				// lpiv1.addRule(RelativeLayout.LEFT_OF, tv.getId());
				lpiv1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				lpiv1.addRule(RelativeLayout.CENTER_VERTICAL);
				lpiv1.setMargins(5, 5, 5, 5);

				tv.setLines(4);

				rlEj.addView(iv2, lpEj);

				rlEj.addView(tv, lptv);

				rlEj.addView(iv1, lpiv1);

				rl.addView(rlEj, i);
			}

		}

		// Se preparan los teléfonos a mostrar en caso de que los haya.
		if (advertiserToShow.getTelefono() != null) {
			for (int i = 0; i < advertiserToShow.getTelefono().size(); i++) {
				final int index = i;

				// ImageView NextIcon
				// Params ImageView Next
				RelativeLayout.LayoutParams lpEj = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpEj.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lpEj.addRule(RelativeLayout.CENTER_VERTICAL);
				lpEj.setMargins(5, 5, 5, 5);

				// Image View Next
				ImageView iv2 = new ImageView(this);
				iv2.setId(1);
				iv2.setImageResource(R.drawable.next_icon);

				// ImageView PhoneIcon
				ImageView iv1 = new ImageView(this);
				iv1.setId(3);
				iv1.setImageResource(R.drawable.phone_icon);

				// Params ImageView PhoneIcon
				RelativeLayout.LayoutParams lpiv1 = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpiv1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				lpiv1.addRule(RelativeLayout.CENTER_VERTICAL);
				lpiv1.setMargins(5, 5, 5, 5);

				// TextView Phone Number
				TextView tv = new TextView(this);
				tv.setId(2);
				tv.setTextSize(18);
				tv.setText(advertiserToShow.getTelefono().get(i));
				tv.setTextColor(Color.BLACK);

				// Params TextView
				RelativeLayout.LayoutParams lptv = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lptv.addRule(RelativeLayout.LEFT_OF, iv2.getId());
				lptv.addRule(RelativeLayout.RIGHT_OF, iv1.getId());
				lptv.setMargins(5, 10, 5, 10);
				lptv.addRule(RelativeLayout.CENTER_VERTICAL);

				tv.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						makeCall(advertiserToShow.getTelefono().get(index));

					}
				});

				RelativeLayout rlEj = new RelativeLayout(this);

				tv.setLines(1);

				rlEj.addView(iv2, lpEj);

				rlEj.addView(tv, lptv);

				rlEj.addView(iv1, lpiv1);

				ll.addView(rlEj, i);

			}

			// se prepara el link al video de youtube en caso de que lo tenga
			final String videoLink = advertiserToShow.getYoutubVideo();
			if (videoLink.equals(null) || videoLink.equals("")) {
				youtubeBlue.setVisibility(View.INVISIBLE);
				youtube.setVisibility(View.INVISIBLE);
			} else {
				youtube.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						openSite(videoLink);
					}
				});
			}

			// en caso de que se tenga galería de imágenes, se muestra
			boolean tieneGalerias = advertiserDao.hasGallery(advertiserToShow
					.getId());
			if (tieneGalerias == true) {
				gal.setVisibility(View.VISIBLE);
				galName.setVisibility(View.VISIBLE);
				galName.setTextSize(23);
				galName.setText("Ver Galería: "
						+ advertiserDao.getGalleryName(advertiserToShow.getId()));
				galName.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						SharedPreferences mySharedPreference = getSharedPreferences(
								"galeriaId", 0);
						Editor editor = mySharedPreference.edit();
						editor.putString("galeriaId", advertiserDao
								.getGalleryId(advertiserToShow.getId()));
						editor.commit();
						intent = new Intent(ShowAdvertiser.this,
								GalleryView.class);
						ShowAdvertiser.this.startActivity(intent);

					}
				});
			} else {
				gal.setVisibility(View.GONE);
				galName.setVisibility(View.GONE);

			}

		}

		// Setup as many emails the business has
		Log.d(TAG, "setup email");
		if (advertiserToShow.getEmail() != null) {
			for (int i = 0; i < advertiserToShow.getEmail().size(); i++) {

				// Image View Mail
				ImageView iv1 = new ImageView(this);
				iv1.setId(1);
				iv1.setImageResource(R.drawable.mail_icon);

				// Params mail icon
				RelativeLayout.LayoutParams lpiv1 = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpiv1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				lpiv1.addRule(RelativeLayout.CENTER_VERTICAL);
				lpiv1.setMargins(5, 5, 5, 5);

				// ImageView NextIcon
				ImageView iv2 = new ImageView(this);
				iv2.setId(3);
				iv2.setImageResource(R.drawable.next_icon);

				// Params NextIcon
				RelativeLayout.LayoutParams lpEj = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpEj.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lpEj.addRule(RelativeLayout.CENTER_VERTICAL);
				lpEj.setMargins(5, 5, 5, 5);

				// TextView Mail
				TextView tv = new TextView(this);
				tv.setId(2);
				tv.setTextSize(18);
				tv.setText(advertiserToShow.getEmail().get(i));
				tv.setTextColor(Color.BLACK);
				final int index = i;
				tv.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						Intent email = new Intent(Intent.ACTION_SEND);
						email.putExtra(Intent.EXTRA_EMAIL, new String[] {
								advertiserToShow.getEmail().get(index), "" });
						email.putExtra(Intent.EXTRA_SUBJECT, "Contacto");
						email.putExtra(Intent.EXTRA_TEXT, "message");
						email.setType("message/rfc822");
						startActivity(Intent.createChooser(email,
								"Choose an Email client :"));
					}
				});

				// Params TextView mail
				RelativeLayout.LayoutParams lptv = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lptv.addRule(RelativeLayout.LEFT_OF, iv2.getId());
				lptv.addRule(RelativeLayout.RIGHT_OF, iv1.getId());
				lptv.setMargins(5, 10, 5, 10);
				lptv.addRule(RelativeLayout.CENTER_VERTICAL);

				RelativeLayout rlEj = new RelativeLayout(this);

				tv.setLines(1);

				rlEj.addView(iv2, lpEj);

				rlEj.addView(tv, lptv);

				rlEj.addView(iv1, lpiv1);

				ll.addView(rlEj, i);

				position = i + 1;

			}
		}

		// Setup the business webpage
		final String webPage = advertiserToShow.getSitioWeb();
		try {
			if (!webPage.equals("")) {
				RelativeLayout rll = new RelativeLayout(this);

				// ImageView browser
				ImageView iv1 = new ImageView(this);
				iv1.setId(1);
				iv1.setImageResource(R.drawable.browser_icon);

				// Params browser icon
				RelativeLayout.LayoutParams lpiv1 = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpiv1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				lpiv1.addRule(RelativeLayout.CENTER_VERTICAL);
				lpiv1.setMargins(5, 5, 5, 5);

				// ImageView NextIcon
				ImageView iv2 = new ImageView(this);
				iv2.setId(2);
				iv2.setImageResource(R.drawable.next_icon);

				// Params NextIcon
				RelativeLayout.LayoutParams lpEj = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpEj.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lpEj.addRule(RelativeLayout.CENTER_VERTICAL);
				lpEj.setMargins(5, 5, 5, 5);

				// TextView browser
				TextView tvweb = new TextView(this);
				tvweb.setId(3);
				tvweb.setTextSize(18);
				tvweb.setText(webPage);
				tvweb.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						openSite(webPage);
					}
				});

				// Params TextView browser
				RelativeLayout.LayoutParams lptv = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lptv.addRule(RelativeLayout.LEFT_OF, iv2.getId());
				lptv.addRule(RelativeLayout.RIGHT_OF, iv1.getId());
				lptv.setMargins(5, 10, 5, 10);
				lptv.addRule(RelativeLayout.CENTER_VERTICAL);

				tvweb.setLines(1);

				rll.addView(iv2, lpEj);

				rll.addView(tvweb, lptv);

				rll.addView(iv1, lpiv1);

				ll.addView(rll, position);

				position++;
			}
		} catch (NullPointerException e) {
			Log.e(TAG, e.toString() + "website");
		}

		// Setup Business Facebook page
		final String facebook = advertiserToShow.getFacebook();

		try {
			if (!facebook.equals("")) {
				// ImageView facebook
				ImageView iv1 = new ImageView(this);
				iv1.setId(1);
				iv1.setImageResource(R.drawable.facebook_icon);

				// Params fb icon
				RelativeLayout.LayoutParams lpiv1 = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpiv1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				lpiv1.addRule(RelativeLayout.CENTER_VERTICAL);
				lpiv1.setMargins(5, 5, 5, 5);

				// ImageView NextIcon
				ImageView iv2 = new ImageView(this);
				iv2.setId(3);
				iv2.setImageResource(R.drawable.next_icon);

				// Params NextIcon
				RelativeLayout.LayoutParams lpEj = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpEj.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lpEj.addRule(RelativeLayout.CENTER_VERTICAL);
				lpEj.setMargins(5, 5, 5, 5);

				// TextView facebook
				TextView tvFacebook = new TextView(this);
				tvFacebook.setId(2);
				tvFacebook.setTextSize(18);
				tvFacebook.setText("Facebook");

				tvFacebook.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						openSite(facebook);
					}
				});

				// Params TextView facebook
				RelativeLayout.LayoutParams lptv = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lptv.addRule(RelativeLayout.LEFT_OF, iv2.getId());
				lptv.addRule(RelativeLayout.RIGHT_OF, iv1.getId());
				lptv.setMargins(5, 10, 5, 10);
				lptv.addRule(RelativeLayout.CENTER_VERTICAL);

				RelativeLayout rlEj = new RelativeLayout(this);

				tvFacebook.setLines(1);

				rlEj.addView(iv2, lpEj);

				rlEj.addView(tvFacebook, lptv);

				rlEj.addView(iv1, lpiv1);

				ll.addView(rlEj, position);

				position++;
			}
		} catch (NullPointerException npe) {
			// donothing
			Log.e(TAG, npe.toString() + "facebook");
		}

		// Setup Business Twitter
		try {
			final String twitter = advertiserToShow.getTwitter();
			if (twitter.equals("") == false) {

				// ImageView Twitter
				ImageView iv1 = new ImageView(this);
				iv1.setId(1);
				iv1.setImageResource(R.drawable.twitter_icon);

				// Params twitter icon
				RelativeLayout.LayoutParams lpiv1 = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpiv1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				lpiv1.addRule(RelativeLayout.CENTER_VERTICAL);
				lpiv1.setMargins(5, 5, 5, 5);

				// ImageView NextIcon
				ImageView iv2 = new ImageView(this);
				iv2.setId(3);
				iv2.setImageResource(R.drawable.next_icon);

				// Params NextIcon
				RelativeLayout.LayoutParams lpEj = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpEj.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lpEj.addRule(RelativeLayout.CENTER_VERTICAL);
				lpEj.setMargins(5, 5, 5, 5);

				// TextView twitter
				TextView tvTwitter = new TextView(this);
				tvTwitter.setId(2);
				tvTwitter.setTextSize(18);
				tvTwitter.setText("Twitter");

				tvTwitter.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						openSite(twitter);
					}
				});

				// Params TextView twitter
				RelativeLayout.LayoutParams lptv = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lptv.addRule(RelativeLayout.LEFT_OF, iv2.getId());
				lptv.addRule(RelativeLayout.RIGHT_OF, iv1.getId());
				lptv.setMargins(5, 10, 5, 10);
				lptv.addRule(RelativeLayout.CENTER_VERTICAL);

				RelativeLayout rlEj = new RelativeLayout(this);

				tvTwitter.setLines(1);

				rlEj.addView(iv2, lpEj);

				rlEj.addView(tvTwitter, lptv);

				rlEj.addView(iv1, lpiv1);

				ll.addView(rlEj, position);

			}
		} catch (NullPointerException npe) {
			// doNothing
			Log.e(TAG, npe.toString() + "twitter");
		}

		// se revisa si tiene cupones
		int tieneCupones = advertiserDao.advertiserCoupons(advertiserToShow
				.getId());

		// si tiene cupones, y si esta loggeado, se muestran los cupones
		if (tieneCupones > 0
				&& (log.equals("Logged") || log.equals("LoggedClub"))) {
			coupon.setVisibility(View.VISIBLE);
			couponLink.setVisibility(View.VISIBLE);
			couponLink.setTextSize(23);
			couponLink.setText("Ver Cupones");
			couponLink.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					loadCoupons.setVisibility(View.VISIBLE);
					Intent i = new Intent(ShowAdvertiser.this, CuponesAdv.class);
					i.putExtra("advId", advertiserToShow.getId());
					i.putExtra("advName", advertiserToShow.getNombre());
					i.putExtra("isClub", false);
					ShowAdvertiser.this.startActivity(i);

				}
			});
		} else {
			coupon.setVisibility(View.GONE);
			couponLink.setVisibility(View.GONE);
		}

		// se revisa que se tengan cupones del club
		int tieneCuponesClub = advertiserDao
				.advertiserCouponsClub(advertiserToShow.getId());

		// si se iniciá con la sesión adecuada, se muestra los cupones del club
		if (tieneCuponesClub > 0 && log.equals("LoggedClub")) {
			couponClub.setVisibility(View.VISIBLE);
			couponLinkClub.setVisibility(View.VISIBLE);
			couponLinkClub.setTextSize(23);
			couponLinkClub.setText("Ver Cupones Club");
			couponLinkClub.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					loadCoupons.setVisibility(View.VISIBLE);
					Intent i = new Intent(ShowAdvertiser.this, CuponesAdv.class);
					i.putExtra("advId", advertiserToShow.getId());
					i.putExtra("advName", advertiserToShow.getNombre());
					i.putExtra("isClub", true);
					ShowAdvertiser.this.startActivity(i);

				}
			});
		} else {
			couponClub.setVisibility(View.GONE);
			couponLinkClub.setVisibility(View.GONE);
		}

		/*
		 * Se obtienen el nombre del advertiser y el país, se guardaron en las
		 * variables para poder probarlos.
		 */
		final TodoManagerApplication ama = (TodoManagerApplication) getApplication();
		String nombreTemporal = advertiserToShow.getNombre();


		boolean isInFavs = fd.isInFavoritos(nombreTemporal);
		if (isInFavs == true) {
			favs.setBackgroundResource(R.drawable.favorito);
			favs.setOnClickListener(new View.OnClickListener() {

				@SuppressWarnings("deprecation")
				public void onClick(View v) {
					fd.removeFromFavoritos(advertiserToShow.getNombre());
					AlertDialog alertDialog = new AlertDialog.Builder(
							ShowAdvertiser.this).create();
					alertDialog.setTitle("Se elimino de favoritos");
					alertDialog.setMessage("Se elimino "
							+ advertiserToShow.getNombre() + " de favoritos");
					alertDialog.setButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									ShowAdvertiser.this.finish();
								}
							});
					alertDialog.show();
					Log.d(TAG, "Eliminado de favoritos");

				}
			});
		} else {
			favs.setBackgroundResource(R.drawable.no_favorito);
			favs.setOnClickListener(new View.OnClickListener() {

				@SuppressWarnings("deprecation")
				public void onClick(View v) {

                    String paisTemp = "";
                    try {
                         paisTemp = getIntent().getStringExtra("pais");
                    }catch (Exception e){
                         paisTemp = ama.getCountry();
                    }

                    fd.addToFavoritos(advertiserToShow, paisTemp);
					AlertDialog alertDialog = new AlertDialog.Builder(
							ShowAdvertiser.this).create();
					alertDialog.setTitle("Agregado a Favoritos");
					alertDialog.setMessage("Se agrego "
							+ advertiserToShow.getNombre() + " a favoritos");
					alertDialog.setButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									ShowAdvertiser.this.finish();
								}
							});
					alertDialog.show();
					Log.d(TAG, "Agregado a favoritos");
				}
			});

		}

	}

	/**
	 * Async Task que se encarga de descargar la información del advertiser,
	 * mientras que en la pantalla principal se muestra un progress dialog que
	 * dice "cargando".
	 * 
	 * @author Publysorpresas
	 * 
	 */
	private class PrepareStuff extends AsyncTask<String, Integer, Void> {

		/** El contexto donde se va a cargar el progress dialog **/
		private Context ctx;
		private ProgressDialog pd = null;

		/**
		 * Método que se ejecuta cuando el async task termina, se ejecuta en el
		 * thread principal.
		 */
		@Override
		protected void onPostExecute(Void result) {
			// se llena la interfaz con la información descargada
			fill();

			// se cancela el progress dialog
			pd.cancel();
			super.onPostExecute(result);
		}

		/**
		 * Método que se ejecuta antes de que empiece el async task, en eeste
		 * caso se prepara el progress dialog y se muestra.
		 */
		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(ctx);
			pd.setTitle("Cargando");
			pd.setMessage("Descargando información");
			pd.setIndeterminate(false);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
			super.onPreExecute();
		}

		/**
		 * Constructor, recibe como parámetro el contexto donde se va a cargar
		 * el progress dialog.
		 * 
		 * @param c
		 *            El contexto donde se cargara el progress dialog.
		 */
		public PrepareStuff(Context c) {
			ctx = c;
		}

		/**
		 * Lo que se hace en un thread aparte mientras el thread principal
		 * muestra el progress dialog, en este caso, se hace la descarga de
		 * información.
		 */
		@Override
		protected Void doInBackground(String... arg0) {
			// la descarga de la informacion
			advertiserToShow = advertiserDao.find(advertiser, arg0[0]);
			return null;
		}

	}

}
