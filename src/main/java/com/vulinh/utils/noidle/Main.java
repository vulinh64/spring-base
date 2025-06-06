package com.vulinh.utils.noidle;

import javax.swing.*;

class Main {

  public static void main(String[] args)
      throws UnsupportedLookAndFeelException,
          ClassNotFoundException,
          InstantiationException,
          IllegalAccessException {
    for (var lookAndFeel : UIManager.getInstalledLookAndFeels()) {
      if ("windows".equalsIgnoreCase(lookAndFeel.getName())) {
        UIManager.setLookAndFeel(lookAndFeel.getClassName());
      }
    }
    
    SwingUtilities.invokeLater(Energy::start);
  }
}
