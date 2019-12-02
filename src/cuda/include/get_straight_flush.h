#ifndef GET_STRAIGHT_FLUSH_H
#define GET_STRAIGHT_FLUSH_H


typedef struct StraightFlushResult {
    bool straightFlushExists;
    int straightFlushHighCardRank;
} StraightFlushResult;

extern "C" __device__ StraightFlushResult get_straight_flush_result(int cards[]);


#endif 
