package de.sb.messenger.persistence;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;

@Entity public class Person extends BaseEntity {

	public static enum Group {
		ADMIN,
		USER
	}
	
	@Pattern(regexp ="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
	        +"[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
	        +"(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
	             message="{invalid.email}")
	@NotNull
	private String email;
	
	private byte[] passHash;
	
	@Valid
	private Group group;
	@Valid
	private Name name;
	@Valid
	private Adress adress;
	@Valid
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="Document", nullable=false)
	private Document avatar;
	
	private List<Message> messageAuthored;
	private Person peopleOberserving;
	private Person peopleOberserved;
	
	public Person() {
		
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
