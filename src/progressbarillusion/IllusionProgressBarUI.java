/*
 * Copyright 1997-2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
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
 * <a href="http://stackoverflow.com/questions/4609628/changing-jprogressbar/4610875#4610875">a StackOverflow question</a>
 * which in turn was asked in response to <a href="http://www.newscientist.com/blogs/nstv/2010/12/best-videos-of-2010-progress-bar-illusion.html">an online video</a>
 * showing that
 * progress bars which have an animated movement to them appear to move faster.
 * In particular, bars which have a pattern moving right to left appear
 * to move faster than those whose pattern moves left to right.
 * 
 * Some source code is taken from the BasicProgressBarUI class, source code
 * available <a href="http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/javax/swing/plaf/basic/BasicProgressBarUI.java">on grepcode.</a>
 *
 * This class is not production worthy; in particular, it only is designed to work
 * with left to right, horizontal progress bars.
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

    /**
     * We create an image containing a gradient from dark to white and back to dark.
     * We tile this pattern multiple times across the length of the progress bar.
     * By redrawing this image multiple times across the length of the bar (and
     * at different offsets each frame), we achieve the illusion of motion.
     */
    private BufferedImage barImage = createRippleImage(Color.blue.darker(), Color.white);

    // Create a ribbon
    protected BufferedImage createRippleImage(Color darkColor, Color lightColor) {
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


    /**
     * Used by an internal timer to increment the state of the animation.
     *
     * This code is copied from the original implementation, but with our own
     * numFrames variable (the standard numFrames is private in ProgressBarUI)
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


    /**
     * This method is called when the progress bar is in determinate mode (e.g.
     * the progress bar is reflecting 50% completion) and the bar needs to be
     * redrawn.
     *
     * In order to achieve an effect of movement, we take the gradient image
     * we created (@see barImage), and tile it across the length of the filled
     * in area of the progress bar.  At each frame, this method is called, and the
     * currentFrameIndex variable is changed.  We use this value to move the
     * center of the tiled images to the right or to the left depending on which
     * direction or animation is moving.
     *
     *
     * @param g the graphics context onto which to draw the determinate progress bar
     * @param c the component
     */
    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        // We are only going to deal with horizontal painting
        if (progressBar.getOrientation() != JProgressBar.HORIZONTAL) {
            super.paintDeterminate(g, c);
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


        // Make sure we only draw in the region of the progress bar.  This allows
        // us to be sloppy with our drawing (which is impossible to avoid when
        // dealing with the drawImage commands) and yet still avoid bad artifacts
        g.setClip(b.left, b.top, amountFull, barRectHeight);


        // Here we calculate a pixel offset by which to shift all of our tiled images.
        // If we're moving right to left, then we offset by a decreasing amount each
        // tick.  If we're moving left to right, we do the opposite.
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
        // ensure both sides have full coverage just to be safe
        numRepetitions += 2;

        for (int i = 0; i < numRepetitions; i++) {
            // The first image we want drawn to the left, even offscreen if
            // necessary.
            int xOffset = (i - 1) * barImage.getWidth() + offset;
            g.drawImage(barImage, xOffset, 0, null);
        }
        g.drawRect(b.left, b.top, amountFull, barRectHeight);
    }

    /**
     * Map a value in one range to a value in a different range. See
     * <a href="http://developmentality.wordpress.com/2009/12/15/useful-utility-functions-0-of-n/">a blog post</a>
     * I wrote about the subject.
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

    /** Linearly interpolate between two values */
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
