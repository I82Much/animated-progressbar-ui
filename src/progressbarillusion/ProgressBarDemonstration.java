/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package progressbarillusion;

import java.applet.Applet;
import javax.swing.JProgressBar;

/**
 * An applet that draws the left to right and right to left progress bars
 * @author ndunn
 */
public class ProgressBarDemonstration extends Applet {

    private IllusionProgressBarUI leftToRight;
    private IllusionProgressBarUI rightToLeft;

    private JProgressBar leftToRightBar;
    private JProgressBar rightToLeftBar;

    /**
     * Initialization method that will be called after the applet is loaded
     * into the browser.
     */
    @Override
    public void init() {
        // TODO start asynchronous download of heavy resources

        leftToRight = new IllusionProgressBarUI();
        leftToRight.setDirection(IllusionProgressBarUI.AnimationDirection.LEFT_TO_RIGHT);
        rightToLeft = new IllusionProgressBarUI();
        rightToLeft.setDirection(IllusionProgressBarUI.AnimationDirection.RIGHT_TO_LEFT);

        leftToRightBar = new JProgressBar(0, 100);
        leftToRightBar.setUI(leftToRight);
        leftToRightBar.setValue(75);

        rightToLeftBar = new JProgressBar(0, 100);
        rightToLeftBar.setUI(rightToLeft);
        rightToLeftBar.setValue(75);


        add(leftToRightBar);
        add(leftToRightBar);
    }

    // TODO overwrite start(), stop() and destroy() methods
}
