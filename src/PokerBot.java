//********************************************************************************************************************************************
// PokerBot.java         Author:Ali Berk Karaarslan     Date:11.04.2024
//
// One Of The Classes Of Poker Project
//********************************************************************************************************************************************

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class PokerBot extends AbstractPlayer {

    private int checkCoefficient = 20;
    private int callCoefficient = 30;
    private int raiseLowerCoefficient = 50;
    private int raiseHigherCoefficient = 60;
    private int allinCoefficient = 65;

    private int smallInterval = 50;
    private int midInterval = 150;
    private int largeInterval = 500;

    //Default Constructor. Creates Player With Name And Balance.
    public PokerBot(String name, int balance){
        super(name, balance);
    }

    public PokerBot(){
        super();
    }

    public int guessOpponentScore(AbstractPlayer player){

        //Gets The Community Cards And Puts Them Into A Empty Custom Deck.
        Deck customDeck =  new Deck(true);
        for(int i = 0; i < Game.communityCards.size(); i++)
            customDeck.insertCard(Game.communityCards.get(i));

        //Calculates The Expected Score Of The Custom Deck.(i.e. expected score of community cards)
        int score = HandCalculator.calculateExpectedScore(HandCalculator.calculateProbabilities(customDeck, 10000));

        if(player.getDecision() != null) {
            //Multiplies The Expected Card Score With A Coefficient Of Opponents' Move.
            if (player.getDecision().equals("check")) {
                return (int) (score * ((double) (100 + checkCoefficient) / 100));
            } else if (player.getDecision().equals("call")) {
                return (int) (score * ((double) (100 + callCoefficient) / 100));
            } else if (player.getDecision().equals("raise")) {
                double betFraction = (double) player.getTotalBetMade() / (player.getBalance() + player.getTotalBetMade());
                double calculatedCoefficient = raiseLowerCoefficient + ((raiseHigherCoefficient - raiseLowerCoefficient) * betFraction);
                return (int) (score * ((double) (100 + calculatedCoefficient) / 100));
            } else if (player.getDecision().equals("allin")) {
                return (int) (score * ((double) (100 + allinCoefficient) / 100));
            }
        }

        //Returns The Final Expected Score.
        return score;
    }

    public int getBotInput(int maxBet, GameCycle gameCycle){

        Iterator<AbstractPlayer> iter = gameCycle.iterator();
        ArrayList<Integer> opponentScores = new ArrayList<>();

        //Calculates All Opponents' Expected Card Values And Store Them In A ArrayList.
        while(iter.hasNext()){
            AbstractPlayer currPlayer = iter.next();
            if(currPlayer.equals(this) || currPlayer.isFold())
                continue;

            if(currPlayer.isPlayed())
                opponentScores.add(guessOpponentScore(currPlayer));
        }

        //Calculates The Max Gap Between Opponents And Itself.
        int personalScore = calculateScore();
        int upperGap = 0;
        int lowerGap = 0;

        for(int i = 0; i < opponentScores.size(); i++){
            if(opponentScores.get(i) > personalScore) {
                if (opponentScores.get(i) - personalScore > upperGap)
                    upperGap = opponentScores.get(i) - personalScore;
            }
            else if(opponentScores.get(i) < personalScore) {
                if (opponentScores.get(i) - personalScore < lowerGap)
                    lowerGap = personalScore - opponentScores.get(i);
            }
        }

        boolean threat = false;
        int maxGap = lowerGap;

        if(upperGap > lowerGap) {
            threat = true;
            maxGap = upperGap;
        }

        //Make A Decision Due To Max Gap Between Opponents
        if(maxGap >= largeInterval){
            if(threat)
                return fold();
            else
                return allIn();
        }
        else if(maxGap >= midInterval){
            if(threat) {
                if (maxBet == 0)
                    return check();
                else
                    return fold();
            }
            else
                return calculateRaise(maxBet, maxGap);

        }
        else if(maxGap >= smallInterval){
            if (maxBet == 0)
                return check();
            else
                return call(maxBet);
        }
        else{

            Random generator = new Random();
            int randomNum = generator.nextInt(1000);
            //AllIn
            if(randomNum<7){
                return allIn();
            }
            //Fold
            else if (randomNum<100){
                return fold();
            }
            //Raise
            else if(randomNum<200){
                return calculateRaise(maxBet, maxGap);
            }
            //Check/Call
            else{
                if (maxBet == 0)
                    return check();
                else
                    return call(maxBet);
            }
        }
    }

    public int calculateRaise(int maxBet, int maxGap){
        if(maxGap >=  ((double) 3/4) * midInterval) {
            if(getBalance()/10 > 2 *maxBet)
                return raise(getBalance()/10);
            else
                return call(maxBet);
        }
        else if(maxGap >= ((double)2/4) * midInterval) {
            if(getBalance()/15 > 2 * maxBet)
                return raise(getBalance()/15);
            else
                return call(maxBet);
        }
        else if(maxGap >= ((double)1/4) * midInterval) {
            if(getBalance()/20 > 2 * maxBet)
                return raise(getBalance()/20);
            else
                return call(maxBet);
        }
        else{
            if (maxBet == 0)
                return check();
            else
                return call(maxBet);
        }
    }
}
