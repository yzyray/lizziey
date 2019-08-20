package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.json.JSONObject;

public class AnalysisTable {

  public JFrame frame;
  private JTable table;
  public int changeRow;

  //    private Object[][] data = {
  //            {1, 2, 3},
  //            {4, 5, 6},
  //            {7, 8, 9}};
  //

  public AnalysisTable() {
    frame = new JFrame();
    frame.setTitle("批量分析排队列表");
    frame.setBounds(0, 0, 620, 320);
    try {
      frame.setIconImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    frame.setLocationRelativeTo(Lizzie.frame);
    // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(null);
    JButton stopGo = new JButton();
    if (Lizzie.leelaz.isPondering()) stopGo.setText("暂停");
    else stopGo.setText("继续");
    stopGo.setBounds(10, 2, 80, 25);
    frame.add(stopGo);
    stopGo.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.leelaz.togglePonder();
            if (Lizzie.leelaz.isPondering()) stopGo.setText("暂停");
            else stopGo.setText("继续");
          }
        });

    JButton stopStart = new JButton();
    if (Lizzie.frame.toolbar.isAutoAna) stopStart.setText("终止");
    else stopStart.setText("开始");
    stopStart.setBounds(90, 2, 80, 25);
    frame.add(stopStart);
    stopStart.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.toolbar.isAutoAna) {
              stopStart.setText("开始");
              Lizzie.frame.toolbar.stopAutoAna();
            } else {
              stopStart.setText("终止");
              Lizzie.frame.toolbar.startAutoAna();
            }
          }
        });

    JButton addFile = new JButton("增加棋谱");
    addFile.setBounds(170, 2, 80, 25);
    frame.add(addFile);
    addFile.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
            FileDialog fileDialog = new FileDialog(Lizzie.frame, "选择棋谱");

            fileDialog.setLocationRelativeTo(Lizzie.frame);
            fileDialog.setDirectory(filesystem.getString("last-folder"));
            fileDialog.setFile("*.sgf;*.gib;*.SGF;*.GIB");

            fileDialog.setMultipleMode(true);
            fileDialog.setMode(0);
            fileDialog.setVisible(true);

            File[] files = fileDialog.getFiles();

            if (files.length > 0) {
              Lizzie.frame.isBatchAna = true;
              for (int i = 0; i < files.length; i++) {
                Lizzie.frame.Batchfiles.add(files[i]);
              }
              Lizzie.frame.analysisTable.refreshTable();
            }
          }
        });

    JPanel panel = new JPanel();
    panel.setBounds(10, 30, 614, 282);
    frame.getContentPane().add(panel);
    panel.setLayout(null);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(0, 0, 584, 242);
    panel.add(scrollPane);

    table = new JTable();
    scrollPane.setViewportView(table);

    table.setModel(
        new DefaultTableModel() {
          @Override
          public Object getValueAt(int row, int column) {
            switch (column) {
              case 0:
                if (row == 0) return "当前";
                else return row + 1;
              case 1:
                return Lizzie.frame.Batchfiles.get(row + Lizzie.frame.BatchAnaNum).getName();
            }

            return "";
          }

          @Override
          public int getRowCount() {
            if (Lizzie.frame.Batchfiles != null)
              return Lizzie.frame.Batchfiles.size() - Lizzie.frame.BatchAnaNum;
            else return 0;
          }

          @Override
          public String getColumnName(int column) {
            if (column == 0) return "序号";
            if (column == 1) return "文件名";
            if (column == 2) return "优先";
            if (column == 3) return "上移";
            if (column == 4) return "下移";
            if (column == 5) return "删除";
            return "无";
          }

          @Override
          public int getColumnCount() {
            return 6;
          }

          @Override
          public void setValueAt(Object aValue, int row, int column) {
            // data[row][column] = aValue;
            //   fireTableCellUpdated(row, column);
          }

          @Override
          public boolean isCellEditable(int row, int column) {
            if (row == 0) return false;
            if (column == 2 || column == 3 || column == 4 || column == 5) {
              return true;
            } else {
              return false;
            }
          }
        });

    table.getColumnModel().getColumn(2).setCellEditor(new MyButtonFirstEditor());

    table.getColumnModel().getColumn(2).setCellRenderer(new MyButtonFirst());

    table.getColumnModel().getColumn(3).setCellEditor(new MyButtonUpEditor());

    table.getColumnModel().getColumn(3).setCellRenderer(new MyButtonUp());

    table.getColumnModel().getColumn(4).setCellEditor(new MyButtonDownEditor());

    table.getColumnModel().getColumn(4).setCellRenderer(new MyButtonDown());

    table.getColumnModel().getColumn(5).setCellEditor(new MyButtonDeleteEditor());

    table.getColumnModel().getColumn(5).setCellRenderer(new MyButtonDelete());
    table.setRowSelectionAllowed(false);
    table.getColumnModel().getColumn(0).setPreferredWidth(40);
    table.getColumnModel().getColumn(1).setPreferredWidth(300);
    table.getColumnModel().getColumn(2).setPreferredWidth(40);
    table.getColumnModel().getColumn(3).setPreferredWidth(40);
    table.getColumnModel().getColumn(4).setPreferredWidth(40);
    table.getColumnModel().getColumn(5).setPreferredWidth(40);
  }

  public void refreshTable() {
    table.repaint();
    table.updateUI();
  }
}

class MyButtonFirst implements TableCellRenderer {
  private JPanel panel;
  private JButton button;

  public MyButtonFirst() {
    initButton();
    initPanel();
    panel.add(button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setMargin(new Insets(0, 0, 0, 0));
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    button.setText("优先");
    return panel;
  }
}

class MyButtonFirstEditor extends AbstractCellEditor implements TableCellEditor {
  private JPanel panel;
  private JButton button;

  public MyButtonFirstEditor() {
    initButton();
    initPanel();
    panel.add(this.button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setMargin(new Insets(0, 0, 0, 0));
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum == 0) return;
            File file =
                Lizzie.frame.Batchfiles.get(
                    Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum);
            Lizzie.frame.Batchfiles.remove(
                Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum);
            Lizzie.frame.Batchfiles.add(Lizzie.frame.BatchAnaNum + 1, file);
            Lizzie.frame.analysisTable.refreshTable();
          }
        });
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  @Override
  public Component getTableCellEditorComponent(
      JTable table, Object value, boolean isSelected, int row, int column) {
    button.setText("优先");
    Lizzie.frame.analysisTable.changeRow = row;
    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    // TODO Auto-generated method stub
    return null;
  }
}

class MyButtonUp implements TableCellRenderer {
  private JPanel panel;
  private JButton button;

  public MyButtonUp() {
    initButton();
    initPanel();
    panel.add(button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setMargin(new Insets(0, 0, 0, 0));
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    button.setText("上移");
    return panel;
  }
}

class MyButtonUpEditor extends AbstractCellEditor implements TableCellEditor {
  private JPanel panel;
  private JButton button;

  public MyButtonUpEditor() {
    initButton();
    initPanel();
    panel.add(this.button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setMargin(new Insets(0, 0, 0, 0));
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.analysisTable.changeRow <= 1) return;
            File file =
                Lizzie.frame.Batchfiles.get(
                    Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum);
            Lizzie.frame.Batchfiles.remove(
                Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum);
            Lizzie.frame.Batchfiles.add(
                Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum - 1, file);
            Lizzie.frame.analysisTable.refreshTable();
          }
        });
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  @Override
  public Component getTableCellEditorComponent(
      JTable table, Object value, boolean isSelected, int row, int column) {
    button.setText("上移");
    Lizzie.frame.analysisTable.changeRow = row;
    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    // TODO Auto-generated method stub
    return null;
  }
}

class MyButtonDown implements TableCellRenderer {
  private JPanel panel;
  private JButton button;

  public MyButtonDown() {
    initButton();
    initPanel();
    panel.add(button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
  }

  private void initPanel() {
    panel = new JPanel();
    button.setMargin(new Insets(0, 0, 0, 0));
    panel.setLayout(new BorderLayout());
  }

  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    button.setText("下移");
    return panel;
  }
}

class MyButtonDownEditor extends AbstractCellEditor implements TableCellEditor {
  private JPanel panel;
  private JButton button;

  public MyButtonDownEditor() {
    initButton();
    initPanel();
    panel.add(this.button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setMargin(new Insets(0, 0, 0, 0));
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum == 0) return;
            if ((Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum)
                >= (Lizzie.frame.Batchfiles.size() - 1)) return;
            File file =
                Lizzie.frame.Batchfiles.get(
                    Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum);
            Lizzie.frame.Batchfiles.remove(
                Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum);
            Lizzie.frame.Batchfiles.add(
                Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum + 1, file);
            Lizzie.frame.analysisTable.refreshTable();
          }
        });
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  @Override
  public Component getTableCellEditorComponent(
      JTable table, Object value, boolean isSelected, int row, int column) {
    button.setText("下移");
    Lizzie.frame.analysisTable.changeRow = row;
    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    // TODO Auto-generated method stub
    return null;
  }
}

class MyButtonDelete implements TableCellRenderer {
  private JPanel panel;
  private JButton button;

  public MyButtonDelete() {
    initButton();
    initPanel();
    panel.add(button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setMargin(new Insets(0, 0, 0, 0));
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    button.setText("删除");
    return panel;
  }
}

class MyButtonDeleteEditor extends AbstractCellEditor implements TableCellEditor {
  private JPanel panel;
  private JButton button;

  public MyButtonDeleteEditor() {
    initButton();
    initPanel();
    panel.add(this.button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setMargin(new Insets(0, 0, 0, 0));
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum == 0) return;
            Lizzie.frame.Batchfiles.remove(
                Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum);
            Lizzie.frame.analysisTable.refreshTable();
          }
        });
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  @Override
  public Component getTableCellEditorComponent(
      JTable table, Object value, boolean isSelected, int row, int column) {
    button.setText("删除");
    Lizzie.frame.analysisTable.changeRow = row;
    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    // TODO Auto-generated method stub
    return null;
  }
}
