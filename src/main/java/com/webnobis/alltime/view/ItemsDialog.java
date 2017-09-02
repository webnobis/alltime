package com.webnobis.alltime.view;

import java.time.Duration;
import java.util.Map;
import java.util.TreeMap;

import com.webnobis.alltime.service.DurationFormatter;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ItemsDialog extends Dialog<Map<String,Duration>> {
	
	private final ComboBox<Map.Entry<String, Duration>> items;
	
	private final ValueField<Duration> duration;
	
	private final TextField description;
	
	private final Button add;
	
	private final Button change;

	public ItemsDialog(Map<String,Duration> currentItems) {
		super();
		
		items = new ComboBox<>(FXCollections.observableArrayList(new TreeMap<>(currentItems).entrySet()));
		items.setOnAction(this::getItem);
		
		duration = new ValueField<>(DurationFormatter::toDuration, DurationFormatter::toString, () -> DurationFormatter.toString(Duration.ZERO));
		description = new TextField("Beschreibung");
		
		add = new Button("Hinzufügen");
		add.setOnAction(this::addItem);
		
		change = new Button("Ändern");
		change.setOnAction(this::changeItem);

		GridPane pane = new GridPane();
		pane.add(items, 0,0);
		pane.add(duration, 0,1);
		pane.add(description, 1,1);
		pane.add(change, 0,2);
		pane.add(add, 1,2);

		
		super.getDialogPane().setContent(pane);
		//super.onShowingProperty().addListener(observable -> observable.addListener(e -> e.));
		super.show();
	}
	
	private void addItem(ActionEvent event) {
		Map.Entry<String, Duration> item = new Item(description.getText(), duration.getValue());
		
		items.getItems().add(item);
	}
	
	private void getItem(ActionEvent event) {
		Map.Entry<String, Duration> item = items.getValue();
		duration.setValue(item.getValue());
		description.setText(item.getKey());
		
		duration.requestFocus();
	}
	
	private void changeItem(ActionEvent event) {
		Map.Entry<String, Duration> item = new Item(description.getText(), duration.getValue());
		
		items.getItems().set(items.getItems().indexOf(items.getValue()), item);
	}

	private class Item implements Map.Entry<String, Duration> {

		private final String key;

		private final Duration value;

		public Item(String key, Duration value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public Duration getValue() {
			return value;
		}

		@Override
		public Duration setValue(Duration value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return "Item [key=" + key + ", value=" + value + "]";
		}

	}

}
