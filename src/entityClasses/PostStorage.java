package entityClasses;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import database.Database;

/*******
 * <p><b>Class:</b> PostStorage Class
 * </p>
 * 
 * <p><b>Responsibilities:</b></p>
 * <p>
 * The PostStorage class acts as the data manager for all Post objects in the system. 
 * It provides functionality for creating, retrieving, searching, editing, 
 * and deleting posts. It also serves as the bridge between
 * the GUI Controller classes and the Database layer.
 * 
 * Supports multiple filtering operations (by category, type, date,
 * author, and keyword), allowing the system to dynamically generate views of
 * posts based on user needs.
 * </p>
 * 
 * <p><b>User Stories Supported:</b></p>
 * <ul>
 *   <li>Create a post</li>
 *   <li>View all posts</li>
 *   <li>Filter posts by category, type, date, author, or keyword</li>
 *   <li>Edit own posts</li>
 *   <li>Delete own posts (Staff can delete all posts as well)</li>
 * </ul>
 * 
 * <p><b>Design Notes:</b></p>
 * <ul>
 *   <li>Soft deletion is used instead of removing posts permanently to allow 
 *   history tracking.</li>
 *   <li>Filtering methods skip deleted posts to ensure users only see active content.</li>
 *   <li>Validation checks are performed before database operations to prevent
 *       invalid data from being stored.</li>
 * </ul>
 * 
 * @author Becka Perez Guerrero
 * @version 1.0
 * 
 */
public class PostStorage {
	
	/****
	 * Complete list of all posts in memory.
	 * It is updated from the database when getAllPosts() is called.
	 */
	List<Post> posts = new ArrayList<>();
	
	/***
	 * Filtered list used for a subset part of all posts based on user search.
	 * It is cleared and reused for each filter request.
	 */
	List<Post> filteredPosts = new ArrayList<>();
	
	/****
	 * Reference for the in-memory database so this package has access.
	 * Required for persistence operations (create, update, delete).
	 */
	private Database theDatabase;
	
	/*****
	 * <p>Method: PostStorage(Database db)</p>
	 * 
	 * <p>
	 * Description: This constructor is used to establish postStorage objects.
	 * 
	 * Initializes the object with a database reference.
	 * </p>
	 * 
	 * @param db	Database instance used for storage.
	 * 
	 */
	public PostStorage(Database db) {
		this.theDatabase = db;
	}

	/*****
	 * <p>Method: resolvePostTypeForSubmission(PostType defaultType, PostType selectedType)</p>
	 *
	 * <p>
	 * Description: Resolves the final post type that should be submitted.
	 * If a user selected a type, that explicit selection is preserved.
	 * Otherwise, the configured default type is used.
	 *
	 * This helper centralizes TP3 selection resolution so controller code and tests
	 * can validate a single source of truth for post type persistence behavior.
	 * </p>
	 *
	 * @param defaultType configured default type for new posts
	 * @param selectedType currently selected type, if any
	 * @return selectedType when present; otherwise defaultType
	 */
	public static PostType resolvePostTypeForSubmission(PostType defaultType, PostType selectedType) {
		// Preserve user intent whenever an explicit selection exists.
		if (selectedType != null) {
			return selectedType;
		}

		return defaultType;
	}
	
	/*****
	 * 
	 * This creates a reply and adds it to the list.
	 * 
	 * <p><b>Notes:</b></p>
	 * <p>
	 * Student Story: "As a user, I want to create a post."
	 *
	 * Validation ensures:
	 * - Title and content are not empty
	 * - Title length is within limits
	 * - Type is selected
	 * </p>
	 * 
	 * @param title		title of post
	 * @param content	content of post
	 * @param user		user making the post
	 * @param category	category of post
	 * @param type		type of post
	 * 
	 * @return string to update status and send error messages
	 */
	public String createPost(String title, String content, User user, 
			PostCategory category, PostType type) {
		
		// Validate title
		if(title == null || title.isEmpty()) {
			return "The title cannot be empty";
		}
		
		// Validate body
		if(content == null || content.isEmpty()) {
			return "The content cannot be empty";
		}
		
		// Validate type and category
		if (type == null) {
			return "Please select type and category.";
		}
		
		// Validate title length
		if (title.length() > 120) {
			return "Title is too large";
		}
		
		// Set default post category
		if (category == null) {
			category = PostCategory.GENERAL;
		}
		
		// Create post object
		Post newPost = new Post(0, type, title, category, content, user.getUserName());
		
		// Copy to database
		try {
			theDatabase.registerPost(newPost);
			posts.add(newPost);
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error creating post in database";
		}
		
		return "";
	}
	
	/*****
	 * <p> Method: hasHigherPrivilege(String authorUsername) </p>
	 * <p> Description: Checks if the current user has strictly higher privilege than the author. </p>
	 */
	private boolean hasHigherPrivilege(String authorUsername) {
		int currentPrivilege = theDatabase.getCurrentAdminRole() ? 2 : (theDatabase.getCurrentNewStaffRole() ? 1 : 0);
		int authorPrivilege = theDatabase.getUserPrivilegeLevel(authorUsername);
		return currentPrivilege > authorPrivilege;
	}

	/*****
	 * <p>
	 * Method: ArrayList getAllPosts()
	 * </p>
	 * * <p>Description: Retrieves all posts from the database.
	 * </p>
	 * * @return list with all posts
	 */
	public List<Post> getAllPosts() {
		posts = theDatabase.getPostsList();
		
		// Filter out deleted posts for users without higher privilege
		posts.removeIf(post -> post.isDeleted() && !hasHigherPrivilege(post.getAuthorUsername()));
		
		System.out.println(posts.size());
		return posts;
	}
	
	/*****
	 * <p>
	 * Method: ArrayList getPostsByCategory(PostCategory category)
	 * </p>
	 * * <p>
	 * Description: Filters posts by category.
	 * </p>
	 * * @param category	specifies the category of post to search
	 * * @return filtered list with posts based on category.
	 */
	public List<Post> getPostsByCategory(PostCategory category) {
		filteredPosts.clear();
		
		for (Post post: posts) {
			if(post.isDeleted() && !hasHigherPrivilege(post.getAuthorUsername())) {
				continue;
			}
			
			if(post.getCategory() == category) {
				filteredPosts.add(post);
			}
		}
		
		return filteredPosts;
	}
	
	/*****
	 * <p>
	 * Method: ArrayList getPostsByType(PostType type)
	 * </p>
	 * * <p>
	 * Description: Filters posts by type.
	 * </p>
	 * * @param type	specifies the type of post to search
	 * * @return filtered list with posts based on type.
	 */
	public List<Post> getPostsByType(PostType type) {
		filteredPosts.clear();
		
		for (Post post: posts) {
			if(post.isDeleted() && !hasHigherPrivilege(post.getAuthorUsername())) {
				continue;
			}
			
			if(post.getTypeOfPost() == type) {
				filteredPosts.add(post);
			}
		}
		
		return filteredPosts;
	}
	
	/*****
	 * <p>
	 * Method: ArrayList getPostsByDate(LocalDateTime date)
	 * </p>
	 * * <p>
	 * Description: Filters posts by date.
	 * </p>
	 * * @param date	specifies date to search
	 * * @return filtered list with posts based on date.
	 */
	public List<Post> getPostsByDate(LocalDateTime date) {
		filteredPosts.clear();
		
		for (Post post: posts) {
			if(post.isDeleted() && !hasHigherPrivilege(post.getAuthorUsername())) {
				continue;
			}
			
			if(post.getDate().toLocalDate().equals(date.toLocalDate())) {
				filteredPosts.add(post);
			}
		}
		
		return filteredPosts;
	}
	
	/*****
	 * <p>
	 * Method: ArrayList getPostsByAuthor(String author)
	 * </p>
	 * * <p>
	 * Description: Filters posts by author.
	 * </p>
	 * * @param author	specifies author to search posts
	 * * @return filtered list with posts based on author.
	 */
	public List<Post> getPostsByAuthor(String author) {
		filteredPosts.clear();
		
		for (Post post: posts) {
			if(post.isDeleted() && !hasHigherPrivilege(post.getAuthorUsername())) {
				continue;
			}
			
			if(post.getAuthorUsername().equalsIgnoreCase(author)) {
				filteredPosts.add(post);
			}
		}
		
		return filteredPosts;
	}
	
	/*****
	 * <p>
	 * Method: ArrayList getPostsByKeyword(String keyword)
	 * </p>
	 * * <p>
	 * Description: Filters posts by a certain keyword
	 * </p>
	 * * @param keyword	specifies keyword to search
	 * * @return filtered list with posts based on a keyword
	 */
	public List<Post> getPostsByKeyword(String keyword) {
		filteredPosts.clear();
		
		if (keyword == null || keyword.isEmpty()) {
			return filteredPosts;
		}
		
		String keywordInLower = keyword.toLowerCase();
		
		for (Post post: posts) {
			if(post.isDeleted() && !hasHigherPrivilege(post.getAuthorUsername())) {
				continue;
			}
			
			if(post.getTitle().toLowerCase().contains(keywordInLower) ||
					post.getContent().toLowerCase().contains(keywordInLower)) {
				filteredPosts.add(post);
			}
		}
		
		return filteredPosts;
	}
	
	/*****
	 * This soft deletes a post in the list.
	 * * <p><b>Notes:</b></p>
	 * <p>
	 * Rules:
	 * - Only the author can delete their post
	 * - Post is not removed, only marked deleted
	 * </p>
	 * * @param post		post to be deleted
	 * @param user		user using the system
	 * * @return string to update status and send error messages
	 */
	public String deletePost(Post post, User user) {
		
		if (post == null) {
			return "Post does not exist";
		}
		
		if (post.isDeleted()) {
			return "This post has already been deleted";
		}
		
		if (!post.getAuthorUsername().equals(user.getUserName())) {
			return "You cannot delete someone else's post";
		}
		
		try {
			theDatabase.deletePost(post.getPostId(), "");
			post.deletePost();
			// Do NOT replace the content with deleteMessage, so higher privileges can still read it.
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error deleting from database";
		}
		
		return "";
	}
	
	/*****
	 * This edits a post.
	 * * <p><b>Notes:</b></p>
	 * <p>
	 * Rules:
	 * - The post cannot be deleted
	 * - User's username has to match author name
	 * </p>
	 * * @param post		post to be edited
	 * @param user		user using the system
	 * @param title		new title
	 * @param content	new content
	 * * @return string to update status and send error messages
	 */
	public String editPost(Post post, User user, String title, String content) {
		if (post == null) {
			return "Post does not exist";
		}
		
		if(post.isDeleted()) {
			return "Cannot edit a deleted post";
		}
		if(title == null || title.isEmpty()) {
			return "Title cannot be empty";
		}
		
		if (content == null || content.isEmpty()) {
			return "Content cannot be empty";
		}
		
		if (!post.getAuthorUsername().equals(user.getUserName())) {
			return "You cannot edit someone else's post";
		}
		
		try {
			theDatabase.updatePost(post.getPostId(), title, content);
			post.editPost(title, content);
		} catch(SQLException e) {
			e.printStackTrace();
			return "Error saving to database";
		}
		
		return "";
	}
	
	/*****
	 * <p>
	 * Method: String displayPost(Post post)
	 * </p>
	 * 
	 * <p>
	 * Description: Sets the proper string used to display a Post.
	 * </p>
	 * 
	 * @param post 	post to display
	 * 
	 * @return content that is displayed in UI
	 */
	public String displayPost(Post post) {
		String author = post.getAuthorUsername();
		String content = post.getContent();
		
		return author + "\t\t" + post.getDate().toLocalDate() + "\n\n" + content;
	}
	
	/*****
	 * <p>
	 * Method: Post findPost(int postId)
	 * </p>
	 * 
	 * <p>
	 * Description: This finds a post in the list.
	 * </p>
	 * 
	 * @param postId	id of post
	 * 
	 * @return post if found, null if no post
	 */
	@SuppressWarnings("unused")
	private Post findPost(int postId) {
		for(Post post : posts) {
			if (post.getPostId() == postId) {
				return post;
			}
		}
		return null;
	}

	/*******
	 * <p> Method: updatePostVisibility(long postId, int visibilityLevel) </p>
	 * <p> Description: Updates the visibility level of a specific post. </p>
	 */
	public void updatePostVisibility(long postId, int visibilityLevel) {
		try {
			theDatabase.updatePostVisibility(postId, visibilityLevel);
		} catch (Exception e) {
			System.out.println("Error updating post visibility: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
}
