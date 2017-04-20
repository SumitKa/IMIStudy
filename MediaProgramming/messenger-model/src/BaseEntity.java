import java.lang.Comparable;

import com.sun.istack.internal.Nullable;

public class BaseEntity implements Comparable {

	private long identity;
	private int version;
	private long creationTimestamp;
	
	@Nullable
	private Message messageCaused;
	
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
}
