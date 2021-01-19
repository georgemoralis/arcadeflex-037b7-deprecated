/*
 * ported to 0.37b7 
*/
package gr.codebb.arcadeflex.WIP.v037b7.mame;

import static gr.codebb.arcadeflex.common.SubArrays.*;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.GameDriver;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.MachineDriver;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.old.arcadeflex.libc_old.FILE;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.GameSamples;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.GfxElement;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.rectangle;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.InputPort;

public class mameH {

    public static final int MAX_GFX_ELEMENTS = 32;
    public static final int MAX_MEMORY_REGIONS = 32;

    public static class RunningMachine {

        public /*unsigned char * */ char[][] u8_memory_region = new char[MAX_MEMORY_REGIONS][];
        public int memory_region_length[] = new int[MAX_MEMORY_REGIONS];
        public int memory_region_type[] = new int[MAX_MEMORY_REGIONS];

        public GfxElement gfx[] = new GfxElement[MAX_GFX_ELEMENTS];
        public osd_bitmap scrbitmap;
        public rectangle visible_area;
        public /*UINT16 * */ char[] pens;
        public /*UINT16 * */ char[] game_colortable;
        public UShortArray remapped_colortable;
        public GameDriver gamedrv;
        public MachineDriver drv;
        public int color_depth;
        public int sample_rate;

        public int obsolete;
        public GameSamples samples;
        public InputPort[] input_ports;
        public InputPort[] input_ports_default;
        public int orientation;
        public GfxElement uifont;
        public int uifontwidth, uifontheight;
        public int uixmin, uiymin;
        public int uiwidth, uiheight;
        public int ui_orientation;
        public rectangle absolute_visible_area;
    }

    public static class GameOptions {

        public FILE record;
        public FILE playback;
        public FILE language_file;
        public int mame_debug;
        public int cheat;
        public int gui_host;
        public int samplerate;
        public int use_samples;
        public int use_emulated_ym3812;
        public int color_depth;
        public int vector_width;
        public int vector_height;
        public int norotate;
        public int ror;
        public int rol;
        public int flipx;
        public int flipy;
        public int beam;
        public int flicker;
        public int translucency;
        public int antialias;
        public int use_artwork;

    }
}
