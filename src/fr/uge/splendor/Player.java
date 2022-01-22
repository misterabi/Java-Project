package fr.uge.splendor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import fr.uge.splendor.graph.CoordoClicCard;
import fr.uge.splendor.graph.Graph;
import fr.uge.splendor.robot.Robot;
import fr.umlv.zen5.ApplicationContext;

/** Used to represent the players */
public class Player {
	private final String name;
	private final int num;
	private int prestige;
	private int IsRobot;
	private List<Gem> tokens, bonus;
	private List<Cards> deck;

	/**
	 * @param name     : name of the player
	 * @param num      :number of the player
	 * @param prestige : amount of point
	 * @param tokens   : token of the player
	 * @param bonus    : token gain by buying cards
	 * @param deck     : card which have been save
	 */
	public Player(String name, int num, int prestige, List<Gem> tokens, List<Gem> bonus,
			ArrayList<Cards> deck) {
		Objects.requireNonNull(name, "Player Name is not present");
		Objects.requireNonNull(num, "Player num is not present");
		Objects.requireNonNull(tokens, "Gem List is undefined");
		Objects.requireNonNull(bonus, "Bonus List is undefined");
		Objects.requireNonNull(prestige, "Prestige is not present");
		Objects.requireNonNull(deck, "Reservation deck is undefined");
		if (num < 0) {
			throw new IllegalAccessError("Player num is not authorized");
		}
		this.num = num;
		if (name.equals("robot")) {
			this.IsRobot = 1;
		} else {
			this.IsRobot = 0;
		}
		this.name = name;
		this.prestige = prestige;
		this.tokens = tokens;
		this.bonus = bonus;
		this.deck = deck;
	}

	/** @return String the name of the player */
	public String name() {
		return name;
	}

	public List<Cards> deck() {
		return deck;
	}

	public List<Gem> bonus() {
		return bonus;
	}

	public List<Gem> tokens() {
		return tokens;
	}

	public int numPlayer() {
		return num;
	}

	public int prestige() {
		return prestige;
	}

	@Override
	public String toString() {
		var res = new StringBuilder();
		res.append("------------ Joueur ").append(num).append(" ------------\nPrénom: ");
		res.append(name).append("\nPoints prestige=").append(prestige).append("\ntokens: ");

		for (var e : tokens)
			res.append(e).append(" ");

		res.append("\nBonus: ");
		for (var e : bonus)
			res.append(e).append(" ");

		if (deck.size() != 0) {
			res.append("\nCards réservées: ");
			for (var e : deck)
				res.append(e).append(" ");
		}

		return res.append("\n").toString();
	}

	/**
	 * Reserve a Dev card of the game of the wanted level in the wanted position.
	 * The position is the number of the wanted card in the game.
	 */
	private int bookingCard	(int level, int position, Game game) {
		Objects.requireNonNull(level);
		Objects.requireNonNull(position);
		Objects.requireNonNull(game);
		if (deck.size() < 3) {
			deck.add(game.dev_in_game().get(level).get(position));
			game.dev_in_game().get(level).remove(position);
			game.updateDevTerrain(1);
			if (game.gems().get(5).nbr() > 0) { /* ajout d'un jeton or si possible */
				tokens.get(5).add(1);
				game.gems().get(5).remove(1);
			}
			return 1;
		} else {
			System.err.println("Impossible.\nVous avez déjà réservé 3 Cards Dev.");
			return 0;
		}
	}

	/**
	 * Verify if the wanted gems are effectivly in the game and if we can effectivly
	 * get them. This function will be used in a for() loop in which she will verify
	 * this fact for all the gems in the game.
	 * 
	 * @param i    index of the list corresponding to a single gem (the one that is
	 *             wanted)
	 * @param gems the list of the gems in the game
	 * @return 1 if we can get this gem or 0 otherwise
	 */
	private int checking_token_in_Lst(String[] lst_gem_input, int i, List<Gem> gems) {
		Objects.requireNonNull(lst_gem_input);
		Objects.requireNonNull(i);
		Objects.requireNonNull(gems);
		for (var elem : gems) {
			if (lst_gem_input[i].equals("yellow")) {
				System.err.println("Impossible de prendre un jeton jaune.\n");
				return 0;
			}
			if (elem.color().equals(lst_gem_input[i]))
				return 1;
		}
		System.err.println("Impossible de prendre ce jeton car il n'existe pas.\n");
		return 0;
	}

	/**
	 * Verify if the wanted gems have differents colors (used for the choice 1:
	 * choose 3 differents gems).
	 * 
	 * @param nb_jeton_choisis the number of wanted gems which is depending on the
	 *                         number of players
	 * @param gems             list of all gems in the game
	 * @return 1 si the wanted gems have differents colors or 0 otherwise
	 */
	private int checking_color(String[] lst_gem_input, int nb_token_select, List<Gem> gems) {
		Objects.requireNonNull(lst_gem_input);
		Objects.requireNonNull(nb_token_select);
		Objects.requireNonNull(gems);
		int i;
		for (i = 0; i < nb_token_select; i++) {
			if (checking_token_in_Lst(lst_gem_input, i, gems) == 0)
				return 0;

			if (i == 2) { /* verification que le premier et le dernier sont different */
				if (lst_gem_input[0].equals(lst_gem_input[2])) {
					System.err.println("Impossible de prendre un jeton ,vous avez choisis 2 tokens identiques\n");
					return 0;
				}
			} else if (nb_token_select >= 2 && i < 1) {
				if (lst_gem_input[i].equals(lst_gem_input[i + 1])) {
					System.err.println("Impossible de prendre un jeton ,vous avez choisis 2 tokens identiques\n");
					return 0;
				}
			}
		}
		if (nb_token_select == 0)
			return 0;
		return 1;
	}

	/**
	 * Verify if the game has enough gems of the wanted color at the wanted
	 * quantity.
	 * 
	 * @param gems the list of all gems in the game
	 * @return 0 if the game dosn't have ennough gems or 1 if he has
	 */
	private int verificationQuantity(String color, int min_quantity, List<Gem> gems) {
		Objects.requireNonNull(color);
		Objects.requireNonNull(min_quantity);
		Objects.requireNonNull(gems);
		for (var elem : gems) {
			if (elem.nbr() <= min_quantity && elem.color().equals(color)) {
				System.err.println("Il n'y a pas assez de gems, veuillez en choisir une autre.\n");
				return 0;
			}
		}
		return 1;
	}

	/** Return the total number of gems of the player. */
	private int totaltokens() {
		int nb_tokens = 0;
		for (var elem : tokens) {
			if(!elem.color().equals("yellow"))
				nb_tokens += elem.nbr();
		}
		return nb_tokens;
	}

	/** @Return int the total amount of gems that a player can get in the game. */
	public int nbtokensSelectPlayer() {
		int nb_token = totaltokens();
		return switch (nb_token) {
		case 10 -> 0;
		case 9 -> 1;
		case 8 -> 2;
		default -> 3;
		};
	}

	/* Return the number of gem he can choose */
	private int nbTokenSelectGame(Game game) {
		int i, tok = 0;
		for (i = 0; i < 5; i++) {
			if (game.gems().get(i).nbr() > 0)
				tok++;
		}
		return tok;
	}

	private int nbTokenSelect(int nbTokenPlayerSelect, int nbTokenGameSelect) {
		if (nbTokenGameSelect == 0 || nbTokenPlayerSelect == 0) {
			System.out.println(
					"Il n'y a pas assez de gem sur le plateau veuillez choisir une autre action ou vous avez trop de gemmes\n");
			return 0;
		} else if (nbTokenGameSelect > 0 && nbTokenPlayerSelect == 1) {
			return 1;
		} else if (nbTokenGameSelect > 1 && nbTokenPlayerSelect == 2) {
			return 2;
		} else {
			return 3;
		}
	}

	/**
	 * The player is getting 3 different gems in the game.
	 * 
	 * @param scanner : get the input
	 * @param gems    list of all gems in the game
	 * @return 1 if the player has been able to take his wanted gems or 0 otherwise
	 */
	public int dif_choice_gem(Scanner scanner, Game game, List<Gem> gems, int version, ApplicationContext context,
			int w, int h) {
		Objects.requireNonNull(scanner);
		Objects.requireNonNull(gems);
		String color_gem = "", input_str;
		String[] lst_gem_input = null;
		int good = 0, i;
		int nb_token_select = nbTokenSelect(nbtokensSelectPlayer(), nbTokenSelectGame(game));

		do { /* Verifiaction de la saisie du joueur */

			if (nb_token_select < 0)
				System.out.println(
						"Vous pouvez pas faire cette action par manque de moyen\n");
			else{
				System.out.println(
						"Rentrer " + nb_token_select + " gems de couleurs différentes (saisir une à la fois):\n");
				for (i = 0; i < nb_token_select; i++) {
			
					if (version == 0 || version == 1) {
						do {
							input_str = scanner.nextLine();
	
						} while (input_str.equals(""));
					} else { /* version >= 2 */
						if (i + 1 == 1)
							Graph.drawGame(game, w, h, context);
						if (IsRobot == 1) {
							input_str = Robot.Color_random(game, 1, color_gem);
						} else
							input_str = Graph.clicTokenSelection(context, w, h, i + 1);
					}
					color_gem = color_gem + input_str + " ";
				}
				lst_gem_input = color_gem.split(" ");
				good = checking_color(lst_gem_input, nb_token_select, gems);
				/* verification qu'il n'y a pas 2 couleurs identiques */
				if (good == 1) {
					for (var gem : lst_gem_input) {
						good = verificationQuantity(gem, 0, gems);
						if (good == 0)
							break;
					}
	
				}
			}
				if ((IsRobot == 1 && Robot.containNullInLst(lst_gem_input) && nb_token_select == 0)
					|| (( nb_token_select == 0 ||good == 0) && returnAction(scanner, version, context, w, h) == 0))
				return 0;
		} while (good == 0);
		add_diff_gem(nb_token_select, gems, lst_gem_input);
		return 1;
	}

	/*
	 * add 3 different gem to the player
	 * 
	 * @param
	 */
	public void add_diff_gem(int nb_gem_select, List<Gem> gems, String[] lst_gem_input) {
		int i;
		if (nb_gem_select > 0) {
			int pos_color = 0;

			/* ajout des gems au joueur */
			for (i = 0; i < nb_gem_select; i++) {
				for (var elem : tokens) {
					if (elem.color().equals(lst_gem_input[pos_color])) {
						elem.add(1);
						break;
					}
				}
				for (var elem : gems) {
					if (elem.color().equals(lst_gem_input[pos_color])) {
						elem.remove(1);
						pos_color++;
						break;
					}
				}
			}
		}
	}

	/*
	 * Add 2 gem to the player
	 * 
	 * @param lst_gem_input a list of gem (the gem the user ask
	 * 
	 * @param gems list of gem of the game
	 */
	public void add_2_gem(String[] lst_gem_input, List<Gem> gems) {
		/* ajout des gems au joueur */
		for (var elem : tokens) {
			if (elem.color().equals(lst_gem_input[0])) {
				elem.add(2);
				break;
			}
		}
		for (var elem : gems) {
			if (elem.color().equals(lst_gem_input[0])) {
				elem.remove(2);
				break;
			}
		}
	}

	/* Ask 2 gem to the player */
	public String ask_2_gem(int version, Scanner scanner, int nbTokenPlayer, Game game, int w, int h,
			ApplicationContext context) {
		String input_str = "";
		if ((version == 0 || version == 1) && nbTokenPlayer >= 2) {
			do {
				input_str = scanner.nextLine();
			} while (input_str.equals(""));
		} else if (version >= 2) { /* version >= 2 */

			Graph.drawGame(game, w, h, context);
			if (IsRobot == 1 && nbTokenPlayer >= 2)
				input_str = Robot.Color_random(game, 4, "");
			else if (nbTokenPlayer >= 2)
				input_str = Graph.clicTokenSelection(context, w, h, 1);
		}
		return input_str;
	}

	/**
	 * The player is getting 2 exact same gems in the game.
	 * 
	 * @param scanner : get the input
	 * @param gems    list of all gems in the game
	 * @return 1 if the player has been able to take his wanted gems or 0 otherwise
	 */
	public int choice_2_gem(Scanner scanner, Game game, List<Gem> gems, int version, ApplicationContext context,
			int w, int h) {
		String color_gem, input_str = "";
		String[] lst_gem_input;
		int good = 0, nbTokenPlayer = nbtokensSelectPlayer();
		do {
			color_gem = "";
			if (nbTokenPlayer >= 2) {
				System.out.println("Choisissez une gemme de couleur à récupérer (2 seront prises):\n");
			} else
				System.out.println("Vous avez trop de gemmes, veuillez choisir une autre action\n");
			input_str = ask_2_gem(version, scanner, nbTokenPlayer, game, w, h, context);
			color_gem = color_gem + input_str + " ";
			lst_gem_input = color_gem.split(" ");
			if (nbTokenPlayer >= 2)
				good = checking_token_in_Lst(lst_gem_input, 0, gems);
			if (good == 1)
				good = verificationQuantity(lst_gem_input[0], 3, gems);
			if (nbTokenPlayer < 2)
				good = 0;
			if ((IsRobot == 1 && input_str == "null") || good == 0 && returnAction(scanner, version, context, w,
					h) == 0) /* Verification que l'action du joueur fonctionne */
				return 0;
		} while (good == 0);
		add_2_gem(lst_gem_input, gems);
		return 1;
	}

	/**
	 * Return an integer written by the user.
	 * 
	 * @param string  the text that will be written for asking the question to the
	 *                user
	 * @param scanner : get the input
	 * @param min     :the min required
	 * @param max     :the max required
	 * @return int :This integer has to be between min and max parameter
	 */
	public static int AskInfo(String string, Scanner scanner, int min, int max) {
		Objects.requireNonNull(string);
		Objects.requireNonNull(scanner);
		Objects.requireNonNull(min);
		Objects.requireNonNull(max);
		int i;
		do {
			System.out.println(string);
			i = scanner.nextInt();
		} while (i < min || i > max);
		return i;
	}

	public int booking(Scanner scanner, Game game, int version, ApplicationContext context,int w, int h) {
		String sentence2 = "Veuillez saisir le niveau de la carte que vous souhaitez ";
		String sentence3 = "Veuillez saisir l'indice de la carte que vous souhaitez ";
		int good=0;
		/* réservation d'une carte du terrain */
		if (version == 0 || version == 1)
			good=  bookingCard(AskInfo(sentence2 + "reservez \n", scanner, 0, 2),
					AskInfo(sentence3 + "reservez \n", scanner, 0, 3), game);
		else { /* version >= 2 */
			CoordoClicCard c;
			if (IsRobot == 0)
				c = Graph.clicCardSelection(context, w, h, game, 4);
			else
				c = selectCardRobot(2, 4);
			good = bookingCard(c.getLevel(), c.getIndex(), game);
		}					
		return good;
	}
	
	private CoordoClicCard SelectCardRobot(Game game) {
        var c = new CoordoClicCard();int nb_gem_gold ;
        for(var i =0;i<deck.size();i++) {                    /* Try to buy a booking card*/
            nb_gem_gold = tokens.get(5).nbr();
            if(CanBuyCard(i, game, deck, nb_gem_gold)) {
                c.setTypeCard(1);
                c.setIndex(i);
                c.setLevel(0);
                return c;
            }}
        for(var i = 2 ; i >= 0;i--) {
            for(var j = 0;j < 4; j++) {
                nb_gem_gold = tokens.get(5).nbr();
                if(CanBuyCard(j, game, game.dev_in_game().get(i), nb_gem_gold)) {
                    c.setTypeCard(0);
                    c.setIndex(j);
                    c.setLevel(i);
                    return c;
                }
            }
        }
        c.setLevel(-1);   /*impossible to buy*/
        return c;
    }
	
	
	/**
	 * The player is choosing a card in the game to buy it or the reserve it.
	 * 
	 * @param Game              : all information about the game
	 * @param scanner           : get the input
	 * @param choix_reservation == 1 if the player wants to reserve the card or 0
	 *                          otherwise
	 * @param version           : version == 0 for the version phase 1 of the game
	 *                          and version == 1 for the fullgame.
	 * @return 1 if the chosen card is present in the game or 0 otherwise
	 */
	public int selectCard(Scanner scanner, Game game, int version, ApplicationContext context,
			int w, int h) {
		Objects.requireNonNull(scanner);
		Objects.requireNonNull(game);
		String sentence1 = "Veuillez saisir 1 pour acheter une carte reservée ou 0 pour acheter une carte de Dev.\n";
		String sentence2 = "Veuillez saisir le niveau de la carte que vous souhaitez ";
		String sentence3 = "Veuillez saisir l'indice de la carte que vous souhaitez ";
		int good = 0;
		CoordoClicCard c;
		while (good == 0) {
			if (version >= 2)
				Graph.drawGame(game, w, h, context);

			if (version >= 2) {
				if(name.equals("robot")) {
					c = SelectCardRobot(game);
					if(c.getLevel()==-1)
						return 0;
				}else {
					c = Graph.clicCardSelection(context, w, h, game, 3);	
				}
				if (c.getTypeCard() == 0) /* achat d'une carte sur le terrain */
					good = buyCard(c.getIndex(), game, game.dev_in_game().get(c.getLevel()), version);
				else /* c.getTypeCard() == 1 */
					/* achat d'une carte réservée */
					good = buyCard(c.getIndex(), game, deck, version);
				
			} else if (version == 1 && AskInfo(sentence1, scanner, 0, 1) == 1)
				/* achat d'une carte réservée */
				good = buyCard(AskInfo(sentence3 + "acheter: \n", scanner, 0, 4), game, deck, version);
			else {
				/* achat d'une carte sur le terrain */
				if (version == 1)
					good = buyCard(AskInfo(sentence3 + "acheter: \n", scanner, 0, 3), game,
							game.dev_in_game().get(AskInfo(sentence2 + "acheter: \n", scanner, 0, 2)), version);
				else /*version == 0*/
					good = buyCard(AskInfo(sentence3 + "acheter: \n", scanner, 0, 3), game, game.dev_in_game().get(0),
							version);
			}
			if (good == 0 && returnAction(scanner, version, context, w, h) == 0)
				return 0;
		}
		return 1;
	}

	/* get a random card of the game */
	private CoordoClicCard selectCardRobot(int maxLevel, int maxIndex) {
		CoordoClicCard c = new CoordoClicCard();
		c.setLevel(Game.getRandomInt(0, maxLevel));
		c.setIndex(Game.getRandomInt(0, maxIndex));
		return c;
	}

	/**
	 * When the player wants to buy a card but when he dosn't have ennough gems to
	 * do so, we use this function to ask him if he wants to modify his choice or
	 * not.
	 * 
	 * @param scanner :get the input
	 * @return 0 if the player wants to modify his action or 1 if he wants to redo
	 *         his previous action
	 */
	private int returnAction(Scanner scanner, int version, ApplicationContext context, int w, int h) {
		Objects.requireNonNull(scanner);
		int i;
		System.err.println("Votre action a été refusée par manque de moyen.\n");
		System.out.println("Si vous souhaitez changer votre action, saisissez le numero -1.");
		System.out.println("Sinon, écrivez un autre nombre pour relancer votre action précédente:\n");
		if(IsRobot == 1)
			return 0;
		if (version == 0 || version == 1)
			i = scanner.nextInt();
		else /* version >= 2 */
			i = Graph.clicReturnAction(context, w, h);
		if (i == -1)
			return 0;
		return 1;
	}
	
	private Boolean CanBuyCard( int index_card,Game game, List<Cards> list,int nb_gem_gold) {
		/* Verifiaction que le joueur possède assez de tokens (avec les tokens bonus) */
		int index = 0;
		for (Gem elem : list.get(index_card).price()) {
			if (elem.nbr() <= bonus.get(index).nbr() + tokens.get(index).nbr() + nb_gem_gold) {
				if((elem.nbr() - (bonus.get(index).nbr() + tokens.get(index).nbr()))>0)
						nb_gem_gold -=(elem.nbr() - (bonus.get(index).nbr() + tokens.get(index).nbr()));
			} else {
				System.err.println("Vous n'avez pas assez de tokens pour l'achat de cette carte!\n");
				return false;
			}
			index++;
		}
		return true;
	}

	/**
	 * The player is buying a card in the game (or a card that he has reserve
	 * before).
	 * 
	 * @param index_card index of the chosen card in the list
	 * @param list       the list of the concern Dev cards, it can be the Dev cards
	 *                   in the game or the Dev cards that the player has reserve
	 * @param version
	 * @return 1 if the player can buy the chosen card or 0 if he can't
	 */
	private int buyCard(int index_card, Game game, List<Cards> list, int version) {
		Objects.requireNonNull(index_card);
		Objects.requireNonNull(game);
		Objects.requireNonNull(list);
		int index = 0;
		int nb_gem_gold = tokens.get(5).nbr();

		if (list.size() > 0) {
			if(!CanBuyCard(index_card, game, list, nb_gem_gold)) {
				return 0;
			}

			index = 0;
			for (Gem elem : list.get(index_card).price()) {
				if (tokens.get(index).color().equals(elem.color())) {

					/* achat qu'avec les tokens bonus */
					if (bonus.get(index).nbr() >= elem.nbr())
						elem.remove(bonus.get(index).nbr());
					/* achat avec les tokens classiques et les tokens bonus */
					else if (bonus.get(index).nbr() + tokens.get(index).nbr() >= elem.nbr()) {
						elem.remove(bonus.get(index).nbr());
						tokens.get(index).remove(elem.nbr());
						game.gems().get(index).add(elem.nbr());
					} else {
						/* Achat avec les gems jaune et le reste */
						elem.remove(bonus.get(index).nbr());
						elem.remove(tokens.get(index).nbr());

						/* ajout des tokens dans le plateau */
						game.gems().get(index).add(tokens.get(index).nbr());
						tokens.get(index).affectation(0);
						tokens.get(5).remove((elem.nbr()));
						game.gems().get(5).add(elem.nbr());
					}
				}
				index++;
			}

			/* ajout des tokens bonus après l'achat d'une carte */
			for (Gem elem : bonus) {
				if (elem.color().equals(list.get(index_card).bonus().color()))
					elem.add(list.get(index_card).bonus().nbr());
			}

			/* mise à jour du prestige, du deck, et des Cards */
			prestige += list.get(index_card).getPrestige();
			list.remove(index_card);
			game.updateDevTerrain(version);
			return 1;
		}
		return 0;
	}

	/**
	 * Verify if a noble can visit a player and if it's the case he will give him
	 * some Prestige points.
	 * 
	 * @param Nob the list of all Nobles in the game
	 */
	public void visitnoble(List<Cards> Nob) {
		Objects.requireNonNull(Nob);
		int n = 0;
		for (var noble : Nob) {
			if (noble.canVisit(bonus)) {
				System.out.println("Oh! Vous avez reçu la visite d'un Noble.\n");
				prestige += noble.getPrestige();
				Nob.remove(n);
				break;
			}
			n++;
		}
	}

	/** @return boolean if the player has won the game. */
	public boolean hasWon() {
		if (prestige >= 15)
			return true;
		return false;
	}
	
	/** @return boolean if the player has won the game. (used in the City extension, version == 3) */
	public boolean hasWonCity(List<Cards> city) {
		for(var c : city) {
			if(prestige >= c.getPrestige() && c.canVisit(bonus)) {
				System.out.println("Vous venez d'acquérir la ville ("+ c.getName() +").\n");
				return true;
			}
		}
		return false;
	}
}
