package gui.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class UrlDialog extends JDialog {
	private static final long serialVersionUID = 1239875L;
	private String url;
	
	/**
	 * Create the dialog.
	 */
	public UrlDialog() {
		setAlwaysOnTop(true);
		setModal(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(UrlDialog.class.getResource("/gui/editor/resources/tree/model.png")));
		setTitle("DEAL (Web Page Mode)");
		setBounds(100, 100, 453, 121);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			urlLbl = new JLabel("Main web page URL:");
			urlLbl.setHorizontalAlignment(SwingConstants.LEFT);
		}
		{
			urlTxtfld = new JTextField();
			urlTxtfld.setHorizontalAlignment(SwingConstants.LEFT);
			urlTxtfld.setText("http://");
		}
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(urlLbl)
					.addContainerGap(325, Short.MAX_VALUE))
				.addComponent(urlTxtfld, GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(urlLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(urlTxtfld, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okBttn = new JButton("OK");
				okBttn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						okButtonActionPerformed(evt);
					}
				});
				okBttn.setActionCommand("OK");
				buttonPane.add(okBttn);
				getRootPane().setDefaultButton(okBttn);
			}
			{
				exitBttn = new JButton("Exit");
				exitBttn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						exitButtonActionPerformed();
					}
				});
				exitBttn.setActionCommand("Exit");
				buttonPane.add(exitBttn);
			}
		}
	}
	
	public void showDialog() {
		pack();
		setVisible(true);
	}
	
	private void okButtonActionPerformed(ActionEvent evt) {
		if(isVisible()) {
			this.url = urlTxtfld.getText();
			clearAndHide();
		}
	}
	
	private void exitButtonActionPerformed() {
		this.url = null;
		clearAndHide();
	}
	
	public void clearAndHide() {
		urlTxtfld.setText(null);
		setVisible(false);
	}
	
	//TODO: doplnit obmedzenia pre url, Pattern, Matcher + regex
	public String getValidatedText() {
		return url;
	}
	
	private final JPanel contentPanel = new JPanel();
	private JTextField urlTxtfld;
	private JLabel urlLbl;
	private JButton okBttn;
	private JButton exitBttn;
}
