/*** m6809: Portable 6809 emulator ******************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.WIP.v037b7.cpu.m6809;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;

public class m6809H
{
	
	public static final int M6809_PC            = 1;
        public static final int M6809_S             = 2;
        public static final int M6809_CC            = 3;
        public static final int M6809_A             = 4;
        public static final int M6809_B             = 5;
        public static final int M6809_U             = 6;
        public static final int M6809_X             = 7;
        public static final int M6809_Y             = 8;
	public static final int M6809_DP            = 9;
        public static final int M6809_NMI_STATE     = 10;
        public static final int M6809_IRQ_STATE     = 11;
        public static final int M6809_FIRQ_STATE    = 12;
	
	public static final int M6809_INT_NONE  = 0;   /* No interrupt required */
	public static final int M6809_INT_IRQ	= 1;	/* Standard IRQ interrupt */
	public static final int M6809_INT_FIRQ	= 2;	/* Fast IRQ */
	public static final int M6809_INT_NMI	= 4;	/* NMI */
	public static final int M6809_IRQ_LINE	= 0;	/* IRQ line number */
	public static final int M6809_FIRQ_LINE = 1;   /* FIRQ line number */
	
	
	/****************************************************************************/
	/* Read a byte from given memory location                                   */
	/****************************************************************************/
	/* ASG 971005 -- changed to cpu_readmem16/cpu_writemem16 */
	public static char M6809_RDMEM(int Addr){ return (char)(cpu_readmem16(Addr) & 0xFF); }
	
	/****************************************************************************/
	/* Write a byte to given memory location                                    */
	/****************************************************************************/
	public static void M6809_WRMEM(int Addr, int Value){ cpu_writemem16(Addr & 0xFFFF,Value & 0xFF); }
	
	/****************************************************************************/
	/* Z80_RDOP() is identical to Z80_RDMEM() except it is used for reading     */
	/* opcodes. In case of system with memory mapped I/O, this function can be  */
	/* used to greatly speed up emulation                                       */
	/****************************************************************************/
	public static char M6809_RDOP(int Addr){ return cpu_readop(Addr); }
	
	/****************************************************************************/
	/* Z80_RDOP_ARG() is identical to Z80_RDOP() except it is used for reading  */
	/* opcode arguments. This difference can be used to support systems that    */
	/* use different encoding mechanisms for opcodes and opcode arguments       */
	/****************************************************************************/
	public static int M6809_RDOP_ARG(int Addr){ return cpu_readop_arg(Addr); }
		
}
