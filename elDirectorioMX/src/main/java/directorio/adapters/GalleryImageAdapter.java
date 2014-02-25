package directorio.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.List;

/**
 * Custom Adapter para la galería.
 * 
 * @author NinjaDevelop
 * 
 */
@SuppressWarnings("deprecation")
public class GalleryImageAdapter extends BaseAdapter {

	private Activity context;

	private static ImageView imageView;

	private List<Bitmap> plotsImages;

	private static ViewHolder holder;

	public GalleryImageAdapter(Activity context, List<Bitmap> plotsImages) {

		this.context = context;
		this.plotsImages = plotsImages;

	}

	@Override
	public int getCount() {
		return plotsImages.size();
	}

	@Override
	public Object getItem(int position) {
		return plotsImages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {

			holder = new ViewHolder();

			imageView = new ImageView(this.context);

			imageView.setPadding(3, 3, 3, 3);

			convertView = imageView;

			holder.imageView = imageView;

			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		holder.imageView.setImageBitmap(plotsImages.get(position));

		holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		holder.imageView.setLayoutParams(new Gallery.LayoutParams(150, 90));

		return imageView;
	}

	private static class ViewHolder {
		ImageView imageView;
	}

}