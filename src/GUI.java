//********************************************************************************************************************************************
// GUI.java         Author:Ali Berk Karaarslan     Date:11.04.2024
//
// One Of The Classes Of Poker Project
//********************************************************************************************************************************************

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class GUI extends JFrame{

    //Game Parameters.
    boolean roundEnded = false;
    boolean gameEnded = false;

    //Player Variables.
    AbstractPlayer currentPlayer = null;   //Stores the current Player.
    AbstractPlayer winner;   //Stores the winner Player.
    int winnerMoney;   //Stores how much money winner won.
    String winningType;   //Stores the winning type. Two Pair, Straight , Flush etc.

    //Gui Parameters.
    MainPanel panel = new MainPanel();
    ButtonPanel buttonPanel = new ButtonPanel();

    public GUI(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Poker by Ali Berk Karaarslan");
        setResizable(false);
        add(panel);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        getContentPane().setLayout(null);

        if(Game.showGui)
            setVisible(true);
    }

    //Paints All The Components Of The Game. Cards, Bets, Winners etc.
    class MainPanel extends JPanel{

        boolean showGrid = false;

        int screenWidth = 1280;
        int screenHeight = 720;
        int gridSize = 20;

        int playerCount = Game.gameCycle.size();

        //TABLE VARIABLES
        int tableWidth = screenWidth - (22*gridSize);
        int tableLength = screenHeight - (10*gridSize);

        double horizontalRadiusEllipse = (double) tableWidth / 2;
        double verticalRadiusEllipse = (double) tableLength / 2;
        double startingAngle = 90;   //Stores The Starting Angle Of The Cards.

        Color backgroundColor = new Color(63, 63, 63);
        Color tableFrameColor = new Color(82, 44, 0);
        Color tableColor = new Color(42, 84, 30);

        //CARD VARIABLES
        int cardLength = 4 * gridSize;
        int cardWidth = 3 * gridSize;
        int cardGapWidth = gridSize / 2;

        Font cardFont = new Font("Monospaced", Font.BOLD, 33);
        Font nameFont = new Font("TimesRoman", Font.BOLD, 18);
        Font betFont = new Font("TimesRoman", Font.BOLD, 20);

        Color cardColor = Color.WHITE;
        Color closedCardColor = new Color(0, 50, 136);
        Color nameColor = new Color(255, 219, 117);
        Color betColor = Color.WHITE;
        Color currentPlayerColor = new Color(255, 125, 21);

        public MainPanel(){
            setPreferredSize(new Dimension(screenWidth, screenHeight));
            setBackground(backgroundColor);
            setLayout(new BorderLayout());
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            //Updates The player count.
            playerCount = Game.gameCycle.size();

            //Paints The Table.
            paintTable(g);

            //Paints The Pots.
            paintPots(g);

            //Paints The Community Cards.
            paintCommunityCards(g);

            //Paints The Winner If Game Or Round Ended.
            if (roundEnded || gameEnded) {
                paintWinner(g, winner, winnerMoney, winningType);

                if(gameEnded)
                    return;
            }

            //Calculates All Players Positions Around The Table and Paints Cards According To These Positions.
            double unitAngle = (double) 360 / playerCount;   //Stores The Angle Between To Consecutive Players.
            double currentAngle = startingAngle;

            Iterator<AbstractPlayer> iter = Game.gameCycle.iterator();
            while(iter.hasNext()){
                AbstractPlayer currPlayer = iter.next();

                double xPosition = Math.sqrt((horizontalRadiusEllipse * horizontalRadiusEllipse * verticalRadiusEllipse * verticalRadiusEllipse) / ((verticalRadiusEllipse * verticalRadiusEllipse) + (horizontalRadiusEllipse * horizontalRadiusEllipse * Math.tan((Math.toRadians(currentAngle))) * Math.tan((Math.toRadians(currentAngle))))) );
                double yPosition = Math.tan((Math.toRadians(currentAngle))) * xPosition;

                if(currentAngle > 90 && currentAngle <= 270){
                    xPosition *= -1;
                    yPosition *= -1;
                }

                xPosition += (double) screenWidth / 2;
                yPosition += (double) screenHeight / 2;

                currentAngle = (currentAngle + unitAngle) % 360;    //Moves To Next Player.

                paintPlayerCards(g, currPlayer, (int) xPosition, (int) yPosition);   //Paints The Player Cards.
            }

            //Draws a Grid.
            if(showGrid) {
                g.setColor(Color.LIGHT_GRAY);
                for (int i = 0; i < screenWidth; i += gridSize) {
                    g.drawLine(i, 0, i, screenHeight);
                }

                for (int i = 0; i < screenHeight; i += gridSize) {
                    g.drawLine(0, i, screenWidth, i);
                }
            }
        }

        //Paints The Table.
        public void paintTable(Graphics g){
            //Draws The Frame Of The Table.
            g.setColor(tableFrameColor);
            g.fillOval(screenWidth /2 - tableWidth/2, screenHeight /2 - tableLength/2, tableWidth, tableLength);

            //Draws The Table.
            g.setColor(tableColor);
            g.fillOval(screenWidth /2 - tableWidth/2 + 2 * gridSize, screenHeight /2 - tableLength/2 + 2 * gridSize, tableWidth - (4 * gridSize), tableLength - (4 * gridSize));
        }

        //Paints Player Cards. Also Calls Paint Names, Balances, Bets Methods.
        public void paintPlayerCards(Graphics g, AbstractPlayer player, int x, int y){

            //If Player Did Not Fold, Draws Its Card With Name, Balance, And Bet.
            if(!player.isFold()) {

                paintPlayerNames(g, player, x, y);
                paintPlayerBalances(g, player, x, y);
                paintPlayerBets(g, player, x, y);

                ArrayList<Card> cards = player.getCards();

                //Changes The Current Player's Card Color.
                if(player.equals(currentPlayer)) {
                    g.setColor(currentPlayerColor);

                    //If Cards Are In The Upper Screen.
                    if(y < screenHeight / 2) {
                        g.fillRect(x - (cardGapWidth + cardWidth) - 5, y - gridSize - 5, cardWidth + 10, cardLength+ 10);
                        g.fillRect(x + cardGapWidth - 5, y - gridSize - 5, cardWidth+ 10, cardLength+ 10);
                    }
                    //If Cards Are In The Lower Screen.
                    else {
                        g.fillRect(x - (cardGapWidth + cardWidth) - 5, y - (3 * gridSize) - 5, cardWidth+ 10, cardLength+ 10);
                        g.fillRect(x + cardGapWidth - 5, y - (3 * gridSize) - 5, cardWidth+ 10, cardLength+ 10);
                    }
                }

                g.setColor(cardColor);
                g.setFont(cardFont);

                if(!Game.debugMode && player instanceof PokerBot && !gameEnded && !roundEnded)
                    g.setColor(closedCardColor);

                //Changes The Winner's Card Color.
                if(player.equals(winner))
                    g.setColor(Color.GREEN);

                //If Cards Are In The Upper Screen.
                if(y < screenHeight / 2) {
                    g.fillRect(x - (cardGapWidth + cardWidth), y - gridSize, cardWidth, cardLength);
                    g.fillRect(x + cardGapWidth, y - gridSize, cardWidth, cardLength);
                }
                //If Cards Are In The Lower Screen.
                else {
                    g.fillRect(x - (cardGapWidth + cardWidth), y - (3 * gridSize), cardWidth, cardLength);
                    g.fillRect(x + cardGapWidth, y - (3 * gridSize), cardWidth, cardLength);
                }

                if(Game.debugMode || player instanceof Player || gameEnded || roundEnded) {

                    if (g instanceof Graphics2D) {
                        Graphics2D g2 = (Graphics2D) g;

                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

                        g.setColor(cardColor);
                        g.setFont(cardFont);

                        //Calculates And Draws First Card.
                        if (cards.get(0).getSuit() == 1 || cards.get(0).getSuit() == 3) g2.setColor(Color.BLACK);
                        else g2.setColor(Color.RED);

                        int stringWidth = g.getFontMetrics().stringWidth(cards.get(0).toString());
                        if (y < screenHeight / 2)
                            g2.drawString(cards.get(0).toString(), x - (cardGapWidth + cardWidth / 2 + stringWidth / 2), y + (int) (1.5 * gridSize));
                        else
                            g2.drawString(cards.get(0).toString(), x - (cardGapWidth + cardWidth / 2 + stringWidth / 2), y - (int) (0.5 * gridSize));


                        //Calculates And Draws Second Card.
                        if (cards.get(1).getSuit() == 1 || cards.get(1).getSuit() == 3) g2.setColor(Color.BLACK);
                        else g2.setColor(Color.RED);

                        stringWidth = g.getFontMetrics().stringWidth(cards.get(1).toString());
                        if (y < screenHeight / 2)
                            g2.drawString(cards.get(1).toString(), x + cardGapWidth + (cardWidth / 2 - stringWidth / 2), y + (int) (1.5 * gridSize));
                        else
                            g2.drawString(cards.get(1).toString(), x + cardGapWidth + (cardWidth / 2 - stringWidth / 2), y - (int) (0.5 * gridSize));
                    }
                }
            }

            //Draws "FOLD" If Player Fold.
            else {
                paintPlayerNames(g, player, x, y);
                paintPlayerBalances(g, player, x, y);

                if (g instanceof Graphics2D) {
                    Graphics2D g2 = (Graphics2D) g;

                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setFont(new Font("TimesRoman", Font.BOLD, 22));
                    g2.setColor(betColor);

                    int stringWidth = g.getFontMetrics().stringWidth("FOLD");

                    //If Cards Are In The Upper Screen.
                    if(y < screenHeight /2)
                        g2.drawString("FOLD", x - stringWidth / 2, y + (int)(1.5 * gridSize));

                    //If Cards Are In The Lower Screen.
                    else
                        g2.drawString("FOLD", x - stringWidth / 2, y - (int)(1.5 * gridSize));
                }
            }
        }

        //Paints The Players' Names.
        public void paintPlayerNames(Graphics g, AbstractPlayer player, int x, int y){

            if (g instanceof Graphics2D) {
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setFont(nameFont);
                g2.setColor(nameColor);

                //Calculates The Position And Draws The Player's Name.
                int stringWidth = g.getFontMetrics().stringWidth(player.getName());

                //If Cards Are In The Upper Screen.
                if(y < screenHeight /2)
                    g2.drawString(player.getName(), x - stringWidth / 2, y - (2 * gridSize));

                //If Cards Are In The Lower Screen.
                else
                    g2.drawString(player.getName(), x - stringWidth / 2, y + (2 * gridSize));
            }
        }

        //Paints The Players' Balances.
        public void paintPlayerBalances(Graphics g, AbstractPlayer player, int x, int y){

            if (g instanceof Graphics2D) {
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setFont(nameFont);
                g2.setColor(nameColor);

                //Calculates The Position And Draws The Player's Balance.
                int stringWidth = g.getFontMetrics().stringWidth("" + player.getBalance());

                //If Cards Are In The Upper Screen.
                if(y < screenHeight /2)
                    g2.drawString("" + player.getBalance(), x - stringWidth / 2, y - (3 * gridSize));

                //If Cards Are In The Lower Screen.
                else
                    g2.drawString("" + player.getBalance(), x - stringWidth / 2, y + (3 * gridSize));
            }
        }

        //Paints The Players' Bets And Bet Types.
        public void paintPlayerBets(Graphics g, AbstractPlayer player, int x, int y){

            //If Player Decided.
            if(player.getDecision() != null){

                //If Player Fold, Then Returns With Nothing. Because In Paint Cards Method, We Paint "FOLD".
                if(player.getDecision().equals("fold"))
                    return;

                if (g instanceof Graphics2D) {
                    Graphics2D g2 = (Graphics2D) g;

                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setFont(betFont);
                    g2.setColor(betColor);

                    //Calculates The Position And Draws The Player's Decision.
                    String playerDecisionStr = "";
                    if(player.getDecision().equals("check"))
                        playerDecisionStr = "CHECK";
                    else if(player.getDecision().equals("call"))
                        playerDecisionStr = "CALL " + player.getTotalBetMade();
                    else if(player.getDecision().equals("raise"))
                        playerDecisionStr = "RAISE " + player.getTotalBetMade();
                    else if(player.getDecision().equals("allin"))
                        playerDecisionStr = "ALL-IN " + player.getTotalBetMade();

                    int stringWidth = g.getFontMetrics().stringWidth(playerDecisionStr);

                    if(y < screenHeight /2)
                        g2.drawString(playerDecisionStr, x - stringWidth / 2, y + (int)(4.5 * gridSize));
                    else
                        g2.drawString(playerDecisionStr, x - stringWidth / 2, y - (4 * gridSize));
                }
            }
        }

        //Paints Desired Amount Of Community Cards.
        public void paintCommunityCards(Graphics g){
            if (g instanceof Graphics2D) {
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                //Calculates The Starting Cards' Position.
                int x = screenWidth /2 - (int)(2.5 * cardWidth) - (2 * cardGapWidth);
                int y = screenHeight /2 - cardLength/2;

                //Draws Community Cards.
                for(int i = 0; i < Game.communityCards.size(); i++){
                    g2.setColor(cardColor);
                    g2.setFont(cardFont);

                    g2.fillRect(x + (i * (cardWidth + cardGapWidth)), y, cardWidth, cardLength);

                    if (Game.communityCards.get(i).getSuit() == 1 || Game.communityCards.get(i).getSuit() == 3) g2.setColor(Color.BLACK);
                    else g2.setColor(Color.RED);

                    int stringWidth = g.getFontMetrics().stringWidth(Game.communityCards.get(i).toString());
                    g2.drawString(Game.communityCards.get(i).toString(), x + + (i * (cardWidth + cardGapWidth)) + (cardWidth/2 - stringWidth/2),  y + (int)(2.5 * gridSize));
                }
            }
        }

        //Paints The Pots.
        public void paintPots(Graphics g){

            Set<ArrayList<AbstractPlayer>> potsSet = Game.pots.keySet();   //Stores The Pots As Set.

            ArrayList<Integer> playerCounts = new ArrayList<>();
            ArrayList<Integer> allPots = new ArrayList<>();

            //Calculates All Pots' Winners
            for (ArrayList<AbstractPlayer> currentGroup : potsSet) {

                //If The Pot Does Not Have Any Money, Then Skips The Pot.
                if (Game.pots.get(currentGroup) == 0)
                    continue;

                playerCounts.add(currentGroup.size());
                allPots.add(Game.pots.get(currentGroup));
            }

            //Sorts The Scores (With Corresponded Player).
            for (int i = 1; i < playerCounts.size(); i++) {
                int currCount = playerCounts.get(i);
                int j = i - 1;
                while (j >= 0 && currCount > playerCounts.get(j)) {
                    Collections.swap(playerCounts, j, j + 1);
                    Collections.swap(allPots, j, j + 1);
                    j--;
                }
            }

            //Draws All Pots.
            for(int i = 0; i < playerCounts.size() ; i++) {
                String potType;

                if(i == 0)
                    potType = "TOTAL POT: " + allPots.get(i);
                else
                    potType = "SIDE POT " + i + ": " + allPots.get(i);

                if (g instanceof Graphics2D) {
                    Graphics2D g2 = (Graphics2D) g;

                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setFont(new Font("TimesRoman", Font.BOLD, 22));
                    g2.setColor(betColor);

                    int stringWidth = g.getFontMetrics().stringWidth(potType);
                    g2.drawString(potType, screenWidth - stringWidth - gridSize,  (int) ((i+1) * (1.5) * gridSize));
                }
            }

        }

        //Paints The Winner.
        public void paintWinner(Graphics g, AbstractPlayer player, int money, String winningType){

            if(player != null) {

                //If Game Ended.
                if(gameEnded){
                    paintTable(g);   //Clearing The Cards From Screen.

                    //Resets The Current Player
                    currentPlayer = player;
                    repaint();

                    if (g instanceof Graphics2D) {
                        Graphics2D g2 = (Graphics2D) g;

                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

                        g2.setFont(new Font("TimesRoman", Font.BOLD, 22));
                        g2.setColor(betColor);

                        //Calculates And Draws Game's Winner.
                        String winnerString = "GAME ENDED. " + player.getName() + " WON " + money;
                        String programmerString = "MADE BY ALI BERK 2024";

                        int stringWidth1 = g.getFontMetrics().stringWidth(winnerString);
                        g2.drawString(winnerString, (screenWidth / 2) - (stringWidth1 / 2), (screenHeight / 2));

                        int stringWidth2 = g.getFontMetrics().stringWidth(programmerString);
                        g2.drawString(programmerString, (screenWidth / 2) - (stringWidth2 / 2), (screenHeight / 2) + (6 * gridSize));
                    }
                }
                //If Round Ended.
                else if(roundEnded) {
                    //Resets The Current Player
                    currentPlayer = player;
                    repaint();

                    if (g instanceof Graphics2D) {
                        Graphics2D g2 = (Graphics2D) g;

                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

                        g2.setFont(new Font("TimesRoman", Font.BOLD, 22));
                        g2.setColor(betColor);

                        //Calculates And Draws The Round's Winner.
                        String winnerString = "" + player.getName() + " WON " + money + " WITH " + winningType;

                        int stringWidth = g.getFontMetrics().stringWidth(winnerString);
                        g2.drawString(winnerString, (screenWidth / 2) - (stringWidth / 2), (screenHeight / 2) + (6 * gridSize));
                    }
                }
            }
        }

    }

    //Contains All The Buttons. Show These Buttons According To Games Situations.
    class ButtonPanel extends JPanel implements ActionListener{

        //Initializes Buttons.
        JButton foldButton =  new JButton("FOLD");
        JButton checkButton  =  new JButton("CHECK");
        JButton callButton  =  new JButton("CALL");
        JButton raiseButton  =  new JButton("RAISE");
        JSlider slider  =  new JSlider(0,100,0);
        JButton continueButton = new JButton("CONTINUE");

        boolean decided = false;   //Stores If Player Pressed On Any Button Or Not.
        int playerDecision;   //Stores Which Button Player Pressed. (Fold, Check, Call. Raise)

        public ButtonPanel(){
            setBackground(new Color(63, 63, 63));
            setLayout(new FlowLayout());

            //Setting Fold Button
            foldButton.setBackground(new Color(136, 136, 136));
            add(foldButton);
            foldButton.addActionListener(this);

            //Setting Check Button
            checkButton.setBackground(new Color(136, 136, 136));
            add(checkButton);
            checkButton.addActionListener(this);

            //Setting Call Button
            callButton.setBackground(new Color(136, 136, 136));
            add(callButton);
            callButton.addActionListener(this);

            //Setting Raise Button
            raiseButton.setBackground(new Color(136, 136, 136));
            add(raiseButton);
            raiseButton.addActionListener(this);

            //Setting Slider
            slider.setBackground(new Color(136, 136, 136));
            slider.setMinorTickSpacing(10);
            slider.setMajorTickSpacing(10);
            slider.setSnapToTicks(true);
            slider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if(slider.getValue() == slider.getMaximum())
                        raiseButton.setText("ALL IN");

                    else
                        raiseButton.setText("RAISE (" + slider.getValue() + ")");
                }
            });
            add(slider);

            //Setting Continue Button
            continueButton.setBackground(new Color(136, 136, 136));
            add(continueButton);
            continueButton.addActionListener(this);

        }

        //Hides All The Buttons.
        public void hideAllButtons(){
            foldButton.setVisible(false);
            checkButton.setVisible(false);
            callButton.setVisible(false);
            raiseButton.setVisible(false);
            slider.setVisible(false);
            continueButton.setVisible(false);
        }

        //Gets Input From Player According To The Button She Pressed.
        public int getPlayerInput(AbstractPlayer player, int maxBet){
            currentPlayer = player;   //Sets given player as currentPlayer.

            //If Player's Bet Is Less Than The Current Bet Or No One Raised The Bet Yet.
            if (player.getTotalBetMade() < maxBet || maxBet == 0) {
                hideAllButtons();

                //If There Is Not Enough Money To Raise Or Call. Player Could Fold Or All-In.
                if (player.getBalance() <= maxBet - player.getTotalBetMade()) {
                    foldButton.setVisible(true);
                    raiseButton.setVisible(true);
                    raiseButton.setText("ALL IN");
                }
                else {

                    //If No One Raised The Bet. Player Could Fold, Check Or Raise.
                    if (maxBet == 0) {
                        foldButton.setVisible(true);
                        checkButton.setVisible(true);
                        raiseButton.setVisible(true);
                        slider.setVisible(true);

                        slider.setMinimum(0);
                        slider.setMaximum(player.getBalance());
                        slider.setValue(0);
                    }

                    //If Someone Raised The Bet. Player Could Fold, Call Or Raise.
                    else {
                        foldButton.setVisible(true);
                        callButton.setVisible(true);
                        raiseButton.setVisible(true);
                        slider.setVisible(true);

                        slider.setMinimum(2 * maxBet);
                        slider.setMaximum(player.getBalance());
                        slider.setValue(2 * maxBet);
                    }
                }
            }
            else{
                return 0;
            }

            //Waits Until Player Decides. (waits until player presses one of the buttons)
            decided = false;

            while(!decided) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            hideAllButtons();

            int decision = playerDecision;   //Gets the players' decision. Which button did player pressed.

            //Player Pressed Fold Button.
            if(decision == 1)
                return player.fold();

            //Player Pressed The Check Or Call Button
            else if(decision == 2){

                //Player Pressed Check Button. (No one Raised The Bet Yet)
                if(maxBet == 0)
                    return player.check();

                //Player Pressed The Call Button. (Completes Its Bet To Max Bet)
                else
                    return player.call(maxBet);
            }

            //Player Pressed The Raise Button.
            else if(decision == 3){
                int newBet = slider.getValue();   //Gets The Desired Raise Amount.

                //If Player Raised The Bet To Its Balance (i.e. all in)
                if(newBet >= player.getBalance() || raiseButton.getText().equals("ALL IN"))
                    return player.allIn();

                //Player Raised The Bet.
                else
                    return player.raise(newBet);
            }
            return 0;
        }

        //Gets Confirmation From Player To Continue.
        public boolean continueNextRound(){
            hideAllButtons();

            //Shows The Continue Button
            if(roundEnded)
                continueButton.setVisible(true);

            //Waits Until Player Decides. (waits until player presses one of the buttons)
            decided = false;

            while(!decided) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            return true;
        }

        //Actions Of Buttons.
        @Override
        public void actionPerformed(ActionEvent e) {
            //Fold Button
            if(e.getSource() == foldButton) {
                if(!decided) {
                    decided = true;
                    playerDecision = 1;
                }
            }
            //Check Button
            else if(e.getSource() == checkButton){
                if(!decided) {
                    decided = true;
                    playerDecision = 2;
                }
            }
            //Call Button
            else if(e.getSource() == callButton){
                if(!decided) {
                    decided = true;
                    playerDecision = 2;
                }
            }
            //Raise Button
            else if(e.getSource() == raiseButton){
                if(!decided) {
                    decided = true;
                    playerDecision = 3;
                }
            }
            //Continue Button
            else if(e.getSource() == continueButton){
                if(!decided) {
                    decided = true;
                }
            }
        }
    }
}
