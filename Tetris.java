////////
import java.util.*;
import java.io.*;

////////
public class Tetris {
	// 是否启动显示功能
	public boolean enableDisplay = false;
	// 显示刷新的速率
	public int displayRefreshInterval = 100;

	// ####
	public static final int h = 24; // 场地高度
	public static final int nBufferLines = 4; // 用来缓存新生成的方块的行数
	public static final int w = 10; // 场地宽度
	public boolean board[][];

	// ####
	public int piece_x, piece_y; // 方块左上角在场地中的坐标
	public boolean piece[][]; // 方块定义，大小为4x4
	public boolean hasPiece; // 当前是否有正在坠落的方块？

	// ####
	public int score;

	// ####
	public enum PieceOperator {
		ShiftLeft,
		ShiftRight,
		Rotate,
		Drop,
		Keep
	}

	// ####
	Tetris() { 
		board = new boolean[h][w];
	}

	// 返回当前场地状况
	public boolean [][] getBoard() {
		boolean tmp[][] = new boolean[h][w];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				tmp[y][x] = board[y][x];
			}
		}
		return tmp;
	}

	// 返回当前方块状况
	public boolean [][] getPiece() {
		boolean tmp[][] = new boolean[4][4];
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				tmp[y][x] = piece[y][x];
			}
		}
		return tmp;
	}

	// 返回当前方块坐标
	public int getPieceX() { return piece_x; }
	public int getPieceY() { return piece_y; }

	// ####
	public void initPiece(Random rand) {
		// generate a random piece
		switch (rand.nextInt(7)) {
			case 0:
				piece = new boolean[][]{{false,false,false,false},
				                        { true, true, true, true},
				                        {false,false,false,false},
				                        {false,false,false,false}};
				break;
			case 1:
				piece = new boolean[][]{{false,false,false,false},
				                        { true, true,false,false},
				                        {false, true, true,false},
				                        {false,false,false,false}};
				break;
			case 2:
				piece = new boolean[][]{{false,false,false,false},
				                        {false,false, true, true},
				                        {false, true, true,false},
				                        {false,false,false,false}};
				break;
			case 3:
				piece = new boolean[][]{{false,false,false,false},
				                        { true, true, true,false},
				                        { true,false,false,false},
				                        {false,false,false,false}};
				break;
			case 4:
				piece = new boolean[][]{{false,false,false,false},
				                        {false, true, true, true},
				                        {false,false,false, true},
				                        {false,false,false,false}};
				break;
			case 5:
				piece = new boolean[][]{{false,false,false,false},
				                        { true, true, true,false},
				                        {false, true,false,false},
				                        {false,false,false,false}};
				break;	
			case 6:
				piece = new boolean[][]{{false,false,false,false},
				                        {false, true, true,false},
				                        {false, true, true,false},
				                        {false,false,false,false}};
				break;
		}

		// deploy
		piece_y = h-1;
		piece_x = w/2;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				if (piece[y][x]) board[piece_y-y][piece_x+x] = true;
			}
		}
	}

	// ####
	public boolean movePiece(PieceOperator op) {
		// remove piece from board
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				if (piece[y][x]) board[piece_y-y][piece_x+x] = false;
			}
		}
		// generate a new piece
		int new_piece_x = piece_x;
		int new_piece_y = piece_y;
		boolean new_piece[][] = new boolean[4][4];
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				new_piece[y][x] = piece[y][x];
			}
		}
		// piece operation
		switch (op) {
			case ShiftLeft:  new_piece_x--; break;
			case ShiftRight: new_piece_x++; break;
			case Drop:       new_piece_y--; break;
			case Rotate:
				for (int y = 0; y < 4; y++) {
					for (int x = 0; x < 4; x++) {
						new_piece[y][x] = piece[x][3-y];
					}
				}
				break;
		}
		// check if new_piece is deployable
		boolean deployable = true;
		for (int x = 0; x < 4; x++)
		{
			for (int y = 0; y < 4; y++)
			{
				if (!new_piece[y][x]) continue;
				if (new_piece_x+x < 0 || new_piece_x+x >= w
				 || new_piece_y-y < 0 || new_piece_y-y >= h
				 || board[new_piece_y-y][new_piece_x+x])
				{
					deployable = false;
					break;
				}
			}
		}
		if (deployable) {
			// replace piece with new_piece
			piece_x = new_piece_x;
			piece_y = new_piece_y;
			for (int y = 0; y < 4; y++) {
				for (int x = 0; x < 4; x++) {
					piece[y][x] = new_piece[y][x];
				}
			}
		}
		// deploy piece
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				if (piece[y][x]) board[piece_y-y][piece_x+x] = true;
			}
		}

		return deployable;
	}

	/**
	 * return false if game over
	 */
	public boolean updateBoard() {
		if (hasPiece)
			return true;

		// piece has landed, update board
		for (int y = h-nBufferLines; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (board[y][x]) // game over
					return false;
			}
		}
		
		for (int y = 0; y < h-nBufferLines; y++) {
			boolean full = true;
			for (int x = 0; x < w; x++) {
				if (!board[y][x]) {
					full = false;
					break;
				}
			}
			if (full) {
				for (int i = y; i < h-nBufferLines; i++) {
					for (int j = 0; j < w; j++) {
						board[i][j] = board[i+1][j];
						board[i+1][j] = false;
					}
				}
				score ++;
				y--;
			}
		}

		return true;
	}

	// ####
	public final void displayBoard() throws IOException, InterruptedException {
		// clear screen, OS dependent
		new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		// print board
		for (int y = h-1; y >= 0; y--) {
			System.out.printf("%s", y < h-4 ? "<!":"  ");
			for (int x = 0; x < w; x++) {
				System.out.printf("%c", board[y][x] ? 'H':' ');
			}
			System.out.printf("%s", y < h-4 ? "!>":"  ");
			System.out.printf("\n");
		}
		System.out.printf("<!");
		for (int x = 0; x < w; x++) {
			System.out.printf("=");
		}
		System.out.printf("!>");
		System.out.printf("\n");
		// print score
		System.out.println();
		System.out.println("score = "+score);
		System.out.println();
		// flush
		System.out.flush();
	}

	// ####
	public PieceOperator robotPlay()  throws IOException, InterruptedException{
		return PieceOperator.Keep;
	}

	// ####
	public final int run(Random rand) throws IOException, InterruptedException {
		score = 0;
		hasPiece = false;
		int dropAlarm = 0;
		while (updateBoard()) {
			if (!hasPiece) {
				initPiece(rand);
				hasPiece = true;
				if(enableDisplay)
				{
					displayBoard();
					Thread.sleep(displayRefreshInterval);
				}
			}
			// call robot to play
			movePiece(robotPlay());
			dropAlarm = (dropAlarm+1)%5;
			if (dropAlarm == 0) {
				boolean landed = !movePiece(PieceOperator.Drop);
				if (landed) hasPiece = false;
			}
//			if (enableDisplay) {
//				displayBoard();
//				Thread.sleep(displayRefreshInterval);
//			}
		}
		return score;
	}

}
