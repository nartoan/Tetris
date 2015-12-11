/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package highScore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author DUC TOAN
 */
public class Score {
    ArrayList<Pair> scoreHigh = new ArrayList();
    
    public Score(String filename) throws FileNotFoundException {
        Scanner inData = new Scanner(new File(filename));
        for (int i = 0; i < 5; i++) {
            while (inData.hasNext()) {
                String [] temp = inData.nextLine().split(":");
                scoreHigh.add(new Pair(temp[0], Integer.parseInt(temp[1])));
            } 
        }
    }
    
    public boolean updateScore(int score) {
        boolean check = false;
        for (int i = 0; i < scoreHigh.size(); i++) {
            if (score >= scoreHigh.get(i).score) {         
                scoreHigh.set(i, new Pair(scoreHigh.get(i).name, score));
                check = true;
                break;
            }
        }
        return check;
    }
    
    public ArrayList<Pair> getArrayList() {
        return scoreHigh;
    }
    
    public void updateScore(String name, int score) {
        for (int i = 0; i < scoreHigh.size(); i++) {
            if (score == scoreHigh.get(i).score) {         
                scoreHigh.set(i, new Pair(name, score));
                break;
            }
        }
    }
    
    public void outToFile(String filename) throws IOException {
        FileWriter out = new FileWriter(new File(filename));
        PrintWriter wr = new PrintWriter(out,true);
        for (Pair scoreHigh1 : scoreHigh) {
            StringBuilder temp = new StringBuilder();
            temp.append(scoreHigh1.name).append(":").append(scoreHigh1.score).append("\r\n");
            wr.write(temp.toString());
        }
        wr.close();
    }
    
    public String print(int i) {
            return scoreHigh.get(i).name + " - " + scoreHigh.get(i).score;
        }
    
    public int high() {
        return scoreHigh.get(0).score;
    }
    
    public class Pair {
        public String name;
        public int score;
        
        public Pair(String name, int score) {
            this.name = name;
            this.score = score;
        }   
    }
}
