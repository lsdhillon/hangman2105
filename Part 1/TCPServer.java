/*
 * Single-threaded TCP server
 * To start a new game, run this file and then run the TCPClient.java
 */

import java.util.*;
import java.io.*;
import java.net.*;

class TCPServer {

        private static PrintWriter output;
        private static String right_word;      // the randomly chosen word to guess
        private static final int k_max =10;   // maximum allowed errors till loss
        private static int k = 0;        // count of errors
        private static StringBuffer guessed_word;// the guessed word


    public static void main (String args[]) throws Exception {
		// throws Exception here because don't want to deal
		// with errors in the rest of the code for simplicity.
		// (Note: NOT a good practice).

         //Welcome socket  ---- SOCKET 1
		 ServerSocket serverSocket = new ServerSocket(7000);
		 // waits for a new connection. Accepts connetion from multiple clients
		 while (true)
		 {
			 System.out.println("Waiting for connection at 7000");
             //Connection socket  --- SOCKET 2
			 Socket s = serverSocket.accept();
			 System.out.println("Connection established from " + s.getInetAddress());

			 // create a BufferedReader object to read strings from
			 // the socket. (read strings FROM CLIENT)
			 BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			 String input = br.readLine();
                         output = new PrintWriter(s.getOutputStream(), true);
                         /* Enter the wordslist, separated by a | here: */
                          String str = "NETWORK|PROGRAMMING|MULTIMEDIA|GRAPHICS|ALGORITHM|LEISURE|EDUCATION|SPORTS|COMMUNICATION|SIMPLICITY";
                          String[] temp;

                          /* delimiter */
                          String delimiter = "\\|";

                          /* given string will be split by the argument delimiter provided. */
                          temp = str.split(delimiter);

                          /* Setting the seed */
                          Random randomGenerator = new Random();

                 	 // keep repeating until an empty line is read.
			 while (input.compareTo("") != 0) {
                              String subs = input.substring(0, 5);
				 // convert input to upper case and echo back to
				 // client.
                                 if (input.compareTo("SEND A WORD!") == 0)
                                 {
                                 k = 0;
                                 right_word = new String(temp[randomGenerator.nextInt(temp.length)]);
                                  char positions[] = new char[right_word.length()];
                                  for (int i=0; i<right_word.length(); i++) {
                                      positions[i] = '-';
                                  }
                                    String st = new String(positions);
                                    guessed_word = new StringBuffer(st);

                                    //send the length of word to client
                                 String len = Integer.toString(right_word.length());
				 output.println("WORD ".concat(len));
                                 System.out.println(right_word);
                                 }
                                 else if(subs.compareTo("GUESS") == 0)
                                 {
                                  /* delimiter */
                                    String delimiter_new = "\\ ";
                                  /* given string will be split by the argument delimiter provided. */
                                    String[] temp_new;
                                    temp_new = input.split(delimiter_new);
                                 String message = checkAnswer(right_word, temp_new[1]);
				 output.println(message);
                                 }

			 input = br.readLine();
			}
			// close current connection
			s.close();
		 }
    }


    public static String checkAnswer(String right_word, String guess) {
                char a = guess.charAt(0);
                String message,t;

                //Letter is not in the word
                if (right_word.indexOf(a) == -1){
                        k++;
                        message = "INCORRECT ".concat(Integer.toString(k));
                        if (k == k_max){
                            message = message.concat(" ").concat("LOSE");
                        }
                }
                else {
                message = "CORRECT";
                // Replace dots in guessed_word with the found letter.
                for (int i=0; i<right_word.length(); i++){
                        if (right_word.charAt(i) == a){
                                guessed_word.setCharAt(i, a);
                                message = message.concat(" ").concat(Integer.toString(i));
                        }
                }

                t = new String(guessed_word);

                // If all the dashes have been filled, you win

                if (t.indexOf('-') == -1){
                      message = message.concat(" WIN");
                }
                }
                 return message;
    }
}
