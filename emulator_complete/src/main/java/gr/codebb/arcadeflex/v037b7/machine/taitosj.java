/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.machine;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.cpu_setbank;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import gr.codebb.arcadeflex.v056.mame.timer.timer_callback;
import static gr.codebb.arcadeflex.v056.mame.timer.timer_set;
import static gr.codebb.arcadeflex.v056.mame.timerH.TIME_IN_USEC;
import static gr.codebb.arcadeflex.v056.mame.timerH.TIME_NOW;

public class taitosj {

    static int u8_fromz80, u8_toz80;
    static int zaccept, zready;

    public static InitMachinePtr taitosj_init_machine_nomcu = new InitMachinePtr() {
        public void handler() {
            /* set the default ROM bank (many games only have one bank and */
 /* never write to the bank selector register) */
            taitosj_bankswitch_w.handler(0, 0);

            zaccept = 1;
            zready = 0;
        }
    };
    public static InitMachinePtr taitosj_init_machine = new InitMachinePtr() {
        public void handler() {
            /* set the default ROM bank (many games only have one bank and */
 /* never write to the bank selector register) */
            taitosj_bankswitch_w.handler(0, 0);

            zaccept = 1;
            zready = 0;
            cpu_set_irq_line(2, 0, CLEAR_LINE);
        }
    };

    public static WriteHandlerPtr taitosj_bankswitch_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            UBytePtr RAM = memory_region(REGION_CPU1);

            cpu_setbank(1, new UBytePtr(RAM, (data & 0x80) != 0 ? 0x10000 : 0x6000));
        }
    };

    /**
     * *************************************************************************
     *
     * PROTECTION HANDLING
     *
     * Some of the games running on this hardware are protected with a 68705
     * mcu. It can either be on a daughter board containing Z80+68705+one ROM,
     * which replaces the Z80 on an unprotected main board; or it can be
     * built-in on the main board. The two are fucntionally equivalent.
     *
     * The 68705 can read commands from the Z80, send back result codes, and has
     * direct access to the Z80 memory space. It can also trigger IRQs on the
     * Z80.
     *
     **************************************************************************
     */
    public static ReadHandlerPtr taitosj_fake_data_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return 0;
        }
    };

    public static WriteHandlerPtr taitosj_fake_data_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {

        }
    };

    public static ReadHandlerPtr taitosj_fake_status_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return 0xff;
        }
    };

    /* timer callback : */
    public static timer_callback taitosj_mcu_real_data_r = new timer_callback() {
        public void handler(int param) {
            zaccept = 1;
        }
    };

    public static ReadHandlerPtr taitosj_mcu_data_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            timer_set(TIME_NOW, 0, taitosj_mcu_real_data_r);
            return u8_toz80;
        }
    };

    /* timer callback : */
    public static timer_callback taitosj_mcu_real_data_w = new timer_callback() {
        public void handler(int data) {
            zready = 1;
            cpu_set_irq_line(2, 0, ASSERT_LINE);
            u8_fromz80 = data & 0xFF;
        }
    };

    public static WriteHandlerPtr taitosj_mcu_data_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            timer_set(TIME_NOW, data, taitosj_mcu_real_data_w);
        }
    };

    public static ReadHandlerPtr taitosj_mcu_status_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            /* mcu synchronization */
            cpu_yielduntil_time(TIME_IN_USEC(5));

            /* bit 0 = the 68705 has read data from the Z80 */
 /* bit 1 = the 68705 has written data for the Z80 */
            return ~((zready << 0) | (zaccept << 1));
        }
    };

    static int u8_portA_in, u8_portA_out;

    public static ReadHandlerPtr taitosj_68705_portA_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return u8_portA_in;
        }
    };

    public static WriteHandlerPtr taitosj_68705_portA_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            u8_portA_out = data & 0xFF;
        }
    };

    /*
	 *  Port B connections:
	 *
	 *  all bits are logical 1 when read (+5V pullup)
	 *
	 *  0   W  !68INTRQ
	 *  1   W  !68LRD (enables latch which holds command from the Z80)
	 *  2   W  !68LWR (loads the latch which holds data for the Z80, and sets a
	 *                 status bit so the Z80 knows there's data waiting)
	 *  3   W  to Z80 !BUSRQ (aka !WAIT) pin
	 *  4   W  !68WRITE (triggers write to main Z80 memory area and increases low
	 *                   8 bits of the latched address)
	 *  5   W  !68READ (triggers read from main Z80 memory area and increases low
	 *                   8 bits of the latched address)
	 *  6   W  !LAL (loads the latch which holds the low 8 bits of the address of
	 *               the main Z80 memory location to access)
	 *  7   W  !UAL (loads the latch which holds the high 8 bits of the address of
	 *               the main Z80 memory location to access)
     */
    public static ReadHandlerPtr taitosj_68705_portB_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return 0xff;
        }
    };

    static int address;

    /* timer callback : 68705 is going to read data from the Z80 */
    public static timer_callback taitosj_mcu_data_real_r = new timer_callback() {
        public void handler(int data) {
            zready = 0;
        }
    };

    /* timer callback : 68705 is writing data for the Z80 */
    public static timer_callback taitosj_mcu_status_real_w = new timer_callback() {
        public void handler(int data) {
            u8_toz80 = data & 0xFF;
            zaccept = 0;
        }
    };

    public static WriteHandlerPtr taitosj_68705_portB_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if ((~data & 0x01) != 0) {

            }
            if ((~data & 0x02) != 0) {
                /* 68705 is going to read data from the Z80 */
                timer_set(TIME_NOW, 0, taitosj_mcu_data_real_r);
                cpu_set_irq_line(2, 0, CLEAR_LINE);
                u8_portA_in = u8_fromz80 & 0xFF;
            }
            if ((~data & 0x04) != 0) {
                /* 68705 is writing data for the Z80 */
                timer_set(TIME_NOW, u8_portA_out, taitosj_mcu_status_real_w);
            }
            if ((~data & 0x10) != 0) {
                memorycontextswap(0);
                cpu_writemem16(address, u8_portA_out);
                memorycontextswap(2);

                /* increase low 8 bits of latched address for burst writes */
                address = (address & 0xff00) | ((address + 1) & 0xff);
            }
            if ((~data & 0x20) != 0) {
                memorycontextswap(0);
                u8_portA_in = cpu_readmem16(address) & 0xFF;
                memorycontextswap(2);
            }
            if ((~data & 0x40) != 0) {
                address = (address & 0xff00) | u8_portA_out;
            }
            if ((~data & 0x80) != 0) {
                address = (address & 0x00ff) | (u8_portA_out << 8);
            }
        }
    };

    /*
	 *  Port C connections:
	 *
	 *  0   R  ZREADY (1 when the Z80 has written a command in the latch)
	 *  1   R  ZACCEPT (1 when the Z80 has read data from the latch)
	 *  2   R  from Z80 !BUSAK pin
	 *  3   R  68INTAK (goes 0 when the interrupt request done with 68INTRQ
	 *                  passes through)
     */
    public static ReadHandlerPtr taitosj_68705_portC_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            int res;

            res = (zready << 0) | (zaccept << 1);
            return res;
        }
    };

    /* Alpine Ski protection crack routines */
    static int protection_value;

    public static WriteHandlerPtr alpine_protection_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            switch (data) {
                case 0x05:
                    protection_value = 0x18;
                    break;
                case 0x07:
                case 0x0c:
                case 0x0f:
                    protection_value = 0x00;
                    /* not used as far as I can tell */
                    break;
                case 0x16:
                    protection_value = 0x08;
                    break;
                case 0x1d:
                    protection_value = 0x18;
                    break;
                default:
                    protection_value = data;
                    /* not used as far as I can tell */
                    break;
            }
        }
    };

    public static WriteHandlerPtr alpinea_bankswitch_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            taitosj_bankswitch_w.handler(offset, data);
            protection_value = data >> 2;
        }
    };

    public static ReadHandlerPtr alpine_port_2_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return input_port_2_r.handler(offset) | protection_value;
        }
    };
}
