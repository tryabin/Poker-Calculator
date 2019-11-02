/*
 *  Copyright 2016 Henry Lee
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

#include <stdio.h>
#include "hash.h"


/*
 * Card id, ranged from 0 to 51.
 * The two least significant bits represent the suit, ranged from 0-3.
 * The rest of it represent the rank, ranged from 0-12.
 * 13 * 4 gives 52 ids.
 */
extern "C" __device__ int evaluate_7cards(int a, int b, int c, int d, int e, int f, int g,
                                          short *binaries_by_id,
                                          short *suitbit_by_id,
                                          short *flush,
                                          short *noflush7,
                                          unsigned char *suits,
                                          int *dp) {
  int suit_hash = 0;

  suit_hash += suitbit_by_id[a];
  suit_hash += suitbit_by_id[b];
  suit_hash += suitbit_by_id[c];
  suit_hash += suitbit_by_id[d];
  suit_hash += suitbit_by_id[e];
  suit_hash += suitbit_by_id[f];
  suit_hash += suitbit_by_id[g];

  if (suits[suit_hash])
  {
    int suit_binary[4] = {0};
    suit_binary[a & 0x3] |= binaries_by_id[a];
    suit_binary[b & 0x3] |= binaries_by_id[b];
    suit_binary[c & 0x3] |= binaries_by_id[c];
    suit_binary[d & 0x3] |= binaries_by_id[d];
    suit_binary[e & 0x3] |= binaries_by_id[e];
    suit_binary[f & 0x3] |= binaries_by_id[f];
    suit_binary[g & 0x3] |= binaries_by_id[g];

    return flush[suit_binary[suits[suit_hash]-1]];
  }

  unsigned char quinary[13] = {0};

  quinary[(a >> 2)]++;
  quinary[(b >> 2)]++;
  quinary[(c >> 2)]++;
  quinary[(d >> 2)]++;
  quinary[(e >> 2)]++;
  quinary[(f >> 2)]++;
  quinary[(g >> 2)]++;

  const int hash = hash_quinary(quinary, 13, 7, dp);

  return noflush7[hash];
}

