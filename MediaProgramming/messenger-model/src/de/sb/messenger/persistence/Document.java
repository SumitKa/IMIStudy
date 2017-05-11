package de.sb.messenger.persistence;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.Column;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlElement;

import org.eclipse.persistence.jpa.jpql.Assert;

@PrimaryKeyJoinColumn
public class Document extends BaseEntity {

	@NotNull
	@Column(name = "contentHash", nullable=false, insertable=false, updatable=true)
	@XmlElement
	private byte[] contentHash;
	
	@Size(min = 1, max = 63)
	@NotNull
	@Column(name = "contentType", nullable=false, insertable=false, updatable=true)
	@XmlElement
	private String contentType;
	
	@Size(min = 1, max = 16777215)
	@NotNull
	@Column(name = "content", nullable=false, insertable=false, updatable=true)
	@XmlElement
	private byte[] content;
	
	protected Document() {
		this(null, null);
	}
	
	public Document(byte[] content, String contentType)
	{
		this.content = content;
		this.contentType = contentType;
		// TODO: behandeln falls content null -> so?
		Assert.isNotNull(content, "content is null or empty");
		this.contentHash = mediaHash(content);
	}

	public byte[] getContentHash() {
		return contentHash;
	}

	public void setContentHash(byte[] contentHash) {
		this.contentHash = contentHash;
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
