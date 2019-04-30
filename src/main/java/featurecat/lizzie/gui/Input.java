package featurecat.lizzie.gui;

import static java.awt.event.KeyEvent.*;

import featurecat.lizzie.Lizzie;
import java.awt.event.*;
import javax.swing.JOptionPane;

public class Input implements MouseListener, KeyListener, MouseWheelListener, MouseMotionListener {
  public static boolean isinsertmode = false;
  public static boolean Draggedmode = false;

  @Override
  public void mouseClicked(MouseEvent e) {}

  @Override
  public void mousePressed(MouseEvent e) {

    if (e.getButton() == MouseEvent.BUTTON1) // left click
    {
      if (Draggedmode) {
        Lizzie.frame.DraggedPress(e.getX(), e.getY());
        return;
      }
      if (!isinsertmode) {
        if (e.getClickCount() == 2) { // TODO: Maybe need to delay check
          Lizzie.frame.onDoubleClicked(e.getX(), e.getY());
        } else {
          Lizzie.frame.onClicked(e.getX(), e.getY());
        }
      } else {

        Lizzie.frame.insertMove(e.getX(), e.getY());
      }

    } else if (e.getButton() == MouseEvent.BUTTON3) // right click
      // undo();
      Lizzie.frame.openRightClickMenu(e.getX(), e.getY());

    //  Lizzie.frame.onRightClicked(e.getX(), e.getY());
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (Draggedmode) {
      Lizzie.frame.DraggedReleased(e.getX(), e.getY());
      return;
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  @Override
  public void mouseDragged(MouseEvent e) {
    if (Draggedmode) {
      Lizzie.frame.DraggedDragged(e.getX(), e.getY());
      return;
    }
    Lizzie.frame.onMouseDragged(e.getX(), e.getY());
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    //	  if(Draggedmode)
    //	  {
    //		  Lizzie.frame.DraggedMoved(e.getX(), e.getY());
    //		  return;
    //	  }
    Lizzie.frame.onMouseMoved(e.getX(), e.getY());
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  public static void undo() {
    undo(1);
  }

  public static void undo(int movesToAdvance) {
    if (Lizzie.board.inAnalysisMode()) Lizzie.board.toggleAnalysis();
    if (Lizzie.frame.isPlayingAgainstLeelaz) {
      Lizzie.frame.isPlayingAgainstLeelaz = false;
    }
    if (Lizzie.frame.incrementDisplayedBranchLength(-movesToAdvance)) {
      return;
    }

    for (int i = 0; i < movesToAdvance; i++) Lizzie.board.previousMove();
  }

  private void undoToChildOfPreviousWithVariation() {
    // Undo until the position just after the junction position.
    // If we are already on such a position, we go to
    // the junction position for convenience.
    // Use cases:
    // [Delete branch] Call this function and then deleteMove.
    // [Go to junction] Call this function twice.
    if (!Lizzie.board.undoToChildOfPreviousWithVariation()) Lizzie.board.previousMove();
  }

  private void undoToFirstParentWithVariations() {
    if (Lizzie.board.undoToChildOfPreviousWithVariation()) {
      Lizzie.board.previousMove();
    }
  }

  private void goCommentNode(boolean moveForward) {
    if (moveForward) {
      redo(Lizzie.board.getHistory().getCurrentHistoryNode().goToNextNodeWithComment());
    } else {
      undo(Lizzie.board.getHistory().getCurrentHistoryNode().goToPreviousNodeWithComment());
    }
  }

  private void redo() {
    redo(1);
  }

  private void redo(int movesToAdvance) {
    if (Lizzie.board.inAnalysisMode()) Lizzie.board.toggleAnalysis();
    if (Lizzie.frame.isPlayingAgainstLeelaz) {
      Lizzie.frame.isPlayingAgainstLeelaz = false;
    }
    if (Lizzie.frame.incrementDisplayedBranchLength(movesToAdvance)) {
      return;
    }

    for (int i = 0; i < movesToAdvance; i++) Lizzie.board.nextMove();
  }

  private void startTemporaryBoard() {
    if (Lizzie.config.showBestMoves) {
      startRawBoard();
    } else {
      Lizzie.config.showBestMovesTemporarily = true;
    }
  }

  private void startRawBoard() {
    if (!Lizzie.config.showRawBoard) {
      Lizzie.frame.startRawBoard();
    }
    Lizzie.config.showRawBoard = true;
  }

  private void stopRawBoard() {
    Lizzie.frame.stopRawBoard();
    Lizzie.config.showRawBoard = false;
  }

  private void stopTemporaryBoard() {
    stopRawBoard();
    Lizzie.config.showBestMovesTemporarily = false;
  }

  private void toggleHints() {
    Lizzie.config.toggleShowBranch();
    Lizzie.config.showSubBoard =
        Lizzie.config.showNextMoves = Lizzie.config.showBestMoves = Lizzie.config.showBranch;
  }

  private void nextBranch() {
    if (Lizzie.frame.isPlayingAgainstLeelaz) {
      Lizzie.frame.isPlayingAgainstLeelaz = false;
    }
    Lizzie.board.nextBranch();
  }

  private void previousBranch() {
    if (Lizzie.frame.isPlayingAgainstLeelaz) {
      Lizzie.frame.isPlayingAgainstLeelaz = false;
    }
    Lizzie.board.previousBranch();
  }

  private void moveBranchUp() {
    Lizzie.board.moveBranchUp();
  }

  private void moveBranchDown() {
    Lizzie.board.moveBranchDown();
  }

  private void deleteMove() {
    Lizzie.board.deleteMove();
  }

  private void deleteBranch() {
    Lizzie.board.deleteBranch();
  }

  private boolean controlIsPressed(KeyEvent e) {
    boolean mac = System.getProperty("os.name", "").toUpperCase().startsWith("MAC");
    return e.isControlDown() || (mac && e.isMetaDown());
  }

  private void toggleShowDynamicKomi() {
    Lizzie.config.showDynamicKomi = !Lizzie.config.showDynamicKomi;
  }

  @Override
  public void keyPressed(KeyEvent e) {
    // If any controls key is pressed, let's disable analysis mode.
    // This is probably the user attempting to exit analysis mode.
    boolean shouldDisableAnalysis = true;

    switch (e.getKeyCode()) {
      case VK_E:
        Lizzie.frame.toggleGtpConsole();
        break;
      case VK_RIGHT:
        if (isinsertmode) {
          JOptionPane.showMessageDialog(null, "请先退出插入棋子模式,或使用右键菜单落子");
          return;
        }
        if (e.isShiftDown()) {
          moveBranchDown();
        } else {

          nextBranch();
        }
        break;

      case VK_LEFT:
        if (isinsertmode) {
          JOptionPane.showMessageDialog(null, "请先退出插入棋子模式,或使用右键菜单落子");
          return;
        }
        if (e.isShiftDown()) {
          moveBranchUp();
        } else if (controlIsPressed(e)) {
          undoToFirstParentWithVariations();
        } else {

          previousBranch();
        }
        break;

      case VK_UP:
        if (isinsertmode) {
          JOptionPane.showMessageDialog(null, "请先退出插入棋子模式,或使用右键菜单落子");
          return;
        }
        if (controlIsPressed(e) && e.isShiftDown()) {
          goCommentNode(false);
        } else if (e.isShiftDown()) {
          undoToChildOfPreviousWithVariation();
        } else if (controlIsPressed(e)) {

          undo(10);
        } else {

          undo();
        }
        break;

      case VK_PAGE_DOWN:
        if (controlIsPressed(e) && e.isShiftDown()) {
          Lizzie.frame.increaseMaxAlpha(-5);
        } else {
          if (isinsertmode) {
            JOptionPane.showMessageDialog(null, "请先退出插入棋子模式,或使用右键菜单落子");
            return;
          }
          redo(10);
        }
        break;

      case VK_DOWN:
        if (isinsertmode) {
          JOptionPane.showMessageDialog(null, "请先退出插入棋子模式,或使用右键菜单落子");
          return;
        }
        if (controlIsPressed(e) && e.isShiftDown()) {
          goCommentNode(true);
        } else if (controlIsPressed(e)) {
          redo(10);
        } else {
          redo();
        }
        break;

      case VK_N:
        // stop the ponder
        if (isinsertmode) {
          JOptionPane.showMessageDialog(null, "请先退出插入棋子模式,或使用右键菜单落子");
          return;
        }
        if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
        LizzieFrame.startNewGame();
        break;
      case VK_SPACE:
        if (Lizzie.frame.isPlayingAgainstLeelaz) {
          Lizzie.frame.isPlayingAgainstLeelaz = false;
          Lizzie.leelaz.isThinking = false;
        }
        Lizzie.leelaz.togglePonder();
        break;

      case VK_L:
        Lizzie.config.toggleShowLcbWinrate();
        break;

      case VK_P:
        Lizzie.board.pass();
        break;

      case VK_COMMA:
        if (!Lizzie.frame.playCurrentVariation()) Lizzie.frame.playBestMove();
        break;

      case VK_M:
        if (e.isAltDown()) {
          if (isinsertmode) {
            JOptionPane.showMessageDialog(null, "请先退出插入棋子模式,或使用右键菜单落子");
            return;
          }
          Lizzie.frame.openChangeMoveDialog();
        } else {
          Lizzie.config.toggleShowMoveNumber();
        }
        break;

      case VK_F:
        Lizzie.config.toggleShowNextMoves();
        break;

      case VK_H:
        Lizzie.config.toggleHandicapInsteadOfWinrate();
        break;

      case VK_PAGE_UP:
        if (controlIsPressed(e) && e.isShiftDown()) {
          Lizzie.frame.increaseMaxAlpha(5);
        } else {
          if (isinsertmode) {
            JOptionPane.showMessageDialog(null, "请先退出插入棋子模式,或使用右键菜单落子");
            return;
          }
          undo(10);
        }
        break;

      case VK_I:
        // stop the ponder
        if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
        Lizzie.frame.editGameInfo();
        break;
      case VK_S:
        // stop the ponder
        if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
        LizzieFrame.saveFile();
        break;

      case VK_O:
        if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
        LizzieFrame.openFile();
        break;

      case VK_V:
        if (controlIsPressed(e)) {
          if (isinsertmode) {
            JOptionPane.showMessageDialog(null, "请先退出插入棋子模式,或使用右键菜单落子");
            return;
          }
          Lizzie.frame.pasteSgf();
        } else {
          //    Lizzie.config.toggleLargeSubBoard();
          Lizzie.config.toggleShowBranch();
        }
        break;

      case VK_HOME:
        if (isinsertmode) {
          JOptionPane.showMessageDialog(null, "请先退出插入棋子模式,或使用右键菜单落子");
          return;
        }
        if (controlIsPressed(e)) {
          Lizzie.board.clear();
        } else {
          while (Lizzie.board.previousMove()) ;
        }
        break;

      case VK_END:
        if (isinsertmode) {
          JOptionPane.showMessageDialog(null, "请先退出插入棋子模式,或使用右键菜单落子");
          return;
        }
        while (Lizzie.board.nextMove()) ;
        break;

      case VK_X:
        if (controlIsPressed(e)) {
          Lizzie.frame.openConfigDialog();
        } else {
          if (!Lizzie.frame.showControls) {
            if (Lizzie.leelaz.isPondering()) {
              wasPonderingWhenControlsShown = true;
              Lizzie.leelaz.togglePonder();
            } else {
              wasPonderingWhenControlsShown = false;
            }
            Lizzie.frame.drawControls();
          }
          Lizzie.frame.showControls = true;
        }
        break;

      case VK_W:
        if (controlIsPressed(e)) {
          Lizzie.config.toggleLargeWinrate();
        } else {
          Lizzie.config.toggleShowWinrate();
        }
        break;

      case VK_G:
        Lizzie.config.toggleShowVariationGraph();
        break;

      case VK_T:
        if (controlIsPressed(e)) {
          Lizzie.config.toggleShowCommentNodeColor();
        } else {
          Lizzie.config.toggleShowComment();
        }
        break;

      case VK_Y:
        Lizzie.config.toggleNodeColorMode();
        break;

      case VK_C:
        if (controlIsPressed(e)) {
          Lizzie.frame.copySgf();
        } else {
          Lizzie.config.toggleCoordinates();
        }
        break;

      case VK_ENTER:
        if (isinsertmode) {
          JOptionPane.showMessageDialog(null, "请先退出插入棋子模式,或使用右键菜单落子");
          return;
        }
        if (!Lizzie.leelaz.isThinking) {
          Lizzie.leelaz.sendCommand(
              "time_settings 0 "
                  + Lizzie.config
                      .config
                      .getJSONObject("leelaz")
                      .getInt("max-game-thinking-time-seconds")
                  + " 1");
          Lizzie.frame.playerIsBlack = !Lizzie.board.getData().blackToPlay;
          Lizzie.frame.isPlayingAgainstLeelaz = true;
          Lizzie.leelaz.genmove((Lizzie.board.getData().blackToPlay ? "B" : "W"));
        }
        break;

      
      case VK_DELETE:    	  
      case VK_BACK_SPACE:
        if (isinsertmode) {
          JOptionPane.showMessageDialog(null, "请先退出插入棋子模式,或使用右键菜单落子");
          return;
        }
        if(e.isAltDown())
        {
            Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
            JOptionPane.showMessageDialog(null, "已清空所有Lizzie推荐点缓存");
        }
        else if (e.isShiftDown()) {
          deleteBranch();
        } else {
          deleteMove();
        }        
        break;

      case VK_Z:
        if (e.isShiftDown()) {
          toggleHints();
        } else {
          startTemporaryBoard();
        }
        break;

      case VK_A:
        if (e.isAltDown() || e.isControlDown()) {
          Lizzie.frame.openAvoidMoveDialog();
        } else {
          shouldDisableAnalysis = false;
          Lizzie.board.toggleAnalysis();
        }
        break;
        // this is copyed from https://github.com/zsalch/lizzie/tree/n_avoiddialog

      case VK_PERIOD:
        if (!Lizzie.board.getHistory().getNext().isPresent()) {
          Lizzie.board.setScoreMode(!Lizzie.board.inScoreMode());
        }
        break;

      case VK_D:
        toggleShowDynamicKomi();
        break;

      case VK_R:
        if (isinsertmode) {
          JOptionPane.showMessageDialog(null, "请先退出插入棋子模式,或使用右键菜单落子");
          return;
        }
        Lizzie.frame.replayBranch();
        break;

      case VK_OPEN_BRACKET:
        if (Lizzie.frame.BoardPositionProportion > 0) Lizzie.frame.BoardPositionProportion--;
        break;

      case VK_CLOSE_BRACKET:
        if (Lizzie.frame.BoardPositionProportion < 8) Lizzie.frame.BoardPositionProportion++;
        break;

      case VK_K:
        Lizzie.config.toggleEvaluationColoring();
        break;

        // Use Ctrl+Num to switching multiple engine

      case VK_1:
      case VK_2:
      case VK_3:
      case VK_4:
      case VK_5:
      case VK_6:
      case VK_7:
      case VK_8:
      case VK_9:
        if (controlIsPressed(e)) {
          Lizzie.switchEngine(e.getKeyCode() - VK_1);
        }
        break;
      case 0:
        if (controlIsPressed(e)) {
          Lizzie.switchEngine(9);
        }
        break;

      default:
        shouldDisableAnalysis = false;
    }

    if (shouldDisableAnalysis && Lizzie.board.inAnalysisMode()) Lizzie.board.toggleAnalysis();

    Lizzie.frame.repaint();
  }

  private boolean wasPonderingWhenControlsShown = false;

  @Override
  public void keyReleased(KeyEvent e) {
    switch (e.getKeyCode()) {
      case VK_X:
        if (wasPonderingWhenControlsShown) Lizzie.leelaz.togglePonder();
        Lizzie.frame.showControls = false;
        Lizzie.frame.repaint();
        break;

      case VK_Z:
        stopTemporaryBoard();
        Lizzie.frame.repaint();
        break;

      default:
    }
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    if (isinsertmode) {
      JOptionPane.showMessageDialog(null, "请先退出插入棋子模式,或使用右键菜单落子");
      return;
    }
    if (Lizzie.frame.processCommentMouseWheelMoved(e)) {
      return;
    }
    if (Lizzie.board.inAnalysisMode()) Lizzie.board.toggleAnalysis();
    if (e.getWheelRotation() > 0) {
      redo();
    } else if (e.getWheelRotation() < 0) {
      undo();
    }
    Lizzie.frame.repaint();
  }
}
