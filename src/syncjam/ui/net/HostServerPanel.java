package syncjam.ui.net;

import syncjam.interfaces.ServiceContainer;
import syncjam.interfaces.Settings;
import syncjam.ui.Colors;
import syncjam.ui.DialogWindow;
import syncjam.ui.UIServices;
import syncjam.ui.buttons.base.ButtonUI;
import syncjam.ui.buttons.base.TextFieldUI;
import syncjam.ui.buttons.base.TextLabelUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class HostServerPanel extends JPanel
{
    private final String defaultPort;
    //private final NetTextField addressField, portField, passField;
    private final TextFieldUI[] fields;
    private final ButtonUI hostButton, cancelButton;

    private KeyAdapter keys = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            switch(key)
            {
                case KeyEvent.VK_ENTER:
                    hostButton.doClick();
                    break;
                case KeyEvent.VK_ESCAPE:
                    cancelButton.doClick();
                    break;
                case KeyEvent.VK_TAB:
                    boolean selected = false;
                    for(TextFieldUI f : fields)
                        if(f.hasFocus())
                            selected = true;
                    if(!selected)
                        fields[0].requestFocus();
                    break;
            }
        }
    };


    HostServerPanel(final NetworkPanel networkPanel, ServiceContainer services) {
        super();
        setOpaque(false);
        defaultPort = services.getService(Settings.class).getDefaultPort();

        GridBagConstraints constraints = new GridBagConstraints();
        this.setLayout(new GridBagLayout());

        UIServices.getMainWindow().addKeyListener(keys);

        TextLabelUI title = new TextLabelUI("Host Server", JLabel.CENTER);
        constraints.insets = new Insets(4,4,4,4);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipadx = 167;
        constraints.ipady = 30;
        constraints.weightx = 1;
        constraints.weighty = .5;
        this.add(title, constraints);

        String[] labels = {"Password", "Port"};
        int numPairs = labels.length;
        fields = new TextFieldUI[numPairs];

        //Create and populate the panel.
        JPanel p1 = new JPanel(new SpringLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Colors.get(Colors.Background2));
            }
        };
        for (int i = 0; i < numPairs; i++) {
            TextLabelUI l = new TextLabelUI(labels[i], JLabel.TRAILING);
            p1.add(l);
            TextFieldUI textField = new TextFieldUI(10, "", keys);
            l.setLabelFor(textField);
            p1.add(textField);
            fields[i] = textField;
        }

        fields[1].setText(defaultPort);
        fields[1].addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                fields[1].setText(fields[1].getText().replaceAll("[^\\d]", ""));
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

        //Panel for action buttons
        JPanel p2 = new JPanel(new SpringLayout());
        p2.setOpaque(false);
        p2.add(hostButton = new ButtonUI(0, 0, Colors.Background2, "Host") {
            @Override
            protected void clicked() {
                String password = fields[0].getText();
                fields[1].setText(fields[1].getText().replaceAll("[^\\d]", ""));
                String portText = fields[1].getText();
                int port = portText.equals("") ? 0 : Integer.parseInt(portText);

                if(port == 0)
                    DialogWindow.showErrorMessage("No port set");
                else
                {
                    UIServices.getMainWindow().removeKeyListener(keys);
                    System.out.println("Hosting: " + port + "\n" + password);
                    networkPanel.hostServer(port, password);
                    networkPanel.back();
                }
            }
        });

        p2.add(cancelButton = new ButtonUI(0, 0, Colors.Background2, "Cancel") {
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
        constraints.ipadx = 60;
        constraints.ipady = 80;
        this.add(p2, constraints);

        repaint();
    }

    private SpringLayout.Constraints getConstraintsForCell(int row, int col, Container parent, int cols)
    {
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Component c = parent.getComponent(row * cols + col);
        return layout.getConstraints(c);
    }

    private void makeCompactGrid(Container parent, int rows, int cols, int initialX, int initialY,
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

