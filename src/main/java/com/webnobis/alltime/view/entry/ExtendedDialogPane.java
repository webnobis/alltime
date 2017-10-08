package com.webnobis.alltime.view.entry;

import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

public class ExtendedDialogPane extends DialogPane {
	
	private final Node extendedNode;

	public ExtendedDialogPane(Node extendedNode) {
		super();
		this.extendedNode = Objects.requireNonNull(extendedNode, "extendedNode is null");
		
		super.setExpanded(true);
		Label label = new Label();
		label.setPrefHeight(0); // zero additional height, because the detail button area is not removable
		super.setExpandableContent(label);
	}

	@Override
	protected Node createDetailsButton() {
		return extendedNode;
	}

}
