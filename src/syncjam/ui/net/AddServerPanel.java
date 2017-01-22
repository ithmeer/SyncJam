package syncjam.ui.net;

import syncjam.ui.Colors;
import syncjam.ui.base.DialogWindow;
import syncjam.ui.UIServices;
import syncjam.ui.buttons.base.ButtonUI;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class AddServerPanel extends JPanel
{
    private final String defaultPort = "9982";
    //private final NetTextField addressField, portField, passField;
    private final NetTextField[] fields;
    private final NetButton addButton, cancelButton;
    private NetworkPanel networkPanel;

    private KeyAdapter keys = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if(networkPanel.isVisible())
            {
                switch (key) {
                    case KeyEvent.VK_ENTER:
                        addButton.clicked();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        cancelButton.clicked();
                        break;
                    case KeyEvent.VK_TAB:
                        boolean selected = false;
                        for (NetTextField f : fields)
                            if (f.hasFocus())
                                selected = true;
                        if (!selected)
                            fields[0].requestFocus();
                        break;
                }
            }
        }
    };

    public AddServerPanel(final NetworkPanel np) {
        super();
        networkPanel = np;
        this.setBackground(Colors.get(Colors.Background1));

        GridBagConstraints constraints = new GridBagConstraints();
        this.setLayout(new GridBagLayout());

        UIServices.getMainWindow().addKeyListener(keys);

        NetLabel title = new NetLabel("Add Server", JLabel.CENTER);
        constraints.insets = new Insets(4,4,4,4);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipadx = 171;
        constraints.ipady = 30;
        constraints.weightx = 1;
        constraints.weighty = .5;
        this.add(title, constraints);

        String[] labels = {"Name", "IP Address", "Port", "Password"};
        int numPairs = labels.length;
        fields = new NetTextField[numPairs];

        //Create and populate the panel.
        JPanel p1 = new JPanel(new SpringLayout());
        p1.setBackground(Colors.get(Colors.Background2));
        for (int i = 0; i < numPairs; i++) {
            NetLabel l = new NetLabel(labels[i], JLabel.TRAILING);
            p1.add(l);
            NetTextField textField = new NetTextField(10, "", keys);
            l.setLabelFor(textField);
            p1.add(textField);
            fields[i] = textField;
        }

        //fields[2].setNumbersOnly(true);
        fields[2].setText(defaultPort);
        fields[2].addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                fields[2].setText(fields[2].getText().replaceAll("[^\\d]", ""));
            }
        });

        //Lay out the panel.
        makeCompactGrid(p1,
                numPairs, 2, //rows, cols
                6, 6,        //initX, initY
                6, 10);       //xPad, yPad

        constraints.insets = new Insets(10,4,8,4);
        constraints.gridy = 1;
        constraints.ipadx = 40;
        constraints.ipady = 10;
        this.add(p1, constraints);


        JPanel p2 = new JPanel(new SpringLayout());
        p2.setBackground(Colors.get(Colors.Background1));

        p2.add(addButton = new NetButton("Add") {
            @Override
            protected void clicked() {
                String name = fields[0].getText();
                String address = fields[1].getText();
                fields[2].setText(fields[2].getText().replaceAll("[^\\d]", ""));
                String portText = fields[2].getText();
                int port = portText.equals("") ? 0 : Integer.parseInt(portText);
                String password = fields[3].getText();

                if(address.equals(""))
                    DialogWindow.showErrorMessage("No server address set");
                else if(port == 0)
                    DialogWindow.showErrorMessage("No port set");
                else
                {
                    UIServices.getMainWindow().removeKeyListener(keys);
                    System.out.println("Adding: " + address + "\n" + port + "\n" + password);
                    networkPanel.addServer(name, address, port, password);
                    networkPanel.back();
                }
            }
        });

        p2.add(cancelButton = new NetButton("Cancel") {
            @Override
            protected void clicked() {
                UIServices.getMainWindow().removeKeyListener(keys);
                networkPanel.back();
            }
        });
        makeCompactGrid(p2,
                2, 1,        //rows, cols
                0, 0,        //initX, initY
                0, 8);       //xPad, yPad

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(4,20,4,20);
        constraints.gridy = 2;
        constraints.ipadx = 30;
        constraints.ipady = 80;
        this.add(p2, constraints);
        repaint();


    }


    protected SpringLayout.Constraints getConstraintsForCell(int row, int col, Container parent, int cols)
    {
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Component c = parent.getComponent(row * cols + col);
        return layout.getConstraints(c);
    }

    protected void makeCompactGrid(Container parent, int rows, int cols, int initialX, int initialY,
                                 int xPad, int yPad)
    {
        SpringLayout layout;
        try {
            layout = (SpringLayout)parent.getLayout();
        }
        catch (ClassCastException exc) {
            System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
            return;
        }

        //Align all cells in each column and make them the same width.
        Spring x = Spring.constant(initialX);
        for (int c = 0; c < cols; c++) {
            Spring width = Spring.constant(0);
            for (int r = 0; r < rows; r++) {
                width = Spring.max(width,
                        getConstraintsForCell(r, c, parent, cols).
                                getWidth());
            }
            for (int r = 0; r < rows; r++) {
                SpringLayout.Constraints constraints =
                        getConstraintsForCell(r, c, parent, cols);
                constraints.setX(x);
                constraints.setWidth(width);
            }
            x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
        }

        //Align all cells in each row and make them the same height.
        Spring y = Spring.constant(initialY);
        for (int r = 0; r < rows; r++) {
            Spring height = Spring.constant(0);
            for (int c = 0; c < cols; c++) {
                height = Spring.max(height,
                        getConstraintsForCell(r, c, parent, cols).
                                getHeight());
            }
            for (int c = 0; c < cols; c++) {
                SpringLayout.Constraints constraints =
                        getConstraintsForCell(r, c, parent, cols);
                constraints.setY(y);
                constraints.setHeight(height);
            }
            y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
        }

        //Set the parent's size.
        SpringLayout.Constraints pCons = layout.getConstraints(parent);
        pCons.setConstraint(SpringLayout.SOUTH, y);
        pCons.setConstraint(SpringLayout.EAST, x);
    }
}

class NetTextField extends TextField
{
    protected NetTextField(int length, String default_text)
    {
        setColumns(length);
        setText(default_text);
        setBackground(Colors.get(Colors.Background1));
        setForeground(Colors.get(Colors.Foreground1));
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                selectAll();
            }
        });
    }
    protected NetTextField(int length, String default_text, KeyAdapter key)
    {
        this(length, default_text);
        addKeyListener(key);
    }
}
class NetLabel extends JLabel
{
    protected NetLabel(String text)
    {
        super(text);
    }
    protected NetLabel(String text, int trailing)
    {
        super(text, trailing);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setForeground(Colors.get(Colors.Foreground1));
    }
}
class NetButton extends ButtonUI
{
    protected NetButton(String text)
    {
        super(0, 0, Colors.Background2);
        setText(text);
        setMargin(new Insets(0,0,0,0));
    }
    @Override
    protected void clicked() {}
}