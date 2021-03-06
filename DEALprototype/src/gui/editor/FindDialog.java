package gui.editor;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Dialog for finding names and descriptions in GUIs.
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class FindDialog extends javax.swing.JDialog {
	private static final long serialVersionUID = 1L;
	/**
     * Creates new form FindDialog
     */
    public FindDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        directionButtonGroup = new javax.swing.ButtonGroup();
        findPanel = new javax.swing.JPanel();
        findLabel = new javax.swing.JLabel();
        findTextField = new javax.swing.JTextField();
        directionPanel = new javax.swing.JPanel();
        forwardRdbtn = new javax.swing.JRadioButton();
        backwardRdbtn = new javax.swing.JRadioButton();
        buttonPanel = new javax.swing.JPanel();
        findButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Find");
        setAlwaysOnTop(true);
        setResizable(false);

        findLabel.setLabelFor(findTextField);
        findLabel.setText("Find:");

        findTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                findTextFieldKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout findPanelLayout = new javax.swing.GroupLayout(findPanel);
        findPanel.setLayout(findPanelLayout);
        findPanelLayout.setHorizontalGroup(
            findPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(findPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(findLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(findTextField)
                .addContainerGap())
        );
        findPanelLayout.setVerticalGroup(
            findPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(findPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(findPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(findLabel)
                    .addComponent(findTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        directionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Direction"));

        directionButtonGroup.add(forwardRdbtn);
        forwardRdbtn.setText("Forward");

        directionButtonGroup.add(backwardRdbtn);
        backwardRdbtn.setText("Backward");

        javax.swing.GroupLayout directionPanelLayout = new javax.swing.GroupLayout(directionPanel);
        directionPanel.setLayout(directionPanelLayout);
        directionPanelLayout.setHorizontalGroup(
            directionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(directionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(directionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(forwardRdbtn)
                    .addComponent(backwardRdbtn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        directionPanelLayout.setVerticalGroup(
            directionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(directionPanelLayout.createSequentialGroup()
                .addComponent(forwardRdbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backwardRdbtn))
        );

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        findButton.setText("Find");
        buttonPanel.add(findButton);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(closeButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(findPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(directionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(findPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(directionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>                        

    /**
     * Close button action.
     * @param evt close button event
     */
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {                                            
        dispose();
    }                                        

    private void findTextFieldKeyPressed(java.awt.event.KeyEvent evt) {                                         
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            // if Enter was pressed, do the same as the findButton
            findButton.doClick();
        }
    }                                        

    public void addFindActionListener(ActionListener listener) {
        findButton.addActionListener(listener);
    }
    
    public String getStringToFind() {
        return findTextField.getText();
    }

    //<editor-fold defaultstate="collapsed" desc="Variables declaration - do not modify">               
    private javax.swing.JRadioButton backwardRdbtn;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.ButtonGroup directionButtonGroup;
    private javax.swing.JPanel directionPanel;
    private javax.swing.JButton findButton;
    private javax.swing.JLabel findLabel;
    private javax.swing.JPanel findPanel;
    private javax.swing.JTextField findTextField;
    private javax.swing.JRadioButton forwardRdbtn;
    //</editor-fold>   
}
