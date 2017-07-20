package de.sb.messenger.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@Entity
@Table(schema = "messenger", name = "Message")
@PrimaryKeyJoinColumn(name = "messageIdentity")
public class Message extends BaseEntity {

	@NotNull
	@ManyToOne
	@JoinColumn(name = "authorReference")
	private Person author;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "subjectReference")
	private BaseEntity subject;

	@Size(min = 1, max = 4093)
	@Column(name = "body")
	@XmlAttribute
	private String body;

	public Message() {
		this(null, null, null);
	}

	public Message(Person author, BaseEntity subject, String body) {
		this.author = author;
		this.subject = subject;
		this.body = body;
	}

	@XmlElement
	public Person getAuthor() {
		return author;
	}

	@XmlElement
	public BaseEntity getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@XmlElement
	public long getSubjectRefrence() {
		return subject.getIdentity();
	}
	@XmlElement
	public long getAuthorReference() {
		return author.getIdentity();
	}
}
