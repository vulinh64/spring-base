package com.vulinh.utils.noidle;

import java.awt.*;
import java.security.SecureRandom;
import java.util.Random;
import javax.swing.*;

/** Create an AWT Robot that moves the mouse cursor to random locations at 10 second interval */
@SuppressWarnings("java:S106")
public class Energy {

  static final Random RANDOM = new SecureRandom();

  static final int ROBOT_DELAY = 10 * 1000; // Ten seconds interval

  static final int MAX_X;
  static final int MAX_Y;

  static {
    var dimension = Toolkit.getDefaultToolkit().getScreenSize(); // Adapt to most screen dimensions
    MAX_X = (int) dimension.getWidth();
    MAX_Y = (int) dimension.getHeight();

    System.out.printf("Detected screen resolution is %s x %s%n", MAX_X, MAX_Y);
  }

  private final TrayIcon trayIcon;
  private volatile boolean isRunning = false;

  public static void start() {
    if (!SystemTray.isSupported()) {
      System.err.println("System tray not supported!");
      return;
    }

    new Energy();
  }

  private Energy() {
    var popup = new PopupMenu();

    trayIcon =
        new TrayIcon(
            Toolkit.getDefaultToolkit().getImage(Energy.class.getResource("/icon.png")),
            "Energy Booster",
            popup);

    trayIcon.setImageAutoSize(true);

    var startItem = new MenuItem("Start");
    var stopItem = new MenuItem(("Stop"));
    var exitItem = new MenuItem("Exit");

    popup.add(startItem);
    popup.add(stopItem);
    popup.addSeparator();
    popup.add(exitItem);

    startItem.addActionListener(e -> startWorking());

    stopItem.addActionListener(e -> stopWorking());

    exitItem.addActionListener(e -> System.exit(0));

    try {
      SystemTray.getSystemTray().add(trayIcon);

      JOptionPane.showMessageDialog(
          null,
          """
                   Look at the System Tray, near the clock ;) Press "^" icon if you don't see it
                   """,
          "Energy Booster",
          JOptionPane.INFORMATION_MESSAGE);
    } catch (AWTException e) {
      System.err.printf("Unable to add tray icon: %s%n", e.getMessage());
      System.exit(0);
    }
  }

  private void stopWorking() {
    if (!isRunning) {
      System.out.println("Already stopped!");
      return;
    }

    isRunning = false;

    System.out.println("Robot has stopped!");

    trayIcon.setToolTip("Stopped");
  }

  private void startWorking() {
    if (isRunning) {
      System.out.println("Already running!");
      return;
    }

    isRunning = true;

    System.out.println("Robot is now running...");

    new Thread(this::robotInAction).start();

    trayIcon.setToolTip("Running...");
  }

  private void robotInAction() {
    while (isRunning) {
      try {
        var robot = new Robot();

        robot.delay(ROBOT_DELAY);

        robot.mouseMove(RANDOM.nextInt(MAX_X), RANDOM.nextInt(MAX_Y));
      } catch (AWTException e) {
        throw new ExceptionInInitializerError(e);
      }
    }
  }
}
