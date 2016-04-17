package fr.soe.a3s.ui.main.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.UIManager;

public class IndeterminateIcon implements Icon{
    private final Color FOREGROUND = new Color(50,20,255,200); //TEST: UIManager.getColor("CheckBox.foreground");
    private final Icon icon = UIManager.getIcon("CheckBox.icon");
    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
        icon.paintIcon(c, g, x, y);
        int w = getIconWidth(), h = getIconHeight();
        int a = 4, b = 2;
        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint(FOREGROUND);
        g2.translate(x, y);
        g2.fillRect(a, (h-b)/2, w-a-a, b);
        g2.translate(-x, -y);
    }
    @Override public int getIconWidth()  { return icon.getIconWidth();  }
    @Override public int getIconHeight() { return icon.getIconHeight(); }
}
