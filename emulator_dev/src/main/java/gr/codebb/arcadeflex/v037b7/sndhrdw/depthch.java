 /*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.sndhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.sound.samples.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;

public class depthch {

    /* output port 0x01 definitions - sound effect drive outputs */
    public static final int OUT_PORT_1_LONGEXPL = 0x01;
    public static final int OUT_PORT_1_SHRTEXPL = 0x02;
    public static final int OUT_PORT_1_SPRAY = 0x04;
    public static final int OUT_PORT_1_SONAR = 0x08;

    public static void PLAY(int id, int loop) {
        sample_start(id, id, loop);
    }

    public static void STOP(int id) {
        sample_stop(id);
    }

    /* sample file names */
    public static String depthch_sample_names[]
            = {
                "*depthch",
                "longex.wav",
                "shortex.wav",
                "spray.wav",
                "sonar.wav",
                "sonarena.wav", /* currently not used */
                null
            };

    /* sample sound IDs - must match sample file name table above */
    public static final int SND_LONGEXPL = 0;
    public static final int SND_SHRTEXPL = 1;
    public static final int SND_SPRAY = 2;
    public static final int SND_SONAR = 3;
    public static final int SND_SONARENA = 4;

    static int port1State = 0;
    public static WriteHandlerPtr depthch_sh_port1_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int bitsChanged;
            int bitsGoneHigh;
            int bitsGoneLow;

            bitsChanged = port1State ^ data;
            bitsGoneHigh = bitsChanged & data;
            bitsGoneLow = bitsChanged & ~data;

            port1State = data;

            if ((bitsGoneHigh & OUT_PORT_1_LONGEXPL) != 0) {
                PLAY(SND_LONGEXPL, 0);
            }

            if ((bitsGoneHigh & OUT_PORT_1_SHRTEXPL) != 0) {
                PLAY(SND_SHRTEXPL, 0);
            }

            if ((bitsGoneHigh & OUT_PORT_1_SPRAY) != 0) {
                PLAY(SND_SPRAY, 0);
            }

            if ((bitsGoneHigh & OUT_PORT_1_SONAR) != 0) {
                PLAY(SND_SONAR, 1);
            }
            if ((bitsGoneLow & OUT_PORT_1_SONAR) != 0) {
                STOP(SND_SONAR);
            }
        }
    };
}
