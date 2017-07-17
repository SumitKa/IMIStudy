package de.sb.messenger.persistence;
import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table
@Embeddable 
public class Address {

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
	
	protected Address() {
		this(null, null, null);
	}
	
	public Address(String street, String postcode, String city)
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