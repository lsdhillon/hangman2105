/*
 * TCP Client
 * To start a new game, run TCPServer.java and then run this file
 */

import java.io.*;
import java.net.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

public class TCPClient extends Applet implements ActionListener{

        private int errors;        // amount of errors
        private String message;   // error or victorie
        private String information; // information of the message
        private String word;      // the word to guess
        private StringBuffer guessed_word;// the guessed_word
        private StringBuffer wrong_guess;// the wrong_guess
        private StringBuffer correct_guess;// the wrong_guess
        private Button newGame;      // Button "Restart"
        private Button bGo;         // Button "Go"
        private TextField guessBox; // letter box
        private Font fnt;           // common font
        public int j = 0;
        public int k = 0;
        private static PrintWriter serverWriter;
        private static BufferedReader br ;
        private boolean testConnection = false;
        private int word_length = 0;

        public void init(){

                fnt = new Font( "Monospaced", 0, 12 );
                setFont(fnt);
                this.setBackground(Color.white);
                this.setSize(500, 500);

                // Create textbox for guessing a letter
                guessBox = new TextField();

                // Create buttons and labels
                newGame = new Button("New Game");
                bGo = new Button("Go!");

                // Add the graphical elements to the applet
                add(new Label("Guess a letter:"));
                add(guessBox);
                add(bGo);
                add(newGame);

                // Buttons are events:
                newGame.addActionListener(this);
                bGo.addActionListener(this);

                // Start first game
                initClient();
                if (testConnection == true)
                initGame();

        }

            public void initClient(){

                try {
            Socket s = new Socket("localhost", 7000);

	    // The next 2 lines create a output stream we can
		// write to.  (To write TO SERVER)
		OutputStream os= s.getOutputStream();
		serverWriter = new PrintWriter(os, true); //to write string,otherwise, need towrite byte array

		// The next 2 lines create a buffer reader that
		// reads from the standard input. (to read stream FROM SERVER
		br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                  testConnection = true;

                }

            catch (Exception e){
            }

        }

        public void getNewWord() {
                try {
                serverWriter.println("SEND A WORD!");
                String st = br.readLine();
                  if (st.compareTo("") != 0) {
                      String[] temp;
                        /* delimiter */
                        String delimiter = "\\ ";

                        /* given string will be split by the argument delimiter provided. */
                        temp = st.split(delimiter);
			word_length = new Integer(temp[1]);
                  }
                }
                catch (Exception e) {}
            }

        public void initGame(){

  /* Setting the errors to 0 */
                errors=0;
                getNewWord();

                char positions[] = new char[word_length];
                for (int i=0; i<word_length; i++) {
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
                guessed_word = new StringBuffer(s);
                String f = new String(fpositions);
                wrong_guess = new StringBuffer(f);
                String c = new String (cpositions);
                correct_guess = new StringBuffer(c);

                guessBox.setText("");

                // Delete the messages

                message="";
                information = "";
                repaint();
        }

        public void paint(Graphics g) {


                // Draw the hangman
                int baseY = 250;

                if (errors >  0){    // ground
                        g.drawLine(90, baseY,200,baseY);
                }
                if (errors >  1){    // bar up
                        g.drawLine(125,baseY,125,baseY-100);
                }
                if (errors >  2){    // side bar
                        g.drawLine(125,baseY-100,175,baseY-100);
                }
                if (errors >  3){    // rope
                        g.drawLine(175,baseY-100,175,baseY-75);
                }
                if (errors >  4){    // head
                        g.drawOval(170,baseY-75,10,12);
                }
                if (errors >  5){    // body
                        g.drawOval(170,baseY-65,15,25);
                }
                if (errors >  6){    // arms
                        g.drawLine(160,baseY-65,170,baseY-60);
                }
                if (errors > 7){
                        g.drawLine(183,baseY-60,193,baseY-65);
                }
                if (errors > 8){    // legs
                        g.drawLine(165,baseY-30,170,baseY-45);
                }
                if (errors > 9){
                        g.drawLine(183,baseY-45,193,baseY-30);
                }


    // Show the messages
    g.drawString( message, 40, baseY+25 );
    g.drawString( information, 25, baseY+45 );

    // Show the information about the word, correct/wrong guesses
    g.drawString( "Word: ", 40, 60);
    g.drawString( "Correct Guesses: ", 40, 90);
    g.drawString( "Incorrect Guesses: ", 40, 120);
    g.drawString( (new String (guessed_word)).toUpperCase(), 90, 60);
    g.drawString( (new String (correct_guess)).toUpperCase().trim(), 160, 90);
    g.drawString( (new String (wrong_guess)).toUpperCase().trim(), 170, 120);

        }

        public void actionPerformed(ActionEvent e){

                if (e.getSource() == newGame){

                        bGo.enable(true);
                        guessBox.enable(true);
                        initGame();
                }

                if (e.getSource() == bGo){
                        String s = guessBox.getText();
                        if (s.compareTo("") != 0)
                        processTurn(s);
                        else
                        message="You sent a blank!";
                        // Delete the letter input box
                        guessBox.setText("");
                        repaint();
                }

        }

        private void processTurn(String s){
                String t, w;
                char a;

                a = s.charAt(0);

                //To check for errors in input

                if (!Character.isLetter(a)){
                          message="Only enter letters!";
                          return;
                }
                if (s.length()>1){
                          message="One letter at a time!";
                          return;
                }

                // Has the letter been guessed

                t = new String(guessed_word);
                if (t.indexOf(s) != -1){
                        message="Letter has already been guessed";
                        return;
                }

                w = new String(wrong_guess);
                if (w.indexOf(s) != -1){
                        message="Letter has already been guessed";
                        return;
                }

                 String[] temp;
                try {
                serverWriter.println(("GUESS ".concat(s)).toUpperCase());
                System.out.println(("GUESS ".concat(s)).toUpperCase());
                String st = br.readLine();
                System.out.println(st);
                 String delimiter = "\\ ";
                        /* given string will be split by the argument delimiter provided. */
                        temp = st.split(delimiter);
                 if (temp[0].compareTo("INCORRECT") == 0) {
                        /* delimiter */

                        message="";
                        errors = new Integer(temp[1]);
                        wrong_guess.setCharAt(j, a);
                        j++;
                        message="Wrong!";
                        if (temp[2].compareTo("LOSE") == 0){
                                message="You lost!";
                                information ="Start a new game!";
                                bGo.enable(false);
                                guessBox.enable(false);
                        }
                        return;
                  }
                  if (temp[0].compareTo("CORRECT") == 0) {

                        int x = 1;
                        correct_guess.setCharAt(k, a);
                        k++;
                        while(temp[x].compareTo("WIN") != 0 && (new Integer(temp[x]) != null)){
                                guessed_word.setCharAt(new Integer(temp[x]), a);
                                x++;}

                                message="Correct!";
                        if (temp[x].compareTo("WIN") == 0) {
                            message="You win!";
                        information ="Start a new game!";
                        bGo.enable(false);
                        guessBox.enable(false);
                         return;
                        }

                        }
                }

                catch (Exception e) {}





    repaint();
        }
}
