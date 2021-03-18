package gr.codebb.arcadeflex.v037b7.sound;

import gr.codebb.arcadeflex.common.PtrLib.ShortPtr;

public class cem3394H {

    public static final int MAX_CEM3394 = 6;

    public static abstract interface externalPtr {

        public abstract void handler(int chip, int count, ShortPtr buffer);
    }

    /* interface */
    public static class cem3394_interface {

        public cem3394_interface(int numchips, int[] volume, double[] vco_zero_freq, double[] filter_zero_freq, externalPtr[] external) {
            this.numchips = numchips;
            this.volume = volume;
            this.vco_zero_freq = vco_zero_freq;
            this.filter_zero_freq = filter_zero_freq;
            this.external = external;
        }
        int numchips;
        /* number of chips */
        int[] volume;//[MAX_CEM3394];						/* playback volume */
        double[] vco_zero_freq;//[MAX_CEM3394];				/* frequency at 0V for VCO */
        double[] filter_zero_freq;//[MAX_CEM3394];			/* frequency at 0V for filter */
        externalPtr[] external; //void (*external[MAX_CEM3394])(int, int, short *);/* external input source (at Machine.sample_rate) */
    }

    /* inputs */
    public static final int CEM3394_VCO_FREQUENCY = 0;
    public static final int CEM3394_MODULATION_AMOUNT = 1;
    public static final int CEM3394_WAVE_SELECT = 2;
    public static final int CEM3394_PULSE_WIDTH = 3;
    public static final int CEM3394_MIXER_BALANCE = 4;
    public static final int CEM3394_FILTER_RESONANCE = 5;
    public static final int CEM3394_FILTER_FREQENCY = 6;
    public static final int CEM3394_FINAL_GAIN = 7;

}
