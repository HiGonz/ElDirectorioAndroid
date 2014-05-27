package directorio.actividades;

import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.content.ComponentName;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by juancarlos on 3/03/14.
 *  Recibidor que se encarga de despertar el dispositivo cuando recibe una notificación.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
        
    }
}