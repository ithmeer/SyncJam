package syncjam.ui.net;

import syncjam.ui.Colors;

import javax.swing.*;
import java.awt.*;

public class HostServerPanel extends JPanel
{
    private final String defaultPort = "9982";
    //private final NetTextField addressField, portField, passField;
    private final NetTextField[] fields;


    public HostServerPanel(final NetworkPanel networkPanel) {
        super();

        this.setBackground(Colors.c_Background1);

        GridLayout gl = new GridLayout(3, 1);
        this.setLayout(gl);

        NetLabel title = new NetLabel("Connection Settings", JLabel.CENTER);
        this.add(title);

        String[] labels = {"Password", "Port"};
        int numPairs = labels.length;
        fields = new NetTextField[numPairs];

        //Create and populate the panel.
        JPanel p1 = new JPanel(new SpringLayout());
        p1.setBackground(Colors.c_Background2);
        for (int i = 0; i < numPairs; i++) {
            NetLabel l = new NetLabel(labels[i], JLabel.TRAILING);
            p1.add(l);
            NetTextField textField = new NetTextField(10, "");
            l.setLabelFor(textField);
            p1.add(textField);
            fields[i] = textField;
        }

        fields[1].setText(defaultPort);

        //Lay out the panel.
        makeCompactGrid(p1,
                numPairs, 2, //rows, cols
                6, 6,        //initX, initY
                6, 10);       //xPad, yPad

        this.add(p1);


        JPanel p2 = new JPanel(new SpringLayout());
        p2.setBackground(Colors.c_Background1);

        p2.add(new NetButton("Host") {
            @Override
            protected void clicked() {
                String password = fields[0].getText();
                int port = Integer.parseInt(fields[1].getText());

                System.out.println("Hosting: " + port + "\n" + password);
                networkPanel.hostServer(port, password);
                networkPanel.back();
            }
        });

        p2.add(new NetButton("Cancel") {
            @Override
            protected void clicked() {
                networkPanel.back();
            }
        });
        makeCompactGrid(p2,
                2, 1,        //rows, cols
                6, 6,        //initX, initY
                6, 8);       //xPad, yPad
        this.add(p2);
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

