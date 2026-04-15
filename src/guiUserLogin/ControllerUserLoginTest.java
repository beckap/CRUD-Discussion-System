package guiUserLogin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.User;
import javafx.application.Platform;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ControllerUserLoginTest {

    @BeforeAll
    static void initFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
            // JavaFX toolkit already initialized.
        }

        fxUnchecked(() -> {
            try {
                Class.forName("guiUserLogin.ViewUserLogin");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    @DisplayName("UL.1 Unknown username denied with generic error")
    void testUnknownUsernameDenied() throws Exception {
        FakeLoginDatabase db = FakeLoginDatabase.baseDb();
        db.userExists = false;
        invalidLogin(db, "ghost_user", "AnyPassword1!");
        assertFalse(db.currentPasswordRequested);
    }

    @Test
    @DisplayName("UL.2 Wrong password denied with generic error")
    void testWrongPasswordDenied() throws Exception {
        FakeLoginDatabase db = FakeLoginDatabase.baseDb();
        db.userExists = true;
        db.currentPassword = "CorrectPassword1!";
        invalidLogin(db, "valid_user", "WrongPassword1!");
        assertFalse(db.rolesRequested);
    }

    @Test
    @DisplayName("UL.3 Empty username/password denied")
    void testEmptyCredentialsDenied() throws Exception {
        FakeLoginDatabase db = FakeLoginDatabase.baseDb();
        db.userExists = false;
        invalidLogin(db, "", "");
    }

    @Test
    @DisplayName("UL.4 Very long username denied")
    void testVeryLongUsernameDenied() throws Exception {
        FakeLoginDatabase db = FakeLoginDatabase.baseDb();
        db.userExists = false;
        String longUsername = "u".repeat(2048);
        invalidLogin(db, longUsername, "Password1!");
        assertEquals(longUsername, db.requestedUsername);
    }

    @Test
    @DisplayName("UL.5 Single-role admin uses admin login path")
    void testSingleRoleAdminUsesAdminPath() throws Exception {
        FakeLoginDatabase db = FakeLoginDatabase.adminDb();
        validLogin(db, "admin_user", "AdminPassword1!");
        assertTrue(db.loginAdminCalled);
        assertFalse(db.loginStaffCalled);
        assertFalse(db.loginStudentCalled);
    }

    @Test
    @DisplayName("UL.6 Single-role student uses student login path")
    void testSingleRoleStudentUsesStudentPath() throws Exception {
        FakeLoginDatabase db = FakeLoginDatabase.studentDb();
        validLogin(db, "student_user", "StudentPassword1!");
        assertTrue(db.loginStudentCalled);
        assertFalse(db.loginAdminCalled);
        assertFalse(db.loginStaffCalled);
    }

    private static void invalidLogin(FakeLoginDatabase db, String username, String password) throws Exception {
        prepareView(username, password);
        setPrivateStaticField(ControllerUserLogin.class, "theDatabase", db);
        assertThrows(NullPointerException.class, () -> fx(() -> ControllerUserLogin.doLogin(null)));
    }

    private static void validLogin(FakeLoginDatabase db, String username, String password) throws Exception {
        prepareView(username, password);
        setPrivateStaticField(ControllerUserLogin.class, "theDatabase", db);
        fx(() -> ControllerUserLogin.doLogin(null));
    }

    private static void prepareView(String username, String password) throws Exception {
        TextField usernameField = fxCreate(TextField::new);
        PasswordField passwordField = fxCreate(PasswordField::new);

        ViewUserLogin.text_Username = usernameField;
        ViewUserLogin.text_Password = passwordField;
        ViewUserLogin.alertUsernamePasswordError = null;

        fx(() -> {
            usernameField.setText(username);
            passwordField.setText(password);
        });
    }

    private static void setPrivateStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    private static void fxUnchecked(Runnable action) {
        try {
            fx(action);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void fx(Runnable action) throws Exception {
        fxCreate(() -> {
            action.run();
            return null;
        });
    }

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

        static FakeLoginDatabase baseDb() {
            return new FakeLoginDatabase();
        }

        static FakeLoginDatabase adminDb() {
            FakeLoginDatabase db = new FakeLoginDatabase();
            db.userExists = true;
            db.currentPassword = "AdminPassword1!";
            db.adminRole = true;
            db.numberOfRoles = 1;
            return db;
        }

        static FakeLoginDatabase studentDb() {
            FakeLoginDatabase db = new FakeLoginDatabase();
            db.userExists = true;
            db.currentPassword = "StudentPassword1!";
            db.studentRole = true;
            db.numberOfRoles = 1;
            return db;
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
            return numberOfRoles;
        }

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
        public void revertOneTimePasswordIfMatch(String username, String usedPassword) {
            // no-op for test
        }
    }
}
