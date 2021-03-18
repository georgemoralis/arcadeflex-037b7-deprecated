/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.sndhrdw;

import gr.codebb.arcadeflex.common.PtrLib.BytePtr;
import static gr.codebb.arcadeflex.old.sound.mixer.*;
import static gr.codebb.arcadeflex.old.sound.mixerH.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.dac.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.samples.*;
import gr.codebb.arcadeflex.WIP.v037b7.sound.samplesH.Samplesinterface;

public class targ {

    static int tone_channel;

    public static int targ_spec_flag;
    static int targ_sh_ctrl0 = 0;
    static int targ_sh_ctrl1 = 0;
    static int tone_active;

    public static final int MAXFREQ_A_TARG = 125000;
    public static final int MAXFREQ_A_SPECTAR = 525000;

    static int sound_a_freq;
    static int tone_pointer;
    static int tone_offset;

    static char tone_prom[]
            = {
                0xE5, 0xE5, 0xED, 0xED, 0xE5, 0xE5, 0xED, 0xED, 0xE7, 0xE7, 0xEF, 0xEF, 0xE7, 0xE7, 0xEF, 0xEF,
                0xC1, 0xE1, 0xC9, 0xE9, 0xC5, 0xE5, 0xCD, 0xED, 0xC3, 0xE3, 0xCB, 0xEB, 0xC7, 0xE7, 0xCF, 0xEF
            };

    /* waveforms for the audio hardware */
    static byte waveform1[]
            = {
                /* sine-wave */
                0x0F, 0x0F, 0x0F, 0x06, 0x06, 0x09, 0x09, 0x06, 0x06, 0x09, 0x06, 0x0D, 0x0F, 0x0F, 0x0D, 0x00,
                (byte) 0xE6, (byte) 0xDE, (byte) 0xE1, (byte) 0xE6, (byte) 0xEC, (byte) 0xE6, (byte) 0xE7, (byte) 0xE7, (byte) 0xE7,
                (byte) 0xEC, (byte) 0xEC, (byte) 0xEC, (byte) 0xE7, (byte) 0xE1, (byte) 0xE1, (byte) 0xE7,};

    static void targ_tone_generator(int data) {
        int maxfreq;

        if (targ_spec_flag != 0) {
            maxfreq = MAXFREQ_A_TARG;
        } else {
            maxfreq = MAXFREQ_A_SPECTAR;
        }

        sound_a_freq = data;
        if (sound_a_freq == 0xFF || sound_a_freq == 0x00) {
            mixer_set_volume(tone_channel, 0);
        } else {
            mixer_set_sample_frequency(tone_channel, maxfreq / (0xFF - sound_a_freq));
            mixer_set_volume(tone_channel, tone_active * 100);
        }
    }

    public static ShStartPtr targ_sh_start = new ShStartPtr() {
        public int handler(MachineSound msound) {
            tone_channel = mixer_allocate_channel(50);

            tone_pointer = 0;
            tone_offset = 0;
            tone_active = 0;
            sound_a_freq = 0x00;
            mixer_set_volume(tone_channel, 0);
            mixer_play_sample(tone_channel, new BytePtr(waveform1), 32, 1000, 1);
            return 0;
        }
    };

    public static ShStopPtr targ_sh_stop = new ShStopPtr() {
        public void handler() {
            mixer_stop_sample(tone_channel);
        }
    };

    public static WriteHandlerPtr targ_sh_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int maxfreq;

            if (targ_spec_flag != 0) {
                maxfreq = MAXFREQ_A_TARG;
            } else {
                maxfreq = MAXFREQ_A_SPECTAR;
            }

            if (offset != 0) {
                if (targ_spec_flag != 0) {
                    if ((data & 0x02) != 0) {
                        tone_offset = 16;
                    } else {
                        tone_offset = 0;
                    }

                    if ((data & 0x01) != 0 && (targ_sh_ctrl1 & 0x01) == 0) {
                        tone_pointer++;
                        if (tone_pointer > 15) {
                            tone_pointer = 0;
                        }
                        targ_tone_generator(tone_prom[tone_pointer + tone_offset]);
                    }
                } else {
                    targ_tone_generator(data);
                }
                targ_sh_ctrl1 = data & 0xFF;
            } else {
                /* cpu music */
                if ((data & 0x01) != (targ_sh_ctrl0 & 0x01)) {
                    DAC_data_w.handler(0, (data & 0x01) * 0xFF);
                }
                /* Shoot */
                if ((data & 0x02) == 0 && (targ_sh_ctrl0 & 0x02) != 0) {
                    if (sample_playing(0) == 0) {
                        sample_start(0, 1, 0);
                    }
                }
                if ((data & 0x02) != 0 && (targ_sh_ctrl0 & 0x02) == 0) {
                    sample_stop(0);
                }

                /* Crash */
                if ((data & 0x20) != 0 && (targ_sh_ctrl0 & 0x20) == 0) {
                    if ((data & 0x40) != 0) {
                        sample_start(1, 2, 0);
                    } else {
                        sample_start(1, 0, 0);
                    }
                }

                /* Sspec */
                if ((data & 0x10) != 0) {
                    sample_stop(2);
                } else {
                    if ((data & 0x08) != (targ_sh_ctrl0 & 0x08)) {
                        if ((data & 0x08) != 0) {
                            sample_start(2, 3, 1);
                        } else {
                            sample_start(2, 4, 1);
                        }
                    }
                }

                /* Game (tone generator enable) */
                if ((data & 0x80) == 0 && (targ_sh_ctrl0 & 0x80) != 0) {
                    tone_pointer = 0;
                    tone_active = 0;
                    if (sound_a_freq == 0xFF || sound_a_freq == 0x00) {
                        mixer_set_volume(tone_channel, 0);
                    } else {
                        mixer_set_sample_frequency(tone_channel, maxfreq / (0xFF - sound_a_freq));
                        mixer_set_volume(tone_channel, 0);
                    }
                }
                if ((data & 0x80) != 0 && (targ_sh_ctrl0 & 0x80) == 0) {
                    tone_active = 1;
                }
                targ_sh_ctrl0 = data & 0xFF;
            }
        }
    };

}

