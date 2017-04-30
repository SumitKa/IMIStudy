package de.sb.messenger.persistence;
import java.lang.Comparable;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity public class BaseEntity implements Comparable<BaseEntity> {

	@NotNull
	@GeneratedValue
	@Id
	private long identity;
	@NotNull
	private int version;
	@NotNull
	private long creationTimestamp;
	
	@Min(0)
	@NotNull
	private Set<Message> messageCaused;
	
	protected BaseEntity() {
		
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
