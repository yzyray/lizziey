package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.Insets;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class ToolbarPositionConfig extends JDialog {

  public ToolbarPositionConfig() {
    setType(Type.POPUP);
    setTitle("显示顺序设置");
    setBounds(0, 0, 150, 140);
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    setLayout(null);
    setLocationRelativeTo(getOwner());
    JComboBox anaPanel = new JComboBox();
    JComboBox autoPlayPanel = new JComboBox();
    JComboBox enginePkPanel = new JComboBox();
    anaPanel.addItem("1");
    anaPanel.addItem("2");
    anaPanel.addItem("3");
    autoPlayPanel.addItem("1");
    autoPlayPanel.addItem("2");
    autoPlayPanel.addItem("3");
    enginePkPanel.addItem("1");
    enginePkPanel.addItem("2");
    enginePkPanel.addItem("3");
    JLabel lblanaPanel = new JLabel("自动分析面板:");
    JLabel lblautoPlayPanel = new JLabel("自动落子面板:");
    JLabel lblenginePkPanel = new JLabel("引擎对战面板:");
    add(lblanaPanel);
    add(lblautoPlayPanel);
    add(lblenginePkPanel);
    add(anaPanel);
    add(autoPlayPanel);
    add(enginePkPanel);
    lblanaPanel.setBounds(5, 3, 80, 25);
    anaPanel.setBounds(85, 7, 40, 18);

    lblenginePkPanel.setBounds(5, 23, 80, 25);
    enginePkPanel.setBounds(85, 27, 40, 18);

    lblautoPlayPanel.setBounds(5, 43, 80, 25);
    autoPlayPanel.setBounds(85, 47, 40, 18);
    JButton okButton = new JButton("确认");
    JButton cancelButton = new JButton("取消");
    okButton.setMargin(new Insets(0, 0, 0, 0));
    cancelButton.setMargin(new Insets(0, 0, 0, 0));
    okButton.setBounds(20, 73, 40, 25);
    cancelButton.setBounds(75, 73, 40, 25);
    add(okButton);
    add(cancelButton);
    anaPanel.setSelectedIndex(Lizzie.frame.toolbar.anaPanelOrder);
    enginePkPanel.setSelectedIndex(Lizzie.frame.toolbar.enginePkOrder);
    autoPlayPanel.setSelectedIndex(Lizzie.frame.toolbar.autoPlayOrder);
    okButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toolbar.anaPanelOrder = anaPanel.getSelectedIndex();
            Lizzie.frame.toolbar.enginePkOrder = enginePkPanel.getSelectedIndex();
            Lizzie.frame.toolbar.autoPlayOrder = autoPlayPanel.getSelectedIndex();
            Lizzie.frame.toolbar.setOrder();
            setVisible(false);
          }
        });
    cancelButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
  }
}
