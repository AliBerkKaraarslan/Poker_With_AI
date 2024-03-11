public class Card {

    private int suit = 0; //Suit type of the card: 1 (spades (♠)), 2 (diamonds (♦)), 3 (clubs (♣)), 4 (hearts (♥))
    private int rank = 0; //Rank type of the card: 2, 3, 4, 5, 6, 7, 8, 9, 10, 11(Jack), 12(Queen), 13(King), 14(A)

    //Empty Constructor. Creates null card.
    public Card(){}

    //Default Constructor.
    public Card(int suit, int rank){
        this.suit = suit;
        this.rank = rank;
    }

    /* MUTATOR METHODS */

    public void setSuit(int suit){
        if(suit > 5 || suit < 0)
            System.out.println("Invalid Type Of Suit Entered. Must Be In Interval 0-4.");
        else
            this.suit = suit;
    }

    public void setRank(int suit){
        if(rank > 14 || rank < 1)
            System.out.println("Invalid Type Of Rank Entered. Must Be In Interval 0-13.");
        else
            this.rank = suit;
    }

    /* ACCESSOR METHODS */

    public int getSuit(){
        return suit;
    }

    public int getRank(){
        return rank;
    }

    //Returns The String Representation Of The Card.
    public String toString(){
        return getSuitAsString(true) + getRankAsString(true);
    }

    //Returns The String Representation Of Suit. If illustrate boolean is true, then returns suit in emoji form.
    public String getSuitAsString(boolean illustrate){
        if(illustrate) {
            if (suit == 1)
                return "♠";
            else if (suit == 2)
                return "♦";
            else if (suit == 3)
                return "♣";
            else if (suit == 4)
                return "♥";
        }
        return "" + suit;
    }

    //Returns The String Representation Of Rank. If illustrate boolean is true, then returns 1,11,12,13 ranks in A,J,Q,K form.
    public String getRankAsString(boolean illustrate){
        if(illustrate) {

            if (rank == 11)
                return "J";
            else if (rank == 12)
                return "Q";
            else if (rank == 13)
                return "K";
            else if (rank == 14 || rank == 1)
                return "A";
        }
        return "" + rank;
    }
}
