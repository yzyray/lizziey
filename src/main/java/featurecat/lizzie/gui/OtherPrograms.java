package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.json.JSONArray;

@SuppressWarnings("serial")
public class OtherPrograms extends JPanel {
  public static Config config;
  public TableModel dataModel;
  JPanel tablepanel;
  JPanel selectpanel = new JPanel();

  JScrollPane scrollpane;
  public static JTable table;
  // public static JLabel checkBlacktxt;
  // public static JLabel checkWhitetxt;
  Font headFont;
  Font winrateFont;
  static JDialog engjf;
  Timer timer;
  int sortnum = 3;
  public static int selectedorder = -1;
  boolean issorted = false;
  // JSpinner dropwinratechooser = new JSpinner(new SpinnerNumberModel(1, 0, 99,
  // 1));
  // JSpinner playoutschooser = new JSpinner(new SpinnerNumberModel(100, 0, 99999,
  // 100));
  // JCheckBox checkBlack = new JCheckBox();
  // JCheckBox checkWhite = new JCheckBox();
  JTextArea command;
  JTextField txtName;
  JLabel engineName;

  JButton scan;
  JButton delete;
  JButton save;
  JButton cancel;
  JButton exit;
  JButton moveUp;
  JButton moveDown;
  int curIndex = -1;

  public String enginePath = "";

  public String engineSacnName = "";
  public String weightPath = "";
  public String commandHelp = "";
  public final ResourceBundle resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings");
  private String osName;
  private BufferedInputStream inputStream;
  private Path curPath;

  public OtherPrograms() {
    // super(new BorderLayout());

    curPath = (new File("")).getAbsoluteFile().toPath();
    osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
    this.setLayout(null);
    dataModel = getTableModel();
    table = new JTable(dataModel);
    selectpanel.setLayout(null);
    winrateFont = new Font("微软雅黑", Font.PLAIN, 14);
    headFont = new Font("微软雅黑", Font.PLAIN, 13);

    table.getTableHeader().setFont(headFont);
    table.setFont(winrateFont);
    table.getTableHeader().setReorderingAllowed(false);
    table.getTableHeader().setResizingAllowed(false);
    TableCellRenderer tcr = new ColorTableCellRenderer();
    table.setDefaultRenderer(Object.class, tcr);
    table.setRowHeight(20);

    tablepanel = new JPanel(new BorderLayout());
    tablepanel.setBounds(0, 330, 885, 432);
    this.add(tablepanel, BorderLayout.SOUTH);
    selectpanel.setBounds(0, 0, 900, 330);
    this.add(selectpanel, BorderLayout.NORTH);
    scrollpane = new JScrollPane(table);

    tablepanel.add(scrollpane);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFillsViewportHeight(true);
    table.getColumnModel().getColumn(0).setPreferredWidth(30);
    table.getColumnModel().getColumn(1).setPreferredWidth(200);
    table.getColumnModel().getColumn(2).setPreferredWidth(400);
    // boolean persisted = Lizzie.config.persistedUi != null;
    // if (persisted
    // && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position") != null
    // && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position").length()
    // == 12) {
    // JSONArray pos =
    // Lizzie.config.persistedUi.getJSONArray("badmoves-list-position");
    // // table.getColumnModel().getColumn(0).setPreferredWidth(pos.getInt(4));
    // // table.getColumnModel().getColumn(1).setPreferredWidth(pos.getInt(5));
    // // table.getColumnModel().getColumn(2).setPreferredWidth(pos.getInt(6));
    // // table.getColumnModel().getColumn(3).setPreferredWidth(pos.getInt(7));
    // }

    JTableHeader header = table.getTableHeader();

    // dropwinratechooser.setValue(Lizzie.config.limitbadmoves);
    // playoutschooser.setValue(Lizzie.config.limitbadplayouts);
    // checkBlack.setSelected(true);
    // checkWhite.setSelected(true);

    engineName = new JLabel("单击选中列表中的启动项进行设置");
    engineName.setFont(new Font("微软雅黑", Font.PLAIN, 14));
    JLabel lblname = new JLabel("名称：");
    txtName = new JTextField();
    command = new JTextArea(5, 80);
    command.setLineWrap(true);
    command.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    txtName.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    JLabel lblcommand = new JLabel("命令行：");

    save = new JButton("保存");
    cancel = new JButton("取消");
    exit = new JButton("退出");
    delete = new JButton("删除");
    scan = new JButton("浏览");
    moveUp = new JButton("上移");
    moveDown = new JButton("下移");

    moveUp.setFocusable(false);
    moveUp.setMargin(new Insets(0, 0, 0, 0));
    moveDown.setFocusable(false);
    moveDown.setMargin(new Insets(0, 0, 0, 0));
    scan.setFocusable(false);
    scan.setMargin(new Insets(0, 0, 0, 0));
    save.setFocusable(false);
    save.setMargin(new Insets(0, 0, 0, 0));
    cancel.setFocusable(false);
    cancel.setMargin(new Insets(0, 0, 0, 0));
    exit.setFocusable(false);
    exit.setMargin(new Insets(0, 0, 0, 0));
    delete.setFocusable(false);
    delete.setMargin(new Insets(0, 0, 0, 0));

    engineName.setBounds(5, 5, 700, 20);
    txtName.setBounds(50, 35, 800, 20);
    lblname.setBounds(5, 35, 45, 20);
    lblcommand.setBounds(5, 65, 50, 20);
    scan.setBounds(5, 85, 40, 20);
    command.setBounds(50, 65, 800, 200);

    moveUp.setBounds(560, 270, 40, 22);
    moveDown.setBounds(610, 270, 40, 22);
    save.setBounds(660, 270, 40, 22);
    cancel.setBounds(710, 270, 40, 22);
    delete.setBounds(760, 270, 40, 22);
    exit.setBounds(810, 270, 40, 22);
    // checkBlacktxt = new JLabel("黑:");
    // checkWhitetxt = new JLabel("白:");
    // JLabel dropwinratechoosertxt = new JLabel("胜率波动筛选:");
    // JLabel playoutschoosertxt = new JLabel("前后计算量筛选:");

    engineName.setEnabled(false);
    txtName.setEnabled(false);
    command.setEnabled(false);
    delete.setEnabled(false);
    moveUp.setEnabled(false);
    moveDown.setEnabled(false);

    cancel.setEnabled(false);
    scan.setEnabled(false);
    selectpanel.add(engineName);
    selectpanel.add(lblname);
    selectpanel.add(txtName);
    selectpanel.add(command);
    selectpanel.add(lblcommand);

    selectpanel.add(scan);
    selectpanel.add(save);
    selectpanel.add(cancel);
    selectpanel.add(exit);
    selectpanel.add(moveUp);
    selectpanel.add(moveDown);
    selectpanel.add(delete);

    //
    // selectpanel.add(checkBlacktxt);
    // selectpanel.add(checkBlack);
    // selectpanel.add(checkWhitetxt);
    // selectpanel.add(checkWhite);
    // selectpanel.add(dropwinratechoosertxt);
    // selectpanel.add(dropwinratechooser);
    // selectpanel.add(playoutschoosertxt);
    // selectpanel.add(playoutschooser);

    // playoutschooser.addChangeListener(
    // new ChangeListener() {
    //
    // public void stateChanged(ChangeEvent e) {
    //
    // Lizzie.config.leelazConfig.putOpt(
    // "badmoves-playouts-limits", playoutschooser.getValue());
    // try {
    // Lizzie.config.save();
    // } catch (IOException e1) {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // }
    // }
    // });
    //
    // dropwinratechooser.addChangeListener(
    // new ChangeListener() {
    //
    // public void stateChanged(ChangeEvent e) {
    //
    // Lizzie.config.leelazConfig.putOpt(
    // "badmoves-winrate-limits", dropwinratechooser.getValue());
    // try {
    // Lizzie.config.save();
    // } catch (IOException e1) {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // }
    // }
    // });

    scan.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            String el = getEngineLine();
            if (!el.isEmpty()) {
              command.setText(el);
              txtName.setText(engineSacnName);
            }
          }
        });
    delete.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            command.setText("");
            saveEngineConfig();
            command.setText("");
            engineName.setText("单击选中列表中的启动项进行设置");
            txtName.setText("");
            engineName.setEnabled(false);
            txtName.setEnabled(false);
            command.setEnabled(false);
            moveUp.setEnabled(false);
            moveDown.setEnabled(false);
            delete.setEnabled(false);
            moveUp.setEnabled(false);
            moveDown.setEnabled(false);
            scan.setEnabled(false);
            cancel.setEnabled(false);
            curIndex = -1;
            table.validate();
            table.updateUI();
            table.getSelectionModel().clearSelection();
          }
        });
    exit.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            engjf.setVisible(false);
          }
        });
    cancel.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            command.setText("");
            engineName.setText("单击选中列表中的启动项进行设置");
            txtName.setText("");
            curIndex = -1;
            engineName.setEnabled(false);
            txtName.setEnabled(false);
            command.setEnabled(false);
            delete.setEnabled(false);
            scan.setEnabled(false);
            cancel.setEnabled(false);
            moveUp.setEnabled(false);
            moveDown.setEnabled(false);
            table.getSelectionModel().clearSelection();
          }
        });
    save.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (curIndex >= 0) saveEngineConfig();
            table.validate();
            table.updateUI();
          }
        });
    table.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());

            handleTableClick(row, col);
          }
        });
    table.addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            // if (e.getKeyCode() == KeyEvent.VK_B) {
            // Lizzie.frame.toggleBadMoves();
            // }
            // if (e.getKeyCode() == KeyEvent.VK_U) {
            // Lizzie.frame.toggleBestMoves();
            // }
            // if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // if (Lizzie.frame.isPlayingAgainstLeelaz) {
            // Lizzie.frame.isPlayingAgainstLeelaz = false;
            // Lizzie.leelaz.isThinking = false;
            // }
            // Lizzie.leelaz.togglePonder();
            // }
            // if (e.getKeyCode() == KeyEvent.VK_Q) {
            // togglealwaysontop();
            // }
          }
        });

    header.addMouseListener(
        new MouseAdapter() {
          public void mouseReleased(MouseEvent e) {
            // int pick = header.columnAtPoint(e.getPoint());
            // sortnum = pick;
            // issorted = !issorted;
          }
        });

    moveUp.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            ArrayList<ProgramData> programData = getProgramData();
            if (curIndex < 1 || curIndex > programData.size() - 1) return;
            ProgramData enginedt = programData.get(curIndex);
            int a = curIndex;
            programData.remove(curIndex);
            programData.add(curIndex - 1, enginedt);

            JSONArray commands = new JSONArray();
            JSONArray names = new JSONArray();
            for (int i = 0; i < programData.size(); i++) {
              ProgramData proDt = programData.get(i);
              commands.put(proDt.commands.trim());
              names.put(proDt.name);
            }
            Lizzie.config.leelazConfig.put("engine-command-list", commands);
            Lizzie.config.leelazConfig.put("engine-name-list", names);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block

            }
            // Lizzie.engineManager.updateEngines();
            Lizzie.frame.menu.updateFastLinks();
            table.validate();
            table.updateUI();
            curIndex = curIndex - 1;
          }
        });
    moveDown.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub

            ArrayList<ProgramData> programData = getProgramData();
            if (curIndex < 0 || curIndex > programData.size() - 2) return;
            ProgramData enginedt = programData.get(curIndex);
            int a = curIndex;
            programData.remove(curIndex);
            programData.add(curIndex + 1, enginedt);

            JSONArray commands = new JSONArray();
            JSONArray names = new JSONArray();
            for (int i = 0; i < programData.size(); i++) {
              ProgramData proDt = programData.get(i);

              commands.put(proDt.commands.trim());
              names.put(proDt.name);
            }
            Lizzie.config.leelazConfig.put("engine-command-list", commands);
            Lizzie.config.leelazConfig.put("engine-name-list", names);
            try {
              Lizzie.config.save();
            } catch (IOException es) {
              // TODO Auto-generated catch block

            }
            // Lizzie.engineManager.updateEngines();
            Lizzie.frame.menu.updateFastLinks();
            table.validate();
            table.updateUI();
            curIndex = curIndex + 1;
          }
        });
  }

  class ColorTableCellRenderer extends DefaultTableCellRenderer {
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

      // if (Lizzie.board.convertNameToCoordinates(table.getValueAt(row,
      // 2).toString())[0]
      // == Lizzie.frame.clickbadmove[0]
      // && Lizzie.board.convertNameToCoordinates(table.getValueAt(row,
      // 2).toString())[1]
      // == Lizzie.frame.clickbadmove[1]) {

      // Color hsbColor =
      // Color.getHSBColor(
      // Color.RGBtoHSB(238, 221, 130, null)[0],
      // Color.RGBtoHSB(238, 221, 130, null)[1],
      // Color.RGBtoHSB(238, 221, 130, null)[2]);
      // setBackground(hsbColor);
      // if (Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) >= 5
      // && Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) <= 10) {
      // Color hsbColor2 =
      // Color.getHSBColor(
      // Color.RGBtoHSB(255, 153, 18, null)[0],
      // Color.RGBtoHSB(255, 153, 18, null)[1],
      // Color.RGBtoHSB(255, 153, 18, null)[2]);
      // setForeground(hsbColor2);
      // } else if (Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) >
      // 10) {
      // setForeground(Color.RED);
      // } else {
      // setForeground(Color.BLACK);
      // }
      // return super.getTableCellRendererComponent(table, value, isSelected, false,
      // row,
      // column);
      // }
      // if (Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) >= 5
      // && Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) <= 10) {
      // Color hsbColor =
      // Color.getHSBColor(
      // Color.RGBtoHSB(255, 153, 18, null)[0],
      // Color.RGBtoHSB(255, 153, 18, null)[1],
      // Color.RGBtoHSB(255, 153, 18, null)[2]);
      // setBackground(Color.WHITE);
      // setForeground(hsbColor);
      // return super.getTableCellRendererComponent(table, value, isSelected, false,
      // row,
      // column);
      // }
      // if (Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) > 10) {
      // setBackground(Color.WHITE);
      // setForeground(Color.RED);
      // return super.getTableCellRendererComponent(table, value, isSelected, false,
      // row,
      // column);
      // } else
      if (row == curIndex) {
        return renderer.getTableCellRendererComponent(table, value, true, false, row, column);
      } else {
        return renderer.getTableCellRendererComponent(table, value, false, false, row, column);
      }
    }
  }

  // private void togglealwaysontop() {
  // if (engjf.isAlwaysOnTop()) {
  // engjf.setAlwaysOnTop(false);
  // Lizzie.config.uiConfig.put("badmoves-always-ontop", false);
  // } else {
  // engjf.setAlwaysOnTop(true);
  // Lizzie.config.uiConfig.put("badmoves-always-ontop", true);
  // if (Lizzie.frame.isAlwaysOnTop()) Lizzie.frame.toggleAlwaysOntop();
  // }
  // try {
  // Lizzie.config.save();
  // } catch (IOException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }

  public boolean isWindows() {
    return osName != null && !osName.contains("darwin") && osName.contains("win");
  }

  private String getEngineLine() {
    String engineLine = "";
    File engineFile = null;
    JFileChooser chooser = new JFileChooser(".");
    if (isWindows()) {
      FileNameExtensionFilter filter =
          new FileNameExtensionFilter(
              resourceBundle.getString("LizzieConfig.title.engine"), "exe", "bat");
      chooser.setFileFilter(filter);
    } else {
      setVisible(false);
    }
    chooser.setMultiSelectionEnabled(false);
    chooser.setDialogTitle(resourceBundle.getString("LizzieConfig.prompt.selectEngine"));
    int result = chooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      engineFile = chooser.getSelectedFile();
      if (engineFile != null) {
        enginePath = engineFile.getAbsolutePath();
        engineSacnName = engineFile.getName();
        return enginePath;
      }
    }
    return engineLine;
  }

  private String relativizePath(Path path) {
    Path relatPath;
    if (path.startsWith(curPath)) {
      relatPath = curPath.relativize(path);
    } else {
      relatPath = path;
    }
    return relatPath.toString();
  }

  private void getCommandHelp() {

    List<String> commands = new ArrayList<String>();
    commands.add(enginePath);
    commands.add("-h");

    ProcessBuilder processBuilder = new ProcessBuilder(commands);
    processBuilder.directory();
    processBuilder.redirectErrorStream(true);
    try {
      Process process = processBuilder.start();
      inputStream = new BufferedInputStream(process.getInputStream());
      ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
      executor.execute(this::read);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void read() {
    try {
      int c;
      StringBuilder line = new StringBuilder();
      while ((c = inputStream.read()) != -1) {
        line.append((char) c);
      }
      commandHelp = line.toString();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleTableClick(int row, int col) {
    // if (selectedorder != row) {
    // int[] coords = Lizzie.board.convertNameToCoordinates(table.getValueAt(row,
    // 2).toString());
    // Lizzie.frame.clickbadmove = coords;
    // Lizzie.frame.boardRenderer.drawbadstone(coords[0], coords[1], Stone.BLACK);
    // Lizzie.frame.repaint();
    // selectedorder = row;
    // } else {
    // Lizzie.frame.clickbadmove = Lizzie.frame.outOfBoundCoordinate;
    // Lizzie.frame.boardRenderer.removedrawmovestone();
    // Lizzie.frame.repaint();
    // selectedorder = -1;
    // table.clearSelection();
    // }
    command.setText(table.getModel().getValueAt(row, 2).toString());
    engineName.setText("设置启动项" + table.getModel().getValueAt(row, 0).toString());
    txtName.setText(table.getModel().getValueAt(row, 1).toString());
    curIndex = Integer.parseInt(table.getModel().getValueAt(row, 0).toString()) - 1;
    engineName.setEnabled(true);
    txtName.setEnabled(true);
    command.setEnabled(true);
    delete.setEnabled(true);
    save.setEnabled(true);
    cancel.setEnabled(true);
    scan.setEnabled(true);
    moveUp.setEnabled(true);
    moveDown.setEnabled(true);
    table.validate();
    table.updateUI();
  }

  private void handleTableDoubleClick(int row, int col) {
    // int movenumber = Integer.parseInt(table.getValueAt(row, 1).toString());
    // Lizzie.board.goToMoveNumber(1);
    // Lizzie.board.goToMoveNumber(movenumber - 1);
    // int[] coords = Lizzie.board.convertNameToCoordinates(table.getValueAt(row,
    // 2).toString());
    // Lizzie.frame.clickbadmove = coords;
    // Lizzie.frame.boardRenderer.drawbadstone(coords[0], coords[1], Stone.BLACK);
    // Lizzie.frame.repaint();
    // selectedorder = row;
  }

  private void saveEngineConfig() {
    ArrayList<ProgramData> programData = getProgramData();
    ProgramData programDt = new ProgramData();
    programDt.index = curIndex;
    programDt.commands = this.command.getText();
    programDt.name = this.txtName.getText();

    if (curIndex + 1 > programData.size()) {
      programData.add(programDt);
    } else {
      programData.remove(curIndex);
      programData.add(curIndex, programDt);
    }
    JSONArray commands = new JSONArray();
    JSONArray names = new JSONArray();
    for (int i = 0; i < programData.size(); i++) {
      ProgramData proDt = programData.get(i);
      if (!commands.equals("")) {
        commands.put(proDt.commands.trim());
        names.put(proDt.name);
      }
    }
    Lizzie.config.leelazConfig.put("program-command-list", commands);
    Lizzie.config.leelazConfig.put("program-name-list", names);
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // Lizzie.engineManager.updateEngines();
    Lizzie.frame.menu.updateFastLinks();
  }

  public ArrayList<ProgramData> getProgramData() {
    ArrayList<ProgramData> ProgramData = new ArrayList<ProgramData>();
    Optional<JSONArray> enginesCommandOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("program-command-list"));
    Optional<JSONArray> enginesNameOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("program-name-list"));

    for (int i = 0;
        i < (enginesCommandOpt.isPresent() ? enginesCommandOpt.get().length() : 0);
        i++) {
      String commands = enginesCommandOpt.get().getString(i);
      if (!commands.equals("")) {
        String name = enginesNameOpt.isPresent() ? enginesNameOpt.get().optString(i, "") : "";
        ProgramData programDt = new ProgramData();
        programDt.commands = commands;
        programDt.name = name;
        programDt.index = i;
        ProgramData.add(programDt);
      }
    }
    return ProgramData;
  }

  public AbstractTableModel getTableModel() {

    return new AbstractTableModel() {
      public int getColumnCount() {

        return 3;
      }

      public int getRowCount() {

        return 30;
      }

      public String getColumnName(int column) {

        if (column == 0) return "序号";
        if (column == 1) return "名称";
        if (column == 2) return "命令行";

        return "无";
      }

      @SuppressWarnings("unchecked")
      public Object getValueAt(int row, int col) {
        ArrayList<ProgramData> ProgramDatas = getProgramData();
        if (row > (ProgramDatas.size() - 1)) {
          if (col == 0) return row + 1;
          return "";
        }
        ProgramData data = ProgramDatas.get(row);

        if (col != 0 && data.commands.equals("")) {
          return "";
        }

        switch (col) {
          case 0:
            return row + 1;
          case 1:
            return data.name;
          case 2:
            return data.commands;
          default:
            return "";
        }
      }
    };
  }

  public static JDialog createDialog() {
    // Create and set up the window.
    engjf = new JDialog();
    engjf.setTitle("快速启动设置");

    engjf.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            engjf.setVisible(false);
          }
        });

    final OtherPrograms newContentPane = new OtherPrograms();
    newContentPane.setOpaque(true); // content panes must be opaque
    engjf.setContentPane(newContentPane);
    // Display the window.
    // jf.setSize(521, 320);

    // boolean persisted = Lizzie.config.persistedUi != null;
    // if (persisted
    // && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position") != null
    // && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position").length()
    // >= 4) {
    // JSONArray pos =
    // Lizzie.config.persistedUi.getJSONArray("badmoves-list-position");
    // // jf.setBounds(pos.getInt(0), pos.getInt(1), pos.getInt(2), pos.getInt(3));
    // engjf.setBounds(50, 50, 900, 800);
    // } else {
    engjf.setBounds(50, 50, 900, 800);
    engjf.setResizable(false);
    // }
    try {
      engjf.setIconImage(ImageIO.read(OtherPrograms.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    engjf.setAlwaysOnTop(true);
    engjf.setLocationRelativeTo(engjf.getOwner());
    // jf.setResizable(false);
    return engjf;
  }
}
