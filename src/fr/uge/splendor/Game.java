package fr.uge.splendor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

import fr.uge.splendor.graph.Graph;
import fr.umlv.zen5.ApplicationContext;

/** Used to represent the game */
public class Game {
	private ArrayList<Cards> noble;
	private ArrayList<Cards> developpement;
	private ArrayList<Cards> city;
	private ArrayList<Player> player;
	private ArrayList<Gem> gems;
	private ArrayList<ArrayList<Cards>> dev_in_game;
	private int actual_player;
	
	/** The game */
	public Game() {
		var liste_nob = new ArrayList<Cards>();
		this.noble = liste_nob;
		var liste_dev = new ArrayList<Cards>();
		this.developpement = liste_dev;
		var liste_city = new ArrayList<Cards>();
		this.city = liste_city;
		var list_joueur = new ArrayList<Player>();
		this.player = list_joueur;
		var liste_gems = new ArrayList<Gem>();
		this.gems = liste_gems;
		var dev1 = new ArrayList<Cards>();
		var dev2 = new ArrayList<Cards>();
		var dev3 = new ArrayList<Cards>();
		var list_dev_in_game = new ArrayList<ArrayList<Cards>>();
		list_dev_in_game.add(dev1);
		list_dev_in_game.add(dev2);
		list_dev_in_game.add(dev3);
		this.dev_in_game = list_dev_in_game;
		int actual_player = 0;
        this.setActualPlayer(actual_player);
		
	}

	/** @return a list gem */
	public ArrayList<Gem> gems() {
		return gems;
	}

	public ArrayList<Player> player() {
		return player;
	}
	
	/** @return a list of card dev */
	public ArrayList<ArrayList<Cards>> dev_in_game() {
		return dev_in_game;
	}
	
	/** @return a list of city cards */
	public ArrayList<Cards> city() {
		return city;
	}

	
	public int getActualPlayer() {
        return actual_player;
    }

    public void setActualPlayer(int actual_player) {
        this.actual_player = actual_player;
    }
    
	@Override
	public String toString() {
		var s = new StringBuilder();
		for (var player : player)
			s.append(player.toString()).append("\n");

		s.append("-----Pioche Cards Dev-----\n");
		for (var dev : developpement)
			s.append(dev.toString()).append("\n");

		s.append("-----Cards Dev Terrain-----\n");
		for (var dev : dev_in_game)
			s.append(dev.toString()).append("\n");

		s.append("-----gems Plateau-----\n");
		for (var dev : gems)
			s.append(dev.toString()).append("\n");

		return s.toString();
	}

	/** Create the list of gems for the game. */
	public void init_token_game() {
		int n;
		if (player.size() == 2)
			n = 4;
		else if (player.size() == 3)
			n = 5;
		else
			n = 6;

		gems = Gem.createGemList(n, n, n, n, n, 5);
	}

	/**
	 * Create the list of Dev and Nobles cards in the game with the informations
	 * located in the file data/Cards.csv.
	 * 
	 * @throws IOException :an exception if the wanted file does not exist
	 * @param p : the path for file which countain all information
	 */
	public void initCard(Path p) throws IOException {
		Objects.requireNonNull(p);
		int li = 0;
		try (var reader = Files.newBufferedReader(p)) {
			String s;
			while ((s = reader.readLine()) != null) {
				if (li >= 2 && li <= 91) /* Cards de dev entre les lignes 2 et 91 */
					developpement.add(Cards.fromText(s));
				if (li > 91 && li <= 101) /* Cards nobles entre les lignes 92 et 101 */
					noble.add(Cards.fromText(s));
				if (li > 101) /* Cards city à partir de la ligne 102 */
					city.add(Cards.fromText(s));
				li++;
			}
			if(!city.isEmpty()) /*ne garder que 3 cités parmis les 7*/
				cleanCityList();
		}
	}
	
	/**
	 * Used to only keep 3 cities in the city list of the game. */
	private void cleanCityList() {
		int min = 0, max = 7, r, i;
		for(i = 0; i < 7; i++) {
			if(city.size() <= 3) return;
			r = getRandomInt(min, max);
			city.remove(r);
			max --;
		}
	}

	/**
	 * @param min : minimum of the random value
	 * @param max : maximum of the random value
	 * @Return int a random integer between min and max.
	 */
	public static int getRandomInt(int min, int max) {
		Objects.requireNonNull(min);
		Objects.requireNonNull(max);
		if ((max - 1) < min)
			throw new IllegalArgumentException("Error: max value is less than min value.");
		Random r = new Random();
		return r.nextInt(((max - 1) - min) + 1) + min;
	}

	/** Draw some Dev cards until the game is full.
     * @param version : the version of the game, 0 if we only want Dev cards from Level 1 
     * and 1 if we want all Dev cards from all Levels. */
	public void updateDevTerrain(int version) {
        int i, res;
        int endNiv1 = 4 - dev_in_game.get(0).size();
        int endNiv2 = 4 - dev_in_game.get(1).size();
        int endNiv3 = 4 - dev_in_game.get(2).size();
        for(i=0; i<endNiv1; i++) { /*Dev niveau 1*/
            res = getRandomInt(0,
                    Cards.getSizeByLevel(developpement, 1));
            dev_in_game.get(0).add(developpement.get(res));
            developpement.remove(res);
        }
        if(version >= 1) {
            for(i=0; i<endNiv2; i++) { /*Dev niveau 2*/
                res = getRandomInt(Cards.getSizeByLevel(developpement, 1),
                        Cards.getSizeByLevel(developpement, 1) + Cards.getSizeByLevel(developpement, 2));
                dev_in_game.get(1).add(developpement.get(res));
                developpement.remove(res);
            }
            for(i=0; i<endNiv3; i++) { /*Dev niveau 3*/
                res = getRandomInt(Cards.getSizeByLevel(developpement, 1) + Cards.getSizeByLevel(developpement, 2),
                        Cards.getSizeByLevel(developpement, 1) + Cards.getSizeByLevel(developpement, 2) + Cards.getSizeByLevel(developpement, 3));
                dev_in_game.get(2).add(developpement.get(res));
                developpement.remove(res);
            }
        }
    }


	/**
	 * Use to select the number of players in the game.
	 * 
	 * @param scanner : this will take the number of players.
	 * @return int : the number of players.
	 */
	public static int SelectNbPlayer(Scanner scanner) {
		return Player.AskInfo("Veuillez choisir le nombre de joueurs. [2-4]", scanner, 2, 4);
	}

	/**
	 * Create one player.
	 * 
	 * @param scanner this will take the name of the player.
	 * @param num     : number of the player.
	 * @return Joueur : the player that has been created.
	 */
	public Player newJoueur(Scanner scanner, int num) {
		Objects.requireNonNull(scanner);
		Objects.requireNonNull(num);
		String str_player;
		do {
			System.out.println("Joueur " + num + ", quel est votre prénom ?");
			str_player = scanner.nextLine();
		} while (str_player.equals(""));
		return new Player(str_player, num, 0, Gem.createGemList(0, 0, 0, 0, 0, 0),
				Gem.createGemList(0, 0, 0, 0, 0, 0), new ArrayList<Cards>());
	}

	/**
     * Create all the players.
     * 
     * @param scanner : take the name of the player
     * @param nb      : number of players in the game.
     */
    public void creationAllPlayers(Scanner scanner, int nb) {
        Objects.requireNonNull(scanner);
        int i;
        for (i = 1; i < nb + 1; i++)
            player.add(newJoueur(scanner, i));
    }

    /**
     * Used to print the Dev cards of the game, so we will print 4 cards of each
     * levels of the deck.
     * 
     * @param dev the list that contains all 3 lists of the Dev cards (3 levels)
     */
    private static String draw_dev_in_game(ArrayList<ArrayList<Cards>> dev) {
        Objects.requireNonNull(dev);
        var s = new StringBuilder();
        for (var level : dev) {
            for (var card : level)
                s.append(card).append("- ");
            s.append("\n\n");
        }
        return s.toString();
    }
    
    public int GameIa(int num_player,Scanner scanner,int version, ApplicationContext context, int w, int h) {
        if(player.get(num_player).booking(scanner, this, version, context, w, h) == 0) {
            if(player.get(num_player).selectCard(scanner, this, version, context, w, h) == 0) {
                if(player.get(num_player).nbtokensSelectPlayer() >= 2 &&player.get(num_player).choice_2_gem(scanner, this, gems, version, context, w, h) ==  0) {
                    player.get(num_player).dif_choice_gem(scanner, this, gems, version, context, w, h);
                    return 1;
                }return 1;
                
            }return 1;
        }
        return 1;
    }
    
    public int GameHuman(int player_selection,int num_player,Scanner scanner,int version, ApplicationContext context, int w, int h) {
    	return switch (player_selection) {
		case 1 ->  player.get(num_player).dif_choice_gem(scanner, this, gems, version, context, w, h);
		case 2 ->  player.get(num_player).choice_2_gem(scanner, this, gems, version, context, w, h);
		case 3 ->  player.get(num_player).selectCard(scanner, this, version, context, w, h);
		case 4 ->  player.get(num_player).booking(scanner, this, version, context, w, h);
		default -> 0;
		};
    }

    public int Selection_player(int version,int num_player, Scanner scanner, ApplicationContext context, int h, int w) {
    	int player_selection = 1;
    	if(version == 0)
        	player_selection = Player.AskInfo("\n", scanner, 0, 3);
        if(version == 1)
        	player_selection = Player.AskInfo("\n", scanner, 0, 4);
        else { /*version >= 2*/
        	Graph.drawGame(this, w, h, context);
			if(!player.get(num_player).name().equals("robot")) {
				player_selection = Graph.clicActionSelection(context, w, h);
    		}
        }
		System.out.println("Choix:" + player_selection);
        return player_selection;
    }
    
	/**
	 * The player can choose his action between 4 possibilities.
	 * 
	 * @param scanner    : take the value on the terminal
	 * @param version : version == 0 for the version phase 1 of the game and version == 1 for the fullgame.
	 * @param num_joueur number of the actual player
	 */
	public void select_action(Scanner scanner, int num_player, int version, ApplicationContext context, int w, int h) {
		Objects.requireNonNull(scanner);
		Objects.requireNonNull(num_player);
		int good;

		System.out.println(player.get(num_player));
		System.out.println(gems);
		System.out.println(draw_dev_in_game(dev_in_game));

		do { /* actions du jeu */
			System.out.println("Veuillez choisir une action en saisissant le numéro de l'action:\n");
            System.out.println("\t1) Pour prendre 3 jetons pierre précieuse de couleur différente.\n");
            System.out.println("\t2) Prendre 2 jetons pierre précieuse de la même couleur.\n");
            System.out.printf("\t3) Acheter 1 carte Développement");
            if (version >= 1)
                System.out.println(
                        " ou préalablement réservée.\n\n\t4) Réserver 1 carte Développement et prendre 1 or (joker).");
            System.out.println("\n");
            
			if(player.get(num_player).name().equals("robot") && version >= 2)
				good= GameIa(num_player, scanner, version, context, w, h);
			else
				good= GameHuman(Selection_player(version, num_player, scanner, context, h, w), num_player, scanner, version, context, w, h);
			
		} while (good == 0);
		if (version == 1 || version == 2)
			player.get(num_player).visitnoble(noble); /* passage d'un noble si possible, sinon rien */
		System.out.println(player.get(num_player)); /* affichage du joueur à la fin de son tour */
	}

	/** @return Joueur the winner of the game or null if no one has won yet. */
	public Player win(int version) {
		var s = new StringBuilder();
		var lst = new ArrayList<Player>();
		for (var p : player) {
			if (version <= 2 && p.hasWon())
				lst.add(p);
			if (version == 3 && p.hasWonCity(city))
				lst.add(p);
		}
		if(lst.size() != 0) {
			drawForWin(lst);
			for(var p : lst)
				s.append(p.name() + " ");
			System.err.println(s + "wins !");
			return lst.get(0);
		}
		return null;
	}
	
	/** We can use this method to remove some players from the list of winning players when there is multiple winners.
	 * @param lst : list of winning players which is going to be modified */
	private static void drawForWin(List<Player> lst){
		int nPlayer = 0, max;
		var lstNbGemBonusForPlayers = new ArrayList<Integer>();
		for(var p : lst) {
			lstNbGemBonusForPlayers.add(0);
			for(var gem : p.bonus()) {
				lstNbGemBonusForPlayers.set(nPlayer, lstNbGemBonusForPlayers.get(nPlayer) + gem.nbr());
			}
			nPlayer++;
		}
		nPlayer = 0;
		max = Collections.max(lstNbGemBonusForPlayers);
		for(var i = 0; i<lst.size(); i++) {
			if(lstNbGemBonusForPlayers.get(nPlayer) == max)
				nPlayer++;
			else {
				lst.remove(nPlayer);
				lstNbGemBonusForPlayers.remove(nPlayer);}
		}
	}
}