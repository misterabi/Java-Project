package fr.uge.splendor;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;


/** Used to represent Noble cards */
public record CardNob(String image, ArrayList<Gem> price) implements Cards {
	final static int prestige = 3;
	
	/** 
	 * @param image : source of the picture
	 * @param price : list of gems which is the price of the visit */
	public CardNob{
		Objects.requireNonNull(image, "Picture is undefined");
		Objects.requireNonNull(price, "Price is undefined");
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
	
	/** Verify if the noble can visit the player. It means that if the player
	 * has ennough bonus gems, a noble will visit him.
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