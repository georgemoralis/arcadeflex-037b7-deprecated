/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.machine;

import static gr.codebb.arcadeflex.v037b7.drivers.lkage.driver_lkage;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.flip_screen_w;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGION_CPU1;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.readinputport;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.cpu_setbank;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.sndintrf.soundlatch_w;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.ReadHandlerPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.WriteHandlerPtr;
import static gr.codebb.arcadeflex.v037b7.vidhrdw.bublbobl.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.old.arcadeflex.libc_old.rand;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.InterruptPtr;
import static gr.codebb.arcadeflex.v037b7.cpu.z80.z80H.Z80_NMI_INT;
import gr.codebb.arcadeflex.v056.mame.timer.timer_callback;
import static gr.codebb.arcadeflex.v056.mame.timer.timer_set;
import static gr.codebb.arcadeflex.v056.mame.timerH.TIME_NOW;

public class lkage {

    static /*unsigned*/ char from_main, from_mcu;
    static int mcu_sent = 0, main_sent = 0;

    /**
     * *************************************************************************
     *
     * Legend of Kage 68705 protection interface
     *
     * The following is ENTIRELY GUESSWORK!!! And moreover, the game seems to
     * work anyway regardless of what the mcu returns.
     *
     **************************************************************************
     */
    static /*unsigned*/ char portA_in, portA_out, ddrA;

    public static ReadHandlerPtr lkage_68705_portA_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            //logerror("%04x: 68705 port A read %02x\n",cpu_get_pc(),portA_in);
            return (portA_out & ddrA) | (portA_in & ~ddrA);
        }
    };

    public static WriteHandlerPtr lkage_68705_portA_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            //logerror("%04x: 68705 port A write %02x\n",cpu_get_pc(),data);
            portA_out = (char) (data & 0xFF);
        }
    };

    public static WriteHandlerPtr lkage_68705_ddrA_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            ddrA = (char) (data & 0xFF);
        }
    };

    /*
	 *  Port B connections:
	 *
	 *  all bits are logical 1 when read (+5V pullup)
	 *
	 *  1   W  when 1.0, enables latch which brings the command from main CPU (read from port A)
	 *  2   W  when 0.1, copies port A to the latch for the main CPU
     */
    static /*unsigned*/ char portB_in, portB_out, ddrB;

    public static ReadHandlerPtr lkage_68705_portB_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return (portB_out & ddrB) | (portB_in & ~ddrB);
        }
    };

    public static WriteHandlerPtr lkage_68705_portB_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            //logerror("%04x: 68705 port B write %02x\n",cpu_get_pc(),data);

            if ((ddrB & 0x02) != 0 && (~data & 0x02) != 0 && (portB_out & 0x02) != 0) {
                portA_in = from_main;
                if (main_sent != 0) {
                    cpu_set_irq_line(2, 0, CLEAR_LINE);
                }
                main_sent = 0;
                logerror("read command %02x from main cpu\n", portA_in);
            }
            if ((ddrB & 0x04) != 0 && (data & 0x04) != 0 && (~portB_out & 0x04) != 0) {
                logerror("send command %02x to main cpu\n", portA_out);
                from_mcu = portA_out;
                mcu_sent = 1;
            }

            portB_out = (char) (data & 0xFF);
        }
    };

    public static WriteHandlerPtr lkage_68705_ddrB_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            ddrB = (char) (data & 0xFF);
        }
    };

    static /*unsigned*/ char portC_in, portC_out, ddrC;

    public static ReadHandlerPtr lkage_68705_portC_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            portC_in = 0;
            if (main_sent != 0) {
                portC_in |= 0x01;
            }
            if (mcu_sent == 0) {
                portC_in |= 0x02;
            }
            //logerror("%04x: 68705 port C read %02x\n",cpu_get_pc(),portC_in);
            return (portC_out & ddrC) | (portC_in & ~ddrC);
        }
    };

    public static WriteHandlerPtr lkage_68705_portC_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            logerror("%04x: 68705 port C write %02x\n", cpu_get_pc(), data);
            portC_out = (char) (data & 0xFF);
        }
    };

    public static WriteHandlerPtr lkage_68705_ddrC_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            ddrC = (char) (data & 0xFF);
        }
    };

    public static WriteHandlerPtr lkage_mcu_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            logerror("%04x: mcu_w %02x\n", cpu_get_pc(), data);
            from_main = (char) (data & 0xFF);
            main_sent = 1;
            if (Machine.gamedrv == driver_lkage) { //arcadeflex fix : bootlegs doesn't have 3third cpu
                cpu_set_irq_line(2, 0, ASSERT_LINE);
            }
        }
    };

    public static ReadHandlerPtr lkage_mcu_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            logerror("%04x: mcu_r %02x\n", cpu_get_pc(), from_mcu);
            mcu_sent = 0;
            return from_mcu;
        }
    };

    public static ReadHandlerPtr lkage_mcu_status_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            int res = 0;

            /* bit 0 = when 1, mcu is ready to receive data from main cpu */
 /* bit 1 = when 1, mcu has sent data to the main cpu */
            //logerror("%04x: mcu_status_r\n",cpu_get_pc());
            if (main_sent == 0) {
                res |= 0x01;
            }
            if (mcu_sent != 0) {
                res |= 0x02;
            }

            return res;
        }
    };
}
