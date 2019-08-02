package featurecat.lizzie.gui;

import static java.awt.RenderingHints.*;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.Branch;
import featurecat.lizzie.analysis.MoveData;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardData;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.rules.Stone;
import featurecat.lizzie.rules.Zobrist;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BoardRenderer {
  // Percentage of the boardLength to offset before drawing black lines
  private static final double MARGIN = 0.03;
  private static final double MARGIN_WITH_COORDINATES = 0.06;
  private static final double STARPOINT_DIAMETER = 0.015;
  private static final BufferedImage emptyImage = new BufferedImage(1, 1, TYPE_INT_ARGB);

  private int x, y;
  private int boardWidth, boardHeight;
  private int shadowRadius;
  private static boolean emptyName = false;
  private static boolean changedName = false;

  private JSONObject uiConfig, uiPersist;
  private int scaledMarginWidth, availableWidth, squareWidth, stoneRadius;
  private int scaledMarginHeight, availableHeight, squareHeight;
  public Optional<Branch> branchOpt = Optional.empty();
  private List<MoveData> bestMoves;

  private BufferedImage cachedBackgroundImage = emptyImage;
  private boolean cachedBackgroundImageHasCoordinatesEnabled = false;
  private int cachedX, cachedY;
  private int cachedBoardWidth = 0, cachedBoardHeight = 0;
  private BufferedImage cachedStonesImage = emptyImage;
  private BufferedImage cachedStonesImagedraged = emptyImage;
  private BufferedImage blockimage = emptyImage;
  private BufferedImage countblockimage = emptyImage;

  private BufferedImage cachedBoardImage = emptyImage;
  private BufferedImage cachedWallpaperImage = emptyImage;
  private BufferedImage cachedStonesShadowImage = emptyImage;
  private BufferedImage cachedStonesShadowImagedraged = emptyImage;
  private Zobrist cachedZhash = new Zobrist(); // defaults to an empty board

  private BufferedImage cachedBlackStoneImage = emptyImage;
  private BufferedImage cachedWhiteStoneImage = emptyImage;

  private BufferedImage branchStonesImage = emptyImage;
  private BufferedImage branchStonesShadowImage;

  private boolean lastInScoreMode = false;

  public Optional<List<String>> variationOpt;

  // special values of displayedBranchLength
  public static final int SHOW_RAW_BOARD = -1;
  public static final int SHOW_NORMAL_BOARD = -2;

  private int displayedBranchLength = SHOW_NORMAL_BOARD;
  private int cachedDisplayedBranchLength = SHOW_RAW_BOARD;
  private boolean showingBranch = false;
  private boolean isMainBoard = false;
  public boolean reverseBestmoves = false;
  private int maxAlpha = 240;
  int number = 1;
  TexturePaint paint;

  public BoardRenderer(boolean isMainBoard) {
    uiConfig = Lizzie.config.uiConfig;
    uiPersist = Lizzie.config.persisted.getJSONObject("ui-persist");
    try {
      maxAlpha = uiPersist.getInt("max-alpha");
    } catch (JSONException e) {
    }
    this.isMainBoard = isMainBoard;
  }

  /** Draw a go board */
  public void draw(Graphics2D g) {

    // setupSizeParameters();

    // Stopwatch timer = new Stopwatch();
    drawGoban(g);
    if (Lizzie.config.showNameInBoard) drawName(g);
    // timer.lap("background");
    drawStones();
    // timer.lap("stones");
    if (Lizzie.board.inScoreMode() && isMainBoard) {
      drawScore(g);
    } else {
      drawBranch();
    }
    // timer.lap("branch");

    renderImages(g);
    // timer.lap("rendering images");

    if (!isMainBoard) {
      drawMoveNumbers(g);
      return;
    }

    if (!isShowingRawBoard()) {
      drawMoveNumbers(g);
      if (Lizzie.config.showNextMoves) {
        drawNextMoves(g);
      }
      // timer.lap("movenumbers");
      if (!Lizzie.frame.isPlayingAgainstLeelaz && Lizzie.config.showBestMovesNow()) {
        if ((Lizzie.board.getHistory().isBlacksTurn()
                && Lizzie.frame.toolbar.chkShowBlack.isSelected())
            || (!Lizzie.board.getHistory().isBlacksTurn()
                && Lizzie.frame.toolbar.chkShowWhite.isSelected())) {
          drawLeelazSuggestions(g);
        }
      }

      drawStoneMarkup(g);
    }

    // timer.lap("leelaz");

    // timer.print();
  }

  /**
   * Return the best move of Leelaz's suggestions
   *
   * @return the optional coordinate name of the best move
   */
  public Optional<String> bestMoveCoordinateName() {
    return bestMoves.isEmpty() ? Optional.empty() : Optional.of(bestMoves.get(0).coordinate);
  }

  /** Calculate good values for boardLength, scaledMargin, availableLength, and squareLength */
  public static int[] availableLength(int boardWidth, int boardHeight, boolean showCoordinates) {
    int[] calculatedPixelMargins = calculatePixelMargins(boardWidth, boardHeight, showCoordinates);
    return (calculatedPixelMargins != null && calculatedPixelMargins.length >= 6)
        ? calculatedPixelMargins
        : new int[] {boardWidth, 0, boardWidth, boardHeight, 0, boardHeight};
  }

  /** Calculate good values for boardLength, scaledMargin, availableLength, and squareLength */
  public void setupSizeParameters() {
    int boardWidth0 = boardWidth;
    int boardHeight0 = boardHeight;

    int[] calculatedPixelMargins = calculatePixelMargins();
    boardWidth = calculatedPixelMargins[0];
    scaledMarginWidth = calculatedPixelMargins[1];
    availableWidth = calculatedPixelMargins[2];
    boardHeight = calculatedPixelMargins[3];
    scaledMarginHeight = calculatedPixelMargins[4];
    availableHeight = calculatedPixelMargins[5];

    squareWidth = calculateSquareWidth(availableWidth);
    squareHeight = calculateSquareHeight(availableHeight);
    if (squareWidth > squareHeight) {
      squareWidth = squareHeight;
      int newWidth = squareWidth * (Board.boardWidth - 1) + 1;
      int diff = availableWidth - newWidth;
      availableWidth = newWidth;
      boardWidth -= diff + (scaledMarginWidth - scaledMarginHeight) * 2;
      scaledMarginWidth = scaledMarginHeight;
    } else if (squareWidth < squareHeight) {
      squareHeight = squareWidth;
      int newHeight = squareHeight * (Board.boardHeight - 1) + 1;
      int diff = availableHeight - newHeight;
      availableHeight = newHeight;
      boardHeight -= diff + (scaledMarginHeight - scaledMarginWidth) * 2;
      scaledMarginHeight = scaledMarginWidth;
    }
    stoneRadius = max(squareWidth, squareHeight) < 4 ? 1 : max(squareWidth, squareHeight) / 2 - 1;

    // re-center board
    setLocation(x + (boardWidth0 - boardWidth) / 2, y + (boardHeight0 - boardHeight) / 2);
  }

  private void drawName(Graphics2D g0) {
    g0.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    String black = Lizzie.board.getHistory().getGameInfo().getPlayerBlack();
    if (black.length() > 20) black = black.substring(0, 20);
    String white = Lizzie.board.getHistory().getGameInfo().getPlayerWhite();
    if (white.length() > 20) white = white.substring(0, 20);
    if (black.equals("") && white.contentEquals("")) {
      if (!emptyName) {
        emptyName = true;
        changedName = true;
      }
      return;
    }
    if (Lizzie.frame.toolbar.isEnginePk && Lizzie.frame.toolbar.isEnginePkBatch) {
      if (Lizzie.frame.toolbar.EnginePkBatchNumberNow % 2 == 0) {
        black = Lizzie.frame.toolbar.pkWhiteWins + " " + black;
        white = white + " " + Lizzie.frame.toolbar.pkBlackWins;
      } else {
        black = Lizzie.frame.toolbar.pkBlackWins + " " + black;
        white = white + " " + Lizzie.frame.toolbar.pkWhiteWins;
      }
    }
    if (emptyName) {
      emptyName = false;
      changedName = true;
      Lizzie.frame.refresh();
    }
    emptyName = false;
    if (Lizzie.board.getHistory().isBlacksTurn()) {
      g0.setColor(Color.WHITE);
      g0.fillOval(
          x + boardWidth / 2 - stoneRadius * 1 / 5,
          y - scaledMarginHeight + stoneRadius + boardHeight,
          stoneRadius,
          stoneRadius);

      g0.setColor(Color.BLACK);
      g0.fillOval(
          x + boardWidth / 2 - stoneRadius * 4 / 5,
          y - scaledMarginHeight + stoneRadius + boardHeight,
          stoneRadius,
          stoneRadius);
    } else {
      g0.setColor(Color.BLACK);
      g0.fillOval(
          x + boardWidth / 2 - stoneRadius * 4 / 5,
          y - scaledMarginHeight + stoneRadius + boardHeight,
          stoneRadius,
          stoneRadius);
      g0.setColor(Color.WHITE);
      g0.fillOval(
          x + boardWidth / 2 - stoneRadius * 1 / 5,
          y - scaledMarginHeight + stoneRadius + boardHeight,
          stoneRadius,
          stoneRadius);
    }
    g0.setColor(Color.BLACK);
    String regex = "[\u4e00-\u9fa5]";

    drawStringBold(
        g0,
        x
            + boardWidth / 2
            - black.replaceAll(regex, "12").length() * stoneRadius / 4
            - stoneRadius * 5 / 4,
        y - scaledMarginHeight + stoneRadius + boardHeight + stoneRadius * 3 / 5,
        Lizzie.frame.uiFont,
        black,
        stoneRadius,
        stoneRadius * black.replaceAll(regex, "12").length() / 2);
    g0.setColor(Color.WHITE);
    drawStringBold(
        g0,
        x
            + boardWidth / 2
            + white.replaceAll(regex, "12").length() * stoneRadius / 4
            + stoneRadius * 5 / 4,
        y - scaledMarginHeight + stoneRadius + boardHeight + stoneRadius * 3 / 5,
        Lizzie.frame.uiFont,
        white,
        stoneRadius,
        stoneRadius * white.replaceAll(regex, "12").length() / 2);

    //    drawStringBoard(
    //        g0,
    //        x + boardWidth / 2 + stoneRadius * 2 / 13,
    //        y - scaledMarginHeight + stoneRadius * 13 / 8 + boardHeight,
    //        Lizzie.frame.uiFont,
    //        Lizzie.board.getHistory().getGameInfo().getKomi() + "",
    //        stoneRadius * 13 / 8,
    //        stoneRadius * 13 / 8);
  }
  /**
   * Draw the green background and go board with lines. We cache the image for a performance boost.
   */
  private void drawGoban(Graphics2D g0) {
    int width = Lizzie.frame.mainPanel.getWidth();
    int height = Lizzie.frame.mainPanel.getHeight();
    // Draw the cached background image if frame size changes
    if (cachedBackgroundImage.getWidth() != width
        || cachedBackgroundImage.getHeight() != height
        || cachedBoardWidth != boardWidth
        || cachedBoardHeight != boardHeight
        || cachedX != x
        || cachedY != y
        || cachedBackgroundImageHasCoordinatesEnabled != showCoordinates()
        || changedName
        || Lizzie.board.isForceRefresh()) {
      changedName = false;
      cachedBoardWidth = boardWidth;
      cachedBoardHeight = boardHeight;
      Lizzie.board.setForceRefresh(false);

      cachedBackgroundImage = new BufferedImage(width, height, TYPE_INT_ARGB);
      Graphics2D g = cachedBackgroundImage.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

      // Draw the wooden background
      drawWoodenBoard(g);

      // Draw the lines
      g.setColor(Color.BLACK);
      for (int i = 0; i < Board.boardHeight; i++) {
        // g.setStroke(new BasicStroke(stoneRadius / 15f));
        if (i == 0 || i == Board.boardHeight - 1) {
          g.setStroke(new BasicStroke(stoneRadius / 10f));
          g.drawLine(
              x + scaledMarginWidth,
              y + scaledMarginHeight + squareHeight * i,
              x + scaledMarginWidth + availableWidth - 1,
              y + scaledMarginHeight + squareHeight * i);
        }
        g.setStroke(new BasicStroke(1f));
        g.drawLine(
            x + scaledMarginWidth,
            y + scaledMarginHeight + squareHeight * i,
            x + scaledMarginWidth + availableWidth - 1,
            y + scaledMarginHeight + squareHeight * i);
      }
      for (int i = 0; i < Board.boardWidth; i++) {
        // g.setStroke(new BasicStroke(stoneRadius / 15f));
        if (i == 0 || i == Board.boardWidth - 1) {
          g.setStroke(new BasicStroke(stoneRadius / 10f));
          g.drawLine(
              x + scaledMarginWidth + squareWidth * i,
              y + scaledMarginHeight,
              x + scaledMarginWidth + squareWidth * i,
              y + scaledMarginHeight + availableHeight - 1);
        }
        g.setStroke(new BasicStroke(1f));
        g.drawLine(
            x + scaledMarginWidth + squareWidth * i,
            y + scaledMarginHeight,
            x + scaledMarginWidth + squareWidth * i,
            y + scaledMarginHeight + availableHeight - 1);
      }

      // Draw the star points
      drawStarPoints(g);

      // Draw coordinates if enabled
      if (showCoordinates()) {
        g.setColor(Color.BLACK);
        for (int i = 0; i < Board.boardWidth; i++) {
          drawString(
              g,
              x + scaledMarginWidth + squareWidth * i,
              y + scaledMarginHeight * 4 / 10,
              Lizzie.frame.uiFont,
              Board.asName(i),
              stoneRadius * 4 / 5,
              stoneRadius);
          if (!Lizzie.config.showNameInBoard
              || Lizzie.board != null
                  && (Lizzie.board.getHistory().getGameInfo().getPlayerWhite().equals("")
                      && Lizzie.board.getHistory().getGameInfo().getPlayerBlack().equals("")))
            drawString(
                g,
                x + scaledMarginWidth + squareWidth * i,
                y - scaledMarginHeight * 4 / 10 + boardHeight,
                Lizzie.frame.uiFont,
                Board.asName(i),
                stoneRadius * 4 / 5,
                stoneRadius);
        }
        for (int i = 0; i < Board.boardHeight; i++) {
          drawString(
              g,
              x + scaledMarginWidth * 4 / 10,
              y + scaledMarginHeight + squareHeight * i,
              Lizzie.frame.uiFont,
              "" + (Board.boardHeight <= 25 ? (Board.boardHeight - i) : (i + 1)),
              stoneRadius * 4 / 5,
              stoneRadius);
          drawString(
              g,
              x - scaledMarginWidth * 4 / 10 + boardWidth,
              y + scaledMarginHeight + squareHeight * i,
              Lizzie.frame.uiFont,
              "" + (Board.boardHeight <= 25 ? (Board.boardHeight - i) : (i + 1)),
              stoneRadius * 4 / 5,
              stoneRadius);
        }
      }
      g.dispose();
    }

    g0.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
    g0.drawImage(cachedBackgroundImage, 0, 0, null);
    cachedX = x;
    cachedY = y;
  }

  /**
   * Draws the star points on the board, according to board size
   *
   * @param g graphics2d object to draw
   */
  private void drawStarPoints(Graphics2D g) {
    if (Board.boardWidth == 19 && Board.boardHeight == 19) {
      drawStarPoints0(3, 3, 6, false, g);
    } else if (Board.boardWidth == 13 && Board.boardHeight == 13) {
      drawStarPoints0(2, 3, 6, true, g);
    } else if (Board.boardWidth == 9 && Board.boardHeight == 9) {
      drawStarPoints0(2, 2, 4, true, g);
    } else if (Board.boardWidth == 7 && Board.boardHeight == 7) {
      drawStarPoints0(2, 2, 2, true, g);
    } else if (Board.boardWidth == 5 && Board.boardHeight == 5) {
      drawStarPoints0(0, 0, 2, true, g);
    }
  }

  private void drawStarPoints0(
      int nStarpoints, int edgeOffset, int gridDistance, boolean center, Graphics2D g) {
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    int starPointRadius = (int) (STARPOINT_DIAMETER * min(boardWidth, boardHeight)) / 2;
    for (int i = 0; i < nStarpoints; i++) {
      for (int j = 0; j < nStarpoints; j++) {
        int centerX = x + scaledMarginWidth + squareWidth * (edgeOffset + gridDistance * i);
        int centerY = y + scaledMarginHeight + squareHeight * (edgeOffset + gridDistance * j);
        fillCircle(g, centerX, centerY, starPointRadius);
      }
    }

    if (center) {
      int centerX = x + scaledMarginWidth + squareWidth * gridDistance;
      int centerY = y + scaledMarginHeight + squareHeight * gridDistance;
      fillCircle(g, centerX, centerY, starPointRadius);
    }
  }

  public void removedrawmovestone() {
    cachedStonesImagedraged = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    cachedStonesShadowImagedraged = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
  }

  public void drawmovestone(int x, int y, Stone stone) {
    cachedStonesImagedraged = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    cachedStonesShadowImagedraged = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = cachedStonesImagedraged.createGraphics();
    Graphics2D gShadow = cachedStonesShadowImagedraged.createGraphics();
    gShadow.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    // gShadow.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    // g.setRenderingHint(RenderingHints.KEY_RENDERING,
    // RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

    // gShadow.setRenderingHint(RenderingHints.KEY_RENDERING,
    // RenderingHints.VALUE_RENDER_QUALITY);
    // gShadow.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    int stoneX = scaledMarginWidth + squareWidth * x;
    int stoneY = scaledMarginHeight + squareHeight * y;
    drawStone(g, gShadow, stoneX, stoneY, stone, x, y);
  }

  public void removecountblock() {
    countblockimage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
  }

  public void drawcountblockkata(ArrayList<Double> tempcount) {
    BufferedImage newEstimateImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = newEstimateImage.createGraphics();
    for (int i = 0; i < tempcount.size(); i++) {

      if ((tempcount.get(i) > 0 && Lizzie.board.getHistory().isBlacksTurn())
          || (tempcount.get(i) < 0 && !Lizzie.board.getHistory().isBlacksTurn())) {
        int y = i / Lizzie.board.boardWidth;
        int x = i % Lizzie.board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        // g.setColor(Color.BLACK);

        int alpha = (int) (tempcount.get(i) * 255);
        Color cl = new Color(0, 0, 0, Math.abs(alpha));
        Color cl2 =
            new Color(
                127 - (Math.abs(alpha) - 1) / 2,
                127 - (Math.abs(alpha) - 1) / 2,
                127 - (Math.abs(alpha) - 1) / 2,
                255);
        if (Lizzie.board.getHistory().getStones()[Lizzie.board.getIndex(x, y)].isBlack())
          g.setColor(cl2);
        else g.setColor(cl);
        g.fillRect(
            (int) (stoneX - squareWidth * 0.3),
            (int) (stoneY - squareHeight * 0.3),
            (int) (squareWidth * 0.6),
            (int) (squareHeight * 0.6));
        ;
      }
      if ((tempcount.get(i) < 0 && Lizzie.board.getHistory().isBlacksTurn())
          || (tempcount.get(i) > 0 && !Lizzie.board.getHistory().isBlacksTurn())) {
        int y = i / Lizzie.board.boardWidth;
        int x = i % Lizzie.board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        int alpha = (int) (tempcount.get(i) * 255);
        Color cl = new Color(255, 255, 255, Math.abs(alpha));
        //        Color cl2 = new Color(Math.abs(alpha), Math.abs(alpha), Math.abs(alpha), 255);
        //        if(Lizzie.board.getHistory().getStones()[Lizzie.board.getIndex(x, y)].isWhite())
        //        	g.setColor(cl2);
        //        else
        g.setColor(cl);
        g.fillRect(
            (int) (stoneX - squareWidth * 0.3),
            (int) (stoneY - squareHeight * 0.3),
            (int) (squareWidth * 0.6),
            (int) (squareHeight * 0.6));
      }
    }
    countblockimage = newEstimateImage;
  }

  private double convertLength(double length) {
    double lengthab = Math.abs(length);
    if (lengthab > 0.2) {
      lengthab = lengthab * 6 / 10;
      return lengthab;
    } else {
      return 0;
    }
  }

  public void drawcountblockkata2(ArrayList<Double> tempcount) {
    BufferedImage newEstimateImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = newEstimateImage.createGraphics();
    for (int i = 0; i < tempcount.size(); i++) {
      if ((tempcount.get(i) > 0 && Lizzie.board.getHistory().isBlacksTurn())
          || (tempcount.get(i) < 0 && !Lizzie.board.getHistory().isBlacksTurn())) {
        int y = i / Lizzie.board.boardWidth;
        int x = i % Lizzie.board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        Color cl = new Color(0, 0, 0, 180);
        g.setColor(cl);
        int length = (int) (convertLength(tempcount.get(i)) * 2 * stoneRadius);
        if (length > 0) g.fillRect(stoneX - length / 2, stoneY - length / 2, length, length);
      }
      if ((tempcount.get(i) < 0 && Lizzie.board.getHistory().isBlacksTurn())
          || (tempcount.get(i) > 0 && !Lizzie.board.getHistory().isBlacksTurn())) {
        int y = i / Lizzie.board.boardWidth;
        int x = i % Lizzie.board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        int length = (int) (convertLength(tempcount.get(i)) * 2 * stoneRadius);

        Color cl = new Color(255, 255, 255, 180);
        g.setColor(cl);
        if (length > 0) g.fillRect(stoneX - length / 2, stoneY - length / 2, length, length);
      }
    }
    countblockimage = newEstimateImage;
  }

  public void drawcountblock(ArrayList<Integer> tempcount) {
    countblockimage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = countblockimage.createGraphics();
    for (int i = 0; i < tempcount.size(); i++) {
      if (tempcount.get(i) > 0) {
        int y = i / Lizzie.board.boardWidth;
        int x = i % Lizzie.board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        g.setColor(Color.BLACK);
        g.fillRect(stoneX - stoneRadius / 2, stoneY - stoneRadius / 2, stoneRadius, stoneRadius);
      }
      if (tempcount.get(i) < 0) {
        int y = i / Lizzie.board.boardWidth;
        int x = i % Lizzie.board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        g.setColor(Color.WHITE);
        g.fillRect(stoneX - stoneRadius / 2, stoneY - stoneRadius / 2, stoneRadius, stoneRadius);
      }
    }
  }

  public void removeblock() {
    blockimage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
  }

  public void drawmoveblock(int x, int y, boolean isblack) {
    if (boardWidth == 0 || boardHeight == 0) return;
    blockimage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Stone[] stones = Lizzie.board.getStones();
    if (stones[Lizzie.board.getIndex(x, y)].isBlack()
        || stones[Lizzie.board.getIndex(x, y)].isWhite()) {
      return;
    }
    Graphics2D g = blockimage.createGraphics();
    int stoneX = scaledMarginWidth + squareWidth * x;
    int stoneY = scaledMarginHeight + squareHeight * y;
    g.setColor(isblack ? Color.BLACK : Color.WHITE);
    g.fillRect(
        stoneX - squareWidth / 4, stoneY - squareWidth / 4, squareWidth / 2, squareWidth / 2);
  }

  public void drawbadstone(int x, int y, Stone stone) {
    cachedStonesImagedraged = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    cachedStonesShadowImagedraged = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = cachedStonesImagedraged.createGraphics();
    Graphics2D gShadow = cachedStonesShadowImagedraged.createGraphics();
    gShadow.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    // gShadow.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    // g.setRenderingHint(RenderingHints.KEY_RENDERING,
    // RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

    // gShadow.setRenderingHint(RenderingHints.KEY_RENDERING,
    // RenderingHints.VALUE_RENDER_QUALITY);
    // gShadow.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    int stoneX = scaledMarginWidth + squareWidth * x;
    int stoneY = scaledMarginHeight + squareHeight * y;
    g.setColor(Color.magenta);
    drawCircle3(g, stoneX, stoneY, stoneRadius * 7 / 6);
  }

  /** Draw the stones. We cache the image for a performance boost. */
  public void drawStones() {
    if (Lizzie.board == null) return;

    // draw a new image if frame size changes or board state changes
    if (cachedStonesImage.getWidth() != boardWidth
        || cachedStonesImage.getHeight() != boardHeight
        || cachedDisplayedBranchLength != displayedBranchLength
        || cachedBackgroundImageHasCoordinatesEnabled != showCoordinates()
        || !cachedZhash.equals(Lizzie.board.getData().zobrist)
        || Lizzie.board.inScoreMode()
        || lastInScoreMode) {

      cachedStonesImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
      cachedStonesShadowImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
      Graphics2D g = cachedStonesImage.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      Graphics2D gShadow = cachedStonesShadowImage.createGraphics();
      gShadow.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

      // we need antialiasing to make the stones pretty. Java is a bit slow at
      // antialiasing; that's
      // why we want the cache
      g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
      gShadow.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

      for (int i = 0; i < Board.boardWidth; i++) {
        for (int j = 0; j < Board.boardHeight; j++) {
          int stoneX = scaledMarginWidth + squareWidth * i;
          int stoneY = scaledMarginHeight + squareHeight * j;
          drawStone(
              g, gShadow, stoneX, stoneY, Lizzie.board.getStones()[Board.getIndex(i, j)], i, j);
        }
      }

      cachedZhash = Lizzie.board.getData().zobrist.clone();
      cachedDisplayedBranchLength = displayedBranchLength;
      cachedBackgroundImageHasCoordinatesEnabled = showCoordinates();
      g.dispose();
      gShadow.dispose();
      lastInScoreMode = false;
    }
    if (Lizzie.board.inScoreMode()) lastInScoreMode = true;
  }

  /*
   * Draw a white/black dot on territory and captured stones. Dame is drawn as red
   * dot.
   */
  private void drawScore(Graphics2D go) {
    Graphics2D g = cachedStonesImage.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    Stone scorestones[] = Lizzie.board.scoreStones();
    int scoreRadius = stoneRadius / 4;
    for (int i = 0; i < Board.boardWidth; i++) {
      for (int j = 0; j < Board.boardHeight; j++) {
        int stoneX = scaledMarginWidth + squareWidth * i;
        int stoneY = scaledMarginHeight + squareHeight * j;
        switch (scorestones[Board.getIndex(i, j)]) {
          case WHITE_POINT:
          case BLACK_CAPTURED:
            g.setColor(Color.white);
            fillCircle(g, stoneX, stoneY, scoreRadius);
            break;
          case BLACK_POINT:
          case WHITE_CAPTURED:
            g.setColor(Color.black);
            fillCircle(g, stoneX, stoneY, scoreRadius);
            break;
          case DAME:
            g.setColor(Color.red);
            fillCircle(g, stoneX, stoneY, scoreRadius);
            break;
        }
      }
    }
    g.dispose();
  }

  /** Draw the 'ghost stones' which show a variationOpt Leelaz is thinking about */
  private void drawBranch() {
    showingBranch = false;
    branchStonesImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    branchStonesShadowImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    branchOpt = Optional.empty();

    if (Lizzie.frame.isPlayingAgainstLeelaz) {
      return;
    }

    // Leela Zero isn't connected yet
    if (Lizzie.leelaz == null) return;

    // calculate best moves and branch
    bestMoves = Lizzie.leelaz.getBestMoves();
    if (MoveData.getPlayouts(bestMoves) < Lizzie.board.getData().getPlayouts()) {
      bestMoves = Lizzie.board.getData().bestMoves;
    }

    variationOpt = Optional.empty();

    if (isMainBoard && (isShowingRawBoard() || !Lizzie.config.showBranchNow())) {
      return;
    }

    Graphics2D g = (Graphics2D) branchStonesImage.getGraphics();
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    Graphics2D gShadow = (Graphics2D) branchStonesShadowImage.getGraphics();
    gShadow.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    Optional<MoveData> suggestedMove = (isMainBoard ? mouseOveredMove() : getBestMove());

    if (!suggestedMove.isPresent() || Lizzie.frame.isheatmap) {
      return;
    }
    List<String> variation = suggestedMove.get().variation;
    Branch branch = null;
    if (Lizzie.frame.toolbar.isEnginePk && Lizzie.frame.toolbar.isGenmove)
      branch =
          new Branch(
              Lizzie.board,
              variation,
              true,
              this.displayedBranchLength > 0 ? displayedBranchLength : 199);
    else
      branch =
          new Branch(
              Lizzie.board,
              variation,
              reverseBestmoves,
              this.displayedBranchLength > 0 ? displayedBranchLength : 199);
    branchOpt = Optional.of(branch);
    variationOpt = Optional.of(variation);
    showingBranch = true;

    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

    for (int i = 0; i < Board.boardWidth; i++) {
      for (int j = 0; j < Board.boardHeight; j++) {
        // Display latest stone for ghost dead stone
        int index = Board.getIndex(i, j);
        Stone stone = branch.data.stones[index];
        boolean isGhost = (stone == Stone.BLACK_GHOST || stone == Stone.WHITE_GHOST);
        if (Lizzie.board.getData().stones[index] != Stone.EMPTY && !isGhost) continue;
        if (branch.data.moveNumberList[index] > maxBranchMoves()) continue;

        int stoneX = scaledMarginWidth + squareWidth * i;
        int stoneY = scaledMarginHeight + squareHeight * j;

        drawStone(g, gShadow, stoneX, stoneY, stone.unGhosted(), i, j);
        if (i == Lizzie.frame.suggestionclick[0] && j == Lizzie.frame.suggestionclick[1]) {
          Optional<int[]> coords1 = Board.asCoordinates(suggestedMove.get().coordinate);
          if (coords1.isPresent()
              && coords1.get()[0] == Lizzie.frame.suggestionclick[0]
              && coords1.get()[1] == Lizzie.frame.suggestionclick[1]) {
            g.setColor(Color.magenta);
            drawCircle3(g, stoneX, stoneY, stoneRadius - 1);
          }
        }
      }
    }

    g.dispose();
    gShadow.dispose();
  }

  private Optional<MoveData> mouseOveredMove() {
    return bestMoves
        .stream()
        .filter(
            move ->
                Board.asCoordinates(move.coordinate)
                    .map(c -> Lizzie.frame.isMouseOver(c[0], c[1]))
                    .orElse(false))
        .findFirst();
  }

  private Optional<MoveData> getBestMove() {
    return bestMoves.isEmpty() ? Optional.empty() : Optional.of(bestMoves.get(0));
  }

  /** Render the shadows and stones in correct background-foreground order */
  private void renderImages(Graphics2D g) {
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
    g.drawImage(cachedStonesShadowImage, x, y, null);
    g.drawImage(cachedStonesShadowImagedraged, x, y, null);
    if (Lizzie.config.showBranchNow()) {
      g.drawImage(branchStonesShadowImage, x, y, null);
    }
    g.drawImage(cachedStonesImage, x, y, null);
    g.drawImage(cachedStonesImagedraged, x, y, null);
    g.drawImage(blockimage, x, y, null);
    g.drawImage(countblockimage, x, y, null);

    if (Lizzie.config.showBranchNow()) {
      g.drawImage(branchStonesImage, x, y, null);
    }
  }

  /** Draw move numbers and/or mark the last played move */
  private void drawMoveNumbers(Graphics2D g) {
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    Board board = Lizzie.board;
    Optional<int[]> lastMoveOpt = branchOpt.map(b -> b.data.lastMove).orElse(board.getLastMove());

    if (!lastMoveOpt.isPresent() && board.getData().moveNumber != 0 && !board.inScoreMode()) {
      g.setColor(
          board.getData().blackToPlay ? new Color(255, 255, 255, 150) : new Color(0, 0, 0, 150));
      g.fillOval(
          x + boardWidth / 2 - 4 * stoneRadius,
          y + boardHeight / 2 - 4 * stoneRadius,
          stoneRadius * 8,
          stoneRadius * 8);
      g.setColor(
          board.getData().blackToPlay ? new Color(0, 0, 0, 255) : new Color(255, 255, 255, 255));
      drawString(
          g,
          x + boardWidth / 2,
          y + boardHeight / 2,
          LizzieFrame.winrateFont,
          "pass",
          stoneRadius * 4,
          stoneRadius * 6);
    }
    if (Lizzie.config.allowMoveNumber == 0 && !branchOpt.isPresent() && !Lizzie.frame.isTrying) {
      if (lastMoveOpt.isPresent()) {
        int[] lastMove = lastMoveOpt.get();

        // Mark the last coordinate
        int lastMoveMarkerRadius = stoneRadius / 2;
        int stoneX = x + scaledMarginWidth + squareWidth * lastMove[0];
        int stoneY = y + scaledMarginHeight + squareHeight * lastMove[1];

        // Set color to the opposite color of whatever is on the board
        boolean isWhite = board.getStones()[Board.getIndex(lastMove[0], lastMove[1])].isWhite();
        // g.setColor(Lizzie.board.getData().blackToPlay ? Color.BLACK : Color.WHITE);
        g.setColor(Color.red);

        switch (Lizzie.config.stoneIndicatorType) {
          case 0:
            drawCircle2(g, stoneX, stoneY, lastMoveMarkerRadius);
            break;
          case 1:
            drawCircle(g, stoneX, stoneY, lastMoveMarkerRadius);
            break;
          case 2:
            fillCircle(g, stoneX, stoneY, (int) (lastMoveMarkerRadius * 0.65));
            break;
        }
        //        if (Lizzie.config.stoneIndicatorType == 2) {
        //            // Use a solid circle instead of
        //            fillCircle(g, stoneX, stoneY, (int) (lastMoveMarkerRadius * 0.65));
        //          } else if (Lizzie.config.stoneIndicatorType == 0) {
        //          } else {
        //            drawCircle2(g, stoneX, stoneY, lastMoveMarkerRadius);
        //          }

        //        if (Lizzie.config.solidStoneIndicator) {
        //          // Use a solid circle instead of
        //          fillCircle(g, stoneX, stoneY, (int) (lastMoveMarkerRadius * 0.65));
        //        } else {
        //          // fillCircle(g, stoneX, stoneY, (int) (lastMoveMarkerRadius * 0.70));
        //          drawCircle2(g, stoneX, stoneY, lastMoveMarkerRadius);
        //          // 需要恢复的
        //        }
      }

      return;
    }

    int[] moveNumberList =
        branchOpt.map(b -> b.data.moveNumberList).orElse(board.getMoveNumberList());

    // Allow to display only last move number
    int lastMoveNumber =
        branchOpt
            .map(b -> b.data.moveNumber)
            .orElse(Arrays.stream(moveNumberList).max().getAsInt());

    for (int i = 0; i < Board.boardWidth; i++) {
      for (int j = 0; j < Board.boardHeight; j++) {
        int stoneX = x + scaledMarginWidth + squareWidth * i;
        int stoneY = y + scaledMarginHeight + squareHeight * j;
        int here = Board.getIndex(i, j);

        // Allow to display only last move number
        if (!Lizzie.frame.isTrying) {
          if ((Lizzie.config.allowMoveNumber > -1
              && lastMoveNumber - moveNumberList[here] >= Lizzie.config.allowMoveNumber)) {
            continue;
          }
        }
        Stone stoneHere = branchOpt.map(b -> b.data.stones[here]).orElse(board.getStones()[here]);
        int mvNum = moveNumberList[Board.getIndex(i, j)];
        // don't write the move number if either: the move number is 0, or there will
        // already be
        // playout information written
        if ((mvNum > 0 || Lizzie.frame.isTrying && mvNum < 0)
            && (!branchOpt.isPresent() || !Lizzie.frame.isMouseOver(i, j))) {
          boolean reverse = (moveNumberList[Board.getIndex(i, j)] > maxBranchMoves());
          if ((lastMoveOpt.isPresent() && lastMoveOpt.get()[0] == i && lastMoveOpt.get()[1] == j)) {
            if (reverse) continue;
            g.setColor(Color.RED.brighter()); // stoneHere.isBlack() ? Color.RED.brighter() :
            // Color.BLUE.brighter());
          } else {
            // Draw white letters on black stones nomally.
            // But use black letters for showing black moves without stones.
            if (reverse) continue;
            g.setColor(stoneHere.isBlack() ^ reverse ? Color.WHITE : Color.BLACK);
          }

          String moveNumberString = mvNum + "";
          if (Lizzie.config.showMoveNumberFromOne && Lizzie.config.allowMoveNumber > 0) {
            if (lastMoveNumber > Lizzie.config.allowMoveNumber)
              moveNumberString = mvNum - (lastMoveNumber - Lizzie.config.allowMoveNumber) + "";
          }
          if (Lizzie.frame.isTrying) {
            if (mvNum < 0) {
              moveNumberString = -mvNum + "";
              drawString(
                  g,
                  stoneX,
                  stoneY,
                  LizzieFrame.uiFont,
                  moveNumberString,
                  (float) (stoneRadius * 1.4),
                  (int) (stoneRadius * 1.4));
            }

          } else if (mvNum >= 100) {
            drawString(
                g,
                stoneX,
                stoneY,
                LizzieFrame.uiFont,
                moveNumberString,
                (float) (stoneRadius * 1.7),
                (int) (stoneRadius * 1.7));
          } else {
            drawString(
                g,
                stoneX,
                stoneY,
                LizzieFrame.uiFont,
                moveNumberString,
                (float) (stoneRadius * 1.4),
                (int) (stoneRadius * 1.4));
          }
        }
      }
    }
  }

  /**
   * Draw all of Leelaz's suggestions as colored stones with winrate/playout statistics overlayed
   */
  private void drawLeelazSuggestions(Graphics2D g) {
    int minAlpha = 32;
    float winrateHueFactor = 0.9f;
    float alphaFactor = 5.0f;
    float redHue = Color.RGBtoHSB(255, 0, 0, null)[0];
    float greenHue = Color.RGBtoHSB(0, 255, 0, null)[0];
    float cyanHue = Lizzie.config.bestMoveColor;

    if (Lizzie.frame.isheatmap && !Lizzie.leelaz.getBestMoves().isEmpty()) {
      Double maxPolicy = 0.0;
      int minPolicy = 0;
      for (int n = 0; n < Lizzie.leelaz.getBestMoves().size(); n++) {
        if (Lizzie.leelaz.getBestMoves().get(n).policy > maxPolicy)
          maxPolicy = Lizzie.leelaz.getBestMoves().get(n).policy;
      }
      for (int i = 0; i < Lizzie.leelaz.getBestMoves().size(); i++) {
        MoveData bestmove = Lizzie.leelaz.getBestMoves().get(i);
        int y1 = 0;
        int x1 = 0;
        Optional<int[]> coord = Board.asCoordinates(bestmove.coordinate);
        if (coord.isPresent()) {
          x1 = coord.get()[0];
          y1 = coord.get()[1];

          int suggestionX = x + scaledMarginWidth + squareWidth * x1;
          int suggestionY = y + scaledMarginHeight + squareHeight * y1;
          double percent = bestmove.policy / maxPolicy;

          // g.setColor(Color.BLACK);
          // g.fillRect(stoneX - stoneRadius / 2, stoneY - stoneRadius / 2, stoneRadius,
          // stoneRadius);

          float hue;
          if (bestmove.policy == maxPolicy) {
            hue = cyanHue;
          } else {
            double fraction;

            fraction = percent;

            // Correction to make differences between colors more perceptually linear
            fraction *= 2;
            if (fraction < 1) { // red to yellow
              fraction = Math.cbrt(fraction * fraction) / 2;
            } else { // yellow to green
              fraction = 1 - Math.sqrt(2 - fraction) / 2;
            }

            hue = redHue + (greenHue - redHue) * (float) fraction;
          }

          float saturation = 1.0f;
          float brightness = 0.85f;
          float alpha =
              minAlpha + (maxAlpha - minAlpha) * max(0, (float) log(percent) / alphaFactor + 1);

          Color hsbColor = Color.getHSBColor(hue, saturation, brightness);
          Color color =
              new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), (int) alpha);
          if (!branchOpt.isPresent()) {
            drawShadow2(g, suggestionX, suggestionY, true, alpha / 255.0f);
            g.setColor(color);
            fillCircle(g, suggestionX, suggestionY, stoneRadius);

            String text =
                String.format("%.1f", ((double) Lizzie.leelaz.getBestMoves().get(i).policy));
            g.setColor(Color.WHITE);
            drawString(
                g,
                suggestionX,
                suggestionY,
                LizzieFrame.winrateFont,
                Font.PLAIN,
                text,
                stoneRadius,
                stoneRadius * 1.9,
                0);
          }
        }
      }

    } else {
      if (bestMoves != null && !bestMoves.isEmpty()) {

        // Collections.sort(bestMoves);

        int maxPlayouts = 0;
        double maxWinrate = 0;
        double minWinrate = 100.0;
        double maxScoreMean = -300;
        List<MoveData> tempbest1 = new ArrayList();
        List<MoveData> tempbest2 = new ArrayList();
        for (int i = 0; i < bestMoves.size(); i++) {
          tempbest1.add(bestMoves.get(i)); // 开始复制一个list的内容到另外一个list
          tempbest2.add(bestMoves.get(i));
        }
        Collections.sort(
            tempbest1,
            new Comparator<MoveData>() {

              @Override
              public int compare(MoveData s1, MoveData s2) {
                // 降序
                if (s1.lcb < s2.lcb) return 1;
                if (s1.lcb > s2.lcb) return -1;
                else return 0;
              }
            });

        Collections.sort(
            tempbest2,
            new Comparator<MoveData>() {

              @Override
              public int compare(MoveData s1, MoveData s2) {
                // 降序
                if (s1.playouts < s2.playouts) return 1;
                if (s1.playouts > s2.playouts) return -1;
                else return 0;
              }
            });
        if (Lizzie.config.leelaversion >= 17 && Lizzie.config.showlcbcolor) {
          for (int i = 0; i < tempbest1.size(); i++) {
            tempbest1.get(i).equalplayouts = tempbest2.get(i).playouts;
          }
        }
        for (MoveData move : bestMoves) {
          if (move.playouts > maxPlayouts) maxPlayouts = move.playouts;
          if (move.winrate > maxWinrate) maxWinrate = move.winrate;
          if (move.winrate < minWinrate) minWinrate = move.winrate;
          if ((Lizzie.leelaz.isKatago || Lizzie.board.isKataBoard)
              && Lizzie.config.showScoremeanInSuggestion) {
            if (move.scoreMean > maxScoreMean) maxScoreMean = move.scoreMean;
          }
        }

        for (int i = 0; i < Board.boardWidth; i++) {
          for (int j = 0; j < Board.boardHeight; j++) {
            Optional<MoveData> moveOpt = Optional.empty();

            // This is inefficient but it looks better with shadows
            number = 1;
            //    if (Lizzie.frame.toolbar.isEnginePk && Lizzie.frame.toolbar.isGenmove) {
            for (MoveData m : bestMoves) {
              Optional<int[]> coord = Board.asCoordinates(m.coordinate);
              if (coord.isPresent()) {
                int[] c = coord.get();
                if (c[0] == i && c[1] == j) {
                  moveOpt = Optional.of(m);
                  break;
                }
                number = number + 1;
              }
            }

            for (MoveData m : tempbest1) {
              Optional<int[]> coord = Board.asCoordinates(m.coordinate);
              if (coord.isPresent()) {
                int[] c = coord.get();
                if (c[0] == i && c[1] == j) {
                  moveOpt = Optional.of(m);
                  break;
                }
                // number = number + 1;
              }
            }
            //            } else {

            //            }

            if (!moveOpt.isPresent()) {
              continue;
            }
            MoveData move = moveOpt.get();

            boolean isBestMove = bestMoves.get(0) == move;
            boolean hasMaxWinrate = move.winrate == maxWinrate;
            boolean flipWinrate =
                uiConfig.getBoolean("win-rate-always-black") && !Lizzie.board.getData().blackToPlay;

            if (move.playouts == 0) {
              continue; // This actually can happen
            }

            float percentPlayouts =
                (Lizzie.config.leelaversion >= 17 && Lizzie.config.showlcbcolor)
                    ? (float) move.equalplayouts / maxPlayouts
                    : (float) move.playouts / maxPlayouts;
            double percentWinrate =
                Math.min(
                    1,
                    Math.max(0.01, move.winrate - minWinrate)
                        / Math.max(0.01, maxWinrate - minWinrate));

            Optional<int[]> coordsOpt = Board.asCoordinates(move.coordinate);
            if (!coordsOpt.isPresent()) {
              continue;
            }
            int[] coords = coordsOpt.get();

            int suggestionX = x + scaledMarginWidth + squareWidth * coords[0];
            int suggestionY = y + scaledMarginHeight + squareHeight * coords[1];

            float hue;
            if (isBestMove) {
              hue = cyanHue;
            } else {
              double fraction;

              fraction = percentPlayouts;

              // Correction to make differences between colors more perceptually linear
              fraction *= 2;
              if (fraction < 1) { // red to yellow
                fraction = Math.cbrt(fraction * fraction) / 2;
              } else { // yellow to green
                fraction = 1 - Math.sqrt(2 - fraction) / 2;
              }

              hue = redHue + (greenHue - redHue) * (float) fraction;
            }

            float saturation = 1.0f;
            float brightness = 0.85f;
            float alpha =
                minAlpha
                    + (maxAlpha - minAlpha)
                        * max(
                            0,
                            (float)
                                        log(
                                            Lizzie.config.colorByWinrateInsteadOfVisits
                                                ? percentWinrate
                                                : percentPlayouts)
                                    / alphaFactor
                                + 1);

            Color hsbColor = Color.getHSBColor(hue, saturation, brightness);
            Color color =
                new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), (int) alpha);

            boolean isMouseOver = Lizzie.frame.isMouseOver(coords[0], coords[1]);
            if (!branchOpt.isPresent()) {
              drawShadow2(g, suggestionX, suggestionY, true, alpha / 255.0f);
              g.setColor(color);
              fillCircle(g, suggestionX, suggestionY, stoneRadius);
            }

            // boolean ringedMove =
            // !Lizzie.config.colorByWinrateInsteadOfVisits && (isBestMove ||
            // hasMaxWinrate);
            // if (!branchOpt.isPresent() || (ringedMove && isMouseOver)) {

            if (!branchOpt.isPresent() || (isMouseOver)) {
              int strokeWidth = 1;
              // if (ringedMove) {
              // strokeWidth = 2;
              // if (isBestMove) {
              // if (hasMaxWinrate) {
              // g.setColor(color.darker());
              // strokeWidth = 1;
              // } else {
              // g.setColor(maxColor);
              // }
              // } else {
              // g.setColor(Color.BLUE);
              // }
              // } else {
              g.setColor(color.darker());
              // }
              g.setStroke(new BasicStroke(strokeWidth));
              if (coords[0] == Lizzie.frame.suggestionclick[0]
                  && coords[1] == Lizzie.frame.suggestionclick[1]) {
                g.setColor(color.magenta);
                drawCircle3(g, suggestionX, suggestionY, stoneRadius - strokeWidth / 2);
              } else {
                // drawCircle4(g, suggestionX, suggestionY, stoneRadius - strokeWidth / 2);
              }
              g.setStroke(new BasicStroke(1));
            }

            if (!branchOpt.isPresent()
                    && (hasMaxWinrate
                        || percentPlayouts >= uiConfig.getDouble("min-playout-ratio-for-stats"))
                || isMouseOver) {
              double roundedWinrate = round(move.winrate * 10) / 10.0;
              if (flipWinrate) {
                roundedWinrate = 100.0 - roundedWinrate;
              }
              g.setColor(Color.BLACK);
              if (branchOpt.isPresent() && Lizzie.board.getData().blackToPlay)
                g.setColor(Color.WHITE);

              String text;
              if (Lizzie.config.handicapInsteadOfWinrate) {
                text = String.format("%.2f", Lizzie.leelaz.winrateToHandicap(move.winrate));
              } else {
                text = String.format("%.1f", roundedWinrate);
              }
              if (Lizzie.config.showSuggestionOrder && number < 10) {
                Color oriColor = g.getColor();
                g.setColor(Color.ORANGE);
                g.fillRect(
                    (int) (suggestionX + stoneRadius * 0.65),
                    (int) (suggestionY - stoneRadius * 1.3),
                    (int) (stoneRadius * 0.58),
                    (int) (stoneRadius * 0.6));
                g.setColor(Color.BLACK);
                drawString(
                    g,
                    (int) (suggestionX + stoneRadius * 0.9),
                    suggestionY - stoneRadius * 7 / 9,
                    LizzieFrame.winrateFont,
                    Font.PLAIN,
                    number + "",
                    (float) (stoneRadius * 0.8),
                    stoneRadius * 0.8,
                    1);
                g.setColor(oriColor);
              }

              // number++;
              Color maxColor = reverseColor(hsbColor, (int) alpha);
              if ((Lizzie.leelaz.isKatago || Lizzie.board.isKataBoard)
                  && Lizzie.config.showScoremeanInSuggestion) {
                if (!Lizzie.config.showWinrateInSuggestion) {
                  double score = move.scoreMean;
                  if (Lizzie.board.getHistory().isBlacksTurn()) {
                    if (Lizzie.config.showKataGoBoardScoreMean) {
                      score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                    }
                  } else {
                    if (Lizzie.config.showKataGoBoardScoreMean) {
                      score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
                    }
                    if (Lizzie.config.kataGoScoreMeanAlwaysBlack) {
                      score = -score;
                    }
                  }
                  Color oriColor = g.getColor();
                  if (Lizzie.config.showPlayoutsInSuggestion) {
                    if (Lizzie.config.showSuggestionMaxRed && move.scoreMean == maxScoreMean)
                      g.setColor(maxColor);
                    drawString(
                        g,
                        suggestionX,
                        suggestionY - stoneRadius * 1 / 9,
                        LizzieFrame.winrateFont,
                        Font.PLAIN,
                        Lizzie.frame.getPlayoutsString(move.playouts),
                        stoneRadius,
                        stoneRadius * 1.6,
                        1);
                    if (Lizzie.config.showSuggestionMaxRed) g.setColor(oriColor);
                    if (Lizzie.config.showSuggestionMaxRed && move.playouts == maxPlayouts)
                      g.setColor(maxColor);
                    drawString(
                        g,
                        suggestionX,
                        suggestionY + stoneRadius * 4 / 9,
                        LizzieFrame.winrateFont,
                        String.format("%.1f", score),
                        (float) (stoneRadius * 1.0),
                        stoneRadius * 1.6);
                  } else {
                    if (Lizzie.config.showSuggestionMaxRed && move.scoreMean == maxScoreMean)
                      g.setColor(maxColor);
                    drawString(
                        g,
                        suggestionX,
                        suggestionY,
                        LizzieFrame.winrateFont,
                        String.format("%.1f", score),
                        (float) (stoneRadius * 2),
                        stoneRadius * 2);
                    if (Lizzie.config.showSuggestionMaxRed) g.setColor(oriColor);
                  }
                  if (Lizzie.config.showSuggestionMaxRed) g.setColor(oriColor);
                } else {

                  Color oriColor = g.getColor();
                  if (Lizzie.config.showPlayoutsInSuggestion) {
                    if (Lizzie.config.showSuggestionMaxRed && hasMaxWinrate) g.setColor(maxColor);
                    if (roundedWinrate < 10) {
                      drawString(
                          g,
                          suggestionX,
                          suggestionY - stoneRadius * 6 / 16,
                          LizzieFrame.winrateFont,
                          Font.PLAIN,
                          text,
                          (float) (stoneRadius * 0.8),
                          stoneRadius * 1.15,
                          1);
                    } else {
                      drawString(
                          g,
                          suggestionX,
                          suggestionY - stoneRadius * 6 / 16,
                          LizzieFrame.winrateFont,
                          Font.PLAIN,
                          text,
                          stoneRadius,
                          stoneRadius * 1.45,
                          1);
                    }
                    if (Lizzie.config.showSuggestionMaxRed) g.setColor(oriColor);
                    if (Lizzie.config.showSuggestionMaxRed && move.playouts == maxPlayouts)
                      g.setColor(maxColor);
                    drawString(
                        g,
                        suggestionX,
                        suggestionY + stoneRadius * 1 / 16,
                        LizzieFrame.winrateFont,
                        Lizzie.frame.getPlayoutsString(move.playouts),
                        (float) (stoneRadius * 0.8),
                        stoneRadius * 1.4);
                    if (Lizzie.config.showSuggestionMaxRed) g.setColor(oriColor);
                    double score = move.scoreMean;
                    if (Lizzie.board.getHistory().isBlacksTurn()) {
                      if (Lizzie.config.showKataGoBoardScoreMean) {
                        score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                      }
                    } else {
                      if (Lizzie.config.showKataGoBoardScoreMean) {
                        score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
                      }
                      if (Lizzie.config.kataGoScoreMeanAlwaysBlack) {
                        score = -score;
                      }
                    }
                    if (Lizzie.config.showSuggestionMaxRed && move.scoreMean == maxScoreMean)
                      g.setColor(maxColor);
                    drawString(
                        g,
                        suggestionX,
                        suggestionY + stoneRadius * 12 / 16,
                        LizzieFrame.winrateFont,
                        String.format("%.1f", score),
                        (float) (stoneRadius * 0.75),
                        stoneRadius * 1.3);
                    if (Lizzie.config.showSuggestionMaxRed) g.setColor(oriColor);
                  } else {
                    double score = move.scoreMean;
                    if (Lizzie.board.getHistory().isBlacksTurn()) {
                      if (Lizzie.config.showKataGoBoardScoreMean) {
                        score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                      }
                    } else {
                      if (Lizzie.config.showKataGoBoardScoreMean) {
                        score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
                      }
                      if (Lizzie.config.kataGoScoreMeanAlwaysBlack) {
                        score = -score;
                      }
                    }
                    if (Lizzie.config.showSuggestionMaxRed && move.scoreMean == maxScoreMean)
                      g.setColor(maxColor);
                    drawString(
                        g,
                        suggestionX,
                        suggestionY - stoneRadius * 1 / 9,
                        LizzieFrame.winrateFont,
                        Font.PLAIN,
                        text,
                        stoneRadius,
                        stoneRadius * 1.6,
                        1);
                    if (Lizzie.config.showSuggestionMaxRed) g.setColor(oriColor);
                    if (Lizzie.config.showSuggestionMaxRed && move.playouts == maxPlayouts)
                      g.setColor(maxColor);
                    drawString(
                        g,
                        suggestionX,
                        suggestionY + stoneRadius * 4 / 9,
                        LizzieFrame.winrateFont,
                        String.format("%.1f", score),
                        (float) (stoneRadius * 1.0),
                        stoneRadius * 1.6);
                  }
                }

              } else {
                Color oriColor = g.getColor();
                if (Lizzie.config.showWinrateInSuggestion) {

                  if (Lizzie.config.showSuggestionMaxRed && hasMaxWinrate) g.setColor(maxColor);
                  if (Lizzie.config.showPlayoutsInSuggestion) {
                    drawString(
                        g,
                        suggestionX,
                        suggestionY - stoneRadius * 1 / 9,
                        LizzieFrame.winrateFont,
                        Font.PLAIN,
                        text,
                        stoneRadius,
                        stoneRadius * 1.6,
                        1);
                  } else {
                    drawString(
                        g,
                        suggestionX,
                        suggestionY,
                        LizzieFrame.winrateFont,
                        text,
                        (float) (stoneRadius * 2),
                        stoneRadius * 2);
                  }
                }
                if (Lizzie.config.showPlayoutsInSuggestion) {
                  if (Lizzie.config.showSuggestionMaxRed) g.setColor(oriColor);
                  if (Lizzie.config.showSuggestionMaxRed && move.playouts == maxPlayouts)
                    g.setColor(maxColor);
                  if (Lizzie.config.showWinrateInSuggestion) {
                    drawString(
                        g,
                        suggestionX,
                        suggestionY + stoneRadius * 4 / 9,
                        LizzieFrame.winrateFont,
                        Lizzie.frame.getPlayoutsString(move.playouts),
                        (float) (stoneRadius * 1.0),
                        stoneRadius * 1.6);
                  } else {
                    drawString(
                        g,
                        suggestionX,
                        suggestionY,
                        LizzieFrame.winrateFont,
                        Lizzie.frame.getPlayoutsString(move.playouts),
                        (float) (stoneRadius * 2),
                        stoneRadius * 2);
                  }
                  if (Lizzie.config.showSuggestionMaxRed) g.setColor(oriColor);
                }
              }
            }
          }
        }
      }
    }
  }

  private void drawNextMoves(Graphics2D g) {

    g.setColor(Lizzie.board.getData().blackToPlay ? Color.BLACK : Color.WHITE);

    List<BoardHistoryNode> nexts = Lizzie.board.getHistory().getNexts();

    for (int i = 0; i < nexts.size(); i++) {
      boolean first = (i == 0);
      nexts
          .get(i)
          .getData()
          .lastMove
          .ifPresent(
              nextMove -> {
                int moveX = x + scaledMarginWidth + squareWidth * nextMove[0];
                int moveY = y + scaledMarginHeight + squareHeight * nextMove[1];
                if (first)
                  g.setStroke(
                      Lizzie.board.getData().blackToPlay
                          ? new BasicStroke(2.5f)
                          : new BasicStroke(3.0f));
                drawCircle(g, moveX, moveY, stoneRadius + 2); // Slightly outside best move circle
                if (first) g.setStroke(new BasicStroke(1.8f));
              });
    }
  }

  private Color reverseColor(Color color, int alpha) {
    // System.out.println("color=="+color);
    int r = color.getRed();
    int g = color.getGreen();
    int b = color.getBlue();
    int r_ = 255 - r;
    int g_ = (255 - g) * 400 / alpha;
    if (g_ > 255) g_ = 255;
    int b_ = (255 - b) * 400 / alpha;
    if (b_ > 255) b_ = 255;
    Color newColor = new Color(r_, g_, b_);

    // System.out.println("newColor=="+newColor);
    return newColor;
  }

  private void drawWoodenBoard(Graphics2D g) {
    if (uiConfig.getBoolean("fancy-board")) {
      // fancy version
      if (cachedBoardImage == emptyImage) {
        cachedBoardImage = Lizzie.config.theme.board();
      }

      drawTextureImage(
          g,
          cachedBoardImage,
          x - 2 * shadowRadius,
          y - 2 * shadowRadius,
          boardWidth + 4 * shadowRadius,
          boardHeight + 4 * shadowRadius);

      if (Lizzie.config.showBorder) {
        g.setStroke(new BasicStroke(shadowRadius * 2));
        // draw border
        g.setColor(new Color(0, 0, 0, 50));
        g.drawRect(
            x - shadowRadius,
            y - shadowRadius,
            boardWidth + 2 * shadowRadius,
            boardHeight + 2 * shadowRadius);
      }
      g.setStroke(new BasicStroke(1));

    } else {
      // simple version
      JSONArray boardColor = uiConfig.getJSONArray("board-color");
      g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
      g.setColor(new Color(boardColor.getInt(0), boardColor.getInt(1), boardColor.getInt(2)));
      g.fillRect(x, y, boardWidth, boardHeight);
    }
  }

  /**
   * Calculates the lengths and pixel margins from a given boardLength.
   *
   * @param boardLength go board's length in pixels; must be boardLength >= BOARD_SIZE - 1
   * @return an array containing the three outputs: new boardLength, scaledMargin, availableLength
   */
  private static int[] calculatePixelMargins(
      int boardWidth, int boardHeight, boolean showCoordinates) {
    // boardLength -= boardLength*MARGIN/3; // account for the shadows we will draw
    // around the edge
    // of the board
    // if (boardLength < Board.BOARD_SIZE - 1)
    // throw new IllegalArgumentException("boardLength may not be less than " +
    // (Board.BOARD_SIZE - 1) + ", but was " + boardLength);

    int scaledMarginWidth;
    int availableWidth;
    int scaledMarginHeight;
    int availableHeight;
    if (Board.boardWidth == Board.boardHeight) {
      boardWidth = min(boardWidth, boardHeight);
    }

    // decrease boardLength until the availableLength will result in square board
    // intersections
    double marginWidth =
        (Board.boardWidth < 3
                ? 0.04
                : (Lizzie.config.showNameInBoard && !emptyName
                    ? 0.055
                    : showCoordinates ? 0.045 : 0.025))
            / Board.boardWidth
            * 19.0;
    boardWidth++;
    do {
      boardWidth--;
      scaledMarginWidth = (int) (marginWidth * boardWidth);
      availableWidth = boardWidth - 2 * scaledMarginWidth;
    } while (!((availableWidth - 1) % (Board.boardWidth - 1) == 0));
    // this will be true if BOARD_SIZE - 1 square intersections, plus one line, will
    // fit
    int squareWidth = 0;
    int squareHeight = 0;
    if (Board.boardWidth != Board.boardHeight) {
      double marginHeight =
          (Board.boardWidth < 3
                  ? 0.04
                  : (Lizzie.config.showNameInBoard && !emptyName
                          ? 0.055
                          : showCoordinates ? 0.045 : 0.03)
                      / Board.boardHeight)
              * 19.0;
      boardHeight++;
      do {
        boardHeight--;
        scaledMarginHeight = (int) (marginHeight * boardHeight);
        availableHeight = boardHeight - 2 * scaledMarginHeight;
      } while (!((availableHeight - 1) % (Board.boardHeight - 1) == 0));
      squareWidth = calculateSquareWidth(availableWidth);
      squareHeight = calculateSquareHeight(availableHeight);
      if (squareWidth > squareHeight) {
        squareWidth = squareHeight;
        int newWidth = squareWidth * (Board.boardWidth - 1) + 1;
        int diff = availableWidth - newWidth;
        availableWidth = newWidth;
        boardWidth -= diff + (scaledMarginWidth - scaledMarginHeight) * 2;
        scaledMarginWidth = scaledMarginHeight;
      } else if (squareWidth < squareHeight) {
        squareHeight = squareWidth;
        int newHeight = squareHeight * (Board.boardHeight - 1) + 1;
        int diff = availableHeight - newHeight;
        availableHeight = newHeight;
        boardHeight -= diff + (scaledMarginHeight - scaledMarginWidth) * 2;
        scaledMarginHeight = scaledMarginWidth;
      }
    } else {
      boardHeight = boardWidth;
      scaledMarginHeight = scaledMarginWidth;
      availableHeight = availableWidth;
    }
    return new int[] {
      boardWidth,
      scaledMarginWidth,
      availableWidth,
      boardHeight,
      scaledMarginHeight,
      availableHeight
    };
  }

  private void drawShadow(Graphics2D g, int centerX, int centerY, boolean isGhost) {
    drawShadow(g, centerX, centerY, isGhost, 1);
  }

  private void drawShadow2(
      Graphics2D g, int centerX, int centerY, boolean isGhost, float shadowStrength) {
    if (!uiConfig.getBoolean("shadows-enabled")) return;

    double r = stoneRadius * 70 / 100;
    final int shadowSize = (int) (r * 0.2) == 0 ? 1 : (int) (r * 0.2);
    final int fartherShadowSize = (int) (r * 0.17) == 0 ? 1 : (int) (r * 0.17);

    final Paint TOP_GRADIENT_PAINT;
    final Paint LOWER_RIGHT_GRADIENT_PAINT;

    if (isGhost) {
      TOP_GRADIENT_PAINT =
          new RadialGradientPaint(
              new Point2D.Float(centerX, centerY),
              stoneRadius + shadowSize,
              new float[] {
                ((float) stoneRadius / (stoneRadius + shadowSize)) - 0.0001f,
                ((float) stoneRadius / (stoneRadius + shadowSize)),
                1.0f
              },
              new Color[] {
                new Color(0, 0, 0, 0),
                new Color(50, 50, 50, (int) (120 * shadowStrength)),
                new Color(0, 0, 0, 0)
              });

      LOWER_RIGHT_GRADIENT_PAINT =
          new RadialGradientPaint(
              new Point2D.Float(centerX + shadowSize * 2 / 3, centerY + shadowSize * 2 / 3),
              stoneRadius + fartherShadowSize,
              new float[] {0.6f, 1.0f},
              new Color[] {new Color(0, 0, 0, 180), new Color(0, 0, 0, 0)});
    } else {
      TOP_GRADIENT_PAINT =
          new RadialGradientPaint(
              new Point2D.Float(centerX, centerY),
              stoneRadius + shadowSize,
              new float[] {0.3f, 1.0f},
              new Color[] {new Color(50, 50, 50, 150), new Color(0, 0, 0, 0)});
      LOWER_RIGHT_GRADIENT_PAINT =
          new RadialGradientPaint(
              new Point2D.Float(centerX + shadowSize, centerY + shadowSize),
              stoneRadius + fartherShadowSize,
              new float[] {0.6f, 1.0f},
              new Color[] {new Color(0, 0, 0, 140), new Color(0, 0, 0, 0)});
    }

    final Paint originalPaint = g.getPaint();

    g.setPaint(TOP_GRADIENT_PAINT);
    fillCircle(g, centerX, centerY, stoneRadius + shadowSize);
    if (!isGhost) {
      g.setPaint(LOWER_RIGHT_GRADIENT_PAINT);
      fillCircle(g, centerX + shadowSize, centerY + shadowSize, stoneRadius + fartherShadowSize);
    }
    g.setPaint(originalPaint);
  }

  private void drawShadow(
      Graphics2D g, int centerX, int centerY, boolean isGhost, float shadowStrength) {
    if (!uiConfig.getBoolean("shadows-enabled")) return;

    double r = stoneRadius * Lizzie.config.shadowSize / 100;
    final int shadowSize = (int) (r * 0.2) == 0 ? 1 : (int) (r * 0.2);
    final int fartherShadowSize = (int) (r * 0.17) == 0 ? 1 : (int) (r * 0.17);

    final Paint TOP_GRADIENT_PAINT;
    final Paint LOWER_RIGHT_GRADIENT_PAINT;

    if (isGhost) {
      TOP_GRADIENT_PAINT =
          new RadialGradientPaint(
              new Point2D.Float(centerX, centerY),
              stoneRadius + shadowSize,
              new float[] {
                ((float) stoneRadius / (stoneRadius + shadowSize)) - 0.0001f,
                ((float) stoneRadius / (stoneRadius + shadowSize)),
                1.0f
              },
              new Color[] {
                new Color(0, 0, 0, 0),
                new Color(50, 50, 50, (int) (120 * shadowStrength)),
                new Color(0, 0, 0, 0)
              });

      LOWER_RIGHT_GRADIENT_PAINT =
          new RadialGradientPaint(
              new Point2D.Float(centerX + shadowSize * 2 / 3, centerY + shadowSize * 2 / 3),
              stoneRadius + fartherShadowSize,
              new float[] {0.6f, 1.0f},
              new Color[] {new Color(0, 0, 0, 180), new Color(0, 0, 0, 0)});
    } else {
      TOP_GRADIENT_PAINT =
          new RadialGradientPaint(
              new Point2D.Float(centerX, centerY),
              stoneRadius + shadowSize,
              new float[] {0.3f, 1.0f},
              new Color[] {new Color(50, 50, 50, 150), new Color(0, 0, 0, 0)});
      LOWER_RIGHT_GRADIENT_PAINT =
          new RadialGradientPaint(
              new Point2D.Float(centerX + shadowSize, centerY + shadowSize),
              stoneRadius + fartherShadowSize,
              new float[] {0.6f, 1.0f},
              new Color[] {new Color(0, 0, 0, 140), new Color(0, 0, 0, 0)});
    }

    final Paint originalPaint = g.getPaint();

    g.setPaint(TOP_GRADIENT_PAINT);
    fillCircle(g, centerX, centerY, stoneRadius + shadowSize);
    if (!isGhost) {
      g.setPaint(LOWER_RIGHT_GRADIENT_PAINT);
      fillCircle(g, centerX + shadowSize, centerY + shadowSize, stoneRadius + fartherShadowSize);
    }
    g.setPaint(originalPaint);
  }

  /** Draws a stone centered at (centerX, centerY) */
  private void drawStone(
      Graphics2D g, Graphics2D gShadow, int centerX, int centerY, Stone color, int x, int y) {
    // g.setRenderingHint(KEY_ALPHA_INTERPOLATION,
    // VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

    if (color.isBlack() || color.isWhite()) {
      boolean isBlack = color.isBlack();
      boolean isGhost = (color == Stone.BLACK_GHOST || color == Stone.WHITE_GHOST);
      if (uiConfig.getBoolean("fancy-stones")) {
        // 需要恢复的
        // if (false) {
        drawShadow(gShadow, centerX, centerY, isGhost);
        int size = stoneRadius * 2 + 1;
        g.drawImage(
            getScaleStone(isBlack, size),
            centerX - stoneRadius,
            centerY - stoneRadius,
            size,
            size,
            null);
      } else {
        // 需要恢复的
        drawShadow(gShadow, centerX, centerY, true);
        Color blackColor = isGhost ? new Color(0, 0, 0) : Color.BLACK;
        Color whiteColor = isGhost ? new Color(255, 255, 255) : Color.WHITE;
        g.setColor(isBlack ? blackColor : whiteColor);
        fillCircle(g, centerX, centerY, stoneRadius);
        if (!isBlack) {
          g.setColor(blackColor);
          drawCircle(g, centerX, centerY, stoneRadius);
        }
      }
    }
  }

  /** Get scaled stone, if cached then return cached */
  private BufferedImage getScaleStone(boolean isBlack, int size) {
    BufferedImage stoneImage = isBlack ? cachedBlackStoneImage : cachedWhiteStoneImage;
    if (stoneImage.getWidth() != size || stoneImage.getHeight() != size) {
      stoneImage = new BufferedImage(size, size, TYPE_INT_ARGB);
      Image img = isBlack ? Lizzie.config.theme.blackStone() : Lizzie.config.theme.whiteStone();
      Graphics2D g2 = stoneImage.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g2.drawImage(img.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
      g2.dispose();
      if (isBlack) {
        cachedBlackStoneImage = stoneImage;
      } else {
        cachedWhiteStoneImage = stoneImage;
      }
    }
    return stoneImage;
  }

  public BufferedImage getWallpaper() {
    if (cachedWallpaperImage == emptyImage) {
      cachedWallpaperImage = Lizzie.config.theme.background();
    }
    return cachedWallpaperImage;
  }

  /**
   * Draw scale smooth image, enhanced display quality (Not use, for future) This function use the
   * traditional Image.getScaledInstance() method to provide the nice quality, but the performance
   * is poor. Recommended for use in a few drawings
   */
  // public void drawScaleSmoothImage(Graphics2D g, BufferedImage img, int x, int
  // y, int width,
  // int height, ImageObserver observer) {
  // BufferedImage newstone = new BufferedImage(width, height, TYPE_INT_ARGB);
  // Graphics2D g2 = newstone.createGraphics();
  // g2.drawImage(img.getScaledInstance(width, height,
  // java.awt.Image.SCALE_SMOOTH), 0, 0,
  // observer);
  // g2.dispose();
  // g.drawImage(newstone, x, y, width, height, observer);
  // }

  /**
   * Draw scale smooth image, enhanced display quality (Not use, for future) This functions use a
   * multi-step approach to prevent the information loss and produces a much higher quality that is
   * close to the Image.getScaledInstance() and faster than Image.getScaledInstance() method.
   */
  // public void drawScaleImage(Graphics2D g, BufferedImage img, int x, int y, int
  // width, int
  // height, ImageObserver observer) {
  // BufferedImage newstone = (BufferedImage)img;
  // int w = img.getWidth();
  // int h = img.getHeight();
  // do {
  // if (w > width) {
  // w /= 2;
  // if (w < width) {
  // w = width;
  // }
  // }
  // if (h > height) {
  // h /= 2;
  // if (h < height) {
  // h = height;
  // }
  // }
  // BufferedImage tmp = new BufferedImage(w, h, TYPE_INT_ARGB);
  // Graphics2D g2 = tmp.createGraphics();
  // g2.setRenderingHint(KEY_INTERPOLATION,
  // VALUE_INTERPOLATION_BICUBIC);
  // g2.drawImage(newstone, 0, 0, w, h, null);
  // g2.dispose();
  // newstone = tmp;
  // }
  // while (w != width || h != height);
  // g.drawImage(newstone, x, y, width, height, observer);
  // }

  /** Draw texture image */
  public void drawTextureImage(
      Graphics2D g, BufferedImage img, int x, int y, int width, int height) {
    paint = new TexturePaint(img, new Rectangle(0, 0, img.getWidth(), img.getHeight()));
    g.setPaint(paint);
    g.fill(new Rectangle(x, y, width, height));
  }

  /**
   * Draw stone Markups
   *
   * @param g
   */
  private void drawStoneMarkup(Graphics2D g) {

    BoardData data = Lizzie.board.getHistory().getData();

    data.getProperties()
        .forEach(
            (key, value) -> {
              if (SGFParser.isListProperty(key)) {
                String[] labels = value.split(",");
                for (String label : labels) {
                  String[] moves = label.split(":");
                  int[] move = SGFParser.convertSgfPosToCoord(moves[0]);
                  if (move != null) {
                    Optional<int[]> lastMove =
                        branchOpt.map(b -> b.data.lastMove).orElse(Lizzie.board.getLastMove());
                    if (lastMove.map(m -> !Arrays.equals(move, m)).orElse(true)) {
                      int moveX = x + scaledMarginWidth + squareWidth * move[0];
                      int moveY = y + scaledMarginHeight + squareHeight * move[1];
                      g.setColor(
                          Lizzie.board.getStones()[Board.getIndex(move[0], move[1])].isBlack()
                              ? Color.WHITE
                              : Color.BLACK);
                      g.setStroke(new BasicStroke(2));
                      if ("LB".equals(key) && moves.length > 1) {
                        // Label
                        double labelRadius = stoneRadius * 1.4;
                        drawString(
                            g,
                            moveX,
                            moveY,
                            LizzieFrame.uiFont,
                            moves[1],
                            (float) labelRadius,
                            labelRadius);
                      } else if ("TR".equals(key)) {
                        drawTriangle(g, moveX, moveY, (stoneRadius + 1) * 2 / 3);
                      } else if ("SQ".equals(key)) {
                        drawSquare(g, moveX, moveY, (stoneRadius + 1) / 2);
                      } else if ("CR".equals(key)) {
                        drawCircle(g, moveX, moveY, stoneRadius * 2 / 3);
                      } else if ("MA".equals(key)) {
                        drawMarkX(g, moveX, moveY, (stoneRadius + 1) / 2);
                      }
                    }
                  }
                }
              }
            });
  }

  /** Draws the triangle of a circle centered at (centerX, centerY) with radius $radius$ */
  private void drawTriangle(Graphics2D g, int centerX, int centerY, int radius) {
    int offset = (int) (3.0 / 2.0 * radius / Math.sqrt(3.0));
    int x[] = {centerX, centerX - offset, centerX + offset};
    int y[] = {centerY - radius, centerY + radius / 2, centerY + radius / 2};
    g.drawPolygon(x, y, 3);
  }

  /** Draws the square of a circle centered at (centerX, centerY) with radius $radius$ */
  private void drawSquare(Graphics2D g, int centerX, int centerY, int radius) {
    g.drawRect(centerX - radius, centerY - radius, radius * 2, radius * 2);
  }

  /** Draws the mark(X) of a circle centered at (centerX, centerY) with radius $radius$ */
  private void drawMarkX(Graphics2D g, int centerX, int centerY, int radius) {
    g.drawLine(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
    g.drawLine(centerX - radius, centerY + radius, centerX + radius, centerY - radius);
  }

  /** Fills in a circle centered at (centerX, centerY) with radius $radius$ */
  private void fillCircle(Graphics2D g, int centerX, int centerY, int radius) {

    g.fillOval(centerX - radius, centerY - radius, 2 * radius + 1, 2 * radius + 1);
  }

  /** Draws the outline of a circle centered at (centerX, centerY) with radius $radius$ */
  private void drawCircle(Graphics2D g, int centerX, int centerY, int radius) {
    // g.setStroke(new BasicStroke(radius / 11.5f));
    g.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
  }

  // private void drawCircle4(Graphics2D g, int centerX, int centerY, int radius)
  // {
  // g.setStroke(new BasicStroke(1f));
  // g.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
  // }

  private void drawCircle3(Graphics2D g, int centerX, int centerY, int radius) {
    g.setStroke(new BasicStroke(radius / 5f));
    g.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
  }

  private void drawCircle2(Graphics2D g, int centerX, int centerY, int radius) {
    int[] xPoints = {centerX, centerX - (11 * radius / 11), centerX + (11 * radius / 11)};
    int[] yPoints = {
      centerY - (10 * radius / 11), centerY + (8 * radius / 11), centerY + (8 * radius / 11)
    };
    g.fillPolygon(xPoints, yPoints, 3);
  }

  /**
   * Draws a string centered at (x, y) of font $fontString$, whose contents are $string$. The
   * maximum/default fontsize will be $maximumFontHeight$, and the length of the drawn string will
   * be at most maximumFontWidth. The resulting actual size depends on the length of $string$.
   * aboveOrBelow is a param that lets you set: aboveOrBelow = -1 -> y is the top of the string
   * aboveOrBelow = 0 -> y is the vertical center of the string aboveOrBelow = 1 -> y is the bottom
   * of the string
   */
  private void drawString(
      Graphics2D g,
      int x,
      int y,
      Font fontBase,
      int style,
      String string,
      float maximumFontHeight,
      double maximumFontWidth,
      int aboveOrBelow) {

    Font font = makeFont(fontBase, style);

    // set maximum size of font
    FontMetrics fm = g.getFontMetrics(font);
    font = font.deriveFont((float) (font.getSize2D() * maximumFontWidth / fm.stringWidth(string)));
    font = font.deriveFont(min(maximumFontHeight, font.getSize()));
    //    if(font.getSize()<15)
    //    	font=new Font(font.getName(),Font.BOLD,font.getSize());
    g.setFont(font);
    fm = g.getFontMetrics(font);
    int height = fm.getAscent() - fm.getDescent();
    int verticalOffset;
    if (aboveOrBelow == -1) {
      verticalOffset = height / 2;
    } else if (aboveOrBelow == 1) {
      verticalOffset = -height / 2;
    } else {
      verticalOffset = 0;
    }

    // bounding box for debugging
    // g.drawRect(x-(int)maximumFontWidth/2, y - height/2 + verticalOffset,
    // (int)maximumFontWidth,
    // height+verticalOffset );
    g.drawString(string, x - fm.stringWidth(string) / 2, y + height / 2 + verticalOffset);
  }

  private void drawString(
      Graphics2D g,
      int x,
      int y,
      Font fontBase,
      String string,
      float maximumFontHeight,
      double maximumFontWidth) {
    drawString(g, x, y, fontBase, Font.PLAIN, string, maximumFontHeight, maximumFontWidth, 0);
  }

  private void drawStringBold(
      Graphics2D g,
      int x,
      int y,
      Font fontBase,
      String string,
      float maximumFontHeight,
      double maximumFontWidth) {
    drawString(g, x, y, fontBase, Font.BOLD, string, maximumFontHeight, maximumFontWidth, 0);
  }

  private void drawStringBoard(
      Graphics2D g,
      int x,
      int y,
      Font fontBase,
      String string,
      float maximumFontHeight,
      double maximumFontWidth) {
    g.setPaint(paint);
    drawString(g, x, y, fontBase, Font.BOLD, string, maximumFontHeight, maximumFontWidth, 0);
  }

  /** @return a font with kerning enabled */
  private Font makeFont(Font fontBase, int style) {
    Font font = fontBase.deriveFont(style, 100);
    Map<TextAttribute, Object> atts = new HashMap<>();
    atts.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
    return font.deriveFont(atts);
  }

  private int[] calculatePixelMargins() {
    return calculatePixelMargins(boardWidth, boardHeight, showCoordinates());
  }

  /**
   * Set the location to render the board
   *
   * @param x x coordinate
   * @param y y coordinate
   */
  public void setLocation(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Point getLocation() {
    return new Point(x, y);
  }

  /**
   * Set the maximum boardLength to render the board
   *
   * @param boardLength the boardLength of the board
   */
  public void setBoardLength(int boardWidth, int boardHeight) {
    this.shadowRadius =
        Lizzie.config.showBorder ? (int) (max(boardWidth, boardHeight) * MARGIN / 6) : 0;
    this.boardWidth = boardWidth - 4 * shadowRadius;
    this.boardHeight = boardHeight - 4 * shadowRadius;
    this.x = x + 2 * shadowRadius;
    this.y = y + 2 * shadowRadius;
  }

  /**
   * @return the actual board length, including the shadows drawn at the edge of the wooden board
   */
  public int[] getActualBoardLength() {
    return new int[] {
      (int) (boardWidth * (1 + MARGIN / 3)), (int) (boardHeight * (1 + MARGIN / 3))
    };
  }

  /**
   * Converts a location on the screen to a location on the board
   *
   * @param x x pixel coordinate
   * @param y y pixel coordinate
   * @return if there is a valid coordinate, an array (x, y) where x and y are between 0 and
   *     BOARD_SIZE - 1. Otherwise, returns Optional.empty
   */
  public Optional<int[]> convertScreenToCoordinates(int x, int y) {
    int marginWidth; // the pixel width of the margins
    int boardWidthWithoutMargins; // the pixel width of the game board without margins
    int marginHeight; // the pixel height of the margins
    int boardHeightWithoutMargins; // the pixel height of the game board without margins

    // calculate a good set of boardLength, scaledMargin, and
    // boardLengthWithoutMargins to use
    // int[] calculatedPixelMargins = calculatePixelMargins();
    // setBoardLength(calculatedPixelMargins[0], calculatedPixelMargins[3]);
    marginWidth = this.scaledMarginWidth;
    marginHeight = this.scaledMarginHeight;

    // transform the pixel coordinates to board coordinates
    x =
        squareWidth == 0
            ? 0
            : Math.floorDiv(x - this.x - marginWidth + squareWidth / 2, squareWidth);
    y =
        squareHeight == 0
            ? 0
            : Math.floorDiv(y - this.y - marginHeight + squareHeight / 2, squareHeight);

    // return these values if they are valid board coordinates
    return Board.isValid(x, y) ? Optional.of(new int[] {x, y}) : Optional.empty();
  }

  /**
   * Calculate the boardLength of each intersection square
   *
   * @param availableLength the pixel board length of the game board without margins
   * @return the board length of each intersection square
   */
  public void setBoardParam(int[] param) {
    boardWidth = param[0];
    scaledMarginWidth = param[1];
    availableWidth = param[2];
    boardHeight = param[3];
    scaledMarginHeight = param[4];
    availableHeight = param[5];

    squareWidth = calculateSquareWidth(availableWidth);
    squareHeight = calculateSquareHeight(availableHeight);
    stoneRadius = max(squareWidth, squareHeight) < 4 ? 1 : max(squareWidth, squareHeight) / 2 - 1;

    // re-center board
    // setLocation(x + (boardWidth0 - boardWidth) / 2, y + (boardHeight0 -
    // boardHeight) / 2);

    this.shadowRadius =
        Lizzie.config.showBorder ? (int) (max(boardWidth, boardHeight) * MARGIN / 6) : 0;
    this.boardWidth = boardWidth - 4 * shadowRadius;
    this.boardHeight = boardHeight - 4 * shadowRadius;
    this.x = x + 2 * shadowRadius;
    this.y = y + 2 * shadowRadius;
  }

  private static int calculateSquareWidth(int availableWidth) {
    return availableWidth / (Board.boardWidth - 1);
  }

  private static int calculateSquareHeight(int availableHeight) {
    return availableHeight / (Board.boardHeight - 1);
  }

  private boolean isShowingRawBoard() {
    return (displayedBranchLength == SHOW_RAW_BOARD || displayedBranchLength == 0);
  }

  private int maxBranchMoves() {
    switch (displayedBranchLength) {
      case SHOW_NORMAL_BOARD:
        return Integer.MAX_VALUE;
      case SHOW_RAW_BOARD:
        return -1;
      default:
        return displayedBranchLength;
    }
  }

  public boolean isShowingBranch() {
    return showingBranch;
  }

  public void setDisplayedBranchLength(int n) {
    displayedBranchLength = n;
  }

  public int getDisplayedBranchLength() {
    return displayedBranchLength;
  }

  public int getReplayBranch() {

    return mouseOveredMove().isPresent() ? mouseOveredMove().get().variation.size() : 0;
  }

  public boolean incrementDisplayedBranchLength(int n) {
    switch (displayedBranchLength) {
      case SHOW_NORMAL_BOARD:
      case SHOW_RAW_BOARD:
        return false;
      default:
        // force nonnegative
        displayedBranchLength = max(0, displayedBranchLength + n);
        return true;
    }
  }

  public void startNormalBoard() {
    setDisplayedBranchLength(SHOW_NORMAL_BOARD);
  }

  public boolean isInside(int x1, int y1) {
    return x <= x1 && x1 < x + boardWidth && y <= y1 && y1 < y + boardHeight;
  }

  private boolean showCoordinates() {
    return isMainBoard && Lizzie.config.showCoordinates;
  }

  public void increaseMaxAlpha(int k) {
    maxAlpha = min(maxAlpha + k, 255);
    uiPersist.put("max-alpha", maxAlpha);
  }

  public boolean isShowingNormalBoard() {
    return displayedBranchLength == SHOW_NORMAL_BOARD;
  }

  public void addSuggestionAsBranch() {
    mouseOveredMove()
        .ifPresent(
            m -> {
              if (m.variation.size() > 0) {
                if (Lizzie.board.getHistory().getCurrentHistoryNode().numberOfChildren() == 0) {
                  Stone color =
                      Lizzie.board.getHistory().getLastMoveColor() == Stone.WHITE
                          ? Stone.BLACK
                          : Stone.WHITE;
                  Lizzie.board.getHistory().pass(color, false, true);
                  Lizzie.board.getHistory().previous();
                }
                for (int i = 0; i < m.variation.size(); i++) {
                  Stone color =
                      Lizzie.board.getHistory().getLastMoveColor() == Stone.WHITE
                          ? Stone.BLACK
                          : Stone.WHITE;
                  Optional<int[]> coordOpt = Board.asCoordinates(m.variation.get(i));
                  if (!coordOpt.isPresent()
                      || !Board.isValid(coordOpt.get()[0], coordOpt.get()[1])) {
                    break;
                  }
                  int[] coord = coordOpt.get();
                  Lizzie.board.getHistory().place(coord[0], coord[1], color, i == 0);
                }
                Lizzie.board.getHistory().toBranchTop();
                Lizzie.frame.refresh();
              }
            });
  }
}
