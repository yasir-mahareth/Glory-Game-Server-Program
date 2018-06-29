/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package glory.game.server;

import java.io.*;
import java.net.*;
import java.sql.*;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class NetComponents extends Thread implements Serializable {
    private String name,initialLetters,name_ForInitial,nameFromNE,finalWord;
    private  int round,rarenessScore,rewardScore,wordScore,finalScore,round_ForInitial;
    private Socket socket;
    ArrayList<ScoreDetails> scoreContainer = new ArrayList<ScoreDetails>();
    ArrayList<InitialLetterDetails> initialLetterContainer = new ArrayList<InitialLetterDetails>();
    ObjectOutputStream oos;
    PrintWriter output;
    
    public NetComponents(Socket socket){
        this.socket=socket;
    }
    
    @Override
    public void run(){
        try{
            BufferedReader input= new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
            
            output = new PrintWriter(socket.getOutputStream(),true);
            
            oos= new ObjectOutputStream(socket.getOutputStream());
           
            String request=  input.readLine();
            
            if(request.equals("score")){
                name= input.readLine();
                round= Integer.parseInt(input.readLine());
                finalWord= input.readLine();
                rarenessScore= Integer.parseInt(input.readLine());
                rewardScore= Integer.parseInt(input.readLine());
                wordScore= Integer.parseInt(input.readLine());
                finalScore= Integer.parseInt(input.readLine());
          
         
                clearPreviousScoreRecords();
                passScoresToDatabase();
                retreiveAndPassScoresToClients();
                                
                System.out.println("\n"); 
            }
                
            if(request.equals("initial letters")){
                
                name_ForInitial=input.readLine();
                round_ForInitial=Integer.parseInt(input.readLine());
                initialLetters=input.readLine();
                
                clearPreviousInitialLetterRecords();
                passInitialLettersToDatabase();
                retreiveAndPassInitialLettersToClients();
                 
                System.out.println("\n"); 
                
            }
            
            if(request.equals("player names")){
                
                nameFromNE =input.readLine();
                clearPreviousNames();
                passPlayerName();
                retreiveAndPassPlayerNamesToClient();
            }
            
        }
        catch(IOException e){
            System.out.println(e);
        } 
        finally{
            try{
                
                socket.close();
            }
            catch(IOException e){
                System.out.println(e);
            }
        }
        
    }
    
    ///////////Score methods //////////////
    
    public void clearPreviousScoreRecords(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/glory_game", "root", "");
            
            PreparedStatement ps = conn.prepareStatement("SELECT name,round,finalword,rarenessScore,rewardScore,wordScore,finalScore FROM rounddetails");
            ResultSet rs= ps.executeQuery();
            
            while(rs.next()){
                ScoreDetails unitScore = new ScoreDetails(rs.getString(1),rs.getInt(2),rs.getString(3),rs.getInt(4),rs.getInt(5),rs.getInt(6),rs.getInt(7));
                scoreContainer.add(unitScore);
            }
            if(round==1 && scoreContainer.size()>1){
                Statement stmt= conn.createStatement();              
                stmt.executeUpdate("DELETE FROM rounddetails");
            }
            scoreContainer.removeAll(scoreContainer);
            
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
    
    public void passScoresToDatabase(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/glory_game", "root", "");            
                        
            PreparedStatement ps = conn.prepareStatement("INSERT INTO rounddetails (name,round,finalword,rarenessScore,rewardScore,wordScore,finalScore) Values (?,?,?,?,?,?,?)");
            ps.setString(1, name);
            ps.setInt(2, round);
            ps.setString(3, finalWord);
            ps.setInt(4, rarenessScore);
            ps.setInt(5, rewardScore);
            ps.setInt(6, wordScore);
            ps.setInt(7,finalScore);
            
            int k=ps.executeUpdate();
            System.out.println("Score details added to database");
        }
        catch(Exception e){
            System.out.println(e);
        }
        
    }
    
    
    public void retreiveAndPassScoresToClients(){
        int r1,r2,scSize;
        
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/glory_game", "root", "");
            
            do{
                PreparedStatement ps = conn.prepareStatement("SELECT name,round,finalword,rarenessScore,rewardScore,wordScore,finalScore FROM rounddetails");
                ResultSet rs= ps.executeQuery();
            
                scoreContainer.clear();
                while(rs.next()){
                    ScoreDetails unitScore = new ScoreDetails(rs.getString(1),rs.getInt(2),rs.getString(3),rs.getInt(4),rs.getInt(5),rs.getInt(6),rs.getInt(7));            
                    scoreContainer.add(unitScore);
                }
                scSize=scoreContainer.size();
                 r1=scoreContainer.get(scSize-1).getRound();
                try{
                    r2=scoreContainer.get(scSize-2).getRound();
                } 
                catch(Exception e){
                    r2=-1;
                }
                 
            }while(scSize<2 || r1!=round || r2!=round  );
            
            
            oos.writeObject(scoreContainer);
            oos.flush();
        
            
        }
        catch(Exception e){
            System.out.println(e);
        }
        
    }
    
    
    
    
    
    
    
     
    ////////// Initial Letters methods //////////
    
    public void clearPreviousInitialLetterRecords(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/glory_game", "root", "");
            
            PreparedStatement ps = conn.prepareStatement("SELECT name,round,initialletters FROM initialletters");
            ResultSet rs= ps.executeQuery();
            
            while(rs.next()){
                InitialLetterDetails initials = new InitialLetterDetails(rs.getString(1),rs.getInt(2),rs.getString(3));
                initialLetterContainer.add(initials);
            }
            if(round_ForInitial==1 && initialLetterContainer.size()>1){
                Statement stmt= conn.createStatement();              
                stmt.executeUpdate("DELETE FROM initialletters");
            }
            scoreContainer.removeAll(scoreContainer);
            
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
    
    public void passInitialLettersToDatabase(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/glory_game", "root", "");            
                        
            PreparedStatement ps = conn.prepareStatement("INSERT INTO initialletters (name,round,initialletters) Values (?,?,?)");
            ps.setString(1, name_ForInitial);
            ps.setInt(2, round_ForInitial);
            ps.setString(3, initialLetters);
            
            
            int k=ps.executeUpdate();
            System.out.println("First 3 letters added to database");
        }
        catch(Exception e){
            System.out.println(e);
        }
        
    }
    
    
    public void retreiveAndPassInitialLettersToClients(){
        int r1,r2,scSize;
        
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/glory_game", "root", "");
            
            do{
                PreparedStatement ps = conn.prepareStatement("SELECT name,round,initialletters FROM initialletters");
                ResultSet rs= ps.executeQuery();
            
                initialLetterContainer.clear();
                while(rs.next()){
                    InitialLetterDetails initials = new InitialLetterDetails(rs.getString(1),rs.getInt(2),rs.getString(3));            
                    initialLetterContainer.add(initials);
                }
                scSize=initialLetterContainer.size();
                 r1=initialLetterContainer.get(scSize-1).getRound();
                try{
                    r2=initialLetterContainer.get(scSize-2).getRound();
                } 
                catch(Exception e){
                    r2=-1;
                }
                 
            }while(scSize<2 || r1!=round_ForInitial || r2!=round_ForInitial  );
            
            
            oos.writeObject(initialLetterContainer);
            oos.flush();
        
            
        }
        catch(Exception e){
            System.out.println(e);
        }
        
    }
    
    
    ////// Player names methods  ////////
    
    
    public void clearPreviousNames(){
        ArrayList<String> nameArray = new ArrayList<String>();
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/glory_game", "root", "");
            
            
            PreparedStatement ps = conn.prepareStatement("SELECT name FROM playernames");
            ResultSet rs= ps.executeQuery();
            
            nameArray.clear();
            
            while(rs.next()){
                String tempName = rs.getString(1);
                nameArray.add(tempName);
            }
            
            if(nameArray.size()>1){
                Statement stmt= conn.createStatement();              
                stmt.executeUpdate("DELETE FROM playernames");       
            }
            
                 
            nameArray.clear();
        }
        catch(Exception e){
            System.out.println(e);
        }

    }
    
    public void passPlayerName(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/glory_game", "root", "");
            
            
            PreparedStatement ps = conn.prepareStatement("INSERT INTO playernames (name) Values (?)");
            ps.setString(1,nameFromNE );
        
            int k=ps.executeUpdate();
            System.out.println("Player name added to database");
        }
        catch(Exception e){
            System.out.println(e);
        }
        
    }
    
    public void retreiveAndPassPlayerNamesToClient(){
        ArrayList<String> nameContainer = new ArrayList<String>();
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/glory_game", "root", "");
            
            do{
                PreparedStatement ps = conn.prepareStatement("SELECT name FROM playernames");
                ResultSet rs= ps.executeQuery();
            
                nameContainer.clear();
            while(rs.next()){
                String tempName = rs.getString(1);
                nameContainer.add(tempName);
            }
            
            }while(nameContainer.size()<=1);
            
            oos.writeObject(nameContainer);
            oos.flush();
                       
        }
        catch(Exception e){
            System.out.println(e);
        }
        
    }
    
    
}
