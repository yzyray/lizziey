package featurecat.lizzie.gui;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.table.TableCellRenderer;

import featurecat.lizzie.Lizzie;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JOptionPane;
import javax.swing.table.TableCellEditor;


public class AnalysisTable {

    public JFrame frame;
    private JTable table;
    
//    private Object[][] data = {
//            {1, 2, 3},
//            {4, 5, 6},
//            {7, 8, 9}};
//    


    public AnalysisTable() {
        frame = new JFrame();
        frame.setTitle("批量分析排队列表");
        frame.setBounds(0, 0, 420, 320);
        frame.setLocationRelativeTo(Lizzie.frame);
       // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        JButton addnew=new JButton ("添加");
        addnew.setBounds(10, 2, 80, 25);
        frame.add(addnew);
        JPanel panel = new JPanel();
        panel.setBounds(10, 30, 414, 282);
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 0, 384, 242);
        panel.add(scrollPane);

        table = new JTable();
        scrollPane.setViewportView(table);
     

        table.setModel(new DefaultTableModel() {
            @Override
            public Object getValueAt(int row, int column) {
            	switch (column) {
            	case 0: return row+1;
            	case 1:return Lizzie.frame.Batchfiles[row].getName();
            	
            	}
            	
                return "";
            }

            @Override
            public int getRowCount() {
                return Lizzie.frame.Batchfiles.length;
            }
            
            @Override
            public String getColumnName(int column) {
                if (column == 0) return "序号";
                if (column == 1) return "文件名";
                if (column == 2) return "优先";
                return "无";
              }

            @Override
            public int getColumnCount() {
                return 3;
            }
            @Override
            public void setValueAt(Object aValue, int row, int column){
               // data[row][column] = aValue;
             //   fireTableCellUpdated(row, column);
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 2) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        table.getColumnModel().getColumn(2).setCellEditor(
                new MyButtonEditor());

        table.getColumnModel().getColumn(2).setCellRenderer(
                new MyButtonRenderer());

        table.setRowSelectionAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(36);
        table.getColumnModel().getColumn(1).setPreferredWidth(114);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        
        
    }
    public void refreshTable() {
  	  table.validate();
        table.updateUI();
  }
}

 class MyButtonRenderer implements TableCellRenderer {
    private JPanel panel;

    private JButton button;
    
    private int num;

    public MyButtonRenderer() {
        initButton();

        initPanel();

        panel.add(button, BorderLayout.CENTER);
    }

    private void initButton() {
        button = new JButton();

    }

    private void initPanel() {
        panel = new JPanel();

        panel.setLayout(new BorderLayout());
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
      //  num = (Integer) value;
        
      //  button.setText(value == null ? "" : String.valueOf(value));
    	  button.setText("最优先");
        return panel;
    }

}
 
 class MyButtonEditor extends AbstractCellEditor implements
 
 TableCellEditor {

/**
* serialVersionUID
*/
private static final long serialVersionUID = -6546334664166791132L;

private JPanel panel;

private JButton button;

private int num;

public MyButtonEditor() {

 initButton();

 initPanel();

 panel.add(this.button, BorderLayout.CENTER);
}

private void initButton() {
 button = new JButton();

 button.addActionListener(new ActionListener() {
     public void actionPerformed(ActionEvent e) {
//         int res = JOptionPane.showConfirmDialog(null,
//                 "Do you want to add 1 to it?", "choose one",
//                 JOptionPane.YES_NO_OPTION);
         //在这里交换FILE顺序,并刷新table
   // 	 this.refreshTable();
//         if(res ==  JOptionPane.YES_OPTION){
//             num++;
//         }
         //stopped!!!!
       //  fireEditingStopped();

     }
 });

}

private void initPanel() {
 panel = new JPanel();

 panel.setLayout(new BorderLayout());
}

@Override
public Component getTableCellEditorComponent(JTable table, Object value,
     boolean isSelected, int row, int column) {
// num = (Integer) value;
 
// button.setText(value == null ? "" : String.valueOf(value));
	 button.setText("最优先1");
	 //在这里取ROW
 return panel;
}

@Override
public Object getCellEditorValue() {
	// TODO Auto-generated method stub
	return null;
}

//@Override
//public Object getCellEditorValue() {
 //return num;
//}

}