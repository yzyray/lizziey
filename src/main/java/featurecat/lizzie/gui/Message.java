package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class Message extends JDialog {
  JLabel lblmessage;

  public Message() {
    this.setModal(true);
    setType(Type.POPUP);
    setTitle("消息提醒");
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());

    lblmessage = new JLabel("", JLabel.CENTER);
    this.add(lblmessage);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setMessage(String message) {
    lblmessage.setText(message);
    setBounds(0, 0, message.length() * 15, 80);
    setLocationRelativeTo(getOwner());
  }
}
