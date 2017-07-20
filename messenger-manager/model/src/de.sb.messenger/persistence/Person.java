package de.sb.messenger.persistence;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(schema = "messenger", name = "Person")
@PrimaryKeyJoinColumn(name = "personIdentity")
public class Person extends BaseEntity {

	public enum Group {
		ADMIN("ADMIN"),
		USER("USER");

		private final String text;

		Group(final String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	@NotNull
	@Size(min = 1, max = 128)
	@Pattern(regexp = ".+@.+")
	@Column(name = "email")
	@XmlElement
	private String email;

	@NotNull
	@Column(name = "passwordHash")
	@XmlTransient
	private byte[] passwordHash = new byte[32];

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "groupAlias")
	@XmlElement
	private Group group;

	@NotNull
	@Valid
	@Embedded
	@XmlElement
	private Name name;

	@NotNull
	@Valid
	@Embedded
	@XmlElement
	private Address address;

	@OneToOne
	@JoinColumn(name = "avatarReference")
	@XmlElement(required = true)
	private Document avatar;

	@OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
	private Set<Message> messagesAuthored;

	@ManyToMany(mappedBy = "peopleObserved", cascade = CascadeType.REMOVE)
	private Set<Person> peopleObserving;

	@ManyToMany
	@JoinTable(
			name = "ObservationAssociation",
			joinColumns = @JoinColumn(name = "observingReference", referencedColumnName = "personIdentity"),
			inverseJoinColumns = @JoinColumn(name = "observedReference", referencedColumnName = "personIdentity")
	)
	private Set<Person> peopleObserved;

	protected Person() {
		this(null, null, null);
	}

	public Person(String email, Document avatar, String passwordHash) {
		this.email = email;
		this.group = Group.USER;
		this.name = new Name();
		this.address = new Address();
		this.avatar = avatar;
		this.passwordHash = passwordHash(passwordHash);
		messagesAuthored = Collections.emptySet();
		peopleObserving = Collections.emptySet();
		peopleObserved = Collections.emptySet();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Document getAvatar() {
		return avatar;
	}

	public void setAvatar(Document avatar) {
		this.avatar = avatar;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public void setPasswordHash(byte[] passwordHash) {
		this.passwordHash = passwordHash;
	}

	@XmlElement
	public Set<Message> getMessagesAuthored() {
		return messagesAuthored;
	}

	@XmlElement
	@JsonBackReference(value="observing")
	public Set<Person> getPeopleObserving() {
		return peopleObserving;
	}

	@XmlElement
	@JsonBackReference(value="observed")
	public Set<Person> getPeopleObserved() {
		return peopleObserved;
	}

	public byte[] getPasswordHash() {
		return passwordHash;
	}

	public static byte[] passwordHash(String password) {
		if (password != null) {
			try {
				return MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
