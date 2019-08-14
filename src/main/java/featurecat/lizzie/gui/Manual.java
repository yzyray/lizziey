package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class Manual extends JDialog {
  boolean isMannul = false;

  public Manual() {
    setType(Type.POPUP);
    setTitle("人工干预");
    setAlwaysOnTop(true);
    setBounds(0, 0, 155, 175);
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    buttonPane.setLayout(null);
    JButton playNow = new JButton("立即落子");
    playNow.setBounds(10, 5, 120, 29);

    playNow.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.board.getHistory().isBlacksTurn())
              Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).playNow = true;
            else
              Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).playNow = true;
          }
        });

    buttonPane.add(playNow);
    JButton manualOne = new JButton("允许人工落子");
    manualOne.setBounds(10, 35, 120, 29);
    manualOne.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (!isMannul) {
              Lizzie.frame.addInput();
              manualOne.setText("关闭人工落子");
            } else {
              Lizzie.frame.removeInput();
              manualOne.setText("允许人工落子");
            }
            isMannul = !isMannul;
          }
        });
    buttonPane.add(manualOne);

    JButton blackResign = new JButton("黑认输");
    blackResign.setBounds(10, 65, 120, 29);
    blackResign.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).resigned = true;
            Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineBlack).isManualB = true;
          }
        });
    buttonPane.add(blackResign);

    JButton whiteResign = new JButton("白认输");
    whiteResign.setBounds(10, 95, 120, 29);
    whiteResign.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).resigned = true;
            Lizzie.engineManager.engineList.get(Lizzie.frame.toolbar.engineWhite).isManualW = true;
          }
        });
    buttonPane.add(whiteResign);

    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    setLocationRelativeTo(getOwner());
  }
}
