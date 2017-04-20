import javax.validation.constraints.*;

public class Message extends BaseEntity {

	private final Person author;
	private final BaseEntity subject;
	
	@Size(min = 1, max = 4093)
	private String body;
	
	public Message(Person author, BaseEntity subject, String body)
	{
		this.author = author;
		this.subject = subject;
		this.setBody(body);
	}

	public Person getAuthor() {
		return author;
	}

	public BaseEntity getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public long getAuthorReference()
	{
		return this.author.getSubjectReference();
	}
}
