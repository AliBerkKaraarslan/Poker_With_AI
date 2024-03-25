import java.util.*;

public class Game {

    static boolean debugMode = false;   //When The Debug Mode Is Enabled, All Players' Cards Are Shown.

    static GameCycle gameCycle = new GameCycle();   //Game Cycle Of The Game.
    private ArrayList<AbstractPlayer> players = new ArrayList<>();  //Stores The Players.

    private Deck deck = new Deck();   //Deck Of The Game
    static ArrayList<Card> communityCards = new ArrayList<>();   //Stores The Community Cards.

    static HashMap<ArrayList<AbstractPlayer>, Integer> pots = new HashMap<>();   //Stores The Main And Side Pots. Player ArrayList Stores the player those shares the pots.
    private ArrayList<AbstractPlayer> activePlayers;   //Stores The Active Players (i.e. players that does not fold or all in)

    private long amountOfDeciding = 1000;   //Determines the deciding amount of the PokerBots.
    private long amountOfWaiting = 1000;   //Determines the waiting amount of the PokerBots after they decide.

    GUI gui;   //GUI Of The Game.

    public Game(){
        setUpGame();
    }

    //Refreshes The GUI.
    public void refreshGui(){
        gui.panel.repaint();
    }

    //Sets Up The Game. Add Players To Game.
    public void setUpGame(){

      
        addPlayer("Bot4", 10000, true);
        addPlayer("Player", 10000, false);
        addPlayer("Bot1", 10000, true);
        addPlayer("Bot2", 10000, true);
        addPlayer("Bot3", 10000, true);
        

        gui = new GUI();  //Setting the gui.
        setUpRound();
    }

    //Sets The Next Round. Gives Roles To Players(Dealer, Small Blind etc.), Then Deals The Cards.
    public void setUpRound(){
        removeEliminatedPlayers();   //Removes The Players With Zero Money.

        //Setting Game Cycle
        gameCycle.rotate();   //Changes The Head Player
        gameCycle.setPlayerRoles();
        clearCommunityCards();  //Clears The Previous Round's Community Cards.
        dealTheCards();

        //If There Is Only One Player Left In The Game. Finishes The Game.
        if(players.size() < 2) {
            gui.gameEnded = true;
            refreshGui();

            System.out.println("GAME ENDED. MADE BY ALI BERK. 2024 MUSIC");
            return;
        }

        gui.roundEnded = false;
        gui.winner = null;   //Sets GUI's Winner To Null.

        //Creating The Main Pot With All The Players.
        pots = new HashMap<>();
        activePlayers = gameCycle.getActivePlayers();
        pots.put(activePlayers, 0);

        gui.buttonPanel.hideAllButtons();
        refreshGui();

        //Starts The Round.
        playTheRound(100,200);
    }

    //Plays One Round From Start To Finish.
    public void playTheRound(int smallBlind, int bigBlind){

        Iterator<AbstractPlayer> iter = gameCycle.iterator();
        int maxBet = 0;   //Stores the max bet made so far.

        //Blind Bets Are Made Here. Works Once In a Round.
        while (iter.hasNext()) {
            AbstractPlayer currPlayer = iter.next();

            //Making Small Blind.
            if (currPlayer.isSmallBlind()) {
                currPlayer.raise(smallBlind);
                increasePot(smallBlind);
            }

            //Making Big Blind.
            if (currPlayer.isBigBlind()) {
                resetPlayedPlayers();
                currPlayer.raise(bigBlind);
                maxBet = bigBlind;
                increasePot(bigBlind);
                break;
            }
        }

        //Resets Iterator If There Is Less Than 4 Players. (Its because first 2 players are automatically bets (small and big blinds))
        if(gameCycle.getActivePlayers().size() < 4)
            iter = gameCycle.iterator();

        //Works Until Round Ended.
        while(true) {

            //Traverses Through Players.
            while (iter.hasNext()) {
                AbstractPlayer currPlayer = iter.next();

                //Skips The Fold Players' Turn.
                if (currPlayer.isFold() || currPlayer.isAllIn()) {
                    //If It is The Last Player Of Cycle, Resets The Iterator.
                    if(!iter.hasNext())
                        iter = gameCycle.iterator();

                    continue;
                }

                System.out.println("---------------------------------------------------------------");
                System.out.println(gameCycle);
                System.out.println("Total pot: " + pots.values());
                revealCommunityCards();
                System.out.println("Max Bet So Far: " + maxBet);
                System.out.println(currPlayer.getName() + "'s current bet:  " + currPlayer.getTotalBetMade());
                System.out.println(currPlayer.getName() + "'s cards:  " + currPlayer.getCards());

                //Gets New Decision From Player, If Player Did Not Play.
                if (!currPlayer.isPlayed()) {

                    int playersOldBet = currPlayer.getTotalBetMade();   //Stores the old bet that player made
                    int playersNewBet = 0;

                    //Gets Decision From The Real Player
                    if(currPlayer instanceof Player)
                        playersNewBet = gui.buttonPanel.getPlayerInput(currPlayer, maxBet);  //Stores the new bet that player made

                    // Gets Decision From The PokerBot
                    else if(currPlayer instanceof PokerBot) {
                        gui.currentPlayer = currPlayer;

                        long start = System.currentTimeMillis();
                        playersNewBet = ((PokerBot) currPlayer).getBotInput(maxBet, gameCycle);
                        long end = System.currentTimeMillis();

                        if ( (end - start) < amountOfDeciding){
                            try {Thread.sleep((amountOfDeciding - (end - start)));}
                            catch (InterruptedException e) {throw new RuntimeException(e);}
                        }
                    }

                    //int playersNewBet = currPlayer.makeDecision(maxBet);   //If Player Wants To Play On Terminal.
                    //If A Player Raises The Bet, Updates The Max Bet And Reset Other Players Played Flag.
                    if (playersNewBet > maxBet) {
                        maxBet = playersNewBet;
                        resetPlayedPlayers();
                        currPlayer.setPlayed(true);
                    }

                    if(playersOldBet > playersNewBet)
                        increasePot(maxBet); //Increases the total bet of the round by players' bet difference from previous bet
                    else
                        increasePot(playersNewBet - playersOldBet); //Increases the total bet of the round by players' bet difference from previous bet

                    refreshGui();

                    try {Thread.sleep(amountOfWaiting);}
                    catch (InterruptedException e) {throw new RuntimeException(e);}
                }

                //If There Is Only One Player Left Who Does Not Fold.
                if (gameCycle.isOnePlayerLeft()){
                    calculateWinners();
                    return;
                }

                //If All Players Played, Resets The Iterator To The Left Of The Dealer, Ends The "Tour".
                if(checkPlayersPlayed()){
                    updatePots();

                    iter = gameCycle.iterator();
                    if(iter.hasNext()) iter.next();

                    //If There Is Only One Active Player Left In The Game, Ends The Round.
                    if (gameCycle.getActivePlayers().size() < 2){
                        calculateWinners();
                        return;
                    }

                    break;
                }

                //Resets The Iterator If It Reached The End.
                if(!iter.hasNext()){
                    iter = gameCycle.iterator();
                }
            }

            //Resets Parameters Of The Tour
            resetPlayedPlayers();
            resetPlayerBets();
            resetPlayerDecisions();

            //If All The Community Cards Revealed And All The Bets Made, Ends The Round To Reveal Winner.
            if(communityCards.size() == 5) {
                calculateWinners();
                return;
            }

            //Increasing Revealed Community Cards Count
            if(communityCards.size() == 0){
                communityCards.add(deck.drawCard());
                communityCards.add(deck.drawCard());
                communityCards.add(deck.drawCard());
            }
            else
                communityCards.add(deck.drawCard());

            maxBet = 0;
        }
    }

    //Ends The Round
    public void endRound(){

        gui.roundEnded = true;
        gui.buttonPanel.continueNextRound();   //Waits Until Player Presses Continue Button

        //Reset Player Parameters.
        for(AbstractPlayer currPlayer : players){
            currPlayer.setPlayed(false);
            currPlayer.setFold(false);
            currPlayer.setAllIn(false);
        }

        setUpRound();

        /*If Player Wants To Play It In Terminal*/
        //Scanner scan = new Scanner(System.in);
        //System.out.println("Press 0 to Continue");
        //if(scan.nextInt() == 0)
        //    setUpRound();
    }

    //Deals The Cards. 5 Community Cards and 2 Card For Each Player.
    public void dealTheCards(){
        deck = new Deck();   //Resets The Deck.
        deck.shuffle();

        //Deals 2 Card For The Players.
        Iterator<AbstractPlayer> iter = gameCycle.iterator();

        while(iter.hasNext()){
            AbstractPlayer currPlayer = iter.next();
            currPlayer.removeCards();
            currPlayer.giveCard(deck.drawCard());
            currPlayer.giveCard(deck.drawCard());
        }
    }

    //Creates And Adds Player Into The Game.
    public AbstractPlayer addPlayer(String name, int balance, boolean bot){

        AbstractPlayer newPlayer;

        if(!bot)
            newPlayer = new Player(name, balance);
        else
            newPlayer = new PokerBot(name, balance);

        gameCycle.addLast(newPlayer);
        players.add(newPlayer);
        return newPlayer;
    }

    //Removes Player From The Game.
    public AbstractPlayer removePlayer(AbstractPlayer player){
        gameCycle.removePlayer(player);
        players.remove(player);
        return player;
    }

    //Shows The Community Cards.
    public String revealCommunityCards(){
        String returnedString = "Community Cards: ";

        for(int i = 0; i < communityCards.size() ; i++){
            returnedString = returnedString.concat(communityCards.get(i) + " ");
        }
        System.out.println(returnedString);
        refreshGui();
        return returnedString;
    }

    //Clears All The Community Cards.
    public void clearCommunityCards(){
        communityCards = new ArrayList<>();
    }

    //Reset All The Players' Bets For The Next 'Tour'.
    public void resetPlayerBets(){
        Iterator<AbstractPlayer> iter = gameCycle.iterator();

        while(iter.hasNext()){
            AbstractPlayer currPlayer = iter.next();
            currPlayer.setTotalBetMade(0);
        }
    }

    //Reset All The Players' Decisions (Check, Call etc.) For The Next 'Tour'.
    public void resetPlayerDecisions(){
        Iterator<AbstractPlayer> iter = gameCycle.iterator();

        while(iter.hasNext()){
            AbstractPlayer currPlayer = iter.next();
            currPlayer.setDecision(null);
        }
    }

    //Reset All The Played Players Except Fold And All In Players For Next 'Tour'.
    public void resetPlayedPlayers(){
        Iterator<AbstractPlayer> iter = gameCycle.iterator();

        while(iter.hasNext()){
            AbstractPlayer currPlayer = iter.next();

            if(!currPlayer.isFold() && !currPlayer.isAllIn())
                currPlayer.setPlayed(false);
        }
    }

    //Returns True If All The Players Played.
    public boolean checkPlayersPlayed(){
        Iterator<AbstractPlayer> iter = gameCycle.iterator();

        while(iter.hasNext()){
            AbstractPlayer currPlayer = iter.next();

            if(!currPlayer.isPlayed())
                return false;
        }
        return true;
    }

    //Removes The Players With Zero Money Left (ie eliminated players)
    public void removeEliminatedPlayers(){
        ArrayList<AbstractPlayer> removedPlayers = new ArrayList<>();
        for(AbstractPlayer currPlayer : players){
            if(currPlayer.getBalance() <= 0)
                removedPlayers.add(currPlayer);
        }

        for(AbstractPlayer currPlayer : removedPlayers){
            removePlayer(currPlayer);
        }
    }

    //Updates All The Pots (main and side pots) And Updates activePlayers.
    public void updatePots(){

        //Creates Side Pot If There Is An AllIn Player.
        for (AbstractPlayer currPlayer : activePlayers) {
            if (currPlayer.isAllIn()) {
                activePlayers = gameCycle.getActivePlayers();
                pots.put(activePlayers, 0);
            }
        }
    }

    //Increases The Current Pot By Amount.
    public void increasePot(int amount){
        int currentPot = pots.remove(activePlayers);   //Gets The Current Pot
        currentPot += amount;
        pots.put(activePlayers, currentPot);
     }

    //Calculates Winners And Returns It.
    public void calculateWinners() {
        revealCommunityCards();

        Set<ArrayList<AbstractPlayer>> potsSet = pots.keySet();   //Stores The Pots As Set.

        //Calculates All Pots' Winners
        for (ArrayList<AbstractPlayer> currentGroup : potsSet) {

            //If The Pot Does Not Have Any Money, Then Skips The Pot.
            if(pots.get(currentGroup) == 0)
                continue;

            ArrayList<AbstractPlayer> playersInGroup = new ArrayList<>();
            ArrayList<Integer> playersScores = new ArrayList<>();

            //Finds The Scores Of The Players In Each Pot.
            for (AbstractPlayer currPlayer : currentGroup) {
                if (!currPlayer.isFold()) {

                    //Creates Custom Deck With Community And Player Cards
                    Deck finalCardsDeck = new Deck(true);
                    while(communityCards.size() < 5)
                        communityCards.add(deck.drawCard());   //Draws Community Cards If It Did Not Finish Drawing.

                    finalCardsDeck.insertCard(communityCards.get(0));
                    finalCardsDeck.insertCard(communityCards.get(1));
                    finalCardsDeck.insertCard(communityCards.get(2));
                    finalCardsDeck.insertCard(communityCards.get(3));
                    finalCardsDeck.insertCard(communityCards.get(4));
                    finalCardsDeck.insertCard(currPlayer.getCards().get(0));
                    finalCardsDeck.insertCard(currPlayer.getCards().get(1));

                    //Adds Player And It's Score To Corresponded ArrayLists.
                    playersInGroup.add(currPlayer);
                    int handScore = HandCalculator.calculateHandScore(finalCardsDeck);
                    playersScores.add(handScore);
                }
            }

            //Sorts The Scores (With Corresponded Player).
            for (int i = 1; i < playersScores.size(); i++) {
                int currScore = playersScores.get(i);
                int j = i - 1;
                while (j >= 0 && currScore > playersScores.get(j)) {
                    Collections.swap(playersScores, j, j + 1);
                    Collections.swap(playersInGroup, j, j + 1);
                    j--;
                }
            }

            //Calculates Number Of Equal Scores.
            int potShareCount = 1;
            for(int i = 1 ; i < playersScores.size() ; i++){
                if(playersScores.get(0) == playersScores.get(i))
                    potShareCount++;
            }

            //Reveals Winners.
            for(int i = 0; i < potShareCount ; i++) {
                //Updates The Winners Balance And Calls Reveal Winner.
                playersInGroup.get(i).setBalance(playersInGroup.get(i).getBalance() + (pots.get(currentGroup) / potShareCount));
                revealWinner(playersInGroup.get(i), (pots.get(currentGroup) / potShareCount));

                //Shows The Biggest Pots Winner On Screen.
                if(i == 0){
                    gui.winner = playersInGroup.get(i);
                    gui.winnerMoney = (pots.get(currentGroup) / potShareCount);
                    gui.winningType = HandCalculator.calculateHandType(playersScores.get(0));
                    refreshGui();
                }
            }
        }
        endRound();
    }

    //Reveals The Winner
    public void revealWinner(AbstractPlayer winner, int money){
        System.out.println("WINNER: "+ winner.getName() + winner.getCards() + ", Total Money Won: " + money);
    }
}