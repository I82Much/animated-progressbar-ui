/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package progressbarillusion;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 * This class exists to illustrate how to make an animated progress bar by
 * making a custom ProgressBarUI delegate.  It was created in response to
 * a StackOverflow question (http://stackoverflow.com/questions/4609628/changing-jprogressbar/4610875#4610875)
 * which in turn was asked in response to an online video (http://www.newscientist.com/blogs/nstv/2010/12/best-videos-of-2010-progress-bar-illusion.html) showing that
 * progress bars which have an animated movement to them appear to move faster.
 * In particular, bars which have a pattern moving right to left appear
 * to move faster than those whose pattern moves left to right.
 * 
 *
 * @author ndunn
 */
public class IllusionProgressBarUI extends BasicProgressBarUI {

    private int numFrames = 200;

    public enum AnimationDirection {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT
    }

    private AnimationDirection direction = AnimationDirection.RIGHT_TO_LEFT;

    public IllusionProgressBarUI() {
        startAnimationTimer();
    }

    private BufferedImage barImage = createRippleImage(Color.blue.darker(), Color.white);

    // Create a ribbon
    public BufferedImage createRippleImage(Color darkColor, Color lightColor) {
        int width = 40;
        int height = 40;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        GradientPaint darkToLight = new GradientPaint(new Point2D.Double(0,0), darkColor, new Point2D.Double(width/2, 0), lightColor);
        GradientPaint lightToDark = new GradientPaint(new Point2D.Double(width/2,0), lightColor, new Point2D.Double(width, 0), darkColor);
        g2.setPaint(darkToLight);
        g2.fillRect(0, 0, width/2, height);
        g2.setPaint(lightToDark);
        g2.fillRect(width/2, 0, width/2, height);

        return image;
    }

    public AnimationDirection getDirection() {
        return direction;
    }

    public void setDirection(AnimationDirection direction) {
        this.direction = direction;
    }

    

    @Override
    protected void paintIndeterminate(Graphics g, JComponent c) {
        super.paintIndeterminate(g, c);
        g.setColor(Color.BLACK);
        g.drawString(String.valueOf(getAnimationIndex()), progressBar.getWidth()/2, progressBar.getHeight());
    }

    /**
     * numFrames is private in the BasicProgressBarUI and always equal to 0 in
     * determinate mode.  Override with our own private variable..
     */
    @Override
    protected void incrementAnimationIndex() {
        int newValue = getAnimationIndex() + 1;
        if (newValue < numFrames) {
            setAnimationIndex(newValue);
        } else {
            setAnimationIndex(0);
        }
    }


    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        // We are only going to deal with horizontal painting
        if (progressBar.getOrientation() != JProgressBar.HORIZONTAL) {
            super.paintDeterminate(g, c);
            return;
        }

        if (!(g instanceof Graphics2D)) {
            return;
        }

        /*
         Copied from the BasicProgressBar code - calculates the actual dimensions of
         the progress bar area, discounting the insets etc
        */
        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return;
        }

        // amount of progress to draw; measured in pixels
        int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

        Graphics2D g2 = (Graphics2D)g;



        // BasicGraphicsUtils is not public.


        boolean leftToRight = true; // BasicGraphicsUtils.isLeftToRight(c)
        if (leftToRight) {


            g.setClip(b.left, b.top, amountFull, barRectHeight);

            int offset = 0;
            if (direction == AnimationDirection.RIGHT_TO_LEFT) {
                offset = (int) (map(getAnimationIndex(), 0, numFrames, barImage.getWidth(), 0));
            }
            else {
                offset = (int) (map(getAnimationIndex(), 0, numFrames, 0, barImage.getWidth()));
            }

            // How many repetitions of the image need to be drawn to ensure that
            // a full progress bar has no gaps in the image?
            int numRepetitions = progressBar.getWidth() / barImage.getWidth();
            // ensure both sides
            numRepetitions += 2;

            // draw it shifted left
            for (int i = 0; i < numRepetitions; i++) {
                // The first image we want drawn to the left, even offscreen if
                // necessary.
                int xOffset = (i - 1) * barImage.getWidth() + offset;
                g.drawImage(barImage, xOffset, 0, null);
            }
            g2.drawRect(b.left, b.top, amountFull, barRectHeight);
//                    amountFull + b.left, (barRectHeight/2) + b.top);
        } else {
            g2.drawLine((barRectWidth + b.left),
                    (barRectHeight/2) + b.top,
                    barRectWidth + b.left - amountFull,
                    (barRectHeight/2) + b.top);
        }
    }

    /**
     * @param value The incoming value to be converted
     * @param low1  Lower bound of the value's current range
     * @param high1 Upper bound of the value's current range
     * @param low2  Lower bound of the value's target range
     * @param high2 Upper bound of the value's target range
     */
    public static final double map(double value, double low1, double high1, double low2, double high2) {

        double diff = value - low1;
        double proportion = diff / (high1 - low1);

        return lerp(low2, high2, proportion);
    }

    // Linearly interpolate between two values
    public static final double lerp(double value1, double value2, double amt) {
        return ((value2 - value1) * amt) + value1;
    }



    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel() {
            {
                JProgressBar bar = new JProgressBar();
                bar.setBorder(BorderFactory.createEtchedBorder());
                bar.setForeground(Color.red);
                final IllusionProgressBarUI ui = new IllusionProgressBarUI();
                bar.setUI(ui);
                bar.setValue(75);
                add(bar);

                JRadioButton leftToRight = new JRadioButton(new AbstractAction("Left to right") {
                    public void actionPerformed(ActionEvent e) {
                        ui.setDirection(AnimationDirection.LEFT_TO_RIGHT);
                    }
                });
                JRadioButton rightToLeft = new JRadioButton(new AbstractAction("Right to left") {
                    public void actionPerformed(ActionEvent e) {
                        ui.setDirection(AnimationDirection.RIGHT_TO_LEFT);
                    }
                });

                ButtonGroup mutuallyExclusiveButtons = new ButtonGroup();
                mutuallyExclusiveButtons.add(leftToRight);
                mutuallyExclusiveButtons.add(rightToLeft);
                rightToLeft.setSelected(true);

                
                add(leftToRight);
                add(rightToLeft);

            }
        };
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
