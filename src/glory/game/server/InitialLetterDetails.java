/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package glory.game.server;

import java.io.Serializable;

/**
 *
 * @author Yaas
 */
public class InitialLetterDetails implements Serializable{
    
    private String playerName;
    private int round;
    private String initialLetters;
   
    
    
    public InitialLetterDetails(String name, int round, String initialLetters){
        this.playerName=name;
        this.round=round;
        this.initialLetters=initialLetters;
        
    }

    public int getRound() {
        return round;
    }

    public String getPlayerName() {
        return playerName;
    }
   

    public String getInitialLetters() {
        return initialLetters;
    }
}
