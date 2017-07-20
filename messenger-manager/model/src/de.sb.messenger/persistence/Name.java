package de.sb.messenger.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Embeddable
public class Name {

	@Size(min = 1, max = 31)
	@NotNull
	@Column(name = "givenName")
	private String given;
	@Size(min = 1, max = 31)
	@NotNull
	@Column(name = "familyName")
	private String family;

	public Name() {
		this(null, null);
	}

	public Name(String given, String family) {
		this.given = given;
		this.family = family;
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