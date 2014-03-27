package clueGame;

import java.util.*;

public class ComputerPlayer extends Player {

	private char lastRoom, currentRoom;
	private ArrayList<Card> seenCards;
	private ArrayList<Suggestion> suggestions;

	public ComputerPlayer(String playerName, String strColor) {
		super(playerName, strColor);
		lastRoom = 'X';
		currentRoom = 'X';
		seenCards = new ArrayList<Card>(cards);
		suggestions = null;
	}

	public BoardCell pickLocation(Board onBoard, int roll) {
		ArrayList<BoardCell> targets = new ArrayList<BoardCell>(
				onBoard.getTargets());
		BoardCell choice = null;

		for (BoardCell b : targets) {
			if (b.isDoorway() && ((RoomCell) b).getInitial() != lastRoom) {
				choice = b;
				break;
			}
		}

		if (choice == null)
			choice = targets.get((int) (Math.random() * targets.size()));

		int newLoc = onBoard.calcIndex(choice.getRow(), choice.getColumn());
		char newRoom = currentRoom;
		if (choice instanceof RoomCell) {
			newRoom = ((RoomCell) choice).getInitial();
		}
		if (currentRoom != 'W')
			lastRoom = currentRoom;
		location = newLoc;
		currentRoom = newRoom;
		return choice;
	}

	public void setLastRoom(char lastRoom) {
		this.lastRoom = lastRoom;
	}

	public void createSuggestion(Set<Card> allCards) {
		suggestions = new ArrayList<Suggestion>();
		Set<Card> workingSet = new HashSet<Card>(allCards);
		workingSet.removeAll(seenCards);
		ArrayList<Card> players = new ArrayList<Card>(), weapons = new ArrayList<Card>(), rooms = new ArrayList<Card>();
		for (Card c : workingSet) {
			if (c.getType() == Card.CardType.PERSON)
				players.add(c);
			if (c.getType() == Card.CardType.WEAPON)
				weapons.add(c);
			if (c.getType() == Card.CardType.ROOM)
				rooms.add(c);
		}
		for (Card p : players)
			for (Card w : weapons)
				for (Card r : rooms)
					suggestions.add(new Suggestion(p, w, r));
	}

	@Override
	public void updateSeen(Card c) {
		if (!seenCards.contains(c))
			seenCards.add(c);
	}

	@Override
	public void removeCard(Card card) {
		super.removeCard(card);
		this.seenCards.remove(card);
	}

	@Override
	public void addCard(Card card) {
		super.addCard(card);
		this.seenCards.add(card);
	}

	public Suggestion chooseSuggestion(Board onBoard) {
		for (int i = 0; i < 100; i++) {
			Suggestion s = suggestions.get((int) (Math.random() * suggestions
					.size()));
			if (s.contains(new Card(onBoard.getRooms().get(currentRoom),
					Card.CardType.ROOM)))
				return s;
		}
		/*
		 * for (Suggestion s : new HashSet<Suggestion>(suggestions)) { if
		 * (s.contains( new Card(onBoard.getRooms().get(currentRoom),
		 * Card.CardType.ROOM))) return s; }
		 */
		return null;
	}

	public void setCurrentRoom(char currentRoom) {
		this.currentRoom = currentRoom;
	}

	public ArrayList<Card> getSeenCards() {
		return seenCards;
	}

	public ArrayList<Suggestion> getSuggestions() {
		return suggestions;
	}

}
