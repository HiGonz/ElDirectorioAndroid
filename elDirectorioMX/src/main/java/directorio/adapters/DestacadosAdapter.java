package directorio.adapters;

import java.util.ArrayList;


import directorio.actividades.R;
import directorio.objetos.Advertiser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Custom Adapter creado para mostrar los destacados.
 * 
 * @author NinjaDevelop
 * 
 */
public class DestacadosAdapter extends BaseAdapter {
	private LayoutInflater li;
	private ArrayList<Advertiser> adds = new ArrayList<Advertiser>();

	public DestacadosAdapter(Context context, ArrayList<Advertiser> addss) {
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

		final Advertiser adver = adds.get(position);

		if (v == null) {
			v = li.inflate(R.layout.adapter_destacados, null);
		}
		// reference to the ImageView component
		//ImageView iconin = (ImageView) v.findViewById(R.id.icon);

		// reference to ImageView imagen
		ImageView mLogo = (ImageView) v.findViewById(R.id.imagendest);
		if (adver.getImgSrc() != null) {
			mLogo.setAdjustViewBounds(true);
			mLogo.setMaxHeight(100);
			mLogo.setMaxWidth(100);
			mLogo.setMinimumHeight(100);
			mLogo.setMinimumWidth(100);
			mLogo.setImageBitmap(BitmapFactory.decodeByteArray(
					adver.getImgSrc(), 0, adver.getImgSrc().length));
		}

		// set 'name' in line 1.
		final TextView nameTv = (TextView) v.findViewById(R.id.linr1);
		nameTv.setText(adver.getNombre());
		nameTv.setTextSize(20);
		nameTv.setGravity(Gravity.CENTER_VERTICAL);
		nameTv.setTypeface(null, Typeface.BOLD);
		// set 'gender' in line 2.
		final TextView genderTv = (TextView) v.findViewById(R.id.linr2);
		genderTv.setText(adver.getDireccion());

		// if View exists, then reuse... else create a new object.

		return v;
	}

}
