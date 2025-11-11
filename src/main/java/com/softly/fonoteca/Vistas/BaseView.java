package com.softly.fonoteca.Vistas;

import javax.swing.*;

public interface BaseView {
    void pack();
    void setVisible(boolean visible);
    void dispose();
    void setDefaultCloseOperation(int operation);
}
