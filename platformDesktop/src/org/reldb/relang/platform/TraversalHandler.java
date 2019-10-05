package org.reldb.relang.platform;

import org.eclipse.swt.SWT;

public class TraversalHandler {
	public enum Traversal {
		Next,
		Down,
		Previous,
		Up,
		None
	}
	
	public static Traversal getTraversal(int event) {
		switch (event) {
		case SWT.TRAVERSE_TAB_NEXT:
			return Traversal.Next;
		case SWT.TRAVERSE_TAB_PREVIOUS:
			return Traversal.Previous;			
		case SWT.TRAVERSE_ARROW_NEXT:
			return Traversal.Down;
		case SWT.TRAVERSE_ARROW_PREVIOUS:
			return Traversal.Up;
		default:
			return Traversal.None;
		}
	}
}
