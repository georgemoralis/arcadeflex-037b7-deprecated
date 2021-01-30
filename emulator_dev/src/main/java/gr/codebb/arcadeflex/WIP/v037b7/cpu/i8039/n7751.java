 /*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.i8039;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i8039.i8039H.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;

public class n7751 extends i8039 {

    public n7751() {
        cpu_num = CPU_N7751;
        num_irqs = 1;
        default_vector = 0;
        overclock = 1.0;
        no_int = N7751_IGNORE_INT;
        irq_int = N7751_EXT_INT;
        nmi_int = -1;
        address_bits = 16;
        address_shift = 0;
        endianess = CPU_IS_LE;
        align_unit = 1;
        max_inst_len = 2;
        abits1 = ABITS1_16;
        abits2 = ABITS2_16;
        abitsmin = ABITS_MIN_16;
        icount = i8039_ICount;
    }

    @Override
    public String cpu_info(Object context, int regnum) {
        switch (regnum) {
            case CPU_INFO_NAME:
                return "N7751";
            case CPU_INFO_VERSION:
                return "1.1";
        }
        return super.cpu_info(context, regnum);
    }
}
