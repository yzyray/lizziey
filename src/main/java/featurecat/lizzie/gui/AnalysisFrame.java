package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.MoveData;
import featurecat.lizzie.analysis.MoveDataSorter;
import featurecat.lizzie.rules.Movelist;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings("serial")

public class AnalysisFrame extends JPanel  {
 
	TableModel dataModel;
	JScrollPane scrollpane;
	JTable table;
	Timer timer;
	
	
	public  AnalysisFrame() {
		dataModel = getTableModel();
		table = new JTable(dataModel);
		table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 16));
		table.setFont(new Font("宋体", 0, 16));
		scrollpane = new JScrollPane(table);
		//table.setAutoCreateRowSorter(true);
		
		timer=new Timer(100,new ActionListener() {			
			public void actionPerformed(ActionEvent evt) {
				dataModel.getColumnCount();
				table.validate();
				table.updateUI();
			}
		});
		timer.start();
		this.add(scrollpane);
		JTableHeader header = table.getTableHeader();
		header.addMouseListener (new MouseAdapter() {
    public void mouseReleased (MouseEvent e) {
    	int pick = header.columnAtPoint(e.getPoint());
    	ArrayList<MoveData> data2=new ArrayList<MoveData>();
    	for(int i=0;i<Lizzie.board.getData().bestMoves.size();i++)
		{
			
			data2.add(Lizzie.board.getData().bestMoves.get(i)) ; 
			
		}
		//Collections.sort(data2) ; 
		
		featurecat.lizzie.analysis.MoveDataSorter MoveDataSorter = new MoveDataSorter(data2);
		 ArrayList sortedMoveData = MoveDataSorter.getSortedMoveDataByPolicy();
		int a=1;
    }
});
	}


	//使用public List<String> getData() 方法得到的List构建数据模型
	//此处使用的外部文件中，每一行的字符串用空格分成四个部分
	//例如，其中一行为：2013-03-18 11:50:55   传感器1    报警，对应表格的一行
	public AbstractTableModel getTableModel() {
		
		return new AbstractTableModel() {
			public int getColumnCount() {
				return 5;
			}
			public int getRowCount() {
				 return Lizzie.board.getData().bestMoves.size();
			}
			@SuppressWarnings("unchecked")
			public Object getValueAt(int row, int col) {
				ArrayList<MoveData> data2=new ArrayList<MoveData>();
				for(int i=0;i<Lizzie.board.getData().bestMoves.size();i++)
				{
					
					data2.add(Lizzie.board.getData().bestMoves.get(i)) ; 
					
				}
				
		//		Collections.sort(data2) ; 
				Collections.sort(data2, new Comparator<MoveData>() {

					@Override
					public int compare(MoveData s1, MoveData s2) {
						// 降序						
						if(s1.lcb<s2.lcb) 
							return 1; 
						if(s1.lcb>s2.lcb) 
						return -1; 
						else 
						return 0; 
					}});
					
			
				//featurecat.lizzie.analysis.MoveDataSorter MoveDataSorter = new MoveDataSorter(data2);
				// ArrayList sortedMoveData = MoveDataSorter.getSortedMoveDataByPolicy();
				 MoveData data = data2.get(row);
				switch (col) {
				 case 0:
		                return data.coordinate;
		            case 1:
		                return data.lcb;
		            case 2:
		                return data.oriwinrate;
		            case 3:
		                return (double)data.playouts;
		            case 4:
		                return data.policy;
		            default:
		                return "";
				}
			
			}
		};
	}
	
	

	
	 public static JDialog createAnalysisDialog(JFrame owner) {
	        // Create and set up the window.
	        JDialog dialog = new JDialog(owner, "测试");

	        // Create and set up the content pane.
	        final AnalysisFrame newContentPane = new AnalysisFrame();
	        newContentPane.setOpaque(true); // content panes must be opaque
	        dialog.setContentPane(newContentPane);

	        // Display the window.
	        dialog.setSize(800, 600);

	        // Handle close event
	        dialog.addComponentListener(new ComponentAdapter() {
	            @Override
	            public void componentHidden(ComponentEvent e) {
	         //       newContentPane.getAnalysisTableModel().setSelectedMove(null);
//	                Lizzie.optionSetting.setAnalysisWindowShow(false);
	            }
	        });

	        return dialog;
	    }
}
    
