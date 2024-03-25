import java.util.ArrayList;
import java.util.Iterator;

public class PokerBot extends AbstractPlayer {

    private int checkCoefficient = 15;
    private int callCoefficient = 20;
    private int raiseLowerCoefficient = 30;
    private int raiseHigherCoefficient = 40;
    private int allinCoefficient = 45;

    private int smallInterval = 100;
    private int midInterval = 200;
    private int largeInterval = 300;

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

        //Multiplies The Expected Card Score With A Coefficient Of Opponents' Move.
        if(player.getDecision().equals("check")){
            return (int) (score * ((double)(100 + checkCoefficient) / 100));
        }
        else if(player.getDecision().equals("call")){
            return (int) (score * ((double)(100 + callCoefficient) / 100));
        }
        else if(player.getDecision().equals("raise")){
            double betFraction = (double)player.getTotalBetMade() / (player.getBalance() + player.getTotalBetMade());
            double calculatedCoefficient = raiseLowerCoefficient + ((raiseHigherCoefficient - raiseLowerCoefficient) * betFraction);
            return (int) (score * ((double)(100 + calculatedCoefficient) / 100));
        }
        else if(player.getDecision().equals("allin")){
            return (int) (score * ((double)(100 + allinCoefficient) / 100));
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
            if(opponentScores.get(i) - personalScore > upperGap && opponentScores.get(i) - personalScore > 0)
                upperGap = opponentScores.get(i) - personalScore;

            if(opponentScores.get(i) - personalScore  < lowerGap && opponentScores.get(i) - personalScore < 0)
                lowerGap = opponentScores.get(i) - personalScore;
        }

        int maxGap;
        if(upperGap > Math.abs(lowerGap))
            maxGap = upperGap;
        else
            maxGap = lowerGap;

        //Make A Decision Due To Max Gap Between Opponents
        if(Math.abs(maxGap) >= largeInterval){

            if(maxGap<0)
                //return allIn();
                return fold();
            else
                return fold();

        }
        else if(Math.abs(maxGap) >= midInterval){
//            if(maxGap<0){
//            }
//            else{
//
//            }

            if(maxBet == 0)
                return check();

            else
                return call(maxBet);
        }
        else if(Math.abs(maxGap) >= smallInterval){

            if(maxBet == 0)
                return check();

            else
                return call(maxBet);

        }
        else{
//            if(maxGap<0){
//
//            }
//            else{
//
//            }

            if(maxBet == 0)
                return check();

            else
                return call(maxBet);
        }

    }
}
