package de.sb.messenger.persistence;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Embeddable
public class Address {

	@Size(max = 63)
	@Column(name = "street")
	private String street;
	@Size(max = 15)
	@Column(name = "postcode")
	private String postcode;
	@NotNull
	@Size(min = 1, max = 63)
	@Column(name = "city")
	private String city;

	public Address() {
		this(null, null, null);
	}

	public Address(String street, String postcode, String city) {
		this.street = street;
		this.postcode = postcode;
		this.city = city;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public String getPostcode() {
		return postcode;
	}

	public String getCity() {
		return city;
	}
}
