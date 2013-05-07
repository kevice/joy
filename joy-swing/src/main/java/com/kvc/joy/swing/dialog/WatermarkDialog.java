package com.kvc.joy.swing.dialog;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.kvc.joy.swing.AuxiliaryDrawLabel;
import com.kvc.joy.swing.DocWartermarkStatus;
import com.kvc.joy.swing.panel.BASPanel;
import com.kvc.joy.swing.table.XTable;

/**
 * 基本的模式框
 * 可绘制水印
 * @author ckcs
 */
public class WatermarkDialog
        extends JDialog
        implements ComponentListener {

    //辅助绘制层
    private AuxiliaryDrawLabel auxiliaryDrawPane;
    private BASPanel contentPanel;

    /** Creates new form WatermarkDialog */
    public WatermarkDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
        initDialog();
    }

    public WatermarkDialog(Window owner, String title, ModalityType modalityType) {
        super(owner, title, modalityType);
        initDialog();
    }

    public WatermarkDialog(Window owner, String title) {
        super(owner, title);
        initDialog();
    }

    public WatermarkDialog(Window owner, ModalityType modalityType) {
        super(owner, modalityType);
        initDialog();
    }

    public WatermarkDialog(Window owner) {
        super(owner);
        initDialog();
    }

    public WatermarkDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        initDialog();
    }

    public WatermarkDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        initDialog();
    }

    public WatermarkDialog(Dialog owner, String title) {
        super(owner, title);
        initDialog();
    }

    public WatermarkDialog(Dialog owner, boolean modal) {
        super(owner, modal);
        initDialog();
    }

    public WatermarkDialog(Dialog owner) {
        super(owner);
        initDialog();
    }

    public WatermarkDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        initDialog();
    }

    public WatermarkDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        initDialog();
    }

    public WatermarkDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initDialog();
    }

    public WatermarkDialog(Frame owner, String title) {
        super(owner, title);
        initDialog();
    }

    public WatermarkDialog(Frame owner) {
        super(owner);
        initDialog();
    }

    public WatermarkDialog() {
        initDialog();
    }

    //初始化自己
    private void initDialog() {
        initComponents();
        initAuxiliaryLayer();
        addComponentListener(this);
    }

   //设置辅助绘制面板的位置
    private void setAuxiliaryDrawPaneBound() {
        if (auxiliaryDrawPane != null) {
            Point local = null;
            if (isShowing()) {
                local = getContentPane().getLocationOnScreen();
            } else {
                local = getContentPane().getLocation();
            }
            SwingUtilities.convertPointFromScreen(local, this);
            auxiliaryDrawPane.setBounds(local.x, local.y, getContentPane().getWidth(), getContentPane().getHeight());
        }
    }

     //这只辅助绘制
    private void initAuxiliaryLayer() {
        auxiliaryDrawPane = new AuxiliaryDrawLabel();
        getLayeredPane().add(auxiliaryDrawPane, JLayeredPane.POPUP_LAYER, 0);
    }

    @Override
    public void setVisible(boolean b) {
        if (b && contentPanel == null) {
            Component[] comps = getContentPane().getComponents();
            for (Component comp : comps) {
                if (comp instanceof BASPanel) {
                    contentPanel = (BASPanel) comp;
                    contentPanel.addPropertyChangeListener(new PropertyChangeListener() {

                        public void propertyChange(PropertyChangeEvent evt) {
                            if (evt.getPropertyName().equals("docStatusChange")) {
                                if (evt.getSource() == contentPanel) {
                                    DocWartermarkStatus status = (DocWartermarkStatus) evt.getNewValue();
                                    auxiliaryDrawPane.updatePanelDoc(status);
                                }
                            }
                        }
                    });
                    break;
                }
            }
        }
        super.setVisible(b);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void componentResized(ComponentEvent e) {
        setAuxiliaryDrawPaneBound();
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
        setAuxiliaryDrawPaneBound();
    }

    public void componentHidden(ComponentEvent e) {
    }

    public static void main(String[] args) {
        final WatermarkDialog dialog = new WatermarkDialog(new JFrame());
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 100);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        final BASPanel panel = new BASPanel();
        panel.setLayout(new BorderLayout());
        DefaultTableModel dm = new DefaultTableModel();
        dm.setDataVector(new Object[][]{{"119", "ooii", "foo", "bar", "ja", "ko", "zh", "ee"},
                    {"911", "ooii", "bar", "foo", "en", "fr", "pt", "dd"}},
                new Object[]{"SNo.", "hh", "11", "22", "Native", "33", "44", "55"});
        // 0      1    2      3       4    5
        JTable table = new XTable(dm);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(panel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
//        panel.setStatus(TicketConstant.APPROVED_STATUS, "dd");
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}