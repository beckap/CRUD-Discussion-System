package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p><b>Class: </b> Reply
 * </p>
 * 
 * <p><b>Purpose:</b></p>
 * <p>This class represents a Reply object of the discussion system in the
 * application. It contains the content of the reply alongside other
 * attributes.
 * </p>
 * 
 * <p><b>Responsibilities:</b></p>
 * <ul>
 * 	<li>Create and initialize a reply object and its associated attributes.</li>
 *  <li>Fetch and read a reply object in the system based on associated reply ID
 *  	or post ID.</li>
 * 	<li>Update an existing reply object's attribute values in the system.</li>
 * 	<li>Delete an existing reply object in the system.</li>
 * </ul>
 * 
 * <p><b>User Stories Supported:</b></p>
 * <ul>
 * 	<li>Students can create a reply</li>
 * 	<li>Students can read a reply</li>
 * 	<li>Students can update a reply</li>
 * 	<li>Students can delete a reply</li>
 * </ul>
 * 
 * @author Becka Perez Guerrero
 * @version 1.00 2026-02-16 Initial version
 * 
 * @author Genesee Harmon
 * @version 1.02 2026-03-19 Updated version with proper Javadoc documentation.
 * 
 * @author Hannah Henderson
 * @version 1.03 2026-03-21 Added read receipublishTime tracking
 */
public class Reply {
	/**
	 * Attributes
	 */
	private long replyId;
	private long postId;
	private String content;
	private String authorUsername;
	private LocalDateTime datePosted;
	private boolean isEdited = false;
	private boolean isDeleted = false;
	private boolean isReadByPostAuthor = false;
	private int visibilityLevel = 0;
	private long publishTime = 0;
	
	/*****
	 * <p>
	 * Method: Reply(long id, long postID, String content, String authorUsername)
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This constructor is used to establish initial reply objects.
	 * </p>
	 * 
	 * @param id	   specifies the id of reply 
	 * 
	 * @param postId   specifies the post ID its linked to
	 * 
	 * @param content  specifies the content of the reply
	 * 
	 * @param authorUsername   specifies the author's username
	 * 
	 */
	public Reply(long id, long postId, String content, String authorUsername) {
		this.replyId = id;
		this.postId = postId;
		this.content = content;
		this.authorUsername = authorUsername;
		this.datePosted = LocalDateTime.now();
	}
	
	/*****
	 * <p>
	 * Method: Reply(long id, long postID, String date, String content, 
	 * 			String authorUsername, boolean isEdited, boolean isDeleted,
	 * 			boolean isReadByPostAuthor)
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This constructor is used to establish reply objects with update attributes.
	 * </p>
	 * 
	 * @param id	   specifies the id of reply 
	 * 
	 * @param postId   specifies the post ID its linked to
	 * 
	 * @param date     specifies the date the reply was created
	 * 
	 * @param content  specifies the content of the reply
	 * 
	 * @param authorUsername   specifies the author's username
	 * 
	 * @param isEdited specifies the boolean value of an edited reply
	 * 
	 * @param isDeleted specifies the boolean value of a deleted reply
	 * 
	 * @param isReadByPostAuthor specifies the boolean value of a read reply
	 * 
	 */
	public Reply(long id, long postId, String date, String content, String authorUsername, 
			boolean isEdited, boolean isDeleted, boolean isReadByPostAuthor, int visibilityLevel, long publishTime) {
		this.replyId = id;
		this.postId = postId;
		this.content = content;
		this.authorUsername = authorUsername;
		this.datePosted = LocalDateTime.parse(date);
		this.isEdited = isEdited;
		this.isDeleted = isDeleted;
		this.isReadByPostAuthor = isReadByPostAuthor;
		this.visibilityLevel = visibilityLevel;
		this.publishTime = publishTime;
	}
	
	/*****
	 * <p>
	 * Method: deleteReply()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This soft deletes the reply.
	 * </p>
	 */
	public void deleteReply() {
		isDeleted = true;
	}

	/*****
	 * <p>
	 * Method: editReply()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This edits the content of the reply.
	 * </p>
	 * 
	 * @param content specifies the new content
	 */
	public void editReply(String content) {
		isEdited = true;
		this.content = content;
		this.isReadByPostAuthor = false;
	}
	
	/*****
	 * <p>
	 * Method: long getReplyId()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This getter returns the reply's id.
	 * </p>
	 * 
	 * @return a value for the ID of reply
	 */
	public long getReplyId() {
		return replyId;
	}

	/*****
	 * <p>
	 * Method: long getPostId()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This getter returns the id of the post.
	 * </p>
	 * 
	 * @return a value for the post ID
	 */
	public long getPostId() {
		return postId;
	}

	/*****
	 * <p>
	 * Method: String getContent()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This getter returns the content.
	 * </p>
	 * 
	 * @return content
	 */
	public String getContent() {
		return content;
	}

	/*****
	 * <p>
	 * Method: String getAuthorUsername()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This getter returns the author's username.
	 * </p>
	 * 
	 * @return author's username
	 */
	public String getAuthorUsername() {
		return authorUsername;
	}

	/*****
	 * <p>
	 * Method: LocalDateTime getDatePosted()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This getter returns the date the reply was posted.
	 * </p>
	 * 
	 * @return date posted
	 */
	public LocalDateTime getDatePosted() {
		return datePosted;
	}

	/*****
	 * <p>
	 * Method: boolean isEdited()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This getter returns a boolean.
	 * </p>
	 * 
	 * @return TRUE if the reply is edited or FALSE if not
	 */
	public boolean isEdited() {
		return isEdited;
	}

	/*****
	 * <p>
	 * Method: boolean isDeleted()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This getter returns a boolean.
	 * </p>
	 * 
	 * @return TRUE if the reply is deleted or FALSE if not
	 */
	public boolean isDeleted() {
		return isDeleted;
	}
	
	/*****
	 * <p>
	 * Method: boolean isReadByPostAuthor()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This getter returns a boolean.
	 * </p>
	 * 
	 * @return TRUE if the reply is read by the post author or FALSE if not
	 */
	public boolean isReadByPostAuthor() {
		return isReadByPostAuthor;
	}
	
	/*****
	 * <p>
	 * Method: markRead()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This marks a reply as read by the post author.
	 * </p>
	 */
	protected void markRead() {
		this.isReadByPostAuthor = true;
	}
	
	/*****
	 * <p>
	 * Method: setContent()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This setter sets the content of Post.
	 * </p>
	 * 
	 * @param content	new content
	 */
	protected void setContent(String content) {
		this.content = content;
	}

	/*****
	 * <p>
	 * Method: setReplyId()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This setter sets the reply id.
	 * </p>
	 * 
	 * @param long1		id
	 */
	public void setReplyId(long long1) {
		this.replyId = long1;
	}
	
	// TODO JavaDoc
	public int getVisibilityLevel() {
		return this.visibilityLevel;
	}

	// TODO JavaDoc
	public long getPublishTime() {
		return this.publishTime;
	}
	
	// TODO JavaDoc
	public void setVisibilityLevel(int visibilityLevel) {
		this.visibilityLevel = visibilityLevel;
	}

	// TODO JavaDoc
	public void setPublishTime(long publishTime) {
		this.publishTime = publishTime;
	}
}
