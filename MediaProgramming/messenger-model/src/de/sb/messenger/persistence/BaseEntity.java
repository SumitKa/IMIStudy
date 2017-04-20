package de.sb.messenger.persistence;
import java.lang.Comparable;
import java.util.List;

import javax.validation.constraints.*;

public class BaseEntity implements Comparable {

	private long identity;
	private int version;
	private long creationTimestamp;
	
	@Min(0)
	private List<Message> messageCaused;
	
	public BaseEntity()
	{
		
	}
	
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
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

	public List<Message> getMessagesCaused() {
		return messageCaused;
	}
}
