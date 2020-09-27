// Main file:

import java.util.*;

public class Blackjack
{  
    // The starting bankroll for the player.
    private static final int STARTING_BANKROLL = 100;
    
    /**
     * Ask the player for a move, hit or stand.
     * 
     * @param sc The Scanner.
     * @return A lowercase string of "hit" or "stand"
     * to indicate the player's move.
     */
    private String getPlayerMove(Scanner sc)
    {
        while (true) {
            System.out.print("Enter move (hit [h]/stand [s]): ");
            String move = sc.nextLine();

            if (move.equals("")) {
                move = sc.nextLine();
            }

            move = move.toLowerCase();

            System.out.println("");
            
            if (move.equals("h") || move.equals("s") ||
                move.equals("hit") || move.equals("stand")) {
                return move;
            }

            System.err.println("Not a valid input.");
            System.out.println("");
        }
    }
    
    /**
     * Play the dealer's turn.
     * 
     * The dealer must hit if the value of the hand is less than 17. 
     * 
     * @param sc The Scanner.
     * @param dealer The dealer's hand.
     * @param deck The deck.
     */
    private void dealerTurn(Scanner sc, Hand dealer, Deck deck)
    {
        while (true) {
            System.out.println("Dealer's hand: " + dealer);
            System.out.println("");
            
            int value = dealer.getValue();
            
            System.out.print("Press Enter to continue...");
            String line = sc.nextLine();
            System.out.println("");
            
            if (value < 17) {
                System.out.println("Dealer hits");
                System.out.println("");
                Card c = deck.deal();
                dealer.addCard(c);
                
                System.out.println("Dealer card was: " + c);
                System.out.println("");
                
                if (dealer.busted()) {
                    System.out.println("Dealer busted!");
                    System.out.println("");
                    System.out.println("Dealer's hand: " + dealer);
                    System.out.println("");
                    break;
                }
            } else {
                System.out.println("Dealer stands.");
                System.out.println("");
                break;
            }
        }
    }
    
    /**
     * Play a player turn by asking the player to hit or stand.
     *
     * @param sc The Scanner.
     * @param player The player's hand.
     * @param deck The deck.
     * @return whether or not the player busted.
     */
    private boolean playerTurn(Scanner sc, Hand player, Deck deck)
    {
        while (true) {
            String move = getPlayerMove(sc);
            
            if (move.equals("h") || move.equals("hit")) {
                Card c = deck.deal();
                System.out.println("Your card was: " + c);
                System.out.println("");
                player.addCard(c);
                System.out.println("Your hand: " + player);
                System.out.println("");
                
                if (player.busted()) {
                    return true;
                }
            } else if (move.equals("s") || move.equals("stand")) {
                // If we didn't hit, the player chose to 
                // stand, which means the turn is over.
                return false;
            }          
        }
    }
    
    /**
     * Determine if the player wins. 
     * 
     * If the player busted, they lose. If the player did not bust but the
     * dealer busted, the player wins.
     * 
     * Then check the values of the hands.
     * 
     * @param player The player's hand.
     * @param dealer The dealer's hand.
     * @return boolean of the player's hand being greater than the dealer's
     * hand.
     */
    private boolean playerWins(Hand player, Hand dealer)
    {
        if (player.busted()) {
            return false;
        }
        
        if (dealer.busted()) {
            return true;
        }
        
        return player.getValue() > dealer.getValue();
    }
    
    /**
     * Check if there was a push, which means the player and dealer tied.
     * 
     * @param player The player's hand.
     * @param dealer The dealer's hand.
     * @return The equality check of the player and dealer's hands.
     */
    private boolean push(Hand player, Hand dealer)
    {
        return player.getValue() == dealer.getValue();
    }
    
    /**
     * Find the winner between the player hand and dealer hand. Return how much
     * was won or lost.
     */
    private double findWinner(Hand dealer, Hand player, int bet)
    {
        if (playerWins(player, dealer)) {
            System.out.println("You win!");
            System.out.println("");
            
            if (player.hasBlackjack()) {
                return 1.5 * bet;
            }
            
            return bet;
        } else if (push(player, dealer)) {
            System.out.println("You and the Dealer push.");
            System.out.println("");
            return 0;
        } else {
            System.out.println("Dealer wins.");
            System.out.println("");
            return -bet;
        }
    }
    
    /**
     * This plays a round of Blackjack which includes:
     * - Creating a deck
     * - Creating the hands
     * - Dealing the round
     * - Playing the player turn
     * - Playing the dealer turn
     * - Finding the winner
     * 
     * @param sc The Scanner.
     * @param bankroll The bankroll for the player.
     * @return The new bankroll for the player.
     */
    private int playRound(Scanner sc, int bankroll)
    {
        int bet = 0;
        boolean promptUser = true;

        while (promptUser) {
            try {
                System.out.print("How much would you like to bet? ");
                bet = sc.nextInt();
                System.out.println("");
                while (bet < 0 || bet > bankroll)
                {
                    if (bet < 0) {
                        System.out.println("You can't bet a negative amount.");
                    } else if (bet > bankroll) {
                        System.out.println("You can't bet more than what you have.");
                    }
                    System.out.println("");
                    System.out.print("How much would you like to bet? ");
                    bet = sc.nextInt();
                    System.out.println("");
                }
                promptUser = false;
            } catch (InputMismatchException e) {
                System.out.println("");
                System.err.println("Not a valid input.");
                System.out.println("");
                sc.next();
            }
        }

        Deck deck = new Deck();
        deck.shuffle();
        
        Hand player = new Hand();
        Hand dealer = new Hand();
        
        player.addCard(deck.deal());
        dealer.addCard(deck.deal());
        player.addCard(deck.deal());
        dealer.addCard(deck.deal());
        
        System.out.println("Your hand: " + player);
        System.out.println("");
        
        
        System.out.print("Dealer's hand: ");
        dealer.printDealerHand();
        System.out.println("");
        
        boolean playerBusted = playerTurn(sc, player, deck);
        
        if (playerBusted) {
            System.out.println("You busted :(");
            System.out.println("");
        } else {
            System.out.print("Press Enter for Dealer turn...");
            sc.nextLine();
            System.out.println("");
            dealerTurn(sc, dealer, deck);
        }
        
        double bankrollChange = findWinner(dealer, player, bet);
        
        bankroll += bankrollChange;
        
        System.out.println("New bankroll: " + bankroll);
        System.out.println("");
        
        return bankroll;
    }
    
    /*
     * Play the game. Initialize the bankroll and keep playing rounds as long
     * as the bankroll is greater than zero and user wants to keep playing.
     */
    public static void main(String[] args)
    {
        Blackjack b = new Blackjack();
        Scanner sc = new Scanner(System.in);

        int bankroll = STARTING_BANKROLL;

        System.out.println("Starting bankroll: " + bankroll);
        System.out.println("");
   
        do {
            bankroll = b.playRound(sc, bankroll);
            
            if (bankroll > 0) {
                System.out.print("Would you like to play again? (Y/N) ");
                String playAgain = sc.nextLine();
                System.out.println("");

                if(playAgain.equalsIgnoreCase("N"))
                {
                    break;
                }
            }
        } while(bankroll > 0);
        
        System.out.println("Thanks for playing!");
    }	
}
