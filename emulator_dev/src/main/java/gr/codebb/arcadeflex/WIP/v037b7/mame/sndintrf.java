/*
* Ported to 0.37b7 
*/
package gr.codebb.arcadeflex.WIP.v037b7.mame;

import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.old.mame.common.freesamples;
import static gr.codebb.arcadeflex.old.sound.mixer.*;
import static gr.codebb.arcadeflex.old.sound.streams.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.ReadHandlerPtr;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.WriteHandlerPtr;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.*;
import static gr.codebb.arcadeflex.v056.mame.timer.*;
import static gr.codebb.arcadeflex.v056.mame.timerH.*;

import gr.codebb.arcadeflex.WIP.v037b7.sound._2151intf;
import gr.codebb.arcadeflex.WIP.v037b7.sound.astrocde;
import gr.codebb.arcadeflex.v037b7.sound.CustomSound;
import gr.codebb.arcadeflex.v037b7.sound.Dummy_snd;
import gr.codebb.arcadeflex.WIP.v037b7.sound.MSM5205;
import gr.codebb.arcadeflex.WIP.v037b7.sound._2203intf;
import gr.codebb.arcadeflex.WIP.v037b7.sound._2608intf;
import gr.codebb.arcadeflex.WIP.v037b7.sound._2610intf;
import gr.codebb.arcadeflex.WIP.v037b7.sound._3526intf;
import gr.codebb.arcadeflex.WIP.v037b7.sound._3812intf;
import gr.codebb.arcadeflex.v037b7.sound._5110intf;
import gr.codebb.arcadeflex.WIP.v037b7.sound.adpcm;
import gr.codebb.arcadeflex.WIP.v037b7.sound.ay8910;
import gr.codebb.arcadeflex.WIP.v037b7.sound.dac;
import gr.codebb.arcadeflex.WIP.v037b7.sound.namco;
import gr.codebb.arcadeflex.WIP.v037b7.sound.okim6295;
import gr.codebb.arcadeflex.WIP.v037b7.sound.samples;
import gr.codebb.arcadeflex.WIP.v037b7.sound.sn76477;
import gr.codebb.arcadeflex.WIP.v037b7.sound.y8950intf;
import gr.codebb.arcadeflex.WIP.v037b7.sound.ym2413;
import gr.codebb.arcadeflex.v058.sound.sn76496;
import gr.codebb.arcadeflex.v058.sound.tms36xx;
import gr.codebb.arcadeflex.WIP.v037b7.sound._5220intf;
import gr.codebb.arcadeflex.WIP.v037b7.sound.pokey;
import gr.codebb.arcadeflex.v058.sound.vlm5030;

public class sndintrf {

    /**
     * *************************************************************************
     *
     * Many games use a master-slave CPU setup. Typically, the main CPU writes a
     * command to some register, and then writes to another register to trigger
     * an interrupt on the slave CPU (the interrupt might also be triggered by
     * the first write). The slave CPU, notified by the interrupt, goes and
     * reads the command.
     *
     **************************************************************************
     */
    static int cleared_value = 0x00;

    static int latch, read_debug;

    public static timer_callback soundlatch_callback = new timer_callback() {
        public void handler(int param) {
            if (read_debug == 0 && latch != param) {
                logerror("Warning: sound latch written before being read. Previous: %02x, new: %02x\n", latch, param);
            }
            latch = param;
            read_debug = 0;
        }
    };
    public static WriteHandlerPtr soundlatch_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* make all the CPUs synchronize, and only AFTER that write the new command to the latch */
            timer_set(TIME_NOW, data, soundlatch_callback);
        }
    };

    public static ReadHandlerPtr soundlatch_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            read_debug = 1;
            return latch;
        }
    };

    public static WriteHandlerPtr soundlatch_clear_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            latch = cleared_value;
        }
    };

    static int latch2, read_debug2;

    public static timer_callback soundlatch2_callback = new timer_callback() {
        public void handler(int param) {
            if (read_debug2 == 0 && latch2 != param) {
                logerror("Warning: sound latch 2 written before being read. Previous: %02x, new: %02x\n", latch2, param);
            }
            latch2 = param;
            read_debug2 = 0;
        }
    };

    public static WriteHandlerPtr soundlatch2_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* make all the CPUs synchronize, and only AFTER that write the new command to the latch */
            timer_set(TIME_NOW, data, soundlatch2_callback);
        }
    };

    public static ReadHandlerPtr soundlatch2_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            read_debug2 = 1;
            return latch2;
        }
    };

    public static WriteHandlerPtr soundlatch2_clear_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            latch2 = cleared_value;
        }
    };

    static int latch3, read_debug3;

    public static timer_callback soundlatch3_callback = new timer_callback() {
        public void handler(int param) {
            if (read_debug3 == 0 && latch3 != param) {
                logerror("Warning: sound latch 3 written before being read. Previous: %02x, new: %02x\n", latch3, param);
            }
            latch3 = param;
            read_debug3 = 0;
        }
    };

    public static WriteHandlerPtr soundlatch3_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* make all the CPUs synchronize, and only AFTER that write the new command to the latch */
            timer_set(TIME_NOW, data, soundlatch3_callback);
        }
    };

    public static ReadHandlerPtr soundlatch3_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            read_debug3 = 1;
            return latch3;
        }
    };

    public static WriteHandlerPtr soundlatch3_clear_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            latch3 = cleared_value;
        }
    };

    static int latch4, read_debug4;

    public static timer_callback soundlatch4_callback = new timer_callback() {
        public void handler(int param) {
            if (read_debug4 == 0 && latch4 != param) {
                logerror("Warning: sound latch 4 written before being read. Previous: %02x, new: %02x\n", latch2, param);
            }
            latch4 = param;
            read_debug4 = 0;
        }
    };

    public static WriteHandlerPtr soundlatch4_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* make all the CPUs synchronize, and only AFTER that write the new command to the latch */
            timer_set(TIME_NOW, data, soundlatch4_callback);
        }
    };

    public static ReadHandlerPtr soundlatch4_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            read_debug4 = 1;
            return latch4;
        }
    };

    public static WriteHandlerPtr soundlatch4_clear_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            latch4 = cleared_value;
        }
    };

    void soundlatch_setclearedvalue(int value) {
        cleared_value = value;
    }

    /**
     * *************************************************************************
     *
     *
     *
     **************************************************************************
     */
    static timer_entry sound_update_timer;
    static double refresh_period;
    static double refresh_period_inv;

    public static abstract class snd_interface {

        public int sound_num;/* ID */
        public String name;/* description */
        public abstract int chips_num(MachineSound msound);/* returns number of chips if applicable */
        public abstract int chips_clock(MachineSound msound);/* returns chips clock if applicable */
        public abstract int start(MachineSound msound);/* starts sound emulation */
        public abstract void stop();/* stops sound emulation */
        public abstract void update();/* updates emulation once per frame if necessary */
        public abstract void reset();/* resets sound emulation */
    }

    /*TODO*/
    // #if (HAS_HC55516)
    /*TODO*/
    // int HC55516_num(const struct MachineSound *msound) { return ((struct
    // hc55516_interface*)msound->sound_interface)->num; }
    /*TODO*/
    // #endif
    /*TODO*/
    // #if (HAS_K007232)
    /*TODO*/
    // int K007232_num(const struct MachineSound *msound) { return ((struct
    // K007232_interface*)msound->sound_interface)->num_chips; }
    /*TODO*/
    // #endif
    /*TODO*/
    // #if (HAS_YM2612 || HAS_YM3438)
    /*TODO*/
    // int YM2612_clock(const struct MachineSound *msound) { return ((struct
    // YM2612interface*)msound->sound_interface)->baseclock; }
    /*TODO*/
    // int YM2612_num(const struct MachineSound *msound) { return ((struct
    // YM2612interface*)msound->sound_interface)->num; }
    /*TODO*/
    // #endif
    /*TODO*/
    // #if (HAS_POKEY)
    /*TODO*/
    // int POKEY_clock(const struct MachineSound *msound) { return ((struct
    // POKEYinterface*)msound->sound_interface)->baseclock; }
    /*TODO*/
    // int POKEY_num(const struct MachineSound *msound) { return ((struct
    // POKEYinterface*)msound->sound_interface)->num; }
    /*TODO*/
    // #endif
    /*TODO*/
 /*TODO*/
    // #if (HAS_YMZ280B)
    /*TODO*/
    // int YMZ280B_clock(const struct MachineSound *msound) { return ((struct
    // YMZ280Binterface*)msound->sound_interface)->baseclock[0]; }
    /*TODO*/
    // int YMZ280B_num(const struct MachineSound *msound) { return ((struct
    // YMZ280Binterface*)msound->sound_interface)->num; }
    /*TODO*/
    // #endif
    /*TODO*/
    /*TODO*/
    // #if (HAS_TMS5220)
    /*TODO*/
    // int TMS5220_clock(const struct MachineSound *msound) { return ((struct
    // TMS5220interface*)msound->sound_interface)->baseclock; }
    /*TODO*/
    // #endif
    /*TODO*/
    // #if (HAS_YM2151 || HAS_YM2151_ALT)
    /*TODO*/
    // int YM2151_clock(const struct MachineSound *msound) { return ((struct
    // YM2151interface*)msound->sound_interface)->baseclock; }
    /*TODO*/
    // int YM2151_num(const struct MachineSound *msound) { return ((struct
    // YM2151interface*)msound->sound_interface)->num; }
    /*TODO*/
    // #endif
    /*TODO*/
    // #if (HAS_NES)
    /*TODO*/
    // int NES_num(const struct MachineSound *msound) { return ((struct
    // NESinterface*)msound->sound_interface)->num; }
    /*TODO*/
    // #endif
    /*TODO*/
    // #if (HAS_UPD7759)
    /*TODO*/
    // int UPD7759_clock(const struct MachineSound *msound) { return ((struct
    // UPD7759_interface*)msound->sound_interface)->clock_rate; }
    /*TODO*/
    // #endif
    /*TODO*/
    // #if (HAS_K051649)
    /*TODO*/
    // int K051649_clock(const struct MachineSound *msound) { return ((struct
    // k051649_interface*)msound->sound_interface)->master_clock; }
    /*TODO*/
    // #endif
    /*TODO*/
    // #if (HAS_K053260)
    /*TODO*/
    // int K053260_clock(const struct MachineSound *msound) { return ((struct
    // K053260_interface*)msound->sound_interface)->clock; }
    /*TODO*/
    // #endif
    /*TODO*/
    // #if (HAS_CEM3394)
    /*TODO*/
    // int cem3394_num(const struct MachineSound *msound) { return ((struct
    // cem3394_interface*)msound->sound_interface)->numchips; }
    /*TODO*/
    // #endif
    /*TODO*/
    // #if (HAS_QSOUND)
    /*TODO*/
    // int qsound_clock(const struct MachineSound *msound) { return ((struct
    // QSound_interface*)msound->sound_interface)->clock; }
    /*TODO*/
    // #endif
    /*TODO*/
    // #if (HAS_SAA1099)
    /*TODO*/
    // int saa1099_num(const struct MachineSound *msound) { return ((struct
    // SAA1099_interface*)msound->sound_interface)->numchips; }
    /*TODO*/
    // #endif
    /*TODO*/
    // #if (HAS_SPEAKER)
    /*TODO*/
    // int speaker_num(const struct MachineSound *msound) { return ((struct
    // Speaker_interface*)msound->sound_interface)->num; }
    /*TODO*/
    // #endif
    /*TODO*/
    // #if (HAS_WAVE)
    /*TODO*/
    // int wave_num(const struct MachineSound *msound) { return ((struct
    // Wave_interface*)msound->sound_interface)->num; }
    /*TODO*/
    // #endif
    /*TODO*/
    // #if (HAS_BEEP)
    /*TODO*/
    // int beep_num(const struct MachineSound *msound) { return ((struct
    // beep_interface*)msound->sound_interface)->num; }
    /*TODO*/
    // #endif
    /*TODO*/
    //
    public static snd_interface sndintf[] = {
        new Dummy_snd(),
        new CustomSound(),
        new samples(),
        new dac(),
        new ay8910(),
        new _2203intf(),
        new _2151intf(),
        new _2608intf(),
        new _2610intf(),
        new Dummy_snd(),
        /*TODO*/
        // #if (HAS_YM2610B)
        /*TODO*/
        //    {
        /*TODO*/
        //		SOUND_YM2610B,
        /*TODO*/
        //		"YM-2610B",
        /*TODO*/
        //		YM2610_num,
        /*TODO*/
        //		YM2610_clock,
        /*TODO*/
        //		YM2610B_sh_start,
        /*TODO*/
        //		YM2610_sh_stop,
        /*TODO*/
        //		0,
        /*TODO*/
        //		YM2610_sh_reset
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new Dummy_snd(),
        /*TODO*/
        // #if (HAS_YM2612)
        /*TODO*/
        //    {
        /*TODO*/
        //		SOUND_YM2612,
        /*TODO*/
        //		"YM-2612",
        /*TODO*/
        //		YM2612_num,
        /*TODO*/
        //		YM2612_clock,
        /*TODO*/
        //		YM2612_sh_start,
        /*TODO*/
        //		YM2612_sh_stop,
        /*TODO*/
        //		0,
        /*TODO*/
        //		YM2612_sh_reset
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new Dummy_snd(),
        /*TODO*/
        // #if (HAS_YM3438)
        /*TODO*/
        //    {
        /*TODO*/
        //		SOUND_YM3438,
        /*TODO*/
        //		"YM-3438",
        /*TODO*/
        //		YM2612_num,
        /*TODO*/
        //		YM2612_clock,
        /*TODO*/
        //		YM2612_sh_start,
        /*TODO*/
        //		YM2612_sh_stop,
        /*TODO*/
        //		0,
        /*TODO*/
        //		YM2612_sh_reset
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new ym2413(),
        new _3812intf(),
        new _3526intf(),
        new Dummy_snd(),
        /*TODO*/
        // #if (HAS_YMZ280B)
        /*TODO*/
        //    {
        /*TODO*/
        //		SOUND_YMZ280B,
        /*TODO*/
        //		"YMZ280B",
        /*TODO*/
        //		YMZ280B_num,
        /*TODO*/
        //		YMZ280B_clock,
        /*TODO*/
        //		YMZ280B_sh_start,
        /*TODO*/
        //		YMZ280B_sh_stop,
        /*TODO*/
        //		0,
        /*TODO*/
        //		0
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new y8950intf(),
        new sn76477(),
        new sn76496(),
        new pokey(),
        /*TODO*/
        // #if (HAS_POKEY)
        /*TODO*/
        //    {
        /*TODO*/
        //		SOUND_POKEY,
        /*TODO*/
        //		"Pokey",
        /*TODO*/
        //		POKEY_num,
        /*TODO*/
        //		POKEY_clock,
        /*TODO*/
        //		pokey_sh_start,
        /*TODO*/
        //		pokey_sh_stop,
        /*TODO*/
        //		0,
        /*TODO*/
        //		0
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new Dummy_snd(),
        /*TODO*/
        // #if (HAS_NES)
        /*TODO*/
        //    {
        /*TODO*/
        //		SOUND_NES,
        /*TODO*/
        //		"Nintendo",
        /*TODO*/
        //		NES_num,
        /*TODO*/
        //		0,
        /*TODO*/
        //		NESPSG_sh_start,
        /*TODO*/
        //		NESPSG_sh_stop,
        /*TODO*/
        //		NESPSG_sh_update,
        /*TODO*/
        //		0
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new astrocde(),
        new namco(),
        new tms36xx(),
        new _5110intf(),
        new _5220intf(),
        new vlm5030(),
        new adpcm(),
        new okim6295(),
        new MSM5205(),
        new Dummy_snd(),
        /*TODO*/
        // #if (HAS_UPD7759)
        /*TODO*/
        //    {
        /*TODO*/
        //		SOUND_UPD7759,
        /*TODO*/
        //		"uPD7759",
        /*TODO*/
        //		0,
        /*TODO*/
        //		UPD7759_clock,
        /*TODO*/
        //		UPD7759_sh_start,
        /*TODO*/
        //		UPD7759_sh_stop,
        /*TODO*/
        //		0,
        /*TODO*/
        //		0
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new Dummy_snd(),
        /*TODO*/
        // #if (HAS_HC55516)
        /*TODO*/
        //    {
        /*TODO*/
        //		SOUND_HC55516,
        /*TODO*/
        //		"HC55516",
        /*TODO*/
        //		HC55516_num,
        /*TODO*/
        //		0,
        /*TODO*/
        //		hc55516_sh_start,
        /*TODO*/
        //		0,
        /*TODO*/
        //		0,
        /*TODO*/
        //		0
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new Dummy_snd(),
        /*TODO*/
        // #if (HAS_K005289)
        /*TODO*/
        //    {
        /*TODO*/
        //		SOUND_K005289,
        /*TODO*/
        //		"005289",
        /*TODO*/
        //		0,
        /*TODO*/
        //		0,
        /*TODO*/
        //		K005289_sh_start,
        /*TODO*/
        //		K005289_sh_stop,
        /*TODO*/
        //		0,
        /*TODO*/
        //		0
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new Dummy_snd(),
        /*TODO*/
        // #if (HAS_K007232)
        /*TODO*/
        //    {
        /*TODO*/
        //		SOUND_K007232,
        /*TODO*/
        //		"007232",
        /*TODO*/
        //		K007232_num,
        /*TODO*/
        //		0,
        /*TODO*/
        //		K007232_sh_start,
        /*TODO*/
        //		0,
        /*TODO*/
        //		0,
        /*TODO*/
        //		0
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new Dummy_snd(),
        /*TODO*/
        // #if (HAS_K051649)
        /*TODO*/
        //    {
        /*TODO*/
        //		SOUND_K051649,
        /*TODO*/
        //		"051649",
        /*TODO*/
        //		0,
        /*TODO*/
        //		K051649_clock,
        /*TODO*/
        //		K051649_sh_start,
        /*TODO*/
        //		K051649_sh_stop,
        /*TODO*/
        //		0,
        /*TODO*/
        //		0
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new Dummy_snd(),
        /*TODO*/
        // #if (HAS_K053260)
        /*TODO*/
        //    {
        /*TODO*/
        //		SOUND_K053260,
        /*TODO*/
        //		"053260",
        /*TODO*/
        //		0,
        /*TODO*/
        //		K053260_clock,
        /*TODO*/
        //		K053260_sh_start,
        /*TODO*/
        //		K053260_sh_stop,
        /*TODO*/
        //		0,
        /*TODO*/
        //		0
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new Dummy_snd(),
        /*TODO*/
        // #if (HAS_SEGAPCM)
        /*TODO*/
        //	{
        /*TODO*/
        //		SOUND_SEGAPCM,
        /*TODO*/
        //		"Sega PCM",
        /*TODO*/
        //		0,
        /*TODO*/
        //		0,
        /*TODO*/
        //		SEGAPCM_sh_start,
        /*TODO*/
        //		SEGAPCM_sh_stop,
        /*TODO*/
        //		SEGAPCM_sh_update,
        /*TODO*/
        //		0
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new Dummy_snd(),
        /*TODO*/
        // #if (HAS_RF5C68)
        /*TODO*/
        //	{
        /*TODO*/
        //		SOUND_RF5C68,
        /*TODO*/
        //		"RF5C68",
        /*TODO*/
        //		0,
        /*TODO*/
        //		0,
        /*TODO*/
        //		RF5C68_sh_start,
        /*TODO*/
        //		RF5C68_sh_stop,
        /*TODO*/
        //		0,
        /*TODO*/
        //		0
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new Dummy_snd(),
        /*TODO*/
        // #if (HAS_CEM3394)
        /*TODO*/
        //	{
        /*TODO*/
        //		SOUND_CEM3394,
        /*TODO*/
        //		"CEM3394",
        /*TODO*/
        //		cem3394_num,
        /*TODO*/
        //		0,
        /*TODO*/
        //		cem3394_sh_start,
        /*TODO*/
        //		cem3394_sh_stop,
        /*TODO*/
        //		0,
        /*TODO*/
        //		0
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new Dummy_snd(),
        /*TODO*/
        // #if (HAS_C140)
        /*TODO*/
        //	{
        /*TODO*/
        //		SOUND_C140,
        /*TODO*/
        //		"C140",
        /*TODO*/
        //		0,
        /*TODO*/
        //		0,
        /*TODO*/
        //		C140_sh_start,
        /*TODO*/
        //		C140_sh_stop,
        /*TODO*/
        //		0,
        /*TODO*/
        //		0
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new Dummy_snd(),
        /*TODO*/
        // #if (HAS_QSOUND)
        /*TODO*/
        //	{
        /*TODO*/
        //		SOUND_QSOUND,
        /*TODO*/
        //		"QSound",
        /*TODO*/
        //		0,
        /*TODO*/
        //		qsound_clock,
        /*TODO*/
        //		qsound_sh_start,
        /*TODO*/
        //		qsound_sh_stop,
        /*TODO*/
        //		0,
        /*TODO*/
        //		0
        /*TODO*/
        //	},
        /*TODO*/
        // #endif
        new Dummy_snd(), /*TODO*/ // #if (HAS_SAA1099)
    /*TODO*/ //	{
    /*TODO*/ //		SOUND_SAA1099,
    /*TODO*/ //		"SAA1099",
    /*TODO*/ //		saa1099_num,
    /*TODO*/ //		0,
    /*TODO*/ //		saa1099_sh_start,
    /*TODO*/ //		saa1099_sh_stop,
    /*TODO*/ //		0,
    /*TODO*/ //		0
    /*TODO*/ //	},
    /*TODO*/ // #endiF
    };

    public static int sound_start() {
        int totalsound = 0;
        int i;
        /* samples will be read later if needed */
        Machine.samples = null;

        refresh_period = TIME_IN_HZ(Machine.drv.frames_per_second);
        refresh_period_inv = 1.0 / refresh_period;
        sound_update_timer = timer_set(TIME_NEVER, 0, null);
        if (mixer_sh_start() != 0) {
            return 1;
        }

        if (streams_sh_start() != 0) {
            return 1;
        }

        while (totalsound < MAX_SOUND && Machine.drv.sound[totalsound].sound_type != 0) {
            if ((sndintf[Machine.drv.sound[totalsound].sound_type].start(Machine.drv.sound[totalsound]))
                    != 0) {
                return 1; // goto getout;
            }
            totalsound++;
        }
        return 0;
    }

    public static void sound_stop() {
        int totalsound = 0;

        while (totalsound < MAX_SOUND && Machine.drv.sound[totalsound].sound_type != 0) {
            sndintf[Machine.drv.sound[totalsound].sound_type].stop();
            totalsound++;
        }

        streams_sh_stop();
        mixer_sh_stop();

        if (sound_update_timer != null) {
            timer_remove(sound_update_timer);
            sound_update_timer = null;
        }

        /* free audio samples */
        freesamples(Machine.samples);
        Machine.samples = null;
    }

    public static void sound_update() {
        int totalsound = 0;

        while (totalsound < MAX_SOUND && Machine.drv.sound[totalsound].sound_type != 0) {
            sndintf[Machine.drv.sound[totalsound].sound_type].update();
            totalsound++;
        }

        streams_sh_update();
        mixer_sh_update();

        timer_reset(sound_update_timer, TIME_NEVER);
    }

    public static void sound_reset() {
        int totalsound = 0;

        while (totalsound < MAX_SOUND && Machine.drv.sound[totalsound].sound_type != 0) {
            sndintf[Machine.drv.sound[totalsound].sound_type].reset();
            totalsound++;
        }
    }

    public static String sound_name(MachineSound msound) {
        if (msound.sound_type < SOUND_COUNT) {
            return sndintf[msound.sound_type].name;
        } else {
            return "";
        }
    }

    public static int sound_num(MachineSound msound) {
        if (msound.sound_type < SOUND_COUNT && sndintf[msound.sound_type].chips_num(msound) != 0) {
            return sndintf[msound.sound_type].chips_num(msound);
        } else {
            return 0;
        }
    }

    public static int sound_clock(MachineSound msound) {
        if (msound.sound_type < SOUND_COUNT && sndintf[msound.sound_type].chips_clock(msound) != 0) {
            return sndintf[msound.sound_type].chips_clock(msound);
        } else {
            return 0;
        }
    }

    public static int sound_scalebufferpos(int value) {
        int result = (int) ((double) value * timer_timeelapsed(sound_update_timer) * refresh_period_inv);
        if (value >= 0) {
            return (result < value) ? result : value;
        } else {
            return (result > value) ? result : value;
        }
    }
}
