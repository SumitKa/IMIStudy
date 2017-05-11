package de.sb.messenger.persistence;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.*;

@Embeddable 
public class Name {

	@Size(min = 1, max = 31)
	@NotNull
	@Column(name = "given", nullable=false, insertable=false, updateable=true)
	private String given;
	
	@Size(min = 1, max = 31)
	@NotNull
	@Column(name = "family", nullable=false, insertable=false, updateable=true)
	private String family;
	
	public Name() {
		
		this.given = "";
		this.family = "";
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
