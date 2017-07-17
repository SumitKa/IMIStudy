package de.sb.messenger.persistence;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.constraints.*;

@Embeddable 
public class Adress {

	@Size(min = 0, max = 63)
	@NotNull
	@Column(name = "street", nullable=false, insertable=false, updatable=true)
	private String street;
	
	@Size(min = 0, max = 15) 
	@NotNull
	@Column(name = "postcode", nullable=false, insertable=false, updatable=true)
	private String postcode;

	@Size(min = 1, max = 63)
	@NotNull
	@Column(name = "city", nullable=false, insertable=false, updatable=true)
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