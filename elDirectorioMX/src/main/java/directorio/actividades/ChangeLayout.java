package directorio.actividades;

import android.content.Context;
import android.content.Intent;

/**
 * Clase que se encarga de empezar las actividades dependiendo de que botón
 * opriman a la hora de abrir el menú.
 * 
 * @author NinjaDevelop
 * 
 */
public class ChangeLayout {

	public static Intent intent;

	public static void layoutChange(int itemId, Context context) {
		switch (itemId) {
		case R.id.snm_categorias:
			intent = new Intent(context, MainCategories.class);
			context.startActivity(intent);
			break;

		case R.id.snm_buscar:
			intent = new Intent(context, Search.class);
			context.startActivity(intent);
			break;

		case R.id.snm_favoritos:
			intent = new Intent(context, Favoritos.class);
			context.startActivity(intent);
			break;
//		case R.id.snm_destacados:
//			intent = new Intent(context, CuponesClub.class);
//			context.startActivity(intent);
//			break;
		case R.id.snm_cupones:
			intent = new Intent(context, Cupones.class);
			context.startActivity(intent);
			break;

		case R.id.snm_franquicias:
			intent = new Intent(context, Franquicias.class);
			context.startActivity(intent);
			break;

		case R.id.snm_anunciate:
			Intent intnt = new Intent(context, Anunciate.class);
			context.startActivity(intnt);
			break;

		case R.id.snm_comparte:
			Intent intent = new Intent(context, Comparte.class);
			context.startActivity(intent);
			break;
		case R.id.snm_configuracion:
			Intent i = new Intent(context, Preferences.class);
			context.startActivity(i);
			break;

		default:
			return;
		}
	}

}
