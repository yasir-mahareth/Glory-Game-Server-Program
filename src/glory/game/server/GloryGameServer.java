/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package glory.game.server;

import java.io.*;
import java.net.*;

/**
 *
 * @author Yaas
 */
public class GloryGameServer {

    
    public static void main(String[] args) {
        try(ServerSocket ss = new ServerSocket(5000)){
            
            while(true){
                Socket socket =ss.accept();
                System.out.println("Client connected!");
                
                NetComponents threadObject=new NetComponents(socket);
                threadObject.start();
            }
            
            
        }
        catch(IOException e){
            System.out.println("Server socket creation error: "+e);
        }
        
        
        
        
    }
    
}
