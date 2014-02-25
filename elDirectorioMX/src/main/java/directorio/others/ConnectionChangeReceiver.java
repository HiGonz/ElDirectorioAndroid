package directorio.others;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import directorio.actividades.NoNetworkActivity;

/**
 * BroadcastReceiver que se encarga de escuchar cada vez que hay un cambio en el
 * estado de la red.
 * 
 * @author Publysorpresas
 * 
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

	// Tags para el monitoreo de los estados
	private final static String TAG1 = "ActiveNetwork";
	private static final String TAG2 = "MobileNetwork";
	private static final String TAG3 = "NoNetwork";

	@Override
	public void onReceive(Context context, Intent intent) {
		// Se hace una referencia al conectivity manager.
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		/*
		 * La variabe activeNetInfo se encarga de monitorear el estado de la
		 * conexión actual, ya sea de wifi o móvil, en caso de que no se está
		 * conectado a nada, el valor de la variable será NULL.
		 */
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

		/*
		 * La variable mobNetInfo se encarga de monitorear el estado de la red
		 * móvil solamente. Si se está  conectado a la red del proveedor de datos
		 * esta variable va a estar en estado Connected(). En caso de que no se
		 * tenga plan de datos, o que el roaming está desactivado, el estado de
		 * esta variable será DISCONNECTED.
		 */
		NetworkInfo mobNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		// If's para monitorear por medio del log el estado de la red
		if (activeNetInfo != null) {
			Log.e(TAG1, activeNetInfo.getState().toString());
		}
		if (mobNetInfo != null) {
			Log.e(TAG2, mobNetInfo.getState().toString());
		}

		// Se revisa si una conexión se cayó
		if (intent.getExtras().getBoolean(
				ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {

			// si no hay red activa, y la red móvil está desconectada
			if (activeNetInfo == null && mobNetInfo.isConnected() == false) {
				Toast.makeText(context, "Error en la red!", Toast.LENGTH_SHORT)
						.show();
				Log.e(TAG3, "There's no network connectivity");
				// cuando se desconecta, empezar la de actividad de no network
				Intent i = new Intent(context, NoNetworkActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			}
		}

	}

}
