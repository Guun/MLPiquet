import java.util.ArrayList;
import java.util.Collections;

/*
 * An implementation of a player.
 */
public class Player {
	/*
	 * The player's name
	 */
	private String name;
	/*
	 * The cards in the player's current hand.
	 */
	public ArrayList<Card> hand = new ArrayList(13);

	/*
	 * The number of cards in the player's current hand.
	 */
	private int numCards;
	
	/*
	 * Player constructor
	 * aName: the name of the player
	 */
	public Player(String aName){
		
		this.name = aName;
		
		//set a player's hand to empty
		this.emptyHand();
	}
	
	/*
	 * Reset the player's hand to have no cards.
	 */
	public void emptyHand() {
		this.hand.clear();
		this.numCards = 0;
	}

	/*
	 * Add a card to the player's hand.
	 * aCard:  the card to add
	 */
	public boolean addCard(Card aCard){
		
		//print error if we already have the max number of cards
		if(this.numCards == 12){
			System.err.printf("%s's hand already has 12 cards; " +
						"cannot add another\n", this.name);
			System.exit(1);
		}
		
		//add new card in next slot and increment number of cards counter
		this.hand.add(numCards, aCard);
		this.numCards++;
		
		return true;
		
	}
	
	public int getPoint(){
		int numClubs = 0;
		int numDiamonds = 0;
		int numSpades = 0;
		int numHearts = 0;
		int maxPoint = 0;
		
		
		// calc number of cards held in each suit
		for (int c= 0; c < this.numCards; c ++){
			
			if(this.hand.get(c).getSuit() == "Clubs"){
				numClubs++;
			}
			if(this.hand.get(c).getSuit() == "Diamonds"){
				numDiamonds++;
			}
			if(this.hand.get(c).getSuit() == "Spades"){
				numSpades++;
			}
			if(this.hand.get(c).getSuit() == "Hearts"){
				numHearts++;
			}
		}
		maxPoint = findMax(numClubs,numDiamonds,numSpades,numHearts); //find max number of cards of any one suit in hand
		System.out.println("Point = " + maxPoint);
		return maxPoint;	
	}
	
	//return max total value of cards in a suit of the hand. Used when both players have same max number of cards in suit 
	public int getPointTieBreak(){
		int numClubs = 0;
		int numDiamonds = 0;
		int numSpades = 0;
		int numHearts = 0;
		int pointValue = 0;
		int maxPoint = 0;
		
		
		// calc total value of cards in suits
		for (int c= 0; c < this.numCards; c ++){
			
			if(this.hand.get(c).getSuit() == "Clubs"){
				numClubs += getValue(this.hand.get(c));
			}
			if(this.hand.get(c).getSuit() == "Diamonds"){
				numDiamonds += getValue(this.hand.get(c));
			}
			if(this.hand.get(c).getSuit() == "Spades"){
				numSpades += getValue(this.hand.get(c));
			}
			if(this.hand.get(c).getSuit() == "Hearts"){
				numHearts += getValue(this.hand.get(c));
			}
		}
		pointValue = findMax(numClubs,numDiamonds,numSpades,numHearts);

		return pointValue;	
	}
	
	//return sequence lengths in hand
	public int[] getSequence(){
		int maxSequence = 0;
	
		ArrayList<Integer> alClubs = new ArrayList<Integer>();
		ArrayList<Integer> alDiamonds = new ArrayList<Integer>();
		ArrayList<Integer> alSpades = new ArrayList<Integer>();
		ArrayList<Integer> alHearts = new ArrayList<Integer>();
		
		
		// sort cards into arraylists of their suit
		for (int c= 0; c < this.numCards; c ++){
			
			if(this.hand.get(c).getSuit() == "Clubs"){
				alClubs.add(this.hand.get(c).getNumber()); 
			}
			if(this.hand.get(c).getSuit() == "Diamonds"){
				alDiamonds.add(this.hand.get(c).getNumber()); 
			}
			if(this.hand.get(c).getSuit() == "Spades"){
				alSpades.add(this.hand.get(c).getNumber()); 
			}
			if(this.hand.get(c).getSuit() == "Hearts"){
				alHearts.add(this.hand.get(c).getNumber()); 
			}
		}
		
		/*System.out.println("Before Sorting:");
		for(int counter: alClubs){
			System.out.println(counter);
		}*/
		
		
		//Sorting of arraylist using Collections.sort*/
		Collections.sort(alClubs);
		Collections.sort(alDiamonds);
		Collections.sort(alSpades);
		Collections.sort(alHearts);
		
		// ArrayList after sorting
		/*System.out.println("After Sorting:");
		for(int counter: alClubs){
			System.out.println(counter);
		}*/
//		if(alClubs.size() > 0){
//		System.out.println("HIGHEST CLUBS TEST " +alClubs.get(alClubs.size()-1));
//		}
//		if(alDiamonds.size() > 0){
//			System.out.println("HIGHEST DIAMONDS TEST " +alDiamonds.get(alDiamonds.size()-1));
//		}
//		if(alSpades.size() > 0){
//			System.out.println("HIGHEST SPADES TEST " +alSpades.get(alSpades.size()-1));
//		}
//		if(alHearts.size() > 0){
//			System.out.println("HIGHEST HEARTS TEST " +alHearts.get(alHearts.size()-1));
//		}
	

		int clubsSeq = findMaxSeq(alClubs) ;
		int diamondsSeq = findMaxSeq(alDiamonds);
		int spadesSeq = findMaxSeq(alSpades);
		int heartsSeq = findMaxSeq(alHearts);
		
		int longestSeq = findMax(clubsSeq, diamondsSeq, spadesSeq, heartsSeq);
		
		int[] sequences = {longestSeq, clubsSeq, diamondsSeq, spadesSeq, heartsSeq};		
		
		//System.out.println("BEST SEQUENCE LENGTH :  " + longestSeq);
		
		return sequences;
	}
	
	//return max set in hand
	public int getSet(){
		int maxSet = 0;
		
		int totTen, totJack, totQueen, totKing, totAce;
		totTen = totJack = totQueen = totKing = totAce = 0;
		
		for(int c= 0; c < this.numCards; c++){
			switch(this.hand.get(c).getNumber()){
			
				case 10:
					totTen += 1;
					break;
				case 11:
					totJack += 1;
					break;
				case 12:
					totQueen += 1;
					break;
				case 13:
					totKing += 1;
					break;
				case 14:
					totAce += 1;
					break;
			}
		}
		//System.out.println("totTen = " +totTen+ "\t totJack = "+totJack+"\t totQueen = " +totQueen+ "\t totKing = " +totKing+ "\t totAce = "+totAce);
		maxSet = findMax(totTen, totJack, totQueen, totKing, totAce);
		return maxSet;
		
	}
	
	//return max of values given
	private int findMax(int... vals) {
		int max = -1;
		for(int d : vals){
			if(d > max) max = d;
		}
		return max;
	}
	
	private int findMaxSeq( ArrayList<Integer> alSuit){
		
		int maxSeq;
		int bestIndex = 0;
		int bestLength = 1;
		int curIndex = 0;
		int curLength = 1;
		int i = 1;
		int startPos = 0;
		
		while((startPos + i) < alSuit.size()-1){
			if (alSuit.get(startPos + i)  ==  alSuit.get(startPos) + i)
			{
				curLength++;
				i++;
			}
			else
			{   
				//restart at this index since it's a new possible starting point
				curLength = 1;
				startPos = startPos + i + 1;
				i++;
			}

			if(curLength > bestLength)
			{
				bestIndex = startPos;
				bestLength = curLength;
			}
		}
		maxSeq = bestLength;
		return maxSeq;
	}
	
	//gets value for cards in the event of a tiebreaker 
	public int getValue(Card card){
		
		if(card.getNumber() == 11 || card.getNumber() == 12 || card.getNumber() == 13){
			return 10;
		}
		if(card.getNumber() == 14){
			return 11;
		}
		else{
			return card.getNumber();
		}
	}
	
	//prints all cards in the player's hand
	public void printHand(){
		
		System.out.printf("%s's cards: \n",  this.name);
		for(int c = 0; c < this.numCards; c++)
		{
			System.out.printf(c + ".  " + " %s\n", this.hand.get(c).toString());
		}
	}

	public void exchange(int randCard, Card card) {
	
		this.hand.set(randCard, card);
		
	}
}
