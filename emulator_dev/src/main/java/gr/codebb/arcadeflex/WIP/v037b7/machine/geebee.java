/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.machine;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.old.mame.common.*;
import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sndhrdw.geebee.geebee_sound_w;

public class geebee {

    /* from sndhrdw/geebee.c */
 /* globals */
    public static int geebee_ball_h;
    public static int geebee_ball_v;
    public static int geebee_bgw;
    public static int geebee_ball_on;
    public static int geebee_inv;

    public static InterruptPtr geebee_interrupt = new InterruptPtr() {
        public int handler() {
            cpu_set_irq_line(0, 0, PULSE_LINE);
            return ignore_interrupt.handler();
        }
    };

    public static InterruptPtr kaitei_interrupt = new InterruptPtr() {
        public int handler() {
            cpu_set_irq_line(0, 0, HOLD_LINE);
            return ignore_interrupt.handler();
        }
    };

    public static ReadHandlerPtr geebee_in_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            int data = readinputport(offset & 3);
            if ((offset & 3) == 2) /* combine with Bonus Life settings ? */ {
                if ((data & 0x02) != 0) /* 5 lives? */ {
                    data |= readinputport(5);
                } else /* 3 lives */ {
                    data |= readinputport(4);
                }
            }
            logerror("in_r %d $%02X\n", offset & 3, data);
            return data;
        }
    };

    public static ReadHandlerPtr navalone_in_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            int data = readinputport(offset & 3);
            if ((offset & 3) == 3) {
                int joy = readinputport(4);
                /* map digital two-way joystick to two fixed VOLIN values */
                if ((joy & 1) != 0) {
                    data = 0xa0;
                }
                if ((joy & 2) != 0) {
                    data = 0x10;
                }
            }
            logerror("in_r %d $%02X\n", offset & 3, data);
            return data;
        }
    };

    public static WriteHandlerPtr geebee_out6_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            switch (offset & 3) {
                case 0:
                    logerror("out6_w:0 ball_h   $%02X\n", data);
                    geebee_ball_h = data ^ 0xff;
                    break;
                case 1:
                    logerror("out6_w:1 ball_v   $%02X\n", data);
                    geebee_ball_v = data ^ 0xff;
                    break;
                case 2:
                    logerror("out6_w:2 n/c      $%02X\n", data);
                    break;
                default:
                    logerror("out6_w:3 sound    $%02X\n", data);
                    geebee_sound_w.handler(offset, data);
                    break;
            }
        }
    };

    public static WriteHandlerPtr geebee_out7_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            switch (offset & 7) {
                case 0:
/*TODO*///                    set_led_status(0, data & 1);
                    break;
                case 1:
/*TODO*///                    set_led_status(1, data & 1);
                    break;
                case 2:
/*TODO*///                    set_led_status(2, data & 1);
                    break;
                case 3:
                    coin_counter_w.handler(0, data & 1);
                    break;
                case 4:
                    coin_lockout_global_w.handler(0, ~data & 1);
                    break;
                case 5:
                    logerror("out7_w:5 bgw      $%02X\n", data);
                    geebee_bgw = data & 1;
                    break;
                case 6:
                    logerror("out7_w:6 ball on  $%02X\n", data);
                    geebee_ball_on = data & 1;
                    break;
                case 7:
                    logerror("out7_w:7 inv      $%02X\n", data);
                    if (geebee_inv != (data & 1)) {
                        memset(dirtybuffer, 1, videoram_size[0]);
                    }
                    geebee_inv = data & 1;
                    break;
            }
        }
    };

}
