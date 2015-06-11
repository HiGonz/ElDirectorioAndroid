package directorio.actividades;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.app.SherlockActivity;
//import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

//import directorio.facebook.FacebookConnector;
//import directorio.facebook.SessionEvents;
import com.facebook.share.model.ShareModel;

/**
 * Clase que se encarga de compartir en las redes sociales.
 * 
 * @author NinjaDevelop
 * 
 */
public class Comparte extends ActionBarActivity implements
		ISideNavigationCallback {

	private SideNavigationView comparteSideNavView;

	// Facebook
	private static final String FACEBOOK_APPID = "	412621975483931";
	private static final String FACEBOOK_PERMISSION = "publish_stream";
	private static final String TAG = "Comparte";
	private static final String MSG = "He descargado la nueva app para Android ElDirectorio.mx\n Bájatela desde Google Play Store en Android. Es GRATIS!\n\nhttps://play.google.com/store/apps/details?id=directorio.actividades";

	//private final Handler mFacebookHandler = new Handler();
	//private FacebookConnector facebookConnector;

	final Runnable mUpdateFacebookNotification = new Runnable() {
		public void run() {
			Toast.makeText(getBaseContext(), "Mensaje de Facebook Enviado!",
					Toast.LENGTH_LONG).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comparte);
		setTitle("Comparte");
		Log.d(TAG, "Actividad " + TAG);

		// SideNavigationMenu
		comparteSideNavView = (SideNavigationView) findViewById(R.id.comparte_sidenavigationview);
		comparteSideNavView.setMenuItems(R.menu.side_navigation_menu);
		comparteSideNavView.setMenuClickCallback(this);

		// Buttons(Imagen/ImageView)
		ImageButton btnFB = (ImageButton) findViewById(R.id.btn_fb);
		//ImageButton btnTwitter = (ImageButton) findViewById(R.id.btn_twitter);
		//ImageButton btnMail = (ImageButton) findViewById(R.id.btn_mail);

		// ClickListeners
	/*	btnMail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_SUBJECT, "El Directorio");
				i.putExtra(
						Intent.EXTRA_TEXT,
						"Hola!\nLa totalmente nueva app Android ElDirectorio.mx ya sali�, B�jatela gratis!\n\nHaz click en el enlace de abajo para descargar la app desde Google Play Store de Android. Es GRATIS!\n\nhttps://play.google.com/store/apps/details?id=directorio.actividades");
				startActivity(Intent.createChooser(i,
						"Selecciona la aplicaci�n..."));
			}
		});*/

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
		customView.setText("Comparte");
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_USE_LOGO);
		getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Facebook



	//	facebookConnector = new FacebookConnector(FACEBOOK_APPID, this,
		//		getApplicationContext(), new String[] { FACEBOOK_PERMISSION });

		btnFB.setOnClickListener(new OnClickListener() {
			@Override
                    public void onClick(View v){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "¡Hola! Los invito a descargar la aplicación de El DirectorioMx.\nDescárgala gratis para Android" +
                    "en http://goo.gl/8tkdHx y para iOS en http://goo.gl/ULgWPA.");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
		}});

		// Twitter - Mandar a mail (de mientras)
		/*btnTwitter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri
						.parse("https://twitter.com/EldirectorioMx"));
				startActivity(i);
			}
		});*/
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		float downXvalue = 0;
		float downYvalue = 0;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// store the X value when the user's finger was pressed down
			downXvalue = event.getX();
			downYvalue = event.getY();
			break;

		case MotionEvent.ACTION_UP:
			// Get the X value when the user released his/her finger
			float currentX = event.getX();
			float currentY = event.getY();
			// check if horizontal or vertical movement was bigger

			if (Math.abs(downXvalue - currentX) > Math.abs(downYvalue
					- currentY)) {
				Log.v(TAG, "x");
				// going backwards: pushing stuff to the right
				if (downXvalue < currentX) {
					Log.v(TAG, "right");
					this.comparteSideNavView.toggleMenu();
				}
			}
			break;
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			comparteSideNavView.toggleMenu();
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
		if (comparteSideNavView.isShown()) {
			comparteSideNavView.hideMenu();
		} else {
			Intent intent = new Intent(Comparte.this, Search.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
	//	com.facebook.Settings.publishInstallAsync(getApplicationContext(),
	//			getString(R.string.facebook_app_id));
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_comparte, menu);
		return true;
	}

	// Facebook Overrides
	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	//	facebookConnector.getFacebook().authorizeCallback(requestCode,
	//			resultCode, data);
	}

	// Facebook Methods
	private String getFacebookMsg() {
		return MSG;
	}

	@SuppressWarnings("deprecation")
	public void postMessage() {

	/*	if (facebookConnector.getFacebook().isSessionValid()) {

			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						postMessageInThread();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						Toast.makeText(getBaseContext(),
								"No se ha mandado el mensaje de Facebook",
								Toast.LENGTH_LONG).show();
						break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Deseas enviar el mensaje de Facebook?")
					.setPositiveButton("Si", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).show();

		} else {
			final Context contxt = this;
			SessionEvents.AuthListener listener = new SessionEvents.AuthListener() {

				@Override
				public void onAuthSucceed() {
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								postMessageInThread();
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								Toast.makeText(
										getBaseContext(),
										"No se ha mandado el mensaje de Facebook",
										Toast.LENGTH_LONG).show();
								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(
							contxt);
					builder.setMessage("Est�s Seguro?")
							.setPositiveButton("Si", dialogClickListener)
							.setNegativeButton("No", dialogClickListener)
							.show();

				}

				@Override
				public void onAuthFail(String error) {

				}
			};
			SessionEvents.addAuthListener(listener);
			facebookConnector.login();
		}*/
	}

	private void postMessageInThread() {
		/*Thread t = new Thread() {
			public void run() {

				try {
					facebookConnector.postMessageOnWall(getFacebookMsg());
					mFacebookHandler.post(mUpdateFacebookNotification);
				} catch (Exception ex) {
					Log.e(TAG, "Error sending msg", ex);
				}
			}
		};
		t.start();*/
	}

}