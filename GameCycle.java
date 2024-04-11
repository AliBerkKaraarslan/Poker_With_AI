//********************************************************************************************************************************************
// GameCycle.java         Author:Ali Berk Karaarslan     Date:11.04.2024
//
// One Of The Classes Of Poker Project
//********************************************************************************************************************************************

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

//Works like Circular Linked List.
public class GameCycle {

    //---------------- NESTED PLAYER NODE CLASS ----------------
    class PlayerNode {

        private AbstractPlayer player;   //Reference to the player stored at this node
        private PlayerNode next;   //Reference to the subsequent node in the list

        public PlayerNode(AbstractPlayer p, PlayerNode n) {
            player = p;
            next = n;
        }

        public AbstractPlayer getPlayer() {
            return player;
        }

        public PlayerNode getNext() {
            return next;
        }

        public void setNext(PlayerNode n) {
            next = n;
        }
    }
    //----------- END OF NESTED PLAYER NODE CLASS -----------


    private PlayerNode tail = null;   //We store the tail (But not head. Because it is easier to access head from tail but not vice versa.)
    private int size = 0;   //Stores the number of nodes in the list.

    //Default Constructor. Constructs Empty List.
    public GameCycle(){}

    /* ACCESSOR METHODS */

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    //Returns The Active Players (ie not fold and all in players).
    public ArrayList<AbstractPlayer> getActivePlayers(){

        ArrayList<AbstractPlayer> activePlayers = new ArrayList<>();
        Iterator<PlayerNode> iter = nodeIterator();

        while(iter.hasNext()){
            PlayerNode currNode = iter.next();

            if(!currNode.getPlayer().isFold() && !currNode.getPlayer().isAllIn())
                activePlayers.add(currNode.getPlayer());
        }
        return activePlayers;
    }

    //Returns True If Everyone, EXCEPT ONE PLAYER Fold.
    public boolean isOnePlayerLeft(){
        int foldCount = 0;
        Iterator<PlayerNode> iter = nodeIterator();

        while(iter.hasNext()){
            PlayerNode currNode = iter.next();

            if(currNode.getPlayer().isFold())
                foldCount++;
        }

        return (foldCount + 1 == size);
    }

    //Returns (But Does Not Remove) The First Element.
    public AbstractPlayer first(){
        if (isEmpty()) return (Player) noPlayersLeftError("Could not access the first Player.");
        return tail.getNext().getPlayer(); // the head is *after* the tail
    }

    //Returns (But Does Not Remove) The Last Element.
    public AbstractPlayer last() {
        if (isEmpty()) return (Player) noPlayersLeftError("Could not access the last Player.");
        return tail.getPlayer();
    }

    //Returns The Players As Iterator.
    public Iterator<AbstractPlayer> iterator() {
        if (isEmpty()){
            noPlayersLeftError("Could not create Player Iterator.");
            return Collections.emptyIterator();
        }

        ArrayList<AbstractPlayer> playerAL = new ArrayList<>();
        PlayerNode currNode = tail.getNext();

        do{//Traverses across the players and adds them into an ArrayList
            playerAL.add(currNode.getPlayer());
            currNode = currNode.getNext();
        }while(!currNode.equals(tail.getNext()));

        return playerAL.iterator();
    }

    //Returns The PlayerNodes As Iterator.
    private Iterator<PlayerNode> nodeIterator() {
        if (isEmpty()){
            noPlayersLeftError("Could not create PlayerNode Iterator.");
            return Collections.emptyIterator();
        }

        ArrayList<PlayerNode> playerAL = new ArrayList<>();
        PlayerNode currNode = tail.getNext();

        do{//Traverses across the players and adds them into an ArrayList
            playerAL.add(currNode);
            currNode = currNode.getNext();
        }while(!currNode.equals(tail.getNext()));

        return playerAL.iterator();
    }

    /* SUCCESSOR METHODS */

    //Rotates The First Element To The Back Of The List. The Old Head Becomes The New Tail.
    public void rotate() {
        //If empty, do nothing.
        if (tail != null)
            tail = tail.getNext();
    }

    //Resets all the PlayerNodes' Roles.
    public void resetPlayerRoles() {
        if (isEmpty()){
            noPlayersLeftError("Could not reset Player roles.");
            return;
        }

        Iterator<PlayerNode> iter = nodeIterator();

        while(iter.hasNext()){
            PlayerNode currNode = iter.next();
            currNode.getPlayer().setDealer(false);
            currNode.getPlayer().setSmallBlind(false);
            currNode.getPlayer().setBigBlind(false);
        }
    }

    //Sets all the PlayerNodes' Roles. (Dealer, Small Blind etc.),
    public void setPlayerRoles(){
        if (isEmpty()){
            noPlayersLeftError("Could not set Player roles.");
            return;
        }
        resetPlayerRoles();

        //If There Is More Than 2 Players.
        if(size()>2){
            tail.getNext().getPlayer().setDealer(true);
            tail.getNext().getNext().getPlayer().setSmallBlind(true);
            tail.getNext().getNext().getNext().getPlayer().setBigBlind(true);
        }
        //Head-to-Head Situation. Only 2 Player Left In The Game. Dealer Posts The Small Blind.
        else if(size == 2){
            tail.getNext().getPlayer().setDealer(true);
            tail.getNext().getPlayer().setSmallBlind(true);
            tail.getNext().getNext().getPlayer().setBigBlind(true);
        }
    }

    //Adds Element E To The Front Of The List.
    public void addFirst(AbstractPlayer p) {
        if (size == 0) {
            tail = new PlayerNode(p, null);
            tail.setNext(tail);  //Link to itself circularly.
        }
        else {
            PlayerNode newest = new PlayerNode(p, tail.getNext());
            tail.setNext(newest);
        }
        size++;
    }

    //Adds Player p To The End Of The List.
    public void addLast(AbstractPlayer p) {
        addFirst(p); //Insert new player at front of list.
        tail = tail.getNext(); //New player becomes the tail.
    }

    //Removes And Returns The First Player.
    public AbstractPlayer removeFirst() {
        if (isEmpty()) return (AbstractPlayer) noPlayersLeftError("Could not remove the first Player."); //Nothing to remove.

        PlayerNode head = tail.getNext();

        if (head == tail)
            tail = null; //Must be the only node left.
        else
            tail.setNext(head.getNext()); //Removes "head" from the list.

        size--;
        return head.getPlayer();
    }

    //Removes The Desired Node And Returns It.
    public AbstractPlayer removePlayer(AbstractPlayer p){
        if (isEmpty()) return (AbstractPlayer) noPlayersLeftError("Could not remove the desired Player."); //Nothing to remove.

        //If desired node is head.
        if(tail.getNext().getPlayer().equals(p))
            return removeFirst();

        Iterator<PlayerNode> iter1 = nodeIterator();   //Stores current node.
        Iterator<PlayerNode> iter2 = nodeIterator();   //Stores previous node.

        if(iter1.hasNext()) iter1.next();   //To move current node one more step future from previous node.

        while(iter1.hasNext()){
            PlayerNode currNode = iter1.next();
            PlayerNode prevNode = iter2.next();

            if(currNode.getPlayer().equals(p)) {

                //If the desired node is tail.
                if (currNode.equals(tail)) {
                    tail = prevNode;
                }
                prevNode.setNext(currNode.getNext());
                currNode.setNext(null);
                size--;
                return currNode.getPlayer();
            }
        }
        return null;
    }

    //Returns The String Representation Of The GameCycle.
    public String toString(){
        if (isEmpty()) return "THERE IS NO PLAYER LEFT IN THE GAME.";

        String returnedString = "";
        Iterator<PlayerNode> iter = nodeIterator();

        while(iter.hasNext()){
            PlayerNode currNode = iter.next();
            returnedString = returnedString.concat(currNode.getPlayer() + "\n");
        }

        return returnedString;
    }

    //Prints No Players Left Error Message And Returns Null. (Kinda works like exceptions.)
    public static Object noPlayersLeftError(String errorMessage){
        System.out.println("NO PLAYERS LEFT ERROR: " + errorMessage);
        return null;
    }
}