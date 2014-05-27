package directorio.servicios;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

import directorio.others.LocationParser;

/**
 * Created by juancarlos on 4/03/14.
 * Este es un intent service que se encarga de registrar el dispositivo con el servidor.
 */
public class RegisterDevice extends IntentService {

    public  RegisterDevice(){
        super("Register");
    }

    /*
    * Este es el metodo principal que manda a llamar el location manager, para obtener la ultima ubicación conocida
    * del dispositivo, obteniendo la ciudad en la que se encuentra, mandandolo junto con su UUID unico para poder mandar la notificación.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        LocationParser lp = new LocationParser();
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        String city;

        try{
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            lp.getAddress(latitude+"",longitude+"");
            city = lp.getCity();
            System.out.println("La ciudad fue: " + city);
        }catch (Exception e){
            city = "";
        }
        System.out.println("La ciudad fue: " + city);

        String uuid = intent.getStringExtra("uuid");
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://173.193.3.218/NotificationService.svc/guardaruuid");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("UUID",uuid));
        params.add(new BasicNameValuePair("ciudad",city));
        try{
            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse resp = httpclient.execute(httppost);
            HttpEntity ent = resp.getEntity();/*y obtenemos una respuesta*/
            String responseText = EntityUtils.toString(ent);
            System.out.println("Aca chido: "+ responseText);
         }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
