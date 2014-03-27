package directorio.servicios;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import directorio.actividades.GcmBroadcastReceiver;
import directorio.actividades.MainCategories;
import directorio.actividades.R;

/**
 * Created by juancarlos on 3/03/14.
 */
public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService(){
        super("732420872532");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // Para obtener el tipo de mensaje que pueda mandar el GCM
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging. MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //Errores son ignorados del tipo GCM
            } else if (GoogleCloudMessaging. MESSAGE_TYPE_DELETED.equals(messageType)) {
                //Ignoramos los mensajes de borrado
            } else if (GoogleCloudMessaging. MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Si es un mensaje normal mandamos la notificación
                sendNotification(extras.getString("message"));
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    /*
    *   Metodo que manda una notificación a la pantalla
    */
    private void sendNotification(String msg) {

        String[] arreglo = msg.split("&");
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,  new Intent(this, MainCategories.class), 0);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("El Directorio")
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(arreglo[0]))
                                .setContentText(msg).setSound(alarmSound);
        mBuilder.setContentIntent(contentIntent);


        try{
            if (arreglo[1].length() > 0){
                String url = arreglo[1];
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                PendingIntent intentopendiente = PendingIntent.getActivity(this, 0,i,0);
                mBuilder.addAction(R.drawable.ic_launcher,url,intentopendiente);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


}
