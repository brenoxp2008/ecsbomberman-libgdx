package br.unb.unbomber;

import br.unb.unbomber.match.TargetFrameRateMatch;
import br.unb.unbomber.systems.AudioSystem;
import br.unb.unbomber.systems.GridSystem;
import br.unb.unbomber.systems.HUDSystem;
import br.unb.unbomber.systems.LoadStageSystem;
import br.unb.unbomber.systems.LoadTextureSystem;
import br.unb.unbomber.systems.PlayerControlSystem;
import br.unb.unbomber.systems.RenderSystem;
import br.unb.unbomber.systems.ScreenPositionSystem;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import ecs.common.match.MatchResult;
import ecs.common.match.MatchResultListener;
import ecs.common.match.MatchSystem;
import ecs.common.match.TournamentController;

/**
 * Bomber match with Libgdx User Interface
 * 
 * @author grodrigues
 *
 */
public class BomberMatchWithUi extends TargetFrameRateMatch{
	final static int DEFAUT_FRAME_RATE = 60;
	

	public int finishingCountDown = DEFAUT_FRAME_RATE;
	
	public int score;
	
	final private String stageId;
	
	final private SpriteBatch batch;
	
	final private TournamentController controller;
	
	public BomberMatchWithUi( SpriteBatch batch, String stageId, TournamentController controller) {
		super(DEFAUT_FRAME_RATE);
		this.stageId = stageId;
		this.batch = batch;
		this.controller = controller;
	}

	public void start(){
		super.addSystem(new GridSystem());
		super.addSystem(new LoadStageSystem(stageId));
		super.addSystem(new PlayerControlSystem());
		super.addSystem(new AudioSystem());
		super.addSystem(new LoadTextureSystem(stageId));
		super.addSystem(new ScreenPositionSystem());
		super.addSystem(new RenderSystem(batch));
		super.addSystem(new HUDSystem(batch));
		super.addSystem(new MatchSystem(easyFinishMatchResultListner));
		
		super.start();
	}

	public void removeAllEntities() {
		//TODO
	}
	
	@Override
	public void update(float delta) {
		if(state == State.RUNNING){
			super.update(delta);
			waitTime();
		}else if(--finishingCountDown > 0 ){
			//TODO turn contestants invencible
			super.update(delta);
			waitTime();
		}else{
			state = State.FINISHED;
		}
	}
	
	MatchResultListener easyFinishMatchResultListner  = new MatchResultListener(){
		@Override
		public void endMatchHandle(MatchResult restult) {
			state = State.FINISHING;
			controller.endMatchHandle(restult);	
		}
	};

}
