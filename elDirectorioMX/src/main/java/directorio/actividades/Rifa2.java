package directorio.actividades;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.sql.Connection;

import directorio.database.DBConnection;

import static java.sql.DriverManager.getConnection;


/**
 * Created by diseno2 on 21/07/2015.
 */


public class Rifa2 extends Fragment {

    Connection conn = DBConnection.getInstance().getConnection();
    EditText mEditNombre;
    EditText mEditDirección;
    EditText mEditEmail;
    EditText mEditPhone;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_rifa2, container, false);


        return rootView;
    }



    public static Rifa2 newInstance(int page, String title) {
        Rifa2 fragmentSecond = new Rifa2();
        Bundle args = new Bundle();
        args.putInt("2", page);
        args.putString("Registro", title);
        fragmentSecond.setArguments(args);
        return fragmentSecond;

    }



}