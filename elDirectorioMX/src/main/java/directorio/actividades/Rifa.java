package directorio.actividades;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by diseno2 on 21/07/2015.
 */


public class Rifa extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_rifa, container, false);

        return rootView;
    }

    // newInstance constructor for creating fragment with arguments
    public static Rifa newInstance(int page, String title) {
        Rifa fragmentFirst = new Rifa();
        Bundle args = new Bundle();
        args.putInt("1", page);
        args.putString("Bienvenida", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;

    }
}