package com.TaskTray;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import com.Launcher;

public class TaskTray {

	private Window window;
	private JPopupMenu popup;
	private CustomCheckBoxMenuItem cbxHide;
	private Rectangle popupDimension;
	
	public TaskTray(Window window) {
		this.window = window;
		init();
	}
	
	private void init() {
		//Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            return;
        }
        TrayIcon trayIcon = new TrayIcon(Launcher.icon);
        final SystemTray tray = SystemTray.getSystemTray();
        
        // Create a pop-up menu components
        Color highlight = new Color(75, 76, 79);
        Color background = new Color(42, 42, 45);
        int trayIconWidth = new TrayIcon(Launcher.icon).getSize().width;
        JLabel title = new JLabel(Launcher.title);
        title.setIconTextGap(10);
        title.setForeground(Color.gray);
        title.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        title.setIcon(new ImageIcon(Launcher.icon.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH)));
        
        cbxHide = new CustomCheckBoxMenuItem("Hide Overlay", background, highlight, Color.white);
        CustomMenuItem settingsItem = new CustomMenuItem("Settings", background, highlight, Color.white);
        //CustomMenuItem aboutItem = new CustomMenuItem("About", background, highlight, Color.white);
        CustomMenuItem quitItem = new CustomMenuItem("Quit", background, highlight, Color.white);
        JSeparator jSperator = new JSeparator();
        jSperator.setForeground(new Color(42, 42, 45));
        jSperator.setBackground(new Color(56, 59, 62));
        JSeparator jSperator2 = new JSeparator();
        jSperator2.setForeground(new Color(42, 42, 45));
        jSperator2.setBackground(new Color(56, 59, 62));

        popup = new JPopupMenu();
        //Add components to pop-up menu
        popup.add(title);
        popup.add(jSperator2);
        popup.add(cbxHide);
        popup.add(settingsItem);
        //popup.add(aboutItem);
        popup.add(jSperator);
        popup.add(quitItem);

        popup.setBackground(background);
        popup.setBorder(BorderFactory.createLineBorder(new Color(56, 59, 62), 1));
        popup.addMouseListener(new MouseAdapter() {
        	public void mouseExited(MouseEvent e) {

        	}
		});
        
        cbxHide.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	toggleOverlay();
            }
        });
        
        settingsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	Launcher.settingsWindow.toggleVisibility();
            }
        });
        
        /*aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                
            }
        });*/
        
        quitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SystemTray.getSystemTray().remove(trayIcon);
                System.exit(0);
            }
        });
        trayIcon.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                	int width = (int) popup.getPreferredSize().getWidth();
                	int height = (int) popup.getPreferredSize().getHeight();
                	int x = e.getX();
                	int y = (int) (e.getY() - height);
                	popup.setLocation(x, y);
                	popupDimension = new Rectangle(x, y, width, height);
                    popup.setInvoker(popup);
                    popup.setVisible(true);
                } else {
                	toggleOverlayButton();
                }
            }
        });
        
        trayIcon.setImageAutoSize(true);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
        }
	}
	
	public void toggleOverlayButton() {
		if (cbxHide.isSelected()) {
			cbxHide.setSelected(false);
		} else {
			cbxHide.setSelected(true);
		}
		toggleOverlay();
	}
	
	public void toggleOverlay() {
		if (!cbxHide.isSelected()) {
			cbxHide.setText("Hide Overlay");
        	window.setVisible(true);
		} else {
			cbxHide.setText("Show Overlay");
        	window.setVisible(false);
		}
	}
	
	public void handlePopup(int x, int y) {
		if (popup == null || !popup.isVisible())
			return;
		if (x < popupDimension.getX() || x > popupDimension.getX() + popupDimension.getWidth() || y < popupDimension.getY() || y > popupDimension.getY() + popupDimension.getHeight())
			popup.setVisible(false);
	}
	
	public JPopupMenu getPopup() {
		return popup;
	}

	public void setPopup(JPopupMenu popup) {
		this.popup = popup;
	}

}
