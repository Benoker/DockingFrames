package glass.eclipse;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;


public class CTestPanel extends JPanel {

   private static final long serialVersionUID = 1L;
   private JPanel jPanel = null;
   private JLabel jLabel1 = null;
   private JLabel jLabel3 = null;
   private JLabel jLabel4 = null;
   private JLabel jLabel2 = null;
   private JScrollPane jScrollPane = null;
   private JTable jTable = null;

   /**
    * This is the default constructor
    */
   public CTestPanel () {
      super();
      initialize();
   }

   /**
    * This method initializes this
    * 
    * @return void
    */
   private void initialize () {
      this.setSize(300, 200);
      setLayout(new BorderLayout());
      addMouseListener(new MouseAdapter() {});
      addMouseMotionListener(new MouseAdapter() {});
      addMouseWheelListener(new MouseAdapter() {});
      this.add(getJPanel(), BorderLayout.CENTER);
   }

   /**
    * This method initializes jPanel	
    * 	
    * @return javax.swing.JPanel	
    */
   private JPanel getJPanel () {
      if (jPanel == null) {
         GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
         gridBagConstraints3.fill = GridBagConstraints.BOTH;
         gridBagConstraints3.gridy = 2;
         gridBagConstraints3.weightx = 0.2;
         gridBagConstraints3.weighty = 0.8;
         gridBagConstraints3.gridx = 0;
         jLabel2 = new JLabel();
         jLabel2.setText("JLabel");
         GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
         gridBagConstraints2.fill = GridBagConstraints.BOTH;
         gridBagConstraints2.gridy = 1;
         gridBagConstraints2.weightx = 0.8;
         gridBagConstraints2.gridx = 1;
         jLabel4 = new JLabel();
         jLabel4.setText("JLabel");
         GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
         gridBagConstraints1.fill = GridBagConstraints.BOTH;
         gridBagConstraints1.gridy = 1;
         gridBagConstraints1.weightx = 0.2;
         gridBagConstraints1.weighty = 0.1;
         gridBagConstraints1.gridx = 0;
         jLabel3 = new JLabel();
         jLabel3.setText("JLabel");
         GridBagConstraints gridBagConstraints = new GridBagConstraints();
         gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.weighty = 0.1;
         gridBagConstraints.gridwidth = 3;
         jLabel1 = new JLabel();
         jLabel1.setText("JLabel");
         jPanel = new JPanel();
         jPanel.setLayout(new GridBagLayout());
         jPanel.add(jLabel1, gridBagConstraints);
         jPanel.add(jLabel3, gridBagConstraints1);
         jPanel.add(jLabel4, gridBagConstraints2);
         jPanel.add(jLabel2, gridBagConstraints3);
      }
      return jPanel;
   }

   /**
    * This method initializes jScrollPane	
    * 	
    * @return javax.swing.JScrollPane	
    */
   @SuppressWarnings("unused")
private JScrollPane getJScrollPane () {
      if (jScrollPane == null) {
         jScrollPane = new JScrollPane();
         jScrollPane.setViewportView(getJTable());
      }
      return jScrollPane;
   }

   /**
    * This method initializes jTable	
    * 	
    * @return javax.swing.JTable	
    */
   private JTable getJTable () {
      if (jTable == null) {
         jTable = new JTable(6, 4);
      }
      return jTable;
   }

   @Override
   protected void paintComponent (Graphics g) {
      super.paintComponent(g);

      setComponentZOrder(getJPanel(), 2);
      setComponentZOrder(getJPanel(), 1);
   }
}
