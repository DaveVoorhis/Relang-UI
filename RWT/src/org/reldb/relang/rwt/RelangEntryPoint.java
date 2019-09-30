package org.reldb.relang.rwt;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.rap.rwt.client.service.ExitConfirmation;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.reldb.relang.core.main.Launcher;

public class RelangEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;
	
	private Composite parent = null;
	
	@Override
    protected void createContents(Composite parent) {
		this.parent = parent;
		
        ExitConfirmation service = RWT.getClient().getService(ExitConfirmation.class);
        service.setMessage("Are you sure you wish to exit this application?");
		
        parent.setLayout(new FillLayout());
        
		clear();	
		RWT.getUISession().addUISessionListener(sessionEvent -> System.out.println("Session " + sessionEvent.toString() + " closing."));
		Launcher.launch(parent);
		parent.layout();
    }

	private void clear() {
		while (parent.getChildren().length > 0)
			parent.getChildren()[0].dispose();
	}
}
