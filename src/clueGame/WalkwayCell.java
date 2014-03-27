// Chris Butler
// Ben Sattelberg
package clueGame;

import java.awt.Color;
import java.awt.Graphics;

public class WalkwayCell extends BoardCell {

	public WalkwayCell(int row, int column) {
		super(row, column);
		// TODO Auto-generated constructor stub
	}

	public boolean isWalkway(){
		return true;
	}

	@Override
	public void draw(Graphics g, Board b) {
		g.setColor(Color.YELLOW);
		g.fillRect(getColumn() * 30, getRow() * 30, 30, 30);
		g.setColor(Color.BLACK);
		g.drawRect(getColumn() * 30 , getRow() * 30, 30, 30);
	}
}
