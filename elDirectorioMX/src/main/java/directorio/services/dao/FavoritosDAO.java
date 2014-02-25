package directorio.services.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

import directorio.database.FavoritosDatabaseHelper;
import directorio.objetos.Advertiser;

/**
 * Clase que se encarga de hacer el acceso a la base de datos de Favoritos.
 * 
 * @author Publysorpresas
 * 
 */
public class FavoritosDAO {
	/** La base de datos **/
	private SQLiteDatabase db;

	/** El objeto helper de la base de datos **/
	private FavoritosDatabaseHelper helper;

	/** El contexto desde donde se manda llamar esta clase **/
	private Context c;

	private static final String TAG = "FavoritosDAO";

	// Tags que representan el nombre de la tabla y sus columnas.
	private static final String TABLA_FAVS = "Favoritos";
	private static final String FAVORITOS_NOMBRE = "nombreFavoritos";
	private static final String ID = "id";
	private static final String PAIS_COLUMN = "Pais";

	/**
	 * Constructor, asigna un valor a la variable que representa el contexto y
	 * prepara la base de datos para ser usada.
	 * 
	 * @param ctx
	 *            EL contexto desde donde se manda llamar esta clase.
	 */
	public FavoritosDAO(Context ctx) {
		c = ctx;
		openDatabase();
	}

	/**
	 * M�todo que abre la base de datos.
	 */
	private void openDatabase() {
		// Se crea un helper usando el contexto que mandá llamar esta clase
		helper = new FavoritosDatabaseHelper(c);
		// se obtiene la base de datos
		db = helper.getWritableDatabase();
	}

	/**
	 * Método que agrega un advertiser a la lista de favoritos.
	 * 
	 * @param adv
	 *            El advertiser a agregar
	 * @param country
	 *            El país que seleccioné el usuario.
	 */
	public void addToFavoritos(Advertiser adv, String country) {
		assert (null != adv);
		// intentar abrir la base de datos
		try {
			if (db.isOpen() == false) {
				openDatabase();
			}
		} catch (NullPointerException npe) {
			// si db.isOpen() marca un null pointer exception, significa que no
			// existe la base de datos, por lo que se crea una
			Log.e(TAG, "Database NULL, creating a new one");
			openDatabase();
		}
		// se prepara el country para que no sea null
		country = country.trim();

		// Variable content values para insertar en la base de datos
		ContentValues cv = new ContentValues();
		cv.put(ID, adv.getId());
		cv.put(FAVORITOS_NOMBRE, adv.getNombre());
		cv.put(PAIS_COLUMN, country);

		// se insertan los valores en la base de datos y se cierra para evitar
		// leaks
		db.insert(TABLA_FAVS, null, cv);
		db.close();
	}

	/**
	 * Método que será llamado cuando no se tenga conexión a internet, en dado
	 * caso, se cargaran los favoritos de la base de datos y no se descargará
	 * información como sus icono, etcétera.
	 * 
	 * @return Una lista con los advertisers que se tienen en la base de datos.
	 */
	public ArrayList<Advertiser> getFavoritosOffline() {
		ArrayList<Advertiser> result = new ArrayList<Advertiser>();

		// se intenta abrir la base de datos
		try {
			if (db.isOpen() == false) {
				openDatabase();
			}
		} catch (NullPointerException npe) {
			// en caso de que la base de datos no exista, se crea una nueva.
			Log.e(TAG, "Database NULL, creating a new one");
			openDatabase();
		}

		// cursor donde se guardan los resultados del query
		Cursor c = db.rawQuery("SELECT * FROM " + TABLA_FAVS + " order by "
				+ FAVORITOS_NOMBRE + ";", null);

		// se guardan los resultados en una lista.
		while (c.moveToNext()) {
			Advertiser a = new Advertiser();
			a.setNombre(c.getString(1));
			a.setDireccion(c.getString(2));
			result.add(a);
		}

		// se cierra el cursor.
		c.close();

		return result;
	}

	/**
	 * Método que carga los favoritos que se guardaron en cada país.
	 * 
	 * @param pais
	 *            El país que seleccioné el usuario
	 * @return Una lista con los advertisers que se guardaron como favoritos.
	 */
	public ArrayList<Advertiser> returnFavoritos(String pais) {
		// se intenta abrir la base de datos
		try {
			if (db.isOpen() == false) {
				openDatabase();
			}
		} catch (NullPointerException npe) {
			Log.e(TAG, "Database NULL, creating a new one");
			openDatabase();
		}
		ArrayList<Advertiser> favoritos = new ArrayList<Advertiser>();
		final ArrayList<String> favs = new ArrayList<String>();
		ArrayList<String> paises = new ArrayList<String>();

		// se hace el query a la base de datos
		Cursor c = db.rawQuery("SELECT * FROM " + "Favoritos where Pais = '"
				+ pais + "' order by nombreFavoritos;", null);

		// se guardan los resultados con su respectivo país.
		while (c.moveToNext()) {
			favs.add(c.getString(1));
			paises.add(c.getString(2));
		}

		// se cierra el cursor
		c.close();

		// se recorre la lista
		for (int i = 0; i < favs.size(); i++) {
			// se descarga la información del advertiser desde el web service
			// para desplegarla
			Advertiser adv = getAdvertiser(favs.get(i), paises.get(i));

			// se establece el advertiser como un favorito
			if (adv != null) {
				adv.setFavorito(true);
				favoritos.add(adv);
			}
		}

		// se cierra la base de datos
		db.close();

		return favoritos;
	}

	/**
	 * Método que descarga la información del advertiser desde el web service
	 * 
	 * @param nombre
	 *            El nombre del advertiser
	 * @param pais
	 *            El país que seleccioné el usuario
	 * @return Un objeto tipo advertiser con toda la informaci�n necesaria
	 */
	public Advertiser getAdvertiser(String nombre, String pais) {
		AdvertiserDAO aDao = new AdvertiserDAO();
		Advertiser resultado = aDao.findAsync(nombre, pais);
		return resultado;
	}

	/**
	 * Método que busca si un negocio está en favoritos.
	 * 
	 * @param name
	 *            El nombre del negocio que se busca en favoritos.
	 * @return True si lo encontr�, de caso contrario, false.
	 */
	public boolean isInFavoritos(String name) {
		// se intenta abrir la base de datos
		try {
			if (db.isOpen() == false) {
				openDatabase();
			}
		} catch (NullPointerException npe) {
			Log.e(TAG, "Database NULL, creating a new one");
			openDatabase();
		}

		// contador
		int exists = 0;
		try {
			// se hace el query para ver si existe
			Cursor c = db.rawQuery(
					"select * from Favoritos where nombreFavoritos = '" + name
							+ "';", null);
			// en caso de que haya resultados, incrementar contador
			while (c.moveToNext()) {
				exists++;
			}
			// en caso de que algo salga mal, return false
		} catch (Exception e) {
			return false;
		}
		// si el contador es mayor a cero, regresar true, de otra forma, false
		if (exists > 0) {
			return true;
		} else
			return false;
	}

	/**
	 * Método que quita de los favoritos.
	 * 
	 * @param name
	 *            El nombre del negocio que se va a quitar.
	 */
	public void removeFromFavoritos(String name) {
		// intentar abrir la base de datos
		try {
			if (db.isOpen() == false) {
				openDatabase();
			}
		} catch (NullPointerException npe) {
			Log.e(TAG, "Database NULL, creating a new one");
			openDatabase();
		}
		
		// ejecutar el query que lo elimina
		SQLiteStatement stmt = db
				.compileStatement("DELETE FROM Favoritos WHERE nombreFavoritos = ?");
		stmt.bindString(1, name);
		stmt.execute();
		
		//cerrar la base de datos
		db.close();
	}

}
