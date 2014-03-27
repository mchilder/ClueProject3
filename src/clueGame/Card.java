package clueGame;

public class Card {
	
	public enum CardType { ROOM, WEAPON, PERSON };
	
	public String getCardName() {
		return cardName;
	}

	public CardType getType() {
		return type;
	}

	private String cardName;
	private CardType type;
	
	public Card(String cardName, CardType type) {
		super();
		this.cardName = cardName;
		this.type = type;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Card)) return false;
		Card c = (Card)o;
		if(!this.cardName.equalsIgnoreCase(c.cardName)) return false;
		if(!(this.type == c.type))return false;
		return true;
	}

	@Override
	public String toString() {
		return "Card [cardName=" + cardName + ", type=" + type + "]";
	}
}
