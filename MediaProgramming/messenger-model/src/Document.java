import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.validation.constraints.*;

public class Document extends BaseEntity {

	private byte[] contentHash;
	
	@Size(min = 1, max = 63)
	private String contentType;
	
	@Size(min = 1, max = 16777215)
	private byte content;
	
	public Document() throws NoSuchAlgorithmException
	{
		try {
			this.contentHash = MessageDigest.getInstance("SHA-256").digest();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public byte mediaHash(byte conntent)
	{
		return 0;
	}
}
