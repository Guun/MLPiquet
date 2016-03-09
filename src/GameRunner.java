
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Collections;
import java.util.Vector;





//import java.util.Vector;
//
//import org.encog.neural.data.NeuralData;
//import org.encog.neural.data.NeuralDataPair;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.DynamicBackPropagation;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.TransferFunctionType;

public class GameRunner {
	public static void main(String[] args){
		
		//variables
		int player1Score = 0;
		int player2Score = 0;
		int round = 0;
		boolean player1Younger = true;
		
		//103 = 32(initial card set dealt) + 32(cards after exchange) + 32(cards discarded) + 6(round of the game) + 1(player role (elderHand/youngerHand)
		double[] player1Inputs = new double[103];
		TrainingSet trainingSet = new TrainingSet(103, 5);
		
		for(int game = 1; game <= 100; game ++){	//number of games to play
			
			player1Score = 0;
			player2Score = 0;
			round = 0;
			while(round < 6){	//six rounds in a game of Piquet
				
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
				
//				for(int counter: youngerKeys){
//					System.out.println(counter);
//				}
				
				//Add keys associated to Player1's current role to player1Inputs
				ArrayList<Integer> keysToAddThisRound = new ArrayList<Integer>();
				if(player1Younger){
					keysToAddThisRound = youngerKeys;
					player1Inputs[102] = 1;
				}
				else{
					keysToAddThisRound = elderKeys;
					player1Inputs[102] = 0;
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
				
				
				//print the initial hands
				System.out.println("Cards are dealt\n");
				elderHand.printHand();
				System.out.println("\n");
				youngerHand.printHand();
				System.out.println("\n");
				
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//elder's turn
				
				talon.printHand();
				System.out.println();
				
				Random rand = new Random();
				int exchanges = rand.nextInt(5)+1; //must be at least 1:  ((0 >= exchanges < 5 ) + 1)  therefore ( 1 <= exchanges <= 5 )
	
				System.out.println("Elder Exchanges: " + exchanges);
				
				int randCard = rand.nextInt(12); //position of card in the elder's hand that will soon be replaced
				int randTalon = rand.nextInt(talon.hand.size()); //random card from talon that will replace one of the elder's cards

				ArrayList<Integer> ePrevPositions = new ArrayList<Integer>();
				ePrevPositions.add(randCard);
				while(exchanges > 0){
				
					System.out.print("\nTalon card (Pos:" +randTalon+ "):" +talon.hand.get(randTalon));
					System.out.print(" will replace card at position " +randCard+ " of the elder's hand (" +elderHand.hand.get(randCard) +" ) \n");
					System.out.println("Elder Exchanges left: " + (exchanges-1));
					//System.out.println("Elder hand card being removed: "+ elderHand.hand.get(randCard) +"\n");
					
					eRemovedCards.hand.add(elderHand.hand.get(randCard));
				
					elderHand.hand.set(randCard, talon.hand.get(randTalon));
					talon.hand.remove(randTalon);
					if(talon.hand.size() > 0){
						
						randTalon = rand.nextInt(talon.hand.size());
					}
					
					for(int i = 0; i < ePrevPositions.size(); i++){ //Prevent exchanging card obtained from Talon.
						if(randCard == ePrevPositions.get(i)){
							System.out.println("Attempted new pos 'randCard'("+randCard+") clashes with prevPosition[" +i+ "]: "+ePrevPositions.get(i));
							randCard = rand.nextInt(12);
							i = 0;
							System.out.println("Got new attempted new pos 'randCard' : "+randCard);
						}
					}
					exchanges--;
				}
				
				//eRemovedCards.printHand();
				elderHand.printHand();
				System.out.print("\nElderHand's removed cards after all exchanges: \n");
				for(int c = 0; c < eRemovedCards.hand.size(); c++)
				{
					System.out.printf(c + ".  " + " %s\n",eRemovedCards.hand.get(c).toString());
				}
				System.out.println();
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
					System.out.print(" will replace card at position " +randCard+ " of the younger's hand (" +youngerHand.hand.get(randCard) +" ) \n");
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
				
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
				
				ArrayList<Integer> removedKeys = new ArrayList<Integer>();
				
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

				
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//print final hands TODO REMOVE
				System.out.println("FINAL HANDS");
				elderHand.printHand();
				youngerHand.printHand();
					
				int elderPoint = elderHand.getPoint();
				int youngerPoint = youngerHand.getPoint();
				int[] elderSequence = elderHand.getSequence();
				int[] youngerSequence = youngerHand.getSequence();
				int elderSet = elderHand.getSet();
				int youngerSet = youngerHand.getSet();
				int youngerSum = 0;
				int elderSum = 0;
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
				
				System.out.println("Elder Sequences (Max/Cl/Di/Sp/He): ");
				for(int i = 0; i <= 4; i++){
					System.out.print(" " +elderSequence[i]);
				}
				System.out.println("\nYounger Sequences (Max/Cl/Di/Sp/He): ");
				for(int i = 0; i <= 4; i++){
					System.out.print(" " +youngerSequence[i]);
				}
				if(youngerSequence[0] < 3 && elderSequence[0] < 3){
					System.out.println("No player had a sequence long enough to score. No points awarded for sequence.");
				}
				System.out.println("\n");
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//TRICK PLAYING PHASE
				
				ArrayList<Integer> p2In = new ArrayList<Integer>(1000);
				ArrayList<Integer> p2Out= new ArrayList<Integer>();
				double[] p2outputs = new double[5];
				int inputCounter = 0;
				
				for(int i = 0; i < 1000; i++){
					p2In.add(3); //invalid number to be trimmed later
				}
				
				
				/////////////////////////////////
				//*************************************************************************************************************************************
				youngerKeys.clear();
				elderKeys.clear();
				//Store keys of cards in each player's hands
				for(int i = 0; i < 12; i++){
					youngerKeys.add(youngerHand.hand.get(i).getKey());
					elderKeys.add(elderHand.hand.get(i).getKey());
				}
				//key for suit = 0-7 , 8-15, 16-23, 24-31
				Collections.sort(youngerKeys);
				Collections.sort(elderKeys);
				
				//Add keys associated to Player1's current role to player1Inputs
				//keysToAddThisRound.clear();
				ArrayList<Integer> keysToAdd = new ArrayList<Integer>();
				if(player1Younger){
					keysToAdd = youngerKeys;
				}
				else{
					keysToAdd = elderKeys;
				}
				
				removedKeys.clear();;
				
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
				//************************************************************************************************************************************
				
				//get player current hand
				for(int i = 0; i < 12; i++){	//if the card is in our hand mark it as 1
					int j = 0;
					j = keysToAdd.get(i);
					p2In.set(j, 1);
					inputCounter++;
				}
				for(int i = 0; i < 32; i++){ //mark unowned cards as 0
					if(p2In.get(i) == 3){
						p2In.set(i, 0);
						inputCounter++;
					}
				}
				
				//note cards opponent does not have
				//opponent cannot have cards we have or that we have discarded
				for(int i = 0; i < 12; i++){	//if the card is in our hand mark it as 1
					int j = 0;
					j = keysToAdd.get(i);
					p2In.set(j+inputCounter, 1);//////////////////////****************************************************************/TODO inputCounter problem, need to track how many inputs but also need to account for adding the Keys in properly, if the inputCounter is increasing then adding the key will give wrong location
				}
				for(int i = 0; i < removedKeys.size(); i++){	//if the card was removed by us mark it as 1
					int j = 0;
					j = removedKeys.get(i);
					p2In.set(j, 1);
				}
				for(int i = inputCounter; i < inputCounter+32; i++){ //mark other cards as 0
					if(p2In.get(i) == 3){
						p2In.set(i, 0);
					}
				}
				inputCounter+=32;
				
				System.out.println("INPUT COUNTER" + inputCounter);
				
				boolean found = false;
				for(int i = 0; i < p2In.size() && found == false; i++){
					if(p2In.get(i) == 3){
						found = true;
						System.out.println("3 found at position : "+i+ " of p2In");
					}
				}
				
				System.out.println(inputCounter);
				System.out.println(p2In.toString());
				p2In.set(inputCounter, 0); //elder starts first trick
				
				int inCounter = 0;
				int outCounter = 0;
				int aMarker = 0;
				
				System.out.println("\nScores before play phase:\nelderSum = " +elderSum+ "\tyoungerSum = " +youngerSum);
				System.out.println();
				int eldDecScore = elderSum;
				int youngDecScore = youngerSum;
				boolean  newTrick = true;
				boolean  eldersTrick = true;
				boolean  youngerWinningTrick = false;
				boolean cardFound = false;
				Card elderCardPlayed = null;
				Card youngerCardPlayed = null;
				boolean matchedSuit = true;
				
				int i = 0;
				int j = 0;
				int z = 0;
				int trickNo = 1;
				
				while(trickNo <= 12){
					System.out.println("INPUT COUNTER" + inputCounter);
					if(eldersTrick && elderHand.hand.size() > 0){ //elderHand leads trick
					
						System.out.println("TrickNo " +trickNo);
						cardFound = false;
						randCard = rand.nextInt(elderHand.hand.size());
						elderSum+= 1;
						System.out.println("Elder begins new trick by playing "+elderHand.hand.get(randCard).getSuit() + "(" +elderHand.hand.get(randCard) + ")");
						elderCardPlayed = elderHand.hand.get(randCard);
						i = 0;
						
						while( i < youngerHand.hand.size()){
							if(cardFound!=true){
								
								//System.out.println("i = " +i+ "\t randCard = " +randCard);
								youngerCardPlayed = youngerHand.hand.get(i);
								
								if(youngerHand.hand.get(i).getSuit() == elderHand.hand.get(randCard).getSuit()){ //youngerHand has a card of same suit as elder
									
									cardFound = true;
									System.out.println("Younger matches suit with "+youngerHand.hand.get(i));
									
									if(youngerHand.hand.get(i).getNumber() < elderHand.hand.get(randCard).getNumber()){ //youngerHand played a card of same suit but of lower value and so, lost the trick
										System.out.println("Elder wins this trick");
						
										eldersTrick = true;
										newTrick = true;
										youngerHand.hand.remove(i);
										elderHand.hand.remove(randCard);
										trickNo++;
										i = youngerHand.hand.size();
									}
									else{ //youngerHand played a higher card of same suit and won the trick
										
										System.out.println("YoungerHand played a higher card of the same suit and won the trick.");
										youngerSum += 1;
										eldersTrick = false;
										youngerHand.hand.remove(i);
										elderHand.hand.remove(randCard);
										trickNo++;
										i = youngerHand.hand.size();
									}
									
								}
								
								if(i >= youngerHand.hand.size()-1 && !cardFound){ 	//youngerHand Has no card of the suit played by elder.
								    j = rand.nextInt(youngerHand.hand.size()); 
									System.out.println("YoungerHand plays card of different suit " +youngerHand.hand.get(j));
									cardFound = true;
									youngerCardPlayed = youngerHand.hand.get(j);
									youngerHand.hand.remove(j);
									elderHand.hand.remove(randCard);
									trickNo++;
								}
								else{ //card of same suit not found but youngerHand still has cards to be checked
									i++;
								}
							}
						}	
						
					}
					
					
					else if(!eldersTrick && youngerHand.hand.size() > 0){//youngerHand leads trick
						System.out.println("TrickNo " +trickNo);
						cardFound = false;
						youngerSum+=1;
						randCard = rand.nextInt(youngerHand.hand.size());
						System.out.println("Younger begins new trick by playing "+youngerHand.hand.get(randCard).getSuit() + "(" +youngerHand.hand.get(randCard) + ")");
						youngerCardPlayed = youngerHand.hand.get(randCard);
						i = 0;
						
						while( i < elderHand.hand.size()){
							if(cardFound!=true){
								
								//System.out.println("i = " +i+ "\t randCard = " +randCard);
								elderCardPlayed = elderHand.hand.get(i);
								
								if(elderHand.hand.get(i).getSuit() == youngerHand.hand.get(randCard).getSuit()){ //elderHand has a card of same suit as younger
									cardFound = true;
									System.out.println("Elder matches suit with "+elderHand.hand.get(i));
									
									if(elderHand.hand.get(i).getNumber() < youngerHand.hand.get(randCard).getNumber()){ //elderHand played a card of same suit but of lower value and so, lost the trick
										System.out.println("Younger wins this trick");
						
										eldersTrick = false;
										newTrick = true;
										elderHand.hand.remove(i);
										youngerHand.hand.remove(randCard);
										trickNo++;
										i = youngerHand.hand.size();
									}
									else{ //elderHand played a higher card of same suit and won the trick

										System.out.println("ElderHand played a higher card of the same suit and won the trick.");
										eldersTrick = true;
										elderHand.hand.remove(i);
										youngerHand.hand.remove(randCard);
										trickNo++;
										i = youngerHand.hand.size();
									}
									
								}
								
								if(i >= elderHand.hand.size()-1 && !cardFound){ 	//elderHand Has no card of the suit played by younger.
									if(elderHand.hand.size() > 0){
										
									    j = rand.nextInt(elderHand.hand.size()); 
										System.out.println("ElderHand plays card of different suit " +elderHand.hand.get(j));
										cardFound = true;
										elderCardPlayed = elderHand.hand.get(j);
										elderHand.hand.remove(j);
										youngerHand.hand.remove(randCard);
										trickNo++;
									}
								}
								else{ //card of same suit not found but elderHand still has cards to be checked
									i++;
								}
							}
						}
					}
					//TODO next input sets accounting for changes made due to play phase, elderCardPlayed youngerCardPlayed, suit matched?
					
					//player hand
					//get player current hand
					for(int y = 0; y < 12; y++){	//if the card is in our hand mark it as 1
						int x = inputCounter;
						x += keysToAdd.get(y);
						p2In.set(x, 1);
					}
					for(int y = inputCounter; y < inputCounter+32; y++){ //mark unowned cards as 0
						if(p2In.get(y) == 3){
							p2In.set(y, 0);
						}
					}
					inputCounter+= 32;
					//note cards opponent does not have
					//opponent cannot have cards we have or that we have discarded
					for(int y = 0; y < 12; y++){	//if the card is in our hand mark it as 1
						int x = inputCounter;
						x += keysToAdd.get(y);
						p2In.set(x, 1);
					}
					for(int y = 0; y < removedKeys.size(); y++){	//if the card was removed by us mark it as 1
						int x = inputCounter;
						x += removedKeys.get(y);
						p2In.set(x, 1);
					}
					
					aMarker = inputCounter;
					
					//if suit was not matched mark cards in that suit as 1
					////key for suit = clubs 0-7 , diamonds 8-15, spades 16-23, hearts 24-31
					if(youngerCardPlayed.getSuit() != elderCardPlayed.getSuit()){
						System.out.println("Suit not matched: " +elderCardPlayed.getSuit());
						switch(elderCardPlayed.getSuit()){
						
							case "Clubs":
								for(int k = 0; k <= 7; k++){
									p2In.set(aMarker + k, 1);
									//inputCounter++;
								}
								break;
							case "Diamonds": 
								for(int k = 8; k <= 15; k++){
									p2In.set(aMarker + k, 1);
									//inputCounter++;
								}
								break;
							case "Spades":
								for(int k = 16; k <= 23; k++){
									p2In.set(aMarker + k, 1);
									//inputCounter++;
								}
								break;
							case "Hearts":
								for(int k = 24; k <= 31; k++){
									p2In.set(aMarker + k, 1);
									//inputCounter++;
								}
								break;
						}
					}
					
					for(int y = inputCounter; y < inputCounter+32; y++){ //mark other cards as 0
						if(p2In.get(y) == 3){
							p2In.set(y, 0);
						}
					}
					inputCounter+=32;			
					
				}
				System.out.println("INPUT COUNTER END" + inputCounter);
				
				int empty = 0;
				for(int i1 = 0; i1 < 1000; i1++){
					System.out.print(p2In.get(i1));
					
					if(p2In.get(i1)== 3){
						empty ++;
					}
				}
				System.out.println("\nempty " +empty);
				
				
				System.out.println("elderHand.size() = " +elderHand.hand.size() + "\tyoungerHand.hand.size() = " + youngerHand.hand.size());
				System.out.println("Elder won " +(elderSum - eldDecScore)+ " points from play.\tYounger won " +(youngerSum-youngDecScore+ " from play."));
				
				
				//check which role won this round
				if(youngerSum > elderSum){
					System.out.println("youngerHand wins!");
				}else{
					System.out.println("elderHand wins!");
				}
				//add score of roles to score of respective players
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
				round++;
				System.out.println("Round " +round+ " end.");
			}
			
			//store score outputs
			double[] outputs = new double[5];
			if(player1Score > player2Score){
				System.out.println("Player1 wins!\t" +player1Score + " - " + player2Score);
				
				if(player1Score - player2Score > 10){ //p1 won by over 10
					outputs[0] = 1.0;
					outputs[1] = 0.0;
					outputs[2] = 0.0;
					outputs[3] = 0.0;
					outputs[4] = 0.0;
				}
				else{								// p1 won by less than 10
					outputs[0] = 0.0;
					outputs[1] = 1.0;
					outputs[2] = 0.0;
					outputs[3] = 0.0;
					outputs[4] = 0.0;
				}
			}
			else if(player2Score > player1Score){
				System.out.println("Player2 wins!\t" +player1Score + " - " + player2Score);
				if(player2Score - player1Score > 10){ //p2 won by over 10
					outputs[0] = 0.0;
					outputs[1] = 0.0;
					outputs[2] = 0.0;
					outputs[3] = 1.0;
					outputs[4] = 0.0;
				}
				else{								//p2 won by less than 10
					outputs[0] = 0.0;
					outputs[1] = 0.0;
					outputs[2] = 1.0;
					outputs[3] = 0.0;
					outputs[4] = 0.0;
				}
			}
			else if(player1Score == player2Score){ //draw
				System.out.println("Scores Equal : Draw. " +player1Score + " - " + player2Score);
				outputs[0] = 0.0;
				outputs[1] = 0.0;
				outputs[2] = 0.0;
				outputs[3] = 0.0;
				outputs[4] = 1.0;
			}
			for(int i = 0; i <= 4; i++){
				System.out.println(outputs[i]);
			}
		
			
			System.out.println("Game " + game + " end.");
			trainingSet.addElement(new SupervisedTrainingElement(player1Inputs, outputs)); //add the set of inputs and outputs recorded to the set of training sets to be used for learning
			
			
		}
		
		MultiLayerPerceptron network = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 103, 50, 5);
		
		network.randomizeWeights();
        DynamicBackPropagation train = new DynamicBackPropagation();
        train.setNeuralNetwork(network); 
        train.setLearningRate(0.0008);	//learning rate of the network, must be very small
        network.setLearningRule(train); 
        
        double newError = -1;
        double oldError = -1;
        double difference = 0;
        double percentDif;
        
        int epoch = 1;
        do
        {
        	if(newError > 0){
        		oldError = newError;
        	}
        	train.doOneLearningIteration(trainingSet);
        	newError = train.getTotalNetworkError();
        	System.out.println("Epoch " + epoch + ", error=" + train.getTotalNetworkError());
        	epoch++;
        	
        	difference = oldError - newError;
        	percentDif = (difference / oldError)*100;
        	//System.out.println("percentDif = " +percentDif);
        } //while(train.getTotalNetworkError()>0.9 );
        while(epoch < 5000);	// training will stop after specified number of iterations
        
        System.out.println("percentDif = " +percentDif);
        
        System.out.println("Neural Network Results:");
        
        double[] testInputs = new double[103];
        for(int i = 0; i < 103; i++){
        	testInputs[i] = 1;
        }
        network.setInput(testInputs);
		network.calculate();
		Vector<Double> output = network.getOutput();
		System.out.println("Output: \n" +output.get(0)+ "\n" +output.get(1)+ "\n" + output.get(2) + "\n" + output.get(3)+ "\n" + output.get(4) + "\n\nNOut = " + network.getOutput());
		
		TestNetworkDecPhase test = new TestNetworkDecPhase(network);
		
		
//      DynamicBackPropagation train = new DynamicBackPropagation();
//      train.setNeuralNetwork(phase2Network);
//      train.setLearningRate(0.0008);
//      phase2Network.setLearningRule(train);
//      
//     	int epoch = 1;
//      do
//      {
//      	train.doOneLearningIteration(trainingSet);
//      	System.out.println("Epoch " + epoch + ", error=" + train.getTotalNetworkError());
//      	epoch++;
//      	
//      } while(train.getTotalNetworkError()>0.9);
//      
//      System.out.println("Neural Network Results:");
//      
//      
//      for(TrainingElement element : trainingSet.trainingElements()) {
//      	network.setInput(element.getInput());
//          network.calculate();
//          Vector<Double> output = network.getOutput();
//          SupervisedTrainingElement ste = (SupervisedTrainingElement)element;
//          
//			System.out.println(element.getInput().get(0) + "," + element.getInput().get(0)
//					+ ", actual=" + output.get(0) + ",ideal=" + ste.getDesiredOutput().get(0));
//		}
		
	}
}
