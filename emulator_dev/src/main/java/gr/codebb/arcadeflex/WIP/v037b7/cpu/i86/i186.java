/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.i86;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.I86H.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i186intfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i86.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i86time.i186_cycles;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.instr186.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.instr86.*;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.table186H.i186_instruction;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;
import gr.codebb.arcadeflex.old.arcadeflex.libc_old.FILE;
import static gr.codebb.arcadeflex.old.arcadeflex.libc_old.fclose;
import static gr.codebb.arcadeflex.old.arcadeflex.libc_old.fopen;
import static gr.codebb.arcadeflex.old.arcadeflex.libc_old.fprintf;
import static gr.codebb.arcadeflex.old.arcadeflex.libc_old.printf;

public class i186 extends i86 {

    public static FILE i186log = fopen("i186.log", "w");  //for debug purposes

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

    @Override
    public void reset(Object param) {
        super.reset(param);
        cycles = i186_cycles;
    }

    public static void i86_set_irq_callback(irqcallbacksPtr callback) {
        I.irq_callback = callback;
    }
    
    public static void i86_set_irq_line(int irqline, int state)
    {
       I.irq_state = state;
        /* if the IF is set, signal an interrupt */
        if (state != CLEAR_LINE && I.IF != 0) {
            i86_interrupt(-1);
        }
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
    public int execute(int num_cycles) {
        /* copy over the cycle counts if they're not correct */
        if (cycles.id != 80186) {
            cycles = i186_cycles;
        }

        /* adjust for any interrupts that came in */
        i86_ICount[0] = num_cycles;
        i86_ICount[0] -= I.extra_cycles;
        I.extra_cycles = 0;

        /* run until we're out */
        while (i86_ICount[0] > 0) {
            if (i186log != null) {
                fprintf(i186log, "[%04x:%04x]=%02x\tAX=%04x\tBX=%04x\tCX=%04x\tDX=%04x\n", I.sregs[CS], I.pc, ReadByte(I.pc), I.regs.w[AX], I.regs.w[BX], I.regs.w[CX], I.regs.w[DX]);
            }
            seg_prefix = 0;
            I.prevpc = I.pc;
            i186_instruction[FETCHOP()].handler();
        }

        /* adjust for any interrupts that came in */
        i86_ICount[0] -= I.extra_cycles;
        I.extra_cycles = 0;

        return num_cycles - i86_ICount[0];
    }
}
