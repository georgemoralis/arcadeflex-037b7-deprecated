/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.v037b7.drivers.battlane.battlane_cpu_control;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.bitmap_alloc;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.bitmap_free;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfx.copyscrollbitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.palette_change_color;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.palette_recalc;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import static gr.codebb.arcadeflex.common.libc.expressions.NOT;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;

public class battlane {

    static osd_bitmap screen_bitmap;

    public static int[] battlane_bitmap_size = new int[1];
    public static UBytePtr battlane_bitmap;
    static int battlane_video_ctrl;

    static int battlane_spriteram_size = 0x100;
    static char[] battlane_spriteram = new char[0x100];

    static int battlane_tileram_size = 0x800;
    static char[] battlane_tileram = new char[0x800];

    static int flipscreen;
    static int battlane_scrolly;
    static int battlane_scrollx;

    static osd_bitmap bkgnd_bitmap;
    /* scroll bitmap */

    public static WriteHandlerPtr battlane_video_ctrl_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /*
	    Video control register
	
	        0x80    = ????
	        0x0e    = Bitmap plane (bank?) select  (0-7)
	        0x01    = Scroll MSB
             */

            battlane_video_ctrl = data;
        }
    };

    public static ReadHandlerPtr battlane_video_ctrl_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return battlane_video_ctrl;
        }
    };

    public static void battlane_set_video_flip(int flip) {

        if (flip != flipscreen) {
            // Invalidate any cached data
        }

        flipscreen = flip;

        /*
	    Don't flip the screen. The render function doesn't support
	    it properly yet.
         */
        flipscreen = 0;

    }

    public static WriteHandlerPtr battlane_scrollx_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            battlane_scrollx = data;
        }
    };

    public static WriteHandlerPtr battlane_scrolly_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            battlane_scrolly = data;
        }
    };

    public static WriteHandlerPtr battlane_tileram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            battlane_tileram[offset] = (char) (data & 0xFF);
        }
    };

    public static ReadHandlerPtr battlane_tileram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return battlane_tileram[offset];
        }
    };

    public static WriteHandlerPtr battlane_spriteram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            battlane_spriteram[offset] = (char) (data & 0xFF);
        }
    };

    public static ReadHandlerPtr battlane_spriteram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return battlane_spriteram[offset];
        }
    };

    public static WriteHandlerPtr battlane_bitmap_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int i, orval;

            orval = (~battlane_video_ctrl >> 1) & 0x07;

            if (orval == 0) {
                orval = 7;
            }

            for (i = 0; i < 8; i++) {
                if ((data & 1 << i) != 0) {
                    screen_bitmap.line[(offset / 0x100) * 8 + i].write((0x2000 - offset) % 0x100, screen_bitmap.line[(offset / 0x100) * 8 + i].read((0x2000 - offset) % 0x100) | orval);
                } else {
                    screen_bitmap.line[(offset / 0x100) * 8 + i].write((0x2000 - offset) % 0x100, screen_bitmap.line[(offset / 0x100) * 8 + i].read((0x2000 - offset) % 0x100) & ~orval);
                }
            }
            battlane_bitmap.write(offset, data);
        }
    };

    public static ReadHandlerPtr battlane_bitmap_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return battlane_bitmap.read(offset);
        }
    };

    /**
     * *************************************************************************
     * Start the video hardware emulation.
     * *************************************************************************
     */
    public static VhStartPtr battlane_vh_start = new VhStartPtr() {
        public int handler() {
            screen_bitmap = bitmap_alloc(0x20 * 8, 0x20 * 8);
            if (screen_bitmap == null) {
                return 1;
            }

            battlane_bitmap = new UBytePtr(battlane_bitmap_size[0]);
            if (battlane_bitmap == null) {
                return 1;
            }

            memset(battlane_spriteram, 0, battlane_spriteram_size);
            memset(battlane_tileram, 255, battlane_tileram_size);

            bkgnd_bitmap = bitmap_alloc(0x0200, 0x0200);
            if (bkgnd_bitmap == null) {
                return 1;
            }

            return 0;
        }
    };

    /**
     * *************************************************************************
     * Stop the video hardware emulation.
     * *************************************************************************
     */
    public static VhStopPtr battlane_vh_stop = new VhStopPtr() {
        public void handler() {
            if (screen_bitmap != null) {
                bitmap_free(screen_bitmap);
            }
            if (battlane_bitmap != null) {
                battlane_bitmap = null;
            }
            if (bkgnd_bitmap != null) {
                bkgnd_bitmap = null;
            }
        }
    };

    /**
     * *************************************************************************
     * Build palette from palette RAM
     * *************************************************************************
     */
    public static void battlane_build_palette() {
        int offset;
        UBytePtr PALETTE = memory_region(REGION_PROMS);

        for (offset = 0; offset < 0x40; offset++) {
            int palette = PALETTE.read(offset);
            int red, green, blue;

            blue = ((palette >> 6) & 0x03) * 16 * 4;
            green = ((palette >> 3) & 0x07) * 16 * 2;
            red = ((palette >> 0) & 0x07) * 16 * 2;

            palette_change_color(offset, red, green, blue);
        }
    }

    /*
	
	public static VhConvertColorPromPtr battlane_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) 
	{
	
	} };
     */
    /**
     * *************************************************************************
     * Draw the game screen in the given osd_bitmap. Do NOT call
     * osd_update_display() from this function, it will be called by the main
     * emulation engine.
     * *************************************************************************
     */
    public static VhUpdatePtr battlane_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int scrollx, scrolly;
            int x, y, offs;

            /* Scroll registers */
            scrolly = 256 * (battlane_video_ctrl & 0x01) + battlane_scrolly;
            scrollx = 256 * (battlane_cpu_control & 0x01) + battlane_scrollx;

            battlane_build_palette();
            if (palette_recalc() != null) {
                // Mark cached layer as dirty
            }

            /* Draw tile map. TODO: Cache it */
            for (offs = 0; offs < 0x400; offs++) {
                int sx, sy;
                int code = battlane_tileram[offs];
                int attr = battlane_tileram[0x400 + offs];

                sx = (offs & 0x0f) + (offs & 0x100) / 16;
                sy = ((offs & 0x200) / 2 + (offs & 0x0f0)) / 16;
                drawgfx(bkgnd_bitmap, Machine.gfx[1 + (attr & 0x01)],
                        code,
                        (attr >> 1) & 0x07,
                        NOT(flipscreen), flipscreen,
                        sx * 16, sy * 16,
                        null,
                        TRANSPARENCY_NONE, 0);

            }
            /* copy the background graphics */
            {
                int scrlx, scrly;
                scrlx = -scrollx;
                scrly = -scrolly;
                copyscrollbitmap(bitmap, bkgnd_bitmap, 1, new int[]{scrly}, 1, new int[]{scrlx}, Machine.visible_area, TRANSPARENCY_NONE, 0);
            }
            {
                //char baf[256];
                //char baf2[40];
                //baf[0]=0;

                /* Draw sprites */
                for (offs = 0; offs < 0x0100; offs += 4) {
                    /*
	           0x80=bank 2
	           0x40=
	           0x20=bank 1
	           0x10=y double
	           0x08=Unknown - all vehicles have this bit clear
	           0x04=x flip
	           0x02=y flip
	           0x01=Sprite enable
                     */
                    int attr = battlane_spriteram[offs + 1];
                    int code = battlane_spriteram[offs + 3];
                    code += 256 * ((attr >> 6) & 0x02);
                    code += 256 * ((attr >> 5) & 0x01);
                    if (offs > 0x00a0) {
                        //sprintf(baf2, "%02x ", attr);
                        //strcat(baf,baf2);
                    }

                    if ((attr & 0x01) != 0) {
                        int sx = battlane_spriteram[offs + 2];
                        int sy = battlane_spriteram[offs];
                        int flipx = attr & 0x04;
                        int flipy = attr & 0x02;
                        if (flipscreen == 0) {
                            sx = 240 - sx;
                            sy = 240 - sy;
                            flipy = NOT(flipy);
                            flipx = NOT(flipx);
                        }
                        if ((attr & 0x10) != 0) /* Double Y direction */ {
                            int dy = 16;
                            if (flipy != 0) {
                                dy = -16;
                            }
                            drawgfx(bitmap, Machine.gfx[0],
                                    code,
                                    0,
                                    flipx, flipy,
                                    sx, sy,
                                    Machine.visible_area,
                                    TRANSPARENCY_PEN, 0);

                            drawgfx(bitmap, Machine.gfx[0],
                                    code + 1,
                                    0,
                                    flipx, flipy,
                                    sx, sy - dy,
                                    Machine.visible_area,
                                    TRANSPARENCY_PEN, 0);
                        } else {
                            drawgfx(bitmap, Machine.gfx[0],
                                    code,
                                    0,
                                    flipx, flipy,
                                    sx, sy,
                                    Machine.visible_area,
                                    TRANSPARENCY_PEN, 0);
                        }
                    }
                }

                //    usrintf_showmessage(baf);
            }
            /* Draw foreground bitmap */
            if (flipscreen != 0) {
                for (y = 0; y < 0x20 * 8; y++) {
                    for (x = 0; x < 0x20 * 8; x++) {
                        int data = screen_bitmap.line[y].read(x);
                        if (data != 0) {
                            bitmap.line[255 - y].write(255 - x, Machine.pens[data]);
                        }
                    }
                }
            } else {
                for (y = 0; y < 0x20 * 8; y++) {
                    for (x = 0; x < 0x20 * 8; x++) {
                        int data = screen_bitmap.line[y].read(x);
                        if (data != 0) {
                            bitmap.line[y].write(x, Machine.pens[data]);
                        }
                    }
                }

            }

        }
    };
}
