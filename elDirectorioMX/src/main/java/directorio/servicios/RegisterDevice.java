package directorio.servicios;

import android.app.IntentService;
import android.content.Intent;

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

/**
 * Created by juancarlos on 4/03/14.
 */
public class RegisterDevice extends IntentService {

    public  RegisterDevice(){
        super("Register");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String uuid = intent.getStringExtra("uuid");
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://173.193.3.218/NotificationService.svc/guardaruuid");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("UUID",uuid));
        try{
            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse resp = httpclient.execute(httppost);
            HttpEntity ent = resp.getEntity();/*y obtenemos una respuesta*/
            String responseText = EntityUtils.toString(ent);
            System.out.println("Aca chido: "+ responseText);
         }catch (Exception e){
            e.printStackTrace();
        }
    }
}
