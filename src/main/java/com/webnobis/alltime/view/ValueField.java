package com.webnobis.alltime.view;

import java.util.function.Function;
import java.util.function.Supplier;

import javafx.scene.control.TextField;

public class ValueField<V> extends TextField {

	private final Function<String, V> textToValueTransformer;

	private final Function<V, String> valueToTextTransformer;

	public ValueField(Function<String, V> textToValueTransformer, Function<V, String> valueToTextTransformer, Supplier<String> nowToTextTransformer) {
		super();
		this.textToValueTransformer = textToValueTransformer;
		this.valueToTextTransformer = valueToTextTransformer;
		setText(nowToTextTransformer.get());
	}

	public V getValue() {
		return textToValueTransformer.apply(getText());
	}

	public void setValue(V value) {
		setText(valueToTextTransformer.apply(value));
	}

}