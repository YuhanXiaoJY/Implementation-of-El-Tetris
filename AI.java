////////
import java.net.SocketOption;
import java.time.Period;
import java.util.*;
import java.io.*;

////////
public class HW1_1600012821 extends Tetris {
	public class myPair
	{
		public int pair_score;
		public int landpoint;
		public myPair(int a, int b)
		{
			pair_score = a;
			landpoint = b;
		}
	}
	// enter your student id here
	public String id = new String("1600012821");
	public int boardInfo[];
	public int rotate;	//旋转次数
	public int shift;	//左右平移
	public int eval_score;

	public boolean IsInitial()
	{
		if(piece_x == w/2 && piece_y == h-1  && eval_score == 0)
			return true;
		else
			return false;
	}

	// ####
	public PieceOperator robotPlay(){
		if(IsInitial())
		{
			boardInfo = new int[w];
			for(int x=0; x<w; x++)
				boardInfo[x] = -1;
			for (int x = 0; x < w; x++) {
				for (int y = h - nBufferLines -1; y >= 0; y--) {
					if (!board[y][x])
						continue;
					boardInfo[x] = y;
					break;
				}
			}        //获得每一列最高的方块的纵坐标

			myPair pos_score[] = new myPair[4];
			pos_score = DiffPos();
			rotate = 0;
			shift = piece_x - pos_score[0].landpoint;
			eval_score = pos_score[0].pair_score;
			for (int i = 1; i < 4; i++) {
				if (pos_score[i].pair_score >= eval_score ) {
					rotate = i;
					shift = piece_x - pos_score[i].landpoint;
					eval_score = pos_score[i].pair_score;
				}
			}
		}
		//移动
		if(rotate > 0)
		{
			rotate--;
			return PieceOperator.Rotate;
		}
		if(shift != 0)
		{
			if(shift > 0)	//左移
			{
				shift--;
				return PieceOperator.ShiftLeft;
			}
			else	//右移
			{
				shift++;
				return PieceOperator.ShiftRight;
			}
		}
		if(eval_score != 0)
			eval_score = 0;
		return PieceOperator.Drop;
	}

	public void CopyPiece(boolean des[][], boolean src[][])
	{
		for(int x = 0; x<4;x++)
			for (int y =0 ; y<4;y++)
			{
				des[x][y] = src[x][y];
			}
	}

	public void CopyBoard(boolean des[][], boolean src[][])
	{
		for(int x = 0; x<w;x++)
			for (int y =0 ; y<h;y++)
			{
				des[y][x] = src[y][x];
			}
	}

	public void RotatePiece(boolean des[][], boolean src[][])
	{
		for(int y = 0; y < 4; y++)
			for(int x = 0; x < 4; x++)
				des[y][x] = src[x][3-y];
	}


	public myPair[] DiffPos()
	{
		myPair pos_score[] = new myPair[4];

		boolean tmp[][]= new boolean[4][4];
		boolean eval_piece[][] = new boolean[4][4];
		CopyPiece(tmp, piece);
		CopyPiece(eval_piece, piece);

		pos_score[0] = eval(eval_piece);	//no rotation
		if(piece[1][1] && piece[1][2] && piece[2][1] && piece[2][2])
		{
			for(int i = 1; i < 4; i++)
				pos_score[i] = new myPair(-1000000000, -10);
			return pos_score;
		}

		RotatePiece(eval_piece, tmp);	//rotate once
		pos_score[1] = eval(eval_piece);	//get the score

		if((piece[1][0] && piece[1][1] && piece[1][2] && piece[1][3])
		|| (piece[1][0] && piece[1][1] && piece[2][1] && piece[2][2])
		|| (piece[2][1] && piece[2][2] && piece[1][2] && piece[1][3]))
		{
			for(int i = 2; i< 4; i++)
				pos_score[i] = new myPair(-1000000000, -10);
			return pos_score;
		}

		RotatePiece(tmp, piece);
		RotatePiece(eval_piece, tmp);	//rotate twice
		pos_score[2] = eval(eval_piece);

		CopyPiece(tmp, eval_piece);
		RotatePiece(eval_piece, tmp);	//rotate third
		pos_score[3] = eval(eval_piece);

		return pos_score;
	}

	public boolean Deployable1(boolean eval_piece[][], int new_piece_x, int new_piece_y)
	{
		boolean deployable = true;
		for (int x = 0; x < 4; x++)
		{
			for (int y = 0; y < 4; y++)
			{
				if (!eval_piece[y][x]) continue;
				if (new_piece_x+x < 0 || new_piece_x+x >= w
						|| new_piece_y-y < 0 || new_piece_y-y >= h)
				{
					return false;
				}
			}

		}
		return deployable;
	}
	public boolean Deployable2(boolean eval_piece[][], int new_piece_x, int new_piece_y)
	{
		boolean deployable = true;
		for (int x = 0; x < 4; x++)
		{
			for (int y = 0; y < 4; y++)
			{
				if (!eval_piece[y][x]) continue;
				if (new_piece_x+x < 0 || new_piece_x+x >= w
						|| new_piece_y-y < 0 || new_piece_y-y >= h
						|| board[new_piece_y-y][new_piece_x+x])
				{
					return false;
				}
			}

		}
		return deployable;
	}

	public void FillBoard(boolean eval_piece[][], int new_piece_x, int new_piece_y, boolean new_board[][])
	{
		for (int x = 0 ; x < 4; x++)
		{
			for (int y = 0; y < 4; y++)
			{
				if(!eval_piece[y][x])
					continue;
				new_board[new_piece_y - y][new_piece_x+x] = eval_piece[y][x];
			}
		}
		for (int y = 0; y < h-nBufferLines; y++) {
			boolean full = true;
			for (int x = 0; x < w; x++) {
				if (!new_board[y][x]) {
					full = false;
					break;
				}
			}
		}
	}

	public myPair eval(boolean eval_piece[][])
	{
		myPair []scoreList = new myPair[w+1];
		for (int i = -2; i<=w-2; i++)
		{
			int new_piece_x = i;
			int new_piece_y = piece_y;

			//判断是否越过边界
			boolean deployable = Deployable1(eval_piece, new_piece_x ,new_piece_y);
			if(!deployable)
			{
				myPair tmp = new myPair(-1000000000, -10);
				scoreList[i+2] = tmp;
				continue;
			}


			int point_x = 0;
			boolean found = false;
			for(int x = 0; x< 4; x++)
			{
				for(int y = 0; y < 4; y++)
				{
					if(eval_piece[y][x])
					{
						point_x = x;
						found = true;
						break;
					}
				}
				if(found)
					break;
			}

			//piece下落，同时填充new_board
			new_piece_y = h - nBufferLines - 1;		//conflict test: let the piece drop from the top
			deployable = Deployable2(eval_piece, new_piece_x, new_piece_y);
			while(deployable)
			{
				new_piece_y--;
				deployable = Deployable2(eval_piece, new_piece_x, new_piece_y);
			}
			new_piece_y++;
			boolean new_board[][] = new boolean[h][w];
			CopyBoard(new_board, board);
			FillBoard(eval_piece, new_piece_x, new_piece_y, new_board);
//			System.out.println("newboard:");
//			my_displayBoard(new_board);
//			System.out.println("board:");
//			my_displayBoard(board);
			//获取score
			Grade grade = GetScore(eval_piece, new_board, new_piece_y, new_piece_x);
			myPair tmp = new myPair(grade.sum, new_piece_x);
			scoreList[i+2] = tmp;
//			System.out.printf("score=%d, x=%d, y=%d, lh=%d, epcm=%d, rt=%d, ct=%d, bh=%d, well=%d\n",
//					tmp.pair_score, new_piece_x, new_piece_y,grade.lh, grade.epcm, grade.rt, grade.ct, grade.bh, grade.well);
		}

		//选取具有max score那个myPair返回
		myPair max_pair = new myPair(-1000000000, -10);
		for(int i = 0 ;i <= w; i++)
		{
			if(scoreList[i].pair_score > max_pair.pair_score)
			{
				max_pair = scoreList[i];
			}
			else if(scoreList[i].pair_score == max_pair.pair_score)
			{
				int prio1 = getPriority(scoreList[i].landpoint);
				int prio2 = getPriority(max_pair.landpoint);
				if(prio1 > prio2)
					max_pair = scoreList[i];
			}
		}
		return max_pair;
	}

	public int getPriority(int landpoint)
	{
		int priority = (landpoint - piece_x )*400;
		if(priority > 0)//右移
			return priority;
		else if(priority < 0)//左移
			return 10-priority;
		else
			return 0;
	}


	public class Grade
	{
		int sum;
		int lh;
		int epcm;
		int rt;
		int ct;
		int bh;
		int well;
		public Grade(int a, int b, int c, int d ,int e, int f, int g)
		{
			sum = a;
			lh = b;
			epcm = c;
			rt = d;
			ct = e;
			bh = f;
			well = g;
		}
	}
	//给出newboard的score
	public Grade GetScore(boolean eval_piece[][], boolean newboard[][], int new_piece_y, int new_piece_x)
	{
		int landingHeight = GetLandingHeight(new_piece_y, eval_piece);
		int erodedPieceCellsMetric = GetErodedPieceCellsMetric(newboard, eval_piece, new_piece_y, new_piece_x);
		int RowTransitions = GetRowTransitions(newboard);
		int ColTransitions = GetColTransitions(newboard);
		int BuriedHoles = GetBuriedHoles(newboard);
		int Wells = GetWells(newboard);

		int sum = -45*landingHeight + 34*erodedPieceCellsMetric -
				32*RowTransitions -
				98*ColTransitions -
				79*BuriedHoles -
				34*Wells;
		Grade grade = new Grade(sum, landingHeight, erodedPieceCellsMetric, RowTransitions, ColTransitions, BuriedHoles, Wells);
		return grade;
	}

	public int GetLandingHeight(int new_piece_y, boolean eval_piece[][])
	{
		for(int y = 0; y < 4; y++)
		{
			for(int x = 0; x < 4; x++)
			{
				if(eval_piece[y][x])
					return new_piece_y-y;
			}
		}
		return 0;
	}

	public int GetErodedPieceCellsMetric(boolean newboard[][], boolean eval_piece[][], int new_piece_y, int new_piece_x)
	{
		int eliminated = 0;
		int usefulBlocks = 0;
		for (int y = 0; y < h-nBufferLines; y++) {
			boolean full = true;
			for (int x = 0; x < w; x++) {
				if (!newboard[y][x]) {
					full = false;
					break;
				}
			}
			if (full) {
				for(int x =0; x< 4; x++)
				{
					if(eval_piece[new_piece_y - y][x])
						usefulBlocks++;
				}
//				for (int i = y; i < h-nBufferLines; i++) {
//					for (int j = 0; j < w; j++) {
//						newboard[i][j] = newboard[i+1][j];
//						newboard[i+1][j] = false;
//					}
//				}
				//System.out.println("**********");
				eliminated ++;
				//y--;
			}
		}
		return (eliminated * usefulBlocks);
	}

	public int GetRowTransitions(boolean newboard[][])
	{
		//获取highest piece的纵坐标
		int highest = -1;
		for(int y= h-nBufferLines-1; y>=0; y--)
		{
			for(int x = 0; x< w; x++)
			{
				if(newboard[y][x])
				{
					highest = y;
					break;
				}
			}
			if(highest >= 0)
				break;
		}

		//计算row_transitions
		int row_transitions = 0;
		for(int y = 0 ; y <= highest; y++)
		{
			int temp =0 ;
			boolean last = true;	//认为边界都是有方块填充的
			boolean now = false;
			for(int x = 0; x < w; x++)
			{
				now = newboard[y][x];
				if(last != now)
				{
					temp++;
				}
				last = now;
			}
			if(!newboard[y][w-1])
				temp++;
			row_transitions += temp;
		}
		return row_transitions;
	}

	public int GetColTransitions(boolean newboard[][])
	{
		int col_transitions = 0;
		for (int x = 0; x< w;x++)
		{
			boolean last = true;
			boolean now = false;
			int temp = 0;
			for (int y = 0 ; y < h - nBufferLines; y++)
			{
				now = newboard[y][x];
				if(now != last)
					temp++;
				last = now;
			}
			col_transitions += temp;
		}
		return col_transitions;
	}

	public int GetBuriedHoles(boolean newboard[][])
	{
		int holes = 0;
		for(int x = 0; x< w; x++)
		{
			int temp = 0;
			boolean upfilled = false;
			for(int y = h-nBufferLines -1 ;y >=0 ; y--)
			{
				//顶上未封
				if((!upfilled) && newboard[y][x])
				{
					upfilled = true;
					continue;
				}
				//顶部已封
				if(upfilled && (!newboard[y][x]))
				{
					temp ++;
				}
			}
			holes += temp;
		}
		return holes;
	}

	public int GetWells(boolean newboard[][])
	{
		int well_points = 0;
		for(int x = 0; x< w; x++)
		{
			int well_dep = 0;	//井深
			for(int y =h-nBufferLines-1 ; y >= 0; y--)
			{
				if(x == 0)
				{
					if(!newboard[y][x] && newboard[y][x+1])
					{
						well_dep ++;
					}
					else if(newboard[y][x])
					{
						well_points += well_dep *(well_dep + 1)/2;
						well_dep = 0;
					}
				}
				else if(x == w-1)
				{
					if(!newboard[y][x] && newboard[y][x-1])
					{
						well_dep ++;
					}
					else if(newboard[y][x])
					{
						well_points += well_dep *(well_dep + 1)/2;
						well_dep = 0;
					}
				}
				else {
					if (!newboard[y][x] && newboard[y][x - 1] && newboard[y][x + 1]) {
						well_dep++;
					} else if (newboard[y][x]) {
						well_points += well_dep * (well_dep + 1) / 2;
						well_dep = 0;
					}
				}
			}
			well_points += well_dep * (well_dep + 1)/2;
		}
		return well_points;
	}


	public void my_displayBoard(boolean newboard[][]){
		// print board
		for (int y = h-1; y >= 0; y--) {
			System.out.printf("%s", y < h-4 ? "<!":"  ");
			for (int x = 0; x < w; x++) {
				System.out.printf("%c", newboard[y][x] ? 'H':' ');
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
}


