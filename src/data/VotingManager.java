/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import irc.TwitchBot;
import java.util.ArrayList;

/**
 * Classe usada para gerenciar o período de votação.
 * @author Dan
 */
public class VotingManager {
    private static final int VOTING_TIMER_SECONDS = 70;
    
    int nVotes;
    ArrayList<String> users;
    int[] games;
    TwitchBot bot;
    
    public VotingManager(TwitchBot tb) {
        bot = tb;
        nVotes = 0;
        users = new ArrayList<>();
        games = new int[7];
    }
    
    public void startVoting() {
        Thread vt = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(VOTING_TIMER_SECONDS*1000);
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                bot.closeVoting();
            }
        };
        vt.start();
    }
    
    public void vote(String user, String vote) {
        if (users.contains(user))
            return;
        
        switch (vote) {
            case "1":
                games[0]++;
                users.add(user);
                nVotes++;
                break;
            case "2":
                games[1]++;
                users.add(user);
                nVotes++;
                break;
            case "3":
                games[2]++;
                users.add(user);
                nVotes++;
                break;
            case "4":
                games[3]++;
                users.add(user);
                nVotes++;
                break;
            case "5":
                games[4]++;
                users.add(user);
                nVotes++;
                break;
            case "6":
                games[5]++;
                users.add(user);
                nVotes++;
                break;
            case "7":
                games[6]++;
                users.add(user);
                nVotes++;
                break;
        }
    }
    
    /**
     * @return O resultado da votação. Retorna -1 se não houve votos.
     */
    public int getResults() {
        int max = 0;
        int result = -1;
        for (int i=0;i<games.length;i++) {
            if (games[i]>max) {
                max = games[i];
                result = i;
            }
        }
        return result;
    }
    
    public int getTotalVotes() {
        return nVotes;
    }
    
}
