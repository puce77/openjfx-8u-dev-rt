/*
 * Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved.
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

package javafx.scene.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.sun.javafx.css.*;
import com.sun.javafx.css.converters.*;

/**
 * <p>
 * A MenuBar control traditionally is placed at the very top of the user
 * interface, and embedded within it are {@link Menu Menus}. To add a menu to
 * a menubar, you add it to the {@link #getMenus() menus} ObservableList.
 * By default, for each menu added to the menubar, it will be
 * represented as a button with the Menu {@link MenuItem#textProperty() text} value displayed.
 * <p>
 * MenuBar sets focusTraversable to false.
 * </p>
 *
 * To create and populate a {@code MenuBar}, you may do what is shown below.
 * Please refer to the {@link Menu} API page for more information on how to
 * configure it.
 * <pre><code>
 * final Menu menu1 = new Menu("File");
 * final Menu menu2 = new Menu("Options");
 * final Menu menu3 = new Menu("Help");
 * 
 * MenuBar menuBar = new MenuBar();
 * menuBar.getMenus().addAll(menu1, menu2, menu3);
 * </code></pre>
 *
 * @see Menu
 * @see MenuItem
 */
@DefaultProperty("menus")
public class MenuBar extends Control {

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public MenuBar() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        setFocusTraversable(false);
    }



    /***************************************************************************
     *                                                                         *
     * Instance variables                                                      *
     *                                                                         *
     **************************************************************************/
    private ObservableList<Menu> menus = FXCollections.<Menu>observableArrayList();


    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Use the system menu bar if the current platform supports it.
     */
    public final BooleanProperty useSystemMenuBarProperty() {
        if (useSystemMenuBar == null) {
            useSystemMenuBar = new StyleableBooleanProperty() {

                @Override
                public StyleableProperty getStyleableProperty() {
                    return StyleableProperties.USE_SYSTEM_MENU_BAR;
                }

                @Override
                public Object getBean() {
                    return MenuBar.this;
                }

                @Override
                public String getName() {
                    return "useSystemMenuBar";
                }
            };
        }
        return useSystemMenuBar;
    }
    private BooleanProperty useSystemMenuBar;
    public final void setUseSystemMenuBar(boolean value) {
        useSystemMenuBarProperty().setValue(value);
    }
    public final boolean isUseSystemMenuBar() {
        return useSystemMenuBar == null ? false : useSystemMenuBar.getValue();
    }


    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * The menus to show within this MenuBar. If this ObservableList is modified at
     * runtime, the MenuBar will update as expected.
     * @see Menu
     */
    public final ObservableList<Menu> getMenus() {
        return menus;
    }


    
    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    private static final String DEFAULT_STYLE_CLASS = "menu-bar";

    private static class StyleableProperties {
        private static final StyleableProperty<MenuBar, Boolean> USE_SYSTEM_MENU_BAR =
                new StyleableProperty<MenuBar, Boolean>("-fx-use-system-menu-bar",
                                                        BooleanConverter.getInstance(),
                                                        false) {
            @Override public boolean isSettable(MenuBar n) {
                return n.useSystemMenuBar == null || !n.useSystemMenuBar.isBound();
            }

            @Override public WritableValue<Boolean> getWritableValue(MenuBar n) {
                return n.useSystemMenuBarProperty();
            }
        };

        private static final List<StyleableProperty> STYLEABLES;
        static {
            final List<StyleableProperty> styleables =
                new ArrayList<StyleableProperty>(Control.impl_CSS_STYLEABLES());
            Collections.addAll(styleables,
                USE_SYSTEM_MENU_BAR
            );
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    /**
     * @treatAsPrivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public static List<StyleableProperty> impl_CSS_STYLEABLES() {
        return MenuBar.StyleableProperties.STYLEABLES;
    }

    /**
     * RT-19263
     * @treatAsPrivate implementation detail
     * @deprecated This is an experimental API that is not intended for general use and is subject to change in future versions
     */
    @Deprecated
    public List<StyleableProperty> impl_getStyleableProperties() {
        return impl_CSS_STYLEABLES();
    }

}

