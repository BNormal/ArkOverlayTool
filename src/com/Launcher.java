package com;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.UIManager;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import com.TaskTray.TaskTray;
import com.settings.SettingsGUI;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;

import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;

public class Launcher {
	private Window overlayWindow;
	private Image overlay;
	private TaskTray tray = null;
	public static SettingsGUI settingsWindow;
	public static Image icon;
	public static String title = "Ark Overlay Tool";
	public static Robot r;
	public boolean autoWalk = false;
	public boolean autoRun = false;
	public boolean ignore = false;
	private boolean gameActive = false;
	private JComponent overlayComp;
	private Map<String, Boolean> keys = new HashMap<String, Boolean>();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Launcher();
					settingsWindow = new SettingsGUI();
				} catch (Exception e) {
				}
			}
		});
	}
	
	private static String getActiveWindowTitle() {
        HWND fgWindow = User32.INSTANCE.GetForegroundWindow();
        int titleLength = User32.INSTANCE.GetWindowTextLength(fgWindow) + 1;
        char[] title = new char[titleLength];
        User32.INSTANCE.GetWindowText(fgWindow, title, titleLength);
        return Native.toString(title);
    }
	
	public void getAllWindows() {//Not being used
		User32.INSTANCE.EnumWindows(new WNDENUMPROC() {
			@Override
			public boolean callback(HWND hWnd, Pointer arg) {
				HWND temp = new HWND();
				temp.setPointer(new Pointer(Long.decode(hWnd.toString().substring(7))));
				char[] windowText = new char[512];
				User32.INSTANCE.GetWindowText(hWnd, windowText, 512);
				String wText = Native.toString(windowText).trim();
				//ARK: Survival Evolved
				if (wText.toLowerCase().contains("notepad"))
					System.out.println(wText + " - " + hWnd);
				return true;
			}
		}, null);
	}

	public Launcher() {
		init();
	    setTray(new TaskTray(overlayWindow));
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("serial")
	private void init() {
		try {
			r = new Robot();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		Thread activeWindowThread = new Thread() {
			public void run() {
				String title = "";
				while (true) {
					try {
						Thread.sleep(10);
						title = getActiveWindowTitle();
						if (title.equals("ARK: Survival Evolved")) {
							if (gameActive == false) {
								gameActive = true;
								overlayComp.repaint();
							}
						} else {
							if (gameActive) {
								gameActive = false;
								overlayComp.repaint();
							}
						}
					} catch (InterruptedException e) {
					}
				}
			}
		};
		activeWindowThread.start();
		Thread autoMoveThread = new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(10);
						if (!gameActive) {
							if (autoWalk || autoRun) {
								autoRun = false;
								autoWalk = false;
								r.keyRelease(KeyEvent.VK_SHIFT);
								r.keyRelease(KeyEvent.VK_W);
							}
							continue;
						}
						if (autoRun) {
							if (!keys.containsKey("left shift")) {
								r.keyPress(KeyEvent.VK_SHIFT);
							}
						}
						if (autoWalk || autoRun) {
							if (!keys.containsKey("w")) {
								r.keyPress(KeyEvent.VK_W);
							}
							//r.delay(20);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		autoMoveThread.start();
		try {
			icon = ImageIO.read(Launcher.class.getResource("/images/ark-icon.png"));
			overlay = ImageIO.read(Launcher.class.getResource("/images/green-crosshair.png"));
		} catch (IOException e2) {
			e2.printStackTrace();
            return;
		}
		try {
			Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
			logger.setLevel(Level.OFF);
			logger.setUseParentHandlers(false);
			GlobalScreen.registerNativeHook();
			NativeKeyListener keyListener = new NativeKeyListener() {
				@Override
				public void nativeKeyPressed(NativeKeyEvent e) {
					String key = NativeKeyEvent.getKeyText(e.getKeyCode());
					if (!keys.containsKey(key)) {
						keys.put(key, true);
						if (ignore == false)
							handleKeyPress(key, true);
						else
							ignore = false;
						//System.out.println(key.toLowerCase() + " : " + e.getKeyCode() + " : " + e.getID());
					}
				}
				@Override
				public void nativeKeyReleased(NativeKeyEvent e) {
					String key = NativeKeyEvent.getKeyText(e.getKeyCode());
					if (keys.containsKey(key)) {
						keys.remove(key);
						if (ignore == false)
							handleKeyPress(key, false);
						else
							ignore = false;
						//System.out.println(key.toLowerCase() + " : " + e.getKeyCode() + " : " + e.getID());
					}
				}
				@Override
				public void nativeKeyTyped(NativeKeyEvent e) {
				}
			};
			
			NativeMouseInputListener mouseListener = new NativeMouseInputListener() {
				@Override
				public void nativeMouseMoved(NativeMouseEvent arg0) {}
				@Override
				public void nativeMouseDragged(NativeMouseEvent arg0) {}
				@Override
				public void nativeMouseReleased(NativeMouseEvent arg0) {}
				@Override
				public void nativeMousePressed(NativeMouseEvent arg0) {}
				@Override
				public void nativeMouseClicked(NativeMouseEvent arg0) {
					if (tray != null && tray.getPopup() != null && tray.getPopup().isVisible())
						tray.handlePopup(arg0.getX(), arg0.getY());
				}
			};
			GlobalScreen.addNativeKeyListener(keyListener);
			GlobalScreen.addNativeMouseListener(mouseListener);
			GlobalScreen.addNativeMouseMotionListener(mouseListener);
		} catch (NativeHookException ex) {
			System.exit(1);
		}
		overlayWindow = new Window(null);
		overlayComp = new JComponent() {
	    	float alpha = 0.8f;
	    	float scale = 0.5f;
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
	        	if (overlay != null) {
	        		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
	        		g2d.drawImage(overlay.getScaledInstance((int) (overlay.getWidth(this) * scale), -1, Image.SCALE_SMOOTH), getWidth() / 2 - overlay.getWidth(this) / 2, getHeight() / 2 - overlay.getHeight(this) / 2, this);
	        		
	        		if (autoRun) {
	        			g2d.setColor(Color.BLACK);
	        			g2d.fillRect(195, 6, 78, 20);
	        			g2d.setColor(Color.WHITE);
	        			g2d.drawString("Auto Run On", 200, 20);
	        		} else if (autoWalk) {
	        			g2d.setColor(Color.BLACK);
	        			g2d.fillRect(195, 6, 83, 20);
	        			g2d.setColor(Color.WHITE);
		        		g2d.drawString("Auto Walk On", 200, 20);
	        		}
	        		
	        		if (!gameActive) {
	        			g2d.clearRect(0, 0, getWidth(), getHeight());
	        		}
	        	}
	        }
	        
	        /**
	         * This is the size of the overlay.
	         */
	        public Dimension getPreferredSize() {
	        	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	            return screenSize;
	        }
	    };
	    overlayWindow.add(overlayComp);
	    overlayWindow.pack();
	    overlayWindow.setLocationRelativeTo(null);
	    overlayWindow.setVisible(true);
	    overlayWindow.setAlwaysOnTop(true);
	    overlayWindow.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
	    /**
	     * This sets the background of the window to be transparent.
	     */
	    setTransparent(overlayWindow);
	}
	
	private void handleKeyPress(String key, boolean held) {
		switch(key.toLowerCase()) {
		case "f1":
			if (!held) {
				autoWalk = !autoWalk;
				if (autoWalk) {
					r.keyRelease(KeyEvent.VK_SHIFT);
					autoRun = false;
				} else {
					r.keyRelease(KeyEvent.VK_W);
				}
			}
			break;
		case "f2":
			if (!held) {
				autoRun = !autoRun;
				if (autoRun)
					autoWalk = false;
				if (!autoRun) {
					r.keyRelease(KeyEvent.VK_SHIFT);
					r.keyRelease(KeyEvent.VK_W);
				}
			}
			break;
		case "left alt":
			if (held) {
				r.keyRelease(KeyEvent.VK_SHIFT);
				r.keyRelease(KeyEvent.VK_W);
				autoRun = false;
				autoWalk = false;
			}
			break;
		}
	}
	
	private void setTransparent(Component w) {
	    WinDef.HWND hwnd = getHWnd(w);
	    int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
	    wl = wl | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
	    User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
	}

	/**
	 * Get the window handle from the OS
	 */
	private HWND getHWnd(Component w) {
	    HWND hwnd = new HWND();
	    hwnd.setPointer(Native.getComponentPointer(w));
	    return hwnd;
	}

	public TaskTray getTray() {
		return tray;
	}

	public void setTray(TaskTray tray) {
		this.tray = tray;
	}
}
