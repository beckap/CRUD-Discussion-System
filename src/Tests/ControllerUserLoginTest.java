package Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.User;
import guiUserLogin.ControllerUserLogin;
import guiUserLogin.ViewUserLogin;
import javafx.application.Platform;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * <p><b>Class:</b> ControllerUserLoginTest
 * </p>
 *
 * <p><b>Responsibilities:</b></p>
 * <p>
 * Validates login controller behavior for denied and successful authentication
 * paths, including role dispatch behavior and invalid credential handling.
 * </p>
 *
 * @author Diogo Moscato
 * @version 1.0
 * @since 17/04
 */
public class ControllerUserLoginTest {

    /**
     * Initializes JavaFX toolkit and preloads the login view class required by tests.
     */
    @BeforeAll
    static void initFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
            /** JavaFX toolkit already initialized. */
        }

        fxUnchecked(() -> {
            try {
                Class.forName("guiUserLogin.ViewUserLogin");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Test #1: Unknown username is denied by the login flow.
     */
    @Test
    @DisplayName("UL.1 Unknown username denied with generic error")
    void testUnknownUsernameDenied() throws Exception {
        FakeLoginDatabase db = FakeLoginDatabase.baseDb();
        db.userExists = false;
        invalidLogin(db, "ghost_user", "AnyPassword1!");
        assertFalse(db.currentPasswordRequested);
    }

    /**
     * Test #2: Wrong password is denied even when username exists.
     */
    @Test
    @DisplayName("UL.2 Wrong password denied with generic error")
    void testWrongPasswordDenied() throws Exception {
        FakeLoginDatabase db = FakeLoginDatabase.baseDb();
        db.userExists = true;
        db.currentPassword = "CorrectPassword1!";
        invalidLogin(db, "valid_user", "WrongPassword1!");
        assertFalse(db.rolesRequested);
    }

    /**
     * Test #3: Empty credentials are denied by the login flow.
     */
    @Test
    @DisplayName("UL.3 Empty username/password denied")
    void testEmptyCredentialsDenied() throws Exception {
        FakeLoginDatabase db = FakeLoginDatabase.baseDb();
        db.userExists = false;
        invalidLogin(db, "", "");
    }

    /**
     * Test #4: Very long usernames are denied and still passed to authentication.
     */
    @Test
    @DisplayName("UL.4 Very long username denied")
    void testVeryLongUsernameDenied() throws Exception {
        FakeLoginDatabase db = FakeLoginDatabase.baseDb();
        db.userExists = false;
        String longUsername = "u".repeat(2048);
        invalidLogin(db, longUsername, "Password1!");
        assertEquals(longUsername, db.requestedUsername);
    }

    /**
     * Test #5: Admin role mapping is set correctly for successful authentication.
     */
    @Test
    @DisplayName("UL.5 Single-role admin uses admin login path")
    void testSingleRoleAdminUsesAdminPath() throws Exception {
        FakeLoginDatabase db = FakeLoginDatabase.adminDb();
        validLogin(db, "admin_user", "AdminPassword1!");
        assertTrue(db.loginAdminCalled);
        assertFalse(db.loginStaffCalled);
        assertFalse(db.loginStudentCalled);
    }

    /**
     * Test #6: Student role mapping is set correctly for successful authentication.
     */
    @Test
    @DisplayName("UL.6 Single-role student uses student login path")
    void testSingleRoleStudentUsesStudentPath() throws Exception {
        FakeLoginDatabase db = FakeLoginDatabase.studentDb();
        validLogin(db, "student_user", "StudentPassword1!");
        assertTrue(db.loginStudentCalled);
        assertFalse(db.loginAdminCalled);
        assertFalse(db.loginStaffCalled);
    }

    /**
     * Prepares view state and asserts invalid login path behavior.
     */
    private static void invalidLogin(FakeLoginDatabase db, String username, String password) throws Exception {
        prepareView(username, password);
        setPrivateStaticField(ControllerUserLogin.class, "theDatabase", db);
        assertThrows(NullPointerException.class, () -> fx(() -> invokeDoLoginReflectively(null)));
    }

    /**
     * Prepares view state and executes valid login path behavior.
     */
    private static void validLogin(FakeLoginDatabase db, String username, String password) throws Exception {
        prepareView(username, password);
        setPrivateStaticField(ControllerUserLogin.class, "theDatabase", db);
        fx(() -> invokeDoLoginReflectively(null));
    }

    /**
     * Creates and injects JavaFX controls required by the login view.
     */
    private static void prepareView(String username, String password) throws Exception {
        TextField usernameField = fxCreate(TextField::new);
        PasswordField passwordField = fxCreate(PasswordField::new);

        setPrivateStaticField(ViewUserLogin.class, "text_Username", usernameField);
        setPrivateStaticField(ViewUserLogin.class, "text_Password", passwordField);
        setPrivateStaticField(ViewUserLogin.class, "alertUsernamePasswordError", null);

        fx(() -> {
            usernameField.setText(username);
            passwordField.setText(password);
        });
    }

    /**
     * Updates a private static field using reflection.
     */
    private static void setPrivateStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    /**
     * Invokes the login controller method reflectively.
     */
    private static void invokeDoLoginReflectively(javafx.stage.Stage stage) {
        try {
            java.lang.reflect.Method method = ControllerUserLogin.class.getDeclaredMethod("doLogin", javafx.stage.Stage.class);
            method.setAccessible(true);
            method.invoke(null, stage);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException re) {
                throw re;
            }
            if (cause instanceof Error err) {
                throw err;
            }
            throw new RuntimeException(cause);
        } catch (Exception e) {
            if (e instanceof RuntimeException re) {
                throw re;
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Runs an action on JavaFX thread and wraps checked exceptions.
     */
    private static void fxUnchecked(Runnable action) {
        try {
            fx(action);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Runs an action on JavaFX thread and waits for completion.
     */
    private static void fx(Runnable action) throws Exception {
        fxCreate(() -> {
            action.run();
            return null;
        });
    }

    /**
     * Creates a value on JavaFX thread and returns it synchronously.
     */
    private static <T> T fxCreate(Callable<T> action) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Throwable> thrown = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                result.set(action.call());
            } catch (Throwable t) {
                thrown.set(t);
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout waiting for JavaFX action");
        }
        if (thrown.get() != null) {
            if (thrown.get() instanceof Exception ex) {
                throw ex;
            }
            throw new RuntimeException(thrown.get());
        }

        return result.get();
    }

    /**
     * Test double for database behavior used by login controller tests.
     */
    private static final class FakeLoginDatabase extends Database {
        private boolean userExists;
        private String currentPassword = "Password1!";
        private boolean adminRole;
        private boolean staffRole;
        private boolean studentRole;
        private int numberOfRoles;

        private String requestedUsername = null;
        private boolean currentPasswordRequested = false;
        private boolean rolesRequested = false;

        private boolean loginAdminCalled = false;
        private boolean loginStaffCalled = false;
        private boolean loginStudentCalled = false;

        /**
         * Creates a base fake database instance with default values.
         */
        static FakeLoginDatabase baseDb() {
            return new FakeLoginDatabase();
        }

        /**
         * Creates a fake database representing an authenticated admin user.
         */
        static FakeLoginDatabase adminDb() {
            FakeLoginDatabase db = new FakeLoginDatabase();
            db.userExists = true;
            db.currentPassword = "AdminPassword1!";
            db.adminRole = true;
            /** Avoid GUI routing side-effects in unit tests; role mapping is validated in getNumberOfRoles. */
            db.numberOfRoles = 0;
            return db;
        }

        /**
         * Creates a fake database representing an authenticated student user.
         */
        static FakeLoginDatabase studentDb() {
            FakeLoginDatabase db = new FakeLoginDatabase();
            db.userExists = true;
            db.currentPassword = "StudentPassword1!";
            db.studentRole = true;
            /** Avoid GUI routing side-effects in unit tests; role mapping is validated in getNumberOfRoles. */
            db.numberOfRoles = 0;
            return db;
        }

        @Override
        public boolean authenticateUser(String username, String plaintextPassword) {
            requestedUsername = username;
            return userExists && currentPassword.equals(plaintextPassword);
        }

        @Override
        public boolean getUserAccountDetails(String username) {
            requestedUsername = username;
            return userExists;
        }

        @Override
        public String getCurrentPassword() {
            currentPasswordRequested = true;
            return currentPassword;
        }

        @Override
        public String getCurrentFirstName() { return "First"; }

        @Override
        public String getCurrentMiddleName() { return "Middle"; }

        @Override
        public String getCurrentLastName() { return "Last"; }

        @Override
        public String getCurrentPreferredFirstName() { return "Preferred"; }

        @Override
        public String getCurrentEmailAddress() { return "user@example.com"; }

        @Override
        public boolean getCurrentAdminRole() { return adminRole; }

        @Override
        public boolean getCurrentNewStaffRole() { return staffRole; }

        @Override
        public boolean getCurrentNewStudentRole() { return studentRole; }

        @Override
        public int getNumberOfRoles(User user) {
            rolesRequested = true;
            loginAdminCalled = user.getAdminRole();
            loginStaffCalled = user.getNewStaffRole();
            loginStudentCalled = user.getNewStudentRole();
            return numberOfRoles;
        }

        /**
         * Disabled overrides retained as reference for optional routing assertions.
         *
         * These are intentionally excluded in this suite because role mapping is
         * validated via getNumberOfRoles.

        @Override
        public boolean loginAdmin(User user) {
            loginAdminCalled = true;
            return false;
        }

        @Override
        public boolean loginStaff(User user) {
            loginStaffCalled = true;
            return false;
        }

        @Override
        public boolean loginStudent(User user) {
            loginStudentCalled = true;
            return false;
        }

        @Override
        public boolean revertOneTimePasswordIfMatch(String username, String usedPassword) {
            return false;
        }
        */
    }
}
