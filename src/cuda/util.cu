extern "C" __device__ int get_suit(int card) {
    return card % 4;
}

extern "C" __device__ int get_rank(int card) {
    return (card - get_suit(card))/4;
}

extern "C" __device__ void sortCardsByRank(int cards[], int n) { 
    int i, key, j; 
    for (i = 1; i < n; i++) { 
        key = cards[i]; 
        j = i - 1; 
  
        /* Move elements of cards[0..i-1], that are 
          greater than key, to one position ahead 
          of their current position */
        while (j >= 0 && get_rank(cards[j]) > get_rank(key)) { 
            cards[j + 1] = cards[j]; 
            j = j - 1; 
        } 
        cards[j + 1] = key; 
    } 
} 