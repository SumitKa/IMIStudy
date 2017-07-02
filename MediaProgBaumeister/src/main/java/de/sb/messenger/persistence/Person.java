package de.sb.messenger.persistence;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlElement;


@Entity
public class Person extends BaseEntity {

	public static enum Group {
		ADMIN,
		USER
	}
	
	@Pattern(regexp ="^(.+)@(.+)$", message="{invalid.email}")
	@NotNull
	@Column(name = "email", nullable=false, insertable=false, updatable=false)
	@XmlElement
	private String email;
	
	@Column(name = "passHash", nullable=false, insertable=false, updatable=false)
	private byte[] passHash;
	
	@Valid
	@Column(name = "group", nullable=false, insertable=false, updatable=false)
	@Enumerated
	@XmlElement
	private Group group;
	
	@Valid
	@Embedded
	@XmlElement
	private Name name;
	
	@Valid
	@Embedded
	@XmlElement
	private Adress adress;
	
	@Valid
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="avatar", nullable=false)
	private Document avatar;

	@Column(name = "messageAuthored", nullable=false, insertable=false, updatable=false)
	@OneToMany
	private Set<Message> messageAuthored;
	
	@Column(name = "peopleOberserving", nullable=false, insertable=false, updatable=false)
	@XmlElement
	@ManyToMany(mappedBy = "peopleObserved")
	private Set<Person> peopleOberserving;

	@Column(name = "peopleOberserved", nullable=false, insertable=false, updatable=false)
	@XmlElement
	@ManyToMany
	@JoinTable(schema = "peopleObserving", name = "", inverseJoinColumns = "", uniqueConstraints = "")
	private Set<Person> peopleOberserved;

	public void setAvatar(Document avatar) {
		this.avatar = avatar;
	}

	public Document getAvatar() {

		return avatar;
	}

	public Person(final String email, final Document avatar) {
		this.name = new Name();
		this.adress = new Adress();
		this.group = Group.USER;
		this.messageAuthored = new HashSet<>();
		this.peopleOberserving = new HashSet<>();
		this.peopleOberserved = new HashSet<>();
		this.passHash = new byte[0];
		this.email = email;
		this.avatar = avatar;
	}
	
	protected Person()
	{
		this(null, null);
	}

    public Set<Message> getMessageAuthored() {
        return messageAuthored;
    }

	public Set<Person> getPeopleOberserving() {
		return peopleOberserving;
	}

	public Set<Person> getPeopleOberserved() {
		return peopleOberserved;
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
