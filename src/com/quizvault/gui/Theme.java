package com.quizvault.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Theme {
    // --- CRISP & CLEAN LIGHT THEME PALETTE ---
    public static final Color COLOR_BACKGROUND = new Color(242, 242, 247);
    public static final Color COLOR_PRIMARY = new Color(0, 122, 255);
    public static final Color COLOR_PRIMARY_LIGHT = new Color(10, 132, 255);
    public static final Color COLOR_WHITE = new Color(255, 255, 255);
    public static final Color COLOR_TEXT_DARK = new Color(28, 28, 30);
    public static final Color COLOR_TEXT_MEDIUM = new Color(60, 60, 67);
    public static final Color COLOR_BORDER = new Color(200, 200, 205);
    public static final Color COLOR_ACCENT = new Color(255, 149, 0);

    // --- NEW BORDER STYLES ---
    public static final Border BORDER_DEFAULT = BorderFactory.createLineBorder(COLOR_BORDER, 1);
    public static final Border BORDER_SELECTED = BorderFactory.createLineBorder(COLOR_PRIMARY, 2); // Thicker, colored border

    public static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 48);
    public static final Font FONT_HEADING = new Font("Arial", Font.BOLD, 24);
    public static final Font FONT_SUBHEADING = new Font("Arial", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("Arial", Font.PLAIN, 16);
    public static final Font FONT_BUTTON = new Font("Arial", Font.BOLD, 16);
    public static final Font FONT_TABLE_HEADER = new Font("Arial", Font.BOLD, 14);

    public static void styleModernButton(JButton button) {
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFont(FONT_BUTTON);
        button.setBackground(COLOR_PRIMARY);
        button.setForeground(COLOR_WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(COLOR_PRIMARY_LIGHT);
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(COLOR_PRIMARY);
            }
        });
    }

    public static void styleTextField(JComponent component) {
        component.setFont(FONT_BODY);
        component.setBackground(COLOR_WHITE);
        component.setForeground(COLOR_TEXT_DARK);
        Border line = BorderFactory.createLineBorder(COLOR_BORDER);
        Border padding = BorderFactory.createEmptyBorder(8, 10, 8, 10);
        component.setBorder(BorderFactory.createCompoundBorder(line, padding));
    }
    
    public static void styleTable(JTable table, JScrollPane scrollPane) {
        table.getTableHeader().setFont(FONT_TABLE_HEADER);
        table.getTableHeader().setBackground(COLOR_BACKGROUND);
        table.getTableHeader().setForeground(COLOR_TEXT_DARK);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        table.setFont(FONT_BODY);
        table.setRowHeight(35);
        table.setGridColor(COLOR_BORDER);
        table.setShowGrid(true);
        table.setBackground(COLOR_WHITE);
        table.setForeground(COLOR_TEXT_DARK);
        table.setSelectionBackground(COLOR_PRIMARY);
        table.setSelectionForeground(COLOR_WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        scrollPane.getViewport().setBackground(COLOR_WHITE);
    }
}