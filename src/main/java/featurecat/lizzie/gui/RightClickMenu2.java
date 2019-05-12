package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.RightClickMenu;
import featurecat.lizzie.rules.Movelist;
//import featurecat.lizzie.rules.Movelistwr;
import featurecat.lizzie.rules.SGFParser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class RightClickMenu2 extends JPopupMenu {
  public static int mousex;
  public static int mousey;
  //  private JMenuItem insertmode;
  private JMenuItem switchone;
  private JMenuItem deleteone;

  public RightClickMenu2() {

    PopupMenuListener listener =
        new PopupMenuListener() {
          public void popupMenuCanceled(PopupMenuEvent e) {}

          public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            if (Lizzie.leelaz.isPondering() && featurecat.lizzie.gui.RightClickMenu.isforcing) {
              if (featurecat.lizzie.gui.RightClickMenu.isforcing) {
                Lizzie.leelaz.analyzeAvoid("allow", Lizzie.board.getcurrentturn(), featurecat.lizzie.gui.RightClickMenu.allowcoords, 1);
              } else {
                Lizzie.leelaz.analyzeAvoid("avoid", Lizzie.board.getcurrentturn(),  featurecat.lizzie.gui.RightClickMenu.avoidcoords, 30);
              }
            }
            if (Lizzie.leelaz.isPondering() && !featurecat.lizzie.gui.RightClickMenu.isforcing) {
              Lizzie.leelaz.ponder();
            }
            Lizzie.frame.isshowrightmenu = false;
          }

          public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

          }
        };

    this.addPopupMenuListener(listener);

    switchone = new JMenuItem("反色");
    deleteone = new JMenuItem("删除");
    this.add(switchone);
    this.add(deleteone);

    switchone.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //  System.out.println("撤销上次编辑");
            switchone();
            //  Lizzie.board.clear();
          }
        });

    deleteone.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //  System.out.println("撤销上次编辑");
        	  deleteone();
            //  Lizzie.board.clear();
          }
        });
  }

  private void switchone() {
	  Optional<int[]> boardCoordinates =  Lizzie.frame.boardRenderer.convertScreenToCoordinates(mousex, mousey);
	    if (boardCoordinates.isPresent()) {
	      int[] coords = boardCoordinates.get();
	 int draggedmovenumer = Lizzie.board.getmovenumber(coords);
	 
	 int currentmovenumber=Lizzie.board.getcurrentmovenumber();
     Lizzie.board.savelistforeditmode();
     Lizzie.board.editmovelistswitch(Lizzie.board.tempallmovelist, draggedmovenumer);
     Lizzie.board.clear();
     Lizzie.board.setlist(Lizzie.board.tempallmovelist);
     Lizzie.board.goToMoveNumber(currentmovenumber);
  }
  }

  private void deleteone() {
	  Optional<int[]> boardCoordinates = Lizzie.frame.boardRenderer.convertScreenToCoordinates(mousex ,mousey);
	    if (boardCoordinates.isPresent()) {
	      int[] coords = boardCoordinates.get();  
	    int draggedmovenumer =  Lizzie.board.getmovenumber(coords);
	    int currentmovenumber=Lizzie.board.getcurrentmovenumber();
        Lizzie.board.savelistforeditmode();
        Lizzie.board.editmovelistdelete(Lizzie.board.tempallmovelist, draggedmovenumer);
        Lizzie.board.clear();
        Lizzie.board.setlist(Lizzie.board.tempallmovelist);
        Lizzie.board.goToMoveNumber(currentmovenumber);
	    }  
  }


  public void Store(int x, int y) {
    mousex = x;
    mousey = y;
  }
}
