package binaryconverter;

import binaryconverter.gui.frmMain;
import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main
{
  public static void main(String[] args) {
    try {
      if (!UIManager.getSystemLookAndFeelClassName().equals("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")) {
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        UIDefaults uiDefaults = UIManager.getDefaults();
        Font f = uiDefaults.getFont("Label.font");
        uiDefaults.put("Label.font", f.deriveFont(f.getStyle(), 10.0F));
        f = uiDefaults.getFont("ComboBox.font");
        uiDefaults.put("ComboBox.font", f.deriveFont(f.getStyle(), 10.0F));
        f = uiDefaults.getFont("Button.font");
        uiDefaults.put("Button.font", f.deriveFont(f.getStyle(), 10.0F));
        f = uiDefaults.getFont("CheckBox.font");
        uiDefaults.put("CheckBox.font", f.deriveFont(f.getStyle(), 10.0F));
        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      } else {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } 
    } catch (UnsupportedLookAndFeelException e) {
      e.printStackTrace();
      System.err.flush();
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
      System.err.flush();
    }
    catch (InstantiationException e) {
      e.printStackTrace();
      System.err.flush();
    }
    catch (IllegalAccessException e) {
      e.printStackTrace();
      System.err.flush();
    } 
    
    EventQueue.invokeLater(new Runnable() {
          public void run() {
            frmMain MainFrame = new frmMain();
            MainFrame.setLocationRelativeTo(null);
            MainFrame.setResizable(false);
            MainFrame.setDefaultCloseOperation(2);
            MainFrame.setVisible(true);
          }
        });
  }
}
