package centralServer;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Gra.Message;

class ConnectionHandler extends Thread{
	private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public ConnectionHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try {
        	System.out.println("New Client tries to connect");
			initializeReader();
			String input;
			while ((input = in.readLine()) != null) {
				ReceivedMessage receivedMessage = new ReceivedMessage(input);
				if (receivedMessage.isClientIdValid()) {
					System.out.println("Processing Message...");
					proceedBasingOnReceivedMessage(receivedMessage);
				}
				System.out.println("Served client: "+receivedMessage.getClientID());
			}

			
		} catch (IOException e) {
			e.printStackTrace();
		} 
    }
    
    public void initializeReader() throws IOException {
		in = new BufferedReader(
		  new InputStreamReader(clientSocket.getInputStream()));
    }
    
    public synchronized void proceedBasingOnReceivedMessage(ReceivedMessage receivedMessage) {
    	Client checkClient = CentralServer.findClientByID(receivedMessage.getClientID());
    	Client client = checkClient != null ? checkClient : new Client(receivedMessage);
    	PrintWriter foundStream = CentralServer.findOutStreamByClient(receivedMessage.getClientID());
    	if (foundStream != null) {
    		out = foundStream;
    	} else {
    		try {
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				CentralServer.outStreams.put( (Integer)receivedMessage.getClientID(), out);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	if(receivedMessage.getContentType().equals("ng")) System.out.println("dupa");
    	switch (receivedMessage.getContentType()) {
        case "ng":
    		Game game = new Game();
    		game.setFirstClient(client);
    		client.setTurn(true);
    		CentralServer.gamesList.add(game);
    		sendMessage(out, "1");
    		System.out.println("Started game: "+game.getGameID()+" with client: "+ receivedMessage.getClientID());
            break;
        case "sp":	
        	client.setBoard(new Board( receivedMessage.getShipsCoordinates() ));
        	sendMessage(out, "1");
        	System.out.println("Ships placed in game: "+CentralServer.findGameByClient(client).getGameID()+" at client: "+ receivedMessage.getClientID());
            break;
        case "jg":
        	if(receivedMessage.isJoiningGameIdValid()) {
        		Game gameToJoin = CentralServer.findGameByID(receivedMessage.getJoiningGameID());
        		if (gameToJoin != null && gameToJoin.getSecondClient() == null) {
        			gameToJoin.setSecondClient(client);
        			sendMessage(out, "1");
        			System.out.println("Client: "+client.getID()+" joining game: "+gameToJoin.getGameID());
        		} else {System.out.println("Something went wrong");
        		}
        	}
            break;
        case "sh":
        	Game foundGame = CentralServer.findGameByClient(client);
        	if(client.getTurn() == true && receivedMessage.isTargetValid() && foundGame != null && foundGame.isFinished() == false && client.hasMoved() == false) {
        		Client opponent = foundGame.getOpponent(client);
        		opponent.shot(receivedMessage.getTarget());
    			client.setMoved(true);
        		client.setTurn(false);
        		opponent.setTurn(true);
        		PrintWriter opponentOut = CentralServer.outStreams.get((Integer)opponent.getID());
        		sendMessage(out, opponent.getBoard().toString());
        		sendMessage(opponentOut, client.getBoard().toString());
	        	if(!opponent.getBoard().hasAliveShip()) {
	        		foundGame.EndGame(client);
	        		closeConnection();
	        	}
        	}
            break;
        default:
        	System.out.println("Received it from: " +receivedMessage.getClientID());
            break;
    	}
    
	}

	public void sendMessage(PrintWriter pw, String message) {
		pw.flush();
		pw.println(message);
	}
    
	public void closeConnection() {
		try {
			clientSocket.close();
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		System.out.println("Closed connection");
	}
    
}