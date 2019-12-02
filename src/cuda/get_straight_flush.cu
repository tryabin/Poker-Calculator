#include <stdio.h>
#include <util.h>
#include <get_straight_flush.h>


extern "C" __device__ StraightFlushResult get_straight_flush_result(int cards[]) {
    
    // Check to see if there is a flush.
    int suitCount[] = {0, 0, 0, 0};
    bool flushFound = false;
    int flushSuit = 0;
    for (int i = 0; i < 7; i++) {
        int card = cards[i];
        suitCount[get_suit(card)]++;
        
        if (suitCount[get_suit(card)] == 5) {
            flushFound = true;
            flushSuit = get_suit(card);
            break;
        }
    }
    
    // If there is a flush, check to see if there is a straight flush.
    int straightFlushHighCardRank = 0;
    bool straightFlushExists = false;
    if (flushFound) {
        
        // Sort the cards by rank ascending.
        sortCardsByRank(cards, 7);
        
        // Check to see if the flush contains an Ace, and check for a lower wheel straight flush.
        int straightFlushCount = 0;
        int prevStraightFlushRank = -1;
        for (int i = 6; i >= 0; i--) {
            if (get_rank(cards[i]) == ACE) {
                if (get_suit(cards[i]) == flushSuit) {
                    straightFlushCount = 1;
                    break;
                }
            }
            else {
                break;
            }
        }
        
        // Find the best straight flush, if it exists.
        for (int i = 0; i < 7; i++) {

            // Ignore cards that are not of the flush suit.
            if (get_suit(cards[i]) != flushSuit) {
                continue;
            }
            
            // Increment the straight counter if the rank of the current card is one above the previous rank in the potential straight.
            if (get_rank(cards[i]) == prevStraightFlushRank + 1) {
                straightFlushCount++;
                
                
                if (straightFlushCount >= 5) {
                    straightFlushExists = true;
                    straightFlushHighCardRank = get_rank(cards[i]);
                }
            }
            else if (get_rank(cards[i]) > prevStraightFlushRank + 1) {
                straightFlushCount = 1;
            }
            
            prevStraightFlushRank = get_rank(cards[i]);   
        }    
    }  

    // Return the result.
    StraightFlushResult result;    
    result.straightFlushExists = straightFlushExists;
    result.straightFlushHighCardRank = straightFlushHighCardRank;
    return result; 
}