package com.webnobis.alltime.view.items;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class ItemListView extends ListView<Item> {

	private static final Item NEW_TRIGGER = new Item("--- Neuer Eintrag ---", Duration.ZERO);

	public ItemListView(Map<String, Duration> items) {
		super(FXCollections.observableArrayList(items.entrySet().stream()
				.map(Item::new)
				.sorted()
				.collect(Collectors.toList())));
		addNewTrigger();
		super.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		super.setCellFactory(unused -> new ItemListCell(15, Arrays.asList("ein Text", "noch ein Text"), Duration.ofHours(8)));
		super.setEditable(true);
		super.getItems().addListener(this::changedItem);
	}

	private void addNewTrigger() {
		super.getItems().add(0, NEW_TRIGGER);
	}

	private void changedItem(Change<? extends Item> change) {
		if (change.next() && change.wasReplaced() && super.getItems().stream().map(Item::getKey).noneMatch(NEW_TRIGGER.getKey()::equals)) {
			addNewTrigger();
		}
	}

}
