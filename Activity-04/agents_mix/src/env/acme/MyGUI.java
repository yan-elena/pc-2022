package acme;
import javax.swing.*;

import java.awt.event.*;
import cartago.*;

public class MyGUI extends Artifact {

	private MyFrame frame;	
	
	public void init() {
		try {
			frame = new MyFrame(this);
			SwingUtilities.invokeAndWait(() -> {
				frame.setVisible(true);		
			});
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	/* called by the EDT */
	
	void notifyEvent(String what) {
		this.beginExtSession();
		signal(what);
		this.endExtSession();
	}
		
	class MyFrame extends JFrame {		
		
		private JButton stopButton, suspendButton;
		private MyGUI artifact;
		private boolean isSuspended;
		
		public MyFrame(MyGUI artifact){
			setTitle("..:: My GUI ::..");
			setSize(200,60);
		
			
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
			setContentPane(mainPanel);
			
			isSuspended = false;
			this.artifact = artifact;
			
			suspendButton = new JButton("suspend");
			mainPanel.add(suspendButton);
			suspendButton.addActionListener(ev -> {
				if (!isSuspended) {
					artifact.notifyEvent("suspended");
					suspendButton.setText("resume");
					isSuspended = true;
				} else {
					artifact.notifyEvent("resumed");
					suspendButton.setText("suspend");
					isSuspended = false;
				}
			});
			
			stopButton = new JButton("stop");
			stopButton.addActionListener(ev -> {
				artifact.notifyEvent("stopped");
			});
			mainPanel.add(stopButton);

		}
	}
}
