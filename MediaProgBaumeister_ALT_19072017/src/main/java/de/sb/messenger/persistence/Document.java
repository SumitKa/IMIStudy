package de.sb.messenger.persistence;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlElement;

import org.eclipse.persistence.jpa.jpql.Assert;

@Entity
@Table (schema = "messenger",name = "Document")
@DiscriminatorValue(value = "Document")
@PrimaryKeyJoinColumn (name = "documentIdentity")
public class Document extends BaseEntity {

    static private final byte[] EMPTY_HASH = mediaHash(new byte[0]);

	@NotNull
	@Column(name = "contentHash", nullable=false, insertable=false)
	@XmlElement
	private byte[] contentHash;
	
	@Size(min = 1, max = 63)
	@NotNull
	@Column(name = "contentType", nullable=false, insertable=false)
	@XmlElement
	private String contentType;
	
	@Size(min = 1, max = 16777215)
	@NotNull
	@Column(name = "content", nullable=false, insertable=false)
	@XmlElement
	private byte[] content;
	
	protected Document() {
		this(null, null);
	}
	
	public Document(byte[] content, String contentType)
	{
		this.content = content;
		this.contentType = contentType;
		this.contentHash = content == null ? EMPTY_HASH : mediaHash(content);
	}

	public byte[] getContentHash() {
		return contentHash;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
		this.contentHash = mediaHash(content);
	}

	static public byte[] mediaHash(byte[] content)
	{
		try {
			return MessageDigest.getInstance("SHA-256").digest(content);
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError(e);
		}
	}
}
