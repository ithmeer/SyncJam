package syncjam.ui.base;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;

/**
 * Created by Marty on 1/30/2017.
 * Custom Color Picker because Java is BUNK, and the only good package i found was signed and that's bothersome
 */
public class ColorPickerUI extends JPanel
{
    private final ColorBox _colorBox;
    private long timeChanged; //Time that color was changed, used to prevent feedback loop of Spinners and ColorBox

    public ColorPickerUI(Color init)
    {
        setOpaque(false);
        setPreferredSize(new Dimension(349,264));
        setLayout(new GridBagLayout());
        GridBagConstraints mc = new GridBagConstraints();

        JPanel selector = new JPanel(new BorderLayout());
        float[] initHSB = Color.RGBtoHSB(init.getRed(), init.getGreen(), init.getBlue(), null);
        HueSlider _slider;
        selector.add(_slider = new HueSlider(initHSB[0]), BorderLayout.EAST);
        selector.add(_colorBox = new ColorBox(init, _slider), BorderLayout.CENTER);
        _colorBox.setBorder(new MatteBorder(1,1,1,1, Color.white));

        mc.fill = GridBagConstraints.BOTH;
        mc.anchor = GridBagConstraints.WEST;
        mc.weightx = 1.0;
        mc.weighty = 1.0;
        mc.gridx = 0;
        mc.insets = new Insets(7,2,2,2);
        this.add(selector, mc);

        GridBagConstraints sc = new GridBagConstraints();
        JPanel side = new JPanel(new GridBagLayout());
        side.setOpaque(false);

    //SELECTED COLOR INDICATOR
        JPanel colorIndicator = new JPanel();
        colorIndicator.setPreferredSize(new Dimension(59,59));
        colorIndicator.setBorder(new MatteBorder(1,1,1,1,Color.white));
        colorIndicator.setBackground(init);
        sc.fill = GridBagConstraints.HORIZONTAL;
        sc.anchor = GridBagConstraints.PAGE_START;
        sc.gridy = 0;
        sc.weighty = 1;
        sc.insets = new Insets(2,2,2,2);
        side.add(colorIndicator, sc);

    //COLOR SPINNERS
        JSpinner redSpinner = new JSpinner();
        redSpinner.setValue(getColor().getRed());
        sc.gridy = 1;
        side.add(redSpinner, sc);

        JSpinner greenSpinner = new JSpinner();
        greenSpinner.setValue(getColor().getGreen());
        sc.gridy = 2;
        side.add(greenSpinner, sc);

        JSpinner blueSpinner = new JSpinner();
        blueSpinner.setValue(getColor().getBlue());
        sc.gridy = 3;
        side.add(blueSpinner, sc);

        ChangeListener spinnerListener = e -> {
            if(System.currentTimeMillis()*1000 - timeChanged > 10000 ) {
                int r = (int) redSpinner.getValue();
                int g = (int) greenSpinner.getValue();
                int b = (int) blueSpinner.getValue();
                setColor(new Color(r, g, b));
            }
        };
        redSpinner.addChangeListener(spinnerListener);
        greenSpinner.addChangeListener(spinnerListener);
        blueSpinner.addChangeListener(spinnerListener);

    //HEX CODE FIELD
        JTextField hexCode = new JTextField(toHex(getColor()),5);
        hexCode.setHorizontalAlignment(JTextField.CENTER);
        hexCode.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void changedUpdate(DocumentEvent e) {
            }
            @Override public void insertUpdate(DocumentEvent e) {
                Runnable updateColor = () -> {
                    if (System.currentTimeMillis() * 1000 - timeChanged > 10000 && hexCode.getText().length() == 6)
                        setColor(toRGB(hexCode.getText()));
                    else if (hexCode.getText().length() > 6)
                        hexCode.setText(hexCode.getText().substring(0, 6));
                };
                SwingUtilities.invokeLater(updateColor);
            }
            @Override public void removeUpdate(DocumentEvent e) {

            }
        });
        hexCode.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                super.focusGained(e);
                hexCode.selectAll();
            }
        });
        sc.gridy = 4;
        side.add(hexCode, sc);

    //CHANGE LISTENER FROM COLORBOX
        addChangeListener(e -> {
            updateTime();
            Color col = getColor();
            colorIndicator.setBackground(col);
            redSpinner.setValue(col.getRed());
            greenSpinner.setValue(col.getGreen());
            blueSpinner.setValue(col.getBlue());
            hexCode.setText(toHex(col));
        });

        mc.anchor = GridBagConstraints.EAST;
        mc.gridx = 1;
        mc.ipadx = 32;
        mc.weightx = 0;
        this.add(side, mc);
    }
    private void updateTime() {
        timeChanged = System.currentTimeMillis() * 1000;
    }

    private String toHex(Color c)
    {
        return Integer.toHexString(c.getRGB()).substring(2).toUpperCase();
    }
    private Color toRGB(String hex) {
        Color out = getColor();
        try {
            out = Color.decode("#"+hex);
        } catch (NumberFormatException e){}
        return out;
    }
    private Color getColor() {
        return _colorBox.getColor();
    }
    private void setColor(Color c) {
        _colorBox.setColor(c);
    }

    public void addChangeListener(PropertyChangeListener listener) {
        _colorBox.addPropertyChangeListener("colorChanged", listener);
    }
}
class ColorBox extends JPanel implements MouseListener, MouseMotionListener
{
    private final HueSlider _hueSlider;
    private final BufferedImage gradient = new BufferedImage(255, 255, BufferedImage.TYPE_INT_RGB);
    private float _saturation = 1, _brightness = 1;
    private Color _color;

    ColorBox(Color init, HueSlider hueSlider) {
        gradient.createGraphics();
        setPreferredSize(new Dimension(255,255));
        setMinimumSize(new Dimension(255,255));
        addMouseListener(this);
        addMouseMotionListener(this);

        _hueSlider = hueSlider;
        _hueSlider.addPropertyChangeListener("hueChanged", e -> ColorBox.this.updateHue(_hueSlider.getHue()));

        float[] initHSB = Color.RGBtoHSB(init.getRed(), init.getGreen(), init.getBlue(), null);
        _saturation = initHSB[1];
        _brightness = initHSB[2];
        updateHue(_hueSlider.getHue());
    }

    Color getColor() {
        float val1 = _hueSlider.getHue();
        float val2 = _saturation;
        float val3 = _brightness;
        return Color.getHSBColor(val1, val2, val3);
    }
    public void setColor(Color c) {
        float[] hsbVals = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        float h = hsbVals[0];
        float s = hsbVals[1];
        float b = hsbVals[2];
        setColor(h, s, b);
    }
    private void setColor(float hue, float sat, float bri) {
        _hueSlider.setHue(hue);
        _saturation = bounds(sat);
        _brightness = bounds(bri);
        updateHue(_hueSlider.getHue());
        repaint();
    }
    private float bounds(float f)
    {
        float aboveZero = Math.max(f, 0f);
        float belowOne = Math.min(aboveZero,1f);
        return belowOne;
    }

    private void updateHue(float i) {
        Color hue = Color.getHSBColor(i, 1, 1);
        int w = gradient.getWidth();
        int h = gradient.getHeight();
        Graphics2D g = (Graphics2D) gradient.getGraphics();
        GradientPaint primary = new GradientPaint(
                0f, 0f, Color.WHITE,
                w, 0f, hue);
        GradientPaint shade = new GradientPaint(
                0f, 0f, new Color(0, 0, 0, 0),
                0f, h, new Color(0, 0, 0, 255));
        g.setPaint(primary);
        g.fillRect(0, 0, w, h);
        g.setPaint(shade);
        g.fillRect(0, 0, w, h);

        repaint();
        firePropertyChange("colorChanged", _color, getColor());
        _color = getColor();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(gradient, 0, 0, null);

        int x = (int)(_saturation*getWidth());
        int y = getHeight() - (int)(_brightness*getHeight());
        g.setColor(Color.white);
        g.drawLine(x-1, y, x-4, y);
        g.drawLine(x+1, y, x+4, y);
        g.drawLine(x, y-1, x, y-4);
        g.drawLine(x, y+1, x, y+4);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        float sat = (float)e.getX()/(float)getWidth();
        float bri = (float)e.getY()/(float)getHeight();
        setColor(_hueSlider.getHue(), sat, 1-bri);
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        float sat = (float)e.getX()/(float)getWidth();
        float bri = (float)e.getY()/(float)getHeight();
        setColor(_hueSlider.getHue(), sat, 1-bri);
    }
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    @Override
    public void mouseReleased(MouseEvent e) {

    }
    @Override
    public void mouseEntered(MouseEvent e) {

    }
    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
class HueSlider extends JPanel implements MouseListener, MouseMotionListener
{
    private final BufferedImage _hueBar = new BufferedImage(16,255,BufferedImage.TYPE_INT_RGB);
    private float _hue = 0f;

    HueSlider(float hue) {
        setPreferredSize(new Dimension(_hueBar.getWidth(), _hueBar.getHeight()));
        makeHueGraphic();
        setHue(hue);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void makeHueGraphic() {
        Graphics g = _hueBar.createGraphics();
        int h = _hueBar.getHeight();
        for(int y = 0; y < h; y++)
        {
            g.setColor(Color.getHSBColor(y/(float)h, 1f, 1f));
            g.drawLine(0, y, _hueBar.getWidth(), y);
        }
    }

    void setHue(float hue) {
        firePropertyChange("hueChanged", _hue, hue);
        _hue = hue;
        repaint();
    }
    float getHue() {
        return _hue;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(_hueBar, 0, 0, null);
        g.setColor(Color.white);
        int val = (int)(_hue * _hueBar.getHeight());
        g.drawLine(0, val-1, _hueBar.getWidth(), val-1);
        g.drawLine(0, val, _hueBar.getWidth(), val);
        g.drawLine(0, val+1, _hueBar.getWidth(), val+1);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        setHue((float)e.getY()/(float)_hueBar.getHeight());
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        setHue((float)e.getY()/(float)_hueBar.getHeight());
    }
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    @Override
    public void mouseReleased(MouseEvent e) {

    }
    @Override
    public void mouseEntered(MouseEvent e) {

    }
    @Override
    public void mouseExited(MouseEvent e) {

    }
    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
