/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.machine;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGION_CPU1;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.*;

public class jedi {

    static /*unsigned*/ char jedi_control_num = 0;
    static /*unsigned*/ char jedi_soundlatch;
    static /*unsigned*/ char jedi_soundacklatch;
    static /*unsigned*/ char jedi_com_stat;

    public static WriteHandlerPtr jedi_rom_banksel_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            UBytePtr RAM = memory_region(REGION_CPU1);

            if ((data & 0x01) != 0) {
                cpu_setbank(1, new UBytePtr(RAM, 0x10000));
            }
            if ((data & 0x02) != 0) {
                cpu_setbank(1, new UBytePtr(RAM, 0x14000));
            }
            if ((data & 0x04) != 0) {
                cpu_setbank(1, new UBytePtr(RAM, 0x18000));
            }
        }
    };

    public static WriteHandlerPtr jedi_sound_reset_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if ((data & 1) != 0) {
                cpu_set_reset_line(1, CLEAR_LINE);
            } else {
                cpu_set_reset_line(1, ASSERT_LINE);
            }
        }
    };

    public static ReadHandlerPtr jedi_control_r = new ReadHandlerPtr() {
        public int handler(int offset) {

            if (jedi_control_num == 0) {
                return readinputport(2);
            } else if (jedi_control_num == 2) {
                return readinputport(3);
            }
            return 0;
        }
    };

    public static WriteHandlerPtr jedi_control_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {

            jedi_control_num = (char) (offset * 0xFF);
        }
    };

    public static WriteHandlerPtr jedi_soundlatch_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            jedi_soundlatch = (char) (data & 0xFF);
            jedi_com_stat |= 0x80;
        }
    };

    public static WriteHandlerPtr jedi_soundacklatch_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            jedi_soundacklatch = (char) (data & 0xFF);
            jedi_com_stat |= 0x40;
        }
    };

    public static ReadHandlerPtr jedi_soundlatch_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            jedi_com_stat &= 0x7F;
            return jedi_soundlatch & 0xFF;
        }
    };

    public static ReadHandlerPtr jedi_soundacklatch_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            jedi_com_stat &= 0xBF;
            return jedi_soundacklatch & 0xFF;
        }
    };

    public static ReadHandlerPtr jedi_soundstat_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return jedi_com_stat & 0xFF;
        }
    };
    static /*unsigned*/ char d;
    public static ReadHandlerPtr jedi_mainstat_r = new ReadHandlerPtr() {
        public int handler(int offset) {

            d = (char) (((jedi_com_stat & 0xC0) >> 1) & 0xFF);
            d = (char) ((d | (input_port_1_r.handler(0) & 0x80)) & 0xFF);
            d = (char) ((d | 0x1B) & 0xFF);
            return d & 0xFF;
        }
    };

}
