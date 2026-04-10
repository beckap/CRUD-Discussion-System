package guiDiscussionSystem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import entityClasses.Post;
import entityClasses.PostCategory;
import entityClasses.PostType;

/*******
 * <p>
 * Title: ModelDiscussionSystem Class.
 * </p>
 * 
 * <p>
 * Description: The Discussion System page Model.
 * This class helps with data manipulation for this GUI page.
 * </p>
 * 
 * @author Becka Perez Guerrero
 * @version 1.00 2026-02-13 Initial version
 * 
 * @author Diogo Moscato
 * @version 2.00 2026-03-18 Added search filtering
 * 
 * @author Hannah Henderson
 * @version 2.1 2026-03-21 Refactored filtering logic, added "my posts" filter
 */
public class ModelDiscussionSystem {
	/**********
	 * <p>
	 * Method: refreshPostList() 
	 * </p>
	 * 
	 * <p> Description: This method populates the list displayed in the GUI
	 * for all posts. 
	 * </p>
	 * 
	 */
	protected static void refreshPostList() {
		ControllerDiscussionSystem.basePosts = new ArrayList<>(ControllerDiscussionSystem.postStorage.getAllPosts());
		refreshSearchedPosts();
	}

	/**********
	 * <p>
	 * Method: refreshFilteredPosts() 
	 * </p>
	 * 
	 * <p> 
	 * Description: This method populates the list displayed in the GUI
	 * for filtered posts when the user makes a filter. 
	 * </p>
	 * 
	 */
	protected static void refreshFilteredPosts() {
		List<Post> allPosts = ControllerDiscussionSystem.postStorage.getAllPosts();
		List<Post> filteredPosts = new ArrayList<>();
		
		boolean categoryOrTypeSelected = false;
		
		if (ViewDiscussionSystem.discussion.isSelected()) {
			filteredPosts.addAll(ControllerDiscussionSystem.postStorage.getPostsByType(PostType.POST));
			categoryOrTypeSelected = true;
		}
		if (ViewDiscussionSystem.question.isSelected()) {
			filteredPosts.addAll(ControllerDiscussionSystem.postStorage.getPostsByType(PostType.QUESTION));
			categoryOrTypeSelected = true;
		}
		if (ViewDiscussionSystem.general.isSelected()) {
			filteredPosts.addAll(ControllerDiscussionSystem.postStorage.getPostsByCategory(PostCategory.GENERAL));
			categoryOrTypeSelected = true;
		}
		if (ViewDiscussionSystem.homework.isSelected()) {
			filteredPosts.addAll(ControllerDiscussionSystem.postStorage.getPostsByCategory(PostCategory.HOMEWORK));
			categoryOrTypeSelected = true;
		}
		if (ViewDiscussionSystem.exams.isSelected()) {
			filteredPosts.addAll(ControllerDiscussionSystem.postStorage.getPostsByCategory(PostCategory.EXAMS));
			categoryOrTypeSelected = true;
		}
		if (ViewDiscussionSystem.lectures.isSelected()) {
			filteredPosts.addAll(ControllerDiscussionSystem.postStorage.getPostsByCategory(PostCategory.LECTURES));
			categoryOrTypeSelected = true;
		}
		
		boolean anySelected = categoryOrTypeSelected || ViewDiscussionSystem.onlyMyPosts.isSelected();
		
		if (!anySelected) {
			refreshPostList();
			return;
		}
		
		// If no additive filters, then add ALL THE POSTS before doing subtractive filters
		if (!categoryOrTypeSelected) {
			filteredPosts.addAll(allPosts);
		}
		
		// Remove duplicates
		HashSet<Post> uniques = new HashSet<>();
		filteredPosts.removeIf(post -> !uniques.add(post));
		
		// Filter based on author if "Only show my posts" is checked
		if (ViewDiscussionSystem.onlyMyPosts.isSelected()) {
			filteredPosts.removeIf(post ->
				!post.getAuthorUsername().equals(ViewDiscussionSystem.theUser.getUserName())
			);
		}
		
		ControllerDiscussionSystem.basePosts = new ArrayList<>(filteredPosts);
		refreshSearchedPosts();
	}

	/**********
	 * <p>
	 * Method: refreshSearchedPosts()
	 * </p>
	 *
	 * <p>
	 * Description: This method updates the list displayed in the GUI by applying
	 * a keyword search on top of the currently active base list (all posts or the
	 * latest filter result).
	 * </p>
	 */
	protected static void refreshSearchedPosts() {
		ViewDiscussionSystem.postsList.getItems().clear();

		List<Post> postsToDisplay = new ArrayList<>(ControllerDiscussionSystem.basePosts);
		String searchKeyword = ViewDiscussionSystem.searchField.getText();

		if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
			String normalizedKeyword = searchKeyword.trim().toLowerCase(Locale.ROOT);
			postsToDisplay.removeIf(post -> !matchesSearchKeyword(post, normalizedKeyword));
		}

		ViewDiscussionSystem.postsList.getItems().addAll(postsToDisplay);
		ControllerDiscussionSystem.setupPostSelection();
	}

	/**********
	 * <p>
	 * Method: matchesSearchKeyword(Post post, String keyword)
	 * </p>
	 *
	 * <p>
	 * Description: Checks whether a post matches the typed search keyword.
	 * The keyword is compared against title, content, and author username.
	 * </p>
	 *
	 * @param post the post to validate against the search keyword
	 * @param keyword the normalized keyword entered by the user
	 * @return true when at least one searchable field contains the keyword
	 */
	private static boolean matchesSearchKeyword(Post post, String keyword) {
		String title = post.getTitle() == null ? "" : post.getTitle().toLowerCase(Locale.ROOT);
		String content = post.getContent() == null ? "" : post.getContent().toLowerCase(Locale.ROOT);
		String author = post.getAuthorUsername() == null ? "" : post.getAuthorUsername().toLowerCase(Locale.ROOT);

		return title.contains(keyword) || content.contains(keyword) || author.contains(keyword);
	}
	
}