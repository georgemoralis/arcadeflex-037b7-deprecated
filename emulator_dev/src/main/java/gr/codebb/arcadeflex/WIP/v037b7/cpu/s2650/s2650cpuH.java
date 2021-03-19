/*
This file is part of Arcadeflex.

Arcadeflex is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Arcadeflex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Arcadeflex.  If not, see <http://www.gnu.org/licenses/>.
 */
 /*
 * ported to v0.37b7
 * 
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.s2650;

public class s2650cpuH {

    public static final int PMSK = 0x1fff;
    /* mask page offset */
    public static final int PLEN = 0x2000;
    /* page length */
    public static final int PAGE = 0x6000;
    /* mask page */
    public static final int AMSK = 0x7fff;
    /* mask address range */

 /* processor status lower */
    public static final int C = 0x01;
    /* carry flag */
    public static final int COM = 0x02;
    /* compare: 0 binary, 1 2s complement */
    public static final int OVF = 0x04;
    /* 2s complement overflow */
    public static final int WC = 0x08;
    /* with carry: use carry in arithmetic / rotate ops */
    public static final int RS = 0x10;
    /* register select 0: R0/R1/R2/R3 1: R0/R4/R5/R6 */
    public static final int IDC = 0x20;
    /* inter digit carry: bit-3-to-bit-4 carry */
    public static final int CC = 0xc0;
    /* condition code */

 /* processor status upper */
    public static final int SP = 0x07;
    /* stack pointer: indexing 8 15bit words */
    public static final int PSU34 = 0x18;
    /* unused bits */
    public static final int II = 0x20;
    /* interrupt inhibit 0: allow, 1: inhibit */
    public static final int FO = 0x40;
    /* flag output */
    public static final int SI = 0x80;
    /* sense input */

//#define R0      S.reg[0]
//#define R1      S.reg[1]
//#define R2      S.reg[2]
//#define R3      S.reg[3]

}
