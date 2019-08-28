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

  //  @Override
  //  public void mouseWheelMoved(MouseWheelEvent e) {
  //
  //    Lizzie.frame.processSubboardMouseWheelMoved(e);
  //  }

  private long wheelWhen;

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    // if (isinsertmode) {
    // return;
    // }
    if (Lizzie.frame.processCommentMouseWheelMoved(e)) {
      return;
    }
    if (Lizzie.frame.processSubboardMouseWheelMoved(e)) {
      return;
    }

    if (e.getWhen() - wheelWhen > 0) {
      wheelWhen = e.getWhen();
      if (Lizzie.board.inAnalysisMode()) Lizzie.board.toggleAnalysis();
      if (e.getWheelRotation() > 0) {
        if (Lizzie.frame.boardRenderer.isShowingBranch()) {
          Lizzie.frame.doBranch(1);
        } else {
          Lizzie.frame.input.redo();
        }
      } else if (e.getWheelRotation() < 0) {
        if (Lizzie.frame.boardRenderer.isShowingBranch()) {
          Lizzie.frame.doBranch(-1);
        } else {
          Lizzie.frame.input.undo();
        }
      }
      Lizzie.frame.refresh();
    }
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
