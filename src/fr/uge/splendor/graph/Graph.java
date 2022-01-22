package fr.uge.splendor.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;

import fr.uge.splendor.Cards;
import fr.uge.splendor.Game;
import fr.uge.splendor.Gem;
import fr.uge.splendor.Player;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.Event.Action;


/** Class used to draw the game in version >= 2*/
public class Graph {
	/** Return a new x to be sized with the width of the screen. */
	private static int x(int px, int width) {
		return (px*width)/2560;
	}
	
	/** Return a new y to be sized with the height of the screen. */
	private static int y(int py, int height) {
		return (py*height)/1440;
	}

	/** Return the correct color of the chosen string color.
	 * For exemple, "blue" will return Color.BLUE. */
	private static Color StringToColor(String color) {
		return switch(color) {
			case "red" -> Color.RED;
			case "blue" -> Color.BLUE;
			case "white" -> Color.WHITE;
			case "green" -> Color.GREEN;
			case "yellow" -> Color.YELLOW;
			default -> Color.BLACK;
		};
	}
	
	/** Draw the specified image given by it's name at the position (x,y) with the size (w,h). */
	private static void image(String name, int x, int y, int w, int h, Graphics2D graphics) {
		Image image = new ImageIcon(Graph.class.getResource(name)).getImage();
		if(image != null) {
			graphics.drawImage(image, x, y, w, h, null, null);
		}
	}
	
	/** Same thing as image() but this will generate a whole new frame of the game with an ApplicationContext parameter. */
	private static void imageByContext(String name, int x, int y, int w, int h, ApplicationContext context) {
		context.renderFrame(graphics ->{
			Image image = new ImageIcon(Graph.class.getResource(name)).getImage();
			if(image != null) {
				graphics.drawImage(image, x, y, w, h, null, null);
			}
		});
	}
	
	/** You can use this to verify if the actual location of the pointer of the mouse is between (inf_x,inf_y) and (sup_x,sup_y).
	 * This static function automaticly use the two size transformation methods x() and y() to adapt the verification to the size of the screen. */
	private static boolean verif(Point2D.Float location, double inf_x, double sup_x, double inf_y, double sup_y, int w, int h) {
		inf_x = x((int)inf_x, w);
		sup_x = x((int)sup_x, w);
		inf_y = y((int)inf_y, h);
		sup_y = y((int)sup_y, h);
		if(location.x > inf_x && location.x < sup_x && location.y > inf_y && location.y < sup_y)
			return true;
		return false;
	}
	
	/** Wait for a POINTER_DOWN action of the mouse and return its location. */
	private static Point2D.Float clic(ApplicationContext context){
		for(;;) {
	        Event event = context.pollOrWaitEvent(10);
	        if (event == null)
	            continue;
	        Action action = event.getAction();
	        if(action == Action.POINTER_DOWN) /*clic de souris*/
	        	return event.getLocation();
		}
	}
	
	/** Draw the title and detect an action in the screen (play, quit).  */
	public static boolean title(int w, int h, ApplicationContext context) {
		Point2D.Float location;
		context.renderFrame(graphics -> {
			image("/title.PNG", 0, 0, w, h, graphics);
		});
		for(;;) {
			location = clic(context);
        	if(verif(location, 456.0, 1142.0, 678.0, 901.0, w, h))
        		return true; /*JOUER*/
        	else if(verif(location, 527.0, 1074.0, 1021.0, 1199.0, w, h))
        		return false; /*QUITTER*/
		}
	}
	
	/** Draw the game (all the cards, the players, the gems, etc...).
	 * Since the game is updating after each turn, this method will also be updated. */
	public static void drawGame(Game game, int w, int h, ApplicationContext context) {
		Objects.requireNonNull(game);
		context.renderFrame(graphics -> {
	        graphics.setFont(new Font("TimesRoman", Font.BOLD, 20));
			image("/plateau.PNG", 0, 0, w, h, graphics); //plateau
			drawCardsInGame(game, w, h, graphics); //cartes dev
			drawTokensInList(game.gems(),10,1200,40, w, h, graphics); //gems
			drawAllPlayer(game,w,h,graphics); //players
			drawStarForActualPlayer(game, w, h, graphics); //star
		});
	}

    /** Draw a bonus gem with a specific color at a position (x,y). */
	private static void drawGemBonus(int x, int y, int w, int h, Color colorGem, Graphics2D graphics) {
        int lst_x[] = { x + x(50,w), x + x(60,w), x + x(80,w), x + x(90,w), x + x(70,w) };
        int lst_y[] = { y + y(20,h), y + y(10,h), y + y(10,h), y + y(20,h), y + y(40,h) };
        graphics.setColor(colorGem);
        graphics.fillPolygon(lst_x, lst_y, 5);
    }

	/** Draw a gem inside a circle with its number at position x,y. A specific color will be automaticly
     * used to draw the border of the gem's circle. */
    private static void drawGem(int x, int y, int size, int w, int h, Color color, int nbGem, Graphics2D graphics) {
        // Couleurs de la bordure et du nombre réglés en fonction de la couleur de la gemme choisie
        Color border = Color.BLACK, number = Color.WHITE;
        if(color == Color.BLACK) border = Color.WHITE;
        if(color == Color.WHITE || color == Color.YELLOW) number = Color.BLACK;
        // Affichage de la gemme
        graphics.setColor(border);
        graphics.fillOval(x, y, x(30+size,w), y(30+size,h));
        graphics.setColor(color);
        graphics.fillOval(x, y, x(29+size,w), y(29+size,h));
        graphics.setColor(number);
        graphics.drawString(String.valueOf(nbGem), x + x(10,w)+(x(size,w)/2), y + y(20,h)+(y(size,h)/2));
    }

	/** Draw a card at the position (x,y) with its bonus gem, price, prestige and background. */
	private static void drawCard(int x, int y, int w, int h, double ratio, Cards card, Graphics2D graphics) {
	    image("/background/"+card.bonus().color()+".png", x, y, x( (int)(300*ratio),w), y((int)(400*ratio),h), graphics); //background
	    drawGemBonus(x+x((int)(200*ratio-50),w), y+y((int)(15*ratio),h), w, h, StringToColor(card.bonus().color()), graphics); //gemme bonus
	    drawAllGemInList(card.price(), ratio, x, y, w, h, graphics); //all gems of this card
		graphics.setColor(Color.WHITE);
		graphics.drawString(String.valueOf(card.getPrestige()), x + x((int)(40*ratio),w), y + y((int)(40*ratio),h)); //prestige
	}
	
	/** Draw all wanted gems in the given list. */
	private static void drawAllGemInList(ArrayList<Gem> gem, double ratio, int x, int y, int w, int h, Graphics2D graphics) {
		int pos=25;
		for(var elem : gem) { //prix
	  	    if(elem.nbr() > 0) {
	  	    	drawGem(x+x((int)(10*ratio),w), y+y((int)(400*ratio),h)-y(pos,h), 0, w, h, StringToColor(elem.color()),elem.nbr(), graphics);
	  	    	pos += 30;}
	    }
	}
	
	/** Draw all cards in the game (all dev cards from level 1,2,3). */
	private static void drawCardsInGame(Game game, int w, int h, Graphics2D graphics) {
		int pos_x, pos_y = 0, x, y = 0;
		for(var lst_dev : game.dev_in_game()) {
			x = 0; pos_x = 0;
			for(var lst_card : lst_dev) {
				drawCard(x(410+pos_x+x,w), y(45+pos_y+y,h), w, h, 1, lst_card, graphics);
				pos_x += 300;
				x += 13;
			}
			y += 75; pos_y += 400;
		}
		if(!game.city().isEmpty()) /*affiche les cités si il existe des cités (version == 3)*/
			drawAllCities(game, w, h, graphics);
	}
	
	/** Draw all 3 cities in the game (only in the City extension, version == 3). */
	private static void drawAllCities(Game game, int w, int h, Graphics2D graphics) {
		int pos_y = 0;
		for(var c : game.city()) {
			graphics.setColor(Color.BLACK);
			graphics.fill3DRect(x(18,w), y(18+pos_y,h), x(330,w), y(150,h), true); /*background*/
			drawAllGemInList(c.price(), 1, x(300,w), y(-237+pos_y,h), w, h, graphics); /*bonus*/
			graphics.setColor(Color.WHITE);
			graphics.drawString(String.valueOf(c.getPrestige()), x(50,w), y(50+pos_y,h)); /*prestige*/
			graphics.drawString(c.getName(), x(50,w), y(100+pos_y,h)); /*name*/
			pos_y += 160;
		}
	}
	
	/** Draw all tokens of the wanted token list (it can be the list of the game tokens or the list of tokens of a card). */
    private static void drawTokensInList(List<Gem> gem, int pos_x, int pos_y, int size, int w, int h, Graphics2D graphics) {
        int i, x = 0, y = 0;
        graphics.setFont(new Font("TimesRoman", Font.BOLD, size/2));

        for(i=0; i<gem.size(); i++) {
            drawGem(x(x,w)+x(pos_x,w), y(y,h)+y(pos_y,h), size, w, h, StringToColor(gem.get(i).color()), gem.get(i).nbr(), graphics);
            x += size*2;
            if(i%2==0 && (i!=0 && i==2)) {
                x = 0;
                y += size*2;
            }
        }
        graphics.setFont(new Font("TimesRoman", Font.BOLD, 20));
    }
    
	/** Draw all the players of the game. */
	public static void drawAllPlayer(Game game, int w, int h, Graphics2D graphics) {
		for(int j=0; j<game.player().size(); j++)
			drawPlayer(game.player().get(j), j, w, h, graphics);
	}
	
	/** Draw the wanted player of the game. */
    public static void drawPlayer(Player player, int j, int w, int h, Graphics2D graphics) {
        graphics.setColor(Color.BLACK);
        graphics.drawString("Name : " + String.valueOf(player.name()), x(1520, w) + x((j % 2) * 420 + 200, w), y(70, h) + y(((j) / 2) * 700, h)); // nom
        graphics.drawString("Prestige Point : " + String.valueOf(player.prestige()), x(1720, w) + x((j % 2) * 420, w), y(170, h) + y(((j) / 2) * 650, h)); // prestige
        for (int i = 0; i<player.deck().size(); i++) { //cartes réservées
            if (i == 2)
                drawCard(x((1720 + ((i % 2) * 140)) + 420 * ((j) % 2), w), y(300 + ((j) / 2) * 700 + 200, h), w, h, 0.5,
                        player.deck().get(i), graphics);
            else
                drawCard(x((1720 + ((i % 2) * 140)) + 420 * ((j) % 2), w), y(300 + ((j) / 2) * 700, h), w, h, 0.5,
                        player.deck().get(i), graphics);
        }
        drawTokensInList(player.tokens(), 1720 + (j % 2) * 420, 200 + ((j) / 2) * 700, 20, w, h, graphics); //gemmes
        drawTokensInList(player.bonus(), 1920 + (j % 2) * 420, 200 + ((j) / 2) * 700, 20, w, h, graphics); //gemmes bonus
    }
    
    /** Draw a star for the actual player of the game (the one who is playing right now). */
    private static void drawStarForActualPlayer(Game game, int w, int h, Graphics2D graphics) {
        int x, y;
        switch(game.getActualPlayer()) {
            case 0 -> {x = 2060; y = 30;}
            case 1 -> {x = 2480; y = 30;}
            case 2 -> {x = 2060; y = 735;}
            default -> {x = 2480; y = 735;}
        }
        image("/star.png", x(x,w), y(y,h), x(50,w), y(50,h), graphics);
    }
    
    /** Wait for the user to choose his action and return the chosen action. */
    public static int clicActionSelection(ApplicationContext context, int w, int h) {
		Point2D.Float location;
		for(;;) {
			location = clic(context);
        	if(verif(location, 17.0, 177.0, 504.0, 706.0, w, h))
        		return 1; /*choix 3 gems différentes*/
        	else if(verif(location, 203.0, 361.0, 505.0, 705.0, w, h))
        		return 2; /*choix 2 gems identiques*/
        	else if(verif(location, 19.0, 179.0, 729.0, 931.0, w, h))
        		return 3; /*acheter carte*/
        	else if(verif(location, 202.0, 361.0, 730.0, 932.0, w, h))
        		return 4; /*réserver carte*/
		}
    }
    
    /** Wait for the user to choose one token of the game to take (this is going to be used as many times
     * as we want, so for the player actions 1 and 2).
     * @param i The specific image for token selection is only drawed if i == 1. */
    public static String clicTokenSelection(ApplicationContext context, int w, int h, int i) {
		Point2D.Float location;
		if(i == 1)
			imageByContext("/fondAction1-2.PNG", 0, 0, w, h, context);
		for(;;) {
			location = clic(context);
        	if(verif(location, 15.0, 67.0, 1204.0, 1261.0, w, h))
        		return "white";
        	else if(verif(location, 95.0, 155.0, 1203.0, 1263.0, w, h))
        		return "blue";
        	else if(verif(location, 175.0, 234.0, 1205.0, 1260.0, w, h))
        		return "green";
        	else if(verif(location, 15.0, 69.0, 1287.0, 1339.0, w, h))
        		return "red";
        	else if(verif(location, 96.0, 152.0, 1287.0, 1338.0, w, h))
        		return "black";
		}
    }
    
    /** Wait for the user to choose a card of the game.
     * @param action Used to diferenciate between action 3(buying a card) and action 4(booking a card). */
    public static CoordoClicCard clicCardSelection(ApplicationContext context, int w, int h, Game game, int action) {
    	double pos_x, pos_y, x, y;
    	int level = 0, index = 0, i, j, decal_i = 0;
		Point2D.Float location;
		var c = new CoordoClicCard();
		imageByContext("/fondAction"+action+".PNG", 0, 0, w, h, context);
		
		for(;;) {
			pos_y = 0.0; y = 0.0; level = 0;
			location = clic(context);                   /*clic sur les cartes dev du terrain*/
			for(var lst_dev : game.dev_in_game()) {
				x = 0.0; pos_x = 0.0; index = 0;
				for(i=0; i<lst_dev.size(); i++) {
					if(verif(location, 410.0+pos_x+x, 710.0+pos_x+x, 45.0+pos_y+y, 445.0+pos_y+y, w, h)) {
						c.setTypeCard(0); c.setIndex(index); c.setLevel(level);
						return c;}
					x += 13.0; pos_x += 300.0; 
					index ++;
				}
				y += 75.0; pos_y += 400.0;
				level ++;
			}
			
			if(action != 4) {
				index = 0;                                                         /*clic sur les cartes dev réservées*/
				for(i=0; i<game.player().get(game.getActualPlayer()).deck().size(); i++) {
					j = game.getActualPlayer();
					if(i == 2) decal_i = 200;
					else decal_i = 0;
					if(verif(location, (1720 + ((i % 2) * 140)) + 420 * ((j) % 2),
							(1720 + ((i % 2) * 140)) + 420 * ((j) % 2) + 150,
							300 + ((j) / 2) * 700 + decal_i,
							300 + ((j) / 2) * 700 + decal_i + 200, w, h)) {
								c.setTypeCard(1); c.setIndex(index); c.setLevel(0);
								return c;}
					index ++;
				}
			}
		}
    }
    
    
    /** Wait for the user to choose if he wants to select a new action or redo his previous action when an error has
     * been detected when he wanted to do an action. */
    public static int clicReturnAction(ApplicationContext context, int w, int h) {
    	imageByContext("/fondRefus.PNG", 0, 0, w, h, context);
		Point2D.Float location;
		for(;;) {
			location = clic(context);
        	if(verif(location, 908.0, 1214.0, 571.0, 897.0, w, h))
        		return -1; /*changer action*/
        	else if(verif(location, 1339.0, 1644.0, 572.0, 897.0, w, h))
        		return 0; /*relancer action*/
		}
    }
}
