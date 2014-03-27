package clueTests;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import clueGame.Board;
import clueGame.BoardCell;

public class OurAdjacencyAndTargetTests {
	private static Board board;
	@BeforeClass
	public static void setUp() {
		board = new Board("OurLayout.csv", "OurLegend.txt");
		board.loadConfigFiles();
		board.calcAdjacencies();
	}

	// Test adjacencies for only adjacent walkways
	@Test
	public void onlyAdjacentWalkways()
	{
		HashSet<Integer> testList = board.getAdjList(board.calcIndex(14, 13));
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.calcIndex(13, 13)));
		assertTrue(testList.contains(board.calcIndex(15,13)));
		assertTrue(testList.contains(board.calcIndex(14, 12)));
		assertTrue(testList.contains(board.calcIndex(14,14)));
	}

	// Test adjacencies along each edge of the map
	@Test
	public void testAdjacencyRoomExit()
	{
		assertEquals(1, board.getAdjList(board.calcIndex(17, 3)).size());

		assertEquals(1, board.getAdjList(board.calcIndex(18, 4)).size());

		assertEquals(1, board.getAdjList(board.calcIndex(10, 14)).size());

		assertEquals(1, board.getAdjList(board.calcIndex(5, 12)).size());
		
		
	}
	
	// Locations besides room cells that aren't doorways
	@Test
	public void testAdjacencyRoomNotDoorways()
	{
		HashSet<Integer> testList = board.getAdjList(board.calcIndex(2, 4));
		assertEquals(3, testList.size());
		testList = board.getAdjList(board.calcIndex(15, 16));
		assertEquals(3, testList.size());
	}
	
	@Test
	public void testDoorDirection()
	{
		HashSet<Integer> testList = board.getAdjList(board.calcIndex(15, 6));
		assertEquals(3, testList.size());
	}

	// Test adjacent to doorway
	@Test
	public void testAdjacencyNextToDoorways()
	{
		HashSet<Integer> testList = board.getAdjList(board.calcIndex(17, 5));
		assertEquals(4, testList.size());
		testList = board.getAdjList(board.calcIndex(4, 7));
		assertEquals(4, testList.size());
	}
	
	// Locations that are doorways
	@Test
	public void testAdjacencyIsDoorway() {
		HashSet<Integer> testList = board.getAdjList(board.calcIndex(14, 8));
		assertEquals(1, testList.size());
		testList = board.getAdjList(board.calcIndex(3, 7));
		assertEquals(1, testList.size());
	}
	
	// Test targets along walkways, two steps
	@Test
	public void testTargetsTwoSteps() {
		board.calcTargets(4, 2, 2);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(5, targets.size());
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(4, 0))));
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(5, 1))));
		
		board.calcTargets(6, 17, 2);
		targets= board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(6, 15))));
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(5, 18))));			
	}
	
	// Test targets along walkways, four steps
	@Test
	public void testTargetsFourSteps() {
		board.calcTargets(7, 7, 4);
		Set<BoardCell> targets= board.getTargets();
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(3, 7))));
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(8, 5))));
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(11, 7))));
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(9, 7))));
		
		board.calcTargets(13, 7, 4);
		targets= board.getTargets();
		assertEquals(15, targets.size());
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(9, 7))));
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(11, 7))));	
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(14, 8))));	
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(15, 7))));	
	}	
	
	// Targets allowing room entry
	@Test
	public void testEntry() {
		board.calcTargets(14, 5, 3);
		Set<BoardCell> targets= board.getTargets();
		Assert.assertEquals(12, targets.size());
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(13, 5))));
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(16, 6))));	
		
		board.calcTargets(7, 13, 4);
		targets = board.getTargets();
		Assert.assertEquals(16, targets.size());
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(5, 12))));
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(10, 14))));	
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(9, 13))));	
	}	
	
	// Targets calculated when leaving a room

	@Test 
	public void testTargetsOutOfRoom()
	{
		board.calcTargets(10, 14, 2);
		Set<BoardCell> targets= board.getTargets();
		Assert.assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(9, 13))));
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(11, 13))));	
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(10, 12))));	
		
		board.calcTargets(15, 7, 1);
		targets = board.getTargets();
		Assert.assertEquals(1, targets.size());
		assertTrue(targets.contains(board.getCellAt(board.calcIndex(14, 7))));
	}
}