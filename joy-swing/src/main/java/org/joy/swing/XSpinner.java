/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.swing;

import javax.swing.*;

/**
 *
 * @author Kevice
 */
public class XSpinner extends JSpinner {

    /**
     * Constructs a complete spinner with pair of next/previous buttons
     * and an editor for the <code>SpinnerModel</code>.
     */
    public XSpinner(SpinnerModel model) {
        super(model);
    }

    /**
     * Constructs a spinner with an <code>Integer SpinnerNumberModel</code>
     * with initial value 0 and no minimum or maximum limits.
     */
    public XSpinner() {
        super();
    }
}
