//********************************************************************************************************************************************
// Player.java         Author:Ali Berk Karaarslan     Date:11.04.2024
//
// One Of The Classes Of Poker Project
//********************************************************************************************************************************************

import java.util.Scanner;

public class Player extends AbstractPlayer{

    //Default Constructor. Creates Player With Name And Balance.
    public Player(String name, int balance){
        super(name, balance);
    }

    public Player(){
        super();
    }

    //Gets Player Input On Terminal.
    public int makeDecision(int maxBet){
        Scanner scan = new Scanner(System.in);

        //If Player's Bet Is Less Than The Current Bet Or No One Raised The Bet Yet.
        if(getTotalBetMade() < maxBet || maxBet == 0){

            //If There Is Not Enough Money To Raise Or Call. Player Could Fold Or All-In.
            if(getBalance() <= maxBet - getTotalBetMade()){
                System.out.println("" + getName() + " (1)fold (3)allin");
            }
            else {

                //If No One Raised The Bet. Player Could Fold, Check Or Raise.
                if (maxBet == 0)
                    System.out.println("" + getName() + " (1)fold (2)check (3)raise");

                    //If Someone Raised The Bet. Player Could Fold, Call Or Raise.
                else
                    System.out.println("" + getName() + " (1)fold (2)call (3)raise");
            }

            int decision = scan.nextInt(); scan.nextLine(); //Gets the players' decision. Which button did player pressed.

            //Player Pressed Fold Button.
            if(decision == 1)
                return fold();

                //Player Pressed The Check Or Call Button
            else if(decision == 2){

                //Player Pressed Check Button. (No one Raised The Bet Yet)
                if(maxBet == 0)
                    return check();

                    //Player Pressed The Call Button. (Completes Its Bet To Max Bet)
                else
                    return call(maxBet);
            }

            //Player Pressed The Raise Button.
            else if(decision == 3){
                if(maxBet >= getBalance())
                    return allIn();

                while (true) {
                    System.out.println("Enter number: At least " + 2 * maxBet);
                    int newBet = scan.nextInt(); scan.nextLine();  //Gets The Desired Raise Amount.

                    //Player must enter at least 2 times amount of max bet.
                    if(newBet < 2 * maxBet || newBet > getBalance())
                        System.out.println("Wrong Amount Entered. Please Enter Again:");

                    //If Player Raised The Bet To Its Balance (i.e. all in)
                    else if(newBet >= getBalance())
                        return allIn();

                    //Player Raised The Bet.
                    else
                        return raise(newBet);
                }
            }
        }
        return 0;
    }
}
