package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.lang.reflect.Field;
import java.util.ArrayList;

public abstract class Player {
	
	private String playerName;
	protected ArrayList<Card> cards;
	protected int location;
	private Color color;
	
	public Color getColor() {
		return color;
	}

	public void setLocation(int location) {
		this.location = location;
	}
	
	public int getLocation() {
		return location;
	}

	public Player(String playerName, String strColor) {
		super();
		this.playerName = playerName;
		cards = new ArrayList<Card>();
		try {     
			// We can use reflection to convert the string to a color
			Field field = Class.forName("java.awt.Color").getField(strColor.trim());     
			this.color = (Color)field.get(null); } 
		catch (Exception e) {  
			this.color = null; // Not defined } 
		}
		location = 0;
	}
	
	public ArrayList<Card> getCards() {
		return cards;
	}
	
	public void addCard(Card card) {
		this.cards.add(card);
	}
	
	public void removeCard(Card card) {
		this.cards.remove(card);
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public Card disproveSuggestion(String person, String weapon, String room) {
		return disproveSuggestion(new Suggestion(person, weapon, room));
	}
	
	public Card disproveSuggestion(Suggestion suggestion) {
		ArrayList<Card> possibleCards = new ArrayList<Card>();
		for(Card c : cards) {
			if(suggestion.contains(c)) possibleCards.add(c);
		}
		if(possibleCards.isEmpty()) return null;
		return possibleCards.get((int) (Math.random() * (possibleCards.size())));
	}
	
	public abstract void updateSeen(Card c);
	
	public void draw(Graphics g, Board b) {
		g.setColor(color);
		g.fillOval(b.indexToRow(location) * 30, b.indexToCol(location) * 30, 30, 30);
	}
}
