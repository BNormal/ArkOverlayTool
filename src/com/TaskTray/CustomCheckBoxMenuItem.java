package com.TaskTray;

import java.awt.Color;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicMenuItemUI;

@SuppressWarnings("serial")
public class CustomCheckBoxMenuItem extends JCheckBoxMenuItem {
	public CustomCheckBoxMenuItem(String title, Color background, Color highlight, Color foreground) {
		super(title);
        CustomMenuUI ui = new CustomMenuUI(highlight, foreground);
        setUI(ui);
        setBackground(background);
        setForeground(foreground);
        setBorder(new EmptyBorder(3, 1, 3, 1));
	}
	
	public class CustomMenuUI extends BasicMenuItemUI {
	    public CustomMenuUI(Color background, Color foreground){
	        super.selectionBackground = background;
	        super.selectionForeground = foreground;
	    }
	}
}
