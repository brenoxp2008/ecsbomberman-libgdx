/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package br.unb.bomberman.ui.screens.battle;

import br.unb.bomberman.ui.screens.Assets;
import br.unb.unbomber.Settings;
import br.unb.unbomber.match.GameMatch;
import br.unb.unbomber.match.GameMatch.State;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import ecs.common.match.TournamentController;

public class GameScreen extends ScreenAdapter {
	static final int GAME_READY = 0;
	static final int GAME_RUNNING = 1;
	static final int GAME_PAUSED = 2;
	static final int GAME_LEVEL_END = 3;
	static final int GAME_OVER = 4;
	
	SpriteBatch batch;
	private int state;

	OrthographicCamera guiCam;
	Vector3 touchPoint;

	Rectangle pauseBounds;
	Rectangle resumeBounds;
	Rectangle quitBounds;
	
	int lastScore;
	String scoreString;
	
	GameMatch match;
	
	TournamentController controller;
	
	public GameScreen (SpriteBatch batch, TournamentController controller, GameMatch match) {
		this.batch = batch;
		this.controller = controller;
		this.match = match;
	}
	
	@Override
	public void show(){
		loadGame();
		match.start();
	}

	private void loadGame() {
		
		guiCam = new OrthographicCamera(320, 480);
		guiCam.position.set(320 / 2, 480 / 2, 0);
		touchPoint = new Vector3();

		pauseBounds = new Rectangle(320 - 64, 480 - 64, 64, 64);
		resumeBounds = new Rectangle(160 - 96, 240, 192, 36);
		quitBounds = new Rectangle(160 - 96, 240 - 36, 192, 36);

		lastScore = 0;
		scoreString = "0";
		
		state = GAME_READY;
	}
	

	@Override
	public void render (float delta) {
		update(delta);
		drawUI(delta);
	}

	public void update (float deltaTime) {
		if (deltaTime > 0.1f) deltaTime = 0.1f;
		
		switch (state) {
		case GAME_READY:
			updateReady();
			break;
		case GAME_RUNNING:
			updateRunning(deltaTime);
			break;
		case GAME_PAUSED:
			updatePaused();
			break;
		case GAME_OVER:
			updateGameOver();
			break;
		}
	}

	private void updateReady () {
		if (Gdx.input.justTouched()) {
			state = GAME_RUNNING;
		}
	}

	private void updateRunning (float deltaTime) {
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			if (pauseBounds.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				Assets.music.setVolume(0f);
				state = GAME_PAUSED;
				return;
			}
		}
		
//		if (match.score != lastScore) {
//			lastScore = match.score;
//			scoreString = "" + lastScore;
//		}
		if (match.state == State.FINISHED) {
			state = GAME_OVER;
			if (lastScore >= Settings.highscores[4])
				scoreString = "NEW HIGHSCORE: " + lastScore;
			else
				scoreString = "" + lastScore;
			Settings.addScore("Player", lastScore);
			Settings.save();
			
			if(controller!=null){
				controller.nextScreen();
			}
		}
	}

	private void updatePaused () {
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			if (resumeBounds.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				Assets.music.setVolume(Settings.soundVolume);
				state = GAME_RUNNING;
				return;
			}

			if (quitBounds.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				Assets.music.setVolume(Settings.soundVolume);
				state = GAME_READY;
				controller.quit();
				return;
			}
		}
	}


	private void updateGameOver () {
		controller.nextScreen();
	}

	public void drawUI (float delta) {
		guiCam.update();
		batch.setProjectionMatrix(guiCam.combined);
		batch.begin();
		switch (state) {
		case GAME_READY:
			presentReady();
			break;
		case GAME_RUNNING:
			presentRunning(delta);
			break;
		case GAME_PAUSED:
			presentPaused();
			break;
		case GAME_LEVEL_END:
			presentLevelEnd();
			break;
		case GAME_OVER:
			presentGameOver();
			break;
		}
		batch.end();
	}

	private void presentReady () {
		batch.draw(Assets.ready, 160 - 192 / 2, 240 - 32 / 2, 192, 32);

	}

	private void presentRunning (float deltaTime) {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.draw(Assets.hudBar, 0, 410, 320, 60);
		batch.draw(Assets.pause, 320 - 40, 480 - 64, 48, 48);
		batch.draw(Assets.p1, 320 - 224, 480 - 56, 20, 24);
		batch.draw(Assets.boxScore, 320 - 200, 480 - 56, 16, 24);
		batch.draw(Assets.p2, 320 - 178, 480 - 56, 20, 24);
		batch.draw(Assets.boxScore, 320 - 154, 480 - 56, 16, 24);
		batch.draw(Assets.p3, 320 - 132, 480 - 56, 20, 24);
		batch.draw(Assets.boxScore, 320 - 108, 480 - 56, 16, 24);
		batch.draw(Assets.p4, 320 - 86, 480 - 56, 20, 24);
		batch.draw(Assets.boxScore, 320 - 60, 480 - 56, 16, 24);
		
		Assets.font.setScale(0.6f, 1);
		Assets.font.draw(batch, scoreString, 320 - 196, 480 - 32);
		
		match.update(deltaTime);
	}

	private void presentPaused () {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.draw(Assets.pauseMenu, 160 - 192 / 2, 240 - 96 / 2, 192, 96);
	}

	private void presentLevelEnd () {
		String topText = "the princess is ...";
		String bottomText = "in another castle!";
		float topWidth = Assets.font.getBounds(topText).width;
		float bottomWidth = Assets.font.getBounds(bottomText).width;
		Assets.font.draw(batch, topText, 160 - topWidth / 2, 480 - 40);
		Assets.font.draw(batch, bottomText, 160 - bottomWidth / 2, 40);
	}

	private void presentGameOver () {
		batch.draw(Assets.gameOver, 160 - 160 / 2, 240 - 96 / 2, 160, 96);
		float scoreWidth = Assets.font.getBounds(scoreString).width;
		Assets.font.draw(batch, scoreString, 160 - scoreWidth / 2, 480 - 20);
	}
	


	@Override
	public void pause () {
		if (state == GAME_RUNNING) {
			state = GAME_PAUSED;
		}
	}
}