package fr.uge.splendor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/** Used to represent all cards (Dev and Nobles) */
public sealed interface Cards permits CardNob, CardDev, CardCity {
	/** return the prestige point of the player
	 *  @return int*/
	int getPrestige(); 
	/** return the name of the card
	 *  @return String*/
	String getName(); 
	/** return the level of the card 
	 * @return ArrayList of gem */
	ArrayList<Gem> price();
	/** return the level of the card 
	 * @return int*/
	int level();		
	/** return the gem
	 *  @return Gem */
	Gem bonus();		
	/** check if a noble can visit a player
	 *  @param gems : a list of gem 
	 *  @return boolean */
	boolean canVisit(List<Gem> gems);
	
	
	/** Get informations of a card by using a String then return the card.
	 * @param cardName the text that contains all informations of the card
	 * @return the card that has been created */
	static Cards fromText(String cardName) {
		Objects.requireNonNull(cardName);
		var s = cardName.split(",");
		Cards card;
		switch(s[0]) {
			case "Dev" -> { /*carte Dev*/
				var price = Gem.createGemList(Integer.parseInt(s[6]), Integer.parseInt(s[7]), 
						Integer.parseInt(s[8]), Integer.parseInt(s[9]), Integer.parseInt(s[10]), -1);
				card = new CardDev(Integer.parseInt(s[1]), new Gem(s[2], 1), Integer.parseInt(s[3]), s[5], price);
			}
			case "Nob" -> { /*carte Noble*/
				var price = Gem.createGemList(Integer.parseInt(s[6]), Integer.parseInt(s[7]), 
						Integer.parseInt(s[8]), Integer.parseInt(s[9]), Integer.parseInt(s[10]), -1);
				card = new CardNob(s[5], price);
			}
			case "City" -> { /*carte City*/
				var price = Gem.createGemList(Integer.parseInt(s[6]), Integer.parseInt(s[7]), 
						Integer.parseInt(s[8]), Integer.parseInt(s[9]), Integer.parseInt(s[10]), -1);
				card = new CardCity(Integer.parseInt(s[3]), s[5], price);
			}
			default -> throw new IllegalArgumentException("This type of card is not supported.");
		}
		return card;
	}
	
	/** Get the size of the chosen Dev deck.
	 * @param list the list of Dev cards
	 * @param level the level of the chosen Dev deck (1, 2 or 3)
	 * @return the size of the chosen deck */
	static int getSizeByLevel(ArrayList<Cards> list, int level) {
		Objects.requireNonNull(list); Objects.requireNonNull(level);
		int s = 0;
		if(level == 1 || level == 2 || level == 3) {
			for(var card: list) {
				if(card.level() == level)
					s++;
			}
		}
		else
			throw new IllegalArgumentException("This card level does not exist.");
		return s;
	}
}
