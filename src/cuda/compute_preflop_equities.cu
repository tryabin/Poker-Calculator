#include <stdio.h>
#include <math.h>
#include <math_constants.h>
#include <phevaluator.h>

#define DECK_SIZE (52)
#define COMBO_DATA_SIZE (5)


extern "C" __global__ void compute_preflop_equities(int *holeCardCombos, int *outcomeTallies, int numberOfCombos,
                                                    short *binaries_by_id,
                                                    short *suitbit_by_id,
                                                    short *flush,
                                                    short *noflush7,
                                                    unsigned char *suits,
                                                    int *dp) {

    int posX = threadIdx.x + blockDim.x*blockIdx.x;
    int posY = threadIdx.y + blockDim.y*blockIdx.y;
    int threadNumber = posX + posY*blockDim.x*gridDim.x;

    if (threadNumber >= numberOfCombos) {
        return;
    }
 
    
    // Add the hole cards to the used cards array.
    int mainCard1 = holeCardCombos[threadNumber*COMBO_DATA_SIZE + 0];
    int mainCard2 = holeCardCombos[threadNumber*COMBO_DATA_SIZE + 1];
    int otherCard1 = holeCardCombos[threadNumber*COMBO_DATA_SIZE + 2];
    int otherCard2 = holeCardCombos[threadNumber*COMBO_DATA_SIZE + 3];

    // Initialize an array to store the used cards.
    bool usedCards[DECK_SIZE];
    for (int i = 0; i < DECK_SIZE; i++) {
        usedCards[i] = false;
    }
    usedCards[mainCard1] = true;
    usedCards[mainCard2] = true;
    usedCards[otherCard1] = true;
    usedCards[otherCard2] = true;
    
    
     // Card 1
    for (int card1 = 0; card1 < DECK_SIZE; card1++) { if (!usedCards[card1]) {

     // Card 2
    for (int card2 = card1+1; card2 < DECK_SIZE; card2++) { if (!usedCards[card2]) {

     // Card 3
    for (int card3 = card2+1; card3 < DECK_SIZE; card3++) { if (!usedCards[card3]) {

     // Card 4
    for (int card4 = card3+1; card4 < DECK_SIZE; card4++) { if (!usedCards[card4]) {

     // Card 5
    for (int card5 = card4+1; card5 < DECK_SIZE; card5++) { if (!usedCards[card5]) {
        
        int mainHandValue = evaluate_7cards(mainCard1, mainCard2, card1, card2, card3, card4, card5, 
                                            binaries_by_id,
                                            suitbit_by_id,
                                            flush,
                                            noflush7,
                                            suits,
                                            dp);
        int otherHandValue = evaluate_7cards(otherCard1, otherCard2, card1, card2, card3, card4, card5,
                                            binaries_by_id,
                                            suitbit_by_id,
                                            flush,
                                            noflush7,
                                            suits,
                                            dp);
        
        
        // Increment the win/loss/tie tallies.
        if (mainHandValue < otherHandValue) {
            outcomeTallies[threadNumber*3 + 0]++;
        }
        else if (mainHandValue > otherHandValue) {
            outcomeTallies[threadNumber*3 + 1]++;
        }
        else {
            outcomeTallies[threadNumber*3 + 2]++;
        }

    }}}}}}}}}}
}

    



