/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.sndhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.machine.z80fmly.*;
import static gr.codebb.arcadeflex.WIP.v037b7.machine.z80fmlyH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.old.sound.mixer.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import gr.codebb.arcadeflex.v037b7.mame.sndintrfH.MachineSound;

public class senjyo {

    /* z80 pio */
    public static IntrPtr pio_interrupt = new IntrPtr() {

        public void handler(int state) {
            cpu_cause_interrupt(1, Z80_VECTOR(0, state));
        }
    };

    static z80pio_interface pio_intf = new z80pio_interface(
            1,
            new IntrPtr[]{pio_interrupt},
            new PortCallbackPtr[]{null},
            new PortCallbackPtr[]{null}
    );

    /* z80 ctc */
    public static IntrPtr ctc_interrupt = new IntrPtr() {

        public void handler(int state) {
            cpu_cause_interrupt(1, Z80_VECTOR(1, state));
        }
    };

    static z80ctc_interface ctc_intf = new z80ctc_interface(
            1, /* 1 chip */
            new int[]{0}, /* clock (filled in from the CPU 0 clock */
            new int[]{NOTIMER_2}, /* timer disables */
            new IntrPtr[]{ctc_interrupt}, /* interrupt handler */
            new WriteHandlerPtr[]{z80ctc_0_trg1_w}, /* ZC/TO0 callback */
            new WriteHandlerPtr[]{null}, /* ZC/TO1 callback */
            new WriteHandlerPtr[]{null} /* ZC/TO2 callback */
    );

    /* single tone generator */
    public static final int SINGLE_LENGTH = 10000;
    public static final int SINGLE_DIVIDER = 8;

    static byte[] _single;
    static int single_rate = 1000;
    static int single_volume = 0;
    static int channel;

    public static WriteHandlerPtr senjyo_volume_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            single_volume = data & 0x0f;
            mixer_set_volume(channel, single_volume * 100 / 15);
        }
    };

    public static ShStartPtr senjyo_sh_start = new ShStartPtr() {
        public int handler(MachineSound msound) {
            int i;

            channel = mixer_allocate_channel(15);
            mixer_set_name(channel, "Tone");

            /* z80 ctc init */
            ctc_intf.baseclock[0] = Machine.drv.cpu[1].cpu_clock;
            z80ctc_init(ctc_intf);

            /* z80 pio init */
            z80pio_init(pio_intf);

            if ((_single = new byte[SINGLE_LENGTH]) == null) {
                _single = null;
                return 1;
            }
            for (i = 0; i < SINGLE_LENGTH; i++) /* freq = ctc2 zco / 8 */ {
                _single[i] = (byte) (((i / SINGLE_DIVIDER) & 0x01) * 127);
            }

            /* CTC2 single tone generator */
            mixer_set_volume(channel, 0);
            mixer_play_sample(channel, new BytePtr(_single), SINGLE_LENGTH, single_rate, 1);

            return 0;
        }
    };

    public static ShStopPtr senjyo_sh_stop = new ShStopPtr() {
        public void handler() {
            _single = null;
        }
    };

    public static ShUpdatePtr senjyo_sh_update = new ShUpdatePtr() {
        public void handler() {
            double period;

            if (Machine.sample_rate == 0) {
                return;
            }

            /* ctc2 timer single tone generator frequency */
            period = z80ctc_getperiod(0, 2);
            if (period != 0) {
                single_rate = (int) (1.0 / period);
            } else {
                single_rate = 0;
            }

            mixer_set_sample_frequency(channel, single_rate);
        }
    };
}
