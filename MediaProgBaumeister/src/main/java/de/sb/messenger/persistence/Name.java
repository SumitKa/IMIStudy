package de.sb.messenger.persistence;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.*;

@Entity
@Table
@Embeddable 
public class Name {

	@Size(min = 1, max = 31)
	@NotNull
	@Column(name = "given", nullable=false, insertable=false)
	private String given;
	
	@Size(min = 1, max = 31)
	@NotNull
	@Column(name = "family", nullable=false, insertable=false)
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
