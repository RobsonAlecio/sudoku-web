package sudoku.solver;

import java.util.List;

public class SudokuSolver {

	private static SudokuSolver instance;

	public static void setInstance(SudokuSolver instance) {
		SudokuSolver.instance = instance;
	}

	public static SudokuSolver getInstance() {
		if (instance == null)
			instance = new SudokuSolver();
		
		return instance;
	}

	public SudokuPuzzle solve(String boardRepresentation) {
		
		SudokuPuzzle puzzleSaved = SudokuPuzzleRepository.getInstance().find(boardRepresentation);
		if (puzzleSaved != null)
			return puzzleSaved;
		
		SudokuBoard problemBoard = new SudokuBoard(boardRepresentation);
		SudokuBoard generatedSolution = generateSolution(problemBoard);
		
		SudokuPuzzle puzzle = new SudokuPuzzle(problemBoard, generatedSolution);
		
		SudokuPuzzleRepository.getInstance().save(puzzle);
		
		return puzzle;
	}

	protected SudokuBoard generateSolution(SudokuBoard problemBoard) {
		SudokuBoard solutionProposal = problemBoard.copy();
		
		log(solutionProposal);
		
		if (solutionProposal.isSolved())
			return solutionProposal;
		
		SudokuBoard newProposal = fillOneCellLeftOnSectors(solutionProposal);
		
		log(newProposal);
		if (newProposal.isSolved())
			return newProposal;

		SudokuBoard freshNewProposal = fillUsingSimpleMarkHeuristic(newProposal);
		
		log(freshNewProposal);
		if (freshNewProposal.isSolved())
			return freshNewProposal;
		
		SudokuBoard brandNewProposal = fillUsingLinesAndColumnInferedHeuristic(freshNewProposal);
		
		log(brandNewProposal);
		
		if (brandNewProposal.isSolved())
			return brandNewProposal;
		
		return brandNewProposal;
	}

	private SudokuBoard fillOneCellLeftOnSectors(SudokuBoard solutionProposal) {
		for (int number = 1; number <= SudokuBoard.BOARD_SIZE; number++) {
			solutionProposal = fillOnCellLeftWith(number, solutionProposal);
		}
		return solutionProposal;
	}
	
	private SudokuBoard fillOnCellLeftWith(int number, SudokuBoard actualProposal) {
		return fillOnCellLeftWith(number, actualProposal, actualProposal);
	}
	
	private SudokuBoard fillOnCellLeftWith(int number, SudokuBoard actualProposal, SudokuBoard evalBoard) {
		SudokuBoard solutionProposal = actualProposal.copy();
		
		for (BoardSector sector: BoardSector.values()) {
			BoardPoint targetPoint = evalBoard.eval(number, sector);
			if (targetPoint != null) {
				solutionProposal = solutionProposal.set(number, targetPoint);
			}
		}
		
		return solutionProposal;
	}

	private SudokuBoard fillUsingSimpleMarkHeuristic(SudokuBoard actualProposal) {
		SudokuBoard solutionProposal = actualProposal.copy();
		
		String initialRepresentation = solutionProposal.getRepresentation();
		do {
			initialRepresentation = solutionProposal.getRepresentation();
			for (int number = 1; number <= SudokuBoard.BOARD_SIZE; number++) {
				SudokuBoard markedBoard = solutionProposal.simpleMark(number);
				log(number, markedBoard);
				solutionProposal = fillOnCellLeftWith(number, solutionProposal, markedBoard);
			}
		} while (!solutionProposal.getRepresentation().equals(initialRepresentation));
		
		return solutionProposal;
	}

	private SudokuBoard fillUsingLinesAndColumnInferedHeuristic(SudokuBoard actualProposal) {
		SudokuBoard solutionProposal = actualProposal.copy();
		
		String initialRepresentation = solutionProposal.getRepresentation();
		do {
			initialRepresentation = solutionProposal.getRepresentation();
			for (int number = 1; number <= SudokuBoard.BOARD_SIZE; number++) {
				SudokuBoard markedBoard = solutionProposal.simpleMark(number);
				log(number, markedBoard);
				solutionProposal = fillOnCellLeftWith(number, solutionProposal, markedBoard);
				
				List<SudokuBoard> inferedLinesBoards = markedBoard.inferLinesAndMark(number);
				for (SudokuBoard inferedLinesMarkedBoard : inferedLinesBoards) {
					log(number, inferedLinesMarkedBoard);
					solutionProposal = fillOnCellLeftWith(number, solutionProposal, inferedLinesMarkedBoard);
				}
				List<SudokuBoard> inferedColumnsBoards = markedBoard.inferColumnsAndMark(number);
				for (SudokuBoard inferedColumnsMarkedBoard : inferedColumnsBoards) {
					log(number, inferedColumnsMarkedBoard);
					solutionProposal = fillOnCellLeftWith(number, solutionProposal, inferedColumnsMarkedBoard);
				}
			}
		} while (!solutionProposal.getRepresentation().equals(initialRepresentation));
	
		return solutionProposal;
	}

	private void log(SudokuBoard board) {
		System.out.println("=====================================");
		System.out.println(board.print());
	}
	
	private void log(int number, SudokuBoard board) {
		System.out.println("=====================================");
		System.out.printf("Analizando numero: %d\n", number);
		System.out.println(board.print());
	}

}
