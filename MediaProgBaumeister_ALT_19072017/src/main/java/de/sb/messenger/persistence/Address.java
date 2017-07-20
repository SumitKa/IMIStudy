package de.sb.messenger.persistence;
import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlElement;

@Embeddable 
public class Address {

	@Column(nullable = true, updatable = true, insertable = true)
	@XmlElement
	@Size(min = 1, max = 63)
	private String street;

	@Column(nullable = true, updatable = true, insertable = true)
	@XmlElement
	@Size(min = 1, max = 15)
	private String postCode;

	@Column(nullable = true, updatable = true, insertable = true)
	@NotNull
	@XmlElement
	@Size(min = 1, max = 63)
	private String city;

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
