package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class Manual extends JDialog {

  public Manual() {
    setType(Type.POPUP);
    setTitle("人工干预");
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    setBounds(0, 0, 150, 150);
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    buttonPane.setLayout(null);
    JButton playNow = new JButton("立即落子");
    playNow.setBounds(20, 20, 100, 29);

    playNow.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.board.getHistory().isBlacksTurn())
              Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playNow = true;
            else
              Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playNow = true;
          }
        });
    JButton manualOne = new JButton("允许人工落子");
    manualOne.setBounds(10, 60, 120, 29);

    manualOne.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.addInput();
          }
        });

    buttonPane.add(manualOne);
    buttonPane.add(playNow);

    setLocationRelativeTo(getOwner());
  }
}
