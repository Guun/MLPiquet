/*
 * 
 * An implementation of a card type
 */
public class Card {
	/*
	 * one of the four valid suits for this card
	 */
	private Suit mySuit;
	/*
	 * The number of this card, where Ace: 14, Jack-King: 11-13
	 */
	private int myNumber;
	
	private int myKey;
	/*
	 * 
	 * Card constructor 
	 * aSuit 	the suit of the card
	 * aNumber 	the number of the card
	 * key		the unique id of the card
	 */
	public Card(Suit aSuit, int aNumber, int key){
		
		this.mySuit = aSuit;
		this.myKey = key;
		if(aNumber >= 7 && aNumber <= 14) {
			this.myNumber = aNumber;
		} else{
			System.err.println(aNumber + " is not a valid Card number");
			System.exit(1);
		}	
	}
	
	/*
	 * returns number of the card
	 * */
	public int getNumber() {
		return myNumber;
	}
	
	public String getSuit() {
		return mySuit.toString();
	}
	
	public int getKey(){
		return myKey;
	}
	
	public String toString(){
		
		String numStr = "Error";
		
		switch(this.myNumber){
			
			case 7:
				numStr = "Seven";
				break;
			case 8: 
				numStr = "Eight";
				break;
			case 9:
				numStr = "Nine";
				break;
			case 10:
				numStr = "Ten";
				break;
			case 11:
				numStr = "Jack";
				break;
			case 12:
				numStr = "Queen";
				break;
			case 13:
				numStr = "King";
				break;
			case 14:
				numStr = "Ace";
				break;	
				
		}
		return numStr + " of " + mySuit.toString() +". Key: " + myKey ;
	}
}
