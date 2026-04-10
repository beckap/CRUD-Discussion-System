package entityClasses;

public class Invitation {

	private String code;
	private String emailAddress;
	private String role;
	private String sender;
	private long dateSent;
	private String recipient;
	private long dateAccepted;
	private String status; // "Outstanding", "Accepted", "Revoked"

	public Invitation(String code, String email, String role, String sender, long dateSent, String recipient,
			long dateAccepted, String status) {
		this.code = code;
		this.emailAddress = email;
		this.role = role;
		this.sender = sender;
		this.dateSent = dateSent;
		this.recipient = recipient;
		this.dateAccepted = dateAccepted;
		this.status = status;
	}

	// Getters:

	public String getCode() {
		return code;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getRole() {
		return role;
	}

	public String getSender() {
		return sender;
	}

	public long getDateSent() {
		return dateSent;
	}

	public String getRecipient() {
		return recipient;
	}

	public long getDateAccepted() {
		return dateAccepted;
	}

	public String getStatus() {
		return status;
	}
}
