package directorio.objetos;

/**
 * Clase que representa una sucursal
 * 
 * @author NinjaDevelop
 * @author Publysorpresas
 * 
 */
public class Sucursal {
	
	// Valores de la sucursal
	private int id;
	private int AdvertiserID;
	private String name;
	private String address;
	private String CityName;
	private double pointX;
	private double pointY;
	private int zipCode;
	private int number;
	private String neighborhood;

	//Constructor
	public Sucursal(int id, int advertiserID, String name, String address,
			String cityName, double pointX, double pointY, int zip, int numero,
			String neighbor) {
		super();
		this.id = id;
		AdvertiserID = advertiserID;
		this.name = name;
		this.address = address;
		CityName = cityName;
		this.pointX = pointX;
		this.pointY = pointY;
		this.zipCode = zip;
		number = numero;
		neighborhood = neighbor;
	}
	
	//Constructor vacío
	public Sucursal() {
		super();
	}

	//---------------------- Getters y Setters ----------------------------------------

	public int getZipCode() {
		return zipCode;
	}

	public void setZipCode(int zipCode) {
		this.zipCode = zipCode;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAdvertiserID() {
		return AdvertiserID;
	}

	public void setAdvertiserID(int advertiserID) {
		AdvertiserID = advertiserID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCityName() {
		return CityName;
	}

	public void setCityName(String cityName) {
		CityName = cityName;
	}

	public double getPointX() {
		return pointX;
	}

	public void setPointX(double pointX) {
		this.pointX = pointX;
	}

	public double getPointY() {
		return pointY;
	}

	public void setPointY(double pointY) {
		this.pointY = pointY;
	}

}