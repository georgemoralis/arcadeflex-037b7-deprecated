/*
 * ported to 0.37b7 
 */
package gr.codebb.arcadeflex.WIP.v037b7.mame;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.SubArrays.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.artworkC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cheat.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.driver.drivers;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.gfxobjC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.OSD_FILETYPE_NVRAM;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.spriteC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.ui_text.*;
import static gr.codebb.arcadeflex.old.arcadeflex.fileio.osd_fclose;
import static gr.codebb.arcadeflex.old.arcadeflex.fileio.osd_fopen;
import static gr.codebb.arcadeflex.old.arcadeflex.libc_old.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.osd_exit;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.osd_init;
import static gr.codebb.arcadeflex.old.arcadeflex.video.osd_clearbitmap;
import static gr.codebb.arcadeflex.old.arcadeflex.video.osd_create_display;
import static gr.codebb.arcadeflex.old.arcadeflex.video.osd_skip_this_frame;
import static gr.codebb.arcadeflex.old.arcadeflex.video_old.*;
import static gr.codebb.arcadeflex.old.mame.common.*;
import static gr.codebb.arcadeflex.old.mame.cpuintrf.cpu_init;
import static gr.codebb.arcadeflex.old.mame.cpuintrf.cpu_run;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.set_pixel_functions;
import static gr.codebb.arcadeflex.old.mame.input.code_close;
import static gr.codebb.arcadeflex.old.mame.input.code_init;
import static gr.codebb.arcadeflex.old.mame.usrintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mameH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGIONFLAG_DISPOSE;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.memory_init;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.memory_shutdown;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.sndintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;

public class mame {

    static RunningMachine machine = new RunningMachine();
    public static RunningMachine Machine = machine;
    static GameDriver gamedrv;
    private static MachineDriver drv;
    static osd_bitmap real_scrbitmap;

    /* Variables to hold the status of various game options */
    public static GameOptions options = new GameOptions();

    static FILE record;
    /* for -record */
    static FILE playback;
    /* for -playback */

    public static int bailing;
    /* set to 1 if the startup is aborted to prevent multiple error messages */

    static int settingsloaded;

    public static int bitmap_dirty;
    /* set by osd_clearbitmap() */

    public static int leds_status;

    public static int run_game(int game) {
        int err;

        /* copy some settings into easier-to-handle variables */
        record = options.record;
        playback = options.playback;

        Machine.gamedrv = gamedrv = drivers[game];
        Machine.drv = drv = gamedrv.drv;

        /* copy configuration */
        if (options.color_depth == 16
                || (options.color_depth != 8 && (Machine.gamedrv.flags & GAME_REQUIRES_16BIT) != 0)) {
            Machine.color_depth = 16;
        } else {
            Machine.color_depth = 8;
        }
        if (options.vector_width == 0) {
            options.vector_width = 640;
        }
        if (options.vector_height == 0) {
            options.vector_height = 480;
        }

        Machine.sample_rate = options.samplerate;

        /* get orientation right */
        Machine.orientation = gamedrv.flags & ORIENTATION_MASK;
        Machine.ui_orientation = ROT0;
        if (options.norotate != 0) {
            Machine.orientation = ROT0;
        }
        if (options.ror != 0) {
            /* if only one of the components is inverted, switch them */
            if ((Machine.orientation & ROT180) == ORIENTATION_FLIP_X
                    || (Machine.orientation & ROT180) == ORIENTATION_FLIP_Y) {
                Machine.orientation ^= ROT180;
            }

            Machine.orientation ^= ROT90;

            /* if only one of the components is inverted, switch them */
            if ((Machine.ui_orientation & ROT180) == ORIENTATION_FLIP_X
                    || (Machine.ui_orientation & ROT180) == ORIENTATION_FLIP_Y) {
                Machine.ui_orientation ^= ROT180;
            }

            Machine.ui_orientation ^= ROT90;
        }

        if (options.rol != 0) {
            /* if only one of the components is inverted, switch them */
            if ((Machine.orientation & ROT180) == ORIENTATION_FLIP_X
                    || (Machine.orientation & ROT180) == ORIENTATION_FLIP_Y) {
                Machine.orientation ^= ROT180;
            }

            Machine.orientation ^= ROT270;

            /* if only one of the components is inverted, switch them */
            if ((Machine.ui_orientation & ROT180) == ORIENTATION_FLIP_X
                    || (Machine.ui_orientation & ROT180) == ORIENTATION_FLIP_Y) {
                Machine.ui_orientation ^= ROT180;
            }

            Machine.ui_orientation ^= ROT270;
        }
        if (options.flipx != 0) {
            Machine.orientation ^= ORIENTATION_FLIP_X;
            Machine.ui_orientation ^= ORIENTATION_FLIP_X;
        }
        if (options.flipy != 0) {
            Machine.orientation ^= ORIENTATION_FLIP_Y;
            Machine.ui_orientation ^= ORIENTATION_FLIP_Y;
        }

        set_pixel_functions();
        /* Do the work*/
        err = 1;
        bailing = 0;

        if (osd_init() == 0) {
            if (init_machine() == 0) {
                if (run_machine() == 0) {
                    err = 0;
                } else if (bailing == 0) {
                    bailing = 1;
                    printf("Unable to start machine emulation\n");
                }

                shutdown_machine();
            } else if (bailing == 0) {
                bailing = 1;
                printf("Unable to initialize machine emulation\n");
            }

            osd_exit();
        } else if (bailing == 0) {
            bailing = 1;
            printf("Unable to initialize system\n");
        }

        return err;
    }

    /**
     * *************************************************************************
     *
     * Initialize the emulated machine (load the roms, initialize the various
     * subsystems...). Returns 0 if successful.
     *
     **************************************************************************
     */
    public static int init_machine() {
        int i;
        if (uistring_init(options.language_file) != 0) {
            return out();
        }
        if (code_init() != 0) {
            return out();
        }

        for (i = 0; i < MAX_MEMORY_REGIONS; i++) {
            Machine.u8_memory_region[i] = null;
            Machine.memory_region_length[i] = 0;
            Machine.memory_region_type[i] = 0;
        }
        if (gamedrv.input_ports != null) {
            Machine.input_ports = input_port_allocate(gamedrv.input_ports);
            if (Machine.input_ports == null) {
                return out_code();
            }
            Machine.input_ports_default = input_port_allocate(gamedrv.input_ports);
            if (Machine.input_ports_default == null) {
                input_port_free(Machine.input_ports);
                Machine.input_ports = null;
                return out_code();
            }
        }

        if (readroms() != 0) {
            return out_free();
        }
        /* Mish:  Multi-session safety - set spriteram size to zero before memory map is set up */
        spriteram_size[0] = 0;
        spriteram_2_size[0] = 0;

        /* first of all initialize the memory handlers, which could be used by the */
 /* other initialization routines */
        cpu_init();

        /* load input ports settings (keys, dip switches, and so on) */
        settingsloaded = load_input_port_settings();

        if (memory_init() == 0) {
            return out_free();
        }

        if (gamedrv.driver_init != null) {
            gamedrv.driver_init.handler();
        }

        return 0;
    }

    static int out_free() {
        input_port_free(Machine.input_ports);
        Machine.input_ports = null;
        input_port_free(Machine.input_ports_default);
        Machine.input_ports_default = null;
        return out_code();
    }

    static int out_code() {

        code_close();
        return out();
    }

    static int out() {
        return 1;
    }

    public static void shutdown_machine() {
        int i;

        /* ASG 971007 free memory element map */
        memory_shutdown();

        /* free the memory allocated for ROM and RAM */
        for (i = 0; i < MAX_MEMORY_REGIONS; i++) {
            Machine.u8_memory_region[i] = null;
            Machine.memory_region_length[i] = 0;
            Machine.memory_region_type[i] = 0;
        }

        /* free the memory allocated for input ports definition */
        input_port_free(Machine.input_ports);
        Machine.input_ports = null;
        input_port_free(Machine.input_ports_default);
        Machine.input_ports_default = null;

        code_close();

        uistring_shutdown();
        /* LBO 042400 */
    }

    static void vh_close() {
        int i;

        for (i = 0; i < MAX_GFX_ELEMENTS; i++) {
            Machine.gfx[i] = null;
        }
        freegfx(Machine.uifont);
        Machine.uifont = null;
        osd_close_display();
        if (Machine.scrbitmap != null) {
            bitmap_free(Machine.scrbitmap);
            Machine.scrbitmap = null;
        }

        palette_stop();

        if ((drv.video_attributes & VIDEO_BUFFERS_SPRITERAM) != 0) {
            buffered_spriteram = null;
            buffered_spriteram_2 = null;
        }
    }

    /* Scale the vector games to a given resolution */
    static void scale_vectorgames(int gfx_width, int gfx_height, int[] width, int[] height) {
        double x_scale, y_scale, scale;

        if ((Machine.orientation & ORIENTATION_SWAP_XY) != 0) {
            x_scale = (double) gfx_width / (double) (height[0]);
            y_scale = (double) gfx_height / (double) (width[0]);
        } else {
            x_scale = (double) gfx_width / (double) (width[0]);
            y_scale = (double) gfx_height / (double) (height[0]);
        }
        if (x_scale < y_scale) {
            scale = x_scale;
        } else {
            scale = y_scale;
        }
        width[0] = (int) ((double) width[0] * scale);
        height[0] = (int) ((double) height[0] * scale);

        /* Padding to an dword value */
        width[0] -= width[0] % 4;
        height[0] -= height[0] % 4;
    }

    public static int vh_open() {
        int i;
        int viswidth, visheight;
        int[] bmwidth = new int[1];
        int[] bmheight = new int[1];

        for (i = 0; i < MAX_GFX_ELEMENTS; i++) {
            Machine.gfx[i] = null;
        }
        Machine.uifont = null;

        if (palette_start() != 0) {
            vh_close();
            return 1;
        }
        /* convert the gfx ROMs into character sets. This is done BEFORE calling the driver's */
 /* convert_color_prom() routine (in palette_init()) because it might need to check the */
 /* Machine.gfx[] data */
        if (drv.gfxdecodeinfo != null) {
            for (i = 0; i < MAX_GFX_ELEMENTS && drv.gfxdecodeinfo[i].memory_region != -1; i++) {
                int reglen = 8 * memory_region_length(drv.gfxdecodeinfo[i].memory_region);
                GfxLayout glcopy;
                int j;

                glcopy = drv.gfxdecodeinfo[i].gfxlayout;//memcpy(&glcopy,drv.gfxdecodeinfo[i].gfxlayout,sizeof(glcopy));
                if ((IS_FRAC(glcopy.total)) != 0) {
                    glcopy.total = reglen / glcopy.charincrement * FRAC_NUM(glcopy.total) / FRAC_DEN(glcopy.total);
                }
                for (j = 0; j < glcopy.planeoffset.length && j < MAX_GFX_PLANES; j++) {
                    if ((IS_FRAC(glcopy.planeoffset[j])) != 0) {
                        glcopy.planeoffset[j] = FRAC_OFFSET(glcopy.planeoffset[j])
                                + reglen * FRAC_NUM(glcopy.planeoffset[j]) / FRAC_DEN(glcopy.planeoffset[j]);
                    }
                }
                for (j = 0; j < MAX_GFX_SIZE; j++) {
                    if (j < glcopy.xoffset.length && (IS_FRAC(glcopy.xoffset[j]) != 0)) {
                        glcopy.xoffset[j] = FRAC_OFFSET(glcopy.xoffset[j])
                                + reglen * FRAC_NUM(glcopy.xoffset[j]) / FRAC_DEN(glcopy.xoffset[j]);
                    }
                    if (j < glcopy.yoffset.length && (IS_FRAC(glcopy.yoffset[j]) != 0)) {
                        glcopy.yoffset[j] = FRAC_OFFSET(glcopy.yoffset[j])
                                + reglen * FRAC_NUM(glcopy.yoffset[j]) / FRAC_DEN(glcopy.yoffset[j]);
                    }
                }
                if ((Machine.gfx[i] = decodegfx(new UBytePtr(memory_region(drv.gfxdecodeinfo[i].memory_region), drv.gfxdecodeinfo[i].start), glcopy)) == null) {

                    vh_close();

                    bailing = 1;
                    printf("Out of memory decoding gfx\n");

                    return 1;
                }
                if (Machine.remapped_colortable != null) {
                    Machine.gfx[i].colortable = new UShortArray(Machine.remapped_colortable, drv.gfxdecodeinfo[i].color_codes_start);
                }
                Machine.gfx[i].total_colors = drv.gfxdecodeinfo[i].total_color_codes;
            }
        }

        bmwidth[0] = drv.screen_width;
        bmheight[0] = drv.screen_height;

        if ((Machine.drv.video_attributes & VIDEO_TYPE_VECTOR) != 0) {
            scale_vectorgames(options.vector_width, options.vector_height, bmwidth, bmheight);
        }

        if ((Machine.drv.video_attributes & VIDEO_TYPE_VECTOR) == 0) {
            viswidth = drv.default_visible_area.max_x - drv.default_visible_area.min_x + 1;
            visheight = drv.default_visible_area.max_y - drv.default_visible_area.min_y + 1;
        } else {
            viswidth = bmwidth[0];
            visheight = bmheight[0];
        }

        if ((Machine.orientation & ORIENTATION_SWAP_XY) != 0) {
            int temp;
            temp = viswidth;
            viswidth = visheight;
            visheight = temp;
        }

        /* create the display bitmap, and allocate the palette */
        if (osd_create_display(viswidth, visheight, Machine.color_depth,
                (int) drv.frames_per_second, drv.video_attributes, Machine.orientation) != 0) {
            vh_close();
            return 1;
        }

        Machine.scrbitmap = bitmap_alloc_depth(bmwidth[0], bmheight[0], Machine.color_depth);
        if (Machine.scrbitmap == null) {
            vh_close();
            return 1;
        }

        set_visible_area(
                drv.default_visible_area.min_x,
                drv.default_visible_area.max_x,
                drv.default_visible_area.min_y,
                drv.default_visible_area.max_y);

        /* create spriteram buffers if necessary */
        if ((drv.video_attributes & VIDEO_BUFFERS_SPRITERAM) != 0) {
            if (spriteram_size[0] != 0) {
                buffered_spriteram = new UBytePtr(spriteram_size[0]);
                if (buffered_spriteram == null) {
                    vh_close();
                    return 1;
                }
                if (spriteram_2_size[0] != 0) {
                    buffered_spriteram_2 = new UBytePtr(spriteram_2_size[0]);
                }
                if (spriteram_2_size[0] != 0 && buffered_spriteram_2 == null) {
                    vh_close();
                    return 1;
                }
            } else {
                logerror("vh_open():  Video buffers spriteram but spriteram_size is 0\n");
                buffered_spriteram = null;
                buffered_spriteram_2 = null;
            }
        }

        /* build our private user interface font */
 /* This must be done AFTER osd_create_display() so the function knows the */
 /* resolution we are running at and can pick a different font depending on it. */
 /* It must be done BEFORE palette_init() because that will also initialize */
 /* (through osd_allocate_colors()) the uifont colortable. */
        if (null == (Machine.uifont = builduifont())) {
            vh_close();
            return 1;
        }

        /* initialize the palette - must be done after osd_create_display() */
        if (palette_init() != 0) {
            vh_close();
            return 1;
        }

        leds_status = 0;

        return 0;
    }

    /**
     * *************************************************************************
     *
     * This function takes care of refreshing the screen, processing user input,
     * and throttling the emulation speed to obtain the required frames per
     * second.
     *
     **************************************************************************
     */
    public static int need_to_clear_bitmap;

    /* set by the user interface */
    public static int updatescreen() {
        /* update sound */
        sound_update();

        if (osd_skip_this_frame() == 0) {
            if (need_to_clear_bitmap != 0) {
                osd_clearbitmap(real_scrbitmap);
                need_to_clear_bitmap = 0;
            }
            draw_screen(bitmap_dirty);
            /* update screen */
            bitmap_dirty = 0;
        }

        if (handle_user_interface(real_scrbitmap) != 0) {
            return 1;
        }

        update_video_and_audio();

        if (drv.vh_eof_callback != null) {
            (drv.vh_eof_callback).handler();
        }

        return 0;
    }

    /**
     * *************************************************************************
     *
     * Draw screen with overlays and backdrops
     *
     **************************************************************************
     */
    public static void draw_screen(int _bitmap_dirty) {

        (Machine.drv.vh_update).handler(Machine.scrbitmap, _bitmap_dirty);

        if (artwork_backdrop != null || artwork_overlay != null) {
            artwork_draw(artwork_real_scrbitmap, Machine.scrbitmap, _bitmap_dirty);
        }
    }

    /**
     * *************************************************************************
     *
     * Calls OSD layer handling overlays and backdrops
     *
     **************************************************************************
     */
    public static void update_video_and_audio() {
        osd_update_video_and_audio(real_scrbitmap, leds_status);
    }

    /**
     * *************************************************************************
     *
     * Run the emulation. Start the various subsystems and the CPU emulation.
     * Returns non zero in case of error.
     *
     **************************************************************************
     */
    public static int run_machine() {
        int res = 1;

        if (vh_open() == 0) {
            tilemap_init();
            sprite_init();
            gfxobj_init();
            if (drv.vh_start == null || drv.vh_start.handler() == 0) /* start the video hardware */ {
                if (sound_start() == 0) /* start the audio hardware */ {
                    int region;
                    real_scrbitmap = (artwork_overlay != null || artwork_backdrop != null) ? artwork_real_scrbitmap : Machine.scrbitmap;

                    /* free memory regions allocated with REGIONFLAG_DISPOSE (typically gfx roms) */
                    for (region = 0; region < MAX_MEMORY_REGIONS; region++) {
                        if ((Machine.memory_region_type[region] & REGIONFLAG_DISPOSE) != 0) {
                            int i;

                            /* invalidate contents to avoid subtle bugs */
                            for (i = 0; i < memory_region_length(region); i++) {
                                memory_region(region).write(i, rand());
                            }

                            Machine.u8_memory_region[region] = null;
                        }
                    }
                    if (settingsloaded == 0) {
                        /* if there is no saved config, it must be first time we run this game, *//* so show the disclaimer. */
                        if (showcopyright(real_scrbitmap) != 0) {
                            return userquit_goto();
                        }
                    }
                    if (showgamewarnings(real_scrbitmap) == 0) /* show info about incorrect behaviour (wrong colors etc.) */ {
                        init_user_interface();

                        /* disable cheat if no roms */
                        if (gamedrv.rom == null) {
                            options.cheat = 0;
                        }

                        if (options.cheat != 0) {
                            InitCheat();
                        }
                        if (drv.nvram_handler != null) {
                            Object f;

                            f = osd_fopen(Machine.gamedrv.name, null, OSD_FILETYPE_NVRAM, 0);
                            drv.nvram_handler.handler(f, 0);
                            if (f != null) {
                                osd_fclose(f);
                            }
                        }

                        cpu_run();
                        /* run the emulation! */

                        if (drv.nvram_handler != null) {
                            Object f;

                            if ((f = osd_fopen(Machine.gamedrv.name, null, OSD_FILETYPE_NVRAM, 1)) != null) {
                                drv.nvram_handler.handler(f, 1);
                                osd_fclose(f);
                            }
                        }

                        if (options.cheat != 0) {
                            StopCheat();
                        }

                        /* save input ports settings */
                        save_input_port_settings();
                    }
                    //userquit:
                    /* the following MUST be done after hiscore_save() otherwise *//* some 68000 games will not work */
                    sound_stop();
                    if (drv.vh_stop != null) {
                        drv.vh_stop.handler();
                    }
                    artwork_kill();
                    res = 0;
                } else if (bailing == 0) {
                    bailing = 1;
                    printf("Unable to start audio emulation\n");
                }
            } else if (bailing == 0) {
                bailing = 1;
                printf("Unable to start video emulation\n");
            }

            gfxobj_close();
            sprite_close();
            tilemap_close();
            vh_close();
        } else if (bailing == 0) {
            bailing = 1;
            printf("Unable to initialize display\n");
        }

        return res;
    }

    public static int userquit_goto() {
        int res;
        /* the following MUST be done after hiscore_save() otherwise *//* some 68000 games will not work */
        sound_stop();
        if (drv.vh_stop != null) {
            drv.vh_stop.handler();
        }
        artwork_kill();
        res = 0;
        gfxobj_close();
        sprite_close();
        tilemap_close();
        vh_close();
        return res;
    }

    public static int mame_highscore_enabled() {
        /* disable high score when record/playback is on */
        if (record != null || playback != null) {
            return 0;
        }

        /* disable high score when cheats are used */
        if (he_did_cheat != 0) {
            return 0;
        }

        return 1;
    }

    public static void set_led_status(int num, int on) {
        if (on != 0) {
            leds_status |= (1 << num);
        } else {
            leds_status &= ~(1 << num);
        }
    }
}
