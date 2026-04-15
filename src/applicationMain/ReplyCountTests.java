package applicationMain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import database.Database;
import entityClasses.AnalyzerException;
import entityClasses.Reply;
import entityClasses.ReplyAnalyzer;
import entityClasses.ReplyStorage;
import entityClasses.User;

/**
 * <p><b>Class:</b> ReplyCountTests (JUnit)
 * </p>
 * 
 * <p><b>Responsibilities:</b></p>
 * <p>
 *   This class tests the prototype logic and its class code.
 * </p>
 * 
 * 
 * <p><b>Design notes:</b></p>
 * <ul>
 *   <li>JUnit tests implemented using \@Test method decorator.</li>
 *   <li>Reproducible test environment and database implemented using Mockito.</li>
 *   <li>Value validation implemented using junit.Assert methods</li>
 *   <li>Database methods handled using Mockito.when()</li>
 * </ul>
 * 
 * @author Becka Perez Guerrero
 * @version 1.0 Initial version
 */
class ReplyCountTests {
	
	/***
	 * Student user for testing
	 */
	User student1 = new User();
	
	/**
	 * Student user for testing
	 */
	User student2 = new User();

	/***
	 * Tests that the analyzer correctly returns 0 when a student has no replies.
	 * 
	 */
	@Test
	void correctlyCountsZeroReplies() {
		Database mockDB = mock(Database.class);
		ReplyStorage storage = new ReplyStorage(mockDB);
		List<Reply> fakeList = new ArrayList<>();
		student1.setUserName("rperezg4");
		
		when(mockDB.getRepliesList()).thenReturn(fakeList);
		try {
			ReplyAnalyzer analyzer = new ReplyAnalyzer(storage, "rperezg4");
			int count = analyzer.countStudentReplies();
			assertEquals(0, count);
		} catch (AnalyzerException e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * Tests that the analyzer correctly counts multiple replies for a student.
	 */
	@Test
	void correctlyCountsReplies() {
		Database mockDB = mock(Database.class);
		ReplyStorage storage = new ReplyStorage(mockDB);
		List<Reply> fakeList = new ArrayList<>();
		student1.setUserName("rperezg4");
		for (int i = 1; i <= 15; i++) {
			storage.createReply("Content of reply #" + i, student1, i);
			fakeList.add(new Reply(i, 1, "Content of reply #" + i, student1.getUserName()));
		}
		
		when(mockDB.getRepliesList()).thenReturn(fakeList);
		try {
			ReplyAnalyzer analyzer = new ReplyAnalyzer(storage, "rperezg4");
			int count = analyzer.countStudentReplies();
			assertEquals(15, count);
		} catch (AnalyzerException e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * Tests that only one reply per post is counted as a unique reply.
	 * 
	 * This verifies that multiple replies to the same post do not count towards
	 * the participation requirement.
	 */
	@Test
	void correctlyCountsOneReplyPerPost() {
		Database mockDB = mock(Database.class);
		ReplyStorage storage = new ReplyStorage(mockDB);
		List<Reply> fakeList = new ArrayList<>();
		student1.setUserName("rperezg4");
		
		
		for (int i = 1; i <= 1; i++) {
			for (int j = 1; j <=2; j++) {
				storage.createReply("Content of reply #" + j, student1, i);
				fakeList.add(new Reply(j, i, "Content of reply #" + j, student1.getUserName()));
			}
		}
		
		when(mockDB.getRepliesList()).thenReturn(fakeList);
		try {
			ReplyAnalyzer analyzer = new ReplyAnalyzer(storage, "rperezg4");
			int count = analyzer.countUniqueReplies();
			assertEquals(1, count);
		} catch (AnalyzerException e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * Tests that replies belonging to other students are not included in the
	 * count.
	 */
	@Test
	void countsRepliesOnlyForCorrectStudent() {
		Database mockDB = mock(Database.class);
		ReplyStorage storage = new ReplyStorage(mockDB);
		List<Reply> fakeList = new ArrayList<>();
		student1.setUserName("rperezg4");
		student2.setUserName("gharmon1");
		
		// Creates 3 replies to one post from student 1
		for (int i = 1; i <= 1; i++) {
			for (int j = 1; j <= 3; j++) {
				storage.createReply("Content of reply #" + j, student1, i);
				fakeList.add(new Reply(j, i, "Content of reply #" + j, student1.getUserName()));
			}
		}
		
		// Creates 3 replies to the same post from student 2
		for(int i = 1; i <= 1; i++) {
			for (int j = 1; j <= 2; j++) {
				storage.createReply("Content of reply #" + j, student2, i);
				fakeList.add(new Reply(j, i, "Content of reply #" + j, student2.getUserName()));
			}
		}
		when(mockDB.getRepliesList()).thenReturn(fakeList);
		try {
			ReplyAnalyzer analyzer = new ReplyAnalyzer(storage, "rperezg4");
			int count = analyzer.countStudentReplies();
			assertEquals(3, count);
		} catch (AnalyzerException e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * Tests that a student with zero replies does not meet the participation requirement.
	 */
	@Test
	void zeroRepliesEvaluation() {
		Database mockDB = mock(Database.class);
		ReplyStorage storage = new ReplyStorage(mockDB);
		List<Reply> fakeList = new ArrayList<>();
		student1.setUserName("rperezg4");
		
		when(mockDB.getRepliesList()).thenReturn(fakeList);
		try {
			ReplyAnalyzer analyzer = new ReplyAnalyzer(storage, "rperezg4");
			boolean progress = analyzer.evaluateStudentParticipation();
			assertEquals(false, progress);
		} catch (AnalyzerException e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * Tests that a student with fewer than 3 unique replies fails the requirement.
	 */
	@Test
	void belowParticipationRequirement() {
		Database mockDB = mock(Database.class);
		ReplyStorage storage = new ReplyStorage(mockDB);
		List<Reply> fakeList = new ArrayList<>();
		student1.setUserName("rperezg4");
		for (int i = 1; i <= 2; i++) {
			for (int j = 1; j <= 3; j++) {
				storage.createReply("Content of reply #" + j, student1, i);
				fakeList.add(new Reply(j, i, "Content of reply #" + j, student1.getUserName()));
			}
		}
		
		when(mockDB.getRepliesList()).thenReturn(fakeList);
		try {
			ReplyAnalyzer analyzer = new ReplyAnalyzer(storage, "rperezg4");
			boolean progress = analyzer.evaluateStudentParticipation();
			assertEquals(false, progress);
		} catch (AnalyzerException e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * Tests that a student with exactly 3 unique replies satisfies the requirement.
	 */
	@Test
	void studentHas3Replies() {
		Database mockDB = mock(Database.class);
		ReplyStorage storage = new ReplyStorage(mockDB);
		List<Reply> fakeList = new ArrayList<>();
		student1.setUserName("rperezg4");
		
		// Creates 3 unique replies to 3 different posts to satisfy requirements
		for (int i = 1; i <= 3; i++) {
			for (int j = 1; j <= 3; j++) {
				storage.createReply("Content of reply #" + j, student1, i);
				fakeList.add(new Reply(j, i, "Content of reply #" + j, student1.getUserName()));
			}
		}
		
		when(mockDB.getRepliesList()).thenReturn(fakeList);
		try {
			ReplyAnalyzer analyzer = new ReplyAnalyzer(storage, "rperezg4");
			boolean progress = analyzer.evaluateStudentParticipation();
			assertEquals(3, analyzer.countUniqueReplies());
			assertEquals(true, progress);
		} catch (AnalyzerException e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * Tests that a student with more than 3 unique replies satisfies the requirement.
	 */
	@Test
	void aboveParticipationRequirement() {
		Database mockDB = mock(Database.class);
		ReplyStorage storage = new ReplyStorage(mockDB);
		List<Reply> fakeList = new ArrayList<>();
		student1.setUserName("rperezg4");
		for (int i = 1; i <= 5; i++) {
			for (int j = 1; j <= 2; j++) {
				storage.createReply("Content of reply #" + j, student1, i);
				fakeList.add(new Reply(j, i, "Content of reply #" + j, student1.getUserName()));
			}
		}
		
		when(mockDB.getRepliesList()).thenReturn(fakeList);
		try {
			ReplyAnalyzer analyzer = new ReplyAnalyzer(storage, "rperezg4");
			boolean progress = analyzer.evaluateStudentParticipation();
			assertEquals(5, analyzer.countUniqueReplies());
			assertEquals(true, progress);
		} catch (AnalyzerException e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * Tests that a null username throws an AnalyzerException.
	 */
	@Test
	void nullStudentUsernameRejection() {
		Database mockDB = mock(Database.class);
		ReplyStorage storage = new ReplyStorage(mockDB);
		try {
			ReplyAnalyzer analyzer = new ReplyAnalyzer(storage, null);
			assertEquals(0, analyzer.countUniqueReplies());
		} catch (AnalyzerException e) {
			assertEquals("Cannot analyze a null username.", e.getMessage());
		}
	}
	
	/***
	 * Tests that an empty username throws an AnalyzerException.
	 */
	@Test
	void unknownStudentUsernameRejection() {
		Database mockDB = mock(Database.class);
		ReplyStorage storage = new ReplyStorage(mockDB);
		try {
			ReplyAnalyzer analyzer = new ReplyAnalyzer(storage, "");
			assertEquals(0, analyzer.countUniqueReplies());
		} catch (AnalyzerException e) {
			assertEquals("Cannot analyze an empty username.", e.getMessage());
		}
	}

}
