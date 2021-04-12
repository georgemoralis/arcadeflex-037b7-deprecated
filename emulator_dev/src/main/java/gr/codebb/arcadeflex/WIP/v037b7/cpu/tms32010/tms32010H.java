package gr.codebb.arcadeflex.WIP.v037b7.cpu.tms32010;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;

public class tms32010H {


    public static final int TMS320C10_PC = 1;
    public static final int TMS320C10_SP = 2;
    public static final int TMS320C10_STR = 3;
    public static final int TMS320C10_ACC = 4;
    public static final int TMS320C10_PREG = 5;
    public static final int TMS320C10_TREG = 6;
    public static final int TMS320C10_AR0 = 7;
    public static final int TMS320C10_AR1 = 8;
    public static final int TMS320C10_STK0 = 9;
    public static final int TMS320C10_STK1 = 10;
    public static final int TMS320C10_STK2 = 11;
    public static final int TMS320C10_STK3 = 12;

    public static final int TMS320C10_DATA_OFFSET = 0x0000;
    public static final int TMS320C10_PGM_OFFSET = 0x8000;
    /*TODO*///
    public static final int TMS320C10_ACTIVE_INT = 0;
    /* Activate INT external interrupt		 */
    public static final int TMS320C10_ACTIVE_BIO = 1;
    /* Activate BIO for use with BIOZ inst	 */
    public static final int TMS320C10_IGNORE_BIO = -1;
    /* Inhibit BIO polled external interrupt */
    public static final int TMS320C10_PENDING = 0x80000000;
    public static final int TMS320C10_NOT_PENDING = 0;
    public static final int TMS320C10_INT_NONE = -1;
    public static final int TMS320C10_ADDR_MASK = 0x0fff;

    /* TMS320C10 can only address 0x0fff */
 /*	 Input a word from given I/O port
     */
    public static int TMS320C10_In(int Port) {
        return cpu_readport(Port) & 0xFFFF;
    }

    /*	 Output a word to given I/O port
     */
    public static void TMS320C10_Out(int Port, int Value) {
        cpu_writeport(Port, Value & 0xFFFF);
    }

    /*	 Read a word from given ROM memory location
 * #define TMS320C10_ROM_RDMEM(A) READ_WORD(&ROM[(A<<1)])
     */
    public static int TMS320C10_ROM_RDMEM(int A) {
        return ((cpu_readmem16((A << 1) + TMS320C10_PGM_OFFSET) << 8) | cpu_readmem16(((A << 1) + TMS320C10_PGM_OFFSET + 1))) & 0xFFFF;
    }


    /*	 Write a word to given ROM memory location
 * #define TMS320C10_ROM_WRMEM(A,V) WRITE_WORD(&ROM[(A<<1)],V)
     */
    public static void TMS320C10_ROM_WRMEM(int A, int V) {
        cpu_writemem16(((A << 1) + TMS320C10_PGM_OFFSET + 1), (V & 0xff));
        cpu_writemem16((A << 1) + TMS320C10_PGM_OFFSET, ((V >> 8) & 0xff));
    }

    /*
 * Read a word from given RAM memory location
 * The following adds 8000h to the address, since MAME doesnt support
 * RAM and ROM living in the same address space. RAM really starts at
 * address 0 and are word entities.
     */
    public static int TMS320C10_RAM_RDMEM(int A) {
        return ((cpu_readmem16((A << 1) + TMS320C10_DATA_OFFSET) << 8) | cpu_readmem16(((A << 1) + TMS320C10_DATA_OFFSET + 1))) & 0xFFFF;
    }

    /*	 Write a word to given RAM memory location
	 The following adds 8000h to the address, since MAME doesnt support
	 RAM and ROM living in the same address space. RAM really starts at
	 address 0 and word entities.
 * #define TMS320C10_RAM_WRMEM(A,V) (cpu_writemem16lew_word(((A<<1)|0x8000),V))
     */
    public static void TMS320C10_RAM_WRMEM(int A, int V) {
        cpu_writemem16(((A << 1) + TMS320C10_DATA_OFFSET + 1), (V & 0x0ff));
        cpu_writemem16(((A << 1) + TMS320C10_DATA_OFFSET), ((V >> 8) & 0x0ff));
    }


    /*	 TMS320C10_RDOP() is identical to TMS320C10_RDMEM() except it is used for reading
 *	 opcodes. In case of system with memory mapped I/O, this function can be
 *	 used to greatly speed up emulation
     */
    public static int TMS320C10_RDOP(int A) {
        return ((cpu_readop((A << 1) + TMS320C10_PGM_OFFSET) << 8) | cpu_readop(((A << 1) + TMS320C10_PGM_OFFSET + 1))) & 0xFFFF;
    }

    /*
 * TMS320C10_RDOP_ARG() is identical to TMS320C10_RDOP() except it is used
 * for reading opcode arguments. This difference can be used to support systems
 * that use different encoding mechanisms for opcodes and opcode arguments
     */
    public static int TMS320C10_RDOP_ARG(int A) {
        return ((cpu_readop_arg((A << 1) + TMS320C10_PGM_OFFSET) << 8) | cpu_readop_arg(((A << 1) + TMS320C10_PGM_OFFSET + 1))) & 0xFFFF;
    }

}

