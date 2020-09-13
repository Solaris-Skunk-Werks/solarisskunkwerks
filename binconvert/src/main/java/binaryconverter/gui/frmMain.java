package binaryconverter.gui;

import filehandlers.BinaryConverter;
import filehandlers.Media;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class frmMain
  extends JFrame
{
  private JButton btnCancel;
  private JButton btnConvert;
  private JButton btnJson;
  private JButton btnSetSource;
  private ButtonGroup grpType;
  private JLabel jLabel1;
  private JLabel jLabel2;
  private JPanel jPanel1;
  private JPanel jPanel2;
  private JPanel jPanel3;
  
  public frmMain() { initComponents(); }
  
  private JPanel jPanel4;
  private JScrollPane jScrollPane1;
  private JRadioButton rdoAmmo;
  private JRadioButton rdoEquipment;
  private JRadioButton rdoPhysicals;
  private JRadioButton rdoQuirks;
  private JRadioButton rdoWeapons;
  private JTextPane txtLog;
  private JTextField txtSource;
  
  private void initComponents() {
    this.grpType = new ButtonGroup();
    this.jPanel1 = new JPanel();
    this.jLabel1 = new JLabel();
    this.txtSource = new JTextField();
    this.btnSetSource = new JButton();
    this.jPanel2 = new JPanel();
    this.btnConvert = new JButton();
    this.btnCancel = new JButton();
    this.btnJson = new JButton();
    this.jPanel3 = new JPanel();
    this.jLabel2 = new JLabel();
    this.jScrollPane1 = new JScrollPane();
    this.txtLog = new JTextPane();
    this.jPanel4 = new JPanel();
    this.rdoWeapons = new JRadioButton();
    this.rdoPhysicals = new JRadioButton();
    this.rdoEquipment = new JRadioButton();
    this.rdoAmmo = new JRadioButton();
    this.rdoQuirks = new JRadioButton();
    
    setDefaultCloseOperation(3);
    getContentPane().setLayout(new GridBagLayout());
    
    this.jLabel1.setText("Source File:");
    this.jPanel1.add(this.jLabel1);
    
    this.txtSource.setEditable(false);
    this.txtSource.setMinimumSize(new Dimension(20, 20));
    this.txtSource.setPreferredSize(new Dimension(20, 20));
    this.jPanel1.add(this.txtSource);
    
    this.btnSetSource.setText("...");
    this.btnSetSource.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            frmMain.this.btnSetSourceActionPerformed(evt);
          }
        });
    this.jPanel1.add(this.btnSetSource);
    
    getContentPane().add(this.jPanel1, new GridBagConstraints());

    this.btnJson.setText("JSON");
    this.btnJson.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        frmMain.this.btnJsonActionPerformed(evt);
      }
    });
    this.jPanel2.add(this.btnJson);
    
    this.btnConvert.setText("Convert");
    this.btnConvert.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            frmMain.this.btnConvertActionPerformed(evt);
          }
        });
    this.jPanel2.add(this.btnConvert);
    
    this.btnCancel.setText("Quit");
    this.btnCancel.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            frmMain.this.btnCancelActionPerformed(evt);
          }
        });
    this.jPanel2.add(this.btnCancel);
    
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = 13;
    getContentPane().add(this.jPanel2, gridBagConstraints);
    
    this.jPanel3.setLayout(new GridBagLayout());
    
    this.jLabel2.setText("Logs");
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    this.jPanel3.add(this.jLabel2, gridBagConstraints);
    
    this.txtLog.setMinimumSize(new Dimension(20, 20));
    this.txtLog.setPreferredSize(new Dimension(200, 200));
    this.jScrollPane1.setViewportView(this.txtLog);
    
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    this.jPanel3.add(this.jScrollPane1, gridBagConstraints);
    
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = 2;
    gridBagConstraints.insets = new Insets(4, 0, 0, 0);
    getContentPane().add(this.jPanel3, gridBagConstraints);
    
    this.jPanel4.setLayout(new BoxLayout(this.jPanel4, 3));
    
    this.grpType.add(this.rdoWeapons);
    this.rdoWeapons.setSelected(true);
    this.rdoWeapons.setText("Weapons File");
    this.jPanel4.add(this.rdoWeapons);
    
    this.grpType.add(this.rdoPhysicals);
    this.rdoPhysicals.setText("Physical Weapons");
    this.jPanel4.add(this.rdoPhysicals);
    
    this.grpType.add(this.rdoEquipment);
    this.rdoEquipment.setText("Equipment");
    this.jPanel4.add(this.rdoEquipment);
    
    this.grpType.add(this.rdoAmmo);
    this.rdoAmmo.setText("Ammunition File");
    this.jPanel4.add(this.rdoAmmo);
    
    this.grpType.add(this.rdoQuirks);
    this.rdoQuirks.setText("Quirks");
    this.jPanel4.add(this.rdoQuirks);
    
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = 17;
    getContentPane().add(this.jPanel4, gridBagConstraints);
    
    pack();
  }
  
  private void btnSetSourceActionPerformed(ActionEvent evt) {
    Media media = new Media();
    File file = media.SelectFile("", "csv,dat", "Select");
    String name = "";
    try {
      name = file.getCanonicalPath();
    } catch (IOException ex) {
      String msg = ex.getMessage() + "\n";
      msg = msg + ex.toString();
      this.txtLog.setText(msg);
      return;
    } catch (NullPointerException e) {
      String msg = e.getMessage() + "\n";
      msg = msg + e.toString();
      this.txtLog.setText(msg);
      return;
    }
    this.txtSource.setText(name);
  }
  
  private void btnConvertActionPerformed(ActionEvent evt) {
    if (!txtSource.getText().endsWith(".csv")) {
      Media.Messager("Binary Conversion can only work with semicolon-delimited files.\nPlease choose an appropriate CSV file.");
      return;
    }

    BinaryConverter bc = new BinaryConverter();
    if (this.rdoWeapons.isSelected()) {
      bc.ConvertRangedWeaponsCSVtoBin(this.txtSource.getText(), this.txtSource.getText().replace(".csv", ".dat"), ";");
    } else if (this.rdoPhysicals.isSelected()) {
      bc.ConvertPhysicalWeaponCSVtoBin(this.txtSource.getText(), this.txtSource.getText().replace(".csv", ".dat"), ";");
    } else if (this.rdoEquipment.isSelected()) {
      bc.ConvertEquipmentCSVtoBin(this.txtSource.getText(), this.txtSource.getText().replace(".csv", ".dat"), ";");
    } else if (this.rdoAmmo.isSelected()) {
      bc.ConvertAmmunitionCSVtoBin(this.txtSource.getText(), this.txtSource.getText().replace(".csv", ".dat"), ";");
    } else if (this.rdoQuirks.isSelected()) {
      bc.ConvertQuirksCSVtoBin(this.txtSource.getText(), this.txtSource.getText().replace(".csv", ".dat"), ";");
    } 
    this.txtLog.setText(bc.GetMessages());
  }

  private void btnJsonActionPerformed(ActionEvent evt) {
    if (!txtSource.getText().endsWith(".dat")) {
      Media.Messager("JSON Conversion can only work with binary files.\nPlease choose an appropriate .dat file.");
      return;
    }
    BinaryConverter bc = new BinaryConverter();
    if (this.rdoWeapons.isSelected()) {
      bc.ConvertRangedWeaponsBintoJson(this.txtSource.getText());
    } else if (this.rdoPhysicals.isSelected()) {
      bc.ConvertPhysicalWeaponsBintoJson(this.txtSource.getText());
    } else if (this.rdoEquipment.isSelected()) {
      bc.ConvertEquipmentBintoJson(this.txtSource.getText());
    } else if (this.rdoAmmo.isSelected()) {
      bc.ConvertAmmunitionBintoJson(this.txtSource.getText());
    } else if (this.rdoQuirks.isSelected()) {
      bc.ConvertQuirksBintoJson(this.txtSource.getText());
    }
    this.txtLog.setText(bc.GetMessages());
  }

  private void btnCancelActionPerformed(ActionEvent evt) { dispose(); }
}
