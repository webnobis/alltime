module com.webnobis.alltime.Alltime {
	
	requires java.base;
	
	requires org.slf4j;
	
	/*
	 * iTextPdf is not ready for modularization (14.09.2018), therefore the name of the artifact
	 */
	requires kernel;
	requires io;
	requires layout;
	
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.swing;
	
	exports com.webnobis.alltime;
	
}