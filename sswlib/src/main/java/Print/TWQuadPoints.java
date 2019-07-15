/*
Copyright (c) 2008~2009, Justin R. Bengtson (poopshotgun@yahoo.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
        this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
        this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.
    * Neither the name of Justin R. Bengtson nor the names of contributors may
        be used to endorse or promote products derived from this software
        without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package Print;

import java.awt.Point;

public class TWQuadPoints implements ifPrintPoints {

    private final static Point[] HeatSinkPoints = {
        new Point( 500, 610 ), new Point( 500, 618 ), new Point( 500, 626 ), new Point( 500, 634 ), new Point( 500, 642 ),
        new Point( 500, 650 ), new Point( 500, 658 ), new Point( 500, 666 ), new Point( 500, 674 ), new Point( 500, 682 ),

        new Point( 508, 610 ), new Point( 508, 618 ), new Point( 508, 626 ), new Point( 508, 634 ), new Point( 508, 642 ),
        new Point( 508, 650 ), new Point( 508, 658 ), new Point( 508, 666 ), new Point( 508, 674 ), new Point( 508, 682 ),

        new Point( 516, 610 ), new Point( 516, 618 ), new Point( 516, 626 ), new Point( 516, 634 ), new Point( 516, 642 ),
        new Point( 516, 650 ), new Point( 516, 658 ), new Point( 516, 666 ), new Point( 516, 674 ), new Point( 516, 682 ),

        new Point( 524, 610 ), new Point( 524, 618 ), new Point( 524, 626 ), new Point( 524, 634 ), new Point( 524, 642 ),
        new Point( 524, 650 ), new Point( 524, 658 ), new Point( 524, 666 ), new Point( 524, 674 ), new Point( 524, 682 ),

        new Point( 532, 610 ), new Point( 532, 618 ), new Point( 532, 626 ), new Point( 532, 634 ), new Point( 532, 642 ),
        new Point( 532, 650 ), new Point( 532, 658 ), new Point( 532, 666 ), new Point( 532, 674 ), new Point( 532, 682 ) };
    private final static Point[] CTArmorPoints = {
        new Point( 451, 86 ), new Point( 457, 86 ), new Point( 463, 86 ), new Point( 469, 86 ), new Point( 475, 86 ), new Point( 481, 86 ), new Point( 487, 86 ),
        new Point( 451, 92 ), new Point( 457, 92 ), new Point( 463, 92 ), new Point( 469, 92 ), new Point( 475, 92 ), new Point( 481, 92 ), new Point( 487, 92 ),
        new Point( 451, 98 ), new Point( 457, 98 ), new Point( 463, 98 ), new Point( 469, 98 ), new Point( 475, 98 ), new Point( 481, 98 ), new Point( 487, 98 ),
        new Point( 451, 104 ), new Point( 457, 104 ), new Point( 463, 104 ), new Point( 469, 104 ), new Point( 475, 104 ), new Point( 481, 104 ), new Point( 487, 104 ),
        new Point( 451, 110 ), new Point( 457, 110 ), new Point( 463, 110 ), new Point( 469, 110 ), new Point( 475, 110 ), new Point( 481, 110 ), new Point( 487, 110 ),
        new Point( 451, 116 ), new Point( 457, 116 ), new Point( 463, 116 ), new Point( 469, 116 ), new Point( 475, 116 ), new Point( 481, 116 ), new Point( 487, 116 ),
        new Point( 451, 122 ), new Point( 457, 122 ), new Point( 463, 122 ), new Point( 469, 122 ), new Point( 475, 122 ), new Point( 481, 122 ), new Point( 487, 122 ),
        new Point( 454, 128 ), new Point( 460, 128 ), new Point( 466, 128 ), new Point( 472, 128 ), new Point( 478, 128 ), new Point( 484, 128 ),
        new Point( 454, 134 ), new Point( 460, 134 ), new Point( 466, 134 ), new Point( 472, 134 ), new Point( 478, 134 ), new Point( 484, 134 ),
        new Point( 469, 140 ) };
    private final static Point[] LTArmorPoints = {
        new Point( 439, 44 ), new Point( 433, 44 ), new Point( 427, 44 ), new Point( 421, 44 ), new Point( 415, 44 ),
        new Point( 439, 50 ), new Point( 433, 50 ), new Point( 427, 50 ), new Point( 421, 50 ), new Point( 415, 50 ),
        new Point( 439, 56 ), new Point( 433, 56 ), new Point( 427, 56 ), new Point( 421, 56 ), new Point( 415, 56 ),
        new Point( 439, 62 ), new Point( 433, 62 ), new Point( 427, 62 ), new Point( 421, 62 ), new Point( 415, 62 ),
        new Point( 439, 68 ), new Point( 433, 68 ), new Point( 427, 68 ), new Point( 421, 68 ), new Point( 415, 68 ),
        new Point( 439, 74 ), new Point( 433, 74 ), new Point( 427, 74 ), new Point( 421, 74 ), new Point( 415, 74 ),
        new Point( 439, 80 ), new Point( 433, 80 ), new Point( 427, 80 ), new Point( 421, 80 ), new Point( 415, 80 ),
        new Point( 439, 86 ), new Point( 433, 86 ), new Point( 427, 86 ), new Point( 421, 86 ), new Point( 415, 86 ),
        new Point( 439, 92 ), new Point( 433, 92 ) };
    private final static Point[] RTArmorPoints = {
        new Point( 499, 44 ), new Point( 505, 44 ), new Point( 511, 44 ), new Point( 517, 44 ), new Point( 523, 44 ),
        new Point( 499, 50 ), new Point( 505, 50 ), new Point( 511, 50 ), new Point( 517, 50 ), new Point( 523, 50 ),
        new Point( 499, 56 ), new Point( 505, 56 ), new Point( 511, 56 ), new Point( 517, 56 ), new Point( 523, 56 ),
        new Point( 499, 62 ), new Point( 505, 62 ), new Point( 511, 62 ), new Point( 517, 62 ), new Point( 523, 62 ),
        new Point( 499, 68 ), new Point( 505, 68 ), new Point( 511, 68 ), new Point( 517, 68 ), new Point( 523, 68 ),
        new Point( 499, 74 ), new Point( 505, 74 ), new Point( 511, 74 ), new Point( 517, 74 ), new Point( 523, 74 ),
        new Point( 499, 80 ), new Point( 505, 80 ), new Point( 511, 80 ), new Point( 517, 80 ), new Point( 523, 80 ),
        new Point( 499, 86 ), new Point( 505, 86 ), new Point( 511, 86 ), new Point( 517, 86 ), new Point( 523, 86 ),
        new Point( 499, 92 ), new Point( 505, 92 ) };
    private final static Point[] HDArmorPoints = {
        new Point( 469, 72 ), new Point( 469, 66 ), new Point( 469, 60 ), new Point( 463, 72 ), new Point( 475, 72 ),
        new Point( 463, 66 ), new Point( 475, 66 ), new Point( 463, 60 ), new Point( 475, 60 ) };
    private final static Point[] LAArmorPoints = {
        new Point( 427, 122 ), new Point( 421, 122 ), new Point( 415, 122 ), new Point( 409, 122 ),
        new Point( 427, 128 ), new Point( 421, 128 ), new Point( 415, 128 ), new Point( 409, 128 ),
        new Point( 426, 134 ), new Point( 420, 134 ), new Point( 414, 134 ), new Point( 408, 134 ),
        new Point( 426, 140 ), new Point( 420, 140 ), new Point( 414, 140 ), new Point( 408, 140 ),
        new Point( 425, 146 ), new Point( 419, 146 ), new Point( 413, 146 ), new Point( 407, 146 ),
        new Point( 425, 152 ), new Point( 419, 152 ), new Point( 413, 152 ), new Point( 407, 152 ),
        new Point( 424, 158 ), new Point( 418, 158 ), new Point( 412, 158 ), new Point( 406, 158 ),
        new Point( 424, 164 ), new Point( 418, 164 ), new Point( 412, 164 ), new Point( 406, 164 ),
        new Point( 423, 170 ), new Point( 417, 170 ), new Point( 411, 170 ), new Point( 405, 170 ),
        new Point( 423, 176 ), new Point( 417, 176 ), new Point( 411, 176 ), new Point( 405, 176 ),
        new Point( 422, 182 ), new Point( 416, 182 ) };
    private final static Point[] RAArmorPoints = {
        new Point( 512, 122 ), new Point( 518, 122 ), new Point( 524, 122 ), new Point( 530, 122 ),
        new Point( 512, 128 ), new Point( 518, 128 ), new Point( 524, 128 ), new Point( 530, 128 ),
        new Point( 513, 134 ), new Point( 519, 134 ), new Point( 525, 134 ), new Point( 531, 134 ),
        new Point( 513, 140 ), new Point( 519, 140 ), new Point( 525, 140 ), new Point( 531, 140 ),
        new Point( 514, 146 ), new Point( 520, 146 ), new Point( 526, 146 ), new Point( 532, 146 ),
        new Point( 514, 152 ), new Point( 520, 152 ), new Point( 526, 152 ), new Point( 532, 152 ),
        new Point( 515, 158 ), new Point( 521, 158 ), new Point( 527, 158 ), new Point( 533, 158 ),
        new Point( 515, 164 ), new Point( 521, 164 ), new Point( 527, 164 ), new Point( 533, 164 ),
        new Point( 516, 170 ), new Point( 522, 170 ), new Point( 528, 170 ), new Point( 534, 170 ),
        new Point( 516, 176 ), new Point( 522, 176 ), new Point( 528, 176 ), new Point( 534, 176 ),
        new Point( 517, 182 ), new Point( 523, 182 ) };
    private final static Point[] LLArmorPoints = {
        new Point( 447, 158 ), new Point( 441, 158 ), new Point( 435, 158 ),
        new Point( 447, 164 ), new Point( 441, 164 ), new Point( 435, 164 ),
        new Point( 447, 170 ), new Point( 441, 170 ), new Point( 435, 170 ),
        new Point( 447, 176 ), new Point( 441, 176 ), new Point( 435, 176 ),
        new Point( 447, 182 ), new Point( 441, 182 ), new Point( 435, 182 ),
        new Point( 447, 188 ), new Point( 441, 188 ), new Point( 435, 188 ),
        new Point( 447, 194 ), new Point( 441, 194 ), new Point( 435, 194 ),
        new Point( 447, 200 ), new Point( 441, 200 ), new Point( 435, 200 ),
        new Point( 447, 206 ), new Point( 441, 206 ), new Point( 435, 206 ),
        new Point( 447, 212 ), new Point( 441, 212 ), new Point( 435, 212 ),
        new Point( 447, 218 ), new Point( 441, 218 ), new Point( 435, 218 ),
        new Point( 447, 224 ), new Point( 441, 224 ), new Point( 435, 224 ),
        new Point( 447, 230 ), new Point( 441, 230 ), new Point( 435, 230 ),
        new Point( 447, 236 ), new Point( 441, 236 ), new Point( 435, 236 ) };
    private final static Point[] RLArmorPoints = {
        new Point( 491, 158 ), new Point( 497, 158 ), new Point( 503, 158 ),
        new Point( 491, 164 ), new Point( 497, 164 ), new Point( 503, 164 ),
        new Point( 491, 170 ), new Point( 497, 170 ), new Point( 503, 170 ),
        new Point( 491, 176 ), new Point( 497, 176 ), new Point( 503, 176 ),
        new Point( 491, 182 ), new Point( 497, 182 ), new Point( 503, 182 ),
        new Point( 491, 188 ), new Point( 497, 188 ), new Point( 503, 188 ),
        new Point( 491, 194 ), new Point( 497, 194 ), new Point( 503, 194 ),
        new Point( 491, 200 ), new Point( 497, 200 ), new Point( 503, 200 ),
        new Point( 491, 206 ), new Point( 497, 206 ), new Point( 503, 206 ),
        new Point( 491, 212 ), new Point( 497, 212 ), new Point( 503, 212 ),
        new Point( 491, 218 ), new Point( 497, 218 ), new Point( 503, 218 ),
        new Point( 491, 224 ), new Point( 497, 224 ), new Point( 503, 224 ),
        new Point( 491, 230 ), new Point( 497, 230 ), new Point( 503, 230 ),
        new Point( 491, 236 ), new Point( 497, 236 ), new Point( 503, 236 ) };
    private final static Point[] CTRArmorPoints = {
        new Point( 457, 292 ), new Point( 463, 292 ), new Point( 469, 292 ), new Point( 475, 292 ), new Point( 481, 292 ),
        new Point( 457, 298 ), new Point( 463, 298 ), new Point( 469, 298 ), new Point( 475, 298 ), new Point( 481, 298 ),
        new Point( 457, 304 ), new Point( 463, 304 ), new Point( 469, 304 ), new Point( 475, 304 ), new Point( 481, 304 ),
        new Point( 457, 310 ), new Point( 463, 310 ), new Point( 469, 310 ), new Point( 475, 310 ), new Point( 481, 310 ),
        new Point( 457, 316 ), new Point( 463, 316 ), new Point( 469, 316 ), new Point( 475, 316 ), new Point( 481, 316 ),
        new Point( 457, 322 ), new Point( 463, 322 ), new Point( 469, 322 ), new Point( 475, 322 ), new Point( 481, 322 ),
        new Point( 457, 328 ), new Point( 463, 328 ), new Point( 469, 328 ), new Point( 475, 328 ), new Point( 481, 328 ),
        new Point( 457, 334 ), new Point( 463, 334 ), new Point( 469, 334 ), new Point( 475, 334 ), new Point( 481, 334 ),
        new Point( 457, 340 ), new Point( 463, 340 ), new Point( 469, 340 ), new Point( 475, 340 ), new Point( 481, 340 ),
        new Point( 457, 346 ), new Point( 463, 346 ), new Point( 469, 346 ), new Point( 475, 346 ), new Point( 481, 346 ),
        new Point( 457, 352 ), new Point( 463, 352 ), new Point( 469, 352 ), new Point( 475, 352 ), new Point( 481, 352 ),
        new Point( 451, 358 ), new Point( 457, 358 ), new Point( 463, 358 ), new Point( 469, 358 ), new Point( 475, 358 ), new Point( 481, 358 ), new Point( 487, 359 ) };
    private final static Point[] LTRArmorPoints = {
        new Point( 449, 295 ), new Point( 443, 295 ),
        new Point( 449, 301 ), new Point( 443, 301 ), new Point( 437, 301 ),
        new Point( 449, 307 ), new Point( 443, 307 ), new Point( 437, 307 ), new Point( 431, 307 ),
        new Point( 449, 313 ), new Point( 443, 313 ), new Point( 437, 313 ), new Point( 431, 313 ), new Point( 425, 313 ),
        new Point( 449, 319 ), new Point( 443, 319 ), new Point( 437, 319 ), new Point( 431, 319 ), new Point( 425, 319 ),
        new Point( 449, 325 ), new Point( 443, 325 ), new Point( 437, 325 ), new Point( 431, 325 ),
        new Point( 449, 331 ), new Point( 443, 331 ), new Point( 437, 331 ), new Point( 431, 331 ),
        new Point( 449, 337 ), new Point( 443, 337 ), new Point( 437, 337 ),
        new Point( 449, 345 ), new Point( 443, 345 ), new Point( 437, 345 ), new Point( 431, 345 ), new Point( 425, 345 ), new Point( 419, 345 ),
        new Point( 449, 351 ), new Point( 443, 351 ), new Point( 437, 351 ), new Point( 431, 351 ), new Point( 425, 351 ), new Point( 419, 351 ) };
    private final static Point[] RTRArmorPoints = {
        new Point( 490, 295 ), new Point( 496, 295 ),
        new Point( 490, 301 ), new Point( 496, 301 ), new Point( 502, 301 ),
        new Point( 490, 307 ), new Point( 496, 307 ), new Point( 502, 307 ), new Point( 508, 307 ),
        new Point( 490, 313 ), new Point( 496, 313 ), new Point( 502, 313 ), new Point( 508, 313 ), new Point( 514, 313 ),
        new Point( 490, 319 ), new Point( 496, 319 ), new Point( 502, 319 ), new Point( 508, 319 ), new Point( 514, 319 ),
        new Point( 490, 325 ), new Point( 496, 325 ), new Point( 502, 325 ), new Point( 508, 325 ),
        new Point( 490, 331 ), new Point( 496, 331 ), new Point( 502, 331 ), new Point( 508, 331 ),
        new Point( 490, 337 ), new Point( 496, 337 ), new Point( 502, 337 ),
        new Point( 490, 345 ), new Point( 496, 345 ), new Point( 502, 345 ), new Point( 508, 345 ), new Point( 514, 345 ), new Point( 520, 345 ),
        new Point( 490, 351 ), new Point( 496, 351 ), new Point( 502, 351 ), new Point( 508, 351 ), new Point( 514, 351 ), new Point( 520, 351 ) };


    private final static Point[] CTCritPoints = {
        new Point( 163, 461 ), new Point( 163, 470 ), new Point( 163, 479 ), new Point( 163, 487 ),
        new Point( 163, 496 ), new Point( 163, 505 ), new Point( 163, 517 ), new Point( 163, 526 ),
        new Point( 163, 535 ), new Point( 163, 543 ), new Point( 163, 552 ), new Point( 163, 561 ),
        new Point( 229, 451 ) //CASE Location
    };
    private final static Point[] HDCritPoints = {
        new Point( 163, 390 ), new Point( 163, 399 ), new Point( 163, 408 ), new Point( 163, 416 ),
        new Point( 163, 424 ), new Point( 163, 433 ),
        new Point( 189, 380 ) //CASE Location
    };
    private final static Point[] LTCritPoints = {
        new Point( 45, 516 ), new Point( 45, 525 ), new Point( 45, 534 ), new Point( 45, 543 ),
        new Point( 45, 551 ), new Point( 45, 560 ), new Point( 45, 572 ), new Point( 45, 581 ),
        new Point( 45, 590 ), new Point( 45, 598 ), new Point( 45, 607 ), new Point( 45, 616 ),
        new Point( 96, 506 ) //CASE Location
    };
    private final static Point[] LACritPoints = {
        new Point( 45, 429 ), new Point( 45, 439 ), new Point( 45, 447 ), new Point( 45, 456 ), new Point( 45, 464 ), new Point( 45, 473 ),
        new Point( 116, 419 ) //CASE Location
    };
    private final static Point[] LLCritPoints = {
        new Point( 45, 657 ), new Point( 45, 666 ), new Point( 45, 674 ), new Point( 45, 682 ), new Point( 45, 691 ), new Point( 45, 700 ),
        new Point( 114, 647 ) //CASE Location
    };
    private final static Point[] RTCritPoints = {
        new Point( 283, 516 ), new Point( 283, 525 ), new Point( 283, 534 ), new Point( 283, 543 ),
        new Point( 283, 551 ), new Point( 283, 560 ), new Point( 283, 572 ), new Point( 283, 581 ),
        new Point( 283, 590 ), new Point( 283, 598 ), new Point( 283, 607 ), new Point( 283, 616 ),
        new Point( 255, 506 ) //CASE Location
    };
    private final static Point[] RACritPoints = {
        new Point( 283, 429 ), new Point( 283, 439 ), new Point( 283, 447 ), new Point( 283, 456 ), new Point( 283, 464 ), new Point( 283, 473 ),
        new Point( 255, 419 ) //CASE Location
    };
    private final static Point[] RLCritPoints = {
        new Point( 283, 657 ), new Point( 283, 666 ), new Point( 283, 674 ), new Point( 283, 682 ), new Point( 283, 691 ), new Point( 283, 700 ),
        new Point( 255, 647 ) //CASE Location
    };


    private final static Point[] HDInternalPoints = {
        new Point( 456, 402 ), new Point( 452, 408 ), new Point( 459, 408 ) };
    private final static Point[] CTInternalPoints = {
        new Point( 443, 420 ), new Point( 449, 420 ), new Point( 455, 420 ), new Point( 461, 420 ), new Point( 467, 420 ),
        new Point( 443, 426 ), new Point( 449, 426 ), new Point( 455, 426 ), new Point( 461, 426 ), new Point( 467, 426 ),
        new Point( 443, 432 ), new Point( 449, 432 ), new Point( 455, 432 ), new Point( 461, 432 ), new Point( 467, 432 ),
        new Point( 443, 438 ), new Point( 449, 438 ), new Point( 455, 438 ), new Point( 461, 438 ), new Point( 467, 438 ),
        new Point( 446, 444 ), new Point( 452, 444 ), new Point( 458, 444 ), new Point( 464, 444 ),
        new Point( 446, 450 ), new Point( 452, 450 ), new Point( 458, 450 ), new Point( 464, 450 ),
        new Point( 449, 456 ), new Point( 455, 456 ), new Point( 461, 456 ) };
    private final static Point[] LTInternalPoints = {
        new Point( 436, 400 ), new Point( 430, 400 ), new Point( 424, 400 ),
        new Point( 436, 406 ), new Point( 430, 406 ), new Point( 424, 406 ), new Point( 418, 406 ),
        new Point( 436, 412 ), new Point( 430, 412 ), new Point( 424, 412 ), new Point( 418, 412 ), new Point( 412, 412 ),
        new Point( 436, 418 ), new Point( 430, 418 ), new Point( 424, 418 ), new Point( 418, 418 ), new Point( 412, 418 ),
        new Point( 436, 424 ), new Point( 430, 424 ), new Point( 424, 424 ), new Point( 418, 424 ) };
    private final static Point[] RTInternalPoints = {
        new Point( 475, 400 ), new Point( 481, 400 ), new Point( 487, 400 ),
        new Point( 475, 406 ), new Point( 481, 406 ), new Point( 487, 406 ), new Point( 493, 406 ),
        new Point( 475, 412 ), new Point( 481, 412 ), new Point( 487, 412 ), new Point( 493, 412 ), new Point( 499, 412 ),
        new Point( 475, 418 ), new Point( 481, 418 ), new Point( 487, 418 ), new Point( 493, 418 ), new Point( 499, 418 ),
        new Point( 475, 424 ), new Point( 481, 424 ), new Point( 487, 424 ), new Point( 493, 424 ) };
    private final static Point[] LLInternalPoints = {
        new Point( 438, 448 ),
        new Point( 435, 454 ),
        new Point( 438, 460 ), new Point( 432, 460 ),
        new Point( 438, 466 ), new Point( 432, 466 ),
        new Point( 438, 472 ), new Point( 432, 472 ),
        new Point( 435, 478 ),
        new Point( 435, 484 ),
        new Point( 438, 490 ), new Point( 432, 490 ),
        new Point( 435, 496 ),
        new Point( 435, 502 ),
        new Point( 435, 508 ),
        new Point( 435, 514 ),
        new Point( 438, 520 ), new Point( 432, 520 ),
        new Point( 438, 526 ), new Point( 432, 526 ),
        new Point( 435, 532 ) };
    private final static Point[] RLInternalPoints = {
        new Point( 473, 448 ),
        new Point( 475, 454 ),
        new Point( 473, 460 ), new Point( 479, 460 ),
        new Point( 473, 466 ), new Point( 479, 466 ),
        new Point( 473, 472 ), new Point( 479, 472 ),
        new Point( 476, 478 ),
        new Point( 476, 484 ),
        new Point( 473, 490 ), new Point( 479, 490 ),
        new Point( 476, 496 ),
        new Point( 476, 502 ),
        new Point( 476, 508 ),
        new Point( 476, 514 ),
        new Point( 473, 520 ), new Point( 479, 520 ),
        new Point( 473, 526 ), new Point( 479, 526 ),
        new Point( 476, 532 ) };
    private final static Point[] LAInternalPoints = {
        new Point( 427, 449 ), new Point( 421, 449 ),
        new Point( 419, 455 ),
        new Point( 418, 461 ),
        new Point( 415, 467 ), new Point( 421, 467 ),
        new Point( 415, 473 ), new Point( 421, 473 ),
        new Point( 418, 479 ),
        new Point( 418, 485 ),
        new Point( 419, 491 ), new Point( 413, 491 ),
        new Point( 419, 497 ), new Point( 413, 497 ),
        new Point( 416, 503 ),
        new Point( 415, 509 ),
        new Point( 414, 515 ),
        new Point( 413, 521 ),
        new Point( 413, 527 ),
        new Point( 413, 533 ),
        new Point( 413, 539 ) };
    private final static Point[] RAInternalPoints = {
        new Point( 484, 449 ), new Point( 490, 449 ),
        new Point( 492, 455 ),
        new Point( 493, 461 ),
        new Point( 490, 467 ), new Point( 496, 467 ),
        new Point( 490, 473 ), new Point( 496, 473 ),
        new Point( 493, 479 ),
        new Point( 493, 485 ),
        new Point( 492, 491 ), new Point( 498, 491 ),
        new Point( 492, 497 ), new Point( 498, 497 ),
        new Point( 495, 503 ),
        new Point( 496, 509 ),
        new Point( 497, 515 ),
        new Point( 498, 521 ),
        new Point( 498, 527 ),
        new Point( 498, 533 ),
        new Point( 498, 539 ) };
    private final static Point[] WeaponPoints = {
        new Point( 18, 187 ), new Point( 28, 187 ), new Point( 102, 187 ), new Point( 123, 187 ), new Point( 138, 187 ),
        new Point( 164, 187 ), new Point( 177, 187 ), new Point( 192, 187 ), new Point( 208, 187 ) };
    private final static Point[] DataPoints = {
        new Point( 40, 104 ), new Point( 65, 130 ), new Point( 65, 140 ), new Point( 65, 150 ),
        new Point( 165, 118 ), new Point( 199, 131 ), new Point( 170, 129 ), new Point( 261, 104 ),
        new Point( 285, 116 ), new Point( 358, 116 ), new Point( 44, 340 ), new Point( 145, 340 ),
        new Point( 499, 594 ), new Point( 515, 594 ), new Point( 522, 699 ), new Point( 522, 713 ),
        new Point( 142, 354 ), new Point( 525, 15 )};
    private final static Point[] InternalInfo = { 
        new Point( 0, 0 ), new Point( 454, 504 ), new Point( 395, 408 ), new Point( 515, 408 ),
        new Point( 392, 476 ), new Point( 517, 476 ), new Point( 389, 525 ), new Point( 521, 525 ) };
    private final static Point[] ArmorInfo = {
        new Point( 478, 28 ), new Point( 469, 194 ), new Point( 388, 120 ), new Point( 548, 120 ),
        new Point( 396, 297 ), new Point( 544, 297 ), new Point( 442, 286 ), new Point( 497, 286 ),
        new Point( 502, 358 ), new Point( 401, 347 ), new Point( 538, 347 ) };

    public Point[] GetCritHDPoints() {
        return HDCritPoints;
    }

    public Point[] GetCritCTPoints() {
        return CTCritPoints;
    }

    public Point[] GetCritLTPoints() {
        return LTCritPoints;
    }

    public Point[] GetCritRTPoints() {
        return RTCritPoints;
    }

    public Point[] GetCritLAPoints() {
        return LACritPoints;
    }

    public Point[] GetCritRAPoints() {
        return RACritPoints;
    }

    public Point[] GetCritLLPoints() {
        return LLCritPoints;
    }

    public Point[] GetCritRLPoints() {
        return RLCritPoints;
    }

    public Point[] GetArmorHDPoints() {
        return HDArmorPoints;
    }

    public Point[] GetArmorCTPoints() {
        return CTArmorPoints;
    }

    public Point[] GetArmorCTRPoints() {
        return CTRArmorPoints;
    }

    public Point[] GetArmorLTPoints() {
        return LTArmorPoints;
    }

    public Point[] GetArmorLTRPoints() {
        return LTRArmorPoints;
    }

    public Point[] GetArmorRTPoints() {
        return RTArmorPoints;
    }

    public Point[] GetArmorRTRPoints() {
        return RTRArmorPoints;
    }

    public Point[] GetArmorLAPoints() {
        return LAArmorPoints;
    }

    public Point[] GetArmorRAPoints() {
        return RAArmorPoints;
    }

    public Point[] GetArmorLLPoints() {
        return LLArmorPoints;
    }

    public Point[] GetArmorRLPoints() {
        return RLArmorPoints;
    }

    public Point[] GetInternalHDPoints() {
        return HDInternalPoints;
    }

    public Point[] GetInternalCTPoints() {
        return CTInternalPoints;
    }

    public Point[] GetInternalLTPoints() {
        return LTInternalPoints;
    }

    public Point[] GetInternalRTPoints() {
        return RTInternalPoints;
    }

    public Point[] GetInternalLAPoints() {
        return LAInternalPoints;
    }

    public Point[] GetInternalRAPoints() {
        return RAInternalPoints;
    }

    public Point[] GetInternalLLPoints() {
        return LLInternalPoints;
    }

    public Point[] GetInternalRLPoints() {
        return RLInternalPoints;
    }

    public Point[] GetInternalInfoPoints() {
        return InternalInfo;
    }

    public Point[] GetArmorInfoPoints() {
        return ArmorInfo;
    }

    public Point[] GetWeaponChartPoints() {
        return WeaponPoints;
    }

    public Point[] GetDataChartPoints() {
        return DataPoints;
    }

    public Point[] GetHeatSinkPoints() {
        return HeatSinkPoints;
    }

    public Point GetMechImageLoc() {
        return new Point( 230, 160 );
    }
    
    public Point GetImageBounds() {
        return new Point( 150, 210 );
    }
    
    public Point GetLogoImageLoc() {
        return new Point( 300, 160 );
    }

    public Point[] GetArmorFrontPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorLeftPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorRightPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorRearPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorTurretPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorTurret2Points() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorRotorPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetInternalFrontPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetInternalLeftPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetInternalRightPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetInternalRearPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetInternalTurretPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetInternalTurret2Points() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetInternalRotorPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
