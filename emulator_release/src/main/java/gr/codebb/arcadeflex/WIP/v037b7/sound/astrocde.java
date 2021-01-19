/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.sound;

import static gr.codebb.arcadeflex.WIP.v037b7.sound.astrocdeH.*;
import gr.codebb.arcadeflex.common.PtrLib.ShortPtr;
import static gr.codebb.arcadeflex.common.libc.cstdlib.rand;
import static gr.codebb.arcadeflex.old.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.old.sound.mixer.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.v037b7.cpu.z80.z80H.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.sndintrf.*;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.*;


public class astrocde extends snd_interface {

    public astrocde() {
        sound_num = SOUND_ASTROCADE;
        name = "Astrocade";
    }
    static astrocade_interface intf;

    static int emulation_rate;
    static int div_by_N_factor;
    static int buffer_len;

    static ShortPtr[] astrocade_buffer = new ShortPtr[MAX_ASTROCADE_CHIPS];

    static int[] sample_pos = new int[MAX_ASTROCADE_CHIPS];

    static int[] current_count_A = new int[MAX_ASTROCADE_CHIPS];
    static int[] current_count_B = new int[MAX_ASTROCADE_CHIPS];
    static int[] current_count_C = new int[MAX_ASTROCADE_CHIPS];
    static int[] current_count_V = new int[MAX_ASTROCADE_CHIPS];
    static int[] current_count_N = new int[MAX_ASTROCADE_CHIPS];

    static int[] current_state_A = new int[MAX_ASTROCADE_CHIPS];
    static int[] current_state_B = new int[MAX_ASTROCADE_CHIPS];
    static int[] current_state_C = new int[MAX_ASTROCADE_CHIPS];
    static int[] current_state_V = new int[MAX_ASTROCADE_CHIPS];

    static int[] current_size_A = new int[MAX_ASTROCADE_CHIPS];
    static int[] current_size_B = new int[MAX_ASTROCADE_CHIPS];
    static int[] current_size_C = new int[MAX_ASTROCADE_CHIPS];
    static int[] current_size_V = new int[MAX_ASTROCADE_CHIPS];
    static int[] current_size_N = new int[MAX_ASTROCADE_CHIPS];

    static int channel;

    /* Registers */
    static int[] master_osc = new int[MAX_ASTROCADE_CHIPS];
    static int[] freq_A = new int[MAX_ASTROCADE_CHIPS];
    static int[] freq_B = new int[MAX_ASTROCADE_CHIPS];
    static int[] freq_C = new int[MAX_ASTROCADE_CHIPS];
    static int[] vol_A = new int[MAX_ASTROCADE_CHIPS];
    static int[] vol_B = new int[MAX_ASTROCADE_CHIPS];
    static int[] vol_C = new int[MAX_ASTROCADE_CHIPS];
    static int[] vibrato = new int[MAX_ASTROCADE_CHIPS];
    static int[] vibrato_speed = new int[MAX_ASTROCADE_CHIPS];
    static int[] mux = new int[MAX_ASTROCADE_CHIPS];
    static int[] noise_am = new int[MAX_ASTROCADE_CHIPS];
    static int[] vol_noise4 = new int[MAX_ASTROCADE_CHIPS];
    static int[] vol_noise8 = new int[MAX_ASTROCADE_CHIPS];

    static int randbyte = 0;
    static int randbit = 1;

    @Override
    public int chips_num(MachineSound msound) {
        return ((astrocade_interface) msound.sound_interface).num;
    }

    @Override
    public int chips_clock(MachineSound msound) {
        return ((astrocade_interface) msound.sound_interface).baseclock;
    }

    static void astrocade_update(int num, int newpos) {
        ShortPtr buffer = astrocade_buffer[num];

        int pos = sample_pos[num];
        int i, data, data16, noise_plus_osc, vib_plus_osc;

        for (i = pos; i < newpos; i++) {
            if (current_count_N[i] == 0) {
                randbyte = rand() & 0xff;
            }

            current_size_V[num] = 32768 * vibrato_speed[num] / div_by_N_factor;

            if (mux[num] == 0) {
                if (current_state_V[num] == -1) {
                    vib_plus_osc = (master_osc[num] - vibrato[num]) & 0xff;
                } else {
                    vib_plus_osc = master_osc[num];
                }
                current_size_A[num] = vib_plus_osc * freq_A[num] / div_by_N_factor;
                current_size_B[num] = vib_plus_osc * freq_B[num] / div_by_N_factor;
                current_size_C[num] = vib_plus_osc * freq_C[num] / div_by_N_factor;
            } else {
                noise_plus_osc = ((master_osc[num] - (vol_noise8[num] & randbyte))) & 0xff;
                current_size_A[num] = noise_plus_osc * freq_A[num] / div_by_N_factor;
                current_size_B[num] = noise_plus_osc * freq_B[num] / div_by_N_factor;
                current_size_C[num] = noise_plus_osc * freq_C[num] / div_by_N_factor;
                current_size_N[num] = 2 * noise_plus_osc / div_by_N_factor;
            }

            data = (current_state_A[num] * vol_A[num]
                    + current_state_B[num] * vol_B[num]
                    + current_state_C[num] * vol_C[num]);

            if (noise_am[num] != 0) {
                randbit = rand() & 1;
                data = data + randbit * vol_noise4[num];
            }

            data16 = data << 8;
            buffer.write(pos++, (short) data16);

            if (current_count_A[num] >= current_size_A[num]) {
                current_state_A[num] = -current_state_A[num];
                current_count_A[num] = 0;
            } else {
                current_count_A[num]++;
            }

            if (current_count_B[num] >= current_size_B[num]) {
                current_state_B[num] = -current_state_B[num];
                current_count_B[num] = 0;
            } else {
                current_count_B[num]++;
            }

            if (current_count_C[num] >= current_size_C[num]) {
                current_state_C[num] = -current_state_C[num];
                current_count_C[num] = 0;
            } else {
                current_count_C[num]++;
            }

            if (current_count_V[num] >= current_size_V[num]) {
                current_state_V[num] = -current_state_V[num];
                current_count_V[num] = 0;
            } else {
                current_count_V[num]++;
            }

            if (current_count_N[num] >= current_size_N[num]) {
                current_count_N[num] = 0;
            } else {
                current_count_N[num]++;
            }
        }
        sample_pos[num] = pos;
    }

    @Override
    public int start(MachineSound msound) {
        int i;

        intf = (astrocade_interface) msound.sound_interface;

        if (Machine.sample_rate == 0) {
            return 0;
        }

        buffer_len = (int) (Machine.sample_rate / Machine.drv.frames_per_second);

        emulation_rate = (int) (buffer_len * Machine.drv.frames_per_second);
        div_by_N_factor = intf.baseclock / emulation_rate;

        channel = mixer_allocate_channels(intf.num, intf.volume);
        /* reserve buffer */
        for (i = 0; i < intf.num; i++) {
            if ((astrocade_buffer[i] = new ShortPtr(2 * buffer_len)) == null) {
                while (--i >= 0) {
                    astrocade_buffer[i] = null;
                }
                return 1;
            }
            /* reset state */
            sample_pos[i] = 0;
            current_count_A[i] = 0;
            current_count_B[i] = 0;
            current_count_C[i] = 0;
            current_count_V[i] = 0;
            current_count_N[i] = 0;
            current_state_A[i] = 1;
            current_state_B[i] = 1;
            current_state_C[i] = 1;
            current_state_V[i] = 1;
        }

        return 0;
    }

    @Override
    public void stop() {
        int i;

        for (i = 0; i < intf.num; i++) {
            astrocade_buffer[i] = null;
        }
    }

    static void astrocade_sound_w(int num, int offset, int data) {
        int i, bvalue, temp_vib;

        /* update */
        astrocade_update(num, sound_scalebufferpos(buffer_len));

        switch (offset) {
            case 0:
                /* Master Oscillator */
                master_osc[num] = data + 1;
                break;

            case 1:
                /* Tone A Frequency */
                freq_A[num] = data + 1;
                break;

            case 2:
                /* Tone B Frequency */
                freq_B[num] = data + 1;
                break;

            case 3:
                /* Tone C Frequency */
                freq_C[num] = data + 1;
                break;

            case 4:
                /* Vibrato Register */
                vibrato[num] = data & 0x3f;

                temp_vib = (data >> 6) & 0x03;
                vibrato_speed[num] = 1;
                for (i = 0; i < temp_vib; i++) {
                    vibrato_speed[num] <<= 1;
                }

                break;

            case 5:
                /* Tone C Volume, Noise Modulation Control */
                vol_C[num] = data & 0x0f;
                mux[num] = (data >> 4) & 0x01;
                noise_am[num] = (data >> 5) & 0x01;
                break;

            case 6:
                /* Tone A & B Volume */
                vol_B[num] = (data >> 4) & 0x0f;
                vol_A[num] = data & 0x0f;
                break;

            case 7:
                /* Noise Volume Register */
                vol_noise8[num] = data;
                vol_noise4[num] = (data >> 4) & 0x0f;
                break;

            case 8:
                /* Sound Block Transfer */

                bvalue = (cpu_get_reg(Z80_BC) >> 8) & 0x07;

                astrocade_sound_w(num, bvalue, data);

                break;
        }
    }

    public static WriteHandlerPtr astrocade_sound1_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            astrocade_sound_w(0, offset, data);
        }
    };

    public static WriteHandlerPtr astrocade_sound2_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            astrocade_sound_w(1, offset, data);
        }
    };

    @Override
    public void update() {
        int num;

        if (Machine.sample_rate == 0) {
            return;
        }

        for (num = 0; num < intf.num; num++) {
            astrocade_update(num, buffer_len);
            /* reset position , step , count */
            sample_pos[num] = 0;
            /* play sound */
            mixer_play_streamed_sample_16(channel + num, astrocade_buffer[num], 2 * buffer_len, emulation_rate);
        }
    }

    @Override
    public void reset() {
        //no functionality
    }
}
