/*
 * Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */


package javafx.scene.layout;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * AnchorPane allows the edges of child nodes to be anchored to an offset from
 * the anchorpane's edges.  If the anchorpane has a border and/or padding set, the
 * offsets will be measured from the inside edge of those insets.
 * <p>
 * AnchorPane lays out each managed child regardless of the child's visible property value;
 * unmanaged children are ignored for all layout calculations.</p>
 * <p>
 * Anchorpanes may be styled with backgrounds and borders using CSS.  See
 * {@link javafx.scene.layout.Region Region} superclass for details.</p>
 *
 * <h4>Anchor Constraints</h4>
 * The application sets anchor constraints on each child to configure the anchors
 * on one or more sides.  If a child is anchored on opposite sides (and is resizable), the
 * anchorpane will resize it to maintain both offsets, otherwise the anchorpane
 * will resize it to its preferred size.  If in the former case (anchored on opposite
 * sides) and the child is not resizable, then only the top/left anchor will be honored.
 * AnchorPane provides a static method for setting each anchor constraint.
 * <p>
 * <table border="1">
 * <tr><th>Constraint</th><th>Type</th><th>Description</th></tr>
 * <tr><td>topAnchor</td><td>double</td><td>distance from the anchorpane's top insets to the child's top edge.</td></tr>
 * <tr><td>leftAnchor</td><td>double</td><td>distance from the anchorpane's left insets to the child's left edge.</td></tr>
 * <tr><td>bottomAnchor</td><td>double</td><td>distance from the anchorpane's bottom insets to the child's bottom edge.</td></tr>
 * <tr><td>rightAnchor</td><td>double</td><td>distance from the anchorpane's right insets to the child's right edge.</td></tr>
 * </table>
 * <p>
 * AnchorPane Example:
 * <pre><code>     AnchorPane anchorpane = new AnchorPane();
 *     // List should stretch as anchorpane is resized
 *     ListView list = new ListView();
 *    <b> AnchorPane.setTopAnchor(list, 10.0);
 *     AnchorPane.setLeftAnchor(list, 10.0);
 *     AnchorPane.setRightAnchor(list, 65.0);</b>
 *     // Button will float on right edge
 *     Button button = new Button("Add");
 *     <b>AnchorPane.setTopAnchor(button, 10.0);
 *     AnchorPane.setRightAnchor(button, 10.0);</b>
 *     anchorpane.getChildren().addAll(list, button);
 * </code></pre>
 *
 * <h4>Resizable Range</h4>
 * An anchorpane's parent will resize the anchorpane within the anchorpane's resizable range
 * during layout.   By default the anchorpane computes this range based on its content
 * as outlined in the table below.
 * <p>
 * <table border="1">
 * <tr><td></td><th>width</th><th>height</th></tr>
 * <tr><th>minimum</th>
 * <td>left/right insets plus width required to display children anchored at left/right with at least their min widths</td>
 * <td>top/bottom insets plus height required to display children anchored at top/bottom with at least their min heights</td></tr>
 * <tr><th>preferred</th>
 * <td>left/right insets plus width required to display children anchored at left/right with at least their pref widths</td>
 * <td>top/bottom insets plus height required to display children anchored at top/bottom with at least their pref heights</td></tr>
 * <tr><th>maximum</th>
 * <td>Double.MAX_VALUE</td><td>Double.MAX_VALUE</td></tr>
 * </table>
 * <p>
 * An anchorpane's unbounded maximum width and height are an indication to the parent that
 * it may be resized beyond its preferred size to fill whatever space is assigned
 * to it.
 * <p>
 * AnchorPane provides properties for setting the size range directly.  These
 * properties default to the sentinel value Region.USE_COMPUTED_SIZE, however the
 * application may set them to other values as needed:
 * <pre><code>     <b>anchorpane.setPrefSize(300, 300);</b>
 * </code></pre>
 * Applications may restore the computed values by setting these properties back
 * to Region.USE_COMPUTED_SIZE.
 * <p>
 * AnchorPane does not clip its content by default, so it is possible that childrens'
 * bounds may extend outside its own bounds if the anchorpane is resized smaller
 * than its preferred size.</p>
 *
 */
public class AnchorPane extends Pane {

    private static final String TOP_ANCHOR = "pane-top-anchor";
    private static final String LEFT_ANCHOR = "pane-left-anchor";
    private static final String BOTTOM_ANCHOR = "pane-bottom-anchor";
    private static final String RIGHT_ANCHOR = "pane-right-anchor";

    /********************************************************************
     *  BEGIN static methods
     ********************************************************************/

    /**
     * Sets the top anchor for the child when contained by an anchorpane.
     * If set, the anchorpane will maintain the child's size and position so
     * that it's top is always offset by that amount from the anchorpane's top
     * content edge.
     * Setting the value to null will remove the constraint.
     * @param child the child node of an anchorpane
     * @param value the offset from the top of the anchorpane
     */
    public static void setTopAnchor(Node child, Double value) {
        setConstraint(child, TOP_ANCHOR, value);
    }

    /**
     * Returns the child's top anchor constraint if set.
     * @param child the child node of an anchorpane
     * @return the offset from the top of the anchorpane or null if no top anchor was set
     */
    public static Double getTopAnchor(Node child) {
        return (Double)getConstraint(child, TOP_ANCHOR);
    }

    /**
     * Sets the left anchor for the child when contained by an anchorpane.
     * If set, the anchorpane will maintain the child's size and position so
     * that it's left is always offset by that amount from the anchorpane's left
     * content edge.
     * Setting the value to null will remove the constraint.
     * @param child the child node of an anchorpane
     * @param value the offset from the left of the anchorpane
     */
    public static void setLeftAnchor(Node child, Double value) {
        setConstraint(child, LEFT_ANCHOR, value);
    }

    /**
     * Returns the child's left anchor constraint if set.
     * @param child the child node of an anchorpane
     * @return the offset from the left of the anchorpane or null if no left anchor was set
     */
    public static Double getLeftAnchor(Node child) {
        return (Double)getConstraint(child, LEFT_ANCHOR);
    }

    /**
     * Sets the bottom anchor for the child when contained by an anchorpane.
     * If set, the anchorpane will maintain the child's size and position so
     * that it's bottom is always offset by that amount from the anchorpane's bottom
     * content edge.
     * Setting the value to null will remove the constraint.
     * @param child the child node of an anchorpane
     * @param value the offset from the bottom of the anchorpane
     */
    public static void setBottomAnchor(Node child, Double value) {
        setConstraint(child, BOTTOM_ANCHOR, value);
    }

    /**
     * Returns the child's bottom anchor constraint if set.
     * @param child the child node of an anchorpane
     * @return the offset from the bottom of the anchorpane or null if no bottom anchor was set
     */
    public static Double getBottomAnchor(Node child) {
        return (Double)getConstraint(child, BOTTOM_ANCHOR);
    }

    /**
     * Sets the bottom anchor for the child when contained by an anchorpane.
     * If set, the anchorpane will maintain the child's size and position so
     * that it's right is always offset by that amount from the anchorpane's right
     * content edge.
     * Setting the value to null will remove the constraint.
     * @param child the child node of an anchorpane
     * @param value the offset from the right of the anchorpane
     */
    public static void setRightAnchor(Node child, Double value) {
        setConstraint(child, RIGHT_ANCHOR, value);
    }

    /**
     * Returns the child's right anchor constraint if set.
     * @param child the child node of an anchorpane
     * @return the offset from the right of the anchorpane or null if no right anchor was set
     */
    public static Double getRightAnchor(Node child) {
        return (Double)getConstraint(child, RIGHT_ANCHOR);
    }

    /**
     * Removes all anchorpane constraints from the child node.
     * @param child the child node
     */
    public static void clearConstraints(Node child) {
        setTopAnchor(child, null);
        setRightAnchor(child, null);
        setBottomAnchor(child, null);
        setLeftAnchor(child, null);
    }
    
    /********************************************************************
     *  END static methods 
     ********************************************************************/

    /**
     * Creates an AnchorPane layout.
     */
    public AnchorPane() {
        super();
    }
    
    @Override protected double computeMinWidth(double height) {
        return computeWidth(true, height);
    }

    @Override protected double computeMinHeight(double width) {
        return computeHeight(true, width);
    }

    @Override protected double computePrefWidth(double height) {
        return computeWidth(false, height);
    }

    @Override protected double computePrefHeight(double width) {
        return computeHeight(false, width);
    }

    private double computeWidth(boolean minimum, double height) {
        double max = 0;
        for (int i = 0; i < getChildren().size(); i++) {
            Node child = getChildren().get(i);
            if (child.isManaged()) {
                Double leftAnchor = getLeftAnchor(child);
                Double rightAnchor = getRightAnchor(child);

                double left = leftAnchor != null? leftAnchor :
                    (rightAnchor != null? 0 : child.getLayoutBounds().getMinX() + child.getLayoutX());
                double right = rightAnchor != null? rightAnchor : 0;
                if (child.getContentBias() == Orientation.VERTICAL) {
                    height = (minimum? child.minHeight(-1) : child.prefHeight(-1));
                }
                max = Math.max(max, left + (minimum? child.minWidth(height) : child.prefWidth(height)) + right);
            }
        }
        return getInsets().getLeft() + max + getInsets().getRight();
    }

    private double computeHeight(boolean minimum, double width) {
        double max = 0;
        for (int i = 0; i < getChildren().size(); i++) {
            Node child = getChildren().get(i);
            if (child.isManaged()) {
                Double topAnchor = getTopAnchor(child);
                Double bottomAnchor = getBottomAnchor(child);

                double top = topAnchor != null? topAnchor :
                    (bottomAnchor != null? 0 : child.getLayoutBounds().getMinY() + child.getLayoutY());
                double bottom = bottomAnchor != null? bottomAnchor : 0;                
                if (child.getContentBias() == Orientation.HORIZONTAL) {
                    width = (minimum? child.minWidth(-1) : child.prefWidth(-1));
                }                  
                max = Math.max(max, top + (minimum? child.minHeight(width) : child.prefHeight(width)) + bottom);
            }
        }
        return getInsets().getTop() + max + getInsets().getBottom();
    }

    private double computeChildWidth(Node child, Double leftAnchor, Double rightAnchor, double height) {
        if (leftAnchor != null && rightAnchor != null && child.isResizable()) {
             return getWidth() - getInsets().getLeft() - getInsets().getRight() - leftAnchor - rightAnchor;
        }
        return computeChildPrefAreaWidth(child, Insets.EMPTY, height);
    }

    private double computeChildHeight(Node child, Double topAnchor, Double bottomAnchor, double width) {
        if (topAnchor != null && bottomAnchor != null && child.isResizable()) {
             return getHeight() - getInsets().getTop() - getInsets().getBottom() - topAnchor - bottomAnchor;
        }
        return computeChildPrefAreaHeight(child, Insets.EMPTY, width);
    }

    @Override protected void layoutChildren() {
        Insets insets = getInsets();
        
        for (int i = 0; i < getChildren().size(); i++) {
            Node child = getChildren().get(i);
            if (child.isManaged()) {
                Double topAnchor = getTopAnchor(child);
                Double bottomAnchor = getBottomAnchor(child);
                Double leftAnchor = getLeftAnchor(child);
                Double rightAnchor = getRightAnchor(child);

                double x = child.getLayoutX() + child.getLayoutBounds().getMinX();
                double y = child.getLayoutY() + child.getLayoutBounds().getMinY();

                double w = -1;
                double h = -1;
                Orientation bias = child.getContentBias();
                if (bias == Orientation.VERTICAL) {
                    // width depends on height
                    h = computeChildHeight(child, topAnchor, bottomAnchor, -1);
                    w = computeChildWidth(child, leftAnchor, rightAnchor, h);

                } else if (bias == Orientation.HORIZONTAL) {
                    // height depends on width
                    w = computeChildWidth(child, leftAnchor, rightAnchor, -1);
                    h = computeChildHeight(child, topAnchor, bottomAnchor, w);
                } else {                    
                    w = computeChildWidth(child, leftAnchor, rightAnchor, -1);
                    h = computeChildHeight(child, topAnchor, bottomAnchor, -1);
                }

                if (leftAnchor != null) {
                    x = insets.getLeft() + leftAnchor;

                } else if (rightAnchor != null) {                   
                    x = getWidth() - insets.getRight() - rightAnchor - w;
                }
                if (topAnchor != null) {
                    y = insets.getTop() + topAnchor;

                } else if (bottomAnchor != null) {
                    y = getHeight() - insets.getBottom() - bottomAnchor - h;
                }
                child.resizeRelocate(x, y, w, h);
            }
        }
    }
}
