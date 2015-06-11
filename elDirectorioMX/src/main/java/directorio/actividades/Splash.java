package directorio.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;


import java.util.Timer;
import java.util.TimerTask;


/*
*   Clase que se encarga de cargar la portada, ademas de hacer un delay de 5 segundos, esta es la clase con la que siempre se inicia.
 */
public class Splash extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        getActionBar().hide();

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
//Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//set content view AFTER ABOVE sequence (to avoid crash)

        setContentView(R.layout.activity_splash_screen);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent mainIntent = new Intent().setClass(Splash.this, Portada.class);
                startActivity(mainIntent);
                finish();//Destruimos esta activity para prevenit que el usuario retorne aqui presionando el boton Atras.
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 5000);
    }
}