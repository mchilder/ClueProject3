package clueGame;

public class Suggestion {
	
	private Card person;
	private Card weapon;
	private Card room;
	
	public Suggestion(Card person, Card weapon, Card room) {
		super();
		this.person = person;
		this.weapon = weapon;
		this.room = room;
	}
	
	public Suggestion(String person, String weapon, String room) {
		super();
		this.person = new Card(person, Card.CardType.PERSON);
		this.weapon = new Card(weapon, Card.CardType.WEAPON);
		this.room = new Card(room, Card.CardType.ROOM);
	}

	public Card getPerson() {
		return person;
	}

	public void setPerson(Card person) {
		this.person = person;
	}

	public Card getWeapon() {
		return weapon;
	}

	public void setWeapon(Card weapon) {
		this.weapon = weapon;
	}

	public Card getRoom() {
		return room;
	}

	public void setRoom(Card room) {
		this.room = room;
	}
	
	public boolean contains(Card test) {
		if(test.equals(person)) return true;
		if(test.equals(room)) return true;
		if(test.equals(weapon)) return true;
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Suggestion)) return false;
		Suggestion s = (Suggestion)o;
		if(!this.room.equals(s.room)) return false;
		if(!this.weapon.equals(s.weapon)) return false;
		if(!this.person.equals(s.person)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "Suggestion [person=" + person + ", weapon=" + weapon
				+ ", room=" + room + "]";
	}
}
