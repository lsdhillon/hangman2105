/*
 * Single-threaded HTTP server to support Hangman game over a browser
 * To start a new game, run this file and enter http://localhost:7000/Server.java in your browser
 */

import java.util.*;
import java.io.*;
import java.net.*;


public class Server {

        private static final int MAX=10;   // amount of errors till loss
        private static  int errors;        // amount of errors
        private static String message;   // message
        private static String information; // information
        private static String rword;      // the right word
        private static StringBuffer gword;// the guessed word
        private static StringBuffer fword;// the wrong guesses
        private static StringBuffer cword;// the right guesses
        private static int len =0, k=0, j=0;
        private static boolean won = false; // to check if user has won


    public static void main (String args[]) throws Exception
	{
		// throws Exception here because don't want to deal
		// with errors in the rest of the code for simplicity.

		// Create a new TCP WELCOME SOCKET that waits for connection at port
		// number 9000.
		ServerSocket serverSock = new ServerSocket(7000);
		System.out.println("SERVER IS WAITING FOR HTTP REQUEST at PORT 7000...");
                  while (true) {
			//Listen & Accept Connection and Create new CONNECTION SOCKET
			Socket s = serverSock.accept();

			// The next 3 lines create a buffer reader that
			// reads from the socket s.
			InputStream is = s.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			// The next 2 lines create a output stream we can
			// write to.
			OutputStream os = s.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);


                       // Read HTTP request (empty line signal end of request)
                        String input = br.readLine();
			String request = "";
                        String current_dir = "C://Images//";

                        while (input.compareTo("") != 0)
			{
                        StringTokenizer st = new StringTokenizer(input);
                        System.out.println(input);
                        if (st.nextToken().equals("GET"))
			{
				// This is a GET request.  Parse request.

                                request = st.nextToken();

                                // Case when a new game is started
				if (request.endsWith("/Server.java")) {
                                newGame();
                                dos.writeBytes("<html><body><font size='5'>" + "<b>------------------------------------------<b>" + "</br></body></html>" );
                                dos.writeBytes("<html><body>" + "<b>Welcome to the Hangman Game<b>" + "</br></body></html>" );
                                dos.writeBytes("<html><body>" + "<b>------------------------------------------<b>" + "</br></body></html>" );
                                dos.writeBytes("<html><body>" + "Word:  " + (new String (gword)).toUpperCase() + "</br></body></html>" );
                                dos.writeBytes("<html><body>" + "Incorrect guesses:  " + (new String (fword)).toUpperCase() + "</br></body></html>" );
                                dos.writeBytes("<html><body>" + "Correct guesses:  " + (new String (cword)).toUpperCase() + "</br></br></body></html>" );
                                dos.writeBytes("<html><body>" +  message + "</br></body></html>" );
                                dos.writeBytes("<html><body>" + information + "</br></br></body></html>" );
                                dos.writeBytes("<html><body>" + "<form method =" + "get" + " action=" +                                     "/" + ">" +
                                        "Your Answer: <input type=" + "text" + " name=" + "Answer " + "><br/>"
                                        + "<input type=submit" + " value=" + "Submit" + ">" + "</form>" +
                                        "</body></html>" );
                                dos.writeBytes("<html><body><img src=" + "0.gif" + "></br></br></body></html>" );
                                }

                                // Case when a letter is guessed
                                else if (request.startsWith("/?Answer="))
                                {
                                 if (request.length() >= 9 && errors < MAX && !won) {
                                String guess = request.substring(9);
                                if (guess.compareTo("") != 0)
                                checkAnswer(guess.toUpperCase());
                                    }
                                dos.writeBytes("<html><body><font size='5'>" + "<b>------------------------------------------<b>" + "</br></body></html>" );
                                dos.writeBytes("<html><body>" + "<b>Welcome to the Hangman Game<b>" + "</br></body></html>" );
                                dos.writeBytes("<html><body>" + "<b>------------------------------------------<b>" + "</br></body></html>" );
                                dos.writeBytes("<html><body>" + "Word: " + (new String (gword)).toUpperCase() + "</br></body></html>" );
                                dos.writeBytes("<html><body>" + "Incorrect: " + (new String (fword)).toUpperCase() + "</br></body></html>" );
                                dos.writeBytes("<html><body>" + "Correct: " + (new String (cword)).toUpperCase() + "</br></br></body></html>" );
                                dos.writeBytes("<html><body>" +  message + "</br></body></html>" );
                                dos.writeBytes("<html><body>" + information + "</br></br></body></html>" );
                                dos.writeBytes("<html><body>" + "<form method =" + "get" + " action=" +
                                        "/" + ">" +
                                        "Your Answer: <input type=" + "text" + " name=" + "Answer " + "><br/>"
                                        + "<input type=submit" + " value=" + "Submit" + ">" + "</form>" +
                                        "</body></html>" );
                                dos.writeBytes("<html><body><img src=" +  errors + ".gif" + "></br></br></body></html>" );
                                 }
                               // Case when client requests for a file
                                 else if (request.endsWith(".gif"))
				{
                                int len = request.length();
				request = request.substring(1).toUpperCase();
                                request = current_dir + request;
				File f = new File(request);
                                if (f.exists()) {
				int size = (int)f.length();

				//Create a File InputStrem to read the File
				FileInputStream fis = new FileInputStream(request);
				byte[] buffer = new byte[size];
				fis.read(buffer);

				// Now, write buffer to client
				// (but, send HTTP response header first)

				dos.writeBytes("HTTP/1.0 200 Okie \r\n");
				dos.writeBytes("Content-type: img/html\r\n");
				dos.writeBytes("\r\n");
				dos.write(buffer, 0, size);
                                dos.writeBytes("<html><body>" + "<img src=" + request + "/>" + "</body></html>");
                         }
                                else {
                                dos.writeBytes("HTTP/1.0 200 Okie \r\n");
                                dos.writeBytes("<html><body>" + "The file does not exist" + "</body></html>");
                                }
			}
                        }
                        // All other requests
                        else {
                        dos.writeBytes("<html><body>" + "" + "</br></body></html>" );
                        }

                        //read next input
                        input = br.readLine();
                        }

			// Close connection (using HTTP 1.0 which is non-persistent).
			s.close();
                  }
	}

           private static void getNewWord() {

                 /* Enter the wordslist, separated by a | here: */
                          String str = "NETWORK|PROGRAMMING|MULTIMEDIA|GRAPHICS|ALGORITHM|LEISURE|EDUCATION|SPORTS|COMMUNICATION|SIMPLICITY";

                          String[] temp;

                          /* delimiter */
                          String delimiter = "\\|";

                          /* given string will be split by the argument delimiter provided. */
                          temp = str.split(delimiter);

                          /* Setting the seed */
                          Random randomGenerator = new Random();
                          rword = new String(temp[randomGenerator.nextInt(temp.length)]);
            }

    private static void newGame(){

  /* Setting the errors to 0 */
                errors=0;
                won = false;
                getNewWord();
                //rword = response;
                len = rword.length();
                char positions[] = new char[len];
                for (int i=0; i<len; i++) {
                        positions[i] = '-';
                }
                char fpositions[] = new char[26];
                for (int i=0; i<26; i++) {
                        fpositions[i] = ' ';
                }
                char cpositions[] = new char[26];
                for (int i=0; i<26; i++) {
                        cpositions[i] = ' ';
                }
                String s = new String(positions);
                gword = new StringBuffer(s);
                String f = new String(fpositions);
                fword = new StringBuffer(f);
                String c = new String (cpositions);
                cword = new StringBuffer(c);

                // Delete the messages

                message="";
                information = "";
        }

     private static void checkAnswer(String s){
                String t, w;
                char a;
                 message="";
                 information="";

                 //Check for errors in input

                a = s.charAt(0);

                if (!Character.isLetter(a)){
                          message="Only enter letters!";
                       information="";
                          return;
                }
                if (s.length()>1){
                          message="One letter at a time!";
                       information="";
                          return;
                }

                // Has the letter been guessed

                t = new String(gword);
                if (t.indexOf(s) != -1){
                        message="Letter has already been guessed";
                       information="";
                        return;
                }

                w = new String(fword);
                if (w.indexOf(s) != -1){
                        message="Letter has already been guessed";
                       information="";
                        return;
                }

                // If the letter doesn't occur in the rword

                if (rword.indexOf(s) == -1){
                        message="Wrong!";
                       information="";
                        errors++;
                         fword.setCharAt(j, a);
                         j++;
                        if (errors == MAX){
                                message="You lost!";
                                information ="Please start a new game";
                        }

                        return;
                }

                // Replace dashes in gword with the found letter.

                for (int i=0; i<rword.length(); i++){
                        if (rword.charAt(i) == a){
                                gword.setCharAt(i, a);
                        }
                }
                cword.setCharAt(k, a);
                k++;
                message="Correct!";
                information="";
                t = new String(gword);

                // If all the dots have been filled, you win

                if (t.indexOf('-') == -1){
                        message="You win!";
                        information="Please start another game :)";
                        won = true;
                        return;
                }
        }

}

