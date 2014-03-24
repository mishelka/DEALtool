package gui.editor;

import gui.editor.DealFileChooser.DealFileChooserType;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class InputFileDialog extends JDialog {
	private static final long serialVersionUID = 1239875L;
	private String url;
	
	/**
	 * Create the dialog.
	 */
	public InputFileDialog() {
		setAlwaysOnTop(true);
		setModal(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(InputFileDialog.class.getResource("/gui/editor/resources/tree/model.png")));
		setTitle("DEAL (Web Page/Ranorex Mode)");
		setBounds(100, 100, 453, 133);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			urlLbl = new JLabel("Web page URL or path to a Ranorex xml file");
			urlLbl.setHorizontalAlignment(SwingConstants.LEFT);
		}
		{
			urlTxtfld = new JTextField();
			urlTxtfld.setHorizontalAlignment(SwingConstants.LEFT);
			urlTxtfld.setText("http://");
		}
		
		JButton btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				browseButtonActionPerformed(evt);
			}
		});
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(urlLbl)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(urlTxtfld, GroupLayout.PREFERRED_SIZE, 328, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnBrowse)))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(urlLbl)
					.addPreferredGap(ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(urlTxtfld, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowse))
					.addGap(67))
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
				closeBttn = new JButton("Close");
				closeBttn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						exitButtonActionPerformed();
					}
				});
				closeBttn.setActionCommand("Close");
				buttonPane.add(closeBttn);
			}
		}
	}
	
	public void showDialog() {
		pack();
		setVisible(true);
	}
	
	private final String RANOREX_XML_PATH = "examples" + File.separator + "windowsGUIs";
	
	private void browseButtonActionPerformed(ActionEvent evt) {
		DealFileChooser chooser = new DealFileChooser(RANOREX_XML_PATH, DealFileChooserType.RANOREX);
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			if(f != null) {
				urlTxtfld.setText(f.getAbsolutePath());
			}	
		}		
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
	private JButton closeBttn;
}
