package guiGradingDashboard;



public class ControllerGradingDashboard {
	
	/**********
	 * <p>
	 * Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method determines the current state of the window and then
	 * establishes the appropriate list of widgets in the Pane to show the proper
	 * set of current values. </p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewGradingDashboard.theRootPane.getChildren().clear();
		
		// Define the view to show the user
		ViewGradingDashboard.theRootPane.getChildren().addAll(ViewGradingDashboard.button_Return);

		// Set the title for the window
		ViewGradingDashboard.theStage.setTitle("Grading Dashboard");
		ViewGradingDashboard.theStage.setScene(ViewGradingDashboard.theGradingDashboardScene);
		ViewGradingDashboard.theStage.show();
	}
	
	
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user home page. </p>
	 * 
	 */
	protected static void performReturn() {
		// Get the roles the user selected during login
		int theRole = applicationMain.FoundationsMain.activeHomePage;

		// Use that role to proceed to that role's home page
		switch (theRole) {
		case 1:
			guiAdminHome.ViewAdminHome.displayAdminHome(ViewGradingDashboard.theStage, ViewGradingDashboard.theUser);
			break;
		case 2:
			guiStaff.ViewStaffHome.displayStaffHome(ViewGradingDashboard.theStage, ViewGradingDashboard.theUser);
			break;
		case 3:
			guiStudent.ViewStudentHome.displayStudentHome(ViewGradingDashboard.theStage, ViewGradingDashboard.theUser);
			break;
		default:
			System.out.println("*** ERROR *** UserUpdate goToUserHome has an invalid role: " + theRole);
			System.exit(0);
		}
	}

	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal
	 * login page where existing users can log in or potential new users with a
	 * invitation code can start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewGradingDashboard.theStage);
	}

	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program. It leaves
	 * the database in a state where the normal login page will be displayed when
	 * the application is restarted. </p>
	 * 
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
