//********************************************************************************************************************************************
// AbstractPlayer.java         Author:Ali Berk Karaarslan     Date:11.04.2024
//
// One Of The Classes Of Poker Project
//********************************************************************************************************************************************

import java.util.ArrayList;
public class AbstractPlayer {
    private String name = null;   //Stores the name of the Player.
    private int balance = -1;   //Stores the balance of the Player. (i.e. total money of the Player)
    private ArrayList<Card> cards = new ArrayList<>(2);   //Stores the 2 card of the Player. Known as Hole Cards.

    //Roles Of The Player.
    private boolean dealer = false;   //Checks, if the player is dealer.
    private boolean smallBlind = false;   //Checks, if the player is smallBlind.
    private boolean bigBlind = false;   //Checks, if the player is dealer.

    private int totalBetMade = 0;   //Stores the total bet made.

    private boolean played = false;   //Checks, if player played.
    private boolean fold = false;    //Checks, if player fold.
    private boolean allIn = false;   //Checks, if player went allIn.

    private String decision = null;   //Stores player's decision. "fold", "check", "call", "raise", "allin"

    //Empty Constructor.
    public AbstractPlayer(){}

    //Default Constructor. Creates Player With Name And Balance.
    public AbstractPlayer(String name, int balance){
        this.name = name;
        this.balance = balance;
    }

    //Gives Card To Player. Could Not Give More Than 2 Cards.
    public void giveCard(Card card){
        if(cards.size() < 2)
            cards.add(card);
        else
            System.out.println("Invalid Number Of Cards Given To: " + name);
    }

    public int calculateScore(){
        //Gets The Community Cards And Puts Them Into A Empty Custom Deck.
        Deck customDeck =  new Deck(true);
        for(int i = 0; i < Game.communityCards.size(); i++)
            customDeck.insertCard(Game.communityCards.get(i));

        for(int i = 0; i < cards.size(); i++)
            customDeck.insertCard(cards.get(i));

        return HandCalculator.guessHandScore(customDeck);
    }

    /* MUTATOR METHODS */

    public void setName(String name){
        this.name = name;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public ArrayList<Card> removeCards(){
        ArrayList<Card> temp = cards;
        cards = new ArrayList<>();
        return temp;
    }

    public void setDealer(boolean dealer){
        this.dealer = dealer;
    }

    public void setSmallBlind(boolean smallBlind){
        this.smallBlind = smallBlind;
    }

    public void setBigBlind(boolean bigBlind){
        this.bigBlind = bigBlind;
    }

    public void setTotalBetMade(int totalBetMade){
        this.totalBetMade = totalBetMade;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }

    public void setFold(boolean fold) {
        this.fold = fold;
    }

    public void setAllIn(boolean allIn) {
        this.allIn = allIn;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }


    /* ACCESSOR METHODS */

    public String getName(){
        return name;
    }

    public int getBalance(){
        return balance;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public boolean isDealer(){
        return dealer;
    }

    public boolean isSmallBlind(){
        return smallBlind;
    }

    public boolean isBigBlind(){
        return bigBlind;
    }

    public int getTotalBetMade(){
        return totalBetMade;
    }

    public boolean isPlayed() {
        return played;
    }

    public boolean isFold() {
        return fold;
    }

    public boolean isAllIn() {
        return allIn;
    }

    public String getDecision() {
        return decision;
    }

    public String toString(){
        String str = "Name: " + name + ", Balance: " + balance;

        if(Game.debugMode && cards.size() == 2)
            str += ", Cards: " + cards.get(0) + " " + cards.get(1);

        str += ", Total Bet Made: " + totalBetMade;

        if(fold) str += ", Fold";
        if(allIn) str += ", All In";

        return str;
    }

    /* ACTIONS */

    //Could Be Used Anytime. Quits The Current Round.
    public int fold() {
        played=true;
        fold = true;
        decision = "fold";
        return totalBetMade;
    }

    //Could Be Used If Nobody Has Yet Made A Bet. Declines To Bet But Keep Their Cards (Still In Game).
    public int check(){
        played=true;
        decision = "check";
        return 0;
    }

    //Could Be Used If Nobody Has Yet Made A Bet.
    public int bet(int amount){
        //If Player Tries To Bet More Than Its Balance, Sets The Amount To Its Balance.
        if(amount - totalBetMade > balance)
            amount = balance + totalBetMade;

        played=true;
        balance -= amount - totalBetMade;

        //If Player Bets All Of Its Money, (means allin)
        if(balance == 0) {
            decision = "allin";
            allIn = true;
        }

        setTotalBetMade(amount);
        return (amount);
    }

    //Could Be Used If Previous Players Bet. Matches The Amount Of The Previous Player Has Bet.
    public int call(int amount){
        played=true;
        decision = "call";
        return bet(amount);
    }

    //Could Be Used Anytime. But Must Increase The Previous Bet.
    public int raise(int amount){
        played=true;
        decision = "raise";
        return bet(amount);
    }

    //Could Be Used Anytime. Increases The Bet To Its Balance.
    public int allIn(){
        played=true;
        allIn = true;
        decision = "allin";
        return bet(balance+totalBetMade);
    }
}
