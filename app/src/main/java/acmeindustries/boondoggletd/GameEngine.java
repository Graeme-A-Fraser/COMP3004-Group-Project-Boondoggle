package acmeindustries.boondoggletd;

import android.graphics.Canvas;

import acmeindustries.boondoggletd.controller.BattlegroundController;
import acmeindustries.boondoggletd.controller.BuildController;
import acmeindustries.boondoggletd.controller.NotificationController;
import acmeindustries.boondoggletd.controller.RecruitController;
import acmeindustries.boondoggletd.model.Battleground;
import acmeindustries.boondoggletd.model.Notification;
import acmeindustries.boondoggletd.model.Player;

import static acmeindustries.boondoggletd.model.Player.GameMode.*;

public class GameEngine {

    private Player player;
    private Notification notification;

    private BattlegroundController battlegroundController;
    private BuildController buildController;
    private RecruitController recruitController;
    private NotificationController notificationController;

    private Battleground bg;
    private float width;
    private float height;

    private float ticks;
    private float frames;
    private float startTime;
    private float totalTime;

    public GameEngine(float w, float h){
        this.width = w;
        this.height = h;
        this.init();
    }

    private void init() {
        bg = new Battleground();
        player = new Player(BATTLEGROUND);
        notification = new Notification();

        // additional controllers for breaking out smaller tasks
        battlegroundController = new BattlegroundController(player, bg, notification, width, height);
        buildController = new BuildController(player, bg, notification, width, height);
        recruitController = new RecruitController(player, bg, notification,  width, height);
        notificationController = new NotificationController(notification);
    }

    public void press(float x, float y){
        //System.out.printf("pressed at %f, %f - gridx: %d, gridy: %d\n", x, y, (int)((x/width)*10), (int)((y/height)*10));

        /* creating towers in bg mode...
        if(bg.addPlayerTower((int)((x/width)*10)-bg.getPlayerGridX(), (int)((y/height)*10)-bg.getPlayerGridY()) == true){
            System.out.println("Tower created!");
        }else{
            System.out.println("Problem creating tower.");
        }
        */
        if(notification.isActive()) {
            notificationController.press(x,y);
        }else {
            switch (player.gm) {
                case BATTLEGROUND:
                    // bg contro
                    battlegroundController.press(x, y);
                    break;
                case BUILDING_SELECTING:
                case BUILDING_PLACING:
                case SELLING:
                    // build controller?
                    buildController.press(x, y);
                    break;
                case RECRUITING:
                    // recruit controller?
                    recruitController.press(x, y);
                    break;
            }
        }
    }

    public void release(float x, float y){
        System.out.printf("released at %f, %f\n", x, y);
    }

    public void update(){
        switch(player.gm){
            case BATTLEGROUND:
                battlegroundController.update();
                break;
            case BUILDING_SELECTING:
            case BUILDING_PLACING:
                buildController.update();
                break;
            case RECRUITING:
                recruitController.update();
                break;
        }
        this.ticks ++;
        totalTime = (System.nanoTime() - startTime) / 1000000000f;
        if(totalTime>10){
            this.ticks = 0;
            this.frames = 0;
            startTime = System.nanoTime();
        }else {
            //System.out.printf("FPS: %f, TICKS: %f\n", frames / totalTime, ticks / totalTime);
        }
    }

    public void render(Canvas canvas){
        this.frames++;
        switch(player.gm){
            case BATTLEGROUND:
                battlegroundController.render(canvas);
                break;
            case BUILDING_SELECTING:
            case BUILDING_PLACING:
            case SELLING:
                buildController.render(canvas);
                break;
            case RECRUITING:
                recruitController.render(canvas);
                break;
        }
        notificationController.render(canvas);
    }
}