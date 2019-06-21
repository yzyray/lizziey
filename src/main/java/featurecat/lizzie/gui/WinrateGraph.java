package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.rules.BoardHistoryNode;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Optional;

public class WinrateGraph {

  private int DOT_RADIUS = 3;
  private int[] origParams = {0, 0, 0, 0};
  private int[] params = {0, 0, 0, 0, 0};
  private int numMovesOfPlayed = 0;
  public int mode = 1;

  public void draw(Graphics2D g, int posx, int posy, int width, int height) {
    BoardHistoryNode curMove = Lizzie.board.getHistory().getCurrentHistoryNode();
    BoardHistoryNode node = curMove;

    // draw background rectangle
    final Paint gradient =
        new GradientPaint(
            new Point2D.Float(posx, posy),
            new Color(20, 20, 20, 150),
            new Point2D.Float(posx, posy + height),
            new Color(155, 155, 155, 150));
    final Paint borderGradient =
        new GradientPaint(
            new Point2D.Float(posx, posy),
            new Color(0, 0, 0, 150),
            new Point2D.Float(posx, posy + height),
            new Color(255, 255, 255, 150));

    Paint original = g.getPaint();
    g.setPaint(gradient);

    g.fillRect(posx, posy, width, height);

    // draw border
    int strokeRadius = Lizzie.config.showBorder ? 3 : 1;
    g.setStroke(new BasicStroke(strokeRadius == 1 ? strokeRadius : 2 * strokeRadius));
    g.setPaint(borderGradient);
    if (Lizzie.config.showBorder) {
      g.drawRect(
          posx + strokeRadius,
          posy + strokeRadius,
          width - 2 * strokeRadius,
          height - 2 * strokeRadius);
    } else {
      g.drawLine(
          posx + strokeRadius, posy + strokeRadius,
          posx - strokeRadius + width, posy + strokeRadius);
    }

    g.setPaint(original);

    // record parameters (before resizing) for calculating moveNumber
    origParams[0] = posx;
    origParams[1] = posy;
    origParams[2] = width;
    origParams[3] = height;

    // resize the box now so it's inside the border
    posx += 2 * strokeRadius;
    posy += 2 * strokeRadius;
    width -= 4 * strokeRadius;
    height -= 4 * strokeRadius;

    // draw lines marking 50% 60% 70% etc.
    Stroke dashed =
        new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {4}, 0);
    g.setStroke(dashed);

    g.setColor(Color.white);
    int winRateGridLines = Lizzie.frame.winRateGridLines;
    int midline = 0;
    int midy = 0;
    if (Lizzie.config.showBlunderBar) {
      midline = (int) Math.ceil(winRateGridLines / 2.0);
      midy = posy + height / 2;
    }
    for (int i = 1; i <= winRateGridLines; i++) {
      double percent = i * 100.0 / (winRateGridLines + 1);
      int y = posy + height - (int) (height * convertWinrate(percent) / 100);
      if (Lizzie.config.showBlunderBar && i == midline) {
        midy = y;
      }
      g.drawLine(posx, y, posx + width, y);
    }

    g.setColor(Lizzie.config.winrateLineColor);
    g.setStroke(new BasicStroke(Lizzie.config.winrateStrokeWidth));

    Optional<BoardHistoryNode> topOfVariation = Optional.empty();
    int numMoves = 0;
    if (!curMove.isMainTrunk()) {
      // We're in a variation, need to draw both main trunk and variation
      // Find top of variation
      BoardHistoryNode top = curMove.findTop();
      topOfVariation = Optional.of(top);
      // Find depth of main trunk, need this for plot scaling
      numMoves = top.getDepth() + top.getData().moveNumber - 1;
      g.setStroke(dashed);
    }

    // Go to end of variation and work our way backwards to the root
    while (node.next().isPresent()) {
      node = node.next().get();
    }
    if (numMoves < node.getData().moveNumber - 1) {
      numMoves = node.getData().moveNumber - 1;
    }

    if (numMoves < 1) return;

    // Plot
    width = (int) (width * 0.95); // Leave some space after last move
    double lastWr = 50;
    double lastWr2 = 50;
    boolean lastNodeOk = false;
    boolean inFirstPath = true;
    int movenum = node.getData().moveNumber - 1;
    int lastOkMove = -1;
    if (Lizzie.config.dynamicWinrateGraphWidth && this.numMovesOfPlayed > 0) {
      numMoves = this.numMovesOfPlayed;
    }

    if (Lizzie.frame.toolbar.isEnginePk || Lizzie.board.isPkBoard) {
      if (numMoves < 2) return;
      Stroke previousStroke = g.getStroke();
      int x =
          posx
              + ((Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber - 1)
                  * width
                  / numMoves);
      g.setStroke(dashed);
      g.setColor(Color.white);
      if (Lizzie.board.getHistory().getCurrentHistoryNode() != Lizzie.board.getHistory().getEnd())
        g.drawLine(x, posy, x, posy + height);
      g.setStroke(previousStroke);
      String moveNumString = "" + node.getData().moveNumber;
      int mw = g.getFontMetrics().stringWidth(moveNumString);
      int margin = strokeRadius;
      int mx = x - posx < width / 2 ? x + margin : x - mw - margin;
      if (node.getData().blackToPlay) {
        g.setColor(Color.WHITE);
      } else {
        g.setColor(Color.BLACK);
      }
      if (Lizzie.board.getHistory().getCurrentHistoryNode() != Lizzie.board.getHistory().getEnd()) {
        Font f = new Font("", Font.BOLD, 12);
        g.setFont(f);
        g.drawString(moveNumString, mx, posy + height - margin);
      }
      while (node.previous().isPresent() && node.previous().get().previous().isPresent()) {
        double wr = 50;
        try {
          wr = node.previous().get().previous().get().getData().bestMoves.get(0).winrate;
        } catch (Exception ex) {
        }
        try {
          wr = node.getData().bestMoves.get(0).winrate;
        } catch (Exception ex) {

        }

        int playouts = node.getData().getPlayouts();
        try {
          lastWr = node.previous().get().previous().get().getData().bestMoves.get(0).winrate;
        } catch (Exception ex) {
        }
        if (node.getData().blackToPlay) {
          g.setColor(Color.BLACK);
          g.drawLine(
              posx + ((movenum - 2) * width / numMoves),
              posy + height - (int) (convertWinrate(lastWr) * height / 100),
              posx + (movenum * width / numMoves),
              posy + height - (int) (convertWinrate(wr) * height / 100));

        } else {
          g.setColor(Color.WHITE);

          g.drawLine(
              posx + ((movenum - 2) * width / numMoves),
              posy + height - (int) (convertWinrate(lastWr) * height / 100),
              posx + (movenum * width / numMoves),
              posy + height - (int) (convertWinrate(wr) * height / 100));
        }

        lastOkMove = movenum - 2;

        if (Lizzie.config.showBlunderBar) {
          g.setColor(Lizzie.config.blunderBarColor);
          double lastMoveRate = convertWinrate(lastWr) - convertWinrate(wr);
          int lastHeight = 0;
          if (Lizzie.config.weightedBlunderBarHeight) {
            // Weighted display: <= 50% will use 75% of height, >= 50% will use 25% of height
            if (Math.abs(lastMoveRate) <= 50) {
              lastHeight = Math.abs((int) (lastMoveRate) * height * 3 / 400);
            } else {
              lastHeight = height / 4 + Math.abs((int) (Math.abs(lastMoveRate)) * height / 400);
            }
          } else {
            lastHeight = Math.abs((int) (lastMoveRate) * height / 200);
          }
          int lastWidth = Math.abs(2 * width / numMoves);
          int rectWidth = Math.max(lastWidth / 10, Lizzie.config.minimumBlunderBarWidth);
          g.fillRect(
              posx + ((movenum - 1) * width / numMoves) + (lastWidth - rectWidth) / 2,
              midy + (node.getData().blackToPlay ? 0 : lastHeight * -1),
              rectWidth,
              lastHeight);
        }

        if (curMove.previous().isPresent()
            && (node == curMove || node == curMove.previous().get())) {
          if (node.getData().blackToPlay) {
            g.setColor(Color.BLACK);
            g.fillOval(
                posx + (movenum * width / numMoves) - DOT_RADIUS,
                posy + height - (int) (convertWinrate(wr) * height / 100) - DOT_RADIUS,
                DOT_RADIUS * 2,
                DOT_RADIUS * 2);
            Font f = new Font("", Font.BOLD, 17);
            g.setFont(f);
            if (wr > 50) {
              if (wr > 90) {
                g.drawString(
                    String.format("%.1f", wr),
                    posx + (movenum * width / numMoves) - 3 * DOT_RADIUS,
                    posy + (height - (int) (convertWinrate(wr) * height / 100)) + 6 * DOT_RADIUS);
              } else {
                g.drawString(
                    String.format("%.1f", wr),
                    posx + (movenum * width / numMoves) - 3 * DOT_RADIUS,
                    posy + (height - (int) (convertWinrate(wr) * height / 100)) - 2 * DOT_RADIUS);
              }
            } else {
              if (wr < 10) {
                g.drawString(
                    String.format("%.1f", wr),
                    posx + (movenum * width / numMoves) - 3 * DOT_RADIUS,
                    posy + (height - (int) (convertWinrate(wr) * height / 100)) - 2 * DOT_RADIUS);
              } else {
                g.drawString(
                    String.format("%.1f", wr),
                    posx + (movenum * width / numMoves) - 3 * DOT_RADIUS,
                    posy + (height - (int) (convertWinrate(wr) * height / 100)) + 6 * DOT_RADIUS);
              }
            }
          } else {
            g.setColor(Color.WHITE);
            g.fillOval(
                posx + (movenum * width / numMoves) - DOT_RADIUS,
                posy + height - (int) (convertWinrate(wr) * height / 100) - DOT_RADIUS,
                DOT_RADIUS * 2,
                DOT_RADIUS * 2);
            Font f = new Font("", Font.PLAIN, 17);
            g.setFont(f);
            if (wr > 50) {
              g.drawString(
                  String.format("%.1f", wr),
                  posx + (movenum * width / numMoves) - 3 * DOT_RADIUS,
                  posy + (height - (int) (convertWinrate(wr) * height / 100)) - 2 * DOT_RADIUS);
            } else {
              g.drawString(
                  String.format("%.1f", wr),
                  posx + (movenum * width / numMoves) - 3 * DOT_RADIUS,
                  posy + (height - (int) (convertWinrate(wr) * height / 100)) + 6 * DOT_RADIUS);
            }
          }
        }
        node = node.previous().get();
        movenum = movenum - 1;
      }
    } else {
      if (mode == 0) {
        double cwr = -1;
        double lastscoreMean = -500;
        double maxcoreMean = 10;
        int cmovenum = -1;
        while (node.previous().isPresent()) {
          double wr = node.getData().winrate;
          int playouts = node.getData().getPlayouts();
          if (node == curMove) {
            if (Lizzie.config.dynamicWinrateGraphWidth
                && node.getData().moveNumber - 1 > this.numMovesOfPlayed) {
              this.numMovesOfPlayed = node.getData().moveNumber - 1;
              numMoves = this.numMovesOfPlayed;
            }
            Leelaz.WinrateStats stats = Lizzie.leelaz.getWinrateStats();
            double bwr = stats.maxWinrate;
            if (bwr >= 0 && stats.totalPlayouts > playouts) {
              wr = bwr;
              playouts = stats.totalPlayouts;
            }
            // Draw a vertical line at the current move
            Stroke previousStroke = g.getStroke();
            int x = posx + (movenum * width / numMoves);
            g.setStroke(dashed);
            g.setColor(Color.white);
            g.drawLine(x, posy, x, posy + height);
            // Show move number
            String moveNumString = "" + node.getData().moveNumber;
            int mw = g.getFontMetrics().stringWidth(moveNumString);
            int margin = strokeRadius;
            int mx = x - posx < width / 2 ? x + margin : x - mw - margin;
            Font f = new Font("", Font.BOLD, 12);
            g.setFont(f);
            g.drawString(moveNumString, mx, posy + height - margin);
            g.setStroke(previousStroke);
          }
          if (playouts > 0) {
            if (wr < 0) {
              wr = 100 - lastWr;
            } else if (!node.getData().blackToPlay) {
              wr = 100 - wr;
            }
            //            if (Lizzie.frame.isPlayingAgainstLeelaz
            //                && Lizzie.frame.playerIsBlack == !node.getData().blackToPlay) {
            //              wr = lastWr;
            //            }

            if (lastNodeOk) g.setColor(Lizzie.config.winrateLineColor);
            else g.setColor(Lizzie.config.winrateMissLineColor);

            if (lastOkMove > 0) {
              if (Lizzie.config.showBlunderBar) {
                Color lineColor = g.getColor();
                g.setColor(Lizzie.config.blunderBarColor);
                double lastMoveRate = convertWinrate(lastWr) - convertWinrate(wr);
                int lastHeight = 0;
                if (Lizzie.config.weightedBlunderBarHeight) {
                  // Weighted display: <= 50% will use 75% of height, >= 50% will use 25% of height
                  if (Math.abs(lastMoveRate) <= 50) {
                    lastHeight = Math.abs((int) (lastMoveRate) * height * 3 / 400);
                  } else {
                    lastHeight =
                        height / 4 + Math.abs((int) (Math.abs(lastMoveRate)) * height / 400);
                  }
                } else {
                  lastHeight = Math.abs((int) (lastMoveRate) * height / 200);
                }
                int lastWidth = Math.abs((movenum - lastOkMove) * width / numMoves);
                int rectWidth = Math.max(lastWidth / 10, Lizzie.config.minimumBlunderBarWidth);
                g.fillRect(
                    posx + (movenum * width / numMoves) + (lastWidth - rectWidth) / 2,
                    midy + (!node.getData().blackToPlay && lastMoveRate > 0 ? lastHeight * -1 : 0),
                    rectWidth,
                    lastHeight);
                g.setColor(lineColor);
              }
              g.drawLine(
                  posx + (lastOkMove * width / numMoves),
                  posy + height - (int) (convertWinrate(lastWr) * height / 100),
                  posx + (movenum * width / numMoves),
                  posy + height - (int) (convertWinrate(wr) * height / 100));
              if (Lizzie.leelaz.isKatago) {
                if (!node.getData().bestMoves.isEmpty()) {
                  double curscoreMean = node.getData().bestMoves.get(0).scoreMean;
                  if (!node.getData().blackToPlay) {
                    curscoreMean = -curscoreMean;
                  }

                  if (Math.abs(curscoreMean) > maxcoreMean) maxcoreMean = Math.abs(curscoreMean);
                  if (lastscoreMean > -500) {
                    Color lineColor = g.getColor();
                    Stroke previousStroke = g.getStroke();
                    g.setColor(Color.RED);
                    g.setStroke(new BasicStroke(1));
                    g.drawLine(
                        posx + (lastOkMove * width / numMoves),
                        posy + height - (int) (curscoreMean * height / maxcoreMean),
                        posx + (movenum * width / numMoves),
                        posy + height - (int) (lastscoreMean * height / maxcoreMean));
                  }
                  lastscoreMean = curscoreMean;
                }
              }
            }

            if (node == curMove) {
              g.setColor(Lizzie.config.winrateLineColor);
              g.fillOval(
                  posx + (movenum * width / numMoves) - DOT_RADIUS,
                  posy + height - (int) (convertWinrate(wr) * height / 100) - DOT_RADIUS,
                  DOT_RADIUS * 2,
                  DOT_RADIUS * 2);
              //              if (node.getData().blackToPlay) g.setColor(Color.WHITE);
              //              else g.setColor(Color.BLACK);
              cwr = wr;
              cmovenum = movenum;
            }
            lastWr = wr;
            lastNodeOk = true;
            // Check if we were in a variation and has reached the main trunk
            if (topOfVariation.isPresent() && topOfVariation.get() == node) {
              // Reached top of variation, go to end of main trunk before continuing
              while (node.next().isPresent()) {
                node = node.next().get();
              }
              movenum = node.getData().moveNumber - 1;
              lastWr = node.getData().winrate;
              if (!node.getData().blackToPlay) lastWr = 100 - lastWr;
              g.setStroke(new BasicStroke(3));
              topOfVariation = Optional.empty();
              if (node.getData().getPlayouts() == 0) {
                lastNodeOk = false;
              }
              inFirstPath = false;
            }
            lastOkMove = lastNodeOk ? movenum : -1;
          } else {
            lastNodeOk = false;
          }

          node = node.previous().get();
          movenum--;
        }
        if (cwr > 0)
          if (cwr > 0) {
            Font f = new Font("", Font.BOLD, 17);
            g.setFont(f);
            g.setColor(Color.CYAN);
            g.drawString(
                String.format("%.1f", cwr),
                posx + (cmovenum * width / numMoves) - 3 * DOT_RADIUS,
                posy + (height - (int) (convertWinrate(cwr) * height / 100)) - 2 * DOT_RADIUS);
          }
      } else if (mode == 1) {
        while (node.previous().isPresent()) {
          double wr = node.getData().winrate;
          int playouts = node.getData().getPlayouts();
          if (node == curMove) {
            if (Lizzie.config.dynamicWinrateGraphWidth
                && node.getData().moveNumber - 1 > this.numMovesOfPlayed) {
              this.numMovesOfPlayed = node.getData().moveNumber - 1;
              numMoves = this.numMovesOfPlayed;
            }
            Leelaz.WinrateStats stats = Lizzie.leelaz.getWinrateStats();
            double bwr = stats.maxWinrate;
            if (bwr >= 0 && stats.totalPlayouts > playouts) {
              wr = bwr;
              playouts = stats.totalPlayouts;
            }
            // Draw a vertical line at the current move
            // Stroke previousStroke = g.getStroke();
            Stroke previousStroke = g.getStroke();
            int x = posx + (movenum * width / numMoves);
            g.setStroke(dashed);
            g.setColor(Color.white);
            if (curMove != Lizzie.board.getHistory().getEnd())
              g.drawLine(x, posy, x, posy + height);

            // Show move number
            String moveNumString = "" + node.getData().moveNumber;
            int mw = g.getFontMetrics().stringWidth(moveNumString);
            int margin = strokeRadius;
            int mx = x - posx < width / 2 ? x + margin : x - mw - margin;
            if (node.getData().blackToPlay) {
              g.setColor(Color.WHITE);
            } else {
              g.setColor(Color.BLACK);
            }
            if (Lizzie.board.getHistory().getCurrentHistoryNode()
                != Lizzie.board.getHistory().getEnd()) {
              Font f = new Font("", Font.BOLD, 12);
              g.setFont(f);
              g.drawString(moveNumString, mx, posy + height - margin);
            }
            g.setStroke(previousStroke);
          }
          if (playouts > 0) {
            if (wr < 0) {
              wr = 100 - lastWr;
            } else if (!node.getData().blackToPlay) {
              wr = 100 - wr;
            }
            //            if (Lizzie.frame.isPlayingAgainstLeelaz
            //                && Lizzie.frame.playerIsBlack == !node.getData().blackToPlay) {
            //              wr = lastWr;
            //            }

            if (lastOkMove > 0) {
              if (Lizzie.config.showBlunderBar) {
                // Color lineColor = g.getColor();
                g.setColor(Lizzie.config.blunderBarColor);
                double lastMoveRate = convertWinrate(lastWr) - convertWinrate(wr);
                int lastHeight = 0;
                if (Lizzie.config.weightedBlunderBarHeight) {
                  // Weighted display: <= 50% will use 75% of height, >= 50% will use 25% of height
                  if (Math.abs(lastMoveRate) <= 50) {
                    lastHeight = Math.abs((int) (lastMoveRate) * height * 3 / 400);
                  } else {
                    lastHeight =
                        height / 4 + Math.abs((int) (Math.abs(lastMoveRate)) * height / 400);
                  }
                } else {
                  lastHeight = Math.abs((int) (lastMoveRate) * height / 200);
                }
                int lastWidth = Math.abs((movenum - lastOkMove) * width / numMoves);
                int rectWidth = Math.max(lastWidth / 10, Lizzie.config.minimumBlunderBarWidth);
                g.fillRect(
                    posx + (movenum * width / numMoves) + (lastWidth - rectWidth) / 2,
                    midy + (!node.getData().blackToPlay && lastMoveRate > 0 ? lastHeight * -1 : 0),
                    rectWidth,
                    lastHeight);
              }
              g.setColor(Color.BLACK);
              if (lastNodeOk) g.setStroke(new BasicStroke(2f));
              else g.setStroke(new BasicStroke(1f));
              // g.setColor(Color.BLACK);
              g.drawLine(
                  posx + (lastOkMove * width / numMoves),
                  posy + height - (int) (convertWinrate(lastWr) * height / 100),
                  posx + (movenum * width / numMoves),
                  posy + height - (int) (convertWinrate(wr) * height / 100));
              g.setColor(Color.WHITE);
              if (lastNodeOk) g.setStroke(new BasicStroke(2f));
              else g.setStroke(new BasicStroke(1f));
              //  g.setColor(Color.WHITE);
              g.drawLine(
                  posx + (lastOkMove * width / numMoves),
                  posy + height - (int) (convertWinrate(100 - lastWr) * height / 100),
                  posx + (movenum * width / numMoves),
                  posy + height - (int) (convertWinrate(100 - wr) * height / 100));
            }

            if (node == curMove) {

              g.setColor(Color.BLACK);
              g.fillOval(
                  posx + (movenum * width / numMoves) - DOT_RADIUS,
                  posy + height - (int) (convertWinrate(wr) * height / 100) - DOT_RADIUS,
                  DOT_RADIUS * 2,
                  DOT_RADIUS * 2);
              Font f = new Font("", Font.BOLD, 17);

              g.setFont(f);
              if (wr > 50) {
                if (wr > 90) {
                  g.drawString(
                      String.format("%.1f", wr),
                      posx + (movenum * width / numMoves) - 3 * DOT_RADIUS,
                      posy + (height - (int) (convertWinrate(wr) * height / 100)) + 6 * DOT_RADIUS);
                } else {
                  g.drawString(
                      String.format("%.1f", wr),
                      posx + (movenum * width / numMoves) - 3 * DOT_RADIUS,
                      posy + (height - (int) (convertWinrate(wr) * height / 100)) - 2 * DOT_RADIUS);
                }
              } else {
                if (wr < 10) {
                  g.drawString(
                      String.format("%.1f", wr),
                      posx + (movenum * width / numMoves) - 3 * DOT_RADIUS,
                      posy + (height - (int) (convertWinrate(wr) * height / 100)) - 2 * DOT_RADIUS);
                } else {
                  g.drawString(
                      String.format("%.1f", wr),
                      posx + (movenum * width / numMoves) - 3 * DOT_RADIUS,
                      posy + (height - (int) (convertWinrate(wr) * height / 100)) + 6 * DOT_RADIUS);
                }
              }
              g.setColor(Color.WHITE);
              Font fw = new Font("", Font.PLAIN, 17);
              g.setFont(fw);
              g.fillOval(
                  posx + (movenum * width / numMoves) - DOT_RADIUS,
                  posy + height - (int) (convertWinrate(100 - wr) * height / 100) - DOT_RADIUS,
                  DOT_RADIUS * 2,
                  DOT_RADIUS * 2);
              if (wr > 50) {
                g.drawString(
                    String.format("%.1f", 100 - wr),
                    posx + (movenum * width / numMoves) - 3 * DOT_RADIUS,
                    posy
                        + (height - (int) (convertWinrate(100 - wr) * height / 100))
                        + 6 * DOT_RADIUS);
              } else {

                g.drawString(
                    String.format("%.1f", 100 - wr),
                    posx + (movenum * width / numMoves) - 3 * DOT_RADIUS,
                    posy
                        + (height - (int) (convertWinrate(100 - wr) * height / 100))
                        - 2 * DOT_RADIUS);
              }
            }
            lastWr = wr;
            lastNodeOk = true;
            // Check if we were in a variation and has reached the main trunk
            if (topOfVariation.isPresent() && topOfVariation.get() == node) {
              // Reached top of variation, go to end of main trunk before continuing
              while (node.next().isPresent()) {
                node = node.next().get();
              }
              movenum = node.getData().moveNumber - 1;
              lastWr = node.getData().winrate;
              if (!node.getData().blackToPlay) lastWr = 100 - lastWr;
              g.setStroke(new BasicStroke(3));
              topOfVariation = Optional.empty();
              if (node.getData().getPlayouts() == 0) {
                lastNodeOk = false;
              }
              inFirstPath = false;
            }
            lastOkMove = lastNodeOk ? movenum : -1;
          } else {
            lastNodeOk = false;
          }

          node = node.previous().get();
          movenum--;
        }
      }
    }
    g.setStroke(new BasicStroke(1));

    // record parameters for calculating moveNumber
    params[0] = posx;
    params[1] = posy;
    params[2] = width;
    params[3] = height;
    params[4] = numMoves;
  }

  private double convertWinrate(double winrate) {
    double maxHandicap = 10;
    if (Lizzie.config.handicapInsteadOfWinrate) {
      double handicap = Lizzie.leelaz.winrateToHandicap(winrate);
      // handicap == + maxHandicap => r == 1.0
      // handicap == - maxHandicap => r == 0.0
      double r = 0.5 + handicap / (2 * maxHandicap);
      return Math.max(0, Math.min(r, 1)) * 100;
    } else {
      return winrate;
    }
  }

  public int moveNumber(int x, int y) {
    int origPosx = origParams[0];
    int origPosy = origParams[1];
    int origWidth = origParams[2];
    int origHeight = origParams[3];
    int posx = params[0];
    int posy = params[1];
    int width = params[2];
    int height = params[3];
    int numMoves = params[4];
    if (origPosx <= x && x < origPosx + origWidth && origPosy <= y && y < origPosy + origHeight) {
      // x == posx + (movenum * width / numMoves) ==> movenum = ...
      int movenum = Math.round((x - posx) * numMoves / (float) width);
      // movenum == moveNumber - 1 ==> moveNumber = ...
      return movenum + 1;
    } else {
      return -1;
    }
  }

  /** Clears winrate status from empty board. */
  public void clear() {
    this.numMovesOfPlayed = 0;
  }
}
