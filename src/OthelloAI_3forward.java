import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;
import java.awt.Point;

public class OthelloAI_3forward {
    private Socket s;
	private InputStream sIn;
	private OutputStream sOut;
	private BufferedReader br;
	private PrintWriter pw;
	private int i, j, k, l, m, n, o, max_line, max_column, opponent_line, opponent_column, value_Evaluate, now_max, max, opponent_Evaluate;
    private byte color, opponentColor, current_turn;
	private String str;
	private String[] strarray;
    private Random rand = new Random();

    private byte[][] board = {
        {0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 1, -1, 0, 0, 0},
		{0, 0, 0, -1, 1, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0}
    };

    private byte[][] tmp_board1 = new byte[8][8];
    private byte[][] tmp_board2 = new byte[8][8];
    private boolean[][] isVaild = new boolean[8][8];

    private int[][] Evaluate_table = {
        {30, -12, 0, -1, -1, 0, -12, 30},
        {-12, -15, -3, -3, -3, -3,-15, -12},
        {0, -3, 0, -1, -1, 0, -3, 0},
        {-1, -3, -1, -1, -1, -1, -3, -1},
        {-1, -3, -1, -1, -1, -1, -3, -1},
        {0, -3, 0, -1, -1, 0, -3, 0},
        {-12, -15, -3, -3, -3, -3,-15, -12},
        {30, -12, 0, -1, -1, 0, -12, 30},
    };

    public OthelloAI_3forward(String hostname, int port) {
		try {
	    	s = new Socket(hostname, port);
	    
	    	sIn = s.getInputStream();
	    	sOut = s.getOutputStream();	
	    	br = new BufferedReader(new InputStreamReader(sIn));
	    	pw = new PrintWriter(new OutputStreamWriter(sOut), true);

            for(i = 0; i < 8; i++) {
                for(j = 0; j < 8; j++) {
                    isVaild[i][j] = false;
                }
            }

	    	while (true) {
				str = br.readLine();
				if (str != null) {
					strarray = str.split(" ");
				} else {
					System.err.println("Disconnected with Server");
					System.exit(1);
				}

				if (strarray[0].equals("START")) {
					color = Byte.parseByte(strarray[1]);
                    opponentColor = (byte)(color * -1);
					pw.println("NICK 6322045");
				}

				if (strarray[0].equals("TURN")) {
                    current_turn = Byte.parseByte(strarray[1]);

                    if (current_turn == color) {
                        turn3_Evaluate();

                        pw.println("PUT " + max_column + " " + max_line);
                    }
				}

                if (strarray[0].equals("BOARD")) {
                    k = 1;
                    for(i = 0; i < 8; i++) {
                        for(j = 0; j < 8; j++) {
                            board[j][i] = Byte.parseByte(strarray[k]);
                            k++;
                        }
                    }
                }

				if (strarray[0].equals("CLOSE")) {
					System.err.println(str);
					System.exit(1);
				}

				if (strarray[0].equals("END")) {
					System.out.println(str);
					System.exit(0);
				}
			}
	    } catch (IOException e) {
	    	System.err.println("Caught IOException");
	    	System.exit(1);
		}
    }

    private boolean isValidMove(int x, int y, byte color, byte[][] board) {
        if (board[x][y] != 0) {
            return false;
        }
    
        int dx, dy;
        byte opponentColor = (byte)(color * -1);
        for (dx = -1; dx <= 1; dx++) {
            for (dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
    
                int i = x + dx;
                int j = y + dy;
                boolean hasOpponentColor = false;
    
                while (i >= 0 && i < 8 && j >= 0 && j < 8 && board[i][j] == opponentColor) {
                    i += dx;
                    j += dy;
                    hasOpponentColor = true;
                }
    
                if (hasOpponentColor && i >= 0 && i < 8 && j >= 0 && j < 8 && board[i][j] == color) {
                    return true;
                }
            }
        }
    
        return false;
    }
    
    private void flipOpponentStones(int x, int y, byte color, byte[][] board) {
		byte opponentColor = (byte)(color * -1);
		int dx, dy;
	
		for (dx = -1; dx <= 1; dx++) {
			for (dy = -1; dy <= 1; dy++) {
				if (dx == 0 && dy == 0) {
					continue;
				}
				int count = 0;
				int i = x + dx;
				int j = y + dy;
				ArrayList<Point> stonesToFlip = new ArrayList<>();
	
				while (i >= 0 && i < 8 && j >= 0 && j < 8 && board[i][j] == opponentColor) {
					count++;
					stonesToFlip.add(new Point(i, j));
					i += dx;
					j += dy;
				}
	
				if (i >= 0 && i < 8 && j >= 0 && j < 8 && count > 0 && board[i][j] == color) {
					for (Point stone : stonesToFlip) {
						board[stone.x][stone.y] = color;
					}
				}
			}
		}
	}

    private void turn3_Evaluate() {
        now_max = -1000000;

        ArrayList<Point> bestMoves = new ArrayList<>();
        int random;
        Point index = new Point(), p;

        for(i = 0; i < 8; i++) {
            for(j = 0; j < 8; j++) {
                for(l = 0; l < 8; l++) {
                    for(m = 0; m < 8; m++) {
                        tmp_board1[l][m] = board[l][m];
                        isVaild[l][m] = isValidMove(l, m, color, board);
                    }
                }

                if (isVaild[i][j]) {
                    value_Evaluate = Evaluate_table[i][j];
                    index.x = i;
                    index.y = j;

                    flipOpponentStones(i, j, color, tmp_board1);

                    for(l = 0; l < 8; l++) {
                        for(m = 0; m < 8; m++) {
                            tmp_board2[l][m] = tmp_board1[l][m];
                            isVaild[l][m] = isValidMove(l, m, opponentColor, tmp_board1);
                        }
                    }

                    if (evaluate_Opponent() != -10000) {
                        value_Evaluate -= evaluate_Opponent();
                        flipOpponentStones(opponent_line, opponent_column, opponentColor, tmp_board2);
                    }

                    for(l = 0; l < 8; l++) {
                        for(m = 0; m < 8; m++) {
                            isVaild[l][m] = isValidMove(l, m, color, tmp_board2);
                        }
                    }

                    for(l = 0; l < 8; l++) {
                        for(m = 0; m < 8; m++) {
                            if (isVaild[l][m]) {
                                value_Evaluate += Evaluate_table[l][m]; 
                            }

                            if (value_Evaluate > now_max) {
                                bestMoves.clear();
                                bestMoves.add(index);

                                now_max = value_Evaluate;
                            } else if (value_Evaluate == now_max) {
                                if (!bestMoves.contains(index)) {
                                    bestMoves.add(index);
                                }
                            }
                        }
                    }
                }
            }
        }

        random = rand.nextInt(bestMoves.size());
        p = bestMoves.get(random);
        max_line = p.x;
        max_column = p.y;
    }

    private int evaluate_Opponent() {
        for(n = 0; n < 8; n++) {
            for(o = 0; o < 8; o++) {
                isVaild[n][o] = isValidMove(n, o, opponentColor, tmp_board1);
            }
        }

        max = -10000;
        for(n = 0; n < 8; n++) {
            for(o = 0; o < 8; o++) {
                if (isVaild[n][o]) {
                    opponent_Evaluate = Evaluate_table[n][o];
                    if (max <= opponent_Evaluate) {
                        max = opponent_Evaluate;
                        opponent_line = n;
                        opponent_column = o;
                    }
                }
            }
        }

        return max;
    }

    public static void main(String args[]) {
		new OthelloAI_3forward(args[0], Integer.parseInt(args[1]));
    }
}