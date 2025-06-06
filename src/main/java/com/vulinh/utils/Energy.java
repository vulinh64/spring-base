package com.vulinh.utils;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.security.SecureRandom;
import java.util.Random;

public class Energy {

  private final TrayIcon trayIcon;
  private volatile boolean isRunning = false;

  private static final Random RANDOM = new SecureRandom();

  private static final int MAX_X;
  private static final int MAX_Y;

  static {
    var dimension = Toolkit.getDefaultToolkit().getScreenSize();
    MAX_X = (int) dimension.getWidth();
    MAX_Y = (int) dimension.getHeight();

    System.out.printf("Screen relative resolution is %s x %s%n", MAX_X, MAX_Y);
  }

  public static void main(String[] args) {
    start();
  }

  public static void start() {
    if (!SystemTray.isSupported()) {
      System.err.println("System tray not supported!");
      return;
    }

    new Energy();
  }

  Energy() {
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
    } catch (AWTException e) {
      System.err.println("Unable to add tray icon.");
      System.exit(0);
    }
  }

  private void stopWorking() {
    if (!isRunning) {
      System.out.println("Already stopped!");
      return;
    }

    isRunning = false;

    System.out.println("Robot has stopped working");

    trayIcon.setToolTip("Stopped");
  }

  private void startWorking() {
    if (isRunning) {
      System.out.println("Already running!");
      return;
    }

    isRunning = true;

    System.out.println("Robot is now running");

    new Thread(this::robotInAction).start();

    trayIcon.setToolTip("Started");
  }

  private void robotInAction() {
    while (isRunning) {
      try {
        var robot = new Robot();

        robot.delay(10 * 1000);

        robot.mouseMove(RANDOM.nextInt(MAX_X), RANDOM.nextInt(MAX_Y));
      } catch (AWTException e) {
        throw new ExceptionInInitializerError(e);
      }
    }
  }
}
