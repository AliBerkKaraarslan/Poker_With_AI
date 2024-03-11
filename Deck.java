import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck{

    private int size = 52;  //Size of the deck.
    private ArrayList<Card> cards; //Stores the cards.

    //Default Constructor. Creates Default 52 Card Deck.
    public Deck(){
        createDeckOfCards();
    }

    //Custom Constructor. If custom Is True, Creates Empty Deck, If custom Is False, Then Creates Default Deck With Size Of 52.
    public Deck(boolean custom){
        if(custom){
            cards = new ArrayList<>();
            size = 0;
        }
        else{
            createDeckOfCards();
        }
    }

    //Creates Default Deck Of Cards Of 52.
    private void createDeckOfCards(){
        cards = new ArrayList<>();  //Removes the elements of current Deck.

        //Creating the cards
        for(int i = 1; i <= 4; i++){
            for(int j = 2; j <= 14; j++){
                cards.add(new Card(i, j));
            }
        }

        sortByRank();
    }

    //Shuffles The Deck With Fisher-Yates Shuffling Algorithm
    public void shuffle(){
        Random generator = new Random();
        for(int i = size-1; i > 0; i--){
            int index = generator.nextInt(i+1);
            Collections.swap(cards,i,index);
        }
    }

    //Draws Random Card From Deck. (removes it from deck).
    public Card drawCard(){
        Random generator = new Random();
        int index = generator.nextInt(size);
        size--;
        return cards.remove(index);
    }

    //Inserts The Desired Card To End Of The Deck.
    public void insertCard(Card card){
        cards.add(card);
        size++;
    }

    //Inserts The Desired Card To End Of The Deck.
    public void removeCard(Card card){
        cards.remove(card);
        size--;
    }

    //Returns The Card In The Index 'index'
    public Card getCard(int index){
        return cards.get(index);
    }

    //Removes The First Card And Returns It.
    public Card removeFirst(){
        Card temp = cards.remove(0);
        size--;
        return temp;
    }

    //Returns The First Card.
    public Card getFirst(){
        return cards.get(0);
    }

    //Returns The Size Of The Deck.
    public int getSize() {
        return size;
    }

    //Sorts The Deck By Suits (Larger to Smaller). If Cards' Ranks Are Equal, Then Sorts Them By Their Ranks. Uses Insertion Sort.
    public void sortBySuit(){

        for(int i = 1; i < cards.size() ; i++){
            Card currCard = cards.get(i);
            int j = i-1;
            while(j>=0 && currCard.getSuit() >= cards.get(j).getSuit()){

                //If Two Cards' Suits Are Equal, Sort Them By Their Ranks.
                if(currCard.getSuit() == cards.get(j).getSuit()){
                    if(currCard.getRank() < cards.get(j).getRank()){
                        j--;
                        continue;
                    }
                }
                Collections.swap(cards,j,j+1);
                j--;
            }
        }
    }

    //Sorts The Deck By Ranks (Larger to Smaller). If Cards' Ranks Are Equal, Then Sorts Them By Their Suit. Uses Insertion Sort.
    public void sortByRank(){

        for(int i = 1; i < cards.size() ; i++){
            Card currCard = cards.get(i);
            int j = i-1;
            while(j>=0 && currCard.getRank() >= cards.get(j).getRank()){

                //If Two Cards' Ranks Are Equal, Sort Them By Their Suits.
                if(currCard.getRank() == cards.get(j).getRank()){
                    if(currCard.getSuit() < cards.get(j).getSuit()){
                        j--;
                        continue;
                    }
                }
                Collections.swap(cards,j,j+1);
                j--;
            }
        }
    }

    //Returns The String Representation Of The Deck.
    public String toString(){
        String returnedString = "";

        for(Card currCard : cards){
            returnedString = returnedString.concat(currCard + " | ");
        }
        return returnedString;
    }

    //Returns Deck As Array.
    public Card[] toArray(){
        Card[] cardArray = new Card[size];
        for(int i = 0 ; i < size ; i++){
            cardArray[i] =  cards.get(i);
        }
        return cardArray;
    }
}
