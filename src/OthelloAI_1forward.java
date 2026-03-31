import java.net.*;
import java.io.*;

public class OthelloAI_1forward {
    private Socket s;
	private InputStream sIn;
	private OutputStream sOut;
	private BufferedReader br;
	private PrintWriter pw;
	private int i, j, k, index_i, index_j, value_Evaluate, now_max;
    private byte opponentColor;
	private String str, color = null, current_turn = null;
	private String[] strarray = null;

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

    private boolean[][] isvaild = new boolean[8][8];

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

    public OthelloAI_1forward(String hostname, int port) {
		try {
	    	s = new Socket(hostname, port);
	    
	    	sIn = s.getInputStream();
	    	sOut = s.getOutputStream();	
	    	br = new BufferedReader(new InputStreamReader(sIn));
	    	pw = new PrintWriter(new OutputStreamWriter(sOut), true);

            for(i = 0; i < 8; i++) {
                for(j = 0; j < 8; j++) {
                    isvaild[i][j] = false;
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
					color = strarray[1];
                    opponentColor = reverse(color);
					pw.println("NICK AI_1forward");
				}

				if (strarray[0].equals("TURN")) {
                    current_turn = strarray[1];
                    if (color.equals(current_turn)) {
                        turn_Evaluate();

                        pw.println("PUT " + index_j + " " + index_i);
                        System.out.println("PUT " + index_j + " " + index_i);
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

    private boolean isValidMove(int x, int y) {
        if (board[x][y] != 0) {
            return false;
        }
    
        int dx, dy;
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
    
                if (hasOpponentColor && i >= 0 && i < 8 && j >= 0 && j < 8 && board[i][j] == Byte.parseByte(color)) {
                    return true;
                }
            }
        }
    
        return false;
    }    

    private byte reverse(String color) {
        if (color.equals("1")) {
            return -1;
        } else {
            return 1;
        }
    }

    private void turn_Evaluate() {
        for(i = 0; i < 8; i++) {
            for(j = 0; j < 8; j++) {
                isvaild[i][j] = isValidMove(i, j);
            }
        }

        now_max = -100;
        for(i = 0; i < 8; i++) {
            for(j = 0; j < 8; j++) {
                if (isvaild[i][j]) {
                    value_Evaluate = Evaluate_table[i][j];
                    if (now_max <= value_Evaluate) {
                        now_max = value_Evaluate;
                        index_i = i;
                        index_j = j;
                    }
                }
            }
        }
    }

    public static void main(String args[]) {
		new OthelloAI_1forward(args[0], Integer.parseInt(args[1]));
    }
}