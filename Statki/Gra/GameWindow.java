package Gra;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;


import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

@SuppressWarnings("serial")
public class GameWindow extends JFrame{
	
	public ClientSocket client;
	
	public boolean turn;
	
	private Dimension gameWindowSize;
	
	private SidePanelInGame mySideBoardInGame;
	
	private ShipsPanel myGamePanel;
	
	private ShipsPanel	opponentGamePanel;
	
	private JLabel	myBoard;
	
	private JLabel opponentBoard;
	
    protected GridBagLayout gBL;
    
    protected GridBagConstraints gBC;
    
    private int frameWidth;
    
    private int frameHeight;
    
    private GameBoard myGameBoard;
	
    private GameBoard opponentGameBoard;
    
	public GameWindow(ClientSocket Client, boolean Turn) {
		
		this.client=Client;
		this.turn=Turn;
		//super();
		frameWidth = 1400;
		frameHeight = 700;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameWindowSize = new Dimension(frameWidth, frameHeight);
		setPreferredSize(gameWindowSize);
		gBL = new GridBagLayout(); 
    	this.setLayout(gBL);				
		//setResizable(false);
		pack();
		setLocationRelativeTo(null);
		createUI();
		this.mySideBoardInGame = new SidePanelInGame(this);
		mySideBoardInGame.startTimeCounting(); //uruchomienie timera - licznika czasu
	}
	public void refreshMyGamePanel(int[][] newFields) 
	{
		myGameBoard.changeFields(newFields);		
		myGameBoard.repaint();
	}
	
	public void refreshOpponentGamePanel(int[][] newFields) 
	{
		opponentGameBoard.changeFields(newFields);		
		opponentGameBoard.repaint();
	}
	
	private void resetField() 
	{
		int [][] newFields = new int[11][11];
		for(int i=0; i<11; i++) {
			for(int j=0; j<11; j++) {
				newFields[i][j] = 0;
			}
		}
	}
	
	private void createUI()
	{
		
    	gBC = new GridBagConstraints();
		
    	String myText = new String("Twoja plansza");
    	myGameBoard = new GameBoard(350,350);
		myGamePanel = new ShipsPanel(frameWidth-400,frameHeight, myText, myGameBoard);
		myGamePanel.setBackground(new Color(8,127,198));		
		myGamePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 50));
		
    	gBC.fill = GridBagConstraints.BOTH;
    	gBC.weightx = 400;
    	gBC.weighty = 1.1;
    	gBC.gridx = 0;
    	gBC.gridy = 0;
    	gBL.setConstraints(myGamePanel, gBC);
    	this.add(myGamePanel, gBC);
    	
    	String opponentText = new String("Plansza przeciwnika");
    	opponentGameBoard = new GameBoard(350,350);
    	opponentGamePanel = new ShipsPanel(frameWidth-400,frameHeight,opponentText, opponentGameBoard);
		opponentGamePanel.setBackground(new Color(8,127,198));		
		opponentGamePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 50));
		
		gBC.fill = GridBagConstraints.BOTH;
    	gBC.weightx = 400;
    	gBC.weighty = 1.1;
    	gBC.gridx = 1;
    	gBC.gridy = 0;
    	gBL.setConstraints(opponentGamePanel, gBC);
    	this.add(opponentGamePanel, gBC);
    	
    	mySideBoardInGame = new SidePanelInGame(this);
    	mySideBoardInGame.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), 
		BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));   
    	mySideBoardInGame.setBackground(new Color(200,200,200));
		mySideBoardInGame.setMyNick("Mec");
		mySideBoardInGame.setMyRank("SILV1");
		mySideBoardInGame.setOpponentNick("Raval");
		mySideBoardInGame.setOpponentRank("GOLD3");
		mySideBoardInGame.setPlayersLabel();	
		mySideBoardInGame.setMyRound(false); //ustawienie czy teraz jest moja kolej, mo¿e byæ false
		
		gBC.fill = GridBagConstraints.BOTH;
		gBC.weightx = 420;
       	gBC.weighty = 1.1;
       	gBC.gridx = 2;
       	gBC.gridy = 0;
       	gBL.setConstraints(mySideBoardInGame, gBC);
       	this.add(mySideBoardInGame, gBC);
        
        this.setVisible(true);
		
		////////////////////////////
		
		//mySideBoardPreGame = new SidePanelPreGame();
		int[] ships = {4,3,3,2,2,1};
		//SideBoardPreGame.setMyShips(ships);
		
		//getContentPane().add(mySideBoardPreGame,BorderLayout.CENTER);      //do testów wystarczy podmieniæ te dwie linie aby zobaczyæ ró¿ne panele
		//getContentPane().add(mySideBoardInGame,BorderLayout.CENTER);
		
	}
}
