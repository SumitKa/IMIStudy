import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.validation.*;
import javax.validation.constraints.*;

public class Person {

	public static enum Group {
		ADMIN,
		USER
	}
	
	@Pattern(regexp ="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
	        +"[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
	        +"(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
	             message="{invalid.email}")
	private String email;
	
	private byte[] passwortHash;
	
	@Valid
	private Group group;
	@Valid
	private Name name;
	@Valid
	private Adress adress;
	
	private Document avatar;
	private Message messageAuthored;
	private Person peopleOberserving;
	private Person peopleOberserved;
	
	public Person()
	{
		
	}
	
	public byte[] passwordHash(String password) throws NoSuchProviderException, NoSuchAlgorithmException
	{
		// TODO set passwordHash ?
		this.passwortHash = MessageDigest.getInstance("UTF-8", password).digest();
		
		return passwortHash;
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
	
	public long getSubjectReference()
	{
		
	}
}
