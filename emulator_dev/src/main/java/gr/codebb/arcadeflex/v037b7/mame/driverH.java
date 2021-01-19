/** ported to v0.37b7 */
package gr.codebb.arcadeflex.v037b7.mame;

import static gr.codebb.arcadeflex.old.arcadeflex.libc_old.CopyArray;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.GfxDecodeInfo;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.rectangle;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.InputPortTiny;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.input_macro;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.MachineSound;

public class driverH {

    public static class MachineCPU {

        public MachineCPU(int ct, int cc, MemoryReadAddress[] mr, MemoryWriteAddress[] mw, IOReadPort[] pr, IOWritePort[] pw, InterruptPtr vb, int vbf, InterruptPtr ti, int tif, Object reset) {
            cpu_type = ct;
            cpu_clock = cc;
            memory_read = mr;
            memory_write = mw;
            port_read = pr;
            port_write = pw;
            vblank_interrupt = vb;
            vblank_interrupts_per_frame = vbf;
            timed_interrupt = ti;
            timed_interrupts_per_second = tif;
            reset_param = reset;
        }

        public MachineCPU(int ct, int cc, MemoryReadAddress[] mr, MemoryWriteAddress[] mw, IOReadPort[] pr, IOWritePort[] pw, InterruptPtr vb, int vbf, InterruptPtr ti, int tif) {
            this(ct, cc, mr, mw, pr, pw, vb, vbf, ti, tif, null);
        }

        public MachineCPU(int ct, int cc, MemoryReadAddress[] mr, MemoryWriteAddress[] mw, IOReadPort[] pr, IOWritePort[] pw, InterruptPtr vb, int vbf) {
            this(ct, cc, mr, mw, pr, pw, vb, vbf, null, 0, null);
        }

        public MachineCPU() {
            this(0, 0, null, null, null, null, null, 0, null, 0, null);
        }

        public static MachineCPU[] create(int n) {
            MachineCPU[] a = new MachineCPU[n];
            for (int k = 0; k < n; k++) {
                a[k] = new MachineCPU();
            }
            return a;
        }

        public int cpu_type;/* see #defines below. */
        public int cpu_clock;/* in Hertz */
        public MemoryReadAddress[] memory_read;
        public MemoryWriteAddress[] memory_write;
        public IOReadPort[] port_read;
        public IOWritePort[] port_write;
        public InterruptPtr vblank_interrupt;
        public int vblank_interrupts_per_frame;/* usually 1 */
 /* use this for interrupts which are not tied to vblank 	*/
 /* usually frequency in Hz, but if you need 				*/
 /* greater precision you can give the period in nanoseconds */
        public InterruptPtr timed_interrupt;
        public int timed_interrupts_per_second;
        /* pointer to a parameter to pass to the CPU cores reset function */
        public Object reset_param;

    }

    public static final int CPU_DUMMY = 0;
    public static final int CPU_Z80 = 1;
    public static final int CPU_8080 = 2;
    public static final int CPU_8085A = 3;
    public static final int CPU_M6502 = 4;
    public static final int CPU_M65C02 = 5;
    public static final int CPU_M6510 = 6;
    public static final int CPU_N2A03 = 7;
    public static final int CPU_M4510 = 8;
    public static final int CPU_H6280 = 9;
    public static final int CPU_I86 = 10;
    public static final int CPU_I186 = 11;
    public static final int CPU_V20 = 12;
    public static final int CPU_V30 = 13;
    public static final int CPU_V33 = 14;
    public static final int CPU_I8035 = 15;/* same as CPU_I8039 */
    public static final int CPU_I8039 = 16;
    public static final int CPU_I8048 = 17;/* same as CPU_I8039 */
    public static final int CPU_N7751 = 18;/* same as CPU_I8039 */
    public static final int CPU_M6800 = 19;/* same as CPU_M6802/CPU_M6808 */
    public static final int CPU_M6801 = 20;/* same as CPU_M6803 */
    public static final int CPU_M6802 = 21;/* same as CPU_M6800/CPU_M6808 */
    public static final int CPU_M6803 = 22;/* same as CPU_M6801 */
    public static final int CPU_M6808 = 23;/* same as CPU_M6800/CPU_M6802 */
    public static final int CPU_HD63701 = 24;/* 6808 with some additional opcodes */
    public static final int CPU_NSC8105 = 25;/* same(?) as CPU_M6802(?) with scrambled opcodes. There is at least one new opcode. */
    public static final int CPU_M6805 = 26;
    public static final int CPU_M68705 = 27;/* same as CPU_M6805 */
    public static final int CPU_HD63705 = 28;/* M6805 family but larger address space, different stack size */
    public static final int CPU_HD6309 = 29;/* same as CPU_M6809 (actually it's not 100% compatible) */
    public static final int CPU_M6809 = 30;
    public static final int CPU_KONAMI = 31;
    public static final int CPU_M68000 = 32;
    public static final int CPU_M68010 = 33;
    public static final int CPU_M68EC020 = 34;
    public static final int CPU_M68020 = 35;
    public static final int CPU_T11 = 36;
    public static final int CPU_S2650 = 37;
    public static final int CPU_TMS34010 = 38;
    public static final int CPU_TMS9980 = 39;
    public static final int CPU_Z8000 = 40;
    public static final int CPU_TMS320C10 = 41;
    public static final int CPU_CCPU = 42;
    public static final int CPU_ADSP2100 = 43;
    public static final int CPU_ADSP2105 = 44;
    public static final int CPU_PSX = 45;

    public static final int CPU_COUNT = 46;

    /* set this if the CPU is used as a slave for audio. It will not be emulated if */
 /* sound is disabled, therefore speeding up a lot the emulation. */
    public static final int CPU_AUDIO_CPU = 0x8000;

    /* the Z80 can be wired to use 16 bit addressing for I/O ports */
    public static final int CPU_16BIT_PORT = 0x4000;

    public static final int CPU_FLAGS_MASK = 0xff00;

    public static final int MAX_CPU = 8;/* MAX_CPU is the maximum number of CPUs which cpuintrf.c  can run at the same time. Currently, 8 is enough. */


    public static final int MAX_SOUND = 5;/* MAX_SOUND is the maximum number of sound subsystems which can run at the same time. Currently, 5 is enough. */


    public static class MachineDriver {

        public MachineDriver() {
        } //null implementation

        public MachineDriver(MachineCPU[] mcp, float fps, int vblank, int cpu_slices, InitMachinePtr im, int sw, int sh, rectangle va, GfxDecodeInfo[] gdi, int tc, int ctl, VhConvertColorPromPtr vccp, int vattr, VhEofCallbackPtr veof, VhStartPtr vsta, VhStopPtr vsto, VhUpdatePtr vup, int sattr, int obs1, int obs2, int obs3, MachineSound[] snd) {
            CopyArray(cpu, mcp);
            frames_per_second = fps;
            vblank_duration = vblank;
            cpu_slices_per_frame = cpu_slices;
            init_machine = im;
            screen_width = sw;
            screen_height = sh;
            default_visible_area = va;
            gfxdecodeinfo = gdi;
            total_colors = tc;
            color_table_len = ctl;
            vh_init_palette = vccp;
            video_attributes = vattr;
            vh_eof_callback = veof;
            vh_start = vsta;
            vh_stop = vsto;
            vh_update = vup;
            sound_attributes = sattr;
            obsolete1 = obs1;
            obsolete2 = obs2;
            obsolete3 = obs3;
            CopyArray(sound, snd);
            nvram_handler = null;
        }

        //same as previous but with nvram_handler
        public MachineDriver(MachineCPU[] mcp, float fps, int vblank, int cpu_slices, InitMachinePtr im, int sw, int sh, rectangle va, GfxDecodeInfo[] gdi, int tc, int ctl, VhConvertColorPromPtr vccp, int vattr, VhEofCallbackPtr veof, VhStartPtr vsta, VhStopPtr vsto, VhUpdatePtr vup, int sattr, int obs1, int obs2, int obs3, MachineSound[] snd, nvramPtr nvr) {
            this(mcp, fps, vblank, cpu_slices, im, sw, sh, va, gdi, tc, ctl, vccp, vattr, veof, vsta, vsto, vup, sattr, obs1, obs2, obs3, snd);
            nvram_handler = nvr;
        }

        /* basic machine hardware */
        public MachineCPU cpu[] = MachineCPU.create(MAX_CPU);
        public float frames_per_second;
        public int vblank_duration;/* in microseconds - see description below */
        public int cpu_slices_per_frame;/* for multicpu games. 1 is the minimum, meaning */
 /* that each CPU runs for the whole video frame */
 /* before giving control to the others. The higher */
 /* this setting, the more closely CPUs are interleaved */
 /* and therefore the more accurate the emulation is. */
 /* However, an higher setting also means slower */
 /* performance. */
        public InitMachinePtr init_machine;

        /* video hardware */
        public int screen_width, screen_height;
        public rectangle default_visible_area;/* the visible area can be changed at */
 /* run time, but it should never be larger than the */
 /* one specified here, in order not to force the */
 /* OS dependant code to resize the display window. */

        public GfxDecodeInfo[] gfxdecodeinfo;
        public int total_colors;/* palette is 3*total_colors bytes long */
        public int color_table_len;
        /* length in bytes of the color lookup table */
        public VhConvertColorPromPtr vh_init_palette;
        public int video_attributes;

        public VhEofCallbackPtr vh_eof_callback;/* called every frame after osd_update_video_and_audio() */
 /* This is useful when there are operations that need */
 /* to be performed every frame regardless of frameskip, */
 /* e.g. sprite buffering or collision detection. */
        public VhStartPtr vh_start;
        public VhStopPtr vh_stop;
        public VhUpdatePtr vh_update;

        /* sound hardware */
        public int sound_attributes;
        public int obsolete1;
        public int obsolete2;
        public int obsolete3;
        public MachineSound sound[] = MachineSound.create(MAX_SOUND);

        /*
           use this to manage nvram/eeprom/cmos/etc.
           It is called before the emulation starts and after it ends. Note that it is
           NOT called when the game is reset, since it is not needed.
           file == 0, read_or_write == 0 -> first time the game is run, initialize nvram
           file != 0, read_or_write == 0 -> load nvram from disk
           file == 0, read_or_write != 0 -> not allowed
           file != 0, read_or_write != 0 -> save nvram to disk
         */
        public nvramPtr nvram_handler;
    }

    /* VBlank is the period when the video beam is outside of the visible area and */
 /* returns from the bottom to the top of the screen to prepare for a new video frame. */
 /* VBlank duration is an important factor in how the game renders itself. MAME */
 /* generates the vblank_interrupt, lets the game run for vblank_duration microseconds, */
 /* and then updates the screen. This faithfully reproduces the behaviour of the real */
 /* hardware. In many cases, the game does video related operations both in its vblank */
 /* interrupt, and in the normal game code; it is therefore important to set up */
 /* vblank_duration accurately to have everything properly in sync. An example of this */
 /* is Commando: if you set vblank_duration to 0, therefore redrawing the screen BEFORE */
 /* the vblank interrupt is executed, sprites will be misaligned when the screen scrolls. */

 /* Here are some predefined, TOTALLY ARBITRARY values for vblank_duration, which should */
 /* be OK for most cases. I have NO IDEA how accurate they are compared to the real */
 /* hardware, they could be completely wrong. */
    public static final int DEFAULT_60HZ_VBLANK_DURATION = 0;
    public static final int DEFAULT_30HZ_VBLANK_DURATION = 0;

    /* If you use IPT_VBLANK, you need a duration different from 0. */
    public static final int DEFAULT_REAL_60HZ_VBLANK_DURATION = 2500;
    public static final int DEFAULT_REAL_30HZ_VBLANK_DURATION = 2500;

    /* flags for video_attributes */

 /* bit 1 of the video attributes indicates whether or not dirty rectangles will work */
    public static final int VIDEO_SUPPORTS_DIRTY = 0x0002;

    /* bit 0 of the video attributes indicates raster or vector video hardware */
    public static final int VIDEO_TYPE_RASTER = 0x0000;
    public static final int VIDEO_TYPE_VECTOR = 0x0001;

    /* bit 2 of the video attributes indicates whether or not the driver modifies the palette */
    public static final int VIDEO_MODIFIES_PALETTE = 0x0004;

    /* bit 3 of the video attributes indicates that the game's palette has 6 or more bits */
 /*       per gun, and would therefore require a 24-bit display. This is entirely up to */
 /*       the OS dpeendant layer, the bitmap will still be 16-bit. */
    public static final int VIDEO_NEEDS_6BITS_PER_GUN = 0x0008;

    /* ASG 980417 - added: */
 /* bit 4 of the video attributes indicates that the driver wants its refresh after */
 /*       the VBLANK instead of before. */
    public static final int VIDEO_UPDATE_BEFORE_VBLANK = 0x0000;
    public static final int VIDEO_UPDATE_AFTER_VBLANK = 0x0010;

    /* In most cases we assume pixels are square (1:1 aspect ratio) but some games need */
 /* different proportions, e.g. 1:2 for Blasteroids */
    public static final int VIDEO_PIXEL_ASPECT_RATIO_MASK = 0x0060;
    public static final int VIDEO_PIXEL_ASPECT_RATIO_1_1 = 0x0000;
    public static final int VIDEO_PIXEL_ASPECT_RATIO_1_2 = 0x0020;
    public static final int VIDEO_PIXEL_ASPECT_RATIO_2_1 = 0x0040;

    public static final int VIDEO_DUAL_MONITOR = 0x0080;


    /* Mish 181099:  See comments in vidhrdw/generic.c for details */
    public static final int VIDEO_BUFFERS_SPRITERAM = 0x0100;

    /* flags for sound_attributes */
    public static final int SOUND_SUPPORTS_STEREO = 0x0001;

    public static class GameDriver {

        //this is used instead of GAME macro
        public GameDriver(String year, String name, String source, RomLoadPtr romload, GameDriver parent, MachineDriver drv, InputPortPtr input, InitDriverPtr init, int monitor, String manufacture, String fullname) {
            this.year = year;
            this.source_file = source;
            this.clone_of = parent;
            this.name = name;
            this.description = fullname;
            this.manufacturer = manufacture;
            this.drv = drv;
            //inputports
            this.driver_init = init;
            romload.handler();//load the rom
            input.handler();//load input
            this.input_ports = input_macro;//copy input macro to input ports
            this.rom = rommodule_macro; //copy rommodule_macro to rom
            this.flags = monitor;
        }

        //GAMEX macro
        public GameDriver(String year, String name, String source, RomLoadPtr romload, GameDriver parent, MachineDriver drv, InputPortPtr input, InitDriverPtr init, int monitor, String manufacture, String fullname, int flags) {
            this.year = year;
            this.source_file = source;
            this.clone_of = parent;
            this.name = name;
            this.description = fullname;
            this.manufacturer = manufacture;
            this.drv = drv;
            //inputports
            this.driver_init = init;
            romload.handler();//load the rom
            input.handler();//load input
            this.input_ports = input_macro;//copy input macro to input ports
            this.rom = rommodule_macro; //copy rommodule_macro to rom
            this.flags = monitor | flags;
        }

        public String source_file;
        public GameDriver clone_of;
        /* if this is a clone, point to */
 /* the main version of the game */
        public String name;
        public String description;
        public String year;
        public String manufacturer;
        public MachineDriver drv;
        public InputPortTiny[] input_ports;
        public InitDriverPtr driver_init;/* optional function to be called during initialization */
 /* This is called ONCE, unlike Machine->init_machine */
 /* which is called every time the game is reset. */

        public RomModule[] rom;

        public int flags;/* orientation and other flags; see defines below */

    }
    /* values for the flags field */

    public static final int ORIENTATION_MASK = 0x0007;
    public static final int ORIENTATION_FLIP_X = 0x0001;/* mirror everything in the X direction */
    public static final int ORIENTATION_FLIP_Y = 0x0002;/* mirror everything in the Y direction */
    public static final int ORIENTATION_SWAP_XY = 0x0004;/* mirror along the top-left/bottom-right diagonal */

    public static final int GAME_NOT_WORKING = 0x0008;
    public static final int GAME_WRONG_COLORS = 0x0010;/* colors are totally wrong */
    public static final int GAME_IMPERFECT_COLORS = 0x0020;/* colors are not 100% accurate, but close */
    public static final int GAME_NO_SOUND = 0x0040;/* sound is missing */
    public static final int GAME_IMPERFECT_SOUND = 0x0080;/* sound is known to be wrong */
    public static final int GAME_REQUIRES_16BIT = 0x0100;/* cannot fit in 256 colors */
    public static final int GAME_NO_COCKTAIL = 0x0200;/* screen flip support is missing */
    public static final int GAME_UNEMULATED_PROTECTION = 0x0400;/* game's protection not fully emulated */
    public static final int NOT_A_DRIVER = 0x4000;/* set by the fake "root" driver_ and by "containers" e.g. driver_neogeo. */

 /* monitor parameters to be used with the GAME() macro */
    public static final int ROT0 = 0x0000;
    public static final int ROT90 = (ORIENTATION_SWAP_XY | ORIENTATION_FLIP_X);/* rotate clockwise 90 degrees */
    public static final int ROT180 = (ORIENTATION_FLIP_X | ORIENTATION_FLIP_Y);/* rotate 180 degrees */
    public static final int ROT270 = (ORIENTATION_SWAP_XY | ORIENTATION_FLIP_Y);/* rotate counter-clockwise 90 degrees */
    public static final int ROT0_16BIT = (ROT0 | GAME_REQUIRES_16BIT);
    public static final int ROT90_16BIT = (ROT90 | GAME_REQUIRES_16BIT);
    public static final int ROT180_16BIT = (ROT180 | GAME_REQUIRES_16BIT);
    public static final int ROT270_16BIT = (ROT270 | GAME_REQUIRES_16BIT);
}
