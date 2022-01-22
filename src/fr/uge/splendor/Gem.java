package fr.uge.splendor;

import java.util.Objects;
import java.util.ArrayList;


/** Used to represent the gems */
public class Gem {
	private String color;
	private int nbr;
	
	/** 
	 * @param color : the color of the gem
	 * @param nbr : the amount of the gem*/
	public Gem(String color, int nbr) {
		Objects.requireNonNull(color, "Color gem is undefined");
		Objects.requireNonNull(nbr, "Gems nbr is not present");
		this.color = color;
		this.nbr = nbr;
	}
	
	/** @return String : the name of the color*/
	public String color() {
		return color;
	}
	/** @return int :the amount of gems*/
	public int nbr() {
		return nbr;
	}
	
	/** 
	 * @param newNbr :Add an amount of gem. */
	public void add(int newNbr) {
		Objects.requireNonNull(newNbr);
		nbr = nbr + newNbr;
	}
	
	/** 
	 * @param newNbr :Delete an amount of gem.  */
	public void remove(int newNbr) {
		Objects.requireNonNull(newNbr);
		nbr = nbr - newNbr;
	}
	
	/**  
	 * @param newNbr : is now the new amount of gems.*/
	public void affectation(int newNbr) {
		Objects.requireNonNull(newNbr);
		nbr = newNbr;
	}
	
	@Override
	public String toString() {
		return color + "=" + nbr;
	}
	
	/** Create a list of gems in the correct order: white, blue, green, red, black, yellow.
	 * The parameters represents the amount of theses gems.
	 * @return the list that has been created */
	static ArrayList<Gem> createGemList(int white, int blue, int green, int red, int black, int yellow) {
		Objects.requireNonNull(white); Objects.requireNonNull(blue);
		Objects.requireNonNull(green); Objects.requireNonNull(red);
		Objects.requireNonNull(black); Objects.requireNonNull(yellow);
		var list = new ArrayList<Gem>();
		list.add(new Gem("white", white));
		list.add(new Gem("blue", blue));
		list.add(new Gem("green", green));
		list.add(new Gem("red", red));
		list.add(new Gem("black", black));
		if(yellow >= 0)
			list.add(new Gem("yellow", yellow));
		return list;
	}
}
