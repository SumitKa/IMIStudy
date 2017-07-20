package de.sb.messenger.persistence;
import java.lang.Comparable;
import java.util.Collections;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@Entity 
@Inheritance (strategy = InheritanceType.JOINED)
@DiscriminatorColumn (name = "discriminator", discriminatorType = DiscriminatorType.STRING)
@XmlAccessorType (XmlAccessType.NONE)
@XmlType
@XmlSeeAlso(value = { Document.class, Person.class, Message.class })
public class BaseEntity implements Comparable<BaseEntity> {

	@NotNull
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable=false, insertable=false, updatable=false)
	private long identity;
	
	@NotNull
	@Column(nullable=false, insertable=false)
	@Version
	private int version;
	
	@NotNull
	@Column(nullable=false, insertable=false, updatable=false)
	private long creationTimestamp;
	
	@Min(0)
	@NotNull
	@OneToMany(mappedBy = "author")
	private Set<Message> messageCaused;
	
	public BaseEntity() {
		this.version = 1;
		this.creationTimestamp = System.currentTimeMillis();
		this.messageCaused = Collections.emptySet();
	}
	
	@Override
	public int compareTo(BaseEntity o) {
		return Long.compare(this.identity, o.identity);
	}

	public long getIdentity() {
		return identity;
	}

	public int getVersion() {
		return version;
	}

	public long getCreationTimestamp() {
		return creationTimestamp;
	}

	public Set<Message> getMessagesCaused() {
		return messageCaused;
	}
}
