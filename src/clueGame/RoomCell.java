// Chris Butler
// Ben Sattelberg
package clueGame;

import java.awt.Color;
import java.awt.Graphics;

public class RoomCell extends BoardCell {

	private DoorDirection doorDirection;
	private char room;
	//private boolean nameCell;
	
	public RoomCell(String thing, int row, int column) {
		super(row, column);
		room = thing.charAt(0);
		if (thing.length() == 1) {
			doorDirection = DoorDirection.NONE;
		} else if (thing.length() == 2) {
			switch (thing.charAt(1)) {
			case 'U': case 'u':
				doorDirection = DoorDirection.UP;
				break;
			case 'D': case 'd':
				doorDirection = DoorDirection.DOWN;
				break;
			case 'L': case 'l':
				doorDirection = DoorDirection.LEFT;
				break;
			case 'R': case 'r':
				doorDirection = DoorDirection.RIGHT;
				break;
			// We don't understand the necessity of this
			case 'N': case 'n':
				doorDirection = DoorDirection.NONE;
				break;
			default:
				throw new BadConfigFormatException("Unknown direction: '" + thing + "'");
			}
		} else {
			throw new BadConfigFormatException("Expected string of length 1 or 2.");
		}
		//nameCell = false;
	}

	@Override
	public boolean isRoom() {
		return true;
	}
	/*public void setNameCell(boolean nameCell) {
		this.nameCell = nameCell;
	}*/

	@Override
	public boolean isDoorway() {
		return doorDirection != DoorDirection.NONE;
	}

	public DoorDirection getDoorDirection() {
		return doorDirection;
	}

	public char getInitial() {
		return room;
	}

	@Override
	public void draw(Graphics g, Board b) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(getColumn() * 30, getRow() * 30, 30, 30);
		if(isDoorway()) {
			g.setColor(Color.BLACK);
			switch(doorDirection) {
			case LEFT:
				g.fillRect(getColumn() * 30, getRow() * 30, 4, 30);
				break;
			case RIGHT:
				g.fillRect((getColumn() + 1) * 30 - 4, getRow() * 30, 4, 30);
				break;
			case UP:
				g.fillRect(getColumn() * 30, getRow() * 30, 30, 4);
				break;
			case DOWN:
				g.fillRect(getColumn() * 30, (getRow() + 1) * 30 - 4, 30, 4);
				break;
			default:
				break;
			}
		}
		if(b.getNameCells().contains(b.calcIndex(getRow(), getColumn()))) {
			g.setColor(Color.BLACK);
			g.drawString(b.getRooms().get(room),  getColumn() * 30, getRow() * 30);
		}
	}

}
