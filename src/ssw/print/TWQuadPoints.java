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

package ssw.print;

import java.awt.Point;

public class TWQuadPoints implements ifPrintPoints {

    private final static Point[] HeatSinkPoints = {
        new Point( 506, 600 ), new Point( 512, 600 ), new Point( 518, 600 ), new Point( 524, 600 ), new Point( 530, 600 ),
        new Point( 506, 606 ), new Point( 512, 606 ), new Point( 518, 606 ), new Point( 524, 606 ), new Point( 530, 606 ),
        new Point( 506, 612 ), new Point( 512, 612 ), new Point( 518, 612 ), new Point( 524, 612 ), new Point( 530, 612 ),
        new Point( 506, 618 ), new Point( 512, 618 ), new Point( 518, 618 ), new Point( 524, 618 ), new Point( 530, 618 ),
        new Point( 506, 624 ), new Point( 512, 624 ), new Point( 518, 624 ), new Point( 524, 624 ), new Point( 530, 624 ),
        new Point( 506, 630 ), new Point( 512, 630 ), new Point( 518, 630 ), new Point( 524, 630 ), new Point( 530, 630 ),
        new Point( 506, 636 ), new Point( 512, 636 ), new Point( 518, 636 ), new Point( 524, 636 ), new Point( 530, 636 ),
        new Point( 506, 642 ), new Point( 512, 642 ), new Point( 518, 642 ), new Point( 524, 642 ), new Point( 530, 642 ),
        new Point( 506, 648 ), new Point( 512, 648 ), new Point( 518, 648 ), new Point( 524, 648 ), new Point( 530, 648 ),
        new Point( 506, 654 ), new Point( 512, 654 ), new Point( 518, 654 ), new Point( 524, 654 ), new Point( 530, 654 ),
        new Point( 506, 660 ), new Point( 512, 660 ), new Point( 518, 660 ), new Point( 524, 660 ), new Point( 530, 660 ),
        new Point( 506, 666 ), new Point( 512, 666 ), new Point( 518, 666 ), new Point( 524, 666 ), new Point( 530, 666 ),
        new Point( 506, 672 ), new Point( 512, 672 ), new Point( 518, 672 ), new Point( 524, 672 ), new Point( 530, 672 ) };
    private final static Point[] CTArmorPoints = {
        new Point( 460, 86 ), new Point( 466, 86 ), new Point( 472, 86 ), new Point( 478, 86 ), new Point( 484, 86 ), new Point( 490, 86 ), new Point( 496, 86 ),
        new Point( 460, 92 ), new Point( 466, 92 ), new Point( 472, 92 ), new Point( 478, 92 ), new Point( 484, 92 ), new Point( 490, 92 ), new Point( 496, 92 ),
        new Point( 460, 98 ), new Point( 466, 98 ), new Point( 472, 98 ), new Point( 478, 98 ), new Point( 484, 98 ), new Point( 490, 98 ), new Point( 496, 98 ),
        new Point( 460, 104 ), new Point( 466, 104 ), new Point( 472, 104 ), new Point( 478, 104 ), new Point( 484, 104 ), new Point( 490, 104 ), new Point( 496, 104 ),
        new Point( 460, 110 ), new Point( 466, 110 ), new Point( 472, 110 ), new Point( 478, 110 ), new Point( 484, 110 ), new Point( 490, 110 ), new Point( 496, 110 ),
        new Point( 460, 116 ), new Point( 466, 116 ), new Point( 472, 116 ), new Point( 478, 116 ), new Point( 484, 116 ), new Point( 490, 116 ), new Point( 496, 116 ),
        new Point( 460, 122 ), new Point( 466, 122 ), new Point( 472, 122 ), new Point( 478, 122 ), new Point( 484, 122 ), new Point( 490, 122 ), new Point( 496, 122 ),
        new Point( 463, 128 ), new Point( 469, 128 ), new Point( 475, 128 ), new Point( 481, 128 ), new Point( 487, 128 ), new Point( 493, 128 ),
        new Point( 463, 134 ), new Point( 469, 134 ), new Point( 475, 134 ), new Point( 481, 134 ), new Point( 487, 134 ), new Point( 493, 134 ),
        new Point( 478, 140 ) };
    private final static Point[] LTArmorPoints = {
        new Point( 448, 44 ), new Point( 442, 44 ), new Point( 436, 44 ), new Point( 430, 44 ), new Point( 424, 44 ),
        new Point( 448, 50 ), new Point( 442, 50 ), new Point( 436, 50 ), new Point( 430, 50 ), new Point( 424, 50 ),
        new Point( 448, 56 ), new Point( 442, 56 ), new Point( 436, 56 ), new Point( 430, 56 ), new Point( 424, 56 ),
        new Point( 448, 62 ), new Point( 442, 62 ), new Point( 436, 62 ), new Point( 430, 62 ), new Point( 424, 62 ),
        new Point( 448, 68 ), new Point( 442, 68 ), new Point( 436, 68 ), new Point( 430, 68 ), new Point( 424, 68 ),
        new Point( 448, 74 ), new Point( 442, 74 ), new Point( 436, 74 ), new Point( 430, 74 ), new Point( 424, 74 ),
        new Point( 448, 80 ), new Point( 442, 80 ), new Point( 436, 80 ), new Point( 430, 80 ), new Point( 424, 80 ),
        new Point( 448, 86 ), new Point( 442, 86 ), new Point( 436, 86 ), new Point( 430, 86 ), new Point( 424, 86 ),
        new Point( 448, 92 ), new Point( 442, 92 ) };
    private final static Point[] RTArmorPoints = {
        new Point( 508, 44 ), new Point( 514, 44 ), new Point( 520, 44 ), new Point( 526, 44 ), new Point( 532, 44 ),
        new Point( 508, 50 ), new Point( 514, 50 ), new Point( 520, 50 ), new Point( 526, 50 ), new Point( 532, 50 ),
        new Point( 508, 56 ), new Point( 514, 56 ), new Point( 520, 56 ), new Point( 526, 56 ), new Point( 532, 56 ),
        new Point( 508, 62 ), new Point( 514, 62 ), new Point( 520, 62 ), new Point( 526, 62 ), new Point( 532, 62 ),
        new Point( 508, 68 ), new Point( 514, 68 ), new Point( 520, 68 ), new Point( 526, 68 ), new Point( 532, 68 ),
        new Point( 508, 74 ), new Point( 514, 74 ), new Point( 520, 74 ), new Point( 526, 74 ), new Point( 532, 74 ),
        new Point( 508, 80 ), new Point( 514, 80 ), new Point( 520, 80 ), new Point( 526, 80 ), new Point( 532, 80 ),
        new Point( 508, 86 ), new Point( 514, 86 ), new Point( 520, 86 ), new Point( 526, 86 ), new Point( 532, 86 ),
        new Point( 508, 92 ), new Point( 514, 92 ) };
    private final static Point[] HDArmorPoints = {
        new Point( 478, 72 ), new Point( 478, 66 ), new Point( 478, 60 ), new Point( 472, 72 ), new Point( 484, 72 ),
        new Point( 472, 66 ), new Point( 484, 66 ), new Point( 472, 60 ), new Point( 484, 60 ) };
    private final static Point[] LAArmorPoints = {
        new Point( 436, 122 ), new Point( 430, 122 ), new Point( 424, 122 ), new Point( 418, 122 ),
        new Point( 436, 128 ), new Point( 430, 128 ), new Point( 424, 128 ), new Point( 418, 128 ),
        new Point( 435, 134 ), new Point( 429, 134 ), new Point( 423, 134 ), new Point( 417, 134 ),
        new Point( 435, 140 ), new Point( 429, 140 ), new Point( 423, 140 ), new Point( 417, 140 ),
        new Point( 434, 146 ), new Point( 428, 146 ), new Point( 422, 146 ), new Point( 416, 146 ),
        new Point( 434, 152 ), new Point( 428, 152 ), new Point( 422, 152 ), new Point( 416, 152 ),
        new Point( 433, 158 ), new Point( 427, 158 ), new Point( 421, 158 ), new Point( 415, 158 ),
        new Point( 433, 164 ), new Point( 427, 164 ), new Point( 421, 164 ), new Point( 415, 164 ),
        new Point( 432, 170 ), new Point( 426, 170 ), new Point( 420, 170 ), new Point( 414, 170 ),
        new Point( 432, 176 ), new Point( 426, 176 ), new Point( 420, 176 ), new Point( 414, 176 ),
        new Point( 431, 182 ), new Point( 425, 182 ) };
    private final static Point[] RAArmorPoints = {
        new Point( 521, 122 ), new Point( 527, 122 ), new Point( 533, 122 ), new Point( 539, 122 ),
        new Point( 521, 128 ), new Point( 527, 128 ), new Point( 533, 128 ), new Point( 539, 128 ),
        new Point( 522, 134 ), new Point( 528, 134 ), new Point( 534, 134 ), new Point( 540, 134 ),
        new Point( 522, 140 ), new Point( 528, 140 ), new Point( 534, 140 ), new Point( 540, 140 ),
        new Point( 523, 146 ), new Point( 529, 146 ), new Point( 535, 146 ), new Point( 541, 146 ),
        new Point( 523, 152 ), new Point( 529, 152 ), new Point( 535, 152 ), new Point( 541, 152 ),
        new Point( 524, 158 ), new Point( 530, 158 ), new Point( 536, 158 ), new Point( 542, 158 ),
        new Point( 524, 164 ), new Point( 530, 164 ), new Point( 536, 164 ), new Point( 542, 164 ),
        new Point( 525, 170 ), new Point( 531, 170 ), new Point( 537, 170 ), new Point( 543, 170 ),
        new Point( 525, 176 ), new Point( 531, 176 ), new Point( 537, 176 ), new Point( 543, 176 ),
        new Point( 526, 182 ), new Point( 532, 182 ) };
    private final static Point[] LLArmorPoints = {
        new Point( 456, 158 ), new Point( 450, 158 ), new Point( 444, 158 ),
        new Point( 456, 164 ), new Point( 450, 164 ), new Point( 444, 164 ),
        new Point( 456, 170 ), new Point( 450, 170 ), new Point( 444, 170 ),
        new Point( 456, 176 ), new Point( 450, 176 ), new Point( 444, 176 ),
        new Point( 456, 182 ), new Point( 450, 182 ), new Point( 444, 182 ),
        new Point( 456, 188 ), new Point( 450, 188 ), new Point( 444, 188 ),
        new Point( 456, 194 ), new Point( 450, 194 ), new Point( 444, 194 ),
        new Point( 456, 200 ), new Point( 450, 200 ), new Point( 444, 200 ),
        new Point( 456, 206 ), new Point( 450, 206 ), new Point( 444, 206 ),
        new Point( 456, 212 ), new Point( 450, 212 ), new Point( 444, 212 ),
        new Point( 456, 218 ), new Point( 450, 218 ), new Point( 444, 218 ),
        new Point( 456, 224 ), new Point( 450, 224 ), new Point( 444, 224 ),
        new Point( 456, 230 ), new Point( 450, 230 ), new Point( 444, 230 ),
        new Point( 456, 236 ), new Point( 450, 236 ), new Point( 444, 236 ) };
    private final static Point[] RLArmorPoints = {
        new Point( 500, 158 ), new Point( 506, 158 ), new Point( 512, 158 ),
        new Point( 500, 164 ), new Point( 506, 164 ), new Point( 512, 164 ),
        new Point( 500, 170 ), new Point( 506, 170 ), new Point( 512, 170 ),
        new Point( 500, 176 ), new Point( 506, 176 ), new Point( 512, 176 ),
        new Point( 500, 182 ), new Point( 506, 182 ), new Point( 512, 182 ),
        new Point( 500, 188 ), new Point( 506, 188 ), new Point( 512, 188 ),
        new Point( 500, 194 ), new Point( 506, 194 ), new Point( 512, 194 ),
        new Point( 500, 200 ), new Point( 506, 200 ), new Point( 512, 200 ),
        new Point( 500, 206 ), new Point( 506, 206 ), new Point( 512, 206 ),
        new Point( 500, 212 ), new Point( 506, 212 ), new Point( 512, 212 ),
        new Point( 500, 218 ), new Point( 506, 218 ), new Point( 512, 218 ),
        new Point( 500, 224 ), new Point( 506, 224 ), new Point( 512, 224 ),
        new Point( 500, 230 ), new Point( 506, 230 ), new Point( 512, 230 ),
        new Point( 500, 236 ), new Point( 506, 236 ), new Point( 512, 236 ) };
    private final static Point[] CTRArmorPoints = {
        new Point( 466, 292 ), new Point( 472, 292 ), new Point( 478, 292 ), new Point( 484, 292 ), new Point( 490, 292 ),
        new Point( 466, 298 ), new Point( 472, 298 ), new Point( 478, 298 ), new Point( 484, 298 ), new Point( 490, 298 ),
        new Point( 466, 304 ), new Point( 472, 304 ), new Point( 478, 304 ), new Point( 484, 304 ), new Point( 490, 304 ),
        new Point( 466, 310 ), new Point( 472, 310 ), new Point( 478, 310 ), new Point( 484, 310 ), new Point( 490, 310 ),
        new Point( 466, 316 ), new Point( 472, 316 ), new Point( 478, 316 ), new Point( 484, 316 ), new Point( 490, 316 ),
        new Point( 466, 322 ), new Point( 472, 322 ), new Point( 478, 322 ), new Point( 484, 322 ), new Point( 490, 322 ),
        new Point( 466, 328 ), new Point( 472, 328 ), new Point( 478, 328 ), new Point( 484, 328 ), new Point( 490, 328 ),
        new Point( 466, 334 ), new Point( 472, 334 ), new Point( 478, 334 ), new Point( 484, 334 ), new Point( 490, 334 ),
        new Point( 466, 340 ), new Point( 472, 340 ), new Point( 478, 340 ), new Point( 484, 340 ), new Point( 490, 340 ),
        new Point( 466, 346 ), new Point( 472, 346 ), new Point( 478, 346 ), new Point( 484, 346 ), new Point( 490, 346 ),
        new Point( 466, 352 ), new Point( 472, 352 ), new Point( 478, 352 ), new Point( 484, 352 ), new Point( 490, 352 ),
        new Point( 460, 358 ), new Point( 466, 358 ), new Point( 472, 358 ), new Point( 478, 358 ), new Point( 484, 358 ), new Point( 490, 358 ), new Point( 496, 359 ) };
    private final static Point[] LTRArmorPoints = {
        new Point( 457, 295 ), new Point( 451, 295 ),
        new Point( 457, 301 ), new Point( 451, 301 ), new Point( 445, 301 ),
        new Point( 457, 307 ), new Point( 451, 307 ), new Point( 445, 307 ), new Point( 439, 307 ),
        new Point( 457, 313 ), new Point( 451, 313 ), new Point( 445, 313 ), new Point( 439, 313 ), new Point( 433, 313 ),
        new Point( 457, 319 ), new Point( 451, 319 ), new Point( 445, 319 ), new Point( 439, 319 ), new Point( 433, 319 ),
        new Point( 457, 325 ), new Point( 451, 325 ), new Point( 445, 325 ), new Point( 439, 325 ),
        new Point( 457, 331 ), new Point( 451, 331 ), new Point( 445, 331 ), new Point( 439, 331 ),
        new Point( 457, 337 ), new Point( 451, 337 ), new Point( 445, 337 ),
        new Point( 457, 345 ), new Point( 451, 345 ), new Point( 445, 345 ), new Point( 439, 345 ), new Point( 433, 345 ), new Point( 427, 345 ),
        new Point( 457, 351 ), new Point( 451, 351 ), new Point( 445, 351 ), new Point( 439, 351 ), new Point( 433, 351 ), new Point( 427, 351 ) };
    private final static Point[] RTRArmorPoints = {
        new Point( 499, 295 ), new Point( 505, 295 ),
        new Point( 499, 301 ), new Point( 505, 301 ), new Point( 511, 301 ),
        new Point( 499, 307 ), new Point( 505, 307 ), new Point( 511, 307 ), new Point( 517, 307 ),
        new Point( 499, 313 ), new Point( 505, 313 ), new Point( 511, 313 ), new Point( 517, 313 ), new Point( 523, 313 ),
        new Point( 499, 319 ), new Point( 505, 319 ), new Point( 511, 319 ), new Point( 517, 319 ), new Point( 523, 319 ),
        new Point( 499, 325 ), new Point( 505, 325 ), new Point( 511, 325 ), new Point( 517, 325 ),
        new Point( 499, 331 ), new Point( 505, 331 ), new Point( 511, 331 ), new Point( 517, 331 ),
        new Point( 499, 337 ), new Point( 505, 337 ), new Point( 511, 337 ),
        new Point( 499, 345 ), new Point( 505, 345 ), new Point( 511, 345 ), new Point( 517, 345 ), new Point( 523, 345 ), new Point( 529, 345 ),
        new Point( 499, 351 ), new Point( 505, 351 ), new Point( 511, 351 ), new Point( 517, 351 ), new Point( 523, 351 ), new Point( 529, 351 ) };
    private final static Point[] CTCritPoints = {
        new Point( 157, 461 ), new Point( 157, 470 ), new Point( 157, 479 ), new Point( 157, 487 ),
        new Point( 157, 496 ), new Point( 157, 505 ), new Point( 157, 517 ), new Point( 157, 526 ),
        new Point( 157, 535 ), new Point( 157, 543 ), new Point( 157, 552 ), new Point( 157, 561 ) };
    private final static Point[] HDCritPoints = {
        new Point( 157, 390 ), new Point( 157, 399 ), new Point( 157, 408 ), new Point( 157, 416 ),
        new Point( 157, 424 ), new Point( 157, 433 ) };
    private final static Point[] LTCritPoints = {
        new Point( 33, 516 ), new Point( 33, 525 ), new Point( 33, 534 ), new Point( 33, 543 ),
        new Point( 33, 551 ), new Point( 33, 560 ), new Point( 33, 572 ), new Point( 33, 581 ),
        new Point( 33, 590 ), new Point( 33, 598 ), new Point( 33, 607 ), new Point( 33, 616 ) };
    private final static Point[] RTCritPoints = {
        new Point( 281, 516 ), new Point( 281, 525 ), new Point( 281, 534 ), new Point( 281, 543 ),
        new Point( 281, 551 ), new Point( 281, 560 ), new Point( 281, 572 ), new Point( 281, 581 ),
        new Point( 281, 590 ), new Point( 281, 598 ), new Point( 281, 607 ), new Point( 281, 616 ) };
    private final static Point[] LACritPoints = {
        new Point( 33, 429 ), new Point( 33, 439 ), new Point( 33, 447 ), new Point( 33, 456 ), new Point( 33, 464 ), new Point( 33, 473 ) };
    private final static Point[] RACritPoints = {
        new Point( 281, 429 ), new Point( 281, 439 ), new Point( 281, 447 ), new Point( 281, 456 ), new Point( 281, 464 ), new Point( 281, 473 ) };
    private final static Point[] LLCritPoints = {
        new Point( 33, 657 ), new Point( 33, 666 ), new Point( 33, 674 ), new Point( 33, 682 ), new Point( 33, 691 ), new Point( 33, 700 ) };
    private final static Point[] RLCritPoints = {
        new Point( 281, 657 ), new Point( 281, 666 ), new Point( 281, 674 ), new Point( 281, 682 ), new Point( 281, 691 ), new Point( 281, 700 ) };
    private final static Point[] HDInternalPoints = {
        new Point( 463, 402 ), new Point( 460, 408 ), new Point( 466, 408 ) };
    private final static Point[] CTInternalPoints = {
        new Point( 451, 420 ), new Point( 457, 420 ), new Point( 463, 420 ), new Point( 469, 420 ), new Point( 475, 420 ),
        new Point( 451, 426 ), new Point( 457, 426 ), new Point( 463, 426 ), new Point( 469, 426 ), new Point( 475, 426 ),
        new Point( 451, 432 ), new Point( 457, 432 ), new Point( 463, 432 ), new Point( 469, 432 ), new Point( 475, 432 ),
        new Point( 451, 438 ), new Point( 457, 438 ), new Point( 463, 438 ), new Point( 469, 438 ), new Point( 475, 438 ),
        new Point( 454, 444 ), new Point( 460, 444 ), new Point( 466, 444 ), new Point( 472, 444 ),
        new Point( 454, 450 ), new Point( 460, 450 ), new Point( 466, 450 ), new Point( 472, 450 ),
        new Point( 457, 456 ), new Point( 463, 456 ), new Point( 469, 456 ) };
    private final static Point[] LTInternalPoints = {
        new Point( 442, 400 ), new Point( 436, 400 ), new Point( 430, 400 ),
        new Point( 442, 406 ), new Point( 436, 406 ), new Point( 430, 406 ), new Point( 424, 406 ),
        new Point( 442, 412 ), new Point( 436, 412 ), new Point( 430, 412 ), new Point( 424, 412 ), new Point( 418, 412 ),
        new Point( 442, 418 ), new Point( 436, 418 ), new Point( 430, 418 ), new Point( 424, 418 ), new Point( 418, 418 ),
        new Point( 442, 424 ), new Point( 436, 424 ), new Point( 430, 424 ), new Point( 424, 424 ) };
    private final static Point[] RTInternalPoints = {
        new Point( 485, 400 ), new Point( 491, 400 ), new Point( 497, 400 ),
        new Point( 485, 406 ), new Point( 491, 406 ), new Point( 497, 406 ), new Point( 503, 406 ),
        new Point( 485, 412 ), new Point( 491, 412 ), new Point( 497, 412 ), new Point( 503, 412 ), new Point( 509, 412 ),
        new Point( 485, 418 ), new Point( 491, 418 ), new Point( 497, 418 ), new Point( 503, 418 ), new Point( 509, 418 ),
        new Point( 485, 424 ), new Point( 491, 424 ), new Point( 497, 424 ), new Point( 503, 424 ) };
    private final static Point[] LLInternalPoints = {
        new Point( 445, 448 ),
        new Point( 442, 454 ),
        new Point( 445, 460 ), new Point( 439, 460 ),
        new Point( 445, 466 ), new Point( 439, 466 ),
        new Point( 445, 472 ), new Point( 439, 472 ),
        new Point( 442, 478 ),
        new Point( 442, 484 ),
        new Point( 445, 490 ), new Point( 439, 490 ),
        new Point( 442, 496 ),
        new Point( 442, 502 ),
        new Point( 442, 508 ),
        new Point( 442, 514 ),
        new Point( 445, 520 ), new Point( 439, 520 ),
        new Point( 445, 526 ), new Point( 439, 526 ),
        new Point( 442, 532 ) };
    private final static Point[] RLInternalPoints = {
        new Point( 482, 448 ),
        new Point( 485, 454 ),
        new Point( 482, 460 ), new Point( 488, 460 ),
        new Point( 482, 466 ), new Point( 488, 466 ),
        new Point( 482, 472 ), new Point( 488, 472 ),
        new Point( 485, 478 ),
        new Point( 485, 484 ),
        new Point( 482, 490 ), new Point( 488, 490 ),
        new Point( 485, 496 ),
        new Point( 485, 502 ),
        new Point( 485, 508 ),
        new Point( 485, 514 ),
        new Point( 482, 520 ), new Point( 488, 520 ),
        new Point( 482, 526 ), new Point( 488, 526 ),
        new Point( 485, 532 ) };
    private final static Point[] LAInternalPoints = {
        new Point( 434, 449 ), new Point( 428, 449 ),
        new Point( 426, 455 ),
        new Point( 425, 461 ),
        new Point( 422, 467 ), new Point( 428, 467 ),
        new Point( 422, 473 ), new Point( 428, 473 ),
        new Point( 425, 479 ),
        new Point( 425, 485 ),
        new Point( 426, 491 ), new Point( 420, 491 ),
        new Point( 426, 497 ), new Point( 420, 497 ),
        new Point( 423, 503 ),
        new Point( 422, 509 ),
        new Point( 421, 515 ),
        new Point( 420, 521 ),
        new Point( 420, 527 ),
        new Point( 420, 533 ),
        new Point( 420, 539 ) };
    private final static Point[] RAInternalPoints = {
        new Point( 493, 449 ), new Point( 499, 449 ),
        new Point( 501, 455 ),
        new Point( 502, 461 ),
        new Point( 499, 467 ), new Point( 505, 467 ),
        new Point( 499, 473 ), new Point( 505, 473 ),
        new Point( 502, 479 ),
        new Point( 502, 485 ), 
        new Point( 501, 491 ), new Point( 507, 491 ),
        new Point( 501, 497 ), new Point( 507, 497 ),
        new Point( 504, 503 ),
        new Point( 505, 509 ),
        new Point( 506, 515 ),
        new Point( 507, 521 ),
        new Point( 507, 527 ),
        new Point( 507, 533 ),
        new Point( 507, 539 ) };
    private final static Point[] WeaponPoints = {
        new Point( 10, 188 ), new Point( 20, 188 ), new Point( 95, 188 ), new Point( 115, 188 ), new Point( 130, 188 ),
        new Point( 158, 188 ), new Point( 170, 188 ), new Point( 185, 188 ), new Point( 203, 188 ) };
    private final static Point[] DataPoints = {
        new Point( 30, 104 ), new Point( 55, 130 ), new Point( 55, 140 ), new Point( 55, 150 ),
        new Point( 160, 118 ), new Point( 197, 131 ), new Point( 197, 142 ), new Point( 262, 104 ),
        new Point( 286, 116 ), new Point( 359, 116 ), new Point( 35, 339 ), new Point( 140, 339 ),
        new Point( 508, 594 ), new Point( 526, 594 ), new Point( 532, 699 ), new Point( 532, 714 ) };
    private final static Point[] InternalInfo = { 
        new Point( 0, 0 ), new Point( 462, 504 ), new Point( 401, 408 ), new Point( 526, 408 ),
        new Point( 398, 476 ), new Point( 527, 476 ), new Point( 395, 525 ), new Point( 532, 525 ) };
    private final static Point[] ArmorInfo = {
        new Point( 488, 28 ), new Point( 478, 194 ), new Point( 393, 120 ), new Point( 560, 120 ),
        new Point( 401, 297 ), new Point( 554, 297 ), new Point( 450, 286 ), new Point( 507, 286 ),
        new Point( 511, 358 ), new Point( 408, 347 ), new Point( 549, 347 ) };

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
        return null;
    }
}
