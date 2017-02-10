package syncjam.ui.buttons.base;

import syncjam.ui.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;

public class PasswordFieldUI extends JPasswordField
{
    private Colors _bg = Colors.Background1, _fg = Colors.Foreground1;
    private final char DEFAULT_ECHO_CHAR;

    public PasswordFieldUI(int length, String default_text)
    {
        setColumns(length);
        setText(default_text);
        setBorder(BorderFactory.createEmptyBorder());
        setBackground(Colors.get(_bg));
        setForeground(Colors.get(_fg));
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                selectAll();
            }
        });
        DEFAULT_ECHO_CHAR = getEchoChar();
    }
    public PasswordFieldUI(int length, String default_text, KeyAdapter key)
    {
        this(length, default_text);
        addKeyListener(key);
    }

    public void showPassword(boolean visible)
    {
        if(visible)
            this.setEchoChar((char)0);
        else
            this.setEchoChar(DEFAULT_ECHO_CHAR);
    }

    public void setBGColor(Colors _bg) {
        this._bg = _bg;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Colors.get(_bg));
        setForeground(Colors.get(_fg));
        setCaretColor(Colors.get(Colors.Foreground2));
    }
}
