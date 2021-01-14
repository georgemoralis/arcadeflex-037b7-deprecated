/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.sndhrdw;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.old.mame.common.*;
import static gr.codebb.arcadeflex.old.sound.mixer.*;
import static gr.codebb.arcadeflex.old2.mame.mame.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.re.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.re.v037b7.mame.sndintrfH.*;

public class bosco {

    /* macro to convert 4-bit unsigned samples to 8-bit signed samples */
    public static int SAMPLE_CONV4(int a) {
        return (0x11 * ((a & 0x0f)) - 0x80);
    }

    static byte[] speech;/* 24k for speech */
    static int channel;

    public static ShStartPtr bosco_sh_start = new ShStartPtr() {
        public int handler(MachineSound msound) {
            int i;
            int /*unsigned char*/ bits;

            channel = mixer_allocate_channel(25);
            mixer_set_name(channel, "Samples");

            speech = new byte[2 * memory_region_length(REGION_SOUND2)];
            if (speech == null) {
                return 1;
            }

            /* decode the rom samples */
            for (i = 0; i < memory_region_length(REGION_SOUND2); i++) {
                bits = memory_region(REGION_SOUND2).read(i) & 0x0f;
                speech[2 * i] = (byte) SAMPLE_CONV4(bits);

                bits = ((memory_region(REGION_SOUND2).read(i) & 0xf0) >> 4) & 0xFF;
                speech[2 * i + 1] = (byte) SAMPLE_CONV4(bits);
            }

            return 0;
        }
    };

    public static ShStopPtr bosco_sh_stop = new ShStopPtr() {
        public void handler() {
            if (speech != null) {
                speech = null;
            }
        }
    };

    public static void bosco_sample_play(int offset, int length) {
        if (Machine.sample_rate == 0) {
            return;
        }

        mixer_play_sample(channel, new BytePtr(speech, offset), length, 4000, 0);
    }
}
