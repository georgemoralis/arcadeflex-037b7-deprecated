/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.machine;

import static gr.codebb.arcadeflex.WIP.v037b7.machine._74123H.*;
import static gr.codebb.arcadeflex.common.libc.expressions.NOT;
import static gr.codebb.arcadeflex.v056.mame.timer.*;
import static gr.codebb.arcadeflex.v056.mame.timerH.*;

public class _74123 {

    public static class TTL74123 {

        TTL74123_interface intf;
        int trigger;
        /* pin 2/10 */
        int trigger_comp;
        /* pin 1/9 */
        int reset_comp;
        /* pin 3/11 */
        int output;
        /* pin 13/5 */
        Object timer;
    };

    static TTL74123[] chip = new TTL74123[MAX_TTL74123];

    static {
        for (int i = 0; i < MAX_TTL74123; i++) {
            chip[i] = new TTL74123();
        }
    }

    public static void set_output(int which, int data) {
        chip[which].output = data;
        chip[which].intf.output_changed_cb.handler();
    }

    public static void TTL74123_config(int which, TTL74123_interface intf) {
        if (which >= MAX_TTL74123) {
            return;
        }

        chip[which].intf = intf;

        /* all inputs are open first */
        chip[which].trigger = 1;
        chip[which].trigger_comp = 1;
        chip[which].reset_comp = 1;
        set_output(which, 1);
    }

    void TTL74123_unconfig() {
        int i;

        for (i = 0; i < MAX_TTL74123; i++) {
            if (chip[i].timer != null) {
                timer_remove(chip[i].timer);
            }
        }

        //memset(&chip, 0, sizeof(chip));
    }

    public static timer_callback clear_callback = new timer_callback() {
        public void handler(int which) {
            TTL74123 c = chip[which];

            c.timer = null;
            set_output(which, 0);
        }
    };

    public static void TTL74123_trigger_w(int which, int data) {
        TTL74123 c = chip[which];

        /* trigger_comp=lo and rising edge on trigger (while reset_comp is hi) */
        if (data != 0) {
            //CHECK_TRIGGER(!c.trigger_comp && !c.trigger && c.reset_comp)
            if ((c.trigger_comp == 0 && c.trigger == 0 && c.reset_comp != 0)) {
                double duration = TIME_IN_SEC(0.68 * c.intf.res * c.intf.cap);

                if (c.timer != null) {
                    timer_reset(c.timer, duration);
                } else {
                    set_output(which, 1);
                    c.timer = timer_set(duration, which, clear_callback);
                }
            }
        } else {
            if (c.timer != null) {
                timer_reset(c.timer, TIME_NOW);
            }
        }

        c.trigger = data;
    }

    public static void TTL74123_trigger_comp_w(int which, int data) {
        TTL74123 c = chip[which];

        /* trigger=hi and falling edge on trigger_comp (while reset_comp is hi) */
        if (data == 0) {
            //CHECK_TRIGGER(c.trigger && c.trigger_comp && c.reset_comp)
            if (c.trigger != 0 && c.trigger_comp != 0 && c.reset_comp != 0) {
                double duration = TIME_IN_SEC(0.68 * c.intf.res * c.intf.cap);

                if (c.timer != null) {
                    timer_reset(c.timer, duration);
                } else {
                    set_output(which, 1);
                    c.timer = timer_set(duration, which, clear_callback);
                }
            }
        } else {
            if (c.timer != null) {
                timer_reset(c.timer, TIME_NOW);
            }
        }

        c.trigger_comp = data;
    }

    public static void TTL74123_reset_comp_w(int which, int data) {
        TTL74123 c = chip[which];

        /* trigger=hi, trigger_comp=lo and rising edge on reset_comp */
        if (data != 0) {
            //CHECK_TRIGGER(c.trigger && !c.trigger_comp && !c.reset_comp)
            if (c.trigger != 0 && c.trigger_comp == 0 && c.reset_comp == 0) {
                double duration = TIME_IN_SEC(0.68 * c.intf.res * c.intf.cap);

                if (c.timer != null) {
                    timer_reset(c.timer, duration);
                } else {
                    set_output(which, 1);
                    c.timer = timer_set(duration, which, clear_callback);
                }
            }
        } else {
            if (c.timer != null) {
                timer_reset(c.timer, TIME_NOW);
            }
        }

        c.reset_comp = data;
    }

    public static int TTL74123_output_r(int which) {
        return chip[which].output;
    }

    public static int TTL74123_output_comp_r(int which) {
        return NOT(chip[which].output);
    }
}
