package clueGame;

import java.util.ArrayList;

public class Solution {
	
	private Card person;
	private Card weapon;
	private Card room;
	
	public Solution(Card person, Card weapon, Card room) {
		super();
		this.person = person;
		this.weapon = weapon;
		this.room = room;
	}
	
	public Solution(String person, String weapon, String room) {
		super();
		this.person = new Card(person, Card.CardType.PERSON);
		this.weapon = new Card(weapon, Card.CardType.WEAPON);
		this.room = new Card(room, Card.CardType.ROOM);
	}
	
	public Card getPerson() {
		return person;
	}
	public Card getWeapon() {
		return weapon;
	}
	public Card getRoom() {
		return room;
	}
	
	public ArrayList<Card> getAllCards() {
		ArrayList<Card> temp = new ArrayList<Card>();
		temp.add(person);
		temp.add(weapon);
		temp.add(room);
		return temp;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Solution)) return false;
		Solution s = (Solution)o;
		if(!this.person.equals(s.person)) return false;
		if(!this.weapon.equals(s.weapon)) return false;
		if(!this.room.equals(s.room)) return false;
		return true;
	}
}
