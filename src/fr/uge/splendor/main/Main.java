package fr.uge.splendor.main;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

import fr.uge.splendor.Game;
import fr.uge.splendor.Player;
import fr.uge.splendor.graph.Graph;
import fr.umlv.zen5.Application;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.ScreenInfo;



/** Main */
public class Main {
	/** Main
	 * @param args : argument*/
	public static void main(String[] args){	
        /* Initialisation du Game */
        int nb_player, version;
        Scanner scanner = new Scanner(System.in);
        var game = new Game();

        version = Player.AskInfo("Choisissez votre version du Game ? [0-3]", scanner, 0, 3);
        
        /* Initialisation des joueurs */
        System.out.println("\nBienvenue au Game du Splendor(v"+version+")!\n");
        
        nb_player = Game.SelectNbPlayer(scanner);
        scanner.nextLine();
        game.creationAllPlayers(scanner, nb_player);

        /* Initialisation des cartes */
        try {
        	if(version == 0)
        		game.initCard(Path.of("data/CartesPhase1.csv"));
        	else if(version == 1 || version == 2)
        		game.initCard(Path.of("data/Cartes.csv"));
        	else /*version >= 3*/
        		game.initCard(Path.of("data/CartesWithCities.csv"));
        } catch (IOException error) {
            System.err.println("File " + error.getMessage() + " does not exist in the data/ directory.");
            System.exit(1);
        }
        
        /* Initialisation des jetons */
        game.init_token_game();
        game.updateDevTerrain(version);
        
        if(version == 0 || version == 1)
        	begin_game(null, game, scanner, nb_player, version);
        else { /*version >= 2*/
        	Application.run(Color.BLACK, context -> {
        		begin_game(context, game, scanner, nb_player, version);
        		context.exit(1);
        	});
        }
	}
	
	
	/** Allow the game to begin. */
	private static void begin_game(ApplicationContext context, Game game, Scanner scanner, int nb_player, int version) {
        boolean ingame = true;
        ScreenInfo screeninfo; int width = 0, height = 0;
        game.setActualPlayer(0);
        if(version >= 2) {
            screeninfo = context.getScreenInfo();
            width = (int)screeninfo.getWidth();
            height = (int)screeninfo.getHeight();

            ingame = Graph.title(width, height, context);
        }
        
        /*le Game se lance*/
        while (ingame) {
            if(version >= 2)
                Graph.drawGame(game, width, height, context);
            
            System.err.println("\n\n--- AU TOUR DU JOUEUR " + (game.getActualPlayer()+1) + " ---\n");
            game.select_action(scanner, game.getActualPlayer(), version, context, width, height); /*actions du Game*/
            game.setActualPlayer(game.getActualPlayer()+1);
            if(game.getActualPlayer() == nb_player && game.win(version) != null) /*fin de partie*/
                ingame = false;
            if (game.getActualPlayer() == nb_player) /*passage au joueur suivant*/
                game.setActualPlayer(0);
        }
        scanner.close();
    }
}