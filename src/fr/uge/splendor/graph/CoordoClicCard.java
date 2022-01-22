package fr.uge.splendor.graph;

/** Class used to represent a clic on a card. */
public class CoordoClicCard{
	private int type_card;
	private int level;
	private int index;
	
	/** We can use this to have a specific index, level, and type of a card. */
	public CoordoClicCard(){
		int type_card = 0, level = 0, index = 0;
		this.setTypeCard(type_card);
		this.setLevel(level);
		this.setIndex(index);
	}

	/** Get the type of the card. */
	public int getTypeCard() {
		return type_card;
	}

	/** Set the type of the card. */
	public void setTypeCard(int type_card) {
		this.type_card = type_card;
	}

	/** Get the level of the card. */
	public int getLevel() {
		return level;
	}

	/** Set the level of the card. */
	public void setLevel(int level) {
		this.level = level;
	}

	/** Get the index of the card. */
	public int getIndex() {
		return index;
	}

	/** Set the index of the card. */
	public void setIndex(int index) {
		this.index = index;
	}
	
	@Override
	public String toString() {
		return "CoordoClicCard["+level+","+index+"(Type:"+type_card+")]";
	}
}