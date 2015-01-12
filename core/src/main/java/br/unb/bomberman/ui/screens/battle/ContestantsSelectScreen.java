package br.unb.bomberman.ui.screens.battle;

import br.unb.bomberman.ui.screens.Assets;
import br.unb.bomberman.ui.screens.MenuButtonFactory;
import br.unb.unbomber.Settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ecs.common.match.Contestant;
import ecs.common.match.ScreenFlowController;
import ecs.common.match.TournamentRules;

public class ContestantsSelectScreen implements Screen {

	private final SpriteBatch batch;
    
	private OrthographicCamera camera;
    
	private Stage stage;

	private ScreenFlowController controller;
	
	private TournamentRules rules;

    public ContestantsSelectScreen(final SpriteBatch batch, ScreenFlowController controller, TournamentRules rules) {
        this.batch = batch;
        this.controller = controller;
        this.rules = rules;
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // Forces the render
        batch.flush();
        stage.act();
        stage.draw();
        batch.end();
    }

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
		stage = new Stage();
        stage.clear();
        MenuButtonFactory buttonFactory = new MenuButtonFactory();
        
    	if (!"COM".equals(rules.getContestants().get(0).getType())) {
			stage.addActor(buttonFactory.makeMenuButton("1 Player - " + rules.getContestants().get(0).getType(), new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Assets.playSound(Assets.clickSound);
					rules.getContestants().get(0).setType("MAN");
					show();
				}
			}));
		}else {
			stage.addActor(buttonFactory.makeMenuButton("1 Player - MAN", new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Assets.playSound(Assets.clickSound);
					rules.getContestants().get(0).setType("COM");
					show();
				}
			}));
		}
			
		String label = "2 Player - " + rules.getContestants().get(1).getType();
        
    	if (!"COM".equals(rules.getContestants().get(1).getType())) {
			stage.addActor(buttonFactory.makeMenuButton(label, new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Assets.playSound(Assets.clickSound);
					rules.getContestants().get(1).setType("MAN");
					show();
				}
			}));
		}else {
			stage.addActor(buttonFactory.makeMenuButton(label, new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Assets.playSound(Assets.clickSound);
					rules.getContestants().get(1).setType("COM");
					show();
				}
			}));
		}
			
        
        Gdx.input.setInputProcessor(stage);
        
        stage.addActor(buttonFactory.makeMenuButton("NEXT", nextClickListener));
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
	
	public void next(){
		controller.nextScreen();
	}
	
	ClickListener nextClickListener = new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y) {
			Assets.playSound(Assets.clickSound);
			next();
		}
	};
}