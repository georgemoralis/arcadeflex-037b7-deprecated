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

public class s2650H {

    /*TODO*///enum {
/*TODO*///	S2650_PC=1, S2650_PS, S2650_R0, S2650_R1, S2650_R2, S2650_R3,
/*TODO*///	S2650_R1A, S2650_R2A, S2650_R3A,
/*TODO*///	S2650_HALT, S2650_IRQ_STATE, S2650_SI, S2650_FO
/*TODO*///};
    public static final int S2650_INT_NONE = 0;
    public static final int S2650_INT_IRQ = 1;

    /* fake control port   M/~IO=0 D/~C=0 E/~NE=0 */
    public static final int S2650_CTRL_PORT = 0x100;

    /* fake data port      M/~IO=0 D/~C=1 E/~NE=0 */
    public static final int S2650_DATA_PORT = 0x101;

    /* extended i/o ports  M/~IO=0 D/~C=x E/~NE=1 */
    public static final int S2650_EXT_PORT = 0xff;
}
