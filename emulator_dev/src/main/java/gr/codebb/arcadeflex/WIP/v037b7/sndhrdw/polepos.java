/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.sndhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import static gr.codebb.arcadeflex.old.sound.mixer.*;
import static gr.codebb.arcadeflex.old.sound.streams.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.*;

public class polepos {

    static int sample_msb = 0;
    static int sample_lsb = 0;
    static int sample_enable = 0;

    static int current_position;
    static int sound_stream;

    /* speech section */
    static int channel;
    static byte[] speech;

    /* macro to convert 4-bit unsigned samples to 8-bit signed samples */
    public static int SAMPLE_CONV4(int a) {
        return (0x11 * ((a & 0x0f)) - 0x80);
    }
    public static final int SAMPLE_SIZE = 0x8000;

    static int volume_table[]
            = {
                2200 * 128 / 10100, 3200 * 128 / 10100, 4400 * 128 / 10100, 5400 * 128 / 10100,
                6900 * 128 / 10100, 7900 * 128 / 10100, 9100 * 128 / 10100, 10100 * 128 / 10100
            };
    static int[] sample_offsets = new int[5];

    /**
     * *********************************
     */
    /* Stream updater                   */
    /**
     * *********************************
     */
    public static StreamInitPtr engine_sound_update = new StreamInitPtr() {
        public void handler(int ch, ShortPtr buffer, int length) {
            int/*UINT32*/ current = current_position;
            int/*UINT32*/ step, clock, slot, volume;
            UBytePtr base;

            /* if we're not enabled, just fill with 0 */
            if (sample_enable == 0 || Machine.sample_rate == 0) {
                memset(buffer, 0, length * 2);
                return;
            }

            /* determine the effective clock rate */
            clock = (Machine.drv.cpu[0].cpu_clock / 64) * ((sample_msb + 1) * 64 + sample_lsb + 1) / (16 * 64);
            step = (clock << 12) / Machine.sample_rate;

            /* determine the volume */
            slot = (sample_msb >> 3) & 7;
            volume = volume_table[slot];
            base = new UBytePtr(memory_region(REGION_SOUND1), 0x1000 + slot * 0x800);

            /* fill in the sample */
            while (length-- != 0) {
                buffer.writeinc((short) ((base.read((current >> 12) & 0x7ff) * volume)));
                current += step;
            }

            current_position = current;
        }
    };

    /**
     * *********************************
     */
    /* Sound handler start              */
    /**
     * *********************************
     */
    public static ShStartPtr polepos_sh_start = new ShStartPtr() {
        public int handler(MachineSound msound) {
            int i, bits, last = 0;

            channel = mixer_allocate_channel(25);
            mixer_set_name(channel, "Speech");

            speech = new byte[16 * SAMPLE_SIZE];
            if (speech == null) {
                return 1;
            }

            /* decode the rom samples, interpolating to make it sound a little better */
            for (i = 0; i < SAMPLE_SIZE; i++) {
                bits = memory_region(REGION_SOUND1).read(0x5000 + i) & 0x0f;
                bits = SAMPLE_CONV4(bits);
                speech[16 * i + 0] = (byte) ((7 * last + 1 * bits) / 8);
                speech[16 * i + 1] = (byte) ((6 * last + 2 * bits) / 8);
                speech[16 * i + 2] = (byte) ((5 * last + 3 * bits) / 8);
                speech[16 * i + 3] = (byte) ((4 * last + 4 * bits) / 8);
                speech[16 * i + 4] = (byte) ((3 * last + 5 * bits) / 8);
                speech[16 * i + 5] = (byte) ((2 * last + 6 * bits) / 8);
                speech[16 * i + 6] = (byte) ((1 * last + 7 * bits) / 8);
                speech[16 * i + 7] = (byte) (bits);
                last = bits;

                bits = (memory_region(REGION_SOUND1).read(0x5000 + i) & 0xf0) >> 4;
                bits = SAMPLE_CONV4(bits);
                speech[16 * i + 8] = (byte) ((7 * last + 1 * bits) / 8);
                speech[16 * i + 9] = (byte) ((6 * last + 2 * bits) / 8);
                speech[16 * i + 10] = (byte) ((5 * last + 3 * bits) / 8);
                speech[16 * i + 11] = (byte) ((4 * last + 4 * bits) / 8);
                speech[16 * i + 12] = (byte) ((3 * last + 5 * bits) / 8);
                speech[16 * i + 13] = (byte) ((2 * last + 6 * bits) / 8);
                speech[16 * i + 14] = (byte) ((1 * last + 7 * bits) / 8);
                speech[16 * i + 15] = (byte) (bits);
                last = bits;
            }

            /* Japanese or US PROM? */
            if (memory_region(REGION_SOUND1).read(0x5000) == 0) {
                /* US */
                sample_offsets[0] = 0x0020;
                sample_offsets[1] = 0x0c00;
                sample_offsets[2] = 0x1c00;
                sample_offsets[3] = 0x2000;
                sample_offsets[4] = 0x2000;
            } else {
                /* Japan */
                sample_offsets[0] = 0x0020;
                sample_offsets[1] = 0x0900;
                sample_offsets[2] = 0x1f00;
                sample_offsets[3] = 0x4000;
                sample_offsets[4] = 0x6000;
                /* How is this triggered? */
            }

            sound_stream = stream_init("Engine Sound", 50, Machine.sample_rate, 0, engine_sound_update);
            current_position = 0;
            sample_msb = sample_lsb = 0;
            sample_enable = 0;
            return 0;
        }
    };

    /**
     * *********************************
     */
    /* Sound handler stop               */
    /**
     * *********************************
     */
    public static ShStopPtr polepos_sh_stop = new ShStopPtr() {
        public void handler() {
            if (speech != null) {
                speech = null;
            }
        }
    };

    /**
     * *********************************
     */
    /* Sound handler update 			*/
    /**
     * *********************************
     */
    public static ShUpdatePtr polepos_sh_update = new ShUpdatePtr() {
        public void handler() {
        }
    };

    /**
     * *********************************
     */
    /* Write LSB of engine sound		*/
    /**
     * *********************************
     */
    public static WriteHandlerPtr polepos_engine_sound_lsb_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            stream_update(sound_stream, 0);
            sample_lsb = data & 62;
            sample_enable = data & 1;
        }
    };

    /**
     * *********************************
     */
    /* Write MSB of engine sound		*/
    /**
     * *********************************
     */
    public static WriteHandlerPtr polepos_engine_sound_msb_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            stream_update(sound_stream, 0);
            sample_msb = data & 63;
        }
    };

    /**
     * *********************************
     */
    /* Play speech sample				*/
    /**
     * *********************************
     */
    public static void polepos_sample_play(int sample) {
        int start = sample_offsets[sample];
        int len = sample_offsets[sample + 1] - start;

        if (Machine.sample_rate == 0) {
            return;
        }

        mixer_play_sample(channel, new BytePtr(speech, start * 16), len * 16, 4000 * 8, 0);
    }
}
