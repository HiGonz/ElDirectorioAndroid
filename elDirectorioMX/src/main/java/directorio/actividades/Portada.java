package directorio.actividades;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androidquery.AQuery;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by juancarlos on 2/05/14.
 */
public class Portada extends Activity {

    Portada por;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_portal);

        AQuery aq = new AQuery(this);
        String url = "http://m.eldirectorio.mx/portada.aspx?pa=1";
        WebView wb =aq.id(R.id.splash_portal_webview).getWebView() ;

        OverWebViewClient owec = new OverWebViewClient();

        wb.setWebViewClient(owec);
        wb.loadUrl(url);



        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent mainIntent = new Intent().setClass(Portada.this, PaisActivity.class);
                startActivity(mainIntent);
            }
        };
        Timer timer = new Timer();
        timer.schedule(task,8000);
    }

    private class OverWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}
