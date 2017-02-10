package syncjam.ui.base;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:

import syncjam.ui.Colors;
import syncjam.ui.UIServices;
import syncjam.ui.buttons.base.TextLabelUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CustomFrame extends JFrame
{
    private JPanel titleBar, contentPanel;
    private ComponentResizer cr;
    public ComponentMover   cm;
    private JButton _minimizeButton, _infoButton;

    public CustomFrame(int minW, int minH, String title) {
        this(minW, minH);
        titleBar.add(new TextLabelUI(title, SwingConstants.CENTER), BorderLayout.CENTER);
    }

    public CustomFrame(int minW, int minH)
    {
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                close();
            }
        });

        ////////mainPanel////////
        JPanel mainPanel = new JPanel(new BorderLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Colors.get(Colors.Background1));
                g.fillRect(0,0,super.getWidth(), super.getHeight());
                g.setColor(Colors.get(Colors.Background2));
                g.drawRect(0,0,super.getWidth(), super.getHeight());
                g.drawRect(0,0,super.getWidth()-1, super.getHeight()-1);
                g.setColor(Colors.get(Colors.Background1).brighter());
                g.drawRect(1,1,super.getWidth()-3, super.getHeight()-3);
            }
        };
        mainPanel.setBorder( new EmptyBorder(6, 6, 6, 6) );
        this.add(mainPanel, BorderLayout.CENTER);

        ////////titleBar////////
        titleBar = new JPanel(new BorderLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Colors.get(Colors.Background1));
                g.fillRect(0,0,super.getWidth(), super.getHeight());
            }
        };
        titleBar.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        ////////Close/Minimize Button Panel////////
        JPanel titleButtons = new JPanel(new BorderLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Colors.get(Colors.Background1));
                g.fillRect(0,0,super.getWidth(), super.getHeight());
            }
        };
        titleButtons.add(makeCloseButton(), BorderLayout.EAST);
        titleButtons.add(_minimizeButton = makeMinimizeButton(), BorderLayout.CENTER);
        titleBar.add(titleButtons, BorderLayout.EAST);

        titleBar.add(_infoButton = makeInfoButton(), BorderLayout.WEST);

        mainPanel.add(titleBar, BorderLayout.NORTH);

        ////////contentPanel////////
        contentPanel = new JPanel(new BorderLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Colors.get(Colors.Background1));
                g.fillRect(0,0,super.getWidth(), super.getHeight());
            }
        };
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        cm = new ComponentMover(this, titleBar);
        cm.registerComponent(contentPanel);
        cr = new ComponentResizer(this);
        cr.setMinimumSize(new Dimension(minW, minH));
        Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
        cr.setMaximumSize(screenSize);

        this.setLocationRelativeTo(null);
    }

    public void open()
    {
        /*try {
            //URL url = new URL("");
            //Toolkit kit = Toolkit.getDefaultToolkit();
            //Image img = kit.createImage(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/

        Image img = UIServices.loadImage("SJLogo128.png");
        this.setIconImage(img);

        this.setUndecorated(true);
        this.pack();
        this.setVisible(true);
        Point pos = getLocation();
        Dimension size = getSize();
        this.setLocation(new Point((int)(pos.getX() - size.getWidth()/2), (int)(pos.getY() - size.getHeight()/2)));
    }
    protected void close() {}

    protected void clickedInfoButton() {
    }

    public void allowMinimizing(boolean allow)
    {
        _minimizeButton.setVisible(allow);
    }

    public void allowResizing(boolean allow)
    {
        if(allow)
            cr.registerComponent(this);
        else
            cr.deregisterComponent(this);
    }

    public void setMinimumSize(Dimension s)
    {
        cr.setMinimumSize(s);

        //Move window left if new minimum size pushes right edge off screen
        Rectangle r = cr.getBoundingRect(this);
        Rectangle b = this.getBounds();
        double rightEdge = b.getX()+b.getWidth();
        double screenRight = r.getX()+r.getWidth();
        if(rightEdge > screenRight)
        {
            double diff = rightEdge - screenRight;
            this.setBounds((int)(b.getX()-diff), (int)b.getY(), (int)b.getWidth(), (int)b.getHeight());
        }
    }
    public Dimension getMinimumSize()       { return cr.getMinimumSize(); }

    public Container getContentPanel() {
        return contentPanel;
    }

    private JButton makeInfoButton() {
        JButton button = new JButton(UIServices.loadIcon("SJLogo14.png", Colors.Foreground1));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setOpaque(false);
        button.addActionListener(e -> {
            clickedInfoButton();
        });
        return button;
    }
    private JButton makeCloseButton() {
        JButton button = new JButton(new CloseIcon());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setOpaque(true);
        button.addActionListener(e -> {
            JComponent b = (JComponent) e.getSource();
            Container c = b.getTopLevelAncestor();
            if (c instanceof Window) {
                Window w = (Window) c;
                w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
            }
        });
        return button;
    }
    private JButton makeMinimizeButton() {
        JButton button = new JButton(new MinimizeIcon());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setOpaque(true);
        button.addActionListener(e -> {
            JComponent b = (JComponent) e.getSource();
            Container c = b.getTopLevelAncestor();
            if (c instanceof JFrame) {
                JFrame w = (JFrame) c;
                w.setState(JFrame.ICONIFIED);
            }
        });
        return button;
    }
}

class CloseIcon implements Icon {
    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.setPaint(Colors.get(Colors.Background1));
        g2.fillRect(0, 0, getIconWidth(), getIconHeight());
        g2.setPaint(Colors.get(Colors.Foreground1));
        g2.drawLine(4,  4, 11, 11);
        g2.drawLine(4,  5, 10, 11);
        g2.drawLine(5,  4, 11, 10);
        g2.drawLine(11, 4,  4, 11);
        g2.drawLine(11, 5,  5, 11);
        g2.drawLine(10, 4,  4, 10);
        g2.dispose();
    }
    @Override public int getIconWidth() {
        return 16;
    }
    @Override public int getIconHeight() {
        return 16;
    }
}
class MinimizeIcon implements Icon {
    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.setPaint(Colors.get(Colors.Background1));
        g2.fillRect(0, 0, getIconWidth(), getIconHeight());
        g2.setPaint(Colors.get(Colors.Foreground1));
        g2.drawLine(4,  11, 11, 11);
        g2.drawLine(5,  10, 10, 10);
        g2.dispose();
    }
    @Override public int getIconWidth() {
        return 16;
    }
    @Override public int getIconHeight() {
        return 16;
    }
}