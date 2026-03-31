import java.net.*;
import java.io.*;
import java.util.*;

class OthelloAIClient_Random {
    public static void main(String args[]) {
		Socket s;
		InputStream sIn;
		OutputStream sOut;
		BufferedReader br;
		PrintWriter pw;
		Random rand;
		int i = 0, j = 0;
		String str, color = null;
		String[] strarray = null;
		if (args.length != 2) {
	    	System.out.println("No hostname and port given");
	    	System.exit(1);
		}
	
		try {
	    	s = new Socket(args[0], Integer.parseInt(args[1]));
	    
	    	sIn = s.getInputStream();
	    	sOut = s.getOutputStream();	
	    	br = new BufferedReader(new InputStreamReader(sIn));
	    	pw = new PrintWriter(new OutputStreamWriter(sOut), true);

			rand = new Random();

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
					pw.println("NICK AIClient");
				}

				if (strarray[0].equals("TURN")) {
					if (color.equals(strarray[1])) {
						i = rand.nextInt(8);
						j = rand.nextInt(8);

						pw.println("PUT " + i + " " + j);
					}
				}

				if (strarray[0].equals("ERROR")) {
					i = rand.nextInt(8);
					j = rand.nextInt(8);

					pw.println("PUT " + i + " " + j);
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
}