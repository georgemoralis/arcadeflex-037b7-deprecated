/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.sndhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.common.PtrLib.ShortPtr;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.v056.mame.timer.*;
import static gr.codebb.arcadeflex.v056.mame.timerH.*;
import static gr.codebb.arcadeflex.old.sound.streams.*;
import gr.codebb.arcadeflex.v037b7.mame.sndintrfH.MachineSound;

public class warpwarp {

    static int CLOCK_16H = (18432000 / 3 / 2 / 16);
    static int CLOCK_1V = (18432000 / 3 / 2 / 384);

    static short[] decay = null;
    static int channel;
    static int sound_latch = 0;
    static int music1_latch = 0;
    static int music2_latch = 0;
    static int sound_signal = 0;
    static int sound_volume = 0;
    static Object sound_volume_timer = null;
    static int music_signal = 0;
    static int music_volume = 0;
    static Object music_volume_timer = null;
    static int noise = 0;

    public static timer_callback sound_volume_decay = new timer_callback() {
        public void handler(int param) {
            if (--sound_volume < 0) {
                sound_volume = 0;
            }
        }
    };

    public static WriteHandlerPtr warpwarp_sound_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            stream_update(channel, 0);
            sound_latch = data;
            sound_volume = 0x7fff;
            /* set sound_volume */
            noise = 0x0000;
            /* reset noise shifter */

 /* faster decay enabled? */
            if ((sound_latch & 8) != 0) {
                /*
			 * R85(?) is 10k, Rb is 0, C92 is 1uF
			 * charge time t1 = 0.693 * (R24 + Rb) * C57 . 0.22176s
			 * discharge time t2 = 0.693 * (Rb) * C57 . 0
			 * C90(?) is only charged via D17 (1N914), no discharge!
			 * Decay:
			 * discharge C90(?) (1uF) through R13||R14 (22k||47k)
			 * 0.639 * 15k * 1uF . 0.9585s
                 */
                if (sound_volume_timer != null) {
                    timer_remove(sound_volume_timer);
                }
                sound_volume_timer = timer_pulse(TIME_IN_HZ(32768 / 0.9585), 0, sound_volume_decay);
            } else {
                /*
			 * discharge only after R93 (100k) and through the 10k
			 * potentiometerin the amplifier section.
			 * 0.639 * 110k * 1uF . 7.0290s
			 * ...but this is not very realistic for the game sound :(
			 * maybe there _is_ a discharge through the diode D17?
                 */
                if (sound_volume_timer != null) {
                    timer_remove(sound_volume_timer);
                }
                //		sound_volume_timer = timer_pulse(TIME_IN_HZ(32768/7.0290), 0, sound_volume_decay);
                sound_volume_timer = timer_pulse(TIME_IN_HZ(32768 / 1.917), 0, sound_volume_decay);
            }
        }
    };

    public static WriteHandlerPtr warpwarp_music1_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            stream_update(channel, 0);
            music1_latch = data & 63;
        }
    };
    public static timer_callback music_volume_decay = new timer_callback() {
        public void handler(int param) {
            if (--music_volume < 0) {
                music_volume = 0;
            }
        }
    };

    public static WriteHandlerPtr warpwarp_music2_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            stream_update(channel, 0);
            music2_latch = data;
            music_volume = 0x7fff;
            /* fast decay enabled? */
            if ((music2_latch & 16) != 0) {
                /*
			 * Ra (R83?) is 10k, Rb is 0, C92 is 1uF
			 * charge time t1 = 0.693 * (Ra + Rb) * C . 0.22176s
			 * discharge time is (nearly) zero, because Rb is zero
			 * C95(?) is only charged via D17, not discharged!
			 * Decay:
			 * discharge C95(?) (10uF) through R13||R14 (22k||47k)
			 * 0.639 * 15k * 10uF . 9.585s
			 * ...I'm sure this is off by one number of magnitude :/
                 */
                if (music_volume_timer != null) {
                    timer_remove(music_volume_timer);
                }
                //		music_volume_timer = timer_pulse(TIME_IN_HZ(32768/9.585), 0, music_volume_decay);
                music_volume_timer = timer_pulse(TIME_IN_HZ(32768 / 0.9585), 0, music_volume_decay);
            } else {
                /*
			 * discharge through R14 (47k),
			 * discharge C95(?) (10uF) through R14 (47k)
			 * 0.639 * 47k * 10uF . 30.033s
                 */
                if (music_volume_timer != null) {
                    timer_remove(music_volume_timer);
                }
                //		music_volume_timer = timer_pulse(TIME_IN_HZ(32768/30.033), 0, music_volume_decay);
                music_volume_timer = timer_pulse(TIME_IN_HZ(32768 / 3.0033), 0, music_volume_decay);
            }

        }
    };
    static int vcarry = 0;
    static int vcount = 0;
    static int mcarry = 0;
    static int mcount = 0;
    public static StreamInitPtr warpwarp_sound_update = new StreamInitPtr() {
        public void handler(int param, ShortPtr buffer, int length) {

            while (length-- != 0) {
                buffer.writeinc((short) ((sound_signal + music_signal) / 2));

                /*
			 * The music signal is selected at a rate of 2H (1.536MHz) from the
			 * four bits of a 4 bit binary counter which is clocked with 16H,
			 * which is 192kHz, and is divided by 4 times (64 - music1_latch).
			 *	0 = 256 steps . 750 Hz
			 *	1 = 252 steps . 761.9 Hz
			 * ...
			 * 32 = 128 steps . 1500 Hz
			 * ...
			 * 48 =  64 steps . 3000 Hz
			 * ...
			 * 63 =   4 steps . 48 kHz
                 */
                mcarry -= CLOCK_16H / (4 * (64 - music1_latch));
                while (mcarry < 0) {
                    mcarry += Machine.sample_rate;
                    mcount++;
                    music_signal = (mcount & ~music2_latch & 15) != 0 ? decay[music_volume] : 0;
                    /* override by noise gate? */
                    if ((music2_latch & 32) != 0 && (noise & 0x8000) != 0) {
                        music_signal = decay[music_volume];
                    }
                }

                /* clock 1V = 8kHz */
                vcarry -= CLOCK_1V;
                while (vcarry < 0) {
                    vcarry += Machine.sample_rate;
                    vcount++;

                    /* noise is clocked with raising edge of 2V */
                    if ((vcount & 3) == 2) {
                        /* bit0 = bit0 ^ !bit10 */
                        if ((noise & 1) == ((noise >> 10) & 1)) {
                            noise = (noise << 1) | 1;
                        } else {
                            noise = noise << 1;
                        }
                    }

                    switch (sound_latch & 7) {
                        case 0:
                            /* 4V */
                            sound_signal = (vcount & 0x04) != 0 ? decay[sound_volume] : 0;
                            break;
                        case 1:
                            /* 8V */
                            sound_signal = (vcount & 0x08) != 0 ? decay[sound_volume] : 0;
                            break;
                        case 2:
                            /* 16V */
                            sound_signal = (vcount & 0x10) != 0 ? decay[sound_volume] : 0;
                            break;
                        case 3:
                            /* 32V */
                            sound_signal = (vcount & 0x20) != 0 ? decay[sound_volume] : 0;
                            break;
                        case 4:
                            /* TONE1 */
                            sound_signal = (vcount & 0x01) == 0 && (vcount & 0x10) == 0 ? decay[sound_volume] : 0;
                            break;
                        case 5:
                            /* TONE2 */
                            sound_signal = (vcount & 0x02) == 0 && (vcount & 0x20) == 0 ? decay[sound_volume] : 0;
                            break;
                        case 6:
                            /* TONE3 */
                            sound_signal = (vcount & 0x04) == 0 && (vcount & 0x40) == 0 ? decay[sound_volume] : 0;
                            break;
                        default:
                            /* NOISE */
 /* QH of 74164 #4V */
                            sound_signal = (noise & 0x8000) != 0 ? decay[sound_volume] : 0;
                    }

                }
            }
        }
    };

    public static ShStartPtr warpwarp_sh_start = new ShStartPtr() {
        public int handler(MachineSound msound) {
            int i;

            decay = new short[32768 * 2];
            if (decay == null) {
                return 1;
            }

            for (i = 0; i < 0x8000; i++) {
                decay[0x7fff - i] = (short) (0x7fff / Math.exp(1.0 * i / 4096));
            }

            channel = stream_init("WarpWarp", 100, Machine.sample_rate, 0, warpwarp_sound_update);
            return 0;
        }
    };

    public static ShStopPtr warpwarp_sh_stop = new ShStopPtr() {
        public void handler() {
            if (decay != null) {
                decay = null;
            }
            music_volume_timer = null;
            sound_volume_timer = null;
        }
    };

    public static ShUpdatePtr warpwarp_sh_update = new ShUpdatePtr() {
        public void handler() {
            stream_update(channel, 0);
        }
    };

}
