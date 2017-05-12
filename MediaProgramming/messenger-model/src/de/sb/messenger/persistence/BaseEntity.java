package de.sb.messenger.persistence;
import java.lang.Comparable;
import java.util.Collections;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@Entity 
@Inheritance
@XmlAccessorType (XmlAccessType.NONE)
@XmlType
@XmlSeeAlso(value = { Document.class, Message.class, Person.class })
public class BaseEntity implements Comparable<BaseEntity> {

	@NotNull
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	@Column(name = "identity", nullable=false, insertable=false, updatable=false)
	private long identity;
	
	@NotNull
	@Column(name = "version", nullable=false, insertable=false)
	@Version
	private int version;
	
	@NotNull
	@Column(name = "creationTimestamp", nullable=false, insertable=false, updatable=false)
	private long creationTimestamp;
	
	@Min(0)
	@NotNull
	@Column(name = "messageCaused", nullable=false, updatable=false, insertable=false)
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
