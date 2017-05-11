package de.sb.messenger.persistence;
import java.lang.Comparable;
import java.util.Collections;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity 
@Inheritance
public class BaseEntity implements Comparable<BaseEntity> {

	@NotNull
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id 
	@Column(name = "id", nullable=false, insertable=false, updateable=false)
	private long identity;
	
	@NotNull
	@Column
	@Version
	private int version;
	
	@NotNull
	@Column
	private long creationTimestamp;
	
	@Min(0)
	@NotNull
	@Column
	@OneToMany
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
