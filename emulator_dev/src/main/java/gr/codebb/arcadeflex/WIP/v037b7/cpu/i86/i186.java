/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.i86;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i186intfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i86.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;

public class i186 extends i86 {

    public i186() {
        cpu_num = CPU_I186;
        num_irqs = 1;
        default_vector = 0;
        overclock = 1.0;
        no_int = I186_INT_NONE;
        irq_int = -1000;
        nmi_int = I186_NMI_INT;
        address_bits = 20;
        address_shift = 0;
        endianess = CPU_IS_LE;
        align_unit = 1;
        max_inst_len = 5;
        abits1 = ABITS1_20;
        abits2 = ABITS2_20;
        abitsmin = ABITS_MIN_20;
        icount = i86_ICount;
    }

    @Override
    public String cpu_info(Object context, int regnum) {
        switch (regnum) {
            case CPU_INFO_NAME:
                return "I186";
        }
        return super.cpu_info(context, regnum);
    }
/*TODO*///#include "i186intf.h"
/*TODO*///
/*TODO*///#undef PREFIX
/*TODO*///#define PREFIX(name) i186##name
/*TODO*///#define PREFIX186(name) i186##name
/*TODO*///
/*TODO*///#define I186
/*TODO*///#include "instr186.h"
/*TODO*///#include "table186.h"
/*TODO*///
/*TODO*///#include "instr86.c"
/*TODO*///#include "instr186.c"
/*TODO*///#undef I186
    @Override
    public int execute(int cycles) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    /*TODO*///	/* copy over the cycle counts if they're not correct */
/*TODO*///	if (cycles.id != 80186)
/*TODO*///		cycles = i186_cycles;
/*TODO*///
/*TODO*///	/* adjust for any interrupts that came in */
/*TODO*///	i86_ICount = num_cycles;
/*TODO*///	i86_ICount -= I.extra_cycles;
/*TODO*///	I.extra_cycles = 0;
/*TODO*///
/*TODO*///	/* run until we're out */
/*TODO*///	while (i86_ICount > 0)
/*TODO*///	{
/*TODO*///#ifdef VERBOSE_DEBUG
/*TODO*///		printf("[%04x:%04x]=%02x\tAX=%04x\tBX=%04x\tCX=%04x\tDX=%04x\n", I.sregs[CS], I.pc, ReadByte(I.pc), I.regs.w[AX],
/*TODO*///			   I.regs.w[BX], I.regs.w[CX], I.regs.w[DX]);
/*TODO*///#endif
/*TODO*///		CALL_MAME_DEBUG;
/*TODO*///
/*TODO*///		seg_prefix = FALSE;
/*TODO*///		I.prevpc = I.pc;
/*TODO*///		TABLE186;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* adjust for any interrupts that came in */
/*TODO*///	i86_ICount -= I.extra_cycles;
/*TODO*///	I.extra_cycles = 0;
/*TODO*///
/*TODO*///	return num_cycles - i86_ICount;
    }
}
