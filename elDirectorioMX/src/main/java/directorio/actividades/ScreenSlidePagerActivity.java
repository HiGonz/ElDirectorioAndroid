package directorio.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.androidquery.AQuery;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import directorio.database.DBConnection;

public class ScreenSlidePagerActivity extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;


    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_slide_rifa);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        AQuery aq = new AQuery(this);

        aq.id(R.id.skipButton).clicked(this, "skipThis");
        aq.id(R.id.continueAction).clicked(this, "skipThis");
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            Intent mainIntent = new Intent().setClass(ScreenSlidePagerActivity.this, Search.class);
            startActivity(mainIntent);
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     *
     * Click de boton de bienvenida
     *
     */
    public void cliCkaoTio(View v) {
        mPager.setCurrentItem(1);
    }

    public void registerClick(View view) throws SQLException {

        DBConnection miConexion;
        miConexion=DBConnection.getInstance();

        if(miConexion!=null)
        {
        Log.d("Conection", "Yeii");
        }

        EditText Nombre = (EditText) findViewById(R.id.entry);
        EditText Direccion = (EditText) findViewById(R.id.entry2);
        EditText Phone = (EditText) findViewById(R.id.entry3);
        EditText Email = (EditText) findViewById(R.id.entry4);

        Random rand = new Random();
        int Folio = rand.nextInt((20000-0) +1) + 1;
        String formatted = String.format("%05d",Folio);

        String insertTableSQL = "INSERT INTO RIFA"
                + "(Name, Direccion, Phone, Email, Folio) VALUES"
                + "(?,?,?,?,?)";
        PreparedStatement preparedStatement = miConexion.conectar().prepareStatement(insertTableSQL);
        preparedStatement.setString(1, Nombre.getText().toString());
        preparedStatement.setString(2, Direccion.getText().toString());
        preparedStatement.setString(3, Phone.getText().toString());
        preparedStatement.setString(4, Email.getText().toString());
        preparedStatement.setString(5, formatted);

        preparedStatement.executeUpdate();

        mPager.setCurrentItem(2);

    }

    public void skipThis() {
        Intent mainIntent = new Intent().setClass(ScreenSlidePagerActivity.this, Search.class);
        startActivity(mainIntent);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return Rifa.newInstance(0, "Page # 1");
                case 1: // Fragment # 0 - This will show SecondFragment
                    return Rifa2.newInstance(1, "Page # 2");
                case 2: // Fragment # 1 - This will show ThirdFragment
                    return Rifa3.newInstance(2, "Page # 3");
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}