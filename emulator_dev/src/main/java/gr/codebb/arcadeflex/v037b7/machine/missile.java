/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.machine;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.pokey.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.*;
import static gr.codebb.arcadeflex.v037b7.vidhrdw.missile.missile_video_mult_w;
import static gr.codebb.arcadeflex.v037b7.vidhrdw.missile.missile_video_r;
import static gr.codebb.arcadeflex.v037b7.vidhrdw.missile.missile_video_w;
import static gr.codebb.arcadeflex.old.mame.common.*;

public class missile {

    static int ctrld;
    static int h_pos, v_pos;

    /**
     * *****************************************************************************************
     */
    public static ReadHandlerPtr missile_IN0_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            if (ctrld != 0) /* trackball */ {
                if (flip_screen() == 0) {
                    return ((readinputport(5) << 4) & 0xf0) | (readinputport(4) & 0x0f);
                } else {
                    return ((readinputport(7) << 4) & 0xf0) | (readinputport(6) & 0x0f);
                }
            } else /* buttons */ {
                return (readinputport(0));
            }
        }
    };

    /**
     * *****************************************************************************************
     */
    public static InitMachinePtr missile_init_machine = new InitMachinePtr() {
        public void handler() {
            h_pos = v_pos = 0;
        }
    };

    /**
     * *****************************************************************************************
     */
    public static WriteHandlerPtr missile_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int pc, opcode;
            offset = offset + 0x640;

            pc = cpu_getpreviouspc();
            opcode = cpu_readop(pc);

            /* 3 different ways to write to video ram - the third is caught by the core memory handler */
            if (opcode == 0x81) {
                /* 	STA ($00,X) */
                missile_video_w.handler(offset, data);
                return;
            }
            if (offset <= 0x3fff) {
                missile_video_mult_w.handler(offset, data);
                return;
            }

            /* $4c00 - watchdog */
            if (offset == 0x4c00) {
                watchdog_reset_w.handler(offset, data);
                return;
            }

            /* $4800 - various IO */
            if (offset == 0x4800) {
                flip_screen_w.handler(0, ~data & 0x40);
                coin_counter_w.handler(0, data & 0x20);
                coin_counter_w.handler(1, data & 0x10);
                coin_counter_w.handler(2, data & 0x08);
/*TODO*///                set_led_status(0, ~data & 0x02);
/*TODO*///                set_led_status(1, ~data & 0x04);
                ctrld = data & 1;
                return;
            }

            /* $4d00 - IRQ acknowledge */
            if (offset == 0x4d00) {
                return;
            }

            /* $4000 - $400f - Pokey */
            if (offset >= 0x4000 && offset <= 0x400f) {
                pokey1_w.handler(offset, data);
                return;
            }

            /* $4b00 - $4b07 - color RAM */
            if (offset >= 0x4b00 && offset <= 0x4b07) {
                int r, g, b;

                r = 0xff * ((~data >> 3) & 1);
                g = 0xff * ((~data >> 2) & 1);
                b = 0xff * ((~data >> 1) & 1);

                palette_change_color(offset - 0x4b00, r, g, b);

                return;
            }

            logerror("possible unmapped write, offset: %04x, data: %02x\n", offset, data);
        }
    };

    /**
     * *****************************************************************************************
     */
    public static UBytePtr missile_video2ram = new UBytePtr();

    public static ReadHandlerPtr missile_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            int pc, opcode;
            offset = offset + 0x1900;

            pc = cpu_getpreviouspc();
            opcode = cpu_readop(pc);

            if (opcode == 0xa1) {
                /* 	LDA ($00,X)  */
                return (missile_video_r.handler(offset));
            }

            if (offset >= 0x5000) {
                return missile_video2ram.read(offset - 0x5000);
            }

            if (offset == 0x4800) {
                return (missile_IN0_r.handler(0));
            }
            if (offset == 0x4900) {
                return (readinputport(1));
            }
            if (offset == 0x4a00) {
                return (readinputport(2));
            }

            if ((offset >= 0x4000) && (offset <= 0x400f)) {
                return (pokey1_r.handler(offset & 0x0f));
            }

            logerror("possible unmapped read, offset: %04x\n", offset);
            return 0;
        }
    };
}
