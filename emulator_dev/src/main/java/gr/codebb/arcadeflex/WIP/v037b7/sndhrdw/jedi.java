/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.sndhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.sound._5220intf.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;

public class jedi {

    /* Misc sound code */
    static /*unsigned*/ char speech_write_buffer;

    public static WriteHandlerPtr jedi_speech_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {

            if (offset < 0xff) {
                speech_write_buffer = (char) (data & 0xFF);
            } else if (offset < 0x1ff) {
                tms5220_data_w.handler(0, speech_write_buffer);
            }
        }
    };

    public static ReadHandlerPtr jedi_speech_ready_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return ((tms5220_ready_r())!=0?0:1) << 7;
        }
    };
}
