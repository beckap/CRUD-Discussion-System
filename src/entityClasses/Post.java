package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p><b>Class: </b> Post
 * </p>
 * 
 * <p><b>Purpose:</b></p>
 * <p>This class represents a Post object of the discussion system in the
 * application. It contains the title and content of the post alongside
 * other attributes.
 * </p>
 * 
 * <p><b>Responsibilities:</b></p>
 * <ul>
 * 	<li>Create and initialize a post object and its associated attributes.</li>
 *  <li>Fetch and read a post object in the system based on associated post ID, title, 
 *  	type, and/or category.</li>
 * 	<li>Update an existing post object's attribute values in the system.</li>
 * 	<li>Delete an existing post object in the system.</li>
 * </ul>
 * 
 * <p><b>User Stories Supported:</b></p>
 * <ul>
 * 	<li>Students can create a post</li>
 * 	<li>Students can read a post</li>
 * 	<li>Students can update a post</li>
 * 	<li>Students can delete a post</li>
 * </ul>
 * 
 * @author Becka Perez Guerrero
 * @version 1.00 2026-02-16 Initial version
 * 
 * @author Genesee Harmon
 * @version 1.02 2026-03-19 Updated version with proper Javadoc documentation.
 * 
 */
public class Post {
	/**
	 * Attributes
	 */
	private LocalDateTime datePosted;
	private long postId;
	private PostType typeOfPost;
	private String title;
	private PostCategory category;
	private String content;
	private String authorUsername;
	private boolean isEdited = false;
	private boolean isDeleted = false;
	private int visibilityLevel = 0;
	private long publishTime = 0;
	
	/*****
	 * <p>
	 * Method: Post(long id, PostType type, String title, PostCategory category, 
			String content, String authorUsername)
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This constructor is used to establish initial post objects.
	 * </p>
	 * 
	 * @param id	   specifies the id  
	 * 
	 * @param type		specifies the type of post
	 * 
	 * @param title    specifies the title of the post
	 * 
	 * @param category specifies the category the post is under
	 * 
	 * @param content  specifies the content of the post
	 * 
	 * @param authorUsername	specifies the author's username
	 * 
	 */
	public Post(long id, PostType type, String title, PostCategory category, 
			String content, String authorUsername) {
		this.postId = id;
		this.authorUsername = authorUsername;
		this.title = title;
		this.typeOfPost = type;
		this.category = category;
		this.content = content;
		this.datePosted = LocalDateTime.now();
	}
	
	/*****
	 * <p>
	 * Method: Post(long id, String date, PostType type, String title, PostCategory category, 
			String content, String authorUsername, boolean isEdited, boolean isDeleted)
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This constructor is used to establish post objects with update attributes.
	 * </p>
	 * 
	 * @param id	   specifies the id  
	 * 
	 * @param date     specifies the date the post was created
	 * 
	 * @param type		specifies the type of post
	 * 
	 * @param title    specifies the title of the post
	 * 
	 * @param category specifies the category the post is under
	 * 
	 * @param content  specifies the content of the post
	 * 
	 * @param authorUsername	specifies the author's username
	 * 
	 * @param isEdited specifies the boolean value of an edited post
	 * 
	 * @param isDeleted specifies the boolean value of a deleted post
	 * @param publishTime 
	 * @param visibilityLevel 
	 * 
	 */
	public Post(long id, String date, String type, String title, String category, 
			String content, String authorUsername, boolean isEdited, boolean isDeleted, int visibilityLevel, long publishTime) {
		this.postId = id;
		this.authorUsername = authorUsername;
		this.title = title;
		this.typeOfPost = PostType.valueOf(type);
		this.category = PostCategory.valueOf(category);
		this.content = content;
		this.datePosted = LocalDateTime.parse(date);
		this.isDeleted = isDeleted;
		this.isEdited = isEdited;
		this.visibilityLevel = visibilityLevel;
		this.publishTime = publishTime;
	}
	
	/*****
	 * <p>
	 * Method: editPost()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This edits the content of the post.
	 * </p>
	 * 
	 * @param title	  specifies the new title
	 * @param content specifies the new content
	 */
	public void editPost(String title, String content) {
		this.title = title;
		this.content = content;
		isEdited = true;
	}
	
	/*****
	 * <p>
	 * Method: deletePost()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This soft deletes the post.
	 * </p>
	 */
	public void deletePost() {
		isDeleted = true;
	}
	
	/*****
	 * <p>
	 * Method: long getPostId()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This getter returns the id.
	 * </p>
	 * 
	 * @return a value for the ID
	 */
	public long getPostId() {
		return postId;
	}
	
	/*****
	 * <p>
	 * Method: String getTitle()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This getter returns the title of post.
	 * </p>
	 * 
	 * @return title
	 */
	public String getTitle() {
		return title;
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
	 * @return username of author
	 */
	public String getAuthorUsername() {
		return authorUsername;
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
	 * Method: PostType getTypeOfPost()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This getter returns the type of post.
	 * </p>
	 * 
	 * @return type of post (POST or QUESTION)
	 */
	public PostType getTypeOfPost() {
		return typeOfPost;
	}
	
	/*****
	 * <p>
	 * Method: PostCategory getCategory()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This getter returns the category.
	 * </p>
	 * 
	 * @return category of post (GENERAL, HOMEWORK, LECTURES, or EXAMS)
	 */
	public PostCategory getCategory() {
		return category;
	}
	
	/*****
	 * <p>
	 * Method: LocalDateTime getDate()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This getter returns the date the post was posted.
	 * </p>
	 * 
	 * @return date posted
	 */
	public LocalDateTime getDate() {
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
	 * @return TRUE if the post is edited or FALSE if not
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
	 * @return TRUE if the post is deleted or FALSE if not
	 */
	public boolean isDeleted() {
		return isDeleted;
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
	 * Method: setPostId()
	 * </p>
	 * 
	 * <p>
	 * DescripublishTimeion: This setter sets the post id.
	 * </p>
	 * 
	 * @param long1		id
	 */
	public void setPostId(long long1) {
		this.postId = long1;
		
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
