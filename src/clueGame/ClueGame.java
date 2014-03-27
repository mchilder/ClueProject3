package clueGame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class ClueGame extends JFrame{
	
	private String peopleFileName;
	private String cardFileName;
	private Map<String, String> people;
	private ArrayList<Player> players;
	private Set<Card> cards;
	private Solution solution;
	private Board board;
	
	public void deal() {
		int index = 0;
		Card slnPerson = null, slnRoom = null, slnWeapon = null;
		for(Card c : cards) {
			if(slnPerson == null && c.getType() == Card.CardType.PERSON) slnPerson = c;
			else if(slnRoom == null && c.getType() == Card.CardType.ROOM) slnRoom = c;
			else if(slnWeapon == null && c.getType() == Card.CardType.WEAPON) slnWeapon = c;
			else {
				players.get(index).addCard(c);
				if(index >= players.size() - 1) index = 0;
				else index++;
			}
		}
		solution = new Solution(slnPerson, slnWeapon, slnRoom);
	}
	
	public void loadConfigFiles() throws BadConfigFormatException {
		try {
			loadPeopleFile(peopleFileName);
			loadCardsFile(cardFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public Set<String> getPeople() {
		return people.keySet();
	}
	public ArrayList<Player> getPlayers() {
		return players;
	}
	public Set<Card> getCards() {
		return cards;
	}
	public void setPeopleFileName(String peopleFileName) {
		this.peopleFileName = peopleFileName;
	}
	public void setCardFileName(String cardFileName) {
		this.cardFileName = cardFileName;
	}
	public ClueGame(String peopleFileName, String cardFileName) {
		this();
		this.peopleFileName = peopleFileName;
		this.cardFileName = cardFileName;
	}
	
	public ClueGame(String peopleFileName, String cardFileName, Board board) {
		this(peopleFileName, cardFileName);
		this.board = board;
		this.board.calcAdjacencies();
		this.board.setPlayers(this.players);
		add(this.board, BorderLayout.CENTER);
	}
	
	public ClueGame() {
		super();
		people = new HashMap<String, String>();
		players = new ArrayList<Player>();
		cards = new HashSet<Card>();
		setTitle("Clue Game");
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(createMenu());
		setSize(700, 700);
		
	}
	
	public void loadPeopleFile(String fName) throws FileNotFoundException, BadConfigFormatException {
		Scanner reader;
		reader = new Scanner(new FileReader(fName));
		while(reader.hasNextLine()) {
			String line = reader.nextLine();
			if (line.length() == 0) { // We found an empty line, that's not OK.
				reader.close();
				throw new BadConfigFormatException("Found a blank line, but expected something useful.");
			}
			else {
				String [] info = line.split(",");
				if(!(info.length == 2)) throw new BadConfigFormatException("Don't have a name and a color");
				people.put(info[0], info[1]);
			}
		}
		reader.close();
		assignPlayers();
	}
	
	private void assignPlayers() {
		ArrayList<String> tempPeople = new ArrayList<String>(people.keySet());
		int randomPerson = (int)(Math.random() * people.size());
		players.add(new HumanPlayer(tempPeople.get(randomPerson), people.get(tempPeople.get(randomPerson))));
		tempPeople.remove(randomPerson);
		for(String s : tempPeople) {
			players.add(new ComputerPlayer(s, people.get(s)));
		}
		for(Player p : players) {
			p.setLocation(board.getStartingLocation());
		}
	}
	
	public void loadCardsFile(String fName) throws FileNotFoundException, BadConfigFormatException {
		Scanner reader;
		reader = new Scanner(new FileReader(fName));
		while(reader.hasNextLine()) {
			String line[] = reader.nextLine().split(",");
			if (line.length == 0) { // We found an empty line, that's not OK.
				reader.close();
				throw new BadConfigFormatException("Found a blank line, but expected something useful.");
			}
			else {
				if(line[0].equalsIgnoreCase("room")) cards.add(new Card(line[1], Card.CardType.ROOM));
				else if(line[0].equalsIgnoreCase("person")) cards.add(new Card(line[1], Card.CardType.PERSON));
				else if(line[0].equalsIgnoreCase("weapon")) cards.add(new Card(line[1], Card.CardType.WEAPON));
				else { reader.close(); throw new BadConfigFormatException("Found a line that doesn't correspond to a room, person, or weapon."); }
			}
		}
		reader.close();
	}
	
	
	//public void selectAnswer() { }
	public void handleSuggestion(String person, String room, String weapon, Player accuser) {
		handleSuggestion(new Suggestion(person, room, weapon), accuser);
	}
	
	public void handleSuggestion(Suggestion suggestion, Player accuser) {
		for(Player p : players) {
			if(!p.equals(accuser)) {
				Card disprover = p.disproveSuggestion(suggestion);
				if(!(disprover == null)) {
					for(Player showTo : players)
						showTo.updateSeen(disprover);
					return;
				}
			}
		}
	}
	
	public boolean checkAccusation(Solution solution) {
		return solution.equals(this.solution);
	}
	public Solution getSolution() {
		return solution;
	}
	public void setSolution(Solution solution) {
		this.solution = solution;
	}
	public Board getBoard() {
		return board;
	}
	
	private JMenu createMenu() {
		JMenu menu = new JMenu("File");
		menu.add(createFileExitItem());
		menu.add(createFileNotesItem());
		return menu;
	}
	
	private JMenuItem createFileExitItem() {
		JMenuItem item = new JMenuItem("Exit");
		class MenuItemListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		}
		item.addActionListener(new MenuItemListener());
		return item;
	}
	
	private JMenuItem createFileNotesItem() {
		JMenuItem item = new JMenuItem("Show Notes");
		class MenuItemListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				DetectiveNotes notes = new DetectiveNotes(cards);
				notes.setVisible(true);
			}
		}
		item.addActionListener(new MenuItemListener());
		return item;
	}
	
	public static void main(String[] args) {
		Board board = new Board("OurLayout.csv", "OurLegend.txt");
		ClueGame game = new ClueGame("PlayerFile.txt", "CardFile.txt", board);
		game.loadConfigFiles();
		
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.setVisible(true);
	}
}
