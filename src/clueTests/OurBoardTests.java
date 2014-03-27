package clueTests;

//import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import clueGame.BadConfigFormatException;
import clueGame.Board;
import clueGame.BoardCell;
import clueGame.DoorDirection;
import clueGame.RoomCell;

public class OurBoardTests {

	private static Board board;
	public static final int NUM_ROOMS = 11;
	public static final int NUM_ROWS = 20;
	public static final int NUM_COLUMNS = 20;

	@BeforeClass
	public static void setUp() {
		board = new Board("OurLayout.csv", "OurLegend.txt");
		board.loadConfigFiles();
	}

	@Test
	public void testRooms() {
		Map<Character, String> rooms = board.getRooms();

		assertEquals(NUM_ROOMS, rooms.size());

		assertEquals("Lemon Showroom", rooms.get('L'));
		assertEquals("Ryan's Room", rooms.get('R'));
		assertEquals("Vulcan Museum", rooms.get('V'));
		assertEquals("Hallway", rooms.get('W'));
		assertEquals("Mattress Emporium", rooms.get('B'));
	}

	@Test
	public void testBoardDimensions() {
		assertEquals(NUM_ROWS, board.getRows());
		assertEquals(NUM_COLUMNS, board.getColumns());	
	}

	@Test
	public void FourDoorDirections() {
		RoomCell room = board.getRoomCellAt(5, 12);
		assertTrue(room.isDoorway());
		assertEquals(DoorDirection.RIGHT, room.getDoorDirection());

		room = board.getRoomCellAt(3, 3);
		assertTrue(room.isDoorway());
		assertEquals(DoorDirection.DOWN, room.getDoorDirection());

		room = board.getRoomCellAt(10, 14);
		assertTrue(room.isDoorway());
		assertEquals(DoorDirection.LEFT, room.getDoorDirection());

		room = board.getRoomCellAt(15, 7);
		assertTrue(room.isDoorway());
		assertEquals(DoorDirection.UP, room.getDoorDirection());

		// Test that room pieces that aren't doors know it
		room = board.getRoomCellAt(3, 0);
		assertFalse(room.isDoorway());	

		// Test that walkways are not doors
		BoardCell cell = board.getCellAt(board.calcIndex(5, 0));
		assertFalse(cell.isDoorway());	
	}

	@Test
	public void testNumberOfDoorways() 
	{
		int numDoors = 0;
		int totalCells = board.getColumns() * board.getRows();
		Assert.assertEquals(400, totalCells);
		for (int i=0; i<totalCells; i++)
		{
			BoardCell cell = board.getCellAt(i);
			if (cell.isDoorway())
				numDoors++;
		}
		Assert.assertEquals(16, numDoors);
	}

	@Test
	public void testCalcIndex() {
		// Test each corner of the board
		assertEquals(0, board.calcIndex(0, 0));
		assertEquals(NUM_COLUMNS-1, board.calcIndex(0, NUM_COLUMNS-1));
		assertEquals(380, board.calcIndex(NUM_ROWS-1, 0));
		assertEquals(399, board.calcIndex(NUM_ROWS-1, NUM_COLUMNS-1));
		// Test a couple others
		assertEquals(21, board.calcIndex(1, 1));
		assertEquals(60, board.calcIndex(2, 20));		
	}

	@Test
	public void testRoomInitials() {
		assertEquals('R', board.getRoomCellAt(0, 0).getInitial());
		assertEquals('B', board.getRoomCellAt(8, 4).getInitial());
		assertEquals('P', board.getRoomCellAt(0, 9).getInitial());
		assertEquals('C', board.getRoomCellAt(19, 19).getInitial());
		assertEquals('O', board.getRoomCellAt(0, 19).getInitial());
	}

	@Test (expected = BadConfigFormatException.class)
	public void testBadRoom() throws BadConfigFormatException {
		Board b = new Board("OurBadLayout.csv", "OurLegend.txt");
		b.loadRoomConfig();
		b.loadBoardConfig();
	}
	
	@Test (expected = BadConfigFormatException.class)
	public void testBadRoomFormat() throws BadConfigFormatException, FileNotFoundException {
		Board b = new Board("OurLayout.csv", "OurBadLegend.txt");
		b.loadRoomConfig();
		b.loadBoardConfig();
	}
}
