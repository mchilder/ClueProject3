package clueTests;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import clueGame.*;

public class GameActionTests {
	ClueGame game;
	Board board;

	@Before
	public void setUp() {
		board = new Board("OurLayout.csv", "OurLegend.txt");
		board.calcAdjacencies();
		game = new ClueGame("PlayerFile.txt", "CardFile.txt", board);
		game.loadConfigFiles();

	}

	@Test
	public void testRandomTargetSelectionWalkway() {
		// test ensuring the computer player picks a random assortment of
		// possibilities from the set of possible targets
		board.calcTargets(7, 7, 2);
		Map<BoardCell, Integer> cellCount = new HashMap<BoardCell, Integer>();
		Set<BoardCell> targets = board.getTargets();
		for (BoardCell b : targets) {
			cellCount.put(b, 0);
		}
		ComputerPlayer us = (ComputerPlayer) game.getPlayers().get(1);
		us.setLocation(board.calcIndex(7, 7));
		for (int i = 0; i < 200; i++) {
			BoardCell b = us.pickLocation(board, 2);
			us.setLastRoom('W');
			Integer x = cellCount.get(b);
			cellCount.remove(b);
			cellCount.put(b, x + 1);
		}
		for (BoardCell b : cellCount.keySet()) {
			Integer i = cellCount.get(b);
			if (i < 10)
				fail(b + " has only " + i
						+ " selections after 500 runs with 15 choices");
		}
	}

	@Test
	public void testAlwaysSelectRoom() {
		// test always selecting a door of a room that hasn't been visited if
		// such an option exists
		board.calcTargets(14, 5, 3);
		ComputerPlayer us = (ComputerPlayer) game.getPlayers().get(1);
		for (int i = 0; i < 500; i++) {
			us.setLocation(board.calcIndex(14, 5));
			us.setLastRoom('V');
			BoardCell b = us.pickLocation(board, 3);
			if (!(b instanceof RoomCell)
					|| ((b instanceof RoomCell) && ((RoomCell) b).getInitial() != 'J'))
				fail(b + " is not in the room it's supposed to be.");
		}
	}

	@Test
	public void testRandomlyIncludeLastVisited() {
		// test random behavior of destination selection if the only door is to
		// a room we just visited
		board.calcTargets(15, 19, 1);
		Map<BoardCell, Integer> cellCount = new HashMap<BoardCell, Integer>();
		Set<BoardCell> targets = board.getTargets();
		for (BoardCell b : targets) {
			cellCount.put(b, 0);
		}
		ComputerPlayer us = (ComputerPlayer) game.getPlayers().get(1);
		for (int i = 0; i < 500; i++) {
			us.setLocation(board.calcIndex(15, 19));
			us.setLastRoom('C');
			BoardCell b = us.pickLocation(board, 1);
			Integer x = cellCount.get(b);
			cellCount.remove(b);
			cellCount.put(b, x + 1);
		}
		for (BoardCell b : cellCount.keySet()) {
			Integer i = cellCount.get(b);
			if (i < 10)
				fail(b + " has only " + i
						+ " selections after 500 runs with 15 choices");
		}
	}

	@Test
	public void testDisproveSuggestion() {
		// disproves suggestion by returning the only possible card
		ArrayList<Player> players = game.getPlayers();
		Player p = players.get(1);
		p.addCard(new Card("Rope", Card.CardType.WEAPON));
		p.addCard(new Card("Miss Scarlet", Card.CardType.PERSON));
		p.addCard(new Card("Hallway", Card.CardType.ROOM));
		p.addCard(new Card("Pipe Wrench", Card.CardType.WEAPON));
		p.addCard(new Card("Mrs. Peacock", Card.CardType.PERSON));
		p.addCard(new Card("Ryan's Room", Card.CardType.ROOM));

		Card c = p.disproveSuggestion("Miss Scarlet", "Revolver",
				"Vulcan Museum");
		assertEquals(c, new Card("Miss Scarlet", Card.CardType.PERSON));

		c = p.disproveSuggestion("Mr. Green", "Revolver", "Hallway");
		assertEquals(c, new Card("Hallway", Card.CardType.ROOM));

		c = p.disproveSuggestion("Mr. Green", "Rope", "Vulcan Museum");
		assertEquals(c, new Card("Rope", Card.CardType.WEAPON));

		// disproves suggestion with multiple cards, returns one at random
		int missScarletCount = 0;
		int hallwayCount = 0;
		int ropeCount = 0;
		Card missScarlet = new Card("Miss Scarlet", Card.CardType.PERSON);
		Card hallway = new Card("Hallway", Card.CardType.ROOM);
		Card rope = new Card("Rope", Card.CardType.WEAPON);

		for (int i = 0; i < 500; i++) {
			c = p.disproveSuggestion("Miss Scarlet", "Rope", "Hallway");
			if (c.equals(missScarlet))
				missScarletCount++;
			else if (c.equals(hallway))
				hallwayCount++;
			else if (c.equals(rope))
				ropeCount++;
		}
		assertTrue(missScarletCount > 10);
		assertTrue(hallwayCount > 10);
		assertTrue(ropeCount > 10);

		// unable to disprove suggestion
		c = p.disproveSuggestion("Mr. Green", "Knife", "Mattress Emporium");
		assertEquals(c, null);
	}

	@Test
	public void testQueriedPlayers() {
		// tests a suggestion which no players can disprove, ensure null is
		// returned
		ArrayList<Player> players = game.getPlayers();
		players.get(0).addCard(new Card("Miss Scarlet", Card.CardType.PERSON));
		players.get(1).addCard(
				new Card("Colonel Mustard", Card.CardType.PERSON));
		players.get(2).addCard(new Card("Mrs. White", Card.CardType.PERSON));
		players.get(3).addCard(new Card("Mr. Green", Card.CardType.PERSON));
		players.get(4).addCard(new Card("Mrs. Peacock", Card.CardType.PERSON));
		players.get(5).addCard(new Card("Rope", Card.CardType.WEAPON));

		game.handleSuggestion("Professor Plum", "Harpoon", "The Man Cave",
				players.get(1));
		ArrayList<Card> seenCards = ((ComputerPlayer) players.get(1))
				.getSeenCards();
		assertTrue(seenCards.contains(new Card("Colonel Mustard",
				Card.CardType.PERSON)));
		assertEquals(1, seenCards.size());

		// make a suggestion that only the human player can disprove, ensure the
		// right card is returned
		game.handleSuggestion("Miss Scarlet", "Harpoon", "The Man Cave",
				players.get(1));
		seenCards = ((ComputerPlayer) players.get(1)).getSeenCards();
		assertTrue(seenCards.contains(new Card("Miss Scarlet",
				Card.CardType.PERSON)));
		assertEquals(2, seenCards.size());

		// make a suggestion that only the one who suggested it can disprove,
		// ensure null is returned
		game.handleSuggestion("Mr. Green", "Harpoon", "The Man Cave",
				players.get(3));
		seenCards = ((ComputerPlayer) players.get(3)).getSeenCards();
		assertTrue(seenCards.contains(new Card("Mr. Green",
				Card.CardType.PERSON)));
		assertTrue(seenCards.contains(new Card("Miss Scarlet",
				Card.CardType.PERSON)));
		assertEquals(2, seenCards.size());

		// make a suggestion that 2 people can disprove, ensure the first
		// person's card is returned
		game.handleSuggestion("Mrs. Peacock", "Rope", "The Man Cave",
				players.get(2));
		seenCards = ((ComputerPlayer) players.get(2)).getSeenCards();
		assertTrue(seenCards.contains(new Card("Mrs. Peacock",
				Card.CardType.PERSON)));
		assertEquals(3, seenCards.size());
	}

	@Test
	public void testComputerPlayerSuggestions() {
		ArrayList<Player> players = game.getPlayers();
		Player p = players.get(1);

		// player has seen all but 3 cards, only one suggestion possible
		for (Card c : game.getCards()) {
			p.addCard(c);
		}

		p.removeCard(new Card("Colonel Mustard", Card.CardType.PERSON));
		p.removeCard(new Card("Candlestick", Card.CardType.WEAPON));
		p.removeCard(new Card("Lemon Showroom", Card.CardType.ROOM));
		((ComputerPlayer) p).createSuggestion(game.getCards());
		ArrayList<Suggestion> suggestions = ((ComputerPlayer) p)
				.getSuggestions();
		Suggestion s = new Suggestion("Colonel Mustard", "Candlestick",
				"Lemon Showroom");
		assertTrue(suggestions.contains(s));
		assertEquals(suggestions.size(), 1);

		// player has seen all but 4 cards, randomly choose suggestions
		p = players.get(2);
		for (Card c : game.getCards()) {
			p.addCard(c);
		}

		p.removeCard(new Card("Professor Plum", Card.CardType.PERSON));
		p.removeCard(new Card("Pipe Wrench", Card.CardType.WEAPON));
		p.removeCard(new Card("Knife", Card.CardType.WEAPON));
		p.removeCard(new Card("Vulcan Museum", Card.CardType.ROOM));
		p.removeCard(new Card("Lemon Showroom", Card.CardType.ROOM));
		((ComputerPlayer) p).createSuggestion(game.getCards());
		suggestions = ((ComputerPlayer) p).getSuggestions();
		Suggestion s1 = new Suggestion("Professor Plum", "Pipe Wrench",
				"Vulcan Museum");
		Suggestion s2 = new Suggestion("Professor Plum", "Knife",
				"Vulcan Museum");
		assertTrue(suggestions.contains(s1));
		assertTrue(suggestions.contains(s2));
		assertEquals(suggestions.size(), 4);

		Suggestion chosen;
		int s1Count = 0;
		int s2Count = 0;
		
		((ComputerPlayer)p).setCurrentRoom('V');

		for (int i = 0; i < 500; i++) {
			chosen = ((ComputerPlayer) p).chooseSuggestion(board);
			if (chosen == null)
				fail("computer player should not be returning a null suggestion");
			if (chosen.equals(s1))
				s1Count++;
			else if (chosen.equals(s2))
				s2Count++;
			else
				fail("computer player is selecting a suggestion outside of its current room");
		}
		assertTrue(s1Count > 10);
		assertTrue(s2Count > 10);

	}

	@Test
	public void testSuggestionContents() {
		Suggestion suggestion = new Suggestion("Professor Plum", "Pipe Wrench",
				"Vulcan Museum");
		assertTrue(suggestion.contains(new Card("Professor Plum",
				Card.CardType.PERSON)));
		assertTrue(suggestion.contains(new Card("Pipe Wrench",
				Card.CardType.WEAPON)));
		assertTrue(suggestion.contains(new Card("Vulcan Museum",
				Card.CardType.ROOM)));
		assertFalse(suggestion.contains(new Card("The Man Cave",
				Card.CardType.ROOM)));

	}

}
