package featurecat.lizzie.gui;

import static java.awt.event.KeyEvent.*;

import featurecat.lizzie.Lizzie;
import java.awt.event.*;

public class InputSubboard implements MouseListener, MouseWheelListener {

  @Override
  public void mouseClicked(MouseEvent e) {}

  @Override
  public void mousePressed(MouseEvent e) {
    Lizzie.frame.processPressOnSub(e);
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {

    Lizzie.frame.processSubboardMouseWheelMoved(e);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseEntered(MouseEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseExited(MouseEvent e) {
    // TODO Auto-generated method stub

  }
}
