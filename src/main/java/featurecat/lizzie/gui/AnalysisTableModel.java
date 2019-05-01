package featurecat.lizzie.gui;

import com.google.common.collect.ImmutableList;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.MoveData;
import featurecat.lizzie.rules.Board;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;


public class AnalysisTableModel extends AbstractTableModel {

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		
		return Lizzie.board.getData().bestMoves.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		
		return 5;
	}
	

	 

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		  MoveData data = Lizzie.board.getData().bestMoves.get(rowIndex);
	        switch (columnIndex) {
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

 
}
