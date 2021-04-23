package com.settings;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import com.Launcher;

public class SettingsGUI {

	private JFrame frame;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SettingsGUI window = new SettingsGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public SettingsGUI() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle(Launcher.title);
		frame.setIconImage(Launcher.icon);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setLocationRelativeTo(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		ImagesPane imagePane = new ImagesPane();
		tabbedPane.add("Select Images", imagePane);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
	}
	
	public void toggleVisibility() {
		frame.setVisible(!frame.isVisible());
	}
}
