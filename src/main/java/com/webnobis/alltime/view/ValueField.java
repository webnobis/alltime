package com.webnobis.alltime.view;

import java.util.function.Function;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

public class ValueField<V> extends TextField {

	private final Function<String, V> textToValueTransformer;

	private final Function<V, String> valueToTextTransformer;

	private V value;

	public ValueField(Function<String, V> textToValueTransformer, Function<V, String> valueToTextTransformer,
			V defaultValue) {
		super();
		this.textToValueTransformer = textToValueTransformer;
		this.valueToTextTransformer = valueToTextTransformer;
		setValue(defaultValue);

		super.focusedProperty().addListener((observable, oldFocus, newFocus) -> validateValue(oldFocus, newFocus));
	}

	private void validateValue(boolean oldFocus, boolean newFocus) {
		if (oldFocus && !newFocus) {
			try {
				getValue();
			} catch (RuntimeException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Fehlerhafter Eintrag");
				alert.setContentText(String.format("Der Inhalt '%s' ist ungültig:%s%s", getText(),
						(char) Character.LINE_SEPARATOR, e.getMessage()));
				alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				alert.showAndWait();
				setValue(value);
			}
		}
	}

	public V getValue() {
		return textToValueTransformer.apply(getText());
	}

	public void setValue(V value) {
		this.value = value;
		setText(valueToTextTransformer.apply(value));
	}

}
