/*
 *
 *Ported to 0.37b7
 */
package gr.codebb.arcadeflex.v037b7.mame;

import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;

public class sndintrfH {

    public static class MachineSound {

        public MachineSound(int sound_type, Object sound_interface) {
            this.sound_type = sound_type;
            this.sound_interface = sound_interface;
        }

        public MachineSound() {
            this(0, null);
        }

        public static MachineSound[] create(int n) {
            MachineSound[] a = new MachineSound[n];
            for (int k = 0; k < n; k++) {
                a[k] = new MachineSound();
            }
            return a;
        }

        public int sound_type;
        public Object sound_interface;
    }

    public static final int SOUND_DUMMY = 0;
    public static final int SOUND_CUSTOM = 1;
    public static final int SOUND_SAMPLES = 2;
    public static final int SOUND_DAC = 3;
    public static final int SOUND_AY8910 = 4;
    public static final int SOUND_YM2203 = 5;
    public static final int SOUND_YM2151 = 6;
    public static final int SOUND_YM2608 = 7;
    public static final int SOUND_YM2610 = 8;
    public static final int SOUND_YM2610B = 9;
    public static final int SOUND_YM2612 = 10;
    public static final int SOUND_YM3438 = 11;
    /* same as YM2612 */
    public static final int SOUND_YM2413 = 12;
    /* YM3812 with predefined instruments */
    public static final int SOUND_YM3812 = 13;
    public static final int SOUND_YM3526 = 14;
    /*100% YM3812 compatible, less features */
    public static final int SOUND_YMZ280B = 15;
    public static final int SOUND_Y8950 = 16;
    /* YM3526 compatible with delta-T ADPCM */
    public static final int SOUND_SN76477 = 17;
    public static final int SOUND_SN76496 = 18;
    public static final int SOUND_POKEY = 19;
    public static final int SOUND_NES = 20;
    public static final int SOUND_ASTROCADE = 21;
    /* Custom I/O chip from Bally/Midway */
    public static final int SOUND_NAMCO = 22;
    public static final int SOUND_TMS36XX = 23;
    /* currently TMS3615 and TMS3617 */
    public static final int SOUND_TMS5110 = 24;
    public static final int SOUND_TMS5220 = 25;
    public static final int SOUND_VLM5030 = 26;
    public static final int SOUND_ADPCM = 27;
    public static final int SOUND_OKIM6295 = 28;
    /* ROM-based ADPCM system */
    public static final int SOUND_MSM5205 = 29;
    /* CPU-based ADPCM system */
    public static final int SOUND_UPD7759 = 30;
    /* ROM-based ADPCM system */
    public static final int SOUND_HC55516 = 31;
    /* Harris family of CVSD CODECs */
    public static final int SOUND_K005289 = 32;
    /* Konami 005289 */
    public static final int SOUND_K007232 = 33;
    /* Konami 007232 */
    public static final int SOUND_K051649 = 34;
    /* Konami 051649 */
    public static final int SOUND_K053260 = 35;
    /* Konami 053260 */
    public static final int SOUND_SEGAPCM = 36;
    public static final int SOUND_RF5C68 = 37;
    public static final int SOUND_CEM3394 = 38;
    public static final int SOUND_C140 = 39;
    public static final int SOUND_QSOUND = 40;
    public static final int SOUND_SAA1099 = 41;
    public static final int SOUND_COUNT = 42;

    /* structure for SOUND_CUSTOM sound drivers */
    public static class CustomSound_interface {

        public CustomSound_interface(ShStartPtr sh_start, ShStopPtr sh_stop, ShUpdatePtr sh_update) {
            this.sh_start = sh_start;
            this.sh_stop = sh_stop;
            this.sh_update = sh_update;
        }

        public ShStartPtr sh_start;
        public ShStopPtr sh_stop;
        public ShUpdatePtr sh_update;
    }
}
