package directorio.objetos;

/**
 * Clase que representa las categorías
 * @author Publysorpresas
 *
 */
public class Categoria {
	
	//Variables de la categoría.
	private int id;
	private String nombre;
	private char letra;

	
	//Constructor
	public Categoria(int id, String nombre, char letra) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.letra = letra;
	}
	
	//Getters y Setters

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public char getLetra() {
		return letra;
	}

	public void setLetra(char letra) {
		this.letra = letra;
	}
}
