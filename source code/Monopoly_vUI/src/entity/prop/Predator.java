package entity.prop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import controler.Game;
import controler.GameGUI;
import controler.MessageDialog;
import entity.player.Player;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Predator extends Prop {

	public Predator() {
		super(null,PropType.predator);
		price = 32;
	}
	public Predator(Player owner) {
		super(owner,PropType.predator);
		price = 32;
	}
	@Override
	public String getDescription() {
		return "卡片名：掠夺卡\n"
				+ "卡片适用对象：距离自己五步以内的对手\n"
				+ "卡片使用效果：\n"
				+ "随机将对手的一张卡牌据为己有";
	}

	@Override
	public String getName() {
		return "掠夺卡";
	}
	
	public int getPrice() {
		return price;
	}
	@Override
	public boolean canWork() {
		return owner.getReachablePlayer(5).size()>0;
	}
	@Override
	public void _work(Stage primaryStage, GameGUI game) {
		
		final Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(primaryStage);
		stage.setTitle("From whom to get a card?");
		
		Group root = new Group();
		Scene scene = new Scene(root,200,250);
		stage.setScene(scene);
		
		VBox box = new VBox();
		box.setSpacing(10);
		box.setPadding(new Insets(20,0,0,20));
		Label title = new Label("All reachable players:");
		ArrayList<RadioButton> player_rbs = new ArrayList<RadioButton>();
		List<Player> reachablePlayers = Game.getGame().getControlPlayer().getReachablePlayer(5);
		reachablePlayers.stream().forEach(e->{
			player_rbs.add(new RadioButton(e.getName()));
		});
		ToggleGroup tg = new ToggleGroup();
		player_rbs.get(0).setSelected(true);
		Button ok_bt = new Button("That's it!");
		
		box.getChildren().add(title);
		player_rbs.stream().forEach(e->{
			e.setToggleGroup(tg);
			box.getChildren().add(e);
			e.setUserData(reachablePlayers.get(player_rbs.indexOf(e)));
		});
		box.getChildren().add(ok_bt);
		
		ok_bt.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				Player target = (Player) tg.getSelectedToggle().getUserData();		
				Prop p = target.getMyProp().stream().findAny().get();
				if(p==null){
					new MessageDialog(game.getStage(),"That player does not have any prop! T.T",250,100);
					return;
				}
				target.getMyProp().remove(p);
				owner.getMyProp().add(p);
				p.owner = owner;
				removeFromOwner();
				stage.hide();
				new MessageDialog(stage,"Got "+p.getName()+" from "+target.getName()+"!",200,100);
			}
		});
		
		root.getChildren().add(box);
		stage.show();
		
	}

}
