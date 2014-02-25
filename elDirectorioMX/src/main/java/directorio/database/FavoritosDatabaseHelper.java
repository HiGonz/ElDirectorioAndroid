package directorio.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Clase que facilita el ingreso a la base de datos donde se guardan los
 * favoritos y el estado del Log in
 * 
 * @author NinjaDevelop
 * @author Carlos Tirado
 * @author Publysorpresas
 * 
 */
public class FavoritosDatabaseHelper extends SQLiteOpenHelper {
	/** Versi�n de la base de datos **/
	private static final int VERSION = 6;

	/** Nombre del archivo de la base de datos **/
	private static final String DB_NAME = "favoritos.sqlite";

	/** Tags que representan los nombres de las tablas y de las columnas **/
	private static final String TABLA_FAVS = "Favoritos";
	private static final String FAVORITOS_NOMBRE = "nombreFavoritos";
	private static final String ID = "id";
	private static final String PAIS_COLUMN = "Pais";
	private static final String TABLA_LOGIN = "Login";
	private static final String Id = "Id";
	private static final String Estado = "Estado";

	private static final String TAG = "FavoritosDatabaseHelper";

	/**
	 * Constructor
	 * 
	 * @param context
	 *            El contexto de la aplicación.
	 */
	public FavoritosDatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// se crea la base de datos
		createTables(db);
		Log.e(TAG, "" + VERSION);
	}

	/**
	 * M�todo que crea las tablas de la base de datos.
	 * 
	 * @param db
	 *            La base de datos donde se crearón las tablas.
	 */
	private void createTables(SQLiteDatabase db) {
		try {
			db.execSQL("create table " + TABLA_FAVS + " (" + ID
					+ " integer primary key not null," + FAVORITOS_NOMBRE
					+ " text, " + PAIS_COLUMN
					+ " text not null default 'M�xico'" + ");");

			db.execSQL("create table " + TABLA_LOGIN + " (" + Id
					+ " text not null," + Estado + " text not null" + ");");
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			// do nothing, already updated
		}
	}

	/**
	 * Lo que se hace cuando se actualiza la base de datos
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*
		 * Los cambios que se hicieron se hicieron por un error que se tenía, en
		 * el cual la columna de país se llamaba Paistext y no pa�s, por eso se
		 * dio de baja la base de datos y se creo una nueva con el nuevo nombre
		 * de la columna.
		 */
		db.execSQL("ALTER TABLE " + TABLA_FAVS + " RENAME TO temp;");
		db.execSQL("create table " + TABLA_FAVS + " (" + ID
				+ " integer primary key not null, " + FAVORITOS_NOMBRE
				+ " text, " + PAIS_COLUMN + " text not null default México);");
		db.execSQL("insert into " + TABLA_FAVS + " (" + FAVORITOS_NOMBRE
				+ ")select " + FAVORITOS_NOMBRE + " from temp;");
		db.execSQL("drop table temp;");

	}
}
