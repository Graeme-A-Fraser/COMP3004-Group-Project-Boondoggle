package acmeindustries.boondoggletd.controller;

import java.util.ArrayDeque;
import java.util.Iterator;

import acmeindustries.boondoggletd.model.Battleground;
import acmeindustries.boondoggletd.model.Creep;

public class Spawner {

    private Battleground bg;

    // time between creeps
    private int spawnTimer;
    private int maxSpawnTimer;

    // creeps to spawn
    private ArrayDeque<Creep> playerCreeps;
    private ArrayDeque<Creep> enemyCreeps;
    private int creepsRemaining;


    public Spawner(Battleground bg){
        this.bg = bg;
        this.maxSpawnTimer = bg.TPS*2;
        this.spawnTimer = maxSpawnTimer;
        this.playerCreeps = new ArrayDeque<Creep>();
        this.enemyCreeps = new ArrayDeque<Creep>();
        this.creepsRemaining = 0;
    }

    public void startRound(){
        this.creepsRemaining= playerCreeps.size() + enemyCreeps.size();
        Iterator<Creep> it = playerCreeps.iterator();
        bg.createPath();
        while(it.hasNext()){
            // create path for creep and add hp based on round (so game ends!)
            Creep c = it.next();
            c.setPath(bg.getPlayerPath());
            c.setMaxHP(c.getMaxHP()+bg.getRoundNumber()*10);
            c.setHp(c.getMaxHP());
        }
        it = enemyCreeps.iterator();
        while(it.hasNext()){
            // create path for creep and add hp based on round (so game ends!)
            Creep c = it.next();
            c.setPath(bg.getEnemyPath());
            c.setMaxHP(c.getMaxHP()+bg.getRoundNumber()*10);
            c.setHp(c.getMaxHP());
        }
        bg.spawning = true;
    }

    public void update(){

        if(bg.spawning){
            this.spawnTimer--;
        }
        if(this.spawnTimer <= 0 ){
            // check if stacks are empty and pop if not
            if(!playerCreeps.isEmpty()){
                bg.addPlayerCreep(playerCreeps.pop());
                creepsRemaining--;
            }
            if(!enemyCreeps.isEmpty()){
                bg.addEnemyCreep(enemyCreeps.pop());
                creepsRemaining--;
            }
            this.spawnTimer = this.maxSpawnTimer;
        }
        if(this.creepsRemaining<=0){
            bg.spawning = false;
            this.spawnTimer = maxSpawnTimer;
        }
    }

    public void pushPlayerCreep(Creep c){
        playerCreeps.push(c);
    }

    public void pushEnemyCreep(Creep c){
        enemyCreeps.push(c);
    }

}
