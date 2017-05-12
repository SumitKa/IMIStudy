package de.sb.messenger.persistence;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlElement;

@Entity
@Table
public class Message extends BaseEntity {

	@NotNull
	private final Person author;
	
	@NotNull
	private final BaseEntity subject;
	
	@Size(min = 1, max = 4093)
	@NotNull
	@Column(name = "body", nullable=false, insertable=false)
	@XmlElement
	private String body;
	
	protected Message() {
		this(null, null, null);
	}
	
	public Message(Person author, BaseEntity subject, String body)
	{
		this.author = author;
		this.subject = subject;
		this.body = body;
	}

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
	public long getAuthorReference()
	{
		return this.author == null ? 0 : this.author.getIdentity();
	}
}
