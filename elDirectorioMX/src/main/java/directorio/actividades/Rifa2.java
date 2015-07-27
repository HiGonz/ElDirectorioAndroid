package directorio.actividades;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by diseno2 on 21/07/2015.
 */


public class Rifa2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_rifa2, container, false);

        return rootView;
    }   public static Rifa2 newInstance(int page, String title) {
        Rifa2 fragmentSecond = new Rifa2();
        Bundle args = new Bundle();
        args.putInt("2", page);
        args.putString("Registro", title);
        fragmentSecond.setArguments(args);
        return fragmentSecond;

    }

}