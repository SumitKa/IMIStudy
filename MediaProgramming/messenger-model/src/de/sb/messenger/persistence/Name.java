package de.sb.messenger.persistence;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.*;

@Embeddable 
public class Name {

	@Size(min = 1, max = 31)
	@NotNull
	@Column
	private String given;
	
	@Size(min = 1, max = 31)
	@NotNull
	@Column
	private String family;
	
	protected Name() {
		
	}

	public String getGiven() {
		return given;
	}

	public void setGiven(String given) {
		this.given = given;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}
}
