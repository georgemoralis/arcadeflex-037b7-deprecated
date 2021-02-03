/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.sndhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.sound.samples.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;

public class invinco {

    /* output port 0x02 definitions - sound effect drive outputs */
    public static final int OUT_PORT_2_SAUCER = 0x04;
    public static final int OUT_PORT_2_MOVE1 = 0x08;
    public static final int OUT_PORT_2_MOVE2 = 0x10;
    public static final int OUT_PORT_2_FIRE = 0x20;
    public static final int OUT_PORT_2_INVHIT = 0x40;
    public static final int OUT_PORT_2_SHIPHIT = 0x80;

    public static void PLAY(int id, int loop) {
        sample_start(id, id, loop);
    }

    public static void STOP(int id) {
        sample_stop(id);
    }

    /* sample file names */
    public static String invinco_sample_names[]
            = {
                "*invinco",
                "saucer.wav",
                "move1.wav",
                "move2.wav",
                "fire.wav",
                "invhit.wav",
                "shiphit.wav",
                "move3.wav", /* currently not used */
                "move4.wav", /* currently not used */
                null
            };

    /* sample sound IDs - must match sample file name table above */
    public static final int SND_SAUCER = 0;
    public static final int SND_MOVE1 = 1;
    public static final int SND_MOVE2 = 2;
    public static final int SND_FIRE = 3;
    public static final int SND_INVHIT = 4;
    public static final int SND_SHIPHIT = 5;
    public static final int SND_MOVE3 = 6;
    public static final int SND_MOVE4 = 7;

    static int port2State = 0;
    public static WriteHandlerPtr invinco_sh_port2_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int bitsChanged;
            int bitsGoneHigh;
            int bitsGoneLow;

            bitsChanged = port2State ^ data;
            bitsGoneHigh = bitsChanged & data;
            bitsGoneLow = bitsChanged & ~data;

            port2State = data;

            if ((bitsGoneLow & OUT_PORT_2_SAUCER) != 0) {
                PLAY(SND_SAUCER, 0);
            }

            if ((bitsGoneLow & OUT_PORT_2_MOVE1) != 0) {
                PLAY(SND_MOVE1, 0);
            }

            if ((bitsGoneLow & OUT_PORT_2_MOVE2) != 0) {
                PLAY(SND_MOVE2, 0);
            }

            if ((bitsGoneLow & OUT_PORT_2_FIRE) != 0) {
                PLAY(SND_FIRE, 0);
            }

            if ((bitsGoneLow & OUT_PORT_2_INVHIT) != 0) {
                PLAY(SND_INVHIT, 0);
            }

            if ((bitsGoneLow & OUT_PORT_2_SHIPHIT) != 0) {
                PLAY(SND_SHIPHIT, 0);
            }
        }
    };
}
