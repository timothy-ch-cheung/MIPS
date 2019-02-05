package main;

import audio.AudioController;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import objects.Entity;
import renderer.Renderer;
import server.Telemetry;
import ui.MenuController;
import utils.Input;
import utils.Map;
import utils.Methods;
import utils.ResourceLoader;
import utils.enums.Direction;

public class Client extends Application {

  private int id;
  private KeyController keyController;
  private Telemetry telemetry;
  private AudioController audioController;
  private Scene gameScene;
  private Stage primaryStage;
  private Renderer renderer;
  private final int xRes = 1920;
  private final int yRes = 1080;
  private ResourceLoader resourceLoader;
  private Entity[] agents;
  Map map;


  public int getId() {
    return id;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    int id = 0; // This will be changed if main joins a lobby, telemetry will give it new id
    audioController = new AudioController();
    keyController = new KeyController();
    resourceLoader = new ResourceLoader("src/test/resources/");
    this.primaryStage = primaryStage;
    //this.gameScene = new Scene(new Label("place holder"), xRes, yRes);
    MenuController menuController = new MenuController(audioController, primaryStage, this);
    StackPane root = (StackPane) menuController.createMainMenu();
    Scene scene = new Scene(root, xRes, yRes);
    final Canvas canvas = new Canvas(xRes, yRes);
    Group gameRoot = new Group();
    gameRoot.getChildren().add(canvas);
    this.gameScene = new Scene(gameRoot);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    renderer = new Renderer(gc, xRes, yRes, resourceLoader.getMapTiles() );
    primaryStage.setScene(scene);
    primaryStage.show();

  }
  
  
  public void startSinglePlayerGame() {
    //TODO Implement fully
  
    System.out.println("Starting single player game...");
    // If hosting if not telemetry will be set by connection method along with new main id
    map = resourceLoader.getMap();
    this.telemetry = new Telemetry(map);
    this.primaryStage.setScene(gameScene);

    gameScene.setOnKeyPressed(keyController);

    startGame();
  }
  
  public void startMultiplayerGame() {
    //TODO Implement
  }
  
  private void startGame() {

    agents = new Entity[5];
    agents[0] = new Entity(true, 0, new Double(1, 3));
    agents[1] = new Entity(false, 1, new Double(1, 2));
    agents[2] = new Entity(false, 2, new Double(1, 2));
    agents[3] = new Entity(false, 3, new Double(1, 2));
    agents[4] = new Entity(false, 4, new Double(1, 2));
    Methods.updateImages(agents, resourceLoader);
    this.primaryStage.setScene(gameScene);
    // AnimationTimer started once game has started
    new AnimationTimer() {
      @Override
      public void handle(long now) {
        processInput();
        Telemetry.processPhysics(agents, map);
        render();
      }
    }.start();
  }

  private void processInput() {
    Direction input = keyController.getActiveKey();
    Direction current = agents[id].getDirection();

    if (input == null | input == current) {
      return;
    }
    System.out.println(input.toString() + "     " + current);
    switch (input) {
      case UP: // Add code here
        // Validate the input
        System.out.println("Direction up");
        informServer(new Input(0, Direction.UP));
        agents[id].setDirection(input);
        break;
      case DOWN: // Add code here
        System.out.println("Direction down");
        informServer(new Input(0, Direction.DOWN));
        agents[id].setDirection(input);
        break;
      case LEFT: // Add code here
        System.out.println("Direction left");
        informServer(new Input(0, Direction.LEFT));
        agents[id].setDirection(input);
        break;
      case RIGHT: // Add code here
        System.out.println("Direction right");
        informServer(new Input(0, Direction.RIGHT));
        agents[id].setDirection(input);
        break;
    }
  }

  private void informServer(Input input) {
      if (id == 0) {
      telemetry.addInput(input);
    } else {
          // TODO integrate with networking to send to telemetry
    }
  }

  private void render() {
    ArrayList<Entity> x = new ArrayList<>();
    for(Entity e : agents){
      x.add(e);
    }
    renderer.render(map, x);
  }
}