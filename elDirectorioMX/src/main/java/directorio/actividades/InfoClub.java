package directorio.actividades;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

/**
 * Actividad que permite mostrar ifnormación sobre el club a quienes no están
 * registrados en el club.
 * 
 * @author Publysorpresas
 * 
 */
public class InfoClub extends SherlockActivity implements
		ISideNavigationCallback {

	private SideNavigationView sideNavigationInfoClub;
	private static final String TAG = "InfoClub";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_club);
		setTitle("Club El Directorio");
		Log.d(TAG, "Actividad " + TAG);

		sideNavigationInfoClub = (SideNavigationView) findViewById(R.id.info_club_sidenavigationview);
		sideNavigationInfoClub.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationInfoClub.setMenuClickCallback(this);

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
		customView.setText("Club El Directorio");
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_USE_LOGO);
		getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			sideNavigationInfoClub.toggleMenu();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	protected void onResume() {
		com.facebook.Settings.publishInstallAsync(getApplicationContext(),
				getString(R.string.facebook_app_id));
		super.onResume();
	}

	@Override
	public void onSideNavigationItemClick(int itemId) {
		ChangeLayout.layoutChange(itemId, this);
		finish();
	}

	@Override
	public void onBackPressed() {
		if (sideNavigationInfoClub.isShown()) {
			sideNavigationInfoClub.hideMenu();
		} else {
			super.onBackPressed();
		}
	}

}
