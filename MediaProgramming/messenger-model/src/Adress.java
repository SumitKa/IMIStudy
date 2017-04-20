import javax.validation.constraints.*;

public class Adress {

	@Size(min = 0, max = 63)
	private String street;
	
	@Size(min = 0, max = 15) 
	private String postcode;

	@Size(min = 1, max = 63)
	private String city;
	
	public Adress()
	{
		
	}

	public String getStreet() {
		return street;
	}

	// TODO ??? @Size
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
