import javax.validation.constraints.*;

public class Name {

	@Size(min = 1, max = 31)
	private String given;
	
	@Size(min = 1, max = 31)
	private String family;
	
	public Name(String given, String family)
	{
		this.given = given;
		this.family = family;
	}

	public String getGiven() {
		return given;
	}

	public void setGiven(@Size(min = 1, max = 31) String given) {
		this.given = given;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(@Size(min = 1, max = 31) String family) {
		this.family = family;
	}
}
