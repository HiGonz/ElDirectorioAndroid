package directorio.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import directorio.actividades.R;
import directorio.objetos.Advertiser;

/**
 * Custom adapter que se encarga de mostrar los negocios y sus imágenes.
 * 
 * @author NinjaDevelop
 * 
 */
public class MyCustomAdapter extends BaseAdapter {

	private LayoutInflater li;
	private ArrayList<Advertiser> adds = new ArrayList<Advertiser>();

	public MyCustomAdapter(Context context, ArrayList<Advertiser> addss) {
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
			v = li.inflate(R.layout.adapters_my_custom_adapter, null);
		}
		// reference to the ImageView component
		ImageView iconoDestacados = (ImageView) v.findViewById(R.id.iconito);
		final ImageView mLogo = (ImageView) v.findViewById(R.id.imagendest);
		if (adver.getFeatured() == false) {
			iconoDestacados.setVisibility(ImageView.INVISIBLE);
		}else {
			iconoDestacados.setVisibility(ImageView.VISIBLE);
		}

		if (adver.getImgSrc() != null) {
			mLogo.setAdjustViewBounds(true);
			mLogo.setMaxHeight(100);
			mLogo.setMaxWidth(100);
			mLogo.setMinimumHeight(100);
			mLogo.setMinimumWidth(100);
			mLogo.setImageBitmap(BitmapFactory.decodeByteArray(
					adver.getImgSrc(), 0, adver.getImgSrc().length));
		}

		
		final TextView nameTv = (TextView) v.findViewById(R.id.linea1);
		nameTv.setText(adver.getNombre());
		nameTv.setTextSize(20);
		
		final TextView genderTv = (TextView) v.findViewById(R.id.linea2);
		genderTv.setText(adver.getDireccion());

		// if View exists, then reuse... else create a new object.

		return v;
	}

}