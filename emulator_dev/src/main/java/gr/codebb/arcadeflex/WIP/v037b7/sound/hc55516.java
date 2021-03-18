/*
 *  Ported to 0.37b7
 */
package gr.codebb.arcadeflex.WIP.v037b7.sound;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.sndintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.hc55516H.*;
import gr.codebb.arcadeflex.common.PtrLib.ShortPtr;
import static gr.codebb.arcadeflex.common.libc.cstdio.sprintf;
import static gr.codebb.arcadeflex.old.sound.streams.*;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.WriteHandlerPtr;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.*;

public class hc55516 extends snd_interface {
    public hc55516() {
        sound_num = SOUND_HC55516;
        name = "HC55516";
    }

    @Override
    public int chips_num(MachineSound msound) {
        return ((hc55516_interface) msound.sound_interface).num;
    }

    @Override
    public int chips_clock(MachineSound msound) {
        return 0;//no functionality expected
    }

    public static final double INTEGRATOR_LEAK_TC = 0.001;
    public static final double FILTER_DECAY_TC = 0.004;
    public static final double FILTER_CHARGE_TC = 0.004;
    public static final double FILTER_MIN = 0.0416;
    public static final double FILTER_MAX = 1.0954;
    public static final double SAMPLE_GAIN = 10000.0;

    public static class hc55516_data {
        int channel;
        char/*UINT8*/    last_clock;
        char/*UINT8*/    databit;
        char/*UINT8*/    shiftreg;

        short curr_value;
        short next_value;

        int/*UINT32*/    update_count;

        double filter;
        double integrator;
    }

    static hc55516_data[] hc55516 = new hc55516_data[MAX_HC55516];
    static double charge, decay, leak;

    @Override
    public int start(MachineSound msound) {
        hc55516_interface intf = (hc55516_interface) msound.sound_interface;
        int i;

		/* compute the fixed charge, decay, and leak time constants */
        charge = Math.pow(Math.exp(-1), 1.0 / (FILTER_CHARGE_TC * 16000.0));
        decay = Math.pow(Math.exp(-1), 1.0 / (FILTER_DECAY_TC * 16000.0));
        leak = Math.pow(Math.exp(-1), 1.0 / (INTEGRATOR_LEAK_TC * 16000.0));

		/* loop over HC55516 chips */
        for (i = 0; i < intf.num; i++) {
            String name;

			/* reset the channel */
            hc55516[i] = new hc55516_data();//memset(chip, 0, sizeof(*chip));

			/* create the stream */
            name = sprintf("HC55516 #%d", i);
            hc55516[i].channel = stream_init(name, intf.volume[i] & 0xff, Machine.sample_rate, i, hc55516_update);

			/* bail on fail */
            if (hc55516[i].channel == -1)
                return 1;
        }

		/* success */
        return 0;
    }

    public static StreamInitPtr hc55516_update = new StreamInitPtr() {
        public void handler(int num, ShortPtr buffer, int length) {
            int data, slope;
            int i;

		/* zero-length? bail */
            if (length == 0)
                return;

		/* track how many samples we've updated without a clock */
            hc55516[num].update_count += length;
            if (hc55516[num].update_count > Machine.sample_rate / 32) {
                hc55516[num].update_count = Machine.sample_rate;
                hc55516[num].next_value = 0;
            }

		/* compute the interpolation slope */
            data = hc55516[num].curr_value;
            slope = ((int) hc55516[num].next_value - data) / length;
            hc55516[num].curr_value = hc55516[num].next_value;

		/* reset the sample count */
            for (i = 0; i < length; i++, data += slope) {
                buffer.write(0, (short) data);
                buffer.offset += 2;
            }
        }
    };

    public static void hc55516_clock_w(int num, int state) {
        int clock = state & 1, diffclock;

		/* update the clock */
        diffclock = clock ^ hc55516[num].last_clock;
        hc55516[num].last_clock = (char) (clock & 0xFF);

		/* speech clock changing (active on rising edge) */
        if (diffclock != 0 && clock != 0) {
            double integrator = hc55516[num].integrator, temp;

			/* clear the update count */
            hc55516[num].update_count = 0;

			/* move the estimator up or down a step based on the bit */
            if (hc55516[num].databit != 0) {
                hc55516[num].shiftreg = (char) ((((hc55516[num].shiftreg << 1) | 1) & 7) & 0xFF);
                integrator += hc55516[num].filter;
            } else {
                hc55516[num].shiftreg = (char) (((hc55516[num].shiftreg << 1) & 7) & 0xFF);
                integrator -= hc55516[num].filter;
            }

			/* simulate leakage */
            integrator *= leak;

			/* if we got all 0's or all 1's in the last n bits, bump the step up */
            if (hc55516[num].shiftreg == 0 || hc55516[num].shiftreg == 7) {
                hc55516[num].filter = FILTER_MAX - ((FILTER_MAX - hc55516[num].filter) * charge);
                if (hc55516[num].filter > FILTER_MAX)
                    hc55516[num].filter = FILTER_MAX;
            }

			/* simulate decay */
            else {
                hc55516[num].filter *= decay;
                if (hc55516[num].filter < FILTER_MIN)
                    hc55516[num].filter = FILTER_MIN;
            }

			/* compute the sample as a 32-bit word */
            temp = integrator * SAMPLE_GAIN;
            hc55516[num].integrator = integrator;

			/* compress the sample range to fit better in a 16-bit word */
            if (temp < 0)
                hc55516[num].next_value = (short) (int) (temp / (-temp * (1.0 / 32768.0) + 1.0));
            else
                hc55516[num].next_value = (short) (int) (temp / (temp * (1.0 / 32768.0) + 1.0));

			/* update the output buffer before changing the registers */
            stream_update(hc55516[num].channel, 0);
        }
    }


    public static void hc55516_digit_w(int num, int data) {
        hc55516[num].databit = (char) ((data & 1) & 0xFF);
    }


    public static void hc55516_clock_clear_w(int num, int data) {
        hc55516_clock_w(num, 0);
    }


    public static void hc55516_clock_set_w(int num, int data) {
        hc55516_clock_w(num, 1);
    }


    public static void hc55516_digit_clock_clear_w(int num, int data) {
        hc55516[num].databit = (char) ((data & 1) & 0xFF);
        hc55516_clock_w(num, 0);
    }


    public static WriteHandlerPtr hc55516_0_digit_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            hc55516_digit_w(0, data);
        }
    };
    public static WriteHandlerPtr hc55516_0_clock_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            hc55516_clock_w(0, data);
        }
    };
    public static WriteHandlerPtr hc55516_0_clock_clear_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            hc55516_clock_clear_w(0, data);
        }
    };
    public static WriteHandlerPtr hc55516_0_clock_set_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            hc55516_clock_set_w(0, data);
        }
    };
    public static WriteHandlerPtr hc55516_0_digit_clock_clear_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            hc55516_digit_clock_clear_w(0, data);
        }
    };

    @Override
    public void stop() {
        //no functionality expected
    }

    @Override
    public void update() {
        //no functionality expected
    }

    @Override
    public void reset() {
        //no functionality expected
    }
}
