package de.sb.messenger.persistence;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlElement;


@Entity
@Table (schema = "messenger",name = "Person")
@DiscriminatorValue(value = "Person")
@PrimaryKeyJoinColumn (name = "personIdentity")
public class Person extends BaseEntity {

	public Name getName() {
		return givenName;
	}

	public static enum Group {
		ADMIN,
		USER
	}
	
	@Pattern(regexp ="^(.+)@(.+)$", message="{invalid.email}")
	@NotNull
	@Column(nullable=false, insertable=false, updatable=false)
	@XmlElement
	private String email;

	@Size (min = 32, max = 32)
	@Column(nullable=false, insertable=false, updatable=false)
	private byte[] passwordHash;

	@Valid
	@Column(name = "groupAlias", nullable=false, insertable=false, updatable=false)
	@Enumerated
	@XmlElement
	private Group group;
	
	@Valid
	@Column(nullable=false, insertable=false, updatable=false)
	@Embedded
	@XmlElement
	private Name givenName;

	@Valid
	@Column(nullable=false, insertable=false, updatable=false)
	@Embedded
	@XmlElement
	private Name familyName;
	
	@Valid
	@Embedded
	@XmlElement
	private Address address;
	
	@Valid
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="avatar", nullable=false, updatable = true)
	private Document avatar;

	@OneToMany (mappedBy = "author")
	private Set<Message> messageAuthored;

	@ManyToMany(mappedBy = "peopleObserved")
	private Set<Person> peopleObserving;

	@ManyToMany
	@JoinTable(
			schema = "messenger",
			name = "ObservationAssociation",
			joinColumns = @JoinColumn (name = "observingReference"),
			inverseJoinColumns = @JoinColumn (name = "observedReference")
	)
	private Set<Person> peopleObserved;


	public Person(final String email, final Document avatar) {
		this.givenName = new Name();
		this.address = new Address();
		this.group = Group.USER;
		this.messageAuthored = new HashSet<>();
		this.peopleObserving = new HashSet<>();
		this.peopleObserved = new HashSet<>();
		this.passwordHash = new byte[32];
		this.email = email;
		this.avatar = avatar;
	}
	
	protected Person()
	{
		this(null, null);
	}

	public void setAvatar(Document avatar) {
		this.avatar = avatar;
	}

	public Document getAvatar() {

		return avatar;
	}

    public Set<Message> getMessageAuthored() {
        return messageAuthored;
    }

	public Set<Person> getPeopleObserving() {
		return peopleObserving;
	}

	public Set<Person> getPeopleObserved() {
		return peopleObserved;
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

	public byte[] getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(byte[] passwordHash) {
		this.passwordHash = passwordHash;
	}

	static public byte[] passwordHash(String password)
	{
		final byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
		return Document.mediaHash(passwordBytes);
	}

}
