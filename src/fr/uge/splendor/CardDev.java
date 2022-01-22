package fr.uge.splendor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/** Used to represent Dev cards */
public record CardDev(int level, Gem bonus, int prestige, String image, ArrayList<Gem> price) implements Cards {
	
	/** 
	 * @param level : the level of the cards
	 * @param bonus : the gem bonus
	 * @param prestige : amount of point that will give
	 * @param image : the source picture
	 * @param price : a list of the price of the card
	 */
	public CardDev{
		Objects.requireNonNull(image, "Picture is undefined");
		Objects.requireNonNull(price, "Price is undefined");
		Objects.requireNonNull(prestige, "Prestige is not present");
		Objects.requireNonNull(level, "Level is not present");
		Objects.requireNonNull(bonus, "Bonus is undefined");
	}

	@Override
	public int getPrestige() {
		return prestige;
	}
	
	@Override
	public String getName() {
		return image;
	}
	
	@Override
	public ArrayList<Gem> price(){
		return price;
	}
	
	@Override
	public int level() {
		return level;
	}
	
	@Override
	public Gem bonus() {
		return bonus;
	}
	
	@Override
	public boolean canVisit(List<Gem> gems) {
		return false;
	}
	
	@Override
	public String toString() {
		var s = new StringBuilder();
		var sPrice = new StringBuilder();
		
		s.append("Gemme: ").append(bonus.color());
		s.append(". Prestige: ").append(prestige).append(". Prix: ");
		for(var element: price)
			sPrice.append(element).append(" ");
		s.append(sPrice.toString());
		
		return s.toString();
	}
}