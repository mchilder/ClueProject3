// Chris Butler
// Ben Sattelberg
package clueGame;

import java.awt.Graphics;

import javax.swing.JComponent;

public abstract class BoardCell extends JComponent{
	
	private int row;
	private int column;
	
	public BoardCell() {
		super();
	}

	public BoardCell(int row, int column) {
		super();
		this.row = row;
		this.column = column;
	}

	public boolean isWalkway(){
		return false;
	}
	
	public boolean isRoom(){
		return false;
	}
	
	public boolean isDoorway(){
		return false;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof BoardCell)) return false;
		BoardCell b = (BoardCell)o;
		return (row == b.row && column == b.column);
	}

	@Override
	public String toString() {
		return "BoardCell [row=" + row + ", column=" + column + "]";
	}
	
	public abstract void draw(Graphics g, Board b);
}
