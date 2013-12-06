package Chess;

public class Controller 
{
	private Board board = new Board();
	private boolean whiteTurn;
	private int totalTurns = 0;
	private String white = "White";
	private String black = "Black";

	private boolean whitePlayerTurn()
	{		
		whiteTurn = (totalTurns % 2 == 0) ? true : false;
		return whiteTurn;
	}

	public void addPieceToBoard(Piece piece, int x, int y)
	{
		Position position = new Position(x, y);
		Square newSquare = new Square(piece, position);
		board.setChessBoardSquare(newSquare, x, y);
	}

	public void movePieceOnBoard(String command, Position start, Position end)
	{
		int x1 = start.getPositionX();
		int y1 = start.getPositionY();
		int x2 = end.getPositionX();
		int y2 = end.getPositionY();
		String startSpot = command.substring(0,2);
		String endSpot = command.substring(3,5);
		String player = "";
		player = whitePlayerTurn() ? "White" : "Black";        
		Piece piece = board.getChessBoardSquare(x1, y1).getPiece();
		Square newSquare = new Square(board.getChessBoardSquare(x2, y2).getPiece(), end);
		Pawn dummyPawn = new Pawn("Pawn");

		System.out.println("\nIt is Player " + player + "'s turn");

		if(piece.getClass() == dummyPawn.getClass())
		{
			if(piece.getPieceColor() == white)
			{
				if(dummyPawn.validWhiteMovement(x1, y1, x2, y2, newSquare))
				{
					movePieceCheck(piece, x1, y1, x1, y2, start, end, command, startSpot, endSpot);
				}
				else if(dummyPawn.pawnCapturing(x1, y1, x2, y2, piece.getPieceColor(), newSquare))
				{
					movePieceCheck(piece, x1, y1, x2, y2, start, end, command, startSpot, endSpot);
				}
				else
				{
					System.out.print(command + " is an invalid move command! Please revise it!");
					System.out.println();
				}
			}
			else if(piece.getPieceColor() == black)
			{
				if(dummyPawn.validBlackMovement(x1, y1, x2, y2, newSquare))
				{
					movePieceCheck(piece, x1, y1, x1, y2, start, end, command, startSpot, endSpot);
				}
				else if(dummyPawn.pawnCapturing(x1, y1, x2, y2, piece.getPieceColor(), newSquare))
				{
					movePieceCheck(piece, x1, y1, x2, y2, start, end, command, startSpot, endSpot);
				}
				else
				{
					System.out.print(command + " is an invalid move command! Please revise it!");
					System.out.println();
				}
			}
		}
		else
		{
			if(piece.validMovement(x1, y1, x2, y2))
			{
				movePieceCheck(piece, x1, y1, x2, y2, start, end, command, startSpot, endSpot);
			}
			else
			{
				System.out.println();
				System.out.print(command + " is an invalid move command! Please revise it!");
				System.out.println();
			}
		}
	}

	private void movePieceCheck(Piece piece, int x1, int y1, int x2, int y2, Position start, Position end, String command, String startSpot, String endSpot)
	{		                                            
		if(board.getChessBoardSquare(x2, y2).getPiece().getPieceColor() != (piece.getPieceColor()))
		{
			if(otherPieceExistsInPath(x1, y1, x2, y2))
			{
				String color = whitePlayerTurn() ? white : black;

				if(piece.getPieceColor() == color)
				{
					board.setChessBoardSquare(new Square(piece, end), x2, y2);

					board.setChessBoardSquare(new Square(new Piece("-"), start), x1, y1);
					board.getChessBoardSquare(x1, y1).getPiece().setPieceColor("-");

					totalTurns++;

					System.out.println();
					System.out.print("Successfully moved piece " + startSpot + " to " + endSpot);
					board.printBoard();
					System.out.println();
				}
				else
				{
					System.out.println();
					System.out.println(command);
					System.out.println("Piece at " + startSpot + " is not your piece!  Choose from your own color!");
				}
			}
			else
			{
				System.out.println();
				System.out.println(command + " \nYou can't do that!  Your path is being blocked!");
			}
		}
		else
		{
			System.out.println();
			System.out.print("There is a an ally piece on the spot you are trying to move to! " + endSpot + " \nMove is not valid!");
		}
	}

	public boolean otherPieceExistsInPath(int x1, int y1, int x2, int y2)
	{
		boolean moveCompletable = true;

		int verticleMovement = y2 - y1;
		int horizontalMovement = x2 - x1;
		int upRightMovement = verticleMovement - horizontalMovement;                
		int diagonalMovement = verticleMovement + horizontalMovement;
		int mathCheck = 0;

		if(verticleMovement != mathCheck && (horizontalMovement == mathCheck))
		{                        
			moveCompletable = (y2 > y1) ? verticleMovementPathCheck(x1, y1, y2) : verticleMovementPathCheck(x1, y2, y1);
		}

		if(horizontalMovement != mathCheck && (verticleMovement == mathCheck))
		{
			moveCompletable = (x2 > x1) ? horizontalMovementPathCheck(y1, x1, x2) : horizontalMovementPathCheck(y1, x2, x1);
		}

		if(diagonalMovement == mathCheck)
		{
			moveCompletable = (x2 < x1) && (y2 > y1) ? diagonalMovementPathCheckBottomRightToTopLeft(y1, x1, x2) : diagonalMovementPathCheckBottomRightToTopLeft(y2, x2, x1); 
		}

		if(upRightMovement == mathCheck || (verticleMovement == horizontalMovement))
		{
			moveCompletable = (x2 > x1) && (y2 > y1) ? diagonalMovementPathCheckBottomLeftToTopRight(y1, x1, x2) : diagonalMovementPathCheckBottomLeftToTopRight(y2, x2, x1);
		}

		return moveCompletable;
	}

	private boolean verticleMovementPathCheck(int x1, int y1, int y2)
	{
		boolean moveCompletable = true;

		for(int i = y1+1; moveCompletable && (i < y2); i++)
		{
			if(board.getChessBoardSquare(x1, i).getPiece().getPieceType() != board.getBlankPiece().getPieceType())
			{
				moveCompletable = false;
			}
		}
		return moveCompletable;
	}

	private boolean horizontalMovementPathCheck(int y1, int x1, int x2)
	{
		boolean moveCompletable = true;

		for(int i = x1+1; moveCompletable && (i < x2); i++)
		{
			if(board.getChessBoardSquare(i, y1).getPiece().getPieceType() != board.getBlankPiece().getPieceType())
			{
				moveCompletable = false;
			}
		}                
		return moveCompletable;
	}

	private boolean diagonalMovementPathCheckBottomRightToTopLeft(int y1, int x1, int x2)
	{
		boolean moveCompletable = true;
		int y = y1+1;
		for(int x = x1-1; moveCompletable && (x > x2); x--, y++)
		{
			if(board.getChessBoardSquare(x, y).getPiece().getPieceType() != board.getBlankPiece().getPieceType())
			{
				moveCompletable = false;
			}
		}

		return moveCompletable;
	}

	private boolean diagonalMovementPathCheckBottomLeftToTopRight(int y1, int x1, int x2)
	{
		boolean moveCompletable = true;

		int y = y1+1;
		for(int x = x1+1; moveCompletable && (x < x2); x++, y++)
		{
			if(board.getChessBoardSquare(x, y).getPiece().getPieceType() != board.getBlankPiece().getPieceType())
			{
				moveCompletable = false;
			}
		}
		return moveCompletable;
	}
}