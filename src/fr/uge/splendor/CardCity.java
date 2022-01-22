package fr.uge.splendor;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;


/** Used to represent City cards */
public record CardCity(int prestige, String image, ArrayList<Gem> price) implements Cards {
	
	/** 
	 * @param prestige : amount of point for the win
	 * @param image : source of the picture
	 * @param price : list of gems which is the price for the win */
	public CardCity{
		Objects.requireNonNull(prestige, "Prestige is not present");
		Objects.requireNonNull(image, "Picture is undefined");
		Objects.requireNonNull(price, "price is undefined");
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
		return -1; /*null*/
	}
	
	@Override
	public Gem bonus() {
		return null;
	}
	
	/** Verify if the player can acquire the city. It means that if the player
	 * has ennough bonus gems, we will return true.
	 * @param gems list of bonus gems */
	@Override
	public boolean canVisit(List<Gem> gems) {
		Objects.requireNonNull(gems);
		int n = 0;
		for(var p: price) {
			if(p.nbr() <= gems.get(n).nbr())
				n++;
			else
				return false;
		}
		return true;
	}
}