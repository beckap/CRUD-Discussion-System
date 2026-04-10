module Phase3 {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	requires org.junit.jupiter.api;
	requires org.mockito;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
}