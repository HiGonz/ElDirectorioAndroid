package directorio.services.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import directorio.database.FavoritosDatabaseHelper;

/**
 * Clase que se encarga del manejo del log in de la base de datos interna de la
 * aplicación
 * 
 * @author Publysorpresas
 * 
 */
public class LogDAO {

	/** La base de datos **/
	private SQLiteDatabase db;

	/** La clase helper que maneja la base de datos **/
	private FavoritosDatabaseHelper helper;

	/** El contexto desde donde se llama esta clase **/
	private Context c;

	private static final String TAG = "LogDao";

	/**
	 * Constructor que guarda el valor del contexto, y abre la base de datos
	 * para sus uso.
	 * 
	 * @param ctx
	 */
	public LogDAO(Context ctx) {
		c = ctx;
		openDatabase();

	}

	/**
	 * Método que abre la base de datos.
	 */
	private void openDatabase() {
		helper = new FavoritosDatabaseHelper(c);
		db = helper.getWritableDatabase();
	}

	/**
	 * Método que regresa el valor de que si se iniciá sesión o no.
	 * 
	 * @return EL valor de si se iniciá sesión o no.
	 */
	public ArrayList<String> dameLogeado() {
		// se abre la base de datos
		try {
			if (db.isOpen() == false) {
				openDatabase();
			}
		} catch (NullPointerException npe) {
			Log.e(TAG, "Database NULL, creating a new one");
			openDatabase();
		}

		ArrayList<String> resultado = new ArrayList<String>();

		// se hace el query a la base de datos
		Cursor c = db.rawQuery("select  * from Login", null);
		while (c.moveToNext()) {
			resultado.add(c.getString(0));
			resultado.add(c.getString(1));
		}

		// se cierran los objetos
		c.close();
		db.close();

		return resultado;
	}

	/**
	 * Método que cierra sesión.
	 */
	public void desloguear() {
		// se abre la base de datos
		try {
			if (db.isOpen() == false) {
				openDatabase();
			}
		} catch (NullPointerException npe) {
			Log.e(TAG, "Database NULL, creating a new one");
			openDatabase();
		}
		
		// se hace el query
		Cursor c = db.rawQuery("delete from Login", null);
		
		// se cierran los objetos
		c.close();
		db.close();
	}

}
