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
		super.setExpandableContent(null);
	}

	@Override
	protected Node createDetailsButton() {
		return extendedNode;
	}

}
