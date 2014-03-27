package clueGame;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.*;


@SuppressWarnings("serial")
public class DetectiveNotes extends JDialog {
	
	public DetectiveNotes(Set<Card> cards) {
		setTitle("Detective Notes");
		setLayout(new GridLayout(3, 1));
		add(new CardPanel(cards, "People", Card.CardType.PERSON));
		add(new CardPanel(cards, "Rooms", Card.CardType.ROOM));
		add(new CardPanel(cards, "Weapons", Card.CardType.WEAPON));
		this.pack();
	}
	
	class CardPanel extends JPanel {
		public CardPanel(Set<Card> cards, String label, Card.CardType type) {
			setLayout(new GridLayout(1, 2));
			JPanel left = new JPanel();
			JPanel right = new JPanel();
			ArrayList<String> boxes = new ArrayList<String>();
			left.setBorder(new TitledBorder(new EtchedBorder(), label));
			right.setBorder(new TitledBorder(new EtchedBorder(), label + " guess"));
			for(Card c : cards) {
				if(c.getType() == type) {
					boxes.add(c.getCardName());
				}
			}
			left.setLayout(new GridLayout((int) Math.ceil(boxes.size() / 2.0), 2));
			JComboBox<String> dropdown = new JComboBox<String>();
			for(String s : boxes) {
				left.add(new JCheckBox(s));
				dropdown.addItem(s);
			}
			right.add(dropdown);
			add(left);
			add(right);
		
		}
	}
}
