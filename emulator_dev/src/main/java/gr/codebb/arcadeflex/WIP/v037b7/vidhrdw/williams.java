/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGION_CPU1;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.cpu_getscanline;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.TRANSPARENCY_NONE;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.palette_recalc;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.videoram_size;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import static gr.codebb.arcadeflex.old.arcadeflex.video.osd_mark_dirty;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import static gr.codebb.arcadeflex.old.mame.drawgfx.drawgfx;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.ReadHandlerPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.VhStartPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.VhStopPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.VhUpdatePtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.WriteHandlerPtr;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.machine.williams.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGION_PROMS;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.cpu_get_pc;
import gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.rectangle;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.cpu_readmem16;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.cpu_writemem16;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.palette_change_color;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.palette_used_colors;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.paletteram_BBGGGRRR_w;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.paletteH.PALETTE_COLOR_TRANSPARENT;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.paletteH.PALETTE_COLOR_UNUSED;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.common.libc.cstring.memcpy;
import static gr.codebb.arcadeflex.common.libc.expressions.sizeof;

public class williams {

    /* RAM globals */
    public static UBytePtr williams_videoram;
    public static UBytePtr williams2_paletteram;

    /* blitter variables */
    public static UBytePtr williams_blitterram = new UBytePtr();
    public static char/*UINT8*/ williams_blitter_xor;
    public static char/*UINT8*/ williams_blitter_remap;
    public static char/*UINT8*/ williams_blitter_clip;


    /* Blaster extra variables */
    public static UBytePtr blaster_video_bits = new UBytePtr();
    public static UBytePtr blaster_color_zero_table = new UBytePtr();
    public static UBytePtr blaster_color_zero_flags = new UBytePtr();
    public static UBytePtr blaster_remap;
    public static UBytePtr blaster_remap_lookup;
    public static char/*UINT8*/ blaster_erase_screen;
    static char blaster_back_color;

    /* tilemap variables */
    public static char/*UINT8*/ williams2_tilemap_mask;
    public static char[] williams2_row_to_palette;
    /* take care of IC79 and J1/J2 */
    public static char/*UINT8*/ williams2_M7_flip;
    public static byte williams2_videoshift;
    public static char/*UINT8*/ williams2_special_bg_color;
    public static char/*UINT8*/ williams2_fg_color;
    /* IC90 */
    public static char/*UINT8*/ williams2_bg_color;
    /* IC89 */

 /* later-Williams video control variables */
    public static UBytePtr williams2_blit_inhibit = new UBytePtr();
    public static UBytePtr williams2_xscroll_low = new UBytePtr();
    public static UBytePtr williams2_xscroll_high = new UBytePtr();

    /* pixel copiers */
    static UBytePtr scanline_dirty;

    public static abstract interface blitter_table_Ptr {

        public abstract void handler(int sstart, int dstart, int w, int h, int data);
    }

    static blitter_table_Ptr[] blitter_table;

    /**
     * ***********************************
     *
     * Dirty marking
     *
     ************************************
     */
    static void mark_dirty(int x1, int y1, int x2, int y2) {
        int temp;

        /* swap X/Y */
        if ((Machine.orientation & ORIENTATION_SWAP_XY) != 0) {
            temp = x1;
            x1 = y1;
            y1 = temp;
            temp = x2;
            x2 = y2;
            y2 = temp;
        }

        /* flip X */
        if ((Machine.orientation & ORIENTATION_FLIP_X) != 0) {
            temp = Machine.scrbitmap.width - 1;
            x1 = temp - x1;
            x2 = temp - x2;
            temp = x1;
            x1 = x2;
            x2 = temp;
        }

        /* flip Y */
        if ((Machine.orientation & ORIENTATION_FLIP_Y) != 0) {
            temp = Machine.scrbitmap.height - 1;
            y1 = temp - y1;
            y2 = temp - y2;
            temp = y1;
            y1 = y2;
            y2 = temp;
        }

        /* mark it */
        osd_mark_dirty(x1, y1, x2, y2, 0);
    }

    /**
     * ***********************************
     *
     * Early Williams video startup/shutdown
     *
     ************************************
     */
    public static VhStartPtr williams_vh_start = new VhStartPtr() {
        public int handler() {
            /* allocate space for video RAM and dirty scanlines */
            williams_videoram = new UBytePtr(videoram_size[0] + 256);
            if (williams_videoram == null) {
                return 1;
            }
            scanline_dirty = new UBytePtr(williams_videoram, videoram_size[0]);
            memset(williams_videoram, 0, videoram_size[0]);
            memset(scanline_dirty, 1, 256);

            /* pick the blitters */
            blitter_table = williams_blitters;
            if (williams_blitter_remap != 0) {
                blitter_table = blaster_blitters;
            }
            if (williams_blitter_clip != 0) {
                blitter_table = sinistar_blitters;
            }

            /* reset special-purpose flags */
            blaster_remap_lookup = null;
            blaster_erase_screen = 0;
            blaster_back_color = 0;
            sinistar_clip = 0xffff;

            return 0;
        }
    };

    public static VhStopPtr williams_vh_stop = new VhStopPtr() {
        public void handler() {
            /* free any remap lookup tables */
            if (blaster_remap_lookup != null) {
                blaster_remap_lookup = null;
            }

            /* free video RAM */
            if (williams_videoram != null) {
                williams_videoram = null;
            }
            scanline_dirty = null;
        }
    };

    /**
     * ***********************************
     *
     * Early Williams video update
     *
     ************************************
     */
    public static void williams_vh_update(int counter) {
        rectangle clip = new rectangle();

        /* wrap around at the bottom */
        if (counter == 0) {
            counter = 256;
        }

        /* determine the clip rect */
        clip.min_x = Machine.visible_area.min_x;
        clip.max_x = Machine.visible_area.max_x;
        clip.min_y = counter - 16;
        clip.max_y = clip.min_y + 15;

        /* combine the clip rect with the visible rect */
        if (Machine.visible_area.min_y > clip.min_y) {
            clip.min_y = Machine.visible_area.min_y;
        }
        if (Machine.visible_area.max_y < clip.max_y) {
            clip.max_y = Machine.visible_area.max_y;
        }

        /* copy */
        if (Machine.scrbitmap.depth == 8) {
            if (williams_blitter_remap != 0) {
                copy_pixels_remap_8(Machine.scrbitmap, clip);
            } else {
                copy_pixels_8(Machine.scrbitmap, clip);
            }
        } else {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///			if (williams_blitter_remap != 0)
/*TODO*///				copy_pixels_remap_16(Machine.scrbitmap, &clip);
/*TODO*///			else
/*TODO*///				copy_pixels_16(Machine.scrbitmap, &clip);
        }

        /* optionally erase from lines 24 downward */
        if (blaster_erase_screen != 0 && clip.max_y > 24) {
            int offset, count;

            /* don't erase above row 24 */
            if (clip.min_y < 24) {
                clip.min_y = 24;
            }

            /* erase the memory associated with this area */
            count = clip.max_y - clip.min_y + 1;
            for (offset = clip.min_y; offset < videoram_size[0]; offset += 0x100) {
                memset(williams_videoram, offset, 0, count);
            }
        }
    }

    public static VhUpdatePtr williams_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            /* full refresh forces us to redraw everything */
            if (palette_recalc() != null || full_refresh != 0) {
                memset(scanline_dirty, 1, 256);
            }
        }
    };

    /**
     * ***********************************
     *
     * Early Williams video I/O
     *
     ************************************
     */
    public static WriteHandlerPtr williams_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* only update if different */
            if (williams_videoram.read(offset) != data) {
                /* store to videoram and mark the scanline dirty */
                williams_videoram.write(offset, data);
                scanline_dirty.write(offset % 256, 1);
            }
        }
    };

    public static ReadHandlerPtr williams_video_counter_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return cpu_getscanline() & 0xfc;
        }
    };

    /**
     * ***********************************
     *
     * Later Williams video startup/shutdown
     *
     ************************************
     */
    public static VhStartPtr williams2_vh_start = new VhStartPtr() {
        public int handler() {
            /* standard initialization */
            if (williams_vh_start.handler() != 0) {
                return 1;
            }

            /* override the blitters */
            blitter_table = williams2_blitters;

            /* allocate a buffer for palette RAM */
            williams2_paletteram = new UBytePtr(4 * 1024 * 4 / 8);
            if (williams2_paletteram == null) {
                williams2_vh_stop.handler();
                return 1;
            }

            /* clear it */
            memset(williams2_paletteram, 0, 4 * 1024 * 4 / 8);

            /* reset the FG/BG colors */
            williams2_fg_color = 0;
            williams2_bg_color = 0;

            return 0;
        }
    };

    public static VhStopPtr williams2_vh_stop = new VhStopPtr() {
        public void handler() {
            /* free palette RAM */
            if (williams2_paletteram != null) {
                williams2_paletteram = null;
            }

            /* clean up other stuff */
            williams_vh_stop.handler();
        }
    };

    /**
     * ***********************************
     *
     * Later Williams video update
     *
     ************************************
     */
    static void williams2_update_tiles(int y, rectangle clip) {
        UBytePtr tileram = new UBytePtr(memory_region(REGION_CPU1), 0xc000);
        int xpixeloffset, xtileoffset;
        int color, col;

        /* assemble the bits that describe the X scroll offset */
        xpixeloffset = (williams2_xscroll_high.read() & 1) * 12
                + (williams2_xscroll_low.read() >> 7) * 6
                + (williams2_xscroll_low.read() & 7)
                + williams2_videoshift;
        xtileoffset = williams2_xscroll_high.read() >> 1;


        /* adjust the offset for the row and compute the palette index */
        tileram.inc(y / 16);
        color = williams2_row_to_palette[y / 16];

        /* 12 columns wide, each block is 24 pixels wide, 288 pixel lines */
        for (col = 0; col <= 12; col++) {
            int map = tileram.read(((col + xtileoffset) * 16) & 0x07ff);

            drawgfx(Machine.scrbitmap, Machine.gfx[0], map & williams2_tilemap_mask,
                    color, map & williams2_M7_flip, 0, col * 24 - xpixeloffset, y,
                    Machine.visible_area, TRANSPARENCY_NONE, 0);
        }

    }

    public static void williams2_vh_update(int counter) {
        rectangle clip = new rectangle();

        /* wrap around at the bottom */
        if (counter == 0) {
            counter = 256;
        }

        /* determine the clip rect */
        clip.min_x = Machine.visible_area.min_x;
        clip.max_x = Machine.visible_area.max_x;
        clip.min_y = counter - 16;
        clip.max_y = clip.min_y + 15;

        /* combine the clip rect with the visible rect */
        if (Machine.visible_area.min_y > clip.min_y) {
            clip.min_y = Machine.visible_area.min_y;
        }
        if (Machine.visible_area.max_y < clip.max_y) {
            clip.max_y = Machine.visible_area.max_y;
        }

        /* redraw the tiles */
        williams2_update_tiles(counter - 16, clip);

        /* copy the bitmap data on top of that */
        if (Machine.scrbitmap.depth == 8) {
            copy_pixels_transparent_8(Machine.scrbitmap, clip);
        } else {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///			copy_pixels_transparent_16(Machine.scrbitmap, clip);
        }
    }

    /**
     * ***********************************
     *
     * Later Williams palette I/O
     *
     ************************************
     */
    static void williams2_modify_color(int color, int offset) {
        int ztable[]
                = {
                    0x0, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9,
                    0xa, 0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11
                };

        int entry_lo = williams2_paletteram.read(offset * 2);
        int entry_hi = williams2_paletteram.read(offset * 2 + 1);
        int i = ztable[(entry_hi >> 4) & 15];
        int b = ((entry_hi >> 0) & 15) * i;
        int g = ((entry_lo >> 4) & 15) * i;
        int r = ((entry_lo >> 0) & 15) * i;

        palette_change_color(color, r & 0xFF, g & 0xFF, b & 0xFF);
    }

    static void williams2_update_fg_color(int offset) {
        int page_offset = williams2_fg_color * 16;

        /* only modify the palette if we're talking to the current page */
        if (offset >= page_offset && offset < page_offset + 16) {
            williams2_modify_color(offset - page_offset, offset);
        }
    }

    static void williams2_update_bg_color(int offset) {
        int page_offset = williams2_bg_color * 16;

        /* non-Mystic Marathon variant */
        if (williams2_special_bg_color == 0) {
            /* only modify the palette if we're talking to the current page */
            if (offset >= page_offset && offset < page_offset + Machine.drv.total_colors - 16) {
                williams2_modify_color(offset - page_offset + 16, offset);
            }
        } /* Mystic Marathon variant */ else {
            /* only modify the palette if we're talking to the current page */
            if (offset >= page_offset && offset < page_offset + 16) {
                williams2_modify_color(offset - page_offset + 16, offset);
            }

            /* check the secondary palette as well */
            page_offset |= 0x10;
            if (offset >= page_offset && offset < page_offset + 16) {
                williams2_modify_color(offset - page_offset + 32, offset);
            }
        }
    }

    public static WriteHandlerPtr williams2_fg_select_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int i, palindex;

            /* if we're already mapped, leave it alone */
            if (williams2_fg_color == data) {
                return;
            }
            williams2_fg_color = (char) ((data & 0x3f) & 0xFF);

            /* remap the foreground colors */
            palindex = williams2_fg_color * 16;
            for (i = 0; i < 16; i++) {
                williams2_modify_color(i, palindex++);
            }
        }
    };

    public static WriteHandlerPtr williams2_bg_select_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int i, palindex;

            /* if we're already mapped, leave it alone */
            if (williams2_bg_color == data) {
                return;
            }
            williams2_bg_color = (char) ((data & 0x3f) & 0xFF);

            /* non-Mystic Marathon variant */
            if (williams2_special_bg_color == 0) {
                /* remap the background colors */
                palindex = williams2_bg_color * 16;
                for (i = 16; i < Machine.drv.total_colors; i++) {
                    williams2_modify_color(i, palindex++);
                }
            } /* Mystic Marathon variant */ else {
                /* remap the background colors */
                palindex = williams2_bg_color * 16;
                for (i = 16; i < 32; i++) {
                    williams2_modify_color(i, palindex++);
                }

                /* remap the secondary background colors */
                palindex = (williams2_bg_color | 1) * 16;
                for (i = 32; i < 48; i++) {
                    williams2_modify_color(i, palindex++);
                }
            }
        }
    };

    /**
     * ***********************************
     *
     * Later Williams video I/O
     *
     ************************************
     */
    public static WriteHandlerPtr williams2_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* bank 3 doesn't touch the screen */
            if ((williams2_bank & 0x03) == 0x03) {
                /* bank 3 from $8000 - $8800 affects palette RAM */
                if (offset >= 0x8000 && offset < 0x8800) {
                    offset -= 0x8000;
                    williams2_paletteram.write(offset, data);

                    /* update the palette value if necessary */
                    offset >>= 1;
                    williams2_update_fg_color(offset);
                    williams2_update_bg_color(offset);
                }
                return;
            }

            /* everyone else talks to the screen */
            williams_videoram.write(offset, data);
        }
    };

    /**
     * ***********************************
     *
     * Blaster-specific video start
     *
     ************************************
     */
    public static VhStartPtr blaster_vh_start = new VhStartPtr() {
        public int handler() {
            int i, j;

            /* standard startup first */
            if (williams_vh_start.handler() != 0) {
                return 1;
            }

            /* Expand the lookup table so that we do one lookup per byte */
            blaster_remap_lookup = new UBytePtr(256 * 256);
            if (blaster_remap_lookup != null) {
                for (i = 0; i < 256; i++) {
                    UBytePtr table = new UBytePtr(memory_region(REGION_PROMS), (i & 0x7f) * 16);
                    for (j = 0; j < 256; j++) {
                        blaster_remap_lookup.write(i * 256 + j, (table.read(j >> 4) << 4) | table.read(j & 0x0f));
                    }
                }
            }

            /* mark color 0 as transparent. we will draw the rainbow background behind it */
            palette_used_colors.write(0, PALETTE_COLOR_TRANSPARENT);
            for (i = 0; i < 256; i++) {
                /* mark as used only the colors used for the visible background lines */
                if (i < Machine.visible_area.min_y || i > Machine.visible_area.max_y) {
                    palette_used_colors.write(16 + i, PALETTE_COLOR_UNUSED);
                }

                /* TODO: this leaves us with a total of 255+1 colors used, which is just */
 /* a bit too much for the palette system to handle them efficiently. */
 /* As a quick workaround, I set the top three lines to be always black. */
 /* To do it correctly, vh_screenrefresh() should group the background */
 /* lines of the same color and mark the others as COLOR_UNUSED. */
 /* The background is very redundant so this can be done easily. */
                palette_used_colors.write(16 + 0 + Machine.visible_area.min_y, PALETTE_COLOR_TRANSPARENT);
                palette_used_colors.write(16 + 1 + Machine.visible_area.min_y, PALETTE_COLOR_TRANSPARENT);
                palette_used_colors.write(16 + 2 + Machine.visible_area.min_y, PALETTE_COLOR_TRANSPARENT);
            }

            return 0;
        }
    };

    /**
     * ***********************************
     *
     * Blaster-specific video refresh
     *
     ************************************
     */
    public static VhUpdatePtr blaster_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int j;

            /* recalculate palette -- unfortunately, there is no recourse if a */
 /* major change occurs; we'll just have to eat it for one frame    */
            for (j = 0; j < 0x100; j++) {
                paletteram_BBGGGRRR_w.handler(j + 16, blaster_color_zero_table.read(j) ^ 0xff);
            }
            palette_recalc();

            /* reset the background color for the next frame */
            blaster_back_color = 0;
        }
    };

    /**
     * ***********************************
     *
     * Blaster-specific enhancements
     *
     ************************************
     */
    public static WriteHandlerPtr blaster_remap_select_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            blaster_remap = new UBytePtr(blaster_remap_lookup, data * 256);
        }
    };

    public static WriteHandlerPtr blaster_video_bits_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            blaster_video_bits.write(data);
            blaster_erase_screen = (char) ((data & 0x02) & 0xFF);
        }
    };

    /**
     * ***********************************
     *
     * Blitter I/O
     *
     ************************************
     */
    public static WriteHandlerPtr williams_blitter_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int sstart, dstart, w, h, count;

            /* store the data */
            williams_blitterram.write(offset, data);

            /* only writes to location 0 trigger the blit */
            if (offset != 0) {
                return;
            }

            /* compute the starting locations */
            sstart = (williams_blitterram.read(2) << 8) + williams_blitterram.read(3);
            dstart = (williams_blitterram.read(4) << 8) + williams_blitterram.read(5);

            /* compute the width and height */
            w = williams_blitterram.read(6) ^ williams_blitter_xor;
            h = williams_blitterram.read(7) ^ williams_blitter_xor;

            /* adjust the width and height */
            if (w == 0) {
                w = 1;
            }
            if (h == 0) {
                h = 1;
            }
            if (w == 255) {
                w = 256;
            }
            if (h == 255) {
                h = 256;
            }

            /* call the appropriate blitter */
            (blitter_table[(data >> 3) & 3]).handler(sstart, dstart, w, h, data);

            /* compute the ending address */
            if ((data & 0x02) != 0) {
                count = h;
            } else {
                count = w + w * h;
            }
            if (count > 256) {
                count = 256;
            }

            /* mark dirty */
            w = dstart % 256;
            while (count-- > 0) {
                scanline_dirty.write(w++ % 256, 1);
            }

            /* Log blits */
            logerror("---------- Blit %02X--------------PC: %04X\n", data, cpu_get_pc());
            logerror("Source : %02X %02X\n", williams_blitterram.read(2), williams_blitterram.read(3));
            logerror("Dest   : %02X %02X\n", williams_blitterram.read(4), williams_blitterram.read(5));
            logerror("W H    : %02X %02X (%d,%d)\n", williams_blitterram.read(6), williams_blitterram.read(7), williams_blitterram.read(6) ^ 4, williams_blitterram.read(7) ^ 4);
            logerror("Mask   : %02X\n", williams_blitterram.read(1));
        }
    };

    /**
     * ***********************************
     * Blitter macros
     ************************************
     */

    /* blit with pixel color 0 == transparent */
    public static void BLASTER_BLIT_TRANSPARENT(int offset, int data, int keepmask) {
        data = blaster_remap.read((data) & 0xff);
        if (data != 0) {
            int pix = ((offset < 0x9700) ? williams_videoram.read(offset) : cpu_readmem16(offset));
            int tempmask = keepmask;

            if ((data & 0xf0) == 0) {
                tempmask |= 0xf0;
            }
            if ((data & 0x0f) == 0) {
                tempmask |= 0x0f;
            }

            pix = (pix & tempmask) | (data & ~tempmask);
            if (offset < 0x9700) {
                williams_videoram.write(offset, pix);
            } else {
                cpu_writemem16(offset, pix);
            }
        }
    }

    public static void WILLIAMS2_BLIT_TRANSPARENT(int offset, int data, int keepmask) {
        if (data != 0) {
            int pix = ((offset < 0x9000 && (williams2_bank & 0x03) != 0x03) ? williams_videoram.read(offset) : cpu_readmem16(offset));
            int tempmask = keepmask;

            if ((data & 0xf0) == 0) {
                tempmask |= 0xf0;
            }
            if ((data & 0x0f) == 0) {
                tempmask |= 0x0f;
            }

            pix = (pix & tempmask) | (data & ~tempmask);
            if (offset < 0x9000 && (williams2_bank & 0x03) != 0x03) {
                williams_videoram.write(offset, pix);
            } else if (offset < 0x9000 || offset >= 0xc000 || williams2_blit_inhibit.read() == 0) {
                cpu_writemem16(offset, pix);
            }
        }
    }

    public static void SINISTAR_BLIT_TRANSPARENT(int offset, int data, int keepmask) {
        if (data != 0) {
            int pix = ((offset < 0x9800) ? williams_videoram.read(offset) : cpu_readmem16(offset));
            int tempmask = keepmask;

            if ((data & 0xf0) == 0) {
                tempmask |= 0xf0;
            }
            if ((data & 0x0f) == 0) {
                tempmask |= 0x0f;
            }

            pix = (pix & tempmask) | (data & ~tempmask);
            if (offset < sinistar_clip) {
                if (offset < 0x9800) {
                    williams_videoram.write(offset, pix);
                } else {
                    cpu_writemem16(offset, pix);
                }
            }
        }
    }

    public static void WILLIAMS_BLIT_TRANSPARENT(int offset, int data, int keepmask) {
        if (data != 0) {
            int pix = ((offset < 0x9800) ? williams_videoram.read(offset) : cpu_readmem16(offset));
            int tempmask = keepmask;

            if ((data & 0xf0) == 0) {
                tempmask |= 0xf0;
            }
            if ((data & 0x0f) == 0) {
                tempmask |= 0x0f;
            }

            pix = (pix & tempmask) | (data & ~tempmask);
            if (offset < 0x9800) {
                williams_videoram.write(offset, pix);
            } else {
                cpu_writemem16(offset, pix);
            }
        }
    }


    /* blit with pixel color 0 == transparent, other pixels == solid color */
    public static void BLASTER_BLIT_TRANSPARENT_SOLID(int offset, int data, int keepmask, int solid) {
        data = blaster_remap.read((data) & 0xff);
        if (data != 0) {
            int pix = ((offset < 0x9700) ? williams_videoram.read(offset) : cpu_readmem16(offset));
            int tempmask = keepmask;

            if ((data & 0xf0) == 0) {
                tempmask |= 0xf0;
            }
            if ((data & 0x0f) == 0) {
                tempmask |= 0x0f;
            }

            pix = (pix & tempmask) | (solid & ~tempmask);
            if (offset < 0x9700) {
                williams_videoram.write(offset, pix);
            } else {
                cpu_writemem16(offset, pix);
            }
        }
    }

    public static void WILLIAMS2_BLIT_TRANSPARENT_SOLID(int offset, int data, int keepmask, int solid) {
        if (data != 0) {
            int pix = ((offset < 0x9000 && (williams2_bank & 0x03) != 0x03) ? williams_videoram.read(offset) : cpu_readmem16(offset));
            int tempmask = keepmask;

            if ((data & 0xf0) == 0) {
                tempmask |= 0xf0;
            }
            if ((data & 0x0f) == 0) {
                tempmask |= 0x0f;
            }

            pix = (pix & tempmask) | (solid & ~tempmask);
            if (offset < 0x9000 && (williams2_bank & 0x03) != 0x03) {
                williams_videoram.write(offset, pix);
            } else if (offset < 0x9000 || offset >= 0xc000 || williams2_blit_inhibit.read() == 0) {
                cpu_writemem16(offset, pix);
            }
        }
    }

    public static void SINISTAR_BLIT_TRANSPARENT_SOLID(int offset, int data, int keepmask, int solid) {
        if (data != 0) {
            int pix = ((offset < 0x9800) ? williams_videoram.read(offset) : cpu_readmem16(offset));
            int tempmask = keepmask;

            if ((data & 0xf0) == 0) {
                tempmask |= 0xf0;
            }
            if ((data & 0x0f) == 0) {
                tempmask |= 0x0f;
            }

            pix = (pix & tempmask) | (solid & ~tempmask);
            if (offset < sinistar_clip) {
                if (offset < 0x9800) {
                    williams_videoram.write(offset, pix);
                } else {
                    cpu_writemem16(offset, pix);
                }
            }
        }
    }

    public static void BLIT_TRANSPARENT_SOLID_WILLIAMS(int offset, int data, int keepmask, int solid) {
        if (data != 0) {
            int pix = ((offset < 0x9800) ? williams_videoram.read(offset) : cpu_readmem16(offset));
            int tempmask = keepmask;

            if ((data & 0xf0) == 0) {
                tempmask |= 0xf0;
            }
            if ((data & 0x0f) == 0) {
                tempmask |= 0x0f;
            }

            pix = (pix & tempmask) | (solid & ~tempmask);
            if (offset < 0x9800) {
                williams_videoram.write(offset, pix);
            } else {
                cpu_writemem16(offset, pix);
            }
        }
    }


    /* blit with no transparency */
    public static void BLASTER_BLIT_OPAQUE(int offset, int data, int keepmask) {
        int pix = ((offset < 0x9700) ? williams_videoram.read(offset) : cpu_readmem16(offset));
        data = blaster_remap.read((data) & 0xff);
        pix = (pix & keepmask) | (data & ~keepmask);
        if (offset < 0x9700) {
            williams_videoram.write(offset, pix);
        } else {
            cpu_writemem16(offset, pix);
        }
    }

    public static void WILLIAMS2_BLIT_OPAQUE(int offset, int data, int keepmask) {
        int pix = ((offset < 0x9000 && (williams2_bank & 0x03) != 0x03) ? williams_videoram.read(offset) : cpu_readmem16(offset));
        pix = (pix & keepmask) | (data & ~keepmask);
        if (offset < 0x9000 && (williams2_bank & 0x03) != 0x03) {
            williams_videoram.write(offset, pix);
        } else if (offset < 0x9000 || offset >= 0xc000 || williams2_blit_inhibit.read() == 0) {
            cpu_writemem16(offset, pix);
        }
    }

    public static void SINISTAR_BLIT_OPAQUE(int offset, int data, int keepmask) {
        int pix = ((offset < 0x9800) ? williams_videoram.read(offset) : cpu_readmem16(offset));
        pix = (pix & keepmask) | (data & ~keepmask);
        if (offset < sinistar_clip) {
            if (offset < 0x9800) {
                williams_videoram.write(offset, pix);
            } else {
                cpu_writemem16(offset, pix);
            }
        }
    }

    public static void WILLIAMS_BLIT_OPAQUE(int offset, int data, int keepmask) {
        int pix = ((offset < 0x9800) ? williams_videoram.read(offset) : cpu_readmem16(offset));
        pix = (pix & keepmask) | (data & ~keepmask);
        if (offset < 0x9800) {
            williams_videoram.write(offset, pix);
        } else {
            cpu_writemem16(offset, pix);
        }
    }

    /* blit with no transparency in a solid color */
    public static void BLASTER_BLIT_OPAQUE_SOLID(int offset, int data, int keepmask, int solid) {
        int pix = ((offset < 0x9700) ? williams_videoram.read(offset) : cpu_readmem16(offset));
        pix = (pix & keepmask) | (solid & ~keepmask);
        if (offset < 0x9700) {
            williams_videoram.write(offset, pix);
        } else {
            cpu_writemem16(offset, pix);
        }
    }

    public static void WILLIAMS2_BLIT_OPAQUE_SOLID(int offset, int data, int keepmask, int solid) {
        int pix = ((offset < 0x9000 && (williams2_bank & 0x03) != 0x03) ? williams_videoram.read(offset) : cpu_readmem16(offset));
        pix = (pix & keepmask) | (solid & ~keepmask);
        if (offset < 0x9000 && (williams2_bank & 0x03) != 0x03) {
            williams_videoram.write(offset, pix);
        } else if (offset < 0x9000 || offset >= 0xc000 || williams2_blit_inhibit.read() == 0) {
            cpu_writemem16(offset, pix);
        }
    }

    public static void SINISTAR_BLIT_OPAQUE_SOLID(int offset, int data, int keepmask, int solid) {
        int pix = ((offset < 0x9800) ? williams_videoram.read(offset) : cpu_readmem16(offset));
        pix = (pix & keepmask) | (solid & ~keepmask);
        if (offset < sinistar_clip) {
            if (offset < 0x9800) {
                williams_videoram.write(offset, pix);
            } else {
                cpu_writemem16(offset, pix);
            }
        }
    }

    public static void WILLIAMS_BLIT_OPAQUE_SOLID(int offset, int data, int keepmask, int solid) {
        int pix = ((offset < 0x9800) ? williams_videoram.read(offset) : cpu_readmem16(offset));
        pix = (pix & keepmask) | (solid & ~keepmask);
        if (offset < 0x9800) {
            williams_videoram.write(offset, pix);
        } else {
            cpu_writemem16(offset, pix);
        }
    }

    /*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	Pixel copy macros
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
/*TODO*///	/* define this so that we get the pixel copying code when we #define WILLIAMS_COPIES 				1
/*TODO*///	
/*TODO*///	#define COPY_NAME 						copy_pixels_8
/*TODO*///	#define COPY_REMAP_NAME					copy_pixels_remap_8
/*TODO*///	#define COPY_TRANSPARENT_NAME			copy_pixels_transparent_8
/*TODO*///	#define TYPE							UINT8
/*TODO*///	#undef TYPE
/*TODO*///	#undef COPY_TRANSPARENT_NAME
/*TODO*///	#undef COPY_REMAP_NAME
/*TODO*///	#undef COPY_NAME
/*TODO*///	
/*TODO*///	#define COPY_NAME 						copy_pixels_16
/*TODO*///	#define COPY_REMAP_NAME					copy_pixels_remap_16
/*TODO*///	#define COPY_TRANSPARENT_NAME			copy_pixels_transparent_16
/*TODO*///	#define TYPE							UINT16
/*TODO*///	#undef TYPE
/*TODO*///	#undef COPY_TRANSPARENT_NAME
/*TODO*///	#undef COPY_REMAP_NAME
/*TODO*///	#undef COPY_NAME
/*TODO*///	
/*TODO*///	
/*TODO*///	
    /**
     * ***********************************
     * Copy pixels from videoram to the screen bitmap
     ************************************
     */
    static void copy_pixels_8(osd_bitmap bitmap, rectangle clip) {
        char[] pens = Machine.pens;
        int pairs = (clip.max_x - clip.min_x + 1) / 2;
        int xoffset = clip.min_x;
        int x, y;

        /* standard case */
        if ((Machine.orientation & ORIENTATION_SWAP_XY) == 0) {
            /* loop over rows */
            for (y = clip.min_y; y <= clip.max_y; y++) {
                UBytePtr source = new UBytePtr(williams_videoram, y + 256 * (xoffset / 2));
                UBytePtr dest;

                /* skip if not dirty */
                if (scanline_dirty.read(y) == 0) {
                    continue;
                }
                scanline_dirty.write(y, 0);
                mark_dirty(clip.min_x, y, clip.max_x, y);

                /* compute starting destination pixel based on flip */
                if ((Machine.orientation & ORIENTATION_FLIP_Y) == 0) {
                    dest = new UBytePtr(bitmap.line[y], 0);
                } else {
                    dest = new UBytePtr(bitmap.line[bitmap.height - 1 - y], 0);
                }

                /* non-X-flipped case */
                if ((Machine.orientation & ORIENTATION_FLIP_X) == 0) {
                    dest.inc(xoffset);
                    for (x = 0; x < pairs; x++, source.offset += 256, dest.offset += 2) {
                        int pix = source.read();
                        dest.write(0, pens[pix >> 4]);
                        dest.write(1, pens[pix & 0x0f]);
                    }
                } /* X-flipped case */ else {
                    dest.inc(bitmap.width - xoffset);
                    for (x = 0; x < pairs; x++, source.offset += 256, dest.offset -= 2) {
                        int pix = source.read();
                        dest.write(-1, pens[pix >> 4]);
                        dest.write(-2, pens[pix & 0x0f]);
                    }
                }
            }
        } /* X/Y swapped case */ else {
            int dy = (bitmap.line[1].offset - bitmap.line[0].offset);
            /*/ sizeof(TYPE);*/

 /* loop over rows */
            for (y = clip.min_y; y <= clip.max_y; y++) {
                UBytePtr source = new UBytePtr(williams_videoram, y + 256 * (xoffset / 2));
                UBytePtr dest;

                /* skip if not dirty */
                if (scanline_dirty.read(y) == 0) {
                    continue;
                }
                scanline_dirty.write(y, 0);
                mark_dirty(clip.min_x, y, clip.max_x, y);

                /* compute starting destination pixel based on flip */
                if ((Machine.orientation & ORIENTATION_FLIP_X) == 0) {
                    dest = new UBytePtr(bitmap.line[0], y);
                } else {
                    dest = new UBytePtr(bitmap.line[0], bitmap.width - 1 - y);
                }

                /* non-Y-flipped case */
                if ((Machine.orientation & ORIENTATION_FLIP_Y) == 0) {
                    dest.inc(xoffset * dy);
                    for (x = 0; x < pairs; x++, source.offset += 256, dest.offset += dy + dy) {
                        int pix = source.read();
                        dest.write(0, pens[pix >> 4]);
                        dest.write(dy, pens[pix & 0x0f]);
                    }
                } /* Y-flipped case */ else {
                    dest.inc((bitmap.height - xoffset) * dy);
                    for (x = 0; x < pairs; x++, source.offset += 256, dest.offset -= dy + dy) {
                        int pix = source.read();
                        dest.write(-dy, pens[pix >> 4]);
                        dest.write(-dy - dy, pens[pix & 0x0f]);
                    }
                }
            }
        }
    }

    /*TODO*///	static void COPY_NAME(struct osd_bitmap *bitmap, const struct rectangle *clip)
/*TODO*///	{
/*TODO*///		const UINT16 *pens = Machine.pens;
/*TODO*///		int pairs = (clip.max_x - clip.min_x + 1) / 2;
/*TODO*///		int xoffset = clip.min_x;
/*TODO*///		int x, y;
/*TODO*///	
/*TODO*///		/* standard case */
/*TODO*///		if (!(Machine.orientation & ORIENTATION_SWAP_XY))
/*TODO*///		{
/*TODO*///			/* loop over rows */
/*TODO*///			for (y = clip.min_y; y <= clip.max_y; y++)
/*TODO*///			{
/*TODO*///				const UINT8 *source = williams_videoram + y + 256 * (xoffset / 2);
/*TODO*///				TYPE *dest;
/*TODO*///	
/*TODO*///				/* skip if not dirty */
/*TODO*///				if (!scanline_dirty[y]) continue;
/*TODO*///				scanline_dirty[y] = 0;
/*TODO*///				mark_dirty(clip.min_x, y, clip.max_x, y);
/*TODO*///	
/*TODO*///				/* compute starting destination pixel based on flip */
/*TODO*///				if (!(Machine.orientation & ORIENTATION_FLIP_Y))
/*TODO*///					dest = &((TYPE *)bitmap.line[y])[0];
/*TODO*///				else
/*TODO*///					dest = &((TYPE *)bitmap.line[bitmap.height - 1 - y])[0];
/*TODO*///	
/*TODO*///				/* non-X-flipped case */
/*TODO*///				if (!(Machine.orientation & ORIENTATION_FLIP_X))
/*TODO*///				{
/*TODO*///					dest += xoffset;
/*TODO*///					for (x = 0; x < pairs; x++, source += 256, dest += 2)
/*TODO*///					{
/*TODO*///						int pix = *source;
/*TODO*///						dest[0] = pens[pix >> 4];
/*TODO*///						dest[1] = pens[pix & 0x0f];
/*TODO*///					}
/*TODO*///				}
/*TODO*///	
/*TODO*///				/* X-flipped case */
/*TODO*///				else
/*TODO*///				{
/*TODO*///					dest += bitmap.width - xoffset;
/*TODO*///					for (x = 0; x < pairs; x++, source += 256, dest -= 2)
/*TODO*///					{
/*TODO*///						int pix = *source;
/*TODO*///						dest[-1] = pens[pix >> 4];
/*TODO*///						dest[-2] = pens[pix & 0x0f];
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* X/Y swapped case */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			int dy = (bitmap.line[1] - bitmap.line[0]) / sizeof(TYPE);
/*TODO*///	
/*TODO*///			/* loop over rows */
/*TODO*///			for (y = clip.min_y; y <= clip.max_y; y++)
/*TODO*///			{
/*TODO*///				const UINT8 *source = williams_videoram + y + 256 * (xoffset / 2);
/*TODO*///				TYPE *dest;
/*TODO*///	
/*TODO*///				/* skip if not dirty */
/*TODO*///				if (!scanline_dirty[y]) continue;
/*TODO*///				scanline_dirty[y] = 0;
/*TODO*///				mark_dirty(clip.min_x, y, clip.max_x, y);
/*TODO*///	
/*TODO*///				/* compute starting destination pixel based on flip */
/*TODO*///				if (!(Machine.orientation & ORIENTATION_FLIP_X))
/*TODO*///					dest = &((TYPE *)bitmap.line[0])[y];
/*TODO*///				else
/*TODO*///					dest = &((TYPE *)bitmap.line[0])[bitmap.width - 1 - y];
/*TODO*///	
/*TODO*///				/* non-Y-flipped case */
/*TODO*///				if (!(Machine.orientation & ORIENTATION_FLIP_Y))
/*TODO*///				{
/*TODO*///					dest += xoffset * dy;
/*TODO*///					for (x = 0; x < pairs; x++, source += 256, dest += dy + dy)
/*TODO*///					{
/*TODO*///						int pix = *source;
/*TODO*///						dest[0] = pens[pix >> 4];
/*TODO*///						dest[dy] = pens[pix & 0x0f];
/*TODO*///					}
/*TODO*///				}
/*TODO*///	
/*TODO*///				/* Y-flipped case */
/*TODO*///				else
/*TODO*///				{
/*TODO*///					dest += (bitmap.height - xoffset) * dy;
/*TODO*///					for (x = 0; x < pairs; x++, source += 256, dest -= dy + dy)
/*TODO*///					{
/*TODO*///						int pix = *source;
/*TODO*///						dest[-dy] = pens[pix >> 4];
/*TODO*///						dest[-dy-dy] = pens[pix & 0x0f];
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
    /**
     * ***********************************
     * Copy pixels from videoram to the screen bitmap, handling Blaster's color
     * 0 latching
     ************************************
     */
    static void copy_pixels_remap_8(osd_bitmap bitmap, rectangle clip) {
        int pairs = (clip.max_x - clip.min_x + 1) / 2;
        int xoffset = clip.min_x;
        char[] pens = new char[16];
        int x, y;

        /* copy the pens to start */
        memcpy(pens, Machine.pens, sizeof(pens));

        /* standard case */
        if ((Machine.orientation & ORIENTATION_SWAP_XY) == 0) {
            /* loop over rows */
            for (y = clip.min_y; y <= clip.max_y; y++) {
                UBytePtr source = new UBytePtr(williams_videoram, y + 256 * (xoffset / 2));
                UBytePtr dest;

                /* pick the background pen */
                if ((blaster_video_bits.read() & 1) != 0) {
                    if ((blaster_color_zero_flags.read(y) & 1) != 0) {
                        blaster_back_color = (blaster_color_zero_table.read(y) != 0xff) ? (char) (16 + y) : (char) 0;
                    }
                } else {
                    blaster_back_color = 0;
                }
                pens[0] = Machine.pens[blaster_back_color];

                /* compute starting destination pixel based on flip */
                if ((Machine.orientation & ORIENTATION_FLIP_Y) == 0) {
                    dest = new UBytePtr(bitmap.line[y], 0);
                } else {
                    dest = new UBytePtr(bitmap.line[bitmap.height - 1 - y], 0);
                }

                /* non-X-flipped case */
                if ((Machine.orientation & ORIENTATION_FLIP_X) == 0) {
                    dest.offset += xoffset;
                    for (x = 0; x < pairs; x++, source.offset += 256, dest.offset += 2) {
                        int pix = source.read();
                        dest.write(0, pens[pix >> 4]);
                        dest.write(1, pens[pix & 0x0f]);
                    }
                } /* X-flipped case */ else {
                    dest.offset += bitmap.width - xoffset;
                    for (x = 0; x < pairs; x++, source.offset += 256, dest.offset -= 2) {
                        int pix = source.read();
                        dest.write(-1, pens[pix >> 4]);
                        dest.write(-2, pens[pix & 0x0f]);
                    }
                }
            }
        } /* X/Y swapped case */ else {
            int dy = (bitmap.line[1].offset - bitmap.line[0].offset); /// sizeof(TYPE);

            /* loop over rows */
            for (y = clip.min_y; y <= clip.max_y; y++) {
                UBytePtr source = new UBytePtr(williams_videoram, y + 256 * (xoffset / 2));
                UBytePtr dest;

                /* pick the background pen */
                if ((blaster_video_bits.read() & 1) != 0) {
                    if ((blaster_color_zero_flags.read(y) & 1) != 0) {
                        blaster_back_color = (blaster_color_zero_table.read(y) != 0xff) ? (char) (16 + y) : (char) 0;
                    }
                } else {
                    blaster_back_color = 0;
                }
                pens[0] = Machine.pens[blaster_back_color];

                /* compute starting destination pixel based on flip */
                if ((Machine.orientation & ORIENTATION_FLIP_X) == 0) {
                    dest = new UBytePtr(bitmap.line[0], y);
                } else {
                    dest = new UBytePtr(bitmap.line[0], bitmap.width - 1 - y);
                }

                /* non-Y-flipped case */
                if ((Machine.orientation & ORIENTATION_FLIP_Y) == 0) {
                    dest.offset += xoffset * dy;
                    for (x = 0; x < pairs; x++, source.offset += 256, dest.offset += dy + dy) {
                        int pix = source.read();
                        dest.write(0, pens[pix >> 4]);
                        dest.write(dy, pens[pix & 0x0f]);
                    }
                } /* Y-flipped case */ else {
                    dest.offset += (bitmap.height - xoffset) * dy;
                    for (x = 0; x < pairs; x++, source.offset += 256, dest.offset -= dy + dy) {
                        int pix = source.read();
                        dest.write(-dy, pens[pix >> 4]);
                        dest.write(-dy - dy, pens[pix & 0x0f]);
                    }
                }
            }
        }
    }

    /*TODO*///	static void COPY_REMAP_NAME(struct osd_bitmap *bitmap, const struct rectangle *clip)
/*TODO*///	{
/*TODO*///		int pairs = (clip.max_x - clip.min_x + 1) / 2;
/*TODO*///		int xoffset = clip.min_x;
/*TODO*///		UINT16 pens[16];
/*TODO*///		int x, y;
/*TODO*///	
/*TODO*///		/* copy the pens to start */
/*TODO*///		memcpy(pens, Machine.pens, sizeof(pens));
/*TODO*///	
/*TODO*///		/* standard case */
/*TODO*///		if (!(Machine.orientation & ORIENTATION_SWAP_XY))
/*TODO*///		{
/*TODO*///			/* loop over rows */
/*TODO*///			for (y = clip.min_y; y <= clip.max_y; y++)
/*TODO*///			{
/*TODO*///				const UINT8 *source = williams_videoram + y + 256 * (xoffset / 2);
/*TODO*///				TYPE *dest;
/*TODO*///	
/*TODO*///				/* pick the background pen */
/*TODO*///				if (*blaster_video_bits & 1)
/*TODO*///				{
/*TODO*///					if (blaster_color_zero_flags[y] & 1)
/*TODO*///						blaster_back_color = (blaster_color_zero_table[y] != 0xff) ? 16 + y : 0;
/*TODO*///				}
/*TODO*///				else
/*TODO*///					blaster_back_color = 0;
/*TODO*///				pens[0] = Machine.pens[blaster_back_color];
/*TODO*///	
/*TODO*///				/* compute starting destination pixel based on flip */
/*TODO*///				if (!(Machine.orientation & ORIENTATION_FLIP_Y))
/*TODO*///					dest = &((TYPE *)bitmap.line[y])[0];
/*TODO*///				else
/*TODO*///					dest = &((TYPE *)bitmap.line[bitmap.height - 1 - y])[0];
/*TODO*///	
/*TODO*///				/* non-X-flipped case */
/*TODO*///				if (!(Machine.orientation & ORIENTATION_FLIP_X))
/*TODO*///				{
/*TODO*///					dest += xoffset;
/*TODO*///					for (x = 0; x < pairs; x++, source += 256, dest += 2)
/*TODO*///					{
/*TODO*///						int pix = *source;
/*TODO*///						dest[0] = pens[pix >> 4];
/*TODO*///						dest[1] = pens[pix & 0x0f];
/*TODO*///					}
/*TODO*///				}
/*TODO*///	
/*TODO*///				/* X-flipped case */
/*TODO*///				else
/*TODO*///				{
/*TODO*///					dest += bitmap.width - xoffset;
/*TODO*///					for (x = 0; x < pairs; x++, source += 256, dest -= 2)
/*TODO*///					{
/*TODO*///						int pix = *source;
/*TODO*///						dest[-1] = pens[pix >> 4];
/*TODO*///						dest[-2] = pens[pix & 0x0f];
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* X/Y swapped case */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			int dy = (bitmap.line[1] - bitmap.line[0]) / sizeof(TYPE);
/*TODO*///	
/*TODO*///			/* loop over rows */
/*TODO*///			for (y = clip.min_y; y <= clip.max_y; y++)
/*TODO*///			{
/*TODO*///				const UINT8 *source = williams_videoram + y + 256 * (xoffset / 2);
/*TODO*///				TYPE *dest;
/*TODO*///	
/*TODO*///				/* pick the background pen */
/*TODO*///				if (*blaster_video_bits & 1)
/*TODO*///				{
/*TODO*///					if (blaster_color_zero_flags[y] & 1)
/*TODO*///						blaster_back_color = (blaster_color_zero_table[y] != 0xff) ? 16 + y : 0;
/*TODO*///				}
/*TODO*///				else
/*TODO*///					blaster_back_color = 0;
/*TODO*///				pens[0] = Machine.pens[blaster_back_color];
/*TODO*///	
/*TODO*///				/* compute starting destination pixel based on flip */
/*TODO*///				if (!(Machine.orientation & ORIENTATION_FLIP_X))
/*TODO*///					dest = &((TYPE *)bitmap.line[0])[y];
/*TODO*///				else
/*TODO*///					dest = &((TYPE *)bitmap.line[0])[bitmap.width - 1 - y];
/*TODO*///	
/*TODO*///				/* non-Y-flipped case */
/*TODO*///				if (!(Machine.orientation & ORIENTATION_FLIP_Y))
/*TODO*///				{
/*TODO*///					dest += xoffset * dy;
/*TODO*///					for (x = 0; x < pairs; x++, source += 256, dest += dy + dy)
/*TODO*///					{
/*TODO*///						int pix = *source;
/*TODO*///						dest[0] = pens[pix >> 4];
/*TODO*///						dest[dy] = pens[pix & 0x0f];
/*TODO*///					}
/*TODO*///				}
/*TODO*///	
/*TODO*///				/* Y-flipped case */
/*TODO*///				else
/*TODO*///				{
/*TODO*///					dest += (bitmap.height - xoffset) * dy;
/*TODO*///					for (x = 0; x < pairs; x++, source += 256, dest -= dy + dy)
/*TODO*///					{
/*TODO*///						int pix = *source;
/*TODO*///						dest[-dy] = pens[pix >> 4];
/*TODO*///						dest[-dy-dy] = pens[pix & 0x0f];
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	
    /**
     * ***********************************
     * Copy pixels from videoram to the screen bitmap, treating color 0 pixels
     * as transparent.
     ************************************
     */
    static void copy_pixels_transparent_8(osd_bitmap bitmap, rectangle clip) {
        char[] pens = Machine.pens;
        int pairs = (clip.max_x - clip.min_x + 1) / 2;
        int xoffset = clip.min_x;
        int x, y;

        /* standard case */
        if ((Machine.orientation & ORIENTATION_SWAP_XY) == 0) {
            /* loop over rows */
            for (y = clip.min_y; y <= clip.max_y; y++) {
                UBytePtr source = new UBytePtr(williams_videoram, y + 256 * (xoffset / 2));
                UBytePtr dest;

                /* compute starting destination pixel based on flip */
                if ((Machine.orientation & ORIENTATION_FLIP_Y) == 0) {
                    dest = new UBytePtr(bitmap.line[y], 0);
                } else {
                    dest = new UBytePtr(bitmap.line[bitmap.height - 1 - y], 0);
                }

                /* non-X-flipped case */
                if ((Machine.orientation & ORIENTATION_FLIP_X) == 0) {
                    dest.offset += xoffset;
                    for (x = 0; x < pairs; x++, source.offset += 256, dest.offset += 2) {
                        int pix = source.read();
                        if (pix != 0) {
                            int p1 = pix >> 4, p2 = pix & 0x0f;
                            if (p1 != 0) {
                                dest.write(0, pens[p1]);
                            }
                            if (p2 != 0) {
                                dest.write(1, pens[p2]);
                            }
                        }
                    }
                } /* X-flipped case */ else {
                    dest.offset += bitmap.width - xoffset;
                    for (x = 0; x < pairs; x++, source.offset += 256, dest.offset -= 2) {
                        int pix = source.read();
                        if (pix != 0) {
                            int p1 = pix >> 4, p2 = pix & 0x0f;
                            if (p1 != 0) {
                                dest.write(-1, pens[p1]);
                            }
                            if (p2 != 0) {
                                dest.write(-2, pens[p2]);
                            }
                        }
                    }
                }
            }
        } /* X/Y swapped case */ else {
            int dy = (bitmap.line[1].offset - bitmap.line[0].offset);// / sizeof(TYPE);

            /* loop over rows */
            for (y = clip.min_y; y <= clip.max_y; y++) {
                UBytePtr source = new UBytePtr(williams_videoram, y + 256 * (xoffset / 2));
                UBytePtr dest;

                /* compute starting destination pixel based on flip */
                if ((Machine.orientation & ORIENTATION_FLIP_X) == 0) {
                    dest = new UBytePtr(bitmap.line[0], y);
                } else {
                    dest = new UBytePtr(bitmap.line[0], bitmap.width - 1 - y);
                }

                /* non-Y-flipped case */
                if ((Machine.orientation & ORIENTATION_FLIP_Y) == 0) {
                    dest.offset += xoffset * dy;
                    for (x = 0; x < pairs; x++, source.offset += 256, dest.offset += dy + dy) {
                        int pix = source.read();
                        if (pix != 0) {
                            int p1 = pix >> 4, p2 = pix & 0x0f;
                            if (p1 != 0) {
                                dest.write(0, pens[p1]);
                            }
                            if (p2 != 0) {
                                dest.write(dy, pens[p2]);
                            }
                        }
                    }
                } /* Y-flipped case */ else {
                    dest.offset += (bitmap.height - xoffset) * dy;
                    for (x = 0; x < pairs; x++, source.offset += 256, dest.offset -= dy + dy) {
                        int pix = source.read();
                        if (pix != 0) {
                            int p1 = pix >> 4, p2 = pix & 0x0f;
                            if (p1 != 0) {
                                dest.write(-dy, pens[p1]);
                            }
                            if (p2 != 0) {
                                dest.write(-dy - dy, pens[p2]);
                            }
                        }
                    }
                }
            }
        }
    }
    /*TODO*///	static void COPY_TRANSPARENT_NAME(struct osd_bitmap *bitmap, const struct rectangle *clip)
/*TODO*///	{
/*TODO*///		const UINT16 *pens = Machine.pens;
/*TODO*///		int pairs = (clip.max_x - clip.min_x + 1) / 2;
/*TODO*///		int xoffset = clip.min_x;
/*TODO*///		int x, y;
/*TODO*///	
/*TODO*///		/* standard case */
/*TODO*///		if (!(Machine.orientation & ORIENTATION_SWAP_XY))
/*TODO*///		{
/*TODO*///			/* loop over rows */
/*TODO*///			for (y = clip.min_y; y <= clip.max_y; y++)
/*TODO*///			{
/*TODO*///				const UINT8 *source = williams_videoram + y + 256 * (xoffset / 2);
/*TODO*///				TYPE *dest;
/*TODO*///	
/*TODO*///				/* compute starting destination pixel based on flip */
/*TODO*///				if (!(Machine.orientation & ORIENTATION_FLIP_Y))
/*TODO*///					dest = &((TYPE *)bitmap.line[y])[0];
/*TODO*///				else
/*TODO*///					dest = &((TYPE *)bitmap.line[bitmap.height - 1 - y])[0];
/*TODO*///	
/*TODO*///				/* non-X-flipped case */
/*TODO*///				if (!(Machine.orientation & ORIENTATION_FLIP_X))
/*TODO*///				{
/*TODO*///					dest += xoffset;
/*TODO*///					for (x = 0; x < pairs; x++, source += 256, dest += 2)
/*TODO*///					{
/*TODO*///						int pix = *source;
/*TODO*///						if (pix != 0)
/*TODO*///						{
/*TODO*///							int p1 = pix >> 4, p2 = pix & 0x0f;
/*TODO*///							if (p1 != 0) dest[0] = pens[p1];
/*TODO*///							if (p2 != 0) dest[1] = pens[p2];
/*TODO*///						}
/*TODO*///					}
/*TODO*///				}
/*TODO*///	
/*TODO*///				/* X-flipped case */
/*TODO*///				else
/*TODO*///				{
/*TODO*///					dest += bitmap.width - xoffset;
/*TODO*///					for (x = 0; x < pairs; x++, source += 256, dest -= 2)
/*TODO*///					{
/*TODO*///						int pix = *source;
/*TODO*///						if (pix != 0)
/*TODO*///						{
/*TODO*///							int p1 = pix >> 4, p2 = pix & 0x0f;
/*TODO*///							if (p1 != 0) dest[-1] = pens[p1];
/*TODO*///							if (p2 != 0) dest[-2] = pens[p2];
/*TODO*///						}
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* X/Y swapped case */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			int dy = (bitmap.line[1] - bitmap.line[0]) / sizeof(TYPE);
/*TODO*///	
/*TODO*///			/* loop over rows */
/*TODO*///			for (y = clip.min_y; y <= clip.max_y; y++)
/*TODO*///			{
/*TODO*///				const UINT8 *source = williams_videoram + y + 256 * (xoffset / 2);
/*TODO*///				TYPE *dest;
/*TODO*///	
/*TODO*///				/* compute starting destination pixel based on flip */
/*TODO*///				if (!(Machine.orientation & ORIENTATION_FLIP_X))
/*TODO*///					dest = &((TYPE *)bitmap.line[0])[y];
/*TODO*///				else
/*TODO*///					dest = &((TYPE *)bitmap.line[0])[bitmap.width - 1 - y];
/*TODO*///	
/*TODO*///				/* non-Y-flipped case */
/*TODO*///				if (!(Machine.orientation & ORIENTATION_FLIP_Y))
/*TODO*///				{
/*TODO*///					dest += xoffset * dy;
/*TODO*///					for (x = 0; x < pairs; x++, source += 256, dest += dy + dy)
/*TODO*///					{
/*TODO*///						int pix = *source;
/*TODO*///						if (pix != 0)
/*TODO*///						{
/*TODO*///							int p1 = pix >> 4, p2 = pix & 0x0f;
/*TODO*///							if (p1 != 0) dest[0] = pens[p1];
/*TODO*///							if (p2 != 0) dest[dy] = pens[p2];
/*TODO*///						}
/*TODO*///					}
/*TODO*///				}
/*TODO*///	
/*TODO*///				/* Y-flipped case */
/*TODO*///				else
/*TODO*///				{
/*TODO*///					dest += (bitmap.height - xoffset) * dy;
/*TODO*///					for (x = 0; x < pairs; x++, source += 256, dest -= dy + dy)
/*TODO*///					{
/*TODO*///						int pix = *source;
/*TODO*///						if (pix != 0)
/*TODO*///						{
/*TODO*///							int p1 = pix >> 4, p2 = pix & 0x0f;
/*TODO*///							if (p1 != 0) dest[-dy] = pens[p1];
/*TODO*///							if (p2 != 0) dest[-dy-dy] = pens[p2];
/*TODO*///						}
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}

    public static blitter_table_Ptr williams_blit_opaque = new blitter_table_Ptr() {

        @Override
        public void handler(int sstart, int dstart, int w, int h, int data) {
            int source, sxadv, syadv;
            int dest, dxadv, dyadv;
            int i, j, solid;
            int keepmask;

            /* compute how much to advance in the x and y loops */
            sxadv = (data & 0x01) != 0 ? 0x100 : 1;
            syadv = (data & 0x01) != 0 ? 1 : w;
            dxadv = (data & 0x02) != 0 ? 0x100 : 1;
            dyadv = (data & 0x02) != 0 ? 1 : w;

            /* determine the common mask */
            keepmask = 0x00;
            if ((data & 0x80) != 0) {
                keepmask |= 0xf0;
            }
            if ((data & 0x40) != 0) {
                keepmask |= 0x0f;
            }
            if (keepmask == 0xff) {
                return;
            }

            /* set the solid pixel value to the mask value */
            solid = williams_blitterram.read(1);

            /* first case: no shifting */
            if ((data & 0x20) == 0) {
                /* loop over the height */
                for (i = 0; i < h; i++) {
                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* loop over the width */
                    for (j = w; j > 0; j--) {
                        int srcdata = cpu_readmem16(source);
                        WILLIAMS_BLIT_OPAQUE(dest, srcdata, keepmask);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    sstart += syadv;
                    dstart += dyadv;
                }
            } /* second case: shifted one pixel */ else {
                /* swap halves of the keep mask and the solid color */
                keepmask = ((keepmask & 0xf0) >> 4) | ((keepmask & 0x0f) << 4);
                solid = ((solid & 0xf0) >> 4) | ((solid & 0x0f) << 4);

                /* loop over the height */
                for (i = 0; i < h; i++) {
                    int pixdata, srcdata, shiftedmask;

                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* left edge case */
                    pixdata = cpu_readmem16(source);
                    srcdata = (pixdata >> 4) & 0x0f;
                    shiftedmask = keepmask | 0xf0;
                    WILLIAMS_BLIT_OPAQUE(dest, srcdata, shiftedmask);

                    source = (source + sxadv) & 0xffff;
                    dest = (dest + dxadv) & 0xffff;

                    /* loop over the width */
                    for (j = w - 1; j > 0; j--) {
                        pixdata = (pixdata << 8) | cpu_readmem16(source);
                        srcdata = (pixdata >> 4) & 0xff;
                        WILLIAMS_BLIT_OPAQUE(dest, srcdata, keepmask);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    /* right edge case */
                    srcdata = (pixdata << 4) & 0xf0;
                    shiftedmask = keepmask | 0x0f;
                    WILLIAMS_BLIT_OPAQUE(dest, srcdata, shiftedmask);

                    sstart += syadv;
                    dstart += dyadv;
                }
            }
        }

    };
    public static blitter_table_Ptr williams_blit_transparent = new blitter_table_Ptr() {

        @Override
        public void handler(int sstart, int dstart, int w, int h, int data) {
            int source, sxadv, syadv;
            int dest, dxadv, dyadv;
            int i, j, solid;
            int keepmask;

            /* compute how much to advance in the x and y loops */
            sxadv = (data & 0x01) != 0 ? 0x100 : 1;
            syadv = (data & 0x01) != 0 ? 1 : w;
            dxadv = (data & 0x02) != 0 ? 0x100 : 1;
            dyadv = (data & 0x02) != 0 ? 1 : w;

            /* determine the common mask */
            keepmask = 0x00;
            if ((data & 0x80) != 0) {
                keepmask |= 0xf0;
            }
            if ((data & 0x40) != 0) {
                keepmask |= 0x0f;
            }
            if (keepmask == 0xff) {
                return;
            }

            /* set the solid pixel value to the mask value */
            solid = williams_blitterram.read(1);

            /* first case: no shifting */
            if ((data & 0x20) == 0) {
                /* loop over the height */
                for (i = 0; i < h; i++) {
                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* loop over the width */
                    for (j = w; j > 0; j--) {
                        int srcdata = cpu_readmem16(source);
                        WILLIAMS_BLIT_TRANSPARENT(dest, srcdata, keepmask);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    sstart += syadv;
                    dstart += dyadv;
                }
            } /* second case: shifted one pixel */ else {
                /* swap halves of the keep mask and the solid color */
                keepmask = ((keepmask & 0xf0) >> 4) | ((keepmask & 0x0f) << 4);
                solid = ((solid & 0xf0) >> 4) | ((solid & 0x0f) << 4);

                /* loop over the height */
                for (i = 0; i < h; i++) {
                    int pixdata, srcdata, shiftedmask;

                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* left edge case */
                    pixdata = cpu_readmem16(source);
                    srcdata = (pixdata >> 4) & 0x0f;
                    shiftedmask = keepmask | 0xf0;
                    WILLIAMS_BLIT_TRANSPARENT(dest, srcdata, shiftedmask);

                    source = (source + sxadv) & 0xffff;
                    dest = (dest + dxadv) & 0xffff;

                    /* loop over the width */
                    for (j = w - 1; j > 0; j--) {
                        pixdata = (pixdata << 8) | cpu_readmem16(source);
                        srcdata = (pixdata >> 4) & 0xff;
                        WILLIAMS_BLIT_TRANSPARENT(dest, srcdata, keepmask);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    /* right edge case */
                    srcdata = (pixdata << 4) & 0xf0;
                    shiftedmask = keepmask | 0x0f;
                    WILLIAMS_BLIT_TRANSPARENT(dest, srcdata, shiftedmask);

                    sstart += syadv;
                    dstart += dyadv;
                }
            }
        }
    };
    public static blitter_table_Ptr williams_blit_opaque_solid = new blitter_table_Ptr() {

        @Override
        public void handler(int sstart, int dstart, int w, int h, int data) {
            int source, sxadv, syadv;
            int dest, dxadv, dyadv;
            int i, j, solid;
            int keepmask;

            /* compute how much to advance in the x and y loops */
            sxadv = (data & 0x01) != 0 ? 0x100 : 1;
            syadv = (data & 0x01) != 0 ? 1 : w;
            dxadv = (data & 0x02) != 0 ? 0x100 : 1;
            dyadv = (data & 0x02) != 0 ? 1 : w;

            /* determine the common mask */
            keepmask = 0x00;
            if ((data & 0x80) != 0) {
                keepmask |= 0xf0;
            }
            if ((data & 0x40) != 0) {
                keepmask |= 0x0f;
            }
            if (keepmask == 0xff) {
                return;
            }

            /* set the solid pixel value to the mask value */
            solid = williams_blitterram.read(1);

            /* first case: no shifting */
            if ((data & 0x20) == 0) {
                /* loop over the height */
                for (i = 0; i < h; i++) {
                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* loop over the width */
                    for (j = w; j > 0; j--) {
                        int srcdata = cpu_readmem16(source);
                        WILLIAMS_BLIT_OPAQUE_SOLID(dest, srcdata, keepmask, solid);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    sstart += syadv;
                    dstart += dyadv;
                }
            } /* second case: shifted one pixel */ else {
                /* swap halves of the keep mask and the solid color */
                keepmask = ((keepmask & 0xf0) >> 4) | ((keepmask & 0x0f) << 4);
                solid = ((solid & 0xf0) >> 4) | ((solid & 0x0f) << 4);

                /* loop over the height */
                for (i = 0; i < h; i++) {
                    int pixdata, srcdata, shiftedmask;

                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* left edge case */
                    pixdata = cpu_readmem16(source);
                    srcdata = (pixdata >> 4) & 0x0f;
                    shiftedmask = keepmask | 0xf0;
                    WILLIAMS_BLIT_OPAQUE_SOLID(dest, srcdata, shiftedmask, solid);

                    source = (source + sxadv) & 0xffff;
                    dest = (dest + dxadv) & 0xffff;

                    /* loop over the width */
                    for (j = w - 1; j > 0; j--) {
                        pixdata = (pixdata << 8) | cpu_readmem16(source);
                        srcdata = (pixdata >> 4) & 0xff;
                        WILLIAMS_BLIT_OPAQUE_SOLID(dest, srcdata, keepmask, solid);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    /* right edge case */
                    srcdata = (pixdata << 4) & 0xf0;
                    shiftedmask = keepmask | 0x0f;
                    WILLIAMS_BLIT_OPAQUE_SOLID(dest, srcdata, shiftedmask, solid);

                    sstart += syadv;
                    dstart += dyadv;
                }
            }
        }
    };
    public static blitter_table_Ptr williams_blit_transparent_solid = new blitter_table_Ptr() {

        @Override
        public void handler(int sstart, int dstart, int w, int h, int data) {
            int source, sxadv, syadv;
            int dest, dxadv, dyadv;
            int i, j, solid;
            int keepmask;

            /* compute how much to advance in the x and y loops */
            sxadv = (data & 0x01) != 0 ? 0x100 : 1;
            syadv = (data & 0x01) != 0 ? 1 : w;
            dxadv = (data & 0x02) != 0 ? 0x100 : 1;
            dyadv = (data & 0x02) != 0 ? 1 : w;

            /* determine the common mask */
            keepmask = 0x00;
            if ((data & 0x80) != 0) {
                keepmask |= 0xf0;
            }
            if ((data & 0x40) != 0) {
                keepmask |= 0x0f;
            }
            if (keepmask == 0xff) {
                return;
            }

            /* set the solid pixel value to the mask value */
            solid = williams_blitterram.read(1);

            /* first case: no shifting */
            if ((data & 0x20) == 0) {
                /* loop over the height */
                for (i = 0; i < h; i++) {
                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* loop over the width */
                    for (j = w; j > 0; j--) {
                        int srcdata = cpu_readmem16(source);
                        BLIT_TRANSPARENT_SOLID_WILLIAMS(dest, srcdata, keepmask, solid);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    sstart += syadv;
                    dstart += dyadv;
                }
            } /* second case: shifted one pixel */ else {
                /* swap halves of the keep mask and the solid color */
                keepmask = ((keepmask & 0xf0) >> 4) | ((keepmask & 0x0f) << 4);
                solid = ((solid & 0xf0) >> 4) | ((solid & 0x0f) << 4);

                /* loop over the height */
                for (i = 0; i < h; i++) {
                    int pixdata, srcdata, shiftedmask;

                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* left edge case */
                    pixdata = cpu_readmem16(source);
                    srcdata = (pixdata >> 4) & 0x0f;
                    shiftedmask = keepmask | 0xf0;
                    BLIT_TRANSPARENT_SOLID_WILLIAMS(dest, srcdata, shiftedmask, solid);

                    source = (source + sxadv) & 0xffff;
                    dest = (dest + dxadv) & 0xffff;

                    /* loop over the width */
                    for (j = w - 1; j > 0; j--) {
                        pixdata = (pixdata << 8) | cpu_readmem16(source);
                        srcdata = (pixdata >> 4) & 0xff;
                        BLIT_TRANSPARENT_SOLID_WILLIAMS(dest, srcdata, keepmask, solid);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    /* right edge case */
                    srcdata = (pixdata << 4) & 0xf0;
                    shiftedmask = keepmask | 0x0f;
                    BLIT_TRANSPARENT_SOLID_WILLIAMS(dest, srcdata, shiftedmask, solid);

                    sstart += syadv;
                    dstart += dyadv;
                }
            }
        }
    };
    static blitter_table_Ptr williams_blitters[]
            = {
                williams_blit_opaque,
                williams_blit_transparent,
                williams_blit_opaque_solid,
                williams_blit_transparent_solid
            };

    public static blitter_table_Ptr sinistar_blit_opaque = new blitter_table_Ptr() {

        @Override
        public void handler(int sstart, int dstart, int w, int h, int data) {
            int source, sxadv, syadv;
            int dest, dxadv, dyadv;
            int i, j, solid;
            int keepmask;

            /* compute how much to advance in the x and y loops */
            sxadv = (data & 0x01) != 0 ? 0x100 : 1;
            syadv = (data & 0x01) != 0 ? 1 : w;
            dxadv = (data & 0x02) != 0 ? 0x100 : 1;
            dyadv = (data & 0x02) != 0 ? 1 : w;

            /* determine the common mask */
            keepmask = 0x00;
            if ((data & 0x80) != 0) {
                keepmask |= 0xf0;
            }
            if ((data & 0x40) != 0) {
                keepmask |= 0x0f;
            }
            if (keepmask == 0xff) {
                return;
            }

            /* set the solid pixel value to the mask value */
            solid = williams_blitterram.read(1);

            /* first case: no shifting */
            if ((data & 0x20) == 0) {
                /* loop over the height */
                for (i = 0; i < h; i++) {
                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* loop over the width */
                    for (j = w; j > 0; j--) {
                        int srcdata = cpu_readmem16(source);
                        SINISTAR_BLIT_OPAQUE(dest, srcdata, keepmask);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    sstart += syadv;
                    dstart += dyadv;
                }
            } /* second case: shifted one pixel */ else {
                /* swap halves of the keep mask and the solid color */
                keepmask = ((keepmask & 0xf0) >> 4) | ((keepmask & 0x0f) << 4);
                solid = ((solid & 0xf0) >> 4) | ((solid & 0x0f) << 4);

                /* loop over the height */
                for (i = 0; i < h; i++) {
                    int pixdata, srcdata, shiftedmask;

                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* left edge case */
                    pixdata = cpu_readmem16(source);
                    srcdata = (pixdata >> 4) & 0x0f;
                    shiftedmask = keepmask | 0xf0;
                    SINISTAR_BLIT_OPAQUE(dest, srcdata, shiftedmask);

                    source = (source + sxadv) & 0xffff;
                    dest = (dest + dxadv) & 0xffff;

                    /* loop over the width */
                    for (j = w - 1; j > 0; j--) {
                        pixdata = (pixdata << 8) | cpu_readmem16(source);
                        srcdata = (pixdata >> 4) & 0xff;
                        SINISTAR_BLIT_OPAQUE(dest, srcdata, keepmask);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    /* right edge case */
                    srcdata = (pixdata << 4) & 0xf0;
                    shiftedmask = keepmask | 0x0f;
                    SINISTAR_BLIT_OPAQUE(dest, srcdata, shiftedmask);

                    sstart += syadv;
                    dstart += dyadv;
                }
            }
        }
    };
    public static blitter_table_Ptr sinistar_blit_transparent = new blitter_table_Ptr() {

        @Override
        public void handler(int sstart, int dstart, int w, int h, int data) {
            int source, sxadv, syadv;
            int dest, dxadv, dyadv;
            int i, j, solid;
            int keepmask;

            /* compute how much to advance in the x and y loops */
            sxadv = (data & 0x01) != 0 ? 0x100 : 1;
            syadv = (data & 0x01) != 0 ? 1 : w;
            dxadv = (data & 0x02) != 0 ? 0x100 : 1;
            dyadv = (data & 0x02) != 0 ? 1 : w;

            /* determine the common mask */
            keepmask = 0x00;
            if ((data & 0x80) != 0) {
                keepmask |= 0xf0;
            }
            if ((data & 0x40) != 0) {
                keepmask |= 0x0f;
            }
            if (keepmask == 0xff) {
                return;
            }

            /* set the solid pixel value to the mask value */
            solid = williams_blitterram.read(1);

            /* first case: no shifting */
            if ((data & 0x20) == 0) {
                /* loop over the height */
                for (i = 0; i < h; i++) {
                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* loop over the width */
                    for (j = w; j > 0; j--) {
                        int srcdata = cpu_readmem16(source);
                        SINISTAR_BLIT_TRANSPARENT(dest, srcdata, keepmask);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    sstart += syadv;
                    dstart += dyadv;
                }
            } /* second case: shifted one pixel */ else {
                /* swap halves of the keep mask and the solid color */
                keepmask = ((keepmask & 0xf0) >> 4) | ((keepmask & 0x0f) << 4);
                solid = ((solid & 0xf0) >> 4) | ((solid & 0x0f) << 4);

                /* loop over the height */
                for (i = 0; i < h; i++) {
                    int pixdata, srcdata, shiftedmask;

                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* left edge case */
                    pixdata = cpu_readmem16(source);
                    srcdata = (pixdata >> 4) & 0x0f;
                    shiftedmask = keepmask | 0xf0;
                    SINISTAR_BLIT_TRANSPARENT(dest, srcdata, shiftedmask);

                    source = (source + sxadv) & 0xffff;
                    dest = (dest + dxadv) & 0xffff;

                    /* loop over the width */
                    for (j = w - 1; j > 0; j--) {
                        pixdata = (pixdata << 8) | cpu_readmem16(source);
                        srcdata = (pixdata >> 4) & 0xff;
                        SINISTAR_BLIT_TRANSPARENT(dest, srcdata, keepmask);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    /* right edge case */
                    srcdata = (pixdata << 4) & 0xf0;
                    shiftedmask = keepmask | 0x0f;
                    SINISTAR_BLIT_TRANSPARENT(dest, srcdata, shiftedmask);

                    sstart += syadv;
                    dstart += dyadv;
                }
            }
        }
    };
    public static blitter_table_Ptr sinistar_blit_opaque_solid = new blitter_table_Ptr() {

        @Override
        public void handler(int sstart, int dstart, int w, int h, int data) {
            int source, sxadv, syadv;
            int dest, dxadv, dyadv;
            int i, j, solid;
            int keepmask;

            /* compute how much to advance in the x and y loops */
            sxadv = (data & 0x01) != 0 ? 0x100 : 1;
            syadv = (data & 0x01) != 0 ? 1 : w;
            dxadv = (data & 0x02) != 0 ? 0x100 : 1;
            dyadv = (data & 0x02) != 0 ? 1 : w;

            /* determine the common mask */
            keepmask = 0x00;
            if ((data & 0x80) != 0) {
                keepmask |= 0xf0;
            }
            if ((data & 0x40) != 0) {
                keepmask |= 0x0f;
            }
            if (keepmask == 0xff) {
                return;
            }

            /* set the solid pixel value to the mask value */
            solid = williams_blitterram.read(1);

            /* first case: no shifting */
            if ((data & 0x20) == 0) {
                /* loop over the height */
                for (i = 0; i < h; i++) {
                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* loop over the width */
                    for (j = w; j > 0; j--) {
                        int srcdata = cpu_readmem16(source);
                        SINISTAR_BLIT_OPAQUE_SOLID(dest, srcdata, keepmask, solid);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    sstart += syadv;
                    dstart += dyadv;
                }
            } /* second case: shifted one pixel */ else {
                /* swap halves of the keep mask and the solid color */
                keepmask = ((keepmask & 0xf0) >> 4) | ((keepmask & 0x0f) << 4);
                solid = ((solid & 0xf0) >> 4) | ((solid & 0x0f) << 4);

                /* loop over the height */
                for (i = 0; i < h; i++) {
                    int pixdata, srcdata, shiftedmask;

                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* left edge case */
                    pixdata = cpu_readmem16(source);
                    srcdata = (pixdata >> 4) & 0x0f;
                    shiftedmask = keepmask | 0xf0;
                    SINISTAR_BLIT_OPAQUE_SOLID(dest, srcdata, shiftedmask, solid);

                    source = (source + sxadv) & 0xffff;
                    dest = (dest + dxadv) & 0xffff;

                    /* loop over the width */
                    for (j = w - 1; j > 0; j--) {
                        pixdata = (pixdata << 8) | cpu_readmem16(source);
                        srcdata = (pixdata >> 4) & 0xff;
                        SINISTAR_BLIT_OPAQUE_SOLID(dest, srcdata, keepmask, solid);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    /* right edge case */
                    srcdata = (pixdata << 4) & 0xf0;
                    shiftedmask = keepmask | 0x0f;
                    SINISTAR_BLIT_OPAQUE_SOLID(dest, srcdata, shiftedmask, solid);

                    sstart += syadv;
                    dstart += dyadv;
                }
            }
        }
    };
    public static blitter_table_Ptr sinistar_blit_transparent_solid = new blitter_table_Ptr() {

        @Override
        public void handler(int sstart, int dstart, int w, int h, int data) {
            int source, sxadv, syadv;
            int dest, dxadv, dyadv;
            int i, j, solid;
            int keepmask;

            /* compute how much to advance in the x and y loops */
            sxadv = (data & 0x01) != 0 ? 0x100 : 1;
            syadv = (data & 0x01) != 0 ? 1 : w;
            dxadv = (data & 0x02) != 0 ? 0x100 : 1;
            dyadv = (data & 0x02) != 0 ? 1 : w;

            /* determine the common mask */
            keepmask = 0x00;
            if ((data & 0x80) != 0) {
                keepmask |= 0xf0;
            }
            if ((data & 0x40) != 0) {
                keepmask |= 0x0f;
            }
            if (keepmask == 0xff) {
                return;
            }

            /* set the solid pixel value to the mask value */
            solid = williams_blitterram.read(1);

            /* first case: no shifting */
            if ((data & 0x20) == 0) {
                /* loop over the height */
                for (i = 0; i < h; i++) {
                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* loop over the width */
                    for (j = w; j > 0; j--) {
                        int srcdata = cpu_readmem16(source);
                        SINISTAR_BLIT_TRANSPARENT_SOLID(dest, srcdata, keepmask, solid);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    sstart += syadv;
                    dstart += dyadv;
                }
            } /* second case: shifted one pixel */ else {
                /* swap halves of the keep mask and the solid color */
                keepmask = ((keepmask & 0xf0) >> 4) | ((keepmask & 0x0f) << 4);
                solid = ((solid & 0xf0) >> 4) | ((solid & 0x0f) << 4);

                /* loop over the height */
                for (i = 0; i < h; i++) {
                    int pixdata, srcdata, shiftedmask;

                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* left edge case */
                    pixdata = cpu_readmem16(source);
                    srcdata = (pixdata >> 4) & 0x0f;
                    shiftedmask = keepmask | 0xf0;
                    SINISTAR_BLIT_TRANSPARENT_SOLID(dest, srcdata, shiftedmask, solid);

                    source = (source + sxadv) & 0xffff;
                    dest = (dest + dxadv) & 0xffff;

                    /* loop over the width */
                    for (j = w - 1; j > 0; j--) {
                        pixdata = (pixdata << 8) | cpu_readmem16(source);
                        srcdata = (pixdata >> 4) & 0xff;
                        SINISTAR_BLIT_TRANSPARENT_SOLID(dest, srcdata, keepmask, solid);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    /* right edge case */
                    srcdata = (pixdata << 4) & 0xf0;
                    shiftedmask = keepmask | 0x0f;
                    SINISTAR_BLIT_TRANSPARENT_SOLID(dest, srcdata, shiftedmask, solid);

                    sstart += syadv;
                    dstart += dyadv;
                }
            }
        }
    };

    static blitter_table_Ptr sinistar_blitters[]
            = {
                sinistar_blit_opaque,
                sinistar_blit_transparent,
                sinistar_blit_opaque_solid,
                sinistar_blit_transparent_solid
            };
    public static blitter_table_Ptr blaster_blit_opaque = new blitter_table_Ptr() {

        @Override
        public void handler(int sstart, int dstart, int w, int h, int data) {
            int source, sxadv, syadv;
            int dest, dxadv, dyadv;
            int i, j, solid;
            int keepmask;

            /* compute how much to advance in the x and y loops */
            sxadv = (data & 0x01) != 0 ? 0x100 : 1;
            syadv = (data & 0x01) != 0 ? 1 : w;
            dxadv = (data & 0x02) != 0 ? 0x100 : 1;
            dyadv = (data & 0x02) != 0 ? 1 : w;

            /* determine the common mask */
            keepmask = 0x00;
            if ((data & 0x80) != 0) {
                keepmask |= 0xf0;
            }
            if ((data & 0x40) != 0) {
                keepmask |= 0x0f;
            }
            if (keepmask == 0xff) {
                return;
            }

            /* set the solid pixel value to the mask value */
            solid = williams_blitterram.read(1);

            /* first case: no shifting */
            if ((data & 0x20) == 0) {
                /* loop over the height */
                for (i = 0; i < h; i++) {
                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* loop over the width */
                    for (j = w; j > 0; j--) {
                        int srcdata = cpu_readmem16(source);
                        BLASTER_BLIT_OPAQUE(dest, srcdata, keepmask);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    sstart += syadv;
                    dstart += dyadv;
                }
            } /* second case: shifted one pixel */ else {
                /* swap halves of the keep mask and the solid color */
                keepmask = ((keepmask & 0xf0) >> 4) | ((keepmask & 0x0f) << 4);
                solid = ((solid & 0xf0) >> 4) | ((solid & 0x0f) << 4);

                /* loop over the height */
                for (i = 0; i < h; i++) {
                    int pixdata, srcdata, shiftedmask;

                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* left edge case */
                    pixdata = cpu_readmem16(source);
                    srcdata = (pixdata >> 4) & 0x0f;
                    shiftedmask = keepmask | 0xf0;
                    BLASTER_BLIT_OPAQUE(dest, srcdata, shiftedmask);

                    source = (source + sxadv) & 0xffff;
                    dest = (dest + dxadv) & 0xffff;

                    /* loop over the width */
                    for (j = w - 1; j > 0; j--) {
                        pixdata = (pixdata << 8) | cpu_readmem16(source);
                        srcdata = (pixdata >> 4) & 0xff;
                        BLASTER_BLIT_OPAQUE(dest, srcdata, keepmask);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    /* right edge case */
                    srcdata = (pixdata << 4) & 0xf0;
                    shiftedmask = keepmask | 0x0f;
                    BLASTER_BLIT_OPAQUE(dest, srcdata, shiftedmask);

                    sstart += syadv;
                    dstart += dyadv;
                }
            }
        }
    };
    public static blitter_table_Ptr blaster_blit_transparent = new blitter_table_Ptr() {

        @Override
        public void handler(int sstart, int dstart, int w, int h, int data) {
            int source, sxadv, syadv;
            int dest, dxadv, dyadv;
            int i, j, solid;
            int keepmask;

            /* compute how much to advance in the x and y loops */
            sxadv = (data & 0x01) != 0 ? 0x100 : 1;
            syadv = (data & 0x01) != 0 ? 1 : w;
            dxadv = (data & 0x02) != 0 ? 0x100 : 1;
            dyadv = (data & 0x02) != 0 ? 1 : w;

            /* determine the common mask */
            keepmask = 0x00;
            if ((data & 0x80) != 0) {
                keepmask |= 0xf0;
            }
            if ((data & 0x40) != 0) {
                keepmask |= 0x0f;
            }
            if (keepmask == 0xff) {
                return;
            }

            /* set the solid pixel value to the mask value */
            solid = williams_blitterram.read(1);

            /* first case: no shifting */
            if ((data & 0x20) == 0) {
                /* loop over the height */
                for (i = 0; i < h; i++) {
                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* loop over the width */
                    for (j = w; j > 0; j--) {
                        int srcdata = cpu_readmem16(source);
                        BLASTER_BLIT_TRANSPARENT(dest, srcdata, keepmask);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    sstart += syadv;
                    dstart += dyadv;
                }
            } /* second case: shifted one pixel */ else {
                /* swap halves of the keep mask and the solid color */
                keepmask = ((keepmask & 0xf0) >> 4) | ((keepmask & 0x0f) << 4);
                solid = ((solid & 0xf0) >> 4) | ((solid & 0x0f) << 4);

                /* loop over the height */
                for (i = 0; i < h; i++) {
                    int pixdata, srcdata, shiftedmask;

                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* left edge case */
                    pixdata = cpu_readmem16(source);
                    srcdata = (pixdata >> 4) & 0x0f;
                    shiftedmask = keepmask | 0xf0;
                    BLASTER_BLIT_TRANSPARENT(dest, srcdata, shiftedmask);

                    source = (source + sxadv) & 0xffff;
                    dest = (dest + dxadv) & 0xffff;

                    /* loop over the width */
                    for (j = w - 1; j > 0; j--) {
                        pixdata = (pixdata << 8) | cpu_readmem16(source);
                        srcdata = (pixdata >> 4) & 0xff;
                        BLASTER_BLIT_TRANSPARENT(dest, srcdata, keepmask);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    /* right edge case */
                    srcdata = (pixdata << 4) & 0xf0;
                    shiftedmask = keepmask | 0x0f;
                    BLASTER_BLIT_TRANSPARENT(dest, srcdata, shiftedmask);

                    sstart += syadv;
                    dstart += dyadv;
                }
            }
        }
    };
    public static blitter_table_Ptr blaster_blit_opaque_solid = new blitter_table_Ptr() {

        @Override
        public void handler(int sstart, int dstart, int w, int h, int data) {
            int source, sxadv, syadv;
            int dest, dxadv, dyadv;
            int i, j, solid;
            int keepmask;

            /* compute how much to advance in the x and y loops */
            sxadv = (data & 0x01) != 0 ? 0x100 : 1;
            syadv = (data & 0x01) != 0 ? 1 : w;
            dxadv = (data & 0x02) != 0 ? 0x100 : 1;
            dyadv = (data & 0x02) != 0 ? 1 : w;

            /* determine the common mask */
            keepmask = 0x00;
            if ((data & 0x80) != 0) {
                keepmask |= 0xf0;
            }
            if ((data & 0x40) != 0) {
                keepmask |= 0x0f;
            }
            if (keepmask == 0xff) {
                return;
            }

            /* set the solid pixel value to the mask value */
            solid = williams_blitterram.read(1);

            /* first case: no shifting */
            if ((data & 0x20) == 0) {
                /* loop over the height */
                for (i = 0; i < h; i++) {
                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* loop over the width */
                    for (j = w; j > 0; j--) {
                        int srcdata = cpu_readmem16(source);
                        BLASTER_BLIT_OPAQUE_SOLID(dest, srcdata, keepmask, solid);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    sstart += syadv;
                    dstart += dyadv;
                }
            } /* second case: shifted one pixel */ else {
                /* swap halves of the keep mask and the solid color */
                keepmask = ((keepmask & 0xf0) >> 4) | ((keepmask & 0x0f) << 4);
                solid = ((solid & 0xf0) >> 4) | ((solid & 0x0f) << 4);

                /* loop over the height */
                for (i = 0; i < h; i++) {
                    int pixdata, srcdata, shiftedmask;

                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* left edge case */
                    pixdata = cpu_readmem16(source);
                    srcdata = (pixdata >> 4) & 0x0f;
                    shiftedmask = keepmask | 0xf0;
                    BLASTER_BLIT_OPAQUE_SOLID(dest, srcdata, shiftedmask, solid);

                    source = (source + sxadv) & 0xffff;
                    dest = (dest + dxadv) & 0xffff;

                    /* loop over the width */
                    for (j = w - 1; j > 0; j--) {
                        pixdata = (pixdata << 8) | cpu_readmem16(source);
                        srcdata = (pixdata >> 4) & 0xff;
                        BLASTER_BLIT_OPAQUE_SOLID(dest, srcdata, keepmask, solid);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    /* right edge case */
                    srcdata = (pixdata << 4) & 0xf0;
                    shiftedmask = keepmask | 0x0f;
                    BLASTER_BLIT_OPAQUE_SOLID(dest, srcdata, shiftedmask, solid);

                    sstart += syadv;
                    dstart += dyadv;
                }
            }
        }
    };
    public static blitter_table_Ptr blaster_blit_transparent_solid = new blitter_table_Ptr() {

        @Override
        public void handler(int sstart, int dstart, int w, int h, int data) {
            int source, sxadv, syadv;
            int dest, dxadv, dyadv;
            int i, j, solid;
            int keepmask;

            /* compute how much to advance in the x and y loops */
            sxadv = (data & 0x01) != 0 ? 0x100 : 1;
            syadv = (data & 0x01) != 0 ? 1 : w;
            dxadv = (data & 0x02) != 0 ? 0x100 : 1;
            dyadv = (data & 0x02) != 0 ? 1 : w;

            /* determine the common mask */
            keepmask = 0x00;
            if ((data & 0x80) != 0) {
                keepmask |= 0xf0;
            }
            if ((data & 0x40) != 0) {
                keepmask |= 0x0f;
            }
            if (keepmask == 0xff) {
                return;
            }

            /* set the solid pixel value to the mask value */
            solid = williams_blitterram.read(1);

            /* first case: no shifting */
            if ((data & 0x20) == 0) {
                /* loop over the height */
                for (i = 0; i < h; i++) {
                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* loop over the width */
                    for (j = w; j > 0; j--) {
                        int srcdata = cpu_readmem16(source);
                        BLASTER_BLIT_TRANSPARENT_SOLID(dest, srcdata, keepmask, solid);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    sstart += syadv;
                    dstart += dyadv;
                }
            } /* second case: shifted one pixel */ else {
                /* swap halves of the keep mask and the solid color */
                keepmask = ((keepmask & 0xf0) >> 4) | ((keepmask & 0x0f) << 4);
                solid = ((solid & 0xf0) >> 4) | ((solid & 0x0f) << 4);

                /* loop over the height */
                for (i = 0; i < h; i++) {
                    int pixdata, srcdata, shiftedmask;

                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* left edge case */
                    pixdata = cpu_readmem16(source);
                    srcdata = (pixdata >> 4) & 0x0f;
                    shiftedmask = keepmask | 0xf0;
                    BLASTER_BLIT_TRANSPARENT_SOLID(dest, srcdata, shiftedmask, solid);

                    source = (source + sxadv) & 0xffff;
                    dest = (dest + dxadv) & 0xffff;

                    /* loop over the width */
                    for (j = w - 1; j > 0; j--) {
                        pixdata = (pixdata << 8) | cpu_readmem16(source);
                        srcdata = (pixdata >> 4) & 0xff;
                        BLASTER_BLIT_TRANSPARENT_SOLID(dest, srcdata, keepmask, solid);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    /* right edge case */
                    srcdata = (pixdata << 4) & 0xf0;
                    shiftedmask = keepmask | 0x0f;
                    BLASTER_BLIT_TRANSPARENT_SOLID(dest, srcdata, shiftedmask, solid);

                    sstart += syadv;
                    dstart += dyadv;
                }
            }
        }
    };

    static blitter_table_Ptr blaster_blitters[]
            = {
                blaster_blit_opaque,
                blaster_blit_transparent,
                blaster_blit_opaque_solid,
                blaster_blit_transparent_solid
            };
    public static blitter_table_Ptr williams2_blit_opaque = new blitter_table_Ptr() {

        @Override
        public void handler(int sstart, int dstart, int w, int h, int data) {
            int source, sxadv, syadv;
            int dest, dxadv, dyadv;
            int i, j, solid;
            int keepmask;

            /* compute how much to advance in the x and y loops */
            sxadv = (data & 0x01) != 0 ? 0x100 : 1;
            syadv = (data & 0x01) != 0 ? 1 : w;
            dxadv = (data & 0x02) != 0 ? 0x100 : 1;
            dyadv = (data & 0x02) != 0 ? 1 : w;

            /* determine the common mask */
            keepmask = 0x00;
            if ((data & 0x80) != 0) {
                keepmask |= 0xf0;
            }
            if ((data & 0x40) != 0) {
                keepmask |= 0x0f;
            }
            if (keepmask == 0xff) {
                return;
            }

            /* set the solid pixel value to the mask value */
            solid = williams_blitterram.read(1);

            /* first case: no shifting */
            if ((data & 0x20) == 0) {
                /* loop over the height */
                for (i = 0; i < h; i++) {
                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* loop over the width */
                    for (j = w; j > 0; j--) {
                        int srcdata = cpu_readmem16(source);
                        WILLIAMS2_BLIT_OPAQUE(dest, srcdata, keepmask);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    sstart += syadv;
                    dstart += dyadv;
                }
            } /* second case: shifted one pixel */ else {
                /* swap halves of the keep mask and the solid color */
                keepmask = ((keepmask & 0xf0) >> 4) | ((keepmask & 0x0f) << 4);
                solid = ((solid & 0xf0) >> 4) | ((solid & 0x0f) << 4);

                /* loop over the height */
                for (i = 0; i < h; i++) {
                    int pixdata, srcdata, shiftedmask;

                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* left edge case */
                    pixdata = cpu_readmem16(source);
                    srcdata = (pixdata >> 4) & 0x0f;
                    shiftedmask = keepmask | 0xf0;
                    WILLIAMS2_BLIT_OPAQUE(dest, srcdata, shiftedmask);

                    source = (source + sxadv) & 0xffff;
                    dest = (dest + dxadv) & 0xffff;

                    /* loop over the width */
                    for (j = w - 1; j > 0; j--) {
                        pixdata = (pixdata << 8) | cpu_readmem16(source);
                        srcdata = (pixdata >> 4) & 0xff;
                        WILLIAMS2_BLIT_OPAQUE(dest, srcdata, keepmask);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    /* right edge case */
                    srcdata = (pixdata << 4) & 0xf0;
                    shiftedmask = keepmask | 0x0f;
                    WILLIAMS2_BLIT_OPAQUE(dest, srcdata, shiftedmask);

                    sstart += syadv;
                    dstart += dyadv;
                }
            }
        }
    };
    public static blitter_table_Ptr williams2_blit_transparent = new blitter_table_Ptr() {

        @Override
        public void handler(int sstart, int dstart, int w, int h, int data) {
            int source, sxadv, syadv;
            int dest, dxadv, dyadv;
            int i, j, solid;
            int keepmask;

            /* compute how much to advance in the x and y loops */
            sxadv = (data & 0x01) != 0 ? 0x100 : 1;
            syadv = (data & 0x01) != 0 ? 1 : w;
            dxadv = (data & 0x02) != 0 ? 0x100 : 1;
            dyadv = (data & 0x02) != 0 ? 1 : w;

            /* determine the common mask */
            keepmask = 0x00;
            if ((data & 0x80) != 0) {
                keepmask |= 0xf0;
            }
            if ((data & 0x40) != 0) {
                keepmask |= 0x0f;
            }
            if (keepmask == 0xff) {
                return;
            }

            /* set the solid pixel value to the mask value */
            solid = williams_blitterram.read(1);

            /* first case: no shifting */
            if ((data & 0x20) == 0) {
                /* loop over the height */
                for (i = 0; i < h; i++) {
                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* loop over the width */
                    for (j = w; j > 0; j--) {
                        int srcdata = cpu_readmem16(source);
                        WILLIAMS2_BLIT_TRANSPARENT(dest, srcdata, keepmask);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    sstart += syadv;
                    dstart += dyadv;
                }
            } /* second case: shifted one pixel */ else {
                /* swap halves of the keep mask and the solid color */
                keepmask = ((keepmask & 0xf0) >> 4) | ((keepmask & 0x0f) << 4);
                solid = ((solid & 0xf0) >> 4) | ((solid & 0x0f) << 4);

                /* loop over the height */
                for (i = 0; i < h; i++) {
                    int pixdata, srcdata, shiftedmask;

                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* left edge case */
                    pixdata = cpu_readmem16(source);
                    srcdata = (pixdata >> 4) & 0x0f;
                    shiftedmask = keepmask | 0xf0;
                    WILLIAMS2_BLIT_TRANSPARENT(dest, srcdata, shiftedmask);

                    source = (source + sxadv) & 0xffff;
                    dest = (dest + dxadv) & 0xffff;

                    /* loop over the width */
                    for (j = w - 1; j > 0; j--) {
                        pixdata = (pixdata << 8) | cpu_readmem16(source);
                        srcdata = (pixdata >> 4) & 0xff;
                        WILLIAMS2_BLIT_TRANSPARENT(dest, srcdata, keepmask);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    /* right edge case */
                    srcdata = (pixdata << 4) & 0xf0;
                    shiftedmask = keepmask | 0x0f;
                    WILLIAMS2_BLIT_TRANSPARENT(dest, srcdata, shiftedmask);

                    sstart += syadv;
                    dstart += dyadv;
                }
            }
        }
    };
    public static blitter_table_Ptr williams2_blit_opaque_solid = new blitter_table_Ptr() {

        @Override
        public void handler(int sstart, int dstart, int w, int h, int data) {
            int source, sxadv, syadv;
            int dest, dxadv, dyadv;
            int i, j, solid;
            int keepmask;

            /* compute how much to advance in the x and y loops */
            sxadv = (data & 0x01) != 0 ? 0x100 : 1;
            syadv = (data & 0x01) != 0 ? 1 : w;
            dxadv = (data & 0x02) != 0 ? 0x100 : 1;
            dyadv = (data & 0x02) != 0 ? 1 : w;

            /* determine the common mask */
            keepmask = 0x00;
            if ((data & 0x80) != 0) {
                keepmask |= 0xf0;
            }
            if ((data & 0x40) != 0) {
                keepmask |= 0x0f;
            }
            if (keepmask == 0xff) {
                return;
            }

            /* set the solid pixel value to the mask value */
            solid = williams_blitterram.read(1);

            /* first case: no shifting */
            if ((data & 0x20) == 0) {
                /* loop over the height */
                for (i = 0; i < h; i++) {
                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* loop over the width */
                    for (j = w; j > 0; j--) {
                        int srcdata = cpu_readmem16(source);
                        WILLIAMS2_BLIT_OPAQUE_SOLID(dest, srcdata, keepmask, solid);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    sstart += syadv;
                    dstart += dyadv;
                }
            } /* second case: shifted one pixel */ else {
                /* swap halves of the keep mask and the solid color */
                keepmask = ((keepmask & 0xf0) >> 4) | ((keepmask & 0x0f) << 4);
                solid = ((solid & 0xf0) >> 4) | ((solid & 0x0f) << 4);

                /* loop over the height */
                for (i = 0; i < h; i++) {
                    int pixdata, srcdata, shiftedmask;

                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* left edge case */
                    pixdata = cpu_readmem16(source);
                    srcdata = (pixdata >> 4) & 0x0f;
                    shiftedmask = keepmask | 0xf0;
                    WILLIAMS2_BLIT_OPAQUE_SOLID(dest, srcdata, shiftedmask, solid);

                    source = (source + sxadv) & 0xffff;
                    dest = (dest + dxadv) & 0xffff;

                    /* loop over the width */
                    for (j = w - 1; j > 0; j--) {
                        pixdata = (pixdata << 8) | cpu_readmem16(source);
                        srcdata = (pixdata >> 4) & 0xff;
                        WILLIAMS2_BLIT_OPAQUE_SOLID(dest, srcdata, keepmask, solid);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    /* right edge case */
                    srcdata = (pixdata << 4) & 0xf0;
                    shiftedmask = keepmask | 0x0f;
                    WILLIAMS2_BLIT_OPAQUE_SOLID(dest, srcdata, shiftedmask, solid);

                    sstart += syadv;
                    dstart += dyadv;
                }
            }
        }
    };
    public static blitter_table_Ptr williams2_blit_transparent_solid = new blitter_table_Ptr() {

        @Override
        public void handler(int sstart, int dstart, int w, int h, int data) {
            int source, sxadv, syadv;
            int dest, dxadv, dyadv;
            int i, j, solid;
            int keepmask;

            /* compute how much to advance in the x and y loops */
            sxadv = (data & 0x01) != 0 ? 0x100 : 1;
            syadv = (data & 0x01) != 0 ? 1 : w;
            dxadv = (data & 0x02) != 0 ? 0x100 : 1;
            dyadv = (data & 0x02) != 0 ? 1 : w;

            /* determine the common mask */
            keepmask = 0x00;
            if ((data & 0x80) != 0) {
                keepmask |= 0xf0;
            }
            if ((data & 0x40) != 0) {
                keepmask |= 0x0f;
            }
            if (keepmask == 0xff) {
                return;
            }

            /* set the solid pixel value to the mask value */
            solid = williams_blitterram.read(1);

            /* first case: no shifting */
            if ((data & 0x20) == 0) {
                /* loop over the height */
                for (i = 0; i < h; i++) {
                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* loop over the width */
                    for (j = w; j > 0; j--) {
                        int srcdata = cpu_readmem16(source);
                        WILLIAMS2_BLIT_TRANSPARENT_SOLID(dest, srcdata, keepmask, solid);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    sstart += syadv;
                    dstart += dyadv;
                }
            } /* second case: shifted one pixel */ else {
                /* swap halves of the keep mask and the solid color */
                keepmask = ((keepmask & 0xf0) >> 4) | ((keepmask & 0x0f) << 4);
                solid = ((solid & 0xf0) >> 4) | ((solid & 0x0f) << 4);

                /* loop over the height */
                for (i = 0; i < h; i++) {
                    int pixdata, srcdata, shiftedmask;

                    source = sstart & 0xffff;
                    dest = dstart & 0xffff;

                    /* left edge case */
                    pixdata = cpu_readmem16(source);
                    srcdata = (pixdata >> 4) & 0x0f;
                    shiftedmask = keepmask | 0xf0;
                    WILLIAMS2_BLIT_TRANSPARENT_SOLID(dest, srcdata, shiftedmask, solid);

                    source = (source + sxadv) & 0xffff;
                    dest = (dest + dxadv) & 0xffff;

                    /* loop over the width */
                    for (j = w - 1; j > 0; j--) {
                        pixdata = (pixdata << 8) | cpu_readmem16(source);
                        srcdata = (pixdata >> 4) & 0xff;
                        WILLIAMS2_BLIT_TRANSPARENT_SOLID(dest, srcdata, keepmask, solid);

                        source = (source + sxadv) & 0xffff;
                        dest = (dest + dxadv) & 0xffff;
                    }

                    /* right edge case */
                    srcdata = (pixdata << 4) & 0xf0;
                    shiftedmask = keepmask | 0x0f;
                    WILLIAMS2_BLIT_TRANSPARENT_SOLID(dest, srcdata, shiftedmask, solid);

                    sstart += syadv;
                    dstart += dyadv;
                }
            }
        }
    };
    static blitter_table_Ptr williams2_blitters[]
            = {
                williams2_blit_opaque,
                williams2_blit_transparent,
                williams2_blit_opaque_solid,
                williams2_blit_transparent_solid
            };
}
