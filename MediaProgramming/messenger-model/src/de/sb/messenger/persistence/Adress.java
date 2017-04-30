package de.sb.messenger.persistence;
import javax.validation.constraints.*;

public class Adress {

	@Size(min = 0, max = 63)
	@NotNull
	private String street;
	
	@Size(min = 0, max = 15) 
	@NotNull
	private String postcode;

	@Size(min = 1, max = 63)
	@NotNull
	private String city;
	
	protected Adress() {
		this(null, null, null);
	}
	
	public Adress(String street, String postcode, String city)
	{
		this.setStreet(street);
		this.setPostcode(postcode);
		this.setCity(city);
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
