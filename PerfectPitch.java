/*
 * Noah Alonso-Torres
 * Perfect Pitch Quiz
 * Quiz on pitches ranging from C3->C4
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class PerfectPitch implements ActionListener{
	
	private JButton listenButton = new JButton("Listen");
	private JButton nearbyButton = new JButton("Nearby");
	private JButton checkButton = new JButton("Check");
	private JButton nextButton = new JButton("Next");
	private JButton submitButton = new JButton("Submit");
	
	private JLabel noteField = new JLabel();
	private JTextField answerField = new JTextField(2);
	private Font bigFont = new Font(Font.MONOSPACED, Font.BOLD, 20);
	
	private String[] soundFile = {"c3/C.wav", "c3/D.wav", "c3/E.wav", "c3/F.wav",
			 "c3/G.wav",  "c3/A.wav", "c3/B.wav", "c4/C.wav",};
	private String[] noteName = {"C3", "D3", "E3", "F3", "G3", "A3", "B3", "C4",};
	
	//Initialize random sound **Allows change() { audio.stop() }
	private int random = (int)(Math.random()*8);
	private Audio audio = new Audio(soundFile[random]);

	public PerfectPitch() {
		JFrame frame = new JFrame("Perfect Pitch");
		frame.getContentPane().setLayout(new BorderLayout());

		JPanel buttonPanelWest = new JPanel(new GridLayout(0,1));
		buttonPanelWest.add(listenButton);
		buttonPanelWest.add(nearbyButton);
		
		JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
		fieldPanel.add(noteField);
		fieldPanel.add(answerField);
		
		JPanel eastButtonPanel = new JPanel(new GridLayout(0,1));
		eastButtonPanel.add(checkButton);
		eastButtonPanel.add(nextButton);
		//noteField.setBorder(new LineBorder(Color.RED, 5));
		
		frame.add(buttonPanelWest, BorderLayout.WEST);
		frame.add(eastButtonPanel, BorderLayout.EAST);
		frame.add(submitButton, BorderLayout.SOUTH);
		frame.add(fieldPanel, BorderLayout.CENTER);
		
		listenButton.addActionListener(this);
		nearbyButton.addActionListener(this);
		submitButton.addActionListener(this);
		checkButton.addActionListener(this);
		nextButton.addActionListener(this);
		fieldPanel.setPreferredSize(new Dimension(200, 100));
		noteField.setHorizontalAlignment(SwingConstants.CENTER);
		answerField.setHorizontalAlignment(SwingConstants.CENTER);
		noteField.setText("?");
		noteField.setFont(bigFont);
		answerField.setFont(bigFont);

		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		change();
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == listenButton) listen();
		if (event.getSource() == nearbyButton) nearby();
		if (event.getSource() == checkButton) check();
		if (event.getSource() == nextButton) change();
		if (event.getSource() == submitButton) submit();
	}
	
	public void change() {
		audio.stop();
		random = (int)(Math.random()*8); //One less than array size
		if (random < 0) random = 0;
		audio = new Audio(soundFile[random]);
		noteField.setText("?");
		answerField.setText("");
	}
	
	public void listen() {
		audio.play();
	}
	
	public void nearby() {
		//audio.play();
		//pause(2);
		//Change, then play
		if (random == 0) {
			//Can't play before note
			audio.play();
			pause(1);
			audio = new Audio(soundFile[random+1]);
			audio.play();
			audio = new Audio(soundFile[random]);
		}
		if (random == 8) {
			//Can't play after note
			audio.play();
			pause(1);
			audio = new Audio(soundFile[random-1]);
			audio.play();
			audio = new Audio(soundFile[random]);
		}
		else {
			audio = new Audio(soundFile[random-1]);
			audio.play();
			pause(1);
			audio = new Audio(soundFile[random]);
			audio.play();
			pause(1);
			audio = new Audio(soundFile[random+1]);
			audio.play();
			audio = new Audio(soundFile[random]);
		}
	}
	
	public void check() {
		noteField.setText(noteName[random]);
	}
	
	public void submit() {
		String input = answerField.getText();
		input = input.toUpperCase();

		if (input.equals(noteName[random])) {
			noteField.setText("Correct: " + noteName[random]);
			noteField.setBorder(new LineBorder(Color.GREEN, 5));
		}
		else {
			noteField.setText("Incorrect: " + noteName[random]);
			noteField.setBorder(new LineBorder(Color.RED, 5));
		}
	}
	
	public void pause(int wait) {
		int sampleLength = audio.getAudioBytes().length;
		int lengthSeconds = (int)(sampleLength/44000);
		
		try {
			//THE TIME IS FROM THE MOMENT IT STARTS PLAYING
			//SET A VARIABLE TO THE LENGTH OF THE SONG (ARRAY/44,000)sec
			//THEN ADD SEC TO THAT
			Thread.sleep((lengthSeconds + wait*1000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		PerfectPitch test = new PerfectPitch();
	}
}
