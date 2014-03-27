// Chris Butler
// Ben Sattelberg
package clueGame;

import java.awt.Graphics;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Board extends JPanel{
	private String board_filename;
	private ArrayList<BoardCell> cells = new ArrayList<BoardCell>();
	private String legend_filename;
	private Map<Character, String> rooms = new HashMap<Character, String>();
	private Set<Integer> nameCells = new HashSet<Integer>();
	private ArrayList<Player> players;
	private ArrayList<Integer> startingLocations;

	private int columns;
	private int rows;

	private ArrayList<HashSet<Integer>> adjMtx = new ArrayList<HashSet<Integer>>();
	private boolean[] visited;
	private Set<BoardCell> targets = new HashSet<BoardCell>();
	private Set<Integer> targets_i;

	public Board(String board_filename, String legend_filename) {
		this.legend_filename = legend_filename;
		this.board_filename = board_filename;
		loadConfigFiles();
		for(int i = 0; i < rows*columns; ++i) {
			adjMtx.add(new HashSet<Integer>());
		}
		visited = new boolean[rows*columns];
		players = null;
		startingLocations = new ArrayList<Integer>();
		
		startingLocations.add(calcIndex(0, 4)); //TODO refactor
		startingLocations.add(calcIndex(0, 11));
		startingLocations.add(calcIndex(10, 0));
		startingLocations.add(calcIndex(19, 5));
		startingLocations.add(calcIndex(19, 14));
		startingLocations.add(calcIndex(6, 19));
	}

	public void calcAdjacencies() {
		for(int i = 0; i < rows * columns; i++) {
			if (cells.get(i).isDoorway()) {
				int index = 0;
				switch (((RoomCell) cells.get(i)).getDoorDirection()) {
				case UP:
					index = i - columns;
					break;
				case DOWN:
					index = i + columns;
					break;
				case LEFT:
					index = i - 1;
					break;
				case RIGHT:
					index = i + 1;
					break;
				case NONE:
					// How did this happen?
					throw new RuntimeException("Issue with: " + cells.get(i));
				}
				adjMtx.get(i).add(index);
				continue;
			}
			if (cells.get(i).isRoom()) {
				continue;
			}

			for(final int index : new int[] {i - 1, i + 1, i - columns, i + columns} ) {
				// Make sure it's in bounds of the board.
				if ( !(0 <= index && index < rows * columns)
						// The next index isn't in the same row or column as i, so it can't be adjacent.
						|| (index / columns != i / columns && index % columns != i % columns)) {
					continue;
				}
				if (cells.get(index).isWalkway() || cells.get(index).isDoorway()) {
					if (cells.get(index).isDoorway()) {
						boolean correct_direction = false;
						switch (((RoomCell) cells.get(index)).getDoorDirection()) {
							case UP:
								correct_direction = index == i + columns;
								break;
							case DOWN:
								correct_direction = index == i - columns;
								break;
							case LEFT:
								correct_direction = index == i + 1;
								break;
							case RIGHT:
								correct_direction = index == i - 1;
								break;
							case NONE:
								break;
						}
						// We aren't approaching the door from the correct side, so it's not adjacent.
						if (!correct_direction) {
							continue;
						}
					}
					adjMtx.get(i).add(index);
				}
			}
		}
	}
	public int calcIndex(int row, int column) {
		return row*this.columns + column;
	}

	public void startTargets(int row, int column, int steps) {
		Arrays.fill(visited, false);
		targets_i = new HashSet<Integer>();

		visited[calcIndex(row, column)] = true;
		calcTargets(calcIndex(row, column), steps);
	}
	public void calcTargets(int row, int column, int steps) {
		startTargets(row, column, steps);
	}
	public void calcTargets(int cell, int steps) {
		for(int i : adjMtx.get(cell)) {
			if (visited[i] == false) {
				visited[i] = true;
				if (steps == 1 || cells.get(i).isDoorway()) {
					targets_i.add(i);
				} else {
					calcTargets(i, steps - 1);
				}
				visited[i] = false;
			}
		}
		visited[cell] = false;
	}
	public HashSet<Integer> getAdjList(int cell) {
		return adjMtx.get(cell);
	}
	public BoardCell getCellAt(int index){
		return cells.get(index);
	}
	public int getColumns(){
		return columns;
	}

	public RoomCell getRoomCellAt(int i, int j) {
		return (RoomCell) getCellAt(calcIndex(i, j));
	}

	public Map<Character, String> getRooms() {
		return rooms;
	}

	public int getRows(){
		return rows;
	}

	public Set<BoardCell> getTargets() {
		targets = new HashSet<BoardCell>();
		for (int i : targets_i) {
			targets.add(cells.get(i));
		}
		return targets;
	}

	public void loadBoardConfig() throws BadConfigFormatException {
		Scanner reader;
		try {
			reader = new Scanner(new FileReader(board_filename));
		} catch (FileNotFoundException e) {
			System.out.println("File not found for the board config.");
			return;
		}
		rows = 0;
		while(reader.hasNextLine()) {
			rows += 1;
			int current_column = 0;
			for(String thing : reader.nextLine().split(",")) {
				// We found an empty line, that's not OK.
				if (thing.length() == 0) {
					reader.close();
					throw new BadConfigFormatException("Found a blank line, but expected something useful.");
				}
				current_column += 1;
				char room = thing.charAt(0);
				if (room == 'W' || room == 'w') {
					cells.add(new WalkwayCell(rows - 1, current_column - 1));
				} else if (rooms.containsKey(room)){
					cells.add(new RoomCell(thing, rows - 1, current_column - 1));
				} else {
					reader.close();
					throw new BadConfigFormatException("Unknown room: " + thing);
				}
			}
			// Only compare columns after the first row.
			if (columns != current_column && rows != 1) {
				reader.close();
				throw new BadConfigFormatException("Dimension mismatch error reading board config file.");
			}
			columns = current_column;
		}
		reader.close();

	}

	public void loadConfigFiles() {
		loadRoomConfig();
		loadBoardConfig();
	}

	public void loadRoomConfig() throws BadConfigFormatException{
		Scanner reader;
		try {
			reader = new Scanner(new FileReader(legend_filename));
		} catch (FileNotFoundException e) {
			System.out.println("File not found for the legend config.");
			return;
		}
		while(reader.hasNextLine()) {
			String[] line = reader.nextLine().split(",");
			if (line.length != 3 || line[0].length() == 0) {
				reader.close();
				throw new BadConfigFormatException("Dimension mismatch error reading legend config file.");
			}
			rooms.put(line[0].charAt(0), line[1].substring(1));
			nameCells.add(Integer.parseInt(line[2]));
		}
		reader.close();
	}
	
	public Set<Integer> getNameCells() {
		return nameCells;
	}

	public void paintComponent(Graphics g) {
		for(BoardCell b : cells) {
			b.draw(g, this);
		}
		for(Player p : players) {
			p.draw(g, this);
		}
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	public int indexToRow(int index) {
		return index / columns;
	}
	
	public int indexToCol(int index) {
		return index % columns;
	}
	
	public int getStartingLocation() {
		int temp = (int) (Math.random() * startingLocations.size());
		Integer val = startingLocations.get(temp);
		startingLocations.remove(temp);
		return val;
	}
}
