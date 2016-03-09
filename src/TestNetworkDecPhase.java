/*
 * This class is used to test the effectiveness of a player making moves as the elder hand in one round.
 * The best possible combination of 1-5 cards to discard are selected by allowing the network to estimate the 
 * likelihood of the desired score for each of the 1585 combinations.
 * The best combination is played based on the best estimated score of the possible combinations.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.math.*;

import org.neuroph.nnet.MultiLayerPerceptron;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

public class TestNetworkDecPhase {
	public TestNetworkDecPhase(MultiLayerPerceptron network){

		int player1Score = 0;
		int player2Score = 0;
		int round = 0;
		boolean player1Younger = false;
		double[] player1Inputs = new double[103];
		
		for(int game = 1; game <= 1; game ++){
			
			for(int i = 0; i<103; i++){
				player1Inputs[i] = 0;
			}
			
			player1Score = 0;
			player2Score = 0;
			round = 0;
			while(round < 1){
				
				// Initialise
				Deck theDeck = new Deck(true);
				boolean newPosition = false;
			
				//Initialise the player objects
				Player youngerHand = new Player("youngerHand");
				Player elderHand = new Player("elderHand");
				Player talon = new Player("Talon");
				Player yRemovedCards = new Player("yRemovedCards");
				Player eRemovedCards = new Player("eRemovedCards");
				
				for(int i = 0; i < 12; i++ ){
					youngerHand.addCard(theDeck.dealNextCard());
					elderHand.addCard(theDeck.dealNextCard());
				}
				
				for(int i = 0; i < 8; i++){
					talon.addCard(theDeck.dealNextCard());
				}
				
				//Store keys of cards in each player's hands
				ArrayList<Integer> youngerKeys = new ArrayList<Integer>();
				ArrayList<Integer> elderKeys = new ArrayList<Integer>();
				for(int i = 0; i < 12; i++){
					youngerKeys.add(youngerHand.hand.get(i).getKey());
					elderKeys.add(elderHand.hand.get(i).getKey());
				}
				Collections.sort(youngerKeys);
				Collections.sort(elderKeys);
				
				//Add keys associated to Player1's current role to player1Inputs
				ArrayList<Integer> keysToAddThisRound = new ArrayList<Integer>();
				if(player1Younger){
					keysToAddThisRound = youngerKeys;
				}
				else{
					keysToAddThisRound = elderKeys;
				}
				//Mark held cards as 1, others as 0
				for(int i = 0; i < 12; i++){
					int j = 0;
					j = keysToAddThisRound.get(i);		//j will be between 0 and 31 inclusive
					player1Inputs[j] = 1.0;
				}
				for(int i = 0; i < 32; i++){
					if(player1Inputs[i] == 0){
						player1Inputs[i] = 0;
					}
				}
	
				
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//elder's turn
			
				Random rand = new Random();
				int exchanges = 0; 
			
				
				//******************************************************************************************************************************************************
				//For each number of exchanges 1-5 choose every combination that number from the 12 cards 
				int count = 0;
				double maxDesiredOutput = 0;
				int[] bestKeys = new int[5];
				for(int i = 0; i <5; i++){
					bestKeys[i] = -1;
				}
				ArrayList<Integer> removedKeys = new ArrayList<Integer>();
				removedKeys.clear();
				double[] testInputs = new double[103];
				
				Card[] cards = new Card[12];
				for(int c = 0; c < 12; c++){
					cards[c] = elderHand.hand.get(c);
				}
				for(int a = 1; a <= 5; a++){
					ICombinatoricsVector<Card> initialVector = Factory.createVector(cards);
					Generator<Card> gen = Factory.createSimpleCombinationGenerator(initialVector, a);
					List<List<Card>> cardList = new ArrayList<List<Card>>();	//list of lists of cards cardlist
					for (ICombinatoricsVector<Card> combination: gen){
						   //System.out.println(combination);
						   cardList.add(combination.getVector());
					}
					//ArrayList<Card> temp;
					
					for(List<Card> l : cardList){	//for each list of cards in cardlist
						removedKeys.clear();
						//temp = elderHand.hand;
						   for(Card card : l){	//for each card in list l
							   
							   removedKeys.add(card.getKey());
							   //temp.remove(card);
						   }
						   elderKeys.clear();
						   boolean flag = false;
						   for(int i = 0; i < 12; i++){
							   for(int z = 0; z < removedKeys.size(); z ++){
								   if(cards[i].getKey() == removedKeys.get(z)){
									   flag = false;
								   }
							   }
							   if(flag){
								elderKeys.add(cards[i].getKey());
							   }
							   flag = true;
							}
						   for(int i = 0; i < 102; i++){
							   testInputs[i] = 0;
						   }
						   for(int i = 0; i < elderKeys.size(); i++){
								int j = 0;
								j = elderKeys.get(i);
								
								testInputs[j+32] = 1.0;
							}
							for(int i = 32; i < 64; i++){
								if(testInputs[i] == 0){
									testInputs[i] = 0;
								}
							}
						   Collections.sort(removedKeys);
							//removed cards
							for(int i = 0; i < removedKeys.size(); i++){
								int j = 0;
								j = removedKeys.get(i);
								
								testInputs[j+64] = 1.0;
							}
							for(int i = 64; i < 96; i++){
								if(testInputs[i] == 0){
									testInputs[i] = 0;
								}
							}
							for(int i = 96; i < 103; i++){
								testInputs[i] = 0;
							}
							testInputs[96+round] = 1;
							
							network.setInput(testInputs);
							network.calculate();
							count++;
							System.out.println("\n\nCount = "+count);
							Vector<Double> output = network.getOutput();
							System.out.println("Output: \n" +output.get(0)+ "\n" +output.get(1)+ "\n" + output.get(2) + "\n" + output.get(3)+ "\n" + output.get(4) + "\nNOut = " + network.getOutput()+"\n");
					 
							if(output.get(0) > maxDesiredOutput){
								maxDesiredOutput = output.get(0);

								for(int n = 0; n < removedKeys.size(); n++){
									bestKeys[n] = removedKeys.get(n);
								}
							}
					}
				}
				System.out.println("\nBestKeys (cards to discard for max prob of desired output): " +bestKeys[0] +"  " + bestKeys[1] +"  "+ bestKeys[2] + "  "+ bestKeys[3] + "  " +bestKeys[4] );
				System.out.println("Max estimated probability possible for desired output using held cards: " +maxDesiredOutput);
				System.out.println("Now playing best estimated cards....\n\n");
				
				elderHand.printHand();
				/////////////////////////////////////////////////************************************************************************************************************
				for(int i = 0; i < 5; i++){
					if(bestKeys[i] != -1){
						exchanges++;
					}
				}
				System.out.println("Elder Exchanges: " + exchanges);
				
				//*********************************************************************************************************************************************************
				int randTalon = rand.nextInt(talon.hand.size()); //random card from talon that will replace one of the elder's cards
	
				ArrayList<Integer> ePrevPositions = new ArrayList<Integer>();
				
				int randCard = -1; //position of card in the elder's hand that will soon be replaced
				
				int z = 0;
				int[] keyPositions = new int[5];
				for(int i = 0; i < 5; i++){
					keyPositions[i] = 9;
				}
				for(int i = 0; i < elderHand.hand.size(); i++){
					for(int j = 0; j < 5; j++){
						if(elderHand.hand.get(i).getKey() == bestKeys[j]){
							//System.out.println("elderHand.hand.get(i): "+elderHand.hand.get(i) +" \t== bestKeys[j] = " + bestKeys[j] + "\t   j =" + j);
							keyPositions[z] = i;
							z++;
						}
					}
				}

				for(int i = 0; i < 5; i++){
					//System.out.println("i = " +i+ "\tkeyPositions[i] = " +keyPositions[i]);
					if(keyPositions[i] != 9){
						randTalon = rand.nextInt(talon.hand.size());
						elderHand.hand.set(keyPositions[i], talon.hand.get(randTalon));
						System.out.print("\nTalon card (Pos:" +randTalon+ "):" +talon.hand.get(randTalon));
						System.out.print(" will replace card at position " +keyPositions[i]+ " of the elder's hand\n");
					}
				}
				
				
				elderHand.printHand();
				System.out.print("\nElderHand's removed cards after all exchanges: \n");
				for(int c = 0; c < eRemovedCards.hand.size(); c++)
				{
					System.out.printf(c + ".  " + " %s\n",eRemovedCards.hand.get(c).toString());
				}
				System.out.println();
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//dealer's turn
				
				//talon3.hand.addAll(talon5.hand); //append remaining cards from elder's talon to younger's talon
				//talon.printHand(true);
				
				youngerHand.printHand();
				System.out.println();
				
				rand = new Random();
				//no of exchanges taken = random (max exchanges allowed)
				exchanges = rand.nextInt(talon.hand.size()) +1 ; //must be at least 1:  ((0 >= exchanges < talonSize ) + 1)  therefore ( 1 <= exchanges <= talonSize )
				System.out.println("Dealer's(YoungerHand's) no. of exchanges : " +exchanges);
				
				randCard = rand.nextInt(12); //hand
				randTalon = rand.nextInt(talon.hand.size());//random card from talon
				ArrayList<Integer> yPrevPositions = new ArrayList<Integer>();
				yPrevPositions.add(randCard);
				
				while(exchanges > 0){
					
					newPosition = false;
					System.out.print("\nTalon card (Pos:" +randTalon+ "):" +talon.hand.get(randTalon));
					System.out.print(" will replace card at position " +randCard+ " of the younger's hand (" +youngerHand.hand.get(randCard) +" )");
					yRemovedCards.hand.add(youngerHand.hand.get(randCard));
					youngerHand.hand.set(randCard, talon.hand.get(randTalon));
					talon.hand.remove(randTalon);
					
					if(talon.hand.size() > 0){
						
						randTalon = rand.nextInt(talon.hand.size());
					}
					randCard = rand.nextInt(12);
					
					for(int i = 0; i < yPrevPositions.size(); i++){ //Prevent exchanging card obtained from Talon.
						if(randCard == yPrevPositions.get(i)){
							System.out.println("Attempted new pos 'randCard'("+randCard+") clashes with prevPosition[" +i+ "]: "+yPrevPositions.get(i));
							randCard = rand.nextInt(12);
							i = 0;
							System.out.println("Got new attempted new pos 'randCard' : "+randCard);
						}
					}
					exchanges--;
				}
				System.out.println();	
				
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				System.out.printf("YoungerHand's removed cards after all exchanges: \n");
				for(int c = 0; c < yRemovedCards.hand.size(); c++)
				{
					System.out.printf(c + ".  " + " %s\n",yRemovedCards.hand.get(c).toString());
				}
				System.out.println();
				
				youngerKeys.clear();
				elderKeys.clear();
				for(int i = 0; i < 12; i++){
					youngerKeys.add(youngerHand.hand.get(i).getKey());
					elderKeys.add(elderHand.hand.get(i).getKey());
				}
				Collections.sort(youngerKeys);
				
				for(int i = 0; i < 12; i++){
					int j = 0;
					j = keysToAddThisRound.get(i);
					
					player1Inputs[j+32] = 1.0;
				}
				for(int i = 32; i < 64; i++){
					if(player1Inputs[i] == 0){
						player1Inputs[i] = 0;
					}
				}
				
				//ArrayList<Integer> removedKeys = new ArrayList<Integer>();
				removedKeys.clear();
				if(player1Younger){
					for(int i = 0; i < yRemovedCards.hand.size(); i++){
						removedKeys.add(yRemovedCards.hand.get(i).getKey());
					}
				}	
				else{
					for(int i = 0; i < eRemovedCards.hand.size(); i++){
						removedKeys.add(eRemovedCards.hand.get(i).getKey());
					}
				}
				Collections.sort(removedKeys);
				//removed cards
				for(int i = 0; i < removedKeys.size(); i++){
					int j = 0;
					j = removedKeys.get(i);
					
					player1Inputs[j+64] = 1.0;
				}
				for(int i = 64; i < 96; i++){
					if(player1Inputs[i] == 0){
						player1Inputs[i] = 0;
					}
				}
			
				for(int i = 96; i < 102; i++){
					player1Inputs[i] = 0;
				}
				player1Inputs[96+round] = 1;
				
				
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//print final hands TODO REMOVE
//				System.out.println("FINAL HANDS");
//				elderHand.printHand();
//				youngerHand.printHand();
					
				int elderPoint = elderHand.getPoint();
				int youngerPoint = youngerHand.getPoint();
				int[] elderSequence = elderHand.getSequence();
				int[] youngerSequence = youngerHand.getSequence();
				int elderSet = elderHand.getSet();
				int youngerSet = youngerHand.getSet();
				int youngerSum = 0;
				int elderSum = 0;
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				if(elderPoint > youngerPoint){
					elderSum = elderSum+elderPoint;
					System.out.println("Elder wins " +elderPoint + " points from Point phase.");
				}
				else if(youngerPoint > elderPoint){
					youngerSum = youngerSum+youngerPoint;
					System.out.println("Younger wins " +youngerPoint + " points from Point phase.");
				}
				else if(youngerPoint == elderPoint){
					if(elderHand.getPointTieBreak() > youngerHand.getPointTieBreak()){
						elderSum = elderSum+elderPoint;
						System.out.println("Elder wins " +elderPoint + " points from Point phase after a tie break.");
					}
					if(youngerHand.getPointTieBreak() > elderHand.getPointTieBreak()){
						youngerSum = youngerSum+youngerPoint;
						System.out.println("Younger wins " +youngerPoint + " points from Point phase after a tie break.");
					}
					else if(elderHand.getPointTieBreak() == youngerHand.getPointTieBreak()){
						System.out.println("Tie break unsuccessful both player have same value point. elder (" +elderHand.getPointTieBreak()+ ") : (" +youngerHand.getPointTieBreak() + ")");
					}
				}
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				int elderSeqPoints = 0;
				int youngerSeqPoints = 0;
				System.out.println("\nElder's max Sequence = " +elderSequence[0]+ "\t Younger's max Sequence = " +youngerSequence[0]);
				if(elderSequence[0] > youngerSequence[0]){
					for(int i = 0; i <= 4; i++){
						if(elderSequence[i] >= 3){
							elderSeqPoints += elderSequence[i];
							
							if(elderSequence[i] >= 5){
								elderSeqPoints += 10;
								System.out.println("Elder wins 10 bonus points for a run of 5 or more!");
							}
						}
					}
					System.out.println("Elder wins a total of " +elderSeqPoints+ " points from Sequence phase.\n");
					elderSum += elderSeqPoints;
				}
				if(youngerSequence[0] > elderSequence[0]){
					for(int i = 0; i <= 4; i++){
						if(youngerSequence[i] >= 3){
							youngerSeqPoints += youngerSequence[i];
							
							if(youngerSequence[i] >= 5){
								youngerSeqPoints += 10;
								System.out.println("Younger wins 10 bonus points for a run of 5 or more clubs!");
							}
						}
					}
					System.out.println("Younger wins a total of " +youngerSeqPoints+ " points from Sequence phase\n");
					youngerSum += youngerSeqPoints;	
				}
				if(youngerSequence[0] < 3 && elderSequence[0] < 3){
					System.out.println("No player had a sequence long enough to score. No points awarded for sequence.");
				}
				
				System.out.println("Elder Sequences (Max/Cl/Di/Sp/He): ");
				for(int i = 0; i <= 4; i++){
					System.out.print(" " +elderSequence[i]);
				}
				System.out.println("\nYounger Sequences (Max/Cl/Di/Sp/He): ");
				for(int i = 0; i <= 4; i++){
					System.out.print(" " +youngerSequence[i]);
				}
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				if(elderSet > youngerSet){
					if(elderSet == 3){
						elderSum += 3;
						System.out.println("Elder wins " +elderSet + " points from Set phase");
					}
					if(elderSet == 4){
						elderSum += 14;
						System.out.println("Elder wins " +(elderSet+10) + " points from Set phase with a Quatorzes!");
					}
				}
				else if(youngerSet > elderSet){
					
					if(youngerSet == 3){
						youngerSum += 3;
						System.out.println("Younger wins " +youngerSet + " points from Set phase");
					}
					if(youngerSet == 4){
						youngerSum += 14;
						System.out.println("Younger wins " +youngerSet + " points from Set phase with a Quatorzes!");
					}
					
				}
				else{
					System.out.println("No points won in Set Phase");
				}
				if(player1Younger){
					player1Score += youngerSum;
					player2Score += elderSum;
					player1Younger = false;
				}
				else{
					player1Score += elderSum;
					player2Score += youngerSum;
					player1Younger = true;
				}
				System.out.print("Player 1 Test Round score: " +player1Score+ "\tPlayer 2 Test Round score: "+player2Score);
			round = 2;
			}
		}
	}
}

