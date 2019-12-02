#ifndef UTIL_H
#define UTIL_H

#define ACE (12)


extern "C" __device__ int get_suit(int card);
extern "C" __device__ int get_rank(int card);
extern "C" __device__ void sortCardsByRank(int cards[], int n);

#endif 
