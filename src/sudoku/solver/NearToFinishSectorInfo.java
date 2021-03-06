package sudoku.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class NearToFinishSectorInfo {

	private BoardSector sector;
	private List<Integer> missingNumbers;
	private List<BoardPoint> emptyCells;
	
	public NearToFinishSectorInfo(BoardSector sector, List<Integer> missingNumbers, List<BoardPoint> emptyCells) {
		super();
		this.sector = sector;
		this.missingNumbers = missingNumbers;
		this.emptyCells = emptyCells;
	}

	public BoardSector getSector() {
		return sector;
	}

	public List<Integer> getMissingNumbers() {
		return Collections.unmodifiableList(missingNumbers);
	}

	public List<BoardPoint> getEmptyCells() {
		return Collections.unmodifiableList(emptyCells);
	}

	public List<Map<Integer, BoardPoint>> getCombinations() {
		List<List<Integer>> sequences = generateSequencesOfMissingNumbers();
		
		List<Map<Integer, BoardPoint>> combinations = new ArrayList<>();
		for (List<Integer> sequence : sequences) {
			HashMap<Integer, BoardPoint> combination = new HashMap<>();
			
			LinkedList<BoardPoint> cellsToFill = new LinkedList<>(emptyCells);
			for (Integer number : sequence)
				combination.put(number, cellsToFill.pop());
			
			combinations.add(combination);
		}
		
		return combinations;
	}

	private List<List<Integer>> generateSequencesOfMissingNumbers() {
		return generateShuffleSequencesFrom(missingNumbers, 0);
	}

	private List<List<Integer>> generateShuffleSequencesFrom(List<Integer> sequence, int startIndex) {
		List<List<Integer>> sequences = new ArrayList<>();
		
		if (startIndex == sequence.size() - 1)
			return sequences;
		
		sequences.add(sequence);
		List<List<Integer>> subSequences = generateShuffleSequencesFrom(sequence, startIndex + 1);
		if (subSequences.contains(sequence))
			subSequences.remove(sequence);
		
		sequences.addAll(subSequences);
		
		for (int i = startIndex + 1; i < sequence.size(); i++) {
			List<Integer> shuffle = new ArrayList<>(sequence);
			Integer value = shuffle.remove(i);
			shuffle.add(startIndex, value);
			sequences.add(shuffle);
			
			List<List<Integer>> innerSubSequences = generateShuffleSequencesFrom(shuffle, startIndex + 1);
			if (innerSubSequences.contains(shuffle))
				innerSubSequences.remove(shuffle);
			sequences.addAll(innerSubSequences);
		}
		
		return sequences;
	}
	
}
