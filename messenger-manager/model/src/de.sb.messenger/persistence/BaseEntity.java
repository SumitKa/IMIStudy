package de.sb.messenger.persistence;

import javax.persistence.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import javax.xml.bind.annotation.*;

import java.util.Collections;
import java.util.Set;

@Entity
@Table(schema = "messenger", name = "BaseEntity")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "discriminator")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
@XmlSeeAlso({Person.class, Document.class, Message.class})
@XmlRootElement
public class BaseEntity implements Comparable<BaseEntity> {

	@NotNull
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "identity")
	@XmlElement
	private long identity;

	@NotNull
	@Min(1)
	@Column(name = "version")
	@XmlElement
	private int version;

	@NotNull
	@Column(name = "creationTimestamp")
	private long creationTimestamp;

	@Size
	@OneToMany(mappedBy = "subject")
	private Set<Message> messageCaused;

	public BaseEntity() {
		this.identity = 0;
		this.version = 1;
		this.creationTimestamp = System.currentTimeMillis();
		this.messageCaused = Collections.emptySet();
	}

	@XmlID
	public long getIdentity() {
		return identity;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@XmlElement
	public long getCreationTimestamp() {
		return creationTimestamp;
	}

	@XmlElement
	public Set<Message> getMessagesCaused() {
		return messageCaused;
	}

	@Override
	public int compareTo(BaseEntity o) {
		return Long.compare(this.identity, o.getIdentity());
	}
}

