package directorio.actividades;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by diseno2 on 21/07/2015.
 */


public class Rifa3 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_rifa3, container, false);

        return rootView;
    } public static Rifa3 newInstance(int page, String title) {
        Rifa3 fragmentThird = new Rifa3();
        Bundle args = new Bundle();
        args.putInt("3", page);
        args.putString("Gracias", title);
        fragmentThird.setArguments(args);
        return fragmentThird;

    }

}
