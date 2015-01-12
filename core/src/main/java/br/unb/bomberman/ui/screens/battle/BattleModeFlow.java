package br.unb.bomberman.ui.screens.battle;


import br.unb.unbomber.BomberMatchWithUi;
import br.unb.unbomber.GDXGame;
import br.unb.unbomber.match.GameMatch;

import com.badlogic.gdx.Screen;

import ecs.common.match.Match;
import ecs.common.match.MatchFactory;
import ecs.common.match.MatchResultListener;
import ecs.common.match.TournamentController;
import ecs.common.match.TournamentController.BattleFlow;
import ecs.common.match.TournamentRules;

/**
 * A Screen delegator that actually manage potentially a sequence of 
 * many screens that implements a complete battle.
 * 
 * @author grodrigues
 *
 */
public class BattleModeFlow {

	private GDXGame game;
	
	private TournamentRules rules;
	
	private TournamentController controller;
	
	private static final int MATCH_TIME = 120 * 60 * 30;
	
	public BattleModeFlow(GDXGame game){
		this.game = game;
	}
	
	private void init() {
	
		rules = new TournamentRules();
		rules.setMatchTime(MATCH_TIME);
		rules.setWinsToChap(3);
		
		controller = new TournamentController(rules, null){

			@Override
			public void getScreen(BattleFlow actual) {
				getGameScreen(actual);
			}

		};
		// TODO rules.setContestants(contestants);
	}
	
	public void start() {
		init();
		controller.nextScreen();
	}
	
	private String getNextStageId(){
		return "stage";
	}
	
	/* Call the Bomberman Tournament Screens */

	public void getGameScreen(BattleFlow actual) {
		game.getScreen().dispose();
		
		Screen nextScreen;
		switch (actual) {
			case SET_CONTESTANTS:
			nextScreen = new ContestantsSelectScreen(game.batch, controller);
			break;
			
			case PLAY_THE_MATCH:
			GameMatch match = new BomberMatchWithUi(game.batch, getNextStageId(), controller);
			nextScreen = new GameScreen(game.batch , controller, match);
			break;

			case MATCH_RESULT:
			//TODO select if win or draw
			nextScreen = new WinScreen(game, controller);
			break;
			
			case TOURNAMENT_RESULT:
			//TODO create a tournament result
			nextScreen =  new WinScreen(game, controller);
			break;
			
			case MAIN_MENU:
			default:
			//TODO create a tournament result
			nextScreen =  game.mainMenuScreen;
		}
		game.setScreen(nextScreen);	
	}

}
