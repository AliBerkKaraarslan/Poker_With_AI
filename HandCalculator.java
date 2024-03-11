import java.util.HashMap;

public class HandCalculator {

    //Calculates Given Deck's Score And Returns It.
    public static int calculateHandScore(Deck deck){

        int royalFlushScore = isRoyalFlush(deck);
        if(royalFlushScore != -1)
            return royalFlushScore;

        int straigtFlushScore = isStraightFlush(deck);
        if(straigtFlushScore != -1)
            return straigtFlushScore;

        int fourOfAKindScore = isFourOfAKind(deck);
        if(fourOfAKindScore != -1)
            return fourOfAKindScore;

        int fullHouseScore = isFullHouse(deck);
        if(fullHouseScore != -1)
            return fullHouseScore;

        int flushScore = isFlush(deck);
        if(flushScore != -1)
            return flushScore;

        int straightScore = isStraight(deck);
        if(straightScore != -1)
            return straightScore;

        int threeOfAKindScore = isThreeOfAKind(deck);
        if(threeOfAKindScore != -1)
            return threeOfAKindScore;

        int twoPairScore = isTwoPair(deck);
        if(twoPairScore != -1)
            return twoPairScore;

        int pairScore = isPair(deck);
        if(pairScore != -1)
            return pairScore;

        return isHighCard(deck);
    }

    //Returns All The Probabilities Of Deck And Returns It As A HashMap. Uses "Monte Carlo Simulations" To Calculate.
    public static HashMap<String, Double> calculateProbabilities(Deck deck, int simCount){

        HashMap<String, Integer> handTypeCounts = new HashMap<>();
        String handType;
        int score;

        //Calculates Deck's Score, Then Increases The Corresponded Hand Type Count In handTypeCount HashMap. (e.g. {STRAIGHT:596, FLUSH:142, PAIR:395})
        for(int i = 0 ; i < simCount ; i++){
            score = guessHandScore(deck);
            handType = calculateHandType(score);

            if(!handTypeCounts.containsKey(handType))
                handTypeCounts.put(handType, 1);
            else
                handTypeCounts.put(handType, handTypeCounts.get(handType)+1);
        }

        //Calculates Percentages And Puts Them In handTypePercentages HashMap. (e.g. {STRAIGHT:0.52, FLUSH:0.12, PAIR:0.36})
        HashMap<String, Double> handTypePercentages = new HashMap<>();

        for(String str : handTypeCounts.keySet())
            handTypePercentages.put(str, ((double) handTypeCounts.get(str) / simCount));

        return handTypePercentages;
    }

    //Randomly Selects Cards To Complete Deck Size To 7. Then Returns Its Calculated Hand Score.
    public static int guessHandScore(Deck deck){

        Deck newDeck = new Deck(true);   //Creates empty Deck.
        Deck fullDeck = new Deck();   //Creates regular Deck with size 52.

        for(int i = 0; i < deck.getSize(); i++) {
            newDeck.insertCard(deck.getCard(i));    //Inserts the cards of deck to newDeck.
            fullDeck.removeCard(deck.getCard(i));    //Removes the cards of deck from fullDeck to avoid same card drawing.
        }

        //Completes The Size To 7.
        while(newDeck.getSize() < 7)
            newDeck.insertCard(fullDeck.drawCard());

        //Calculates And Returns The Hand Score.
        return calculateHandScore(newDeck);
    }

    //Calculates The Expected Score Of Hand Scores.
    public static int calculateExpectedScore(HashMap<String, Double> handTypePercentages){
        int expectedScore = 0;

        for(String handType : handTypePercentages.keySet())
            expectedScore += handTypePercentages.get(handType) * getHandScore(handType);

        return expectedScore;
    }

    //Calculates Hand Type And Returns It As A String
    public static String calculateHandType(int playerScore){

        if(playerScore / 100 == 0)
            return "HIGH CARD";

        else if(playerScore / 100 == 1)
            return "PAIR";

        else if(playerScore / 100 == 2)
            return "TWO PAIR";

        else if(playerScore / 100 == 3)
            return "THREE OF A KIND";

        else if(playerScore / 100 == 4)
            return "STRAIGHT";

        else if(playerScore / 100 == 5)
            return "FLUSH";

        else if(playerScore / 100 == 6)
            return "FULL HOUSE";

        else if(playerScore / 100 == 7)
            return "FOUR OF A KIND";

        else if(playerScore / 100 == 8)
            return "STRAIGHT FLUSH";

        else if(playerScore / 100 == 9)
            return "ROYAL FLUSH";

        return null;
    }

    //Returns The Score Representation Of Given Hand Type
    public static int getHandScore(String handType){

        if(handType.equals("HIGH CARD"))
            return 0;

        else if(handType.equals("PAIR"))
            return 100;

        else if(handType.equals("TWO PAIR"))
            return 200;

        else if(handType.equals("THREE OF A KIND"))
            return 300;

        else if(handType.equals("STRAIGHT"))
            return 400;

        else if(handType.equals("FLUSH"))
            return 500;

        else if(handType.equals("FULL HOUSE"))
            return 600;

        else if(handType.equals("FOUR OF A KIND"))
            return 700;

        else if(handType.equals("STRAIGHT FLUSH"))
            return 800;

        else if(handType.equals("ROYAL FLUSH"))
            return 900;

        return -1;
    }

    /* HAND COMBINATIONS */

    //Ace, King, Queen, Jack, And 10 All The Same Suit. (Score = 900 + HighCard)
    public static int isRoyalFlush(Deck deck){
        deck.sortBySuit();
        Card[] deckArray = deck.toArray();

        for(int i = 0 ; i < deck.getSize()-4 ; i++) {
            if (deckArray[i].getRank() == 14) {
                if ((deckArray[i].getRank() - deckArray[i+4].getRank() == 4) && (deckArray[i].getSuit() - deckArray[i+4].getSuit() == 0))
                    return (900 + deckArray[i].getRank());
            }
        }
        return -1;
    }

    //Five Cards Of Sequential Rank That Are All The Same Suit. (Score = 800 + HighCard)
    public static int isStraightFlush(Deck deck){
        deck.sortBySuit();
        Card[] deckArray = deck.toArray();

        for(int i = 0 ; i < deck.getSize()-4 ; i++) {
            if ((deckArray[i].getRank() - deckArray[i+4].getRank() == 4) && (deckArray[i].getSuit() - deckArray[i+4].getSuit() == 0))
                return (800 + deckArray[i].getRank());
        }
        return -1;
    }

    //Four Cards Of The Same Rank, Plus One Of Another Rank. (Score = 700 + FourOfAKind Rank + Other Card Rank)
    public static int isFourOfAKind(Deck deck){
        deck.sortByRank();
        Card[] deckArray = deck.toArray();

        for(int i = 0 ; i < deck.getSize()-3 ; i++) {
            if ((deckArray[i].getRank() - deckArray[i+3].getRank() == 0)){
                if(i == 0)
                    return (700 + deckArray[i].getRank() + deckArray[i+5].getRank());
                else
                    return (700 + deckArray[i].getRank() + deckArray[0].getRank());
            }
        }

        return -1;
    }

    //Three Cards Of The Same Rank And Two Cards Of Another Rank. (Score = 600 + ThreeOfAKind Rank + Pair Rank)
    public static int isFullHouse(Deck deck){
        deck.sortByRank();
        Card[] deckArray = deck.toArray();

        boolean threeOfAKind = false;
        boolean pair = false;
        int threeOfAKindRank = -1;
        int pairRank = -1;

        for(int i = 0 ; i < deck.getSize()-1 ; i++) {
            if(!threeOfAKind && i < deck.getSize()-2) {
                if ((deckArray[i].getRank() - deckArray[i + 2].getRank() == 0)) {
                    threeOfAKind = true;
                    threeOfAKindRank = deckArray[i].getRank();
                    i+=1;
                    continue;
                }
            }

            if (!pair) {
                if ((deckArray[i].getRank() - deckArray[i + 1].getRank() == 0)) {
                    pair = true;
                    pairRank = deckArray[i].getRank();
                }
            }
        }

        if(threeOfAKind && pair)
            return 600 + threeOfAKindRank + pairRank;

        return -1;
    }

    //Five Cards Of The Same Suit That Are Not Of Sequential Rank. (Score = 500 + All Card Ranks)
    public static int isFlush(Deck deck){
        deck.sortBySuit();
        Card[] deckArray = deck.toArray();

        for(int i = 0 ; i < deck.getSize()-4 ; i++) {
            if (deckArray[i].getSuit() - deckArray[i+4].getSuit() == 0)
                return (500 + deckArray[i].getRank() + deckArray[i+1].getRank() + deckArray[i+2].getRank() + deckArray[i+3].getRank() + deckArray[i+4].getRank());
        }

        return -1;
    }

    //Five Cards Of Sequential Rank That Are Not All The Same Suit. (Score = 400 + HighCard)
    public static int isStraight(Deck deck){
        deck.sortByRank();
        Card[] deckArray = deck.toArray();

        for(int i = 0 ; i < deck.getSize()-4 ; i++) {

            int currRank = deckArray[i].getRank();

            int j = i+1;
            int count = 1;

            while(j < deck.getSize()){
                if(deckArray[j].getRank() == currRank - 1){
                    count++;
                    currRank--;
                }
                j++;
            }

            if(count > 4)
                return (400 + deckArray[i].getRank());
        }

        return -1;
    }

    //Three Cards Of The Same Rank, Plus Two Others That Are Not A Pair. (Score = 300 + ThreeOfAKind Rank + Other 2 HighCard Rank)
    public static int isThreeOfAKind(Deck deck){
        deck.sortByRank();
        Card[] deckArray = deck.toArray();

        for(int i = 0 ; i < deck.getSize()-2 ; i++) {
            if ((deckArray[i].getRank() - deckArray[i+2].getRank() == 0))

                if(i == 0)
                    return (300 + deckArray[i].getRank() + deckArray[i+3].getRank() + deckArray[i+4].getRank());
                else if(i == 1)
                    return (300 + deckArray[i].getRank() + deckArray[0].getRank() + deckArray[i+3].getRank());
                else
                    return (300 + deckArray[i].getRank() + deckArray[0].getRank() + deckArray[1].getRank());
        }

        return -1;
    }

    //Two Different Sets Of Two Cards Of The Same Rank, Plus A Card Of A Third Rank. (Score = 200 + Pair Ranks + Other HighCard Rank)
    public static int isTwoPair(Deck deck){
        deck.sortByRank();
        Card[] deckArray = deck.toArray();

        int pairCount = 0;
        int pairRanks = 0;
        int pair1Index = -1;
        int pair2Index = -1;

        for(int i = 0 ; i < deck.getSize()-1 ; i++) {

            if(pairCount > 1)
                break;

            if ((deckArray[i].getRank() - deckArray[i+1].getRank() == 0)) {

                if(pairCount == 0)
                    pair1Index = i;
                else if(pairCount == 1)
                    pair2Index = i;

                pairCount++;
                i++;
                pairRanks += deckArray[i].getRank();
            }
        }

        if(pairCount > 1) {
            if (pair1Index == 0 && pair2Index > 2)
                return (200 + pairRanks + deckArray[2].getRank());
            else if (pair1Index > 0)
                return (200 + pairRanks + deckArray[0].getRank());
            else
                return (200 + pairRanks + deckArray[4].getRank());
        }
        return -1;
    }

    //Two Cards Of The Same Rank, Plus Three Cards Of Other Unmatched Ranks. (Score = 100 + Pair Rank + Other Card Rank)
    public static int isPair(Deck deck){
        deck.sortByRank();
        Card[] deckArray = deck.toArray();

        for(int i = 0 ; i < deck.getSize()-1 ; i++) {
            if ((deckArray[i].getRank() - deckArray[i+1].getRank() == 0))
                if(i == 0)
                    return (100 + deckArray[i].getRank() + deckArray[2].getRank());
                else
                    return (100 + deckArray[i].getRank() + deckArray[0].getRank());

        }

        return -1;
    }

    //Any Other Configuration Of Cards, Ranked From The Highest Card To Lowest. (Score = 0 + All Cards Ranks)
    public static int isHighCard(Deck deck){
        deck.sortByRank();
        Card[] deckArray = deck.toArray();

        return deckArray[0].getRank() + deckArray[1].getRank() + deckArray[2].getRank() + deckArray[3].getRank() + deckArray[4].getRank();
    }
}
