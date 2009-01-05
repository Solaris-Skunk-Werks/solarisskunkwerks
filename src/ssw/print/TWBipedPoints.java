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

public class TWBipedPoints implements ifPrintPoints {

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
        new Point( 464, 84 ), new Point( 470, 84 ), new Point( 476, 84 ), new Point( 482, 84 ), new Point( 488, 84 ),
        new Point( 464, 90 ), new Point( 470, 90 ), new Point( 476, 90 ), new Point( 482, 90 ), new Point( 488, 90 ),
        new Point( 464, 96 ), new Point( 470, 96 ), new Point( 476, 96 ), new Point( 482, 96 ), new Point( 488, 96 ),
        new Point( 464, 102 ), new Point( 470, 102 ), new Point( 476, 102 ), new Point( 482, 102 ), new Point( 488, 102 ),
        new Point( 464, 108 ), new Point( 470, 108 ), new Point( 476, 108 ), new Point( 482, 108 ), new Point( 488, 108 ),
        new Point( 464, 114 ), new Point( 470, 114 ), new Point( 476, 114 ), new Point( 482, 114 ), new Point( 488, 114 ),
        new Point( 464, 120 ), new Point( 470, 120 ), new Point( 476, 120 ), new Point( 482, 120 ), new Point( 488, 120 ),
        new Point( 467, 126 ), new Point( 473, 126 ), new Point( 479, 126 ), new Point( 485, 126 ),
        new Point( 467, 132 ), new Point( 473, 132 ), new Point( 479, 132 ), new Point( 485, 132 ),
        new Point( 467, 138 ), new Point( 473, 138 ), new Point( 479, 138 ), new Point( 485, 138 ),
        new Point( 467, 144 ), new Point( 473, 144 ), new Point( 479, 144 ), new Point( 485, 144 ),
        new Point( 467, 150 ), new Point( 473, 150 ), new Point( 479, 150 ), new Point( 485, 150 ),
        new Point( 467, 156 ), new Point( 473, 156 ), new Point( 479, 156 ), new Point( 485, 156 ),
        new Point( 470, 162 ), new Point( 476, 162 ), new Point( 482, 162 ) };
    private final static Point[] LTArmorPoints = {
        new Point( 453, 66 ), new Point( 447, 66 ), new Point( 441, 66 ), new Point( 435, 66 ), new Point( 429, 66 ),
        new Point( 453, 72 ), new Point( 447, 72 ), new Point( 441, 72 ), new Point( 435, 72 ), new Point( 429, 72 ),
        new Point( 453, 78 ), new Point( 447, 78 ), new Point( 441, 78 ), new Point( 435, 78 ), new Point( 429, 78 ),
        new Point( 453, 84 ), new Point( 447, 84 ), new Point( 441, 84 ), new Point( 435, 84 ), new Point( 429, 84 ),
        new Point( 453, 90 ), new Point( 447, 90 ), new Point( 441, 90 ), new Point( 435, 90 ),
        new Point( 453, 96 ), new Point( 447, 96 ), new Point( 441, 96 ), new Point( 435, 96 ),
        new Point( 453, 102 ), new Point( 447, 102 ), new Point( 453, 108 ), new Point( 447, 108 ),
        new Point( 453, 114 ), new Point( 447, 114 ), new Point( 454, 120 ), new Point( 448, 120 ),
        new Point( 455, 126 ), new Point( 449, 126 ), new Point( 456, 138 ), new Point( 450, 138 ),
        new Point( 456, 144 ), new Point( 450, 144 ) };
    private final static Point[] RTArmorPoints = {
        new Point( 499, 66 ), new Point( 505, 66 ), new Point( 511, 66 ), new Point( 517, 66 ), new Point( 523, 66 ),
        new Point( 499, 72 ), new Point( 505, 72 ), new Point( 511, 72 ), new Point( 517, 72 ), new Point( 523, 72 ),
        new Point( 499, 78 ), new Point( 505, 78 ), new Point( 511, 78 ), new Point( 517, 78 ), new Point( 523, 78 ),
        new Point( 499, 84 ), new Point( 505, 84 ), new Point( 511, 84 ), new Point( 517, 84 ), new Point( 523, 84 ),
        new Point( 499, 90 ), new Point( 505, 90 ), new Point( 511, 90 ), new Point( 517, 90 ),
        new Point( 499, 96 ), new Point( 505, 96 ), new Point( 511, 96 ), new Point( 517, 96 ),
        new Point( 499, 102 ), new Point( 505, 102 ), new Point( 499, 108 ), new Point( 505, 108 ),
        new Point( 499, 114 ), new Point( 505, 114 ), new Point( 498, 120 ), new Point( 504, 120 ),
        new Point( 497, 126 ), new Point( 503, 126 ), new Point( 496, 138 ), new Point( 502, 138 ),
        new Point( 496, 144 ), new Point( 502, 144 ) };
    private final static Point[] HDArmorPoints = {
        new Point( 476, 66 ), new Point( 476, 60 ), new Point( 476, 54 ), new Point( 470, 66 ), new Point( 482, 66 ),
        new Point( 470, 60 ), new Point( 482, 60 ), new Point( 470, 54 ), new Point( 482, 54 ) };
    private final static Point[] LAArmorPoints = {
        new Point( 414, 59 ), new Point( 408, 59 ),
        new Point( 414, 65 ), new Point( 408, 65 ), new Point( 402, 65 ), new Point( 396, 65 ),
        new Point( 414, 71 ), new Point( 408, 71 ), new Point( 402, 71 ), new Point( 396, 71 ),
        new Point( 414, 77 ), new Point( 408, 77 ), new Point( 402, 77 ), new Point( 396, 77 ),
        new Point( 414, 83 ), new Point( 408, 83 ), new Point( 402, 83 ), new Point( 396, 83 ),
        new Point( 408, 89 ), new Point( 402, 89 ), new Point( 408, 95 ), new Point( 402, 95 ),
        new Point( 408, 101 ), new Point( 402, 101 ), new Point( 408, 107 ), new Point( 402, 107 ),
        new Point( 408, 113 ), new Point( 402, 113 ), new Point( 408, 119 ), new Point( 402, 119 ),
        new Point( 408, 125 ), new Point( 402, 125 ), new Point( 408, 131 ), new Point( 402, 131 ) };
    private final static Point[] RAArmorPoints = {
        new Point( 538, 59 ), new Point( 544, 59 ),
        new Point( 538, 65 ), new Point( 544, 65 ), new Point( 550, 65 ), new Point( 556, 65 ),
        new Point( 538, 71 ), new Point( 544, 71 ), new Point( 550, 71 ), new Point( 556, 71 ),
        new Point( 538, 77 ), new Point( 544, 77 ), new Point( 550, 77 ), new Point( 556, 77 ),
        new Point( 538, 83 ), new Point( 544, 83 ), new Point( 550, 83 ), new Point( 556, 83 ),
        new Point( 544, 89 ), new Point( 550, 89 ), new Point( 544, 95 ), new Point( 550, 95 ),
        new Point( 544, 101 ), new Point( 550, 101 ), new Point( 544, 107 ), new Point( 550, 107 ),
        new Point( 544, 113 ), new Point( 550, 113 ), new Point( 544, 119 ), new Point( 550, 119 ),
        new Point( 544, 125 ), new Point( 550, 125 ), new Point( 544, 131 ), new Point( 550, 131 ) };
    private final static Point[] LLArmorPoints = {
        new Point( 456, 158 ), new Point( 450, 158 ), new Point( 444, 158 ),
        new Point( 456, 164 ), new Point( 450, 164 ), new Point( 444, 164 ), new Point( 438, 164 ),
        new Point( 456, 170 ), new Point( 450, 170 ), new Point( 444, 170 ), new Point( 438, 170 ),
        new Point( 456, 176 ), new Point( 450, 176 ), new Point( 444, 176 ), new Point( 438, 176 ),
        new Point( 450, 182 ), new Point( 444, 182 ), new Point( 438, 182 ),
        new Point( 450, 188 ), new Point( 444, 188 ), new Point( 438, 188 ), new Point( 432, 188 ),
        new Point( 450, 194 ), new Point( 444, 194 ), new Point( 438, 194 ), new Point( 432, 194 ),
        new Point( 444, 200 ), new Point( 438, 200 ), new Point( 432, 200 ), new Point( 426, 200 ),
        new Point( 444, 206 ), new Point( 438, 206 ), new Point( 432, 206 ), new Point( 426, 206 ),
        new Point( 444, 212 ), new Point( 438, 212 ), new Point( 432, 212 ), new Point( 426, 212 ),
        new Point( 444, 218 ), new Point( 438, 218 ), new Point( 432, 218 ), new Point( 426, 218 ) };
    private final static Point[] RLArmorPoints = {
        new Point( 496, 158 ), new Point( 502, 158 ), new Point( 508, 158 ),
        new Point( 496, 164 ), new Point( 502, 164 ), new Point( 508, 164 ), new Point( 514, 164 ),
        new Point( 496, 170 ), new Point( 502, 170 ), new Point( 508, 170 ), new Point( 514, 170 ),
        new Point( 496, 176 ), new Point( 502, 176 ), new Point( 508, 176 ), new Point( 514, 176 ),
        new Point( 502, 182 ), new Point( 508, 182 ), new Point( 514, 182 ),
        new Point( 502, 188 ), new Point( 508, 188 ), new Point( 514, 188 ), new Point( 520, 188 ),
        new Point( 502, 194 ), new Point( 508, 194 ), new Point( 514, 194 ), new Point( 520, 194 ),
        new Point( 508, 200 ), new Point( 514, 200 ), new Point( 520, 200 ), new Point( 526, 200 ),
        new Point( 508, 206 ), new Point( 514, 206 ), new Point( 520, 206 ), new Point( 526, 206 ),
        new Point( 508, 212 ), new Point( 514, 212 ), new Point( 520, 212 ), new Point( 526, 212 ),
        new Point( 508, 218 ), new Point( 514, 218 ), new Point( 520, 218 ), new Point( 526, 218 ) };
    private final static Point[] CTRArmorPoints = {
        new Point( 467, 286 ), new Point( 473, 286 ), new Point( 479, 286 ), new Point( 485, 286 ),
        new Point( 467, 292 ), new Point( 473, 292 ), new Point( 479, 292 ), new Point( 485, 292 ),
        new Point( 467, 298 ), new Point( 473, 298 ), new Point( 479, 298 ), new Point( 485, 298 ),
        new Point( 467, 304 ), new Point( 473, 304 ), new Point( 479, 304 ), new Point( 485, 304 ),
        new Point( 467, 310 ), new Point( 473, 310 ), new Point( 479, 310 ), new Point( 485, 310 ),
        new Point( 467, 316 ), new Point( 473, 316 ), new Point( 479, 316 ), new Point( 485, 316 ),
        new Point( 467, 322 ), new Point( 473, 322 ), new Point( 479, 322 ), new Point( 485, 322 ),
        new Point( 461, 328 ), new Point( 467, 328 ), new Point( 473, 328 ), new Point( 479, 328 ), new Point( 485, 328 ), new Point( 491, 328 ),
        new Point( 461, 334 ), new Point( 467, 334 ), new Point( 473, 334 ), new Point( 479, 334 ), new Point( 485, 334 ), new Point( 491, 334 ),
        new Point( 461, 340 ), new Point( 467, 340 ), new Point( 473, 340 ), new Point( 479, 340 ), new Point( 485, 340 ), new Point( 491, 340 ),
        new Point( 461, 346 ), new Point( 467, 346 ), new Point( 473, 346 ), new Point( 479, 346 ), new Point( 485, 346 ), new Point( 491, 346 ),
        new Point( 473, 280 ), new Point( 479, 280 ),
        new Point( 461, 352 ), new Point( 467, 352 ), new Point( 473, 352 ), new Point( 479, 352 ), new Point( 485, 352 ), new Point( 491, 352 ),
        new Point( 473, 274 ), new Point( 479, 274 ) };
    private final static Point[] LTRArmorPoints = {
        new Point( 456, 297 ), new Point( 450, 297 ), new Point( 444, 297 ), new Point( 438, 297 ), new Point( 432, 297 ),
        new Point( 456, 303 ), new Point( 450, 303 ), new Point( 444, 303 ), new Point( 438, 303 ), new Point( 432, 303 ),
        new Point( 456, 309 ), new Point( 450, 309 ), new Point( 444, 309 ), new Point( 438, 309 ), new Point( 432, 309 ),
        new Point( 456, 315 ), new Point( 450, 315 ), new Point( 444, 315 ), new Point( 438, 315 ), new Point( 432, 315 ),
        new Point( 450, 321 ), new Point( 444, 321 ), new Point( 438, 321 ), new Point( 432, 321 ),
        new Point( 450, 327 ), new Point( 444, 327 ), new Point( 438, 327 ), new Point( 432, 327 ),
        new Point( 450, 333 ), new Point( 444, 333 ), new Point( 438, 333 ), new Point( 432, 333 ),
        new Point( 450, 339 ), new Point( 444, 339 ), new Point( 438, 339 ), new Point( 432, 339 ),
        new Point( 450, 345 ), new Point( 444, 345 ), new Point( 438, 345 ), new Point( 432, 345 ),
        new Point( 450, 351 ), new Point( 444, 351 ) };
    private final static Point[] RTRArmorPoints = {
        new Point( 497, 297 ), new Point( 503, 297 ), new Point( 509, 297 ), new Point( 515, 297 ), new Point( 521, 297 ),
        new Point( 497, 303 ), new Point( 503, 303 ), new Point( 509, 303 ), new Point( 515, 303 ), new Point( 521, 303 ),
        new Point( 497, 309 ), new Point( 503, 309 ), new Point( 509, 309 ), new Point( 515, 309 ), new Point( 521, 309 ),
        new Point( 497, 315 ), new Point( 503, 315 ), new Point( 509, 315 ), new Point( 515, 315 ), new Point( 521, 315 ),
        new Point( 503, 321 ), new Point( 509, 321 ), new Point( 515, 321 ), new Point( 521, 321 ),
        new Point( 503, 327 ), new Point( 509, 327 ), new Point( 515, 327 ), new Point( 521, 327 ),
        new Point( 503, 333 ), new Point( 509, 333 ), new Point( 515, 333 ), new Point( 521, 333 ),
        new Point( 503, 339 ), new Point( 509, 339 ), new Point( 515, 339 ), new Point( 521, 339 ),
        new Point( 503, 345 ), new Point( 509, 345 ), new Point( 515, 345 ), new Point( 521, 345 ),
        new Point( 503, 351 ), new Point( 509, 351 ) };
    private final static Point[] CTCritPoints = {
        new Point( 157, 461 ), new Point( 157, 470 ), new Point( 157, 479 ), new Point( 157, 487 ),
        new Point( 157, 496 ), new Point( 157, 505 ), new Point( 157, 517 ), new Point( 157, 526 ),
        new Point( 157, 535 ), new Point( 157, 543 ), new Point( 157, 552 ), new Point( 157, 561 ) };
    private final static Point[] HDCritPoints = {
        new Point( 157, 390 ), new Point( 157, 399 ), new Point( 157, 408 ), new Point( 157, 416 ),
        new Point( 157, 424 ), new Point( 157, 433 ) };
    private final static Point[] LTCritPoints = {
        new Point( 33, 539 ), new Point( 33, 548 ), new Point( 33, 557 ), new Point( 33, 565 ),
        new Point( 33, 574 ), new Point( 33, 583 ), new Point( 33, 595 ), new Point( 33, 604 ),
        new Point( 33, 613 ), new Point( 33, 621 ), new Point( 33, 630 ), new Point( 33, 639 ) };
    private final static Point[] RTCritPoints = {
        new Point( 280, 539 ), new Point( 280, 548 ), new Point( 280, 557 ), new Point( 280, 565 ),
        new Point( 280, 574 ), new Point( 280, 583 ), new Point( 280, 595 ), new Point( 280, 604 ),
        new Point( 280, 613 ), new Point( 280, 621 ), new Point( 280, 630 ), new Point( 280, 639 ) };
    private final static Point[] LACritPoints = {
        new Point( 33, 400 ), new Point( 33, 408 ), new Point( 33, 416 ), new Point( 33, 425 ),
        new Point( 33, 434 ), new Point( 33, 442 ), new Point( 33, 456 ), new Point( 33, 464 ),
        new Point( 33, 472 ), new Point( 33, 481 ), new Point( 33, 490 ), new Point( 33, 499 ) };
    private final static Point[] RACritPoints = {
        new Point( 280, 400 ), new Point( 280, 408 ), new Point( 280, 416 ), new Point( 280, 425 ),
        new Point( 280, 434 ), new Point( 280, 442 ), new Point( 280, 456 ), new Point( 280, 464 ),
        new Point( 280, 472 ), new Point( 280, 481 ), new Point( 280, 490 ), new Point( 280, 499 ) };
    private final static Point[] LLCritPoints = {
        new Point( 33, 680 ), new Point( 33, 689 ), new Point( 33, 698 ), new Point( 33, 706 ),
        new Point( 33, 714 ), new Point( 33, 723 ) };
    private final static Point[] RLCritPoints = {
        new Point( 280, 680 ), new Point( 280, 689 ), new Point( 280, 698 ), new Point( 280, 706 ),
        new Point( 280, 714 ), new Point( 280, 723 ) };
    private final static Point[] HDInternalPoints = {
        new Point( 462, 390 ), new Point( 459, 396 ), new Point( 465, 396 ) };
    private final static Point[] CTInternalPoints = {
        new Point( 454, 412 ), new Point( 460, 412 ), new Point( 466, 412 ), new Point( 472, 412 ),
        new Point( 454, 418 ), new Point( 460, 418 ), new Point( 466, 418 ), new Point( 472, 418 ),
        new Point( 454, 424 ), new Point( 460, 424 ), new Point( 466, 424 ), new Point( 472, 424 ),
        new Point( 454, 430 ), new Point( 460, 430 ), new Point( 466, 430 ), new Point( 472, 430 ),
        new Point( 457, 436 ), new Point( 463, 436 ), new Point( 469, 436 ),
        new Point( 457, 442 ), new Point( 463, 442 ), new Point( 469, 442 ),
        new Point( 457, 448 ), new Point( 463, 448 ), new Point( 469, 448 ),
        new Point( 457, 454 ), new Point( 463, 454 ), new Point( 469, 454 ),
        new Point( 457, 460 ), new Point( 463, 460 ), new Point( 469, 460 ) };
    private final static Point[] LTInternalPoints = {
        new Point( 446, 402 ), new Point( 440, 402 ), new Point( 434, 402 ),
        new Point( 446, 408 ), new Point( 440, 408 ), new Point( 434, 408 ),
        new Point( 446, 414 ), new Point( 440, 414 ), new Point( 434, 414 ),
        new Point( 446, 420 ), new Point( 440, 420 ), new Point( 434, 420 ),
        new Point( 446, 426 ), new Point( 446, 432 ), new Point( 447, 438 ),
        new Point( 448, 444 ), new Point( 448, 450 ), new Point( 448, 456 ),
        new Point( 442, 450 ), new Point( 442, 456 ), new Point( 437, 453 ) };
    private final static Point[] RTInternalPoints = {
        new Point( 480, 402 ), new Point( 486, 402 ), new Point( 492, 402 ),
        new Point( 480, 408 ), new Point( 486, 408 ), new Point( 492, 408 ),
        new Point( 480, 414 ), new Point( 486, 414 ), new Point( 492, 414 ),
        new Point( 480, 420 ), new Point( 486, 420 ), new Point( 492, 420 ),
        new Point( 480, 426 ), new Point( 480, 432 ), new Point( 479, 438 ),
        new Point( 478, 444 ), new Point( 478, 450 ), new Point( 478, 456 ),
        new Point( 484, 450 ), new Point( 484, 456 ), new Point( 489, 453 ) };
    private final static Point[] LLInternalPoints = {
        new Point( 446, 464 ), new Point( 440, 464 ), new Point( 445, 470 ), new Point( 439, 470 ),
        new Point( 444, 476 ), new Point( 438, 476 ), new Point( 443, 482 ), new Point( 437, 482 ),
        new Point( 441, 488 ), new Point( 435, 488 ), new Point( 441, 494 ), new Point( 435, 494 ),
        new Point( 439, 500 ), new Point( 433, 500 ), new Point( 438, 506 ), new Point( 432, 506 ),
        new Point( 438, 512 ), new Point( 432, 512 ), new Point( 438, 518 ), new Point( 432, 518 ), new Point( 432, 524 ) };
    private final static Point[] RLInternalPoints = {
        new Point( 480, 464 ), new Point( 486, 464 ), new Point( 481, 470 ), new Point( 487, 470 ),
        new Point( 482, 476 ), new Point( 488, 476 ), new Point( 483, 482 ), new Point( 489, 482 ),
        new Point( 485, 488 ), new Point( 491, 488 ), new Point( 485, 494 ), new Point( 491, 494 ),
        new Point( 487, 500 ), new Point( 493, 500 ), new Point( 488, 506 ), new Point( 494, 506 ),
        new Point( 488, 512 ), new Point( 494, 512 ), new Point( 488, 518 ), new Point( 494, 518 ), new Point( 494, 524 ) };
    private final static Point[] LAInternalPoints = {
        new Point( 418, 401 ), new Point( 412, 401 ), new Point( 417, 407 ), new Point( 411, 407 ),
        new Point( 416, 413 ), new Point( 410, 413 ), new Point( 415, 419 ), new Point( 409, 419 ),
        new Point( 415, 425 ), new Point( 409, 425 ), new Point( 412, 431 ), new Point( 412, 437 ),
        new Point( 411, 443 ), new Point( 411, 449 ), new Point( 410, 455 ), new Point( 410, 461 ), new Point( 409, 467 ) };
    private final static Point[] RAInternalPoints = {
        new Point( 507, 401 ), new Point( 513, 401 ), new Point( 508, 407 ), new Point( 514, 407 ),
        new Point( 509, 413 ), new Point( 515, 413 ), new Point( 510, 419 ), new Point( 516, 419 ),
        new Point( 511, 425 ), new Point( 517, 425 ), new Point( 514, 431 ), new Point( 514, 437 ),
        new Point( 515, 443 ), new Point( 515, 449 ), new Point( 516, 455 ), new Point( 516, 461 ), new Point( 517, 467 ) };
    private final static Point[] WeaponPoints = {
        new Point( 10, 188 ), new Point( 20, 188 ), new Point( 95, 188 ), new Point( 115, 188 ), new Point( 130, 188 ),
        new Point( 158, 188 ), new Point( 170, 188 ), new Point( 185, 188 ), new Point( 203, 188 ) };
    private final static Point[] DataPoints = {
        new Point( 30, 104 ), new Point( 55, 130 ), new Point( 55, 140 ), new Point( 55, 150 ),
        new Point( 160, 117 ), new Point( 197, 131 ), new Point( 197, 142 ), new Point( 261, 104 ),
        new Point( 285, 116 ), new Point( 358, 116 ), new Point( 35, 339 ), new Point( 140, 339 ),
        new Point( 506, 594 ), new Point( 524, 594 ), new Point( 532, 699 ), new Point( 532, 714 ) };
    private final static Point[] InternalInfo = { 
        new Point( 0, 0 ), new Point( 462, 504 ), new Point( 433, 394 ), new Point( 529, 394 ),
        new Point( 390, 473 ), new Point( 536, 473 ), new Point( 403, 534 ), new Point( 523, 534 ) };
    private final static Point[] ArmorInfo = {
        new Point( 488, 28 ), new Point( 477, 207 ), new Point( 435, 42 ), new Point( 513, 42 ),
        new Point( 398, 201 ), new Point( 552, 201 ), new Point( 389, 260 ), new Point( 560, 260 ),
        new Point( 483, 265 ), new Point( 403, 352 ), new Point( 550, 352 ) };

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
