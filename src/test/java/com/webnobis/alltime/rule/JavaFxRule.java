package com.webnobis.alltime.rule;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javafx.application.Platform;

public class JavaFxRule implements TestRule {

	@Override
	public Statement apply(Statement base, Description description) {
		return new JavaFxThreadEvaluateStatement(base);
	}
	
	private class JavaFxThreadEvaluateStatement extends Statement {
		
		private final Statement statement;

		public JavaFxThreadEvaluateStatement(Statement statement) {
			this.statement = statement;
		}

		@Override
		public void evaluate() throws Throwable {
			final AtomicReference<RuntimeException> e = new AtomicReference<>();
			CountDownLatch countDown = new CountDownLatch(1);
			Platform.runLater(() -> {
				try {
					statement.evaluate();
				} catch (Throwable t) {
					e.set(new RuntimeException(t));
				} finally {
					countDown.countDown();
				}
			});
			try {
				countDown.await();
			} catch (InterruptedException e1) {
				throw new RuntimeException(e1);
			}
			if (e.get() != null) {
				throw e.get();
			}
		}
		
	}

}
