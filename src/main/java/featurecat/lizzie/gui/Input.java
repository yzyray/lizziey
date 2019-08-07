package featurecat.lizzie.gui;

import static java.awt.event.KeyEvent.*;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.GameInfo;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.SwingUtilities;

public class Input implements MouseListener, KeyListener, MouseWheelListener, MouseMotionListener {
  // public static boolean isinsertmode = false;
  public static boolean Draggedmode = false;
  public static int insert = 0;
  public static boolean shouldDisableAnalysis = true;

  public boolean nowheelPress = false;

  @Override
  public void mouseClicked(MouseEvent e) {}

  @Override
  public void mousePressed(MouseEvent e) {
    if (Lizzie.frame.processPressOnSub(e)) {
      return;
    }
    if (Lizzie.frame.toolbar.isEnginePk) {
      if (e.getButton() == MouseEvent.BUTTON1) Lizzie.frame.onClickedForManul(e.getX(), e.getY());
      return;
    }
    if (SwingUtilities.isMiddleMouseButton(e)) {

      Lizzie.frame.replayBranchByWheel();
    }
    if (e.getButton() == MouseEvent.BUTTON1) // left click
    {
      if (e.getClickCount() == 2
          && !Lizzie.frame.isTrying
          && !Lizzie.frame.urlSgf
          && Lizzie.config.allowDrageDoubleClick) { // TODO: Maybe need to delay check
        Lizzie.frame.onDoubleClicked(e.getX(), e.getY());
      } else {
        if (insert == 0) {
          Lizzie.frame.onClicked(e.getX(), e.getY());
        } else if (insert == 1) {
          if (Lizzie.frame.iscoordsempty(e.getX(), e.getY())) {

            int[] coords = Lizzie.frame.convertmousexytocoords(e.getX(), e.getY());
            int currentmovenumber = Lizzie.board.getcurrentmovenumber();
            Lizzie.board.savelistforeditmode();

            Lizzie.board.editmovelistadd(
                Lizzie.board.tempallmovelist, currentmovenumber, coords[0], coords[1], true);
            Lizzie.board.clearforedit();
            Lizzie.board.setlist(Lizzie.board.tempallmovelist);
            Lizzie.board.goToMoveNumber(currentmovenumber + 1);
          }
        } else if (insert == 2) {

        }
      }

    } else if (e.getButton() == MouseEvent.BUTTON3) // right click
    {
      if (!Lizzie.frame.openRightClickMenu(e.getX(), e.getY())) undo();
    }

    Lizzie.frame.toolbar.setTxtUnfocuse();
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (Draggedmode
        && !Lizzie.frame.isTrying
        && !Lizzie.frame.urlSgf
        && Lizzie.config.allowDrageDoubleClick) {
      Lizzie.frame.DraggedReleased(e.getX(), e.getY());
      return;
    }
    if (SwingUtilities.isMiddleMouseButton(e)) {
      if (nowheelPress) {
        nowheelPress = false;
      } else {
        int moveNumber = Lizzie.board.getcurrentmovenumber();
        if (Lizzie.frame.playCurrentVariation2()) {
          Lizzie.board.goToMoveNumberBeyondBranch(moveNumber);
          if (Lizzie.engineManager.currentEngineNo >= 0) Lizzie.engineManager.isEmpty = false;
          if (Lizzie.board.getHistory().getCurrentHistoryNode().hasVariations()) {
            try {
              Lizzie.board.place(
                  Lizzie.board
                      .getHistory()
                      .getCurrentHistoryNode()
                      .getVariation(
                          Lizzie.board.getHistory().getCurrentHistoryNode().getVariations().size()
                              - 1)
                      .get()
                      .getData()
                      .lastMove
                      .get()[0],
                  Lizzie.board
                      .getHistory()
                      .getCurrentHistoryNode()
                      .getVariation(
                          Lizzie.board.getHistory().getCurrentHistoryNode().getVariations().size()
                              - 1)
                      .get()
                      .getData()
                      .lastMove
                      .get()[1]);
            } catch (Exception ex) {
            }
          } else {
            Lizzie.board.nextMove();
          }
        }
      }
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  @Override
  public void mouseDragged(MouseEvent e) {
    if (Draggedmode
        && !Lizzie.frame.isTrying
        && !Lizzie.frame.urlSgf
        && Lizzie.config.allowDrageDoubleClick) {
      Lizzie.frame.DraggedDragged(e.getX(), e.getY());
      return;
    }
    Lizzie.frame.onMouseDragged(e.getX(), e.getY());
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    if (Draggedmode
        && !Lizzie.frame.isTrying
        && !Lizzie.frame.urlSgf
        && Lizzie.config.allowDrageDoubleClick) {
      Lizzie.frame.DraggedMoved(e.getX(), e.getY());
      return;
    }
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

  public static void redo(int movesToAdvance) {
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

  //  private void toggleHints() {
  //    Lizzie.config.toggleShowBranch();
  //    Lizzie.config.showSubBoard =
  //        Lizzie.config.showNextMoves = Lizzie.config.showBestMoves = Lizzie.config.showBranch;
  //  }

  public static void nextBranch() {
    if (Lizzie.frame.isPlayingAgainstLeelaz) {
      Lizzie.frame.isPlayingAgainstLeelaz = false;
    }
    Lizzie.board.nextBranch();
  }

  public static void previousBranch() {
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

    switch (e.getKeyCode()) {
      case VK_E:
        if (e.isAltDown()) {
          if (Lizzie.frame.toolbar.isEnginePk) {
            Message msg = new Message();
            msg.setMessage("请等待当前引擎对战结束,或使用详细工具栏引擎对战面板中的[终止]按钮中断对战");
            msg.setVisible(true);
            return;
          }
          Lizzie.frame.toolbar.enginePkBlack.setEnabled(true);
          Lizzie.frame.toolbar.enginePkWhite.setEnabled(true);
          NewEngineGameDialog engineGame = new NewEngineGameDialog();
          GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
          engineGame.setGameInfo(gameInfo);
          engineGame.setVisible(true);
          Lizzie.frame.toolbar.resetEnginePk();
          if (engineGame.isCancelled()) {
            Lizzie.frame.toolbar.chkenginePk.setSelected(false);
            Lizzie.frame.toolbar.enginePkBlack.setEnabled(false);
            Lizzie.frame.toolbar.enginePkWhite.setEnabled(false);
            return;
          }

          Lizzie.board.getHistory().setGameInfo(gameInfo);
          Lizzie.leelaz.sendCommand("komi " + gameInfo.getKomi());
          Lizzie.frame.komi = gameInfo.getKomi() + "";

          Lizzie.frame.toolbar.chkenginePk.setSelected(true);
          Lizzie.frame.toolbar.isEnginePk = true;
          Lizzie.frame.toolbar.startEnginePk();
        }
        Lizzie.frame.toggleGtpConsole();
        break;
      case VK_RIGHT:
        // if (isinsertmode) {
        // return;
        // }
        if (e.isShiftDown()) {
          moveBranchDown();
        } else {

          nextBranch();
        }
        break;

      case VK_LEFT:
        // if (isinsertmode) {
        // return;
        // }
        if (e.isShiftDown()) {
          moveBranchUp();
        } else if (controlIsPressed(e)) {
          undoToFirstParentWithVariations();
        } else {

          previousBranch();
        }
        break;
      case VK_U:
        Lizzie.frame.toggleBestMoves();
        break;
      case VK_UP:
        // if (isinsertmode) {
        // return;
        // }
        if (controlIsPressed(e) && e.isShiftDown()) {
          goCommentNode(false);
        } else if (e.isShiftDown()) {
          undoToChildOfPreviousWithVariation();
        } else if (controlIsPressed(e)) {
          Lizzie.frame.noautocounting();
          undo(10);
        } else {

          undo();
        }
        break;

      case VK_PAGE_DOWN:
        if (controlIsPressed(e) && e.isShiftDown()) {
          Lizzie.frame.increaseMaxAlpha(-5);
        } else {
          // if (isinsertmode) {
          // return;
          // }
          Lizzie.frame.noautocounting();
          redo(10);
        }
        break;

      case VK_DOWN:
        // if (isinsertmode) {
        // return;
        // }
        if (controlIsPressed(e) && e.isShiftDown()) {
          goCommentNode(true);
        } else if (controlIsPressed(e)) {
          Lizzie.frame.noautocounting();
          redo(10);
        } else {
          redo();
        }
        break;

      case VK_N:
        // stop the ponder
        // if (isinsertmode) {
        // return;
        // }
        if (e.isAltDown()) {
          if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
          LizzieFrame.startNewGame();
        } else {
          if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
          Lizzie.frame.menu.newGame();
        }
        break;
      case VK_SPACE:
        if (Lizzie.frame.isPlayingAgainstLeelaz) {
          Lizzie.frame.isPlayingAgainstLeelaz = false;
          Lizzie.leelaz.isThinking = false;
          Lizzie.leelaz.togglePonder();
        }
        if (Lizzie.frame.isAnaPlayingAgainstLeelaz) {
          Lizzie.frame.isAnaPlayingAgainstLeelaz = false;
          Lizzie.frame.toolbar.chkAutoPlay.setSelected(false);
          Lizzie.frame.toolbar.isAutoPlay = false;
          Lizzie.frame.toolbar.chkAutoPlayBlack.setSelected(false);
          Lizzie.frame.toolbar.chkAutoPlayWhite.setSelected(false);
          Lizzie.frame.toolbar.chkShowBlack.setSelected(true);
          Lizzie.frame.toolbar.chkShowWhite.setSelected(true);
          Lizzie.leelaz.togglePonder();
        }
        Lizzie.leelaz.togglePonder();
        break;

      case VK_L:
        if (e.isAltDown()) {
          Lizzie.config.toggleShowLcbWinrate();
        } else while (Lizzie.board.setAsMainBranch()) ;
        break;

      case VK_P:
        Lizzie.board.pass();
        break;

      case VK_COMMA:
        if (!Lizzie.config.showSuggestionVaritions) {
          if (Lizzie.frame.isMouseOver) Lizzie.frame.playCurrentVariation();
          else Lizzie.frame.playBestMove();
        } else {
          if (!Lizzie.frame.playCurrentVariation()) Lizzie.frame.playBestMove();
        }
        break;

      case VK_M:
        if (e.isAltDown()) {
          // if (isinsertmode) {
          // return;
          // }
          Lizzie.frame.openChangeMoveDialog();
        } else {
          Lizzie.config.toggleShowMoveNumber();
        }
        break;

      case VK_F:
        if (e.isAltDown()) Lizzie.config.toggleShowNextMoves();
        else {
          if (e.isShiftDown()) Lizzie.config.toggleLargeSubBoard();
          else if (Lizzie.frame.toolbar.chkShowBlack.isSelected()
              || Lizzie.frame.toolbar.chkShowBlack.isSelected()) {
            Lizzie.frame.toolbar.chkShowBlack.setSelected(false);
            Lizzie.frame.toolbar.chkShowWhite.setSelected(false);
          } else {
            Lizzie.frame.toolbar.chkShowBlack.setSelected(true);
            Lizzie.frame.toolbar.chkShowWhite.setSelected(true);
          }
        }
        Lizzie.frame.refresh();
        break;

      case VK_H:
        // Lizzie.config.toggleHandicapInsteadOfWinrate();
        if (e.isAltDown()) {
          Lizzie.config.showHeat = !Lizzie.config.showHeat;
          Lizzie.frame.subBoardRenderer.showHeat = Lizzie.config.showHeat;
          Lizzie.frame.subBoardRenderer.clearBranch();
          Lizzie.frame.subBoardRenderer.removeHeat();
        } else Lizzie.frame.toggleheatmap();
        break;

      case VK_PAGE_UP:
        if (controlIsPressed(e) && e.isShiftDown()) {
          Lizzie.frame.increaseMaxAlpha(5);
        } else {
          // if (isinsertmode) {
          // return;
          // }
          Lizzie.frame.noautocounting();
          undo(10);
        }
        break;

      case VK_I:
        // stop the ponder
        // if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
        if (e.isControlDown()) {
          SetBoardSize st = new SetBoardSize();
          st.setVisible(true);
        } else Lizzie.frame.editGameInfo();
        break;
      case VK_S:
        // stop the ponder
        if (e.isShiftDown()) {
          Lizzie.frame.saveImage(
              Lizzie.frame.statx,
              Lizzie.frame.staty,
              (int) (Lizzie.frame.grw * 1.03),
              Lizzie.frame.grh + Lizzie.frame.stath);
        } else {
          if (e.isAltDown()) {
            Lizzie.frame.saveImage();
          } else {
            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
            LizzieFrame.saveFile();
          }
        }
        break;

      case VK_O:
        if (e.isShiftDown()) {
          Lizzie.frame.bowser("https://home.yikeweiqi.com/#/live", "弈客直播");
        } else {
          Lizzie.frame.noautocounting();
          Lizzie.frame.openFileAll();
        }
        break;

      case VK_V:
        // Lizzie.frame.getBowserUrl();
        if (controlIsPressed(e)) {
          // if (isinsertmode) {
          // return;
          // }
          Lizzie.frame.pasteSgf();
        } else if (e.isAltDown()) {
          Lizzie.config.showSuggestionVaritions = !Lizzie.config.showSuggestionVaritions;
          Lizzie.config.uiConfig.put(
              "show-suggestion-varitions", Lizzie.config.showSuggestionVaritions);
          try {
            Lizzie.config.save();
          } catch (IOException es) {
            // TODO Auto-generated catch block
          }
        } else {
          Lizzie.frame.tryPlay();
        }
        break;

      case VK_HOME:
        Lizzie.frame.noautocounting();
        // if (isinsertmode) {
        // return;
        // }
        if (controlIsPressed(e)) {
          Lizzie.board.clear();
          if (Lizzie.leelaz.isPondering()) {
            Lizzie.leelaz.ponder();
          }
        } else {
          while (Lizzie.board.previousMove()) ;
        }
        break;

      case VK_END:
        Lizzie.frame.noautocounting();
        // if (isinsertmode) {
        // return;
        // }
        while (Lizzie.board.nextMove()) ;
        break;

      case VK_X:
        if (controlIsPressed(e)) {
          Lizzie.frame.openConfigDialog();
        } else {
          if (!Lizzie.frame.showControls) {
            //             if (Lizzie.leelaz.isPondering()) {
            //             wasPonderingWhenControlsShown = true;
            //             Lizzie.leelaz.togglePonder();
            //             } else {
            //             wasPonderingWhenControlsShown = false;
            //             }
            Lizzie.frame.drawControls();
            // Lizzie.frame.showControls = true;
          }
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
        if (e.isShiftDown()) {
          Lizzie.frame.savePicToClipboard(
              Lizzie.frame.boardX, Lizzie.frame.boardY, Lizzie.frame.maxSize, Lizzie.frame.maxSize);
        } else if (controlIsPressed(e)) {
          Lizzie.frame.copySgf();
        } else {
          Lizzie.config.toggleCoordinates();
        }
        break;

      case VK_ENTER:
        // if (isinsertmode) {
        // return;
        // }
        if (e.isAltDown()) {
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
        } else {
          Lizzie.frame.toolbar.txtAutoPlayTime.setText(
              Lizzie.config.config.getJSONObject("leelaz").getInt("max-game-thinking-time-seconds")
                  + "");
          if (Lizzie.board.getHistory().isBlacksTurn()) {
            Lizzie.frame.toolbar.chkAutoPlayBlack.setSelected(true);
            Lizzie.frame.toolbar.chkAutoPlayWhite.setSelected(false);
          } else {
            Lizzie.frame.toolbar.chkAutoPlayBlack.setSelected(false);
            Lizzie.frame.toolbar.chkAutoPlayWhite.setSelected(true);
          }
          Lizzie.frame.toolbar.chkAutoPlayTime.setSelected(true);
          Lizzie.frame.toolbar.chkAutoPlay.setSelected(true);
          Lizzie.frame.toolbar.chkShowBlack.setSelected(false);
          Lizzie.frame.toolbar.chkShowWhite.setSelected(false);
          Lizzie.frame.isAnaPlayingAgainstLeelaz = true;
          Lizzie.frame.toolbar.isAutoPlay = true;
          Lizzie.leelaz.ponder();
        }
        break;

      case VK_B:
        Lizzie.frame.toggleBadMoves();
        break;
      case VK_DELETE:
      case VK_BACK_SPACE:
        // if (isinsertmode) {
        // return;
        // }
        if (e.isAltDown()) {
          Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
        } else if (e.isShiftDown()) {
          deleteBranch();
        } else {
          deleteMove();
        }
        break;

      case VK_Z:
        //  if (e.isShiftDown()) {
        Lizzie.config.toggleShowSubBoard();
        // }
        //        else {
        //          startTemporaryBoard();
        //        }
        break;
      case VK_Q:
        // Lizzie.frame.toggleAlwaysOntop();
        Lizzie.frame.openOnlineDialog();
        break;

      case VK_A:
        if (e.isAltDown() || e.isControlDown()) {
          Lizzie.frame.openAvoidMoveDialog();
        } else {
          shouldDisableAnalysis = false;
          StartAnaDialog newgame = new StartAnaDialog();
          newgame.setVisible(true);
          if (newgame.isCancelled()) {
            Lizzie.frame.toolbar.resetAutoAna();
          }
        }
        break;
        // this is copyed from https://github.com/zsalch/lizzie/tree/n_avoiddialog

      case VK_PERIOD:
        if (Lizzie.leelaz.isKatago) {
          Lizzie.config.showKataGoEstimate = !Lizzie.config.showKataGoEstimate;
          if (!Lizzie.config.showKataGoEstimate) {
            Lizzie.frame.boardRenderer.removecountblock();
            if (Lizzie.config.showSubBoard) Lizzie.frame.subBoardRenderer.removecountblock();
          }

          Lizzie.leelaz.ponder();
          Lizzie.config.uiConfig.put("show-katago-estimate", Lizzie.config.showKataGoEstimate);
          try {
            Lizzie.config.save();
          } catch (IOException es) {
            // TODO Auto-generated catch block
          }
        } else Lizzie.frame.countstones();
        // if (!Lizzie.board.getHistory().getNext().isPresent()) {
        // Lizzie.board.setScoreMode(!Lizzie.board.inScoreMode());
        // }
        break;

      case VK_D:
        toggleShowDynamicKomi();

        break;

      case VK_R:
        // if (isinsertmode) {
        // return;
        // }
        Lizzie.frame.replayBranch();
        break;

      case VK_OPEN_BRACKET:
        if (Lizzie.frame.BoardPositionProportion > 0) Lizzie.frame.BoardPositionProportion--;
        break;

      case VK_CLOSE_BRACKET:
        if (Lizzie.frame.BoardPositionProportion < 8) Lizzie.frame.BoardPositionProportion++;
        break;

      case VK_K:
        if (e.isAltDown()) {
          Lizzie.config.toggleEvaluationColoring();
        }
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
          Lizzie.engineManager.switchEngine(e.getKeyCode() - VK_1);
        }
        break;
      case VK_0:
        if (controlIsPressed(e)) {
          Lizzie.engineManager.switchEngine(9);
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
        // if (wasPonderingWhenControlsShown) Lizzie.leelaz.togglePonder();
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
        if (Lizzie.frame.isMouseOver) {
          Lizzie.frame.doBranch(1);
        } else {
          redo();
        }
      } else if (e.getWheelRotation() < 0) {
        if (Lizzie.frame.isMouseOver) {
          Lizzie.frame.doBranch(-1);
        } else {
          undo();
        }
      }
      Lizzie.frame.refresh();
    }
  }
}
