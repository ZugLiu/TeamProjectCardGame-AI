package structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.ability.Spell;
import utils.BasicObjectBuilders;
import utils.Preparation;
import utils.StaticConfFiles;

public class AIPlayer extends Player {
	private HashMap<Unit, ArrayList<Integer>> enemyUnitPos; // 敌方场上单位id及其坐标
	private HashMap<Unit, ArrayList<Integer>> friendlyUnitPos; // 我方场上单位id及其坐标
	private List<Card> hand;
	private int i;
	private int j;

	public void doSomething(ActorRef out, GameState gameState) {
		// AI player will do something to modify its cards and units
		this.AIHand(gameState);

		// get enemy and friendly units and their positions
		this.getEnemyUnitOnBoard(gameState);
		this.getFriendlyUnitOnBoard(gameState);

		// get enemy and friendly avatars and their positions
		this.getEnemyAvatarOnBoard(gameState);
		this.getFriendlyAvatarOnBoard(gameState);

		// play a card on a certain tile
		i = 7;
		j = 3;
		this.checkUnitCostAndPlace(gameState, hand.get(0), out, i, j);
		
		
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// AI finish its round, add 1 to round number
		switchToHumanPlayer(out, gameState);
	}

	private void switchToHumanPlayer(ActorRef out, GameState gameState) {
		// human players' turn
		gameState.rounds++;
		Preparation.turnOverTheCoin(gameState);

		// tell human player that it finish its turn
		BasicCommands.addPlayer2Notification(out, "I'm done!", 2);

		// draw card to human player and distribute Mana
		Preparation.drawCardsToPlayer(gameState);
		Preparation.distributeMana(out, gameState, gameState.player);
		Preparation.resetMoveAndAttackCount(gameState.gameBoard.getPlayer1UnitList());

		// clean current Hand and show human player's hand
		Preparation.cleanHand(out, gameState.aiPlayer);
		Preparation.showHand(out, gameState.player);

		// print turn switching notification
		BasicCommands.addPlayer1Notification(out, "Player1's turn!", 2);
	}

	private void getEnemyUnitOnBoard(GameState gs) {
		this.enemyUnitPos = new HashMap<>();
		for (Unit u : gs.gameBoard.getPlayer1UnitList()) {
			ArrayList<Integer> pos = new ArrayList<>();
			pos.add(u.getPosition().getTilex());
			pos.add(u.getPosition().getTiley());

			this.enemyUnitPos.put(u, pos);
		}

		// for test
		System.out.println("Enemy Unit Positions:");
		for (Map.Entry<Unit, ArrayList<Integer>> entries : enemyUnitPos.entrySet()) {
			System.out.println(entries.getKey().getId() + ": " + entries.getValue());
		}
	}

	private ArrayList<Integer> getEnemyAvatarOnBoard(GameState gs) {
		ArrayList<Integer> temp = null;
		for (Map.Entry<Unit, ArrayList<Integer>> entries : enemyUnitPos.entrySet()) {
			if (entries.getKey().getId() == 40) { // the id of enemy avatar is 40
				temp = entries.getValue();
			}
		}
		// for test
		System.out.print("Enemy Avatar Position is: ");
		for (Integer i : temp) {
			System.out.print(i + " ");
		}
		System.out.println();
		return temp;
	}

	private void AIHand(GameState gameState) {
		hand = gameState.aiPlayer.getHand();
	}

	// 获得己方单位的坐标。Get friendly unit positions
	private void getFriendlyUnitOnBoard(GameState gs) {
		this.friendlyUnitPos = new HashMap<>();
		for (Unit u : gs.gameBoard.getPlayer2UnitList()) {
			ArrayList<Integer> pos = new ArrayList<>();
			pos.add(u.getPosition().getTilex());
			pos.add(u.getPosition().getTiley());

			this.friendlyUnitPos.put(u, pos);
		}

		// for test
		System.out.println("Friendly Unit Positions: ");
		for (Map.Entry<Unit, ArrayList<Integer>> entries : friendlyUnitPos.entrySet()) {
			System.out.println(entries.getKey().getId() + ": " + entries.getValue());
		}

	}

	private ArrayList<Integer> getFriendlyAvatarOnBoard(GameState gs) {
		ArrayList<Integer> temp = null;
		for (Map.Entry<Unit, ArrayList<Integer>> entries : friendlyUnitPos.entrySet()) {
			if (entries.getKey().getId() == 41) { // the id of friendly avatar is 40
				temp = entries.getValue();
			}
		}
		// for test
		System.out.print("Friendly Avatar Position is: ");
		for (Integer i : temp) {
			System.out.print(i + " ");
		}
		System.out.println();
		return temp;
	}

	// 将某个单位放置在棋牌上的某个坐标 place a unit on a tile
	private void placeAUnit(GameState gameState, ActorRef out, int x, int y, Card card) {

		// initialize the unit object
		Unit unit = Preparation.transCardToUnit(card);

		// show the unit
		if (unit != null) {
			// load it to the board
			gameState.gameBoard.getTile(x, y).setOccupied(true);
			gameState.gameBoard.addplayer2UnitToBoard(unit);
			Tile tile = BasicObjectBuilders.loadTile(x, y);
			unit.setPositionByTile(tile);
			unit.showUnit(out, tile, card.getBigCard().getAttack(), card.getBigCard().getHealth());

			// set unit stun
			gameState.stunUnitList.add(unit);
		}

		// discard the summoned card
		gameState.aiPlayer.getHand().remove(card);

		// fresh the game screen
		gameState.gameBoard.drawBoard(out);

		System.out.println(card.getCardname() + "is on board");
		System.out.println(unit.getAbilities());
	}
	
	// check if the target tile is summonable and if the mana is enough to summon the unit
	private void checkUnitCostAndPlace(GameState gs, Card c, ActorRef out, int tilex, int tiley) {
		int mana = gs.aiPlayer.getMana();
		gs.selectedCard = c;
		
		//to know where the unit can be placed on
		gs.gameBoard.calculateAvailableSummonPlace(gs);
		
		if(gs.gameBoard.getTile(tilex, tiley).isAllowSummon()) {
			//if this tile is summonable
			//to know whether the cost is larger than mana
			if (c.getManacost() <= mana) {
				this.placeAUnit(gs, out, tilex, tiley, c);
				gs.aiPlayer.setMana(mana -= c.getManacost());
				System.out.println(c.getCardName() + "is on board now!");
			} else {
				System.out.println("Not enough mana!");
			}
		}else {
			System.out.println("This tile is not summonable!");
		}
			
		gs.gameBoard.cleanBoardFlag();
	}

	// 将某个spell释放给某个单位 cast a spell to a unit
	private void castSpell() {

	}

	// 指挥一个单位移动到目标坐标 move a unit to a tile
	private void moveTo(GameState gs, Unit u, ActorRef out, int tilex, int tiley) {
		// play animation
		BasicCommands.moveUnitToTile(out, gs.selectedUnit, gs.gameBoard.getTile(tilex, tiley));

		// set target board occupied
		gs.gameBoard.resetUnitTileOccupied(gs.selectedUnit, tilex, tiley);

		// set unit position
		gs.selectedUnit.setPositionByCoordination(tilex, tiley);

		// decrease move chance
		gs.selectedUnit.decreaseMoveCount();

		// cancel selection
		gs.selectedUnit = null;
	}

	// 计算应该走到那个砖块
	private void caculateToWhichTile() {

	}

}
