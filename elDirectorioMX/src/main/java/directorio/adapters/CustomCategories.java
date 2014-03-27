package directorio.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import directorio.actividades.R;

/**
 * Custom BaseAdapter que se encarga de mostrar las categorías principales con
 * sus respectivos logos.
 * 
 * @author NinjaDevelop
 * 
 */
public class CustomCategories extends BaseAdapter {

	private LayoutInflater li;
	private ArrayList<String> adds = new ArrayList<String>();
	private final Context con;

	public CustomCategories(Context context, ArrayList<String> addss) {
		con = context;
		li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (addss != null)
			adds = addss;
	}

	public int getCount() {
		return adds.size();
	}

	public Object getItem(int position) {
		return adds.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressLint({ "ParserError", "ParserError", "ParserError" })
	public View getView(int position, View convertView, ViewGroup parent) {
		// current view object
		View v = convertView;

		final String cat = adds.get(position);

		if (v == null) {
			v = li.inflate(R.layout.adapters_custom_categories, null);
		}

		final ImageView mLogo = (ImageView) v.findViewById(R.id.imagendest);

		if (cat.equals("Bancos")) {
			mLogo.setImageDrawable(v.getResources().getDrawable(
					R.drawable.banco));// con.getResources().getDrawable(R.drawable.banco));
		} else if (cat.equals("Farmacias")) {
			mLogo.setImageDrawable(con.getResources().getDrawable(
					R.drawable.farmacias));
		} else if (cat.equals("Hospitales")) {
			mLogo.setImageDrawable(con.getResources().getDrawable(
					R.drawable.hospitales));
		} else if (cat.equals("Hoteles")) {
			mLogo.setImageDrawable(con.getResources().getDrawable(
					R.drawable.hoteles));
		} else if (cat.equals("Médicos")) {
			mLogo.setImageDrawable(con.getResources().getDrawable(
					R.drawable.medicos));
		} else if (cat.equals("Restaurantes y Bares")) {
			mLogo.setImageDrawable(con.getResources().getDrawable(
					R.drawable.restaurantes));
		} else if (cat.equals("Servicios")) {
			mLogo.setImageDrawable(con.getResources().getDrawable(
					R.drawable.servicios));
		} else if (cat.equals("Todas las categorias")) {
			mLogo.setImageDrawable(con.getResources().getDrawable(
					R.drawable.espacioblanco));
		}else {
			mLogo.setImageDrawable(con.getResources().getDrawable(
					R.drawable.espacioblanco));
		}

		final TextView nameTv = (TextView) v.findViewById(R.id.linea1);
		nameTv.setText(cat);
		nameTv.setTextSize(20);
		nameTv.setGravity(Gravity.CENTER_VERTICAL);
		nameTv.setTypeface(null, Typeface.BOLD);

		return v;
	}
}