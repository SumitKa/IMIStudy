package de.sb.messenger.persistence;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import javax.validation.executable.ValidateOnExecution;

@Entity
public class Person extends BaseEntity {

	public static enum Group {
		ADMIN,
		USER
	}
	
	@Pattern(regexp ="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
	        +"[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
	        +"(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
	             message="{invalid.email}")
	@NotNull
	@Column
	private String email;
	
	@Column
	private byte[] passHash;
	
	@Valid
	@Column
	@Enumerated
	private Group group;
	
	@Valid
	@Embedded
	private Name name;
	
	@Valid
	@Embedded
	private Adress adress;
	
	@Valid
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="Document", nullable=false)
	private Document avatar;
	
	@Column
	@OneToMany
	private List<Message> messageAuthored;
	
	// TODO ? muss hier ManyToMany oder OneToMany eingebaut werden?
	@Column
	private Person peopleOberserving;
	
	@Column
	private Person peopleOberserved;
	
	protected Person() {
		
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	
	static public byte[] passwordHash(String password)
	{
		final byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
		return Document.mediaHash(passwordBytes);
	}

}
