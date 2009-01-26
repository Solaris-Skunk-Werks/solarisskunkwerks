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
        new Point( 500, 610 ), new Point( 506, 610 ), new Point( 512, 610 ), new Point( 518, 610 ), new Point( 524, 610 ),
        new Point( 500, 616 ), new Point( 506, 616 ), new Point( 512, 616 ), new Point( 518, 616 ), new Point( 524, 616 ),
        new Point( 500, 622 ), new Point( 506, 622 ), new Point( 512, 622 ), new Point( 518, 622 ), new Point( 524, 622 ),
        new Point( 500, 628 ), new Point( 506, 628 ), new Point( 512, 628 ), new Point( 518, 628 ), new Point( 524, 628 ),
        new Point( 500, 634 ), new Point( 506, 634 ), new Point( 512, 634 ), new Point( 518, 634 ), new Point( 524, 634 ),
        new Point( 500, 640 ), new Point( 506, 640 ), new Point( 512, 640 ), new Point( 518, 640 ), new Point( 524, 640 ),
        new Point( 500, 646 ), new Point( 506, 646 ), new Point( 512, 646 ), new Point( 518, 646 ), new Point( 524, 646 ),
        new Point( 500, 652 ), new Point( 506, 652 ), new Point( 512, 652 ), new Point( 518, 652 ), new Point( 524, 652 ),
        new Point( 500, 658 ), new Point( 506, 658 ), new Point( 512, 658 ), new Point( 518, 658 ), new Point( 524, 658 ),
        new Point( 500, 664 ), new Point( 506, 664 ), new Point( 512, 664 ), new Point( 518, 664 ), new Point( 524, 664 ),
        new Point( 500, 670 ), new Point( 506, 670 ), new Point( 512, 670 ), new Point( 518, 670 ), new Point( 524, 670 ),
        new Point( 500, 676 ), new Point( 506, 676 ), new Point( 512, 676 ), new Point( 518, 676 ), new Point( 524, 676 ), };
    private final static Point[] CTArmorPoints = {
        new Point( 457, 84 ), new Point( 463, 84 ), new Point( 469, 84 ), new Point( 475, 84 ), new Point( 481, 84 ),
        new Point( 457, 90 ), new Point( 463, 90 ), new Point( 469, 90 ), new Point( 475, 90 ), new Point( 481, 90 ),
        new Point( 457, 96 ), new Point( 463, 96 ), new Point( 469, 96 ), new Point( 475, 96 ), new Point( 481, 96 ),
        new Point( 457, 102 ), new Point( 463, 102 ), new Point( 469, 102 ), new Point( 475, 102 ), new Point( 481, 102 ),
        new Point( 457, 108 ), new Point( 463, 108 ), new Point( 469, 108 ), new Point( 475, 108 ), new Point( 481, 108 ),
        new Point( 457, 114 ), new Point( 463, 114 ), new Point( 469, 114 ), new Point( 475, 114 ), new Point( 481, 114 ),
        new Point( 457, 120 ), new Point( 463, 120 ), new Point( 469, 120 ), new Point( 475, 120 ), new Point( 481, 120 ),
        new Point( 460, 126 ), new Point( 466, 126 ), new Point( 472, 126 ), new Point( 478, 126 ),
        new Point( 460, 132 ), new Point( 466, 132 ), new Point( 472, 132 ), new Point( 478, 132 ),
        new Point( 460, 138 ), new Point( 466, 138 ), new Point( 472, 138 ), new Point( 478, 138 ),
        new Point( 460, 144 ), new Point( 466, 144 ), new Point( 472, 144 ), new Point( 478, 144 ),
        new Point( 460, 150 ), new Point( 466, 150 ), new Point( 472, 150 ), new Point( 478, 150 ),
        new Point( 460, 156 ), new Point( 466, 156 ), new Point( 472, 156 ), new Point( 478, 156 ),
        new Point( 463, 162 ), new Point( 469, 162 ), new Point( 475, 162 ) };
    private final static Point[] LTArmorPoints = {
        new Point( 447, 66 ), new Point( 441, 66 ), new Point( 435, 66 ), new Point( 429, 66 ), new Point( 423, 66 ),
        new Point( 447, 72 ), new Point( 441, 72 ), new Point( 435, 72 ), new Point( 429, 72 ), new Point( 423, 72 ),
        new Point( 447, 78 ), new Point( 441, 78 ), new Point( 435, 78 ), new Point( 429, 78 ), new Point( 423, 78 ),
        new Point( 447, 84 ), new Point( 441, 84 ), new Point( 435, 84 ), new Point( 429, 84 ), new Point( 423, 84 ),
        new Point( 447, 90 ), new Point( 441, 90 ), new Point( 435, 90 ), new Point( 429, 90 ),
        new Point( 447, 96 ), new Point( 441, 96 ), new Point( 435, 96 ), new Point( 429, 96 ),
        new Point( 447, 102 ), new Point( 441, 102 ), new Point( 447, 108 ), new Point( 441, 108 ),
        new Point( 447, 114 ), new Point( 441, 114 ), new Point( 443, 120 ), new Point( 447, 120 ),
        new Point( 449, 126 ), new Point( 443, 126 ), new Point( 450, 138 ), new Point( 449, 138 ),
        new Point( 450, 144 ), new Point( 449, 144 ) };
    private final static Point[] RTArmorPoints = {
        new Point( 491, 66 ), new Point( 497, 66 ), new Point( 503, 66 ), new Point( 509, 66 ), new Point( 515, 66 ),
        new Point( 491, 72 ), new Point( 497, 72 ), new Point( 503, 72 ), new Point( 509, 72 ), new Point( 515, 72 ),
        new Point( 491, 78 ), new Point( 497, 78 ), new Point( 503, 78 ), new Point( 509, 78 ), new Point( 515, 78 ),
        new Point( 491, 84 ), new Point( 497, 84 ), new Point( 503, 84 ), new Point( 509, 84 ), new Point( 515, 84 ),
        new Point( 491, 90 ), new Point( 497, 90 ), new Point( 503, 90 ), new Point( 509, 90 ),
        new Point( 491, 96 ), new Point( 497, 96 ), new Point( 503, 96 ), new Point( 509, 96 ),
        new Point( 491, 102 ), new Point( 497, 102 ), new Point( 491, 108 ), new Point( 497, 108 ),
        new Point( 491, 114 ), new Point( 497, 114 ), new Point( 498, 120 ), new Point( 504, 120 ),
        new Point( 489, 126 ), new Point( 495, 126 ), new Point( 480, 138 ), new Point( 496, 138 ),
        new Point( 480, 144 ), new Point( 496, 144 ) };
    private final static Point[] HDArmorPoints = {
        new Point( 469, 66 ), new Point( 469, 60 ), new Point( 469, 54 ), new Point( 463, 66 ), new Point( 475, 66 ),
        new Point( 463, 60 ), new Point( 475, 60 ), new Point( 463, 54 ), new Point( 475, 54 ) };
    private final static Point[] LAArmorPoints = {
        new Point( 409, 59 ), new Point( 403, 59 ),
        new Point( 409, 65 ), new Point( 403, 65 ), new Point( 397, 65 ), new Point( 391, 65 ),
        new Point( 409, 71 ), new Point( 403, 71 ), new Point( 397, 71 ), new Point( 391, 71 ),
        new Point( 409, 77 ), new Point( 403, 77 ), new Point( 397, 77 ), new Point( 391, 77 ),
        new Point( 409, 83 ), new Point( 403, 83 ), new Point( 397, 83 ), new Point( 391, 83 ),
        new Point( 403, 89 ), new Point( 397, 89 ),
        new Point( 403, 95 ), new Point( 397, 95 ),
        new Point( 403, 101 ), new Point( 397, 101 ), 
        new Point( 403, 107 ), new Point( 397, 107 ),
        new Point( 403, 113 ), new Point( 397, 113 ),
        new Point( 403, 119 ), new Point( 397, 119 ),
        new Point( 403, 125 ), new Point( 397, 125 ),
        new Point( 403, 131 ), new Point( 397, 131 ) };
    private final static Point[] RAArmorPoints = {
        new Point( 528, 59 ), new Point( 534, 59 ),
        new Point( 528, 65 ), new Point( 534, 65 ), new Point( 540, 65 ), new Point( 546, 65 ),
        new Point( 528, 71 ), new Point( 534, 71 ), new Point( 540, 71 ), new Point( 546, 71 ),
        new Point( 528, 77 ), new Point( 534, 77 ), new Point( 540, 77 ), new Point( 546, 77 ),
        new Point( 528, 83 ), new Point( 534, 83 ), new Point( 540, 83 ), new Point( 546, 83 ),
        new Point( 534, 89 ), new Point( 540, 89 ), new Point( 534, 95 ), new Point( 540, 95 ),
        new Point( 534, 101 ), new Point( 540, 101 ), new Point( 534, 107 ), new Point( 540, 107 ),
        new Point( 534, 113 ), new Point( 540, 113 ), new Point( 534, 119 ), new Point( 540, 119 ),
        new Point( 534, 125 ), new Point( 540, 125 ), new Point( 534, 131 ), new Point( 540, 131 ) };
    private final static Point[] LLArmorPoints = {
        new Point( 449, 158 ), new Point( 443, 158 ), new Point( 437, 158 ),
        new Point( 449, 164 ), new Point( 443, 164 ), new Point( 437, 164 ), new Point( 431, 164 ),
        new Point( 449, 170 ), new Point( 443, 170 ), new Point( 437, 170 ), new Point( 431, 170 ),
        new Point( 449, 176 ), new Point( 443, 176 ), new Point( 437, 176 ), new Point( 431, 176 ),
        new Point( 443, 182 ), new Point( 437, 182 ), new Point( 431, 182 ),
        new Point( 443, 188 ), new Point( 437, 188 ), new Point( 431, 188 ), new Point( 425, 188 ),
        new Point( 443, 194 ), new Point( 437, 194 ), new Point( 431, 194 ), new Point( 425, 194 ),
        new Point( 437, 200 ), new Point( 431, 200 ), new Point( 425, 200 ), new Point( 419, 200 ),
        new Point( 437, 206 ), new Point( 431, 206 ), new Point( 425, 206 ), new Point( 419, 206 ),
        new Point( 437, 212 ), new Point( 431, 212 ), new Point( 425, 212 ), new Point( 419, 212 ),
        new Point( 437, 218 ), new Point( 431, 218 ), new Point( 425, 218 ), new Point( 419, 218 ) };
    private final static Point[] RLArmorPoints = {
        new Point( 489, 158 ), new Point( 495, 158 ), new Point( 501, 158 ),
        new Point( 489, 164 ), new Point( 495, 164 ), new Point( 501, 164 ), new Point( 507, 164 ),
        new Point( 489, 170 ), new Point( 495, 170 ), new Point( 501, 170 ), new Point( 507, 170 ),
        new Point( 489, 176 ), new Point( 495, 176 ), new Point( 501, 176 ), new Point( 507, 176 ),
        new Point( 495, 182 ), new Point( 501, 182 ), new Point( 507, 182 ),
        new Point( 495, 188 ), new Point( 501, 188 ), new Point( 507, 188 ), new Point( 513, 188 ),
        new Point( 495, 194 ), new Point( 501, 194 ), new Point( 507, 194 ), new Point( 513, 194 ),
        new Point( 501, 200 ), new Point( 507, 200 ), new Point( 513, 200 ), new Point( 519, 200 ),
        new Point( 501, 206 ), new Point( 507, 206 ), new Point( 513, 206 ), new Point( 519, 206 ),
        new Point( 501, 212 ), new Point( 507, 212 ), new Point( 513, 212 ), new Point( 519, 212 ),
        new Point( 501, 218 ), new Point( 507, 218 ), new Point( 513, 218 ), new Point( 519, 218 ) };
    private final static Point[] CTRArmorPoints = {
        new Point( 460, 286 ), new Point( 466, 286 ), new Point( 472, 286 ), new Point( 478, 286 ),
        new Point( 460, 292 ), new Point( 466, 292 ), new Point( 472, 292 ), new Point( 478, 292 ),
        new Point( 460, 298 ), new Point( 466, 298 ), new Point( 472, 298 ), new Point( 478, 298 ),
        new Point( 460, 304 ), new Point( 466, 304 ), new Point( 472, 304 ), new Point( 478, 304 ),
        new Point( 460, 310 ), new Point( 466, 310 ), new Point( 472, 310 ), new Point( 478, 310 ),
        new Point( 460, 316 ), new Point( 466, 316 ), new Point( 472, 316 ), new Point( 478, 316 ),
        new Point( 460, 322 ), new Point( 466, 322 ), new Point( 472, 322 ), new Point( 478, 322 ),
        new Point( 451, 328 ), new Point( 460, 328 ), new Point( 466, 328 ), new Point( 472, 328 ), new Point( 478, 328 ), new Point( 491, 328 ),
        new Point( 451, 334 ), new Point( 460, 334 ), new Point( 466, 334 ), new Point( 472, 334 ), new Point( 478, 334 ), new Point( 491, 334 ),
        new Point( 451, 340 ), new Point( 460, 340 ), new Point( 466, 340 ), new Point( 472, 340 ), new Point( 478, 340 ), new Point( 491, 340 ),
        new Point( 451, 346 ), new Point( 460, 346 ), new Point( 466, 346 ), new Point( 472, 346 ), new Point( 478, 346 ), new Point( 491, 346 ),
        new Point( 466, 280 ), new Point( 472, 280 ),
        new Point( 451, 352 ), new Point( 460, 352 ), new Point( 466, 352 ), new Point( 472, 352 ), new Point( 478, 352 ), new Point( 491, 352 ),
        new Point( 466, 274 ), new Point( 472, 274 ) };
    private final static Point[] LTRArmorPoints = {
        new Point( 449, 297 ), new Point( 443, 297 ), new Point( 437, 297 ), new Point( 431, 297 ), new Point( 425, 297 ),
        new Point( 449, 303 ), new Point( 443, 303 ), new Point( 437, 303 ), new Point( 431, 303 ), new Point( 425, 303 ),
        new Point( 449, 309 ), new Point( 443, 309 ), new Point( 437, 309 ), new Point( 431, 309 ), new Point( 425, 309 ),
        new Point( 449, 315 ), new Point( 443, 315 ), new Point( 437, 315 ), new Point( 431, 315 ), new Point( 425, 315 ),
        new Point( 443, 321 ), new Point( 437, 321 ), new Point( 431, 321 ), new Point( 425, 321 ),
        new Point( 443, 327 ), new Point( 437, 327 ), new Point( 431, 327 ), new Point( 425, 327 ),
        new Point( 443, 333 ), new Point( 437, 333 ), new Point( 431, 333 ), new Point( 425, 333 ),
        new Point( 443, 339 ), new Point( 437, 339 ), new Point( 431, 339 ), new Point( 425, 339 ),
        new Point( 443, 345 ), new Point( 437, 345 ), new Point( 431, 345 ), new Point( 425, 345 ),
        new Point( 443, 351 ), new Point( 437, 351 ) };
    private final static Point[] RTRArmorPoints = {
        new Point( 489, 297 ), new Point( 495, 297 ), new Point( 501, 297 ), new Point( 507, 297 ), new Point( 513, 297 ),
        new Point( 489, 303 ), new Point( 495, 303 ), new Point( 501, 303 ), new Point( 507, 303 ), new Point( 513, 303 ),
        new Point( 489, 309 ), new Point( 495, 309 ), new Point( 501, 309 ), new Point( 507, 309 ), new Point( 513, 309 ),
        new Point( 489, 315 ), new Point( 495, 315 ), new Point( 501, 315 ), new Point( 507, 315 ), new Point( 513, 315 ),
        new Point( 495, 321 ), new Point( 501, 321 ), new Point( 507, 321 ), new Point( 513, 321 ),
        new Point( 495, 327 ), new Point( 501, 327 ), new Point( 507, 327 ), new Point( 513, 327 ),
        new Point( 495, 333 ), new Point( 501, 333 ), new Point( 507, 333 ), new Point( 513, 333 ),
        new Point( 495, 339 ), new Point( 501, 339 ), new Point( 507, 339 ), new Point( 513, 339 ),
        new Point( 495, 345 ), new Point( 501, 345 ), new Point( 507, 345 ), new Point( 513, 345 ),
        new Point( 495, 351 ), new Point( 501, 351 ) };
    private final static Point[] CTCritPoints = {
        new Point( 160, 461 ), new Point( 160, 470 ), new Point( 160, 478 ), new Point( 160, 487 ),
        new Point( 160, 495 ), new Point( 160, 504 ), new Point( 160, 517 ), new Point( 160, 526 ),
        new Point( 160, 534 ), new Point( 160, 543 ), new Point( 160, 551 ), new Point( 160, 560 ) };
    private final static Point[] HDCritPoints = {
        new Point( 160, 390 ), new Point( 160, 399 ), new Point( 160, 408 ), new Point( 160, 416 ),
        new Point( 160, 424 ), new Point( 160, 433 ) };
    private final static Point[] LTCritPoints = {
        new Point( 42, 539 ), new Point( 42, 548 ), new Point( 42, 557 ), new Point( 42, 565 ),
        new Point( 42, 574 ), new Point( 42, 583 ), new Point( 42, 595 ), new Point( 42, 604 ),
        new Point( 42, 613 ), new Point( 42, 621 ), new Point( 42, 630 ), new Point( 42, 639 ) };
    private final static Point[] RTCritPoints = {
        new Point( 280, 539 ), new Point( 280, 548 ), new Point( 280, 557 ), new Point( 280, 565 ),
        new Point( 280, 574 ), new Point( 280, 583 ), new Point( 280, 595 ), new Point( 280, 604 ),
        new Point( 280, 613 ), new Point( 280, 621 ), new Point( 280, 630 ), new Point( 280, 639 ) };
    private final static Point[] LACritPoints = {
        new Point( 42, 400 ), new Point( 42, 408 ), new Point( 42, 416 ), new Point( 42, 425 ),
        new Point( 42, 434 ), new Point( 42, 442 ), new Point( 42, 456 ), new Point( 42, 464 ),
        new Point( 42, 472 ), new Point( 42, 481 ), new Point( 42, 490 ), new Point( 42, 499 ) };
    private final static Point[] RACritPoints = {
        new Point( 280, 400 ), new Point( 280, 408 ), new Point( 280, 416 ), new Point( 280, 425 ),
        new Point( 280, 434 ), new Point( 280, 442 ), new Point( 280, 456 ), new Point( 280, 464 ),
        new Point( 280, 472 ), new Point( 280, 481 ), new Point( 280, 490 ), new Point( 280, 499 ) };
    private final static Point[] LLCritPoints = {
        new Point( 42, 680 ), new Point( 42, 689 ), new Point( 42, 698 ), new Point( 42, 706 ),
        new Point( 42, 714 ), new Point( 42, 723 ) };
    private final static Point[] RLCritPoints = {
        new Point( 280, 680 ), new Point( 280, 689 ), new Point( 280, 698 ), new Point( 280, 706 ),
        new Point( 280, 714 ), new Point( 280, 723 ) };
    private final static Point[] HDInternalPoints = {
        new Point( 456, 390 ), new Point( 453, 396 ), new Point( 459, 396 ) };
    private final static Point[] CTInternalPoints = {
        new Point( 449, 411 ), new Point( 454, 411 ), new Point( 459, 411 ), new Point( 464, 411 ),
        new Point( 449, 417 ), new Point( 454, 417 ), new Point( 459, 417 ), new Point( 464, 417 ),
        new Point( 449, 423 ), new Point( 454, 423 ), new Point( 459, 423 ), new Point( 464, 423 ),
        new Point( 449, 429 ), new Point( 454, 429 ), new Point( 459, 429 ), new Point( 464, 429 ),
        new Point( 451, 435 ), new Point( 456, 435 ), new Point( 461, 435 ),
        new Point( 451, 441 ), new Point( 456, 441 ), new Point( 461, 441 ),
        new Point( 451, 447 ), new Point( 456, 447 ), new Point( 461, 447 ),
        new Point( 451, 453 ), new Point( 456, 453 ), new Point( 461, 453 ),
        new Point( 453, 459 ), new Point( 459, 459 ),
        new Point( 456, 464 ) };
    private final static Point[] LTInternalPoints = {
        new Point( 440, 402 ), new Point( 434, 402 ), new Point( 428, 402 ),
        new Point( 440, 408 ), new Point( 434, 408 ), new Point( 428, 408 ),
        new Point( 440, 414 ), new Point( 434, 414 ), new Point( 428, 414 ),
        new Point( 440, 420 ), new Point( 434, 420 ), new Point( 428, 420 ),
        new Point( 440, 426 ), new Point( 440, 432 ), new Point( 441, 438 ),
        new Point( 442, 444 ), new Point( 442, 450 ), new Point( 442, 456 ),
        new Point( 436, 450 ), new Point( 436, 456 ), new Point( 431, 453 ) };
    private final static Point[] RTInternalPoints = {
        new Point( 472, 402 ), new Point( 478, 402 ), new Point( 484, 402 ),
        new Point( 472, 408 ), new Point( 478, 408 ), new Point( 484, 408 ),
        new Point( 472, 414 ), new Point( 478, 414 ), new Point( 484, 414 ),
        new Point( 472, 420 ), new Point( 478, 420 ), new Point( 484, 420 ),
        new Point( 472, 426 ), new Point( 472, 432 ), new Point( 471, 438 ),
        new Point( 470, 444 ), new Point( 470, 450 ), new Point( 470, 456 ),
        new Point( 476, 450 ), new Point( 476, 456 ), new Point( 481, 453 ) };
    private final static Point[] LLInternalPoints = {
        new Point( 439, 464 ), new Point( 433, 464 ), new Point( 438, 470 ), new Point( 432, 470 ),
        new Point( 437, 476 ), new Point( 431, 476 ), new Point( 436, 482 ), new Point( 430, 482 ),
        new Point( 435, 488 ), new Point( 429, 488 ), new Point( 434, 494 ), new Point( 428, 494 ),
        new Point( 433, 500 ), new Point( 427, 500 ), new Point( 431, 506 ), new Point( 425, 506 ),
        new Point( 431, 512 ), new Point( 425, 512 ), new Point( 431, 518 ), new Point( 425, 518 ), new Point( 425, 524 ) };
    private final static Point[] RLInternalPoints = {
        new Point( 473, 464 ), new Point( 479, 464 ), new Point( 474, 470 ), new Point( 480, 470 ),
        new Point( 475, 476 ), new Point( 481, 476 ), new Point( 476, 482 ), new Point( 482, 482 ),
        new Point( 477, 488 ), new Point( 483, 488 ), new Point( 478, 494 ), new Point( 484, 494 ),
        new Point( 479, 500 ), new Point( 485, 500 ), new Point( 481, 506 ), new Point( 487, 506 ),
        new Point( 481, 512 ), new Point( 487, 512 ), new Point( 481, 518 ), new Point( 487, 518 ), new Point( 487, 524 ) };
    private final static Point[] LAInternalPoints = {
        new Point( 413, 401 ), new Point( 407, 401 ), new Point( 412, 407 ), new Point( 406, 407 ),
        new Point( 411, 413 ), new Point( 405, 413 ), new Point( 410, 419 ), new Point( 404, 419 ),
        new Point( 410, 425 ), new Point( 404, 425 ), new Point( 407, 431 ), new Point( 407, 437 ),
        new Point( 406, 443 ), new Point( 406, 449 ), new Point( 405, 455 ), new Point( 405, 461 ), new Point( 404, 467 ) };
    private final static Point[] RAInternalPoints = {
        new Point( 499, 401 ), new Point( 505, 401 ), new Point( 500, 407 ), new Point( 506, 407 ),
        new Point( 501, 413 ), new Point( 507, 413 ), new Point( 502, 419 ), new Point( 508, 419 ),
        new Point( 502, 425 ), new Point( 508, 425 ), new Point( 506, 431 ), new Point( 506, 437 ),
        new Point( 507, 443 ), new Point( 507, 449 ), new Point( 508, 455 ), new Point( 508, 461 ), new Point( 508, 467 ) };
    private final static Point[] WeaponPoints = {
        new Point( 17, 187 ), new Point( 28, 187 ), new Point( 102, 187 ), new Point( 120, 187 ), new Point( 136, 187 ),
        new Point( 162, 187 ), new Point( 175, 187 ), new Point( 190, 187 ), new Point( 208, 187 ) };
    private final static Point[] DataPoints = {
        new Point( 40, 104 ), new Point( 65, 130 ), new Point( 65, 140 ), new Point( 65, 150 ),
        new Point( 165, 118 ), new Point( 199, 131 ), new Point( 170, 129 ), new Point( 261, 104 ),
        new Point( 285, 116 ), new Point( 358, 116 ), new Point( 44, 340 ), new Point( 145, 340 ),
        new Point( 499, 594 ), new Point( 515, 594 ), new Point( 522, 699 ), new Point( 522, 713 ),
        new Point( 142, 354)};
    private final static Point[] InternalInfo = { 
        new Point( 0, 0 ), new Point( 456, 503 ), new Point( 428, 394 ), new Point( 521, 394 ),
        new Point( 385, 473 ), new Point( 526, 473 ), new Point( 398, 534 ), new Point( 514, 534 ) };
    private final static Point[] ArmorInfo = {
        new Point( 480, 28 ), new Point( 470, 207 ), new Point( 429, 42 ), new Point( 505, 42 ),
        new Point( 393, 201 ), new Point( 543, 201 ), new Point( 384, 260 ), new Point( 550, 260 ),
        new Point( 476, 265 ), new Point( 397, 352 ), new Point( 541, 352 ) };

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
}
