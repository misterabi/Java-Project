package fr.uge.splendor.robot;

import java.util.ArrayList;

import fr.uge.splendor.Game;

public class Robot {
	/*
	 * Return a random color of gem in the list with a minimun of quantity
	 */
	public static String Color_random(Game game, int min, String colorSelect) {
		var lst_color = new ArrayList<Integer>();
		var lst_color_select = colorSelect.split(" ");
		int index = 0;
		for (var color : game.gems()) {
			if (color.nbr() >= min && color.color() != "yellow") {
				lst_color.add(index);
			}
			for (var elem : lst_color_select) {
				if (elem.equals(color.color()))
					lst_color.remove(lst_color.size() - 1);
			}
			index++;
		}
		if (lst_color.size() == 0) {
			return "null";
		} else {
			int random = Game.getRandomInt(0, lst_color.size());
			return game.gems().get(lst_color.get(random)).color();
		}
	}


	
	public static boolean containNullInLst(String[] lst) {
		if(lst == null) {
			return true;
		}
		return 	false;
	}
}
