package de.sb.messenger.persistence;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

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
	@Valid
	private Document avatar;
	
	@Min(0)
	private List<Message> messageAuthored;
	@Min(0)
	private Person peopleOberserving;
	@Min(0)
	private Person peopleOberserved;
	
	public Person()
	{
		
	}
	
	public byte[] passwordHash(String password)
	{
		try {
			this.passwortHash = MessageDigest.getInstance("UTF-8", password).digest();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		return 0;
	}
}
