package com.webnobis.alltime.rule;

import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import org.junit.rules.ExternalResource;

import javafx.embed.swing.JFXPanel;

public class JavaFxInitRule extends ExternalResource {

	@Override
	protected void before() throws Throwable {
		CountDownLatch countDown = new CountDownLatch(1);
		SwingUtilities.invokeLater(() -> {
			new JFXPanel();
			countDown.countDown();
		});
		countDown.await();
	}

}
