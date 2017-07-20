package de.sb.messenger.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Entity
@Table(schema = "messenger", name = "Document")
@PrimaryKeyJoinColumn(name = "documentIdentity")
public class Document extends BaseEntity {

	@NotNull
	@Size(min = 32, max = 32)
	@Column(name = "contentHash")
	private byte[] contentHash;

	@NotNull
	@Size(min = 1, max = 63)
	@Pattern(regexp = "[a-z]+/[a-z|.|+|-]+")
	@Column(name = "contentType")
	@XmlAttribute
	private String contentType;

	@NotNull
	@Size(min = 1, max = 16777215)
	@Column(name = "content")
	@XmlTransient
	private byte[] content;

	public Document() {
		this(null, null);
	}

	public Document(String contentType, byte[] content) {
		this.contentHash = content == null ? new byte[32] : mediaHash(content);
		this.contentType = contentType;
		this.content = content;
	}

	@XmlElement
	public byte[] getContentHash() {
		return contentHash;
	}

	public String getContentType() {
		return contentType;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setContent(byte[] content) {
		this.content = content;
		this.contentHash = mediaHash(content);
	}

	public static byte[] mediaHash(byte[] content) {

		byte[] hash = null;

		try {
			hash = MessageDigest.getInstance("SHA-256").digest(content);
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return hash;
	}
}
