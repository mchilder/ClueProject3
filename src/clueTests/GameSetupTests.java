package clueTests;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.*;

import org.junit.*;

import clueGame.*;

public class GameSetupTests {
	
	ClueGame game;

	@Before
	public void setUp() {
		game = new ClueGame("PlayerFile.txt", "CardFile.txt");
		game.loadConfigFiles();
	}

	@Test
	public void testGoodPeopleFileLoading() {
		//Checks that all of the people listed in the people config file
		//are there and that the list only had 6 people
		ArrayList<String> people = new ArrayList(game.getPeople());
		assertTrue(people.contains("Miss Scarlet"));
		assertTrue(people.contains("Colonel Mustard"));
		assertTrue(people.contains("Mrs. White"));
		assertTrue(people.contains("Mr. Green"));
		assertTrue(people.contains("Mrs. Peacock"));
		assertTrue(people.contains("Professor Plum"));
		assertEquals(people.size(), 6);
	}
	
	@Test
	public void testGoodCardFileLoading() {
		//Checks that the total number of cards is correct and 
		//that there is the correct number of cards of each type
		//also checks that the array contains specific cards from 
		//the config file
		Set<Card> cards = game.getCards();
		int roomCards = 0, peopleCards = 0, weaponCards = 0;
		boolean containsManCave = false, containsRyansRoom = false, constainsColonelMustard = false, containsMrsPeacock = false, containsRevolver = false, containsPipeWrench = false;
		for(Card c : cards) {
			if(c.getType() == Card.CardType.ROOM) roomCards++;
			if(c.getType() == Card.CardType.PERSON) peopleCards++;
			if(c.getType() == Card.CardType.WEAPON) weaponCards++;
			if(c.equals(new Card("The Man Cave", Card.CardType.ROOM))) containsManCave = true;
			else if(c.equals(new Card("Ryan's Room", Card.CardType.ROOM))) containsRyansRoom = true;
			else if(c.equals(new Card("Colonel Mustard", Card.CardType.PERSON))) constainsColonelMustard = true;
			else if(c.equals(new Card("Mrs. Peacock", Card.CardType.PERSON))) containsMrsPeacock = true;
			else if(c.equals(new Card("Revolver", Card.CardType.WEAPON))) containsRevolver = true;
			else if(c.equals(new Card("Pipe Wrench", Card.CardType.WEAPON))) containsPipeWrench = true;
		}
		assertTrue(containsManCave);
		assertTrue(containsRyansRoom);
		assertTrue(constainsColonelMustard);
		assertTrue(containsMrsPeacock);
		assertTrue(containsRevolver);
		assertTrue(containsPipeWrench);
		assertEquals(roomCards, 9);
		assertEquals(peopleCards, 6);
		assertEquals(weaponCards, 6);
		assertEquals(cards.size(), 21);
	}
	
	@Test
	public void testCardEquality() {
		assertTrue((new Card("a", Card.CardType.ROOM)).equals(new Card("a", Card.CardType.ROOM)));
		assertTrue((new Card("B", Card.CardType.WEAPON)).equals(new Card("b", Card.CardType.WEAPON)));
		assertTrue((new Card("Mrs. Peacock", Card.CardType.PERSON)).equals(new Card("mrs. peacock", Card.CardType.PERSON)));
		assertFalse((new Card("a", Card.CardType.ROOM)).equals(new Card("B", Card.CardType.ROOM)));
		assertFalse((new Card("a", Card.CardType.WEAPON)).equals(new Card("a", Card.CardType.ROOM)));
	}
	
	@Test (expected = BadConfigFormatException.class)
	public void testBadPeopleFileLoading() throws FileNotFoundException, BadConfigFormatException {
		//Checks that an incorrectly formatted player file throws an error
		game.loadPeopleFile("BadPlayerFile.txt");
	}
	
	@Test (expected = FileNotFoundException.class)
	public void testNonexistantPeopleFileLoading() throws FileNotFoundException, BadConfigFormatException {
		//Checks that a nonexistent file throws an error
		game.loadPeopleFile("NotAPlayerFile.txt");
	}
	
	@Test (expected = BadConfigFormatException.class)
	public void testBadCardFileLoading() throws FileNotFoundException, BadConfigFormatException {
		//Checks that an incorrectly formatted player file throws an error
		game.loadPeopleFile("BadCardFile.txt");
	}
	
	@Test (expected = FileNotFoundException.class)
	public void testNonexistantCardFileLoading() throws FileNotFoundException, BadConfigFormatException {
		//Checks that a nonexistent file throws an error
		game.loadPeopleFile("NotACardFile.txt");
	}
	
	@Test
	public void testPlayerNumCards() {
		//Checks that each player has 3 or 4 cards each
		ArrayList<Player> players = game.getPlayers();
		game.deal();
		for(Player p : players) {
			assertTrue(p.getCards().size() == 3);
		}
	}
	
	@Test
	public void testAllCardsDealt() {
		//Checks that all of the cards have been dealt and 
		//that no duplicates have been dealt
		//Creates a new set of all the cards that the players
		//hold and compares it to the original deck
		game.deal();
		ArrayList<Player> players = game.getPlayers();
		Set<Card> playersCards = new HashSet<Card>();
		for(Player p : players) {
			playersCards.addAll(p.getCards());
		}
		assertEquals(playersCards.size(), game.getCards().size() - 3);
		Set<Card> allCards = new HashSet<Card>();
		allCards.addAll(playersCards);
		allCards.addAll(game.getSolution().getAllCards());
		assertTrue(allCards.containsAll(game.getCards()));
	}
	
	@Test
	public void testAccusations() {
		//Checks the checkAccusation method with one correct solution,
		//and three each with only one item wrong 
		game.setSolution(new Solution("Mr. Green", "Harpoon", "Lemon Showroom"));
		assertTrue(game.checkAccusation(new Solution("Mr. Green", "Harpoon", "Lemon Showroom")));
		assertFalse(game.checkAccusation(new Solution("Professor Plum", "Harpoon", "Lemon Showroom")));
		assertFalse(game.checkAccusation(new Solution("Mr. Green", "Knife", "Lemon Showroom")));
		assertFalse(game.checkAccusation(new Solution("Mr. Green", "Harpoon", "Mattress Emporium")));
	}

}
